/*
 * Copyright 2002-2026 Drew Noakes and contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.imaging.heif;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.heif.HeifDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HeifMetadataReaderTest
{
    private static final int FTYP_LENGTH = 20;

    @Test
    public void testDeeplyNestedBoxesReportErrorRatherThanOverflowingStack() throws Exception
    {
        HeifDirectory directory = readNestedContainers(10000);

        assertTrue(containsError(directory, "Box nesting too deep."));
    }

    @Test
    public void testNestingLimit() throws Exception
    {
        HeifDirectory directory = readNestedContainers(100);
        assertFalse(containsError(directory, "Box nesting too deep."));
        assertEquals("heic", directory.getString(HeifDirectory.TAG_MAJOR_BRAND));

        assertTrue(containsError(readNestedContainers(101), "Box nesting too deep."));
    }

    @Test
    public void testXmpItemIsRead() throws Exception
    {
        Metadata metadata = readSingleItemFile("application/rdf+xml");

        XmpDirectory xmpDirectory = metadata.getFirstDirectoryOfType(XmpDirectory.class);
        assertNotNull(xmpDirectory);
        assertEquals("Test", xmpDirectory.getXmpProperties().get("xmp:CreatorTool"));
    }

    @Test
    public void testOtherMimeItemIsIgnored() throws Exception
    {
        Metadata metadata = readSingleItemFile("text/plain");

        assertNull(metadata.getFirstDirectoryOfType(XmpDirectory.class));
    }

    /** Reads a file whose 'meta' box describes a single 'mime' item, stored in 'mdat', holding an XMP packet. */
    private static Metadata readSingleItemFile(String contentType) throws Exception
    {
        byte[] xmp = ("<x:xmpmeta xmlns:x=\"adobe:ns:meta/\"><rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
            + "<rdf:Description rdf:about=\"\" xmlns:xmp=\"http://ns.adobe.com/xap/1.0/\" xmp:CreatorTool=\"Test\"/>"
            + "</rdf:RDF></x:xmpmeta>").getBytes(StandardCharsets.UTF_8);

        // hdlr: version and flags, pre-defined, handler type, three reserved words, empty name
        byte[] hdlr = box("hdlr", concat(new byte[8], ascii("pict"), new byte[13]));
        // infe version 2: item ID 1, protection index 0, item type, empty name, content type
        byte[] infe = box("infe", concat(new byte[] {2, 0, 0, 0, 0, 1, 0, 0}, ascii("mime"), new byte[1], ascii(contentType), new byte[1]));
        // iinf version 0 with one entry
        byte[] iinf = box("iinf", concat(new byte[] {0, 0, 0, 0, 0, 1}, infe));

        byte[] ftyp = box("ftyp", concat(ascii("heic"), new byte[4], ascii("mif1")));
        int ilocLength = 8 + 4 + 2 + 2 + 2 + 2 + 2 + 4 + 4;
        int metaLength = 8 + 4 + hdlr.length + iinf.length + ilocLength;
        int xmpOffset = ftyp.length + metaLength + 8;

        // iloc version 0: 4-byte offsets and lengths, no base offset, one item with one extent
        byte[] iloc = box("iloc", concat(new byte[] {0, 0, 0, 0, 0x44, 0, 0, 1, 0, 1, 0, 0, 0, 1}, int32(xmpOffset), int32(xmp.length)));
        byte[] meta = box("meta", concat(new byte[4], hdlr, iinf, iloc));
        byte[] mdat = box("mdat", xmp);

        assertEquals(metaLength, meta.length);
        return ImageMetadataReader.readMetadata(new ByteArrayInputStream(concat(ftyp, meta, mdat)));
    }

    private static byte[] box(String type, byte[] payload)
    {
        return concat(int32(8 + payload.length), ascii(type), payload);
    }

    private static byte[] int32(int value)
    {
        return new byte[] {(byte)(value >> 24), (byte)(value >> 16), (byte)(value >> 8), (byte)value};
    }

    private static byte[] ascii(String value)
    {
        return value.getBytes(StandardCharsets.US_ASCII);
    }

    private static byte[] concat(byte[]... parts)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] part : parts) {
            out.write(part, 0, part.length);
        }
        return out.toByteArray();
    }

    /**
     * Reads a file in which each 'ipco' box is nested within the previous one. Every box is
     * individually well formed, so only the nesting depth is abnormal.
     */
    private static HeifDirectory readNestedContainers(int count) throws Exception
    {
        byte[] bytes = new byte[FTYP_LENGTH + count * 8];

        writeBoxHeader(bytes, 0, FTYP_LENGTH, "ftyp");
        writeString(bytes, 8, "heic");
        writeString(bytes, 16, "mif1");

        for (int i = 0; i < count; i++) {
            writeBoxHeader(bytes, FTYP_LENGTH + i * 8, (count - i) * 8, "ipco");
        }

        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes));
        HeifDirectory directory = metadata.getFirstDirectoryOfType(HeifDirectory.class);
        assertNotNull(directory);
        return directory;
    }

    private static boolean containsError(Directory directory, String message)
    {
        for (String error : directory.getErrors()) {
            if (error.equals(message))
                return true;
        }
        return false;
    }

    private static void writeBoxHeader(byte[] bytes, int offset, long size, String type)
    {
        bytes[offset] = (byte)(size >> 24);
        bytes[offset + 1] = (byte)(size >> 16);
        bytes[offset + 2] = (byte)(size >> 8);
        bytes[offset + 3] = (byte)size;
        writeString(bytes, offset + 4, type);
    }

    private static void writeString(byte[] bytes, int offset, String value)
    {
        for (int i = 0; i < value.length(); i++) {
            bytes[offset + i] = (byte)value.charAt(i);
        }
    }
}

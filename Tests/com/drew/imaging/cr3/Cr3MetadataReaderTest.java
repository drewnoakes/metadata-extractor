/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
 *    https://github.com/drewnoakes/metadata-extractor/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.imaging.cr3;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.isobmff.IsoBoxContext;
import com.drew.imaging.isobmff.IsoBoxReader;
import com.drew.imaging.isobmff.IsoBoxReaders;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.cr3.Cr3Directory;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.exif.makernotes.CanonMakernoteDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class Cr3MetadataReaderTest
{
    @Test
    public void testStandardAndLargeFileType() throws Exception
    {
        byte[] payload = join(ascii("crx "), new byte[] {0, 0, 0, 1}, ascii("crx isom"));
        ByteArrayOutputStream large = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(large);
        out.writeInt(1);
        out.writeBytes("ftyp");
        out.writeLong(payload.length + 16);
        out.write(payload);
        for (byte[] bytes : new byte[][] {box("ftyp", payload), large.toByteArray()}) {
            Metadata metadata = Cr3MetadataReader.readMetadata(new ByteArrayInputStream(bytes));
            Cr3Directory directory = metadata.getFirstDirectoryOfType(Cr3Directory.class);
            assertNotNull(directory);
            assertFalse(metadata.hasErrors());
            assertEquals("crx ", directory.getString(Cr3Directory.TAG_MAJOR_BRAND));
            assertEquals(1, directory.getLong(Cr3Directory.TAG_MINOR_VERSION));
            assertArrayEquals(new String[] {"crx ", "isom"},
                directory.getStringArray(Cr3Directory.TAG_COMPATIBLE_BRANDS));
        }
    }

    @Test
    public void testTiffDirectoriesAndImageDimensions() throws Exception
    {
        byte[] canon = join(
            box("CMT1", tiff(ExifIFD0Directory.TAG_IMAGE_WIDTH, 640)),
            box("CMT2", tiff(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT, 100)),
            box("CMT3", tiff(CanonMakernoteDirectory.TAG_CANON_SERIAL_NUMBER, 1234)),
            box("CMT4", tiff(GpsDirectory.TAG_ALTITUDE_REF, 0)),
            box("CNCV", join(ascii("CanonCR3"), new byte[2])),
            box("THMB", ByteBuffer.allocate(16).putInt(0).putShort((short)160).putShort((short)120).array()));
        byte[] preview = ByteBuffer.allocate(26)
            .putLong(0).putInt(18).put(ascii("PRVW")).putInt(0).putShort((short)0)
            .putShort((short)1620).putShort((short)1080).array();
        Metadata metadata = Cr3MetadataReader.readMetadata(new ByteArrayInputStream(join(
            box("moov", box("uuid", join(Cr3Readers.USERTYPE_CANON_MAIN, canon))),
            box("uuid", join(Cr3Readers.USERTYPE_CANON_PREVIEW, preview)))));
        assertFalse(metadata.hasErrors());
        assertEquals(640, metadata.getFirstDirectoryOfType(ExifIFD0Directory.class).getInt(ExifIFD0Directory.TAG_IMAGE_WIDTH));
        assertEquals(100, metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class).getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
        assertEquals(1234, metadata.getFirstDirectoryOfType(CanonMakernoteDirectory.class).getInt(CanonMakernoteDirectory.TAG_CANON_SERIAL_NUMBER));
        assertEquals(0, metadata.getFirstDirectoryOfType(GpsDirectory.class).getInt(GpsDirectory.TAG_ALTITUDE_REF));
        Cr3Directory directory = metadata.getFirstDirectoryOfType(Cr3Directory.class);
        assertEquals("CanonCR3", directory.getString(Cr3Directory.TAG_COMPRESSOR_VERSION));
        assertEquals(160, directory.getInt(Cr3Directory.TAG_THUMBNAIL_WIDTH));
        assertEquals(120, directory.getInt(Cr3Directory.TAG_THUMBNAIL_HEIGHT));
        assertEquals(1620, directory.getInt(Cr3Directory.TAG_PREVIEW_WIDTH));
        assertEquals(1080, directory.getInt(Cr3Directory.TAG_PREVIEW_HEIGHT));
    }

    @Test
    public void testEofTerminatedXmpAndFileTypeDetection() throws Exception
    {
        byte[] xmp = ascii("<x:xmpmeta xmlns:x='adobe:ns:meta/'>"
            + "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>"
            + "<rdf:Description rdf:about='' xmlns:xmp='http://ns.adobe.com/xap/1.0/' xmp:Rating='3'/>"
            + "</rdf:RDF></x:xmpmeta>");
        byte[] bytes = join(
            box("ftyp", join(ascii("crx "), new byte[4], ascii("isom"))),
            new byte[4], ascii("uuid"), Cr3Readers.USERTYPE_CANON_XMP, xmp);
        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes));
        assertFalse(metadata.hasErrors());
        XmpDirectory directory = metadata.getFirstDirectoryOfType(XmpDirectory.class);
        assertNotNull(directory);
        assertEquals("3", directory.getXmpProperties().get("xmp:Rating"));
    }

    @Test
    public void testPreviewJpegIsSkipped() throws Exception
    {
        byte[] jpeg = new byte[1024 * 1024];
        byte[] preview = join(ByteBuffer.allocate(26)
            .putLong(0).putInt(18 + jpeg.length).put(ascii("PRVW")).putInt(0).putShort((short)0)
            .putShort((short)1620).putShort((short)1080).array(), jpeg);
        byte[] xmp = ascii("<x:xmpmeta xmlns:x='adobe:ns:meta/'>"
            + "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>"
            + "<rdf:Description rdf:about='' xmlns:xmp='http://ns.adobe.com/xap/1.0/' xmp:Rating='3'/>"
            + "</rdf:RDF></x:xmpmeta>");
        Metadata metadata = Cr3MetadataReader.readMetadata(new ByteArrayInputStream(join(
            box("uuid", join(Cr3Readers.USERTYPE_CANON_PREVIEW, preview)),
            box("uuid", join(Cr3Readers.USERTYPE_CANON_XMP, xmp)))));
        assertFalse(metadata.hasErrors());
        Cr3Directory directory = metadata.getFirstDirectoryOfType(Cr3Directory.class);
        assertEquals(1620, directory.getInt(Cr3Directory.TAG_PREVIEW_WIDTH));
        assertEquals(1080, directory.getInt(Cr3Directory.TAG_PREVIEW_HEIGHT));
        assertEquals("3", metadata.getFirstDirectoryOfType(XmpDirectory.class).getXmpProperties().get("xmp:Rating"));
    }

    @Test
    public void testJpegLocations() throws Exception
    {
        byte[] thumbnail = join(new byte[] {(byte) 0xFF, (byte) 0xD8}, new byte[30]);
        byte[] preview = join(new byte[] {(byte) 0xFF, (byte) 0xD8}, new byte[100]);
        byte[] file = join(
            box("ftyp", join(ascii("crx "), new byte[4], ascii("crx isom"))),
            box("moov", box("uuid", join(Cr3Readers.USERTYPE_CANON_MAIN,
                box("THMB", join(jpegRecordHeader(4, 8, 160, 120, thumbnail.length), thumbnail))))),
            box("uuid", join(Cr3Readers.USERTYPE_CANON_PREVIEW, new byte[8],
                box("PRVW", join(jpegRecordHeader(6, 12, 1620, 1080, preview.length), preview)))),
            box("mdat", new byte[64]));
        Metadata metadata = Cr3MetadataReader.readMetadata(new ByteArrayInputStream(file));
        assertFalse(metadata.hasErrors());
        Cr3Directory directory = metadata.getFirstDirectoryOfType(Cr3Directory.class);
        assertEquals(160, directory.getInt(Cr3Directory.TAG_THUMBNAIL_WIDTH));
        assertEquals(1080, directory.getInt(Cr3Directory.TAG_PREVIEW_HEIGHT));
        assertArrayEquals(thumbnail, slice(file, directory.getLong(Cr3Directory.TAG_THUMBNAIL_OFFSET),
            directory.getLong(Cr3Directory.TAG_THUMBNAIL_LENGTH)));
        assertArrayEquals(preview, slice(file, directory.getLong(Cr3Directory.TAG_PREVIEW_OFFSET),
            directory.getLong(Cr3Directory.TAG_PREVIEW_LENGTH)));
    }

    @Test
    public void testJpegLengthBeyondTheBox() throws Exception
    {
        byte[] preview = new byte[100];
        Metadata metadata = Cr3MetadataReader.readMetadata(new ByteArrayInputStream(
            box("uuid", join(Cr3Readers.USERTYPE_CANON_PREVIEW, new byte[8],
                box("PRVW", join(jpegRecordHeader(6, 12, 1620, 1080, preview.length + 1), preview))))));
        Cr3Directory directory = metadata.getFirstDirectoryOfType(Cr3Directory.class);
        assertEquals(1620, directory.getInt(Cr3Directory.TAG_PREVIEW_WIDTH));
        assertFalse(directory.containsTag(Cr3Directory.TAG_PREVIEW_OFFSET));
        assertFalse(directory.containsTag(Cr3Directory.TAG_PREVIEW_LENGTH));
        assertTrue(directory.hasErrors());
    }

    @Test
    public void testMalformedFileTypeKeepsLaterBoxes() throws Exception
    {
        Metadata metadata = Cr3MetadataReader.readMetadata(new ByteArrayInputStream(join(
            box("ftyp", new byte[] {1, 2}),
            box("uuid", join(Cr3Readers.USERTYPE_CANON_PREVIEW, new byte[8],
                box("PRVW", jpegRecordHeader(6, 12, 1620, 1080, 0)))))));
        Cr3Directory directory = metadata.getFirstDirectoryOfType(Cr3Directory.class);
        assertTrue(directory.hasErrors());
        assertEquals(1620, directory.getInt(Cr3Directory.TAG_PREVIEW_WIDTH));
    }

    @Test
    public void testPreviewToEndOfInput() throws Exception
    {
        byte[] preview = {(byte) 0xff, (byte) 0xd8, 1, 2, (byte) 0xff, (byte) 0xd9};
        byte[] file = join(new byte[] {0, 0, 0, 0}, ascii("uuid"), Cr3Readers.USERTYPE_CANON_PREVIEW, new byte[8],
            new byte[] {0, 0, 0, 0}, ascii("PRVW"), jpegRecordHeader(6, 12, 1620, 1080, preview.length), preview);
        Metadata metadata = Cr3MetadataReader.readMetadata(new ByteArrayInputStream(file));
        Cr3Directory directory = metadata.getFirstDirectoryOfType(Cr3Directory.class);
        assertFalse(metadata.hasErrors());
        assertArrayEquals(preview, slice(file, directory.getLong(Cr3Directory.TAG_PREVIEW_OFFSET),
            directory.getLong(Cr3Directory.TAG_PREVIEW_LENGTH)));
    }

    @Test
    public void testTruncatedJpegGetsNoLocation() throws Exception
    {
        byte[] preview = new byte[100];
        byte[] file = box("uuid", join(Cr3Readers.USERTYPE_CANON_PREVIEW, new byte[8],
            box("PRVW", join(jpegRecordHeader(6, 12, 1620, 1080, preview.length), preview))));
        byte[] truncated = java.util.Arrays.copyOf(file, file.length - 10);
        Metadata metadata = Cr3MetadataReader.readMetadata(new ByteArrayInputStream(truncated) {
            @Override
            public synchronized long skip(long n)
            {
                super.skip(n);
                return n;
            }
        });
        Cr3Directory directory = metadata.getFirstDirectoryOfType(Cr3Directory.class);
        assertEquals(1620, directory.getInt(Cr3Directory.TAG_PREVIEW_WIDTH));
        assertFalse(directory.containsTag(Cr3Directory.TAG_PREVIEW_OFFSET));
        assertTrue(directory.hasErrors());
    }

    @Test
    public void testLaterRecordWithoutJpegKeepsTheEarlierOne() throws Exception
    {
        byte[] preview = new byte[100];
        byte[] valid = box("uuid", join(Cr3Readers.USERTYPE_CANON_PREVIEW, new byte[8],
            box("PRVW", join(jpegRecordHeader(6, 12, 1620, 1080, preview.length), preview))));
        byte[] invalid = box("uuid", join(Cr3Readers.USERTYPE_CANON_PREVIEW, new byte[8],
            box("PRVW", join(jpegRecordHeader(6, 12, 800, 600, preview.length + 1), preview))));
        byte[] file = join(valid, invalid);
        Metadata metadata = Cr3MetadataReader.readMetadata(new ByteArrayInputStream(file));
        Cr3Directory directory = metadata.getFirstDirectoryOfType(Cr3Directory.class);
        assertEquals(1620, directory.getInt(Cr3Directory.TAG_PREVIEW_WIDTH));
        assertEquals(1080, directory.getInt(Cr3Directory.TAG_PREVIEW_HEIGHT));
        assertArrayEquals(preview, slice(file, directory.getLong(Cr3Directory.TAG_PREVIEW_OFFSET),
            directory.getLong(Cr3Directory.TAG_PREVIEW_LENGTH)));
        assertTrue(directory.hasErrors());
    }

    /** A reader of one's own, next to the default readers, gets the preview bytes in the same pass. */
    @Test
    public void testOwnReaderReadsThePreviewInOnePass() throws Exception
    {
        final byte[] jpeg = {(byte) 0xff, (byte) 0xd8, 1, 2, 3, (byte) 0xff, (byte) 0xd9};
        byte[] file = join(box("ftyp", join(ascii("crx "), new byte[4])),
            box("uuid", join(Cr3Readers.USERTYPE_CANON_PREVIEW, new byte[8],
                box("PRVW", join(jpegRecordHeader(6, 12, 1620, 1080, jpeg.length), jpeg)))));
        List<IsoBoxReader> readers = new ArrayList<IsoBoxReader>(Cr3MetadataReader.ALL_READERS);
        readers.add(IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box) throws IOException
            {
                SequentialReader payload = box.payload();
                payload.skip(12);
                long length = payload.getUInt32();
                box.root().get(Previews.class).jpegs.add(payload.getBytes((int) length));
            }
        }, "uuid:eaf42b5e-1c98-4b88-b9fb-b7dc406e4d16/PRVW"));
        IsoBoxReaders walk = Cr3MetadataReader.compile(readers);

        for (int i = 0; i < 2; i++) {
            Metadata metadata = new Metadata();
            IsoBoxContext root = Cr3MetadataReader.newRoot(metadata, null);
            Previews previews = new Previews();
            root.put(Previews.class, previews);
            Cr3MetadataReader.extract(new ByteArrayInputStream(file), walk, root);

            assertEquals(1, previews.jpegs.size());
            assertArrayEquals(jpeg, previews.jpegs.get(0));
            Cr3Directory directory = metadata.getFirstDirectoryOfType(Cr3Directory.class);
            assertEquals(1620, directory.getInt(Cr3Directory.TAG_PREVIEW_WIDTH));
            assertEquals("crx ", directory.getString(Cr3Directory.TAG_MAJOR_BRAND));
            assertFalse(metadata.hasErrors());
        }
    }

    private static final class Previews
    {
        final List<byte[]> jpegs = new ArrayList<byte[]>();
    }

    private static byte[] jpegRecordHeader(int dimensionsAt, int lengthAt, int width, int height, int length)
    {
        return ByteBuffer.allocate(16)
            .putShort(dimensionsAt, (short) width).putShort(dimensionsAt + 2, (short) height)
            .putInt(lengthAt, length).array();
    }

    private static byte[] slice(byte[] bytes, long offset, long length)
    {
        return java.util.Arrays.copyOfRange(bytes, (int) offset, (int) (offset + length));
    }

    private static byte[] tiff(int tag, int value)
    {
        return ByteBuffer.allocate(26).order(ByteOrder.LITTLE_ENDIAN)
            .put((byte)'I').put((byte)'I').putShort((short)42).putInt(8)
            .putShort((short)1).putShort((short)tag).putShort((short)3)
            .putInt(1).putShort((short)value).putShort((short)0).putInt(0).array();
    }

    private static byte[] ascii(String value)
    {
        return value.getBytes(StandardCharsets.US_ASCII);
    }

    private static byte[] box(String type, byte[] payload) throws IOException
    {
        return join(ByteBuffer.allocate(8).putInt(payload.length + 8).put(ascii(type)).array(), payload);
    }

    private static byte[] join(byte[]... parts) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        for (byte[] part : parts)
            bytes.write(part);
        return bytes.toByteArray();
    }
}

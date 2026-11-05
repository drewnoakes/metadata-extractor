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

package com.drew.metadata.bmp;

import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class BmpReaderTest
{
    @NotNull
    public static BmpHeaderDirectory processBytes(@NotNull String file) throws Exception
    {
        Metadata metadata = new Metadata();
        InputStream stream = new FileInputStream(file);
        new BmpReader().extract(new StreamReader(stream), metadata);
        stream.close();

        BmpHeaderDirectory directory = metadata.getFirstDirectoryOfType(BmpHeaderDirectory.class);
        assertNotNull(directory);
        return directory;
    }

    @Test
    public void testLinkedProfileBoundedReads() throws Exception
    {
        byte[][] payloads = { { 'A', 0 }, { 'A' }, { 'c', 'a', 'f', (byte)0xE9, 0, 7 } };
        for (int i = 0; i < payloads.length; i++) {
            byte[] payload = payloads[i];
            ByteBuffer bytes = ByteBuffer.allocate(14 + 124 + payload.length).order(ByteOrder.LITTLE_ENDIAN);
            bytes.putShort(0, (short)BmpReader.BITMAP);
            bytes.putInt(2, bytes.capacity());
            bytes.putInt(14, 124); // BITMAPV5HEADER
            bytes.putInt(18, 1); // Width
            bytes.putInt(22, 1); // Height
            bytes.putShort(26, (short)1); // Colour planes
            bytes.putShort(28, (short)24); // Bits per pixel
            bytes.putInt(14 + 56, (int)BmpHeaderDirectory.ColorSpaceType.PROFILE_LINKED.getValue());
            bytes.putInt(14 + 112, 124); // Profile offset relative to the DIB header
            bytes.putInt(14 + 116, i < 2 ? Integer.MAX_VALUE : payload.length);
            bytes.position(14 + 124);
            bytes.put(payload);

            StreamReader reader = new StreamReader(new ByteArrayInputStream(bytes.array())) {
                @Override
                public byte[] getBytes(int count) throws IOException
                {
                    if (count > 1024)
                        throw new AssertionError("Oversized allocating read: " + count);
                    return super.getBytes(count);
                }
            };
            Metadata metadata = new Metadata();
            new BmpReader().extract(reader, metadata);
            BmpHeaderDirectory directory = metadata.getFirstDirectoryOfType(BmpHeaderDirectory.class);
            assertNotNull(directory);
            if (i < 2) {
                assertEquals(1, directory.getErrorCount());
                assertEquals("Unable to read BMP header", directory.getErrors().iterator().next());
            } else {
                assertFalse(directory.hasErrors());
                assertEquals("caf\u00e9", directory.getString(BmpHeaderDirectory.TAG_LINKED_PROFILE));
                assertEquals(bytes.capacity(), reader.getPosition());
            }
        }
    }

    @Test
    public void testMsPaint16color() throws Exception
    {
        BmpHeaderDirectory directory = processBytes("Tests/Data/16color-10x10.bmp");

        assertFalse(directory.hasErrors());

        assertEquals(10, directory.getInt(BmpHeaderDirectory.TAG_IMAGE_WIDTH));
        assertEquals(10, directory.getInt(BmpHeaderDirectory.TAG_IMAGE_HEIGHT));
        assertEquals(4, directory.getInt(BmpHeaderDirectory.TAG_BITS_PER_PIXEL));
        assertEquals("None", directory.getDescription(BmpHeaderDirectory.TAG_COMPRESSION));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_X_PIXELS_PER_METER));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_Y_PIXELS_PER_METER));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_PALETTE_COLOUR_COUNT));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_IMPORTANT_COLOUR_COUNT));
        assertEquals(1, directory.getInt(BmpHeaderDirectory.TAG_COLOUR_PLANES));
        assertEquals(40, directory.getInt(BmpHeaderDirectory.TAG_HEADER_SIZE));
    }

    @Test
    public void testMsPaint24bpp() throws Exception
    {
        BmpHeaderDirectory directory = processBytes("Tests/Data/24bpp-10x10.bmp");

        assertFalse(directory.hasErrors());

        assertEquals(10, directory.getInt(BmpHeaderDirectory.TAG_IMAGE_WIDTH));
        assertEquals(10, directory.getInt(BmpHeaderDirectory.TAG_IMAGE_HEIGHT));
        assertEquals(24, directory.getInt(BmpHeaderDirectory.TAG_BITS_PER_PIXEL));
        assertEquals("None", directory.getDescription(BmpHeaderDirectory.TAG_COMPRESSION));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_X_PIXELS_PER_METER));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_Y_PIXELS_PER_METER));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_PALETTE_COLOUR_COUNT));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_IMPORTANT_COLOUR_COUNT));
        assertEquals(1, directory.getInt(BmpHeaderDirectory.TAG_COLOUR_PLANES));
        assertEquals(40, directory.getInt(BmpHeaderDirectory.TAG_HEADER_SIZE));
    }
}

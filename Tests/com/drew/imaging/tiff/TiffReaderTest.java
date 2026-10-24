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
package com.drew.imaging.tiff;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifTiffHandler;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for {@link TiffReader}.
 *
 * @author Dominik Schmidt
 */
public class TiffReaderTest
{
    @Test
    public void testUnsupportedBigTiffOffsetByteSize() throws Exception
    {
        // A BigTIFF header declaring an offset byte size other than 8 must be reported
        // gracefully as an error, not throw and not read any tags
        byte[] bytes = new byte[]{
            'I', 'I', 0x2B, 0x00,   // little-endian BigTIFF marker
            0x04, 0x00,             // offset byte size = 4 (unsupported; must be 8)
            0x00, 0x00,             // reserved
            0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00  // first IFD offset
        };
        Metadata metadata = new Metadata();
        new TiffReader().processTiff(new ByteArrayReader(bytes),
            new ExifTiffHandler(metadata, null, 0), 0);

        assertTrue("expected a recorded error", metadata.hasErrors());
        // the marker pushes an (empty) IFD0; it must carry the error and no tags
        ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        assertNotNull(ifd0);
        assertEquals(0, ifd0.getTagCount());
        boolean sawOffsetSizeError = false;
        for (String error : ifd0.getErrors()) {
            if (error.contains("offset byte size")) {
                sawOffsetSizeError = true;
            }
        }
        assertTrue("expected offset-byte-size error", sawOffsetSizeError);
    }

    @Test
    public void testBigTiffComponentCountsOutsideIndexRange() throws Exception
    {
        for (int format : new int[] { 1, 13, 18 }) {
            for (long count : new long[] { -1, (long)Integer.MAX_VALUE + 1, (1L << 62) + 1, Long.MAX_VALUE }) {
                ByteBuffer data = createBigTiff(72);
                data.putLong(2);
                putBigTiffTag(data, ExifDirectoryBase.TAG_SUB_IFD_OFFSET, format, count, 64);
                putBigTiffTag(data, ExifDirectoryBase.TAG_IMAGE_WIDTH, 3, 1, 123);
                data.putLong(0);

                Metadata metadata = readBigTiff(data);
                assertHasError(metadata, "Illegal number of components");
                assertEquals(1, metadata.getDirectoryCount());
                assertEquals(123, metadata.getFirstDirectoryOfType(ExifIFD0Directory.class).getInt(ExifDirectoryBase.TAG_IMAGE_WIDTH));
            }
        }
    }

    @Test
    public void testBigTiffByteCountOutsideIndexRange() throws Exception
    {
        ByteBuffer data = createBigTiff(52);
        data.putLong(1);
        putBigTiffTag(data, ExifDirectoryBase.TAG_IMAGE_WIDTH, 3, Integer.MAX_VALUE, 48);
        data.putLong(0);
        assertHasError(readBigTiff(data), "Illegal number of bytes");
    }

    @Test
    public void testBigTiffCustomByteCountsOutsideIndexRange() throws Exception
    {
        for (final long byteCount : new long[] { -1, (long)Integer.MAX_VALUE + 1, Long.MAX_VALUE }) {
            ByteBuffer data = createBigTiff(52);
            data.putLong(1);
            putBigTiffTag(data, ExifDirectoryBase.TAG_IMAGE_WIDTH, 99, 1, 48);
            data.putLong(0);
            Metadata metadata = new Metadata();
            ExifTiffHandler handler = new ExifTiffHandler(metadata, null, 0)
            {
                @Override
                public Long tryCustomProcessFormat(int tagId, int formatCode, long componentCount)
                {
                    return byteCount;
                }
            };
            new TiffReader().processTiff(new ByteArrayReader(data.array()), handler, 0);
            assertHasError(metadata, "Illegal number of bytes");
        }
    }

    @Test
    public void testBigTiffTagOffsetsOutsideData() throws Exception
    {
        for (int headerOffset : new int[] { 0, 8 }) {
            for (long offset : new long[] { -1, (long)Integer.MAX_VALUE + 1, Long.MAX_VALUE, 48 }) {
                ByteBuffer data = createBigTiff(52);
                data.putLong(1);
                putBigTiffTag(data, ExifDirectoryBase.TAG_IMAGE_WIDTH, 1, 9, offset);
                data.putLong(0);
                assertTrue(readBigTiff(data, headerOffset).hasErrors());
            }
        }
    }

    @Test
    public void testBigTiffFirstIfdOffsetsOutsideIndexRange() throws Exception
    {
        for (int headerOffset : new int[] { 0, 8 }) {
            for (long offset : new long[] { -1, (long)Integer.MAX_VALUE + 1, Long.MAX_VALUE }) {
                ByteBuffer data = createBigTiff(32);
                data.putLong(8, offset);
                data.putLong(0).putLong(0);
                assertHasError(readBigTiff(data, headerOffset), "First IFD offset is out of range");
            }
        }
    }

    @Test
    public void testBigTiffFollowerOffsetsOutsideIndexRange() throws Exception
    {
        for (int headerOffset : new int[] { 0, 8 }) {
            for (long offset : new long[] { -1, (long)Integer.MAX_VALUE + 1, Long.MAX_VALUE }) {
                ByteBuffer data = createBigTiff(32);
                data.putLong(0).putLong(offset);
                assertHasError(readBigTiff(data, headerOffset), "Illegal follower IFD offset");
            }
        }
    }

    @Test
    public void testBigTiffReservedHeaderField() throws Exception
    {
        for (ByteOrder order : new ByteOrder[] { ByteOrder.LITTLE_ENDIAN, ByteOrder.BIG_ENDIAN }) {
            ByteBuffer data = createBigTiff(32, order);
            data.putShort(6, (short)1);
            data.putLong(0).putLong(0);
            Metadata metadata = readBigTiff(data);
            assertHasError(metadata, "reserved");
            assertEquals(0, metadata.getFirstDirectoryOfType(ExifIFD0Directory.class).getTagCount());
        }
    }

    @Test
    public void testBigTiffIfdWith65536Entries() throws Exception
    {
        final int count = 65536;
        ByteBuffer data = createBigTiff(16 + 8 + count * 20 + 8);
        data.putLong(count);
        for (int tagId = 0; tagId < count; tagId++)
            putBigTiffTag(data, tagId, 3, 1, 1);
        data.putLong(0);

        Metadata metadata = new Metadata();
        final int[] tagsSeen = { 0 };
        ExifTiffHandler handler = new ExifTiffHandler(metadata, null, 0)
        {
            @Override
            public boolean customProcessTag(int tagOffset, Set<Integer> processedIfdOffsets,
                int tiffHeaderOffset, RandomAccessReader reader, int tagId, int byteCount)
            {
                assertEquals(tagsSeen[0]++, tagId);
                return true;
            }
        };
        new TiffReader().processTiff(new ByteArrayReader(data.array()), handler, 0);
        assertFalse(metadata.hasErrors());
        assertEquals(count, tagsSeen[0]);
    }

    @Test
    public void testBigTiffInvalidIfdEntryCounts() throws Exception
    {
        for (long count : new long[] { -1, 65536, Long.MAX_VALUE }) {
            ByteBuffer data = createBigTiff(32);
            data.putLong(count).putLong(0);
            Metadata metadata = readBigTiff(data);
            assertTrue(metadata.hasErrors());
            assertEquals(0, metadata.getFirstDirectoryOfType(ExifIFD0Directory.class).getTagCount());
        }
    }

    @Test
    public void testBigTiffSubIfdArrayRecoversAfterInvalidOffset() throws Exception
    {
        for (int headerOffset : new int[] { 0, 8 }) {
            for (long offset : new long[] { -1, (long)Integer.MAX_VALUE + 1, Long.MAX_VALUE, 120 }) {
                ByteBuffer data = createBigTiff(104);
                data.putLong(2);
                putBigTiffTag(data, ExifDirectoryBase.TAG_SUB_IFD_OFFSET, 18, 2, 72);
                putBigTiffTag(data, ExifDirectoryBase.TAG_IMAGE_WIDTH, 3, 1, 123);
                data.putLong(0);
                data.putLong(offset).putLong(88);
                data.putLong(0).putLong(0);

                Metadata metadata = readBigTiff(data, headerOffset);
                assertTrue(metadata.hasErrors());
                ExifIFD0Directory root = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                assertEquals(123, root.getInt(ExifDirectoryBase.TAG_IMAGE_WIDTH));
                assertEquals(2, metadata.getDirectoriesOfType(ExifSubIFDDirectory.class).size());
                for (ExifSubIFDDirectory child : metadata.getDirectoriesOfType(ExifSubIFDDirectory.class))
                    assertEquals(root, child.getParent());
            }
        }
    }

    @Test
    public void testBigTiffSubIfdPointerFormats() throws Exception
    {
        for (int format : new int[] { 4, 13, 16, 18 }) {
            for (int count : new int[] { 1, 2 }) {
                ByteBuffer data = createBigTiff(112);
                data.putLong(1);
                long value = 80;
                if (count == 2)
                    value = format == 4 || format == 13 ? (96L << 32) | 80 : 64;
                putBigTiffTag(data, ExifDirectoryBase.TAG_SUB_IFD_OFFSET, format, count, value);
                data.putLong(0);
                data.position(64);
                data.putLong(80).putLong(96);
                data.putLong(0).putLong(0);
                data.putLong(0).putLong(0);

                Metadata metadata = readBigTiff(data);
                assertFalse(metadata.hasErrors());
                assertEquals(count, metadata.getDirectoriesOfType(ExifSubIFDDirectory.class).size());
            }
        }
    }

    @Test
    public void testBigTiffInlineRationalWithNonzeroHeaderOffset() throws Exception
    {
        for (ByteOrder order : new ByteOrder[] { ByteOrder.LITTLE_ENDIAN, ByteOrder.BIG_ENDIAN }) {
            ByteBuffer data = createBigTiff(52, order);
            data.putLong(1);
            data.putShort((short)ExifDirectoryBase.TAG_X_RESOLUTION).putShort((short)5).putLong(1);
            data.putInt(300).putInt(1).putLong(0);
            Metadata metadata = readBigTiff(data, 8);
            assertFalse(metadata.hasErrors());
            assertEquals(300, metadata.getFirstDirectoryOfType(ExifIFD0Directory.class).getInt(ExifDirectoryBase.TAG_X_RESOLUTION));
        }
    }

    private static ByteBuffer createBigTiff(int size)
    {
        return createBigTiff(size, ByteOrder.LITTLE_ENDIAN);
    }

    private static ByteBuffer createBigTiff(int size, ByteOrder order)
    {
        ByteBuffer data = ByteBuffer.allocate(size).order(order);
        byte marker = (byte)(order == ByteOrder.LITTLE_ENDIAN ? 'I' : 'M');
        data.put(marker).put(marker).putShort((short)43).putShort((short)8).putShort((short)0).putLong(16);
        return data;
    }

    private static void putBigTiffTag(ByteBuffer data, int tagId, int format, long count, long value)
    {
        data.putShort((short)tagId).putShort((short)format).putLong(count).putLong(value);
    }

    private static Metadata readBigTiff(ByteBuffer data) throws Exception
    {
        return readBigTiff(data, 0);
    }

    private static Metadata readBigTiff(ByteBuffer data, int headerOffset) throws Exception
    {
        ByteBuffer prefixed = ByteBuffer.allocate(data.capacity() + headerOffset);
        prefixed.position(headerOffset);
        prefixed.put(data.array());
        Metadata metadata = new Metadata();
        new TiffReader().processTiff(new ByteArrayReader(prefixed.array()),
            new ExifTiffHandler(metadata, null, headerOffset), headerOffset);
        return metadata;
    }

    private static void assertHasError(Metadata metadata, String message)
    {
        for (Directory directory : metadata.getDirectories())
            for (String error : directory.getErrors())
                if (error.contains(message))
                    return;
        fail("Expected error containing: " + message);
    }
}

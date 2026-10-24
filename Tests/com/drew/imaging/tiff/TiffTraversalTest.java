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
 *    https://github.com/drewnoakes/metadata-extractor/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.imaging.tiff;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifImageDirectory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifTiffHandler;
import com.drew.metadata.file.FileTypeDirectory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TiffTraversalTest
{
    @Test
    public void testLongFollowerChain() throws Exception
    {
        assertLongFollowerChain(false);
    }

    @Test
    public void testLongBigTiffFollowerChain() throws Exception
    {
        assertLongFollowerChain(true);
    }

    private static void assertLongFollowerChain(boolean bigTiff) throws Exception
    {
        int count = 65568;
        ByteBuffer data = createTiff((bigTiff ? 16 : 8) + (bigTiff ? 56 : 30) * count, bigTiff);
        for (int i = 0; i < count; i++) {
            if (bigTiff)
                data.putLong(2);
            else
                data.putShort((short)2);
            putTag(data, ExifDirectoryBase.TAG_IMAGE_WIDTH, i + 1, bigTiff);
            putTag(data, ExifDirectoryBase.TAG_PAGE_NUMBER, i, bigTiff);
            if (bigTiff)
                data.putLong(i == count - 1 ? 0 : data.position() + 8);
            else
                data.putInt(i == count - 1 ? 0 : data.position() + 4);
        }

        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(data.array()));
        assertFalse(metadata.hasErrors());
        assertEquals(count - 1, metadata.getDirectoriesOfType(ExifImageDirectory.class).size());
        int width = 1;
        for (Directory directory : metadata.getDirectories()) {
            if (directory instanceof ExifIFD0Directory || directory instanceof ExifImageDirectory)
                assertEquals(width++, directory.getInt(ExifDirectoryBase.TAG_IMAGE_WIDTH));
        }
        assertEquals(count + 1, width);
    }

    @Test
    public void testDeepSubIfdChain() throws Exception
    {
        assertDeepSubIfdChain(false, false);
    }

    @Test
    public void testDeepSubIfdChainInJpeg() throws Exception
    {
        assertDeepSubIfdChain(true, false);
    }

    @Test
    public void testDeepBigTiffSubIfdChain() throws Exception
    {
        assertDeepSubIfdChain(false, true);
    }

    private static void assertDeepSubIfdChain(boolean jpeg, boolean bigTiff) throws Exception
    {
        int count = 3600;
        ByteBuffer data = createTiff((bigTiff ? 16 : 8) + (bigTiff ? 36 : 18) * count, bigTiff);
        for (int i = 0; i < count; i++) {
            if (bigTiff)
                data.putLong(1);
            else
                data.putShort((short)1);
            putTag(data, i == count - 1 ? ExifDirectoryBase.TAG_IMAGE_HEIGHT : ExifDirectoryBase.TAG_SUB_IFD_OFFSET,
                i == count - 1 ? 123 : data.position() + (bigTiff ? 28 : 16), bigTiff);
            if (bigTiff)
                data.putLong(0);
            else
                data.putInt(0);
        }

        Metadata metadata;
        if (jpeg) {
            ByteBuffer image = ByteBuffer.allocate(data.capacity() + 14);
            image.putShort((short)0xFFD8).putShort((short)0xFFE1);
            image.putShort((short)(data.capacity() + 8));
            image.put(new byte[] { 'E', 'x', 'i', 'f', 0, 0 }).put(data.array());
            image.putShort((short)0xFFD9);
            metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(image.array()));
            assertEquals("image/jpeg", metadata.getFirstDirectoryOfType(FileTypeDirectory.class).getString(FileTypeDirectory.TAG_DETECTED_FILE_MIME_TYPE));
        } else {
            metadata = readTiff(data);
        }
        assertFalse(metadata.hasErrors());
        assertEquals(count - 1, metadata.getDirectoriesOfType(ExifSubIFDDirectory.class).size());
        Directory parent = null;
        int directoryCount = 0;
        for (Directory directory : metadata.getDirectories()) {
            if (!(directory instanceof ExifIFD0Directory) && !(directory instanceof ExifSubIFDDirectory))
                continue;
            assertSame(parent, directory.getParent());
            parent = directory;
            directoryCount++;
        }
        assertEquals(count, directoryCount);
        assertNotNull(parent);
        assertEquals(123, parent.getInt(ExifDirectoryBase.TAG_IMAGE_HEIGHT));
    }

    @Test
    public void testSubIfdArrayAndFollowerOrder() throws Exception
    {
        ByteBuffer data = createTiff(112);
        data.putShort((short)3);
        putTag(data, ExifDirectoryBase.TAG_IMAGE_WIDTH, 1);
        data.putShort((short)ExifDirectoryBase.TAG_SUB_IFD_OFFSET).putShort((short)4).putInt(2).putInt(50);
        putTag(data, ExifDirectoryBase.TAG_IMAGE_HEIGHT, 9);
        data.putInt(94);
        data.putInt(58).putInt(76);
        for (int i = 2; i <= 4; i++) {
            data.putShort((short)1);
            putTag(data, ExifDirectoryBase.TAG_IMAGE_WIDTH, i);
            data.putInt(0);
        }

        Metadata metadata = new Metadata();
        TrackingHandler handler = new TrackingHandler(metadata);
        new TiffReader().processTiff(new ByteArrayReader(data.array()), handler, 0);

        assertFalse(metadata.hasErrors());
        assertEquals(Arrays.asList(1L, 2L, 3L, 4L), handler.widths);
        assertEquals(Arrays.asList("Exif SubIFD", "Exif SubIFD", "Exif Thumbnail", "Exif IFD0"), handler.endings);
        assertEquals(4, metadata.getDirectoryCount());
        Directory root = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        assertNotNull(root);
        assertEquals(9, root.getInt(ExifDirectoryBase.TAG_IMAGE_HEIGHT));
        for (Directory directory : metadata.getDirectories())
            if (directory != root)
                assertSame(root, directory.getParent());
        assertNull(handler.currentDirectory());
    }

    @Test
    public void testCyclicSubIfd() throws Exception
    {
        ByteBuffer data = createTiff(38);
        data.putShort((short)2);
        putTag(data, ExifDirectoryBase.TAG_SUB_IFD_OFFSET, 8);
        putTag(data, ExifDirectoryBase.TAG_IMAGE_WIDTH, 123);
        data.putInt(0);

        Metadata metadata = new Metadata();
        TrackingHandler handler = new TrackingHandler(metadata);
        new TiffReader().processTiff(new ByteArrayReader(data.array()), handler, 0);

        assertFalse(metadata.hasErrors());
        assertEquals(2, metadata.getDirectoryCount());
        assertEquals(123, metadata.getFirstDirectoryOfType(ExifIFD0Directory.class).getInt(ExifDirectoryBase.TAG_IMAGE_WIDTH));
        assertEquals(Arrays.asList("Exif SubIFD", "Exif IFD0"), handler.endings);
        assertNull(handler.currentDirectory());
    }

    @Test
    public void testInvalidSubIfdRestoresParent() throws Exception
    {
        ByteBuffer data = createTiff(38);
        data.putShort((short)2);
        putTag(data, ExifDirectoryBase.TAG_SUB_IFD_OFFSET, 1000);
        putTag(data, ExifDirectoryBase.TAG_IMAGE_WIDTH, 123);
        data.putInt(0);

        Metadata metadata = readTiff(data);
        assertTrue(metadata.hasErrors());
        assertEquals(123, metadata.getFirstDirectoryOfType(ExifIFD0Directory.class).getInt(ExifDirectoryBase.TAG_IMAGE_WIDTH));
        Directory child = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        assertNotNull(child);
        assertEquals("Ignored IFD marked to start outside data segment", child.getErrors().iterator().next());
    }

    @Test
    public void testByteOrderRestoredAfterSubIfd() throws Exception
    {
        ByteBuffer data = createMixedByteOrderTiff();
        Metadata metadata = new Metadata();
        ByteArrayReader reader = new ByteArrayReader(data.array());
        TrackingHandler handler = new TrackingHandler(metadata);
        new TiffReader().processTiff(reader, handler, 0);

        assertFalse(metadata.hasErrors());
        assertFalse(reader.isMotorolaByteOrder());
        assertEquals(Arrays.asList(456L, 123L), handler.widths);
        assertEquals(Arrays.asList("Exif SubIFD", "Exif IFD0"), handler.endings);
        assertNull(handler.currentDirectory());
    }

    @Test
    public void testIOExceptionUnwindsIfdsAndByteOrder() throws Exception
    {
        ByteArrayReader reader = new ByteArrayReader(createMixedByteOrderTiff().array())
        {
            @Override
            public byte getByte(int index) throws IOException
            {
                if (index == 48)
                    throw new IOException("Read failed");
                return super.getByte(index);
            }
        };
        TrackingHandler handler = new TrackingHandler(new Metadata());
        try {
            new TiffReader().processTiff(reader, handler, 0);
            fail("Expected IOException");
        } catch (IOException e) {
            assertEquals("Read failed", e.getMessage());
        }
        assertFalse(reader.isMotorolaByteOrder());
        assertEquals(Arrays.asList("Exif SubIFD", "Exif IFD0"), handler.endings);
        assertNull(handler.currentDirectory());
    }

    private static ByteBuffer createMixedByteOrderTiff()
    {
        ByteBuffer data = createTiff(56);
        data.putShort((short)2);
        putTag(data, ExifDirectoryBase.TAG_SUB_IFD_OFFSET, 38);
        putTag(data, ExifDirectoryBase.TAG_IMAGE_WIDTH, 123);
        data.putInt(0);
        data.order(ByteOrder.BIG_ENDIAN).putShort((short)1);
        putTag(data, ExifDirectoryBase.TAG_IMAGE_WIDTH, 456);
        data.putInt(0);
        return data;
    }

    private static class TrackingHandler extends ExifTiffHandler
    {
        final List<Long> widths = new ArrayList<Long>();
        final List<String> endings = new ArrayList<String>();

        TrackingHandler(Metadata metadata)
        {
            super(metadata, null, 0);
        }

        @Override
        public void setInt32u(int tagId, long value)
        {
            if (tagId == ExifDirectoryBase.TAG_IMAGE_WIDTH)
                widths.add(value);
            super.setInt32u(tagId, value);
        }

        @Override
        public void endingIFD()
        {
            endings.add(_currentDirectory.getName());
            super.endingIFD();
        }

        Directory currentDirectory()
        {
            return _currentDirectory;
        }
    }

    private static ByteBuffer createTiff(int size)
    {
        return createTiff(size, false);
    }

    private static ByteBuffer createTiff(int size, boolean bigTiff)
    {
        ByteBuffer data = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
        data.put((byte)'I').put((byte)'I').putShort((short)(bigTiff ? 43 : 42));
        if (bigTiff)
            data.putShort((short)8).putShort((short)0).putLong(16);
        else
            data.putInt(8);
        return data;
    }

    private static void putTag(ByteBuffer data, int tagId, int value)
    {
        putTag(data, tagId, value, false);
    }

    private static void putTag(ByteBuffer data, int tagId, int value, boolean bigTiff)
    {
        data.putShort((short)tagId).putShort((short)(bigTiff && tagId == ExifDirectoryBase.TAG_SUB_IFD_OFFSET ? 18 : 4));
        if (bigTiff)
            data.putLong(1).putLong(value);
        else
            data.putInt(1).putInt(value);
    }

    private static Metadata readTiff(ByteBuffer data) throws Exception
    {
        Metadata metadata = new Metadata();
        new TiffReader().processTiff(new ByteArrayReader(data.array()), new ExifTiffHandler(metadata, null, 0), 0);
        return metadata;
    }
}

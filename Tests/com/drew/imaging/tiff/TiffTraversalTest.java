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
import com.drew.metadata.exif.PanasonicRawIFD0Directory;
import com.drew.metadata.exif.makernotes.OlympusMakernoteDirectory;
import com.drew.metadata.file.FileTypeDirectory;
import com.drew.metadata.photoshop.PhotoshopDirectory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    @Test
    public void testNestedMakernoteIfdsAtLimit() throws Exception
    {
        assertNestedOlympusMainInfo(30, 31, false);
    }

    @Test
    public void testNestedMakernoteIfdsBeyondLimit() throws Exception
    {
        assertNestedOlympusMainInfo(31, 32, true);
    }

    @Test
    public void testDeeplyNestedMakernoteIfds() throws Exception
    {
        assertNestedOlympusMainInfo(10000, 32, true);
    }

    /**
     * Each Olympus IFD holds an inline Main Info sub-IFD, which ExifTiffHandler reads with a nested
     * processIfd call, followed by a quality tag that must stay in that IFD's own directory.
     */
    private static void assertNestedOlympusMainInfo(int levels, int expectedDirectories, boolean expectLimit) throws Exception
    {
        ByteBuffer data = createTiff(52 + 30 * (levels + 1));
        data.putShort((short)1);
        putTag(data, ExifIFD0Directory.TAG_EXIF_SUB_IFD_OFFSET, 26);
        data.putInt(0);
        data.putShort((short)1);
        data.putShort((short)ExifDirectoryBase.TAG_MAKERNOTE).putShort((short)7).putInt(8).putInt(44);
        data.putInt(0);
        data.put(new byte[] { 'O', 'L', 'Y', 'M', 'P', 0, 1, 0 });
        for (int i = 0; i <= levels; i++) {
            int start = data.position();
            data.putShort((short)(i < levels ? 2 : 1));
            if (i < levels)
                data.putShort((short)OlympusMakernoteDirectory.TAG_MAIN_INFO).putShort((short)7).putInt(30).putInt(start + 30);
            data.putShort((short)OlympusMakernoteDirectory.TAG_JPEG_QUALITY).putShort((short)3).putInt(1).putInt(i);
            data.putInt(0);
            data.position(start + 30);
        }

        Metadata metadata = readTiff(data);
        List<OlympusMakernoteDirectory> directories = new ArrayList<OlympusMakernoteDirectory>(
            metadata.getDirectoriesOfType(OlympusMakernoteDirectory.class));
        assertEquals(expectedDirectories, directories.size());
        int processed = expectLimit ? expectedDirectories - 1 : expectedDirectories;
        for (int i = 0; i < processed; i++)
            assertEquals(i, directories.get(i).getInt(OlympusMakernoteDirectory.TAG_JPEG_QUALITY));
        if (expectLimit) {
            OlympusMakernoteDirectory skipped = directories.get(processed);
            assertFalse(skipped.containsTag(OlympusMakernoteDirectory.TAG_JPEG_QUALITY));
            assertEquals(1, skipped.getErrorCount());
            assertEquals("IFD nesting too deep", skipped.getErrors().iterator().next());
        } else {
            assertFalse(metadata.hasErrors());
        }
    }

    @Test
    public void testDeeplyNestedPhotoshopExif() throws Exception
    {
        // A TIFF whose IFD0 Photoshop tag holds Exif data 1 (0x0422), which is another such TIFF.
        int levels = 2000;
        ByteBuffer data = ByteBuffer.allocate(14 + 38 * levels);
        for (int i = levels; i > 0; i--) {
            int innerTiff = 14 + 38 * (i - 1);
            data.put((byte)'M').put((byte)'M').putShort((short)42).putInt(8);
            data.putShort((short)1);
            data.putShort((short)ExifDirectoryBase.TAG_PHOTOSHOP_SETTINGS).putShort((short)7).putInt(12 + innerTiff).putInt(26);
            data.putInt(0);
            data.put(new byte[] { '8', 'B', 'I', 'M' }).putShort((short)PhotoshopDirectory.TAG_EXIF_DATA_1).putShort((short)0).putInt(innerTiff);
        }
        data.put((byte)'M').put((byte)'M').putShort((short)42).putInt(8).putShort((short)0).putInt(0);

        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(data.array()));
        assertEquals(32, metadata.getDirectoriesOfType(PhotoshopDirectory.class).size());
        assertNestingLimitReported(metadata.getDirectoriesOfType(ExifIFD0Directory.class), 33);
    }

    @Test
    public void testDeeplyNestedJpgFromRaw() throws Exception
    {
        // A JPEG whose Exif is a Panasonic RAW IFD0 holding JpgFromRaw, which is another such JPEG.
        int levels = 1600;
        ByteBuffer data = ByteBuffer.allocate(4 + 40 * levels);
        for (int i = levels; i > 0; i--) {
            int innerJpeg = 4 + 40 * (i - 1);
            data.putShort((short)0xFFD8).putShort((short)0xFFE1).putShort((short)(34 + innerJpeg));
            data.put(new byte[] { 'E', 'x', 'i', 'f', 0, 0 });
            data.put((byte)'M').put((byte)'M').putShort((short)0x55).putInt(8);
            data.putShort((short)1);
            data.putShort((short)PanasonicRawIFD0Directory.TagJpgFromRaw).putShort((short)7).putInt(innerJpeg).putInt(26);
            data.putInt(0);
        }
        data.putShort((short)0xFFD8).putShort((short)0xFFD9);
        for (int i = 0; i < levels; i++)
            data.putShort((short)0xFFD9);

        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(data.array()));
        assertNestingLimitReported(metadata.getDirectoriesOfType(PanasonicRawIFD0Directory.class), 33);
    }

    private static void assertNestingLimitReported(Collection<? extends Directory> ifd0Directories, int expectedCount)
    {
        assertEquals(expectedCount, ifd0Directories.size());
        int errors = 0;
        for (Directory directory : ifd0Directories) {
            if (directory.hasErrors()) {
                assertEquals(1, directory.getErrorCount());
                assertEquals("IFD nesting too deep", directory.getErrors().iterator().next());
                errors++;
            }
        }
        assertEquals(1, errors);
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

/*
 * Copyright 2002-2016 Drew Noakes
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
package com.drew.metadata.exif;

import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.filter.MetadataFilter;
import com.drew.tools.FileUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * JUnit test case for class ExifReader.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ExifReaderTest
{
    @NotNull
    public static Metadata processBytes(@Nullable MetadataFilter filter, @NotNull String filePath) throws IOException
    {
        Metadata metadata = new Metadata();
        byte[] bytes = FileUtil.readBytes(filePath);
        if (filter == null)
            new ExifReader().extract(new ByteArrayReader(bytes), metadata, ExifReader.JPEG_SEGMENT_PREAMBLE.length(), (Directory) null);
        else
            new ExifReader().extract(new ByteArrayReader(bytes), metadata, ExifReader.JPEG_SEGMENT_PREAMBLE.length(), null, filter);
        return metadata;
    }

    @NotNull
    public static <T extends Directory> T processBytes(@NotNull String filePath, @NotNull Class<T> directoryClass) throws IOException
    {
        T directory = processBytes(null, filePath).getFirstDirectoryOfType(directoryClass);
        assertNotNull(directory);
        return directory;
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testExtractWithNullDataThrows() throws Exception
    {
        try{
            new ExifReader().readJpegSegments(null, new Metadata(), JpegSegmentType.APP1);
            fail("Exception expected");
        } catch (NullPointerException npe) {
            // passed
        }
    }

    @Test
    public void testLoadFujifilmJpeg() throws Exception
    {
        ExifSubIFDDirectory directory = ExifReaderTest.processBytes("Tests/Data/withExif.jpg.app1", ExifSubIFDDirectory.class);

        final String description = directory.getDescription(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
        assertNotNull(description);
        assertEquals("80", description);
        // TODO decide if this should still be returned -- it was being calculated upon setting of a related tag
//      assertEquals("F9", directory.getDescription(ExifSubIFDDirectory.TAG_APERTURE));
    }

    @Test
    public void testExtractFilteredMetadata() throws Exception {
        Metadata metadata = processBytes(new MetadataFilter() {

            @Override
            public boolean tagFilter(Directory directory, int tagType) {
                return
                    directory instanceof ExifSubIFDDirectory && tagType != ExifSubIFDDirectory.TAG_EXPOSURE_PROGRAM && tagType != ExifSubIFDDirectory.TAG_WHITE_BALANCE ||
                    directory instanceof ExifInteropDirectory && tagType != ExifInteropDirectory.TAG_INTEROP_INDEX ||
                    directory instanceof ExifThumbnailDirectory;
            }

            @Override
            public boolean directoryFilter(Class<? extends Directory> directory) {
                return directory == ExifSubIFDDirectory.class || directory == ExifInteropDirectory.class || directory == ExifThumbnailDirectory.class;
            }
        }, "Tests/Data/withExif.jpg.app1");

        assertEquals(3, metadata.getDirectoryCount());
        Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        assertNotNull(directory);
        assertEquals(19, directory.getTagCount());
        assertFalse(directory.containsTag(ExifSubIFDDirectory.TAG_EXPOSURE_PROGRAM));
        assertFalse(directory.containsTag(ExifSubIFDDirectory.TAG_WHITE_BALANCE));
        assertTrue(directory.containsTag(ExifSubIFDDirectory.TAG_EXIF_VERSION));
        assertTrue(directory.containsTag(ExifSubIFDDirectory.TAG_COMPRESSED_AVERAGE_BITS_PER_PIXEL));
        assertEquals("2.10", directory.getDescription(ExifSubIFDDirectory.TAG_EXIF_VERSION));
        assertEquals(2, directory.getInt(ExifSubIFDDirectory.TAG_COMPRESSED_AVERAGE_BITS_PER_PIXEL));
        assertNull(metadata.getFirstDirectoryOfType(ExifIFD0Directory.class));

        metadata = processBytes(new MetadataFilter() {

            @Override
            public boolean tagFilter(Directory directory, int tagType) {
                return false;
            }

            @Override
            public boolean directoryFilter(Class<? extends Directory> directory) {
                return true;
            }

        }, "Tests/Data/withExif.jpg.app1");
        assertEquals(4, metadata.getDirectoryCount());
        for (Directory dir : metadata.getDirectories()) {
            assertEquals(0, dir.getTagCount());
        }

        metadata = processBytes(new MetadataFilter() {

            @Override
            public boolean tagFilter(Directory directory, int tagType) {
                return true;
            }

            @Override
            public boolean directoryFilter(Class<? extends Directory> directory) {
                return false;
            }

        }, "Tests/Data/withExif.jpg.app1");
        assertEquals(0, metadata.getDirectoryCount());
    }

    @Test
    public void testReadJpegSegmentWithNoExifData() throws Exception
    {
        byte[] badExifData = new byte[]{ 1,2,3,4,5,6,7,8,9,10 };
        Metadata metadata = new Metadata();
        ArrayList<byte[]> segments = new ArrayList<byte[]>();
        segments.add(badExifData);
        new ExifReader().readJpegSegments(segments, metadata, JpegSegmentType.APP1);
        assertEquals(0, metadata.getDirectoryCount());
        assertFalse(metadata.hasErrors());
    }

    @Test
    public void testCrashRegressionTest() throws Exception
    {
        // This image was created via a resize in ACDSee.
        // It seems to have a reference to an IFD starting outside the data segment.
        // I've noticed that ACDSee reports a Comment for this image, yet ExifReader doesn't report one.
        ExifSubIFDDirectory directory = ExifReaderTest.processBytes("Tests/Data/crash01.jpg.app1", ExifSubIFDDirectory.class);

        assertTrue(directory.getTagCount() > 0);
    }

    @Test
    public void testDateTime() throws Exception
    {
        ExifIFD0Directory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifIFD0Directory.class);

        assertEquals("2002:11:27 18:00:35", directory.getString(ExifIFD0Directory.TAG_DATETIME));
    }

    @Test
    public void testThumbnailXResolution() throws Exception
    {
        ExifThumbnailDirectory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifThumbnailDirectory.class);

        Rational rational = directory.getRational(ExifThumbnailDirectory.TAG_X_RESOLUTION);
        assertNotNull(rational);
        assertEquals(72, rational.getNumerator());
        assertEquals(1, rational.getDenominator());
    }

    @Test
    public void testThumbnailYResolution() throws Exception
    {
        ExifThumbnailDirectory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifThumbnailDirectory.class);

        Rational rational = directory.getRational(ExifThumbnailDirectory.TAG_Y_RESOLUTION);
        assertNotNull(rational);
        assertEquals(72, rational.getNumerator());
        assertEquals(1, rational.getDenominator());
    }

    @Test
    public void testThumbnailOffset() throws Exception
    {
        ExifThumbnailDirectory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifThumbnailDirectory.class);

        assertEquals(192, directory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET));
    }

    @Test
    public void testThumbnailLength() throws Exception
    {
        ExifThumbnailDirectory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifThumbnailDirectory.class);

        assertEquals(2970, directory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_LENGTH));
    }

    @Test
    public void testThumbnailData() throws Exception
    {
        ExifThumbnailDirectory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifThumbnailDirectory.class);
        byte[] thumbnailData = directory.getThumbnailData();
        assertNotNull(thumbnailData);
        assertEquals(2970, thumbnailData.length);
    }

    @Test
    public void testCompression() throws Exception
    {
        ExifThumbnailDirectory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifThumbnailDirectory.class);

        // 6 means JPEG compression
        assertEquals(6, directory.getInt(ExifThumbnailDirectory.TAG_COMPRESSION));
    }

    @Test
    public void testStackOverflowOnRevisitationOfSameDirectory() throws Exception
    {
        // An error has been discovered in Exif data segments where a directory is referenced
        // repeatedly.  Thanks to Alistair Dickie for providing the sample data used in this
        // unit test.

        Metadata metadata = processBytes(null, "Tests/Data/recursiveDirectories.jpg.app1");

        // Mostly we're just happy at this point that we didn't get stuck in an infinite loop.

        assertEquals(5, metadata.getDirectoryCount());
    }

    @Test
    public void testDifferenceImageAndThumbnailOrientations() throws Exception
    {
        // This metadata contains different orientations for the thumbnail and the main image.
        // These values used to be merged into a single directory, causing errors.
        // This unit test demonstrates correct behaviour.
        Metadata metadata = processBytes(null, "Tests/Data/repeatedOrientationTagWithDifferentValues.jpg.app1");
        ExifIFD0Directory ifd0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        ExifThumbnailDirectory thumbnailDirectory = metadata.getFirstDirectoryOfType(ExifThumbnailDirectory.class);

        assertNotNull(ifd0Directory);
        assertNotNull(thumbnailDirectory);

        assertEquals(1, ifd0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION));
        assertEquals(8, thumbnailDirectory.getInt(ExifThumbnailDirectory.TAG_ORIENTATION));
    }

/*
    public void testUncompressedYCbCrThumbnail() throws Exception
    {
        String fileName = "withUncompressedYCbCrThumbnail.jpg";
        String thumbnailFileName = "withUncompressedYCbCrThumbnail.bmp";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        ExifSubIFDDirectory directory = (ExifSubIFDDirectory)metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        directory.writeThumbnail(thumbnailFileName);

        fileName = "withUncompressedYCbCrThumbnail2.jpg";
        thumbnailFileName = "withUncompressedYCbCrThumbnail2.bmp";
        metadata = new ExifReader(new File(fileName)).extract();
        directory = (ExifSubIFDDirectory)metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        directory.writeThumbnail(thumbnailFileName);
        fileName = "withUncompressedYCbCrThumbnail3.jpg";
        thumbnailFileName = "withUncompressedYCbCrThumbnail3.bmp";
        metadata = new ExifReader(new File(fileName)).extract();
        directory = (ExifSubIFDDirectory)metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        directory.writeThumbnail(thumbnailFileName);
        fileName = "withUncompressedYCbCrThumbnail4.jpg";
        thumbnailFileName = "withUncompressedYCbCrThumbnail4.bmp";
        metadata = new ExifReader(new File(fileName)).extract();
        directory = (ExifSubIFDDirectory)metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        directory.writeThumbnail(thumbnailFileName);
    }

    public void testUncompressedRGBThumbnail() throws Exception
    {
        String fileName = "withUncompressedRGBThumbnail.jpg";
        String thumbnailFileName = "withUncompressedRGBThumbnail.bmp";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        ExifSubIFDDirectory directory = (ExifSubIFDDirectory)metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        directory.writeThumbnail(thumbnailFileName);
    }
*/
}

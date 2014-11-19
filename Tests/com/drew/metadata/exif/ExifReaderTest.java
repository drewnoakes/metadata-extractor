/*
 * Copyright 2002-2014 Drew Noakes
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.google.common.io.Files;

/**
 * JUnit test case for class ExifReader.
 *
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ExifReaderTest
{
    @NotNull
    public static Metadata processBytes(@NotNull final String filePath) throws IOException
    {
        final Metadata metadata = new Metadata();
		new ExifReader().extract(Files.toByteArray(new File(filePath)), metadata, JpegSegmentType.APP1);
        return metadata;
    }

    @NotNull
    public static <T extends Directory> T processBytes(@NotNull final String filePath, @NotNull final Class<T> directoryClass) throws IOException
    {
        final T directory = processBytes(filePath).getDirectory(directoryClass);
        assertNotNull(directory);
        return directory;
    }

    @Test
    public void testExtractWithNullDataThrows() throws Exception
    {
        try{
            new ExifReader().extract(null, new Metadata(), JpegSegmentType.APP1);
            Assert.fail("Exception expected");
        } catch (final NullPointerException npe) {
            // passed
        }
    }

    @Test
    public void testExtractWithNullMetadataThrows() throws Exception
    {
        try{
            new ExifReader().extract(new byte[10], null, JpegSegmentType.APP1);
            Assert.fail("Exception expected");
        } catch (final NullPointerException npe) {
            // passed
        }
    }

    @Test
    public void testLoadFujifilmJpeg() throws Exception
    {
        final ExifSubIFDDirectory directory = ExifReaderTest.processBytes("Tests/Data/withExif.jpg.app1", ExifSubIFDDirectory.class);

        final String description = directory.getDescription(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
        assertNotNull(description);
        assertEquals("80", description);
        // TODO decide if this should still be returned -- it was being calculated upon setting of a related tag
//      assertEquals("F9", directory.getDescription(ExifSubIFDDirectory.TAG_APERTURE));
    }

    @Test
    public void testLoadJpegWithNoExifData() throws Exception
    {
        final byte[] badExifData = new byte[]{ 1,2,3,4,5,6,7,8,9,10 };
        final Metadata metadata = new Metadata();
        new ExifReader().extract(badExifData, metadata, JpegSegmentType.APP1);
        assertEquals(0, metadata.getDirectoryCount());
    }

    @Test
    public void testCrashRegressionTest() throws Exception
    {
        // This image was created via a resize in ACDSee.
        // It seems to have a reference to an IFD starting outside the data segment.
        // I've noticed that ACDSee reports a Comment for this image, yet ExifReader doesn't report one.
        final ExifSubIFDDirectory directory = ExifReaderTest.processBytes("Tests/Data/crash01.jpg.app1", ExifSubIFDDirectory.class);

        assertTrue(directory.getTagCount() > 0);
    }

    @Test
    public void testDateTime() throws Exception
    {
        final ExifIFD0Directory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifIFD0Directory.class);

        assertEquals("2002:11:27 18:00:35", directory.getString(ExifIFD0Directory.TAG_DATETIME));
    }

    @Test
    public void testThumbnailXResolution() throws Exception
    {
        final ExifThumbnailDirectory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifThumbnailDirectory.class);

		final Rational rational = directory.getRational(ExifCommonDirectoryTags.TAG_X_RESOLUTION);
        assertNotNull(rational);
        assertEquals(72, rational.getNumerator());
        assertEquals(1, rational.getDenominator());
    }

    @Test
    public void testThumbnailYResolution() throws Exception
    {
        final ExifThumbnailDirectory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifThumbnailDirectory.class);

		final Rational rational = directory.getRational(ExifCommonDirectoryTags.TAG_Y_RESOLUTION);
        assertNotNull(rational);
        assertEquals(72, rational.getNumerator());
        assertEquals(1, rational.getDenominator());
    }

    @Test
    public void testThumbnailOffset() throws Exception
    {
        final ExifThumbnailDirectory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifThumbnailDirectory.class);

        assertEquals(192, directory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET));
    }

    @Test
    public void testThumbnailLength() throws Exception
    {
        final ExifThumbnailDirectory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifThumbnailDirectory.class);

        assertEquals(2970, directory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_LENGTH));
    }

    @Test
    public void testThumbnailData() throws Exception
    {
        final ExifThumbnailDirectory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifThumbnailDirectory.class);
        final byte[] thumbnailData = directory.getThumbnailData();
        assertNotNull(thumbnailData);
        assertEquals(2970, thumbnailData.length);
    }

    @Test
    public void testThumbnailCompression() throws Exception
    {
        final ExifThumbnailDirectory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifThumbnailDirectory.class);

        // 6 means JPEG compression
        assertEquals(6, directory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_COMPRESSION));
    }

    @Test
    public void testStackOverflowOnRevisitationOfSameDirectory() throws Exception
    {
        // An error has been discovered in Exif data segments where a directory is referenced
        // repeatedly.  Thanks to Alistair Dickie for providing the sample data used in this
        // unit test.

        final Metadata metadata = processBytes("Tests/Data/recursiveDirectories.jpg.app1");

        // Mostly we're just happy at this point that we didn't get stuck in an infinite loop.

        assertEquals(5, metadata.getDirectoryCount());
    }

    @Test
    public void testDifferenceImageAndThumbnailOrientations() throws Exception
    {
        // This metadata contains different orientations for the thumbnail and the main image.
        // These values used to be merged into a single directory, causing errors.
        // This unit test demonstrates correct behaviour.
        final Metadata metadata = processBytes("Tests/Data/repeatedOrientationTagWithDifferentValues.jpg.app1");
        final ExifIFD0Directory ifd0Directory = metadata.getDirectory(ExifIFD0Directory.class);
        final ExifThumbnailDirectory thumbnailDirectory = metadata.getDirectory(ExifThumbnailDirectory.class);

        assertNotNull(ifd0Directory);
        assertNotNull(thumbnailDirectory);

		assertEquals(1, ifd0Directory.getInt(ExifCommonDirectoryTags.TAG_ORIENTATION));
		assertEquals(8, thumbnailDirectory.getInt(ExifCommonDirectoryTags.TAG_ORIENTATION));
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

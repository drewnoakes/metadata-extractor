/*
 * Copyright 2002-2012 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.metadata.exif;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.Rational;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * JUnit test case for class ExifReader.
 * 
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ExifReaderTest
{
    @Test
    public void testExtractWithNullDataThrows() throws Exception
    {
        try{
            new ExifReader().extract(null, new Metadata());
            Assert.fail("Exception expected");
        } catch (NullPointerException npe) {
            // passed
        }
    }

    @Test
    public void testExtractWithNullMetadataThrows() throws Exception
    {
        try{
            new ExifReader().extract(new ByteArrayReader(new byte[10]), null);
            Assert.fail("Exception expected");
        } catch (NullPointerException npe) {
            // passed
        }
    }

    @Test
    public void testLoadFujiFilmJpeg() throws Exception
    {
        ExifSubIFDDirectory directory = readDirectoryFromJpegFile("withExif.jpg", ExifSubIFDDirectory.class);

        final String description = directory.getDescription(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
        assertNotNull(description);
        assertEquals("80", description);
        // TODO decide if this should still be returned -- it was being calculated upon setting of a related tag
//      assertEquals("F9", directory.getDescription(ExifSubIFDDirectory.TAG_APERTURE));
    }

    @Test
    public void testLoadJpegWithNoExifData() throws Exception
    {
        byte[] badExifData = new byte[]{ 1,2,3,4,5,6,7,8,9,10 };
        Metadata metadata = new Metadata();
        new ExifReader().extract(new ByteArrayReader(badExifData), metadata);
        Directory directory = metadata.getDirectory(ExifSubIFDDirectory.class);

        assertNotNull(directory);
        assertEquals(0, directory.getTagCount());
    }

    @Test
    public void testCrashRegressionTest() throws Exception
    {
        // This image was created via a resize in ACDSee.
        // It seems to have a reference to an IFD starting outside the data segment.
        // I've noticed that ACDSee reports a Comment for this image, yet ExifReader doesn't report one.
        Directory directory = readDirectoryFromJpegFile("crash01.jpg", ExifSubIFDDirectory.class);

        Assert.assertTrue(directory.getTagCount() > 0);
    }

    @Test
    public void testDateTime() throws Exception
    {
        Directory directory = readDirectoryFromJpegFile("manuallyAddedThumbnail.jpg", ExifIFD0Directory.class);

        assertEquals("2002:11:27 18:00:35", directory.getString(ExifIFD0Directory.TAG_DATETIME));
    }

    @Test
    public void testThumbnailXResolution() throws Exception
    {
        Directory directory = readDirectoryFromJpegFile("manuallyAddedThumbnail.jpg", ExifThumbnailDirectory.class);

        Rational rational = directory.getRational(ExifThumbnailDirectory.TAG_X_RESOLUTION);
        assertNotNull(rational);
        assertEquals(72, rational.getNumerator());
        assertEquals(1, rational.getDenominator());
    }

    @Test
    public void testThumbnailYResolution() throws Exception
    {
        Directory directory = readDirectoryFromJpegFile("manuallyAddedThumbnail.jpg", ExifThumbnailDirectory.class);

        Rational rational = directory.getRational(ExifThumbnailDirectory.TAG_Y_RESOLUTION);
        assertNotNull(rational);
        assertEquals(72, rational.getNumerator());
        assertEquals(1, rational.getDenominator());
    }

    @Test
    public void testThumbnailOffset() throws Exception
    {
        Directory directory = readDirectoryFromJpegFile("manuallyAddedThumbnail.jpg", ExifThumbnailDirectory.class);

        assertEquals(192, directory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET));
    }

    @Test
    public void testThumbnailLength() throws Exception
    {
        Directory directory = readDirectoryFromJpegFile("manuallyAddedThumbnail.jpg", ExifThumbnailDirectory.class);

        assertEquals(2970, directory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_LENGTH));
    }

    @Test
    public void testThumbnailData() throws Exception
    {
        ExifThumbnailDirectory directory = readDirectoryFromJpegFile("manuallyAddedThumbnail.jpg", ExifThumbnailDirectory.class);
        final byte[] thumbnailData = directory.getThumbnailData();
        assertNotNull(thumbnailData);
        assertEquals(2970, thumbnailData.length);
    }

    @Test
    public void testThumbnailCompression() throws Exception
    {
        Directory directory = readDirectoryFromJpegFile("manuallyAddedThumbnail.jpg", ExifThumbnailDirectory.class);

        // 6 means JPEG compression
        assertEquals(6, directory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_COMPRESSION));
    }

    @Test
    public void testStackOverflowOnRevisitationOfSameDirectory() throws Exception
    {
        // an error has been discovered in Exif data segments where a directory is referenced
        // repeatedly.  thanks to Alistair Dickie for providing the sample image used in this
        // unit test.
        readDirectoryFromMetadataFile("Tests/com/drew/metadata/exif/recursiveDirectories.metadata", ExifSubIFDDirectory.class);
    }

    @Test
    public void testDifferenceImageAndThumbnailOrientations() throws Exception
    {
        // This metadata contains different orientations for the thumbnail and the main image.
        // These values used to be merged into a single directory, causing errors.
        // This unit test demonstrates correct behaviour.
        final ExifIFD0Directory ifd0Directory = readDirectoryFromMetadataFile("Tests/com/drew/metadata/exif/repeatedOrientationTagWithDifferentValues.metadata", ExifIFD0Directory.class);
        final ExifThumbnailDirectory thumbnailDirectory = readDirectoryFromMetadataFile("Tests/com/drew/metadata/exif/repeatedOrientationTagWithDifferentValues.metadata", ExifThumbnailDirectory.class);

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

    @NotNull
    private static <T extends Directory> T readDirectoryFromJpegFile(String fileName, final Class<T> directoryClass) throws JpegProcessingException, IOException
    {
        InputStream inputStream = ExifReaderTest.class.getResourceAsStream(fileName);
        final byte[] segmentBytes = JpegSegmentReader.readSegments(new StreamReader(inputStream), null).getSegment(JpegSegmentType.APP1);
        inputStream.close();
        assertNotNull(segmentBytes);

        Metadata metadata = new Metadata();
        new ExifReader().extract(new ByteArrayReader(segmentBytes), metadata);
        T directory = metadata.getDirectory(directoryClass);
        assertNotNull(directory);

        return directory;
    }

    @NotNull
    private static <T extends Directory> T readDirectoryFromMetadataFile(String fileName, final Class<T> directoryClass) throws JpegProcessingException, IOException, ClassNotFoundException
    {
        InputStream inputStream = new FileInputStream(fileName);
        assertNotNull(inputStream);
        byte[] segmentBytes = JpegSegmentData.fromFile(inputStream).getSegment(JpegSegmentType.APP1);
        inputStream.close();
        assertNotNull(segmentBytes);

        Metadata metadata = new Metadata();
        new ExifReader().extract(new ByteArrayReader(segmentBytes), metadata);
        final T directory = metadata.getDirectory(directoryClass);
        assertNotNull(directory);
        return directory;
    }
}

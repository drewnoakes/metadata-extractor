/*
 * Copyright 2002-2011 Drew Noakes
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
package com.drew.metadata.exif.test;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.ExifReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

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
            new ExifReader().extract(new byte[10], null);
            Assert.fail("Exception expected");
        } catch (NullPointerException npe) {
            // passed
        }
    }

    @Test
    public void testLoadFujiFilmJpeg() throws Exception
    {
        String jpegWithExif = "Source/com/drew/metadata/exif/test/withExif.jpg";
        Metadata metadata = new Metadata();
        final byte[] data = new JpegSegmentReader(new File(jpegWithExif)).readSegment(JpegSegmentReader.SEGMENT_APP1);
        new ExifReader().extract(data, metadata);
        ExifDirectory directory = metadata.getDirectory(ExifDirectory.class);
        Assert.assertEquals("80", directory.getDescription(ExifDirectory.TAG_ISO_EQUIVALENT));
        // TODO decide if this should still be returned -- it was being calculated upon setting of a related tag
//      assertEquals("F9", directory.getDescription(ExifDirectory.TAG_APERTURE));
    }

    @Test
    public void testLoadJpegWithNoExifData() throws Exception
    {
        byte[] badExifData = new byte[]{ 1,2,3,4,5,6,7,8,9,10 };
        Metadata metadata = new Metadata();
        new ExifReader().extract(badExifData, metadata);
        Directory directory = metadata.getDirectory(ExifDirectory.class);

        Assert.assertNotNull(directory);
        Assert.assertEquals(0, directory.getTagCount());
    }

    @Test
    public void testCrashRegressionTest() throws Exception
    {
        // This image was created via a resize in ACDSee.
        // It seems to have a reference to an IFD starting outside the data segment.
        // I've noticed that ACDSee reports a Comment for this image, yet ExifReader doesn't report one.
        Directory directory = readExifDirectoryFromJpegFile("Source/com/drew/metadata/exif/test/crash01.jpg");

        Assert.assertTrue(directory.getTagCount() > 0);
    }

    @Test
    public void testThumbnailOffset() throws Exception
    {
        Directory directory = readExifDirectoryFromJpegFile("Source/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg");

        Assert.assertEquals(192, directory.getInt(ExifDirectory.TAG_THUMBNAIL_OFFSET));
    }

    @Test
    public void testThumbnailLength() throws Exception
    {
        Directory directory = readExifDirectoryFromJpegFile("Source/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg");

        Assert.assertEquals(2970, directory.getInt(ExifDirectory.TAG_THUMBNAIL_LENGTH));
    }

    @Test
    public void testDateTime() throws Exception
    {
        Directory directory = readExifDirectoryFromJpegFile("Source/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg");

        Assert.assertEquals("2002:11:27 18:00:35", directory.getString(ExifDirectory.TAG_DATETIME));
    }

    @Test
    public void testXResolution() throws Exception
    {
        Directory directory = readExifDirectoryFromJpegFile("Source/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg");

        Rational rational = directory.getRational(ExifDirectory.TAG_X_RESOLUTION);
        Assert.assertNotNull(rational);
        Assert.assertEquals(72, rational.getNumerator());
        Assert.assertEquals(1, rational.getDenominator());
    }

    @Test
    public void testYResolution() throws Exception
    {
        Directory directory = readExifDirectoryFromJpegFile("Source/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg");

        Rational rational = directory.getRational(ExifDirectory.TAG_Y_RESOLUTION);
        Assert.assertNotNull(rational);
        Assert.assertEquals(72, rational.getNumerator());
        Assert.assertEquals(1, rational.getDenominator());
    }

    @Test
    public void testCompression() throws Exception
    {
        Directory directory = readExifDirectoryFromJpegFile("Source/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg");

        // 6 means JPEG compression
        Assert.assertEquals(6, directory.getInt(ExifDirectory.TAG_THUMBNAIL_COMPRESSION));
    }

    @Test
    public void testStackOverflowOnRevisitationOfSameDirectory() throws Exception
    {
        // an error has been discovered in Exif data segments where a directory is referenced
        // repeatedly.  thanks to Alistair Dickie for providing the sample image used in this
        // unit test.
        readExifDirectoryFromMetadataFile("Source/com/drew/metadata/exif/test/recursiveDirectories.metadata");
    }

/*
    public void testUncompressedYCbCrThumbnail() throws Exception
    {
        String fileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail.jpg";
        String thumnailFileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail.bmp";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        ExifDirectory directory = (ExifDirectory)metadata.getOrCreateDirectory(ExifDirectory.class);
        directory.writeThumbnail(thumnailFileName);

        fileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail2.jpg";
        thumnailFileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail2.bmp";
        metadata = new ExifReader(new File(fileName)).extract();
        directory = (ExifDirectory)metadata.getOrCreateDirectory(ExifDirectory.class);
        directory.writeThumbnail(thumnailFileName);
        fileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail3.jpg";
        thumnailFileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail3.bmp";
        metadata = new ExifReader(new File(fileName)).extract();
        directory = (ExifDirectory)metadata.getOrCreateDirectory(ExifDirectory.class);
        directory.writeThumbnail(thumnailFileName);
        fileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail4.jpg";
        thumnailFileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail4.bmp";
        metadata = new ExifReader(new File(fileName)).extract();
        directory = (ExifDirectory)metadata.getOrCreateDirectory(ExifDirectory.class);
        directory.writeThumbnail(thumnailFileName);
    }

    public void testUncompressedRGBThumbnail() throws Exception
    {
        String fileName = "Source/com/drew/metadata/exif/test/withUncompressedRGBThumbnail.jpg";
        String thumnailFileName = "Source/com/drew/metadata/exif/test/withUncompressedRGBThumbnail.bmp";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        ExifDirectory directory = (ExifDirectory)metadata.getOrCreateDirectory(ExifDirectory.class);
        directory.writeThumbnail(thumnailFileName);
    }
*/

    @NotNull
    private static Directory readExifDirectoryFromJpegFile(String fileName) throws JpegProcessingException, IOException
    {
        Metadata metadata = new Metadata();
        byte[] data = new JpegSegmentReader(new File(fileName)).readSegment(JpegSegmentReader.SEGMENT_APP1);
        Assert.assertNotNull(data);
        new ExifReader().extract(data, metadata);
        final ExifDirectory directory = metadata.getDirectory(ExifDirectory.class);
        Assert.assertNotNull(directory);
        return directory;
    }

    @NotNull
    private static Directory readExifDirectoryFromMetadataFile(String fileName) throws JpegProcessingException, IOException, ClassNotFoundException
    {
        Metadata metadata = new Metadata();
        final JpegSegmentData jpegSegmentData = JpegSegmentData.fromFile(new File(fileName));
        byte[] data = jpegSegmentData.getSegment(JpegSegmentReader.SEGMENT_APP1);
        Assert.assertNotNull(data);
        new ExifReader().extract(data, metadata);
        final ExifDirectory directory = metadata.getDirectory(ExifDirectory.class);
        Assert.assertNotNull(directory);
        return directory;
    }
}

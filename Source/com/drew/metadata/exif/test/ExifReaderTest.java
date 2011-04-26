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

import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.ExifReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * JUnit test case for class ExifReader.
 * 
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ExifReaderTest
{
    @Test
    public void testLoadFujiFilmJpeg() throws Exception
    {
        String jpegWithExif = "Source/com/drew/metadata/exif/test/withExif.jpg";
        Metadata metadata = new Metadata();
        new ExifReader(new File(jpegWithExif)).extract(metadata);
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        Assert.assertEquals("80", directory.getDescription(ExifDirectory.TAG_ISO_EQUIVALENT));
        // TODO decide if this should still be returned -- it was being calculated upon setting of a related tag
//      assertEquals("F9", directory.getDescription(ExifDirectory.TAG_APERTURE));
    }

    @Test
    public void testLoadJpegWithoutExifData() throws Exception
    {
        String jpegNoExif = "Source/com/drew/metadata/exif/test/noExif.jpg";
        Metadata metadata = new Metadata();
        new ExifReader(new File(jpegNoExif)).extract(metadata);
        Assert.assertTrue(!metadata.containsDirectory(ExifDirectory.class));
    }

    @Test
    public void testLoadJpegWithBadExifData() throws Exception
    {
        // This test used to ensure an exception was thrown when loading a particular jpeg
        // The intention has since changed, and the API should only throw exceptions in completely
        // fatal situations.  Now, the Metadata object returned has no new tags.
        String jpegBadExif = "Source/com/drew/metadata/exif/test/badExif.jpg"; // Exif data segment doesn't begin with 'Exif'
        Metadata metadata = new Metadata();
        new ExifReader(new File(jpegBadExif)).extract(metadata);
        Assert.assertEquals(0, metadata.getDirectory(ExifDirectory.class).getTagCount());
    }

    @Test
    public void testCrashRegressionTest() throws Exception
    {
        // this image was created via a resize in ACDSee
        // it seems to have a reference to an IFD starting outside the data segment
        // i've noticed that ACDSee reports a Comment for this image, yet ExifReader doesn't report one
        String fileName = "Source/com/drew/metadata/exif/test/crash01.jpg";
        Metadata metadata = new Metadata();
        new ExifReader(new File(fileName)).extract(metadata);
        Assert.assertTrue(metadata.getDirectory(ExifDirectory.class).getTagCount() > 0);
    }

    @Test
    public void testThumbnailOffset() throws Exception
    {
        String fileName = "Source/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg";
        Metadata metadata = new Metadata();
        new ExifReader(new File(fileName)).extract(metadata);
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        Assert.assertEquals(192, directory.getInt(ExifDirectory.TAG_THUMBNAIL_OFFSET));
    }

    @Test
    public void testThumbnailLength() throws Exception
    {
        String fileName = "Source/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg";
        Metadata metadata = new Metadata();
        new ExifReader(new File(fileName)).extract(metadata);
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        Assert.assertEquals(2970, directory.getInt(ExifDirectory.TAG_THUMBNAIL_LENGTH));
    }

    @Test
    public void testDateTime() throws Exception
    {
        String fileName = "Source/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg";
        Metadata metadata = new Metadata();
        new ExifReader(new File(fileName)).extract(metadata);
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        Assert.assertEquals("2002:11:27 18:00:35", directory.getString(ExifDirectory.TAG_DATETIME));
    }

    @Test
    public void testXResolution() throws Exception
    {
        String fileName = "Source/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg";
        Metadata metadata = new Metadata();
        new ExifReader(new File(fileName)).extract(metadata);
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        Rational rational = directory.getRational(ExifDirectory.TAG_X_RESOLUTION);
        Assert.assertEquals(72, rational.getNumerator());
        Assert.assertEquals(1, rational.getDenominator());
    }

    @Test
    public void testYResolution() throws Exception
    {
        String fileName = "Source/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg";
        Metadata metadata = new Metadata();
        new ExifReader(new File(fileName)).extract(metadata);
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        Rational rational = directory.getRational(ExifDirectory.TAG_Y_RESOLUTION);
        Assert.assertEquals(72, rational.getNumerator());
        Assert.assertEquals(1, rational.getDenominator());
    }

    @Test
    public void testCompression() throws Exception
    {
        String fileName = "Source/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg";
        Metadata metadata = new Metadata();
        new ExifReader(new File(fileName)).extract(metadata);
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        // 6 means JPEG compression
        Assert.assertEquals(6, directory.getInt(ExifDirectory.TAG_THUMBNAIL_COMPRESSION));
    }

    @Test
    public void testStackOverflowOnRevisitationOfSameDirectory() throws Exception
    {
        // an error has been discovered in Exif data segments where a directory is referenced
        // repeatedly.  thanks to Alistair Dickie for providing the sample image used in this
        // unit test.
        File metadataFile = new File("Source/com/drew/metadata/exif/test/recursiveDirectories.metadata");
        Metadata metadata = new Metadata();
        new ExifReader(JpegSegmentData.fromFile(metadataFile)).extract(metadata);
        metadata.getDirectory(ExifDirectory.class);
//        String fileName = "Source/com/drew/metadata/exif/test/recursiveDirectories.jpg";
//        Metadata metadata = new ExifReader(new File(fileName)).extract();
//        metadata.getDirectory(ExifDirectory.class);
    }


/*
    public void testUncompressedYCbCrThumbnail() throws Exception
    {
        String fileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail.jpg";
        String thumnailFileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail.bmp";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        ExifDirectory directory = (ExifDirectory)metadata.getDirectory(ExifDirectory.class);
        directory.writeThumbnail(thumnailFileName);

        fileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail2.jpg";
        thumnailFileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail2.bmp";
        metadata = new ExifReader(new File(fileName)).extract();
        directory = (ExifDirectory)metadata.getDirectory(ExifDirectory.class);
        directory.writeThumbnail(thumnailFileName);
        fileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail3.jpg";
        thumnailFileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail3.bmp";
        metadata = new ExifReader(new File(fileName)).extract();
        directory = (ExifDirectory)metadata.getDirectory(ExifDirectory.class);
        directory.writeThumbnail(thumnailFileName);
        fileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail4.jpg";
        thumnailFileName = "Source/com/drew/metadata/exif/test/withUncompressedYCbCrThumbnail4.bmp";
        metadata = new ExifReader(new File(fileName)).extract();
        directory = (ExifDirectory)metadata.getDirectory(ExifDirectory.class);
        directory.writeThumbnail(thumnailFileName);
    }

    public void testUncompressedRGBThumbnail() throws Exception
    {
        String fileName = "Source/com/drew/metadata/exif/test/withUncompressedRGBThumbnail.jpg";
        String thumnailFileName = "Source/com/drew/metadata/exif/test/withUncompressedRGBThumbnail.bmp";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        ExifDirectory directory = (ExifDirectory)metadata.getDirectory(ExifDirectory.class);
        directory.writeThumbnail(thumnailFileName);
    }
*/
}

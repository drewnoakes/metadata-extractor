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
package com.drew.imaging.jpeg.test;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Contains JUnit tests for the JpegSegmentReader class.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegSegmentReaderTest
{
    @Test
    public void testIsJpegWithJpegFile() throws Exception
    {
        File jpeg = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        try {
            new JpegSegmentReader(jpeg);
        } catch (JpegProcessingException e) {
            Assert.fail("Error creating JpegSegmentReader");
        }
    }

    @Test
    public void testIsJpegWithNonJpegFile() throws Exception
    {
        File nonJpeg = new File("Source/com/drew/imaging/jpeg/test/JpegSegmentReaderTest.java");
        try {
            new JpegSegmentReader(nonJpeg);
            Assert.fail("shouldn't be able to construct JpegSegmentReader with non-jpeg file");
        } catch (JpegProcessingException e) {
            // expect exception
        }
    }

    @Test
    public void testReadApp1Segment() throws Exception
    {
        File jpeg = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        JpegSegmentReader segmentReader = new JpegSegmentReader(jpeg);
        byte[] exifData = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APP1);
        Assert.assertTrue("exif data too short", exifData.length > 4);
        Assert.assertEquals("Exif", new String(exifData, 0, 4));
    }

    @Test
    public void testReadDQTSegment() throws Exception
    {
        File jpeg = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        JpegSegmentReader segmentReader = new JpegSegmentReader(jpeg);
        byte[] quantizationTableData = segmentReader.readSegment(JpegSegmentReader.SEGMENT_DQT);
        Assert.assertTrue("shouldn't have zero length quantizationTableData", quantizationTableData.length > 0);
        Assert.assertTrue("quantizationTableData shouldn't start with 'Exif'", !"Exif".equals(new String(quantizationTableData, 0, 4)));
    }

    @Test
    public void testReadJpegByteArray() throws Exception
    {
        File jpeg = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        byte[] fileContents = new byte[(int)jpeg.length()];
        new FileInputStream(jpeg).read(fileContents);
        new JpegSegmentReader(fileContents).readSegment(JpegSegmentReader.SEGMENT_APP1);
    }

    @Test
    public void testCreateWithInputStream() throws Exception
    {
        File jpeg = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        InputStream in = new FileInputStream(jpeg);
        JpegSegmentReader reader = null;
        try {
            reader = new JpegSegmentReader(in);
        } catch (JpegProcessingException e) {
            Assert.fail("Error constructing JpegSegmentReader using InputStream");
        }
        // this will never happen, as fail() is guaranteed to throw an AssertionException
        if (reader==null)
            return;
        byte[] exifData = reader.readSegment(JpegSegmentReader.SEGMENT_APP1);
        Assert.assertEquals("Exif", new String(exifData, 0, 4));
    }

    @Test
    public void testReadSecondSegmentInstance() throws Exception
    {
        File jpeg = new File("Source/com/drew/imaging/jpeg/test/withExifAndIptc.jpg");
        JpegSegmentReader reader = new JpegSegmentReader(jpeg);
        byte[] exifData0 = reader.readSegment(JpegSegmentReader.SEGMENT_APP1, 0);
        byte[] exifData1 = reader.readSegment(JpegSegmentReader.SEGMENT_APP1, 1);
        Assert.assertEquals("Exif", new String(exifData0, 0, 4));
        Assert.assertEquals("http", new String(exifData1, 0, 4));
    }

    @Test
    public void testReadNonExistentSegmentInstance() throws Exception
    {
        File jpeg = new File("Source/com/drew/imaging/jpeg/test/withExifAndIptc.jpg");
        JpegSegmentReader reader = new JpegSegmentReader(jpeg);
        Assert.assertNull("third exif segment shouldn't exist", reader.readSegment(JpegSegmentReader.SEGMENT_APP1, 3));
    }

    @Test
    public void testGetSegmentCount() throws Exception
    {
        File jpeg = new File("Source/com/drew/imaging/jpeg/test/withExifAndIptc.jpg");
        JpegSegmentReader reader = new JpegSegmentReader(jpeg);
        Assert.assertEquals(2, reader.getSegmentCount(JpegSegmentReader.SEGMENT_APP1));
        Assert.assertEquals(1, reader.getSegmentCount(JpegSegmentReader.SEGMENT_APP2));
        Assert.assertEquals(0, reader.getSegmentCount(JpegSegmentReader.SEGMENT_APP3));
    }

    @Test
    public void testCreateWithFileAndReadMultipleSegments() throws Exception
    {
        File jpeg = new File("Source/com/drew/imaging/jpeg/test/withExifAndIptc.jpg");
        JpegSegmentReader reader = new JpegSegmentReader(jpeg);
        validateMultipleSegmentRead(reader);
    }

    @Test
    public void testCreateWithInputStreamAndReadMultipleSegments() throws Exception
    {
        File jpeg = new File("Source/com/drew/imaging/jpeg/test/withExifAndIptc.jpg");
        InputStream in = new FileInputStream(jpeg);
        JpegSegmentReader reader = new JpegSegmentReader(in);
        validateMultipleSegmentRead(reader);
    }

    private void validateMultipleSegmentRead(JpegSegmentReader reader) throws JpegProcessingException
    {
        byte[] iptcData = reader.readSegment(JpegSegmentReader.SEGMENT_APPD);
        byte[] exifData = reader.readSegment(JpegSegmentReader.SEGMENT_APP1);
        Assert.assertTrue("exif data too short", exifData.length > 4);
        Metadata metadata = new Metadata();
        new ExifReader(exifData).extract(metadata);
        new IptcReader(iptcData).extract(metadata);
        Assert.assertEquals("Exif", new String(exifData, 0, 4));
    }
}

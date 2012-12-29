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
package com.drew.imaging.jpeg;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

/**
 * Contains JUnit tests for the JpegSegmentReader class.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegSegmentReaderTest
{
    @Test
    public void testLoadJpegWithoutExifDataReturnsNull() throws Exception
    {
        JpegSegmentData segmentData = JpegSegmentReader.readSegments(new File("Tests/com/drew/metadata/exif/noExif.jpg"), null);
        Assert.assertNull(segmentData.getSegment(JpegSegmentType.APP1));
    }

    @Test
    public void testWithJpegFile() throws Exception
    {
        try {
            JpegSegmentReader.readSegments(new File("Tests/com/drew/metadata/exif/withExif.jpg"), null);
        } catch (JpegProcessingException e) {
            Assert.fail("Error creating JpegSegmentReader");
        }
    }

    @Test
    public void testWithNonJpegFile() throws Exception
    {
        try {
            JpegSegmentReader.readSegments(new File("Tests/com/drew/imaging/jpeg/JpegSegmentReaderTest.java"), null);
            Assert.fail("shouldn't be able to construct JpegSegmentReader with non-jpeg file");
        } catch (JpegProcessingException e) {
            // expect exception
        }
    }

    @Test
    public void testReadApp1Segment() throws Exception
    {
        JpegSegmentData segmentData = JpegSegmentReader.readSegments(new File("Tests/com/drew/metadata/exif/withExif.jpg"), null);
        byte[] exifData = segmentData.getSegment(JpegSegmentType.APP1);
        Assert.assertNotNull(exifData);
        Assert.assertTrue("exif data too short", exifData.length > 4);
        Assert.assertEquals("Exif", new String(exifData, 0, 4));
    }

    @Test
    public void testReadDQTSegment() throws Exception
    {
        JpegSegmentData segmentData = JpegSegmentReader.readSegments(new File("Tests/com/drew/metadata/exif/withExif.jpg"), null);
        byte[] quantizationTableData = segmentData.getSegment(JpegSegmentType.DQT);
        Assert.assertNotNull(quantizationTableData);
        Assert.assertTrue("shouldn't have zero length quantizationTableData", quantizationTableData.length > 0);
        Assert.assertTrue("quantizationTableData shouldn't start with 'Exif'", !"Exif".equals(new String(quantizationTableData, 0, 4)));
    }

    @Test
    public void testReadJpegByteArray() throws Exception
    {
        File jpeg = new File("Tests/com/drew/metadata/exif/withExif.jpg");
        byte[] exifData = JpegSegmentReader.readSegments(jpeg, Arrays.asList(JpegSegmentType.APP1)).getSegment(JpegSegmentType.APP1);
        Assert.assertEquals("Exif", new String(exifData, 0, 4));
    }

    @Test
    public void testReadSecondSegmentInstance() throws Exception
    {
        JpegSegmentData segmentData = JpegSegmentReader.readSegments(new File("Tests/com/drew/imaging/jpeg/withExifAndIptc.jpg"), null);
        byte[] exifData0 = segmentData.getSegment(JpegSegmentType.APP1, 0);
        byte[] exifData1 = segmentData.getSegment(JpegSegmentType.APP1, 1);
        Assert.assertEquals("Exif", new String(exifData0, 0, 4));
        Assert.assertEquals("http", new String(exifData1, 0, 4));
    }

    @Test
    public void testReadNonExistentSegmentInstance() throws Exception
    {
        JpegSegmentData segmentData = JpegSegmentReader.readSegments(new File("Tests/com/drew/imaging/jpeg/withExifAndIptc.jpg"), null);
        Assert.assertNull("third exif segment shouldn't exist", segmentData.getSegment(JpegSegmentType.APP1, 3));
    }

    @Test
    public void testGetSegmentCount() throws Exception
    {
        JpegSegmentData segmentData = JpegSegmentReader.readSegments(new File("Tests/com/drew/imaging/jpeg/withExifAndIptc.jpg"), null);
        Assert.assertEquals(2, segmentData.getSegmentCount(JpegSegmentType.APP1));
        Assert.assertEquals(1, segmentData.getSegmentCount(JpegSegmentType.APP2));
        Assert.assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP3));
    }

    @Test
    public void testCreateWithFileAndReadMultipleSegments() throws Exception
    {
        JpegSegmentData segmentData = JpegSegmentReader.readSegments(new File("Tests/com/drew/imaging/jpeg/withExifAndIptc.jpg"), null);
        validateMultipleSegmentRead(segmentData);
    }

    @Test
    public void testCreateWithInputStreamAndReadMultipleSegments() throws Exception
    {
        validateMultipleSegmentRead(JpegSegmentReader.readSegments(new File("Tests/com/drew/imaging/jpeg/withExifAndIptc.jpg"), null));
    }

    private void validateMultipleSegmentRead(JpegSegmentData segmentData) throws JpegProcessingException
    {
        byte[] iptcData = segmentData.getSegment(JpegSegmentType.APPD);
        byte[] exifData = segmentData.getSegment(JpegSegmentType.APP1);
        Assert.assertNotNull(iptcData);
        Assert.assertNotNull(exifData);
        Assert.assertTrue("exif data too short", exifData.length > 4);
        // TODO extracting the data doesn't mean anything in this test case...
        Metadata metadata = new Metadata();
        new ExifReader().extract(new ByteArrayReader(exifData), metadata);
        new IptcReader().extract(new SequentialByteArrayReader(iptcData), metadata, iptcData.length);
        Assert.assertEquals("Exif", new String(exifData, 0, 4));
    }
}

/*
 * JpegSegmentReaderTest.java
 *
 * Test class written by Drew Noakes.
 *
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 04-Nov-2002 00:54:00 using IntelliJ IDEA
 */
package com.drew.imaging.jpeg.test;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Contains JUnit tests for the JpegSegmentReader class.
 */
public class JpegSegmentReaderTest extends TestCase
{
    public JpegSegmentReaderTest(String s)
    {
        super(s);
    }

    public void testIsJpegWithJpegFile() throws Exception
    {
        File jpeg = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        try {
            new JpegSegmentReader(jpeg);
        } catch (JpegProcessingException e) {
            fail("Error creating JpegSegmentReader");
        }
    }

    public void testIsJpegWithNonJpegFile() throws Exception
    {
        File nonJpeg = new File("Source/com/drew/metadata/test/AllTests.java");
        try {
            new JpegSegmentReader(nonJpeg);
            fail("shouldn't be able to construct JpegSegmentReader with non-jpeg file");
        } catch (JpegProcessingException e) {
            // expect exception
        }
    }

    public void testReadApp1Segment() throws Exception
    {
        File jpeg = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        JpegSegmentReader segmentReader = new JpegSegmentReader(jpeg);
        byte[] exifData = segmentReader.readSegment(JpegSegmentReader.SEGMENT_APP1);
        assertTrue("exif data too short", exifData.length > 4);
        assertEquals("Exif", new String(exifData, 0, 4));
    }

    public void testReadDQTSegment() throws Exception
    {
        File jpeg = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        JpegSegmentReader segmentReader = new JpegSegmentReader(jpeg);
        byte[] quantizationTableData = segmentReader.readSegment(JpegSegmentReader.SEGMENT_DQT);
        assertTrue("shouldn't have zero length quantizationTableData", quantizationTableData.length > 0);
        assertTrue("quantizationTableData shouldn't start with 'Exif'", !"Exif".equals(new String(quantizationTableData, 0, 4)));
    }

    public void testReadJpegByteArray() throws Exception
    {
        File jpeg = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        byte[] fileContents = new byte[(int)jpeg.length()];
        new FileInputStream(jpeg).read(fileContents);
        new JpegSegmentReader(fileContents).readSegment(JpegSegmentReader.SEGMENT_APP1);
    }

    public void testCreateWithInputStream() throws Exception
    {
        File jpeg = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        InputStream in = new FileInputStream(jpeg);
        JpegSegmentReader reader = null;
        try {
            reader = new JpegSegmentReader(in);
        } catch (JpegProcessingException e) {
            fail("Error constructing JpegSegmentReader using InputStream");
        }
        // this will never happen, as fail() is guaranteed to throw an AssertionException
        if (reader==null)
            return;
        byte[] exifData = reader.readSegment(JpegSegmentReader.SEGMENT_APP1);
        assertEquals("Exif", new String(exifData, 0, 4));
    }

    public void testReadSecondSegmentInstanace() throws Exception
    {
        File jpeg = new File("Source/com/drew/imaging/jpeg/test/withExifAndIptc.jpg");
        JpegSegmentReader reader = new JpegSegmentReader(jpeg);
        byte[] exifData0 = reader.readSegment(JpegSegmentReader.SEGMENT_APP1, 0);
        byte[] exifData1 = reader.readSegment(JpegSegmentReader.SEGMENT_APP1, 1);
        assertEquals("Exif", new String(exifData0, 0, 4));
        assertEquals("http", new String(exifData1, 0, 4));
    }

    public void testReadNonExistantSegmentInstance() throws Exception
    {
        File jpeg = new File("Source/com/drew/imaging/jpeg/test/withExifAndIptc.jpg");
        JpegSegmentReader reader = new JpegSegmentReader(jpeg);
        assertNull("third exif segment shouldn't exist", reader.readSegment(JpegSegmentReader.SEGMENT_APP1, 3));
    }

    public void testGetSegmentCount() throws Exception
    {
        File jpeg = new File("Source/com/drew/imaging/jpeg/test/withExifAndIptc.jpg");
        JpegSegmentReader reader = new JpegSegmentReader(jpeg);
        assertEquals(2, reader.getSegmentCount(JpegSegmentReader.SEGMENT_APP1));
        assertEquals(1, reader.getSegmentCount(JpegSegmentReader.SEGMENT_APP2));
        assertEquals(0, reader.getSegmentCount(JpegSegmentReader.SEGMENT_APP3));
    }

    public void testCreateWithFileAndReadMultipleSegments() throws Exception
    {
        File jpeg = new File("Source/com/drew/imaging/jpeg/test/withExifAndIptc.jpg");
        JpegSegmentReader reader = new JpegSegmentReader(jpeg);
        validateMultipleSegmentRead(reader);
    }

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
        assertTrue("exif data too short", exifData.length > 4);
        new ExifReader(exifData).extract();
        new IptcReader(iptcData).extract();
        assertEquals("Exif", new String(exifData, 0, 4));
    }
}

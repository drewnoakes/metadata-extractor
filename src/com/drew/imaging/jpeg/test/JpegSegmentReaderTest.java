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
 *   drew@drewnoakes.com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 04-Nov-2002 00:54:00 using IntelliJ IDEA
 */
package com.drew.imaging.jpeg.test;

import com.drew.imaging.exif.ExifExtractor;
import com.drew.imaging.exif.ExifProcessingException;
import com.drew.imaging.exif.ImageInfo;
import com.drew.imaging.jpeg.JpegSegmentReader;
import junit.framework.TestCase;

import java.io.File;

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
        File jpeg = new File("src/com/drew/imaging/exif/test/withExif.jpg");
        JpegSegmentReader segmentReader = new JpegSegmentReader(jpeg);
        assertTrue("expect isJpeg() for jpeg file", segmentReader.isJpeg());
    }

    public void testIsJpegWithNonJpegFile() throws Exception
    {
        File nonJpeg = new File("src/com/drew/imaging/exif/test/AllTests.java");
        JpegSegmentReader segmentReader = new JpegSegmentReader(nonJpeg);
        assertTrue("expect !isJpeg() for non-jpeg file", !segmentReader.isJpeg());
    }

    public void testReadApp1Segment() throws Exception
    {
        File jpeg = new File("src/com/drew/imaging/exif/test/withExif.jpg");
        JpegSegmentReader segmentReader = new JpegSegmentReader(jpeg);
        byte[] exifData = segmentReader.readSegment(JpegSegmentReader.SEGMENT_MARKER_APP1);
        assertTrue("exif data too short", exifData.length > 4);
        assertEquals("Exif", new String(exifData, 0, 4));
        ExifExtractor extractor = new ExifExtractor(exifData);
        try {
            ImageInfo info = extractor.extract();
            assertTrue("app1 segment should contain exif tags", info.countTags() > 0);
        } catch (ExifProcessingException e) {
            fail("app1 segment couldn't be read by exif extractor");
        }
    }

    public void testReadDQTSegment() throws Exception
    {
        File jpeg = new File("src/com/drew/imaging/exif/test/withExif.jpg");
        JpegSegmentReader segmentReader = new JpegSegmentReader(jpeg);
        byte[] quantizationTableData = segmentReader.readSegment(JpegSegmentReader.SEGMENT_MARKER_DQT);
        assertTrue("shouldn't have zero length quantizationTableData", quantizationTableData.length > 0);
        assertTrue("quantizationTableData shouldn't start with 'Exif'", !"Exif".equals(new String(quantizationTableData, 0, 4)));
    }
}

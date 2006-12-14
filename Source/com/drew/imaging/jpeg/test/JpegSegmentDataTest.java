/*
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
 */
package com.drew.imaging.jpeg.test;

import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.lang.test.TestHelper;
import junit.framework.TestCase;

import java.io.File;

/**
 *
 */
public class JpegSegmentDataTest extends TestCase
{
    public JpegSegmentDataTest(String name)
    {
        super(name);
    }

    public void testAddAndGetSegment() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        byte segmentMarker = (byte)12;
        byte[] segmentBytes = new byte[] { 1,2,3 };

        segmentData.addSegment(segmentMarker, segmentBytes);
        assertEquals(1, segmentData.getSegmentCount(segmentMarker));
        TestHelper.assertEqualArrays(segmentBytes, segmentData.getSegment(segmentMarker));
    }

    public void testContainsSegment() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        byte segmentMarker = (byte)12;
        byte[] segmentBytes = new byte[] { 1,2,3 };

        assertTrue(!segmentData.containsSegment(segmentMarker));

        segmentData.addSegment(segmentMarker, segmentBytes);

        assertTrue(segmentData.containsSegment(segmentMarker));
    }

    public void testAddingMultipleSegments() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        byte segmentMarker1 = (byte)12;
        byte segmentMarker2 = (byte)21;
        byte[] segmentBytes1 = new byte[] { 1,2,3 };
        byte[] segmentBytes2 = new byte[] { 3,2,1 };

        segmentData.addSegment(segmentMarker1, segmentBytes1);
        segmentData.addSegment(segmentMarker2, segmentBytes2);
        assertEquals(1, segmentData.getSegmentCount(segmentMarker1));
        assertEquals(1, segmentData.getSegmentCount(segmentMarker2));
        TestHelper.assertEqualArrays(segmentBytes1, segmentData.getSegment(segmentMarker1));
        TestHelper.assertEqualArrays(segmentBytes2, segmentData.getSegment(segmentMarker2));
    }

    public void testSegmentWithMultipleOccurrences() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        byte segmentMarker = (byte)12;
        byte[] segmentBytes1 = new byte[] { 1,2,3 };
        byte[] segmentBytes2 = new byte[] { 3,2,1 };

        segmentData.addSegment(segmentMarker, segmentBytes1);
        segmentData.addSegment(segmentMarker, segmentBytes2);
        assertEquals(2, segmentData.getSegmentCount(segmentMarker));
        TestHelper.assertEqualArrays(segmentBytes1, segmentData.getSegment(segmentMarker));
        TestHelper.assertEqualArrays(segmentBytes1, segmentData.getSegment(segmentMarker, 0));
        TestHelper.assertEqualArrays(segmentBytes2, segmentData.getSegment(segmentMarker, 1));
    }

    public void testRemoveSegmentOccurrence() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        byte segmentMarker = (byte)12;
        byte[] segmentBytes1 = new byte[] { 1,2,3 };
        byte[] segmentBytes2 = new byte[] { 3,2,1 };

        segmentData.addSegment(segmentMarker, segmentBytes1);
        segmentData.addSegment(segmentMarker, segmentBytes2);

        assertEquals(2, segmentData.getSegmentCount(segmentMarker));

        TestHelper.assertEqualArrays(segmentBytes1, segmentData.getSegment(segmentMarker, 0));

        segmentData.removeSegmentOccurrence(segmentMarker, 0);

        TestHelper.assertEqualArrays(segmentBytes2, segmentData.getSegment(segmentMarker, 0));
    }

    public void testRemoveSegment() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        byte segmentMarker = (byte)12;
        byte[] segmentBytes1 = new byte[] { 1,2,3 };
        byte[] segmentBytes2 = new byte[] { 3,2,1 };

        segmentData.addSegment(segmentMarker, segmentBytes1);
        segmentData.addSegment(segmentMarker, segmentBytes2);

        assertEquals(2, segmentData.getSegmentCount(segmentMarker));
        assertTrue(segmentData.containsSegment(segmentMarker));

        TestHelper.assertEqualArrays(segmentBytes1, segmentData.getSegment(segmentMarker, 0));

        segmentData.removeSegment(segmentMarker);

        assertTrue(!segmentData.containsSegment(segmentMarker));
        assertEquals(0, segmentData.getSegmentCount(segmentMarker));
    }

    public void testToAndFromFile() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();
        byte segmentMarker = (byte)12;
        byte[] segmentBytes = new byte[] { 1,2,3 };

        segmentData.addSegment(segmentMarker, segmentBytes);
        assertTrue(segmentData.containsSegment(segmentMarker));

        File tempFile = File.createTempFile("JpegSegmentDataTest", "tmp");
        JpegSegmentData.ToFile(tempFile, segmentData);
        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);
        segmentData = JpegSegmentData.FromFile(tempFile);

        tempFile.delete();
        assertTrue(!tempFile.exists());

        assertNotNull(segmentData);
        assertTrue(segmentData.containsSegment(segmentMarker));
        TestHelper.assertEqualArrays(segmentBytes, segmentData.getSegment(segmentMarker));
    }
}

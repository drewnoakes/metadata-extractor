/*
 * Copyright 2002-2015 Drew Noakes
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
package com.drew.imaging.jpeg;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings({ "ConstantConditions" })
public class JpegSegmentDataTest
{
    @Test
    public void testAddAndGetSegment() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        byte segmentMarker = (byte)12;
        byte[] segmentBytes = new byte[] { 1,2,3 };

        segmentData.addSegment(segmentMarker, segmentBytes);
        assertEquals(1, segmentData.getSegmentCount(segmentMarker));
        assertArrayEquals(segmentBytes, segmentData.getSegment(segmentMarker));
    }

    @Test
    public void testContainsSegment() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        byte segmentMarker = (byte)12;
        byte[] segmentBytes = new byte[] { 1,2,3 };

        assertTrue(!segmentData.containsSegment(segmentMarker));

        segmentData.addSegment(segmentMarker, segmentBytes);

        assertTrue(segmentData.containsSegment(segmentMarker));
    }

    @Test
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
        assertArrayEquals(segmentBytes1, segmentData.getSegment(segmentMarker1));
        assertArrayEquals(segmentBytes2, segmentData.getSegment(segmentMarker2));
    }

    @Test
    public void testSegmentWithMultipleOccurrences() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        byte segmentMarker = (byte)12;
        byte[] segmentBytes1 = new byte[] { 1,2,3 };
        byte[] segmentBytes2 = new byte[] { 3,2,1 };

        segmentData.addSegment(segmentMarker, segmentBytes1);
        segmentData.addSegment(segmentMarker, segmentBytes2);
        assertEquals(2, segmentData.getSegmentCount(segmentMarker));
        assertArrayEquals(segmentBytes1, segmentData.getSegment(segmentMarker));
        assertArrayEquals(segmentBytes1, segmentData.getSegment(segmentMarker, 0));
        assertArrayEquals(segmentBytes2, segmentData.getSegment(segmentMarker, 1));
    }

    @Test
    public void testRemoveSegmentOccurrence() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        byte segmentMarker = (byte)12;
        byte[] segmentBytes1 = new byte[] { 1,2,3 };
        byte[] segmentBytes2 = new byte[] { 3,2,1 };

        segmentData.addSegment(segmentMarker, segmentBytes1);
        segmentData.addSegment(segmentMarker, segmentBytes2);

        assertEquals(2, segmentData.getSegmentCount(segmentMarker));

        assertArrayEquals(segmentBytes1, segmentData.getSegment(segmentMarker, 0));

        segmentData.removeSegmentOccurrence(segmentMarker, 0);

        assertArrayEquals(segmentBytes2, segmentData.getSegment(segmentMarker, 0));
    }

    @Test
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

        assertArrayEquals(segmentBytes1, segmentData.getSegment(segmentMarker, 0));

        segmentData.removeSegment(segmentMarker);

        assertTrue(!segmentData.containsSegment(segmentMarker));
        assertEquals(0, segmentData.getSegmentCount(segmentMarker));
    }
}

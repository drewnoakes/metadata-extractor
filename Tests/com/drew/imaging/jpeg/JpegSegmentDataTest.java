/*
 * Copyright 2002-2017 Drew Noakes
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

import com.drew.lang.ReaderInfo;
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

        JpegSegmentType segmentType = JpegSegmentType.APP0;
        byte[] segmentBytes = new byte[] { 1,2,3 };

        JpegSegment segment = new JpegSegment(segmentType, ReaderInfo.createFromArray(segmentBytes));
        segmentData.addSegment(segment);
        assertEquals(1, segmentData.getSegmentCount(segmentType));
        assertArrayEquals(segmentBytes, segmentData.getSegment(segmentType).getReader().toArray());
    }

    @Test
    public void testContainsSegment() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        JpegSegmentType segmentType = JpegSegmentType.APP0;
        byte[] segmentBytes = new byte[] { 1,2,3 };

        assertTrue(!segmentData.containsSegment(segmentType));

        JpegSegment segment = new JpegSegment(segmentType, ReaderInfo.createFromArray(segmentBytes));
        segmentData.addSegment(segment);

        assertTrue(segmentData.containsSegment(segmentType));
    }

    @Test
    public void testAddingMultipleSegments() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        JpegSegmentType segmentType1 = JpegSegmentType.APP0;
        JpegSegmentType segmentType2 = JpegSegmentType.APP1;
        byte[] segmentBytes1 = new byte[] { 1,2,3 };
        byte[] segmentBytes2 = new byte[] { 3,2,1 };

        JpegSegment segment1 = new JpegSegment(segmentType1, ReaderInfo.createFromArray(segmentBytes1));
        JpegSegment segment2 = new JpegSegment(segmentType2, ReaderInfo.createFromArray(segmentBytes2));
        
        segmentData.addSegment(segment1);
        segmentData.addSegment(segment2);
        assertEquals(1, segmentData.getSegmentCount(segmentType1));
        assertEquals(1, segmentData.getSegmentCount(segmentType2));
        assertArrayEquals(segmentBytes1, segmentData.getSegment(segmentType1).getReader().toArray());
        assertArrayEquals(segmentBytes2, segmentData.getSegment(segmentType2).getReader().toArray());
    }

    @Test
    public void testSegmentWithMultipleOccurrences() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        JpegSegmentType segmentType = JpegSegmentType.APP0;
        byte[] segmentBytes1 = new byte[] { 1,2,3 };
        byte[] segmentBytes2 = new byte[] { 3,2,1 };

        JpegSegment segment1 = new JpegSegment(segmentType, ReaderInfo.createFromArray(segmentBytes1));
        JpegSegment segment2 = new JpegSegment(segmentType, ReaderInfo.createFromArray(segmentBytes2));
        
        segmentData.addSegment(segment1);
        segmentData.addSegment(segment2);
        assertEquals(2, segmentData.getSegmentCount(segmentType));
        assertArrayEquals(segmentBytes1, segmentData.getSegment(segmentType).getReader().toArray());
        assertArrayEquals(segmentBytes1, segmentData.getSegment(segmentType, 0).getReader().toArray());
        assertArrayEquals(segmentBytes2, segmentData.getSegment(segmentType, 1).getReader().toArray());
    }

    @Test
    public void testRemoveSegmentOccurrence() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        JpegSegmentType segmentType = JpegSegmentType.APP0;
        byte[] segmentBytes1 = new byte[] { 1,2,3 };
        byte[] segmentBytes2 = new byte[] { 3,2,1 };

        JpegSegment segment1 = new JpegSegment(segmentType, ReaderInfo.createFromArray(segmentBytes1));
        JpegSegment segment2 = new JpegSegment(segmentType, ReaderInfo.createFromArray(segmentBytes2));
        
        segmentData.addSegment(segment1);
        segmentData.addSegment(segment2);

        assertEquals(2, segmentData.getSegmentCount(segmentType));

        assertArrayEquals(segmentBytes1, segmentData.getSegment(segmentType, 0).getReader().toArray());

        segmentData.removeSegmentOccurrence(segmentType, 0);

        assertArrayEquals(segmentBytes2, segmentData.getSegment(segmentType, 0).getReader().toArray());
    }

    @Test
    public void testRemoveSegment() throws Exception
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        JpegSegmentType segmentType = JpegSegmentType.APP0;
        byte[] segmentBytes1 = new byte[] { 1,2,3 };
        byte[] segmentBytes2 = new byte[] { 3,2,1 };

        JpegSegment segment1 = new JpegSegment(segmentType, ReaderInfo.createFromArray(segmentBytes1));
        JpegSegment segment2 = new JpegSegment(segmentType, ReaderInfo.createFromArray(segmentBytes2));
        
        segmentData.addSegment(segment1);
        segmentData.addSegment(segment2);

        assertEquals(2, segmentData.getSegmentCount(segmentType));
        assertTrue(segmentData.containsSegment(segmentType));

        assertArrayEquals(segmentBytes1, segmentData.getSegment(segmentType, 0).getReader().toArray());

        segmentData.removeSegment(segmentType);

        assertTrue(!segmentData.containsSegment(segmentType));
        assertEquals(0, segmentData.getSegmentCount(segmentType));
    }
}

/*
 * Copyright 2002-2019 Drew Noakes and contributors
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

import com.drew.tools.FileUtil;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link JpegSegmentReader}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class JpegSegmentReaderTest
{
    @Test
    public void testReadAllSegments() throws Exception
    {
        JpegSegmentData segmentData = JpegSegmentReader.readSegments(new File("Tests/Data/withExifAndIptc.jpg"), null);

        assertEquals(1, segmentData.getSegmentCount(JpegSegmentType.APP0));
        assertArrayEquals(
                FileUtil.readBytes("Tests/Data/withExifAndIptc.jpg.app0"),
                segmentData.getSegment(JpegSegmentType.APP0));
        assertNull(segmentData.getSegment(JpegSegmentType.APP0, 1));

        assertEquals(2, segmentData.getSegmentCount(JpegSegmentType.APP1));
        assertArrayEquals(
                FileUtil.readBytes("Tests/Data/withExifAndIptc.jpg.app1.0"),
                segmentData.getSegment(JpegSegmentType.APP1, 0));
        assertArrayEquals(
                FileUtil.readBytes("Tests/Data/withExifAndIptc.jpg.app1.1"),
                segmentData.getSegment(JpegSegmentType.APP1, 1));
        assertNull(segmentData.getSegment(JpegSegmentType.APP1, 2));

        assertEquals(1, segmentData.getSegmentCount(JpegSegmentType.APP2));
        assertArrayEquals(
                FileUtil.readBytes("Tests/Data/withExifAndIptc.jpg.app2"),
                segmentData.getSegment(JpegSegmentType.APP2));
        assertNull(segmentData.getSegment(JpegSegmentType.APP2, 1));

        assertEquals(1, segmentData.getSegmentCount(JpegSegmentType.APPD));
        assertArrayEquals(
                FileUtil.readBytes("Tests/Data/withExifAndIptc.jpg.appd"),
                segmentData.getSegment(JpegSegmentType.APPD));
        assertNull(segmentData.getSegment(JpegSegmentType.APPD, 1));

        assertEquals(1, segmentData.getSegmentCount(JpegSegmentType.APPE));
        assertArrayEquals(
                FileUtil.readBytes("Tests/Data/withExifAndIptc.jpg.appe"),
                segmentData.getSegment(JpegSegmentType.APPE));
        assertNull(segmentData.getSegment(JpegSegmentType.APPE, 1));

        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP3));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP4));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP5));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP6));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP7));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP8));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP9));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPA));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPB));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPC));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPF));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.COM));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.DAC));
        assertEquals(4, segmentData.getSegmentCount(JpegSegmentType.DHT));
        assertEquals(2, segmentData.getSegmentCount(JpegSegmentType.DQT));
        assertEquals(1, segmentData.getSegmentCount(JpegSegmentType.SOF0));

        assertNull(segmentData.getSegment(JpegSegmentType.APP3, 0));
    }

    @Test
    public void testReadSpecificSegments() throws Exception
    {
        JpegSegmentData segmentData = JpegSegmentReader.readSegments(
                new File("Tests/Data/withExifAndIptc.jpg"),
                Arrays.asList(JpegSegmentType.APP0, JpegSegmentType.APP2));

        assertEquals(1, segmentData.getSegmentCount(JpegSegmentType.APP0));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP1));
        assertEquals(1, segmentData.getSegmentCount(JpegSegmentType.APP2));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPD));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPE));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP3));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP4));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP5));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP6));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP7));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP8));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP9));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPA));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPB));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPC));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPF));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.COM));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.DHT));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.SOF0));

        assertArrayEquals(
                FileUtil.readBytes("Tests/Data/withExifAndIptc.jpg.app0"),
                segmentData.getSegment(JpegSegmentType.APP0));
        assertArrayEquals(
                FileUtil.readBytes("Tests/Data/withExifAndIptc.jpg.app2"),
                segmentData.getSegment(JpegSegmentType.APP2));
    }

    @Test
    public void testReadDhtSegment() throws Exception
    {
        JpegSegmentData segmentData = JpegSegmentReader.readSegments(
            new File("Tests/Data/withExifAndIptc.jpg"),
            Collections.singletonList(JpegSegmentType.DHT));

        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP0));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP1));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP2));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPD));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPE));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP3));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP4));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP5));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP6));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP7));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP8));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APP9));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPA));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPB));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPC));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.APPF));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.COM));
        assertEquals(4, segmentData.getSegmentCount(JpegSegmentType.DHT));
        assertEquals(0, segmentData.getSegmentCount(JpegSegmentType.SOF0));
    }

    @Test
    public void testLoadJpegWithoutExifDataReturnsNull() throws Exception
    {
        JpegSegmentData segmentData = JpegSegmentReader.readSegments(new File("Tests/Data/noExif.jpg"), null);
        assertNull(segmentData.getSegment(JpegSegmentType.APP1));
    }

    @Test
    public void testWithNonJpegFile() throws Exception
    {
        try {
            JpegSegmentReader.readSegments(new File("Tests/com/drew/imaging/jpeg/JpegSegmentReaderTest.java"), null);
            fail("shouldn't be able to construct JpegSegmentReader with non-JPEG file");
        } catch (JpegProcessingException e) {
            // expect exception
        }
    }
}

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
package com.drew.metadata.jpeg.test;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.jpeg.JpegComponent;
import com.drew.metadata.jpeg.JpegDirectory;
import com.drew.metadata.jpeg.JpegReader;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegReaderTest extends TestCase
{
    private JpegDirectory _directory;

    public JpegReaderTest(String s)
    {
        super(s);
    }

    public void setUp() throws JpegProcessingException, FileNotFoundException
    {
        // use a known testing image
        File jpegFile = new File("Source/com/drew/metadata/jpeg/test/simple.jpg");
        JpegReader reader = new JpegReader(jpegFile);
        Metadata metadata = new Metadata();
        reader.extract(metadata);
        assertTrue(metadata.containsDirectory(JpegDirectory.class));
        _directory = (JpegDirectory)metadata.getDirectory(JpegDirectory.class);
    }

    public void testExtract_Width() throws Exception
    {
        assertEquals(800, _directory.getInt(JpegDirectory.TAG_JPEG_IMAGE_WIDTH));
    }

    public void testExtract_Height() throws Exception
    {
        assertEquals(600, _directory.getInt(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT));
    }

    public void testExtract_DataPrecision() throws Exception
    {
        assertEquals(8, _directory.getInt(JpegDirectory.TAG_JPEG_DATA_PRECISION));
    }

    public void testExtract_NumberOfComponents() throws Exception
    {
        assertEquals(3, _directory.getInt(JpegDirectory.TAG_JPEG_NUMBER_OF_COMPONENTS));
    }

    public void testComponentData1() throws Exception
    {
        JpegComponent component = (JpegComponent)_directory.getObject(JpegDirectory.TAG_JPEG_COMPONENT_DATA_1);
        assertEquals("Y", component.getComponentName());
        assertEquals(1, component.getComponentId());
        assertEquals(0, component.getQuantizationTableNumber());
        assertEquals(2, component.getHorizontalSamplingFactor());
        assertEquals(2, component.getVerticalSamplingFactor());
    }

    public void testComponentData2() throws Exception
    {
        JpegComponent component = (JpegComponent)_directory.getObject(JpegDirectory.TAG_JPEG_COMPONENT_DATA_2);
        assertEquals("Cb", component.getComponentName());
        assertEquals(2, component.getComponentId());
        assertEquals(1, component.getQuantizationTableNumber());
        assertEquals(1, component.getHorizontalSamplingFactor());
        assertEquals(1, component.getVerticalSamplingFactor());
        assertEquals("Cb component: Quantization table 1, Sampling factors 1 horiz/1 vert", _directory.getDescription(JpegDirectory.TAG_JPEG_COMPONENT_DATA_2));
    }

    public void testComponentData3() throws Exception
    {
        JpegComponent component = (JpegComponent)_directory.getObject(JpegDirectory.TAG_JPEG_COMPONENT_DATA_3);
        assertEquals("Cr", component.getComponentName());
        assertEquals(3, component.getComponentId());
        assertEquals(1, component.getQuantizationTableNumber());
        assertEquals(1, component.getHorizontalSamplingFactor());
        assertEquals(1, component.getVerticalSamplingFactor());
        assertEquals("Cr component: Quantization table 1, Sampling factors 1 horiz/1 vert", _directory.getDescription(JpegDirectory.TAG_JPEG_COMPONENT_DATA_3));
    }

/*
    // this test is part of an incomplete investigation into extracting audio from JPG files
    public void testJpegWithAudio() throws Exception
    {
        // use a known testing image
        File jpegFile = new File("Source/com/drew/metadata/jpeg/test/audioPresent.jpg");

        JpegSegmentReader jpegSegmentReader = new JpegSegmentReader(jpegFile);
        byte[] segment1Bytes = jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APP2);
        System.out.println(segment1Bytes.length);

//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APP1));
        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APP2).length);
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APP3));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APP4));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APP5));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APP6));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APP7));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APP8));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APP9));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APPA));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APPB));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APPC));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APPD));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APPE));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_APPF));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_COM));
        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_DHT).length);
        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_DQT).length);
        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_SOF0).length);
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SEGMENT_SOI));

        // write the segment's data out to a wav file...
        File audioFile = new File("Source/com/drew/metadata/jpeg/test/audio.wav");
        FileOutputStream os = null;
        try
        {
            os = new FileOutputStream(audioFile);
            os.write(segment1Bytes);
        }
        finally
        {
            if (os!=null)
                os.close();
        }
    }
*/
}

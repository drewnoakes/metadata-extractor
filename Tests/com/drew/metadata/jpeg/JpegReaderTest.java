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
package com.drew.metadata.jpeg;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegReaderTest
{
    private JpegDirectory _directory;

    @Before
    public void setUp() throws JpegProcessingException, IOException
    {
        // use a known testing image
        final JpegSegmentData segmentData = JpegSegmentReader.readSegments("Tests/com/drew/metadata/jpeg/simple.jpg");
        final byte[] data = segmentData.getSegment(JpegSegmentType.SOF0);
        MetadataReader reader = new JpegReader();
        Metadata metadata = new Metadata();
        Assert.assertNotNull(data);
        reader.extract(new ByteArrayReader(data), metadata);
        Assert.assertTrue(metadata.containsDirectory(JpegDirectory.class));
        _directory = metadata.getOrCreateDirectory(JpegDirectory.class);
    }

    @Test
    public void testExtract_Width() throws Exception
    {
        Assert.assertEquals(800, _directory.getInt(JpegDirectory.TAG_JPEG_IMAGE_WIDTH));
    }

    @Test
    public void testExtract_Height() throws Exception
    {
        Assert.assertEquals(600, _directory.getInt(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT));
    }

    @Test
    public void testExtract_DataPrecision() throws Exception
    {
        Assert.assertEquals(8, _directory.getInt(JpegDirectory.TAG_JPEG_DATA_PRECISION));
    }

    @Test
    public void testExtract_NumberOfComponents() throws Exception
    {
        Assert.assertEquals(3, _directory.getInt(JpegDirectory.TAG_JPEG_NUMBER_OF_COMPONENTS));
    }

    @Test
    public void testComponentData1() throws Exception
    {
        JpegComponent component = (JpegComponent)_directory.getObject(JpegDirectory.TAG_JPEG_COMPONENT_DATA_1);
        Assert.assertNotNull(component);
        Assert.assertEquals("Y", component.getComponentName());
        Assert.assertEquals(1, component.getComponentId());
        Assert.assertEquals(0, component.getQuantizationTableNumber());
        Assert.assertEquals(2, component.getHorizontalSamplingFactor());
        Assert.assertEquals(2, component.getVerticalSamplingFactor());
    }

    @Test
    public void testComponentData2() throws Exception
    {
        JpegComponent component = (JpegComponent)_directory.getObject(JpegDirectory.TAG_JPEG_COMPONENT_DATA_2);
        Assert.assertNotNull(component);
        Assert.assertEquals("Cb", component.getComponentName());
        Assert.assertEquals(2, component.getComponentId());
        Assert.assertEquals(1, component.getQuantizationTableNumber());
        Assert.assertEquals(1, component.getHorizontalSamplingFactor());
        Assert.assertEquals(1, component.getVerticalSamplingFactor());
        Assert.assertEquals("Cb component: Quantization table 1, Sampling factors 1 horiz/1 vert", _directory.getDescription(JpegDirectory.TAG_JPEG_COMPONENT_DATA_2));
    }

    @Test
    public void testComponentData3() throws Exception
    {
        JpegComponent component = (JpegComponent)_directory.getObject(JpegDirectory.TAG_JPEG_COMPONENT_DATA_3);
        Assert.assertNotNull(component);
        Assert.assertEquals("Cr", component.getComponentName());
        Assert.assertEquals(3, component.getComponentId());
        Assert.assertEquals(1, component.getQuantizationTableNumber());
        Assert.assertEquals(1, component.getHorizontalSamplingFactor());
        Assert.assertEquals(1, component.getVerticalSamplingFactor());
        Assert.assertEquals("Cr component: Quantization table 1, Sampling factors 1 horiz/1 vert", _directory.getDescription(JpegDirectory.TAG_JPEG_COMPONENT_DATA_3));
    }

/*
    // this test is part of an incomplete investigation into extracting audio from JPG files
    public void testJpegWithAudio() throws Exception
    {
        // use a known testing image
        File jpegFile = new File("Tests/com/drew/metadata/jpeg/audioPresent.jpg");

        JpegSegmentReader jpegSegmentReader = new JpegSegmentReader(jpegFile);
        byte[] segment1Bytes = jpegSegmentReader.readSegment(JpegSegmentReader.APP2);
        System.out.println(segment1Bytes.length);

//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APP1));
        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APP2).length);
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APP3));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APP4));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APP5));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APP6));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APP7));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APP8));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APP9));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APPA));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APPB));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APPC));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APPD));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APPE));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.APPF));
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.COM));
        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.DHT).length);
        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.DQT).length);
        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SOF0).length);
//        System.out.println(jpegSegmentReader.readSegment(JpegSegmentReader.SOI));

        // write the segment's data out to a wav file...
        File audioFile = new File("Tests/com/drew/metadata/jpeg/audio.wav");
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

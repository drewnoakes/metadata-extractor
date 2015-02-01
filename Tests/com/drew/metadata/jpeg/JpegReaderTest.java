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
package com.drew.metadata.jpeg;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.tools.FileUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class JpegReaderTest
{
    @NotNull
    public static JpegDirectory processBytes(String filePath) throws IOException
    {
        Metadata metadata = new Metadata();
        new JpegReader().extract(FileUtil.readBytes(filePath), metadata, JpegSegmentType.SOF0);

        JpegDirectory directory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
        assertNotNull(directory);
        return directory;
    }

    private JpegDirectory _directory;

    @Before
    public void setUp() throws JpegProcessingException, IOException
    {
        _directory = processBytes("Tests/Data/simple.jpg.sof0");
    }

    @Test
    public void testExtract_Width() throws Exception
    {
        assertEquals(800, _directory.getInt(JpegDirectory.TAG_IMAGE_WIDTH));
    }

    @Test
    public void testExtract_Height() throws Exception
    {
        assertEquals(600, _directory.getInt(JpegDirectory.TAG_IMAGE_HEIGHT));
    }

    @Test
    public void testExtract_DataPrecision() throws Exception
    {
        assertEquals(8, _directory.getInt(JpegDirectory.TAG_DATA_PRECISION));
    }

    @Test
    public void testExtract_NumberOfComponents() throws Exception
    {
        assertEquals(3, _directory.getInt(JpegDirectory.TAG_NUMBER_OF_COMPONENTS));
    }

    @Test
    public void testComponentData1() throws Exception
    {
        JpegComponent component = (JpegComponent)_directory.getObject(JpegDirectory.TAG_COMPONENT_DATA_1);

        assertNotNull(component);
        assertEquals("Y", component.getComponentName());
        assertEquals(1, component.getComponentId());
        assertEquals(0, component.getQuantizationTableNumber());
        assertEquals(2, component.getHorizontalSamplingFactor());
        assertEquals(2, component.getVerticalSamplingFactor());
    }

    @Test
    public void testComponentData2() throws Exception
    {
        JpegComponent component = (JpegComponent)_directory.getObject(JpegDirectory.TAG_COMPONENT_DATA_2);

        assertNotNull(component);
        assertEquals("Cb", component.getComponentName());
        assertEquals(2, component.getComponentId());
        assertEquals(1, component.getQuantizationTableNumber());
        assertEquals(1, component.getHorizontalSamplingFactor());
        assertEquals(1, component.getVerticalSamplingFactor());
        assertEquals("Cb component: Quantization table 1, Sampling factors 1 horiz/1 vert", _directory.getDescription(JpegDirectory.TAG_COMPONENT_DATA_2));
    }

    @Test
    public void testComponentData3() throws Exception
    {
        JpegComponent component = (JpegComponent)_directory.getObject(JpegDirectory.TAG_COMPONENT_DATA_3);

        assertNotNull(component);
        assertEquals("Cr", component.getComponentName());
        assertEquals(3, component.getComponentId());
        assertEquals(1, component.getQuantizationTableNumber());
        assertEquals(1, component.getHorizontalSamplingFactor());
        assertEquals(1, component.getVerticalSamplingFactor());
        assertEquals("Cr component: Quantization table 1, Sampling factors 1 horiz/1 vert", _directory.getDescription(JpegDirectory.TAG_COMPONENT_DATA_3));
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

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
 * Created by IntelliJ IDEA.
 * User: dnoakes
 * Date: 09-Oct-2003
 * Time: 15:22:23
 * To change this template use Options | File Templates.
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
        // use a known testing image and extract the width
        File jpegFile = new File("src/com/drew/metadata/jpeg/test/simple.jpg");
        JpegReader reader = new JpegReader(jpegFile);
        Metadata metadata = reader.extract();
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
}

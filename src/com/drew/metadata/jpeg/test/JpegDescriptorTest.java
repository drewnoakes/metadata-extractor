package com.drew.metadata.jpeg.test;

import com.drew.metadata.MetadataException;
import com.drew.metadata.jpeg.JpegComponent;
import com.drew.metadata.jpeg.JpegDescriptor;
import com.drew.metadata.jpeg.JpegDirectory;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: dnoakes
 * Date: 09-Oct-2003
 * Time: 15:22:23
 * To change this template use Options | File Templates.
 */
public class JpegDescriptorTest extends TestCase
{
    private JpegDirectory _directory;
    private JpegDescriptor _descriptor;

    public JpegDescriptorTest(String s)
    {
        super(s);
    }

    public void setUp() throws Exception
    {
        _directory = new JpegDirectory();
        _descriptor = new JpegDescriptor(_directory);
    }

    public void testGetComponentDataDescription_InvalidComponentNumber() throws Exception
    {
        try {
            _descriptor.getComponentDataDescription(1);
            fail("Excepted exception");
        } catch (MetadataException e) {
            // expect exception
        }
    }

    public void testGetImageWidthDescription() throws Exception
    {
        _directory.setInt(JpegDirectory.TAG_JPEG_IMAGE_WIDTH, 123);
        assertEquals("123 pixels", _descriptor.getImageWidthDescription());
        assertEquals("123 pixels", _directory.getDescription(JpegDirectory.TAG_JPEG_IMAGE_WIDTH));
    }

    public void testGetImageHeightDescription() throws Exception
    {
        _directory.setInt(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT, 123);
        assertEquals("123 pixels", _descriptor.getImageHeightDescription());
        assertEquals("123 pixels", _directory.getDescription(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT));
    }

    public void testGetDataPrecisionDescription() throws Exception
    {
        _directory.setInt(JpegDirectory.TAG_JPEG_DATA_PRECISION, 8);
        assertEquals("8 bits", _descriptor.getDataPrecisionDescription());
        assertEquals("8 bits", _directory.getDescription(JpegDirectory.TAG_JPEG_DATA_PRECISION));
    }

    public void testGetComponentDescription() throws MetadataException
    {
        JpegComponent component1 = new JpegComponent(1, 0x22, 0);
        _directory.setObject(JpegDirectory.TAG_JPEG_COMPONENT_DATA_1, component1);
        assertEquals("Y component: Quantization table 0, Sampling factors 2 horiz/2 vert", _directory.getDescription(JpegDirectory.TAG_JPEG_COMPONENT_DATA_1));
        assertEquals("Y component: Quantization table 0, Sampling factors 2 horiz/2 vert", _descriptor.getComponentDataDescription(0));
    }
}

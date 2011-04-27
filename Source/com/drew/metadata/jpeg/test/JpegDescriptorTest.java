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

import com.drew.metadata.MetadataException;
import com.drew.metadata.jpeg.JpegComponent;
import com.drew.metadata.jpeg.JpegDescriptor;
import com.drew.metadata.jpeg.JpegDirectory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegDescriptorTest
{
    private JpegDirectory _directory;
    private JpegDescriptor _descriptor;

    @Before
    public void setUp() throws Exception
    {
        _directory = new JpegDirectory();
        _descriptor = new JpegDescriptor(_directory);
    }

    @Test
    public void testGetComponentDataDescription_InvalidComponentNumber() throws Exception
    {
        Assert.assertNull(_descriptor.getComponentDataDescription(1));
    }

    @Test
    public void testGetImageWidthDescription() throws Exception
    {
        _directory.setInt(JpegDirectory.TAG_JPEG_IMAGE_WIDTH, 123);
        Assert.assertEquals("123 pixels", _descriptor.getImageWidthDescription());
        Assert.assertEquals("123 pixels", _directory.getDescription(JpegDirectory.TAG_JPEG_IMAGE_WIDTH));
    }

    @Test
    public void testGetImageHeightDescription() throws Exception
    {
        _directory.setInt(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT, 123);
        Assert.assertEquals("123 pixels", _descriptor.getImageHeightDescription());
        Assert.assertEquals("123 pixels", _directory.getDescription(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT));
    }

    @Test
    public void testGetDataPrecisionDescription() throws Exception
    {
        _directory.setInt(JpegDirectory.TAG_JPEG_DATA_PRECISION, 8);
        Assert.assertEquals("8 bits", _descriptor.getDataPrecisionDescription());
        Assert.assertEquals("8 bits", _directory.getDescription(JpegDirectory.TAG_JPEG_DATA_PRECISION));
    }

    @Test
    public void testGetComponentDescription() throws MetadataException
    {
        JpegComponent component1 = new JpegComponent(1, 0x22, 0);
        _directory.setObject(JpegDirectory.TAG_JPEG_COMPONENT_DATA_1, component1);
        Assert.assertEquals("Y component: Quantization table 0, Sampling factors 2 horiz/2 vert", _directory.getDescription(JpegDirectory.TAG_JPEG_COMPONENT_DATA_1));
        Assert.assertEquals("Y component: Quantization table 0, Sampling factors 2 horiz/2 vert", _descriptor.getComponentDataDescription(0));
    }
}

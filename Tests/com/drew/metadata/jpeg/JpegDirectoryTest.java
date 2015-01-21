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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class JpegDirectoryTest
{
    private JpegDirectory _directory;

    @Before
    public void setUp()
    {
        _directory = new JpegDirectory();
    }

    @Test
    public void testSetAndGetValue() throws Exception
    {
        _directory.setInt(123, 8);
        assertEquals(8, _directory.getInt(123));
    }

    @Test
    public void testGetComponent_NotAdded()
    {
        assertNull(_directory.getComponent(1));
    }

    // NOTE tests for individual tag values exist in JpegReaderTest.java

    @Test
    public void testGetImageWidth() throws Exception
    {
        _directory.setInt(JpegDirectory.TAG_IMAGE_WIDTH, 123);
        assertEquals(123, _directory.getImageWidth());
    }

    @Test
    public void testGetImageHeight() throws Exception
    {
        _directory.setInt(JpegDirectory.TAG_IMAGE_HEIGHT, 123);
        assertEquals(123, _directory.getImageHeight());
    }


    @Test
    public void testGetNumberOfComponents() throws Exception
    {
        _directory.setInt(JpegDirectory.TAG_NUMBER_OF_COMPONENTS, 3);
        assertEquals(3, _directory.getNumberOfComponents());
        assertEquals("3", _directory.getDescription(JpegDirectory.TAG_NUMBER_OF_COMPONENTS));
    }

    @Test
    public void testGetComponent() throws Exception
    {
        JpegComponent component1 = new JpegComponent(1, 2, 3);
        JpegComponent component2 = new JpegComponent(1, 2, 3);
        JpegComponent component3 = new JpegComponent(1, 2, 3);
        JpegComponent component4 = new JpegComponent(1, 2, 3);

        _directory.setObject(JpegDirectory.TAG_COMPONENT_DATA_1, component1);
        _directory.setObject(JpegDirectory.TAG_COMPONENT_DATA_2, component2);
        _directory.setObject(JpegDirectory.TAG_COMPONENT_DATA_3, component3);
        _directory.setObject(JpegDirectory.TAG_COMPONENT_DATA_4, component4);

        // component numbers are zero-indexed for this method
        assertSame(component1, _directory.getComponent(0));
        assertSame(component2, _directory.getComponent(1));
        assertSame(component3, _directory.getComponent(2));
        assertSame(component4, _directory.getComponent(3));
    }
}

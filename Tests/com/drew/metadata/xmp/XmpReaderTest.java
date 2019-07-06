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
package com.drew.metadata.xmp;

import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.metadata.Metadata;
import com.drew.tools.FileUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class XmpReaderTest
{
    private XmpDirectory _directory;

    @Before
    public void setUp() throws Exception
    {
        Metadata metadata = new Metadata();
        List<byte[]> jpegSegments = new ArrayList<byte[]>();
        jpegSegments.add(FileUtil.readBytes("Tests/Data/withXmpAndIptc.jpg.app1.1"));
        new XmpReader().readJpegSegments(jpegSegments, metadata, JpegSegmentType.APP1);

        Collection<XmpDirectory> xmpDirectories = metadata.getDirectoriesOfType(XmpDirectory.class);

        assertNotNull(xmpDirectories);
        assertEquals(1, xmpDirectories.size());

        _directory = xmpDirectories.iterator().next();

        assertFalse(_directory.hasErrors());
    }

    @Test
    public void testExtract_HasXMPMeta() throws Exception
    {
        assertNotNull(_directory.getXMPMeta());
    }

    @Test
    public void testExtract_PropertyCount() throws Exception
    {
        assertEquals(168, _directory.getInt(XmpDirectory.TAG_XMP_VALUE_COUNT));
    }

    @Test
    public void testGetXmpProperties() throws Exception
    {
        Map<String,String> propertyMap = _directory.getXmpProperties();

        assertEquals(168, propertyMap.size());

        assertTrue(propertyMap.containsKey("photoshop:Country"));
        assertEquals("Deutschland", propertyMap.get("photoshop:Country"));

        assertTrue(propertyMap.containsKey("tiff:ImageLength"));
        assertEquals("900", propertyMap.get("tiff:ImageLength"));
    }
}

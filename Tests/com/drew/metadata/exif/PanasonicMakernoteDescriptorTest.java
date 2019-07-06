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

package com.drew.metadata.exif;

import com.drew.metadata.Age;
import com.drew.metadata.Face;
import com.drew.metadata.exif.makernotes.PanasonicMakernoteDirectory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author psandhaus, Drew Noakes
 */
public class PanasonicMakernoteDescriptorTest
{
    private PanasonicMakernoteDirectory _panasonicDirectory;

    @Before
    public void setUp() throws Exception
    {
        _panasonicDirectory = ExifReaderTest.processBytes("Tests/Data/withPanasonicFaces.jpg.app1", PanasonicMakernoteDirectory.class);
    }

    @Test
    public void testGetDetectedFaces() throws Exception
    {
        Face expResult = new Face(142, 120, 76, 76, null, null);
        Face[] result = _panasonicDirectory.getDetectedFaces();
        assertNotNull(result);
        assertEquals(expResult, result[0]);
    }

    @Test
    public void testGetRecognizedFaces() throws Exception
    {
        Face expResult = new Face(142, 120, 76, 76, "NIELS", new Age(31, 7, 15, 0, 0, 0));
        Face[] result = _panasonicDirectory.getRecognizedFaces();
        assertNotNull(result);
        assertEquals(expResult, result[0]);
    }
}

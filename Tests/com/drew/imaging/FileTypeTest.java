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
package com.drew.imaging;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FileTypeTest
{
    @Test
    public void testExtensions()
    {
        assertEquals("jpg", FileType.Jpeg.getCommonExtension());
        assertEquals("bmp", FileType.Bmp.getCommonExtension());

        assertEquals("JPEG", FileType.Jpeg.getName());
        assertEquals("BMP", FileType.Bmp.getName());

        assertArrayEquals(new String[]{"jpg", "jpeg", "jpe"}, FileType.Jpeg.getAllExtensions());

        assertNull(FileType.Unknown.getCommonExtension());
    }
}

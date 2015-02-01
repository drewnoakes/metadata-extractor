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

package com.drew.metadata.gif;

import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class GifReaderTest
{
    @NotNull
    public static GifHeaderDirectory processBytes(@NotNull String file) throws Exception
    {
        Metadata metadata = new Metadata();
        InputStream stream = new FileInputStream(file);
        new GifReader().extract(new StreamReader(stream), metadata);
        stream.close();

        GifHeaderDirectory directory = metadata.getFirstDirectoryOfType(GifHeaderDirectory.class);
        assertNotNull(directory);
        return directory;
    }

    @Test
    public void testMsPaintGif() throws Exception
    {
        GifHeaderDirectory directory = processBytes("Tests/Data/mspaint-10x10.gif");

        assertFalse(directory.hasErrors());

        assertEquals("89a", directory.getString(GifHeaderDirectory.TAG_GIF_FORMAT_VERSION));
        assertEquals(10, directory.getInt(GifHeaderDirectory.TAG_IMAGE_WIDTH));
        assertEquals(10, directory.getInt(GifHeaderDirectory.TAG_IMAGE_HEIGHT));
        assertEquals(256, directory.getInt(GifHeaderDirectory.TAG_COLOR_TABLE_SIZE));
        assertFalse(directory.getBoolean(GifHeaderDirectory.TAG_IS_COLOR_TABLE_SORTED));
        assertEquals(8, directory.getInt(GifHeaderDirectory.TAG_BITS_PER_PIXEL));
        assertTrue(directory.getBoolean(GifHeaderDirectory.TAG_HAS_GLOBAL_COLOR_TABLE));
        assertEquals(0, directory.getInt(GifHeaderDirectory.TAG_TRANSPARENT_COLOR_INDEX));
    }

    @Test
    public void testPhotoshopGif() throws Exception
    {
        GifHeaderDirectory directory = processBytes("Tests/Data/photoshop-8x12-32colors-alpha.gif");

        assertFalse(directory.hasErrors());

        assertEquals("89a", directory.getString(GifHeaderDirectory.TAG_GIF_FORMAT_VERSION));
        assertEquals(8, directory.getInt(GifHeaderDirectory.TAG_IMAGE_WIDTH));
        assertEquals(12, directory.getInt(GifHeaderDirectory.TAG_IMAGE_HEIGHT));
        assertEquals(32, directory.getInt(GifHeaderDirectory.TAG_COLOR_TABLE_SIZE));
        assertFalse(directory.getBoolean(GifHeaderDirectory.TAG_IS_COLOR_TABLE_SORTED));
        assertEquals(5, directory.getInt(GifHeaderDirectory.TAG_BITS_PER_PIXEL));
        assertTrue(directory.getBoolean(GifHeaderDirectory.TAG_HAS_GLOBAL_COLOR_TABLE));
        assertEquals(8, directory.getInt(GifHeaderDirectory.TAG_TRANSPARENT_COLOR_INDEX));
    }
}

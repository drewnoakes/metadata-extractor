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

package com.drew.metadata.bmp;

import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class BmpReaderTest
{
    @NotNull
    public static BmpHeaderDirectory processBytes(@NotNull String file) throws Exception
    {
        Metadata metadata = new Metadata();
        InputStream stream = new FileInputStream(file);
        new BmpReader().extract(new StreamReader(stream), metadata);
        stream.close();

        BmpHeaderDirectory directory = metadata.getFirstDirectoryOfType(BmpHeaderDirectory.class);
        assertNotNull(directory);
        return directory;
    }

    @Test
    public void testMsPaint16color() throws Exception
    {
        BmpHeaderDirectory directory = processBytes("Tests/Data/16color-10x10.bmp");

        assertFalse(directory.hasErrors());

        assertEquals(10, directory.getInt(BmpHeaderDirectory.TAG_IMAGE_WIDTH));
        assertEquals(10, directory.getInt(BmpHeaderDirectory.TAG_IMAGE_HEIGHT));
        assertEquals(4, directory.getInt(BmpHeaderDirectory.TAG_BITS_PER_PIXEL));
        assertEquals("None", directory.getDescription(BmpHeaderDirectory.TAG_COMPRESSION));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_X_PIXELS_PER_METER));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_Y_PIXELS_PER_METER));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_PALETTE_COLOUR_COUNT));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_IMPORTANT_COLOUR_COUNT));
        assertEquals(1, directory.getInt(BmpHeaderDirectory.TAG_COLOUR_PLANES));
        assertEquals(40, directory.getInt(BmpHeaderDirectory.TAG_HEADER_SIZE));
    }

    @Test
    public void testMsPaint24bpp() throws Exception
    {
        BmpHeaderDirectory directory = processBytes("Tests/Data/24bpp-10x10.bmp");

        assertFalse(directory.hasErrors());

        assertEquals(10, directory.getInt(BmpHeaderDirectory.TAG_IMAGE_WIDTH));
        assertEquals(10, directory.getInt(BmpHeaderDirectory.TAG_IMAGE_HEIGHT));
        assertEquals(24, directory.getInt(BmpHeaderDirectory.TAG_BITS_PER_PIXEL));
        assertEquals("None", directory.getDescription(BmpHeaderDirectory.TAG_COMPRESSION));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_X_PIXELS_PER_METER));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_Y_PIXELS_PER_METER));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_PALETTE_COLOUR_COUNT));
        assertEquals(0, directory.getInt(BmpHeaderDirectory.TAG_IMPORTANT_COLOUR_COUNT));
        assertEquals(1, directory.getInt(BmpHeaderDirectory.TAG_COLOUR_PLANES));
        assertEquals(40, directory.getInt(BmpHeaderDirectory.TAG_HEADER_SIZE));
    }
}

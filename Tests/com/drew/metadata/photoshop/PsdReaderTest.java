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

package com.drew.metadata.photoshop;

import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PsdReaderTest
{
    @NotNull
    public static PsdHeaderDirectory processBytes(@NotNull String file) throws Exception
    {
        Metadata metadata = new Metadata();
        InputStream stream = new FileInputStream(new File(file));
        try {
            new PsdReader().extract(new StreamReader(stream), metadata);
        } catch (Exception e) {
            stream.close();
            throw e;
        }

        PsdHeaderDirectory directory = metadata.getFirstDirectoryOfType(PsdHeaderDirectory.class);
        assertNotNull(directory);
        return directory;
    }

    @Test
    public void test8x8x8bitGrayscale() throws Exception
    {
        PsdHeaderDirectory directory = processBytes("Tests/Data/8x4x8bit-Grayscale.psd");

        assertEquals(8, directory.getInt(PsdHeaderDirectory.TAG_IMAGE_WIDTH));
        assertEquals(4, directory.getInt(PsdHeaderDirectory.TAG_IMAGE_HEIGHT));
        assertEquals(8, directory.getInt(PsdHeaderDirectory.TAG_BITS_PER_CHANNEL));
        assertEquals(1, directory.getInt(PsdHeaderDirectory.TAG_CHANNEL_COUNT));
        assertEquals(1, directory.getInt(PsdHeaderDirectory.TAG_COLOR_MODE)); // 1 = grayscale
    }

    @Test
    public void test10x12x16bitCMYK() throws Exception
    {
        PsdHeaderDirectory directory = processBytes("Tests/Data/10x12x16bit-CMYK.psd");

        assertEquals(10, directory.getInt(PsdHeaderDirectory.TAG_IMAGE_WIDTH));
        assertEquals(12, directory.getInt(PsdHeaderDirectory.TAG_IMAGE_HEIGHT));
        assertEquals(16, directory.getInt(PsdHeaderDirectory.TAG_BITS_PER_CHANNEL));
        assertEquals(4, directory.getInt(PsdHeaderDirectory.TAG_CHANNEL_COUNT));
        assertEquals(4, directory.getInt(PsdHeaderDirectory.TAG_COLOR_MODE)); // 4 = CMYK
    }
}

/*
 * Copyright 2002-2012 Drew Noakes
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

package com.drew.metadata.photoshop;

import com.drew.lang.RandomAccessFileReader;
import com.drew.metadata.Metadata;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class PsdReaderTest
{
    public PsdHeaderDirectory readPsdHeader(String file) throws Exception
    {
        Metadata metadata = new Metadata();
        RandomAccessFile randomAccessFile = new RandomAccessFile(new File(file), "r");
        new PsdReader().extract(new RandomAccessFileReader(randomAccessFile), metadata);
        randomAccessFile.close();
        Assert.assertTrue(metadata.containsDirectory(PsdHeaderDirectory.class));
        return metadata.getOrCreateDirectory(PsdHeaderDirectory.class);
    }

    @Test
    public void test8x8x8bitGrayscale() throws Exception
    {
        PsdHeaderDirectory directory = readPsdHeader("Tests/com/drew/metadata/photoshop/8x4x8bit-Grayscale.psd");
        Assert.assertEquals(8, directory.getInt(PsdHeaderDirectory.TAG_IMAGE_WIDTH));
        Assert.assertEquals(4, directory.getInt(PsdHeaderDirectory.TAG_IMAGE_HEIGHT));
        Assert.assertEquals(8, directory.getInt(PsdHeaderDirectory.TAG_BITS_PER_CHANNEL));
        Assert.assertEquals(1, directory.getInt(PsdHeaderDirectory.TAG_CHANNEL_COUNT));
        Assert.assertEquals(1, directory.getInt(PsdHeaderDirectory.TAG_COLOR_MODE)); // 1 = grayscale
    }

    @Test
    public void test10x12x16bitCMYK() throws Exception
    {
        PsdHeaderDirectory directory = readPsdHeader("Tests/com/drew/metadata/photoshop/10x12x16bit-CMYK.psd");
        Assert.assertEquals(10, directory.getInt(PsdHeaderDirectory.TAG_IMAGE_WIDTH));
        Assert.assertEquals(12, directory.getInt(PsdHeaderDirectory.TAG_IMAGE_HEIGHT));
        Assert.assertEquals(16, directory.getInt(PsdHeaderDirectory.TAG_BITS_PER_CHANNEL));
        Assert.assertEquals(4, directory.getInt(PsdHeaderDirectory.TAG_CHANNEL_COUNT));
        Assert.assertEquals(4, directory.getInt(PsdHeaderDirectory.TAG_COLOR_MODE)); // 4 = CMYK
    }
}

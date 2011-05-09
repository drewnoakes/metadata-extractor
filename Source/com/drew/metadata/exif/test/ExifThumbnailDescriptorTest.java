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

package com.drew.metadata.exif.test;

import com.drew.metadata.exif.ExifThumbnailDescriptor;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test case for class ExifThumbnailDescriptor.
 *
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ExifThumbnailDescriptorTest
{
    @Test
    public void testGetYCbCrSubsamplingDescription() throws Exception
    {
        ExifThumbnailDirectory directory = new ExifThumbnailDirectory();
        directory.setIntArray(ExifThumbnailDirectory.TAG_YCBCR_SUBSAMPLING, new int[]{2, 1});

        ExifThumbnailDescriptor descriptor = new ExifThumbnailDescriptor(directory);
        Assert.assertEquals("YCbCr4:2:2", descriptor.getDescription(ExifThumbnailDirectory.TAG_YCBCR_SUBSAMPLING));
        Assert.assertEquals("YCbCr4:2:2", descriptor.getYCbCrSubsamplingDescription());

        directory.setIntArray(ExifThumbnailDirectory.TAG_YCBCR_SUBSAMPLING, new int[]{2, 2});

        Assert.assertEquals("YCbCr4:2:0", descriptor.getDescription(ExifThumbnailDirectory.TAG_YCBCR_SUBSAMPLING));
        Assert.assertEquals("YCbCr4:2:0", descriptor.getYCbCrSubsamplingDescription());
    }

    @Test
    public void testThumbnailDataGetDescription() throws Exception
    {
        // This isn't a real Exif tag, but we use it as though it is one just to store the bytes and so it can
        // be used in conjunction with the descriptor.
        final int thumbnailDataTagId = 0xF001;

        ExifThumbnailDirectory directory = new ExifThumbnailDirectory();
        directory.setByteArray(thumbnailDataTagId, new byte[50]);

        ExifThumbnailDescriptor descriptor = new ExifThumbnailDescriptor(directory);
        Assert.assertEquals("[50 bytes]", descriptor.getDescription(thumbnailDataTagId));
    }
}

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

import org.junit.Test;

import static com.drew.metadata.exif.ExifThumbnailDirectory.*;
import static org.junit.Assert.assertEquals;

/**
 * JUnit test case for class ExifThumbnailDescriptor.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ExifThumbnailDescriptorTest
{
    @Test
    public void testGetYCbCrSubsamplingDescription() throws Exception
    {
        ExifThumbnailDirectory directory = new ExifThumbnailDirectory();
        directory.setIntArray(TAG_YCBCR_SUBSAMPLING, new int[]{2, 1});

        ExifThumbnailDescriptor descriptor = new ExifThumbnailDescriptor(directory);
        assertEquals("YCbCr4:2:2", descriptor.getDescription(TAG_YCBCR_SUBSAMPLING));
        assertEquals("YCbCr4:2:2", descriptor.getYCbCrSubsamplingDescription());

        directory.setIntArray(TAG_YCBCR_SUBSAMPLING, new int[]{2, 2});

        assertEquals("YCbCr4:2:0", descriptor.getDescription(TAG_YCBCR_SUBSAMPLING));
        assertEquals("YCbCr4:2:0", descriptor.getYCbCrSubsamplingDescription());
    }
}

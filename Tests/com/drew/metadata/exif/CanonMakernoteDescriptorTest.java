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
package com.drew.metadata.exif;

import com.drew.metadata.exif.makernotes.CanonMakernoteDescriptor;
import com.drew.metadata.exif.makernotes.CanonMakernoteDirectory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class CanonMakernoteDescriptorTest
{
    @Test
    public void testGetFlashBiasDescription() throws Exception
    {
        CanonMakernoteDirectory directory = new CanonMakernoteDirectory();
        CanonMakernoteDescriptor descriptor = new CanonMakernoteDescriptor(directory);

        // set and check values

        directory.setInt(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS, 0xFFC0);
        assertEquals("-2.0 EV", descriptor.getDescription(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS, 0xffd4);
        assertEquals("-1.375 EV", descriptor.getDescription(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS, 0x0000);
        assertEquals("0.0 EV", descriptor.getDescription(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS, 0x000c);
        assertEquals("0.375 EV", descriptor.getDescription(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS, 0x0010);
        assertEquals("0.5 EV", descriptor.getDescription(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS, 0x0014);
        assertEquals("0.625 EV", descriptor.getDescription(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS, 0x0020);
        assertEquals("1.0 EV", descriptor.getDescription(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS, 0x0030);
        assertEquals("1.5 EV", descriptor.getDescription(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS, 0x0034);
        assertEquals("1.625 EV", descriptor.getDescription(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS, 0x0040);
        assertEquals("2.0 EV", descriptor.getDescription(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS));
    }
}

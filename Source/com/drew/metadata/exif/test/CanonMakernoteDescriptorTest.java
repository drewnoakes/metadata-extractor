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

import com.drew.metadata.exif.CanonMakernoteDescriptor;
import com.drew.metadata.exif.CanonMakernoteDirectory;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class CanonMakernoteDescriptorTest
{
    @Test
    public void testGetFlashBiasDescription() throws Exception
    {
        CanonMakernoteDirectory directory = new CanonMakernoteDirectory();
        CanonMakernoteDescriptor descriptor = new CanonMakernoteDescriptor(directory);

        // set and check values

        directory.setInt(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS, 0xFFC0);
        Assert.assertEquals("-2.0 EV", descriptor.getDescription(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS, 0xffd4);
        Assert.assertEquals("-1.375 EV", descriptor.getDescription(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS, 0x0000);
        Assert.assertEquals("0.0 EV", descriptor.getDescription(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS, 0x000c);
        Assert.assertEquals("0.375 EV", descriptor.getDescription(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS, 0x0010);
        Assert.assertEquals("0.5 EV", descriptor.getDescription(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS, 0x0014);
        Assert.assertEquals("0.625 EV", descriptor.getDescription(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS, 0x0020);
        Assert.assertEquals("1.0 EV", descriptor.getDescription(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS, 0x0030);
        Assert.assertEquals("1.5 EV", descriptor.getDescription(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS, 0x0034);
        Assert.assertEquals("1.625 EV", descriptor.getDescription(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS));

        directory.setInt(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS, 0x0040);
        Assert.assertEquals("2.0 EV", descriptor.getDescription(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS));
    }
}

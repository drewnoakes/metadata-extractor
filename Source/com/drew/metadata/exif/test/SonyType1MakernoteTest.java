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

package com.drew.metadata.exif.test;

import com.drew.lang.test.TestHelper;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.SonyType1MakernoteDescriptor;
import com.drew.metadata.exif.SonyType1MakernoteDirectory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/** @author Drew Noakes http://drewnoakes.com */
public class SonyType1MakernoteTest
{
    @Test public void testSonyType1Makernote() throws ClassNotFoundException, IOException
    {
        Metadata metadata = TestHelper.readJpegMetadataFile("Source/com/drew/metadata/exif/test/sonyType1.metadata");

        SonyType1MakernoteDirectory directory = metadata.getDirectory(SonyType1MakernoteDirectory.class);

        Assert.assertNotNull(directory);
        Assert.assertFalse(directory.hasErrors());

        SonyType1MakernoteDescriptor descriptor = new SonyType1MakernoteDescriptor(directory);

        Assert.assertNull(directory.getObject(SonyType1MakernoteDirectory.TAG_COLOR_TEMPERATURE));
        Assert.assertNull(descriptor.getColorTemperatureDescription());
        Assert.assertNull(directory.getObject(SonyType1MakernoteDirectory.TAG_SCENE_MODE));
        Assert.assertNull(descriptor.getSceneModeDescription());
        Assert.assertNull(directory.getObject(SonyType1MakernoteDirectory.TAG_ZONE_MATCHING));
        Assert.assertNull(descriptor.getZoneMatchingDescription());
        Assert.assertNull(directory.getObject(SonyType1MakernoteDirectory.TAG_DYNAMIC_RANGE_OPTIMISER));
        Assert.assertNull(descriptor.getDynamicRangeOptimizerDescription());
        Assert.assertNull(directory.getObject(SonyType1MakernoteDirectory.TAG_IMAGE_STABILISATION));
        Assert.assertNull(descriptor.getImageStabilizationDescription());
        Assert.assertNull(directory.getObject(SonyType1MakernoteDirectory.TAG_COLOR_MODE));
        Assert.assertNull(descriptor.getColorModeDescription());

        Assert.assertEquals("On (Shooting)", descriptor.getAntiBlurDescription());
        Assert.assertEquals("Auto", descriptor.getExposureModeDescription());
        Assert.assertEquals("Off", descriptor.getLongExposureNoiseReductionDescription());
        Assert.assertEquals("Off", descriptor.getMacroDescription());
        Assert.assertEquals("Normal", descriptor.getQualityDescription());
    }
}

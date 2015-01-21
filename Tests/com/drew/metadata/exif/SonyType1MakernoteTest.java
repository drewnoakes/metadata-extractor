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

import com.drew.metadata.exif.makernotes.SonyType1MakernoteDescriptor;
import com.drew.metadata.exif.makernotes.SonyType1MakernoteDirectory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class SonyType1MakernoteTest
{
    @Test public void testSonyType1Makernote() throws Exception
    {
        SonyType1MakernoteDirectory directory = ExifReaderTest.processBytes("Tests/Data/sonyType1.jpg.app1", SonyType1MakernoteDirectory.class);

        assertNotNull(directory);
        assertFalse(directory.hasErrors());

        SonyType1MakernoteDescriptor descriptor = new SonyType1MakernoteDescriptor(directory);

        assertNull(directory.getObject(SonyType1MakernoteDirectory.TAG_COLOR_TEMPERATURE));
        assertNull(descriptor.getColorTemperatureDescription());
        assertNull(directory.getObject(SonyType1MakernoteDirectory.TAG_SCENE_MODE));
        assertNull(descriptor.getSceneModeDescription());
        assertNull(directory.getObject(SonyType1MakernoteDirectory.TAG_ZONE_MATCHING));
        assertNull(descriptor.getZoneMatchingDescription());
        assertNull(directory.getObject(SonyType1MakernoteDirectory.TAG_DYNAMIC_RANGE_OPTIMISER));
        assertNull(descriptor.getDynamicRangeOptimizerDescription());
        assertNull(directory.getObject(SonyType1MakernoteDirectory.TAG_IMAGE_STABILISATION));
        assertNull(descriptor.getImageStabilizationDescription());
        assertNull(directory.getObject(SonyType1MakernoteDirectory.TAG_COLOR_MODE));
        assertNull(descriptor.getColorModeDescription());

        assertEquals("On (Shooting)", descriptor.getAntiBlurDescription());
        assertEquals("Program", descriptor.getExposureModeDescription());
        assertEquals("Off", descriptor.getLongExposureNoiseReductionDescription());
        assertEquals("Off", descriptor.getMacroDescription());
        assertEquals("Normal", descriptor.getJpegQualityDescription());
    }
}

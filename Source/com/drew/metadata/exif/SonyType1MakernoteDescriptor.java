/*
 * Copyright 2002-2013 Drew Noakes
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
package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string representations of tag values stored in a <code>SonyType1MakernoteDirectory</code>.
 * Thanks to David Carson for the initial version of this class.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class SonyType1MakernoteDescriptor extends TagDescriptor<SonyType1MakernoteDirectory>
{
    public SonyType1MakernoteDescriptor(@NotNull SonyType1MakernoteDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case SonyType1MakernoteDirectory.TAG_FLASH_EXPOSURE_COMP:
                return getFlashExposureCompensationDescription();
            case SonyType1MakernoteDirectory.TAG_COLOR_TEMPERATURE:
                return getColorTemperatureDescription();
            case SonyType1MakernoteDirectory.TAG_SCENE_MODE:
                return getSceneModeDescription();
            case SonyType1MakernoteDirectory.TAG_ZONE_MATCHING:
                return getZoneMatchingDescription();
            case SonyType1MakernoteDirectory.TAG_DYNAMIC_RANGE_OPTIMISER:
                return getDynamicRangeOptimizerDescription();
            case SonyType1MakernoteDirectory.TAG_IMAGE_STABILISATION:
                return getImageStabilizationDescription();
            // Unfortunately it seems that there is no definite mapping between a lens ID and a lens model
            // http://gvsoft.homedns.org/exif/makernote-sony-type1.html#0xb027
//            case SonyType1MakernoteDirectory.TAG_LENS_ID:
//                return getLensIDDescription();
            case SonyType1MakernoteDirectory.TAG_COLOR_MODE:
                return getColorModeDescription();
            case SonyType1MakernoteDirectory.TAG_MACRO:
                return getMacroDescription();
            case SonyType1MakernoteDirectory.TAG_EXPOSURE_MODE:
                return getExposureModeDescription();
            case SonyType1MakernoteDirectory.TAG_JPEG_QUALITY:
                return getJpegQualityDescription();
            case SonyType1MakernoteDirectory.TAG_ANTI_BLUR:
                return getAntiBlurDescription();
            case SonyType1MakernoteDirectory.TAG_LONG_EXPOSURE_NOISE_REDUCTION:
                return getLongExposureNoiseReductionDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getFlashExposureCompensationDescription()
    {
        Integer value = _directory.getInteger(SonyType1MakernoteDirectory.TAG_FLASH_EXPOSURE_COMP);
        if (value==null)
            return null;
        return String.format("%d EV", value);
    }

    @Nullable
    public String getColorTemperatureDescription()
    {
        Integer value = _directory.getInteger(SonyType1MakernoteDirectory.TAG_COLOR_TEMPERATURE);
        if (value==null)
            return null;
        if (value==0)
            return "Auto";
        int kelvin = ((value & 0x00FF0000) >> 8) | ((value & 0xFF000000) >> 24);
        return String.format("%d K", kelvin);
    }

    @Nullable
    public String getSceneModeDescription()
    {
        Integer value = _directory.getInteger(SonyType1MakernoteDirectory.TAG_SCENE_MODE);
        if (value==null)
            return null;
        switch (value){
            case 0: return "Standard";
            case 1: return "Portrait";
            case 2: return "Text";
            case 3: return "Night Scene";
            case 4: return "Sunset";
            case 5: return "Sports";
            case 6: return "Landscape";
            case 7: return "Night Portrait";
            case 8: return "Macro";
            case 9: return "Super Macro";
            case 16: return "Auto";
            case 17: return "Night View/Portrait";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getZoneMatchingDescription()
    {
        Integer value = _directory.getInteger(SonyType1MakernoteDirectory.TAG_ZONE_MATCHING);
        if (value==null)
            return null;
        switch (value){
            case 0: return "ISO Setting Used";
            case 1: return "High Key";
            case 2: return "Low Key";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getDynamicRangeOptimizerDescription()
    {
        Integer value = _directory.getInteger(SonyType1MakernoteDirectory.TAG_DYNAMIC_RANGE_OPTIMISER);
        if (value==null)
            return null;
        switch (value){
            case 0: return "Off";
            case 1: return "Standard";
            case 2: return "Advanced Auto";
            case 8: return "Advanced LV1";
            case 9: return "Advanced LV2";
            case 10: return "Advanced LV3";
            case 11: return "Advanced LV4";
            case 12: return "Advanced LV5";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getImageStabilizationDescription()
    {
        Integer value = _directory.getInteger(SonyType1MakernoteDirectory.TAG_IMAGE_STABILISATION);
        if (value==null)
            return null;
        // Different non-zero 'on' values have been observed.  Do they mean different things?
        return value == 0 ? "Off" : "On";
    }

    @Nullable
    public String getColorModeDescription()
    {
        Integer value = _directory.getInteger(SonyType1MakernoteDirectory.TAG_COLOR_MODE);
        if (value==null)
            return null;
        switch (value){
            case 0: return "Standard";
            case 1: return "Vivid";
            case 2: return "Portrait";
            case 3: return "Landscape";
            case 4: return "Sunset";
            case 5: return "Night Portrait";
            case 6: return "Black & White";
            case 7: return "Adobe RGB";
            case 12:
            case 100: return "Neutral";
            case 101: return "Clear";
            case 102: return "Deep";
            case 103: return "Light";
            case 104: return "Night View";
            case 105: return "Autumn Leaves";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getMacroDescription()
    {
        Integer value = _directory.getInteger(SonyType1MakernoteDirectory.TAG_MACRO);
        if (value==null)
            return null;
        switch (value){
            case 0: return "Off";
            case 1: return "On";
            case 2: return "Magnifying Glass/Super Macro";
            case 0xFFFF: return "N/A";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getExposureModeDescription()
    {
        Integer value = _directory.getInteger(SonyType1MakernoteDirectory.TAG_EXPOSURE_MODE);
        if (value==null)
            return null;
        switch (value){
            case 0: return "Auto";
            case 5: return "Landscape";
            case 6: return "Program";
            case 7: return "Aperture Priority";
            case 8: return "Shutter Priority";
            case 9: return "Night Scene";
            case 15: return "Manual";
            case 0xFFFF: return "N/A";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getJpegQualityDescription()
    {
        Integer value = _directory.getInteger(SonyType1MakernoteDirectory.TAG_JPEG_QUALITY);
        if (value==null)
            return null;
        switch (value){
            case 0: return "Normal";
            case 1: return "Fine";
            case 0xFFFF: return "N/A";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getAntiBlurDescription()
    {
        Integer value = _directory.getInteger(SonyType1MakernoteDirectory.TAG_ANTI_BLUR);
        if (value==null)
            return null;
        switch (value){
            case 0: return "Off";
            case 1: return "On (Continuous)";
            case 2: return "On (Shooting)";
            case 0xFFFF: return "N/A";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getLongExposureNoiseReductionDescription()
    {
        Integer value = _directory.getInteger(SonyType1MakernoteDirectory.TAG_LONG_EXPOSURE_NOISE_REDUCTION);
        if (value==null)
            return null;
        switch (value){
            case 0: return "Off";
            case 1: return "On";
            case 0xFFFF: return "N/A";
            default: return String.format("Unknown (%d)", value);
        }
    }
}

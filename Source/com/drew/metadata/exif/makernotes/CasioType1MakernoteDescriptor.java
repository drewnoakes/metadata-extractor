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
package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.makernotes.CasioType1MakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link CasioType1MakernoteDirectory}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class CasioType1MakernoteDescriptor extends TagDescriptor<CasioType1MakernoteDirectory>
{
    public CasioType1MakernoteDescriptor(@NotNull CasioType1MakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_RECORDING_MODE:
                return getRecordingModeDescription();
            case TAG_QUALITY:
                return getQualityDescription();
            case TAG_FOCUSING_MODE:
                return getFocusingModeDescription();
            case TAG_FLASH_MODE:
                return getFlashModeDescription();
            case TAG_FLASH_INTENSITY:
                return getFlashIntensityDescription();
            case TAG_OBJECT_DISTANCE:
                return getObjectDistanceDescription();
            case TAG_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case TAG_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case TAG_SHARPNESS:
                return getSharpnessDescription();
            case TAG_CONTRAST:
                return getContrastDescription();
            case TAG_SATURATION:
                return getSaturationDescription();
            case TAG_CCD_SENSITIVITY:
                return getCcdSensitivityDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getCcdSensitivityDescription()
    {
        Integer value = _directory.getInteger(TAG_CCD_SENSITIVITY);

        if (value == null)
            return null;

        switch (value) {
            // these four for QV3000
            case 64: return "Normal";
            case 125: return "+1.0";
            case 250: return "+2.0";
            case 244: return "+3.0";
            // these two for QV8000/2000
            case 80: return "Normal (ISO 80 equivalent)";
            case 100: return "High";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSaturationDescription()
    {
        return getIndexedDescription(TAG_SATURATION, "Normal", "Low", "High");
    }

    @Nullable
    public String getContrastDescription()
    {
        return getIndexedDescription(TAG_CONTRAST, "Normal", "Low", "High");
    }

    @Nullable
    public String getSharpnessDescription()
    {
        return getIndexedDescription(TAG_SHARPNESS, "Normal", "Soft", "Hard");
    }

    @Nullable
    public String getDigitalZoomDescription()
    {
        Integer value = _directory.getInteger(TAG_DIGITAL_ZOOM);

        if (value == null)
            return null;

        switch (value) {
            case 0x10000: return "No digital zoom";
            case 0x10001: return "2x digital zoom";
            case 0x20000: return "2x digital zoom";
            case 0x40000: return "4x digital zoom";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        Integer value = _directory.getInteger(TAG_WHITE_BALANCE);

        if (value == null)
            return null;

        switch (value) {
            case 1: return "Auto";
            case 2: return "Tungsten";
            case 3: return "Daylight";
            case 4: return "Florescent";
            case 5: return "Shade";
            case 129: return "Manual";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getObjectDistanceDescription()
    {
        Integer value = _directory.getInteger(TAG_OBJECT_DISTANCE);
        return value == null ? null : getFocalLengthDescription(value);
    }

    @Nullable
    public String getFlashIntensityDescription()
    {
        Integer value = _directory.getInteger(TAG_FLASH_INTENSITY);

        if (value == null)
            return null;

        switch (value) {
            case 11: return "Weak";
            case 13: return "Normal";
            case 15: return "Strong";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFlashModeDescription()
    {
        return getIndexedDescription(TAG_FLASH_MODE, 1, "Auto", "On", "Off", "Red eye reduction");
    }

    @Nullable
    public String getFocusingModeDescription()
    {
        return getIndexedDescription(TAG_FOCUSING_MODE, 2, "Macro", "Auto focus", "Manual focus", "Infinity");
    }

    @Nullable
    public String getQualityDescription()
    {
        return getIndexedDescription(TAG_QUALITY, 1, "Economy", "Normal", "Fine");
    }

    @Nullable
    public String getRecordingModeDescription()
    {
        return getIndexedDescription(TAG_RECORDING_MODE, 1, "Single shutter", "Panorama", "Night scene", "Portrait", "Landscape");
    }
}

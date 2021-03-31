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

import static com.drew.metadata.exif.makernotes.PentaxMakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link PentaxMakernoteDirectory}.
 * <p>
 * Some information about this makernote taken from here:
 * http://www.ozhiker.com/electronics/pjmt/jpeg_info/pentax_mn.html
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class PentaxMakernoteDescriptor extends TagDescriptor<PentaxMakernoteDirectory>
{
    public PentaxMakernoteDescriptor(@NotNull PentaxMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_CAPTURE_MODE:
                return getCaptureModeDescription();
            case TAG_QUALITY_LEVEL:
                return getQualityLevelDescription();
            case TAG_FOCUS_MODE:
                return getFocusModeDescription();
            case TAG_FLASH_MODE:
                return getFlashModeDescription();
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
            case TAG_ISO_SPEED:
                return getIsoSpeedDescription();
            case TAG_COLOUR:
                return getColourDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getColourDescription()
    {
        return getIndexedDescription(TAG_COLOUR, 1, "Normal", "Black & White", "Sepia");
    }

    @Nullable
    public String getIsoSpeedDescription()
    {
        Integer value = _directory.getInteger(TAG_ISO_SPEED);
        if (value == null)
            return null;
        switch (value) {
            // TODO there must be other values which aren't catered for here
            case 10: return "ISO 100";
            case 16: return "ISO 200";
            case 100: return "ISO 100";
            case 200: return "ISO 200";
            default: return "Unknown (" + value + ")";
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
        Float value = _directory.getFloatObject(TAG_DIGITAL_ZOOM);
        if (value == null)
            return null;
        if (value == 0)
            return "Off";
        return Float.toString(value);
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        return getIndexedDescription(TAG_WHITE_BALANCE,
            "Auto", "Daylight", "Shade", "Tungsten", "Fluorescent", "Manual");
    }

    @Nullable
    public String getFlashModeDescription()
    {
        return getIndexedDescription(TAG_FLASH_MODE,
            1, "Auto", "Flash On", null, "Flash Off", null, "Red-eye Reduction");
    }

    @Nullable
    public String getFocusModeDescription()
    {
        return getIndexedDescription(TAG_FOCUS_MODE, 2, "Custom", "Auto");
    }

    @Nullable
    public String getQualityLevelDescription()
    {
        return getIndexedDescription(TAG_QUALITY_LEVEL, "Good", "Better", "Best");
    }

    @Nullable
    public String getCaptureModeDescription()
    {
        return getIndexedDescription(TAG_CAPTURE_MODE,
            "Auto", "Night-scene", "Manual", null, "Multiple");
    }
}

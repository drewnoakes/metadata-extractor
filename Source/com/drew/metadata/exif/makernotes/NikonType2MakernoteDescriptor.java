/*
 * Copyright 2002-2017 Drew Noakes
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

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.text.DecimalFormat;

import static com.drew.metadata.exif.makernotes.NikonType2MakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link NikonType2MakernoteDirectory}.
 *
 * Type-2 applies to the E990 and D-series cameras such as the D1, D70 and D100.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class NikonType2MakernoteDescriptor extends TagDescriptor<NikonType2MakernoteDirectory>
{
    public NikonType2MakernoteDescriptor(@NotNull NikonType2MakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType)
        {
            case TAG_PROGRAM_SHIFT:
                return getProgramShiftDescription();
            case TAG_EXPOSURE_DIFFERENCE:
                return getExposureDifferenceDescription();
            case TAG_LENS:
                return getLensDescription();
            case TAG_CAMERA_HUE_ADJUSTMENT:
                return getHueAdjustmentDescription();
            case TAG_CAMERA_COLOR_MODE:
                return getColorModeDescription();
            case TAG_AUTO_FLASH_COMPENSATION:
                return getAutoFlashCompensationDescription();
            case TAG_FLASH_EXPOSURE_COMPENSATION:
                return getFlashExposureCompensationDescription();
            case TAG_FLASH_BRACKET_COMPENSATION:
                return getFlashBracketCompensationDescription();
            case TAG_EXPOSURE_TUNING:
                return getExposureTuningDescription();
            case TAG_LENS_STOPS:
                return getLensStopsDescription();
            case TAG_COLOR_SPACE:
                return getColorSpaceDescription();
            case TAG_ACTIVE_D_LIGHTING:
                return getActiveDLightingDescription();
            case TAG_VIGNETTE_CONTROL:
                return getVignetteControlDescription();
            case TAG_ISO_1:
                return getIsoSettingDescription();
            case TAG_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case TAG_FLASH_USED:
                return getFlashUsedDescription();
            case TAG_AF_FOCUS_POSITION:
                return getAutoFocusPositionDescription();
            case TAG_FIRMWARE_VERSION:
                return getFirmwareVersionDescription();
            case TAG_LENS_TYPE:
                return getLensTypeDescription();
            case TAG_SHOOTING_MODE:
                return getShootingModeDescription();
            case TAG_NEF_COMPRESSION:
                return getNEFCompressionDescription();
            case TAG_HIGH_ISO_NOISE_REDUCTION:
                return getHighISONoiseReductionDescription();
            case TAG_POWER_UP_TIME:
                return getPowerUpTimeDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getPowerUpTimeDescription()
    {
        return getEpochTimeDescription(TAG_POWER_UP_TIME);
    }

    @Nullable
    public String getHighISONoiseReductionDescription()
    {
        return getIndexedDescription(TAG_HIGH_ISO_NOISE_REDUCTION,
            "Off",
            "Minimal",
            "Low",
            null,
            "Normal",
            null,
            "High"
        );
    }

    @Nullable
    public String getFlashUsedDescription()
    {
        return getIndexedDescription(TAG_FLASH_USED,
            "Flash Not Used",
            "Manual Flash",
            null,
            "Flash Not Ready",
            null,
            null,
            null,
            "External Flash",
            "Fired, Commander Mode",
            "Fired, TTL Mode"
        );
    }

    @Nullable
    public String getNEFCompressionDescription()
    {
        return getIndexedDescription(TAG_NEF_COMPRESSION,
            1,
            "Lossy (Type 1)",
            null,
            "Uncompressed",
            null,
            null,
            null,
            "Lossless",
            "Lossy (Type 2)"
        );
    }

    @Nullable
    public String getShootingModeDescription()
    {
        return getBitFlagDescription(TAG_SHOOTING_MODE,
            // LSB [low label, high label]
            new String[]{"Single Frame", "Continuous"},
            "Delay",
            null,
            "PC Control",
            "Exposure Bracketing",
            "Auto ISO",
            "White-Balance Bracketing",
            "IR Control"
        );
    }

    @Nullable
    public String getLensTypeDescription()
    {
        return getBitFlagDescription(TAG_LENS_TYPE,
            // LSB [low label, high label]
            new String[]{"AF", "MF"},
            "D",
            "G",
            "VR"
        );
    }

    @Nullable
    public String getColorSpaceDescription()
    {
        return getIndexedDescription(TAG_COLOR_SPACE,
            1,
            "sRGB",
            "Adobe RGB"
        );
    }

    @Nullable
    public String getActiveDLightingDescription()
    {
        Integer value = _directory.getInteger(TAG_ACTIVE_D_LIGHTING);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "Off";
            case 1: return "Light";
            case 3: return "Normal";
            case 5: return "High";
            case 7: return "Extra High";
            case 65535: return "Auto";
            default: return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getVignetteControlDescription()
    {
        Integer value = _directory.getInteger(TAG_VIGNETTE_CONTROL);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "Off";
            case 1: return "Low";
            case 3: return "Normal";
            case 5: return "High";
            default: return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getAutoFocusPositionDescription()
    {
        int[] values = _directory.getIntArray(TAG_AF_FOCUS_POSITION);
        if (values==null)
            return null;
        if (values.length != 4 || values[0] != 0 || values[2] != 0 || values[3] != 0) {
            return "Unknown (" + _directory.getString(TAG_AF_FOCUS_POSITION) + ")";
        }
        switch (values[1]) {
            case 0:
                return "Centre";
            case 1:
                return "Top";
            case 2:
                return "Bottom";
            case 3:
                return "Left";
            case 4:
                return "Right";
            default:
                return "Unknown (" + values[1] + ")";
        }
    }

    @Nullable
    public String getDigitalZoomDescription()
    {
        Rational value = _directory.getRational(TAG_DIGITAL_ZOOM);
        if (value==null)
            return null;
        return value.intValue() == 1
                ? "No digital zoom"
                : value.toSimpleString(true) + "x digital zoom";
    }

    @Nullable
    public String getProgramShiftDescription()
    {
        return getEVDescription(TAG_PROGRAM_SHIFT);
    }

    @Nullable
    public String getExposureDifferenceDescription()
    {
        return getEVDescription(TAG_EXPOSURE_DIFFERENCE);
    }

    @Nullable
    public String getAutoFlashCompensationDescription()
    {
        return getEVDescription(TAG_AUTO_FLASH_COMPENSATION);
    }

    @Nullable
    public String getFlashExposureCompensationDescription()
    {
        return getEVDescription(TAG_FLASH_EXPOSURE_COMPENSATION);
    }

    @Nullable
    public String getFlashBracketCompensationDescription()
    {
        return getEVDescription(TAG_FLASH_BRACKET_COMPENSATION);
    }

    @Nullable
    public String getExposureTuningDescription()
    {
        return getEVDescription(TAG_EXPOSURE_TUNING);
    }

    @Nullable
    public String getLensStopsDescription()
    {
        return getEVDescription(TAG_LENS_STOPS);
    }

    @Nullable
    private String getEVDescription(int tagType)
    {
        int[] values = _directory.getIntArray(tagType);
        if (values == null || values.length < 2)
            return null;
        if (values.length < 3 || values[2] == 0)
            return null;
        final DecimalFormat decimalFormat = new DecimalFormat("0.##");
        double ev = values[0] * values[1] / (double)values[2];
        return decimalFormat.format(ev) + " EV";
    }

    @Nullable
    public String getIsoSettingDescription()
    {
        int[] values = _directory.getIntArray(TAG_ISO_1);
        if (values == null)
            return null;
        if (values[0] != 0 || values[1] == 0)
            return "Unknown (" + _directory.getString(TAG_ISO_1) + ")";
        return "ISO " + values[1];
    }

    @Nullable
    public String getLensDescription()
    {
        return getLensSpecificationDescription(TAG_LENS);
    }

    @Nullable
    public String getHueAdjustmentDescription()
    {
        return getFormattedString(TAG_CAMERA_HUE_ADJUSTMENT, "%s degrees");
    }

    @Nullable
    public String getColorModeDescription()
    {
        String value = _directory.getString(TAG_CAMERA_COLOR_MODE);
        return value == null ? null : value.startsWith("MODE1") ? "Mode I (sRGB)" : value;
    }

    @Nullable
    public String getFirmwareVersionDescription()
    {
        return getVersionBytesDescription(TAG_FIRMWARE_VERSION, 2);
    }
}

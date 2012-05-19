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
package com.drew.metadata.exif;

import com.drew.lang.Rational;
import com.drew.lang.StringUtil;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Provides human-readable string representations of tag values stored in a <code>NikonType2MakernoteDirectory</code>.
 *
 * Type-2 applies to the E990 and D-series cameras such as the D1, D70 and D100.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class NikonType2MakernoteDescriptor extends TagDescriptor<NikonType2MakernoteDirectory>
{
    public NikonType2MakernoteDescriptor(@NotNull NikonType2MakernoteDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType)
        {
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_PROGRAM_SHIFT:
                return getProgramShiftDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_EXPOSURE_DIFFERENCE:
                return getExposureDifferenceDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS:
                return getLensDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_HUE_ADJUSTMENT:
                return getHueAdjustmentDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_COLOR_MODE:
                return getColorModeDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION:
                return getAutoFlashCompensationDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FLASH_EXPOSURE_COMPENSATION:
                return getFlashExposureCompensationDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FLASH_BRACKET_COMPENSATION:
                return getFlashBracketCompensationDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_EXPOSURE_TUNING:
                return getExposureTuningDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS_STOPS:
                return getLensStopsDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_COLOR_SPACE:
                return getColorSpaceDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ACTIVE_D_LIGHTING:
                return getActiveDLightingDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_VIGNETTE_CONTROL:
                return getVignetteControlDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_1:
                return getIsoSettingDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FLASH_USED:
                return getFlashUsedDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_FOCUS_POSITION:
                return getAutoFocusPositionDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FIRMWARE_VERSION:
                return getFirmwareVersionDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS_TYPE:
                return getLensTypeDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_SHOOTING_MODE:
                return getShootingModeDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_NEF_COMPRESSION:
                return getNEFCompressionDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_HIGH_ISO_NOISE_REDUCTION:
                return getHighISONoiseReductionDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_POWER_UP_TIME:
                return getPowerUpTimeDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getPowerUpTimeDescription()
    {
        Long value = _directory.getLongObject(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_POWER_UP_TIME);
        if (value==null)
            return null; // TODO have observed a byte[8] here which is likely some kind of date (ticks as long?)
        return new Date(value).toString();
    }

    @Nullable
    public String getHighISONoiseReductionDescription()
    {
        Integer value = _directory.getInteger(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_HIGH_ISO_NOISE_REDUCTION);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "Off";
            case 1: return "Minimal";
            case 2: return "Low";
            case 4: return "Normal";
            case 6: return "High";
            default: return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFlashUsedDescription()
    {
        Integer value = _directory.getInteger(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FLASH_USED);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "Flash Not Used";
            case 1: return "Manual Flash";
            case 3: return "Flash Not Ready";
            case 7: return "External Flash";
            case 8: return "Fired, Commander Mode";
            case 9: return "Fired, TTL Mode";
            default: return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getNEFCompressionDescription()
    {
        Integer value = _directory.getInteger(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_NEF_COMPRESSION);
        if (value==null)
            return null;
        switch (value) {
            case 1: return "Lossy (Type 1)";
            case 3: return "Uncompressed";
            case 7: return "Lossless";
            case 8: return "Lossy (Type 2)";
            default: return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getShootingModeDescription()
    {
        Integer value = _directory.getInteger(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_SHOOTING_MODE);
        if (value==null)
            return null;
        Collection<String> bits = new ArrayList<String>();

        if ((value&1)==1)
            bits.add("Continuous");
        else
            bits.add("Single Frame");

        if ((value&2)==2)
            bits.add("Delay");
        // Don't know about 3
        if ((value&8)==8)
            bits.add("PC Control");
        if ((value&16)==16)
            bits.add("Exposure Bracketing");
        if ((value&32)==32)
            bits.add("Auto ISO");
        if ((value&64)==64)
            bits.add("White-Balance Bracketing");
        if ((value&128)==128)
            bits.add("IR Control");

        return StringUtil.join(bits, ", ");
    }

    @Nullable
    public String getLensTypeDescription()
    {
        Integer value = _directory.getInteger(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS_TYPE);
        if (value==null)
            return null;

        Collection<String> bits = new ArrayList<String>();

        // TODO validate these values, as 14 is labelled as AF-C elsewhere but appears here as AF-D-G-VR

        if ((value&1)==1)
            bits.add("MF");
        else
            bits.add("AF");

        if ((value&2)==2)
            bits.add("D");

        if ((value&4)==4)
            bits.add("G");

        if ((value&8)==8)
            bits.add("VR");

        return StringUtil.join(bits, ", ");
    }

    @Nullable
    public String getColorSpaceDescription()
    {
        Integer value = _directory.getInteger(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_COLOR_SPACE);
        if (value==null)
            return null;
        switch (value) {
            case 1: return "sRGB";
            case 2: return "Adobe RGB";
            default: return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getActiveDLightingDescription()
    {
        Integer value = _directory.getInteger(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ACTIVE_D_LIGHTING);
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
        Integer value = _directory.getInteger(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_VIGNETTE_CONTROL);
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
        int[] values = _directory.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_FOCUS_POSITION);
        if (values==null)
            return null;
        if (values.length != 4 || values[0] != 0 || values[2] != 0 || values[3] != 0) {
            return "Unknown (" + _directory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_FOCUS_POSITION) + ")";
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
        Rational value = _directory.getRational(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_DIGITAL_ZOOM);
        if (value==null)
            return null;
        return value.intValue() == 1
                ? "No digital zoom"
                : value.toSimpleString(true) + "x digital zoom";
    }

    @Nullable
    public String getProgramShiftDescription()
    {
        int[] values = _directory.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_PROGRAM_SHIFT);
        return getEVDescription(values);
    }

    @Nullable
    public String getExposureDifferenceDescription()
    {
        int[] values = _directory.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_EXPOSURE_DIFFERENCE);
        return getEVDescription(values);
    }

    @NotNull
    public String getAutoFlashCompensationDescription()
    {
        int[] values = _directory.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION);
        return getEVDescription(values);
    }

    @NotNull
    public String getFlashExposureCompensationDescription()
    {
        int[] values = _directory.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FLASH_EXPOSURE_COMPENSATION);
        return getEVDescription(values);
    }

    @NotNull
    public String getFlashBracketCompensationDescription()
    {
        int[] values = _directory.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FLASH_BRACKET_COMPENSATION);
        return getEVDescription(values);
    }

    @NotNull
    public String getExposureTuningDescription()
    {
        int[] values = _directory.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_EXPOSURE_TUNING);
        return getEVDescription(values);
    }

    @NotNull
    public String getLensStopsDescription()
    {
        int[] values = _directory.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS_STOPS);
        return getEVDescription(values);
    }

    @Nullable
    private static String getEVDescription(@Nullable int[] values)
    {
        if (values==null)
            return null;
        if (values.length<3 || values[2]==0)
            return null;
        final DecimalFormat decimalFormat = new DecimalFormat("0.##");
        double ev = values[0] * values[1] / (double)values[2];
        return decimalFormat.format(ev) + " EV";
    }

    @Nullable
    public String getIsoSettingDescription()
    {
        int[] values = _directory.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_1);
        if (values==null)
            return null;
        if (values[0] != 0 || values[1] == 0)
            return "Unknown (" + _directory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_1) + ")";
        return "ISO " + values[1];
    }

    @Nullable
    public String getLensDescription()
    {
        Rational[] values = _directory.getRationalArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS);

        if (values==null)
            return null;

        if (values.length<4)
            return _directory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS);

        StringBuilder description = new StringBuilder();
        description.append(values[0].intValue());
        description.append('-');
        description.append(values[1].intValue());
        description.append("mm f/");
        description.append(values[2].floatValue());
        description.append('-');
        description.append(values[3].floatValue());

        return description.toString();
    }

    @Nullable
    public String getHueAdjustmentDescription()
    {
        final String value = _directory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_HUE_ADJUSTMENT);
        if (value==null)
            return null;
        return value + " degrees";
    }

    @Nullable
    public String getColorModeDescription()
    {
        String value = _directory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_COLOR_MODE);
        if (value==null)
            return null;
        if (value.startsWith("MODE1"))
            return "Mode I (sRGB)";
        return value;
    }

    @Nullable
    public String getFirmwareVersionDescription()
    {
        int[] values = _directory.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FIRMWARE_VERSION);
        if (values==null)
            return null;
        return ExifSubIFDDescriptor.convertBytesToVersionString(values, 2);
    }
}

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
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string representations of tag values stored in a <code>NikonType1MakernoteDirectory</code>.
 * <p/>
 * Type-1 is for E-Series cameras prior to (not including) E990.  For example: E700, E800, E900,
 * E900S, E910, E950.
 * <p/>
 * MakerNote starts from ASCII string "Nikon". Data format is the same as IFD, but it starts from
 * offset 0x08. This is the same as Olympus except start string. Example of actual data
 * structure is shown below.
 * <pre><code>
 * :0000: 4E 69 6B 6F 6E 00 01 00-05 00 02 00 02 00 06 00 Nikon...........
 * :0010: 00 00 EC 02 00 00 03 00-03 00 01 00 00 00 06 00 ................
 * </code></pre>
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class NikonType1MakernoteDescriptor extends TagDescriptor<NikonType1MakernoteDirectory>
{
    public NikonType1MakernoteDescriptor(@NotNull NikonType1MakernoteDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_QUALITY:
                return getQualityDescription();
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_COLOR_MODE:
                return getColorModeDescription();
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_IMAGE_ADJUSTMENT:
                return getImageAdjustmentDescription();
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_CCD_SENSITIVITY:
                return getCcdSensitivityDescription();
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_FOCUS:
                return getFocusDescription();
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_CONVERTER:
                return getConverterDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getConverterDescription()
    {
        Integer value = _directory.getInteger(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_CONVERTER);
        if (value == null)
            return null;
        switch (value) {
            case 0:
                return "None";
            case 1:
                return "Fisheye converter";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getDigitalZoomDescription()
    {
        Rational value = _directory.getRational(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_DIGITAL_ZOOM);
        if (value == null)
            return null;
        if (value.getNumerator() == 0) {
            return "No digital zoom";
        }
        return value.toSimpleString(true) + "x digital zoom";
    }

    @Nullable
    public String getFocusDescription()
    {
        Rational value = _directory.getRational(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_FOCUS);
        if (value == null)
            return null;
        if (value.getNumerator() == 1 && value.getDenominator() == 0) {
            return "Infinite";
        }
        return value.toSimpleString(true);
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        Integer value = _directory.getInteger(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_WHITE_BALANCE);
        if (value == null)
            return null;
        switch (value) {
            case 0:
                return "Auto";
            case 1:
                return "Preset";
            case 2:
                return "Daylight";
            case 3:
                return "Incandescence";
            case 4:
                return "Florescence";
            case 5:
                return "Cloudy";
            case 6:
                return "SpeedLight";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getCcdSensitivityDescription()
    {
        Integer value = _directory.getInteger(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_CCD_SENSITIVITY);
        if (value == null)
            return null;
        switch (value) {
            case 0:
                return "ISO80";
            case 2:
                return "ISO160";
            case 4:
                return "ISO320";
            case 5:
                return "ISO100";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getImageAdjustmentDescription()
    {
        Integer value = _directory.getInteger(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_IMAGE_ADJUSTMENT);
        if (value == null)
            return null;
        switch (value) {
            case 0:
                return "Normal";
            case 1:
                return "Bright +";
            case 2:
                return "Bright -";
            case 3:
                return "Contrast +";
            case 4:
                return "Contrast -";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getColorModeDescription()
    {
        Integer value = _directory.getInteger(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_COLOR_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "Color";
            case 2:
                return "Monochrome";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getQualityDescription()
    {
        Integer value = _directory.getInteger(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_QUALITY);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "VGA Basic";
            case 2:
                return "VGA Normal";
            case 3:
                return "VGA Fine";
            case 4:
                return "SXGA Basic";
            case 5:
                return "SXGA Normal";
            case 6:
                return "SXGA Fine";
            default:
                return "Unknown (" + value + ")";
        }
    }
}

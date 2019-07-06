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

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.makernotes.NikonType1MakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link NikonType1MakernoteDirectory}.
 * <p>
 * Type-1 is for E-Series cameras prior to (not including) E990.  For example: E700, E800, E900,
 * E900S, E910, E950.
 * <p>
 * Makernote starts from ASCII string "Nikon". Data format is the same as IFD, but it starts from
 * offset 0x08. This is the same as Olympus except start string. Example of actual data
 * structure is shown below.
 * <pre><code>
 * :0000: 4E 69 6B 6F 6E 00 01 00-05 00 02 00 02 00 06 00 Nikon...........
 * :0010: 00 00 EC 02 00 00 03 00-03 00 01 00 00 00 06 00 ................
 * </code></pre>
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class NikonType1MakernoteDescriptor extends TagDescriptor<NikonType1MakernoteDirectory>
{
    public NikonType1MakernoteDescriptor(@NotNull NikonType1MakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_QUALITY:
                return getQualityDescription();
            case TAG_COLOR_MODE:
                return getColorModeDescription();
            case TAG_IMAGE_ADJUSTMENT:
                return getImageAdjustmentDescription();
            case TAG_CCD_SENSITIVITY:
                return getCcdSensitivityDescription();
            case TAG_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case TAG_FOCUS:
                return getFocusDescription();
            case TAG_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case TAG_CONVERTER:
                return getConverterDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getConverterDescription()
    {
        return getIndexedDescription(TAG_CONVERTER, "None", "Fisheye converter");
    }

    @Nullable
    public String getDigitalZoomDescription()
    {
        Rational value = _directory.getRational(TAG_DIGITAL_ZOOM);
        return value == null
            ? null
            : value.getNumerator() == 0
                ? "No digital zoom"
                : value.toSimpleString(true) + "x digital zoom";
    }

    @Nullable
    public String getFocusDescription()
    {
        Rational value = _directory.getRational(TAG_FOCUS);
        return value == null
            ? null
            : value.getNumerator() == 1 && value.getDenominator() == 0
                ? "Infinite"
                : value.toSimpleString(true);
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        return getIndexedDescription(TAG_WHITE_BALANCE,
            "Auto",
            "Preset",
            "Daylight",
            "Incandescence",
            "Florescence",
            "Cloudy",
            "SpeedLight"
        );
    }

    @Nullable
    public String getCcdSensitivityDescription()
    {
        return getIndexedDescription(TAG_CCD_SENSITIVITY,
            "ISO80",
            null,
            "ISO160",
            null,
            "ISO320",
            "ISO100"
        );
    }

    @Nullable
    public String getImageAdjustmentDescription()
    {
        return getIndexedDescription(TAG_IMAGE_ADJUSTMENT,
            "Normal",
            "Bright +",
            "Bright -",
            "Contrast +",
            "Contrast -"
        );
    }

    @Nullable
    public String getColorModeDescription()
    {
        return getIndexedDescription(TAG_COLOR_MODE,
            1,
            "Color",
            "Monochrome"
        );
    }

    @Nullable
    public String getQualityDescription()
    {
        return getIndexedDescription(TAG_QUALITY,
            1,
            "VGA Basic",
            "VGA Normal",
            "VGA Fine",
            "SXGA Basic",
            "SXGA Normal",
            "SXGA Fine"
        );
    }
}

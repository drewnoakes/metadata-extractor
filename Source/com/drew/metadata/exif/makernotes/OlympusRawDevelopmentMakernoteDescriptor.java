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
package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.makernotes.OlympusRawDevelopmentMakernoteDirectory.*;

/**
 * Provides human-readable String representations of tag values stored in a {@link OlympusRawDevelopmentMakernoteDirectory}.
 * <p>
 * Some Description functions converted from Exiftool version 10.10 created by Phil Harvey
 * http://www.sno.phy.queensu.ca/~phil/exiftool/
 * lib\Image\ExifTool\Olympus.pm
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class OlympusRawDevelopmentMakernoteDescriptor extends TagDescriptor<OlympusRawDevelopmentMakernoteDirectory>
{
    public OlympusRawDevelopmentMakernoteDescriptor(@NotNull OlympusRawDevelopmentMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TagRawDevVersion:
                return getRawDevVersionDescription();
            case TagRawDevColorSpace:
                return getRawDevColorSpaceDescription();
            case TagRawDevEngine:
                return getRawDevEngineDescription();
            case TagRawDevNoiseReduction:
                return getRawDevNoiseReductionDescription();
            case TagRawDevEditStatus:
                return getRawDevEditStatusDescription();
            case TagRawDevSettings:
                return getRawDevSettingsDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getRawDevVersionDescription()
    {
        return getVersionBytesDescription(TagRawDevVersion, 4);
    }

    @Nullable
    public String getRawDevColorSpaceDescription()
    {
        return getIndexedDescription(TagRawDevColorSpace,
            "sRGB", "Adobe RGB", "Pro Photo RGB");
    }

    @Nullable
    public String getRawDevEngineDescription()
    {
        return getIndexedDescription(TagRawDevEngine,
            "High Speed", "High Function", "Advanced High Speed", "Advanced High Function");
    }

    @Nullable
    public String getRawDevNoiseReductionDescription()
    {
        Integer value = _directory.getInteger(TagRawDevNoiseReduction);
        if (value == null)
            return null;

        if (value == 0)
            return "(none)";

        StringBuilder sb = new StringBuilder();
        int v = value;

        if ((v        & 1) != 0) sb.append("Noise Reduction, ");
        if (((v >> 1) & 1) != 0) sb.append("Noise Filter, ");
        if (((v >> 2) & 1) != 0) sb.append("Noise Filter (ISO Boost), ");

        return sb.substring(0, sb.length() - 2);
    }

    @Nullable
    public String getRawDevEditStatusDescription()
    {
        Integer value = _directory.getInteger(TagRawDevEditStatus);
        if (value == null)
            return null;

        switch (value)
        {
            case 0:
                return "Original";
            case 1:
                return "Edited (Landscape)";
            case 6:
            case 8:
                return "Edited (Portrait)";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getRawDevSettingsDescription()
    {
        Integer value = _directory.getInteger(TagRawDevSettings);
        if (value == null)
            return null;

        if (value == 0)
            return "(none)";

        StringBuilder sb = new StringBuilder();
        int v = value;

        if ((v        & 1) != 0) sb.append("WB Color Temp, ");
        if (((v >> 1) & 1) != 0) sb.append("WB Gray Point, ");
        if (((v >> 2) & 1) != 0) sb.append("Saturation, ");
        if (((v >> 3) & 1) != 0) sb.append("Contrast, ");
        if (((v >> 4) & 1) != 0) sb.append("Sharpness, ");
        if (((v >> 5) & 1) != 0) sb.append("Color Space, ");
        if (((v >> 6) & 1) != 0) sb.append("High Function, ");
        if (((v >> 7) & 1) != 0) sb.append("Noise Reduction, ");

        return sb.substring(0, sb.length() - 2);
    }

}

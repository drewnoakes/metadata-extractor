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

import java.util.HashMap;

import static com.drew.metadata.exif.makernotes.OlympusRawDevelopment2MakernoteDirectory.*;

/**
 * Provides human-readable String representations of tag values stored in a {@link OlympusRawDevelopment2MakernoteDirectory}.
 * <p>
 * Some Description functions converted from Exiftool version 10.10 created by Phil Harvey
 * http://www.sno.phy.queensu.ca/~phil/exiftool/
 * lib\Image\ExifTool\Olympus.pm
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class OlympusRawDevelopment2MakernoteDescriptor extends TagDescriptor<OlympusRawDevelopment2MakernoteDirectory>
{
    public OlympusRawDevelopment2MakernoteDescriptor(@NotNull OlympusRawDevelopment2MakernoteDirectory directory)
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
            case TagRawDevExposureBiasValue:
                return getRawDevExposureBiasValueDescription();
            case TagRawDevColorSpace:
                return getRawDevColorSpaceDescription();
            case TagRawDevNoiseReduction:
                return getRawDevNoiseReductionDescription();
            case TagRawDevEngine:
                return getRawDevEngineDescription();
            case TagRawDevPictureMode:
                return getRawDevPictureModeDescription();
            case TagRawDevPmBwFilter:
                return getRawDevPmBwFilterDescription();
            case TagRawDevPmPictureTone:
                return getRawDevPmPictureToneDescription();
            case TagRawDevArtFilter:
                return getRawDevArtFilterDescription();
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
    public String getRawDevExposureBiasValueDescription()
    {
        return getIndexedDescription(TagRawDevExposureBiasValue,
                1, "Color Temperature", "Gray Point");
    }

    @Nullable
    public String getRawDevColorSpaceDescription()
    {
        return getIndexedDescription(TagRawDevColorSpace,
            "sRGB", "Adobe RGB", "Pro Photo RGB");
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
    public String getRawDevEngineDescription()
    {
        return getIndexedDescription(TagRawDevEngine,
            "High Speed", "High Function", "Advanced High Speed", "Advanced High Function");
    }

    @Nullable
    public String getRawDevPictureModeDescription()
    {
        Integer value = _directory.getInteger(TagRawDevPictureMode);
        if (value == null)
            return null;

        switch (value)
        {
            case 1:
                return "Vivid";
            case 2:
                return "Natural";
            case 3:
                return "Muted";
            case 256:
                return "Monotone";
            case 512:
                return "Sepia";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getRawDevPmBwFilterDescription()
    {
        return getIndexedDescription(TagRawDevPmBwFilter,
            "Neutral", "Yellow", "Orange", "Red", "Green");
    }

    @Nullable
    public String getRawDevPmPictureToneDescription()
    {
        return getIndexedDescription(TagRawDevPmPictureTone,
            "Neutral", "Sepia", "Blue", "Purple", "Green");
    }

    @Nullable
    public String getRawDevArtFilterDescription()
    {
        return getFilterDescription(TagRawDevArtFilter);
    }

    @Nullable
    public String getFilterDescription(int tag)
    {
        int[] values = _directory.getIntArray(tag);
        if (values == null || values.length == 0)
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i == 0)
                sb.append(_filters.containsKey(values[i]) ? _filters.get(values[i]) : "[unknown]");
            else
                sb.append(values[i]).append("; ");
            sb.append("; ");
        }

        return sb.substring(0, sb.length() - 2);
    }

    // RawDevArtFilter values
    private static final HashMap<Integer, String> _filters = new HashMap<Integer, String>();

    static {
        _filters.put(0, "Off");
        _filters.put(1, "Soft Focus");
        _filters.put(2, "Pop Art");
        _filters.put(3, "Pale & Light Color");
        _filters.put(4, "Light Tone");
        _filters.put(5, "Pin Hole");
        _filters.put(6, "Grainy Film");
        _filters.put(9, "Diorama");
        _filters.put(10, "Cross Process");
        _filters.put(12, "Fish Eye");
        _filters.put(13, "Drawing");
        _filters.put(14, "Gentle Sepia");
        _filters.put(15, "Pale & Light Color II");
        _filters.put(16, "Pop Art II");
        _filters.put(17, "Pin Hole II");
        _filters.put(18, "Pin Hole III");
        _filters.put(19, "Grainy Film II");
        _filters.put(20, "Dramatic Tone");
        _filters.put(21, "Punk");
        _filters.put(22, "Soft Focus 2");
        _filters.put(23, "Sparkle");
        _filters.put(24, "Watercolor");
        _filters.put(25, "Key Line");
        _filters.put(26, "Key Line II");
        _filters.put(27, "Miniature");
        _filters.put(28, "Reflection");
        _filters.put(29, "Fragmented");
        _filters.put(31, "Cross Process II");
        _filters.put(32, "Dramatic Tone II");
        _filters.put(33, "Watercolor I");
        _filters.put(34, "Watercolor II");
        _filters.put(35, "Diorama II");
        _filters.put(36, "Vintage");
        _filters.put(37, "Vintage II");
        _filters.put(38, "Vintage III");
        _filters.put(39, "Partial Color");
        _filters.put(40, "Partial Color II");
        _filters.put(41, "Partial Color III");
    }
}

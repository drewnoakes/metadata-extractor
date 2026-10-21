/*
 * Copyright 2026 Drew Noakes and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.drew.metadata.mkv;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import static com.drew.metadata.mkv.MkvVideoDirectory.*;

public class MkvVideoDescriptor extends MkvTrackDescriptor<MkvVideoDirectory>
{
    public MkvVideoDescriptor(@NotNull MkvVideoDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_FLAG_INTERLACED:
                return getIndexedDescription(tagType, "Undetermined", "Interlaced", "Progressive");
            case TAG_DISPLAY_UNIT:
                return getIndexedDescription(tagType, "Pixels", "Centimeters", "Inches",
                    "Display Aspect Ratio", "Unknown");
            case TAG_RANGE:
                return getIndexedDescription(tagType, "Unspecified", "Broadcast Range",
                    "Full Range", "Defined by Matrix Coefficients and Transfer Characteristics");
            case TAG_CHROMA_SITING_HORZ:
                return getIndexedDescription(tagType, "Unspecified", "Left", "Half");
            case TAG_CHROMA_SITING_VERT:
                return getIndexedDescription(tagType, "Unspecified", "Top", "Half");
            case TAG_MATRIX_COEFFICIENTS:
                return getIndexedDescription(tagType, "Identity", "BT.709", "Unspecified", null,
                    "FCC 73.682", "BT.470BG", "SMPTE 170M", "SMPTE 240M", "YCoCg",
                    "BT.2020 Non-constant Luminance", "BT.2020 Constant Luminance", "SMPTE 2085",
                    "Chromaticity-derived Non-constant Luminance", "Chromaticity-derived Constant Luminance", "ICtCp");
            case TAG_TRANSFER_CHARACTERISTICS:
                return getIndexedDescription(tagType, 1, "BT.709", "Unspecified", null,
                    "Gamma 2.2", "Gamma 2.8", "SMPTE 170M", "SMPTE 240M", "Linear",
                    "Logarithmic (100:1)", "Logarithmic (316:1)", "IEC 61966-2-4", "BT.1361",
                    "IEC 61966-2-1 (sRGB)", "BT.2020 10-bit", "BT.2020 12-bit",
                    "SMPTE 2084 (PQ)", "SMPTE 428-1", "ARIB STD-B67 (HLG)");
            case TAG_PRIMARIES:
                Long primaries = _directory.getLongObject(tagType);
                if (Long.valueOf(22).equals(primaries))
                    return "EBU Tech. 3213-E";
                return getIndexedDescription(tagType, 1, "BT.709", "Unspecified", null,
                    "BT.470M", "BT.470BG", "SMPTE 170M", "SMPTE 240M", "Film", "BT.2020",
                    "SMPTE 428-1", "SMPTE RP 431-2", "SMPTE EG 432-1");
            case TAG_FRAME_RATE:
                return getFormattedString(tagType, "%s fps");
            default:
                return super.getDescription(tagType);
        }
    }
}

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

package com.drew.metadata.pcx;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.pcx.PcxDirectory.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class PcxDescriptor extends TagDescriptor<PcxDirectory>
{
    public PcxDescriptor(@NotNull PcxDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_VERSION:
                return getVersionDescription();
            case TAG_COLOR_PLANES:
                return getColorPlanesDescription();
            case TAG_PALETTE_TYPE:
                return getPaletteTypeDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getVersionDescription()
    {
        // Prior to v2.5 of PC Paintbrush, the PCX image file format was considered proprietary information
        // by ZSoft Corporation

        return getIndexedDescription(TAG_VERSION,
            "2.5 with fixed EGA palette information",
            null,
            "2.8 with modifiable EGA palette information",
            "2.8 without palette information (default palette)",
            "PC Paintbrush for Windows",
            "3.0 or better");
    }

    @Nullable
    public String getColorPlanesDescription()
    {
        return getIndexedDescription(TAG_COLOR_PLANES, 3,
            "24-bit color",
            "16 colors");
    }

    @Nullable
    public String getPaletteTypeDescription()
    {
        return getIndexedDescription(TAG_PALETTE_TYPE, 1,
            "Color or B&W",
            "Grayscale");
    }
}

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

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string representations of tag values stored in a <code>OlympusMakernoteDirectory</code>.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class OlympusMakernoteDescriptor extends TagDescriptor<OlympusMakernoteDirectory>
{
    public OlympusMakernoteDescriptor(@NotNull OlympusMakernoteDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case OlympusMakernoteDirectory.TAG_OLYMPUS_SPECIAL_MODE:
                return getSpecialModeDescription();
            case OlympusMakernoteDirectory.TAG_OLYMPUS_JPEG_QUALITY:
                return getJpegQualityDescription();
            case OlympusMakernoteDirectory.TAG_OLYMPUS_MACRO_MODE:
                return getMacroModeDescription();
            case OlympusMakernoteDirectory.TAG_OLYMPUS_DIGI_ZOOM_RATIO:
                return getDigiZoomRatioDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getDigiZoomRatioDescription()
    {
        Integer value = _directory.getInteger(OlympusMakernoteDirectory.TAG_OLYMPUS_DIGI_ZOOM_RATIO);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Normal";
            case 2:
                return "Digital 2x Zoom";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getMacroModeDescription()
    {
        Integer value = _directory.getInteger(OlympusMakernoteDirectory.TAG_OLYMPUS_MACRO_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Normal (no macro)";
            case 1:
                return "Macro";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getJpegQualityDescription()
    {
        Integer value = _directory.getInteger(OlympusMakernoteDirectory.TAG_OLYMPUS_JPEG_QUALITY);
        if (value==null)
            return null;
        switch (value) {
            case 1:
                return "SQ";
            case 2:
                return "HQ";
            case 3:
                return "SHQ";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSpecialModeDescription()
    {
        int[] values = _directory.getIntArray(OlympusMakernoteDirectory.TAG_OLYMPUS_SPECIAL_MODE);
        if (values==null)
            return null;
        if (values.length < 1)
            return "";
        StringBuilder desc = new StringBuilder();
        switch (values[0]) {
            case 0:
                desc.append("Normal picture taking mode");
                break;
            case 1:
                desc.append("Unknown picture taking mode");
                break;
            case 2:
                desc.append("Fast picture taking mode");
                break;
            case 3:
                desc.append("Panorama picture taking mode");
                break;
            default:
                desc.append("Unknown picture taking mode");
                break;
        }

        if (values.length < 2)
            return desc.toString();
        desc.append(" - ");
        switch (values[1]) {
            case 0:
                desc.append("Unknown sequence number");
                break;
            case 1:
                desc.append("1st in a sequence");
                break;
            case 2:
                desc.append("2nd in a sequence");
                break;
            case 3:
                desc.append("3rd in a sequence");
                break;
            default:
                desc.append(values[1]);
                desc.append("th in a sequence");
                break;
        }
        if (values.length < 3)
            return desc.toString();
        desc.append(" - ");
        switch (values[2]) {
            case 1:
                desc.append("Left to right panorama direction");
                break;
            case 2:
                desc.append("Right to left panorama direction");
                break;
            case 3:
                desc.append("Bottom to top panorama direction");
                break;
            case 4:
                desc.append("Top to bottom panorama direction");
                break;
        }
        return desc.toString();
    }
}

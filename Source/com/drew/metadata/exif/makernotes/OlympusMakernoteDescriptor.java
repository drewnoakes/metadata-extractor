/*
 * Copyright 2002-2013 Drew Noakes
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
package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.makernotes.OlympusMakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link OlympusMakernoteDirectory}.
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
            case TAG_SPECIAL_MODE:
                return getSpecialModeDescription();
            case TAG_JPEG_QUALITY:
                return getJpegQualityDescription();
            case TAG_MACRO_MODE:
                return getMacroModeDescription();
            case TAG_BW_MODE:
                return getBWModeDescription();
            case TAG_DIGI_ZOOM_RATIO:
                return getDigiZoomRatioDescription();
            case TAG_CAMERA_ID:
                return getCameraIdDescription();
            case TAG_FLASH_MODE:
                return getFlashModeDescription();
            case TAG_FOCUS_RANGE:
                return getFocusRangeDescription();
            case TAG_FOCUS_MODE:
                return getFocusModeDescription();
            case TAG_SHARPNESS:
                return getSharpnessDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getSharpnessDescription()
    {
        return getIndexedDescription(TAG_SHARPNESS, "Normal", "Hard", "Soft");
    }

    @Nullable
    public String getFocusModeDescription()
    {
        return getIndexedDescription(TAG_FOCUS_MODE, "Auto", "Manual");
    }

    @Nullable
    public String getFocusRangeDescription()
    {
        return getIndexedDescription(TAG_FOCUS_RANGE, "Normal", "Macro");
    }

    @Nullable
    public String getFlashModeDescription()
    {
        return getIndexedDescription(TAG_FLASH_MODE, null, null, "On", "Off");
    }

    @Nullable
    public String getDigiZoomRatioDescription()
    {
        return getIndexedDescription(TAG_DIGI_ZOOM_RATIO, "Normal", null, "Digital 2x Zoom");
    }

    @Nullable
    public String getCameraIdDescription()
    {
        byte[] bytes = _directory.getByteArray(TAG_CAMERA_ID);
        if (bytes == null)
            return null;
        return new String(bytes);
    }

    @Nullable
    public String getMacroModeDescription()
    {
        return getIndexedDescription(TAG_MACRO_MODE, "Normal (no macro)", "Macro");
    }

    @Nullable
    public String getBWModeDescription()
    {
        return getIndexedDescription(TAG_BW_MODE, "Off", "On");
    }

    @Nullable
    public String getJpegQualityDescription()
    {
        return getIndexedDescription(TAG_JPEG_QUALITY, null, "SQ", "HQ", "SHQ");
    }

    @Nullable
    public String getSpecialModeDescription()
    {
        long[] values = (long[])_directory.getObject(TAG_SPECIAL_MODE);
        if (values==null)
            return null;
        if (values.length < 1)
            return "";
        StringBuilder desc = new StringBuilder();

        switch ((int)values[0]) {
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

        if (values.length >= 2) {
            switch ((int)values[1]) {
                case 0:
                    break;
                case 1:
                    desc.append(" / 1st in a sequence");
                    break;
                case 2:
                    desc.append(" / 2nd in a sequence");
                    break;
                case 3:
                    desc.append(" / 3rd in a sequence");
                    break;
                default:
                    desc.append(" / ");
                    desc.append(values[1]);
                    desc.append("th in a sequence");
                    break;
            }
        }
        if (values.length >= 3) {
            switch ((int)values[2]) {
                case 1:
                    desc.append(" / Left to right panorama direction");
                    break;
                case 2:
                    desc.append(" / Right to left panorama direction");
                    break;
                case 3:
                    desc.append(" / Bottom to top panorama direction");
                    break;
                case 4:
                    desc.append(" / Top to bottom panorama direction");
                    break;
            }
        }

        return desc.toString();
    }
}

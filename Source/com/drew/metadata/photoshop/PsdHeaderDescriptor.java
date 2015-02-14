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

package com.drew.metadata.photoshop;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PsdHeaderDescriptor extends TagDescriptor<PsdHeaderDirectory>
{
    public PsdHeaderDescriptor(@NotNull PsdHeaderDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case PsdHeaderDirectory.TAG_CHANNEL_COUNT:
                return getChannelCountDescription();
            case PsdHeaderDirectory.TAG_BITS_PER_CHANNEL:
                return getBitsPerChannelDescription();
            case PsdHeaderDirectory.TAG_COLOR_MODE:
                return getColorModeDescription();
            case PsdHeaderDirectory.TAG_IMAGE_HEIGHT:
                return getImageHeightDescription();
            case PsdHeaderDirectory.TAG_IMAGE_WIDTH:
                return getImageWidthDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getChannelCountDescription()
    {
        try {
            // Supported range is 1 to 56.
            Integer value = _directory.getInteger(PsdHeaderDirectory.TAG_CHANNEL_COUNT);
            if (value == null)
                return null;
            return value + " channel" + (value == 1 ? "" : "s");
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public String getBitsPerChannelDescription()
    {
        try {
            // Supported values are 1, 8, 16 and 32.
            Integer value = _directory.getInteger(PsdHeaderDirectory.TAG_BITS_PER_CHANNEL);
            if (value == null)
                return null;
            return value + " bit" + (value == 1 ? "" : "s") + " per channel";
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public String getColorModeDescription()
    {
        // Bitmap = 0; Grayscale = 1; Indexed = 2; RGB = 3; CMYK = 4; Multichannel = 7; Duotone = 8; Lab = 9
        try {
            Integer value = _directory.getInteger(PsdHeaderDirectory.TAG_COLOR_MODE);
            if (value == null)
                return null;
            switch (value){
                case 0: return "Bitmap";
                case 1: return "Grayscale";
                case 2: return "Indexed";
                case 3: return "RGB";
                case 4: return "CMYK";
                case 7: return "Multichannel";
                case 8: return "Duotone";
                case 9: return "Lab";
                default: return "Unknown color mode (" + value + ")";
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public String getImageHeightDescription()
    {
        try {
            Integer value = _directory.getInteger(PsdHeaderDirectory.TAG_IMAGE_HEIGHT);
            if (value == null)
                return null;
            return value + " pixel" + (value == 1 ? "" : "s");
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public String getImageWidthDescription()
    {
        try {
            Integer value = _directory.getInteger(PsdHeaderDirectory.TAG_IMAGE_WIDTH);
            if (value == null)
                return null;
            return value + " pixel" + (value == 1 ? "" : "s");
        } catch (Exception e) {
            return null;
        }
    }
}

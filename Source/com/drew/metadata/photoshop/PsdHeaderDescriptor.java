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

package com.drew.metadata.photoshop;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.photoshop.PsdHeaderDirectory.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
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
            case TAG_CHANNEL_COUNT:
                return getChannelCountDescription();
            case TAG_BITS_PER_CHANNEL:
                return getBitsPerChannelDescription();
            case TAG_COLOR_MODE:
                return getColorModeDescription();
            case TAG_IMAGE_HEIGHT:
                return getImageHeightDescription();
            case TAG_IMAGE_WIDTH:
                return getImageWidthDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getChannelCountDescription()
    {
        // Supported range is 1 to 56.
        Integer value = _directory.getInteger(TAG_CHANNEL_COUNT);
        if (value == null)
            return null;
        return value + " channel" + (value == 1 ? "" : "s");
    }

    @Nullable
    public String getBitsPerChannelDescription()
    {
        // Supported values are 1, 8, 16 and 32.
        Integer value = _directory.getInteger(TAG_BITS_PER_CHANNEL);
        if (value == null)
            return null;
        return value + " bit" + (value == 1 ? "" : "s") + " per channel";
    }

    @Nullable
    public String getColorModeDescription()
    {
        return getIndexedDescription(TAG_COLOR_MODE,
            "Bitmap",
            "Grayscale",
            "Indexed",
            "RGB",
            "CMYK",
            null,
            null,
            "Multichannel",
            "Duotone",
            "Lab");
    }

    @Nullable
    public String getImageHeightDescription()
    {
        Integer value = _directory.getInteger(TAG_IMAGE_HEIGHT);
        if (value == null)
            return null;
        return value + " pixel" + (value == 1 ? "" : "s");
    }

    @Nullable
    public String getImageWidthDescription()
    {
        try {
            Integer value = _directory.getInteger(TAG_IMAGE_WIDTH);
            if (value == null)
                return null;
            return value + " pixel" + (value == 1 ? "" : "s");
        } catch (Exception e) {
            return null;
        }
    }
}

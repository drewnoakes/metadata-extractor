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
package com.drew.metadata.mp4.media;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.mp4.media.Mp4VideoDirectory.*;

public class Mp4VideoDescriptor extends TagDescriptor<Mp4VideoDirectory>
{
    public Mp4VideoDescriptor(@NotNull Mp4VideoDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_HEIGHT:
            case TAG_WIDTH:
                return getPixelDescription(tagType);
            case TAG_DEPTH:
                return getDepthDescription();
            case TAG_COLOR_TABLE:
                return getColorTableDescription();
            case TAG_GRAPHICS_MODE:
                return getGraphicsModeDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    private String getPixelDescription(int tagType)
    {
        String value = _directory.getString(tagType);
        return value == null ? null : value + " pixels";
    }

    private String getDepthDescription()
    {
        Integer value = _directory.getInteger(TAG_DEPTH);
        if (value == null)
            return null;

        switch (value) {
            case (40):
            case (36):
            case (34):
                return (value - 32) + "-bit grayscale";
            default:
                return "Unknown (" + value + ")";
        }
    }

    private String getColorTableDescription()
    {
        Integer value = _directory.getInteger(TAG_COLOR_TABLE);
        if (value == null)
            return null;

        switch (value) {
            case (-1):
                Integer depth = _directory.getInteger(TAG_DEPTH);
                if (depth == null)
                    return "None";

                if (depth < 16) {
                    return "Default";
                } else {
                    return "None";
                }
            case (0):
                return "Color table within file";
            default:
                return "Unknown (" + value + ")";
        }
    }

    private String getGraphicsModeDescription()
    {
        Integer value = _directory.getInteger(TAG_GRAPHICS_MODE);
        if (value == null)
            return null;

        switch (value) {
            case (0x00):
                return "Copy";
            case (0x40):
                return "Dither copy";
            case (0x20):
                return "Blend";
            case (0x24):
                return "Transparent";
            case (0x100):
                return "Straight alpha";
            case (0x101):
                return "Premul white alpha";
            case (0x102):
                return "Premul black alpha";
            case (0x104):
                return "Straight alpha blend";
            case (0x103):
                return "Composition (dither copy)";
            default:
                return "Unknown (" + value + ")";
        }
    }
}

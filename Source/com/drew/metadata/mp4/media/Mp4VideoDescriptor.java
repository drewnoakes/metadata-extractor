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
            case (Mp4VideoDirectory.TAG_HEIGHT):
            case (Mp4VideoDirectory.TAG_WIDTH):
                return getPixelDescription(tagType);
            case (Mp4VideoDirectory.TAG_DEPTH):
                return getDepthDescription(tagType);
            case (Mp4VideoDirectory.TAG_COLOR_TABLE):
                return getColorTableDescription(tagType);
            default:
                return super.getDescription(tagType);
        }
    }

    private String getPixelDescription(int tagType)
    {
        return _directory.getString(tagType) + " pixels";
    }

    private String getDepthDescription(int tagType)
    {
        int depth = _directory.getInteger(tagType);
        switch (depth) {
            case (40):
            case (36):
            case (34):
                return (depth - 32) + "-bit grayscale";
            default:
                return Integer.toString(depth);
        }
    }

    private String getColorTableDescription(int tagType)
    {
        int colorTableId = _directory.getInteger(tagType);

        switch (colorTableId) {
            case (-1):
                if (_directory.getInteger(Mp4VideoDirectory.TAG_DEPTH) < 16) {
                    return "Default";
                } else {
                    return "None";
                }
            case (0):
                return "Color table within file";
            default:
                return Integer.toString(colorTableId);
        }
    }
}

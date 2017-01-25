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

package com.drew.metadata.ico;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.ico.IcoDirectory.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class IcoDescriptor extends TagDescriptor<IcoDirectory>
{
    public IcoDescriptor(@NotNull IcoDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_IMAGE_TYPE:
                return getImageTypeDescription();
            case TAG_IMAGE_WIDTH:
                return getImageWidthDescription();
            case TAG_IMAGE_HEIGHT:
                return getImageHeightDescription();
            case TAG_COLOUR_PALETTE_SIZE:
                return getColourPaletteSizeDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getImageTypeDescription()
    {
        return getIndexedDescription(TAG_IMAGE_TYPE, 1, "Icon", "Cursor");
    }

    @Nullable
    public String getImageWidthDescription()
    {
        Integer width = _directory.getInteger(TAG_IMAGE_WIDTH);
        if (width == null)
            return null;
        return (width == 0 ? 256 : width) + " pixels";
    }

    @Nullable
    public String getImageHeightDescription()
    {
        Integer width = _directory.getInteger(TAG_IMAGE_HEIGHT);
        if (width == null)
            return null;
        return (width == 0 ? 256 : width) + " pixels";
    }

    @Nullable
    public String getColourPaletteSizeDescription()
    {
        Integer size = _directory.getInteger(TAG_COLOUR_PALETTE_SIZE);
        if (size == null)
            return null;
        return size == 0 ? "No palette" : size + " colour" + (size == 1 ? "" : "s");
    }
}

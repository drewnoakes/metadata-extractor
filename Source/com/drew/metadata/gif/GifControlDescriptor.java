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
package com.drew.metadata.gif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.gif.GifControlDirectory.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 * @author Kevin Mott https://github.com/kwhopper
 */
@SuppressWarnings("WeakerAccess")
public class GifControlDescriptor extends TagDescriptor<GifControlDirectory>
{
    public GifControlDescriptor(@NotNull GifControlDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_DISPOSAL_METHOD:
                return getDisposalMethodDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getDisposalMethodDescription()
    {
        Integer value = _directory.getInteger(TAG_DISPOSAL_METHOD);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Not Specified";
            case 1: return "Don't Dispose";
            case 2: return "Restore to Background Color";
            case 3: return "Restore to Previous";
            case 4:
            case 5:
            case 6:
            case 7: return "To Be Defined";
            default: return "Invalid value (" + value + ")";
        }
    }
}

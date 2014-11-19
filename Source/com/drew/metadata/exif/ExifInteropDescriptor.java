/*
 * Copyright 2002-2014 Drew Noakes
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
package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.ExifInteropDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link ExifInteropDirectory}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ExifInteropDescriptor extends TagDescriptor<ExifInteropDirectory>
{
    public ExifInteropDescriptor(@NotNull ExifInteropDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_INTEROP_INDEX:
                return getInteropIndexDescription();
            case TAG_INTEROP_VERSION:
                return getInteropVersionDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getInteropVersionDescription()
    {
        return getVersionBytesDescription(TAG_INTEROP_VERSION, 2);
    }

    @Nullable
    public String getInteropIndexDescription()
    {
        String value = _directory.getString(TAG_INTEROP_INDEX);

        if (value == null)
            return null;

        return "R98".equalsIgnoreCase(value.trim())
                ? "Recommended Exif Interoperability Rules (ExifR98)"
                : "Unknown (" + value + ")";
    }
}

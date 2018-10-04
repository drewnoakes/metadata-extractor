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
package com.drew.metadata.netpbm;

import com.drew.lang.annotations.Nullable;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.netpbm.NetpbmHeaderDirectory.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 * @author Kevin Mott https://github.com/kwhopper
 */
@SuppressWarnings("WeakerAccess")
public class NetpbmHeaderDescriptor extends TagDescriptor<NetpbmHeaderDirectory>
{
    public NetpbmHeaderDescriptor(@NotNull NetpbmHeaderDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_FORMAT_TYPE:
                return getFormatTypeDescription();
            default:
                return super.getDescription(tagType);
        }
    }
    
    @Nullable
    private String getFormatTypeDescription()
    {
        return getIndexedDescription(TAG_FORMAT_TYPE, 1,
            "Portable BitMap (ASCII, B&W)",
            "Portable GrayMap (ASCII, B&W)",
            "Portable PixMap (ASCII, B&W)",
            "Portable BitMap (RAW, B&W)",
            "Portable GrayMap (RAW, B&W)",
            "Portable PixMap (RAW, B&W)",
            "Portable Arbitrary Map"
        );
    }
}

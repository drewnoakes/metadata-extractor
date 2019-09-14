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
package com.drew.metadata.heif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

public class HeifDirectory extends Directory
{
    public static final int TAG_MAJOR_BRAND                             = 1;
    public static final int TAG_MINOR_VERSION                           = 2;
    public static final int TAG_COMPATIBLE_BRANDS                       = 3;

    public static final int TAG_IMAGE_WIDTH                             = 4;
    public static final int TAG_IMAGE_HEIGHT                            = 5;
    public static final int TAG_IMAGE_ROTATION                          = 6;
    public static final int TAG_BITS_PER_CHANNEL                        = 7;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_MAJOR_BRAND, "Major Brand");
        _tagNameMap.put(TAG_MINOR_VERSION, "Minor Version");
        _tagNameMap.put(TAG_COMPATIBLE_BRANDS, "Compatible Brands");
        _tagNameMap.put(TAG_IMAGE_WIDTH, "Width");
        _tagNameMap.put(TAG_IMAGE_HEIGHT, "Height");
        _tagNameMap.put(TAG_IMAGE_ROTATION, "Rotation");
        _tagNameMap.put(TAG_BITS_PER_CHANNEL, "Bits Per Channel");
    }

    public HeifDirectory()
    {
        this.setDescriptor(new HeifDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "HEIF";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

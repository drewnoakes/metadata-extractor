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
package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes tags specific to Apple cameras.
 * <p>
 * Using information from http://owl.phy.queensu.ca/~phil/exiftool/TagNames/Apple.html
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class AppleMakernoteDirectory extends Directory
{
    public static final int TAG_RUN_TIME = 0x0003;
    public static final int TAG_HDR_IMAGE_TYPE = 0x000a;
    public static final int TAG_BURST_UUID = 0x000b;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_RUN_TIME, "Run Time");
        _tagNameMap.put(TAG_HDR_IMAGE_TYPE, "HDR Image Type");
        _tagNameMap.put(TAG_BURST_UUID, "Burst UUID");
    }

    public AppleMakernoteDirectory()
    {
        this.setDescriptor(new AppleMakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Apple Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

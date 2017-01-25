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

package com.drew.metadata.photoshop;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

import java.util.HashMap;

/**
 * Holds the data found in Photoshop "ducky" segments, created during Save-for-Web.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class DuckyDirectory extends Directory
{
    public static final int TAG_QUALITY = 1;
    public static final int TAG_COMMENT = 2;
    public static final int TAG_COPYRIGHT = 3;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_QUALITY, "Quality");
        _tagNameMap.put(TAG_COMMENT, "Comment");
        _tagNameMap.put(TAG_COPYRIGHT, "Copyright");
    }

    public DuckyDirectory()
    {
        this.setDescriptor(new TagDescriptor<DuckyDirectory>(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Ducky";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

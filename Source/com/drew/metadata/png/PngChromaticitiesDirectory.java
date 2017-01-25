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
package com.drew.metadata.png;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

import java.util.HashMap;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class PngChromaticitiesDirectory extends Directory
{
    public static final int TAG_WHITE_POINT_X = 1;
    public static final int TAG_WHITE_POINT_Y = 2;
    public static final int TAG_RED_X = 3;
    public static final int TAG_RED_Y = 4;
    public static final int TAG_GREEN_X = 5;
    public static final int TAG_GREEN_Y = 6;
    public static final int TAG_BLUE_X = 7;
    public static final int TAG_BLUE_Y = 8;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_WHITE_POINT_X, "White Point X");
        _tagNameMap.put(TAG_WHITE_POINT_Y, "White Point Y");
        _tagNameMap.put(TAG_RED_X, "Red X");
        _tagNameMap.put(TAG_RED_Y, "Red Y");
        _tagNameMap.put(TAG_GREEN_X, "Green X");
        _tagNameMap.put(TAG_GREEN_Y, "Green Y");
        _tagNameMap.put(TAG_BLUE_X, "Blue X");
        _tagNameMap.put(TAG_BLUE_Y, "Blue Y");
    }

    public PngChromaticitiesDirectory()
    {
        this.setDescriptor(new TagDescriptor<PngChromaticitiesDirectory>(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "PNG Chromaticities";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

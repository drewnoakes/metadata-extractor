/*
 * Copyright 2002-2016 Drew Noakes
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
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Drew Noakes https://drewnoakes.com
 * @author Kevin Mott https://github.com/kwhopper
 */
@SuppressWarnings("WeakerAccess")
public class GifImageDirectory extends Directory
{
    public static final int TagLeft = 1;
    public static final int TagTop = 2;
    public static final int TagWidth = 3;
    public static final int TagHeight = 4;
    public static final int TagHasLocalColourTable = 5;
    public static final int TagIsInterlaced = 6;
    public static final int TagIsColorTableSorted = 7;
    public static final int TagLocalColourTableBitsPerPixel = 8;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TagLeft, "Left");
        _tagNameMap.put(TagTop, "Top");
        _tagNameMap.put(TagWidth, "Width");
        _tagNameMap.put(TagHeight, "Height");
        _tagNameMap.put(TagHasLocalColourTable, "Has Local Colour Table");
        _tagNameMap.put(TagIsInterlaced, "Is Interlaced");
        _tagNameMap.put(TagIsColorTableSorted, "Is Local Colour Table Sorted");
        _tagNameMap.put(TagLocalColourTableBitsPerPixel, "Local Colour Table Bits Per Pixel");
    }

    public GifImageDirectory()
    {
        this.setDescriptor(new GifImageDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "GIF Image";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

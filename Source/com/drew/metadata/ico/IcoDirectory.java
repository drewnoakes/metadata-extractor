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
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class IcoDirectory extends Directory
{
    public static final int TAG_IMAGE_TYPE = 1;

    public static final int TAG_IMAGE_WIDTH = 2;
    public static final int TAG_IMAGE_HEIGHT = 3;
    public static final int TAG_COLOUR_PALETTE_SIZE = 4;
    public static final int TAG_COLOUR_PLANES = 5;
    public static final int TAG_CURSOR_HOTSPOT_X = 6;
    public static final int TAG_BITS_PER_PIXEL = 7;
    public static final int TAG_CURSOR_HOTSPOT_Y = 8;
    public static final int TAG_IMAGE_SIZE_BYTES = 9;
    public static final int TAG_IMAGE_OFFSET_BYTES = 10;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_IMAGE_TYPE, "Image Type");
        _tagNameMap.put(TAG_IMAGE_WIDTH, "Image Width");
        _tagNameMap.put(TAG_IMAGE_HEIGHT, "Image Height");
        _tagNameMap.put(TAG_COLOUR_PALETTE_SIZE, "Colour Palette Size");
        _tagNameMap.put(TAG_COLOUR_PLANES, "Colour Planes");
        _tagNameMap.put(TAG_CURSOR_HOTSPOT_X, "Hotspot X");
        _tagNameMap.put(TAG_BITS_PER_PIXEL, "Bits Per Pixel");
        _tagNameMap.put(TAG_CURSOR_HOTSPOT_Y, "Hotspot Y");
        _tagNameMap.put(TAG_IMAGE_SIZE_BYTES, "Image Size Bytes");
        _tagNameMap.put(TAG_IMAGE_OFFSET_BYTES, "Image Offset Bytes");
    }

    public IcoDirectory()
    {
        this.setDescriptor(new IcoDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "ICO";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

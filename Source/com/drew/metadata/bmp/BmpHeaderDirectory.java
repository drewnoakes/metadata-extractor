/*
 * Copyright 2002-2015 Drew Noakes
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
package com.drew.metadata.bmp;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class BmpHeaderDirectory extends Directory
{
    public static final int TAG_HEADER_SIZE = -1;

    public static final int TAG_IMAGE_HEIGHT = 1;
    public static final int TAG_IMAGE_WIDTH = 2;
    public static final int TAG_COLOUR_PLANES = 3;
    public static final int TAG_BITS_PER_PIXEL = 4;
    public static final int TAG_COMPRESSION = 5;
    public static final int TAG_X_PIXELS_PER_METER = 6;
    public static final int TAG_Y_PIXELS_PER_METER = 7;
    public static final int TAG_PALETTE_COLOUR_COUNT = 8;
    public static final int TAG_IMPORTANT_COLOUR_COUNT = 9;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_HEADER_SIZE, "Header Size");

        _tagNameMap.put(TAG_IMAGE_HEIGHT, "Image Height");
        _tagNameMap.put(TAG_IMAGE_WIDTH, "Image Width");
        _tagNameMap.put(TAG_COLOUR_PLANES, "Planes");
        _tagNameMap.put(TAG_BITS_PER_PIXEL, "Bits Per Pixel");
        _tagNameMap.put(TAG_COMPRESSION, "Compression");
        _tagNameMap.put(TAG_X_PIXELS_PER_METER, "X Pixels per Meter");
        _tagNameMap.put(TAG_Y_PIXELS_PER_METER, "Y Pixels per Meter");
        _tagNameMap.put(TAG_PALETTE_COLOUR_COUNT, "Palette Colour Count");
        _tagNameMap.put(TAG_IMPORTANT_COLOUR_COUNT, "Important Colour Count");
    }

    public BmpHeaderDirectory()
    {
        this.setDescriptor(new BmpHeaderDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "BMP Header";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

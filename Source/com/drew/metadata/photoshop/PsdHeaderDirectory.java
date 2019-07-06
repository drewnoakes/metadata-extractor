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

package com.drew.metadata.photoshop;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Holds the basic metadata found in the header of a Photoshop PSD file.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class PsdHeaderDirectory extends Directory
{
    /**
     * The number of channels in the image, including any alpha channels. Supported range is 1 to 56.
     */
    public static final int TAG_CHANNEL_COUNT = 1;
    /**
     * The height of the image in pixels.
     */
    public static final int TAG_IMAGE_HEIGHT = 2;
    /**
     * The width of the image in pixels.
     */
    public static final int TAG_IMAGE_WIDTH = 3;
    /**
     * The number of bits per channel. Supported values are 1, 8, 16 and 32.
     */
    public static final int TAG_BITS_PER_CHANNEL = 4;
    /**
     * The color mode of the file. Supported values are:
     * Bitmap = 0; Grayscale = 1; Indexed = 2; RGB = 3; CMYK = 4; Multichannel = 7; Duotone = 8; Lab = 9.
     */
    public static final int TAG_COLOR_MODE = 5;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_CHANNEL_COUNT, "Channel Count");
        _tagNameMap.put(TAG_IMAGE_HEIGHT, "Image Height");
        _tagNameMap.put(TAG_IMAGE_WIDTH, "Image Width");
        _tagNameMap.put(TAG_BITS_PER_CHANNEL, "Bits Per Channel");
        _tagNameMap.put(TAG_COLOR_MODE, "Color Mode");
    }

    public PsdHeaderDirectory()
    {
        this.setDescriptor(new PsdHeaderDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "PSD Header";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

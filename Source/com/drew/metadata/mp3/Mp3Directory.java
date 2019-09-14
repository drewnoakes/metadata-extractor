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
package com.drew.metadata.mp3;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class Mp3Directory extends Directory
{

    public static final int TAG_ID = 1;
    public static final int TAG_LAYER = 2;
    public static final int TAG_BITRATE = 3;
    public static final int TAG_FREQUENCY = 4;
    public static final int TAG_MODE = 5;
    public static final int TAG_EMPHASIS = 6;
    public static final int TAG_COPYRIGHT = 7;
    public static final int TAG_FRAME_SIZE = 8;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_ID, "ID");
        _tagNameMap.put(TAG_LAYER, "Layer");
        _tagNameMap.put(TAG_BITRATE, "Bitrate");
        _tagNameMap.put(TAG_FREQUENCY, "Frequency");
        _tagNameMap.put(TAG_MODE, "Mode");
        _tagNameMap.put(TAG_EMPHASIS, "Emphasis Method");
        _tagNameMap.put(TAG_COPYRIGHT, "Copyright");
        _tagNameMap.put(TAG_FRAME_SIZE, "Frame Size");
    }

    public Mp3Directory()
    {
        this.setDescriptor(new Mp3Descriptor(this));
    }

    @Override
    public String getName()
    {
        return "MP3";
    }

    @Override
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

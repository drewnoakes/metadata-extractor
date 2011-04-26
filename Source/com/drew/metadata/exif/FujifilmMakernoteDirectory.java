/*
 * Copyright 2002-2011 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes tags specific to Fujifilm cameras.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class FujifilmMakernoteDirectory extends Directory
{
    public static final int TAG_FUJIFILM_MAKERNOTE_VERSION = 0x0000;
    public static final int TAG_FUJIFILM_QUALITY = 0x1000;
    public static final int TAG_FUJIFILM_SHARPNESS = 0x1001;
    public static final int TAG_FUJIFILM_WHITE_BALANCE = 0x1002;
    public static final int TAG_FUJIFILM_COLOR = 0x1003;
    public static final int TAG_FUJIFILM_TONE = 0x1004;
    public static final int TAG_FUJIFILM_FLASH_MODE = 0x1010;
    public static final int TAG_FUJIFILM_FLASH_STRENGTH = 0x1011;
    public static final int TAG_FUJIFILM_MACRO = 0x1020;
    public static final int TAG_FUJIFILM_FOCUS_MODE = 0x1021;
    public static final int TAG_FUJIFILM_SLOW_SYNCHRO = 0x1030;
    public static final int TAG_FUJIFILM_PICTURE_MODE = 0x1031;
    public static final int TAG_FUJIFILM_UNKNOWN_1 = 0x1032;
    public static final int TAG_FUJIFILM_CONTINUOUS_TAKING_OR_AUTO_BRACKETTING = 0x1100;
    public static final int TAG_FUJIFILM_UNKNOWN_2 = 0x1200;
    public static final int TAG_FUJIFILM_BLUR_WARNING = 0x1300;
    public static final int TAG_FUJIFILM_FOCUS_WARNING = 0x1301;
    public static final int TAG_FUJIFILM_AE_WARNING = 0x1302;

    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_FUJIFILM_AE_WARNING, "AE Warning");
        _tagNameMap.put(TAG_FUJIFILM_BLUR_WARNING, "Blur Warning");
        _tagNameMap.put(TAG_FUJIFILM_COLOR, "Color");
        _tagNameMap.put(TAG_FUJIFILM_CONTINUOUS_TAKING_OR_AUTO_BRACKETTING, "Continuous Taking Or Auto Bracketting");
        _tagNameMap.put(TAG_FUJIFILM_FLASH_MODE, "Flash Mode");
        _tagNameMap.put(TAG_FUJIFILM_FLASH_STRENGTH, "Flash Strength");
        _tagNameMap.put(TAG_FUJIFILM_FOCUS_MODE, "Focus Mode");
        _tagNameMap.put(TAG_FUJIFILM_FOCUS_WARNING, "Focus Warning");
        _tagNameMap.put(TAG_FUJIFILM_MACRO, "Macro");
        _tagNameMap.put(TAG_FUJIFILM_MAKERNOTE_VERSION, "Makernote Version");
        _tagNameMap.put(TAG_FUJIFILM_PICTURE_MODE, "Picture Mode");
        _tagNameMap.put(TAG_FUJIFILM_QUALITY, "Quality");
        _tagNameMap.put(TAG_FUJIFILM_SHARPNESS, "Sharpness");
        _tagNameMap.put(TAG_FUJIFILM_SLOW_SYNCHRO, "Slow Synchro");
        _tagNameMap.put(TAG_FUJIFILM_TONE, "Tone");
        _tagNameMap.put(TAG_FUJIFILM_UNKNOWN_1, "Makernote Unknown 1");
        _tagNameMap.put(TAG_FUJIFILM_UNKNOWN_2, "Makernote Unknown 2");
        _tagNameMap.put(TAG_FUJIFILM_WHITE_BALANCE, "White Balance");
    }

    public FujifilmMakernoteDirectory()
    {
        this.setDescriptor(new FujifilmMakernoteDescriptor(this));
    }

    public String getName()
    {
        return "FujiFilm Makernote";
    }

    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

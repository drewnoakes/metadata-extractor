/*
 * Copyright 2002-2012 Drew Noakes
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

import com.drew.lang.annotations.NotNull;
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
    public static final int TAG_FUJIFILM_QUALITY = 0x1000; // 4096
    public static final int TAG_FUJIFILM_SHARPNESS = 0x1001; // 4097
    public static final int TAG_FUJIFILM_WHITE_BALANCE = 0x1002; // 4098
    public static final int TAG_FUJIFILM_COLOR_SATURATION = 0x1003; // 4099
    public static final int TAG_FUJIFILM_TONE = 0x1004; // 4100
    public static final int TAG_FUJIFILM_FLASH_MODE = 0x1010; // 4112
    public static final int TAG_FUJIFILM_FLASH_STRENGTH = 0x1011; // 4113
    public static final int TAG_FUJIFILM_MACRO = 0x1020; // 4128
    public static final int TAG_FUJIFILM_FOCUS_MODE = 0x1021; // 4129
    public static final int TAG_FUJIFILM_SLOW_SYNCH = 0x1030; // 4144
    public static final int TAG_FUJIFILM_PICTURE_MODE = 0x1031; // 4145
    public static final int TAG_FUJIFILM_UNKNOWN_1 = 0x1032; // 4146
    public static final int TAG_FUJIFILM_CONTINUOUS_TAKING_OR_AUTO_BRACKETTING = 0x1100; // 4352
    public static final int TAG_FUJIFILM_UNKNOWN_2 = 0x1200; // 4608
    public static final int TAG_FUJIFILM_BLUR_WARNING = 0x1300; // 4864
    public static final int TAG_FUJIFILM_FOCUS_WARNING = 0x1301; // 4865
    public static final int TAG_FUJIFILM_AE_WARNING = 0x1302; // 4866

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_FUJIFILM_MAKERNOTE_VERSION, "Makernote Version");
        _tagNameMap.put(TAG_FUJIFILM_QUALITY, "Quality");
        _tagNameMap.put(TAG_FUJIFILM_SHARPNESS, "Sharpness");
        _tagNameMap.put(TAG_FUJIFILM_WHITE_BALANCE, "White Balance");
        _tagNameMap.put(TAG_FUJIFILM_COLOR_SATURATION, "Color Saturation");
        _tagNameMap.put(TAG_FUJIFILM_TONE, "Tone (Contrast)");
        _tagNameMap.put(TAG_FUJIFILM_FLASH_MODE, "Flash Mode");
        _tagNameMap.put(TAG_FUJIFILM_FLASH_STRENGTH, "Flash Strength");
        _tagNameMap.put(TAG_FUJIFILM_MACRO, "Macro");
        _tagNameMap.put(TAG_FUJIFILM_FOCUS_MODE, "Focus Mode");
        _tagNameMap.put(TAG_FUJIFILM_SLOW_SYNCH, "Slow Synch");
        _tagNameMap.put(TAG_FUJIFILM_PICTURE_MODE, "Picture Mode");
        _tagNameMap.put(TAG_FUJIFILM_UNKNOWN_1, "Makernote Unknown 1");
        _tagNameMap.put(TAG_FUJIFILM_CONTINUOUS_TAKING_OR_AUTO_BRACKETTING, "Continuous Taking Or Auto Bracketting");
        _tagNameMap.put(TAG_FUJIFILM_UNKNOWN_2, "Makernote Unknown 2");
        _tagNameMap.put(TAG_FUJIFILM_BLUR_WARNING, "Blur Warning");
        _tagNameMap.put(TAG_FUJIFILM_FOCUS_WARNING, "Focus Warning");
        _tagNameMap.put(TAG_FUJIFILM_AE_WARNING, "AE Warning");
    }

    public FujifilmMakernoteDirectory()
    {
        this.setDescriptor(new FujifilmMakernoteDescriptor(this));
    }

    @NotNull
    public String getName()
    {
        return "FujiFilm Makernote";
    }

    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

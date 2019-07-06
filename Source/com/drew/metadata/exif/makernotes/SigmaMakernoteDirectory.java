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

package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes tags specific to Sigma / Foveon cameras.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class SigmaMakernoteDirectory extends Directory
{
    public static final int TAG_SERIAL_NUMBER = 0x2;
    public static final int TAG_DRIVE_MODE = 0x3;
    public static final int TAG_RESOLUTION_MODE = 0x4;
    public static final int TAG_AUTO_FOCUS_MODE = 0x5;
    public static final int TAG_FOCUS_SETTING = 0x6;
    public static final int TAG_WHITE_BALANCE = 0x7;
    public static final int TAG_EXPOSURE_MODE = 0x8;
    public static final int TAG_METERING_MODE = 0x9;
    public static final int TAG_LENS_RANGE = 0xa;
    public static final int TAG_COLOR_SPACE = 0xb;
    public static final int TAG_EXPOSURE = 0xc;
    public static final int TAG_CONTRAST = 0xd;
    public static final int TAG_SHADOW = 0xe;
    public static final int TAG_HIGHLIGHT = 0xf;
    public static final int TAG_SATURATION = 0x10;
    public static final int TAG_SHARPNESS = 0x11;
    public static final int TAG_FILL_LIGHT = 0x12;
    public static final int TAG_COLOR_ADJUSTMENT = 0x14;
    public static final int TAG_ADJUSTMENT_MODE = 0x15;
    public static final int TAG_QUALITY = 0x16;
    public static final int TAG_FIRMWARE = 0x17;
    public static final int TAG_SOFTWARE = 0x18;
    public static final int TAG_AUTO_BRACKET = 0x19;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_SERIAL_NUMBER, "Serial Number");
        _tagNameMap.put(TAG_DRIVE_MODE, "Drive Mode");
        _tagNameMap.put(TAG_RESOLUTION_MODE, "Resolution Mode");
        _tagNameMap.put(TAG_AUTO_FOCUS_MODE, "Auto Focus Mode");
        _tagNameMap.put(TAG_FOCUS_SETTING, "Focus Setting");
        _tagNameMap.put(TAG_WHITE_BALANCE, "White Balance");
        _tagNameMap.put(TAG_EXPOSURE_MODE, "Exposure Mode");
        _tagNameMap.put(TAG_METERING_MODE, "Metering Mode");
        _tagNameMap.put(TAG_LENS_RANGE, "Lens Range");
        _tagNameMap.put(TAG_COLOR_SPACE, "Color Space");
        _tagNameMap.put(TAG_EXPOSURE, "Exposure");
        _tagNameMap.put(TAG_CONTRAST, "Contrast");
        _tagNameMap.put(TAG_SHADOW, "Shadow");
        _tagNameMap.put(TAG_HIGHLIGHT, "Highlight");
        _tagNameMap.put(TAG_SATURATION, "Saturation");
        _tagNameMap.put(TAG_SHARPNESS, "Sharpness");
        _tagNameMap.put(TAG_FILL_LIGHT, "Fill Light");
        _tagNameMap.put(TAG_COLOR_ADJUSTMENT, "Color Adjustment");
        _tagNameMap.put(TAG_ADJUSTMENT_MODE, "Adjustment Mode");
        _tagNameMap.put(TAG_QUALITY, "Quality");
        _tagNameMap.put(TAG_FIRMWARE, "Firmware");
        _tagNameMap.put(TAG_SOFTWARE, "Software");
        _tagNameMap.put(TAG_AUTO_BRACKET, "Auto Bracket");
    }


    public SigmaMakernoteDirectory()
    {
        this.setDescriptor(new SigmaMakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Sigma Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

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
package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes tags specific to Pentax and Asahi cameras.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class PentaxMakernoteDirectory extends Directory
{
    /**
     * 0 = Auto
     * 1 = Night-scene
     * 2 = Manual
     * 4 = Multiple
     */
    public static final int TAG_CAPTURE_MODE = 0x0001;

    /**
     * 0 = Good
     * 1 = Better
     * 2 = Best
     */
    public static final int TAG_QUALITY_LEVEL = 0x0002;

    /**
     * 2 = Custom
     * 3 = Auto
     */
    public static final int TAG_FOCUS_MODE = 0x0003;

    /**
     * 1 = Auto
     * 2 = Flash on
     * 4 = Flash off
     * 6 = Red-eye Reduction
     */
    public static final int TAG_FLASH_MODE = 0x0004;

    /**
     * 0 = Auto
     * 1 = Daylight
     * 2 = Shade
     * 3 = Tungsten
     * 4 = Fluorescent
     * 5 = Manual
     */
    public static final int TAG_WHITE_BALANCE = 0x0007;

    /**
     * (0 = Off)
     */
    public static final int TAG_DIGITAL_ZOOM = 0x000A;

    /**
     * 0 = Normal
     * 1 = Soft
     * 2 = Hard
     */
    public static final int TAG_SHARPNESS = 0x000B;

    /**
     * 0 = Normal
     * 1 = Low
     * 2 = High
     */
    public static final int TAG_CONTRAST = 0x000C;

    /**
     * 0 = Normal
     * 1 = Low
     * 2 = High
     */
    public static final int TAG_SATURATION = 0x000D;

    /**
     * 10 = ISO 100
     * 16 = ISO 200
     * 100 = ISO 100
     * 200 = ISO 200
     */
    public static final int TAG_ISO_SPEED = 0x0014;

    /**
     * 1 = Normal
     * 2 = Black &amp; White
     * 3 = Sepia
     */
    public static final int TAG_COLOUR = 0x0017;

    /**
     * See Print Image Matching for specification.
     * http://www.ozhiker.com/electronics/pjmt/jpeg_info/pim.html
     */
    public static final int TAG_PRINT_IMAGE_MATCHING_INFO = 0x0E00;

    /**
     * (String).
     */
    public static final int TAG_TIME_ZONE = 0x1000;

    /**
     * (String).
     */
    public static final int TAG_DAYLIGHT_SAVINGS = 0x1001;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_CAPTURE_MODE, "Capture Mode");
        _tagNameMap.put(TAG_QUALITY_LEVEL, "Quality Level");
        _tagNameMap.put(TAG_FOCUS_MODE, "Focus Mode");
        _tagNameMap.put(TAG_FLASH_MODE, "Flash Mode");
        _tagNameMap.put(TAG_WHITE_BALANCE, "White Balance");
        _tagNameMap.put(TAG_DIGITAL_ZOOM, "Digital Zoom");
        _tagNameMap.put(TAG_SHARPNESS, "Sharpness");
        _tagNameMap.put(TAG_CONTRAST, "Contrast");
        _tagNameMap.put(TAG_SATURATION, "Saturation");
        _tagNameMap.put(TAG_ISO_SPEED, "ISO Speed");
        _tagNameMap.put(TAG_COLOUR, "Colour");
        _tagNameMap.put(TAG_PRINT_IMAGE_MATCHING_INFO, "Print Image Matching (PIM) Info");
        _tagNameMap.put(TAG_TIME_ZONE, "Time Zone");
        _tagNameMap.put(TAG_DAYLIGHT_SAVINGS, "Daylight Savings");
    }

    public PentaxMakernoteDirectory()
    {
        this.setDescriptor(new PentaxMakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Pentax Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

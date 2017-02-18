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
 * Describes tags specific to Kodak cameras.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class KodakMakernoteDirectory extends Directory
{
    public final static int TAG_KODAK_MODEL = 0;
    public final static int TAG_QUALITY = 9;
    public final static int TAG_BURST_MODE = 10;
    public final static int TAG_IMAGE_WIDTH = 12;
    public final static int TAG_IMAGE_HEIGHT = 14;
    public final static int TAG_YEAR_CREATED = 16;
    public final static int TAG_MONTH_DAY_CREATED = 18;
    public final static int TAG_TIME_CREATED = 20;
    public final static int TAG_BURST_MODE_2 = 24;
    public final static int TAG_SHUTTER_MODE = 27;
    public final static int TAG_METERING_MODE = 28;
    public final static int TAG_SEQUENCE_NUMBER = 29;
    public final static int TAG_F_NUMBER = 30;
    public final static int TAG_EXPOSURE_TIME = 32;
    public final static int TAG_EXPOSURE_COMPENSATION = 36;
    public final static int TAG_FOCUS_MODE = 56;
    public final static int TAG_WHITE_BALANCE = 64;
    public final static int TAG_FLASH_MODE = 92;
    public final static int TAG_FLASH_FIRED = 93;
    public final static int TAG_ISO_SETTING = 94;
    public final static int TAG_ISO = 96;
    public final static int TAG_TOTAL_ZOOM = 98;
    public final static int TAG_DATE_TIME_STAMP = 100;
    public final static int TAG_COLOR_MODE = 102;
    public final static int TAG_DIGITAL_ZOOM = 104;
    public final static int TAG_SHARPNESS = 107;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_KODAK_MODEL, "Kodak Model");
        _tagNameMap.put(TAG_QUALITY, "Quality");
        _tagNameMap.put(TAG_BURST_MODE, "Burst Mode");
        _tagNameMap.put(TAG_IMAGE_WIDTH, "Image Width");
        _tagNameMap.put(TAG_IMAGE_HEIGHT, "Image Height");
        _tagNameMap.put(TAG_YEAR_CREATED, "Year Created");
        _tagNameMap.put(TAG_MONTH_DAY_CREATED, "Month/Day Created");
        _tagNameMap.put(TAG_TIME_CREATED, "Time Created");
        _tagNameMap.put(TAG_BURST_MODE_2, "Burst Mode 2");
        _tagNameMap.put(TAG_SHUTTER_MODE, "Shutter Speed");
        _tagNameMap.put(TAG_METERING_MODE, "Metering Mode");
        _tagNameMap.put(TAG_SEQUENCE_NUMBER, "Sequence Number");
        _tagNameMap.put(TAG_F_NUMBER, "F Number");
        _tagNameMap.put(TAG_EXPOSURE_TIME, "Exposure Time");
        _tagNameMap.put(TAG_EXPOSURE_COMPENSATION, "Exposure Compensation");
        _tagNameMap.put(TAG_FOCUS_MODE, "Focus Mode");
        _tagNameMap.put(TAG_WHITE_BALANCE, "White Balance");
        _tagNameMap.put(TAG_FLASH_MODE, "Flash Mode");
        _tagNameMap.put(TAG_FLASH_FIRED, "Flash Fired");
        _tagNameMap.put(TAG_ISO_SETTING, "ISO Setting");
        _tagNameMap.put(TAG_ISO, "ISO");
        _tagNameMap.put(TAG_TOTAL_ZOOM, "Total Zoom");
        _tagNameMap.put(TAG_DATE_TIME_STAMP, "Date/Time Stamp");
        _tagNameMap.put(TAG_COLOR_MODE, "Color Mode");
        _tagNameMap.put(TAG_DIGITAL_ZOOM, "Digital Zoom");
        _tagNameMap.put(TAG_SHARPNESS, "Sharpness");
    }

    public KodakMakernoteDirectory()
    {
        this.setDescriptor(new KodakMakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Kodak Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

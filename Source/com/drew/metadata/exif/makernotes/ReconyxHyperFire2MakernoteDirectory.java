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
 * Describes tags specific to Reconyx HyperFire 2 cameras.
  */
@SuppressWarnings("WeakerAccess")
public class ReconyxHyperFire2MakernoteDirectory extends Directory
{
    public static final int TAG_FILE_NUMBER = 16;
    public static final int TAG_DIRECTORY_NUMBER = 18;
    public static final int TAG_FIRMWARE_VERSION = 42;
    public static final int TAG_FIRMWARE_DATE = 48;
    public static final int TAG_TRIGGER_MODE = 52;
    public static final int TAG_SEQUENCE = 54;
    public static final int TAG_EVENT_NUMBER = 58;
    public static final int TAG_DATE_TIME_ORIGINAL = 62;
    public static final int TAG_DAY_OF_WEEK = 74;
    public static final int TAG_MOON_PHASE = 76;
    public static final int TAG_AMBIENT_TEMPERATURE_FAHRENHEIT = 78;
    public static final int TAG_AMBIENT_TEMPERATURE = 80;
    public static final int TAG_CONTRAST = 82;
    public static final int TAG_BRIGHTNESS = 84;
    public static final int TAG_SHARPNESS = 86;
    public static final int TAG_SATURATION = 88;
    public static final int TAG_FLASH = 90;
    public static final int TAG_AMBIENT_INFRARED = 92;
    public static final int TAG_AMBIENT_LIGHT = 94;
    public static final int TAG_MOTION_SENSITIVITY = 96;
    public static final int TAG_BATTERY_VOLTAGE = 98;
    public static final int TAG_BATTERY_VOLTAGE_AVG = 100;
    public static final int TAG_BATTERY_TYPE = 102;
    public static final int TAG_USER_LABEL = 104;
    public static final int TAG_SERIAL_NUMBER = 126;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_FILE_NUMBER, "File Number");
        _tagNameMap.put(TAG_DIRECTORY_NUMBER, "Directory Number");
        _tagNameMap.put(TAG_FIRMWARE_VERSION, "Firmware Version");
        _tagNameMap.put(TAG_FIRMWARE_DATE, "Firmware Date");
        _tagNameMap.put(TAG_TRIGGER_MODE, "Trigger Mode");
        _tagNameMap.put(TAG_SEQUENCE, "Sequence");
        _tagNameMap.put(TAG_EVENT_NUMBER, "Event Number");
        _tagNameMap.put(TAG_DATE_TIME_ORIGINAL, "Date/Time Original");
        _tagNameMap.put(TAG_DAY_OF_WEEK, "DaY of Week");
        _tagNameMap.put(TAG_MOON_PHASE, "Moon Phase");
        _tagNameMap.put(TAG_AMBIENT_TEMPERATURE_FAHRENHEIT, "Ambient Temperature Fahrenheit");
        _tagNameMap.put(TAG_AMBIENT_TEMPERATURE, "Ambient Temperature");
        _tagNameMap.put(TAG_CONTRAST, "Contrast");
        _tagNameMap.put(TAG_BRIGHTNESS, "Brightness");
        _tagNameMap.put(TAG_SHARPNESS, "Sharpness");
        _tagNameMap.put(TAG_SATURATION, "Saturation");
        _tagNameMap.put(TAG_FLASH, "Flash");
        _tagNameMap.put(TAG_AMBIENT_INFRARED, "Ambient Infrared");
        _tagNameMap.put(TAG_AMBIENT_LIGHT, "Ambient Light");
        _tagNameMap.put(TAG_MOTION_SENSITIVITY, "Motion Sensitivity");
        _tagNameMap.put(TAG_BATTERY_VOLTAGE, "Battery Voltage");
        _tagNameMap.put(TAG_BATTERY_VOLTAGE_AVG, "Battery Voltage Average");
        _tagNameMap.put(TAG_BATTERY_TYPE, "Battery Type");
        _tagNameMap.put(TAG_USER_LABEL, "User Label");
        _tagNameMap.put(TAG_SERIAL_NUMBER, "Serial Number");
    }

    public ReconyxHyperFire2MakernoteDirectory()
    {
        this.setDescriptor(new ReconyxHyperFire2MakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Reconyx HyperFire 2 Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

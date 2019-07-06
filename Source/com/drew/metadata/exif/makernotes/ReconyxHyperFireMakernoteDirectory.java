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
 * Describes tags specific to Reconyx HyperFire cameras.
 *
 * Reconyx uses a fixed makernote block.  Tag values are the byte index of the tag within the makernote.
 * @author Todd West http://cascadescarnivoreproject.blogspot.com
 */
@SuppressWarnings("WeakerAccess")
public class ReconyxHyperFireMakernoteDirectory extends Directory
{
    /**
     * Version number used for identifying makernotes from Reconyx HyperFire cameras.
     */
    public static final int MAKERNOTE_VERSION = 61697;

    public static final int TAG_MAKERNOTE_VERSION = 0;
    public static final int TAG_FIRMWARE_VERSION = 2;
    public static final int TAG_TRIGGER_MODE = 12;
    public static final int TAG_SEQUENCE = 14;
    public static final int TAG_EVENT_NUMBER = 18;
    public static final int TAG_DATE_TIME_ORIGINAL = 22;
    public static final int TAG_MOON_PHASE = 36;
    public static final int TAG_AMBIENT_TEMPERATURE_FAHRENHEIT = 38;
    public static final int TAG_AMBIENT_TEMPERATURE = 40;
    public static final int TAG_SERIAL_NUMBER = 42;
    public static final int TAG_CONTRAST = 72;
    public static final int TAG_BRIGHTNESS = 74;
    public static final int TAG_SHARPNESS = 76;
    public static final int TAG_SATURATION = 78;
    public static final int TAG_INFRARED_ILLUMINATOR = 80;
    public static final int TAG_MOTION_SENSITIVITY = 82;
    public static final int TAG_BATTERY_VOLTAGE = 84;
    public static final int TAG_USER_LABEL = 86;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_MAKERNOTE_VERSION, "Makernote Version");
        _tagNameMap.put(TAG_FIRMWARE_VERSION, "Firmware Version");
        _tagNameMap.put(TAG_TRIGGER_MODE, "Trigger Mode");
        _tagNameMap.put(TAG_SEQUENCE, "Sequence");
        _tagNameMap.put(TAG_EVENT_NUMBER, "Event Number");
        _tagNameMap.put(TAG_DATE_TIME_ORIGINAL, "Date/Time Original");
        _tagNameMap.put(TAG_MOON_PHASE, "Moon Phase");
        _tagNameMap.put(TAG_AMBIENT_TEMPERATURE_FAHRENHEIT, "Ambient Temperature Fahrenheit");
        _tagNameMap.put(TAG_AMBIENT_TEMPERATURE, "Ambient Temperature");
        _tagNameMap.put(TAG_SERIAL_NUMBER, "Serial Number");
        _tagNameMap.put(TAG_CONTRAST, "Contrast");
        _tagNameMap.put(TAG_BRIGHTNESS, "Brightness");
        _tagNameMap.put(TAG_SHARPNESS, "Sharpness");
        _tagNameMap.put(TAG_SATURATION, "Saturation");
        _tagNameMap.put(TAG_INFRARED_ILLUMINATOR, "Infrared Illuminator");
        _tagNameMap.put(TAG_MOTION_SENSITIVITY, "Motion Sensitivity");
        _tagNameMap.put(TAG_BATTERY_VOLTAGE, "Battery Voltage");
        _tagNameMap.put(TAG_USER_LABEL, "User Label");
    }

    public ReconyxHyperFireMakernoteDirectory()
    {
        this.setDescriptor(new ReconyxHyperFireMakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Reconyx HyperFire Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

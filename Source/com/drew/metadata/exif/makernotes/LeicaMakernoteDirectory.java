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
 * Describes tags specific to certain Leica cameras.
 * <p>
 * Tag reference from: http://gvsoft.homedns.org/exif/makernote-leica-type1.html
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class LeicaMakernoteDirectory extends Directory
{
    public static final int TAG_QUALITY = 0x0300;
    public static final int TAG_USER_PROFILE = 0x0302;
    public static final int TAG_SERIAL_NUMBER = 0x0303;
    public static final int TAG_WHITE_BALANCE = 0x0304;

    public static final int TAG_LENS_TYPE = 0x0310;
    public static final int TAG_EXTERNAL_SENSOR_BRIGHTNESS_VALUE = 0x0311;
    public static final int TAG_MEASURED_LV = 0x0312;
    public static final int TAG_APPROXIMATE_F_NUMBER = 0x0313;

    public static final int TAG_CAMERA_TEMPERATURE = 0x0320;
    public static final int TAG_COLOR_TEMPERATURE = 0x0321;
    public static final int TAG_WB_RED_LEVEL = 0x0322;
    public static final int TAG_WB_GREEN_LEVEL = 0x0323;
    public static final int TAG_WB_BLUE_LEVEL = 0x0324;

    public static final int TAG_CCD_VERSION = 0x0330;
    public static final int TAG_CCD_BOARD_VERSION = 0x0331;
    public static final int TAG_CONTROLLER_BOARD_VERSION = 0x0332;
    public static final int TAG_M16_C_VERSION = 0x0333;

    public static final int TAG_IMAGE_ID_NUMBER = 0x0340;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_QUALITY, "Quality");
        _tagNameMap.put(TAG_USER_PROFILE, "User Profile");
        _tagNameMap.put(TAG_SERIAL_NUMBER, "Serial Number");
        _tagNameMap.put(TAG_WHITE_BALANCE, "White Balance");

        _tagNameMap.put(TAG_LENS_TYPE, "Lens Type");
        _tagNameMap.put(TAG_EXTERNAL_SENSOR_BRIGHTNESS_VALUE, "External Sensor Brightness Value");
        _tagNameMap.put(TAG_MEASURED_LV, "Measured LV");
        _tagNameMap.put(TAG_APPROXIMATE_F_NUMBER, "Approximate F Number");

        _tagNameMap.put(TAG_CAMERA_TEMPERATURE, "Camera Temperature");
        _tagNameMap.put(TAG_COLOR_TEMPERATURE, "Color Temperature");
        _tagNameMap.put(TAG_WB_RED_LEVEL, "WB Red Level");
        _tagNameMap.put(TAG_WB_GREEN_LEVEL, "WB Green Level");
        _tagNameMap.put(TAG_WB_BLUE_LEVEL, "WB Blue Level");

        _tagNameMap.put(TAG_CCD_VERSION, "CCD Version");
        _tagNameMap.put(TAG_CCD_BOARD_VERSION, "CCD Board Version");
        _tagNameMap.put(TAG_CONTROLLER_BOARD_VERSION, "Controller Board Version");
        _tagNameMap.put(TAG_M16_C_VERSION, "M16 C Version");

        _tagNameMap.put(TAG_IMAGE_ID_NUMBER, "Image ID Number");
    }

    public LeicaMakernoteDirectory()
    {
        this.setDescriptor(new LeicaMakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Leica Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

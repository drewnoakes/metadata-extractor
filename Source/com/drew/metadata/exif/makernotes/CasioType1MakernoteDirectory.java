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
 * Describes tags specific to Casio (type 1) cameras.
 *
 * A standard TIFF IFD directory but always uses Motorola (Big-Endian) Byte Alignment.
 * Makernote data begins immediately (no header).
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class CasioType1MakernoteDirectory extends Directory
{
    public static final int TAG_RECORDING_MODE = 0x0001;
    public static final int TAG_QUALITY = 0x0002;
    public static final int TAG_FOCUSING_MODE = 0x0003;
    public static final int TAG_FLASH_MODE = 0x0004;
    public static final int TAG_FLASH_INTENSITY = 0x0005;
    public static final int TAG_OBJECT_DISTANCE = 0x0006;
    public static final int TAG_WHITE_BALANCE = 0x0007;
    public static final int TAG_UNKNOWN_1 = 0x0008;
    public static final int TAG_UNKNOWN_2 = 0x0009;
    public static final int TAG_DIGITAL_ZOOM = 0x000A;
    public static final int TAG_SHARPNESS = 0x000B;
    public static final int TAG_CONTRAST = 0x000C;
    public static final int TAG_SATURATION = 0x000D;
    public static final int TAG_UNKNOWN_3 = 0x000E;
    public static final int TAG_UNKNOWN_4 = 0x000F;
    public static final int TAG_UNKNOWN_5 = 0x0010;
    public static final int TAG_UNKNOWN_6 = 0x0011;
    public static final int TAG_UNKNOWN_7 = 0x0012;
    public static final int TAG_UNKNOWN_8 = 0x0013;
    public static final int TAG_CCD_SENSITIVITY = 0x0014;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_CCD_SENSITIVITY, "CCD Sensitivity");
        _tagNameMap.put(TAG_CONTRAST, "Contrast");
        _tagNameMap.put(TAG_DIGITAL_ZOOM, "Digital Zoom");
        _tagNameMap.put(TAG_FLASH_INTENSITY, "Flash Intensity");
        _tagNameMap.put(TAG_FLASH_MODE, "Flash Mode");
        _tagNameMap.put(TAG_FOCUSING_MODE, "Focusing Mode");
        _tagNameMap.put(TAG_OBJECT_DISTANCE, "Object Distance");
        _tagNameMap.put(TAG_QUALITY, "Quality");
        _tagNameMap.put(TAG_RECORDING_MODE, "Recording Mode");
        _tagNameMap.put(TAG_SATURATION, "Saturation");
        _tagNameMap.put(TAG_SHARPNESS, "Sharpness");
        _tagNameMap.put(TAG_UNKNOWN_1, "Makernote Unknown 1");
        _tagNameMap.put(TAG_UNKNOWN_2, "Makernote Unknown 2");
        _tagNameMap.put(TAG_UNKNOWN_3, "Makernote Unknown 3");
        _tagNameMap.put(TAG_UNKNOWN_4, "Makernote Unknown 4");
        _tagNameMap.put(TAG_UNKNOWN_5, "Makernote Unknown 5");
        _tagNameMap.put(TAG_UNKNOWN_6, "Makernote Unknown 6");
        _tagNameMap.put(TAG_UNKNOWN_7, "Makernote Unknown 7");
        _tagNameMap.put(TAG_UNKNOWN_8, "Makernote Unknown 8");
        _tagNameMap.put(TAG_WHITE_BALANCE, "White Balance");
    }

    public CasioType1MakernoteDirectory()
    {
        this.setDescriptor(new CasioType1MakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Casio Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

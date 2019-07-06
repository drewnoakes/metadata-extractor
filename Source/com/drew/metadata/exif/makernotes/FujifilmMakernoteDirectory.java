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
 * Describes tags specific to Fujifilm cameras.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class FujifilmMakernoteDirectory extends Directory
{
    public static final int TAG_MAKERNOTE_VERSION = 0x0000;
    public static final int TAG_SERIAL_NUMBER = 0x0010;

    public static final int TAG_QUALITY = 0x1000;
    public static final int TAG_SHARPNESS = 0x1001;
    public static final int TAG_WHITE_BALANCE = 0x1002;
    public static final int TAG_COLOR_SATURATION = 0x1003;
    public static final int TAG_TONE = 0x1004;
    public static final int TAG_COLOR_TEMPERATURE = 0x1005;
    public static final int TAG_CONTRAST = 0x1006;

    public static final int TAG_WHITE_BALANCE_FINE_TUNE = 0x100a;
    public static final int TAG_NOISE_REDUCTION = 0x100b;
    public static final int TAG_HIGH_ISO_NOISE_REDUCTION = 0x100e;

    public static final int TAG_FLASH_MODE = 0x1010;
    public static final int TAG_FLASH_EV = 0x1011;

    public static final int TAG_MACRO = 0x1020;
    public static final int TAG_FOCUS_MODE = 0x1021;
    public static final int TAG_FOCUS_PIXEL = 0x1023;

    public static final int TAG_SLOW_SYNC = 0x1030;
    public static final int TAG_PICTURE_MODE = 0x1031;
    public static final int TAG_EXR_AUTO = 0x1033;
    public static final int TAG_EXR_MODE = 0x1034;

    public static final int TAG_AUTO_BRACKETING = 0x1100;
    public static final int TAG_SEQUENCE_NUMBER = 0x1101;

    public static final int TAG_FINE_PIX_COLOR = 0x1210;

    public static final int TAG_BLUR_WARNING = 0x1300;
    public static final int TAG_FOCUS_WARNING = 0x1301;
    public static final int TAG_AUTO_EXPOSURE_WARNING = 0x1302;
    public static final int TAG_GE_IMAGE_SIZE = 0x1304;

    public static final int TAG_DYNAMIC_RANGE = 0x1400;
    public static final int TAG_FILM_MODE = 0x1401;
    public static final int TAG_DYNAMIC_RANGE_SETTING = 0x1402;
    public static final int TAG_DEVELOPMENT_DYNAMIC_RANGE = 0x1403;
    public static final int TAG_MIN_FOCAL_LENGTH = 0x1404;
    public static final int TAG_MAX_FOCAL_LENGTH = 0x1405;
    public static final int TAG_MAX_APERTURE_AT_MIN_FOCAL = 0x1406;
    public static final int TAG_MAX_APERTURE_AT_MAX_FOCAL = 0x1407;

    public static final int TAG_AUTO_DYNAMIC_RANGE = 0x140b;

    public static final int TAG_FACES_DETECTED = 0x4100;
    /**
     * Left, top, right and bottom coordinates in full-sized image for each face detected.
     */
    public static final int TAG_FACE_POSITIONS = 0x4103;
    public static final int TAG_FACE_REC_INFO = 0x4282;

    public static final int TAG_FILE_SOURCE = 0x8000;
    public static final int TAG_ORDER_NUMBER = 0x8002;
    public static final int TAG_FRAME_NUMBER = 0x8003;

    public static final int TAG_PARALLAX = 0xb211;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_MAKERNOTE_VERSION, "Makernote Version");
        _tagNameMap.put(TAG_SERIAL_NUMBER, "Serial Number");

        _tagNameMap.put(TAG_QUALITY, "Quality");
        _tagNameMap.put(TAG_SHARPNESS, "Sharpness");
        _tagNameMap.put(TAG_WHITE_BALANCE, "White Balance");
        _tagNameMap.put(TAG_COLOR_SATURATION, "Color Saturation");
        _tagNameMap.put(TAG_TONE, "Tone (Contrast)");
        _tagNameMap.put(TAG_COLOR_TEMPERATURE, "Color Temperature");
        _tagNameMap.put(TAG_CONTRAST, "Contrast");

        _tagNameMap.put(TAG_WHITE_BALANCE_FINE_TUNE, "White Balance Fine Tune");
        _tagNameMap.put(TAG_NOISE_REDUCTION, "Noise Reduction");
        _tagNameMap.put(TAG_HIGH_ISO_NOISE_REDUCTION, "High ISO Noise Reduction");

        _tagNameMap.put(TAG_FLASH_MODE, "Flash Mode");
        _tagNameMap.put(TAG_FLASH_EV, "Flash Strength");

        _tagNameMap.put(TAG_MACRO, "Macro");
        _tagNameMap.put(TAG_FOCUS_MODE, "Focus Mode");
        _tagNameMap.put(TAG_FOCUS_PIXEL, "Focus Pixel");

        _tagNameMap.put(TAG_SLOW_SYNC, "Slow Sync");
        _tagNameMap.put(TAG_PICTURE_MODE, "Picture Mode");
        _tagNameMap.put(TAG_EXR_AUTO, "EXR Auto");
        _tagNameMap.put(TAG_EXR_MODE, "EXR Mode");

        _tagNameMap.put(TAG_AUTO_BRACKETING, "Auto Bracketing");
        _tagNameMap.put(TAG_SEQUENCE_NUMBER, "Sequence Number");

        _tagNameMap.put(TAG_FINE_PIX_COLOR, "FinePix Color Setting");

        _tagNameMap.put(TAG_BLUR_WARNING, "Blur Warning");
        _tagNameMap.put(TAG_FOCUS_WARNING, "Focus Warning");
        _tagNameMap.put(TAG_AUTO_EXPOSURE_WARNING, "AE Warning");
        _tagNameMap.put(TAG_GE_IMAGE_SIZE, "GE Image Size");

        _tagNameMap.put(TAG_DYNAMIC_RANGE, "Dynamic Range");
        _tagNameMap.put(TAG_FILM_MODE, "Film Mode");
        _tagNameMap.put(TAG_DYNAMIC_RANGE_SETTING, "Dynamic Range Setting");
        _tagNameMap.put(TAG_DEVELOPMENT_DYNAMIC_RANGE, "Development Dynamic Range");
        _tagNameMap.put(TAG_MIN_FOCAL_LENGTH, "Minimum Focal Length");
        _tagNameMap.put(TAG_MAX_FOCAL_LENGTH, "Maximum Focal Length");
        _tagNameMap.put(TAG_MAX_APERTURE_AT_MIN_FOCAL, "Maximum Aperture at Minimum Focal Length");
        _tagNameMap.put(TAG_MAX_APERTURE_AT_MAX_FOCAL, "Maximum Aperture at Maximum Focal Length");

        _tagNameMap.put(TAG_AUTO_DYNAMIC_RANGE, "Auto Dynamic Range");

        _tagNameMap.put(TAG_FACES_DETECTED, "Faces Detected");
        _tagNameMap.put(TAG_FACE_POSITIONS, "Face Positions");
        _tagNameMap.put(TAG_FACE_REC_INFO, "Face Detection Data");

        _tagNameMap.put(TAG_FILE_SOURCE, "File Source");
        _tagNameMap.put(TAG_ORDER_NUMBER, "Order Number");
        _tagNameMap.put(TAG_FRAME_NUMBER, "Frame Number");

        _tagNameMap.put(TAG_PARALLAX, "Parallax");
    }

    public FujifilmMakernoteDirectory()
    {
        this.setDescriptor(new FujifilmMakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Fujifilm Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

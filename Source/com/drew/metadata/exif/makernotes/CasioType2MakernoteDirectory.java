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
 * Describes tags specific to Casio (type 2) cameras.
 *
 * A standard TIFF IFD directory but always uses Motorola (Big-Endian) Byte Alignment.
 * Makernote data begins after a 6-byte header: "QVC\x00\x00\x00"
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class CasioType2MakernoteDirectory extends Directory
{
    /**
     * 2 values - x,y dimensions in pixels.
     */
    public static final int TAG_THUMBNAIL_DIMENSIONS = 0x0002;
    /**
     * Size in bytes
     */
    public static final int TAG_THUMBNAIL_SIZE = 0x0003;
    /**
     * Offset of Preview Thumbnail
     */
    public static final int TAG_THUMBNAIL_OFFSET = 0x0004;
    /**
     * 1 = Fine
     * 2 = Super Fine
     */
    public static final int TAG_QUALITY_MODE = 0x0008;
    /**
     * 0 = 640 x 480 pixels
     * 4 = 1600 x 1200 pixels
     * 5 = 2048 x 1536 pixels
     * 20 = 2288 x 1712 pixels
     * 21 = 2592 x 1944 pixels
     * 22 = 2304 x 1728 pixels
     * 36 = 3008 x 2008 pixels
     */
    public static final int TAG_IMAGE_SIZE = 0x0009;
    /**
     * 0 = Normal
     * 1 = Macro
     */
    public static final int TAG_FOCUS_MODE_1 = 0x000D;
    /**
     * 3 = 50
     * 4 = 64
     * 6 = 100
     * 9 = 200
     */
    public static final int TAG_ISO_SENSITIVITY = 0x0014;
    /**
     * 0 = Auto
     * 1 = Daylight
     * 2 = Shade
     * 3 = Tungsten
     * 4 = Fluorescent
     * 5 = Manual
     */
    public static final int TAG_WHITE_BALANCE_1 = 0x0019;
    /**
     * Units are tenths of a millimetre
     */
    public static final int TAG_FOCAL_LENGTH = 0x001D;
    /**
     * 0 = -1
     * 1 = Normal
     * 2 = +1
     */
    public static final int TAG_SATURATION = 0x001F;
    /**
     * 0 = -1
     * 1 = Normal
     * 2 = +1
     */
    public static final int TAG_CONTRAST = 0x0020;
    /**
     * 0 = -1
     * 1 = Normal
     * 2 = +1
     */
    public static final int TAG_SHARPNESS = 0x0021;
    /**
     * See PIM specification here: http://www.ozhiker.com/electronics/pjmt/jpeg_info/pim.html
     */
    public static final int TAG_PRINT_IMAGE_MATCHING_INFO = 0x0E00;
    /**
     * Alternate thumbnail offset
     */
    public static final int TAG_PREVIEW_THUMBNAIL = 0x2000;
    /**
     *
     */
    public static final int TAG_WHITE_BALANCE_BIAS = 0x2011;
    /**
     * 12 = Flash
     * 0 = Manual
     * 1 = Auto?
     * 4 = Flash?
     */
    public static final int TAG_WHITE_BALANCE_2 = 0x2012;
    /**
     * Units are millimetres
     */
    public static final int TAG_OBJECT_DISTANCE = 0x2022;
    /**
     * 0 = Off
     */
    public static final int TAG_FLASH_DISTANCE = 0x2034;
    /**
     * 2 = Normal Mode
     */
    public static final int TAG_RECORD_MODE = 0x3000;
    /**
     * 1 = Off?
     */
    public static final int TAG_SELF_TIMER = 0x3001;
    /**
     * 3 = Fine
     */
    public static final int TAG_QUALITY = 0x3002;
    /**
     * 1 = Fixation
     * 6 = Multi-Area Auto Focus
     */
    public static final int TAG_FOCUS_MODE_2 = 0x3003;
    /**
     * (string)
     */
    public static final int TAG_TIME_ZONE = 0x3006;
    /**
     *
     */
    public static final int TAG_BESTSHOT_MODE = 0x3007;
    /**
     * 0 = Off
     * 1 = On?
     */
    public static final int TAG_CCD_ISO_SENSITIVITY = 0x3014;
    /**
     * 0 = Off
     */
    public static final int TAG_COLOUR_MODE = 0x3015;
    /**
     * 0 = Off
     */
    public static final int TAG_ENHANCEMENT = 0x3016;
    /**
     * 0 = Off
     */
    public static final int TAG_FILTER = 0x3017;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        // TODO add missing names
        _tagNameMap.put(TAG_THUMBNAIL_DIMENSIONS, "Thumbnail Dimensions");
        _tagNameMap.put(TAG_THUMBNAIL_SIZE, "Thumbnail Size");
        _tagNameMap.put(TAG_THUMBNAIL_OFFSET, "Thumbnail Offset");
        _tagNameMap.put(TAG_QUALITY_MODE, "Quality Mode");
        _tagNameMap.put(TAG_IMAGE_SIZE, "Image Size");
        _tagNameMap.put(TAG_FOCUS_MODE_1, "Focus Mode");
        _tagNameMap.put(TAG_ISO_SENSITIVITY, "ISO Sensitivity");
        _tagNameMap.put(TAG_WHITE_BALANCE_1, "White Balance");
        _tagNameMap.put(TAG_FOCAL_LENGTH, "Focal Length");
        _tagNameMap.put(TAG_SATURATION, "Saturation");
        _tagNameMap.put(TAG_CONTRAST, "Contrast");
        _tagNameMap.put(TAG_SHARPNESS, "Sharpness");
        _tagNameMap.put(TAG_PRINT_IMAGE_MATCHING_INFO, "Print Image Matching (PIM) Info");
        _tagNameMap.put(TAG_PREVIEW_THUMBNAIL, "Casio Preview Thumbnail");
        _tagNameMap.put(TAG_WHITE_BALANCE_BIAS, "White Balance Bias");
        _tagNameMap.put(TAG_WHITE_BALANCE_2, "White Balance");
        _tagNameMap.put(TAG_OBJECT_DISTANCE, "Object Distance");
        _tagNameMap.put(TAG_FLASH_DISTANCE, "Flash Distance");
        _tagNameMap.put(TAG_RECORD_MODE, "Record Mode");
        _tagNameMap.put(TAG_SELF_TIMER, "Self Timer");
        _tagNameMap.put(TAG_QUALITY, "Quality");
        _tagNameMap.put(TAG_FOCUS_MODE_2, "Focus Mode");
        _tagNameMap.put(TAG_TIME_ZONE, "Time Zone");
        _tagNameMap.put(TAG_BESTSHOT_MODE, "BestShot Mode");
        _tagNameMap.put(TAG_CCD_ISO_SENSITIVITY, "CCD ISO Sensitivity");
        _tagNameMap.put(TAG_COLOUR_MODE, "Colour Mode");
        _tagNameMap.put(TAG_ENHANCEMENT, "Enhancement");
        _tagNameMap.put(TAG_FILTER, "Filter");
    }

    public CasioType2MakernoteDirectory()
    {
        this.setDescriptor(new CasioType2MakernoteDescriptor(this));
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

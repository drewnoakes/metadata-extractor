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
 * The Olympus makernote is used by many manufacturers (Konica, Minolta and Epson...), and as such contains some tags
 * that appear specific to those manufacturers.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class OlympusMakernoteDirectory extends Directory
{
    /** Used by Konica / Minolta cameras. */
    public static final int TAG_OLYMPUS_MAKERNOTE_VERSION = 0x0000;
    /** Used by Konica / Minolta cameras. */
    public static final int TAG_OLYMPUS_CAMERA_SETTINGS_1 = 0x0001;
    /** Alternate Camera Settings Tag. Used by Konica / Minolta cameras. */
    public static final int TAG_OLYMPUS_CAMERA_SETTINGS_2 = 0x0003;
    /** Used by Konica / Minolta cameras. */
    public static final int TAG_OLYMPUS_COMPRESSED_IMAGE_SIZE = 0x0040;
    /** Used by Konica / Minolta cameras. */
    public static final int TAG_OLYMPUS_MINOLTA_THUMBNAIL_OFFSET_1 = 0x0081;
    /** Alternate Thumbnail Offset. Used by Konica / Minolta cameras. */
    public static final int TAG_OLYMPUS_MINOLTA_THUMBNAIL_OFFSET_2 = 0x0088;
    /** Length of thumbnail in bytes. Used by Konica / Minolta cameras. */
    public static final int TAG_OLYMPUS_MINOLTA_THUMBNAIL_LENGTH = 0x0089;

    /**
     * Used by Konica / Minolta cameras
     * 0 = Natural Colour
     * 1 = Black & White
     * 2 = Vivid colour
     * 3 = Solarization
     * 4 = AdobeRGB
     */
    public static final int TAG_OLYMPUS_COLOUR_MODE = 0x0101;

    /**
     * Used by Konica / Minolta cameras.
     * 0 = Raw
     * 1 = Super Fine
     * 2 = Fine
     * 3 = Standard
     * 4 = Extra Fine
     */
    public static final int TAG_OLYMPUS_IMAGE_QUALITY_1 = 0x0102;

    /**
     * Not 100% sure about this tag.
     * <p/>
     * Used by Konica / Minolta cameras.
     * 0 = Raw
     * 1 = Super Fine
     * 2 = Fine
     * 3 = Standard
     * 4 = Extra Fine
     */
    public static final int TAG_OLYMPUS_IMAGE_QUALITY_2 = 0x0103;


    /**
     * Three values:
     * Value 1: 0=Normal, 2=Fast, 3=Panorama
     * Value 2: Sequence Number Value 3:
     * 1 = Panorama Direction: Left to Right
     * 2 = Panorama Direction: Right to Left
     * 3 = Panorama Direction: Bottom to Top
     * 4 = Panorama Direction: Top to Bottom
     */
    public static final int TAG_OLYMPUS_SPECIAL_MODE = 0x0200;

    /**
     * 1 = Standard Quality
     * 2 = High Quality
     * 3 = Super High Quality
     */
    public static final int TAG_OLYMPUS_JPEG_QUALITY = 0x0201;

    /**
     * 0 = Normal (Not Macro)
     * 1 = Macro
     */
    public static final int TAG_OLYMPUS_MACRO_MODE = 0x0202;

    public static final int TAG_OLYMPUS_UNKNOWN_1 = 0x0203;

    /** Zoom Factor (0 or 1 = normal) */
    public static final int TAG_OLYMPUS_DIGI_ZOOM_RATIO = 0x0204;
    public static final int TAG_OLYMPUS_UNKNOWN_2 = 0x0205;
    public static final int TAG_OLYMPUS_UNKNOWN_3 = 0x0206;
    public static final int TAG_OLYMPUS_FIRMWARE_VERSION = 0x0207;
    public static final int TAG_OLYMPUS_PICT_INFO = 0x0208;
    public static final int TAG_OLYMPUS_CAMERA_ID = 0x0209;

    /**
     * Used by Epson cameras
     * Units = pixels
     */
    public static final int TAG_OLYMPUS_IMAGE_WIDTH = 0x020B;

    /**
     * Used by Epson cameras
     * Units = pixels
     */
    public static final int TAG_OLYMPUS_IMAGE_HEIGHT = 0x020C;

    /** A string. Used by Epson cameras. */
    public static final int TAG_OLYMPUS_ORIGINAL_MANUFACTURER_MODEL = 0x020D;

    /**
     * See the PIM specification here:
     * http://www.ozhiker.com/electronics/pjmt/jpeg_info/pim.html
     */
    public static final int TAG_OLYMPUS_PRINT_IMAGE_MATCHING_INFO = 0x0E00;

    public static final int TAG_OLYMPUS_DATA_DUMP = 0x0F00;
    public static final int TAG_OLYMPUS_FLASH_MODE = 0x1004;
    public static final int TAG_OLYMPUS_BRACKET = 0x1006;
    public static final int TAG_OLYMPUS_FOCUS_MODE = 0x100B;
    public static final int TAG_OLYMPUS_FOCUS_DISTANCE = 0x100C;
    public static final int TAG_OLYMPUS_ZOOM = 0x100D;
    public static final int TAG_OLYMPUS_MACRO_FOCUS = 0x100E;
    public static final int TAG_OLYMPUS_SHARPNESS = 0x100F;
    public static final int TAG_OLYMPUS_COLOUR_MATRIX = 0x1011;
    public static final int TAG_OLYMPUS_BLACK_LEVEL = 0x1012;
    public static final int TAG_OLYMPUS_WHITE_BALANCE = 0x1015;
    public static final int TAG_OLYMPUS_RED_BIAS = 0x1017;
    public static final int TAG_OLYMPUS_BLUE_BIAS = 0x1018;
    public static final int TAG_OLYMPUS_SERIAL_NUMBER = 0x101A;
    public static final int TAG_OLYMPUS_FLASH_BIAS = 0x1023;
    public static final int TAG_OLYMPUS_CONTRAST = 0x1029;
    public static final int TAG_OLYMPUS_SHARPNESS_FACTOR = 0x102A;
    public static final int TAG_OLYMPUS_COLOUR_CONTROL = 0x102B;
    public static final int TAG_OLYMPUS_VALID_BITS = 0x102C;
    public static final int TAG_OLYMPUS_CORING_FILTER = 0x102D;
    public static final int TAG_OLYMPUS_FINAL_WIDTH = 0x102E;
    public static final int TAG_OLYMPUS_FINAL_HEIGHT = 0x102F;
    public static final int TAG_OLYMPUS_COMPRESSION_RATIO = 0x1034;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_OLYMPUS_SPECIAL_MODE, "Special Mode");
        _tagNameMap.put(TAG_OLYMPUS_JPEG_QUALITY, "Jpeg Quality");
        _tagNameMap.put(TAG_OLYMPUS_MACRO_MODE, "Macro");
        _tagNameMap.put(TAG_OLYMPUS_UNKNOWN_1, "Makernote Unknown 1");
        _tagNameMap.put(TAG_OLYMPUS_DIGI_ZOOM_RATIO, "DigiZoom Ratio");
        _tagNameMap.put(TAG_OLYMPUS_UNKNOWN_2, "Makernote Unknown 2");
        _tagNameMap.put(TAG_OLYMPUS_UNKNOWN_3, "Makernote Unknown 3");
        _tagNameMap.put(TAG_OLYMPUS_FIRMWARE_VERSION, "Firmware Version");
        _tagNameMap.put(TAG_OLYMPUS_PICT_INFO, "Pict Info");
        _tagNameMap.put(TAG_OLYMPUS_CAMERA_ID, "Camera Id");
        _tagNameMap.put(TAG_OLYMPUS_DATA_DUMP, "Data Dump");
        _tagNameMap.put(TAG_OLYMPUS_MAKERNOTE_VERSION, "Makernote Version");
        _tagNameMap.put(TAG_OLYMPUS_CAMERA_SETTINGS_1, "Camera Settings");
        _tagNameMap.put(TAG_OLYMPUS_CAMERA_SETTINGS_2, "Camera Settings");
        _tagNameMap.put(TAG_OLYMPUS_COMPRESSED_IMAGE_SIZE, "Compressed Image Size");
        _tagNameMap.put(TAG_OLYMPUS_MINOLTA_THUMBNAIL_OFFSET_1, "Thumbnail Offset");
        _tagNameMap.put(TAG_OLYMPUS_MINOLTA_THUMBNAIL_OFFSET_2, "Thumbnail Offset");
        _tagNameMap.put(TAG_OLYMPUS_MINOLTA_THUMBNAIL_LENGTH, "Thumbnail Length");
        _tagNameMap.put(TAG_OLYMPUS_COLOUR_MODE, "Colour Mode");
        _tagNameMap.put(TAG_OLYMPUS_IMAGE_QUALITY_1, "Image Quality");
        _tagNameMap.put(TAG_OLYMPUS_IMAGE_QUALITY_2, "Image Quality");
        _tagNameMap.put(TAG_OLYMPUS_IMAGE_HEIGHT, "Image Height");
        _tagNameMap.put(TAG_OLYMPUS_IMAGE_WIDTH, "Image Width");
        _tagNameMap.put(TAG_OLYMPUS_ORIGINAL_MANUFACTURER_MODEL, "Original Manufacturer Model");
        _tagNameMap.put(TAG_OLYMPUS_PRINT_IMAGE_MATCHING_INFO, "Print Image Matching (PIM) Info");
        _tagNameMap.put(TAG_OLYMPUS_FLASH_MODE, "Flash Mode");
        _tagNameMap.put(TAG_OLYMPUS_BRACKET, "Bracket");
        _tagNameMap.put(TAG_OLYMPUS_FOCUS_MODE, "Focus Mode");
        _tagNameMap.put(TAG_OLYMPUS_FOCUS_DISTANCE, "Focus Distance");
        _tagNameMap.put(TAG_OLYMPUS_ZOOM, "Zoom");
        _tagNameMap.put(TAG_OLYMPUS_MACRO_FOCUS, "Macro Focus");
        _tagNameMap.put(TAG_OLYMPUS_SHARPNESS, "Sharpness");
        _tagNameMap.put(TAG_OLYMPUS_COLOUR_MATRIX, "Colour Matrix");
        _tagNameMap.put(TAG_OLYMPUS_BLACK_LEVEL, "Black Level");
        _tagNameMap.put(TAG_OLYMPUS_WHITE_BALANCE, "White Balance");
        _tagNameMap.put(TAG_OLYMPUS_RED_BIAS, "Red Bias");
        _tagNameMap.put(TAG_OLYMPUS_BLUE_BIAS, "Blue Bias");
        _tagNameMap.put(TAG_OLYMPUS_SERIAL_NUMBER, "Serial Number");
        _tagNameMap.put(TAG_OLYMPUS_FLASH_BIAS, "Flash Bias");
        _tagNameMap.put(TAG_OLYMPUS_CONTRAST, "Contrast");
        _tagNameMap.put(TAG_OLYMPUS_SHARPNESS_FACTOR, "Sharpness Factor");
        _tagNameMap.put(TAG_OLYMPUS_COLOUR_CONTROL, "Colour Control");
        _tagNameMap.put(TAG_OLYMPUS_VALID_BITS, "Valid Bits");
        _tagNameMap.put(TAG_OLYMPUS_CORING_FILTER, "Coring Filter");
        _tagNameMap.put(TAG_OLYMPUS_FINAL_WIDTH, "Final Width");
        _tagNameMap.put(TAG_OLYMPUS_FINAL_HEIGHT, "Final Height");
        _tagNameMap.put(TAG_OLYMPUS_COMPRESSION_RATIO, "Compression Ratio");
    }

    public OlympusMakernoteDirectory()
    {
        this.setDescriptor(new OlympusMakernoteDescriptor(this));
    }

    @NotNull
    public String getName()
    {
        return "Olympus Makernote";
    }

    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

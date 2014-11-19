/*
 * Copyright 2002-2014 Drew Noakes
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

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.io.IOException;
import java.util.HashMap;

/**
 * The Olympus makernote is used by many manufacturers (Epson, Konica, Minolta and Agfa...), and as such contains some tags
 * that appear specific to those manufacturers.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class OlympusMakernoteDirectory extends Directory
{
    /** Used by Konica / Minolta cameras. */
    public static final int TAG_MAKERNOTE_VERSION = 0x0000;
    /** Used by Konica / Minolta cameras. */
    public static final int TAG_CAMERA_SETTINGS_1 = 0x0001;
    /** Alternate Camera Settings Tag. Used by Konica / Minolta cameras. */
    public static final int TAG_CAMERA_SETTINGS_2 = 0x0003;
    /** Used by Konica / Minolta cameras. */
    public static final int TAG_COMPRESSED_IMAGE_SIZE = 0x0040;
    /** Used by Konica / Minolta cameras. */
    public static final int TAG_MINOLTA_THUMBNAIL_OFFSET_1 = 0x0081;
    /** Alternate Thumbnail Offset. Used by Konica / Minolta cameras. */
    public static final int TAG_MINOLTA_THUMBNAIL_OFFSET_2 = 0x0088;
    /** Length of thumbnail in bytes. Used by Konica / Minolta cameras. */
    public static final int TAG_MINOLTA_THUMBNAIL_LENGTH = 0x0089;

    /**
     * Used by Konica / Minolta cameras
     * 0 = Natural Colour
     * 1 = Black & White
     * 2 = Vivid colour
     * 3 = Solarization
     * 4 = AdobeRGB
     */
    public static final int TAG_COLOUR_MODE = 0x0101;

    /**
     * Used by Konica / Minolta cameras.
     * 0 = Raw
     * 1 = Super Fine
     * 2 = Fine
     * 3 = Standard
     * 4 = Extra Fine
     */
    public static final int TAG_IMAGE_QUALITY_1 = 0x0102;

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
    public static final int TAG_IMAGE_QUALITY_2 = 0x0103;


    /**
     * Three values:
     * Value 1: 0=Normal, 2=Fast, 3=Panorama
     * Value 2: Sequence Number Value 3:
     * 1 = Panorama Direction: Left to Right
     * 2 = Panorama Direction: Right to Left
     * 3 = Panorama Direction: Bottom to Top
     * 4 = Panorama Direction: Top to Bottom
     */
    public static final int TAG_SPECIAL_MODE = 0x0200;

    /**
     * 1 = Standard Quality
     * 2 = High Quality
     * 3 = Super High Quality
     */
    public static final int TAG_JPEG_QUALITY = 0x0201;

    /**
     * 0 = Normal (Not Macro)
     * 1 = Macro
     */
    public static final int TAG_MACRO_MODE = 0x0202;

    /**
     * 0 = Off, 1 = On
     */
    public static final int TAG_BW_MODE = 0x0203;

    /** Zoom Factor (0 or 1 = normal) */
    public static final int TAG_DIGI_ZOOM_RATIO = 0x0204;
    public static final int TAG_FOCAL_PLANE_DIAGONAL = 0x0205;
    public static final int TAG_LENS_DISTORTION_PARAMETERS = 0x0206;
    public static final int TAG_FIRMWARE_VERSION = 0x0207;
    public static final int TAG_PICT_INFO = 0x0208;
    public static final int TAG_CAMERA_ID = 0x0209;

    /**
     * Used by Epson cameras
     * Units = pixels
     */
    public static final int TAG_IMAGE_WIDTH = 0x020B;

    /**
     * Used by Epson cameras
     * Units = pixels
     */
    public static final int TAG_IMAGE_HEIGHT = 0x020C;

    /** A string. Used by Epson cameras. */
    public static final int TAG_ORIGINAL_MANUFACTURER_MODEL = 0x020D;

    /**
     * See the PIM specification here:
     * http://www.ozhiker.com/electronics/pjmt/jpeg_info/pim.html
     */
    public static final int TAG_PRINT_IMAGE_MATCHING_INFO = 0x0E00;

    public static final int TAG_DATA_DUMP = 0x0F00;

    public static final int TAG_SHUTTER_SPEED_VALUE = 0x1000;
    public static final int TAG_ISO_VALUE = 0x1001;
    public static final int TAG_APERTURE_VALUE = 0x1002;
    public static final int TAG_BRIGHTNESS_VALUE = 0x1003;
    public static final int TAG_FLASH_MODE = 0x1004;
    public static final int TAG_BRACKET = 0x1006;
    public static final int TAG_FOCUS_RANGE = 0x100A;
    public static final int TAG_FOCUS_MODE = 0x100B;
    public static final int TAG_FOCUS_DISTANCE = 0x100C;
    public static final int TAG_ZOOM = 0x100D;
    public static final int TAG_MACRO_FOCUS = 0x100E;
    public static final int TAG_SHARPNESS = 0x100F;
    public static final int TAG_COLOUR_MATRIX = 0x1011;
    public static final int TAG_BLACK_LEVEL = 0x1012;
    public static final int TAG_WHITE_BALANCE = 0x1015;
    public static final int TAG_RED_BIAS = 0x1017;
    public static final int TAG_BLUE_BIAS = 0x1018;
    public static final int TAG_SERIAL_NUMBER = 0x101A;
    public static final int TAG_FLASH_BIAS = 0x1023;
    public static final int TAG_CONTRAST = 0x1029;
    public static final int TAG_SHARPNESS_FACTOR = 0x102A;
    public static final int TAG_COLOUR_CONTROL = 0x102B;
    public static final int TAG_VALID_BITS = 0x102C;
    public static final int TAG_CORING_FILTER = 0x102D;
    public static final int TAG_FINAL_WIDTH = 0x102E;
    public static final int TAG_FINAL_HEIGHT = 0x102F;
    public static final int TAG_COMPRESSION_RATIO = 0x1034;

    public final static class CameraSettings
    {
        // These 'sub'-tag values have been created for consistency -- they don't exist within the Makernote IFD
        private static final int OFFSET = 0xF000;

        public static final int TAG_EXPOSURE_MODE = OFFSET + 2;
        public static final int TAG_FLASH_MODE = OFFSET + 3;
        public static final int TAG_WHITE_BALANCE = OFFSET + 4;
        public static final int TAG_IMAGE_SIZE = OFFSET + 5;
        public static final int TAG_IMAGE_QUALITY = OFFSET + 6;
        public static final int TAG_SHOOTING_MODE = OFFSET + 7;
        public static final int TAG_METERING_MODE = OFFSET + 8;
        public static final int TAG_APEX_FILM_SPEED_VALUE = OFFSET + 9;
        public static final int TAG_APEX_SHUTTER_SPEED_TIME_VALUE = OFFSET + 10;
        public static final int TAG_APEX_APERTURE_VALUE = OFFSET + 11;
        public static final int TAG_MACRO_MODE = OFFSET + 12;
        public static final int TAG_DIGITAL_ZOOM = OFFSET + 13;
        public static final int TAG_EXPOSURE_COMPENSATION = OFFSET + 14;
        public static final int TAG_BRACKET_STEP = OFFSET + 15;

        public static final int TAG_INTERVAL_LENGTH = OFFSET + 17;
        public static final int TAG_INTERVAL_NUMBER = OFFSET + 18;
        public static final int TAG_FOCAL_LENGTH = OFFSET + 19;
        public static final int TAG_FOCUS_DISTANCE = OFFSET + 20;
        public static final int TAG_FLASH_FIRED = OFFSET + 21;
        public static final int TAG_DATE = OFFSET + 22;
        public static final int TAG_TIME = OFFSET + 23;
        public static final int TAG_MAX_APERTURE_AT_FOCAL_LENGTH = OFFSET + 24;

        public static final int TAG_FILE_NUMBER_MEMORY = OFFSET + 27;
        public static final int TAG_LAST_FILE_NUMBER = OFFSET + 28;
        public static final int TAG_WHITE_BALANCE_RED = OFFSET + 29;
        public static final int TAG_WHITE_BALANCE_GREEN = OFFSET + 30;
        public static final int TAG_WHITE_BALANCE_BLUE = OFFSET + 31;
        public static final int TAG_SATURATION = OFFSET + 32;
        public static final int TAG_CONTRAST = OFFSET + 33;
        public static final int TAG_SHARPNESS = OFFSET + 34;
        public static final int TAG_SUBJECT_PROGRAM = OFFSET + 35;
        public static final int TAG_FLASH_COMPENSATION = OFFSET + 36;
        public static final int TAG_ISO_SETTING = OFFSET + 37;
        public static final int TAG_CAMERA_MODEL = OFFSET + 38;
        public static final int TAG_INTERVAL_MODE = OFFSET + 39;
        public static final int TAG_FOLDER_NAME = OFFSET + 40;
        public static final int TAG_COLOR_MODE = OFFSET + 41;
        public static final int TAG_COLOR_FILTER = OFFSET + 42;
        public static final int TAG_BLACK_AND_WHITE_FILTER = OFFSET + 43;
        public static final int TAG_INTERNAL_FLASH = OFFSET + 44;
        public static final int TAG_APEX_BRIGHTNESS_VALUE = OFFSET + 45;
        public static final int TAG_SPOT_FOCUS_POINT_X_COORDINATE = OFFSET + 46;
        public static final int TAG_SPOT_FOCUS_POINT_Y_COORDINATE = OFFSET + 47;
        public static final int TAG_WIDE_FOCUS_ZONE = OFFSET + 48;
        public static final int TAG_FOCUS_MODE = OFFSET + 49;
        public static final int TAG_FOCUS_AREA = OFFSET + 50;
        public static final int TAG_DEC_SWITCH_POSITION = OFFSET + 51;
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_SPECIAL_MODE, "Special Mode");
        _tagNameMap.put(TAG_JPEG_QUALITY, "JPEG Quality");
        _tagNameMap.put(TAG_MACRO_MODE, "Macro");
        _tagNameMap.put(TAG_BW_MODE, "BW Mode");
        _tagNameMap.put(TAG_DIGI_ZOOM_RATIO, "DigiZoom Ratio");
        _tagNameMap.put(TAG_FOCAL_PLANE_DIAGONAL, "Focal Plane Diagonal");
        _tagNameMap.put(TAG_LENS_DISTORTION_PARAMETERS, "Lens Distortion Parameters");
        _tagNameMap.put(TAG_FIRMWARE_VERSION, "Firmware Version");
        _tagNameMap.put(TAG_PICT_INFO, "Pict Info");
        _tagNameMap.put(TAG_CAMERA_ID, "Camera Id");
        _tagNameMap.put(TAG_DATA_DUMP, "Data Dump");
        _tagNameMap.put(TAG_MAKERNOTE_VERSION, "Makernote Version");
        _tagNameMap.put(TAG_CAMERA_SETTINGS_1, "Camera Settings");
        _tagNameMap.put(TAG_CAMERA_SETTINGS_2, "Camera Settings");
        _tagNameMap.put(TAG_COMPRESSED_IMAGE_SIZE, "Compressed Image Size");
        _tagNameMap.put(TAG_MINOLTA_THUMBNAIL_OFFSET_1, "Thumbnail Offset");
        _tagNameMap.put(TAG_MINOLTA_THUMBNAIL_OFFSET_2, "Thumbnail Offset");
        _tagNameMap.put(TAG_MINOLTA_THUMBNAIL_LENGTH, "Thumbnail Length");
        _tagNameMap.put(TAG_COLOUR_MODE, "Colour Mode");
        _tagNameMap.put(TAG_IMAGE_QUALITY_1, "Image Quality");
        _tagNameMap.put(TAG_IMAGE_QUALITY_2, "Image Quality");
        _tagNameMap.put(TAG_IMAGE_HEIGHT, "Image Height");
        _tagNameMap.put(TAG_IMAGE_WIDTH, "Image Width");
        _tagNameMap.put(TAG_ORIGINAL_MANUFACTURER_MODEL, "Original Manufacturer Model");
        _tagNameMap.put(TAG_PRINT_IMAGE_MATCHING_INFO, "Print Image Matching (PIM) Info");

        _tagNameMap.put(TAG_SHUTTER_SPEED_VALUE, "Shutter Speed Value");
        _tagNameMap.put(TAG_ISO_VALUE, "ISO Value");
        _tagNameMap.put(TAG_APERTURE_VALUE, "Aperture Value");
        _tagNameMap.put(TAG_BRIGHTNESS_VALUE, "Brightness Value");
        _tagNameMap.put(TAG_FLASH_MODE, "Flash Mode");
        _tagNameMap.put(TAG_BRACKET, "Bracket");
        _tagNameMap.put(TAG_FOCUS_RANGE, "Focus Range");
        _tagNameMap.put(TAG_FOCUS_MODE, "Focus Mode");
        _tagNameMap.put(TAG_FOCUS_DISTANCE, "Focus Distance");
        _tagNameMap.put(TAG_ZOOM, "Zoom");
        _tagNameMap.put(TAG_MACRO_FOCUS, "Macro Focus");
        _tagNameMap.put(TAG_SHARPNESS, "Sharpness");
        _tagNameMap.put(TAG_COLOUR_MATRIX, "Colour Matrix");
        _tagNameMap.put(TAG_BLACK_LEVEL, "Black Level");
        _tagNameMap.put(TAG_WHITE_BALANCE, "White Balance");
        _tagNameMap.put(TAG_RED_BIAS, "Red Bias");
        _tagNameMap.put(TAG_BLUE_BIAS, "Blue Bias");
        _tagNameMap.put(TAG_SERIAL_NUMBER, "Serial Number");
        _tagNameMap.put(TAG_FLASH_BIAS, "Flash Bias");
        _tagNameMap.put(TAG_CONTRAST, "Contrast");
        _tagNameMap.put(TAG_SHARPNESS_FACTOR, "Sharpness Factor");
        _tagNameMap.put(TAG_COLOUR_CONTROL, "Colour Control");
        _tagNameMap.put(TAG_VALID_BITS, "Valid Bits");
        _tagNameMap.put(TAG_CORING_FILTER, "Coring Filter");
        _tagNameMap.put(TAG_FINAL_WIDTH, "Final Width");
        _tagNameMap.put(TAG_FINAL_HEIGHT, "Final Height");
        _tagNameMap.put(TAG_COMPRESSION_RATIO, "Compression Ratio");
        
        _tagNameMap.put(CameraSettings.TAG_EXPOSURE_MODE, "Exposure Mode");
        _tagNameMap.put(CameraSettings.TAG_FLASH_MODE, "Flash Mode");
        _tagNameMap.put(CameraSettings.TAG_WHITE_BALANCE, "White Balance");
        _tagNameMap.put(CameraSettings.TAG_IMAGE_SIZE, "Image Size");
        _tagNameMap.put(CameraSettings.TAG_IMAGE_QUALITY, "Image Quality");
        _tagNameMap.put(CameraSettings.TAG_SHOOTING_MODE, "Shooting Mode");
        _tagNameMap.put(CameraSettings.TAG_METERING_MODE, "Metering Mode");
        _tagNameMap.put(CameraSettings.TAG_APEX_FILM_SPEED_VALUE, "Apex Film Speed Value");
        _tagNameMap.put(CameraSettings.TAG_APEX_SHUTTER_SPEED_TIME_VALUE, "Apex Shutter Speed Time Value");
        _tagNameMap.put(CameraSettings.TAG_APEX_APERTURE_VALUE, "Apex Aperture Value");
        _tagNameMap.put(CameraSettings.TAG_MACRO_MODE, "Macro Mode");
        _tagNameMap.put(CameraSettings.TAG_DIGITAL_ZOOM, "Digital Zoom");
        _tagNameMap.put(CameraSettings.TAG_EXPOSURE_COMPENSATION, "Exposure Compensation");
        _tagNameMap.put(CameraSettings.TAG_BRACKET_STEP, "Bracket Step");

        _tagNameMap.put(CameraSettings.TAG_INTERVAL_LENGTH, "Interval Length");
        _tagNameMap.put(CameraSettings.TAG_INTERVAL_NUMBER, "Interval Number");
        _tagNameMap.put(CameraSettings.TAG_FOCAL_LENGTH, "Focal Length");
        _tagNameMap.put(CameraSettings.TAG_FOCUS_DISTANCE, "Focus Distance");
        _tagNameMap.put(CameraSettings.TAG_FLASH_FIRED, "Flash Fired");
        _tagNameMap.put(CameraSettings.TAG_DATE, "Date");
        _tagNameMap.put(CameraSettings.TAG_TIME, "Time");
        _tagNameMap.put(CameraSettings.TAG_MAX_APERTURE_AT_FOCAL_LENGTH, "Max Aperture at Focal Length");

        _tagNameMap.put(CameraSettings.TAG_FILE_NUMBER_MEMORY, "File Number Memory");
        _tagNameMap.put(CameraSettings.TAG_LAST_FILE_NUMBER, "Last File Number");
        _tagNameMap.put(CameraSettings.TAG_WHITE_BALANCE_RED, "White Balance Red");
        _tagNameMap.put(CameraSettings.TAG_WHITE_BALANCE_GREEN, "White Balance Green");
        _tagNameMap.put(CameraSettings.TAG_WHITE_BALANCE_BLUE, "White Balance Blue");
        _tagNameMap.put(CameraSettings.TAG_SATURATION, "Saturation");
        _tagNameMap.put(CameraSettings.TAG_CONTRAST, "Contrast");
        _tagNameMap.put(CameraSettings.TAG_SHARPNESS, "Sharpness");
        _tagNameMap.put(CameraSettings.TAG_SUBJECT_PROGRAM, "Subject Program");
        _tagNameMap.put(CameraSettings.TAG_FLASH_COMPENSATION, "Flash Compensation");
        _tagNameMap.put(CameraSettings.TAG_ISO_SETTING, "ISO Setting");
        _tagNameMap.put(CameraSettings.TAG_CAMERA_MODEL, "Camera Model");
        _tagNameMap.put(CameraSettings.TAG_INTERVAL_MODE, "Interval Mode");
        _tagNameMap.put(CameraSettings.TAG_FOLDER_NAME, "Folder Name");
        _tagNameMap.put(CameraSettings.TAG_COLOR_MODE, "Color Mode");
        _tagNameMap.put(CameraSettings.TAG_COLOR_FILTER, "Color Filter");
        _tagNameMap.put(CameraSettings.TAG_BLACK_AND_WHITE_FILTER, "Black and White Filter");
        _tagNameMap.put(CameraSettings.TAG_INTERNAL_FLASH, "Internal Flash");
        _tagNameMap.put(CameraSettings.TAG_APEX_BRIGHTNESS_VALUE, "Apex Brightness Value");
        _tagNameMap.put(CameraSettings.TAG_SPOT_FOCUS_POINT_X_COORDINATE, "Spot Focus Point X Coordinate");
        _tagNameMap.put(CameraSettings.TAG_SPOT_FOCUS_POINT_Y_COORDINATE, "Spot Focus Point Y Coordinate");
        _tagNameMap.put(CameraSettings.TAG_WIDE_FOCUS_ZONE, "Wide Focus Zone");
        _tagNameMap.put(CameraSettings.TAG_FOCUS_MODE, "Focus Mode");
        _tagNameMap.put(CameraSettings.TAG_FOCUS_AREA, "Focus Area");
        _tagNameMap.put(CameraSettings.TAG_DEC_SWITCH_POSITION, "DEC Switch Position");
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

    @Override
    public void setByteArray(int tagType, @NotNull byte[] bytes)
    {
        if (tagType == TAG_CAMERA_SETTINGS_1 || tagType == TAG_CAMERA_SETTINGS_2) {
            processCameraSettings(bytes);
        } else {
            super.setByteArray(tagType, bytes);
        }
    }

    private void processCameraSettings(byte[] bytes)
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(bytes);
        reader.setMotorolaByteOrder(true);

        int count = bytes.length / 4;

        try {
            for (int i = 0; i < count; i++) {
                int value = reader.getInt32();
                setInt(CameraSettings.OFFSET + i, value);
            }
        } catch (IOException e) {
            // Should never happen, given that we check the length of the bytes beforehand.
            e.printStackTrace();
        }
    }

    public boolean isIntervalMode()
    {
        Long value = getLongObject(CameraSettings.TAG_SHOOTING_MODE);
        return value != null && value == 5;
    }

    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

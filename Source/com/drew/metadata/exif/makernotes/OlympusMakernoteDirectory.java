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
@SuppressWarnings("WeakerAccess")
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

    public static final int TAG_THUMBNAIL_IMAGE = 0x0100;

    /**
     * Used by Konica / Minolta cameras
     * 0 = Natural Colour
     * 1 = Black &amp; White
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
     * <p>
     * Used by Konica / Minolta cameras.
     * 0 = Raw
     * 1 = Super Fine
     * 2 = Fine
     * 3 = Standard
     * 4 = Extra Fine
     */
    public static final int TAG_IMAGE_QUALITY_2 = 0x0103;

    public static final int TAG_BODY_FIRMWARE_VERSION = 0x0104;

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
    public static final int TAG_DIGITAL_ZOOM = 0x0204;
    public static final int TAG_FOCAL_PLANE_DIAGONAL = 0x0205;
    public static final int TAG_LENS_DISTORTION_PARAMETERS = 0x0206;
    public static final int TAG_CAMERA_TYPE = 0x0207;
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

    public static final int TAG_PREVIEW_IMAGE = 0x0280;
    public static final int TAG_PRE_CAPTURE_FRAMES = 0x0300;
    public static final int TAG_WHITE_BOARD = 0x0301;
    public static final int TAG_ONE_TOUCH_WB = 0x0302;
    public static final int TAG_WHITE_BALANCE_BRACKET = 0x0303;
    public static final int TAG_WHITE_BALANCE_BIAS = 0x0304;
    public static final int TAG_SCENE_MODE = 0x0403;
    public static final int TAG_SERIAL_NUMBER_1 = 0x0404;
    public static final int TAG_FIRMWARE = 0x0405;

    /**
     * See the PIM specification here:
     * http://www.ozhiker.com/electronics/pjmt/jpeg_info/pim.html
     */
    public static final int TAG_PRINT_IMAGE_MATCHING_INFO = 0x0E00;

    public static final int TAG_DATA_DUMP_1 = 0x0F00;
    public static final int TAG_DATA_DUMP_2 = 0x0F01;

    public static final int TAG_SHUTTER_SPEED_VALUE = 0x1000;
    public static final int TAG_ISO_VALUE = 0x1001;
    public static final int TAG_APERTURE_VALUE = 0x1002;
    public static final int TAG_BRIGHTNESS_VALUE = 0x1003;
    public static final int TAG_FLASH_MODE = 0x1004;
    public static final int TAG_FLASH_DEVICE = 0x1005;
    public static final int TAG_BRACKET = 0x1006;
    public static final int TAG_SENSOR_TEMPERATURE = 0x1007;
    public static final int TAG_LENS_TEMPERATURE = 0x1008;
    public static final int TAG_LIGHT_CONDITION = 0x1009;
    public static final int TAG_FOCUS_RANGE = 0x100A;
    public static final int TAG_FOCUS_MODE = 0x100B;
    public static final int TAG_FOCUS_DISTANCE = 0x100C;
    public static final int TAG_ZOOM = 0x100D;
    public static final int TAG_MACRO_FOCUS = 0x100E;
    public static final int TAG_SHARPNESS = 0x100F;
    public static final int TAG_FLASH_CHARGE_LEVEL = 0x1010;
    public static final int TAG_COLOUR_MATRIX = 0x1011;
    public static final int TAG_BLACK_LEVEL = 0x1012;
    public static final int TAG_COLOR_TEMPERATURE_BG = 0x1013;
    public static final int TAG_COLOR_TEMPERATURE_RG = 0x1014;
    public static final int TAG_WB_MODE = 0x1015;
//    public static final int TAG_ = 0x1016;
    public static final int TAG_RED_BALANCE = 0x1017;
    public static final int TAG_BLUE_BALANCE = 0x1018;
    public static final int TAG_COLOR_MATRIX_NUMBER = 0x1019;
    public static final int TAG_SERIAL_NUMBER_2 = 0x101A;

    public static final int TAG_EXTERNAL_FLASH_AE1_0 = 0x101B;
    public static final int TAG_EXTERNAL_FLASH_AE2_0 = 0x101C;
    public static final int TAG_INTERNAL_FLASH_AE1_0 = 0x101D;
    public static final int TAG_INTERNAL_FLASH_AE2_0 = 0x101E;
    public static final int TAG_EXTERNAL_FLASH_AE1 = 0x101F;
    public static final int TAG_EXTERNAL_FLASH_AE2 = 0x1020;
    public static final int TAG_INTERNAL_FLASH_AE1 = 0x1021;
    public static final int TAG_INTERNAL_FLASH_AE2 = 0x1022;

    public static final int TAG_FLASH_BIAS = 0x1023;
    public static final int TAG_INTERNAL_FLASH_TABLE = 0x1024;
    public static final int TAG_EXTERNAL_FLASH_G_VALUE = 0x1025;
    public static final int TAG_EXTERNAL_FLASH_BOUNCE = 0x1026;
    public static final int TAG_EXTERNAL_FLASH_ZOOM = 0x1027;
    public static final int TAG_EXTERNAL_FLASH_MODE = 0x1028;
    public static final int TAG_CONTRAST = 0x1029;
    public static final int TAG_SHARPNESS_FACTOR = 0x102A;
    public static final int TAG_COLOUR_CONTROL = 0x102B;
    public static final int TAG_VALID_BITS = 0x102C;
    public static final int TAG_CORING_FILTER = 0x102D;
    public static final int TAG_OLYMPUS_IMAGE_WIDTH = 0x102E;
    public static final int TAG_OLYMPUS_IMAGE_HEIGHT = 0x102F;
    public static final int TAG_SCENE_DETECT = 0x1030;
    public static final int TAG_SCENE_AREA = 0x1031;
//    public static final int TAG_ = 0x1032;
    public static final int TAG_SCENE_DETECT_DATA = 0x1033;
    public static final int TAG_COMPRESSION_RATIO = 0x1034;
    public static final int TAG_PREVIEW_IMAGE_VALID = 0x1035;
    public static final int TAG_PREVIEW_IMAGE_START = 0x1036;
    public static final int TAG_PREVIEW_IMAGE_LENGTH = 0x1037;
    public static final int TAG_AF_RESULT = 0x1038;
    public static final int TAG_CCD_SCAN_MODE = 0x1039;
    public static final int TAG_NOISE_REDUCTION = 0x103A;
    public static final int TAG_INFINITY_LENS_STEP = 0x103B;
    public static final int TAG_NEAR_LENS_STEP = 0x103C;
    public static final int TAG_LIGHT_VALUE_CENTER = 0x103D;
    public static final int TAG_LIGHT_VALUE_PERIPHERY = 0x103E;
    public static final int TAG_FIELD_COUNT = 0x103F;
    public static final int TAG_EQUIPMENT = 0x2010;
    public static final int TAG_CAMERA_SETTINGS = 0x2020;
    public static final int TAG_RAW_DEVELOPMENT = 0x2030;
    public static final int TAG_RAW_DEVELOPMENT_2 = 0x2031;
    public static final int TAG_IMAGE_PROCESSING = 0x2040;
    public static final int TAG_FOCUS_INFO = 0x2050;
    public static final int TAG_RAW_INFO = 0x3000;
    public static final int TAG_MAIN_INFO = 0x4000;

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
        // 16 missing
        public static final int TAG_INTERVAL_LENGTH = OFFSET + 17;
        public static final int TAG_INTERVAL_NUMBER = OFFSET + 18;
        public static final int TAG_FOCAL_LENGTH = OFFSET + 19;
        public static final int TAG_FOCUS_DISTANCE = OFFSET + 20;
        public static final int TAG_FLASH_FIRED = OFFSET + 21;
        public static final int TAG_DATE = OFFSET + 22;
        public static final int TAG_TIME = OFFSET + 23;
        public static final int TAG_MAX_APERTURE_AT_FOCAL_LENGTH = OFFSET + 24;
        // 25, 26 missing
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
        _tagNameMap.put(TAG_MAKERNOTE_VERSION, "Makernote Version");
        _tagNameMap.put(TAG_CAMERA_SETTINGS_1, "Camera Settings");
        _tagNameMap.put(TAG_CAMERA_SETTINGS_2, "Camera Settings");
        _tagNameMap.put(TAG_COMPRESSED_IMAGE_SIZE, "Compressed Image Size");
        _tagNameMap.put(TAG_MINOLTA_THUMBNAIL_OFFSET_1, "Thumbnail Offset");
        _tagNameMap.put(TAG_MINOLTA_THUMBNAIL_OFFSET_2, "Thumbnail Offset");
        _tagNameMap.put(TAG_MINOLTA_THUMBNAIL_LENGTH, "Thumbnail Length");
        _tagNameMap.put(TAG_THUMBNAIL_IMAGE, "Thumbnail Image");
        _tagNameMap.put(TAG_COLOUR_MODE, "Colour Mode");
        _tagNameMap.put(TAG_IMAGE_QUALITY_1, "Image Quality");
        _tagNameMap.put(TAG_IMAGE_QUALITY_2, "Image Quality");
        _tagNameMap.put(TAG_BODY_FIRMWARE_VERSION, "Body Firmware Version");
        _tagNameMap.put(TAG_SPECIAL_MODE, "Special Mode");
        _tagNameMap.put(TAG_JPEG_QUALITY, "JPEG Quality");
        _tagNameMap.put(TAG_MACRO_MODE, "Macro");
        _tagNameMap.put(TAG_BW_MODE, "BW Mode");
        _tagNameMap.put(TAG_DIGITAL_ZOOM, "Digital Zoom");
        _tagNameMap.put(TAG_FOCAL_PLANE_DIAGONAL, "Focal Plane Diagonal");
        _tagNameMap.put(TAG_LENS_DISTORTION_PARAMETERS, "Lens Distortion Parameters");
        _tagNameMap.put(TAG_CAMERA_TYPE, "Camera Type");
        _tagNameMap.put(TAG_PICT_INFO, "Pict Info");
        _tagNameMap.put(TAG_CAMERA_ID, "Camera Id");
        _tagNameMap.put(TAG_IMAGE_WIDTH, "Image Width");
        _tagNameMap.put(TAG_IMAGE_HEIGHT, "Image Height");
        _tagNameMap.put(TAG_ORIGINAL_MANUFACTURER_MODEL, "Original Manufacturer Model");
        _tagNameMap.put(TAG_PREVIEW_IMAGE, "Preview Image");
        _tagNameMap.put(TAG_PRE_CAPTURE_FRAMES, "Pre Capture Frames");
        _tagNameMap.put(TAG_WHITE_BOARD, "White Board");
        _tagNameMap.put(TAG_ONE_TOUCH_WB, "One Touch WB");
        _tagNameMap.put(TAG_WHITE_BALANCE_BRACKET, "White Balance Bracket");
        _tagNameMap.put(TAG_WHITE_BALANCE_BIAS, "White Balance Bias");
        _tagNameMap.put(TAG_SCENE_MODE, "Scene Mode");
        _tagNameMap.put(TAG_SERIAL_NUMBER_1, "Serial Number");
        _tagNameMap.put(TAG_FIRMWARE, "Firmware");
        _tagNameMap.put(TAG_PRINT_IMAGE_MATCHING_INFO, "Print Image Matching (PIM) Info");
        _tagNameMap.put(TAG_DATA_DUMP_1, "Data Dump");
        _tagNameMap.put(TAG_DATA_DUMP_2, "Data Dump 2");
        _tagNameMap.put(TAG_SHUTTER_SPEED_VALUE, "Shutter Speed Value");
        _tagNameMap.put(TAG_ISO_VALUE, "ISO Value");
        _tagNameMap.put(TAG_APERTURE_VALUE, "Aperture Value");
        _tagNameMap.put(TAG_BRIGHTNESS_VALUE, "Brightness Value");
        _tagNameMap.put(TAG_FLASH_MODE, "Flash Mode");
        _tagNameMap.put(TAG_FLASH_DEVICE, "Flash Device");
        _tagNameMap.put(TAG_BRACKET, "Bracket");
        _tagNameMap.put(TAG_SENSOR_TEMPERATURE, "Sensor Temperature");
        _tagNameMap.put(TAG_LENS_TEMPERATURE, "Lens Temperature");
        _tagNameMap.put(TAG_LIGHT_CONDITION, "Light Condition");
        _tagNameMap.put(TAG_FOCUS_RANGE, "Focus Range");
        _tagNameMap.put(TAG_FOCUS_MODE, "Focus Mode");
        _tagNameMap.put(TAG_FOCUS_DISTANCE, "Focus Distance");
        _tagNameMap.put(TAG_ZOOM, "Zoom");
        _tagNameMap.put(TAG_MACRO_FOCUS, "Macro Focus");
        _tagNameMap.put(TAG_SHARPNESS, "Sharpness");
        _tagNameMap.put(TAG_FLASH_CHARGE_LEVEL, "Flash Charge Level");
        _tagNameMap.put(TAG_COLOUR_MATRIX, "Colour Matrix");
        _tagNameMap.put(TAG_BLACK_LEVEL, "Black Level");
        _tagNameMap.put(TAG_COLOR_TEMPERATURE_BG, "Color Temperature BG");
        _tagNameMap.put(TAG_COLOR_TEMPERATURE_RG, "Color Temperature RG");
        _tagNameMap.put(TAG_WB_MODE, "White Balance Mode");
        _tagNameMap.put(TAG_RED_BALANCE, "Red Balance");
        _tagNameMap.put(TAG_BLUE_BALANCE, "Blue Balance");
        _tagNameMap.put(TAG_COLOR_MATRIX_NUMBER, "Color Matrix Number");
        _tagNameMap.put(TAG_SERIAL_NUMBER_2, "Serial Number");
        _tagNameMap.put(TAG_EXTERNAL_FLASH_AE1_0, "External Flash AE1 0");
        _tagNameMap.put(TAG_EXTERNAL_FLASH_AE2_0, "External Flash AE2 0");
        _tagNameMap.put(TAG_INTERNAL_FLASH_AE1_0, "Internal Flash AE1 0");
        _tagNameMap.put(TAG_INTERNAL_FLASH_AE2_0, "Internal Flash AE2 0");
        _tagNameMap.put(TAG_EXTERNAL_FLASH_AE1, "External Flash AE1");
        _tagNameMap.put(TAG_EXTERNAL_FLASH_AE2, "External Flash AE2");
        _tagNameMap.put(TAG_INTERNAL_FLASH_AE1, "Internal Flash AE1");
        _tagNameMap.put(TAG_INTERNAL_FLASH_AE2, "Internal Flash AE2");
        _tagNameMap.put(TAG_FLASH_BIAS, "Flash Bias");
        _tagNameMap.put(TAG_INTERNAL_FLASH_TABLE, "Internal Flash Table");
        _tagNameMap.put(TAG_EXTERNAL_FLASH_G_VALUE, "External Flash G Value");
        _tagNameMap.put(TAG_EXTERNAL_FLASH_BOUNCE, "External Flash Bounce");
        _tagNameMap.put(TAG_EXTERNAL_FLASH_ZOOM, "External Flash Zoom");
        _tagNameMap.put(TAG_EXTERNAL_FLASH_MODE, "External Flash Mode");
        _tagNameMap.put(TAG_CONTRAST, "Contrast");
        _tagNameMap.put(TAG_SHARPNESS_FACTOR, "Sharpness Factor");
        _tagNameMap.put(TAG_COLOUR_CONTROL, "Colour Control");
        _tagNameMap.put(TAG_VALID_BITS, "Valid Bits");
        _tagNameMap.put(TAG_CORING_FILTER, "Coring Filter");
        _tagNameMap.put(TAG_OLYMPUS_IMAGE_WIDTH, "Olympus Image Width");
        _tagNameMap.put(TAG_OLYMPUS_IMAGE_HEIGHT, "Olympus Image Height");
        _tagNameMap.put(TAG_SCENE_DETECT, "Scene Detect");
        _tagNameMap.put(TAG_SCENE_AREA, "Scene Area");
        _tagNameMap.put(TAG_SCENE_DETECT_DATA, "Scene Detect Data");
        _tagNameMap.put(TAG_COMPRESSION_RATIO, "Compression Ratio");
        _tagNameMap.put(TAG_PREVIEW_IMAGE_VALID, "Preview Image Valid");
        _tagNameMap.put(TAG_PREVIEW_IMAGE_START, "Preview Image Start");
        _tagNameMap.put(TAG_PREVIEW_IMAGE_LENGTH, "Preview Image Length");
        _tagNameMap.put(TAG_AF_RESULT, "AF Result");
        _tagNameMap.put(TAG_CCD_SCAN_MODE, "CCD Scan Mode");
        _tagNameMap.put(TAG_NOISE_REDUCTION, "Noise Reduction");
        _tagNameMap.put(TAG_INFINITY_LENS_STEP, "Infinity Lens Step");
        _tagNameMap.put(TAG_NEAR_LENS_STEP, "Near Lens Step");
        _tagNameMap.put(TAG_LIGHT_VALUE_CENTER, "Light Value Center");
        _tagNameMap.put(TAG_LIGHT_VALUE_PERIPHERY, "Light Value Periphery");
        _tagNameMap.put(TAG_FIELD_COUNT, "Field Count");
        _tagNameMap.put(TAG_EQUIPMENT, "Equipment");
        _tagNameMap.put(TAG_CAMERA_SETTINGS, "Camera Settings");
        _tagNameMap.put(TAG_RAW_DEVELOPMENT, "Raw Development");
        _tagNameMap.put(TAG_RAW_DEVELOPMENT_2, "Raw Development 2");
        _tagNameMap.put(TAG_IMAGE_PROCESSING, "Image Processing");
        _tagNameMap.put(TAG_FOCUS_INFO, "Focus Info");
        _tagNameMap.put(TAG_RAW_INFO, "Raw Info");
        _tagNameMap.put(TAG_MAIN_INFO, "Main Info");

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

    @Override
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

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    // <summary>
    // These values are currently decoded only for Olympus models.  Models with
    // Olympus-style maker notes from other brands such as Acer, BenQ, Hitachi, HP,
    // Premier, Konica-Minolta, Maginon, Ricoh, Rollei, SeaLife, Sony, Supra,
    // Vivitar are not listed.
    // </summary>
    // <remarks>
    // Converted from Exiftool version 10.33 created by Phil Harvey
    // http://www.sno.phy.queensu.ca/~phil/exiftool/
    // lib\Image\ExifTool\Olympus.pm
    // </remarks>
    public static final HashMap<String, String> OlympusCameraTypes = new HashMap<String, String>();

    static {
        OlympusCameraTypes.put("D4028", "X-2,C-50Z");
        OlympusCameraTypes.put("D4029", "E-20,E-20N,E-20P");
        OlympusCameraTypes.put("D4034", "C720UZ");
        OlympusCameraTypes.put("D4040", "E-1");
        OlympusCameraTypes.put("D4041", "E-300");
        OlympusCameraTypes.put("D4083", "C2Z,D520Z,C220Z");
        OlympusCameraTypes.put("D4106", "u20D,S400D,u400D");
        OlympusCameraTypes.put("D4120", "X-1");
        OlympusCameraTypes.put("D4122", "u10D,S300D,u300D");
        OlympusCameraTypes.put("D4125", "AZ-1");
        OlympusCameraTypes.put("D4141", "C150,D390");
        OlympusCameraTypes.put("D4193", "C-5000Z");
        OlympusCameraTypes.put("D4194", "X-3,C-60Z");
        OlympusCameraTypes.put("D4199", "u30D,S410D,u410D");
        OlympusCameraTypes.put("D4205", "X450,D535Z,C370Z");
        OlympusCameraTypes.put("D4210", "C160,D395");
        OlympusCameraTypes.put("D4211", "C725UZ");
        OlympusCameraTypes.put("D4213", "FerrariMODEL2003");
        OlympusCameraTypes.put("D4216", "u15D");
        OlympusCameraTypes.put("D4217", "u25D");
        OlympusCameraTypes.put("D4220", "u-miniD,Stylus V");
        OlympusCameraTypes.put("D4221", "u40D,S500,uD500");
        OlympusCameraTypes.put("D4231", "FerrariMODEL2004");
        OlympusCameraTypes.put("D4240", "X500,D590Z,C470Z");
        OlympusCameraTypes.put("D4244", "uD800,S800");
        OlympusCameraTypes.put("D4256", "u720SW,S720SW");
        OlympusCameraTypes.put("D4261", "X600,D630,FE5500");
        OlympusCameraTypes.put("D4262", "uD600,S600");
        OlympusCameraTypes.put("D4301", "u810/S810"); // (yes, "/".  Olympus is not consistent in the notation)
        OlympusCameraTypes.put("D4302", "u710,S710");
        OlympusCameraTypes.put("D4303", "u700,S700");
        OlympusCameraTypes.put("D4304", "FE100,X710");
        OlympusCameraTypes.put("D4305", "FE110,X705");
        OlympusCameraTypes.put("D4310", "FE-130,X-720");
        OlympusCameraTypes.put("D4311", "FE-140,X-725");
        OlympusCameraTypes.put("D4312", "FE150,X730");
        OlympusCameraTypes.put("D4313", "FE160,X735");
        OlympusCameraTypes.put("D4314", "u740,S740");
        OlympusCameraTypes.put("D4315", "u750,S750");
        OlympusCameraTypes.put("D4316", "u730/S730");
        OlympusCameraTypes.put("D4317", "FE115,X715");
        OlympusCameraTypes.put("D4321", "SP550UZ");
        OlympusCameraTypes.put("D4322", "SP510UZ");
        OlympusCameraTypes.put("D4324", "FE170,X760");
        OlympusCameraTypes.put("D4326", "FE200");
        OlympusCameraTypes.put("D4327", "FE190/X750"); // (also SX876)
        OlympusCameraTypes.put("D4328", "u760,S760");
        OlympusCameraTypes.put("D4330", "FE180/X745"); // (also SX875)
        OlympusCameraTypes.put("D4331", "u1000/S1000");
        OlympusCameraTypes.put("D4332", "u770SW,S770SW");
        OlympusCameraTypes.put("D4333", "FE240/X795");
        OlympusCameraTypes.put("D4334", "FE210,X775");
        OlympusCameraTypes.put("D4336", "FE230/X790");
        OlympusCameraTypes.put("D4337", "FE220,X785");
        OlympusCameraTypes.put("D4338", "u725SW,S725SW");
        OlympusCameraTypes.put("D4339", "FE250/X800");
        OlympusCameraTypes.put("D4341", "u780,S780");
        OlympusCameraTypes.put("D4343", "u790SW,S790SW");
        OlympusCameraTypes.put("D4344", "u1020,S1020");
        OlympusCameraTypes.put("D4346", "FE15,X10");
        OlympusCameraTypes.put("D4348", "FE280,X820,C520");
        OlympusCameraTypes.put("D4349", "FE300,X830");
        OlympusCameraTypes.put("D4350", "u820,S820");
        OlympusCameraTypes.put("D4351", "u1200,S1200");
        OlympusCameraTypes.put("D4352", "FE270,X815,C510");
        OlympusCameraTypes.put("D4353", "u795SW,S795SW");
        OlympusCameraTypes.put("D4354", "u1030SW,S1030SW");
        OlympusCameraTypes.put("D4355", "SP560UZ");
        OlympusCameraTypes.put("D4356", "u1010,S1010");
        OlympusCameraTypes.put("D4357", "u830,S830");
        OlympusCameraTypes.put("D4359", "u840,S840");
        OlympusCameraTypes.put("D4360", "FE350WIDE,X865");
        OlympusCameraTypes.put("D4361", "u850SW,S850SW");
        OlympusCameraTypes.put("D4362", "FE340,X855,C560");
        OlympusCameraTypes.put("D4363", "FE320,X835,C540");
        OlympusCameraTypes.put("D4364", "SP570UZ");
        OlympusCameraTypes.put("D4366", "FE330,X845,C550");
        OlympusCameraTypes.put("D4368", "FE310,X840,C530");
        OlympusCameraTypes.put("D4370", "u1050SW,S1050SW");
        OlympusCameraTypes.put("D4371", "u1060,S1060");
        OlympusCameraTypes.put("D4372", "FE370,X880,C575");
        OlympusCameraTypes.put("D4374", "SP565UZ");
        OlympusCameraTypes.put("D4377", "u1040,S1040");
        OlympusCameraTypes.put("D4378", "FE360,X875,C570");
        OlympusCameraTypes.put("D4379", "FE20,X15,C25");
        OlympusCameraTypes.put("D4380", "uT6000,ST6000");
        OlympusCameraTypes.put("D4381", "uT8000,ST8000");
        OlympusCameraTypes.put("D4382", "u9000,S9000");
        OlympusCameraTypes.put("D4384", "SP590UZ");
        OlympusCameraTypes.put("D4385", "FE3010,X895");
        OlympusCameraTypes.put("D4386", "FE3000,X890");
        OlympusCameraTypes.put("D4387", "FE35,X30");
        OlympusCameraTypes.put("D4388", "u550WP,S550WP");
        OlympusCameraTypes.put("D4390", "FE5000,X905");
        OlympusCameraTypes.put("D4391", "u5000");
        OlympusCameraTypes.put("D4392", "u7000,S7000");
        OlympusCameraTypes.put("D4396", "FE5010,X915");
        OlympusCameraTypes.put("D4397", "FE25,X20");
        OlympusCameraTypes.put("D4398", "FE45,X40");
        OlympusCameraTypes.put("D4401", "XZ-1");
        OlympusCameraTypes.put("D4402", "uT6010,ST6010");
        OlympusCameraTypes.put("D4406", "u7010,S7010 / u7020,S7020");
        OlympusCameraTypes.put("D4407", "FE4010,X930");
        OlympusCameraTypes.put("D4408", "X560WP");
        OlympusCameraTypes.put("D4409", "FE26,X21");
        OlympusCameraTypes.put("D4410", "FE4000,X920,X925");
        OlympusCameraTypes.put("D4411", "FE46,X41,X42");
        OlympusCameraTypes.put("D4412", "FE5020,X935");
        OlympusCameraTypes.put("D4413", "uTough-3000");
        OlympusCameraTypes.put("D4414", "StylusTough-6020");
        OlympusCameraTypes.put("D4415", "StylusTough-8010");
        OlympusCameraTypes.put("D4417", "u5010,S5010");
        OlympusCameraTypes.put("D4418", "u7040,S7040");
        OlympusCameraTypes.put("D4419", "u9010,S9010");
        OlympusCameraTypes.put("D4423", "FE4040");
        OlympusCameraTypes.put("D4424", "FE47,X43");
        OlympusCameraTypes.put("D4426", "FE4030,X950");
        OlympusCameraTypes.put("D4428", "FE5030,X965,X960");
        OlympusCameraTypes.put("D4430", "u7030,S7030");
        OlympusCameraTypes.put("D4432", "SP600UZ");
        OlympusCameraTypes.put("D4434", "SP800UZ");
        OlympusCameraTypes.put("D4439", "FE4020,X940");
        OlympusCameraTypes.put("D4442", "FE5035");
        OlympusCameraTypes.put("D4448", "FE4050,X970");
        OlympusCameraTypes.put("D4450", "FE5050,X985");
        OlympusCameraTypes.put("D4454", "u-7050");
        OlympusCameraTypes.put("D4464", "T10,X27");
        OlympusCameraTypes.put("D4470", "FE5040,X980");
        OlympusCameraTypes.put("D4472", "TG-310");
        OlympusCameraTypes.put("D4474", "TG-610");
        OlympusCameraTypes.put("D4476", "TG-810");
        OlympusCameraTypes.put("D4478", "VG145,VG140,D715");
        OlympusCameraTypes.put("D4479", "VG130,D710");
        OlympusCameraTypes.put("D4480", "VG120,D705");
        OlympusCameraTypes.put("D4482", "VR310,D720");
        OlympusCameraTypes.put("D4484", "VR320,D725");
        OlympusCameraTypes.put("D4486", "VR330,D730");
        OlympusCameraTypes.put("D4488", "VG110,D700");
        OlympusCameraTypes.put("D4490", "SP-610UZ");
        OlympusCameraTypes.put("D4492", "SZ-10");
        OlympusCameraTypes.put("D4494", "SZ-20");
        OlympusCameraTypes.put("D4496", "SZ-30MR");
        OlympusCameraTypes.put("D4498", "SP-810UZ");
        OlympusCameraTypes.put("D4500", "SZ-11");
        OlympusCameraTypes.put("D4504", "TG-615");
        OlympusCameraTypes.put("D4508", "TG-620");
        OlympusCameraTypes.put("D4510", "TG-820");
        OlympusCameraTypes.put("D4512", "TG-1");
        OlympusCameraTypes.put("D4516", "SH-21");
        OlympusCameraTypes.put("D4519", "SZ-14");
        OlympusCameraTypes.put("D4520", "SZ-31MR");
        OlympusCameraTypes.put("D4521", "SH-25MR");
        OlympusCameraTypes.put("D4523", "SP-720UZ");
        OlympusCameraTypes.put("D4529", "VG170");
        OlympusCameraTypes.put("D4531", "XZ-2");
        OlympusCameraTypes.put("D4535", "SP-620UZ");
        OlympusCameraTypes.put("D4536", "TG-320");
        OlympusCameraTypes.put("D4537", "VR340,D750");
        OlympusCameraTypes.put("D4538", "VG160,X990,D745");
        OlympusCameraTypes.put("D4541", "SZ-12");
        OlympusCameraTypes.put("D4545", "VH410");
        OlympusCameraTypes.put("D4546", "XZ-10"); //IB
        OlympusCameraTypes.put("D4547", "TG-2");
        OlympusCameraTypes.put("D4548", "TG-830");
        OlympusCameraTypes.put("D4549", "TG-630");
        OlympusCameraTypes.put("D4550", "SH-50");
        OlympusCameraTypes.put("D4553", "SZ-16,DZ-105");
        OlympusCameraTypes.put("D4562", "SP-820UZ");
        OlympusCameraTypes.put("D4566", "SZ-15");
        OlympusCameraTypes.put("D4572", "STYLUS1");
        OlympusCameraTypes.put("D4574", "TG-3");
        OlympusCameraTypes.put("D4575", "TG-850");
        OlympusCameraTypes.put("D4579", "SP-100EE");
        OlympusCameraTypes.put("D4580", "SH-60");
        OlympusCameraTypes.put("D4581", "SH-1");
        OlympusCameraTypes.put("D4582", "TG-835");
        OlympusCameraTypes.put("D4585", "SH-2 / SH-3");
        OlympusCameraTypes.put("D4586", "TG-4");
        OlympusCameraTypes.put("D4587", "TG-860");
        OlympusCameraTypes.put("D4591", "TG-870");
        OlympusCameraTypes.put("D4809", "C2500L");
        OlympusCameraTypes.put("D4842", "E-10");
        OlympusCameraTypes.put("D4856", "C-1");
        OlympusCameraTypes.put("D4857", "C-1Z,D-150Z");
        OlympusCameraTypes.put("DCHC", "D500L");
        OlympusCameraTypes.put("DCHT", "D600L / D620L");
        OlympusCameraTypes.put("K0055", "AIR-A01");
        OlympusCameraTypes.put("S0003", "E-330");
        OlympusCameraTypes.put("S0004", "E-500");
        OlympusCameraTypes.put("S0009", "E-400");
        OlympusCameraTypes.put("S0010", "E-510");
        OlympusCameraTypes.put("S0011", "E-3");
        OlympusCameraTypes.put("S0013", "E-410");
        OlympusCameraTypes.put("S0016", "E-420");
        OlympusCameraTypes.put("S0017", "E-30");
        OlympusCameraTypes.put("S0018", "E-520");
        OlympusCameraTypes.put("S0019", "E-P1");
        OlympusCameraTypes.put("S0023", "E-620");
        OlympusCameraTypes.put("S0026", "E-P2");
        OlympusCameraTypes.put("S0027", "E-PL1");
        OlympusCameraTypes.put("S0029", "E-450");
        OlympusCameraTypes.put("S0030", "E-600");
        OlympusCameraTypes.put("S0032", "E-P3");
        OlympusCameraTypes.put("S0033", "E-5");
        OlympusCameraTypes.put("S0034", "E-PL2");
        OlympusCameraTypes.put("S0036", "E-M5");
        OlympusCameraTypes.put("S0038", "E-PL3");
        OlympusCameraTypes.put("S0039", "E-PM1");
        OlympusCameraTypes.put("S0040", "E-PL1s");
        OlympusCameraTypes.put("S0042", "E-PL5");
        OlympusCameraTypes.put("S0043", "E-PM2");
        OlympusCameraTypes.put("S0044", "E-P5");
        OlympusCameraTypes.put("S0045", "E-PL6");
        OlympusCameraTypes.put("S0046", "E-PL7"); //IB
        OlympusCameraTypes.put("S0047", "E-M1");
        OlympusCameraTypes.put("S0051", "E-M10");
        OlympusCameraTypes.put("S0052", "E-M5MarkII"); //IB
        OlympusCameraTypes.put("S0059", "E-M10MarkII");
        OlympusCameraTypes.put("S0061", "PEN-F"); //forum7005
        OlympusCameraTypes.put("S0065", "E-PL8");
        OlympusCameraTypes.put("S0067", "E-M1MarkII");
        OlympusCameraTypes.put("SR45", "D220");
        OlympusCameraTypes.put("SR55", "D320L");
        OlympusCameraTypes.put("SR83", "D340L");
        OlympusCameraTypes.put("SR85", "C830L,D340R");
        OlympusCameraTypes.put("SR852", "C860L,D360L");
        OlympusCameraTypes.put("SR872", "C900Z,D400Z");
        OlympusCameraTypes.put("SR874", "C960Z,D460Z");
        OlympusCameraTypes.put("SR951", "C2000Z");
        OlympusCameraTypes.put("SR952", "C21");
        OlympusCameraTypes.put("SR953", "C21T.commu");
        OlympusCameraTypes.put("SR954", "C2020Z");
        OlympusCameraTypes.put("SR955", "C990Z,D490Z");
        OlympusCameraTypes.put("SR956", "C211Z");
        OlympusCameraTypes.put("SR959", "C990ZS,D490Z");
        OlympusCameraTypes.put("SR95A", "C2100UZ");
        OlympusCameraTypes.put("SR971", "C100,D370");
        OlympusCameraTypes.put("SR973", "C2,D230");
        OlympusCameraTypes.put("SX151", "E100RS");
        OlympusCameraTypes.put("SX351", "C3000Z / C3030Z");
        OlympusCameraTypes.put("SX354", "C3040Z");
        OlympusCameraTypes.put("SX355", "C2040Z");
        OlympusCameraTypes.put("SX357", "C700UZ");
        OlympusCameraTypes.put("SX358", "C200Z,D510Z");
        OlympusCameraTypes.put("SX374", "C3100Z,C3020Z");
        OlympusCameraTypes.put("SX552", "C4040Z");
        OlympusCameraTypes.put("SX553", "C40Z,D40Z");
        OlympusCameraTypes.put("SX556", "C730UZ");
        OlympusCameraTypes.put("SX558", "C5050Z");
        OlympusCameraTypes.put("SX571", "C120,D380");
        OlympusCameraTypes.put("SX574", "C300Z,D550Z");
        OlympusCameraTypes.put("SX575", "C4100Z,C4000Z");
        OlympusCameraTypes.put("SX751", "X200,D560Z,C350Z");
        OlympusCameraTypes.put("SX752", "X300,D565Z,C450Z");
        OlympusCameraTypes.put("SX753", "C750UZ");
        OlympusCameraTypes.put("SX754", "C740UZ");
        OlympusCameraTypes.put("SX755", "C755UZ");
        OlympusCameraTypes.put("SX756", "C5060WZ");
        OlympusCameraTypes.put("SX757", "C8080WZ");
        OlympusCameraTypes.put("SX758", "X350,D575Z,C360Z");
        OlympusCameraTypes.put("SX759", "X400,D580Z,C460Z");
        OlympusCameraTypes.put("SX75A", "AZ-2ZOOM");
        OlympusCameraTypes.put("SX75B", "D595Z,C500Z");
        OlympusCameraTypes.put("SX75C", "X550,D545Z,C480Z");
        OlympusCameraTypes.put("SX75D", "IR-300");
        OlympusCameraTypes.put("SX75F", "C55Z,C5500Z");
        OlympusCameraTypes.put("SX75G", "C170,D425");
        OlympusCameraTypes.put("SX75J", "C180,D435");
        OlympusCameraTypes.put("SX771", "C760UZ");
        OlympusCameraTypes.put("SX772", "C770UZ");
        OlympusCameraTypes.put("SX773", "C745UZ");
        OlympusCameraTypes.put("SX774", "X250,D560Z,C350Z");
        OlympusCameraTypes.put("SX775", "X100,D540Z,C310Z");
        OlympusCameraTypes.put("SX776", "C460ZdelSol");
        OlympusCameraTypes.put("SX777", "C765UZ");
        OlympusCameraTypes.put("SX77A", "D555Z,C315Z");
        OlympusCameraTypes.put("SX851", "C7070WZ");
        OlympusCameraTypes.put("SX852", "C70Z,C7000Z");
        OlympusCameraTypes.put("SX853", "SP500UZ");
        OlympusCameraTypes.put("SX854", "SP310");
        OlympusCameraTypes.put("SX855", "SP350");
        OlympusCameraTypes.put("SX873", "SP320");
        OlympusCameraTypes.put("SX875", "FE180/X745"); // (also D4330)
        OlympusCameraTypes.put("SX876", "FE190/X750"); // (also D4327)

        //   other brands
        //    4MP9Q3", "Camera 4MP-9Q3'
        //    4MP9T2", "BenQ DC C420 / Camera 4MP-9T2'
        //    5MP9Q3", "Camera 5MP-9Q3" },
        //    5MP9X9", "Camera 5MP-9X9" },
        //   '5MP-9T'=> 'Camera 5MP-9T3" },
        //   '5MP-9Y'=> 'Camera 5MP-9Y2" },
        //   '6MP-9U'=> 'Camera 6MP-9U9" },
        //    7MP9Q3", "Camera 7MP-9Q3" },
        //   '8MP-9U'=> 'Camera 8MP-9U4" },
        //    CE5330", "Acer CE-5330" },
        //   'CP-853'=> 'Acer CP-8531" },
        //    CS5531", "Acer CS5531" },
        //    DC500 ", "SeaLife DC500" },
        //    DC7370", "Camera 7MP-9GA" },
        //    DC7371", "Camera 7MP-9GM" },
        //    DC7371", "Hitachi HDC-751E" },
        //    DC7375", "Hitachi HDC-763E / Rollei RCP-7330X / Ricoh Caplio RR770 / Vivitar ViviCam 7330" },
        //   'DC E63'=> 'BenQ DC E63+" },
        //   'DC P86'=> 'BenQ DC P860" },
        //    DS5340", "Maginon Performic S5 / Premier 5MP-9M7" },
        //    DS5341", "BenQ E53+ / Supra TCM X50 / Maginon X50 / Premier 5MP-9P8" },
        //    DS5346", "Premier 5MP-9Q2" },
        //    E500  ", "Konica Minolta DiMAGE E500" },
        //    MAGINO", "Maginon X60" },
        //    Mz60  ", "HP Photosmart Mz60" },
        //    Q3DIGI", "Camera 5MP-9Q3" },
        //    SLIMLI", "Supra Slimline X6" },
        //    V8300s", "Vivitar V8300s" },
    }
}

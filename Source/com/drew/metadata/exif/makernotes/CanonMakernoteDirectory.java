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
 * Describes tags specific to Canon cameras.
 *
 * Thanks to Bill Richards for his contribution to this makernote directory.
 *
 * Many tag definitions explained here: http://www.ozhiker.com/electronics/pjmt/jpeg_info/canon_mn.html
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class CanonMakernoteDirectory extends Directory
{
    // These TAG_*_ARRAY Exif tags map to arrays of int16 values which are split out into separate 'fake' tags.
    // When an attempt is made to set one of these on the directory, it is split and the corresponding offset added to the tagType.
    // So the resulting tag is the offset + the index into the array.

    private static final int TAG_CAMERA_SETTINGS_ARRAY          = 0x0001;
    private static final int TAG_FOCAL_LENGTH_ARRAY             = 0x0002;
//    private static final int TAG_FLASH_INFO                     = 0x0003;
    private static final int TAG_SHOT_INFO_ARRAY                = 0x0004;
    private static final int TAG_PANORAMA_ARRAY                 = 0x0005;

    public static final int TAG_CANON_IMAGE_TYPE                = 0x0006;
    public static final int TAG_CANON_FIRMWARE_VERSION          = 0x0007;
    public static final int TAG_CANON_IMAGE_NUMBER              = 0x0008;
    public static final int TAG_CANON_OWNER_NAME                = 0x0009;
    public static final int TAG_CANON_SERIAL_NUMBER             = 0x000C;
    public static final int TAG_CAMERA_INFO_ARRAY               = 0x000D; // depends upon model, so leave for now
    public static final int TAG_CANON_FILE_LENGTH               = 0x000E;
    public static final int TAG_CANON_CUSTOM_FUNCTIONS_ARRAY    = 0x000F; // depends upon model, so leave for now
    public static final int TAG_MODEL_ID                        = 0x0010;
    public static final int TAG_MOVIE_INFO_ARRAY                = 0x0011; // not currently decoded as not sure we see it in still images
    private static final int TAG_AF_INFO_ARRAY                  = 0x0012; // not currently decoded
    public static final int TAG_THUMBNAIL_IMAGE_VALID_AREA      = 0x0013;
    public static final int TAG_SERIAL_NUMBER_FORMAT            = 0x0015;
    public static final int TAG_SUPER_MACRO                     = 0x001A;
    public static final int TAG_DATE_STAMP_MODE                 = 0x001C;
    public static final int TAG_MY_COLORS                       = 0x001D;
    public static final int TAG_FIRMWARE_REVISION               = 0x001E;
    public static final int TAG_CATEGORIES                      = 0x0023;
    public static final int TAG_FACE_DETECT_ARRAY_1             = 0x0024;
    public static final int TAG_FACE_DETECT_ARRAY_2             = 0x0025;
    public static final int TAG_AF_INFO_ARRAY_2                 = 0x0026;
    public static final int TAG_IMAGE_UNIQUE_ID                 = 0x0028;

    public static final int TAG_RAW_DATA_OFFSET                 = 0x0081;
    public static final int TAG_ORIGINAL_DECISION_DATA_OFFSET   = 0x0083;

    public static final int TAG_CUSTOM_FUNCTIONS_1D_ARRAY       = 0x0090; // not currently decoded
    public static final int TAG_PERSONAL_FUNCTIONS_ARRAY        = 0x0091; // not currently decoded
    public static final int TAG_PERSONAL_FUNCTION_VALUES_ARRAY  = 0x0092; // not currently decoded
    public static final int TAG_FILE_INFO_ARRAY                 = 0x0093; // not currently decoded
    public static final int TAG_AF_POINTS_IN_FOCUS_1D           = 0x0094;
    public static final int TAG_LENS_MODEL                      = 0x0095;
    public static final int TAG_SERIAL_INFO_ARRAY               = 0x0096; // not currently decoded
    public static final int TAG_DUST_REMOVAL_DATA               = 0x0097;
    public static final int TAG_CROP_INFO                       = 0x0098; // not currently decoded
    public static final int TAG_CUSTOM_FUNCTIONS_ARRAY_2        = 0x0099; // not currently decoded
    public static final int TAG_ASPECT_INFO_ARRAY               = 0x009A; // not currently decoded
    public static final int TAG_PROCESSING_INFO_ARRAY           = 0x00A0; // not currently decoded
    public static final int TAG_TONE_CURVE_TABLE                = 0x00A1;
    public static final int TAG_SHARPNESS_TABLE                 = 0x00A2;
    public static final int TAG_SHARPNESS_FREQ_TABLE            = 0x00A3;
    public static final int TAG_WHITE_BALANCE_TABLE             = 0x00A4;
    public static final int TAG_COLOR_BALANCE_ARRAY             = 0x00A9; // not currently decoded
    public static final int TAG_MEASURED_COLOR_ARRAY            = 0x00AA; // not currently decoded
    public static final int TAG_COLOR_TEMPERATURE               = 0x00AE;
    public static final int TAG_CANON_FLAGS_ARRAY               = 0x00B0; // not currently decoded
    public static final int TAG_MODIFIED_INFO_ARRAY             = 0x00B1; // not currently decoded
    public static final int TAG_TONE_CURVE_MATCHING             = 0x00B2;
    public static final int TAG_WHITE_BALANCE_MATCHING          = 0x00B3;
    public static final int TAG_COLOR_SPACE                     = 0x00B4;
    public static final int TAG_PREVIEW_IMAGE_INFO_ARRAY        = 0x00B6; // not currently decoded
    public static final int TAG_VRD_OFFSET                      = 0x00D0;
    public static final int TAG_SENSOR_INFO_ARRAY               = 0x00E0; // not currently decoded

    public static final int TAG_COLOR_DATA_ARRAY_2              = 0x4001; // depends upon camera model, not currently decoded
    public static final int TAG_CRW_PARAM                       = 0x4002; // depends upon camera model, not currently decoded
    public static final int TAG_COLOR_INFO_ARRAY_2              = 0x4003; // not currently decoded
    public static final int TAG_BLACK_LEVEL                     = 0x4008; // not currently decoded
    public static final int TAG_CUSTOM_PICTURE_STYLE_FILE_NAME  = 0x4010;
    public static final int TAG_COLOR_INFO_ARRAY                = 0x4013; // not currently decoded
    public static final int TAG_VIGNETTING_CORRECTION_ARRAY_1   = 0x4015; // not currently decoded
    public static final int TAG_VIGNETTING_CORRECTION_ARRAY_2   = 0x4016; // not currently decoded
    public static final int TAG_LIGHTING_OPTIMIZER_ARRAY        = 0x4018; // not currently decoded
    public static final int TAG_LENS_INFO_ARRAY                 = 0x4019; // not currently decoded
    public static final int TAG_AMBIANCE_INFO_ARRAY             = 0x4020; // not currently decoded
    public static final int TAG_FILTER_INFO_ARRAY               = 0x4024; // not currently decoded

    public final static class CameraSettings
    {
        // These 'sub'-tag values have been created for consistency -- they don't exist within the exif segment
        private static final int OFFSET = 0xC100;

        /**
         * 1 = Macro
         * 2 = Normal
         */
        public static final int TAG_MACRO_MODE = OFFSET + 0x01;
        public static final int TAG_SELF_TIMER_DELAY = OFFSET + 0x02;
        /**
         * 2 = Normal
         * 3 = Fine
         * 5 = Superfine
         */
        public static final int TAG_QUALITY = OFFSET + 0x03;
        /**
         * 0 = Flash Not Fired
         * 1 = Auto
         * 2 = On
         * 3 = Red Eye Reduction
         * 4 = Slow Synchro
         * 5 = Auto + Red Eye Reduction
         * 6 = On + Red Eye Reduction
         * 16 = External Flash
         */
        public static final int TAG_FLASH_MODE = OFFSET + 0x04;
        /**
         * 0 = Single Frame or Timer Mode
         * 1 = Continuous
         */
        public static final int TAG_CONTINUOUS_DRIVE_MODE = OFFSET + 0x05;
        public static final int TAG_UNKNOWN_2 = OFFSET + 0x06;
        /**
         * 0 = One-Shot
         * 1 = AI Servo
         * 2 = AI Focus
         * 3 = Manual Focus
         * 4 = Single
         * 5 = Continuous
         * 6 = Manual Focus
         */
        public static final int TAG_FOCUS_MODE_1 = OFFSET + 0x07;
        public static final int TAG_UNKNOWN_3 = OFFSET + 0x08;
        public static final int TAG_RECORD_MODE = OFFSET + 0x09;
        /**
         * 0 = Large
         * 1 = Medium
         * 2 = Small
         */
        public static final int TAG_IMAGE_SIZE = OFFSET + 0x0A;
        /**
         * 0 = Full Auto
         * 1 = Manual
         * 2 = Landscape
         * 3 = Fast Shutter
         * 4 = Slow Shutter
         * 5 = Night
         * 6 = Black &amp; White
         * 7 = Sepia
         * 8 = Portrait
         * 9 = Sports
         * 10 = Macro / Close-Up
         * 11 = Pan Focus
         */
        public static final int TAG_EASY_SHOOTING_MODE = OFFSET + 0x0B;
        /**
         * 0 = No Digital Zoom
         * 1 = 2x
         * 2 = 4x
         */
        public static final int TAG_DIGITAL_ZOOM = OFFSET + 0x0C;
        /**
         * 0 = Normal
         * 1 = High
         * 65535 = Low
         */
        public static final int TAG_CONTRAST = OFFSET + 0x0D;
        /**
         * 0 = Normal
         * 1 = High
         * 65535 = Low
         */
        public static final int TAG_SATURATION = OFFSET + 0x0E;
        /**
         * 0 = Normal
         * 1 = High
         * 65535 = Low
         */
        public static final int TAG_SHARPNESS = OFFSET + 0x0F;
        /**
         * 0 = Check ISOSpeedRatings EXIF tag for ISO Speed
         * 15 = Auto ISO
         * 16 = ISO 50
         * 17 = ISO 100
         * 18 = ISO 200
         * 19 = ISO 400
         */
        public static final int TAG_ISO = OFFSET + 0x10;
        /**
         * 3 = Evaluative
         * 4 = Partial
         * 5 = Centre Weighted
         */
        public static final int TAG_METERING_MODE = OFFSET + 0x11;
        /**
         * 0 = Manual
         * 1 = Auto
         * 3 = Close-up (Macro)
         * 8 = Locked (Pan Mode)
         */
        public static final int TAG_FOCUS_TYPE = OFFSET + 0x12;
        /**
         * 12288 = None (Manual Focus)
         * 12289 = Auto Selected
         * 12290 = Right
         * 12291 = Centre
         * 12292 = Left
         */
        public static final int TAG_AF_POINT_SELECTED = OFFSET + 0x13;
        /**
         * 0 = Easy Shooting (See Easy Shooting Mode)
         * 1 = Program
         * 2 = Tv-Priority
         * 3 = Av-Priority
         * 4 = Manual
         * 5 = A-DEP
         */
        public static final int TAG_EXPOSURE_MODE = OFFSET + 0x14;
        public static final int TAG_UNKNOWN_7 = OFFSET + 0x15;
        public static final int TAG_LENS_TYPE = OFFSET + 0x16;
        public static final int TAG_LONG_FOCAL_LENGTH = OFFSET + 0x17;
        public static final int TAG_SHORT_FOCAL_LENGTH = OFFSET + 0x18;
        public static final int TAG_FOCAL_UNITS_PER_MM = OFFSET + 0x19;
        public static final int TAG_MAX_APERTURE = OFFSET + 0x1A;
        public static final int TAG_MIN_APERTURE = OFFSET + 0x1B;
        /**
         * 0 = Flash Did Not Fire
         * 1 = Flash Fired
         */
        public static final int TAG_FLASH_ACTIVITY = OFFSET + 0x1C;
        public static final int TAG_FLASH_DETAILS = OFFSET + 0x1D;
        public static final int TAG_FOCUS_CONTINUOUS = OFFSET + 0x1E;
        public static final int TAG_AE_SETTING = OFFSET + 0x1F;
        /**
         * 0 = Focus Mode: Single
         * 1 = Focus Mode: Continuous
         */
        public static final int TAG_FOCUS_MODE_2 = OFFSET + 0x20;

        public static final int TAG_DISPLAY_APERTURE = OFFSET + 0x21;
        public static final int TAG_ZOOM_SOURCE_WIDTH = OFFSET + 0x22;
        public static final int TAG_ZOOM_TARGET_WIDTH = OFFSET + 0x23;

        public static final int TAG_SPOT_METERING_MODE = OFFSET + 0x25;
        public static final int TAG_PHOTO_EFFECT = OFFSET + 0x26;
        public static final int TAG_MANUAL_FLASH_OUTPUT = OFFSET + 0x27;

        public static final int TAG_COLOR_TONE = OFFSET + 0x29;
        public static final int TAG_SRAW_QUALITY = OFFSET + 0x2D;
    }

    public final static class FocalLength
    {
        // These 'sub'-tag values have been created for consistency -- they don't exist within the exif segment

        private static final int OFFSET = 0xC200;

        /**
         * 0 = Auto
         * 1 = Sunny
         * 2 = Cloudy
         * 3 = Tungsten
         * 4 = Florescent
         * 5 = Flash
         * 6 = Custom
         */
        public static final int TAG_WHITE_BALANCE = OFFSET + 0x07;
        public static final int TAG_SEQUENCE_NUMBER = OFFSET + 0x09;
        public static final int TAG_AF_POINT_USED = OFFSET + 0x0E;
        /**
         * The value of this tag may be translated into a flash bias value, in EV.
         *
         * 0xffc0 = -2 EV
         * 0xffcc = -1.67 EV
         * 0xffd0 = -1.5 EV
         * 0xffd4 = -1.33 EV
         * 0xffe0 = -1 EV
         * 0xffec = -0.67 EV
         * 0xfff0 = -0.5 EV
         * 0xfff4 = -0.33 EV
         * 0x0000 = 0 EV
         * 0x000c = 0.33 EV
         * 0x0010 = 0.5 EV
         * 0x0014 = 0.67 EV
         * 0x0020 = 1 EV
         * 0x002c = 1.33 EV
         * 0x0030 = 1.5 EV
         * 0x0034 = 1.67 EV
         * 0x0040 = 2 EV
         */
        public static final int TAG_FLASH_BIAS = OFFSET + 0x0F;
        public static final int TAG_AUTO_EXPOSURE_BRACKETING = OFFSET + 0x10;
        public static final int TAG_AEB_BRACKET_VALUE = OFFSET + 0x11;
        public static final int TAG_SUBJECT_DISTANCE = OFFSET + 0x13;
    }

    public final static class ShotInfo
    {
        // These 'sub'-tag values have been created for consistency -- they don't exist within the exif segment

        private static final int OFFSET = 0xC400;

        public static final int TAG_AUTO_ISO = OFFSET + 1;
        public static final int TAG_BASE_ISO = OFFSET + 2;
        public static final int TAG_MEASURED_EV = OFFSET + 3;
        public static final int TAG_TARGET_APERTURE = OFFSET + 4;
        public static final int TAG_TARGET_EXPOSURE_TIME = OFFSET + 5;
        public static final int TAG_EXPOSURE_COMPENSATION = OFFSET + 6;
        public static final int TAG_WHITE_BALANCE = OFFSET + 7;
        public static final int TAG_SLOW_SHUTTER = OFFSET + 8;
        public static final int TAG_SEQUENCE_NUMBER = OFFSET + 9;
        public static final int TAG_OPTICAL_ZOOM_CODE = OFFSET + 10;
        public static final int TAG_CAMERA_TEMPERATURE = OFFSET + 12;
        public static final int TAG_FLASH_GUIDE_NUMBER = OFFSET + 13;
        public static final int TAG_AF_POINTS_IN_FOCUS = OFFSET + 14;
        public static final int TAG_FLASH_EXPOSURE_BRACKETING = OFFSET + 15;
        public static final int TAG_AUTO_EXPOSURE_BRACKETING = OFFSET + 16;
        public static final int TAG_AEB_BRACKET_VALUE = OFFSET + 17;
        public static final int TAG_CONTROL_MODE = OFFSET + 18;
        public static final int TAG_FOCUS_DISTANCE_UPPER = OFFSET + 19;
        public static final int TAG_FOCUS_DISTANCE_LOWER = OFFSET + 20;
        public static final int TAG_F_NUMBER = OFFSET + 21;
        public static final int TAG_EXPOSURE_TIME = OFFSET + 22;
        public static final int TAG_MEASURED_EV_2 = OFFSET + 23;
        public static final int TAG_BULB_DURATION = OFFSET + 24;
        public static final int TAG_CAMERA_TYPE = OFFSET + 26;
        public static final int TAG_AUTO_ROTATE = OFFSET + 27;
        public static final int TAG_ND_FILTER = OFFSET + 28;
        public static final int TAG_SELF_TIMER_2 = OFFSET + 29;
        public static final int TAG_FLASH_OUTPUT = OFFSET + 33;
    }

    public final static class Panorama
    {
        // These 'sub'-tag values have been created for consistency -- they don't exist within the exif segment

        private static final int OFFSET = 0xC500;

        public static final int TAG_PANORAMA_FRAME_NUMBER = OFFSET + 2;
        public static final int TAG_PANORAMA_DIRECTION = OFFSET + 5;
    }

    public final static class AFInfo
    {
        // These 'sub'-tag values have been created for consistency -- they don't exist within the exif segment

        private static final int OFFSET = 0xD200;

        public static final int TAG_NUM_AF_POINTS = OFFSET;
        public static final int TAG_VALID_AF_POINTS = OFFSET + 1;
        public static final int TAG_IMAGE_WIDTH = OFFSET + 2;
        public static final int TAG_IMAGE_HEIGHT = OFFSET + 3;
        public static final int TAG_AF_IMAGE_WIDTH = OFFSET + 4;
        public static final int TAG_AF_IMAGE_HEIGHT = OFFSET + 5;
        public static final int TAG_AF_AREA_WIDTH = OFFSET + 6;
        public static final int TAG_AF_AREA_HEIGHT = OFFSET + 7;
        public static final int TAG_AF_AREA_X_POSITIONS = OFFSET + 8;
        public static final int TAG_AF_AREA_Y_POSITIONS = OFFSET + 9;
        public static final int TAG_AF_POINTS_IN_FOCUS = OFFSET + 10;
        public static final int TAG_PRIMARY_AF_POINT_1 = OFFSET + 11;
        public static final int TAG_PRIMARY_AF_POINT_2 = OFFSET + 12; // not sure why there are two of these
    }

//    /**
//     * Long Exposure Noise Reduction
//     * 0 = Off
//     * 1 = On
//     */
//    public static final int TAG_CANON_CUSTOM_FUNCTION_LONG_EXPOSURE_NOISE_REDUCTION = 0xC301;
//
//    /**
//     * Shutter/Auto Exposure-lock buttons
//     * 0 = AF/AE lock
//     * 1 = AE lock/AF
//     * 2 = AF/AF lock
//     * 3 = AE+release/AE+AF
//     */
//    public static final int TAG_CANON_CUSTOM_FUNCTION_SHUTTER_AUTO_EXPOSURE_LOCK_BUTTONS = 0xC302;
//
//    /**
//     * Mirror lockup
//     * 0 = Disable
//     * 1 = Enable
//     */
//    public static final int TAG_CANON_CUSTOM_FUNCTION_MIRROR_LOCKUP = 0xC303;
//
//    /**
//     * Tv/Av and exposure level
//     * 0 = 1/2 stop
//     * 1 = 1/3 stop
//     */
//    public static final int TAG_CANON_CUSTOM_FUNCTION_TV_AV_AND_EXPOSURE_LEVEL = 0xC304;
//
//    /**
//     * AF-assist light
//     * 0 = On (Auto)
//     * 1 = Off
//     */
//    public static final int TAG_CANON_CUSTOM_FUNCTION_AF_ASSIST_LIGHT = 0xC305;
//
//    /**
//     * Shutter speed in Av mode
//     * 0 = Automatic
//     * 1 = 1/200 (fixed)
//     */
//    public static final int TAG_CANON_CUSTOM_FUNCTION_SHUTTER_SPEED_IN_AV_MODE = 0xC306;
//
//    /**
//     * Auto-Exposure Bracketing sequence/auto cancellation
//     * 0 = 0,-,+ / Enabled
//     * 1 = 0,-,+ / Disabled
//     * 2 = -,0,+ / Enabled
//     * 3 = -,0,+ / Disabled
//     */
//    public static final int TAG_CANON_CUSTOM_FUNCTION_BRACKETING = 0xC307;
//
//    /**
//     * Shutter Curtain Sync
//     * 0 = 1st Curtain Sync
//     * 1 = 2nd Curtain Sync
//     */
//    public static final int TAG_CANON_CUSTOM_FUNCTION_SHUTTER_CURTAIN_SYNC = 0xC308;
//
//    /**
//     * Lens Auto-Focus stop button Function Switch
//     * 0 = AF stop
//     * 1 = Operate AF
//     * 2 = Lock AE and start timer
//     */
//    public static final int TAG_CANON_CUSTOM_FUNCTION_AF_STOP = 0xC309;
//
//    /**
//     * Auto reduction of fill flash
//     * 0 = Enable
//     * 1 = Disable
//     */
//    public static final int TAG_CANON_CUSTOM_FUNCTION_FILL_FLASH_REDUCTION = 0xC30A;
//
//    /**
//     * Menu button return position
//     * 0 = Top
//     * 1 = Previous (volatile)
//     * 2 = Previous
//     */
//    public static final int TAG_CANON_CUSTOM_FUNCTION_MENU_BUTTON_RETURN = 0xC30B;
//
//    /**
//     * SET button function when shooting
//     * 0 = Not Assigned
//     * 1 = Change Quality
//     * 2 = Change ISO Speed
//     * 3 = Select Parameters
//     */
//    public static final int TAG_CANON_CUSTOM_FUNCTION_SET_BUTTON_FUNCTION = 0xC30C;
//
//    /**
//     * Sensor cleaning
//     * 0 = Disable
//     * 1 = Enable
//     */
//    public static final int TAG_CANON_CUSTOM_FUNCTION_SENSOR_CLEANING = 0xC30D;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_CANON_FIRMWARE_VERSION, "Firmware Version");
        _tagNameMap.put(TAG_CANON_IMAGE_NUMBER, "Image Number");
        _tagNameMap.put(TAG_CANON_IMAGE_TYPE, "Image Type");
        _tagNameMap.put(TAG_CANON_OWNER_NAME, "Owner Name");
        _tagNameMap.put(TAG_CANON_SERIAL_NUMBER, "Camera Serial Number");
        _tagNameMap.put(TAG_CAMERA_INFO_ARRAY, "Camera Info Array");
        _tagNameMap.put(TAG_CANON_FILE_LENGTH, "File Length");
        _tagNameMap.put(TAG_CANON_CUSTOM_FUNCTIONS_ARRAY, "Custom Functions");
        _tagNameMap.put(TAG_MODEL_ID, "Canon Model ID");
        _tagNameMap.put(TAG_MOVIE_INFO_ARRAY, "Movie Info Array");

        _tagNameMap.put(CameraSettings.TAG_AF_POINT_SELECTED, "AF Point Selected");
        _tagNameMap.put(CameraSettings.TAG_CONTINUOUS_DRIVE_MODE, "Continuous Drive Mode");
        _tagNameMap.put(CameraSettings.TAG_CONTRAST, "Contrast");
        _tagNameMap.put(CameraSettings.TAG_EASY_SHOOTING_MODE, "Easy Shooting Mode");
        _tagNameMap.put(CameraSettings.TAG_EXPOSURE_MODE, "Exposure Mode");
        _tagNameMap.put(CameraSettings.TAG_FLASH_DETAILS, "Flash Details");
        _tagNameMap.put(CameraSettings.TAG_FLASH_MODE, "Flash Mode");
        _tagNameMap.put(CameraSettings.TAG_FOCAL_UNITS_PER_MM, "Focal Units per mm");
        _tagNameMap.put(CameraSettings.TAG_FOCUS_MODE_1, "Focus Mode");
        _tagNameMap.put(CameraSettings.TAG_FOCUS_MODE_2, "Focus Mode");
        _tagNameMap.put(CameraSettings.TAG_IMAGE_SIZE, "Image Size");
        _tagNameMap.put(CameraSettings.TAG_ISO, "Iso");
        _tagNameMap.put(CameraSettings.TAG_LONG_FOCAL_LENGTH, "Long Focal Length");
        _tagNameMap.put(CameraSettings.TAG_MACRO_MODE, "Macro Mode");
        _tagNameMap.put(CameraSettings.TAG_METERING_MODE, "Metering Mode");
        _tagNameMap.put(CameraSettings.TAG_SATURATION, "Saturation");
        _tagNameMap.put(CameraSettings.TAG_SELF_TIMER_DELAY, "Self Timer Delay");
        _tagNameMap.put(CameraSettings.TAG_SHARPNESS, "Sharpness");
        _tagNameMap.put(CameraSettings.TAG_SHORT_FOCAL_LENGTH, "Short Focal Length");
        _tagNameMap.put(CameraSettings.TAG_QUALITY, "Quality");
        _tagNameMap.put(CameraSettings.TAG_UNKNOWN_2, "Unknown Camera Setting 2");
        _tagNameMap.put(CameraSettings.TAG_UNKNOWN_3, "Unknown Camera Setting 3");
        _tagNameMap.put(CameraSettings.TAG_RECORD_MODE, "Record Mode");
        _tagNameMap.put(CameraSettings.TAG_DIGITAL_ZOOM, "Digital Zoom");
        _tagNameMap.put(CameraSettings.TAG_FOCUS_TYPE, "Focus Type");
        _tagNameMap.put(CameraSettings.TAG_UNKNOWN_7, "Unknown Camera Setting 7");
        _tagNameMap.put(CameraSettings.TAG_LENS_TYPE, "Lens Type");
        _tagNameMap.put(CameraSettings.TAG_MAX_APERTURE, "Max Aperture");
        _tagNameMap.put(CameraSettings.TAG_MIN_APERTURE, "Min Aperture");
        _tagNameMap.put(CameraSettings.TAG_FLASH_ACTIVITY, "Flash Activity");
        _tagNameMap.put(CameraSettings.TAG_FOCUS_CONTINUOUS, "Focus Continuous");
        _tagNameMap.put(CameraSettings.TAG_AE_SETTING, "AE Setting");
        _tagNameMap.put(CameraSettings.TAG_DISPLAY_APERTURE, "Display Aperture");
        _tagNameMap.put(CameraSettings.TAG_ZOOM_SOURCE_WIDTH, "Zoom Source Width");
        _tagNameMap.put(CameraSettings.TAG_ZOOM_TARGET_WIDTH, "Zoom Target Width");
        _tagNameMap.put(CameraSettings.TAG_SPOT_METERING_MODE, "Spot Metering Mode");
        _tagNameMap.put(CameraSettings.TAG_PHOTO_EFFECT, "Photo Effect");
        _tagNameMap.put(CameraSettings.TAG_MANUAL_FLASH_OUTPUT, "Manual Flash Output");
        _tagNameMap.put(CameraSettings.TAG_COLOR_TONE, "Color Tone");
        _tagNameMap.put(CameraSettings.TAG_SRAW_QUALITY, "SRAW Quality");

        _tagNameMap.put(FocalLength.TAG_WHITE_BALANCE, "White Balance");
        _tagNameMap.put(FocalLength.TAG_SEQUENCE_NUMBER, "Sequence Number");
        _tagNameMap.put(FocalLength.TAG_AF_POINT_USED, "AF Point Used");
        _tagNameMap.put(FocalLength.TAG_FLASH_BIAS, "Flash Bias");
        _tagNameMap.put(FocalLength.TAG_AUTO_EXPOSURE_BRACKETING, "Auto Exposure Bracketing");
        _tagNameMap.put(FocalLength.TAG_AEB_BRACKET_VALUE, "AEB Bracket Value");
        _tagNameMap.put(FocalLength.TAG_SUBJECT_DISTANCE, "Subject Distance");

        _tagNameMap.put(ShotInfo.TAG_AUTO_ISO, "Auto ISO");
        _tagNameMap.put(ShotInfo.TAG_BASE_ISO, "Base ISO");
        _tagNameMap.put(ShotInfo.TAG_MEASURED_EV, "Measured EV");
        _tagNameMap.put(ShotInfo.TAG_TARGET_APERTURE, "Target Aperture");
        _tagNameMap.put(ShotInfo.TAG_TARGET_EXPOSURE_TIME, "Target Exposure Time");
        _tagNameMap.put(ShotInfo.TAG_EXPOSURE_COMPENSATION, "Exposure Compensation");
        _tagNameMap.put(ShotInfo.TAG_WHITE_BALANCE, "White Balance");
        _tagNameMap.put(ShotInfo.TAG_SLOW_SHUTTER, "Slow Shutter");
        _tagNameMap.put(ShotInfo.TAG_SEQUENCE_NUMBER, "Sequence Number");
        _tagNameMap.put(ShotInfo.TAG_OPTICAL_ZOOM_CODE, "Optical Zoom Code");
        _tagNameMap.put(ShotInfo.TAG_CAMERA_TEMPERATURE, "Camera Temperature");
        _tagNameMap.put(ShotInfo.TAG_FLASH_GUIDE_NUMBER, "Flash Guide Number");
        _tagNameMap.put(ShotInfo.TAG_AF_POINTS_IN_FOCUS, "AF Points in Focus");
        _tagNameMap.put(ShotInfo.TAG_FLASH_EXPOSURE_BRACKETING, "Flash Exposure Compensation");
        _tagNameMap.put(ShotInfo.TAG_AUTO_EXPOSURE_BRACKETING, "Auto Exposure Bracketing");
        _tagNameMap.put(ShotInfo.TAG_AEB_BRACKET_VALUE, "AEB Bracket Value");
        _tagNameMap.put(ShotInfo.TAG_CONTROL_MODE, "Control Mode");
        _tagNameMap.put(ShotInfo.TAG_FOCUS_DISTANCE_UPPER, "Focus Distance Upper");
        _tagNameMap.put(ShotInfo.TAG_FOCUS_DISTANCE_LOWER, "Focus Distance Lower");
        _tagNameMap.put(ShotInfo.TAG_F_NUMBER, "F Number");
        _tagNameMap.put(ShotInfo.TAG_EXPOSURE_TIME, "Exposure Time");
        _tagNameMap.put(ShotInfo.TAG_MEASURED_EV_2, "Measured EV 2");
        _tagNameMap.put(ShotInfo.TAG_BULB_DURATION, "Bulb Duration");
        _tagNameMap.put(ShotInfo.TAG_CAMERA_TYPE, "Camera Type");
        _tagNameMap.put(ShotInfo.TAG_AUTO_ROTATE, "Auto Rotate");
        _tagNameMap.put(ShotInfo.TAG_ND_FILTER, "ND Filter");
        _tagNameMap.put(ShotInfo.TAG_SELF_TIMER_2, "Self Timer 2");
        _tagNameMap.put(ShotInfo.TAG_FLASH_OUTPUT, "Flash Output");

        _tagNameMap.put(Panorama.TAG_PANORAMA_FRAME_NUMBER, "Panorama Frame Number");
        _tagNameMap.put(Panorama.TAG_PANORAMA_DIRECTION, "Panorama Direction");

        _tagNameMap.put(AFInfo.TAG_NUM_AF_POINTS, "AF Point Count");
        _tagNameMap.put(AFInfo.TAG_VALID_AF_POINTS, "Valid AF Point Count");
        _tagNameMap.put(AFInfo.TAG_IMAGE_WIDTH, "Image Width");
        _tagNameMap.put(AFInfo.TAG_IMAGE_HEIGHT, "Image Height");
        _tagNameMap.put(AFInfo.TAG_AF_IMAGE_WIDTH, "AF Image Width");
        _tagNameMap.put(AFInfo.TAG_AF_IMAGE_HEIGHT, "AF Image Height");
        _tagNameMap.put(AFInfo.TAG_AF_AREA_WIDTH, "AF Area Width");
        _tagNameMap.put(AFInfo.TAG_AF_AREA_HEIGHT, "AF Area Height");
        _tagNameMap.put(AFInfo.TAG_AF_AREA_X_POSITIONS, "AF Area X Positions");
        _tagNameMap.put(AFInfo.TAG_AF_AREA_Y_POSITIONS, "AF Area Y Positions");
        _tagNameMap.put(AFInfo.TAG_AF_POINTS_IN_FOCUS, "AF Points in Focus");
        _tagNameMap.put(AFInfo.TAG_PRIMARY_AF_POINT_1, "Primary AF Point 1");
        _tagNameMap.put(AFInfo.TAG_PRIMARY_AF_POINT_2, "Primary AF Point 2");

//        _tagNameMap.put(TAG_CANON_CUSTOM_FUNCTION_LONG_EXPOSURE_NOISE_REDUCTION, "Long Exposure Noise Reduction");
//        _tagNameMap.put(TAG_CANON_CUSTOM_FUNCTION_SHUTTER_AUTO_EXPOSURE_LOCK_BUTTONS, "Shutter/Auto Exposure-lock Buttons");
//        _tagNameMap.put(TAG_CANON_CUSTOM_FUNCTION_MIRROR_LOCKUP, "Mirror Lockup");
//        _tagNameMap.put(TAG_CANON_CUSTOM_FUNCTION_TV_AV_AND_EXPOSURE_LEVEL, "Tv/Av And Exposure Level");
//        _tagNameMap.put(TAG_CANON_CUSTOM_FUNCTION_AF_ASSIST_LIGHT, "AF-Assist Light");
//        _tagNameMap.put(TAG_CANON_CUSTOM_FUNCTION_SHUTTER_SPEED_IN_AV_MODE, "Shutter Speed in Av Mode");
//        _tagNameMap.put(TAG_CANON_CUSTOM_FUNCTION_BRACKETING, "Auto-Exposure Bracketing Sequence/Auto Cancellation");
//        _tagNameMap.put(TAG_CANON_CUSTOM_FUNCTION_SHUTTER_CURTAIN_SYNC, "Shutter Curtain Sync");
//        _tagNameMap.put(TAG_CANON_CUSTOM_FUNCTION_AF_STOP, "Lens Auto-Focus Stop Button Function Switch");
//        _tagNameMap.put(TAG_CANON_CUSTOM_FUNCTION_FILL_FLASH_REDUCTION, "Auto Reduction of Fill Flash");
//        _tagNameMap.put(TAG_CANON_CUSTOM_FUNCTION_MENU_BUTTON_RETURN, "Menu Button Return Position");
//        _tagNameMap.put(TAG_CANON_CUSTOM_FUNCTION_SET_BUTTON_FUNCTION, "SET Button Function When Shooting");
//        _tagNameMap.put(TAG_CANON_CUSTOM_FUNCTION_SENSOR_CLEANING, "Sensor Cleaning");

        _tagNameMap.put(TAG_THUMBNAIL_IMAGE_VALID_AREA, "Thumbnail Image Valid Area");
        _tagNameMap.put(TAG_SERIAL_NUMBER_FORMAT, "Serial Number Format");
        _tagNameMap.put(TAG_SUPER_MACRO, "Super Macro");
        _tagNameMap.put(TAG_DATE_STAMP_MODE, "Date Stamp Mode");
        _tagNameMap.put(TAG_MY_COLORS, "My Colors");
        _tagNameMap.put(TAG_FIRMWARE_REVISION, "Firmware Revision");
        _tagNameMap.put(TAG_CATEGORIES, "Categories");
        _tagNameMap.put(TAG_FACE_DETECT_ARRAY_1, "Face Detect Array 1");
        _tagNameMap.put(TAG_FACE_DETECT_ARRAY_2, "Face Detect Array 2");
        _tagNameMap.put(TAG_AF_INFO_ARRAY_2, "AF Info Array 2");
        _tagNameMap.put(TAG_IMAGE_UNIQUE_ID, "Image Unique ID");
        _tagNameMap.put(TAG_RAW_DATA_OFFSET, "Raw Data Offset");
        _tagNameMap.put(TAG_ORIGINAL_DECISION_DATA_OFFSET, "Original Decision Data Offset");
        _tagNameMap.put(TAG_CUSTOM_FUNCTIONS_1D_ARRAY, "Custom Functions (1D) Array");
        _tagNameMap.put(TAG_PERSONAL_FUNCTIONS_ARRAY, "Personal Functions Array");
        _tagNameMap.put(TAG_PERSONAL_FUNCTION_VALUES_ARRAY, "Personal Function Values Array");
        _tagNameMap.put(TAG_FILE_INFO_ARRAY, "File Info Array");
        _tagNameMap.put(TAG_AF_POINTS_IN_FOCUS_1D, "AF Points in Focus (1D)");
        _tagNameMap.put(TAG_LENS_MODEL, "Lens Model");
        _tagNameMap.put(TAG_SERIAL_INFO_ARRAY, "Serial Info Array");
        _tagNameMap.put(TAG_DUST_REMOVAL_DATA, "Dust Removal Data");
        _tagNameMap.put(TAG_CROP_INFO, "Crop Info");
        _tagNameMap.put(TAG_CUSTOM_FUNCTIONS_ARRAY_2, "Custom Functions Array 2");
        _tagNameMap.put(TAG_ASPECT_INFO_ARRAY, "Aspect Information Array");
        _tagNameMap.put(TAG_PROCESSING_INFO_ARRAY, "Processing Information Array");
        _tagNameMap.put(TAG_TONE_CURVE_TABLE, "Tone Curve Table");
        _tagNameMap.put(TAG_SHARPNESS_TABLE, "Sharpness Table");
        _tagNameMap.put(TAG_SHARPNESS_FREQ_TABLE, "Sharpness Frequency Table");
        _tagNameMap.put(TAG_WHITE_BALANCE_TABLE, "White Balance Table");
        _tagNameMap.put(TAG_COLOR_BALANCE_ARRAY, "Color Balance Array");
        _tagNameMap.put(TAG_MEASURED_COLOR_ARRAY, "Measured Color Array");
        _tagNameMap.put(TAG_COLOR_TEMPERATURE, "Color Temperature");
        _tagNameMap.put(TAG_CANON_FLAGS_ARRAY, "Canon Flags Array");
        _tagNameMap.put(TAG_MODIFIED_INFO_ARRAY, "Modified Information Array");
        _tagNameMap.put(TAG_TONE_CURVE_MATCHING, "Tone Curve Matching");
        _tagNameMap.put(TAG_WHITE_BALANCE_MATCHING, "White Balance Matching");
        _tagNameMap.put(TAG_COLOR_SPACE, "Color Space");
        _tagNameMap.put(TAG_PREVIEW_IMAGE_INFO_ARRAY, "Preview Image Info Array");
        _tagNameMap.put(TAG_VRD_OFFSET, "VRD Offset");
        _tagNameMap.put(TAG_SENSOR_INFO_ARRAY, "Sensor Information Array");
        _tagNameMap.put(TAG_COLOR_DATA_ARRAY_2, "Color Data Array 1");
        _tagNameMap.put(TAG_CRW_PARAM, "CRW Parameters");
        _tagNameMap.put(TAG_COLOR_INFO_ARRAY_2, "Color Data Array 2");
        _tagNameMap.put(TAG_BLACK_LEVEL, "Black Level");
        _tagNameMap.put(TAG_CUSTOM_PICTURE_STYLE_FILE_NAME, "Custom Picture Style File Name");
        _tagNameMap.put(TAG_COLOR_INFO_ARRAY, "Color Info Array");
        _tagNameMap.put(TAG_VIGNETTING_CORRECTION_ARRAY_1, "Vignetting Correction Array 1");
        _tagNameMap.put(TAG_VIGNETTING_CORRECTION_ARRAY_2, "Vignetting Correction Array 2");
        _tagNameMap.put(TAG_LIGHTING_OPTIMIZER_ARRAY, "Lighting Optimizer Array");
        _tagNameMap.put(TAG_LENS_INFO_ARRAY, "Lens Info Array");
        _tagNameMap.put(TAG_AMBIANCE_INFO_ARRAY, "Ambiance Info Array");
        _tagNameMap.put(TAG_FILTER_INFO_ARRAY, "Filter Info Array");
    }

    public CanonMakernoteDirectory()
    {
        this.setDescriptor(new CanonMakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Canon Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    @Override
    public void setObjectArray(int tagType, @NotNull Object array)
    {
        // TODO is there some way to drop out 'null' or 'zero' values that are present in the array to reduce the noise?

        if (!(array instanceof int[])) {
            // no special handling...
            super.setObjectArray(tagType, array);
            return;
        }

        // Certain Canon tags contain arrays of values that we split into 'fake' tags as each
        // index in the array has its own meaning and decoding.
        // Pick those tags out here and throw away the original array.
        // Otherwise just add as usual.
        switch (tagType) {
            case TAG_CAMERA_SETTINGS_ARRAY: {
                int[] ints = (int[])array;
                for (int i = 0; i < ints.length; i++)
                    setInt(CameraSettings.OFFSET + i, ints[i]);
                break;
            }
            case TAG_FOCAL_LENGTH_ARRAY: {
                int[] ints = (int[])array;
                for (int i = 0; i < ints.length; i++)
                    setInt(FocalLength.OFFSET + i, ints[i]);
                break;
            }
            case TAG_SHOT_INFO_ARRAY: {
                int[] ints = (int[])array;
                for (int i = 0; i < ints.length; i++)
                    setInt(ShotInfo.OFFSET + i, ints[i]);
                break;
            }
            case TAG_PANORAMA_ARRAY: {
                int[] ints = (int[])array;
                for (int i = 0; i < ints.length; i++)
                    setInt(Panorama.OFFSET + i, ints[i]);
                break;
            }
            // TODO the interpretation of the custom functions tag depends upon the camera model
//            case TAG_CANON_CUSTOM_FUNCTIONS_ARRAY:
//                int subTagTypeBase = 0xC300;
//                // we intentionally skip the first array member
//                for (int i = 1; i < ints.length; i++)
//                    setInt(subTagTypeBase + i + 1, ints[i] & 0x0F);
//                break;
            case TAG_AF_INFO_ARRAY: {
                // Notes from Exiftool 10.10 by Phil Harvey, lib\Image\Exiftool\Canon.pm:
                // Auto-focus information used by many older Canon models. The values in this
                // record are sequential, and some have variable sizes based on the value of
                // numafpoints (which may be 1,5,7,9,15,45, or 53). The AFArea coordinates are
                // given in a system where the image has dimensions given by AFImageWidth and
                // AFImageHeight, and 0,0 is the image center. The direction of the Y axis
                // depends on the camera model, with positive Y upwards for EOS models, but
                // apparently downwards for PowerShot models.

                // AFInfo is another array with 'fake' tags. The first int of the array contains
                // the number of AF points. Iterate through the array one byte at a time, generally
                // assuming one byte corresponds to one tag UNLESS certain tag numbers are encountered.
                // For these, read specific subsequent bytes from the array based on the tag type. The
                // number of bytes read can vary.

                int[] values = (int[])array;
                int numafpoints = values[0];
                int tagnumber = 0;
                for (int i = 0; i < values.length; i++)
                {
                    // These two tags store 'numafpoints' bytes of data in the array
                    if (AFInfo.OFFSET + tagnumber == AFInfo.TAG_AF_AREA_X_POSITIONS ||
                        AFInfo.OFFSET + tagnumber == AFInfo.TAG_AF_AREA_Y_POSITIONS)
                    {
                        // There could be incorrect data in the array, so boundary check
                        if (values.length - 1 >= (i + numafpoints))
                        {
                            short[] areaPositions = new short[numafpoints];
                            for (int j = 0; j < areaPositions.length; j++)
                                areaPositions[j] = (short)values[i + j];

                            super.setObjectArray(AFInfo.OFFSET + tagnumber, areaPositions);
                        }
                        i += numafpoints - 1;   // assume these bytes are processed and skip
                    }
                    else if (AFInfo.OFFSET + tagnumber == AFInfo.TAG_AF_POINTS_IN_FOCUS)
                    {
                        short[] pointsInFocus = new short[((numafpoints + 15) / 16)];

                        // There could be incorrect data in the array, so boundary check
                        if (values.length - 1 >= (i + pointsInFocus.length))
                        {
                            for (int j = 0; j < pointsInFocus.length; j++)
                                pointsInFocus[j] = (short)values[i + j];

                            super.setObjectArray(AFInfo.OFFSET + tagnumber, pointsInFocus);
                        }
                        i += pointsInFocus.length - 1;  // assume these bytes are processed and skip
                    }
                    else
                        super.setObjectArray(AFInfo.OFFSET + tagnumber, values[i]);
                    tagnumber++;
                }
                break;
            }
            default: {
                // no special handling...
                super.setObjectArray(tagType, array);
                break;
            }
        }
    }
}

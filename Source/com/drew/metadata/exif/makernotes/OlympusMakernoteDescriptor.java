/*
 * Copyright 2002-2013 Drew Noakes
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
package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.util.GregorianCalendar;

import static com.drew.metadata.exif.makernotes.OlympusMakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link OlympusMakernoteDirectory}.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class OlympusMakernoteDescriptor extends TagDescriptor<OlympusMakernoteDirectory>
{
    // TODO extend support for some offset-encoded byte[] tags: http://www.ozhiker.com/electronics/pjmt/jpeg_info/olympus_mn.html

    public OlympusMakernoteDescriptor(@NotNull OlympusMakernoteDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_MAKERNOTE_VERSION:
                return getMakernoteVersionDescription();
            case TAG_COLOUR_MODE:
                return getColorModeDescription();
            case TAG_IMAGE_QUALITY_1:
                return getImageQuality1Description();
            case TAG_IMAGE_QUALITY_2:
                return getImageQuality2Description();
            case TAG_SPECIAL_MODE:
                return getSpecialModeDescription();
            case TAG_JPEG_QUALITY:
                return getJpegQualityDescription();
            case TAG_MACRO_MODE:
                return getMacroModeDescription();
            case TAG_BW_MODE:
                return getBWModeDescription();
            case TAG_DIGI_ZOOM_RATIO:
                return getDigiZoomRatioDescription();
            case TAG_CAMERA_ID:
                return getCameraIdDescription();
            case TAG_FLASH_MODE:
                return getFlashModeDescription();
            case TAG_FOCUS_RANGE:
                return getFocusRangeDescription();
            case TAG_FOCUS_MODE:
                return getFocusModeDescription();
            case TAG_SHARPNESS:
                return getSharpnessDescription();

            case CameraSettings.TAG_EXPOSURE_MODE:
                return GET_EXPOSURE_MODE_DESCRIPTION();
            case CameraSettings.TAG_FLASH_MODE:
                return GET_FLASH_MODE_DESCRIPTION();
            case CameraSettings.TAG_WHITE_BALANCE:
                return GET_WHITE_BALANCE_DESCRIPTION();
            case CameraSettings.TAG_IMAGE_SIZE:
                return GET_IMAGE_SIZE_DESCRIPTION();
            case CameraSettings.TAG_IMAGE_QUALITY:
                return GET_IMAGE_QUALITY_DESCRIPTION();
            case CameraSettings.TAG_SHOOTING_MODE:
                return GET_SHOOTING_MODE_DESCRIPTION();
            case CameraSettings.TAG_METERING_MODE:
                return GET_METERING_MODE_DESCRIPTION();
            case CameraSettings.TAG_APEX_FILM_SPEED_VALUE:
                return GET_APEX_FILM_SPEED_VALUE_DESCRIPTION();
            case CameraSettings.TAG_APEX_SHUTTER_SPEED_TIME_VALUE:
                return GET_APEX_SHUTTER_SPEED_TIME_VALUE_DESCRIPTION();
            case CameraSettings.TAG_APEX_APERTURE_VALUE:
                return GET_APEX_APERTURE_VALUE_DESCRIPTION();
            case CameraSettings.TAG_MACRO_MODE:
                return GET_MACRO_MODE_DESCRIPTION();
            case CameraSettings.TAG_DIGITAL_ZOOM:
                return GET_DIGITAL_ZOOM_DESCRIPTION();
            case CameraSettings.TAG_EXPOSURE_COMPENSATION:
                return GET_EXPOSURE_COMPENSATION_DESCRIPTION();
            case CameraSettings.TAG_BRACKET_STEP:
                return GET_BRACKET_STEP_DESCRIPTION();

            case CameraSettings.TAG_INTERVAL_LENGTH:
                return GET_INTERVAL_LENGTH_DESCRIPTION();
            case CameraSettings.TAG_INTERVAL_NUMBER:
                return GET_INTERVAL_NUMBER_DESCRIPTION();
            case CameraSettings.TAG_FOCAL_LENGTH:
                return GET_FOCAL_LENGTH_DESCRIPTION();
            case CameraSettings.TAG_FOCUS_DISTANCE:
                return GET_FOCUS_DISTANCE_DESCRIPTION();
            case CameraSettings.TAG_FLASH_FIRED:
                return GET_FLASH_FIRED_DESCRIPTION();
            case CameraSettings.TAG_DATE:
                return GET_DATE_DESCRIPTION();
            case CameraSettings.TAG_TIME:
                return GET_TIME_DESCRIPTION();
            case CameraSettings.TAG_MAX_APERTURE_AT_FOCAL_LENGTH:
                return GET_MAX_APERTURE_AT_FOCAL_LENGTH_DESCRIPTION();

            case CameraSettings.TAG_FILE_NUMBER_MEMORY:
                return GET_FILE_NUMBER_MEMORY_DESCRIPTION();
            case CameraSettings.TAG_LAST_FILE_NUMBER:
                return GET_LAST_FILE_NUMBER_DESCRIPTION();
            case CameraSettings.TAG_WHITE_BALANCE_RED:
                return GET_WHITE_BALANCE_RED_DESCRIPTION();
            case CameraSettings.TAG_WHITE_BALANCE_GREEN:
                return GET_WHITE_BALANCE_GREEN_DESCRIPTION();
            case CameraSettings.TAG_WHITE_BALANCE_BLUE:
                return GET_WHITE_BALANCE_BLUE_DESCRIPTION();
            case CameraSettings.TAG_SATURATION:
                return GET_SATURATION_DESCRIPTION();
            case CameraSettings.TAG_CONTRAST:
                return GET_CONTRAST_DESCRIPTION();
            case CameraSettings.TAG_SHARPNESS:
                return GET_SHARPNESS_DESCRIPTION();
            case CameraSettings.TAG_SUBJECT_PROGRAM:
                return GET_SUBJECT_PROGRAM_DESCRIPTION();
            case CameraSettings.TAG_FLASH_COMPENSATION:
                return GET_FLASH_COMPENSATION_DESCRIPTION();
            case CameraSettings.TAG_ISO_SETTING:
                return GET_ISO_SETTING_DESCRIPTION();
            case CameraSettings.TAG_CAMERA_MODEL:
                return GET_CAMERA_MODEL_DESCRIPTION();
            case CameraSettings.TAG_INTERVAL_MODE:
                return GET_INTERVAL_MODE_DESCRIPTION();
            case CameraSettings.TAG_FOLDER_NAME:
                return GET_FOLDER_NAME_DESCRIPTION();
            case CameraSettings.TAG_COLOR_MODE:
                return GET_COLOR_MODE_DESCRIPTION();
            case CameraSettings.TAG_COLOR_FILTER:
                return GET_COLOR_FILTER_DESCRIPTION();
            case CameraSettings.TAG_BLACK_AND_WHITE_FILTER:
                return GET_BLACK_AND_WHITE_FILTER_DESCRIPTION();
            case CameraSettings.TAG_INTERNAL_FLASH:
                return GET_INTERNAL_FLASH_DESCRIPTION();
            case CameraSettings.TAG_APEX_BRIGHTNESS_VALUE:
                return GET_APEX_BRIGHTNESS_VALUE_DESCRIPTION();
            case CameraSettings.TAG_SPOT_FOCUS_POINT_X_COORDINATE:
                return GET_SPOT_FOCUS_POINT_X_COORDINATE_DESCRIPTION();
            case CameraSettings.TAG_SPOT_FOCUS_POINT_Y_COORDINATE:
                return GET_SPOT_FOCUS_POINT_Y_COORDINATE_DESCRIPTION();
            case CameraSettings.TAG_WIDE_FOCUS_ZONE:
                return GET_WIDE_FOCUS_ZONE_DESCRIPTION();
            case CameraSettings.TAG_FOCUS_MODE:
                return GET_FOCUS_MODE_DESCRIPTION();
            case CameraSettings.TAG_FOCUS_AREA:
                return GET_FOCUS_AREA_DESCRIPTION();
            case CameraSettings.TAG_DEC_SWITCH_POSITION:
                return GET_DEC_SWITCH_POSITION_DESCRIPTION();

            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String GET_EXPOSURE_MODE_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_EXPOSURE_MODE, "P", "A", "S", "M");
    }

    @Nullable
    public String GET_FLASH_MODE_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_FLASH_MODE,
            "Normal", "Red-eye reduction", "Rear flash sync", "Wireless");
    }

    @Nullable
    public String GET_WHITE_BALANCE_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_WHITE_BALANCE,
            "Auto", // 0
            "Daylight",
            "Cloudy",
            "Tungsten",
            null,
            "Custom", // 5
            null,
            "Fluorescent",
            "Fluorescent 2",
            null,
            null, // 10
            "Custom 2",
            "Custom 3"
        );
    }

    @Nullable
    public String GET_IMAGE_SIZE_DESCRIPTION()
    {
        // This is a pretty weird way to store this information!
        return getIndexedDescription(CameraSettings.TAG_IMAGE_SIZE, "2560 x 1920", "1600 x 1200", "1280 x 960", "640 x 480");
    }

    @Nullable
    public String GET_IMAGE_QUALITY_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_IMAGE_QUALITY, "Raw", "Super Fine", "Fine", "Standard", "Economy", "Extra Fine");
    }

    @Nullable
    public String GET_SHOOTING_MODE_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_SHOOTING_MODE,
            "Single",
            "Continuous",
            "Self Timer",
            null,
            "Bracketing",
            "Interval",
            "UHS Continuous",
            "HS Continuous"
        );
    }

    @Nullable
    public String GET_METERING_MODE_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_METERING_MODE, "Multi-Segment", "Centre Weighted", "Spot");
    }

    @Nullable
    public String GET_APEX_FILM_SPEED_VALUE_DESCRIPTION()
    {
        // http://www.ozhiker.com/electronics/pjmt/jpeg_info/minolta_mn.html#Minolta_Camera_Settings
        // Apex Speed value = value/8-1 ,
        // ISO = (2^(value/8-1))*3.125
        Long value = _directory.getLongObject(CameraSettings.TAG_APEX_FILM_SPEED_VALUE);

        if (value == null)
            return null;

        double iso = Math.pow((value / 8d) - 1, 2) * 3.125;
        return Double.toString(iso);
    }

    @Nullable
    public String GET_APEX_SHUTTER_SPEED_TIME_VALUE_DESCRIPTION()
    {
        // http://www.ozhiker.com/electronics/pjmt/jpeg_info/minolta_mn.html#Minolta_Camera_Settings
        // Apex Time value = value/8-6 ,
        // ShutterSpeed = 2^( (48-value)/8 ),
        // Due to rounding error value=8 should be displayed as 30 sec.
        Long value = _directory.getLongObject(CameraSettings.TAG_APEX_SHUTTER_SPEED_TIME_VALUE);

        if (value == null)
            return null;

        double shutterSpeed = Math.pow((49-value) / 8d, 2);
        return Double.toString(shutterSpeed) + " sec";
    }

    @Nullable
    public String GET_APEX_APERTURE_VALUE_DESCRIPTION()
    {
        // http://www.ozhiker.com/electronics/pjmt/jpeg_info/minolta_mn.html#Minolta_Camera_Settings
        // Apex Aperture Value = value/8-1 ,
        // Aperture F-stop = 2^( value/16-0.5 )
        Long value = _directory.getLongObject(CameraSettings.TAG_APEX_APERTURE_VALUE);

        if (value == null)
            return null;

        double fStop = Math.pow((value/16d) - 0.5, 2);
        return "F" + Double.toString(fStop);
    }

    @Nullable
    public String GET_MACRO_MODE_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_MACRO_MODE, "Off", "On");
    }

    @Nullable
    public String GET_DIGITAL_ZOOM_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_DIGITAL_ZOOM, "Off", "Electronic magnification", "Digital zoom 2x");
    }

    @Nullable
    public String GET_EXPOSURE_COMPENSATION_DESCRIPTION()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_EXPOSURE_COMPENSATION);
        return value == null ? null : ((value / 3d) - 2) + " EV";
    }

    @Nullable
    public String GET_BRACKET_STEP_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_BRACKET_STEP, "1/3 EV", "2/3 EV", "1 EV");
    }

    @Nullable
    public String GET_INTERVAL_LENGTH_DESCRIPTION()
    {
        if (!_directory.isIntervalMode())
            return "N/A";

        Long value = _directory.getLongObject(CameraSettings.TAG_INTERVAL_LENGTH);
        return value == null ? null : value + " min";
    }

    @Nullable
    public String GET_INTERVAL_NUMBER_DESCRIPTION()
    {
        if (!_directory.isIntervalMode())
            return "N/A";

        Long value = _directory.getLongObject(CameraSettings.TAG_INTERVAL_NUMBER);
        return value == null ? null : Long.toString(value);
    }

    @Nullable
    public String GET_FOCAL_LENGTH_DESCRIPTION()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_FOCAL_LENGTH);
        return value == null ? null : Double.toString(value/256d) + " mm";
    }

    @Nullable
    public String GET_FOCUS_DISTANCE_DESCRIPTION()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_FOCUS_DISTANCE);
        return value == null
            ? null
            : value == 0
                ? "Infinity"
                : value + " mm";
    }

    @Nullable
    public String GET_FLASH_FIRED_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_FLASH_FIRED, "No", "Yes");
    }

    @Nullable
    public String GET_DATE_DESCRIPTION()
    {
        // day = value%256,
        // month = floor( (value - floor( value/65536 )*65536 )/256 )
        // year = floor( value/65536)
        Long value = _directory.getLongObject(CameraSettings.TAG_DATE);
        if (value == null)
            return null;
        long day = value & 0xFF;
        long month = (value >> 16) & 0xFF;
        long year = (value >> 8) & 0xFF;
        return new GregorianCalendar((int)year + 1970, (int)month, (int)day).getTime().toString();
    }

    @Nullable
    public String GET_TIME_DESCRIPTION()
    {
        // hours = floor( value/65536 ),
        // minutes = floor( ( value - floor( value/65536 )*65536 )/256 ),
        // seconds = value%256
        Long value = _directory.getLongObject(CameraSettings.TAG_TIME);
        if (value == null)
            return null;
        long hours = (value >> 8) & 0xFF;
        long minutes = (value >> 16) & 0xFF;
        long seconds = value & 0xFF;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Nullable
    public String GET_MAX_APERTURE_AT_FOCAL_LENGTH_DESCRIPTION()
    {
        // Aperture F-Stop = 2^(value/16-0.5)
        Long value = _directory.getLongObject(CameraSettings.TAG_TIME);
        if (value == null)
            return null;
        double fStop = Math.pow((value/16d) - 0.5, 2);
        return "F" + fStop;
    }

    @Nullable
    public String GET_FILE_NUMBER_MEMORY_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_FILE_NUMBER_MEMORY, "Off", "On");
    }

    @Nullable
    public String GET_LAST_FILE_NUMBER_DESCRIPTION()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_LAST_FILE_NUMBER);
        return value == null
            ? null
            : value == 0
                ? "File Number Memory Off"
                : Long.toString(value);
    }

    @Nullable
    public String GET_WHITE_BALANCE_RED_DESCRIPTION()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_WHITE_BALANCE_RED);
        return value == null ? null : Double.toString(value/256d);
    }

    @Nullable
    public String GET_WHITE_BALANCE_GREEN_DESCRIPTION()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_WHITE_BALANCE_GREEN);
        return value == null ? null : Double.toString(value/256d);
    }

    @Nullable
    public String GET_WHITE_BALANCE_BLUE_DESCRIPTION()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_WHITE_BALANCE_BLUE);
        return value == null ? null : Double.toString(value/256d);
    }

    @Nullable
    public String GET_SATURATION_DESCRIPTION()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_SATURATION);
        return value == null ? null : Long.toString(value-3);
    }

    @Nullable
    public String GET_CONTRAST_DESCRIPTION()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_CONTRAST);
        return value == null ? null : Long.toString(value-3);
    }

    @Nullable
    public String GET_SHARPNESS_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_SHARPNESS, "Hard", "Normal", "Soft");
    }

    @Nullable
    public String GET_SUBJECT_PROGRAM_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_SUBJECT_PROGRAM, "None", "Portrait", "Text", "Night Portrait", "Sunset", "Sports Action");
    }

    @Nullable
    public String GET_FLASH_COMPENSATION_DESCRIPTION()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_FLASH_COMPENSATION);
        return value == null ? null : ((value-6)/3d) + " EV";
    }

    @Nullable
    public String GET_ISO_SETTING_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_ISO_SETTING, "100", "200", "400", "800", "Auto", "64");
    }

    @Nullable
    public String GET_CAMERA_MODEL_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_CAMERA_MODEL,
            "DiMAGE 7",
            "DiMAGE 5",
            "DiMAGE S304",
            "DiMAGE S404",
            "DiMAGE 7i",
            "DiMAGE 7Hi",
            "DiMAGE A1",
            "DiMAGE S414");
    }

    @Nullable
    public String GET_INTERVAL_MODE_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_INTERVAL_MODE, "Still Image", "Time Lapse Movie");
    }

    @Nullable
    public String GET_FOLDER_NAME_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_FOLDER_NAME, "Standard Form", "Data Form");
    }

    @Nullable
    public String GET_COLOR_MODE_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_COLOR_MODE, "Natural Color", "Black & White", "Vivid Color", "Solarization", "AdobeRGB");
    }

    @Nullable
    public String GET_COLOR_FILTER_DESCRIPTION()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_COLOR_FILTER);
        return value == null ? null : Long.toString(value-3);
    }

    @Nullable
    public String GET_BLACK_AND_WHITE_FILTER_DESCRIPTION()
    {
        return super.getDescription(CameraSettings.TAG_BLACK_AND_WHITE_FILTER);
    }

    @Nullable
    public String GET_INTERNAL_FLASH_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_INTERNAL_FLASH, "Did Not Fire", "Fired");
    }

    @Nullable
    public String GET_APEX_BRIGHTNESS_VALUE_DESCRIPTION()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_APEX_BRIGHTNESS_VALUE);
        return value == null ? null : Double.toString((value/8d)-6);
    }

    @Nullable
    public String GET_SPOT_FOCUS_POINT_X_COORDINATE_DESCRIPTION()
    {
        return super.getDescription(CameraSettings.TAG_SPOT_FOCUS_POINT_X_COORDINATE);
    }

    @Nullable
    public String GET_SPOT_FOCUS_POINT_Y_COORDINATE_DESCRIPTION()
    {
        return super.getDescription(CameraSettings.TAG_SPOT_FOCUS_POINT_Y_COORDINATE);
    }

    @Nullable
    public String GET_WIDE_FOCUS_ZONE_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_WIDE_FOCUS_ZONE,
            "No Zone or AF Failed",
            "Center Zone (Horizontal Orientation)",
            "Center Zone (Vertical Orientation)",
            "Left Zone",
            "Right Zone"
        );
    }

    @Nullable
    public String GET_FOCUS_MODE_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_FOCUS_MODE, "Auto Focus", "Manual Focus");
    }

    @Nullable
    public String GET_FOCUS_AREA_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_FOCUS_AREA, "Wide Focus (Normal)", "Spot Focus");
    }

    @Nullable
    public String GET_DEC_SWITCH_POSITION_DESCRIPTION()
    {
        return getIndexedDescription(CameraSettings.TAG_DEC_SWITCH_POSITION, "Exposure", "Contrast", "Saturation", "Filter");
    }
    
    @Nullable
    public String getMakernoteVersionDescription()
    {
        return getVersionBytesDescription(TAG_MAKERNOTE_VERSION, 2);
    }

    @Nullable
    public String getImageQuality2Description()
    {
        return getIndexedDescription(TAG_IMAGE_QUALITY_2,
            "Raw",
            "Super Fine",
            "Fine",
            "Standard",
            "Extra Fine");
    }

    @Nullable
    public String getImageQuality1Description()
    {
        return getIndexedDescription(TAG_IMAGE_QUALITY_1,
            "Raw",
            "Super Fine",
            "Fine",
            "Standard",
            "Extra Fine");
    }

    @Nullable
    public String getColorModeDescription()
    {
        return getIndexedDescription(TAG_COLOUR_MODE,
            "Natural Colour",
            "Black & White",
            "Vivid Colour",
            "Solarization",
            "AdobeRGB");
    }

    @Nullable
    public String getSharpnessDescription()
    {
        return getIndexedDescription(TAG_SHARPNESS, "Normal", "Hard", "Soft");
    }

    @Nullable
    public String getFocusModeDescription()
    {
        return getIndexedDescription(TAG_FOCUS_MODE, "Auto", "Manual");
    }

    @Nullable
    public String getFocusRangeDescription()
    {
        return getIndexedDescription(TAG_FOCUS_RANGE, "Normal", "Macro");
    }

    @Nullable
    public String getFlashModeDescription()
    {
        return getIndexedDescription(TAG_FLASH_MODE, null, null, "On", "Off");
    }

    @Nullable
    public String getDigiZoomRatioDescription()
    {
        return getIndexedDescription(TAG_DIGI_ZOOM_RATIO, "Normal", null, "Digital 2x Zoom");
    }

    @Nullable
    public String getCameraIdDescription()
    {
        byte[] bytes = _directory.getByteArray(TAG_CAMERA_ID);
        if (bytes == null)
            return null;
        return new String(bytes);
    }

    @Nullable
    public String getMacroModeDescription()
    {
        return getIndexedDescription(TAG_MACRO_MODE, "Normal (no macro)", "Macro");
    }

    @Nullable
    public String getBWModeDescription()
    {
        return getIndexedDescription(TAG_BW_MODE, "Off", "On");
    }

    @Nullable
    public String getJpegQualityDescription()
    {
        return getIndexedDescription(TAG_JPEG_QUALITY,
            1,
            "Standard Quality",
            "High Quality",
            "Super High Quality");
    }

    @Nullable
    public String getSpecialModeDescription()
    {
        long[] values = (long[])_directory.getObject(TAG_SPECIAL_MODE);
        if (values==null)
            return null;
        if (values.length < 1)
            return "";
        StringBuilder desc = new StringBuilder();

        switch ((int)values[0]) {
            case 0:
                desc.append("Normal picture taking mode");
                break;
            case 1:
                desc.append("Unknown picture taking mode");
                break;
            case 2:
                desc.append("Fast picture taking mode");
                break;
            case 3:
                desc.append("Panorama picture taking mode");
                break;
            default:
                desc.append("Unknown picture taking mode");
                break;
        }

        if (values.length >= 2) {
            switch ((int)values[1]) {
                case 0:
                    break;
                case 1:
                    desc.append(" / 1st in a sequence");
                    break;
                case 2:
                    desc.append(" / 2nd in a sequence");
                    break;
                case 3:
                    desc.append(" / 3rd in a sequence");
                    break;
                default:
                    desc.append(" / ");
                    desc.append(values[1]);
                    desc.append("th in a sequence");
                    break;
            }
        }
        if (values.length >= 3) {
            switch ((int)values[2]) {
                case 1:
                    desc.append(" / Left to right panorama direction");
                    break;
                case 2:
                    desc.append(" / Right to left panorama direction");
                    break;
                case 3:
                    desc.append(" / Bottom to top panorama direction");
                    break;
                case 4:
                    desc.append(" / Top to bottom panorama direction");
                    break;
            }
        }

        return desc.toString();
    }
}

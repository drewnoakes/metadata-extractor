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

import com.drew.imaging.PhotographicConversions;
import com.drew.lang.Rational;
import com.drew.lang.DateUtil;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static com.drew.metadata.exif.makernotes.OlympusMakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link OlympusMakernoteDirectory}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class OlympusMakernoteDescriptor extends TagDescriptor<OlympusMakernoteDirectory>
{
    // TODO extend support for some offset-encoded byte[] tags: http://www.ozhiker.com/electronics/pjmt/jpeg_info/olympus_mn.html

    public OlympusMakernoteDescriptor(@NotNull OlympusMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
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
            case TAG_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case TAG_FOCAL_PLANE_DIAGONAL:
                return getFocalPlaneDiagonalDescription();
            case TAG_CAMERA_TYPE:
                return getCameraTypeDescription();
            case TAG_CAMERA_ID:
                return getCameraIdDescription();
            case TAG_ONE_TOUCH_WB:
                return getOneTouchWbDescription();
            case TAG_SHUTTER_SPEED_VALUE:
                return getShutterSpeedDescription();
            case TAG_ISO_VALUE:
                return getIsoValueDescription();
            case TAG_APERTURE_VALUE:
                return getApertureValueDescription();
            case TAG_FLASH_MODE:
                return getFlashModeDescription();
            case TAG_FOCUS_RANGE:
                return getFocusRangeDescription();
            case TAG_FOCUS_MODE:
                return getFocusModeDescription();
            case TAG_SHARPNESS:
                return getSharpnessDescription();
            case TAG_COLOUR_MATRIX:
                return getColorMatrixDescription();
            case TAG_WB_MODE:
                return getWbModeDescription();
            case TAG_RED_BALANCE:
                return getRedBalanceDescription();
            case TAG_BLUE_BALANCE:
                return getBlueBalanceDescription();
            case TAG_CONTRAST:
                return getContrastDescription();
            case TAG_PREVIEW_IMAGE_VALID:
                return getPreviewImageValidDescription();

            case CameraSettings.TAG_EXPOSURE_MODE:
                return getExposureModeDescription();
            case CameraSettings.TAG_FLASH_MODE:
                return getFlashModeCameraSettingDescription();
            case CameraSettings.TAG_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case CameraSettings.TAG_IMAGE_SIZE:
                return getImageSizeDescription();
            case CameraSettings.TAG_IMAGE_QUALITY:
                return getImageQualityDescription();
            case CameraSettings.TAG_SHOOTING_MODE:
                return getShootingModeDescription();
            case CameraSettings.TAG_METERING_MODE:
                return getMeteringModeDescription();
            case CameraSettings.TAG_APEX_FILM_SPEED_VALUE:
                return getApexFilmSpeedDescription();
            case CameraSettings.TAG_APEX_SHUTTER_SPEED_TIME_VALUE:
                return getApexShutterSpeedTimeDescription();
            case CameraSettings.TAG_APEX_APERTURE_VALUE:
                return getApexApertureDescription();
            case CameraSettings.TAG_MACRO_MODE:
                return getMacroModeCameraSettingDescription();
            case CameraSettings.TAG_DIGITAL_ZOOM:
                return getDigitalZoomCameraSettingDescription();
            case CameraSettings.TAG_EXPOSURE_COMPENSATION:
                return getExposureCompensationDescription();
            case CameraSettings.TAG_BRACKET_STEP:
                return getBracketStepDescription();

            case CameraSettings.TAG_INTERVAL_LENGTH:
                return getIntervalLengthDescription();
            case CameraSettings.TAG_INTERVAL_NUMBER:
                return getIntervalNumberDescription();
            case CameraSettings.TAG_FOCAL_LENGTH:
                return getFocalLengthDescription();
            case CameraSettings.TAG_FOCUS_DISTANCE:
                return getFocusDistanceDescription();
            case CameraSettings.TAG_FLASH_FIRED:
                return getFlashFiredDescription();
            case CameraSettings.TAG_DATE:
                return getDateDescription();
            case CameraSettings.TAG_TIME:
                return getTimeDescription();
            case CameraSettings.TAG_MAX_APERTURE_AT_FOCAL_LENGTH:
                return getMaxApertureAtFocalLengthDescription();

            case CameraSettings.TAG_FILE_NUMBER_MEMORY:
                return getFileNumberMemoryDescription();
            case CameraSettings.TAG_LAST_FILE_NUMBER:
                return getLastFileNumberDescription();
            case CameraSettings.TAG_WHITE_BALANCE_RED:
                return getWhiteBalanceRedDescription();
            case CameraSettings.TAG_WHITE_BALANCE_GREEN:
                return getWhiteBalanceGreenDescription();
            case CameraSettings.TAG_WHITE_BALANCE_BLUE:
                return getWhiteBalanceBlueDescription();
            case CameraSettings.TAG_SATURATION:
                return getSaturationDescription();
            case CameraSettings.TAG_CONTRAST:
                return getContrastCameraSettingDescription();
            case CameraSettings.TAG_SHARPNESS:
                return getSharpnessCameraSettingDescription();
            case CameraSettings.TAG_SUBJECT_PROGRAM:
                return getSubjectProgramDescription();
            case CameraSettings.TAG_FLASH_COMPENSATION:
                return getFlashCompensationDescription();
            case CameraSettings.TAG_ISO_SETTING:
                return getIsoSettingDescription();
            case CameraSettings.TAG_CAMERA_MODEL:
                return getCameraModelDescription();
            case CameraSettings.TAG_INTERVAL_MODE:
                return getIntervalModeDescription();
            case CameraSettings.TAG_FOLDER_NAME:
                return getFolderNameDescription();
            case CameraSettings.TAG_COLOR_MODE:
                return getColorModeCameraSettingDescription();
            case CameraSettings.TAG_COLOR_FILTER:
                return getColorFilterDescription();
            case CameraSettings.TAG_BLACK_AND_WHITE_FILTER:
                return getBlackAndWhiteFilterDescription();
            case CameraSettings.TAG_INTERNAL_FLASH:
                return getInternalFlashDescription();
            case CameraSettings.TAG_APEX_BRIGHTNESS_VALUE:
                return getApexBrightnessDescription();
            case CameraSettings.TAG_SPOT_FOCUS_POINT_X_COORDINATE:
                return getSpotFocusPointXCoordinateDescription();
            case CameraSettings.TAG_SPOT_FOCUS_POINT_Y_COORDINATE:
                return getSpotFocusPointYCoordinateDescription();
            case CameraSettings.TAG_WIDE_FOCUS_ZONE:
                return getWideFocusZoneDescription();
            case CameraSettings.TAG_FOCUS_MODE:
                return getFocusModeCameraSettingDescription();
            case CameraSettings.TAG_FOCUS_AREA:
                return getFocusAreaDescription();
            case CameraSettings.TAG_DEC_SWITCH_POSITION:
                return getDecSwitchPositionDescription();

            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getExposureModeDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_EXPOSURE_MODE, "P", "A", "S", "M");
    }

    @Nullable
    public String getFlashModeCameraSettingDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_FLASH_MODE,
            "Normal", "Red-eye reduction", "Rear flash sync", "Wireless");
    }

    @Nullable
    public String getWhiteBalanceDescription()
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
    public String getImageSizeDescription()
    {
        // This is a pretty weird way to store this information!
        return getIndexedDescription(CameraSettings.TAG_IMAGE_SIZE, "2560 x 1920", "1600 x 1200", "1280 x 960", "640 x 480");
    }

    @Nullable
    public String getImageQualityDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_IMAGE_QUALITY, "Raw", "Super Fine", "Fine", "Standard", "Economy", "Extra Fine");
    }

    @Nullable
    public String getShootingModeDescription()
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
    public String getMeteringModeDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_METERING_MODE, "Multi-Segment", "Centre Weighted", "Spot");
    }

    @Nullable
    public String getApexFilmSpeedDescription()
    {
        // http://www.ozhiker.com/electronics/pjmt/jpeg_info/minolta_mn.html#Minolta_Camera_Settings
        // Apex Speed value = value/8-1 ,
        // ISO = (2^(value/8-1))*3.125
        Long value = _directory.getLongObject(CameraSettings.TAG_APEX_FILM_SPEED_VALUE);

        if (value == null)
            return null;

        double iso = Math.pow((value / 8d) - 1, 2) * 3.125;
        DecimalFormat format = new DecimalFormat("0.##");
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(iso);
    }

    @Nullable
    public String getApexShutterSpeedTimeDescription()
    {
        // http://www.ozhiker.com/electronics/pjmt/jpeg_info/minolta_mn.html#Minolta_Camera_Settings
        // Apex Time value = value/8-6 ,
        // ShutterSpeed = 2^( (48-value)/8 ),
        // Due to rounding error value=8 should be displayed as 30 sec.
        Long value = _directory.getLongObject(CameraSettings.TAG_APEX_SHUTTER_SPEED_TIME_VALUE);

        if (value == null)
            return null;

        double shutterSpeed = Math.pow((49-value) / 8d, 2);
        DecimalFormat format = new DecimalFormat("0.###");
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(shutterSpeed) + " sec";
    }

    @Nullable
    public String getApexApertureDescription()
    {
        // http://www.ozhiker.com/electronics/pjmt/jpeg_info/minolta_mn.html#Minolta_Camera_Settings
        // Apex Aperture Value = value/8-1 ,
        // Aperture F-stop = 2^( value/16-0.5 )
        Long value = _directory.getLongObject(CameraSettings.TAG_APEX_APERTURE_VALUE);

        if (value == null)
            return null;

        double fStop = Math.pow((value/16d) - 0.5, 2);
        return getFStopDescription(fStop);
    }

    @Nullable
    public String getMacroModeCameraSettingDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_MACRO_MODE, "Off", "On");
    }

    @Nullable
    public String getDigitalZoomCameraSettingDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_DIGITAL_ZOOM, "Off", "Electronic magnification", "Digital zoom 2x");
    }

    @Nullable
    public String getExposureCompensationDescription()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_EXPOSURE_COMPENSATION);
        DecimalFormat format = new DecimalFormat("0.##");
        return value == null ? null : format.format((value / 3d) - 2) + " EV";
    }

    @Nullable
    public String getBracketStepDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_BRACKET_STEP, "1/3 EV", "2/3 EV", "1 EV");
    }

    @Nullable
    public String getIntervalLengthDescription()
    {
        if (!_directory.isIntervalMode())
            return "N/A";

        Long value = _directory.getLongObject(CameraSettings.TAG_INTERVAL_LENGTH);
        return value == null ? null : value + " min";
    }

    @Nullable
    public String getIntervalNumberDescription()
    {
        if (!_directory.isIntervalMode())
            return "N/A";

        Long value = _directory.getLongObject(CameraSettings.TAG_INTERVAL_NUMBER);
        return value == null ? null : Long.toString(value);
    }

    @Nullable
    public String getFocalLengthDescription()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_FOCAL_LENGTH);
        return value == null ? null : getFocalLengthDescription(value/256d);
    }

    @Nullable
    public String getFocusDistanceDescription()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_FOCUS_DISTANCE);
        return value == null
            ? null
            : value == 0
                ? "Infinity"
                : value + " mm";
    }

    @Nullable
    public String getFlashFiredDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_FLASH_FIRED, "No", "Yes");
    }

    @Nullable
    public String getDateDescription()
    {
        // day = value%256,
        // month = floor( (value - floor( value/65536 )*65536 )/256 )
        // year = floor( value/65536)
        Long value = _directory.getLongObject(CameraSettings.TAG_DATE);
        if (value == null)
            return null;

        int day = (int) (value & 0xFF);
        int month = (int) ((value >> 16) & 0xFF);
        int year = (int) ((value >> 8) & 0xFF) + 1970;

        if (!DateUtil.isValidDate(year, month, day))
            return "Invalid date";

        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }

    @Nullable
    public String getTimeDescription()
    {
        // hours = floor( value/65536 ),
        // minutes = floor( ( value - floor( value/65536 )*65536 )/256 ),
        // seconds = value%256
        Long value = _directory.getLongObject(CameraSettings.TAG_TIME);
        if (value == null)
            return null;

        int hours = (int) ((value >> 8) & 0xFF);
        int minutes = (int) ((value >> 16) & 0xFF);
        int seconds = (int) (value & 0xFF);

        if (!DateUtil.isValidTime(hours, minutes, seconds))
            return "Invalid time";

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Nullable
    public String getMaxApertureAtFocalLengthDescription()
    {
        // Aperture F-Stop = 2^(value/16-0.5)
        Long value = _directory.getLongObject(CameraSettings.TAG_TIME);
        if (value == null)
            return null;
        double fStop = Math.pow((value/16d) - 0.5, 2);
        return getFStopDescription(fStop);
    }

    @Nullable
    public String getFileNumberMemoryDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_FILE_NUMBER_MEMORY, "Off", "On");
    }

    @Nullable
    public String getLastFileNumberDescription()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_LAST_FILE_NUMBER);
        return value == null
            ? null
            : value == 0
                ? "File Number Memory Off"
                : Long.toString(value);
    }

    @Nullable
    public String getWhiteBalanceRedDescription()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_WHITE_BALANCE_RED);
        DecimalFormat format = new DecimalFormat("0.##");
        return value == null ? null : format.format(value/256d);
    }

    @Nullable
    public String getWhiteBalanceGreenDescription()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_WHITE_BALANCE_GREEN);
        DecimalFormat format = new DecimalFormat("0.##");
        return value == null ? null : format.format(value/256d);
    }

    @Nullable
    public String getWhiteBalanceBlueDescription()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_WHITE_BALANCE_BLUE);
        DecimalFormat format = new DecimalFormat("0.##");
        return value == null ? null : format.format(value / 256d);
    }

    @Nullable
    public String getSaturationDescription()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_SATURATION);
        return value == null ? null : Long.toString(value-3);
    }

    @Nullable
    public String getContrastCameraSettingDescription()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_CONTRAST);
        return value == null ? null : Long.toString(value-3);
    }

    @Nullable
    public String getSharpnessCameraSettingDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_SHARPNESS, "Hard", "Normal", "Soft");
    }

    @Nullable
    public String getSubjectProgramDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_SUBJECT_PROGRAM, "None", "Portrait", "Text", "Night Portrait", "Sunset", "Sports Action");
    }

    @Nullable
    public String getFlashCompensationDescription()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_FLASH_COMPENSATION);
        DecimalFormat format = new DecimalFormat("0.##");
        return value == null ? null : format.format((value-6)/3d) + " EV";
    }

    @Nullable
    public String getIsoSettingDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_ISO_SETTING, "100", "200", "400", "800", "Auto", "64");
    }

    @Nullable
    public String getCameraModelDescription()
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
    public String getIntervalModeDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_INTERVAL_MODE, "Still Image", "Time Lapse Movie");
    }

    @Nullable
    public String getFolderNameDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_FOLDER_NAME, "Standard Form", "Data Form");
    }

    @Nullable
    public String getColorModeCameraSettingDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_COLOR_MODE, "Natural Color", "Black & White", "Vivid Color", "Solarization", "AdobeRGB");
    }

    @Nullable
    public String getColorFilterDescription()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_COLOR_FILTER);
        return value == null ? null : Long.toString(value-3);
    }

    @Nullable
    public String getBlackAndWhiteFilterDescription()
    {
        return super.getDescription(CameraSettings.TAG_BLACK_AND_WHITE_FILTER);
    }

    @Nullable
    public String getInternalFlashDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_INTERNAL_FLASH, "Did Not Fire", "Fired");
    }

    @Nullable
    public String getApexBrightnessDescription()
    {
        Long value = _directory.getLongObject(CameraSettings.TAG_APEX_BRIGHTNESS_VALUE);
        DecimalFormat format = new DecimalFormat("0.##");
        return value == null ? null : format.format((value/8d)-6);
    }

    @Nullable
    public String getSpotFocusPointXCoordinateDescription()
    {
        return super.getDescription(CameraSettings.TAG_SPOT_FOCUS_POINT_X_COORDINATE);
    }

    @Nullable
    public String getSpotFocusPointYCoordinateDescription()
    {
        return super.getDescription(CameraSettings.TAG_SPOT_FOCUS_POINT_Y_COORDINATE);
    }

    @Nullable
    public String getWideFocusZoneDescription()
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
    public String getFocusModeCameraSettingDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_FOCUS_MODE, "Auto Focus", "Manual Focus");
    }

    @Nullable
    public String getFocusAreaDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_FOCUS_AREA, "Wide Focus (Normal)", "Spot Focus");
    }

    @Nullable
    public String getDecSwitchPositionDescription()
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
    public String getColorMatrixDescription()
    {
        int[] obj = _directory.getIntArray(TAG_COLOUR_MATRIX);
        if (obj == null)
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < obj.length; i++) {
            sb.append((short)obj[i]);
            if (i < obj.length - 1)
                sb.append(" ");
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    @Nullable
    public String getWbModeDescription()
    {
        int[] obj = _directory.getIntArray(TAG_WB_MODE);
        if (obj == null)
            return null;

        String val = String.format("%d %d", obj[0], obj[1]);

        if(val.equals("1 0"))
            return "Auto";
        else if(val.equals("1 2"))
            return "Auto (2)";
        else if(val.equals("1 4"))
            return "Auto (4)";
        else if(val.equals("2 2"))
            return "3000 Kelvin";
        else if(val.equals("2 3"))
            return "3700 Kelvin";
        else if(val.equals("2 4"))
            return "4000 Kelvin";
        else if(val.equals("2 5"))
            return "4500 Kelvin";
        else if(val.equals("2 6"))
            return "5500 Kelvin";
        else if(val.equals("2 7"))
            return "6500 Kelvin";
        else if(val.equals("2 8"))
            return "7500 Kelvin";
        else if(val.equals("3 0"))
            return "One-touch";
        else
            return "Unknown " + val;
    }

    @Nullable
    public String getRedBalanceDescription()
    {
        int[] values = _directory.getIntArray(TAG_RED_BALANCE);
        if (values == null)
            return null;

        short value = (short)values[0];

        return String.valueOf((double)value/256d);
    }

    @Nullable
    public String getBlueBalanceDescription()
    {
        int[] values = _directory.getIntArray(TAG_BLUE_BALANCE);
        if (values == null)
            return null;

        short value = (short)values[0];

        return String.valueOf((double)value/256d);
    }

    @Nullable
    public String getContrastDescription()
    {
        return getIndexedDescription(TAG_CONTRAST, "High", "Normal", "Low");
    }

    @Nullable
    public String getPreviewImageValidDescription()
    {
        return getIndexedDescription(TAG_PREVIEW_IMAGE_VALID, "No", "Yes");
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
    public String getDigitalZoomDescription()
    {
        Rational value = _directory.getRational(TAG_DIGITAL_ZOOM);
        if (value == null)
            return null;
        return value.toSimpleString(false);
    }

    @Nullable
    public String getFocalPlaneDiagonalDescription()
    {
        Rational value = _directory.getRational(TAG_FOCAL_PLANE_DIAGONAL);
        if (value == null)
            return null;

        DecimalFormat format = new DecimalFormat("0.###");
        return format.format(value.doubleValue()) + " mm";
    }

    @Nullable
    public String getCameraTypeDescription()
    {
        String cameratype = _directory.getString(TAG_CAMERA_TYPE);
        if(cameratype == null)
            return null;

        if(OlympusMakernoteDirectory.OlympusCameraTypes.containsKey(cameratype))
            return OlympusMakernoteDirectory.OlympusCameraTypes.get(cameratype);

        return cameratype;
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
    public String getOneTouchWbDescription()
    {
        return getIndexedDescription(TAG_ONE_TOUCH_WB, "Off", "On", "On (Preset)");
    }

    @Nullable
    public String getShutterSpeedDescription()
    {
        return super.getShutterSpeedDescription(TAG_SHUTTER_SPEED_VALUE);
    }

    @Nullable
    public String getIsoValueDescription()
    {
        Rational value = _directory.getRational(TAG_ISO_VALUE);
        if (value == null)
            return null;

        return String.valueOf(Math.round(Math.pow(2, value.doubleValue() - 5) * 100));
    }

    @Nullable
    public String getApertureValueDescription()
    {
        Double aperture = _directory.getDoubleObject(TAG_APERTURE_VALUE);
        if (aperture == null)
            return null;
        double fStop = PhotographicConversions.apertureToFStop(aperture);
        return getFStopDescription(fStop);
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
        String cameratype = _directory.getString(TAG_CAMERA_TYPE);

        if(cameratype != null)
        {
            Integer value = _directory.getInteger(TAG_JPEG_QUALITY);
            if(value == null)
                return null;

            if((cameratype.startsWith("SX") && !cameratype.startsWith("SX151"))
                || cameratype.startsWith("D4322"))
            {
                switch (value)
                {
                    case 0:
                        return "Standard Quality (Low)";
                    case 1:
                        return "High Quality (Normal)";
                    case 2:
                        return "Super High Quality (Fine)";
                    case 6:
                        return "RAW";
                    default:
                        return "Unknown (" + value.toString() + ")";
                }
            }
            else
            {
                switch (value)
                {
                    case 0:
                        return "Standard Quality (Low)";
                    case 1:
                        return "High Quality (Normal)";
                    case 2:
                        return "Super High Quality (Fine)";
                    case 4:
                        return "RAW";
                    case 5:
                        return "Medium-Fine";
                    case 6:
                        return "Small-Fine";
                    case 33:
                        return "Uncompressed";
                    default:
                        return "Unknown (" + value.toString() + ")";
                }
            }
        }
        else
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

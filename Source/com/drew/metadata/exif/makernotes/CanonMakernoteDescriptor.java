/*
 * Copyright 2002-2016 Drew Noakes
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
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.text.DecimalFormat;

import static com.drew.metadata.exif.makernotes.CanonMakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link CanonMakernoteDirectory}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class CanonMakernoteDescriptor extends TagDescriptor<CanonMakernoteDirectory>
{
    public CanonMakernoteDescriptor(@NotNull CanonMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_CANON_SERIAL_NUMBER:
                return getSerialNumberDescription();
            case CameraSettings.TAG_FLASH_ACTIVITY:
                return getFlashActivityDescription();
            case CameraSettings.TAG_FOCUS_TYPE:
                return getFocusTypeDescription();
            case CameraSettings.TAG_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case CameraSettings.TAG_RECORD_MODE:
                return getRecordModeDescription();
            case CameraSettings.TAG_QUALITY:
                return getQualityDescription();
            case CameraSettings.TAG_MACRO_MODE:
                return getMacroModeDescription();
            case CameraSettings.TAG_SELF_TIMER_DELAY:
                return getSelfTimerDelayDescription();
            case CameraSettings.TAG_FLASH_MODE:
                return getFlashModeDescription();
            case CameraSettings.TAG_CONTINUOUS_DRIVE_MODE:
                return getContinuousDriveModeDescription();
            case CameraSettings.TAG_FOCUS_MODE_1:
                return getFocusMode1Description();
            case CameraSettings.TAG_IMAGE_SIZE:
                return getImageSizeDescription();
            case CameraSettings.TAG_EASY_SHOOTING_MODE:
                return getEasyShootingModeDescription();
            case CameraSettings.TAG_CONTRAST:
                return getContrastDescription();
            case CameraSettings.TAG_SATURATION:
                return getSaturationDescription();
            case CameraSettings.TAG_SHARPNESS:
                return getSharpnessDescription();
            case CameraSettings.TAG_ISO:
                return getIsoDescription();
            case CameraSettings.TAG_METERING_MODE:
                return getMeteringModeDescription();
            case CameraSettings.TAG_AF_POINT_SELECTED:
                return getAfPointSelectedDescription();
            case CameraSettings.TAG_EXPOSURE_MODE:
                return getExposureModeDescription();
            case CameraSettings.TAG_LENS_TYPE:
                return getLensTypeDescription();
            case CameraSettings.TAG_LONG_FOCAL_LENGTH:
                return getLongFocalLengthDescription();
            case CameraSettings.TAG_SHORT_FOCAL_LENGTH:
                return getShortFocalLengthDescription();
            case CameraSettings.TAG_FOCAL_UNITS_PER_MM:
                return getFocalUnitsPerMillimetreDescription();
            case CameraSettings.TAG_FLASH_DETAILS:
                return getFlashDetailsDescription();
            case CameraSettings.TAG_FOCUS_MODE_2:
                return getFocusMode2Description();
            case FocalLength.TAG_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case FocalLength.TAG_AF_POINT_USED:
                return getAfPointUsedDescription();
            case FocalLength.TAG_FLASH_BIAS:
                return getFlashBiasDescription();
            case AFInfo.TAG_AF_POINTS_IN_FOCUS:
                return getTagAfPointsInFocus();
            case CameraSettings.TAG_MAX_APERTURE:
                return getMaxApertureDescription();
            case CameraSettings.TAG_MIN_APERTURE:
                return getMinApertureDescription();
            case CameraSettings.TAG_FOCUS_CONTINUOUS:
                return getFocusContinuousDescription();
            case CameraSettings.TAG_AE_SETTING:
                return getAESettingDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_DISPLAY_APERTURE:
                return getDisplayApertureDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_SPOT_METERING_MODE:
                return getSpotMeteringModeDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_PHOTO_EFFECT:
                return getPhotoEffectDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_MANUAL_FLASH_OUTPUT:
                return getManualFlashOutputDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_COLOR_TONE:
                return getColorToneDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_SRAW_QUALITY:
                return getSRawQualityDescription();

            // It turns out that these values are dependent upon the camera model and therefore the below code was
            // incorrect for some Canon models.  This needs to be revisited.

//            case TAG_CANON_CUSTOM_FUNCTION_LONG_EXPOSURE_NOISE_REDUCTION:
//                return getLongExposureNoiseReductionDescription();
//            case TAG_CANON_CUSTOM_FUNCTION_SHUTTER_AUTO_EXPOSURE_LOCK_BUTTONS:
//                return getShutterAutoExposureLockButtonDescription();
//            case TAG_CANON_CUSTOM_FUNCTION_MIRROR_LOCKUP:
//                return getMirrorLockupDescription();
//            case TAG_CANON_CUSTOM_FUNCTION_TV_AV_AND_EXPOSURE_LEVEL:
//                return getTvAndAvExposureLevelDescription();
//            case TAG_CANON_CUSTOM_FUNCTION_AF_ASSIST_LIGHT:
//                return getAutoFocusAssistLightDescription();
//            case TAG_CANON_CUSTOM_FUNCTION_SHUTTER_SPEED_IN_AV_MODE:
//                return getShutterSpeedInAvModeDescription();
//            case TAG_CANON_CUSTOM_FUNCTION_BRACKETING:
//                return getAutoExposureBracketingSequenceAndAutoCancellationDescription();
//            case TAG_CANON_CUSTOM_FUNCTION_SHUTTER_CURTAIN_SYNC:
//                return getShutterCurtainSyncDescription();
//            case TAG_CANON_CUSTOM_FUNCTION_AF_STOP:
//                return getLensAutoFocusStopButtonDescription();
//            case TAG_CANON_CUSTOM_FUNCTION_FILL_FLASH_REDUCTION:
//                return getFillFlashReductionDescription();
//            case TAG_CANON_CUSTOM_FUNCTION_MENU_BUTTON_RETURN:
//                return getMenuButtonReturnPositionDescription();
//            case TAG_CANON_CUSTOM_FUNCTION_SET_BUTTON_FUNCTION:
//                return getSetButtonFunctionWhenShootingDescription();
//            case TAG_CANON_CUSTOM_FUNCTION_SENSOR_CLEANING:
//                return getSensorCleaningDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getSerialNumberDescription()
    {
        // http://www.ozhiker.com/electronics/pjmt/jpeg_info/canon_mn.html
        Integer value = _directory.getInteger(TAG_CANON_SERIAL_NUMBER);
        if (value == null)
            return null;
        return String.format("%04X%05d", (value >> 8) & 0xFF, value & 0xFF);
    }

/*
    @Nullable
    public String getLongExposureNoiseReductionDescription()
    {
        Integer value = _directory.getInteger(TAG_CANON_CUSTOM_FUNCTION_LONG_EXPOSURE_NOISE_REDUCTION);
        if (value==null)
            return null;
        switch (value) {
            case 0:     return "Off";
            case 1:     return "On";
            default:    return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getShutterAutoExposureLockButtonDescription()
    {
        Integer value = _directory.getInteger(TAG_CANON_CUSTOM_FUNCTION_SHUTTER_AUTO_EXPOSURE_LOCK_BUTTONS);
        if (value==null)
            return null;
        switch (value) {
            case 0:     return "AF/AE lock";
            case 1:     return "AE lock/AF";
            case 2:     return "AF/AF lock";
            case 3:     return "AE+release/AE+AF";
            default:    return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getMirrorLockupDescription()
    {
        Integer value = _directory.getInteger(TAG_CANON_CUSTOM_FUNCTION_MIRROR_LOCKUP);
        if (value==null)
            return null;
        switch (value) {
            case 0:     return "Disabled";
            case 1:     return "Enabled";
            default:    return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getTvAndAvExposureLevelDescription()
    {
        Integer value = _directory.getInteger(TAG_CANON_CUSTOM_FUNCTION_TV_AV_AND_EXPOSURE_LEVEL);
        if (value==null)
            return null;
        switch (value) {
            case 0:     return "1/2 stop";
            case 1:     return "1/3 stop";
            default:    return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getAutoFocusAssistLightDescription()
    {
        Integer value = _directory.getInteger(TAG_CANON_CUSTOM_FUNCTION_AF_ASSIST_LIGHT);
        if (value==null)
            return null;
        switch (value) {
            case 0:     return "On (Auto)";
            case 1:     return "Off";
            default:    return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getShutterSpeedInAvModeDescription()
    {
        Integer value = _directory.getInteger(TAG_CANON_CUSTOM_FUNCTION_SHUTTER_SPEED_IN_AV_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 0:     return "Automatic";
            case 1:     return "1/200 (fixed)";
            default:    return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getAutoExposureBracketingSequenceAndAutoCancellationDescription()
    {
        Integer value = _directory.getInteger(TAG_CANON_CUSTOM_FUNCTION_BRACKETING);
        if (value==null)
            return null;
        switch (value) {
            case 0:     return "0,-,+ / Enabled";
            case 1:     return "0,-,+ / Disabled";
            case 2:     return "-,0,+ / Enabled";
            case 3:     return "-,0,+ / Disabled";
            default:    return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getShutterCurtainSyncDescription()
    {
        Integer value = _directory.getInteger(TAG_CANON_CUSTOM_FUNCTION_SHUTTER_CURTAIN_SYNC);
        if (value==null)
            return null;
        switch (value) {
            case 0:     return "1st Curtain Sync";
            case 1:     return "2nd Curtain Sync";
            default:    return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getLensAutoFocusStopButtonDescription()
    {
        Integer value = _directory.getInteger(TAG_CANON_CUSTOM_FUNCTION_AF_STOP);
        if (value==null)
            return null;
        switch (value) {
            case 0:     return "AF stop";
            case 1:     return "Operate AF";
            case 2:     return "Lock AE and start timer";
            default:    return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFillFlashReductionDescription()
    {
        Integer value = _directory.getInteger(TAG_CANON_CUSTOM_FUNCTION_FILL_FLASH_REDUCTION);
        if (value==null)
            return null;
        switch (value) {
            case 0:     return "Enabled";
            case 1:     return "Disabled";
            default:    return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getMenuButtonReturnPositionDescription()
    {
        Integer value = _directory.getInteger(TAG_CANON_CUSTOM_FUNCTION_MENU_BUTTON_RETURN);
        if (value==null)
            return null;
        switch (value) {
            case 0:     return "Top";
            case 1:     return "Previous (volatile)";
            case 2:     return "Previous";
            default:    return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSetButtonFunctionWhenShootingDescription()
    {
        Integer value = _directory.getInteger(TAG_CANON_CUSTOM_FUNCTION_SET_BUTTON_FUNCTION);
        if (value==null)
            return null;
        switch (value) {
            case 0:     return "Not Assigned";
            case 1:     return "Change Quality";
            case 2:     return "Change ISO Speed";
            case 3:     return "Select Parameters";
            default:    return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSensorCleaningDescription()
    {
        Integer value = _directory.getInteger(TAG_CANON_CUSTOM_FUNCTION_SENSOR_CLEANING);
        if (value==null)
            return null;
        switch (value) {
            case 0:     return "Disabled";
            case 1:     return "Enabled";
            default:    return "Unknown (" + value + ")";
        }
    }
*/

    @Nullable
    public String getFlashBiasDescription()
    {
        Integer value = _directory.getInteger(FocalLength.TAG_FLASH_BIAS);

        if (value == null)
            return null;

        boolean isNegative = false;
        if (value > 0xF000) {
            isNegative = true;
            value = 0xFFFF - value;
            value++;
        }

        // this tag is interesting in that the values returned are:
        //  0, 0.375, 0.5, 0.626, 1
        // not
        //  0, 0.33,  0.5, 0.66,  1

        return (isNegative ? "-" : "") + Float.toString(value / 32f) + " EV";
    }

    @Nullable
    public String getAfPointUsedDescription()
    {
        Integer value = _directory.getInteger(FocalLength.TAG_AF_POINT_USED);
        if (value == null)
            return null;
        if ((value & 0x7) == 0) {
            return "Right";
        } else if ((value & 0x7) == 1) {
            return "Centre";
        } else if ((value & 0x7) == 2) {
            return "Left";
        } else {
            return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getTagAfPointsInFocus()
    {
        Integer value = _directory.getInteger(AFInfo.TAG_AF_POINTS_IN_FOCUS);
        if (value == null)
            return null;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 16; i++)
        {
            if ((value & 1 << i) != 0)
            {
                if (sb.length() != 0)
                    sb.append(',');
                sb.append(i);
            }
        }

        return sb.length() == 0 ? "None" : sb.toString();
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        return getIndexedDescription(
            FocalLength.TAG_WHITE_BALANCE,
            "Auto",
            "Sunny",
            "Cloudy",
            "Tungsten",
            "Florescent",
            "Flash",
            "Custom"
        );
    }

    @Nullable
    public String getFocusMode2Description()
    {
        return getIndexedDescription(CameraSettings.TAG_FOCUS_MODE_2, "Single", "Continuous");
    }

    @Nullable
    public String getFlashDetailsDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_FLASH_DETAILS);
        if (value == null)
            return null;
        if (((value >> 14) & 1) != 0) {
            return "External E-TTL";
        }
        if (((value >> 13) & 1) != 0) {
            return "Internal flash";
        }
        if (((value >> 11) & 1) != 0) {
            return "FP sync used";
        }
        if (((value >> 4) & 1) != 0) {
            return "FP sync enabled";
        }
        return "Unknown (" + value + ")";
    }

    @Nullable
    public String getFocalUnitsPerMillimetreDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_FOCAL_UNITS_PER_MM);
        if (value == null)
            return null;
        if (value != 0) {
            return Integer.toString(value);
        } else {
            return "";
        }
    }

    @Nullable
    public String getShortFocalLengthDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_SHORT_FOCAL_LENGTH);
        if (value == null)
            return null;
        String units = getFocalUnitsPerMillimetreDescription();
        return Integer.toString(value) + " " + units;
    }

    @Nullable
    public String getLongFocalLengthDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_LONG_FOCAL_LENGTH);
        if (value == null)
            return null;
        String units = getFocalUnitsPerMillimetreDescription();
        return Integer.toString(value) + " " + units;
    }

    @Nullable
    public String getExposureModeDescription()
    {
        return getIndexedDescription(
            CameraSettings.TAG_EXPOSURE_MODE,
            "Easy shooting",
            "Program",
            "Tv-priority",
            "Av-priority",
            "Manual",
            "A-DEP"
        );
    }

    @Nullable
    public String getLensTypeDescription() {
        Integer value = _directory.getInteger(CameraSettings.TAG_LENS_TYPE);
        if (value == null)
            return null;

        if (value == 0xFFFF)
            return "N/A";
        return Integer.toString(value);
    }

    @Nullable
    public String getMaxApertureDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_MAX_APERTURE);
        if (value == null)
            return null;
        if (value > 512)
            return String.format("Unknown (%d)", value);
        return getFStopDescription(Math.exp(decodeCanonEv(value) * Math.log(2.0) / 2.0));
    }

    @Nullable
    public String getMinApertureDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_MIN_APERTURE);
        if (value == null)
            return null;
        if (value > 512)
            return String.format("Unknown (%d)", value);
        return getFStopDescription(Math.exp(decodeCanonEv(value) * Math.log(2.0) / 2.0));
    }

    @Nullable
    public String getAfPointSelectedDescription()
    {
        return getIndexedDescription(
            CameraSettings.TAG_AF_POINT_SELECTED,
            0x3000,
            "None (MF)",
            "Auto selected",
            "Right",
            "Centre",
            "Left"
        );
    }

    @Nullable
    public String getMeteringModeDescription()
    {
        return getIndexedDescription(
            CameraSettings.TAG_METERING_MODE,
            3,
            "Evaluative",
            "Partial",
            "Centre weighted"
        );
    }

    @Nullable
    public String getIsoDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_ISO);
        if (value == null)
            return null;

        // Canon PowerShot S3 is special
        int canonMask = 0x4000;
        if ((value & canonMask) != 0)
            return "" + (value & ~canonMask);

        switch (value) {
            case 0:
                return "Not specified (see ISOSpeedRatings tag)";
            case 15:
                return "Auto";
            case 16:
                return "50";
            case 17:
                return "100";
            case 18:
                return "200";
            case 19:
                return "400";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSharpnessDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_SHARPNESS);
        if (value == null)
            return null;
        switch (value) {
            case 0xFFFF:
                return "Low";
            case 0x000:
                return "Normal";
            case 0x001:
                return "High";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSaturationDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_SATURATION);
        if (value == null)
            return null;
        switch (value) {
            case 0xFFFF:
                return "Low";
            case 0x000:
                return "Normal";
            case 0x001:
                return "High";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getContrastDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_CONTRAST);
        if (value == null)
            return null;
        switch (value) {
            case 0xFFFF:
                return "Low";
            case 0x000:
                return "Normal";
            case 0x001:
                return "High";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getEasyShootingModeDescription()
    {
        return getIndexedDescription(
            CameraSettings.TAG_EASY_SHOOTING_MODE,
            "Full auto",
            "Manual",
            "Landscape",
            "Fast shutter",
            "Slow shutter",
            "Night",
            "B&W",
            "Sepia",
            "Portrait",
            "Sports",
            "Macro / Closeup",
            "Pan focus"
        );
    }

    @Nullable
    public String getImageSizeDescription()
    {
        return getIndexedDescription(
            CameraSettings.TAG_IMAGE_SIZE,
            "Large",
            "Medium",
            "Small"
        );
    }

    @Nullable
    public String getFocusMode1Description()
    {
        return getIndexedDescription(
            CameraSettings.TAG_FOCUS_MODE_1,
            "One-shot",
            "AI Servo",
            "AI Focus",
            "Manual Focus",
            // TODO should check field 32 here (FOCUS_MODE_2)
            "Single",
            "Continuous",
            "Manual Focus"
        );
    }

    @Nullable
    public String getContinuousDriveModeDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_CONTINUOUS_DRIVE_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 0:
                final Integer delay = _directory.getInteger(CameraSettings.TAG_SELF_TIMER_DELAY);
                if (delay != null)
                    return delay == 0 ? "Single shot" : "Single shot with self-timer";
            case 1:
                return "Continuous";
        }
        return "Unknown (" + value + ")";
    }

    @Nullable
    public String getFlashModeDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_FLASH_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 0:
                return "No flash fired";
            case 1:
                return "Auto";
            case 2:
                return "On";
            case 3:
                return "Red-eye reduction";
            case 4:
                return "Slow-synchro";
            case 5:
                return "Auto and red-eye reduction";
            case 6:
                return "On and red-eye reduction";
            case 16:
                // note: this value not set on Canon D30
                return "External flash";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSelfTimerDelayDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_SELF_TIMER_DELAY);
        if (value == null)
            return null;
        if (value == 0) {
            return "Self timer not used";
        } else {
            DecimalFormat format = new DecimalFormat("0.##");
            return format.format((double)value * 0.1d) + " sec";
        }
    }

    @Nullable
    public String getMacroModeDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_MACRO_MODE, 1, "Macro", "Normal");
    }

    @Nullable
    public String getQualityDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_QUALITY, 2, "Normal", "Fine", null, "Superfine");
    }

    @Nullable
    public String getDigitalZoomDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_DIGITAL_ZOOM, "No digital zoom", "2x", "4x");
    }

    @Nullable
    public String getRecordModeDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_RECORD_MODE, 1, "JPEG", "CRW+THM", "AVI+THM", "TIF", "TIF+JPEG", "CR2", "CR2+JPEG", null, "MOV", "MP4");
    }

    @Nullable
    public String getFocusTypeDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_FOCUS_TYPE);
        if (value == null)
            return null;
        switch (value) {
            case 0:
                return "Manual";
            case 1:
                return "Auto";
            case 3:
                return "Close-up (Macro)";
            case 8:
                return "Locked (Pan Mode)";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFlashActivityDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_FLASH_ACTIVITY, "Flash did not fire", "Flash fired");
    }

    @Nullable
    public String getFocusContinuousDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_FOCUS_CONTINUOUS, 0,
            "Single", "Continous", null, null, null, null, null, null, "Manual");
    }

    @Nullable
    public String getAESettingDescription()
    {
        return getIndexedDescription(CameraSettings.TAG_AE_SETTING, 0,
            "Normal AE", "Exposure Compensation", "AE Lock", "AE Lock + Exposure Comp.", "No AE");
    }

    @Nullable
    public String getDisplayApertureDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_DISPLAY_APERTURE);
        if (value == null)
            return null;

        if (value == 0xFFFF)
            return value.toString();
        return getFStopDescription(value / 10f);
    }

    @Nullable
    public String getSpotMeteringModeDescription()
    {
        return getIndexedDescription(CanonMakernoteDirectory.CameraSettings.TAG_SPOT_METERING_MODE, 0,
            "Center", "AF Point");
    }

    @Nullable
    public String getPhotoEffectDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_PHOTO_EFFECT);
        if (value == null)
            return null;

        switch (value)
        {
            case 0:
                return "Off";
            case 1:
                return "Vivid";
            case 2:
                return "Neutral";
            case 3:
                return "Smooth";
            case 4:
                return "Sepia";
            case 5:
                return "B&W";
            case 6:
                return "Custom";
            case 100:
                return "My Color Data";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getManualFlashOutputDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_MANUAL_FLASH_OUTPUT);
        if (value == null)
            return null;

        switch (value)
        {
            case 0:
                return "n/a";
            case 0x500:
                return "Full";
            case 0x502:
                return "Medium";
            case 0x504:
                return "Low";
            case 0x7fff:
                return "n/a";   // (EOS models)
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getColorToneDescription()
    {
        Integer value = _directory.getInteger(CameraSettings.TAG_COLOR_TONE);
        if (value == null)
            return null;

        return value == 0x7fff ? "n/a" : value.toString();
    }

    @Nullable
    public String getSRawQualityDescription()
    {
        return getIndexedDescription(CanonMakernoteDirectory.CameraSettings.TAG_SRAW_QUALITY, 0, "n/a", "sRAW1 (mRAW)", "sRAW2 (sRAW)");
    }

    /**
     * Canon hex-based EV (modulo 0x20) to real number.
     *
     * Converted from Exiftool version 10.10 created by Phil Harvey
     * http://www.sno.phy.queensu.ca/~phil/exiftool/
     * lib\Image\ExifTool\Canon.pm
     *
     *         eg) 0x00 -> 0
     *             0x0c -> 0.33333
     *             0x10 -> 0.5
     *             0x14 -> 0.66666
     *             0x20 -> 1   ... etc
     */
    private double decodeCanonEv(int val)
    {
        int sign = 1;
        if (val < 0)
        {
            val = -val;
            sign = -1;
        }

        int frac = val & 0x1f;
        val -= frac;

        if (frac == 0x0c)
            frac = 0x20 / 3;
        else if (frac == 0x14)
            frac = 0x40 / 3;

        return sign * (val + frac) / (double)0x20;
    }
}

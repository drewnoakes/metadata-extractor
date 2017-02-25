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
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.text.DecimalFormat;
import java.util.HashMap;

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

        return _lensTypeById.containsKey(value)
            ? _lensTypeById.get(value)
            : String.format("Unknown (%d)", value);
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
            "Single", "Continuous", null, null, null, null, null, null, "Manual");
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

    /**
     *  Map from <see cref="CanonMakernoteDirectory.CameraSettings.TagLensType"/> to string descriptions.
     *
     *  Data sourced from http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/Canon.html#LensType
     *
     *  Note that only Canon lenses are listed. Lenses from other manufacturers may identify themselves to the camera
     *  as being from this set, but in fact may be quite different. This limits the usefulness of this data,
     *  unfortunately.
     */
    private static final HashMap<Integer, String> _lensTypeById = new HashMap<Integer, String>();

    static {
        _lensTypeById.put(1, "Canon EF 50mm f/1.8");
        _lensTypeById.put(2, "Canon EF 28mm f/2.8");
        _lensTypeById.put(3, "Canon EF 135mm f/2.8 Soft");
        _lensTypeById.put(4, "Canon EF 35-105mm f/3.5-4.5 or Sigma Lens");
        _lensTypeById.put(5, "Canon EF 35-70mm f/3.5-4.5");
        _lensTypeById.put(6, "Canon EF 28-70mm f/3.5-4.5 or Sigma or Tokina Lens");
        _lensTypeById.put(7, "Canon EF 100-300mm f/5.6L");
        _lensTypeById.put(8, "Canon EF 100-300mm f/5.6 or Sigma or Tokina Lens");
        _lensTypeById.put(9, "Canon EF 70-210mm f/4");
        _lensTypeById.put(10, "Canon EF 50mm f/2.5 Macro or Sigma Lens");
        _lensTypeById.put(11, "Canon EF 35mm f/2");
        _lensTypeById.put(13, "Canon EF 15mm f/2.8 Fisheye");
        _lensTypeById.put(14, "Canon EF 50-200mm f/3.5-4.5L");
        _lensTypeById.put(15, "Canon EF 50-200mm f/3.5-4.5");
        _lensTypeById.put(16, "Canon EF 35-135mm f/3.5-4.5");
        _lensTypeById.put(17, "Canon EF 35-70mm f/3.5-4.5A");
        _lensTypeById.put(18, "Canon EF 28-70mm f/3.5-4.5");
        _lensTypeById.put(20, "Canon EF 100-200mm f/4.5A");
        _lensTypeById.put(21, "Canon EF 80-200mm f/2.8L");
        _lensTypeById.put(22, "Canon EF 20-35mm f/2.8L or Tokina Lens");
        _lensTypeById.put(23, "Canon EF 35-105mm f/3.5-4.5");
        _lensTypeById.put(24, "Canon EF 35-80mm f/4-5.6 Power Zoom");
        _lensTypeById.put(25, "Canon EF 35-80mm f/4-5.6 Power Zoom");
        _lensTypeById.put(26, "Canon EF 100mm f/2.8 Macro or Other Lens");
        _lensTypeById.put(27, "Canon EF 35-80mm f/4-5.6");
        _lensTypeById.put(28, "Canon EF 80-200mm f/4.5-5.6 or Tamron Lens");
        _lensTypeById.put(29, "Canon EF 50mm f/1.8 II");
        _lensTypeById.put(30, "Canon EF 35-105mm f/4.5-5.6");
        _lensTypeById.put(31, "Canon EF 75-300mm f/4-5.6 or Tamron Lens");
        _lensTypeById.put(32, "Canon EF 24mm f/2.8 or Sigma Lens");
        _lensTypeById.put(33, "Voigtlander or Carl Zeiss Lens");
        _lensTypeById.put(35, "Canon EF 35-80mm f/4-5.6");
        _lensTypeById.put(36, "Canon EF 38-76mm f/4.5-5.6");
        _lensTypeById.put(37, "Canon EF 35-80mm f/4-5.6 or Tamron Lens");
        _lensTypeById.put(38, "Canon EF 80-200mm f/4.5-5.6");
        _lensTypeById.put(39, "Canon EF 75-300mm f/4-5.6");
        _lensTypeById.put(40, "Canon EF 28-80mm f/3.5-5.6");
        _lensTypeById.put(41, "Canon EF 28-90mm f/4-5.6");
        _lensTypeById.put(42, "Canon EF 28-200mm f/3.5-5.6 or Tamron Lens");
        _lensTypeById.put(43, "Canon EF 28-105mm f/4-5.6");
        _lensTypeById.put(44, "Canon EF 90-300mm f/4.5-5.6");
        _lensTypeById.put(45, "Canon EF-S 18-55mm f/3.5-5.6 [II]");
        _lensTypeById.put(46, "Canon EF 28-90mm f/4-5.6");
        _lensTypeById.put(47, "Zeiss Milvus 35mm f/2 or 50mm f/2");
        _lensTypeById.put(48, "Canon EF-S 18-55mm f/3.5-5.6 IS");
        _lensTypeById.put(49, "Canon EF-S 55-250mm f/4-5.6 IS");
        _lensTypeById.put(50, "Canon EF-S 18-200mm f/3.5-5.6 IS");
        _lensTypeById.put(51, "Canon EF-S 18-135mm f/3.5-5.6 IS");
        _lensTypeById.put(52, "Canon EF-S 18-55mm f/3.5-5.6 IS II");
        _lensTypeById.put(53, "Canon EF-S 18-55mm f/3.5-5.6 III");
        _lensTypeById.put(54, "Canon EF-S 55-250mm f/4-5.6 IS II");
        _lensTypeById.put(94, "Canon TS-E 17mm f/4L");
        _lensTypeById.put(95, "Canon TS-E 24.0mm f/3.5 L II");
        _lensTypeById.put(124, "Canon MP-E 65mm f/2.8 1-5x Macro Photo");
        _lensTypeById.put(125, "Canon TS-E 24mm f/3.5L");
        _lensTypeById.put(126, "Canon TS-E 45mm f/2.8");
        _lensTypeById.put(127, "Canon TS-E 90mm f/2.8");
        _lensTypeById.put(129, "Canon EF 300mm f/2.8L");
        _lensTypeById.put(130, "Canon EF 50mm f/1.0L");
        _lensTypeById.put(131, "Canon EF 28-80mm f/2.8-4L or Sigma Lens");
        _lensTypeById.put(132, "Canon EF 1200mm f/5.6L");
        _lensTypeById.put(134, "Canon EF 600mm f/4L IS");
        _lensTypeById.put(135, "Canon EF 200mm f/1.8L");
        _lensTypeById.put(136, "Canon EF 300mm f/2.8L");
        _lensTypeById.put(137, "Canon EF 85mm f/1.2L or Sigma or Tamron Lens");
        _lensTypeById.put(138, "Canon EF 28-80mm f/2.8-4L");
        _lensTypeById.put(139, "Canon EF 400mm f/2.8L");
        _lensTypeById.put(140, "Canon EF 500mm f/4.5L");
        _lensTypeById.put(141, "Canon EF 500mm f/4.5L");
        _lensTypeById.put(142, "Canon EF 300mm f/2.8L IS");
        _lensTypeById.put(143, "Canon EF 500mm f/4L IS or Sigma Lens");
        _lensTypeById.put(144, "Canon EF 35-135mm f/4-5.6 USM");
        _lensTypeById.put(145, "Canon EF 100-300mm f/4.5-5.6 USM");
        _lensTypeById.put(146, "Canon EF 70-210mm f/3.5-4.5 USM");
        _lensTypeById.put(147, "Canon EF 35-135mm f/4-5.6 USM");
        _lensTypeById.put(148, "Canon EF 28-80mm f/3.5-5.6 USM");
        _lensTypeById.put(149, "Canon EF 100mm f/2 USM");
        _lensTypeById.put(150, "Canon EF 14mm f/2.8L or Sigma Lens");
        _lensTypeById.put(151, "Canon EF 200mm f/2.8L");
        _lensTypeById.put(152, "Canon EF 300mm f/4L IS or Sigma Lens");
        _lensTypeById.put(153, "Canon EF 35-350mm f/3.5-5.6L or Sigma or Tamron Lens");
        _lensTypeById.put(154, "Canon EF 20mm f/2.8 USM or Zeiss Lens");
        _lensTypeById.put(155, "Canon EF 85mm f/1.8 USM");
        _lensTypeById.put(156, "Canon EF 28-105mm f/3.5-4.5 USM or Tamron Lens");
        _lensTypeById.put(160, "Canon EF 20-35mm f/3.5-4.5 USM or Tamron or Tokina Lens");
        _lensTypeById.put(161, "Canon EF 28-70mm f/2.8L or Sigma or Tamron Lens");
        _lensTypeById.put(162, "Canon EF 200mm f/2.8L");
        _lensTypeById.put(163, "Canon EF 300mm f/4L");
        _lensTypeById.put(164, "Canon EF 400mm f/5.6L");
        _lensTypeById.put(165, "Canon EF 70-200mm f/2.8 L");
        _lensTypeById.put(166, "Canon EF 70-200mm f/2.8 L + 1.4x");
        _lensTypeById.put(167, "Canon EF 70-200mm f/2.8 L + 2x");
        _lensTypeById.put(168, "Canon EF 28mm f/1.8 USM or Sigma Lens");
        _lensTypeById.put(169, "Canon EF 17-35mm f/2.8L or Sigma Lens");
        _lensTypeById.put(170, "Canon EF 200mm f/2.8L II");
        _lensTypeById.put(171, "Canon EF 300mm f/4L");
        _lensTypeById.put(172, "Canon EF 400mm f/5.6L or Sigma Lens");
        _lensTypeById.put(173, "Canon EF 180mm Macro f/3.5L or Sigma Lens");
        _lensTypeById.put(174, "Canon EF 135mm f/2L or Other Lens");
        _lensTypeById.put(175, "Canon EF 400mm f/2.8L");
        _lensTypeById.put(176, "Canon EF 24-85mm f/3.5-4.5 USM");
        _lensTypeById.put(177, "Canon EF 300mm f/4L IS");
        _lensTypeById.put(178, "Canon EF 28-135mm f/3.5-5.6 IS");
        _lensTypeById.put(179, "Canon EF 24mm f/1.4L");
        _lensTypeById.put(180, "Canon EF 35mm f/1.4L or Other Lens");
        _lensTypeById.put(181, "Canon EF 100-400mm f/4.5-5.6L IS + 1.4x or Sigma Lens");
        _lensTypeById.put(182, "Canon EF 100-400mm f/4.5-5.6L IS + 2x or Sigma Lens");
        _lensTypeById.put(183, "Canon EF 100-400mm f/4.5-5.6L IS or Sigma Lens");
        _lensTypeById.put(184, "Canon EF 400mm f/2.8L + 2x");
        _lensTypeById.put(185, "Canon EF 600mm f/4L IS");
        _lensTypeById.put(186, "Canon EF 70-200mm f/4L");
        _lensTypeById.put(187, "Canon EF 70-200mm f/4L + 1.4x");
        _lensTypeById.put(188, "Canon EF 70-200mm f/4L + 2x");
        _lensTypeById.put(189, "Canon EF 70-200mm f/4L + 2.8x");
        _lensTypeById.put(190, "Canon EF 100mm f/2.8 Macro USM");
        _lensTypeById.put(191, "Canon EF 400mm f/4 DO IS");
        _lensTypeById.put(193, "Canon EF 35-80mm f/4-5.6 USM");
        _lensTypeById.put(194, "Canon EF 80-200mm f/4.5-5.6 USM");
        _lensTypeById.put(195, "Canon EF 35-105mm f/4.5-5.6 USM");
        _lensTypeById.put(196, "Canon EF 75-300mm f/4-5.6 USM");
        _lensTypeById.put(197, "Canon EF 75-300mm f/4-5.6 IS USM");
        _lensTypeById.put(198, "Canon EF 50mm f/1.4 USM or Zeiss Lens");
        _lensTypeById.put(199, "Canon EF 28-80mm f/3.5-5.6 USM");
        _lensTypeById.put(200, "Canon EF 75-300mm f/4-5.6 USM");
        _lensTypeById.put(201, "Canon EF 28-80mm f/3.5-5.6 USM");
        _lensTypeById.put(202, "Canon EF 28-80mm f/3.5-5.6 USM IV");
        _lensTypeById.put(208, "Canon EF 22-55mm f/4-5.6 USM");
        _lensTypeById.put(209, "Canon EF 55-200mm f/4.5-5.6");
        _lensTypeById.put(210, "Canon EF 28-90mm f/4-5.6 USM");
        _lensTypeById.put(211, "Canon EF 28-200mm f/3.5-5.6 USM");
        _lensTypeById.put(212, "Canon EF 28-105mm f/4-5.6 USM");
        _lensTypeById.put(213, "Canon EF 90-300mm f/4.5-5.6 USM or Tamron Lens");
        _lensTypeById.put(214, "Canon EF-S 18-55mm f/3.5-5.6 USM");
        _lensTypeById.put(215, "Canon EF 55-200mm f/4.5-5.6 II USM");
        _lensTypeById.put(217, "Tamron AF 18-270mm f/3.5-6.3 Di II VC PZD");
        _lensTypeById.put(224, "Canon EF 70-200mm f/2.8L IS");
        _lensTypeById.put(225, "Canon EF 70-200mm f/2.8L IS + 1.4x");
        _lensTypeById.put(226, "Canon EF 70-200mm f/2.8L IS + 2x");
        _lensTypeById.put(227, "Canon EF 70-200mm f/2.8L IS + 2.8x");
        _lensTypeById.put(228, "Canon EF 28-105mm f/3.5-4.5 USM");
        _lensTypeById.put(229, "Canon EF 16-35mm f/2.8L");
        _lensTypeById.put(230, "Canon EF 24-70mm f/2.8L");
        _lensTypeById.put(231, "Canon EF 17-40mm f/4L");
        _lensTypeById.put(232, "Canon EF 70-300mm f/4.5-5.6 DO IS USM");
        _lensTypeById.put(233, "Canon EF 28-300mm f/3.5-5.6L IS");
        _lensTypeById.put(234, "Canon EF-S 17-85mm f/4-5.6 IS USM or Tokina Lens");
        _lensTypeById.put(235, "Canon EF-S 10-22mm f/3.5-4.5 USM");
        _lensTypeById.put(236, "Canon EF-S 60mm f/2.8 Macro USM");
        _lensTypeById.put(237, "Canon EF 24-105mm f/4L IS");
        _lensTypeById.put(238, "Canon EF 70-300mm f/4-5.6 IS USM");
        _lensTypeById.put(239, "Canon EF 85mm f/1.2L II");
        _lensTypeById.put(240, "Canon EF-S 17-55mm f/2.8 IS USM");
        _lensTypeById.put(241, "Canon EF 50mm f/1.2L");
        _lensTypeById.put(242, "Canon EF 70-200mm f/4L IS");
        _lensTypeById.put(243, "Canon EF 70-200mm f/4L IS + 1.4x");
        _lensTypeById.put(244, "Canon EF 70-200mm f/4L IS + 2x");
        _lensTypeById.put(245, "Canon EF 70-200mm f/4L IS + 2.8x");
        _lensTypeById.put(246, "Canon EF 16-35mm f/2.8L II");
        _lensTypeById.put(247, "Canon EF 14mm f/2.8L II USM");
        _lensTypeById.put(248, "Canon EF 200mm f/2L IS or Sigma Lens");
        _lensTypeById.put(249, "Canon EF 800mm f/5.6L IS");
        _lensTypeById.put(250, "Canon EF 24mm f/1.4L II or Sigma Lens");
        _lensTypeById.put(251, "Canon EF 70-200mm f/2.8L IS II USM");
        _lensTypeById.put(252, "Canon EF 70-200mm f/2.8L IS II USM + 1.4x");
        _lensTypeById.put(253, "Canon EF 70-200mm f/2.8L IS II USM + 2x");
        _lensTypeById.put(254, "Canon EF 100mm f/2.8L Macro IS USM");
        _lensTypeById.put(255, "Sigma 24-105mm f/4 DG OS HSM | A or Other Sigma Lens");
        _lensTypeById.put(488, "Canon EF-S 15-85mm f/3.5-5.6 IS USM");
        _lensTypeById.put(489, "Canon EF 70-300mm f/4-5.6L IS USM");
        _lensTypeById.put(490, "Canon EF 8-15mm f/4L Fisheye USM");
        _lensTypeById.put(491, "Canon EF 300mm f/2.8L IS II USM");
        _lensTypeById.put(492, "Canon EF 400mm f/2.8L IS II USM");
        _lensTypeById.put(493, "Canon EF 500mm f/4L IS II USM or EF 24-105mm f4L IS USM");
        _lensTypeById.put(494, "Canon EF 600mm f/4.0L IS II USM");
        _lensTypeById.put(495, "Canon EF 24-70mm f/2.8L II USM");
        _lensTypeById.put(496, "Canon EF 200-400mm f/4L IS USM");
        _lensTypeById.put(499, "Canon EF 200-400mm f/4L IS USM + 1.4x");
        _lensTypeById.put(502, "Canon EF 28mm f/2.8 IS USM");
        _lensTypeById.put(503, "Canon EF 24mm f/2.8 IS USM");
        _lensTypeById.put(504, "Canon EF 24-70mm f/4L IS USM");
        _lensTypeById.put(505, "Canon EF 35mm f/2 IS USM");
        _lensTypeById.put(506, "Canon EF 400mm f/4 DO IS II USM");
        _lensTypeById.put(507, "Canon EF 16-35mm f/4L IS USM");
        _lensTypeById.put(508, "Canon EF 11-24mm f/4L USM");
        _lensTypeById.put(747, "Canon EF 100-400mm f/4.5-5.6L IS II USM");
        _lensTypeById.put(750, "Canon EF 35mm f/1.4L II USM");
        _lensTypeById.put(4142, "Canon EF-S 18-135mm f/3.5-5.6 IS STM");
        _lensTypeById.put(4143, "Canon EF-M 18-55mm f/3.5-5.6 IS STM or Tamron Lens");
        _lensTypeById.put(4144, "Canon EF 40mm f/2.8 STM");
        _lensTypeById.put(4145, "Canon EF-M 22mm f/2 STM");
        _lensTypeById.put(4146, "Canon EF-S 18-55mm f/3.5-5.6 IS STM");
        _lensTypeById.put(4147, "Canon EF-M 11-22mm f/4-5.6 IS STM");
        _lensTypeById.put(4148, "Canon EF-S 55-250mm f/4-5.6 IS STM");
        _lensTypeById.put(4149, "Canon EF-M 55-200mm f/4.5-6.3 IS STM");
        _lensTypeById.put(4150, "Canon EF-S 10-18mm f/4.5-5.6 IS STM");
        _lensTypeById.put(4152, "Canon EF 24-105mm f/3.5-5.6 IS STM");
        _lensTypeById.put(4153, "Canon EF-M 15-45mm f/3.5-6.3 IS STM");
        _lensTypeById.put(4154, "Canon EF-S 24mm f/2.8 STM");
        _lensTypeById.put(4156, "Canon EF 50mm f/1.8 STM");
        _lensTypeById.put(36912, "Canon EF-S 18-135mm f/3.5-5.6 IS USM");
        _lensTypeById.put(65535, "N/A");
    }
}

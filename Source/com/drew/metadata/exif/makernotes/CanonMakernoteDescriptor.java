/*
 * Copyright 2002-2015 Drew Noakes
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

import static com.drew.metadata.exif.makernotes.CanonMakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link CanonMakernoteDirectory}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
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

        return ((isNegative) ? "-" : "") + Float.toString(value / 32f) + " EV";
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
        if (((value >> 14) & 1) > 0) {
            return "External E-TTL";
        }
        if (((value >> 13) & 1) > 0) {
            return "Internal flash";
        }
        if (((value >> 11) & 1) > 0) {
            return "FP sync used";
        }
        if (((value >> 4) & 1) > 0) {
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
        if ((value & canonMask) > 0)
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
            // TODO find an image that tests this calculation
            return Double.toString((double)value * 0.1d) + " sec";
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
}

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
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string representations of tag values stored in a <code>CanonMakernoteDirectory</code>.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class CanonMakernoteDescriptor extends TagDescriptor<CanonMakernoteDirectory>
{
    public CanonMakernoteDescriptor(@NotNull CanonMakernoteDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case CanonMakernoteDirectory.TAG_CANON_SERIAL_NUMBER:
                return getSerialNumberDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_FLASH_ACTIVITY:
                return getFlashActivityDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_FOCUS_TYPE:
                return getFocusTypeDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_QUALITY:
                return getQualityDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_MACRO_MODE:
                return getMacroModeDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_SELF_TIMER_DELAY:
                return getSelfTimerDelayDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_FLASH_MODE:
                return getFlashModeDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_CONTINUOUS_DRIVE_MODE:
                return getContinuousDriveModeDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_FOCUS_MODE_1:
                return getFocusMode1Description();
            case CanonMakernoteDirectory.CameraSettings.TAG_IMAGE_SIZE:
                return getImageSizeDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_EASY_SHOOTING_MODE:
                return getEasyShootingModeDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_CONTRAST:
                return getContrastDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_SATURATION:
                return getSaturationDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_SHARPNESS:
                return getSharpnessDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_ISO:
                return getIsoDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_METERING_MODE:
                return getMeteringModeDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_AF_POINT_SELECTED:
                return getAfPointSelectedDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_EXPOSURE_MODE:
                return getExposureModeDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_LONG_FOCAL_LENGTH:
                return getLongFocalLengthDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_SHORT_FOCAL_LENGTH:
                return getShortFocalLengthDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_FOCAL_UNITS_PER_MM:
                return getFocalUnitsPerMillimetreDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_FLASH_DETAILS:
                return getFlashDetailsDescription();
            case CanonMakernoteDirectory.CameraSettings.TAG_FOCUS_MODE_2:
                return getFocusMode2Description();
            case CanonMakernoteDirectory.FocalLength.TAG_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case CanonMakernoteDirectory.FocalLength.TAG_AF_POINT_USED:
                return getAfPointUsedDescription();
            case CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS:
                return getFlashBiasDescription();

            // It turns out that these values are dependent upon the camera model and therefore the below code was
            // incorrect for some Canon models.  This needs to be revisited.

//            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_LONG_EXPOSURE_NOISE_REDUCTION:
//                return getLongExposureNoiseReductionDescription();
//            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_AUTO_EXPOSURE_LOCK_BUTTONS:
//                return getShutterAutoExposureLockButtonDescription();
//            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_MIRROR_LOCKUP:
//                return getMirrorLockupDescription();
//            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_TV_AV_AND_EXPOSURE_LEVEL:
//                return getTvAndAvExposureLevelDescription();
//            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_AF_ASSIST_LIGHT:
//                return getAutoFocusAssistLightDescription();
//            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_SPEED_IN_AV_MODE:
//                return getShutterSpeedInAvModeDescription();
//            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_BRACKETTING:
//                return getAutoExposureBrackettingSequenceAndAutoCancellationDescription();
//            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_CURTAIN_SYNC:
//                return getShutterCurtainSyncDescription();
//            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_AF_STOP:
//                return getLensAutoFocusStopButtonDescription();
//            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_FILL_FLASH_REDUCTION:
//                return getFillFlashReductionDescription();
//            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_MENU_BUTTON_RETURN:
//                return getMenuButtonReturnPositionDescription();
//            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SET_BUTTON_FUNCTION:
//                return getSetButtonFunctionWhenShootingDescription();
//            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SENSOR_CLEANING:
//                return getSensorCleaningDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getSerialNumberDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.TAG_CANON_SERIAL_NUMBER);
        if (value==null)
            return null;
        return String.format("%04X%05d", (value >> 8) & 0xFF, value & 0xFF);
    }

/*
    @Nullable
    public String getLongExposureNoiseReductionDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_LONG_EXPOSURE_NOISE_REDUCTION);
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_AUTO_EXPOSURE_LOCK_BUTTONS);
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_MIRROR_LOCKUP);
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_TV_AV_AND_EXPOSURE_LEVEL);
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_AF_ASSIST_LIGHT);
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_SPEED_IN_AV_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 0:     return "Automatic";
            case 1:     return "1/200 (fixed)";
            default:    return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getAutoExposureBrackettingSequenceAndAutoCancellationDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_BRACKETTING);
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_CURTAIN_SYNC);
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_AF_STOP);
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_FILL_FLASH_REDUCTION);
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_MENU_BUTTON_RETURN);
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SET_BUTTON_FUNCTION);
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SENSOR_CLEANING);
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.FocalLength.TAG_FLASH_BIAS);

        if (value==null)
            return null;

        boolean isNegative = false;
        if (value > 0xF000)
        {
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.FocalLength.TAG_AF_POINT_USED);
        if (value==null)
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.FocalLength.TAG_WHITE_BALANCE);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Auto";
            case 1:
                return "Sunny";
            case 2:
                return "Cloudy";
            case 3:
                return "Tungsten";
            case 4:
                return "Florescent";
            case 5:
                return "Flash";
            case 6:
                return "Custom";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFocusMode2Description()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_FOCUS_MODE_2);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Single";
            case 1:
                return "Continuous";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFlashDetailsDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_FLASH_DETAILS);
        if (value==null)
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_FOCAL_UNITS_PER_MM);
        if (value==null)
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_SHORT_FOCAL_LENGTH);
        if (value==null)
            return null;
        String units = getFocalUnitsPerMillimetreDescription();
        return Integer.toString(value) + " " + units;
    }

    @Nullable
    public String getLongFocalLengthDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_LONG_FOCAL_LENGTH);
        if (value==null)
            return null;
        String units = getFocalUnitsPerMillimetreDescription();
        return Integer.toString(value) + " " + units;
    }

    @Nullable
    public String getExposureModeDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_EXPOSURE_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Easy shooting";
            case 1:
                return "Program";
            case 2:
                return "Tv-priority";
            case 3:
                return "Av-priority";
            case 4:
                return "Manual";
            case 5:
                return "A-DEP";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getAfPointSelectedDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_AF_POINT_SELECTED);
        if (value==null)
            return null;
        switch (value) {
            case 0x3000:
                return "None (MF)";
            case 0x3001:
                return "Auto selected";
            case 0x3002:
                return "Right";
            case 0x3003:
                return "Centre";
            case 0x3004:
                return "Left";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getMeteringModeDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_METERING_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 3:
                return "Evaluative";
            case 4:
                return "Partial";
            case 5:
                return "Centre weighted";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getIsoDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_ISO);
        if (value==null)
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_SHARPNESS);
        if (value==null)
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_SATURATION);
        if (value==null)
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_CONTRAST);
        if (value==null)
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_EASY_SHOOTING_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Full auto";
            case 1:
                return "Manual";
            case 2:
                return "Landscape";
            case 3:
                return "Fast shutter";
            case 4:
                return "Slow shutter";
            case 5:
                return "Night";
            case 6:
                return "B&W";
            case 7:
                return "Sepia";
            case 8:
                return "Portrait";
            case 9:
                return "Sports";
            case 10:
                return "Macro / Closeup";
            case 11:
                return "Pan focus";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getImageSizeDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_IMAGE_SIZE);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Large";
            case 1:
                return "Medium";
            case 2:
                return "Small";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFocusMode1Description()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_FOCUS_MODE_1);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "One-shot";
            case 1:
                return "AI Servo";
            case 2:
                return "AI Focus";
            case 3:
                return "Manual Focus";
            case 4:
                // TODO should check field 32 here (FOCUS_MODE_2)
                return "Single";
            case 5:
                return "Continuous";
            case 6:
                return "Manual Focus";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getContinuousDriveModeDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_CONTINUOUS_DRIVE_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                final Integer delay = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_SELF_TIMER_DELAY);
                if (delay!=null)
                    return delay == 0 ? "Single shot" : "Single shot with self-timer";
            case 1:
                return "Continuous";
        }
        return "Unknown (" + value + ")";
    }

    @Nullable
    public String getFlashModeDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_FLASH_MODE);
        if (value==null)
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_SELF_TIMER_DELAY);
        if (value==null)
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_MACRO_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 1:
                return "Macro";
            case 2:
                return "Normal";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getQualityDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_QUALITY);
        if (value==null)
            return null;
        switch (value) {
            case 2:
                return "Normal";
            case 3:
                return "Fine";
            case 5:
                return "Superfine";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getDigitalZoomDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_DIGITAL_ZOOM);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "No digital zoom";
            case 1:
                return "2x";
            case 2:
                return "4x";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFocusTypeDescription()
    {
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_FOCUS_TYPE);
        if (value==null)
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
        Integer value = _directory.getInteger(CanonMakernoteDirectory.CameraSettings.TAG_FLASH_ACTIVITY);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Flash did not fire";
            case 1:
                return "Flash fired";
            default:
                return "Unknown (" + value + ")";
        }
    }
}

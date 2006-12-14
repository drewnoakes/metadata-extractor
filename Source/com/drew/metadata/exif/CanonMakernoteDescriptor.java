/*
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 27-Nov-2002 10:12:05 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string represenations of tag values stored in a <code>CanonMakernoteDirectory</code>.
 */
public class CanonMakernoteDescriptor extends TagDescriptor
{
    public CanonMakernoteDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType) {
            case CanonMakernoteDirectory.TAG_CANON_STATE1_FLASH_ACTIVITY:
                return getFlashActivityDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_FOCUS_TYPE:
                return getFocusTypeDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_QUALITY:
                return getQualityDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_MACRO_MODE:
                return getMacroModeDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_SELF_TIMER_DELAY:
                return getSelfTimerDelayDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_FLASH_MODE:
                return getFlashModeDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_CONTINUOUS_DRIVE_MODE:
                return getContinuousDriveModeDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_FOCUS_MODE_1:
                return getFocusMode1Description();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_IMAGE_SIZE:
                return getImageSizeDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_EASY_SHOOTING_MODE:
                return getEasyShootingModeDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_CONTRAST:
                return getContrastDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_SATURATION:
                return getSaturationDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_SHARPNESS:
                return getSharpnessDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_ISO:
                return getIsoDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_METERING_MODE:
                return getMeteringModeDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_AF_POINT_SELECTED:
                return getAfPointSelectedDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_EXPOSURE_MODE:
                return getExposureModeDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_LONG_FOCAL_LENGTH:
                return getLongFocalLengthDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_SHORT_FOCAL_LENGTH:
                return getShortFocalLengthDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_FOCAL_UNITS_PER_MM:
                return getFocalUnitsPerMillimetreDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_FLASH_DETAILS:
                return getFlashDetailsDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE1_FOCUS_MODE_2:
                return getFocusMode2Description();
            case CanonMakernoteDirectory.TAG_CANON_STATE2_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE2_AF_POINT_USED:
                return getAfPointUsedDescription();
            case CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS:
                return getFlashBiasDescription();
            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_LONG_EXPOSURE_NOISE_REDUCTION:
                return getLongExposureNoiseReductionDescription();
            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_AUTO_EXPOSURE_LOCK_BUTTONS:
                return getShutterAutoExposureLockButtonDescription();
            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_MIRROR_LOCKUP:
                return getMirrorLockupDescription();
            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_TV_AV_AND_EXPOSURE_LEVEL:
                return getTvAndAvExposureLevelDescription();
            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_AF_ASSIST_LIGHT:
                return getAutoFocusAssistLightDescription();
            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_SPEED_IN_AV_MODE:
                return getShutterSpeedInAvModeDescription();
            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_BRACKETTING:
                return getAutoExposureBrackettingSequenceAndAutoCancellationDescription();
            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_CURTAIN_SYNC:
                return getShutterCurtainSyncDescription();
            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_AF_STOP:
                return getLensAutoFocusStopButtonDescription();
            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_FILL_FLASH_REDUCTION:
                return getFillFlashReductionDescription();
            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_MENU_BUTTON_RETURN:
                return getMenuButtonReturnPositionDescription();
            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SET_BUTTON_FUNCTION:
                return getSetButtonFunctionWhenShootingDescription();
            case CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SENSOR_CLEANING:
                return getSensorCleaningDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    public String getLongExposureNoiseReductionDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_LONG_EXPOSURE_NOISE_REDUCTION)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_LONG_EXPOSURE_NOISE_REDUCTION);
        switch (value) {
            case 0:     return "Off";
            case 1:     return "On";
            default:    return "Unknown (" + value + ")";
        }
    }
    public String getShutterAutoExposureLockButtonDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_AUTO_EXPOSURE_LOCK_BUTTONS)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_AUTO_EXPOSURE_LOCK_BUTTONS);
        switch (value) {
            case 0:     return "AF/AE lock";
            case 1:     return "AE lock/AF";
            case 2:     return "AF/AF lock";
            case 3:     return "AE+release/AE+AF";
            default:    return "Unknown (" + value + ")";
        }
    }
    public String getMirrorLockupDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_MIRROR_LOCKUP)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_MIRROR_LOCKUP);
        switch (value) {
            case 0:     return "Disabled";
            case 1:     return "Enabled";
            default:    return "Unknown (" + value + ")";
        }
    }
    public String getTvAndAvExposureLevelDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_TV_AV_AND_EXPOSURE_LEVEL)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_TV_AV_AND_EXPOSURE_LEVEL);
        switch (value) {
            case 0:     return "1/2 stop";
            case 1:     return "1/3 stop";
            default:    return "Unknown (" + value + ")";
        }
    }
    public String getAutoFocusAssistLightDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_AF_ASSIST_LIGHT)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_AF_ASSIST_LIGHT);
        switch (value) {
            case 0:     return "On (Auto)";
            case 1:     return "Off";
            default:    return "Unknown (" + value + ")";
        }
    }
    public String getShutterSpeedInAvModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_SPEED_IN_AV_MODE)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_SPEED_IN_AV_MODE);
        switch (value) {
            case 0:     return "Automatic";
            case 1:     return "1/200 (fixed)";
            default:    return "Unknown (" + value + ")";
        }
    }
    public String getAutoExposureBrackettingSequenceAndAutoCancellationDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_BRACKETTING)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_BRACKETTING);
        switch (value) {
            case 0:     return "0,-,+ / Enabled";
            case 1:     return "0,-,+ / Disabled";
            case 2:     return "-,0,+ / Enabled";
            case 3:     return "-,0,+ / Disabled";
            default:    return "Unknown (" + value + ")";
        }
    }
    public String getShutterCurtainSyncDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_CURTAIN_SYNC)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SHUTTER_CURTAIN_SYNC);
        switch (value) {
            case 0:     return "1st Curtain Sync";
            case 1:     return "2nd Curtain Sync";
            default:    return "Unknown (" + value + ")";
        }
    }
    public String getLensAutoFocusStopButtonDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_AF_STOP)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_AF_STOP);
        switch (value) {
            case 0:     return "AF stop";
            case 1:     return "Operate AF";
            case 2:     return "Lock AE and start timer";
            default:    return "Unknown (" + value + ")";
        }
    }
    public String getFillFlashReductionDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_FILL_FLASH_REDUCTION)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_FILL_FLASH_REDUCTION);
        switch (value) {
            case 0:     return "Enabled";
            case 1:     return "Disabled";
            default:    return "Unknown (" + value + ")";
        }
    }
    public String getMenuButtonReturnPositionDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_MENU_BUTTON_RETURN)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_MENU_BUTTON_RETURN);
        switch (value) {
            case 0:     return "Top";
            case 1:     return "Previous (volatile)";
            case 2:     return "Previous";
            default:    return "Unknown (" + value + ")";
        }
    }
    public String getSetButtonFunctionWhenShootingDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SET_BUTTON_FUNCTION)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SET_BUTTON_FUNCTION);
        switch (value) {
            case 0:     return "Not Assigned";
            case 1:     return "Change Quality";
            case 2:     return "Change ISO Speed";
            case 3:     return "Select Parameters";
            default:    return "Unknown (" + value + ")";
        }
    }
    public String getSensorCleaningDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SENSOR_CLEANING)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_CUSTOM_FUNCTION_SENSOR_CLEANING);
        switch (value) {
            case 0:     return "Disabled";
            case 1:     return "Enabled";
            default:    return "Unknown (" + value + ")";
        }
    }

    public String getFlashBiasDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS)) return null;

        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS);

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

    public String getAfPointUsedDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE2_AF_POINT_USED)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE2_AF_POINT_USED);
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

    public String getWhiteBalanceDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE2_WHITE_BALANCE)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE2_WHITE_BALANCE);
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
                return "Flourescent";
            case 5:
                return "Flash";
            case 6:
                return "Custom";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getFocusMode2Description() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_FOCUS_MODE_2)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_FOCUS_MODE_2);
        switch (value) {
            case 0:
                return "Single";
            case 1:
                return "Continuous";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getFlashDetailsDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_FLASH_DETAILS)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_FLASH_DETAILS);
        if (((value << 14) & 1) > 0) {
            return "External E-TTL";
        }
        if (((value << 13) & 1) > 0) {
            return "Internal flash";
        }
        if (((value << 11) & 1) > 0) {
            return "FP sync used";
        }
        if (((value << 4) & 1) > 0) {
            return "FP sync enabled";
        }
        return "Unknown (" + value + ")";
    }

    public String getFocalUnitsPerMillimetreDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_FOCAL_UNITS_PER_MM)) return "";
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_FOCAL_UNITS_PER_MM);
        if (value != 0) {
            return Integer.toString(value);
        } else {
            return "";
        }
    }

    public String getShortFocalLengthDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_SHORT_FOCAL_LENGTH)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_SHORT_FOCAL_LENGTH);
        String units = getFocalUnitsPerMillimetreDescription();
        return Integer.toString(value) + " " + units;
    }

    public String getLongFocalLengthDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_LONG_FOCAL_LENGTH)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_LONG_FOCAL_LENGTH);
        String units = getFocalUnitsPerMillimetreDescription();
        return Integer.toString(value) + " " + units;
    }

    public String getExposureModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_EXPOSURE_MODE)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_EXPOSURE_MODE);
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

    public String getAfPointSelectedDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_AF_POINT_SELECTED)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_AF_POINT_SELECTED);
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

    public String getMeteringModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_METERING_MODE)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_METERING_MODE);
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

    public String getIsoDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_ISO)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_ISO);
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

    public String getSharpnessDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_SHARPNESS)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_SHARPNESS);
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

    public String getSaturationDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_SATURATION)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_SATURATION);
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

    public String getContrastDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_CONTRAST)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_CONTRAST);
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

    public String getEasyShootingModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_EASY_SHOOTING_MODE)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_EASY_SHOOTING_MODE);
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

    public String getImageSizeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_IMAGE_SIZE)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_IMAGE_SIZE);
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

    public String getFocusMode1Description() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_FOCUS_MODE_1)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_FOCUS_MODE_1);
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

    public String getContinuousDriveModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_CONTINUOUS_DRIVE_MODE)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_CONTINUOUS_DRIVE_MODE);
        switch (value) {
            case 0:
                if (_directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_SELF_TIMER_DELAY) == 0) {
                    return "Single shot";
                } else {
                    return "Single shot with self-timer";
                }
            case 1:
                return "Continuous";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getFlashModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_FLASH_MODE)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_FLASH_MODE);
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
                return "Extenal flash";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getSelfTimerDelayDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_SELF_TIMER_DELAY)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_SELF_TIMER_DELAY);
        if (value == 0) {
            return "Self timer not used";
        } else {
            // TODO find an image that tests this calculation
            return Double.toString((double)value * 0.1d) + " sec";
        }
    }

    public String getMacroModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_MACRO_MODE)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_MACRO_MODE);
        switch (value) {
            case 1:
                return "Macro";
            case 2:
                return "Normal";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getQualityDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_QUALITY)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_QUALITY);
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

    public String getDigitalZoomDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_DIGITAL_ZOOM)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_DIGITAL_ZOOM);
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

    public String getFocusTypeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_FOCUS_TYPE)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_FOCUS_TYPE);
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

    public String getFlashActivityDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_FLASH_ACTIVITY)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_FLASH_ACTIVITY);
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

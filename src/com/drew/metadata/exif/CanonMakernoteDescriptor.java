/*
 * Created by dnoakes on 27-Nov-2002 10:12:05 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 *
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
            default:
                return _directory.getString(tagType);
        }
    }

    private String getFlashBiasDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE2_FLASH_BIAS);
        switch (value) {
            case 0xffc0:
                return "-2 EV";
            case 0xffcc:
                return "-1.67 EV";
            case 0xffd0:
                return "-1.50 EV";
            case 0xffd4:
                return "-1.33 EV";
            case 0xffe0:
                return "-1 EV";
            case 0xffec:
                return "-0.67 EV";
            case 0xfff0:
                return "-0.50 EV";
            case 0xfff4:
                return "-0.33 EV";
            case 0x0000:
                return "0 EV";
            case 0x000c:
                return "0.33 EV";
            case 0x0010:
                return "0.50 EV";
            case 0x0014:
                return "0.67 EV";
            case 0x0020:
                return "1 EV";
            case 0x002c:
                return "1.33 EV";
            case 0x0030:
                return "1.50 EV";
            case 0x0034:
                return "1.67 EV";
            case 0x0040:
                return "2 EV";
            default:
                return "Unknown (" + value + ")";
        }
    }

    private String getAfPointUsedDescription() throws MetadataException
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

    private String getWhiteBalanceDescription() throws MetadataException
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

    private String getFocusMode2Description() throws MetadataException
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

    private String getFlashDetailsDescription() throws MetadataException
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

    private String getFocalUnitsPerMillimetreDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_FOCAL_UNITS_PER_MM)) return "";
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_FOCAL_UNITS_PER_MM);
        if (value != 0) {
            return Integer.toString(value);
        } else {
            return "";
        }
    }

    private String getShortFocalLengthDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_SHORT_FOCAL_LENGTH)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_SHORT_FOCAL_LENGTH);
        String units = getFocalUnitsPerMillimetreDescription();
        return Integer.toString(value) + " " + units;
    }

    private String getLongFocalLengthDescription() throws MetadataException
    {
        if (!_directory.containsTag(CanonMakernoteDirectory.TAG_CANON_STATE1_LONG_FOCAL_LENGTH)) return null;
        int value = _directory.getInt(CanonMakernoteDirectory.TAG_CANON_STATE1_LONG_FOCAL_LENGTH);
        String units = getFocalUnitsPerMillimetreDescription();
        return Integer.toString(value) + " " + units;
    }

    private String getExposureModeDescription() throws MetadataException
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

    private String getAfPointSelectedDescription() throws MetadataException
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

    private String getMeteringModeDescription() throws MetadataException
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

    private String getIsoDescription() throws MetadataException
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

    private String getSharpnessDescription() throws MetadataException
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

    private String getSaturationDescription() throws MetadataException
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

    private String getContrastDescription() throws MetadataException
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

    private String getEasyShootingModeDescription() throws MetadataException
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

    private String getImageSizeDescription() throws MetadataException
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

    private String getFocusMode1Description() throws MetadataException
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
                return "MF";
            case 4:
                // TODO should check field 32 here (FOCUS_MODE_2)
                return "Single";
            case 5:
                return "Continuous";
            case 6:
                return "MF";
            default:
                return "Unknown (" + value + ")";
        }
    }

    private String getContinuousDriveModeDescription() throws MetadataException
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

    private String getFlashModeDescription() throws MetadataException
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

    private String getSelfTimerDelayDescription() throws MetadataException
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

    private String getMacroModeDescription() throws MetadataException
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
}

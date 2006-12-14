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
 * Created by dnoakes on 27-Nov-2002 10:10:47 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes tags specific to Canon cameras.
 *
 * Thanks to Bill Richards for his contribution to this makernote directory.
 *
 * Many tag definitions explained here: http://www.ozhiker.com/electronics/pjmt/jpeg_info/canon_mn.html
 */
public class CanonMakernoteDirectory extends Directory
{
    // CANON cameras have some funny bespoke fields that need further processing...
    public static final int TAG_CANON_CAMERA_STATE_1 = 0x0001;
    public static final int TAG_CANON_CAMERA_STATE_2 = 0x0004;

    public static final int TAG_CANON_IMAGE_TYPE = 0x0006;
    public static final int TAG_CANON_FIRMWARE_VERSION = 0x0007;
    public static final int TAG_CANON_IMAGE_NUMBER = 0x0008;
    public static final int TAG_CANON_OWNER_NAME = 0x0009;
    /**
     * To display serial number as on camera use: printf( "%04X%05d", highbyte, lowbyte )
     * TODO handle this in CanonMakernoteDescriptor
     */
    public static final int TAG_CANON_SERIAL_NUMBER = 0x000C;
    public static final int TAG_CANON_UNKNOWN_1 = 0x000D;
    public static final int TAG_CANON_CUSTOM_FUNCTIONS = 0x000F;

    // These 'sub'-tag values have been created for consistency -- they don't exist within the exif segment
    /**
     * 1 = Macro
     * 2 = Normal
     */
    public static final int TAG_CANON_STATE1_MACRO_MODE = 0xC101;
    public static final int TAG_CANON_STATE1_SELF_TIMER_DELAY = 0xC102;
    /**
     * 2 = Normal
     * 3 = Fine
     * 5 = Superfine
     */
    public static final int TAG_CANON_STATE1_QUALITY = 0xC103;
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
    public static final int TAG_CANON_STATE1_FLASH_MODE = 0xC104;
    /**
     * 0 = Single Frame or Timer Mode
     * 1 = Continuous
     */
    public static final int TAG_CANON_STATE1_CONTINUOUS_DRIVE_MODE = 0xC105;
    public static final int TAG_CANON_STATE1_UNKNOWN_2 = 0xC106;
    /**
     * 0 = One-Shot
     * 1 = AI Servo
     * 2 = AI Focus
     * 3 = Manual Focus
     * 4 = Single
     * 5 = Continuous
     * 6 = Manual Focus
     */
    public static final int TAG_CANON_STATE1_FOCUS_MODE_1 = 0xC107;
    public static final int TAG_CANON_STATE1_UNKNOWN_3 = 0xC108;
    public static final int TAG_CANON_STATE1_UNKNOWN_4 = 0xC109;
    /**
     * 0 = Large
     * 1 = Medium
     * 2 = Small
     */
    public static final int TAG_CANON_STATE1_IMAGE_SIZE = 0xC10A;
    /**
     * 0 = Full Auto
     * 1 = Manual
     * 2 = Landscape
     * 3 = Fast Shutter
     * 4 = Slow Shutter
     * 5 = Night
     * 6 = Black & White
     * 7 = Sepia
     * 8 = Portrait
     * 9 = Sports
     * 10 = Macro / Close-Up
     * 11 = Pan Focus
     */
    public static final int TAG_CANON_STATE1_EASY_SHOOTING_MODE = 0xC10B;
    /**
     * 0 = No Digital Zoom
     * 1 = 2x
     * 2 = 4x
     */
    public static final int TAG_CANON_STATE1_DIGITAL_ZOOM = 0xC10C;
    /**
     * 0 = Normal
     * 1 = High
     * 65535 = Low
     */
    public static final int TAG_CANON_STATE1_CONTRAST = 0xC10D;
    /**
     * 0 = Normal
     * 1 = High
     * 65535 = Low
     */
    public static final int TAG_CANON_STATE1_SATURATION = 0xC10E;
    /**
     * 0 = Normal
     * 1 = High
     * 65535 = Low
     */
    public static final int TAG_CANON_STATE1_SHARPNESS = 0xC10F;
    /**
     * 0 = Check ISOSpeedRatings EXIF tag for ISO Speed
     * 15 = Auto ISO
     * 16 = ISO 50
     * 17 = ISO 100
     * 18 = ISO 200
     * 19 = ISO 400
     */
    public static final int TAG_CANON_STATE1_ISO = 0xC110;
    /**
     * 3 = Evaluative
     * 4 = Partial
     * 5 = Centre Weighted
     */
    public static final int TAG_CANON_STATE1_METERING_MODE = 0xC111;
    /**
     * 0 = Manual
     * 1 = Auto
     * 3 = Close-up (Macro)
     * 8 = Locked (Pan Mode)
     */
    public static final int TAG_CANON_STATE1_FOCUS_TYPE = 0xC112;
    /**
     * 12288 = None (Manual Focus)
     * 12289 = Auto Selected
     * 12290 = Right
     * 12291 = Centre
     * 12292 = Left
     */
    public static final int TAG_CANON_STATE1_AF_POINT_SELECTED = 0xC113;
    /**
     * 0 = Easy Shooting (See Easy Shooting Mode)
     * 1 = Program
     * 2 = Tv-Priority
     * 3 = Av-Priority
     * 4 = Manual
     * 5 = A-DEP
     */
    public static final int TAG_CANON_STATE1_EXPOSURE_MODE = 0xC114;
    public static final int TAG_CANON_STATE1_UNKNOWN_7 = 0xC115;
    public static final int TAG_CANON_STATE1_UNKNOWN_8 = 0xC116;
    public static final int TAG_CANON_STATE1_LONG_FOCAL_LENGTH = 0xC117;
    public static final int TAG_CANON_STATE1_SHORT_FOCAL_LENGTH = 0xC118;
    public static final int TAG_CANON_STATE1_FOCAL_UNITS_PER_MM = 0xC119;
    public static final int TAG_CANON_STATE1_UNKNOWN_9 = 0xC11A;
    public static final int TAG_CANON_STATE1_UNKNOWN_10 = 0xC11B;
    /**
     * 0 = Flash Did Not Fire
     * 1 = Flash Fired
     */
    public static final int TAG_CANON_STATE1_FLASH_ACTIVITY = 0xC11C;
    public static final int TAG_CANON_STATE1_FLASH_DETAILS = 0xC11D;
    public static final int TAG_CANON_STATE1_UNKNOWN_12 = 0xC11E;
    public static final int TAG_CANON_STATE1_UNKNOWN_13 = 0xC11F;
    /**
     * 0 = Focus Mode: Single
     * 1 = Focus Mode: Continuous
     */
    public static final int TAG_CANON_STATE1_FOCUS_MODE_2 = 0xC120;

    /**
     * 0 = Auto
     * 1 = Sunny
     * 2 = Cloudy
     * 3 = Tungsten
     * 4 = Flourescent
     * 5 = Flash
     * 6 = Custom
     */
    public static final int TAG_CANON_STATE2_WHITE_BALANCE = 0xC207;
    public static final int TAG_CANON_STATE2_SEQUENCE_NUMBER = 0xC209;
    public static final int TAG_CANON_STATE2_AF_POINT_USED = 0xC20E;
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
    public static final int TAG_CANON_STATE2_FLASH_BIAS = 0xC20F;
    public static final int TAG_CANON_STATE2_AUTO_EXPOSURE_BRACKETING = 0xC210;
    public static final int TAG_CANON_STATE2_AEB_BRACKET_VALUE = 0xC211;
    public static final int TAG_CANON_STATE2_SUBJECT_DISTANCE = 0xC213;

    /**
     * Long Exposure Noise Reduction
     * 0 = Off
     * 1 = On
     */
    public static final int TAG_CANON_CUSTOM_FUNCTION_LONG_EXPOSURE_NOISE_REDUCTION = 0xC301;

    /**
     * Shutter/Auto Exposure-lock buttons
     * 0 = AF/AE lock
     * 1 = AE lock/AF
     * 2 = AF/AF lock
     * 3 = AE+release/AE+AF
     */
    public static final int TAG_CANON_CUSTOM_FUNCTION_SHUTTER_AUTO_EXPOSURE_LOCK_BUTTONS = 0xC302;

    /**
     * Mirror lockup
     * 0 = Disable
     * 1 = Enable
     */
    public static final int TAG_CANON_CUSTOM_FUNCTION_MIRROR_LOCKUP = 0xC303;

    /**
     * Tv/Av and exposure level
     * 0 = 1/2 stop
     * 1 = 1/3 stop
     */
    public static final int TAG_CANON_CUSTOM_FUNCTION_TV_AV_AND_EXPOSURE_LEVEL = 0xC304;

    /**
     * AF-assist light
     * 0 = On (Auto)
     * 1 = Off
     */
    public static final int TAG_CANON_CUSTOM_FUNCTION_AF_ASSIST_LIGHT = 0xC305;

    /**
     * Shutter speed in Av mode
     * 0 = Automatic
     * 1 = 1/200 (fixed)
     */
    public static final int TAG_CANON_CUSTOM_FUNCTION_SHUTTER_SPEED_IN_AV_MODE = 0xC306;

    /**
     * Auto-Exposure Bracketting sequence/auto cancellation
     * 0 = 0,-,+ / Enabled
     * 1 = 0,-,+ / Disabled
     * 2 = -,0,+ / Enabled
     * 3 = -,0,+ / Disabled
     */
    public static final int TAG_CANON_CUSTOM_FUNCTION_BRACKETTING = 0xC307;

    /**
     * Shutter Curtain Sync
     * 0 = 1st Curtain Sync
     * 1 = 2nd Curtain Sync
     */
    public static final int TAG_CANON_CUSTOM_FUNCTION_SHUTTER_CURTAIN_SYNC = 0xC308;

    /**
     * Lens Auto-Focus stop button Function Switch
     * 0 = AF stop
     * 1 = Operate AF
     * 2 = Lock AE and start timer
     */
    public static final int TAG_CANON_CUSTOM_FUNCTION_AF_STOP = 0xC309;

    /**
     * Auto reduction of fill flash
     * 0 = Enable
     * 1 = Disable
     */
    public static final int TAG_CANON_CUSTOM_FUNCTION_FILL_FLASH_REDUCTION = 0xC30A;

    /**
     * Menu button return position
     * 0 = Top
     * 1 = Previous (volatile)
     * 2 = Previous
     */
    public static final int TAG_CANON_CUSTOM_FUNCTION_MENU_BUTTON_RETURN = 0xC30B;

    /**
     * SET button function when shooting
     * 0 = Not Assigned
     * 1 = Change Quality
     * 2 = Change ISO Speed
     * 3 = Select Parameters
     */
    public static final int TAG_CANON_CUSTOM_FUNCTION_SET_BUTTON_FUNCTION = 0xC30C;

    /**
     * Sensor cleaning
     * 0 = Disable
     * 1 = Enable
     */
    public static final int TAG_CANON_CUSTOM_FUNCTION_SENSOR_CLEANING = 0xC30D;

    // 9  A  B  C  D  E  F  10 11 12 13
    // 9  10 11 12 13 14 15 16 17 18 19
    protected static final HashMap _tagNameMap = new HashMap();

    static
    {
        _tagNameMap.put(new Integer(TAG_CANON_FIRMWARE_VERSION), "Firmware Version");
        _tagNameMap.put(new Integer(TAG_CANON_IMAGE_NUMBER), "Image Number");
        _tagNameMap.put(new Integer(TAG_CANON_IMAGE_TYPE), "Image Type");
        _tagNameMap.put(new Integer(TAG_CANON_OWNER_NAME), "Owner Name");
        _tagNameMap.put(new Integer(TAG_CANON_UNKNOWN_1), "Makernote Unknown 1");
        _tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTIONS), "Custom Functions");
        _tagNameMap.put(new Integer(TAG_CANON_SERIAL_NUMBER), "Camera Serial Number");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_AF_POINT_SELECTED), "AF Point Selected");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_CONTINUOUS_DRIVE_MODE), "Continuous Drive Mode");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_CONTRAST), "Contrast");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_EASY_SHOOTING_MODE), "Easy Shooting Mode");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_EXPOSURE_MODE), "Exposure Mode");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_FLASH_DETAILS), "Flash Details");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_FLASH_MODE), "Flash Mode");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_FOCAL_UNITS_PER_MM), "Focal Units per mm");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_FOCUS_MODE_1), "Focus Mode");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_FOCUS_MODE_2), "Focus Mode");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_IMAGE_SIZE), "Image Size");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_ISO), "Iso");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_LONG_FOCAL_LENGTH), "Long Focal Length");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_MACRO_MODE), "Macro Mode");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_METERING_MODE), "Metering Mode");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_SATURATION), "Saturation");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_SELF_TIMER_DELAY), "Self Timer Delay");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_SHARPNESS), "Sharpness");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_SHORT_FOCAL_LENGTH), "Short Focal Length");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_QUALITY), "Quality");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_2), "Unknown Camera State 2");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_3), "Unknown Camera State 3");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_4), "Unknown Camera State 4");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_DIGITAL_ZOOM), "Digital Zoom");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_FOCUS_TYPE), "Focus Type");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_7), "Unknown Camera State 7");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_8), "Unknown Camera State 8");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_9), "Unknown Camera State 9");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_10), "Unknown Camera State 10");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_FLASH_ACTIVITY), "Flash Activity");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_12), "Unknown Camera State 12");
        _tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_13), "Unknown Camera State 13");
        _tagNameMap.put(new Integer(TAG_CANON_STATE2_WHITE_BALANCE), "White Balance");
        _tagNameMap.put(new Integer(TAG_CANON_STATE2_SEQUENCE_NUMBER), "Sequence Number");
        _tagNameMap.put(new Integer(TAG_CANON_STATE2_AF_POINT_USED), "AF Point Used");
        _tagNameMap.put(new Integer(TAG_CANON_STATE2_FLASH_BIAS), "Flash Bias");
        _tagNameMap.put(new Integer(TAG_CANON_STATE2_AUTO_EXPOSURE_BRACKETING), "Auto Exposure Bracketing");
        _tagNameMap.put(new Integer(TAG_CANON_STATE2_AEB_BRACKET_VALUE), "AEB Bracket Value");
        _tagNameMap.put(new Integer(TAG_CANON_STATE2_SUBJECT_DISTANCE), "Subject Distance");

        _tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTION_LONG_EXPOSURE_NOISE_REDUCTION), "Long Exposure Noise Reduction");
        _tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTION_SHUTTER_AUTO_EXPOSURE_LOCK_BUTTONS), "Shutter/Auto Exposure-lock Buttons");
        _tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTION_MIRROR_LOCKUP), "Mirror Lockup");
        _tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTION_TV_AV_AND_EXPOSURE_LEVEL), "Tv/Av And Exposure Level");
        _tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTION_AF_ASSIST_LIGHT), "AF-Assist Light");
        _tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTION_SHUTTER_SPEED_IN_AV_MODE), "Shutter Speed in Av Mode");
        _tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTION_BRACKETTING), "Auto-Exposure Bracketting Sequence/Auto Cancellation");
        _tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTION_SHUTTER_CURTAIN_SYNC), "Shutter Curtain Sync");
        _tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTION_AF_STOP), "Lens Auto-Focus Stop Button Function Switch");
        _tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTION_FILL_FLASH_REDUCTION), "Auto Reduction of Fill Flash");
        _tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTION_MENU_BUTTON_RETURN), "Menu Button Return Position");
        _tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTION_SET_BUTTON_FUNCTION), "SET Button Function When Shooting");
        _tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTION_SENSOR_CLEANING), "Sensor Cleaning");
    }

    public CanonMakernoteDirectory()
    {
        this.setDescriptor(new CanonMakernoteDescriptor(this));
    }

    public String getName()
    {
        return "Canon Makernote";
    }

    protected HashMap getTagNameMap()
    {
        return _tagNameMap;
    }

    /**
     * We need special handling for selected tags.
     * @param tagType
     * @param ints
     */
    public void setIntArray(int tagType, int[] ints)
    {
        if (tagType == TAG_CANON_CAMERA_STATE_1) {
            // this single tag has multiple values within
            int subTagTypeBase = 0xC100;
            // we intentionally skip the first array member
            for (int i = 1; i < ints.length; i++) {
                setInt(subTagTypeBase + i, ints[i]);
            }
        } else if (tagType == TAG_CANON_CAMERA_STATE_2) {
            // this single tag has multiple values within
            int subTagTypeBase = 0xC200;
            // we intentionally skip the first array member
            for (int i = 1; i < ints.length; i++) {
                setInt(subTagTypeBase + i, ints[i]);
            }
        } if (tagType == TAG_CANON_CUSTOM_FUNCTIONS) {
            // this single tag has multiple values within
            int subTagTypeBase = 0xC300;
            // we intentionally skip the first array member
            for (int i = 1; i < ints.length; i++) {
                setInt(subTagTypeBase + i + 1, ints[i] & 0x0F);
            }
        } else {
            // no special handling...
            super.setIntArray(tagType, ints);
        }
    }
}

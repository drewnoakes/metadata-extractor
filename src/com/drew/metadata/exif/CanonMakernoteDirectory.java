/*
 * Created by dnoakes on 27-Nov-2002 10:10:47 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 *
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
    public static final int TAG_CANON_SERIAL_NUMBER = 0x000C;
    public static final int TAG_CANON_UNKNOWN_1 = 0x000D;
    public static final int TAG_CANON_CUSTOM_FUNCTIONS = 0x000F;

    // These 'sub'-tag values have been created for consistency -- they don't exist within the exif segment
    public static final int TAG_CANON_STATE1_MACRO_MODE = 0xC101;
    public static final int TAG_CANON_STATE1_SELF_TIMER_DELAY = 0xC102;
    public static final int TAG_CANON_STATE1_UNKNOWN_1 = 0xC103;
    public static final int TAG_CANON_STATE1_FLASH_MODE = 0xC104;
    public static final int TAG_CANON_STATE1_CONTINUOUS_DRIVE_MODE = 0xC105;
    public static final int TAG_CANON_STATE1_UNKNOWN_2 = 0xC106;
    public static final int TAG_CANON_STATE1_FOCUS_MODE_1 = 0xC107;
    public static final int TAG_CANON_STATE1_UNKNOWN_3 = 0xC108;
    public static final int TAG_CANON_STATE1_UNKNOWN_4 = 0xC109;
    public static final int TAG_CANON_STATE1_IMAGE_SIZE = 0xC10A;
    public static final int TAG_CANON_STATE1_EASY_SHOOTING_MODE = 0xC10B;
    public static final int TAG_CANON_STATE1_UNKNOWN_5 = 0xC10C;
    public static final int TAG_CANON_STATE1_CONTRAST = 0xC10D;
    public static final int TAG_CANON_STATE1_SATURATION = 0xC10E;
    public static final int TAG_CANON_STATE1_SHARPNESS = 0xC10F;
    public static final int TAG_CANON_STATE1_ISO = 0xC110;
    public static final int TAG_CANON_STATE1_METERING_MODE = 0xC111;
    public static final int TAG_CANON_STATE1_UNKNOWN_6 = 0xC112;
    public static final int TAG_CANON_STATE1_AF_POINT_SELECTED = 0xC113;
    public static final int TAG_CANON_STATE1_EXPOSURE_MODE = 0xC114;
    public static final int TAG_CANON_STATE1_UNKNOWN_7 = 0xC115;
    public static final int TAG_CANON_STATE1_UNKNOWN_8 = 0xC116;
    public static final int TAG_CANON_STATE1_LONG_FOCAL_LENGTH = 0xC117;
    public static final int TAG_CANON_STATE1_SHORT_FOCAL_LENGTH = 0xC118;
    public static final int TAG_CANON_STATE1_FOCAL_UNITS_PER_MM = 0xC119;
    public static final int TAG_CANON_STATE1_UNKNOWN_9 = 0xC11A;
    public static final int TAG_CANON_STATE1_UNKNOWN_10 = 0xC11B;
    public static final int TAG_CANON_STATE1_UNKNOWN_11 = 0xC11C;
    public static final int TAG_CANON_STATE1_FLASH_DETAILS = 0xC11D;
    public static final int TAG_CANON_STATE1_UNKNOWN_12 = 0xC11E;
    public static final int TAG_CANON_STATE1_UNKNOWN_13 = 0xC11F;
    public static final int TAG_CANON_STATE1_FOCUS_MODE_2 = 0xC120;

    public static final int TAG_CANON_STATE2_WHITE_BALANCE = 0xC207;
    public static final int TAG_CANON_STATE2_SEQUENCE_NUMBER = 0xC209;
    public static final int TAG_CANON_STATE2_AF_POINT_USED = 0xC20E;
    public static final int TAG_CANON_STATE2_FLASH_BIAS = 0xC20F;
    public static final int TAG_CANON_STATE2_SUBJECT_DISTANCE = 0xC213;

    // 9  A  B  C  D  E  F  10 11 12 13
    // 9  10 11 12 13 14 15 16 17 18 19
    protected static final HashMap tagNameMap = new HashMap();

    static
    {
        tagNameMap.put(new Integer(TAG_CANON_FIRMWARE_VERSION), "Firmware Version");
        tagNameMap.put(new Integer(TAG_CANON_IMAGE_NUMBER), "Image Number");
        tagNameMap.put(new Integer(TAG_CANON_IMAGE_TYPE), "Image Type");
        tagNameMap.put(new Integer(TAG_CANON_OWNER_NAME), "Owner Name");
        tagNameMap.put(new Integer(TAG_CANON_UNKNOWN_1), "Makernote Unknown 1");
        tagNameMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTIONS), "Custom Functions");
        tagNameMap.put(new Integer(TAG_CANON_SERIAL_NUMBER), "Camera Serial Number");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_AF_POINT_SELECTED), "AF Point Selected");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_CONTINUOUS_DRIVE_MODE), "Continuous Drive Mode");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_CONTRAST), "Contrast");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_EASY_SHOOTING_MODE), "Easy Shooting Mode");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_EXPOSURE_MODE), "Exposure Mode");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_FLASH_DETAILS), "Flash Details");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_FLASH_MODE), "Flash Mode");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_FOCAL_UNITS_PER_MM), "Focal Units per mm");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_FOCUS_MODE_1), "Focus Mode");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_FOCUS_MODE_2), "Focus Mode");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_IMAGE_SIZE), "Image Size");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_ISO), "Iso");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_LONG_FOCAL_LENGTH), "Long Focal Length");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_MACRO_MODE), "Macro Mode");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_METERING_MODE), "Metering Mode");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_SATURATION), "Saturation");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_SELF_TIMER_DELAY), "Self Timer Delay");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_SHARPNESS), "Sharpness");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_SHORT_FOCAL_LENGTH), "Short Focal Length");
/*
        tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_1), "Unknown Camera State 1");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_2), "Unknown Camera State 2");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_3), "Unknown Camera State 3");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_4), "Unknown Camera State 4");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_5), "Unknown Camera State 5");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_6), "Unknown Camera State 6");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_7), "Unknown Camera State 7");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_8), "Unknown Camera State 8");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_9), "Unknown Camera State 9");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_10), "Unknown Camera State 10");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_11), "Unknown Camera State 11");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_12), "Unknown Camera State 12");
        tagNameMap.put(new Integer(TAG_CANON_STATE1_UNKNOWN_13), "Unknown Camera State 13");
*/

        tagNameMap.put(new Integer(TAG_CANON_STATE2_WHITE_BALANCE), "White Balance");
        tagNameMap.put(new Integer(TAG_CANON_STATE2_SEQUENCE_NUMBER), "Sequence Number");
        tagNameMap.put(new Integer(TAG_CANON_STATE2_AF_POINT_USED), "AF Point Used");
        tagNameMap.put(new Integer(TAG_CANON_STATE2_FLASH_BIAS), "Flash Bias");
        tagNameMap.put(new Integer(TAG_CANON_STATE2_SUBJECT_DISTANCE), "Subject Distance");
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
        return tagNameMap;
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
        } else {
            // no special handling...
            super.setIntArray(tagType, ints);
        }
    }
}

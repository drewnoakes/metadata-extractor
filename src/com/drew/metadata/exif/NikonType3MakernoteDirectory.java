/*
 * Created by dnoakes on 3-Oct-2002 10:10:47 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * The type-3 directory is for D-Series cameras such as the D1 and D100.
 * Thanks to Fabrizio Giudici for publishing his reverse-engineering of the D1 makernote data.
 * http://www.timelesswanderings.net/equipment/D100/NEF.html
 *
 * Additional sample images have been observed, and their tag values recorded in javadoc comments for each tag's field.
 * New tags have subsequently been added since Fabrizio's observations.
 */
public class NikonType3MakernoteDirectory extends Directory
{

    /**
     * Values observed
     * - 0200
     */
    public static final int TAG_NIKON_TYPE3_FIRMWARE_VERSION = 1;

    /**
     * Values observed
     * - 0 250
     * - 0 400
     */
    public static final int TAG_NIKON_TYPE3_ISO_1 = 2;

    /**
     * Values observed
     * - FILE
     * - RAW
     */
    public static final int TAG_NIKON_TYPE3_FILE_FORMAT = 4;

    /**
     * Values observed
     * - AUTO
     * - SUNNY
     */
    public static final int TAG_NIKON_TYPE3_CAMERA_WHITE_BALANCE  = 5;

    /**
     * Values observed
     * - AUTO
     * - NORMAL
     */
    public static final int TAG_NIKON_TYPE3_CAMERA_SHARPENING = 6;

    /**
     * Values observed
     * - AF-S
     */
    public static final int TAG_NIKON_TYPE3_AF_TYPE = 7;

    /**
     * Values observed
     * - NORMAL
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_17 = 8;

    /**
     * Values observed
     * -
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_18 = 9;

    /**
     * Values observed
     * - 0
     */
    public static final int TAG_NIKON_TYPE3_CAMERA_WHITE_BALANCE_FINE = 11;

    /**
     * Values observed
     * - 2.25882352 1.76078431 0.0 0.0
     */
    public static final int TAG_NIKON_TYPE3_CAMERA_WHITE_BALANCE_RB_COEFF = 12;

    /**
     * Values observed
     * -
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_1 = 13;

    /**
     * Values observed
     * -
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_2 = 14;

    /**
     * Values observed
     * - 914
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_3 = 17;

    /**
     * Values observed
     * -
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_19 = 18;

    /**
     * Values observed
     * - 0 250
     */
    public static final int TAG_NIKON_TYPE3_ISO_2 = 19;

    /**
     * Values observed
     * - AUTO
     */
    public static final int TAG_NIKON_TYPE3_CAMERA_TONE_COMPENSATION = 129;

    /**
     * Values observed
     * - 6
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_4 = 131;

    /**
     * Values observed
     * - 240/10 850/10 35/10 45/10
     */
    public static final int TAG_NIKON_TYPE3_LENS = 132;

    /**
     * Values observed
     * - 0
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_5 = 135;

    /**
     * Values observed
     * -
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_6 = 136;

    /**
     * Values observed
     * - 0
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_7 = 137;

    /**
     * Values observed
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_8 = 139;

    /**
     * Values observed
     * - 0
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_20 = 138;

    /**
     * Values observed
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_9 = 140;

    /**
     * Values observed
     * - MODE1
     */
    public static final int TAG_NIKON_TYPE3_CAMERA_COLOR_MODE = 141;

    /**
     * Values observed
     * - NATURAL
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_10 = 144;

    /**
     * Values observed
     * - 0100)
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_11 = 145;

    /**
     * Values observed
     * - 0
     */
    public static final int TAG_NIKON_TYPE3_CAMERA_HUE_ADJUSTMENT = 146;

    /**
     * Values observed
     * - OFF
     */
    public static final int TAG_NIKON_TYPE3_NOISE_REDUCTION = 149;

    /**
     * Values observed
     * - 0100'~e3
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_12 = 151;

    /**
     * Values observed
     * - 0100fht@7b,4x,D"Y
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_13 = 152;

    /**
     * Values observed
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_14 = 153;

    /**
     * Values observed
     * - 78/10 78/10
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_15 = 154;

    /**
     * Values observed
     */
    public static final int TAG_NIKON_TYPE3_CAPTURE_EDITOR_DATA = 3585;

    /**
     * Values observed
     */
    public static final int TAG_NIKON_TYPE3_UNKNOWN_16 = 3600;

    protected static final HashMap tagNameMap = new HashMap();

    static
    {
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_FIRMWARE_VERSION), "Firmware Version");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_ISO_1), "ISO");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_FILE_FORMAT), "File Format");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_CAMERA_WHITE_BALANCE), "White Balance");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_CAMERA_SHARPENING), "Sharpening");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_AF_TYPE), "AF Type");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_CAMERA_WHITE_BALANCE_FINE), "White Balance Fine");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_CAMERA_WHITE_BALANCE_RB_COEFF), "White Balance RB Coefficients");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_ISO_2), "ISO");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_CAMERA_TONE_COMPENSATION), "Tone Compensation");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_LENS), "Lens");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_CAMERA_COLOR_MODE), "Colour Mode");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_CAMERA_HUE_ADJUSTMENT), "Camera Hue Adjustment");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_NOISE_REDUCTION), "Noise Reduction");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_CAPTURE_EDITOR_DATA), "Capture Editor Data");

        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_1), "Unknown 01");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_2), "Unknown 02");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_3), "Unknown 03");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_4), "Unknown 04");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_5), "Unknown 05");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_6), "Unknown 06");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_7), "Unknown 07");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_8), "Unknown 08");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_9), "Unknown 09");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_10), "Unknown 10");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_11), "Unknown 11");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_12), "Unknown 12");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_13), "Unknown 13");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_14), "Unknown 14");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_15), "Unknown 15");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_16), "Unknown 16");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_17), "Unknown 17");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_18), "Unknown 18");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_19), "Unknown 19");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE3_UNKNOWN_20), "Unknown 20");
    }

    public NikonType3MakernoteDirectory()
    {
        this.setDescriptor(new NikonType3MakernoteDescriptor(this));
    }

    public String getName()
    {
        return "Nikon Makernote";
    }

    protected HashMap getTagNameMap()
    {
        return tagNameMap;
    }
}

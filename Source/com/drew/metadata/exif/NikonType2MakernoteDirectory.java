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
 * Created by dnoakes on 3-Oct-2002 10:10:47 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.util.HashMap;

/**
 * Describes tags specific to Nikon (type 2) cameras.  Type-2 applies to the E990 and D-series cameras such as the E990, D1,
 * D70 and D100.
 *
 * Thanks to Fabrizio Giudici for publishing his reverse-engineering of the D100 makernote data.
 * http://www.timelesswanderings.net/equipment/D100/NEF.html
 *
 * Note that the camera implements image protection (locking images) via the file's 'readonly' attribute.  Similarly
 * image hiding uses the 'hidden' attribute (observed on the D70).  Consequently, these values are not available here.
 *
 * Additional sample images have been observed, and their tag values recorded in javadoc comments for each tag's field.
 * New tags have subsequently been added since Fabrizio's observations.
 *
 * In earlier models (such as the E990 and D1), this directory begins at the first byte of the makernote IFD.  In
 * later models, the IFD was given the standard prefix to indicate the camera models (most other manufacturers also
 * provide this prefix to aid in software decoding).
 */
public class NikonType2MakernoteDirectory extends Directory
{
    /**
     * Values observed
     * - 0200 (D70)
     * - 0200 (D1X)
     */
    public static final int TAG_NIKON_TYPE2_FIRMWARE_VERSION = 0x0001;

    /**
     * Values observed
     * - 0 250
     * - 0 400
     */
    public static final int TAG_NIKON_TYPE2_ISO_1 = 0x0002;

    /**
     * Values observed
     * - COLOR (seen in the D1X)
     */
    public static final int TAG_NIKON_TYPE2_COLOR_MODE = 0x0003;

    /**
     * Values observed
     * - FILE
     * - RAW
     * - NORMAL
     * - FINE
     */
    public static final int TAG_NIKON_TYPE2_QUALITY_AND_FILE_FORMAT = 0x0004;

    /**
     * The white balance as set in the camera.
     *
     * Values observed
     * - AUTO
     * - SUNNY (D70)
     * - FLASH (D1X)
     * (presumably also SHADOW / INCANDESCENT / FLUORESCENT / CLOUDY)
     */
    public static final int TAG_NIKON_TYPE2_CAMERA_WHITE_BALANCE  = 0x0005;

    /**
     * The sharpening as set in the camera.
     *
     * Values observed
     * - AUTO
     * - NORMAL (D70)
     * - NONE (D1X)
     */
    public static final int TAG_NIKON_TYPE2_CAMERA_SHARPENING = 0x0006;

    /**
     * The auto-focus type used by the camera.
     *
     * Values observed
     * - AF-S
     * - AF-C
     * - MANUAL
     */
    public static final int TAG_NIKON_TYPE2_AF_TYPE = 0x0007;

    /**
     * Values observed
     * - NORMAL
     * - RED-EYE
     *
     * Note: when TAG_NIKON_TYPE2_AUTO_FLASH_MODE is blank, Nikon Browser displays "Flash Sync Mode: Not Attached"
     */
    public static final int TAG_NIKON_TYPE2_FLASH_SYNC_MODE = 0x0008;

    /**
     * Values observed
     * - Built-in,TTL
     * - Optional,TTL (with speedlight SB800, flash sync mode as NORMAL.  NikonBrowser reports Auto Flash Comp: 0 EV -- which tag is that?) (D70)
     * - NEW_TTL (Nikon Browser interprets as "D-TTL")
     * - (blank -- accompanied FlashSyncMode of NORMAL) (D70)
     */
    public static final int TAG_NIKON_TYPE2_AUTO_FLASH_MODE = 0x0009;

    /**
     * Added during merge of Type2 & Type3.  May apply to earlier models, such as E990 and D1.
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_34 = 0x000A;

    /**
     * Values observed
     * - 0
     */
    public static final int TAG_NIKON_TYPE2_CAMERA_WHITE_BALANCE_FINE = 0x000B;

    /**
     * The first two numbers are coefficients to multiply red and blue channels according to white balance as set in the
     * camera. The meaning of the third and the fourth numbers is unknown.
     *
     * Values observed
     * - 2.25882352 1.76078431 0.0 0.0
     * - 10242/1 34305/1 0/1 0/1
     * - 234765625/100000000 1140625/1000000 1/1 1/1
     */
    public static final int TAG_NIKON_TYPE2_CAMERA_WHITE_BALANCE_RB_COEFF = 0x000C;

    /**
     * Values observed
     * - 0,1,6,0 (hex)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_1 = 0x000D;

    /**
     * Values observed
     * - î
     * - 0,1,c,0 (hex)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_2 = 0x000E;

    /**
     * Added during merge of Type2 & Type3.  May apply to earlier models, such as E990 and D1.
     */
    public static final int TAG_NIKON_TYPE2_ISO_SELECTION = 0x000F;

    /**
     * Added during merge of Type2 & Type3.  May apply to earlier models, such as E990 and D1.
     */
    public static final int TAG_NIKON_TYPE2_DATA_DUMP = 0x0010;

    /**
     * Values observed
     * - 914
     * - 1379 (D70)
     * - 2781 (D1X)
     * - 6942 (D100)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_3 = 0x0011;

    /**
     * Values observed
     * - (no value -- blank)
     */
    public static final int TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION = 0x0012;

    /**
     * Values observed
     * - 0 250
     * - 0 400
     */
    public static final int TAG_NIKON_TYPE2_ISO_2 = 0x0013;

    /**
     * Values observed
     * - 0 0 49163 53255
     * - 0 0 3008 2000 (the image dimensions were 3008x2000) (D70)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_21 = 0x0016;

    /**
     * Values observed
     * - (blank)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_22 = 0x0017;

    /**
     * Values observed
     * - (blank)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_23 = 0x0018;

    /**
     * Values observed
     * - 0
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_24 = 0x0019;

    /**
     * Added during merge of Type2 & Type3.  May apply to earlier models, such as E990 and D1.
     */
    public static final int TAG_NIKON_TYPE2_IMAGE_ADJUSTMENT = 0x0080;

    /**
     * The tone compensation as set in the camera.
     *
     * Values observed
     * - AUTO
     * - NORMAL (D1X, D100)
     */
    public static final int TAG_NIKON_TYPE2_CAMERA_TONE_COMPENSATION = 0x0081;

    /**
     * Added during merge of Type2 & Type3.  May apply to earlier models, such as E990 and D1.
     */
    public static final int TAG_NIKON_TYPE2_ADAPTER = 0x0082;

    /**
     * Values observed
     * - 6
     * - 6 (D70)
     * - 2 (D1X)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_4 = 0x0083;

    /**
     * A pair of focal/max-fstop values that describe the lens used.
     *
     * Values observed
     * - 180.0,180.0,2.8,2.8 (D100)
     * - 240/10 850/10 35/10 45/10
     * - 18-70mm f/3.5-4.5 (D70)
     * - 17-35mm f/2.8-2.8 (D1X)
     * - 70-200mm f/2.8-2.8 (D70)
     *
     * Nikon Browser identifies the lens as "18-70mm F/3.5-4.5 G" which
     * is identical to metadata extractor, except for the "G".  This must
     * be coming from another tag...
     */
    public static final int TAG_NIKON_TYPE2_LENS = 0x0084;

    /**
     * Added during merge of Type2 & Type3.  May apply to earlier models, such as E990 and D1.
     */
    public static final int TAG_NIKON_TYPE2_MANUAL_FOCUS_DISTANCE = 0x0085;

    /**
     * Added during merge of Type2 & Type3.  May apply to earlier models, such as E990 and D1.
     */
    public static final int TAG_NIKON_TYPE2_DIGITAL_ZOOM = 0x0086;

    /**
     * Values observed
     * - 0
     * - 9
     * - 3 (D1X)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_5 = 0x0087;

    /**
     * Values observed
     * -
     */
    public static final int TAG_NIKON_TYPE2_AF_FOCUS_POSITION = 0x0088;

    /**
     * Values observed
     * - 0
     * - 1
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_7 = 0x0089;

    /**
     * Values observed
     * - 0
     * - 0
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_20 = 0x008A;

    /**
     * Values observed
     * - 48,1,c,0 (hex) (D100)
     * - @
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_8 = 0x008B;

    /**
     * Unknown.  Fabrizio believes this may be a lookup table for the user-defined curve.
     *
     * Values observed
     * - (blank) (D1X)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_9 = 0x008C;

    /**
     * The color space as set in the camera.
     *
     * Values observed
     * - MODE1
     * - Mode I (sRGB) (D70)
     * - MODE2 (D1X, D100)
     */
    public static final int TAG_NIKON_TYPE2_CAMERA_COLOR_MODE = 0x008D;

    /**
     * Values observed
     * - NATURAL
     * - SPEEDLIGHT (D70, D1X)
     */
    public static final int TAG_NIKON_TYPE2_LIGHT_SOURCE = 0x0090;

    /**
     * Values observed
     * - 0100)
     * - 0103 (D70)
     * - 0100 (D1X)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_11 = 0x0091;

    /**
     * The hue adjustment as set in the camera.
     *
     * Values observed
     * - 0
     */
    public static final int TAG_NIKON_TYPE2_CAMERA_HUE_ADJUSTMENT = 0x0092;

    /**
     * Values observed
     * - OFF
     */
    public static final int TAG_NIKON_TYPE2_NOISE_REDUCTION = 0x0095;

    /**
     * Values observed
     * - 0100'~e3
     * - 0103
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_12 = 0x0097;

    /**
     * Values observed
     * - 0100fht@7b,4x,D"Y
     * - 01015
     * - 0100w\cH+D$$h$î5Q (D1X)
     * - 30,31,30,30,0,0,b,48,7c,7c,24,24,5,15,24,0,0,0,0,0 (hex) (D100)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_13 = 0x0098;

    /**
     * Values observed
     * - 2014 662 (D1X)
     * - 1517,1012 (D100)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_14 = 0x0099;

    /**
     * Values observed
     * - 78/10 78/10
     * - 78/10 78/10 (D70)
     * - 59/10 59/5 (D1X)
     * - 7.8,7.8 (D100)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_15 = 0x009A;

    /**
     * Values observed
     * - NO= 00002539
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_25 = 0x00A0;

    /**
     * Values observed
     * - 1564851
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_26 = 0x00A2;

    /**
     * Values observed
     * - 0
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_27 = 0x00A3;

    /**
     * This appears to be a sequence number to indentify the exposure.  This value seems to increment
     * for consecutive exposures (observed on D70).
     *
     * Values observed
     * - 5062
     */
    public static final int TAG_NIKON_TYPE2_EXPOSURE_SEQUENCE_NUMBER = 0x00A7;

    /**
     * Values observed
     * - 0100 (D70)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_32 = 0x00A8;

    /**
     * Values observed
     * - NORMAL (D70)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_33 = 0x00A9;

    /**
     * Nikon Browser suggests this value represents Saturation...
     * Values observed
     * - NORMAL (D70)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_29 = 0x00AA;

    /**
     * Values observed
     * - AUTO (D70)
     * - (blank) (D70)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_30 = 0x00AB;

    /**
     * Data about changes set by Nikon Capture Editor.
     *
     * Values observed
     */
    public static final int TAG_NIKON_TYPE2_CAPTURE_EDITOR_DATA = 0x0E01;

    /**
     * Values observed
     * - 1473
     * - 7036 (D100)
     */
    public static final int TAG_NIKON_TYPE2_UNKNOWN_16 = 0x0E10;

    protected static final HashMap _tagNameMap = new HashMap();

    static
    {
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_FIRMWARE_VERSION), "Firmware Version");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_ISO_1), "ISO");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_QUALITY_AND_FILE_FORMAT), "Quality & File Format");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_CAMERA_WHITE_BALANCE), "White Balance");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_CAMERA_SHARPENING), "Sharpening");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_AF_TYPE), "AF Type");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_CAMERA_WHITE_BALANCE_FINE), "White Balance Fine");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_CAMERA_WHITE_BALANCE_RB_COEFF), "White Balance RB Coefficients");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_ISO_2), "ISO");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_ISO_SELECTION), "ISO Selection");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_DATA_DUMP), "Data Dump");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_IMAGE_ADJUSTMENT), "Image Adjustment");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_CAMERA_TONE_COMPENSATION), "Tone Compensation");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_ADAPTER), "Adapter");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_LENS), "Lens");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_MANUAL_FOCUS_DISTANCE), "Manual Focus Distance");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_DIGITAL_ZOOM), "Digital Zoom");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_CAMERA_COLOR_MODE), "Colour Mode");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_CAMERA_HUE_ADJUSTMENT), "Camera Hue Adjustment");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_NOISE_REDUCTION), "Noise Reduction");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_CAPTURE_EDITOR_DATA), "Capture Editor Data");

        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_1), "Unknown 01");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_2), "Unknown 02");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_3), "Unknown 03");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_4), "Unknown 04");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_5), "Unknown 05");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_AF_FOCUS_POSITION), "AF Focus Position");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_7), "Unknown 07");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_8), "Unknown 08");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_9), "Unknown 09");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_LIGHT_SOURCE), "Light source");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_11), "Unknown 11");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_12), "Unknown 12");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_13), "Unknown 13");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_14), "Unknown 14");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_15), "Unknown 15");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_16), "Unknown 16");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_FLASH_SYNC_MODE), "Flash Sync Mode");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_AUTO_FLASH_MODE), "Auto Flash Mode");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION), "Auto Flash Compensation");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_EXPOSURE_SEQUENCE_NUMBER), "Exposure Sequence Number");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_COLOR_MODE), "Color Mode");

        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_20), "Unknown 20");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_21), "Unknown 21");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_22), "Unknown 22");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_23), "Unknown 23");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_24), "Unknown 24");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_25), "Unknown 25");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_26), "Unknown 26");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_27), "Unknown 27");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_29), "Unknown 29");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_30), "Unknown 30");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_32), "Unknown 32");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_33), "Unknown 33");
    }

    public NikonType2MakernoteDirectory()
    {
        this.setDescriptor(new NikonType2MakernoteDescriptor(this));
    }

    public Rational getAutoFlashCompensation() throws MetadataException
    {
        if (!containsTag(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION))
            return null;

        byte[] bytes = getByteArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION);
        return CalculateFlashCompensationFromBytes(bytes);
    }

    public static Rational CalculateFlashCompensationFromBytes(byte[] bytes)
    {
        if (bytes.length==3)
        {
            byte denominator = bytes[2];
            int numerator = (int)bytes[0] * bytes[1];
            return new Rational(numerator, denominator);
        }
        return null;
    }

    public String getName()
    {
        return "Nikon Makernote";
    }

    protected HashMap getTagNameMap()
    {
        return _tagNameMap;
    }
}

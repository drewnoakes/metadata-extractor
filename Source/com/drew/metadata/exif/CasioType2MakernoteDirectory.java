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
 * Describes tags specific to Casio (type 2) cameras.
 *
 * A standard TIFF IFD directory but always uses Motorola (Big-Endian) Byte Alignment.
 * Makernote data begins after a 6-byte header: "QVC\x00\x00\x00"
 */
public class CasioType2MakernoteDirectory extends Directory
{
    /**
     * 2 values - x,y dimensions in pixels.
     */
    public static final int TAG_CASIO_TYPE2_THUMBNAIL_DIMENSIONS = 0x0002;
    /**
     * Size in bytes
     */
    public static final int TAG_CASIO_TYPE2_THUMBNAIL_SIZE = 0x0003;
    /**
     * Offset of Preview Thumbnail
     */
    public static final int TAG_CASIO_TYPE2_THUMBNAIL_OFFSET = 0x0004;
    /**
     * 1 = Fine
     * 2 = Super Fine
     */
    public static final int TAG_CASIO_TYPE2_QUALITY_MODE = 0x0008;
    /**
     * 0 = 640 x 480 pixels
     * 4 = 1600 x 1200 pixels
     * 5 = 2048 x 1536 pixels
     * 20 = 2288 x 1712 pixels
     * 21 = 2592 x 1944 pixels
     * 22 = 2304 x 1728 pixels
     * 36 = 3008 x 2008 pixels
     */
    public static final int TAG_CASIO_TYPE2_IMAGE_SIZE = 0x0009;
    /**
     * 0 = Normal
     * 1 = Macro
     */
    public static final int TAG_CASIO_TYPE2_FOCUS_MODE_1 = 0x000D;
    /**
     * 3 = 50
     * 4 = 64
     * 6 = 100
     * 9 = 200
     */
    public static final int TAG_CASIO_TYPE2_ISO_SENSITIVITY = 0x0014;
    /**
     * 0 = Auto
     * 1 = Daylight
     * 2 = Shade
     * 3 = Tungsten
     * 4 = Fluorescent
     * 5 = Manual
     */
    public static final int TAG_CASIO_TYPE2_WHITE_BALANCE_1 = 0x0019;
    /**
     * Units are tenths of a millimetre
     */
    public static final int TAG_CASIO_TYPE2_FOCAL_LENGTH = 0x001D;
    /**
     * 0 = -1
     * 1 = Normal
     * 2 = +1
     */
    public static final int TAG_CASIO_TYPE2_SATURATION = 0x001F;
    /**
     * 0 = -1
     * 1 = Normal
     * 2 = +1
     */
    public static final int TAG_CASIO_TYPE2_CONTRAST = 0x0020;
    /**
     * 0 = -1
     * 1 = Normal
     * 2 = +1
     */
    public static final int TAG_CASIO_TYPE2_SHARPNESS = 0x0021;
    /**
     * See PIM specification here: http://www.ozhiker.com/electronics/pjmt/jpeg_info/pim.html
     */
    public static final int TAG_CASIO_TYPE2_PRINT_IMAGE_MATCHING_INFO = 0x0E00;
    /**
     * Alternate thumbnail offset
     */
    public static final int TAG_CASIO_TYPE2_CASIO_PREVIEW_THUMBNAIL = 0x2000;
    /**
     *
     */
    public static final int TAG_CASIO_TYPE2_WHITE_BALANCE_BIAS = 0x2011;
    /**
     * 12 = Flash
     * 0 = Manual
     * 1 = Auto?
     * 4 = Flash?
     */
    public static final int TAG_CASIO_TYPE2_WHITE_BALANCE_2 = 0x2012;
    /**
     * Units are millimetres
     */
    public static final int TAG_CASIO_TYPE2_OBJECT_DISTANCE = 0x2022;
    /**
     * 0 = Off
     */
    public static final int TAG_CASIO_TYPE2_FLASH_DISTANCE = 0x2034;
    /**
     * 2 = Normal Mode
     */
    public static final int TAG_CASIO_TYPE2_RECORD_MODE = 0x3000;
    /**
     * 1 = Off?
     */
    public static final int TAG_CASIO_TYPE2_SELF_TIMER = 0x3001;
    /**
     * 3 = Fine
     */
    public static final int TAG_CASIO_TYPE2_QUALITY = 0x3002;
    /**
     * 1 = Fixation
     * 6 = Multi-Area Auto Focus
     */
    public static final int TAG_CASIO_TYPE2_FOCUS_MODE_2 = 0x3003;
    /**
     * (string)
     */
    public static final int TAG_CASIO_TYPE2_TIME_ZONE = 0x3006;
    /**
     *
     */
    public static final int TAG_CASIO_TYPE2_BESTSHOT_MODE = 0x3007;
    /**
     * 0 = Off
     * 1 = On?
     */
    public static final int TAG_CASIO_TYPE2_CCD_ISO_SENSITIVITY = 0x3014;
    /**
     * 0 = Off
     */
    public static final int TAG_CASIO_TYPE2_COLOUR_MODE = 0x3015;
    /**
     * 0 = Off
     */
    public static final int TAG_CASIO_TYPE2_ENHANCEMENT = 0x3016;
    /**
     * 0 = Off
     */
    public static final int TAG_CASIO_TYPE2_FILTER = 0x3017;

    protected static final HashMap tagNameMap = new HashMap();

    static
    {
        // TODO add names
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_THUMBNAIL_DIMENSIONS), "Thumbnail Dimensions");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_THUMBNAIL_SIZE), "Thumbnail Size");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_THUMBNAIL_OFFSET), "Thumbnail Offset");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_QUALITY_MODE), "Quality Mode");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_IMAGE_SIZE), "Image Size");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_FOCUS_MODE_1), "Focus Mode");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_ISO_SENSITIVITY), "ISO Sensitivity");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_WHITE_BALANCE_1), "White Balance");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_FOCAL_LENGTH), "Focal Length");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_SATURATION), "Saturation");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_CONTRAST), "Contrast");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_SHARPNESS), "Sharpness");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_PRINT_IMAGE_MATCHING_INFO), "Print Image Matching (PIM) Info");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_CASIO_PREVIEW_THUMBNAIL), "Casio Preview Thumbnail");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_WHITE_BALANCE_BIAS), "White Balance Bias");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_WHITE_BALANCE_2), "White Balance");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_OBJECT_DISTANCE), "Object Distance");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_FLASH_DISTANCE), "Flash Distance");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_RECORD_MODE), "Record Mode");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_SELF_TIMER), "Self Timer");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_QUALITY), "Quality");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_FOCUS_MODE_2), "Focus Mode");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_TIME_ZONE), "Time Zone");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_BESTSHOT_MODE), "BestShot Mode");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_CCD_ISO_SENSITIVITY), "CCD ISO Sensitivity");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_COLOUR_MODE), "Colour Mode");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_ENHANCEMENT), "Enhancement");
        tagNameMap.put(new Integer(TAG_CASIO_TYPE2_FILTER), "Filter");
    }

    public CasioType2MakernoteDirectory()
    {
        this.setDescriptor(new CasioType2MakernoteDescriptor(this));
    }

    public String getName()
    {
        return "Casio Makernote";
    }

    protected HashMap getTagNameMap()
    {
        return tagNameMap;
    }
}

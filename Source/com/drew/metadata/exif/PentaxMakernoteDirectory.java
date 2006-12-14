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
 * Describes tags specific to Pentax and Asahi cameras.
 */
public class PentaxMakernoteDirectory extends Directory
{
    /**
     * 0 = Auto
     * 1 = Night-scene
     * 2 = Manual
     * 4 = Multiple
     */
    public static final int TAG_PENTAX_CAPTURE_MODE = 0x0001;

    /**
     * 0 = Good
     * 1 = Better
     * 2 = Best
     */
    public static final int TAG_PENTAX_QUALITY_LEVEL = 0x0002;

    /**
     * 2 = Custom
     * 3 = Auto
     */
    public static final int TAG_PENTAX_FOCUS_MODE = 0x0003;

    /**
     * 1 = Auto
     * 2 = Flash on
     * 4 = Flash off
     * 6 = Red-eye Reduction
     */
    public static final int TAG_PENTAX_FLASH_MODE = 0x0004;

    /**
     * 0 = Auto
     * 1 = Daylight
     * 2 = Shade
     * 3 = Tungsten
     * 4 = Fluorescent
     * 5 = Manual
     */
    public static final int TAG_PENTAX_WHITE_BALANCE = 0x0007;

    /**
     * (0 = Off)
     */
    public static final int TAG_PENTAX_DIGITAL_ZOOM = 0x000A;

    /**
     * 0 = Normal
     * 1 = Soft
     * 2 = Hard
     */
    public static final int TAG_PENTAX_SHARPNESS = 0x000B;

    /**
     * 0 = Normal
     * 1 = Low
     * 2 = High
     */
    public static final int TAG_PENTAX_CONTRAST = 0x000C;

    /**
     * 0 = Normal
     * 1 = Low
     * 2 = High
     */
    public static final int TAG_PENTAX_SATURATION = 0x000D;

    /**
     * 10 = ISO 100
     * 16 = ISO 200
     * 100 = ISO 100
     * 200 = ISO 200
     */
    public static final int TAG_PENTAX_ISO_SPEED = 0x0014;

    /**
     * 1 = Normal
     * 2 = Black & White
     * 3 = Sepia
     */
    public static final int TAG_PENTAX_COLOUR = 0x0017;

    /**
     * See Print Image Matching for specification.
     * http://www.ozhiker.com/electronics/pjmt/jpeg_info/pim.html
     */
    public static final int TAG_PENTAX_PRINT_IMAGE_MATCHING_INFO = 0x0E00;

    /**
     * (String).
     */
    public static final int TAG_PENTAX_TIME_ZONE = 0x1000;

    /**
     * (String).
     */
    public static final int TAG_PENTAX_DAYLIGHT_SAVINGS = 0x1001;

    protected static final HashMap tagNameMap = new HashMap();

    static
    {
        tagNameMap.put(new Integer(TAG_PENTAX_CAPTURE_MODE), "Capture Mode");
        tagNameMap.put(new Integer(TAG_PENTAX_QUALITY_LEVEL), "Quality Level");
        tagNameMap.put(new Integer(TAG_PENTAX_FOCUS_MODE), "Focus Mode");
        tagNameMap.put(new Integer(TAG_PENTAX_FLASH_MODE), "Flash Mode");
        tagNameMap.put(new Integer(TAG_PENTAX_WHITE_BALANCE), "White Balance");
        tagNameMap.put(new Integer(TAG_PENTAX_DIGITAL_ZOOM), "Digital Zoom");
        tagNameMap.put(new Integer(TAG_PENTAX_SHARPNESS), "Sharpness");
        tagNameMap.put(new Integer(TAG_PENTAX_CONTRAST), "Contrast");
        tagNameMap.put(new Integer(TAG_PENTAX_SATURATION), "Saturation");
        tagNameMap.put(new Integer(TAG_PENTAX_ISO_SPEED), "ISO Speed");
        tagNameMap.put(new Integer(TAG_PENTAX_COLOUR), "Colour");
        tagNameMap.put(new Integer(TAG_PENTAX_PRINT_IMAGE_MATCHING_INFO), "Print Image Matching (PIM) Info");
        tagNameMap.put(new Integer(TAG_PENTAX_TIME_ZONE), "Time Zone");
        tagNameMap.put(new Integer(TAG_PENTAX_DAYLIGHT_SAVINGS), "Daylight Savings");
    }

    public PentaxMakernoteDirectory()
    {
        this.setDescriptor(new PentaxMakernoteDescriptor(this));
    }

    public String getName()
    {
        return "Pentax Makernote";
    }

    protected HashMap getTagNameMap()
    {
        return tagNameMap;
    }
}

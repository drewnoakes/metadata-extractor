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
 * Describes tags specific to Casio (type 1) cameras.
 *
 * A standard TIFF IFD directory but always uses Motorola (Big-Endian) Byte Alignment.
 * Makernote data begins immediately (no header).
 */
public class CasioType1MakernoteDirectory extends Directory
{
    public static final int TAG_CASIO_RECORDING_MODE = 0x0001;
    public static final int TAG_CASIO_QUALITY = 0x0002;
    public static final int TAG_CASIO_FOCUSING_MODE = 0x0003;
    public static final int TAG_CASIO_FLASH_MODE = 0x0004;
    public static final int TAG_CASIO_FLASH_INTENSITY = 0x0005;
    public static final int TAG_CASIO_OBJECT_DISTANCE = 0x0006;
    public static final int TAG_CASIO_WHITE_BALANCE = 0x0007;
    public static final int TAG_CASIO_UNKNOWN_1 = 0x0008;
    public static final int TAG_CASIO_UNKNOWN_2 = 0x0009;
    public static final int TAG_CASIO_DIGITAL_ZOOM = 0x000A;
    public static final int TAG_CASIO_SHARPNESS = 0x000B;
    public static final int TAG_CASIO_CONTRAST = 0x000C;
    public static final int TAG_CASIO_SATURATION = 0x000D;
    public static final int TAG_CASIO_UNKNOWN_3 = 0x000E;
    public static final int TAG_CASIO_UNKNOWN_4 = 0x000F;
    public static final int TAG_CASIO_UNKNOWN_5 = 0x0010;
    public static final int TAG_CASIO_UNKNOWN_6 = 0x0011;
    public static final int TAG_CASIO_UNKNOWN_7 = 0x0012;
    public static final int TAG_CASIO_UNKNOWN_8 = 0x0013;
    public static final int TAG_CASIO_CCD_SENSITIVITY = 0x0014;

    protected static final HashMap tagNameMap = new HashMap();

    static
    {
        tagNameMap.put(new Integer(TAG_CASIO_CCD_SENSITIVITY), "CCD Sensitivity");
        tagNameMap.put(new Integer(TAG_CASIO_CONTRAST), "Contrast");
        tagNameMap.put(new Integer(TAG_CASIO_DIGITAL_ZOOM), "Digital Zoom");
        tagNameMap.put(new Integer(TAG_CASIO_FLASH_INTENSITY), "Flash Intensity");
        tagNameMap.put(new Integer(TAG_CASIO_FLASH_MODE), "Flash Mode");
        tagNameMap.put(new Integer(TAG_CASIO_FOCUSING_MODE), "Focussing Mode");
        tagNameMap.put(new Integer(TAG_CASIO_OBJECT_DISTANCE), "Object Distance");
        tagNameMap.put(new Integer(TAG_CASIO_QUALITY), "Quality");
        tagNameMap.put(new Integer(TAG_CASIO_RECORDING_MODE), "Recording Mode");
        tagNameMap.put(new Integer(TAG_CASIO_SATURATION), "Saturation");
        tagNameMap.put(new Integer(TAG_CASIO_SHARPNESS), "Sharpness");
        tagNameMap.put(new Integer(TAG_CASIO_UNKNOWN_1), "Makernote Unknown 1");
        tagNameMap.put(new Integer(TAG_CASIO_UNKNOWN_2), "Makernote Unknown 2");
        tagNameMap.put(new Integer(TAG_CASIO_UNKNOWN_3), "Makernote Unknown 3");
        tagNameMap.put(new Integer(TAG_CASIO_UNKNOWN_4), "Makernote Unknown 4");
        tagNameMap.put(new Integer(TAG_CASIO_UNKNOWN_5), "Makernote Unknown 5");
        tagNameMap.put(new Integer(TAG_CASIO_UNKNOWN_6), "Makernote Unknown 6");
        tagNameMap.put(new Integer(TAG_CASIO_UNKNOWN_7), "Makernote Unknown 7");
        tagNameMap.put(new Integer(TAG_CASIO_UNKNOWN_8), "Makernote Unknown 8");
        tagNameMap.put(new Integer(TAG_CASIO_WHITE_BALANCE), "White Balance");
    }

    public CasioType1MakernoteDirectory()
    {
        this.setDescriptor(new CasioType1MakernoteDescriptor(this));
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

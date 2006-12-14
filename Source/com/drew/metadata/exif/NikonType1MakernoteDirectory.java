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
 * Describes tags specific to Nikon (type 1) cameras.  Type-1 is for E-Series cameras prior to (not including) E990.
 *
 * There are 3 formats of Nikon's MakerNote. MakerNote of E700/E800/E900/E900S/E910/E950
 * starts from ASCII string "Nikon". Data format is the same as IFD, but it starts from
 * offset 0x08. This is the same as Olympus except start string. Example of actual data
 * structure is shown below.
 * <pre><code>
 * :0000: 4E 69 6B 6F 6E 00 01 00-05 00 02 00 02 00 06 00 Nikon...........
 * :0010: 00 00 EC 02 00 00 03 00-03 00 01 00 00 00 06 00 ................
 * </code></pre>
 */
public class NikonType1MakernoteDirectory extends Directory
{
    public static final int TAG_NIKON_TYPE1_UNKNOWN_1 = 0x0002;
    public static final int TAG_NIKON_TYPE1_QUALITY = 0x0003;
    public static final int TAG_NIKON_TYPE1_COLOR_MODE = 0x0004;
    public static final int TAG_NIKON_TYPE1_IMAGE_ADJUSTMENT = 0x0005;
    public static final int TAG_NIKON_TYPE1_CCD_SENSITIVITY = 0x0006;
    public static final int TAG_NIKON_TYPE1_WHITE_BALANCE = 0x0007;
    public static final int TAG_NIKON_TYPE1_FOCUS = 0x0008;
    public static final int TAG_NIKON_TYPE1_UNKNOWN_2 = 0x0009;
    public static final int TAG_NIKON_TYPE1_DIGITAL_ZOOM = 0x000A;
    public static final int TAG_NIKON_TYPE1_CONVERTER = 0x000B;
    public static final int TAG_NIKON_TYPE1_UNKNOWN_3 = 0x0F00;

    protected static final HashMap _tagNameMap = new HashMap();

    static
    {
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE1_CCD_SENSITIVITY), "CCD Sensitivity");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE1_COLOR_MODE), "Color Mode");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE1_DIGITAL_ZOOM), "Digital Zoom");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE1_CONVERTER), "Fisheye Converter");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE1_FOCUS), "Focus");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE1_IMAGE_ADJUSTMENT), "Image Adjustment");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE1_QUALITY), "Quality");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE1_UNKNOWN_1), "Makernote Unknown 1");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE1_UNKNOWN_2), "Makernote Unknown 2");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE1_UNKNOWN_3), "Makernote Unknown 3");
        _tagNameMap.put(new Integer(TAG_NIKON_TYPE1_WHITE_BALANCE), "White Balance");
    }

    public NikonType1MakernoteDirectory()
    {
        this.setDescriptor(new NikonType1MakernoteDescriptor(this));
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

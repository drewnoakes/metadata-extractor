/*
 * Created by dnoakes on 27-Nov-2002 10:10:47 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 *
 */
public class NikonType1MakernoteDirectory extends Directory
{
    // TYPE1 is for E-Series cameras prior to (not including) E990
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

    protected static final HashMap tagNameMap = new HashMap();

    static
    {
        tagNameMap.put(new Integer(TAG_NIKON_TYPE1_CCD_SENSITIVITY), "CCD Sensitivity");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE1_COLOR_MODE), "Color Mode");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE1_DIGITAL_ZOOM), "Digital Zoom");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE1_CONVERTER), "Fisheye Converter");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE1_FOCUS), "Focus");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE1_IMAGE_ADJUSTMENT), "Image Adjustment");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE1_QUALITY), "Quality");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE1_UNKNOWN_1), "Makernote Unknown 1");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE1_UNKNOWN_2), "Makernote Unknown 2");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE1_UNKNOWN_3), "Makernote Unknown 3");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE1_WHITE_BALANCE), "White Balance");
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
        return tagNameMap;
    }
}

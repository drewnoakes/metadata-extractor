/*
 * Created by dnoakes on 27-Nov-2002 10:10:47 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 *
 */
public class NikonType2MakernoteDirectory extends Directory
{
    // TYPE2 is for E990, D1 and later
    public static final int TAG_NIKON_TYPE2_UNKNOWN_1 = 0x0001;
    public static final int TAG_NIKON_TYPE2_ISO_SETTING = 0x0002;
    public static final int TAG_NIKON_TYPE2_COLOR_MODE = 0x0003;
    public static final int TAG_NIKON_TYPE2_QUALITY = 0x0004;
    public static final int TAG_NIKON_TYPE2_WHITE_BALANCE = 0x0005;
    public static final int TAG_NIKON_TYPE2_IMAGE_SHARPENING = 0x0006;
    public static final int TAG_NIKON_TYPE2_FOCUS_MODE = 0x0007;
    public static final int TAG_NIKON_TYPE2_FLASH_SETTING = 0x0008;
    public static final int TAG_NIKON_TYPE2_UNKNOWN_2 = 0x000A;
    public static final int TAG_NIKON_TYPE2_ISO_SELECTION = 0x000F;
    public static final int TAG_NIKON_TYPE2_IMAGE_ADJUSTMENT = 0x0080;
    public static final int TAG_NIKON_TYPE2_ADAPTER = 0x0082;
    public static final int TAG_NIKON_TYPE2_MANUAL_FOCUS_DISTANCE = 0x0085;
    public static final int TAG_NIKON_TYPE2_DIGITAL_ZOOM = 0x0086;
    public static final int TAG_NIKON_TYPE2_AF_FOCUS_POSITION = 0x0088;
    public static final int TAG_NIKON_TYPE2_DATA_DUMP = 0x0010;

    protected static final HashMap tagNameMap = new HashMap();

    static
    {
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_ADAPTER), "Adapter");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_AF_FOCUS_POSITION), "AF Focus Position");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_COLOR_MODE), "Color Mode");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_DATA_DUMP), "Data Dump");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_DIGITAL_ZOOM), "Digital Zoom");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_FLASH_SETTING), "Flash Setting");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_FOCUS_MODE), "Focus Mode");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_IMAGE_ADJUSTMENT), "Image Adjustment");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_IMAGE_SHARPENING), "Image Sharpening");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_ISO_SELECTION), "ISO Selection");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_ISO_SETTING), "ISO Setting");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_MANUAL_FOCUS_DISTANCE), "Focus Distance");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_QUALITY), "Quality");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_1), "Makernote Unknown 1");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_2), "Makernote Unknown 2");
        tagNameMap.put(new Integer(TAG_NIKON_TYPE2_WHITE_BALANCE), "White Balance");
    }

    public NikonType2MakernoteDirectory()
    {
        this.setDescriptor(new NikonType2MakernoteDescriptor(this));
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

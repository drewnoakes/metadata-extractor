/*
 * Created by dnoakes on 27-Nov-2002 10:10:47 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 *
 */
public class OlympusMakernoteDirectory extends Directory
{
    public static final int TAG_OLYMPUS_SPECIAL_MODE = 0x0200;
    public static final int TAG_OLYMPUS_JPEG_QUALITY = 0x0201;
    public static final int TAG_OLYMPUS_MACRO_MODE = 0x0202;
    public static final int TAG_OLYMPUS_UNKNOWN_1 = 0x0203;
    public static final int TAG_OLYMPUS_DIGI_ZOOM_RATIO = 0x0204;
    public static final int TAG_OLYMPUS_UNKNOWN_2 = 0x0205;
    public static final int TAG_OLYMPUS_UNKNOWN_3 = 0x0206;
    public static final int TAG_OLYMPUS_FIRMWARE_VERSION = 0x0207;
    public static final int TAG_OLYMPUS_PICT_INFO = 0x0208;
    public static final int TAG_OLYMPUS_CAMERA_ID = 0x0209;
    public static final int TAG_OLYMPUS_DATA_DUMP = 0x0F00;

    protected static final HashMap tagNameMap = new HashMap();

    static
    {
        tagNameMap.put(new Integer(TAG_OLYMPUS_SPECIAL_MODE), "Special Mode");
        tagNameMap.put(new Integer(TAG_OLYMPUS_JPEG_QUALITY), "Jpeg Quality");
        tagNameMap.put(new Integer(TAG_OLYMPUS_MACRO_MODE), "Macro");
        tagNameMap.put(new Integer(TAG_OLYMPUS_UNKNOWN_1), "Makernote Unknown 1");
        tagNameMap.put(new Integer(TAG_OLYMPUS_DIGI_ZOOM_RATIO), "DigiZoom Ratio");
        tagNameMap.put(new Integer(TAG_OLYMPUS_UNKNOWN_2), "Makernote Unknown 2");
        tagNameMap.put(new Integer(TAG_OLYMPUS_UNKNOWN_3), "Makernote Unknown 3");
        tagNameMap.put(new Integer(TAG_OLYMPUS_FIRMWARE_VERSION), "Firmware Version");
        tagNameMap.put(new Integer(TAG_OLYMPUS_PICT_INFO), "Pict Info");
        tagNameMap.put(new Integer(TAG_OLYMPUS_CAMERA_ID), "Camera Id");
        tagNameMap.put(new Integer(TAG_OLYMPUS_DATA_DUMP), "Data Dump");
    }

    public OlympusMakernoteDirectory()
    {
        this.setDescriptor(new OlympusMakernoteDescriptor(this));
    }

    public String getName()
    {
        return "Olympus Makernote";
    }

    protected HashMap getTagNameMap()
    {
        return tagNameMap;
    }
}

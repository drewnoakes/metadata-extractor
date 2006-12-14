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
 * Describes tags specific to Panasonic cameras.
 */
public class PanasonicMakernoteDirectory extends Directory
{
    public static final int TAG_PANASONIC_QUALITY_MODE = 0x0001;
    public static final int TAG_PANASONIC_VERSION = 0x0002;
    /**
     * 1 = On
     * 2 = Off
     */
    public static final int TAG_PANASONIC_MACRO_MODE = 0x001C;
    /**
     * 1 = Normal
     * 2 = Portrait
     * 9 = Macro 
     */
    public static final int TAG_PANASONIC_RECORD_MODE = 0x001F;
    public static final int TAG_PANASONIC_PRINT_IMAGE_MATCHING_INFO = 0x0E00;

    protected static final HashMap tagNameMap = new HashMap();

    static
    {
        tagNameMap.put(new Integer(TAG_PANASONIC_QUALITY_MODE), "Quality Mode");
        tagNameMap.put(new Integer(TAG_PANASONIC_VERSION), "Version");
        tagNameMap.put(new Integer(TAG_PANASONIC_MACRO_MODE), "Macro Mode");
        tagNameMap.put(new Integer(TAG_PANASONIC_RECORD_MODE), "Record Mode");
        tagNameMap.put(new Integer(TAG_PANASONIC_PRINT_IMAGE_MATCHING_INFO), "Print Image Matching (PIM) Info");
    }

    public PanasonicMakernoteDirectory()
    {
        this.setDescriptor(new PanasonicMakernoteDescriptor(this));
    }

    public String getName()
    {
        return "Panasonic Makernote";
    }

    protected HashMap getTagNameMap()
    {
        return tagNameMap;
    }
}

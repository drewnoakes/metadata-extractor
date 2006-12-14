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
 * Created by dnoakes on 26-Nov-2002 10:58:13 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes Exif interoperability tags.
 */
public class ExifInteropDirectory extends Directory
{
    public static final int TAG_INTEROP_INDEX = 0x0001;
    public static final int TAG_INTEROP_VERSION = 0x0002;
    public static final int TAG_RELATED_IMAGE_FILE_FORMAT = 0x1000;
    public static final int TAG_RELATED_IMAGE_WIDTH = 0x1001;
    public static final int TAG_RELATED_IMAGE_LENGTH = 0x1002;

    protected static final HashMap tagNameMap;

    static
    {
        tagNameMap = new HashMap();
        tagNameMap.put(new Integer(TAG_INTEROP_INDEX), "Interoperability Index");
        tagNameMap.put(new Integer(TAG_INTEROP_VERSION), "Interoperability Version");
        tagNameMap.put(new Integer(TAG_RELATED_IMAGE_FILE_FORMAT), "Related Image File Format");
        tagNameMap.put(new Integer(TAG_RELATED_IMAGE_WIDTH), "Related Image Width");
        tagNameMap.put(new Integer(TAG_RELATED_IMAGE_LENGTH), "Related Image Length");
    }

    public ExifInteropDirectory()
    {
        this.setDescriptor(new ExifInteropDescriptor(this));
    }

    public String getName()
    {
        return "Interoperability";
    }

    protected HashMap getTagNameMap()
    {
        return tagNameMap;
    }
}

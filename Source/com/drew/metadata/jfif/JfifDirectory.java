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
 *   drew@drewnoakes.com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by Drew Noakes on Apr 25, 2011 using IntelliJ IDEA.
 */
package com.drew.metadata.jfif;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.util.HashMap;

/**
 * Directory of tags and values for the SOF0 Jfif segment.  This segment holds basic metadata about the image.
 *
 * @author YB, Drew Noakes
 */
public class JfifDirectory extends Directory
{
    public static final int TAG_JFIF_VERSION = 5;
    /** Units for pixel density fields.  One of None, Pixels per Inch, Pixels per Centimetre. */
    public static final int TAG_JFIF_UNITS = 7;
    public static final int TAG_JFIF_RESX = 8;
    public static final int TAG_JFIF_RESY = 10;

    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_JFIF_VERSION, "Version");
        _tagNameMap.put(TAG_JFIF_UNITS, "Resolution Units");
        _tagNameMap.put(TAG_JFIF_RESY, "Y Resolution");
        _tagNameMap.put(TAG_JFIF_RESX, "X Resolution");
    }

    public JfifDirectory()
    {
        this.setDescriptor(new JfifDescriptor(this));
    }

    public String getName()
    {
        return "Jfif";
    }

    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    public int getVersion() throws MetadataException
    {
        return getInt(JfifDirectory.TAG_JFIF_VERSION);
    }

    public int getResUnits() throws MetadataException
    {
        return getInt(JfifDirectory.TAG_JFIF_UNITS);
    }

    public int getImageWidth() throws MetadataException
    {
        return getInt(JfifDirectory.TAG_JFIF_RESY);
    }

    public int getImageHeight() throws MetadataException
    {
        return getInt(JfifDirectory.TAG_JFIF_RESX);
    }

}
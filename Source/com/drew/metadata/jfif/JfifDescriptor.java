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
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string versions of the tags stored in a JfifDirectory.
 * <p/>
 * More info at: http://en.wikipedia.org/wiki/JPEG_File_Interchange_Format
 *
 * @author YB, Drew Noakes
 */
public class JfifDescriptor extends TagDescriptor
{
    public JfifDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType) {
            case JfifDirectory.TAG_JFIF_RESX:
                return getImageResXDescription();
            case JfifDirectory.TAG_JFIF_RESY:
                return getImageResYDescription();
            case JfifDirectory.TAG_JFIF_VERSION:
                return getImageVersionDescription();
            case JfifDirectory.TAG_JFIF_UNITS:
                return getImageResUnitsDescription();
        }

        return _directory.getString(tagType);
    }

    public String getImageVersionDescription() throws MetadataException
    {
        if (!_directory.containsTag(JfifDirectory.TAG_JFIF_VERSION))
            return null;
        int v = _directory.getInt(JfifDirectory.TAG_JFIF_VERSION);
        return String.format("%d.%d", (v & 0xFF00) >> 8, v & 0xFF);
    }

    public String getImageResYDescription() throws MetadataException
    {
        if (!_directory.containsTag(JfifDirectory.TAG_JFIF_RESY))
            return null;
        int resY = _directory.getInt(JfifDirectory.TAG_JFIF_RESY);
        return String.format("%d dot%s",
                resY,
                resY==1 ? "" : "s");
    }

    public String getImageResXDescription() throws MetadataException
    {
        if (!_directory.containsTag(JfifDirectory.TAG_JFIF_RESX))
            return null;
        int resX = _directory.getInt(JfifDirectory.TAG_JFIF_RESX);
        return String.format("%d dot%s",
                resX,
                resX==1 ? "" : "s");
    }

    public String getImageResUnitsDescription() throws MetadataException
    {
        if (!_directory.containsTag(JfifDirectory.TAG_JFIF_UNITS))
            return null;
        switch (_directory.getInt(JfifDirectory.TAG_JFIF_UNITS)) {
            case 0: return "none";
            case 1: return "inch";
            case 2: return "centimetre";
            default:
                return "unit";
        }
    }
}
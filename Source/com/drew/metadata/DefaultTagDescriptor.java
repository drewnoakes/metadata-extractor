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
 * Created by dnoakes on 22-Nov-2002 16:45:19 using IntelliJ IDEA.
 */
package com.drew.metadata;

/**
 * A default implementation of the abstract TagDescriptor.  As this class is not coded with awareness of any metadata
 * tags, it simply reports tag names using the format 'Unknown tag 0x00' (with the corresponding tag number in hex)
 * and gives descriptions using the default string representation of the value.
 */
public class DefaultTagDescriptor extends TagDescriptor
{
    public DefaultTagDescriptor(Directory directory)
    {
        super(directory);
    }

    /**
     * Gets a best-effort tag name using the format 'Unknown tag 0x00' (with the corresponding tag type in hex).
     * @param tagType the tag type identifier.
     * @return a string representation of the tag name.
     */
    public String getTagName(int tagType)
    {
        String hex = Integer.toHexString(tagType).toUpperCase();
        while (hex.length() < 4) hex = "0" + hex;
        return "Unknown tag 0x" + hex;
    }

    /**
     * Gets the default string representation of the tag's value.
     * @param tagType the tag type identifier.
     * @return a string representation of the tag's value.
     */
    public String getDescription(int tagType)
    {
        return _directory.getString(tagType);
    }
}

/*
 * ImageInfo.java
 *
 * This class is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  Similarly, I release this Java version under the
 * same license, though I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   drew.noakes@drewnoakes.com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 28-Oct-2002 13:51:28 using IntelliJ IDEA.
 *
 * Created 28 Oct 2002 (v1.2)
 * - New class to encapsualte information about a particular tag
 */
package com.drew.imaging.exif;

/**
 * Encapsulates information about a particular Exif tag.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class TagValue
{
    /**
     * Private reference to the IFD directory type of this tag.
     */
    private final int directoryType;

    /**
     * Private reference to the type of this tag.
     */
    private final int tagType;

    /**
     * Private reference to the ImageInfo object that created this tag
     * and contains master-copy information for all tags extracted from
     * the image.
     */
    private final ImageInfo info;

    /**
     * Creates a TagValue with all necessary state.
     * @param directoryType the IFD directory type (e.g. IFD_EXIF)
     * @param tagType the tag type (e.g. TAG_APERTURE)
     * @param info a reference to the ImageInfo object created during
     *        the extraction process
     */
    public TagValue(int directoryType, int tagType, ImageInfo info)
    {
        this.directoryType = directoryType;
        this.tagType = tagType;
        this.info = info;
    }

    /**
     * Gets the directory type (e.g. IFD_EXIF)
     * @return the directory type as an int
     */
    public int getDirectoryType()
    {
        return directoryType;
    }

    /**
     * Gets the tag type as one of the TAG_* enumerated constants in
     * <code>ExifTagValues</code> e.g. <code>TAG_APERTURE</code> as an
     * int
     * @return the tag type as an int
     */
    public int getTagType()
    {
        return tagType;
    }

    /**
     * Gets the tag type as one of the TAG_* enumerated constants in
     * <code>ExifTagValues</code> e.g. <code>TAG_APERTURE</code> in hex
     * notation as a String (i.e. <code>0x100E</code>)
     * @return the tag type as a string in hexadecimal notation
     */
    public String getTagTypeHex()
    {
        String hex = Integer.toHexString(tagType);
        while (hex.length() < 4) hex = "0" + hex;
        return "0x" + hex;
    }

    /**
     * Get a description of the tag's value, considering enumerated values
     * and units.
     * @return a description of the tag's value
     */
    public String getDescription()
    {
        return info.getDescription(directoryType, tagType);
    }

    /**
     * Get the name of the tag, such as <code>Aperture</code>, or
     * <code>InteropVersion</code>.
     * @return the tag's name
     */
    public String getTagName()
    {
        return info.getTagName(directoryType, tagType);
    }

    /**
     * Get the name of the directory in which the tag exists, such as
     * <code>Exif</code>, <code>GPS</code> or <code>Interoperability</code>.
     * @return name of the directory in which this tag exists
     */
    public String getDirectoryName()
    {
        return info.getDirectoryName(directoryType);
    }

    /**
     * A basic representation of the tag's type and value in format:
     * <code>FNumber - F2.8</code>.
     * @return the tag's type and value
     */
    public String toString()
    {
        return getTagName() + " - " + getDescription();
    }
}

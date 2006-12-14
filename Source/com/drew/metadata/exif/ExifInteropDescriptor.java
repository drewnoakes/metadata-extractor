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
 * Created by dnoakes on 12-Nov-2002 22:27:34 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string represenations of tag values stored in a <code>ExifInteropDirectory</code>.
 */
public class ExifInteropDescriptor extends TagDescriptor
{
    public ExifInteropDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType) {
            case ExifInteropDirectory.TAG_INTEROP_INDEX:
                return getInteropIndexDescription();
            case ExifInteropDirectory.TAG_INTEROP_VERSION:
                return getInteropVersionDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    public String getInteropVersionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifInteropDirectory.TAG_INTEROP_VERSION)) return null;
        int[] ints = _directory.getIntArray(ExifInteropDirectory.TAG_INTEROP_VERSION);
        return ExifDescriptor.convertBytesToVersionString(ints);
    }

    public String getInteropIndexDescription()
    {
        if (!_directory.containsTag(ExifInteropDirectory.TAG_INTEROP_INDEX)) return null;
        String interopIndex = _directory.getString(ExifInteropDirectory.TAG_INTEROP_INDEX).trim();
        if ("R98".equalsIgnoreCase(interopIndex)) {
            return "Recommended Exif Interoperability Rules (ExifR98)";
        } else {
            return "Unknown (" + interopIndex + ")";
        }
    }
}

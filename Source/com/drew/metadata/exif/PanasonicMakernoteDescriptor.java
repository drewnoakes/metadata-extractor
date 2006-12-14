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
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string represenations of tag values stored in a <code>PanasonicMakernoteDirectory</code>.
 *
 * Some information about this makernote taken from here:
 * http://www.ozhiker.com/electronics/pjmt/jpeg_info/panasonic_mn.html
 */
public class PanasonicMakernoteDescriptor extends TagDescriptor
{
    public PanasonicMakernoteDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType)
        {
            case PanasonicMakernoteDirectory.TAG_PANASONIC_MACRO_MODE:
                return getMacroModeDescription();
            case PanasonicMakernoteDirectory.TAG_PANASONIC_RECORD_MODE:
                return getRecordModeDescription();
            case PanasonicMakernoteDirectory.TAG_PANASONIC_PRINT_IMAGE_MATCHING_INFO:
                return getPrintImageMatchingInfoDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    public String getPrintImageMatchingInfoDescription() throws MetadataException
    {
        if (!_directory.containsTag(PanasonicMakernoteDirectory.TAG_PANASONIC_PRINT_IMAGE_MATCHING_INFO)) return null;
        byte[] bytes = _directory.getByteArray(PanasonicMakernoteDirectory.TAG_PANASONIC_PRINT_IMAGE_MATCHING_INFO);
        return "(" + bytes.length + " bytes)";
    }

    public String getMacroModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(PanasonicMakernoteDirectory.TAG_PANASONIC_MACRO_MODE)) return null;
        int value = _directory.getInt(PanasonicMakernoteDirectory.TAG_PANASONIC_MACRO_MODE);
        switch (value) {
            case 1:
                return "On";
            case 2:
                return "Off";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getRecordModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(PanasonicMakernoteDirectory.TAG_PANASONIC_RECORD_MODE)) return null;
        int value = _directory.getInt(PanasonicMakernoteDirectory.TAG_PANASONIC_RECORD_MODE);
        switch (value) {
            case 1:
                return "Normal";
            case 2:
                return "Portrait";
            case 9:
                return "Macro";
            default:
                return "Unknown (" + value + ")";
        }
    }
}

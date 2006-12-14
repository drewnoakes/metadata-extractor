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
 * Provides human-readable string represenations of tag values stored in a <code>OlympusMakernoteDirectory</code>.
 */
public class OlympusMakernoteDescriptor extends TagDescriptor
{
    public OlympusMakernoteDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType) {
            case OlympusMakernoteDirectory.TAG_OLYMPUS_SPECIAL_MODE:
                return getSpecialModeDescription();
            case OlympusMakernoteDirectory.TAG_OLYMPUS_JPEG_QUALITY:
                return getJpegQualityDescription();
            case OlympusMakernoteDirectory.TAG_OLYMPUS_MACRO_MODE:
                return getMacroModeDescription();
            case OlympusMakernoteDirectory.TAG_OLYMPUS_DIGI_ZOOM_RATIO:
                return getDigiZoomRatioDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    public String getDigiZoomRatioDescription() throws MetadataException
    {
        if (!_directory.containsTag(OlympusMakernoteDirectory.TAG_OLYMPUS_DIGI_ZOOM_RATIO)) return null;
        int value = _directory.getInt(OlympusMakernoteDirectory.TAG_OLYMPUS_DIGI_ZOOM_RATIO);
        switch (value) {
            case 0:
                return "Normal";
            case 2:
                return "Digital 2x Zoom";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getMacroModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(OlympusMakernoteDirectory.TAG_OLYMPUS_MACRO_MODE)) return null;
        int value = _directory.getInt(OlympusMakernoteDirectory.TAG_OLYMPUS_MACRO_MODE);
        switch (value) {
            case 0:
                return "Normal (no macro)";
            case 1:
                return "Macro";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getJpegQualityDescription() throws MetadataException
    {
        if (!_directory.containsTag(OlympusMakernoteDirectory.TAG_OLYMPUS_JPEG_QUALITY)) return null;
        int value = _directory.getInt(OlympusMakernoteDirectory.TAG_OLYMPUS_JPEG_QUALITY);
        switch (value) {
            case 1:
                return "SQ";
            case 2:
                return "HQ";
            case 3:
                return "SHQ";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getSpecialModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(OlympusMakernoteDirectory.TAG_OLYMPUS_SPECIAL_MODE)) return null;
        int[] values = _directory.getIntArray(OlympusMakernoteDirectory.TAG_OLYMPUS_SPECIAL_MODE);
        StringBuffer desc = new StringBuffer();
        switch (values[0]) {
            case 0:
                desc.append("Normal picture taking mode");
                break;
            case 1:
                desc.append("Unknown picture taking mode");
                break;
            case 2:
                desc.append("Fast picture taking mode");
                break;
            case 3:
                desc.append("Panorama picture taking mode");
                break;
            default:
                desc.append("Unknown picture taking mode");
                break;
        }
        desc.append(" - ");
        switch (values[1]) {
            case 0:
                desc.append("Unknown sequence number");
                break;
            case 1:
                desc.append("1st in a sequnce");
                break;
            case 2:
                desc.append("2nd in a sequence");
                break;
            case 3:
                desc.append("3rd in a sequence");
                break;
            default:
                desc.append(values[1]);
                desc.append("th in a sequence");
                break;
        }
        switch (values[2]) {
            case 1:
                desc.append("Left to right panorama direction");
                break;
            case 2:
                desc.append("Right to left panorama direction");
                break;
            case 3:
                desc.append("Bottom to top panorama direction");
                break;
            case 4:
                desc.append("Top to bottom panorama direction");
                break;
        }
        return desc.toString();
    }
}

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

import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string represenations of tag values stored in a <code>NikonType1MakernoteDirectory</code>.
 *
 * Type-1 is for E-Series cameras prior to (not including) E990.  For example: E700, E800, E900,
 * E900S, E910, E950.
 *
 * MakerNote starts from ASCII string "Nikon". Data format is the same as IFD, but it starts from
 * offset 0x08. This is the same as Olympus except start string. Example of actual data
 * structure is shown below.
 * <pre><code>
 * :0000: 4E 69 6B 6F 6E 00 01 00-05 00 02 00 02 00 06 00 Nikon...........
 * :0010: 00 00 EC 02 00 00 03 00-03 00 01 00 00 00 06 00 ................
 * </code></pre>
 */
public class NikonType1MakernoteDescriptor extends TagDescriptor
{
    public NikonType1MakernoteDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType) {
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_QUALITY:
                return getQualityDescription();
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_COLOR_MODE:
                return getColorModeDescription();
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_IMAGE_ADJUSTMENT:
                return getImageAdjustmentDescription();
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_CCD_SENSITIVITY:
                return getCcdSensitivityDescription();
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_FOCUS:
                return getFocusDescription();
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_CONVERTER:
                return getConverterDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    public String getConverterDescription() throws MetadataException
    {
        if (!_directory.containsTag(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_CONVERTER)) return null;
        int value = _directory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_CONVERTER);
        switch (value) {
            case 0:
                return "None";
            case 1:
                return "Fisheye converter";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getDigitalZoomDescription() throws MetadataException
    {
        if (!_directory.containsTag(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_DIGITAL_ZOOM)) return null;
        Rational value = _directory.getRational(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_DIGITAL_ZOOM);
        if (value.getNumerator() == 0) {
            return "No digital zoom";
        }
        return value.toSimpleString(true) + "x digital zoom";
    }

    public String getFocusDescription() throws MetadataException
    {
        if (!_directory.containsTag(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_FOCUS)) return null;
        Rational value = _directory.getRational(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_FOCUS);
        if (value.getNumerator() == 1 && value.getDenominator() == 0) {
            return "Infinite";
        }
        return value.toSimpleString(true);
    }

    public String getWhiteBalanceDescription() throws MetadataException
    {
        if (!_directory.containsTag(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_WHITE_BALANCE)) return null;
        int value = _directory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_WHITE_BALANCE);
        switch (value) {
            case 0:
                return "Auto";
            case 1:
                return "Preset";
            case 2:
                return "Daylight";
            case 3:
                return "Incandescense";
            case 4:
                return "Flourescence";
            case 5:
                return "Cloudy";
            case 6:
                return "SpeedLight";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getCcdSensitivityDescription() throws MetadataException
    {
        if (!_directory.containsTag(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_CCD_SENSITIVITY)) return null;
        int value = _directory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_CCD_SENSITIVITY);
        switch (value) {
            case 0:
                return "ISO80";
            case 2:
                return "ISO160";
            case 4:
                return "ISO320";
            case 5:
                return "ISO100";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getImageAdjustmentDescription() throws MetadataException
    {
        if (!_directory.containsTag(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_IMAGE_ADJUSTMENT)) return null;
        int value = _directory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_IMAGE_ADJUSTMENT);
        switch (value) {
            case 0:
                return "Normal";
            case 1:
                return "Bright +";
            case 2:
                return "Bright -";
            case 3:
                return "Contrast +";
            case 4:
                return "Contrast -";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getColorModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_COLOR_MODE)) return null;
        int value = _directory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_COLOR_MODE);
        switch (value) {
            case 1:
                return "Color";
            case 2:
                return "Monochrome";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getQualityDescription() throws MetadataException
    {
        if (!_directory.containsTag(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_QUALITY)) return null;
        int value = _directory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_QUALITY);
        switch (value) {
            case 1:
                return "VGA Basic";
            case 2:
                return "VGA Normal";
            case 3:
                return "VGA Fine";
            case 4:
                return "SXGA Basic";
            case 5:
                return "SXGA Normal";
            case 6:
                return "SXGA Fine";
            default:
                return "Unknown (" + value + ")";
        }
    }
}

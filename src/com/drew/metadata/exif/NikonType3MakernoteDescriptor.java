/*
 * Created by dnoakes on 3-Oct-2002 10:12:05 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;
import com.drew.metadata.MetadataException;
import com.drew.lang.Rational;

/**
 * There are 3 formats of Nikon's MakerNote. MakerNote of E700/E800/E900/E900S/E910/E950
 * starts from ASCII string "Nikon". Data format is the same as IFD, but it starts from
 * offset 0x08. This is the same as Olympus except start string. Example of actual data
 * structure is shown below.
 * <pre><code>
 * :0000: 4E 69 6B 6F 6E 00 02 00-00 00 4D 4D 00 2A 00 00 Nikon....MM.*...
 * :0010: 00 08 00 1E 00 01 00 07-00 00 00 04 30 32 30 30 ............0200
 * </code></pre>
 */
public class NikonType3MakernoteDescriptor extends TagDescriptor
{
    public NikonType3MakernoteDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType)
        {
            case NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_LENS:
                return getLensDescription();
            case NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_CAMERA_HUE_ADJUSTMENT:
                return getHueAdjustmentDescription();
            case NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_CAMERA_COLOR_MODE:
                return getColorModeDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    public String getLensDescription() throws MetadataException
    {
        if (!_directory.containsTag(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_LENS))
            return null;

        Rational[] lensValues = _directory.getRationalArray(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_LENS);

        if (lensValues.length!=4)
            return _directory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_LENS);

        StringBuffer description = new StringBuffer();
        description.append(lensValues[0].intValue());
        description.append('-');
        description.append(lensValues[1].intValue());
        description.append("mm f/");
        description.append(lensValues[2].floatValue());
        description.append('-');
        description.append(lensValues[3].floatValue());

        return description.toString();
    }

    public String getHueAdjustmentDescription()
    {
        if (!_directory.containsTag(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_CAMERA_HUE_ADJUSTMENT))
            return null;

        return _directory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_CAMERA_HUE_ADJUSTMENT) + " degrees";
    }

    public String getColorModeDescription()
    {
        if (!_directory.containsTag(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_CAMERA_COLOR_MODE))
            return null;

        String raw = _directory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_CAMERA_COLOR_MODE);
        if (raw.startsWith("MODE1"))
            return "Mode I (sRGB)";

        return raw;
    }
}

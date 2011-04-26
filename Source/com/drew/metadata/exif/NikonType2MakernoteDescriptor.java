/*
 * Copyright 2002-2011 Drew Noakes
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.metadata.exif;

import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

import java.text.DecimalFormat;

/**
 * Provides human-readable string representations of tag values stored in a <code>NikonType2MakernoteDirectory</code>.
 *
 * Type-2 applies to the E990 and D-series cameras such as the D1, D70 and D100.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class NikonType2MakernoteDescriptor extends TagDescriptor
{
    public NikonType2MakernoteDescriptor(Directory directory)
    {
        super(directory);
    }

    private NikonType2MakernoteDirectory getMakernoteDirectory()
    {
        return (NikonType2MakernoteDirectory)_directory;
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType)
        {
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS:
                return getLensDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_HUE_ADJUSTMENT:
                return getHueAdjustmentDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_COLOR_MODE:
                return getColorModeDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION:
                return getAutoFlashCompensationDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_1:
                return getIsoSettingDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_FOCUS_POSITION:
                return getAutoFocusPositionDescription();
            case NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FIRMWARE_VERSION:
                return getAutoFirmwareVersionDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    public String getAutoFocusPositionDescription() throws MetadataException
    {
        if (!_directory.containsTag(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_FOCUS_POSITION)) return null;
        int[] values = _directory.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_FOCUS_POSITION);
        if (values.length != 4 || values[0] != 0 || values[2] != 0 || values[3] != 0) {
            return "Unknown (" + _directory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_FOCUS_POSITION) + ")";
        }
        switch (values[1]) {
            case 0:
                return "Centre";
            case 1:
                return "Top";
            case 2:
                return "Bottom";
            case 3:
                return "Left";
            case 4:
                return "Right";
            default:
                return "Unknown (" + values[1] + ")";
        }
    }

    public String getDigitalZoomDescription() throws MetadataException
    {
        if (!_directory.containsTag(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_DIGITAL_ZOOM)) return null;
        Rational rational = _directory.getRational(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_DIGITAL_ZOOM);
        if (rational.intValue() == 1) {
            return "No digital zoom";
        }
        return rational.toSimpleString(true) + "x digital zoom";
    }

    public String getIsoSettingDescription() throws MetadataException
    {
        if (!_directory.containsTag(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_1)) return null;
        int[] values = _directory.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_1);
        if (values[0] != 0 || values[1] == 0) {
            return "Unknown (" + _directory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_1) + ")";
        }
        return "ISO " + values[1];
    }

    public String getAutoFlashCompensationDescription() throws MetadataException
    {
        Rational ev = getMakernoteDirectory().getAutoFlashCompensation();

        if (ev==null)
            return "Unknown";

        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        return decimalFormat.format(ev.floatValue()) + " EV";
    }

    public String getLensDescription() throws MetadataException
    {
        if (!_directory.containsTag(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS))
            return null;

        Rational[] lensValues = _directory.getRationalArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS);

        if (lensValues.length!=4)
            return _directory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS);

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
        if (!_directory.containsTag(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_HUE_ADJUSTMENT))
            return null;

        return _directory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_HUE_ADJUSTMENT) + " degrees";
    }

    public String getColorModeDescription()
    {
        if (!_directory.containsTag(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_COLOR_MODE))
            return null;

        String raw = _directory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_COLOR_MODE);
        if (raw.startsWith("MODE1"))
            return "Mode I (sRGB)";

        return raw;
    }

    public String getAutoFirmwareVersionDescription() throws MetadataException
    {
        if (!_directory.containsTag(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FIRMWARE_VERSION))
            return null;

        int[] ints = _directory.getIntArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FIRMWARE_VERSION);
        return ExifDescriptor.convertBytesToVersionString(ints);
    }
}

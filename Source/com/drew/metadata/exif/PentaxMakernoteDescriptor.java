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

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string representations of tag values stored in a <code>PentaxMakernoteDirectory</code>.
 * <p/>
 * Some information about this makernote taken from here:
 * http://www.ozhiker.com/electronics/pjmt/jpeg_info/pentax_mn.html
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class PentaxMakernoteDescriptor extends TagDescriptor
{
    public PentaxMakernoteDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType) 
        {
            case PentaxMakernoteDirectory.TAG_PENTAX_CAPTURE_MODE:
                return getCaptureModeDescription();
            case PentaxMakernoteDirectory.TAG_PENTAX_QUALITY_LEVEL:
                return getQualityLevelDescription();
            case PentaxMakernoteDirectory.TAG_PENTAX_FOCUS_MODE:
                return getFocusModeDescription();
            case PentaxMakernoteDirectory.TAG_PENTAX_FLASH_MODE:
                return getFlashModeDescription();
            case PentaxMakernoteDirectory.TAG_PENTAX_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case PentaxMakernoteDirectory.TAG_PENTAX_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case PentaxMakernoteDirectory.TAG_PENTAX_SHARPNESS:
                return getSharpnessDescription();
            case PentaxMakernoteDirectory.TAG_PENTAX_CONTRAST:
                return getContrastDescription();
            case PentaxMakernoteDirectory.TAG_PENTAX_SATURATION:
                return getSaturationDescription();
            case PentaxMakernoteDirectory.TAG_PENTAX_ISO_SPEED:
                return getIsoSpeedDescription();
            case PentaxMakernoteDirectory.TAG_PENTAX_COLOUR:
                return getColourDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    public String getColourDescription() throws MetadataException
    {
        if (!_directory.containsTag(PentaxMakernoteDirectory.TAG_PENTAX_COLOUR)) return null;
        int value = _directory.getInt(PentaxMakernoteDirectory.TAG_PENTAX_COLOUR);
        switch (value)
        {
            case 1:  return "Normal";
            case 2:  return "Black & White";
            case 3:  return "Sepia";
            default: return "Unknown (" + value + ")";
        }
    }

    public String getIsoSpeedDescription() throws MetadataException
    {
        if (!_directory.containsTag(PentaxMakernoteDirectory.TAG_PENTAX_ISO_SPEED)) return null;
        int value = _directory.getInt(PentaxMakernoteDirectory.TAG_PENTAX_ISO_SPEED);
        switch (value)
        {
            // TODO there must be other values which aren't catered for here
            case 10:  return "ISO 100";
            case 16:  return "ISO 200";
            case 100: return "ISO 100";
            case 200: return "ISO 200";
            default: return "Unknown (" + value + ")";
        }
    }

    public String getSaturationDescription() throws MetadataException
    {
        if (!_directory.containsTag(PentaxMakernoteDirectory.TAG_PENTAX_SATURATION)) return null;
        int value = _directory.getInt(PentaxMakernoteDirectory.TAG_PENTAX_SATURATION);
        switch (value)
        {
            case 0:  return "Normal";
            case 1:  return "Low";
            case 2:  return "High";
            default: return "Unknown (" + value + ")";
        }
    }

    public String getContrastDescription() throws MetadataException
    {
        if (!_directory.containsTag(PentaxMakernoteDirectory.TAG_PENTAX_CONTRAST)) return null;
        int value = _directory.getInt(PentaxMakernoteDirectory.TAG_PENTAX_CONTRAST);
        switch (value)
        {
            case 0:  return "Normal";
            case 1:  return "Low";
            case 2:  return "High";
            default: return "Unknown (" + value + ")";
        }
    }

    public String getSharpnessDescription() throws MetadataException
    {
        if (!_directory.containsTag(PentaxMakernoteDirectory.TAG_PENTAX_SHARPNESS)) return null;
        int value = _directory.getInt(PentaxMakernoteDirectory.TAG_PENTAX_SHARPNESS);
        switch (value)
        {
            case 0:  return "Normal";
            case 1:  return "Soft";
            case 2:  return "Hard";
            default: return "Unknown (" + value + ")";
        }
    }

    public String getDigitalZoomDescription() throws MetadataException
    {
        if (!_directory.containsTag(PentaxMakernoteDirectory.TAG_PENTAX_DIGITAL_ZOOM)) return null;
        float value = _directory.getFloat(PentaxMakernoteDirectory.TAG_PENTAX_DIGITAL_ZOOM);
        if (value==0)
            return "Off";
        return Float.toString(value);
    }

    public String getWhiteBalanceDescription() throws MetadataException
    {
        if (!_directory.containsTag(PentaxMakernoteDirectory.TAG_PENTAX_WHITE_BALANCE)) return null;
        int value = _directory.getInt(PentaxMakernoteDirectory.TAG_PENTAX_WHITE_BALANCE);
        switch (value)
        {
            case 0:  return "Auto";
            case 1:  return "Daylight";
            case 2:  return "Shade";
            case 3:  return "Tungsten";
            case 4:  return "Fluorescent";
            case 5:  return "Manual";
            default: return "Unknown (" + value + ")";
        }
    }

    public String getFlashModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(PentaxMakernoteDirectory.TAG_PENTAX_FLASH_MODE)) return null;
        int value = _directory.getInt(PentaxMakernoteDirectory.TAG_PENTAX_FLASH_MODE);
        switch (value)
        {
            case 1:  return "Auto";
            case 2:  return "Flash On";
            case 4:  return "Flash Off";
            case 6:  return "Red-eye Reduction";
            default: return "Unknown (" + value + ")";
        }
    }

    public String getFocusModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(PentaxMakernoteDirectory.TAG_PENTAX_FOCUS_MODE)) return null;
        int value = _directory.getInt(PentaxMakernoteDirectory.TAG_PENTAX_FOCUS_MODE);
        switch (value)
        {
            case 2:  return "Custom";
            case 3:  return "Auto";
            default: return "Unknown (" + value + ")";
        }
    }

    public String getQualityLevelDescription() throws MetadataException
    {
        if (!_directory.containsTag(PentaxMakernoteDirectory.TAG_PENTAX_QUALITY_LEVEL)) return null;
        int value = _directory.getInt(PentaxMakernoteDirectory.TAG_PENTAX_QUALITY_LEVEL);
        switch (value)
        {
            case 0:  return "Good";
            case 1:  return "Better";
            case 2:  return "Best";
            default: return "Unknown (" + value + ")";
        }
    }

    public String getCaptureModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(PentaxMakernoteDirectory.TAG_PENTAX_CAPTURE_MODE)) return null;
        int value = _directory.getInt(PentaxMakernoteDirectory.TAG_PENTAX_CAPTURE_MODE);
        switch (value)
        {
            case 1:  return "Auto";
            case 2:  return "Night-scene";
            case 3:  return "Manual";
            case 4:  return "Multiple";
            default: return "Unknown (" + value + ")";
        }
    }

/*
    public String getPrintImageMatchingInfoDescription() throws MetadataException
    {
        if (!_directory.containsTag(PentaxMakernoteDirectory.TAG_PANASONIC_PRINT_IMAGE_MATCHING_INFO)) return null;
        byte[] bytes = _directory.getByteArray(PentaxMakernoteDirectory.TAG_PANASONIC_PRINT_IMAGE_MATCHING_INFO);
        return "(" + bytes.length + " bytes)";
    }

    public String getMacroModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(PentaxMakernoteDirectory.TAG_PANASONIC_MACRO_MODE)) return null;
        int value = _directory.getInt(PentaxMakernoteDirectory.TAG_PANASONIC_MACRO_MODE);
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
        if (!_directory.containsTag(PentaxMakernoteDirectory.TAG_PANASONIC_RECORD_MODE)) return null;
        int value = _directory.getInt(PentaxMakernoteDirectory.TAG_PANASONIC_RECORD_MODE);
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
*/
}

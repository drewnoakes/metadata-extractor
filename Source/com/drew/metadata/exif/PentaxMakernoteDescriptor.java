/*
 * Copyright 2002-2012 Drew Noakes
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

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string representations of tag values stored in a <code>PentaxMakernoteDirectory</code>.
 * <p/>
 * Some information about this makernote taken from here:
 * http://www.ozhiker.com/electronics/pjmt/jpeg_info/pentax_mn.html
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class PentaxMakernoteDescriptor extends TagDescriptor<PentaxMakernoteDirectory>
{
    public PentaxMakernoteDescriptor(@NotNull PentaxMakernoteDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
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
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getColourDescription()
    {
        Integer value = _directory.getInteger(PentaxMakernoteDirectory.TAG_PENTAX_COLOUR);
        if (value==null)
            return null;
        switch (value)
        {
            case 1:  return "Normal";
            case 2:  return "Black & White";
            case 3:  return "Sepia";
            default: return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getIsoSpeedDescription()
    {
        Integer value = _directory.getInteger(PentaxMakernoteDirectory.TAG_PENTAX_ISO_SPEED);
        if (value==null)
            return null;
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

    @Nullable
    public String getSaturationDescription()
    {
        Integer value = _directory.getInteger(PentaxMakernoteDirectory.TAG_PENTAX_SATURATION);
        if (value==null)
            return null;
        switch (value)
        {
            case 0:  return "Normal";
            case 1:  return "Low";
            case 2:  return "High";
            default: return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getContrastDescription()
    {
        Integer value = _directory.getInteger(PentaxMakernoteDirectory.TAG_PENTAX_CONTRAST);
        if (value==null)
            return null;
        switch (value)
        {
            case 0:  return "Normal";
            case 1:  return "Low";
            case 2:  return "High";
            default: return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSharpnessDescription()
    {
        Integer value = _directory.getInteger(PentaxMakernoteDirectory.TAG_PENTAX_SHARPNESS);
        if (value==null)
            return null;
        switch (value)
        {
            case 0:  return "Normal";
            case 1:  return "Soft";
            case 2:  return "Hard";
            default: return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getDigitalZoomDescription()
    {
        Float value = _directory.getFloatObject(PentaxMakernoteDirectory.TAG_PENTAX_DIGITAL_ZOOM);
        if (value==null)
            return null;
        if (value==0)
            return "Off";
        return Float.toString(value);
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        Integer value = _directory.getInteger(PentaxMakernoteDirectory.TAG_PENTAX_WHITE_BALANCE);
        if (value==null)
            return null;
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

    @Nullable
    public String getFlashModeDescription()
    {
        Integer value = _directory.getInteger(PentaxMakernoteDirectory.TAG_PENTAX_FLASH_MODE);
        if (value==null)
            return null;
        switch (value)
        {
            case 1:  return "Auto";
            case 2:  return "Flash On";
            case 4:  return "Flash Off";
            case 6:  return "Red-eye Reduction";
            default: return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFocusModeDescription()
    {
        Integer value = _directory.getInteger(PentaxMakernoteDirectory.TAG_PENTAX_FOCUS_MODE);
        if (value==null)
            return null;
        switch (value)
        {
            case 2:  return "Custom";
            case 3:  return "Auto";
            default: return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getQualityLevelDescription()
    {
        Integer value = _directory.getInteger(PentaxMakernoteDirectory.TAG_PENTAX_QUALITY_LEVEL);
        if (value==null)
            return null;
        switch (value)
        {
            case 0:  return "Good";
            case 1:  return "Better";
            case 2:  return "Best";
            default: return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getCaptureModeDescription()
    {
        Integer value = _directory.getInteger(PentaxMakernoteDirectory.TAG_PENTAX_CAPTURE_MODE);
        if (value==null)
            return null;
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
    public String getPrintImageMatchingInfoDescription()
    {
        byte[] bytes = _directory.getByteArray(PentaxMakernoteDirectory.TAG_PANASONIC_PRINT_IMAGE_MATCHING_INFO);
        if (bytes==null)
            return null;
        return "(" + bytes.length + " bytes)";
    }

    public String getMacroModeDescription()
    {
        Integer value = _directory.getInteger(PentaxMakernoteDirectory.TAG_PANASONIC_MACRO_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 1:
                return "On";
            case 2:
                return "Off";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getRecordModeDescription()
    {
        Integer value = _directory.getInteger(PentaxMakernoteDirectory.TAG_PANASONIC_RECORD_MODE);
        if (value==null)
            return null;
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

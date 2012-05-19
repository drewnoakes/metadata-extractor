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
 * Provides human-readable string representations of tag values stored in a <code>CasioType1MakernoteDirectory</code>.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class CasioType1MakernoteDescriptor extends TagDescriptor<CasioType1MakernoteDirectory>
{
    public CasioType1MakernoteDescriptor(@NotNull CasioType1MakernoteDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case CasioType1MakernoteDirectory.TAG_CASIO_RECORDING_MODE:
                return getRecordingModeDescription();
            case CasioType1MakernoteDirectory.TAG_CASIO_QUALITY:
                return getQualityDescription();
            case CasioType1MakernoteDirectory.TAG_CASIO_FOCUSING_MODE:
                return getFocusingModeDescription();
            case CasioType1MakernoteDirectory.TAG_CASIO_FLASH_MODE:
                return getFlashModeDescription();
            case CasioType1MakernoteDirectory.TAG_CASIO_FLASH_INTENSITY:
                return getFlashIntensityDescription();
            case CasioType1MakernoteDirectory.TAG_CASIO_OBJECT_DISTANCE:
                return getObjectDistanceDescription();
            case CasioType1MakernoteDirectory.TAG_CASIO_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case CasioType1MakernoteDirectory.TAG_CASIO_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case CasioType1MakernoteDirectory.TAG_CASIO_SHARPNESS:
                return getSharpnessDescription();
            case CasioType1MakernoteDirectory.TAG_CASIO_CONTRAST:
                return getContrastDescription();
            case CasioType1MakernoteDirectory.TAG_CASIO_SATURATION:
                return getSaturationDescription();
            case CasioType1MakernoteDirectory.TAG_CASIO_CCD_SENSITIVITY:
                return getCcdSensitivityDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getCcdSensitivityDescription()
    {
        Integer value = _directory.getInteger(CasioType1MakernoteDirectory.TAG_CASIO_CCD_SENSITIVITY);

        if (value == null)
            return null;

        switch (value) {
            // these four for QV3000
            case 64:
                return "Normal";
            case 125:
                return "+1.0";
            case 250:
                return "+2.0";
            case 244:
                return "+3.0";
                // these two for QV8000/2000
            case 80:
                return "Normal (ISO 80 equivalent)";
            case 100:
                return "High";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSaturationDescription()
    {
        Integer value = _directory.getInteger(CasioType1MakernoteDirectory.TAG_CASIO_SATURATION);

        if (value == null)
            return null;

        switch (value) {
            case 0:
                return "Normal";
            case 1:
                return "Low";
            case 2:
                return "High";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getContrastDescription()
    {
        Integer value = _directory.getInteger(CasioType1MakernoteDirectory.TAG_CASIO_CONTRAST);

        if (value == null)
            return null;

        switch (value) {
            case 0:
                return "Normal";
            case 1:
                return "Low";
            case 2:
                return "High";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSharpnessDescription()
    {
        Integer value = _directory.getInteger(CasioType1MakernoteDirectory.TAG_CASIO_SHARPNESS);

        if (value == null)
            return null;

        switch (value) {
            case 0:
                return "Normal";
            case 1:
                return "Soft";
            case 2:
                return "Hard";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getDigitalZoomDescription()
    {
        Integer value = _directory.getInteger(CasioType1MakernoteDirectory.TAG_CASIO_DIGITAL_ZOOM);

        if (value == null)
            return null;

        switch (value) {
            case 0x10000:
                return "No digital zoom";
            case 0x10001:
                return "2x digital zoom";
            case 0x20000:
                return "2x digital zoom";
            case 0x40000:
                return "4x digital zoom";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        Integer value = _directory.getInteger(CasioType1MakernoteDirectory.TAG_CASIO_WHITE_BALANCE);

        if (value == null)
            return null;

        switch (value) {
            case 1:
                return "Auto";
            case 2:
                return "Tungsten";
            case 3:
                return "Daylight";
            case 4:
                return "Florescent";
            case 5:
                return "Shade";
            case 129:
                return "Manual";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getObjectDistanceDescription()
    {
        Integer value = _directory.getInteger(CasioType1MakernoteDirectory.TAG_CASIO_OBJECT_DISTANCE);

        if (value == null)
            return null;

        return value + " mm";
    }

    @Nullable
    public String getFlashIntensityDescription()
    {
        Integer value = _directory.getInteger(CasioType1MakernoteDirectory.TAG_CASIO_FLASH_INTENSITY);

        if (value == null)
            return null;

        switch (value) {
            case 11:
                return "Weak";
            case 13:
                return "Normal";
            case 15:
                return "Strong";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFlashModeDescription()
    {
        Integer value = _directory.getInteger(CasioType1MakernoteDirectory.TAG_CASIO_FLASH_MODE);

        if (value == null)
            return null;

        switch (value) {
            case 1:
                return "Auto";
            case 2:
                return "On";
            case 3:
                return "Off";
            case 4:
                // this documented as additional value for off here:
                // http://www.ozhiker.com/electronics/pjmt/jpeg_info/casio_mn.html
                return "Red eye reduction";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFocusingModeDescription()
    {
        Integer value = _directory.getInteger(CasioType1MakernoteDirectory.TAG_CASIO_FOCUSING_MODE);

        if (value == null)
            return null;

        switch (value) {
            case 2:
                return "Macro";
            case 3:
                return "Auto focus";
            case 4:
                return "Manual focus";
            case 5:
                return "Infinity";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getQualityDescription()
    {
        Integer value = _directory.getInteger(CasioType1MakernoteDirectory.TAG_CASIO_QUALITY);

        if (value == null)
            return null;

        switch (value) {
            case 1:
                return "Economy";
            case 2:
                return "Normal";
            case 3:
                return "Fine";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getRecordingModeDescription()
    {
        Integer value = _directory.getInteger(CasioType1MakernoteDirectory.TAG_CASIO_RECORDING_MODE);

        if (value == null)
            return null;

        switch (value) {
            case 1:
                return "Single shutter";
            case 2:
                return "Panorama";
            case 3:
                return "Night scene";
            case 4:
                return "Portrait";
            case 5:
                return "Landscape";
            default:
                return "Unknown (" + value + ")";
        }
    }
}

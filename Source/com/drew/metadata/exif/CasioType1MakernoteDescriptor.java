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
 * Created by dnoakes on 27-Nov-2002 10:12:05 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string represenations of tag values stored in a <code>CasioType1MakernoteDirectory</code>.
 */
public class CasioType1MakernoteDescriptor extends TagDescriptor
{
    public CasioType1MakernoteDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
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
                return _directory.getString(tagType);
        }
    }

    public String getCcdSensitivityDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType1MakernoteDirectory.TAG_CASIO_CCD_SENSITIVITY)) return null;
        int value = _directory.getInt(CasioType1MakernoteDirectory.TAG_CASIO_CCD_SENSITIVITY);
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

    public String getSaturationDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType1MakernoteDirectory.TAG_CASIO_SATURATION)) return null;
        int value = _directory.getInt(CasioType1MakernoteDirectory.TAG_CASIO_SATURATION);
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

    public String getContrastDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType1MakernoteDirectory.TAG_CASIO_CONTRAST)) return null;
        int value = _directory.getInt(CasioType1MakernoteDirectory.TAG_CASIO_CONTRAST);
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

    public String getSharpnessDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType1MakernoteDirectory.TAG_CASIO_SHARPNESS)) return null;
        int value = _directory.getInt(CasioType1MakernoteDirectory.TAG_CASIO_SHARPNESS);
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

    public String getDigitalZoomDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType1MakernoteDirectory.TAG_CASIO_DIGITAL_ZOOM)) return null;
        int value = _directory.getInt(CasioType1MakernoteDirectory.TAG_CASIO_DIGITAL_ZOOM);
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

    public String getWhiteBalanceDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType1MakernoteDirectory.TAG_CASIO_WHITE_BALANCE)) return null;
        int value = _directory.getInt(CasioType1MakernoteDirectory.TAG_CASIO_WHITE_BALANCE);
        switch (value) {
            case 1:
                return "Auto";
            case 2:
                return "Tungsten";
            case 3:
                return "Daylight";
            case 4:
                return "Flourescent";
            case 5:
                return "Shade";
            case 129:
                return "Manual";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getObjectDistanceDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType1MakernoteDirectory.TAG_CASIO_OBJECT_DISTANCE)) return null;
        int value = _directory.getInt(CasioType1MakernoteDirectory.TAG_CASIO_OBJECT_DISTANCE);
        return value + " mm";
    }

    public String getFlashIntensityDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType1MakernoteDirectory.TAG_CASIO_FLASH_INTENSITY)) return null;
        int value = _directory.getInt(CasioType1MakernoteDirectory.TAG_CASIO_FLASH_INTENSITY);
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

    public String getFlashModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType1MakernoteDirectory.TAG_CASIO_FLASH_MODE)) return null;
        int value = _directory.getInt(CasioType1MakernoteDirectory.TAG_CASIO_FLASH_MODE);
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

    public String getFocusingModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType1MakernoteDirectory.TAG_CASIO_FOCUSING_MODE)) return null;
        int value = _directory.getInt(CasioType1MakernoteDirectory.TAG_CASIO_FOCUSING_MODE);
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

    public String getQualityDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType1MakernoteDirectory.TAG_CASIO_QUALITY)) return null;
        int value = _directory.getInt(CasioType1MakernoteDirectory.TAG_CASIO_QUALITY);
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

    public String getRecordingModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType1MakernoteDirectory.TAG_CASIO_RECORDING_MODE)) return null;
        int value = _directory.getInt(CasioType1MakernoteDirectory.TAG_CASIO_RECORDING_MODE);
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

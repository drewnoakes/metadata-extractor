/*
 * Created by dnoakes on 27-Nov-2002 10:12:05 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 *
 */
public class CasioMakernoteDescriptor extends TagDescriptor
{
    public CasioMakernoteDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType) {
            case CasioMakernoteDirectory.TAG_CASIO_RECORDING_MODE:
                return getRecordingModeDescription();
            case CasioMakernoteDirectory.TAG_CASIO_QUALITY:
                return getQualityDescription();
            case CasioMakernoteDirectory.TAG_CASIO_FOCUSING_MODE:
                return getFocusingModeDescription();
            case CasioMakernoteDirectory.TAG_CASIO_FLASH_MODE:
                return getFlashModeDescription();
            case CasioMakernoteDirectory.TAG_CASIO_FLASH_INTENSITY:
                return getFlashIntensityDescription();
            case CasioMakernoteDirectory.TAG_CASIO_OBJECT_DISTANCE:
                return getObjectDistanceDescription();
            case CasioMakernoteDirectory.TAG_CASIO_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case CasioMakernoteDirectory.TAG_CASIO_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case CasioMakernoteDirectory.TAG_CASIO_SHARPNESS:
                return getSharpnessDescription();
            case CasioMakernoteDirectory.TAG_CASIO_CONTRAST:
                return getContrastDescription();
            case CasioMakernoteDirectory.TAG_CASIO_SATURATION:
                return getSaturationDescription();
            case CasioMakernoteDirectory.TAG_CASIO_CCD_SENSITIVITY:
                return getCcdSensitivityDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    private String getCcdSensitivityDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioMakernoteDirectory.TAG_CASIO_CCD_SENSITIVITY)) return null;
        int value = _directory.getInt(CasioMakernoteDirectory.TAG_CASIO_CCD_SENSITIVITY);
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
                return "Normal";
            case 100:
                return "High";
            default:
                return "Unknown (" + value + ")";
        }
    }

    private String getSaturationDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioMakernoteDirectory.TAG_CASIO_SATURATION)) return null;
        int value = _directory.getInt(CasioMakernoteDirectory.TAG_CASIO_SATURATION);
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

    private String getContrastDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioMakernoteDirectory.TAG_CASIO_CONTRAST)) return null;
        int value = _directory.getInt(CasioMakernoteDirectory.TAG_CASIO_CONTRAST);
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

    private String getSharpnessDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioMakernoteDirectory.TAG_CASIO_SHARPNESS)) return null;
        int value = _directory.getInt(CasioMakernoteDirectory.TAG_CASIO_SHARPNESS);
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

    private String getDigitalZoomDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioMakernoteDirectory.TAG_CASIO_DIGITAL_ZOOM)) return null;
        int value = _directory.getInt(CasioMakernoteDirectory.TAG_CASIO_DIGITAL_ZOOM);
        switch (value) {
            case 65536:
                return "No digital zoom";
            case 65537:
                return "2x digital zoom";
            default:
                return "Unknown (" + value + ")";
        }
    }

    private String getWhiteBalanceDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioMakernoteDirectory.TAG_CASIO_WHITE_BALANCE)) return null;
        int value = _directory.getInt(CasioMakernoteDirectory.TAG_CASIO_WHITE_BALANCE);
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

    private String getObjectDistanceDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioMakernoteDirectory.TAG_CASIO_OBJECT_DISTANCE)) return null;
        int value = _directory.getInt(CasioMakernoteDirectory.TAG_CASIO_OBJECT_DISTANCE);
        return value + "mm";
    }

    private String getFlashIntensityDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioMakernoteDirectory.TAG_CASIO_FLASH_INTENSITY)) return null;
        int value = _directory.getInt(CasioMakernoteDirectory.TAG_CASIO_FLASH_INTENSITY);
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

    private String getFlashModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioMakernoteDirectory.TAG_CASIO_FLASH_MODE)) return null;
        int value = _directory.getInt(CasioMakernoteDirectory.TAG_CASIO_FLASH_MODE);
        switch (value) {
            case 1:
                return "Auto";
            case 2:
                return "On";
            case 3:
                return "Off";
            case 4:
                return "Red eye reduction";
            default:
                return "Unknown (" + value + ")";
        }
    }

    private String getFocusingModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioMakernoteDirectory.TAG_CASIO_FOCUSING_MODE)) return null;
        int value = _directory.getInt(CasioMakernoteDirectory.TAG_CASIO_FOCUSING_MODE);
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

    private String getQualityDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioMakernoteDirectory.TAG_CASIO_QUALITY)) return null;
        int value = _directory.getInt(CasioMakernoteDirectory.TAG_CASIO_QUALITY);
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

    private String getFocussingModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioMakernoteDirectory.TAG_CASIO_FOCUSING_MODE)) return null;
        int value = _directory.getInt(CasioMakernoteDirectory.TAG_CASIO_FOCUSING_MODE);
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

    private String getRecordingModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioMakernoteDirectory.TAG_CASIO_RECORDING_MODE)) return null;
        int value = _directory.getInt(CasioMakernoteDirectory.TAG_CASIO_RECORDING_MODE);
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

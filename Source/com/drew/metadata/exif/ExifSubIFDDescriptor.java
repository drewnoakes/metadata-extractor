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

import com.drew.imaging.PhotographicConversions;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides human-readable string representations of tag values stored in a <code>ExifSubIFDDirectory</code>.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class ExifSubIFDDescriptor extends TagDescriptor<ExifSubIFDDirectory>
{
    /**
     * Dictates whether rational values will be represented in decimal format in instances
     * where decimal notation is elegant (such as 1/2 -> 0.5, but not 1/3).
     */
    private final boolean _allowDecimalRepresentationOfRationals = true;

    @NotNull
    private static final java.text.DecimalFormat SimpleDecimalFormatter = new DecimalFormat("0.#");

    public ExifSubIFDDescriptor(@NotNull ExifSubIFDDirectory directory)
    {
        super(directory);
    }

    // Note for the potential addition of brightness presentation in eV:
    // Brightness of taken subject. To calculate Exposure(Ev) from BrightnessValue(Bv),
    // you must add SensitivityValue(Sv).
    // Ev=BV+Sv   Sv=log2(ISOSpeedRating/3.125)
    // ISO100:Sv=5, ISO200:Sv=6, ISO400:Sv=7, ISO125:Sv=5.32.

    /**
     * Returns a descriptive value of the the specified tag for this image.
     * Where possible, known values will be substituted here in place of the raw
     * tokens actually kept in the Exif segment.  If no substitution is
     * available, the value provided by getString(int) will be returned.
     * @param tagType the tag to find a description for
     * @return a description of the image's value for the specified tag, or
     *         <code>null</code> if the tag hasn't been defined.
     */
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case ExifSubIFDDirectory.TAG_NEW_SUBFILE_TYPE:
                return getNewSubfileTypeDescription();
            case ExifSubIFDDirectory.TAG_SUBFILE_TYPE:
                return getSubfileTypeDescription();
            case ExifSubIFDDirectory.TAG_THRESHOLDING:
                return getThresholdingDescription();
            case ExifSubIFDDirectory.TAG_FILL_ORDER:
                return getFillOrderDescription();
            case ExifSubIFDDirectory.TAG_EXPOSURE_TIME:
                return getExposureTimeDescription();
            case ExifSubIFDDirectory.TAG_SHUTTER_SPEED:
                return getShutterSpeedDescription();
            case ExifSubIFDDirectory.TAG_FNUMBER:
                return getFNumberDescription();
            case ExifSubIFDDirectory.TAG_COMPRESSED_AVERAGE_BITS_PER_PIXEL:
                return getCompressedAverageBitsPerPixelDescription();
            case ExifSubIFDDirectory.TAG_SUBJECT_DISTANCE:
                return getSubjectDistanceDescription();
            case ExifSubIFDDirectory.TAG_METERING_MODE:
                return getMeteringModeDescription();
            case ExifSubIFDDirectory.TAG_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case ExifSubIFDDirectory.TAG_FLASH:
                return getFlashDescription();
            case ExifSubIFDDirectory.TAG_FOCAL_LENGTH:
                return getFocalLengthDescription();
            case ExifSubIFDDirectory.TAG_COLOR_SPACE:
                return getColorSpaceDescription();
            case ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH:
                return getExifImageWidthDescription();
            case ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT:
                return getExifImageHeightDescription();
            case ExifSubIFDDirectory.TAG_FOCAL_PLANE_UNIT:
                return getFocalPlaneResolutionUnitDescription();
            case ExifSubIFDDirectory.TAG_FOCAL_PLANE_X_RES:
                return getFocalPlaneXResolutionDescription();
            case ExifSubIFDDirectory.TAG_FOCAL_PLANE_Y_RES:
                return getFocalPlaneYResolutionDescription();
            case ExifSubIFDDirectory.TAG_BITS_PER_SAMPLE:
                return getBitsPerSampleDescription();
            case ExifSubIFDDirectory.TAG_PHOTOMETRIC_INTERPRETATION:
                return getPhotometricInterpretationDescription();
            case ExifSubIFDDirectory.TAG_ROWS_PER_STRIP:
                return getRowsPerStripDescription();
            case ExifSubIFDDirectory.TAG_STRIP_BYTE_COUNTS:
                return getStripByteCountsDescription();
            case ExifSubIFDDirectory.TAG_SAMPLES_PER_PIXEL:
                return getSamplesPerPixelDescription();
            case ExifSubIFDDirectory.TAG_PLANAR_CONFIGURATION:
                return getPlanarConfigurationDescription();
            case ExifSubIFDDirectory.TAG_YCBCR_SUBSAMPLING:
                return getYCbCrSubsamplingDescription();
            case ExifSubIFDDirectory.TAG_EXPOSURE_PROGRAM:
                return getExposureProgramDescription();
            case ExifSubIFDDirectory.TAG_APERTURE:
                return getApertureValueDescription();
            case ExifSubIFDDirectory.TAG_MAX_APERTURE:
                return getMaxApertureValueDescription();
            case ExifSubIFDDirectory.TAG_SENSING_METHOD:
                return getSensingMethodDescription();
            case ExifSubIFDDirectory.TAG_EXPOSURE_BIAS:
                return getExposureBiasDescription();
            case ExifSubIFDDirectory.TAG_FILE_SOURCE:
                return getFileSourceDescription();
            case ExifSubIFDDirectory.TAG_SCENE_TYPE:
                return getSceneTypeDescription();
            case ExifSubIFDDirectory.TAG_COMPONENTS_CONFIGURATION:
                return getComponentConfigurationDescription();
            case ExifSubIFDDirectory.TAG_EXIF_VERSION:
                return getExifVersionDescription();
            case ExifSubIFDDirectory.TAG_FLASHPIX_VERSION:
                return getFlashPixVersionDescription();
            case ExifSubIFDDirectory.TAG_ISO_EQUIVALENT:
                return getIsoEquivalentDescription();
            case ExifSubIFDDirectory.TAG_USER_COMMENT:
                return getUserCommentDescription();
            case ExifSubIFDDirectory.TAG_CUSTOM_RENDERED:
                return getCustomRenderedDescription();
            case ExifSubIFDDirectory.TAG_EXPOSURE_MODE:
                return getExposureModeDescription();
            case ExifSubIFDDirectory.TAG_WHITE_BALANCE_MODE:
                return getWhiteBalanceModeDescription();
            case ExifSubIFDDirectory.TAG_DIGITAL_ZOOM_RATIO:
                return getDigitalZoomRatioDescription();
            case ExifSubIFDDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH:
                return get35mmFilmEquivFocalLengthDescription();
            case ExifSubIFDDirectory.TAG_SCENE_CAPTURE_TYPE:
                return getSceneCaptureTypeDescription();
            case ExifSubIFDDirectory.TAG_GAIN_CONTROL:
                return getGainControlDescription();
            case ExifSubIFDDirectory.TAG_CONTRAST:
                return getContrastDescription();
            case ExifSubIFDDirectory.TAG_SATURATION:
                return getSaturationDescription();
            case ExifSubIFDDirectory.TAG_SHARPNESS:
                return getSharpnessDescription();
            case ExifSubIFDDirectory.TAG_SUBJECT_DISTANCE_RANGE:
                return getSubjectDistanceRangeDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getNewSubfileTypeDescription()
    {
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_NEW_SUBFILE_TYPE);
        if (value==null)
            return null;
        switch (value) {
            case 1: return "Full-resolution image";
            case 2: return "Reduced-resolution image";
            case 3: return "Single page of multi-page reduced-resolution image";
            case 4: return "Transparency mask";
            case 5: return "Transparency mask of reduced-resolution image";
            case 6: return "Transparency mask of multi-page image";
            case 7: return "Transparency mask of reduced-resolution multi-page image";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSubfileTypeDescription()
    {
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_SUBFILE_TYPE);
        if (value==null)
            return null;
        switch (value) {
            case 1: return "Full-resolution image";
            case 2: return "Reduced-resolution image";
            case 3: return "Single page of multi-page image";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getThresholdingDescription()
    {
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_THRESHOLDING);
        if (value==null)
            return null;
        switch (value) {
            case 1: return "No dithering or halftoning";
            case 2: return "Ordered dither or halftone";
            case 3: return "Randomized dither";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFillOrderDescription()
    {
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_FILL_ORDER);
        if (value==null)
            return null;
        switch (value) {
            case 1: return "Normal";
            case 2: return "Reversed";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSubjectDistanceRangeDescription()
    {
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_SUBJECT_DISTANCE_RANGE);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "Unknown";
            case 1: return "Macro";
            case 2: return "Close view";
            case 3: return "Distant view";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSharpnessDescription()
    {
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_SHARPNESS);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "None";
            case 1: return "Low";
            case 2: return "Hard";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSaturationDescription()
    {
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_SATURATION);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "None";
            case 1: return "Low saturation";
            case 2: return "High saturation";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getContrastDescription()
    {
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_CONTRAST);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "None";
            case 1: return "Soft";
            case 2: return "Hard";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getGainControlDescription()
    {
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_GAIN_CONTROL);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "None";
            case 1: return "Low gain up";
            case 2: return "Low gain down";
            case 3: return "High gain up";
            case 4: return "High gain down";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSceneCaptureTypeDescription()
    {
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_SCENE_CAPTURE_TYPE);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "Standard";
            case 1: return "Landscape";
            case 2: return "Portrait";
            case 3: return "Night scene";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String get35mmFilmEquivFocalLengthDescription()
    {
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH);
        if (value==null)
            return null;
        if (value==0)
            return "Unknown";
        else
            return SimpleDecimalFormatter.format(value) + "mm";
    }

    @Nullable
    public String getDigitalZoomRatioDescription()
    {
        Rational value = _directory.getRational(ExifSubIFDDirectory.TAG_DIGITAL_ZOOM_RATIO);
        if (value==null)
            return null;
        if (value.getNumerator()==0)
            return "Digital zoom not used.";
        return SimpleDecimalFormatter.format(value.doubleValue());
    }

    @Nullable
    public String getWhiteBalanceModeDescription()
    {
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_WHITE_BALANCE_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "Auto white balance";
            case 1: return "Manual white balance";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getExposureModeDescription()
    {
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_EXPOSURE_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "Auto exposure";
            case 1: return "Manual exposure";
            case 2: return "Auto bracket";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getCustomRenderedDescription()
    {
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_CUSTOM_RENDERED);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "Normal process";
            case 1: return "Custom process";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getUserCommentDescription()
    {
        byte[] commentBytes = _directory.getByteArray(ExifSubIFDDirectory.TAG_USER_COMMENT);
        if (commentBytes==null)
            return null;
        if (commentBytes.length == 0)
            return "";

        final Map<String, String> encodingMap = new HashMap<String, String>();
        encodingMap.put("ASCII",    System.getProperty("file.encoding")); // Someone suggested "ISO-8859-1".
        encodingMap.put("UNICODE",  "UTF-16LE");
        encodingMap.put("JIS",      "Shift-JIS"); // We assume this charset for now.  Another suggestion is "JIS".

        try {
            if (commentBytes.length >= 10) {
                String firstTenBytesString = new String(commentBytes, 0, 10);

                // try each encoding name
                for (Map.Entry<String, String> pair : encodingMap.entrySet()) {
                    String encodingName = pair.getKey();
                    String charset = pair.getValue();
                    if (firstTenBytesString.startsWith(encodingName)) {
                        // skip any null or blank characters commonly present after the encoding name, up to a limit of 10 from the start
                        for (int j = encodingName.length(); j < 10; j++) {
                            byte b = commentBytes[j];
                            if (b != '\0' && b != ' ')
                                return new String(commentBytes, j, commentBytes.length - j, charset).trim();
                        }
                        return new String(commentBytes, 10, commentBytes.length - 10, charset).trim();
                    }
                }
            }
            // special handling fell through, return a plain string representation
            return new String(commentBytes, System.getProperty("file.encoding")).trim();
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    @Nullable
    public String getIsoEquivalentDescription()
    {
        // Have seen an exception here from files produced by ACDSEE that stored an int[] here with two values
        Integer isoEquiv = _directory.getInteger(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
        if (isoEquiv==null)
            return null;
        // There used to be a check here that multiplied ISO values < 50 by 200.
        // Issue 36 shows a smart-phone image from a Samsung Galaxy S2 with ISO-40.
        return Integer.toString(isoEquiv);
    }

    @Nullable
    public String getExifVersionDescription()
    {
        int[] ints = _directory.getIntArray(ExifSubIFDDirectory.TAG_EXIF_VERSION);
        if (ints==null)
            return null;
        return ExifSubIFDDescriptor.convertBytesToVersionString(ints, 2);
    }

    @Nullable
    public String getFlashPixVersionDescription()
    {
        int[] ints = _directory.getIntArray(ExifSubIFDDirectory.TAG_FLASHPIX_VERSION);
        if (ints==null)
            return null;
        return ExifSubIFDDescriptor.convertBytesToVersionString(ints, 2);
    }

    @Nullable
    public String getSceneTypeDescription()
    {
        Integer sceneType = _directory.getInteger(ExifSubIFDDirectory.TAG_SCENE_TYPE);
        if (sceneType==null)
            return null;
        return sceneType == 1
                ? "Directly photographed image"
                : "Unknown (" + sceneType + ")";
    }

    @Nullable
    public String getFileSourceDescription()
    {
        Integer fileSource = _directory.getInteger(ExifSubIFDDirectory.TAG_FILE_SOURCE);
        if (fileSource==null)
            return null;
        return fileSource == 3
                ? "Digital Still Camera (DSC)"
                : "Unknown (" + fileSource + ")";
    }

    @Nullable
    public String getExposureBiasDescription()
    {
        Rational value = _directory.getRational(ExifSubIFDDirectory.TAG_EXPOSURE_BIAS);
        if (value==null)
            return null;
        return value.toSimpleString(true) + " EV";
    }

    @Nullable
    public String getMaxApertureValueDescription()
    {
        Double aperture = _directory.getDoubleObject(ExifSubIFDDirectory.TAG_MAX_APERTURE);
        if (aperture==null)
            return null;
        double fStop = PhotographicConversions.apertureToFStop(aperture);
        return "F" + SimpleDecimalFormatter.format(fStop);
    }

    @Nullable
    public String getApertureValueDescription()
    {
        Double aperture = _directory.getDoubleObject(ExifSubIFDDirectory.TAG_APERTURE);
        if (aperture==null)
            return null;
        double fStop = PhotographicConversions.apertureToFStop(aperture);
        return "F" + SimpleDecimalFormatter.format(fStop);
    }

    @Nullable
    public String getExposureProgramDescription()
    {
        // '1' means manual control, '2' program normal, '3' aperture priority,
        // '4' shutter priority, '5' program creative (slow program),
        // '6' program action(high-speed program), '7' portrait mode, '8' landscape mode.
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_EXPOSURE_PROGRAM);
        if (value==null)
            return null;
        switch (value) {
            case 1: return "Manual control";
            case 2: return "Program normal";
            case 3: return "Aperture priority";
            case 4: return "Shutter priority";
            case 5: return "Program creative (slow program)";
            case 6: return "Program action (high-speed program)";
            case 7: return "Portrait mode";
            case 8: return "Landscape mode";
            default:
                return "Unknown program (" + value + ")";
        }
    }

    @Nullable
    public String getYCbCrSubsamplingDescription()
    {
        int[] positions = _directory.getIntArray(ExifSubIFDDirectory.TAG_YCBCR_SUBSAMPLING);
        if (positions==null)
            return null;
        if (positions[0] == 2 && positions[1] == 1) {
            return "YCbCr4:2:2";
        } else if (positions[0] == 2 && positions[1] == 2) {
            return "YCbCr4:2:0";
        } else {
            return "(Unknown)";
        }
    }

    @Nullable
    public String getPlanarConfigurationDescription()
    {
        // When image format is no compression YCbCr, this value shows byte aligns of YCbCr
        // data. If value is '1', Y/Cb/Cr value is chunky format, contiguous for each subsampling
        // pixel. If value is '2', Y/Cb/Cr value is separated and stored to Y plane/Cb plane/Cr
        // plane format.
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_PLANAR_CONFIGURATION);
        if (value==null)
            return null;
        switch (value) {
            case 1: return "Chunky (contiguous for each subsampling pixel)";
            case 2: return "Separate (Y-plane/Cb-plane/Cr-plane format)";
            default:
                return "Unknown configuration";
        }
    }

    @Nullable
    public String getSamplesPerPixelDescription()
    {
        String value = _directory.getString(ExifSubIFDDirectory.TAG_SAMPLES_PER_PIXEL);
        return value==null ? null : value + " samples/pixel";
    }

    @Nullable
    public String getRowsPerStripDescription()
    {
        final String value = _directory.getString(ExifSubIFDDirectory.TAG_ROWS_PER_STRIP);
        return value==null ? null : value + " rows/strip";
    }

    @Nullable
    public String getStripByteCountsDescription()
    {
        final String value = _directory.getString(ExifSubIFDDirectory.TAG_STRIP_BYTE_COUNTS);
        return value==null ? null : value + " bytes";
    }

    @Nullable
    public String getPhotometricInterpretationDescription()
    {
        // Shows the color space of the image data components
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_PHOTOMETRIC_INTERPRETATION);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "WhiteIsZero";
            case 1: return "BlackIsZero";
            case 2: return "RGB";
            case 3: return "RGB Palette";
            case 4: return "Transparency Mask";
            case 5: return "CMYK";
            case 6: return "YCbCr";
            case 8: return "CIELab";
            case 9: return "ICCLab";
            case 10: return "ITULab";
            case 32803: return "Color Filter Array";
            case 32844: return "Pixar LogL";
            case 32845: return "Pixar LogLuv";
            case 32892: return "Linear Raw";
            default:
                return "Unknown colour space";
        }
    }

    @Nullable
    public String getBitsPerSampleDescription()
    {
        String value = _directory.getString(ExifSubIFDDirectory.TAG_BITS_PER_SAMPLE);
        return value==null ? null : value + " bits/component/pixel";
    }

    @Nullable
    public String getFocalPlaneXResolutionDescription()
    {
        Rational rational = _directory.getRational(ExifSubIFDDirectory.TAG_FOCAL_PLANE_X_RES);
        if (rational==null)
            return null;
        final String unit = getFocalPlaneResolutionUnitDescription();
        return rational.getReciprocal().toSimpleString(_allowDecimalRepresentationOfRationals)
            + (unit==null ? "" : " " + unit.toLowerCase());
    }

    @Nullable
    public String getFocalPlaneYResolutionDescription()
    {
        Rational rational = _directory.getRational(ExifSubIFDDirectory.TAG_FOCAL_PLANE_Y_RES);
        if (rational==null)
            return null;
        final String unit = getFocalPlaneResolutionUnitDescription();
        return rational.getReciprocal().toSimpleString(_allowDecimalRepresentationOfRationals)
            + (unit==null ? "" : " " + unit.toLowerCase());
    }

    @Nullable
    public String getFocalPlaneResolutionUnitDescription()
    {
        // Unit of FocalPlaneXResolution/FocalPlaneYResolution. '1' means no-unit,
        // '2' inch, '3' centimeter.
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_FOCAL_PLANE_UNIT);
        if (value==null)
            return null;
        switch (value) {
            case 1: return "(No unit)";
            case 2: return "Inches";
            case 3: return "cm";
            default:
                return "";
        }
    }

    @Nullable
    public String getExifImageWidthDescription()
    {
        final Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH);
        if (value==null)
            return null;
        return value + " pixels";
    }

    @Nullable
    public String getExifImageHeightDescription()
    {
        final Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT);
        if (value==null)
            return null;
        return value + " pixels";
    }

    @Nullable
    public String getColorSpaceDescription()
    {
        final Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_COLOR_SPACE);
        if (value==null)
            return null;
        if (value == 1)
            return "sRGB";
        if (value == 65535)
            return "Undefined";
        return "Unknown";
    }

    @Nullable
    public String getFocalLengthDescription()
    {
        Rational value = _directory.getRational(ExifSubIFDDirectory.TAG_FOCAL_LENGTH);
        if (value==null)
            return null;
        java.text.DecimalFormat formatter = new DecimalFormat("0.0##");
        return formatter.format(value.doubleValue()) + " mm";
    }

    @Nullable
    public String getFlashDescription()
    {
        /*
         * This is a bitmask.
         * 0 = flash fired
         * 1 = return detected
         * 2 = return able to be detected
         * 3 = unknown
         * 4 = auto used
         * 5 = unknown
         * 6 = red eye reduction used
         */

        final Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_FLASH);

        if (value==null)
            return null;

        StringBuilder sb = new StringBuilder();

        if ((value & 0x1)!=0)
            sb.append("Flash fired");
        else
            sb.append("Flash did not fire");

        // check if we're able to detect a return, before we mention it
        if ((value & 0x4)!=0)
        {
            if ((value & 0x2)!=0)
                sb.append(", return detected");
            else
                sb.append(", return not detected");
        }

        if ((value & 0x10)!=0)
            sb.append(", auto");

        if ((value & 0x40)!=0)
            sb.append(", red-eye reduction");

        return sb.toString();
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        // '0' means unknown, '1' daylight, '2' fluorescent, '3' tungsten, '10' flash,
        // '17' standard light A, '18' standard light B, '19' standard light C, '20' D55,
        // '21' D65, '22' D75, '255' other.
        final Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_WHITE_BALANCE);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "Unknown";
            case 1: return "Daylight";
            case 2: return "Florescent";
            case 3: return "Tungsten";
            case 10: return "Flash";
            case 17: return "Standard light";
            case 18: return "Standard light (B)";
            case 19: return "Standard light (C)";
            case 20: return "D55";
            case 21: return "D65";
            case 22: return "D75";
            case 255: return "(Other)";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getMeteringModeDescription()
    {
        // '0' means unknown, '1' average, '2' center weighted average, '3' spot
        // '4' multi-spot, '5' multi-segment, '6' partial, '255' other
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_METERING_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "Unknown";
            case 1: return "Average";
            case 2: return "Center weighted average";
            case 3: return "Spot";
            case 4: return "Multi-spot";
            case 5: return "Multi-segment";
            case 6: return "Partial";
            case 255: return "(Other)";
            default:
                return "";
        }
    }

    @Nullable
    public String getSubjectDistanceDescription()
    {
        Rational value = _directory.getRational(ExifSubIFDDirectory.TAG_SUBJECT_DISTANCE);
        if (value==null)
            return null;
        java.text.DecimalFormat formatter = new DecimalFormat("0.0##");
        return formatter.format(value.doubleValue()) + " metres";
    }

    @Nullable
    public String getCompressedAverageBitsPerPixelDescription()
    {
        Rational value = _directory.getRational(ExifSubIFDDirectory.TAG_COMPRESSED_AVERAGE_BITS_PER_PIXEL);
        if (value==null)
            return null;
        String ratio = value.toSimpleString(_allowDecimalRepresentationOfRationals);
        if (value.isInteger() && value.intValue() == 1) {
            return ratio + " bit/pixel";
        } else {
            return ratio + " bits/pixel";
        }
    }

    @Nullable
    public String getExposureTimeDescription()
    {
        String value = _directory.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
        return value==null ? null : value + " sec";
    }

    @Nullable
    public String getShutterSpeedDescription()
    {
        // I believe this method to now be stable, but am leaving some alternative snippets of
        // code in here, to assist anyone who's looking into this (given that I don't have a public CVS).

//        float apexValue = _directory.getFloat(ExifSubIFDDirectory.TAG_SHUTTER_SPEED);
//        int apexPower = (int)Math.pow(2.0, apexValue);
//        return "1/" + apexPower + " sec";
        // TODO test this method
        // thanks to Mark Edwards for spotting and patching a bug in the calculation of this
        // description (spotted bug using a Canon EOS 300D)
        // thanks also to Gli Blr for spotting this bug
        Float apexValue = _directory.getFloatObject(ExifSubIFDDirectory.TAG_SHUTTER_SPEED);
        if (apexValue==null)
            return null;
        if (apexValue<=1) {
            float apexPower = (float)(1/(Math.exp(apexValue*Math.log(2))));
            long apexPower10 = Math.round((double)apexPower * 10.0);
            float fApexPower = (float) apexPower10 / 10.0f;
            return fApexPower + " sec";
        } else {
            int apexPower = (int)((Math.exp(apexValue*Math.log(2))));
            return "1/" + apexPower + " sec";
        }

/*
        // This alternative implementation offered by Bill Richards
        // TODO determine which is the correct / more-correct implementation
        double apexValue = _directory.getDouble(ExifSubIFDDirectory.TAG_SHUTTER_SPEED);
        double apexPower = Math.pow(2.0, apexValue);

        StringBuffer sb = new StringBuffer();
        if (apexPower > 1)
            apexPower = Math.floor(apexPower);

        if (apexPower < 1) {
            sb.append((int)Math.round(1/apexPower));
        } else {
            sb.append("1/");
            sb.append((int)apexPower);
        }
        sb.append(" sec");
        return sb.toString();
*/

    }

    @Nullable
    public String getFNumberDescription()
    {
        Rational value = _directory.getRational(ExifSubIFDDirectory.TAG_FNUMBER);
        if (value==null)
            return null;
        return "F" + SimpleDecimalFormatter.format(value.doubleValue());
    }

    @Nullable
    public String getSensingMethodDescription()
    {
        // '1' Not defined, '2' One-chip color area sensor, '3' Two-chip color area sensor
        // '4' Three-chip color area sensor, '5' Color sequential area sensor
        // '7' Trilinear sensor '8' Color sequential linear sensor,  'Other' reserved
        Integer value = _directory.getInteger(ExifSubIFDDirectory.TAG_SENSING_METHOD);
        if (value==null)
            return null;
        switch (value) {
            case 1: return "(Not defined)";
            case 2: return "One-chip color area sensor";
            case 3: return "Two-chip color area sensor";
            case 4: return "Three-chip color area sensor";
            case 5: return "Color sequential area sensor";
            case 7: return "Trilinear sensor";
            case 8: return "Color sequential linear sensor";
            default:
                return "";
        }
    }

    @Nullable
    public String getComponentConfigurationDescription()
    {
        int[] components = _directory.getIntArray(ExifSubIFDDirectory.TAG_COMPONENTS_CONFIGURATION);
        if (components==null)
            return null;
        String[] componentStrings = {"", "Y", "Cb", "Cr", "R", "G", "B"};
        StringBuilder componentConfig = new StringBuilder();
        for (int i = 0; i < Math.min(4, components.length); i++) {
            int j = components[i];
            if (j > 0 && j < componentStrings.length) {
                componentConfig.append(componentStrings[j]);
            }
        }
        return componentConfig.toString();
    }
}

/*
 * Copyright 2002-2017 Drew Noakes
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
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */

package com.drew.metadata.exif;

import com.drew.imaging.PhotographicConversions;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static com.drew.metadata.exif.ExifDirectoryBase.*;

/**
 * Base class for several Exif format descriptor classes.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public abstract class ExifDescriptorBase<T extends Directory> extends TagDescriptor<T>
{
    /**
     * Dictates whether rational values will be represented in decimal format in instances
     * where decimal notation is elegant (such as 1/2 -> 0.5, but not 1/3).
     */
    private final boolean _allowDecimalRepresentationOfRationals = true;

    // Note for the potential addition of brightness presentation in eV:
    // Brightness of taken subject. To calculate Exposure(Ev) from BrightnessValue(Bv),
    // you must add SensitivityValue(Sv).
    // Ev=BV+Sv   Sv=log2(ISOSpeedRating/3.125)
    // ISO100:Sv=5, ISO200:Sv=6, ISO400:Sv=7, ISO125:Sv=5.32.

    public ExifDescriptorBase(@NotNull T directory)
    {
        super(directory);
    }

    @Nullable
    @Override
    public String getDescription(int tagType)
    {
        // TODO order case blocks and corresponding methods in the same order as the TAG_* values are defined

        switch (tagType) {
            case TAG_INTEROP_INDEX:
                return getInteropIndexDescription();
            case TAG_INTEROP_VERSION:
                return getInteropVersionDescription();
            case TAG_ORIENTATION:
                return getOrientationDescription();
            case TAG_RESOLUTION_UNIT:
                return getResolutionDescription();
            case TAG_YCBCR_POSITIONING:
                return getYCbCrPositioningDescription();
            case TAG_X_RESOLUTION:
                return getXResolutionDescription();
            case TAG_Y_RESOLUTION:
                return getYResolutionDescription();
            case TAG_IMAGE_WIDTH:
                return getImageWidthDescription();
            case TAG_IMAGE_HEIGHT:
                return getImageHeightDescription();
            case TAG_BITS_PER_SAMPLE:
                return getBitsPerSampleDescription();
            case TAG_PHOTOMETRIC_INTERPRETATION:
                return getPhotometricInterpretationDescription();
            case TAG_ROWS_PER_STRIP:
                return getRowsPerStripDescription();
            case TAG_STRIP_BYTE_COUNTS:
                return getStripByteCountsDescription();
            case TAG_SAMPLES_PER_PIXEL:
                return getSamplesPerPixelDescription();
            case TAG_PLANAR_CONFIGURATION:
                return getPlanarConfigurationDescription();
            case TAG_YCBCR_SUBSAMPLING:
                return getYCbCrSubsamplingDescription();
            case TAG_REFERENCE_BLACK_WHITE:
                return getReferenceBlackWhiteDescription();
            case TAG_WIN_AUTHOR:
                return getWindowsAuthorDescription();
            case TAG_WIN_COMMENT:
                return getWindowsCommentDescription();
            case TAG_WIN_KEYWORDS:
                return getWindowsKeywordsDescription();
            case TAG_WIN_SUBJECT:
                return getWindowsSubjectDescription();
            case TAG_WIN_TITLE:
                return getWindowsTitleDescription();
            case TAG_NEW_SUBFILE_TYPE:
                return getNewSubfileTypeDescription();
            case TAG_SUBFILE_TYPE:
                return getSubfileTypeDescription();
            case TAG_THRESHOLDING:
                return getThresholdingDescription();
            case TAG_FILL_ORDER:
                return getFillOrderDescription();
            case TAG_CFA_PATTERN_2:
                return getCfaPattern2Description();
            case TAG_EXPOSURE_TIME:
                return getExposureTimeDescription();
            case TAG_SHUTTER_SPEED:
                return getShutterSpeedDescription();
            case TAG_FNUMBER:
                return getFNumberDescription();
            case TAG_COMPRESSED_AVERAGE_BITS_PER_PIXEL:
                return getCompressedAverageBitsPerPixelDescription();
            case TAG_SUBJECT_DISTANCE:
                return getSubjectDistanceDescription();
            case TAG_METERING_MODE:
                return getMeteringModeDescription();
            case TAG_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case TAG_FLASH:
                return getFlashDescription();
            case TAG_FOCAL_LENGTH:
                return getFocalLengthDescription();
            case TAG_COLOR_SPACE:
                return getColorSpaceDescription();
            case TAG_EXIF_IMAGE_WIDTH:
                return getExifImageWidthDescription();
            case TAG_EXIF_IMAGE_HEIGHT:
                return getExifImageHeightDescription();
            case TAG_FOCAL_PLANE_RESOLUTION_UNIT:
                return getFocalPlaneResolutionUnitDescription();
            case TAG_FOCAL_PLANE_X_RESOLUTION:
                return getFocalPlaneXResolutionDescription();
            case TAG_FOCAL_PLANE_Y_RESOLUTION:
                return getFocalPlaneYResolutionDescription();
            case TAG_EXPOSURE_PROGRAM:
                return getExposureProgramDescription();
            case TAG_APERTURE:
                return getApertureValueDescription();
            case TAG_MAX_APERTURE:
                return getMaxApertureValueDescription();
            case TAG_SENSING_METHOD:
                return getSensingMethodDescription();
            case TAG_EXPOSURE_BIAS:
                return getExposureBiasDescription();
            case TAG_FILE_SOURCE:
                return getFileSourceDescription();
            case TAG_SCENE_TYPE:
                return getSceneTypeDescription();
            case TAG_CFA_PATTERN:
                return getCfaPatternDescription();
            case TAG_COMPONENTS_CONFIGURATION:
                return getComponentConfigurationDescription();
            case TAG_EXIF_VERSION:
                return getExifVersionDescription();
            case TAG_FLASHPIX_VERSION:
                return getFlashPixVersionDescription();
            case TAG_ISO_EQUIVALENT:
                return getIsoEquivalentDescription();
            case TAG_USER_COMMENT:
                return getUserCommentDescription();
            case TAG_CUSTOM_RENDERED:
                return getCustomRenderedDescription();
            case TAG_EXPOSURE_MODE:
                return getExposureModeDescription();
            case TAG_WHITE_BALANCE_MODE:
                return getWhiteBalanceModeDescription();
            case TAG_DIGITAL_ZOOM_RATIO:
                return getDigitalZoomRatioDescription();
            case TAG_35MM_FILM_EQUIV_FOCAL_LENGTH:
                return get35mmFilmEquivFocalLengthDescription();
            case TAG_SCENE_CAPTURE_TYPE:
                return getSceneCaptureTypeDescription();
            case TAG_GAIN_CONTROL:
                return getGainControlDescription();
            case TAG_CONTRAST:
                return getContrastDescription();
            case TAG_SATURATION:
                return getSaturationDescription();
            case TAG_SHARPNESS:
                return getSharpnessDescription();
            case TAG_SUBJECT_DISTANCE_RANGE:
                return getSubjectDistanceRangeDescription();
            case TAG_SENSITIVITY_TYPE:
                return getSensitivityTypeRangeDescription();
            case TAG_COMPRESSION:
                return getCompressionDescription();
            case TAG_JPEG_PROC:
                return getJpegProcDescription();
            case TAG_LENS_SPECIFICATION:
                return getLensSpecificationDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getInteropVersionDescription()
    {
        return getVersionBytesDescription(TAG_INTEROP_VERSION, 2);
    }

    @Nullable
    public String getInteropIndexDescription()
    {
        String value = _directory.getString(TAG_INTEROP_INDEX);

        if (value == null)
            return null;

        return "R98".equalsIgnoreCase(value.trim())
            ? "Recommended Exif Interoperability Rules (ExifR98)"
            : "Unknown (" + value + ")";
    }

    @Nullable
    public String getReferenceBlackWhiteDescription()
    {
        // For some reason, sometimes this is read as a long[] and
        // getIntArray isn't able to deal with it
        int[] ints = _directory.getIntArray(TAG_REFERENCE_BLACK_WHITE);
        if (ints==null || ints.length < 6)
        {
            Object o = _directory.getObject(TAG_REFERENCE_BLACK_WHITE);
            if (o != null && (o instanceof long[]))
            {
                long[] longs = (long[])o;
                if (longs.length < 6)
                    return null;

                ints = new int[longs.length];
                for (int i = 0; i < longs.length; i++)
                    ints[i] = (int)longs[i];
            }
            else
                return null;
        }

        int blackR = ints[0];
        int whiteR = ints[1];
        int blackG = ints[2];
        int whiteG = ints[3];
        int blackB = ints[4];
        int whiteB = ints[5];
        return String.format("[%d,%d,%d] [%d,%d,%d]", blackR, blackG, blackB, whiteR, whiteG, whiteB);
    }

    @Nullable
    public String getYResolutionDescription()
    {
        Rational value = _directory.getRational(TAG_Y_RESOLUTION);
        if (value==null)
            return null;
        final String unit = getResolutionDescription();
        return String.format("%s dots per %s",
            value.toSimpleString(_allowDecimalRepresentationOfRationals),
            unit == null ? "unit" : unit.toLowerCase());
    }

    @Nullable
    public String getXResolutionDescription()
    {
        Rational value = _directory.getRational(TAG_X_RESOLUTION);
        if (value == null)
            return null;
        final String unit = getResolutionDescription();
        return String.format("%s dots per %s",
            value.toSimpleString(_allowDecimalRepresentationOfRationals),
            unit == null ? "unit" : unit.toLowerCase());
    }

    @Nullable
    public String getYCbCrPositioningDescription()
    {
        return getIndexedDescription(TAG_YCBCR_POSITIONING, 1, "Center of pixel array", "Datum point");
    }

    @Nullable
    public String getOrientationDescription()
    {
        return super.getOrientationDescription(TAG_ORIENTATION);
    }

    @Nullable
    public String getResolutionDescription()
    {
        // '1' means no-unit, '2' means inch, '3' means centimeter. Default value is '2'(inch)
        return getIndexedDescription(TAG_RESOLUTION_UNIT, 1, "(No unit)", "Inch", "cm");
    }

    /** The Windows specific tags uses plain Unicode. */
    @Nullable
    private String getUnicodeDescription(int tag)
    {
        byte[] bytes = _directory.getByteArray(tag);
        if (bytes == null)
            return null;
        try {
            // Decode the unicode string and trim the unicode zero "\0" from the end.
            return new String(bytes, "UTF-16LE").trim();
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    @Nullable
    public String getWindowsAuthorDescription()
    {
        return getUnicodeDescription(TAG_WIN_AUTHOR);
    }

    @Nullable
    public String getWindowsCommentDescription()
    {
        return getUnicodeDescription(TAG_WIN_COMMENT);
    }

    @Nullable
    public String getWindowsKeywordsDescription()
    {
        return getUnicodeDescription(TAG_WIN_KEYWORDS);
    }

    @Nullable
    public String getWindowsTitleDescription()
    {
        return getUnicodeDescription(TAG_WIN_TITLE);
    }

    @Nullable
    public String getWindowsSubjectDescription()
    {
        return getUnicodeDescription(TAG_WIN_SUBJECT);
    }

    @Nullable
    public String getYCbCrSubsamplingDescription()
    {
        int[] positions = _directory.getIntArray(TAG_YCBCR_SUBSAMPLING);
        if (positions == null || positions.length < 2)
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
        return getIndexedDescription(TAG_PLANAR_CONFIGURATION,
            1,
            "Chunky (contiguous for each subsampling pixel)",
            "Separate (Y-plane/Cb-plane/Cr-plane format)"
        );
    }

    @Nullable
    public String getSamplesPerPixelDescription()
    {
        String value = _directory.getString(TAG_SAMPLES_PER_PIXEL);
        return value == null ? null : value + " samples/pixel";
    }

    @Nullable
    public String getRowsPerStripDescription()
    {
        final String value = _directory.getString(TAG_ROWS_PER_STRIP);
        return value == null ? null : value + " rows/strip";
    }

    @Nullable
    public String getStripByteCountsDescription()
    {
        final String value = _directory.getString(TAG_STRIP_BYTE_COUNTS);
        return value == null ? null : value + " bytes";
    }

    @Nullable
    public String getPhotometricInterpretationDescription()
    {
        // Shows the color space of the image data components
        Integer value = _directory.getInteger(TAG_PHOTOMETRIC_INTERPRETATION);
        if (value == null)
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
        String value = _directory.getString(TAG_BITS_PER_SAMPLE);
        return value == null ? null : value + " bits/component/pixel";
    }

    @Nullable
    public String getImageWidthDescription()
    {
        String value = _directory.getString(TAG_IMAGE_WIDTH);
        return value == null ? null : value + " pixels";
    }

    @Nullable
    public String getImageHeightDescription()
    {
        String value = _directory.getString(TAG_IMAGE_HEIGHT);
        return value == null ? null : value + " pixels";
    }

    @Nullable
    public String getNewSubfileTypeDescription()
    {
        return getIndexedDescription(TAG_NEW_SUBFILE_TYPE, 0,
            "Full-resolution image",
            "Reduced-resolution image",
            "Single page of multi-page image",
            "Single page of multi-page reduced-resolution image",
            "Transparency mask",
            "Transparency mask of reduced-resolution image",
            "Transparency mask of multi-page image",
            "Transparency mask of reduced-resolution multi-page image"
        );
    }

    @Nullable
    public String getSubfileTypeDescription()
    {
        return getIndexedDescription(TAG_SUBFILE_TYPE, 1,
            "Full-resolution image",
            "Reduced-resolution image",
            "Single page of multi-page image"
        );
    }

    @Nullable
    public String getThresholdingDescription()
    {
        return getIndexedDescription(TAG_THRESHOLDING, 1,
            "No dithering or halftoning",
            "Ordered dither or halftone",
            "Randomized dither"
        );
    }

    @Nullable
    public String getFillOrderDescription()
    {
        return getIndexedDescription(TAG_FILL_ORDER, 1,
            "Normal",
            "Reversed"
        );
    }

    @Nullable
    public String getSubjectDistanceRangeDescription()
    {
        return getIndexedDescription(TAG_SUBJECT_DISTANCE_RANGE,
            "Unknown",
            "Macro",
            "Close view",
            "Distant view"
        );
    }

    @Nullable
    public String getSensitivityTypeRangeDescription()
    {
        return getIndexedDescription(TAG_SENSITIVITY_TYPE,
            "Unknown",
            "Standard Output Sensitivity",
            "Recommended Exposure Index",
            "ISO Speed",
            "Standard Output Sensitivity and Recommended Exposure Index",
            "Standard Output Sensitivity and ISO Speed",
            "Recommended Exposure Index and ISO Speed",
            "Standard Output Sensitivity, Recommended Exposure Index and ISO Speed"
        );
    }

    @Nullable
    public String getLensSpecificationDescription()
    {
        return getLensSpecificationDescription(TAG_LENS_SPECIFICATION);
    }

    @Nullable
    public String getSharpnessDescription()
    {
        return getIndexedDescription(TAG_SHARPNESS,
            "None",
            "Low",
            "Hard"
        );
    }

    @Nullable
    public String getSaturationDescription()
    {
        return getIndexedDescription(TAG_SATURATION,
            "None",
            "Low saturation",
            "High saturation"
        );
    }

    @Nullable
    public String getContrastDescription()
    {
        return getIndexedDescription(TAG_CONTRAST,
            "None",
            "Soft",
            "Hard"
        );
    }

    @Nullable
    public String getGainControlDescription()
    {
        return getIndexedDescription(TAG_GAIN_CONTROL,
            "None",
            "Low gain up",
            "Low gain down",
            "High gain up",
            "High gain down"
        );
    }

    @Nullable
    public String getSceneCaptureTypeDescription()
    {
        return getIndexedDescription(TAG_SCENE_CAPTURE_TYPE,
            "Standard",
            "Landscape",
            "Portrait",
            "Night scene"
        );
    }

    @Nullable
    public String get35mmFilmEquivFocalLengthDescription()
    {
        Integer value = _directory.getInteger(TAG_35MM_FILM_EQUIV_FOCAL_LENGTH);
        return value == null
            ? null
            : value == 0
                ? "Unknown"
                : getFocalLengthDescription(value);
    }

    @Nullable
    public String getDigitalZoomRatioDescription()
    {
        Rational value = _directory.getRational(TAG_DIGITAL_ZOOM_RATIO);
        return value == null
            ? null
            : value.getNumerator() == 0
                ? "Digital zoom not used"
                : new DecimalFormat("0.#").format(value.doubleValue());
    }

    @Nullable
    public String getWhiteBalanceModeDescription()
    {
        return getIndexedDescription(TAG_WHITE_BALANCE_MODE,
            "Auto white balance",
            "Manual white balance"
        );
    }

    @Nullable
    public String getExposureModeDescription()
    {
        return getIndexedDescription(TAG_EXPOSURE_MODE,
            "Auto exposure",
            "Manual exposure",
            "Auto bracket"
        );
    }

    @Nullable
    public String getCustomRenderedDescription()
    {
        return getIndexedDescription(TAG_CUSTOM_RENDERED,
            "Normal process",
            "Custom process"
        );
    }

    @Nullable
    public String getUserCommentDescription()
    {
        byte[] commentBytes = _directory.getByteArray(TAG_USER_COMMENT);
        if (commentBytes == null)
            return null;
        if (commentBytes.length == 0)
            return "";

        final Map<String, String> encodingMap = new HashMap<String, String>();
        encodingMap.put("ASCII", System.getProperty("file.encoding")); // Someone suggested "ISO-8859-1".
        encodingMap.put("UNICODE", "UTF-16LE");
        encodingMap.put("JIS", "Shift-JIS"); // We assume this charset for now.  Another suggestion is "JIS".

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
        Integer isoEquiv = _directory.getInteger(TAG_ISO_EQUIVALENT);
        // There used to be a check here that multiplied ISO values < 50 by 200.
        // Issue 36 shows a smart-phone image from a Samsung Galaxy S2 with ISO-40.
        return isoEquiv != null
            ? Integer.toString(isoEquiv)
            : null;
    }

    @Nullable
    public String getExifVersionDescription()
    {
        return getVersionBytesDescription(TAG_EXIF_VERSION, 2);
    }

    @Nullable
    public String getFlashPixVersionDescription()
    {
        return getVersionBytesDescription(TAG_FLASHPIX_VERSION, 2);
    }

    @Nullable
    public String getSceneTypeDescription()
    {
        return getIndexedDescription(TAG_SCENE_TYPE,
            1,
            "Directly photographed image"
        );
    }

    /// <summary>
    /// String description of CFA Pattern
    /// </summary>
    /// <remarks>
    /// Converted from Exiftool version 10.33 created by Phil Harvey
    /// http://www.sno.phy.queensu.ca/~phil/exiftool/
    /// lib\Image\ExifTool\Exif.pm
    ///
    /// Indicates the color filter array (CFA) geometric pattern of the image sensor when a one-chip color area sensor is used.
    /// It does not apply to all sensing methods.
    /// </remarks>
    @Nullable
    public String getCfaPatternDescription()
    {
        return formatCFAPattern(decodeCfaPattern(TAG_CFA_PATTERN));
    }

    /// <summary>
    /// String description of CFA Pattern
    /// </summary>
    /// <remarks>
    /// Indicates the color filter array (CFA) geometric pattern of the image sensor when a one-chip color area sensor is used.
    /// It does not apply to all sensing methods.
    ///
    /// ExifDirectoryBase.TAG_CFA_PATTERN_2 holds only the pixel pattern. ExifDirectoryBase.TAG_CFA_REPEAT_PATTERN_DIM is expected to exist and pass
    /// some conditional tests.
    /// </remarks>
    @Nullable
    public String getCfaPattern2Description()
    {
        byte[] values = _directory.getByteArray(TAG_CFA_PATTERN_2);
        if (values == null)
            return null;

        int[] repeatPattern = _directory.getIntArray(TAG_CFA_REPEAT_PATTERN_DIM);
        if (repeatPattern == null)
            return String.format("Repeat Pattern not found for CFAPattern (%s)", super.getDescription(TAG_CFA_PATTERN_2));

        if (repeatPattern.length == 2 && values.length == (repeatPattern[0] * repeatPattern[1]))
        {
            int[] intpattern = new int[2 + values.length];
            intpattern[0] = repeatPattern[0];
            intpattern[1] = repeatPattern[1];

            for (int i = 0; i < values.length; i++)
                intpattern[i + 2] = values[i] & 0xFF;   // convert the values[i] byte to unsigned

            return formatCFAPattern(intpattern);
        }

        return String.format("Unknown Pattern (%s)", super.getDescription(TAG_CFA_PATTERN_2));
    }

    @Nullable
    private static String formatCFAPattern(@Nullable int[] pattern)
    {
        if (pattern == null)
            return null;
        if (pattern.length < 2)
            return "<truncated data>";
        if (pattern[0] == 0 && pattern[1] == 0)
            return "<zero pattern size>";

        int end = 2 + pattern[0] * pattern[1];
        if (end > pattern.length)
            return "<invalid pattern size>";

        String[] cfaColors = { "Red", "Green", "Blue", "Cyan", "Magenta", "Yellow", "White" };

        StringBuilder ret = new StringBuilder();
        ret.append("[");
        for (int pos = 2; pos < end; pos++)
        {
            if (pattern[pos] <= cfaColors.length - 1)
                ret.append(cfaColors[pattern[pos]]);
            else
                ret.append("Unknown");      // indicated pattern position is outside the array bounds

            if ((pos - 2) % pattern[1] == 0)
                ret.append(",");
            else if(pos != end - 1)
                ret.append("][");
        }
        ret.append("]");

        return ret.toString();
    }

    /// <summary>
    /// Decode raw CFAPattern value
    /// </summary>
    /// <remarks>
    /// Converted from Exiftool version 10.33 created by Phil Harvey
    /// http://www.sno.phy.queensu.ca/~phil/exiftool/
    /// lib\Image\ExifTool\Exif.pm
    ///
    /// The value consists of:
    /// - Two short, being the grid width and height of the repeated pattern.
    /// - Next, for every pixel in that pattern, an identification code.
    /// </remarks>
    @Nullable
    private int[] decodeCfaPattern(int tagType)
    {
        int[] ret;

        byte[] values = _directory.getByteArray(tagType);
        if (values == null)
            return null;

        if (values.length < 4)
        {
            ret = new int[values.length];
            for (int i = 0; i < values.length; i++)
                ret[i] = values[i];
            return ret;
        }

        ret = new int[values.length - 2];

        try {
            ByteArrayReader reader = new ByteArrayReader(values);

            // first two values should be read as 16-bits (2 bytes)
            short item0 = reader.getInt16(0);
            short item1 = reader.getInt16(2);

            Boolean copyArray = false;
            int end = 2 + item0 * item1;
            if (end > values.length) // sanity check in case of byte order problems; calculated 'end' should be <= length of the values
            {
                // try swapping byte order (I have seen this order different than in EXIF)
                reader.setMotorolaByteOrder(!reader.isMotorolaByteOrder());
                item0 = reader.getInt16(0);
                item1 = reader.getInt16(2);

                if (values.length >= (2 + item0 * item1))
                    copyArray = true;
            }
            else
                copyArray = true;

            if(copyArray)
            {
                ret[0] = item0;
                ret[1] = item1;

                for (int i = 4; i < values.length; i++)
                    ret[i - 2] = reader.getInt8(i);
            }
        } catch (IOException ex) {
            _directory.addError("IO exception processing data: " + ex.getMessage());
        }

        return ret;
    }

    @Nullable
    public String getFileSourceDescription()
    {
        return getIndexedDescription(TAG_FILE_SOURCE,
            1,
            "Film Scanner",
            "Reflection Print Scanner",
            "Digital Still Camera (DSC)"
        );
    }

    @Nullable
    public String getExposureBiasDescription()
    {
        Rational value = _directory.getRational(TAG_EXPOSURE_BIAS);
        if (value == null)
            return null;
        return value.toSimpleString(true) + " EV";
    }

    @Nullable
    public String getMaxApertureValueDescription()
    {
        Double aperture = _directory.getDoubleObject(TAG_MAX_APERTURE);
        if (aperture == null)
            return null;
        double fStop = PhotographicConversions.apertureToFStop(aperture);
        return getFStopDescription(fStop);
    }

    @Nullable
    public String getApertureValueDescription()
    {
        Double aperture = _directory.getDoubleObject(TAG_APERTURE);
        if (aperture == null)
            return null;
        double fStop = PhotographicConversions.apertureToFStop(aperture);
        return getFStopDescription(fStop);
    }

    @Nullable
    public String getExposureProgramDescription()
    {
        return getIndexedDescription(TAG_EXPOSURE_PROGRAM,
            1,
            "Manual control",
            "Program normal",
            "Aperture priority",
            "Shutter priority",
            "Program creative (slow program)",
            "Program action (high-speed program)",
            "Portrait mode",
            "Landscape mode"
        );
    }


    @Nullable
    public String getFocalPlaneXResolutionDescription()
    {
        Rational rational = _directory.getRational(TAG_FOCAL_PLANE_X_RESOLUTION);
        if (rational == null)
            return null;
        final String unit = getFocalPlaneResolutionUnitDescription();
        return rational.getReciprocal().toSimpleString(_allowDecimalRepresentationOfRationals)
            + (unit == null ? "" : " " + unit.toLowerCase());
    }

    @Nullable
    public String getFocalPlaneYResolutionDescription()
    {
        Rational rational = _directory.getRational(TAG_FOCAL_PLANE_Y_RESOLUTION);
        if (rational == null)
            return null;
        final String unit = getFocalPlaneResolutionUnitDescription();
        return rational.getReciprocal().toSimpleString(_allowDecimalRepresentationOfRationals)
            + (unit == null ? "" : " " + unit.toLowerCase());
    }

    @Nullable
    public String getFocalPlaneResolutionUnitDescription()
    {
        // Unit of FocalPlaneXResolution/FocalPlaneYResolution.
        // '1' means no-unit, '2' inch, '3' centimeter.
        return getIndexedDescription(TAG_FOCAL_PLANE_RESOLUTION_UNIT,
            1,
            "(No unit)",
            "Inches",
            "cm"
        );
    }

    @Nullable
    public String getExifImageWidthDescription()
    {
        final Integer value = _directory.getInteger(TAG_EXIF_IMAGE_WIDTH);
        return value == null ? null : value + " pixels";
    }

    @Nullable
    public String getExifImageHeightDescription()
    {
        final Integer value = _directory.getInteger(TAG_EXIF_IMAGE_HEIGHT);
        return value == null ? null : value + " pixels";
    }

    @Nullable
    public String getColorSpaceDescription()
    {
        final Integer value = _directory.getInteger(TAG_COLOR_SPACE);
        if (value == null)
            return null;
        if (value == 1)
            return "sRGB";
        if (value == 65535)
            return "Undefined";
        return "Unknown (" + value + ")";
    }

    @Nullable
    public String getFocalLengthDescription()
    {
        Rational value = _directory.getRational(TAG_FOCAL_LENGTH);
        return value == null ? null : getFocalLengthDescription(value.doubleValue());
    }

    @Nullable
    public String getFlashDescription()
    {
        /*
         * This is a bit mask.
         * 0 = flash fired
         * 1 = return detected
         * 2 = return able to be detected
         * 3 = unknown
         * 4 = auto used
         * 5 = unknown
         * 6 = red eye reduction used
         */

        final Integer value = _directory.getInteger(TAG_FLASH);

        if (value == null)
            return null;

        StringBuilder sb = new StringBuilder();

        if ((value & 0x1) != 0)
            sb.append("Flash fired");
        else
            sb.append("Flash did not fire");

        // check if we're able to detect a return, before we mention it
        if ((value & 0x4) != 0) {
            if ((value & 0x2) != 0)
                sb.append(", return detected");
            else
                sb.append(", return not detected");
        }

        if ((value & 0x10) != 0)
            sb.append(", auto");

        if ((value & 0x40) != 0)
            sb.append(", red-eye reduction");

        return sb.toString();
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        // See http://web.archive.org/web/20131018091152/http://exif.org/Exif2-2.PDF page 35
        final Integer value = _directory.getInteger(TAG_WHITE_BALANCE);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Unknown";
            case 1: return "Daylight";
            case 2: return "Florescent";
            case 3: return "Tungsten";
            case 4: return "Flash";
            case 9: return "Fine Weather";
            case 10: return "Cloudy";
            case 11: return "Shade";
            case 12: return "Daylight Fluorescent";
            case 13: return "Day White Fluorescent";
            case 14: return "Cool White Fluorescent";
            case 15: return "White Fluorescent";
            case 16: return "Warm White Fluorescent";
            case 17: return "Standard light";
            case 18: return "Standard light (B)";
            case 19: return "Standard light (C)";
            case 20: return "D55";
            case 21: return "D65";
            case 22: return "D75";
            case 23: return "D50";
            case 24: return "Studio Tungsten";
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
        Integer value = _directory.getInteger(TAG_METERING_MODE);
        if (value == null)
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
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getCompressionDescription()
    {
        Integer value = _directory.getInteger(TAG_COMPRESSION);
        if (value == null)
            return null;
        switch (value) {
            case 1: return "Uncompressed";
            case 2: return "CCITT 1D";
            case 3: return "T4/Group 3 Fax";
            case 4: return "T6/Group 4 Fax";
            case 5: return "LZW";
            case 6: return "JPEG (old-style)";
            case 7: return "JPEG";
            case 8: return "Adobe Deflate";
            case 9: return "JBIG B&W";
            case 10: return "JBIG Color";
            case 99: return "JPEG";
            case 262: return "Kodak 262";
            case 32766: return "Next";
            case 32767: return "Sony ARW Compressed";
            case 32769: return "Packed RAW";
            case 32770: return "Samsung SRW Compressed";
            case 32771: return "CCIRLEW";
            case 32772: return "Samsung SRW Compressed 2";
            case 32773: return "PackBits";
            case 32809: return "Thunderscan";
            case 32867: return "Kodak KDC Compressed";
            case 32895: return "IT8CTPAD";
            case 32896: return "IT8LW";
            case 32897: return "IT8MP";
            case 32898: return "IT8BL";
            case 32908: return "PixarFilm";
            case 32909: return "PixarLog";
            case 32946: return "Deflate";
            case 32947: return "DCS";
            case 34661: return "JBIG";
            case 34676: return "SGILog";
            case 34677: return "SGILog24";
            case 34712: return "JPEG 2000";
            case 34713: return "Nikon NEF Compressed";
            case 34715: return "JBIG2 TIFF FX";
            case 34718: return "Microsoft Document Imaging (MDI) Binary Level Codec";
            case 34719: return "Microsoft Document Imaging (MDI) Progressive Transform Codec";
            case 34720: return "Microsoft Document Imaging (MDI) Vector";
            case 34892: return "Lossy JPEG";
            case 65000: return "Kodak DCR Compressed";
            case 65535: return "Pentax PEF Compressed";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSubjectDistanceDescription()
    {
        Rational value = _directory.getRational(TAG_SUBJECT_DISTANCE);
        if (value == null)
            return null;
        DecimalFormat formatter = new DecimalFormat("0.0##");
        return formatter.format(value.doubleValue()) + " metres";
    }

    @Nullable
    public String getCompressedAverageBitsPerPixelDescription()
    {
        Rational value = _directory.getRational(TAG_COMPRESSED_AVERAGE_BITS_PER_PIXEL);
        if (value == null)
            return null;
        String ratio = value.toSimpleString(_allowDecimalRepresentationOfRationals);
        return value.isInteger() && value.intValue() == 1
            ? ratio + " bit/pixel"
            : ratio + " bits/pixel";
    }

    @Nullable
    public String getExposureTimeDescription()
    {
        String value = _directory.getString(TAG_EXPOSURE_TIME);
        return value == null ? null : value + " sec";
    }

    @Nullable
    public String getShutterSpeedDescription()
    {
        return super.getShutterSpeedDescription(TAG_SHUTTER_SPEED);
    }

    @Nullable
    public String getFNumberDescription()
    {
        Rational value = _directory.getRational(TAG_FNUMBER);
        if (value == null)
            return null;
        return getFStopDescription(value.doubleValue());
    }

    @Nullable
    public String getSensingMethodDescription()
    {
        // '1' Not defined, '2' One-chip color area sensor, '3' Two-chip color area sensor
        // '4' Three-chip color area sensor, '5' Color sequential area sensor
        // '7' Trilinear sensor '8' Color sequential linear sensor,  'Other' reserved
        return getIndexedDescription(TAG_SENSING_METHOD,
            1,
            "(Not defined)",
            "One-chip color area sensor",
            "Two-chip color area sensor",
            "Three-chip color area sensor",
            "Color sequential area sensor",
            null,
            "Trilinear sensor",
            "Color sequential linear sensor"
        );
    }

    @Nullable
    public String getComponentConfigurationDescription()
    {
        int[] components = _directory.getIntArray(TAG_COMPONENTS_CONFIGURATION);
        if (components == null)
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

    @Nullable
    public String getJpegProcDescription()
    {
        Integer value = _directory.getInteger(TAG_JPEG_PROC);
        if (value == null)
            return null;
        switch (value) {
            case 1: return "Baseline";
            case 14: return "Lossless";
            default:
                return "Unknown (" + value + ")";
        }
    }
}

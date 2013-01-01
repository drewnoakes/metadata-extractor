/*
 * Copyright 2002-2013 Drew Noakes
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
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.ExifThumbnailDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link ExifThumbnailDirectory}.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class ExifThumbnailDescriptor extends TagDescriptor<ExifThumbnailDirectory>
{
    /**
     * Dictates whether rational values will be represented in decimal format in instances
     * where decimal notation is elegant (such as 1/2 -> 0.5, but not 1/3).
     */
    private final boolean _allowDecimalRepresentationOfRationals = true;

    public ExifThumbnailDescriptor(@NotNull ExifThumbnailDirectory directory)
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
     *
     * @param tagType the tag to find a description for
     * @return a description of the image's value for the specified tag, or
     *         <code>null</code> if the tag hasn't been defined.
     */
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
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
            case TAG_THUMBNAIL_OFFSET:
                return getThumbnailOffsetDescription();
            case TAG_THUMBNAIL_LENGTH:
                return getThumbnailLengthDescription();
            case TAG_THUMBNAIL_IMAGE_WIDTH:
                return getThumbnailImageWidthDescription();
            case TAG_THUMBNAIL_IMAGE_HEIGHT:
                return getThumbnailImageHeightDescription();
            case TAG_BITS_PER_SAMPLE:
                return getBitsPerSampleDescription();
            case TAG_THUMBNAIL_COMPRESSION:
                return getCompressionDescription();
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
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getReferenceBlackWhiteDescription()
    {
        int[] ints = _directory.getIntArray(TAG_REFERENCE_BLACK_WHITE);
        if (ints == null || ints.length < 6)
            return null;
        int blackR = ints[0];
        int whiteR = ints[1];
        int blackG = ints[2];
        int whiteG = ints[3];
        int blackB = ints[4];
        int whiteB = ints[5];
        return String.format("[%d,%d,%d] [%d,%d,%d]", blackR, blackG, blackB, whiteR, whiteG, whiteB);
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
    public String getCompressionDescription()
    {
        Integer value = _directory.getInteger(TAG_THUMBNAIL_COMPRESSION);
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
            case 32766: return "Next";
            case 32771: return "CCIRLEW";
            case 32773: return "PackBits";
            case 32809: return "Thunderscan";
            case 32895: return "IT8CTPAD";
            case 32896: return "IT8LW";
            case 32897: return "IT8MP";
            case 32898: return "IT8BL";
            case 32908: return "PixarFilm";
            case 32909: return "PixarLog";
            case 32946: return "Deflate";
            case 32947: return "DCS";
            case 32661: return "JBIG";
            case 32676: return "SGILog";
            case 32677: return "SGILog24";
            case 32712: return "JPEG 2000";
            case 32713: return "Nikon NEF Compressed";
            default:
                return "Unknown compression";
        }
    }

    @Nullable
    public String getBitsPerSampleDescription()
    {
        String value = _directory.getString(TAG_BITS_PER_SAMPLE);
        return value == null ? null : value + " bits/component/pixel";
    }

    @Nullable
    public String getThumbnailImageWidthDescription()
    {
        String value = _directory.getString(TAG_THUMBNAIL_IMAGE_WIDTH);
        return value == null ? null : value + " pixels";
    }

    @Nullable
    public String getThumbnailImageHeightDescription()
    {
        String value = _directory.getString(TAG_THUMBNAIL_IMAGE_HEIGHT);
        return value == null ? null : value + " pixels";
    }

    @Nullable
    public String getThumbnailLengthDescription()
    {
        String value = _directory.getString(TAG_THUMBNAIL_LENGTH);
        return value == null ? null : value + " bytes";
    }

    @Nullable
    public String getThumbnailOffsetDescription()
    {
        String value = _directory.getString(TAG_THUMBNAIL_OFFSET);
        return value == null ? null : value + " bytes";
    }

    @Nullable
    public String getYResolutionDescription()
    {
        Rational value = _directory.getRational(TAG_Y_RESOLUTION);
        if (value == null)
            return null;
        final String unit = getResolutionDescription();
        return value.toSimpleString(_allowDecimalRepresentationOfRationals) +
            " dots per " +
            (unit == null ? "unit" : unit.toLowerCase());
    }

    @Nullable
    public String getXResolutionDescription()
    {
        Rational value = _directory.getRational(TAG_X_RESOLUTION);
        if (value == null)
            return null;
        final String unit = getResolutionDescription();
        return value.toSimpleString(_allowDecimalRepresentationOfRationals) +
            " dots per " +
            (unit == null ? "unit" : unit.toLowerCase());
    }

    @Nullable
    public String getYCbCrPositioningDescription()
    {
        return getIndexedDescription(TAG_YCBCR_POSITIONING, 1, "Center of pixel array", "Datum point");
    }

    @Nullable
    public String getOrientationDescription()
    {
        return getIndexedDescription(TAG_ORIENTATION, 1,
            "Top, left side (Horizontal / normal)",
            "Top, right side (Mirror horizontal)",
            "Bottom, right side (Rotate 180)",
            "Bottom, left side (Mirror vertical)",
            "Left side, top (Mirror horizontal and rotate 270 CW)",
            "Right side, top (Rotate 90 CW)",
            "Right side, bottom (Mirror horizontal and rotate 90 CW)",
            "Left side, bottom (Rotate 270 CW)");
    }

    @Nullable
    public String getResolutionDescription()
    {
        // '1' means no-unit, '2' means inch, '3' means centimeter. Default value is '2'(inch)
        return getIndexedDescription(TAG_RESOLUTION_UNIT, 1, "(No unit)", "Inch", "cm");
    }
}

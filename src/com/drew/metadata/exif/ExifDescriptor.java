/*
 * Created by dnoakes on 12-Nov-2002 22:27:15 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

import java.text.DecimalFormat;

/**
 *
 */
public class ExifDescriptor extends TagDescriptor
{
    /**
     * Dictates whether rational values will be represented in decimal format in instances
     * where decimal notation is elegant (such as 1/2 -> 0.5, but not 1/3).
     */
    private boolean _allowDecimalRepresentationOfRationals = true;

    public ExifDescriptor(Directory directory)
    {
        super(directory);
    }

    /**
     * Returns a descriptive value of the the specified tag for this image.
     * Where possible, known values will be substituted here in place of the raw
     * tokens actually kept in the Exif segment.  If no substitution is
     * available, the value provided by getString(int) will be returned.
     * @param tagType the tag to find a description for
     * @return a description of the image's value for the specified tag, or
     *         <code>null</code> if the tag hasn't been defined.
     */
    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType) {
            case ExifDirectory.TAG_ORIENTATION:
                return getOrientationDescription();
            case ExifDirectory.TAG_RESOLUTION_UNIT:
                return getResolutionDescription();
            case ExifDirectory.TAG_YCBCR_POSITIONING:
                return getYCbCrPositioningDescription();
            case ExifDirectory.TAG_EXPOSURE_TIME:
                return getExposureTimeDescription();
            case ExifDirectory.TAG_SHUTTER_SPEED:
                return getShutterSpeedDescription();
            case ExifDirectory.TAG_FNUMBER:
                return getFNumberDescription();
            case ExifDirectory.TAG_X_RESOLUTION:
                return getXResolutionDescription();
            case ExifDirectory.TAG_Y_RESOLUTION:
                return getYResolutionDescription();
            case ExifDirectory.TAG_THUMBNAIL_OFFSET:
                return getThumbnailOffsetDescription();
            case ExifDirectory.TAG_THUMBNAIL_LENGTH:
                return getThumbnailLengthDescription();
            case ExifDirectory.TAG_COMPRESSION_LEVEL:
                return getCompressionLevelDescription();
            case ExifDirectory.TAG_SUBJECT_DISTANCE:
                return getSubjectDistanceDescription();
            case ExifDirectory.TAG_METERING_MODE:
                return getMeteringModeDescription();
            case ExifDirectory.TAG_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case ExifDirectory.TAG_FLASH:
                return getFlashDescription();
            case ExifDirectory.TAG_FOCAL_LENGTH:
                return getFocalLengthDescription();
            case ExifDirectory.TAG_COLOR_SPACE:
                return getColorSpaceDescription();
            case ExifDirectory.TAG_EXIF_IMAGE_WIDTH:
                return getExifImageWidthDescription();
            case ExifDirectory.TAG_EXIF_IMAGE_HEIGHT:
                return getExifImageHeightDescription();
            case ExifDirectory.TAG_FOCAL_PLANE_UNIT:
                return getFocalPlaneResolutionUnitDescription();
            case ExifDirectory.TAG_FOCAL_PLANE_X_RES:
                return getFocalPlaneXResolutionDescription();
            case ExifDirectory.TAG_FOCAL_PLANE_Y_RES:
                return getFocalPlaneYResolutionDescription();
            case ExifDirectory.TAG_THUMBNAIL_IMAGE_WIDTH:
                return getThumbnailImageWidthDescription();
            case ExifDirectory.TAG_THUMBNAIL_IMAGE_HEIGHT:
                return getThumbnailImageHeightDescription();
            case ExifDirectory.TAG_BITS_PER_SAMPLE:
                return getBitsPerSampleDescription();
            case ExifDirectory.TAG_COMPRESSION:
                return getCompressionDescription();
            case ExifDirectory.TAG_PHOTOMETRIC_INTERPRETATION:
                return getPhotometricInterpretationDescription();
            case ExifDirectory.TAG_ROWS_PER_STRIP:
                return getRowsPerStripDescription();
            case ExifDirectory.TAG_STRIP_BYTE_COUNTS:
                return getStripByteCountsDescription();
            case ExifDirectory.TAG_SAMPLES_PER_PIXEL:
                return getSamplesPerPixelDescription();
            case ExifDirectory.TAG_PLANAR_CONFIGURATION:
                return getPlanarConfigurationDescription();
            case ExifDirectory.TAG_YCBCR_SUBSAMPLING:
                return getYCbCrSubsamplingDescription();
            case ExifDirectory.TAG_EXPOSURE_PROGRAM:
                return getExposureProgramDescription();
            case ExifDirectory.TAG_APERTURE:
                return getApertureValueDescription();
            case ExifDirectory.TAG_MAX_APERTURE:
                return getMaxApertureValueDescription();
            case ExifDirectory.TAG_SENSING_METHOD:
                return getSensingMethodDescription();
            case ExifDirectory.TAG_EXPOSURE_BIAS:
                return getExposureBiasDescription();
            case ExifDirectory.TAG_FILE_SOURCE:
                return getFileSourceDescription();
            case ExifDirectory.TAG_SCENE_TYPE:
                return getSceneTypeDescription();
            case ExifDirectory.TAG_COMPONENTS_CONFIGURATION:
                return getComponentConfigurationDescription();
            case ExifDirectory.TAG_EXIF_VERSION:
                return getExifVersionDescription();
            case ExifDirectory.TAG_FLASHPIX_VERSION:
                return getFlashPixVersionDescription();
            case ExifDirectory.TAG_REFERENCE_BLACK_WHITE:
                return getReferenceBlackWhiteDescription();
            case ExifDirectory.TAG_ISO_EQUIVALENT:
                return getIsoEquivalentDescription();
            case ExifDirectory.TAG_THUMBNAIL_DATA:
                return getThumbnailDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    private String getThumbnailDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_THUMBNAIL_DATA)) return null;
        int[] thumbnailBytes = _directory.getIntArray(ExifDirectory.TAG_THUMBNAIL_DATA);
        return "[" + thumbnailBytes.length + " bytes of thumbnail data]";
    }

    private String getIsoEquivalentDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_ISO_EQUIVALENT)) return null;
        int isoEquiv = _directory.getInt(ExifDirectory.TAG_ISO_EQUIVALENT);
        if (isoEquiv < 50) {
            isoEquiv *= 200;
        }
        return Integer.toString(isoEquiv);
    }

    private String getReferenceBlackWhiteDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_REFERENCE_BLACK_WHITE)) return null;
        int[] ints = _directory.getIntArray(ExifDirectory.TAG_REFERENCE_BLACK_WHITE);
        int blackR = ints[0];
        int whiteR = ints[1];
        int blackG = ints[2];
        int whiteG = ints[3];
        int blackB = ints[4];
        int whiteB = ints[5];
        String pos = "[" + blackR + "," + blackG + "," + blackB + "] " +
                "[" + whiteR + "," + whiteG + "," + whiteB + "]";
        return pos;
    }

    private String getExifVersionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_EXIF_VERSION)) return null;
        int[] ints = _directory.getIntArray(ExifDirectory.TAG_EXIF_VERSION);
        return ExifDescriptor.convertBytesToVersionString(ints);
    }

    private String getFlashPixVersionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FLASHPIX_VERSION)) return null;
        int[] ints = _directory.getIntArray(ExifDirectory.TAG_FLASHPIX_VERSION);
        return ExifDescriptor.convertBytesToVersionString(ints);
    }

    private String getSceneTypeDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_SCENE_TYPE)) return null;
        int sceneType = _directory.getInt(ExifDirectory.TAG_SCENE_TYPE);
        if (sceneType == 1) {
            return "Directly photographed image";
        } else {
            return "Unknown (" + sceneType + ")";
        }
    }

    private String getFileSourceDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FILE_SOURCE)) return null;
        int fileSource = _directory.getInt(ExifDirectory.TAG_FILE_SOURCE);
        if (fileSource == 3) {
            return "Digital Still Camera (DSC)";
        } else {
            return "Unknown (" + fileSource + ")";
        }
    }

    private String getExposureBiasDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_EXPOSURE_BIAS)) return null;
        Rational exposureBias = _directory.getRational(ExifDirectory.TAG_EXPOSURE_BIAS);
        return exposureBias.toSimpleString(true);
    }

    private String getMaxApertureValueDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_MAX_APERTURE)) return null;
        double apertureApex = _directory.getDouble(ExifDirectory.TAG_MAX_APERTURE);
        double rootTwo = Math.sqrt(2);
        double fStop = Math.pow(rootTwo, apertureApex);
        java.text.DecimalFormat formatter = new DecimalFormat("0.#");
        return "F" + formatter.format(fStop);
    }

    private String getApertureValueDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_APERTURE)) return null;
        double apertureApex = _directory.getDouble(ExifDirectory.TAG_APERTURE);
        double rootTwo = Math.sqrt(2);
        double fStop = Math.pow(rootTwo, apertureApex);
        java.text.DecimalFormat formatter = new DecimalFormat("0.#");
        return "F" + formatter.format(fStop);
    }

    private String getExposureProgramDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_EXPOSURE_PROGRAM)) return null;
        // '1' means manual control, '2' program normal, '3' aperture priority,
        // '4' shutter priority, '5' program creative (slow program),
        // '6' program action(high-speed program), '7' portrait mode, '8' landscape mode.
        switch (_directory.getInt(ExifDirectory.TAG_EXPOSURE_PROGRAM)) {
            case 1:
                return "Manual control";
            case 2:
                return "Program normal";
            case 3:
                return "Aperture priority";
            case 4:
                return "Shutter priority";
            case 5:
                return "Program creative (slow program)";
            case 6:
                return "Program action (high-speed program)";
            case 7:
                return "Portrait mode";
            case 8:
                return "Landscape mode";
            default:
                return "Unknown program (" + _directory.getInt(ExifDirectory.TAG_EXPOSURE_PROGRAM) + ")";
        }
    }

    private String getYCbCrSubsamplingDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_YCBCR_SUBSAMPLING)) return null;
        int[] positions = _directory.getIntArray(ExifDirectory.TAG_YCBCR_SUBSAMPLING);
        if (positions[0] == 2 && positions[1] == 1) {
            return "YCbCr4:2:2";
        } else if (positions[0] == 2 && positions[1] == 2) {
            return "YCbCr4:2:0";
        } else {
            return "(Unknown)";
        }
    }

    private String getPlanarConfigurationDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_PLANAR_CONFIGURATION)) return null;
        // When image format is no compression YCbCr, this value shows byte aligns of YCbCr
        // data. If value is '1', Y/Cb/Cr value is chunky format, contiguous for each subsampling
        // pixel. If value is '2', Y/Cb/Cr value is separated and stored to Y plane/Cb plane/Cr
        // plane format.

        switch (_directory.getInt(ExifDirectory.TAG_PLANAR_CONFIGURATION)) {
            case 1:
                return "Chunky (contiguous for each subsampling pixel)";
            case 2:
                return "Separate (Y-plane/Cb-plane/Cr-plane format)";
            default:
                return "Unknown configuration";
        }
    }

    private String getSamplesPerPixelDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_SAMPLES_PER_PIXEL)) return null;
        return _directory.getString(ExifDirectory.TAG_SAMPLES_PER_PIXEL) + " samples/pixel";
    }

    private String getRowsPerStripDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_ROWS_PER_STRIP)) return null;
        return _directory.getString(ExifDirectory.TAG_ROWS_PER_STRIP) + " rows/strip";
    }

    private String getStripByteCountsDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_STRIP_BYTE_COUNTS)) return null;
        return _directory.getString(ExifDirectory.TAG_STRIP_BYTE_COUNTS) + " bytes";
    }

    private String getPhotometricInterpretationDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_PHOTOMETRIC_INTERPRETATION)) return null;
        // Shows the color space of the image data components. '1' means monochrome,
        // '2' means RGB, '6' means YCbCr.
        switch (_directory.getInt(ExifDirectory.TAG_PHOTOMETRIC_INTERPRETATION)) {
            case 1:
                return "Monochrome";
            case 2:
                return "RGB";
            case 6:
                return "YCbCr";
            default:
                return "Unknown colour space";
        }
    }

    private String getCompressionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_COMPRESSION)) return null;
        // '1' means no compression, '6' means JPEG compression.
        switch (_directory.getInt(ExifDirectory.TAG_COMPRESSION)) {
            case 1:
                return "No compression";
            case 6:
                return "JPEG compression";
            default:
                return "Unknown compression";
        }
    }

    private String getBitsPerSampleDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_BITS_PER_SAMPLE)) return null;
        return _directory.getString(ExifDirectory.TAG_BITS_PER_SAMPLE) + " bits/component/pixel";
    }

    private String getThumbnailImageWidthDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_THUMBNAIL_IMAGE_WIDTH)) return null;
        return _directory.getString(ExifDirectory.TAG_THUMBNAIL_IMAGE_WIDTH) + " pixels";
    }

    private String getThumbnailImageHeightDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_THUMBNAIL_IMAGE_HEIGHT)) return null;
        return _directory.getString(ExifDirectory.TAG_THUMBNAIL_IMAGE_HEIGHT) + " pixels";
    }

    private String getFocalPlaneXResolutionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FOCAL_PLANE_X_RES)) return null;
        Rational rational = _directory.getRational(ExifDirectory.TAG_FOCAL_PLANE_X_RES);
        return rational.getReciprocal().toSimpleString(_allowDecimalRepresentationOfRationals) + " " +
                getFocalPlaneResolutionUnitDescription().toLowerCase();
    }

    private String getFocalPlaneYResolutionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_COMPRESSION)) return null;
        Rational rational = _directory.getRational(ExifDirectory.TAG_FOCAL_PLANE_Y_RES);
        return rational.getReciprocal().toSimpleString(_allowDecimalRepresentationOfRationals) + " " +
                getFocalPlaneResolutionUnitDescription().toLowerCase();
    }

    private String getFocalPlaneResolutionUnitDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FOCAL_PLANE_UNIT)) return null;
        // Unit of FocalPlaneXResoluton/FocalPlaneYResolution. '1' means no-unit,
        // '2' inch, '3' centimeter.
        switch (_directory.getInt(ExifDirectory.TAG_FOCAL_PLANE_UNIT)) {
            case 1:
                return "(No unit)";
            case 2:
                return "Inches";
            case 3:
                return "cm";
            default:
                return "";
        }
    }

    private String getExifImageWidthDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_EXIF_IMAGE_WIDTH)) return null;
        return _directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_WIDTH) + " pixels";
    }

    private String getExifImageHeightDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT)) return null;
        return _directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT) + " pixels";
    }

    private String getColorSpaceDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_COLOR_SPACE)) return null;
        int colorSpace = _directory.getInt(ExifDirectory.TAG_COLOR_SPACE);
        if (colorSpace == 1) {
            return "sRGB";
        } else if (colorSpace == 65535) {
            return "Undefined";
        } else {
            return "Unknown";
        }
    }

    private String getFocalLengthDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FOCAL_LENGTH)) return null;
        java.text.DecimalFormat formatter = new DecimalFormat("0.0##");
        Rational focalLength = _directory.getRational(ExifDirectory.TAG_FOCAL_LENGTH);
        return formatter.format(focalLength.doubleValue()) + " mm";
    }

    private String getFlashDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FLASH)) return null;
        // '0' means flash did not fire, '1' flash fired, '5' flash fired but strobe return
        // light not detected, '7' flash fired and strobe return light detected.
        switch (_directory.getInt(ExifDirectory.TAG_FLASH)) {
            case 0:
                return "No flash fired";
            case 1:
                return "Flash fired";
            case 5:
                return "Flash fired but strobe return light not detected";
            case 7:
                return "flash fired and strobe return light detected";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_FLASH) + ")";
        }
    }

    private String getWhiteBalanceDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_WHITE_BALANCE)) return null;
        // '0' means unknown, '1' daylight, '2' fluorescent, '3' tungsten, '10' flash,
        // '17' standard light A, '18' standard light B, '19' standard light C, '20' D55,
        // '21' D65, '22' D75, '255' other.
        switch (_directory.getInt(ExifDirectory.TAG_WHITE_BALANCE)) {
            case 0:
                return "Unknown";
            case 1:
                return "Daylight";
            case 2:
                return "Flourescent";
            case 3:
                return "Tungsten";
            case 10:
                return "Flash";
            case 17:
                return "Standard light";
            case 18:
                return "Standard light (B)";
            case 19:
                return "Standard light (C)";
            case 20:
                return "D55";
            case 21:
                return "D65";
            case 22:
                return "D75";
            case 255:
                return "(Other)";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_WHITE_BALANCE) + ")";
        }
    }

    private String getMeteringModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_METERING_MODE)) return null;
        // '0' means unknown, '1' average, '2' center weighted average, '3' spot
        // '4' multi-spot, '5' multi-segment, '6' partial, '255' other
        int meteringMode = _directory.getInt(ExifDirectory.TAG_METERING_MODE);
        switch (meteringMode) {
            case 0:
                return "Unknown";
            case 1:
                return "Average";
            case 2:
                return "Center weighted average";
            case 3:
                return "Spot";
            case 4:
                return "Multi-spot";
            case 5:
                return "Multi-segment";
            case 6:
                return "Partial";
            case 255:
                return "(Other)";
            default:
                return "";
        }
    }

    private String getSubjectDistanceDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_SUBJECT_DISTANCE)) return null;
        Rational distance = _directory.getRational(ExifDirectory.TAG_SUBJECT_DISTANCE);
        java.text.DecimalFormat formatter = new DecimalFormat("0.0##");
        return formatter.format(distance.doubleValue()) + " metres";
    }

    private String getCompressionLevelDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_COMPRESSION_LEVEL)) return null;
        Rational compressionRatio = _directory.getRational(ExifDirectory.TAG_COMPRESSION_LEVEL);
        String ratio = compressionRatio.toSimpleString(_allowDecimalRepresentationOfRationals);
        if (compressionRatio.isInteger() && compressionRatio.intValue() == 1) {
            return ratio + " bit/pixel";
        } else {
            return ratio + " bits/pixel";
        }
    }

    private String getThumbnailLengthDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_THUMBNAIL_LENGTH)) return null;
        return _directory.getString(ExifDirectory.TAG_THUMBNAIL_LENGTH) + " bytes";
    }

    private String getThumbnailOffsetDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_THUMBNAIL_OFFSET)) return null;
        return _directory.getString(ExifDirectory.TAG_THUMBNAIL_OFFSET) + " bytes";
    }

    private String getYResolutionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_Y_RESOLUTION)) return null;
        Rational resolution = _directory.getRational(ExifDirectory.TAG_Y_RESOLUTION);
        return resolution.toSimpleString(_allowDecimalRepresentationOfRationals) + 
                " dots per " +
                getResolutionDescription().toLowerCase();
    }

    private String getXResolutionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_X_RESOLUTION)) return null;
        Rational resolution = _directory.getRational(ExifDirectory.TAG_X_RESOLUTION);
        return resolution.toSimpleString(_allowDecimalRepresentationOfRationals) +
                " dots per " +
                getResolutionDescription().toLowerCase();
    }

    private String getExposureTimeDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_EXPOSURE_TIME)) return null;
        return _directory.getString(ExifDirectory.TAG_EXPOSURE_TIME) + " sec";
    }

    private String getShutterSpeedDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_SHUTTER_SPEED)) return null;
        // Incorrect math bug fixed by Hendrik Wördehoff - 20 Sep 2002
        int apexValue = _directory.getInt(ExifDirectory.TAG_SHUTTER_SPEED);
//        int apexPower = (int)(Math.pow(2.0, apexValue) + 0.5);
        // addition of 0.5 removed upon suggestion of Varuni Witana, who detected incorrect values for Canon cameras,
        // which calculate both shutterspeed and exposuretime
        int apexPower = (int)Math.pow(2.0, apexValue);
        return "1/" + apexPower + " sec";
    }

    private String getFNumberDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FNUMBER)) return null;
        Rational fNumber = _directory.getRational(ExifDirectory.TAG_FNUMBER);
        java.text.DecimalFormat formatter = new DecimalFormat("0.#");
        return "F" + formatter.format(fNumber.doubleValue());
    }

    private String getYCbCrPositioningDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_YCBCR_POSITIONING)) return null;
        int yCbCrPosition = _directory.getInt(ExifDirectory.TAG_YCBCR_POSITIONING);
        switch (yCbCrPosition) {
            case 1:
                return "Center of pixel array";
            case 2:
                return "Datum point";
            default:
                return String.valueOf(yCbCrPosition);
        }
    }

    private String getOrientationDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_ORIENTATION)) return null;
        int orientation = _directory.getInt(ExifDirectory.TAG_ORIENTATION);
        switch (orientation) {
            case 1:
                return "top, left side";
            case 2:
                return "top, right side";
            case 3:
                return "bottom, right side";
            case 4:
                return "bottom, left side";
            case 5:
                return "left side, top";
            case 6:
                return "right side, top";
            case 7:
                return "right side, bottom";
            case 8:
                return "left side, bottom";
            default:
                return String.valueOf(orientation);
        }
    }

    private String getResolutionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_RESOLUTION_UNIT)) return "";
        // '1' means no-unit, '2' means inch, '3' means centimeter. Default value is '2'(inch)
        int resolutionUnit = _directory.getInt(ExifDirectory.TAG_RESOLUTION_UNIT);
        switch (resolutionUnit) {
            case 1:
                return "(No unit)";
            case 2:
                return "Inch";
            case 3:
                return "cm";
            default:
                return "";
        }
    }

    private String getSensingMethodDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_SENSING_METHOD)) return null;
        // '1' Not defined, '2' One-chip color area sensor, '3' Two-chip color area sensor
        // '4' Three-chip color area sensor, '5' Color sequential area sensor
        // '7' Trilinear sensor '8' Color sequential linear sensor,  'Other' reserved
        int sensingMethod = _directory.getInt(ExifDirectory.TAG_SENSING_METHOD);
        switch (sensingMethod) {
            case 1:
                return "(Not defined)";
            case 2:
                return "One-chip color area sensor";
            case 3:
                return "Two-chip color area sensor";
            case 4:
                return "Three-chip color area sensor";
            case 5:
                return "Color sequential area sensor";
            case 7:
                return "Trilinear sensor";
            case 8:
                return "Color sequential linear sensor";
            default:
                return "";
        }
    }

    private String getComponentConfigurationDescription() throws MetadataException
    {
        int[] components = _directory.getIntArray(ExifDirectory.TAG_COMPONENTS_CONFIGURATION);
        String[] componentStrings = {"", "Y", "Cb", "Cr", "R", "G", "B"};
        StringBuffer componentConfig = new StringBuffer();
        for (int i = 0; i < Math.min(4, components.length); i++) {
            int j = components[i];
            if (j > 0 && j < componentStrings.length) {
                componentConfig.append(componentStrings[j]);
            }
        }
        return componentConfig.toString();
    }

    /**
     * Takes a series of 4 bytes from the specified offset, and converts these to a
     * well-known version number, where possible.  For example, (hex) 30 32 31 30 == 2.10).
     * @param components the four version values
     * @return the version as a string of form 2.10
     */
    public static String convertBytesToVersionString(int[] components)
    {
        StringBuffer version = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            if (i == 2) version.append('.');
            String digit = String.valueOf((char)components[i]);
            if (i == 0 && "0".equals(digit)) continue;
            version.append(digit);
        }
        return version.toString();
    }
}

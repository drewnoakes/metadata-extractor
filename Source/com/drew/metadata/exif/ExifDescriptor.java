/*
 * ExifDescriptor.java
 *
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
 * Created by dnoakes on 12-Nov-2002 22:27:15 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.imaging.PhotographicConversions;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

/**
 * Provides human-readable string represenations of tag values stored in a <code>ExifDirectory</code>.
 */
public class ExifDescriptor extends TagDescriptor
{
    /**
     * Dictates whether rational values will be represented in decimal format in instances
     * where decimal notation is elegant (such as 1/2 -> 0.5, but not 1/3).
     */
    private boolean _allowDecimalRepresentationOfRationals = true;

    private static final java.text.DecimalFormat SimpleDecimalFormatter = new DecimalFormat("0.#");

    public ExifDescriptor(Directory directory)
    {
        super(directory);
    }

    // Note for the potential addition of brightness presentation in eV:
    // Brightness of taken subject. To calculate Exposure(Ev) from BrigtnessValue(Bv),
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
    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType) {
            case ExifDirectory.TAG_ORIENTATION:
                return getOrientationDescription();
            case ExifDirectory.TAG_NEW_SUBFILE_TYPE:
                return getNewSubfileTypeDescription();
            case ExifDirectory.TAG_SUBFILE_TYPE:
                return getSubfileTypeDescription();
            case ExifDirectory.TAG_THRESHOLDING:
                return getThresholdingDescription();
            case ExifDirectory.TAG_FILL_ORDER:
                return getFillOrderDescription();
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
            case ExifDirectory.TAG_USER_COMMENT:
                return getUserCommentDescription();
            case ExifDirectory.TAG_CUSTOM_RENDERED:
                return getCustomRenderedDescription();
            case ExifDirectory.TAG_EXPOSURE_MODE:
                return getExposureModeDescription();
            case ExifDirectory.TAG_WHITE_BALANCE_MODE:
                return getWhiteBalanceModeDescription();
            case ExifDirectory.TAG_DIGITAL_ZOOM_RATIO:
                return getDigitalZoomRatioDescription();
            case ExifDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH:
                return get35mmFilmEquivFocalLengthDescription();
            case ExifDirectory.TAG_SCENE_CAPTURE_TYPE:
                return getSceneCaptureTypeDescription();
            case ExifDirectory.TAG_GAIN_CONTROL:
                return getGainControlDescription();
            case ExifDirectory.TAG_CONTRAST:
                return getContrastDescription();
            case ExifDirectory.TAG_SATURATION:
                return getSaturationDescription();
            case ExifDirectory.TAG_SHARPNESS:
                return getSharpnessDescription();
            case ExifDirectory.TAG_SUBJECT_DISTANCE_RANGE:
                return getSubjectDistanceRangeDescription();

            case ExifDirectory.TAG_WIN_AUTHOR:
               return getWindowsAuthorDescription();
            case ExifDirectory.TAG_WIN_COMMENT:
               return getWindowsCommentDescription();
            case ExifDirectory.TAG_WIN_KEYWORDS:
               return getWindowsKeywordsDescription();
            case ExifDirectory.TAG_WIN_SUBJECT:
               return getWindowsSubjectDescription();
            case ExifDirectory.TAG_WIN_TITLE:
               return getWindowsTitleDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    public String getNewSubfileTypeDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_NEW_SUBFILE_TYPE)) return null;
        switch (_directory.getInt(ExifDirectory.TAG_NEW_SUBFILE_TYPE)) {
            case 1: return "Full-resolution image";
            case 2: return "Reduced-resolution image";
            case 3: return "Single page of multi-page reduced-resolution image";
            case 4: return "Transparency mask";
            case 5: return "Transparency mask of reduced-resolution image";
            case 6: return "Transparency mask of multi-page image";
            case 7: return "Transparency mask of reduced-resolution multi-page image";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_NEW_SUBFILE_TYPE) + ")";
        }
    }

    public String getSubfileTypeDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_SUBFILE_TYPE)) return null;
        switch (_directory.getInt(ExifDirectory.TAG_SUBFILE_TYPE)) {
            case 1: return "Full-resolution image";
            case 2: return "Reduced-resolution image";
            case 3: return "Single page of multi-page image";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_SUBFILE_TYPE) + ")";
        }
    }

    public String getThresholdingDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_THRESHOLDING)) return null;
        switch (_directory.getInt(ExifDirectory.TAG_THRESHOLDING)) {
            case 1: return "No dithering or halftoning";
            case 2: return "Ordered dither or halftone";
            case 3: return "Randomized dither";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_THRESHOLDING) + ")";
        }
    }

    public String getFillOrderDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FILL_ORDER)) return null;
        switch (_directory.getInt(ExifDirectory.TAG_FILL_ORDER)) {
            case 1: return "Normal";
            case 2: return "Reversed";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_FILL_ORDER) + ")";
        }
    }

    public String getSubjectDistanceRangeDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_SUBJECT_DISTANCE_RANGE)) return null;
        switch (_directory.getInt(ExifDirectory.TAG_SUBJECT_DISTANCE_RANGE)) {
            case 0:
                return "Unknown";
            case 1:
                return "Macro";
            case 2:
                return "Close view";
            case 3:
                return "Distant view";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_SUBJECT_DISTANCE_RANGE) + ")";
        }
    }

    public String getSharpnessDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_SHARPNESS)) return null;
        switch (_directory.getInt(ExifDirectory.TAG_SHARPNESS)) {
            case 0:
                return "None";
            case 1:
                return "Low";
            case 2:
                return "Hard";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_SHARPNESS) + ")";
        }
    }

    public String getSaturationDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_SATURATION)) return null;
        switch (_directory.getInt(ExifDirectory.TAG_SATURATION)) {
            case 0:
                return "None";
            case 1:
                return "Low saturation";
            case 2:
                return "High saturation";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_SATURATION) + ")";
        }
    }

    public String getContrastDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_CONTRAST)) return null;
        switch (_directory.getInt(ExifDirectory.TAG_CONTRAST)) {
            case 0:
                return "None";
            case 1:
                return "Soft";
            case 2:
                return "Hard";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_CONTRAST) + ")";
        }
    }

    public String getGainControlDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_GAIN_CONTROL)) return null;
        switch (_directory.getInt(ExifDirectory.TAG_GAIN_CONTROL)) {
            case 0:
                return "None";
            case 1:
                return "Low gain up";
            case 2:
                return "Low gain down";
            case 3:
                return "High gain up";
            case 4:
                return "High gain down";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_GAIN_CONTROL) + ")";
        }
    }

    public String getSceneCaptureTypeDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_SCENE_CAPTURE_TYPE)) return null;
        switch (_directory.getInt(ExifDirectory.TAG_SCENE_CAPTURE_TYPE)) {
            case 0:
                return "Standard";
            case 1:
                return "Landscape";
            case 2:
                return "Portrait";
            case 3:
                return "Night scene";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_SCENE_CAPTURE_TYPE) + ")";
        }
    }

    public String get35mmFilmEquivFocalLengthDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH)) return null;
        int equivalentFocalLength = _directory.getInt(ExifDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH);

        if (equivalentFocalLength==0)
            return "Unknown";
        else
            return SimpleDecimalFormatter.format(equivalentFocalLength) + "mm";
    }

    public String getDigitalZoomRatioDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_DIGITAL_ZOOM_RATIO)) return null;
        Rational rational = _directory.getRational(ExifDirectory.TAG_DIGITAL_ZOOM_RATIO);
        if (rational.getNumerator()==0)
            return "Digital zoom not used.";

        return SimpleDecimalFormatter.format(rational.doubleValue());
    }

    public String getWhiteBalanceModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_WHITE_BALANCE_MODE)) return null;
        switch (_directory.getInt(ExifDirectory.TAG_WHITE_BALANCE_MODE)) {
            case 0:
                return "Auto white balance";
            case 1:
                return "Manual white balance";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_WHITE_BALANCE_MODE) + ")";
        }
    }

    public String getExposureModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_EXPOSURE_MODE)) return null;
        switch (_directory.getInt(ExifDirectory.TAG_EXPOSURE_MODE)) {
            case 0:
                return "Auto exposure";
            case 1:
                return "Manual exposure";
            case 2:
                return "Auto bracket";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_EXPOSURE_MODE) + ")";
        }
    }

    public String getCustomRenderedDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_CUSTOM_RENDERED)) return null;
        switch (_directory.getInt(ExifDirectory.TAG_CUSTOM_RENDERED)) {
            case 0:
                return "Normal process";
            case 1:
                return "Custom process";
            default:
                return "Unknown (" + _directory.getInt(ExifDirectory.TAG_CUSTOM_RENDERED) + ")";
        }
    }

    public String getUserCommentDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_USER_COMMENT)) return null;

        byte[] commentBytes = _directory.getByteArray(ExifDirectory.TAG_USER_COMMENT);

        if (commentBytes.length==0)
            return "";

        final String[] encodingNames = new String[] { "ASCII", "UNICODE", "JIS" };

        if (commentBytes.length>=10)
        {
            String encodingRegion = new String(commentBytes, 0, 10);

            // try each encoding name
            for (int i = 0; i<encodingNames.length; i++) {
                String encodingName = encodingNames[i];
                if (encodingRegion.startsWith(encodingName))
                {
                    // remove the null characters (and any spaces) commonly present after the encoding name
                    for (int j = encodingName.length(); j<10; j++) {
                        byte b = commentBytes[j];
                        if (b!='\0' && b!=' ') {
                           if (encodingName.equals("UNICODE")) {
                              try {
                                 return new String(commentBytes, j, commentBytes.length - j, "UTF-16LE").trim();
                              }
                              catch (UnsupportedEncodingException ex) {
                                 return null;
                              }
                           }
                           return new String(commentBytes, j, commentBytes.length - j).trim();
                        }
                    }
                    return new String(commentBytes, 10, commentBytes.length - 10).trim();
                }
            }
        }

        // special handling fell through, return a plain string representation
        return new String(commentBytes).trim();
    }

    public String getThumbnailDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_THUMBNAIL_DATA)) return null;
        int[] thumbnailBytes = _directory.getIntArray(ExifDirectory.TAG_THUMBNAIL_DATA);
        return "[" + thumbnailBytes.length + " bytes of thumbnail data]";
    }

    public String getIsoEquivalentDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_ISO_EQUIVALENT)) return null;
        int isoEquiv = _directory.getInt(ExifDirectory.TAG_ISO_EQUIVALENT);
        if (isoEquiv < 50) {
            isoEquiv *= 200;
        }
        return Integer.toString(isoEquiv);
    }

    public String getReferenceBlackWhiteDescription() throws MetadataException
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

    public String getExifVersionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_EXIF_VERSION)) return null;
        int[] ints = _directory.getIntArray(ExifDirectory.TAG_EXIF_VERSION);
        return ExifDescriptor.convertBytesToVersionString(ints);
    }

    public String getFlashPixVersionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FLASHPIX_VERSION)) return null;
        int[] ints = _directory.getIntArray(ExifDirectory.TAG_FLASHPIX_VERSION);
        return ExifDescriptor.convertBytesToVersionString(ints);
    }

    public String getSceneTypeDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_SCENE_TYPE)) return null;
        int sceneType = _directory.getInt(ExifDirectory.TAG_SCENE_TYPE);
        if (sceneType == 1) {
            return "Directly photographed image";
        } else {
            return "Unknown (" + sceneType + ")";
        }
    }

    public String getFileSourceDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FILE_SOURCE)) return null;
        int fileSource = _directory.getInt(ExifDirectory.TAG_FILE_SOURCE);
        if (fileSource == 3) {
            return "Digital Still Camera (DSC)";
        } else {
            return "Unknown (" + fileSource + ")";
        }
    }

    public String getExposureBiasDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_EXPOSURE_BIAS)) return null;
        Rational exposureBias = _directory.getRational(ExifDirectory.TAG_EXPOSURE_BIAS);
        return exposureBias.toSimpleString(true) + " EV";
    }

    public String getMaxApertureValueDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_MAX_APERTURE)) return null;
        double aperture = _directory.getDouble(ExifDirectory.TAG_MAX_APERTURE);
        double fStop = PhotographicConversions.apertureToFStop(aperture);
        return "F" + SimpleDecimalFormatter.format(fStop);
    }

    public String getApertureValueDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_APERTURE)) return null;
        double aperture = _directory.getDouble(ExifDirectory.TAG_APERTURE);
        double fStop = PhotographicConversions.apertureToFStop(aperture);
        return "F" + SimpleDecimalFormatter.format(fStop);
    }

    public String getExposureProgramDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_EXPOSURE_PROGRAM)) return null;
        // '1' means manual control, '2' program normal, '3' aperture priority,
        // '4' shutter priority, '5' program creative (slow program),
        // '6' program action(high-speed program), '7' portrait mode, '8' landscape mode.
        switch (_directory.getInt(ExifDirectory.TAG_EXPOSURE_PROGRAM)) {
            case 1: return "Manual control";
            case 2: return "Program normal";
            case 3: return "Aperture priority";
            case 4: return "Shutter priority";
            case 5: return "Program creative (slow program)";
            case 6: return "Program action (high-speed program)";
            case 7: return "Portrait mode";
            case 8: return "Landscape mode";
            default:
                return "Unknown program (" + _directory.getInt(ExifDirectory.TAG_EXPOSURE_PROGRAM) + ")";
        }
    }

    public String getYCbCrSubsamplingDescription() throws MetadataException
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

    public String getPlanarConfigurationDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_PLANAR_CONFIGURATION)) return null;
        // When image format is no compression YCbCr, this value shows byte aligns of YCbCr
        // data. If value is '1', Y/Cb/Cr value is chunky format, contiguous for each subsampling
        // pixel. If value is '2', Y/Cb/Cr value is separated and stored to Y plane/Cb plane/Cr
        // plane format.

        switch (_directory.getInt(ExifDirectory.TAG_PLANAR_CONFIGURATION)) {
            case 1: return "Chunky (contiguous for each subsampling pixel)";
            case 2: return "Separate (Y-plane/Cb-plane/Cr-plane format)";
            default:
                return "Unknown configuration";
        }
    }

    public String getSamplesPerPixelDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_SAMPLES_PER_PIXEL)) return null;
        return _directory.getString(ExifDirectory.TAG_SAMPLES_PER_PIXEL) + " samples/pixel";
    }

    public String getRowsPerStripDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_ROWS_PER_STRIP)) return null;
        return _directory.getString(ExifDirectory.TAG_ROWS_PER_STRIP) + " rows/strip";
    }

    public String getStripByteCountsDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_STRIP_BYTE_COUNTS)) return null;
        return _directory.getString(ExifDirectory.TAG_STRIP_BYTE_COUNTS) + " bytes";
    }

    public String getPhotometricInterpretationDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_PHOTOMETRIC_INTERPRETATION)) return null;
        // Shows the color space of the image data components
        switch (_directory.getInt(ExifDirectory.TAG_PHOTOMETRIC_INTERPRETATION)) {
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

    public String getCompressionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_COMPRESSION)) return null;
        switch (_directory.getInt(ExifDirectory.TAG_COMPRESSION)) {
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

    public String getBitsPerSampleDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_BITS_PER_SAMPLE)) return null;
        return _directory.getString(ExifDirectory.TAG_BITS_PER_SAMPLE) + " bits/component/pixel";
    }

    public String getThumbnailImageWidthDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_THUMBNAIL_IMAGE_WIDTH)) return null;
        return _directory.getString(ExifDirectory.TAG_THUMBNAIL_IMAGE_WIDTH) + " pixels";
    }

    public String getThumbnailImageHeightDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_THUMBNAIL_IMAGE_HEIGHT)) return null;
        return _directory.getString(ExifDirectory.TAG_THUMBNAIL_IMAGE_HEIGHT) + " pixels";
    }

    public String getFocalPlaneXResolutionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FOCAL_PLANE_X_RES)) return null;
        Rational rational = _directory.getRational(ExifDirectory.TAG_FOCAL_PLANE_X_RES);
        return rational.getReciprocal().toSimpleString(_allowDecimalRepresentationOfRationals) + " " +
                getFocalPlaneResolutionUnitDescription().toLowerCase();
    }

    public String getFocalPlaneYResolutionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FOCAL_PLANE_Y_RES)) return null;
        Rational rational = _directory.getRational(ExifDirectory.TAG_FOCAL_PLANE_Y_RES);
        return rational.getReciprocal().toSimpleString(_allowDecimalRepresentationOfRationals) + " " +
                getFocalPlaneResolutionUnitDescription().toLowerCase();
    }

    public String getFocalPlaneResolutionUnitDescription() throws MetadataException
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

    public String getExifImageWidthDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_EXIF_IMAGE_WIDTH)) return null;
        return _directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_WIDTH) + " pixels";
    }

    public String getExifImageHeightDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT)) return null;
        return _directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT) + " pixels";
    }

    public String getColorSpaceDescription() throws MetadataException
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

    public String getFocalLengthDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FOCAL_LENGTH)) return null;
        java.text.DecimalFormat formatter = new DecimalFormat("0.0##");
        Rational focalLength = _directory.getRational(ExifDirectory.TAG_FOCAL_LENGTH);
        return formatter.format(focalLength.doubleValue()) + " mm";
    }

    public String getFlashDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FLASH)) return null;

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

        int val = _directory.getInt(ExifDirectory.TAG_FLASH);

        StringBuffer sb = new StringBuffer();

        if ((val & 0x1)!=0)
            sb.append("Flash fired");
        else
            sb.append("Flash did not fire");

        // check if we're able to detect a return, before we mention it
        if ((val & 0x4)!=0)
        {
            if ((val & 0x2)!=0)
                sb.append(", return detected");
            else
                sb.append(", return not detected");
        }

        if ((val & 0x10)!=0)
            sb.append(", auto");

        if ((val & 0x40)!=0)
            sb.append(", red-eye reduction");

        return sb.toString();
    }

    public String getWhiteBalanceDescription() throws MetadataException
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

    public String getMeteringModeDescription() throws MetadataException
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

    public String getSubjectDistanceDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_SUBJECT_DISTANCE)) return null;
        Rational distance = _directory.getRational(ExifDirectory.TAG_SUBJECT_DISTANCE);
        java.text.DecimalFormat formatter = new DecimalFormat("0.0##");
        return formatter.format(distance.doubleValue()) + " metres";
    }

    public String getCompressionLevelDescription() throws MetadataException
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

    public String getThumbnailLengthDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_THUMBNAIL_LENGTH)) return null;
        return _directory.getString(ExifDirectory.TAG_THUMBNAIL_LENGTH) + " bytes";
    }

    public String getThumbnailOffsetDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_THUMBNAIL_OFFSET)) return null;
        return _directory.getString(ExifDirectory.TAG_THUMBNAIL_OFFSET) + " bytes";
    }

    public String getYResolutionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_Y_RESOLUTION)) return null;
        Rational resolution = _directory.getRational(ExifDirectory.TAG_Y_RESOLUTION);
        return resolution.toSimpleString(_allowDecimalRepresentationOfRationals) +
                " dots per " +
                getResolutionDescription().toLowerCase();
    }

    public String getXResolutionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_X_RESOLUTION)) return null;
        Rational resolution = _directory.getRational(ExifDirectory.TAG_X_RESOLUTION);
        return resolution.toSimpleString(_allowDecimalRepresentationOfRationals) +
                " dots per " +
                getResolutionDescription().toLowerCase();
    }

    public String getExposureTimeDescription()
    {
        if (!_directory.containsTag(ExifDirectory.TAG_EXPOSURE_TIME)) return null;
        return _directory.getString(ExifDirectory.TAG_EXPOSURE_TIME) + " sec";
    }

    public String getShutterSpeedDescription() throws MetadataException
    {
        // I believe this method to now be stable, but am leaving some alternative snippets of
        // code in here, to assist anyone who's looking into this (given that I don't have a public CVS).

        if (!_directory.containsTag(ExifDirectory.TAG_SHUTTER_SPEED)) return null;
//        float apexValue = _directory.getFloat(ExifDirectory.TAG_SHUTTER_SPEED);
//        int apexPower = (int)Math.pow(2.0, apexValue);
//        return "1/" + apexPower + " sec";
        // TODO test this method
        // thanks to Mark Edwards for spotting and patching a bug in the calculation of this
        // description (spotted bug using a Canon EOS 300D)
        // thanks also to Gli Blr for spotting this bug
        float apexValue = _directory.getFloat(ExifDirectory.TAG_SHUTTER_SPEED);
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
        double apexValue = _directory.getDouble(ExifDirectory.TAG_SHUTTER_SPEED);
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

    public String getFNumberDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_FNUMBER)) return null;
        Rational fNumber = _directory.getRational(ExifDirectory.TAG_FNUMBER);
        return "F" + SimpleDecimalFormatter.format(fNumber.doubleValue());
    }

    public String getYCbCrPositioningDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_YCBCR_POSITIONING)) return null;
        int yCbCrPosition = _directory.getInt(ExifDirectory.TAG_YCBCR_POSITIONING);
        switch (yCbCrPosition) {
            case 1: return "Center of pixel array";
            case 2: return "Datum point";
            default:
                return String.valueOf(yCbCrPosition);
        }
    }

    public String getOrientationDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_ORIENTATION)) return null;
        int orientation = _directory.getInt(ExifDirectory.TAG_ORIENTATION);
        switch (orientation) {
            case 1: return "Top, left side (Horizontal / normal)";
            case 2: return "Top, right side (Mirror horizontal)";
            case 3: return "Bottom, right side (Rotate 180)";
            case 4: return "Bottom, left side (Mirror vertical)";
            case 5: return "Left side, top (Mirror horizontal and rotate 270 CW)";
            case 6: return "Right side, top (Rotate 90 CW)";
            case 7: return "Right side, bottom (Mirror horizontal and rotate 90 CW)";
            case 8: return "Left side, bottom (Rotate 270 CW)";
            default:
                return String.valueOf(orientation);
        }
    }

    public String getResolutionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifDirectory.TAG_RESOLUTION_UNIT)) return "";
        // '1' means no-unit, '2' means inch, '3' means centimeter. Default value is '2'(inch)
        int resolutionUnit = _directory.getInt(ExifDirectory.TAG_RESOLUTION_UNIT);
        switch (resolutionUnit) {
            case 1: return "(No unit)";
            case 2: return "Inch";
            case 3: return "cm";
            default:
                return "";
        }
    }

    public String getSensingMethodDescription() throws MetadataException
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

    public String getComponentConfigurationDescription() throws MetadataException
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
        for (int i = 0; i < 4 && i < components.length; i++) {
            if (i == 2) version.append('.');
            String digit = String.valueOf((char)components[i]);
            if (i == 0 && "0".equals(digit)) continue;
            version.append(digit);
        }
        return version.toString();
    }

    /**
     * The Windows specific tags uses plain Unicode
     */
    private String getUnicodeDescription(int tag) throws MetadataException
    {
         if (!_directory.containsTag(tag)) return null;
         byte[] commentBytes = _directory.getByteArray(tag);
         try {
             // decode the unicode string
             // trim it, as i'm seeing a junk character on the end
            return new String(commentBytes, "UTF-16LE").trim();
         }
         catch (UnsupportedEncodingException ex) {
            return null;
         }
    }

    public String getWindowsAuthorDescription() throws MetadataException
    {
       return getUnicodeDescription(ExifDirectory.TAG_WIN_AUTHOR);
    }

    public String getWindowsCommentDescription() throws MetadataException
    {
       return getUnicodeDescription(ExifDirectory.TAG_WIN_COMMENT);
    }

    public String getWindowsKeywordsDescription() throws MetadataException
    {
       return getUnicodeDescription(ExifDirectory.TAG_WIN_KEYWORDS);
    }

    public String getWindowsTitleDescription() throws MetadataException
    {
       return getUnicodeDescription(ExifDirectory.TAG_WIN_TITLE);
    }

    public String getWindowsSubjectDescription() throws MetadataException
    {
       return getUnicodeDescription(ExifDirectory.TAG_WIN_SUBJECT);
    }
}

/*
 * ImageInfo.java
 *
 * This class is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  Similarly, I release this Java version under the
 * same license, though I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   drew.noakes@drewnoakes.com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created on 28 April 2002, 17:40
 * Modified 04 Aug 2002
 * - Adjusted javadoc
 * - Added
 */
package com.drew.imaging.exif;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.text.DecimalFormat;

/**
 *
 * @author  Drew Noakes drew.noakes@drewnoakes.com
 */
public final class ImageInfo implements ExifTagValues
{
    /**
     * Static Map for lookup of Exif tag names (such as 'Model' or 'XResolution')
     * by Integer tag codes.
     */
    private static final HashMap tagMap = new HashMap();

    static {
        tagMap.put(new Integer(0x0001), "InteroperabilityIndex");
        tagMap.put(new Integer(0x0002), "InteroperabilityVersion");
        tagMap.put(new Integer(0x00fe), "NewSubfileType");
        tagMap.put(new Integer(0x00ff), "SubfileType");
        tagMap.put(new Integer(0x0100), "ThumbnailImageWidth");
        tagMap.put(new Integer(0x0101), "ThumbnailImageHeight");
        tagMap.put(new Integer(0x0102), "BitsPerSample");
        tagMap.put(new Integer(0x0103), "Compression");
        tagMap.put(new Integer(0x0106), "PhotometricInterpretation");
        tagMap.put(new Integer(0x010A), "FillOrder");
        tagMap.put(new Integer(0x010D), "DocumentName");
        tagMap.put(new Integer(0x010E), "ImageDescription");
        tagMap.put(new Integer(0x010F), "Make");
        tagMap.put(new Integer(0x0110), "Model");
        tagMap.put(new Integer(0x0111), "StripOffsets");
        tagMap.put(new Integer(0x0112), "Orientation");
        tagMap.put(new Integer(0x0115), "SamplesPerPixel");
        tagMap.put(new Integer(0x0116), "RowsPerStrip");
        tagMap.put(new Integer(0x0117), "StripByteCounts");
        tagMap.put(new Integer(0x011A), "XResolution");
        tagMap.put(new Integer(0x011B), "YResolution");
        tagMap.put(new Integer(0x011C), "PlanarConfiguration");
        tagMap.put(new Integer(0x0128), "ResolutionUnit");
        tagMap.put(new Integer(0x012D), "TransferFunction");
        tagMap.put(new Integer(0x0131), "Software");
        tagMap.put(new Integer(0x0132), "DateTime");
        tagMap.put(new Integer(0x013B), "Artist");
        tagMap.put(new Integer(0x013d), "Predictor");
        tagMap.put(new Integer(0x013E), "WhitePoint");
        tagMap.put(new Integer(0x013F), "PrimaryChromaticities");
        tagMap.put(new Integer(0x0142), "TileWidth");
        tagMap.put(new Integer(0x0143), "TileLength");
        tagMap.put(new Integer(0x0144), "TileOffsets");
        tagMap.put(new Integer(0x0145), "TileByteCounts");
        tagMap.put(new Integer(0x014a), "SubIFDs");
        tagMap.put(new Integer(0x015b), "JPEGTables");
        tagMap.put(new Integer(0x0156), "TransferRange");
        tagMap.put(new Integer(0x0200), "JPEGProc");
        tagMap.put(new Integer(0x0201), "ThumbnailOffset");
        tagMap.put(new Integer(0x0202), "ThumbnailLength");
        tagMap.put(new Integer(0x0211), "YCbCrCoefficients");
        tagMap.put(new Integer(0x0212), "YCbCrSubSampling");
        tagMap.put(new Integer(0x0213), "YCbCrPositioning");
        tagMap.put(new Integer(0x0214), "ReferenceBlackWhite");
        tagMap.put(new Integer(0x1000), "RelatedImageFileFormat");
        tagMap.put(new Integer(0x1001), "RelatedImageWidth");
        tagMap.put(new Integer(0x1002), "RelatedImageLength");
        tagMap.put(new Integer(0x828D), "CFARepeatPatternDim");
        tagMap.put(new Integer(0x828E), "CFAPattern");
        tagMap.put(new Integer(0x828F), "BatteryLevel");
        tagMap.put(new Integer(0x8298), "Copyright");
        tagMap.put(new Integer(0x829A), "ExposureTime");
        tagMap.put(new Integer(0x829D), "FNumber");
        tagMap.put(new Integer(0x83BB), "IPTC/NAA");
        tagMap.put(new Integer(0x8769), "ExifOffset");
        tagMap.put(new Integer(0x8773), "InterColorProfile");
        tagMap.put(new Integer(0x8822), "ExposureProgram");
        tagMap.put(new Integer(0x8824), "SpectralSensitivity");
        tagMap.put(new Integer(0x8825), "GPSInfo");
        tagMap.put(new Integer(0x8827), "ISOSpeedRatings");
        tagMap.put(new Integer(0x8828), "OECF");
        tagMap.put(new Integer(0x8829), "Interlace");
        tagMap.put(new Integer(0x882a), "TimeZoneOffset");
        tagMap.put(new Integer(0x882b), "SelfTimerMode");
        tagMap.put(new Integer(0x9000), "ExifVersion");
        tagMap.put(new Integer(0x9003), "DateTimeOriginal");
        tagMap.put(new Integer(0x9004), "DateTimeDigitized");
        tagMap.put(new Integer(0x9101), "ComponentsConfiguration");
        tagMap.put(new Integer(0x9102), "CompressedBitsPerPixel");
        tagMap.put(new Integer(0x9201), "ShutterSpeedValue");
        tagMap.put(new Integer(0x9202), "ApertureValue");
        tagMap.put(new Integer(0x9203), "BrightnessValue");
        tagMap.put(new Integer(0x9204), "ExposureBiasValue");
        tagMap.put(new Integer(0x9205), "MaxApertureValue");
        tagMap.put(new Integer(0x9206), "SubjectDistance");
        tagMap.put(new Integer(0x9207), "MeteringMode");
        tagMap.put(new Integer(0x9208), "LightSource");
        tagMap.put(new Integer(0x9209), "Flash");
        tagMap.put(new Integer(0x920A), "FocalLength");
        tagMap.put(new Integer(0x920B), "FlashEnergy");
        tagMap.put(new Integer(0x920c), "SpatialFrequencyResponse");
        tagMap.put(new Integer(0x920d), "Noise");
        tagMap.put(new Integer(0x9211), "ImageNumber");
        tagMap.put(new Integer(0x9212), "SecurityClassification");
        tagMap.put(new Integer(0x9213), "ImageHistory");
        tagMap.put(new Integer(0x9214), "SubjectLocation");
        tagMap.put(new Integer(0x9215), "ExposureIndex");
        tagMap.put(new Integer(0x9216), "TIFF/EPStandardID");
        tagMap.put(new Integer(0x927C), "MakerNote");
        tagMap.put(new Integer(0x9286), "UserComment");
        tagMap.put(new Integer(0x9290), "SubSecTime");
        tagMap.put(new Integer(0x9291), "SubSecTimeOriginal");
        tagMap.put(new Integer(0x9292), "SubSecTimeDigitized");
        tagMap.put(new Integer(0xA000), "FlashPixVersion");
        tagMap.put(new Integer(0xA001), "ColorSpace");
        tagMap.put(new Integer(0xA002), "ExifImageWidth");
        tagMap.put(new Integer(0xA003), "ExifImageHeight");
        tagMap.put(new Integer(0xA004), "RelatedSoundFile");
        tagMap.put(new Integer(0xA005), "InteroperabilityOffset");
        // 0x920B in TIFF/EP
        tagMap.put(new Integer(0xA20B), "FlashEnergy");
        // 0x920C in TIFF/EP
        tagMap.put(new Integer(0xA20C), "SpatialFrequencyResponse");
        // 0x920E in TIFF/EP
        tagMap.put(new Integer(0xA20E), "FocalPlaneXResolution");
        // 0x920F in TIFF/EP
        tagMap.put(new Integer(0xA20F), "FocalPlaneYResolution");
        // 0x9210 in TIFF/EP
        tagMap.put(new Integer(0xA210), "FocalPlaneResolutionUnit");
        // 0x9214 in TIFF/EP
        tagMap.put(new Integer(0xA214), "SubjectLocation");
        // 0x9215 in TIFF/EP
        tagMap.put(new Integer(0xA215), "ExposureIndex");
        // 0x9217 in TIFF/EP
        tagMap.put(new Integer(0xA217), "SensingMethod");
        tagMap.put(new Integer(0xA300), "FileSource");
        tagMap.put(new Integer(0xA301), "SceneType");
        tagMap.put(new Integer(0xA302), "CFAPattern");
    }

    /**
     * Static helper method to provide the name associated with an Exif tag's
     * number.
     * @param tagType the Exif tag value as an int
     * @return the Exif tag name as a String
     */
    public static String getTagName(int tagType)
    {
        String tagName = (String)tagMap.get(new Integer(tagType));
        if (tagName==null) {
            tagName = "Unknown tag (0x"+Integer.toHexString(tagType)+")";
        }
        return tagName;
    }

    /**
     * Image attributes are stored here, hashed by the Integer representation of
     * the 'tagType'.
     */
    private HashMap valueMap = new HashMap();

    /**
     * Lists tag values set against this object, assisting creation of an
     * Iterator.
     */
    private final ArrayList definedTagList = new ArrayList();

    /**
     * Creates a new instance of ImageInfo.  Package private.
     */
    ImageInfo()
    {
        //
    }
    
    /**
     * Sets an int value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag as an int
     */
    public void setInt(int tagType, int value)
    {
        addObject(tagType, new Integer(value));
    }

    /**
     * Sets a double value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag as a double
     */
    public void setDouble(int tagType, double value)
    {
        addObject(tagType, new Double(value));
    }

    /**
     * Sets a float value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag as a float
     */
    public void setFloat(int tagType, float value)
    {
        addObject(tagType, new Float(value));
    }

    /**
     * Sets an int value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag as a String
     */
    public void setString(int tagType, String value)
    {
        addObject(tagType, value);
    }

    /**
     * Sets an int value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag as a boolean
     */
    public void setBoolean(int tagType, boolean value)
    {
        addObject(tagType, new Boolean(value));
    }
    
    /**
     * Sets a long value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag as a long
     */
    public void setLong(int tagType, long value)
    {
        addObject(tagType, new Long(value));
    }

    /**
     * Sets a java.util.Date value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag as a java.util.Date
     */
    public void setDate(int tagType, java.util.Date value)
    {
        addObject(tagType, value);
    }

    /**
     * Sets a Rational value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param numerator the rational number's numerator
     * @param denominator the rational number's denominator
     */
    public void setRational(int tagType, int numerator, int denominator)
    {
        addObject(tagType, new Rational(numerator, denominator));
    }
    
    /**
     * Private helper method, containing common functionality for all 'add'
     * methods.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag
     */
    private void addObject(int tagType, Object value)
    {
        Integer tag = new Integer(tagType);
        if (!valueMap.containsKey(tag)) {
            definedTagList.add(tag);
        }
        valueMap.put(tag, value);
    }
    
    /**
     * Creates an Iterator over the tag types set against this image, preserving the order
     * in which they were set.  Should the same tag have been set more than once, it's first
     * position is maintained, even though the final value is used.
     * @return an Iterator of tag types set for this image
     */
    public Iterator getTagIterator()
    {
        return definedTagList.iterator();
    }
    
    /**
     * Returns a count of unique tag types which have been set.
     * @return the number of unique tag types set for this image
     */
    public int countTags()
    {
        return definedTagList.size();
    }
    
    /**
     * Indicates whether the specified tag type has been set.
     * @param tagType the tag type to check for
     * @return true if a value exists for the specified tag type, false if not
     */
    public boolean containsTag(int tagType)
    {
        return valueMap.containsKey(new Integer(tagType));
    }

    // TODO handle the below exceptions more elegantly... throwing Runtime Exceptions is bad form...

    /**
     * Returns the specified tag's value as an int, if possible.
     */
    public int getInt(int tagType)
    {
        Object o = valueMap.get(new Integer(tagType));
        if (o==null) {
            throw new RuntimeException("Requested tag has not been set -- check using containsTag() first");
        } else if (o instanceof String) {
            return Integer.parseInt((String)o);
        } else if (o instanceof Number) {
            return ((Number)o).intValue();
        } else {
            throw new RuntimeException("Requested tag cannot be cast to int");
        }
    }

    /**
     * Returns the specified tag's value as a double, if possible.
     */
    public double getDouble(int tagType)
    {
        Object o = valueMap.get(new Integer(tagType));
        if (o==null) {
            throw new RuntimeException("Requested tag has not been set -- check using containsTag() first");
        } else if (o instanceof String) {
            return Double.parseDouble((String)o);
        } else if (o instanceof Number) {
            return ((Number)o).doubleValue();
        } else {
            throw new RuntimeException("Requested tag cannot be cast to double");
        }
    }

    /**
     * Returns the specified tag's value as a float, if possible.
     */
    public float getFloat(int tagType)
    {
        Object o = valueMap.get(new Integer(tagType));
        if (o==null) {
            throw new RuntimeException("Requested tag has not been set -- check using containsTag() first");
        } else if (o instanceof String) {
            return Float.parseFloat((String)o);
        } else if (o instanceof Number) {
            return ((Number)o).floatValue();
        } else {
            throw new RuntimeException("Requested tag cannot be cast to float");
        }
    }

    /**
     * Returns the specified tag's value as a long, if possible.
     */
    public long getLong(int tagType)
    {
        Object o = valueMap.get(new Integer(tagType));
        if (o==null) {
            throw new RuntimeException("Requested tag has not been set -- check using containsTag() first");
        } else if (o instanceof String) {
            return Long.parseLong((String)o);
        } else if (o instanceof Number) {
            return ((Number)o).longValue();
        } else {
            throw new RuntimeException("Requested tag cannot be cast to long");
        }
    }
    
    /**
     * Returns the specified tag's value as a boolean, if possible.
     */
    public boolean getBoolean(int tagType)
    {
        Object o = valueMap.get(new Integer(tagType));
        if (o==null) {
            throw new RuntimeException("Requested tag has not been set -- check using containsTag() first");
        } else if (o instanceof Boolean) {
            return ((Boolean)o).booleanValue();
        } else if (o instanceof String) {
            return Boolean.getBoolean((String)o);
        } else if (o instanceof Number) {
            return (((Number)o).doubleValue()!=0);
        } else {
            throw new RuntimeException("Requested tag cannot be cast to boolean");
        }
    }
    
    /**
     * Returns the specified tag's value as a java.util.Date, if possible.
     */
    public java.util.Date getDate(int tagType)
    {
        Object o = valueMap.get(new Integer(tagType));
        if (o==null) {
            throw new RuntimeException("Requested tag has not been set -- check using containsTag() first");
        } else if (o instanceof java.util.Date) {
            return (java.util.Date)o;
        } else {
            throw new RuntimeException("Requested tag cannot be cast to java.util.Date");
        }
    }
    
    /**
     * Returns the specified tag's value as a Rational, if possible.
     */
    public Rational getRational(int tagType)
    {
        Object o = valueMap.get(new Integer(tagType));
        if (o==null) {
            throw new RuntimeException("Requested tag has not been set -- check using containsTag() first");
        } else if (o instanceof Rational) {
            return (Rational)o;
        } else {
            throw new RuntimeException("Requested tag cannot be cast to Rational");
        }
    }

    /**
     * Returns the specified tag's value as a String.  In many cases, more
     * presentable values will be obtained from getDescription(int).
     * @return the String reprensentation of the tag's value, or
     *         <code>null</code> if the tag hasn't been defined.
     */
    public String getString(int tagType)
    {
        Object val = valueMap.get(new Integer(tagType));
        if (val==null) {
            return null;
        } else {
            return val.toString();
        }
    }

    /**
     * Returns a descriptive value of the the specified tag for this image.
     * Where possible, known values will be substituted here in place of the raw
     * tokens actually kept in the Exif segment.  If no substitution is
     * available, the value provided by getString(int) will be returned.
     * <p>
     * This and getString(int) are the only 'get' methods that won't throw an
     * exception.
     * @param tagType the tag to find a description for
     * @return a description of the image's value for the specified tag, or
     *         <code>null</code> if the tag hasn't been defined.
     */
    public String getDescription(int tagType)
    {
        if (!this.containsTag(tagType))
        {
            return null;
        }

        String description;

        switch (tagType)
        {
            case TAG_ORIENTATION:
                description = getOrientationDescription();
                break;

            case TAG_RESOLUTION_UNIT:
                description = getResolutionDescription();
                break;

            case TAG_YCBCR_POSITIONING:
                description = getYCbCrPositioningDescription();
                break;

            case TAG_EXPOSURE_TIME:
                description = getExposureTimeDescription();
                break;

            case TAG_SHUTTER_SPEED:
                description = getShutterSpeedDescription();
                break;

            case TAG_FNUMBER:
                description = getFNumberDescription();
                break;

            case TAG_X_RESOLUTION:
                description = getXResolutionDescription();
                break;

            case TAG_Y_RESOLUTION:
                description = getYResolutionDescription();
                break;

            case TAG_THUMBNAIL_OFFSET:
                description = getThumbnailOffsetDescription();
                break;

            case TAG_THUMBNAIL_LENGTH:
                description = getThumbnailLengthDescription();
                break;

            case TAG_COMPRESSION_LEVEL:
                description = getCompressionLevel();
                break;

            case TAG_SUBJECT_DISTANCE:
                description = getSubjectDistanceDescription();
                break;

            case TAG_METERING_MODE:
                description = getMeteringModeDescription();
                break;

            case TAG_WHITE_BALANCE:
                description = getWhiteBalanceDescription();
                break;

            case TAG_FLASH:
                description = getFlashDescription();
                break;

            case TAG_FOCAL_LENGTH:
                description = getFocalLengthDescription();
                break;

            case TAG_COLOR_SPACE:
                description = getColorSpaceDescription();
                break;

            case TAG_EXIF_IMAGE_WIDTH:
                description = getExifImageWidthDescription();
                break;

            case TAG_EXIF_IMAGE_HEIGHT:
                description = getExifImageHeightDescription();
                break;

            case TAG_FOCAL_PLANE_UNIT:
                description = getFocalPlaneResolutionUnitDescription();
                break;

            case TAG_FOCAL_PLANE_X_RES:
                description = getFocalPlaneXResolutionDescription();
                break;

            case TAG_FOCAL_PLANE_Y_RES:
                description = getFocalPlaneYResolutionDescription();
                break;

            case TAG_THUMBNAIL_IMAGE_WIDTH:
                description = getThumbnailImageWidthDescription();
                break;

            case TAG_THUMBNAIL_IMAGE_HEIGHT:
                description = getThumbnailImageHeightDescription();
                break;

            case TAG_BITS_PER_SAMPLE:
                description = getBitsPerSampleDescription();
                break;

            case TAG_COMPRESSION:
                description = getCompressionDescription();
                break;

            case TAG_PHOTOMETRIC_INTERPRETATION:
                description = getPhotometricInterpretationDescription();
                break;

            case TAG_ROWS_PER_STRIP:
                description = getRowsPerStripDescription();
                break;

            case TAG_STRIP_BYTE_COUNTS:
                description = getStripByteCountsDescription();
                break;

            case TAG_SAMPLES_PER_PIXEL:
                description = getSamplesPerPixelDescription();
                break;

            case TAG_PLANAR_CONFIGURATION:
                description = getPlanarConfigurationDescription();
                break;

            case TAG_YCBCR_SUBSAMPLING:
                description = getYCbCrSubsamplingDescription();
                break;

            case TAG_EXPOSURE_PROGRAM:
                description = getExposureProgramDescription();
                break;

            case TAG_APERTURE:
                description = getApertureValueDescription();
                break;

            case TAG_MAX_APERTURE:
                description = getMaxApertureValueDescription();
                break;

            default:
                description = getString(tagType);
        }

        return description;
    }

    private String getMaxApertureValueDescription()
    {
        double apertureApex = getDouble(TAG_MAX_APERTURE);
        double rootTwo = Math.sqrt(2);
        double fStop = Math.pow(rootTwo, apertureApex);
        java.text.DecimalFormat formatter = new DecimalFormat("0.#");
        return "F" + formatter.format(fStop);
    }

    private String getApertureValueDescription()
    {
        double apertureApex = getDouble(TAG_APERTURE);
        double rootTwo = Math.sqrt(2);
        double fStop = Math.pow(rootTwo, apertureApex);
        java.text.DecimalFormat formatter = new DecimalFormat("0.#");
        return "F" + formatter.format(fStop);
    }

    private String getExposureProgramDescription()
    {
        // '1' means manual control, '2' program normal, '3' aperture priority,
        // '4' shutter priority, '5' program creative (slow program),
        // '6' program action(high-speed program), '7' portrait mode, '8' landscape mode.
        String description;
        switch (this.getInt(TAG_EXPOSURE_PROGRAM))
        {
            case 1:
                description = "Manual control";
                break;
            case 2:
                description = "Program normal";
                break;
            case 3:
                description = "Aperture priority";
                break;
            case 4:
                description = "Shutter priority";
                break;
            case 5:
                description = "Program creative (slow program)";
                break;
            case 6:
                description = "Program action (high-speed program)";
                break;
            case 7:
                description = "Portrait mode";
                break;
            case 8:
                description = "Landscape mode";
                break;
            default:
                description = "Unknown program ("+this.getInt(TAG_EXPOSURE_PROGRAM)+")";
        }
        return description;
    }

    private String getYCbCrSubsamplingDescription()
    {
        return getString(TAG_YCBCR_SUBSAMPLING) + " samples";
    }

    private String getPlanarConfigurationDescription()
    {
        // When image format is no compression YCbCr, this value shows byte aligns of YCbCr
        // data. If value is '1', Y/Cb/Cr value is chunky format, contiguous for each subsampling
        // pixel. If value is '2', Y/Cb/Cr value is separated and stored to Y plane/Cb plane/Cr
        // plane format.

        String description;
        switch (this.getInt(TAG_PLANAR_CONFIGURATION))
        {
            case 1:
                description = "Chunky (contiguous for each subsampling pixel)";
                break;
            case 2:
                description = "Separate (Y-plane/Cb-plane/Cr-plane format)";
                break;
            default:
                description = "Unknown configuration";
        }
        return description;
    }

    private String getSamplesPerPixelDescription()
    {
        return getString(TAG_SAMPLES_PER_PIXEL) + " samples/pixel";
    }

    private String getRowsPerStripDescription()
    {
        return getString(TAG_ROWS_PER_STRIP) + " rows/strip";
    }

    private String getStripByteCountsDescription()
    {
        return getString(TAG_STRIP_BYTE_COUNTS) + " bytes";
    }

    private String getPhotometricInterpretationDescription()
    {
        // Shows the color space of the image data components. '1' means monochrome,
        // '2' means RGB, '6' means YCbCr.
        String description;
        switch (this.getInt(TAG_COMPRESSION))
        {
            case 1:
                description = "Monochrome";
                break;
            case 2:
                description = "RGB";
                break;
            case 6:
                description = "YCbCr";
                break;
            default:
                description = "Unknown colour space";
        }
        return description;
    }

    private String getCompressionDescription()
    {
        // '1' means no compression, '6' means JPEG compression.
        String description;
        switch (this.getInt(TAG_COMPRESSION))
        {
            case 1:
                description = "No compression";
                break;
            case 6:
                description = "JPEG compression";
                break;
            default:
                description = "Unknown compression";
        }
        return description;
    }

    private String getBitsPerSampleDescription()
    {
        return getString(TAG_BITS_PER_SAMPLE) + " bits/component/pixel";
    }

    private String getThumbnailImageWidthDescription()
    {
        return getString(TAG_THUMBNAIL_IMAGE_WIDTH) + " pixels";
    }

    private String getThumbnailImageHeightDescription()
    {
        return getString(TAG_THUMBNAIL_IMAGE_HEIGHT) + " pixels";
    }

    private String getFocalPlaneXResolutionDescription()
    {
        Rational rational = getRational(TAG_FOCAL_PLANE_X_RES);
        return rational.getReciprocal() +
                " " +
                getFocalPlaneResolutionUnitDescription().toLowerCase();
    }

    private String getFocalPlaneYResolutionDescription()
    {
        Rational rational = getRational(TAG_FOCAL_PLANE_Y_RES);
        return rational.getReciprocal() +
                " " +
                getFocalPlaneResolutionUnitDescription().toLowerCase();
    }

    private String getFocalPlaneResolutionUnitDescription()
    {
        // Unit of FocalPlaneXResoluton/FocalPlaneYResolution. '1' means no-unit,
        // '2' inch, '3' centimeter.
        String description;
        switch (this.getInt(TAG_FOCAL_PLANE_UNIT))
        {
            case 1:
                description = "(No unit)";
                break;
            case 2:
                description = "Inches";
                break;
            case 3:
                description = "cm";
                break;
            default:
                description = "";
        }
        return description;
    }

    private String getExifImageWidthDescription()
    {
        return getInt(TAG_EXIF_IMAGE_WIDTH) + " pixels";
    }

    private String getExifImageHeightDescription()
    {
        return getInt(TAG_EXIF_IMAGE_HEIGHT) + " pixels";
    }

    private String getColorSpaceDescription()
    {
        String description;
        int colorSpace = getInt(TAG_COLOR_SPACE);
        if (colorSpace==1) {
            description = "sRGB";
        } else if (colorSpace==65535) {
            description = "Undefined";
        } else {
            description = "Unknown";
        }
        return description;
    }

    private String getFocalLengthDescription()
    {
        return getRational(TAG_FOCAL_LENGTH).doubleValue() + " mm";
    }

    private String getFlashDescription()
    {
        // '0' means flash did not fire, '1' flash fired, '5' flash fired but strobe return
        // light not detected, '7' flash fired and strobe return light detected.
        String description;
        switch (this.getInt(TAG_FLASH))
        {
            case 0:
                description = "No flash fired";
                break;
            case 1:
                description = "Flash fired";
                break;
            case 5:
                description = "Flash fired but strobe return light not detected";
                break;
            case 7:
                description = "flash fired and strobe return light detected";
                break;
            default:
                description = "Unknown ("+this.getInt(TAG_FLASH)+")";
        }
        return description;
    }

    private String getWhiteBalanceDescription()
    {
        // '0' means unknown, '1' daylight, '2' fluorescent, '3' tungsten, '10' flash,
        // '17' standard light A, '18' standard light B, '19' standard light C, '20' D55,
        // '21' D65, '22' D75, '255' other.
        String description;
        switch (this.getInt(TAG_WHITE_BALANCE))
        {
            case 0:
                description = "Unknown";
                break;
            case 1:
                description = "Daylight";
                break;
            case 2:
                description = "Flourescent";
                break;
            case 3:
                description = "Tungsten";
                break;
            case 10:
                description = "Flash";
                break;
            case 17:
                description = "Standard light";
                break;
            case 18:
                description = "Standard light (B)";
                break;
            case 19:
                description = "Standard light (C)";
                break;
            case 20:
                description = "D55";
                break;
            case 21:
                description = "D65";
                break;
            case 22:
                description = "D75";
                break;
            case 255:
                description = "(Other)";
                break;
            default:
                description = "Unknown ("+this.getInt(TAG_WHITE_BALANCE)+")";
        }
        return description;
    }

    private String getMeteringModeDescription()
    {
        // '0' means unknown, '1' average, '2' center weighted average, '3' spot
        // '4' multi-spot, '5' multi-segment, '6' partial, '255' other
        String description;
        switch (this.getInt(TAG_METERING_MODE))
        {
            case 0:
                description = "Unknown";
                break;
            case 1:
                description = "Average";
                break;
            case 2:
                description = "Center weighted average";
                break;
            case 3:
                description = "Spot";
                break;
            case 4:
                description = "Multi-spot";
                break;
            case 5:
                description = "Multi-segment";
                break;
            case 6:
                description = "Partial";
                break;
            case 255:
                description = "(Other)";
                break;
            default:
                description = "";
        }
        return description;
    }

    private String getSubjectDistanceDescription()
    {
        Rational distance = getRational(TAG_SUBJECT_DISTANCE);
        return  distance.doubleValue() + " metres";
    }

    private String getCompressionLevel()
    {
        Rational compressionRatio = getRational(TAG_COMPRESSION_LEVEL);
        String str;
        if (compressionRatio.isInteger()) {
            str = Integer.toString(compressionRatio.intValue());
        } else {
            str = compressionRatio.toString();
        }
        return str + " bits/pixel";
    }

    private String getThumbnailLengthDescription()
    {
        return getString(TAG_THUMBNAIL_LENGTH) + " bytes";
    }

    private String getThumbnailOffsetDescription()
    {
        return getString(TAG_THUMBNAIL_OFFSET) + " bytes";
    }

    private String getYResolutionDescription()
    {
        Rational inverseResolution = getRational(TAG_Y_RESOLUTION);
        return inverseResolution.getReciprocal() + " " +
                getResolutionDescription().toLowerCase();
    }

    private String getXResolutionDescription()
    {
        Rational inverseResolution = getRational(TAG_X_RESOLUTION);
        return inverseResolution.getReciprocal() + " " +
                getResolutionDescription().toLowerCase();
    }

    private String getExposureTimeDescription()
    {
        return getString(TAG_EXPOSURE_TIME) + " sec";
    }

    private String getShutterSpeedDescription()
    {
        int apexValue = getInt(TAG_SHUTTER_SPEED);
        int apexPower = 2 ^ apexValue;
        return "1/" + apexPower + " sec";
    }

    private String getFNumberDescription()
    {
        Rational fNumber = getRational(TAG_FNUMBER);
        java.text.DecimalFormat formatter = new DecimalFormat("0.#");
        return "F" + formatter.format(fNumber.doubleValue());
    }

    private String getYCbCrPositioningDescription()
    {
        String description;
        switch (this.getInt(TAG_ORIENTATION))
        {
            case 1:
                description = "Center of pixel array";
                break;
            case 2:
                description = "Datum point";
                break;
            default:
                description = "";
        }

        return description;
    }

    private String getOrientationDescription()
    {
        String description;
        switch (this.getInt(TAG_ORIENTATION))
        {
            case 1:
                description = "top, left side";
                break;
            case 2:
                description = "top, right side";
                break;
            case 3:
                description = "bottom, right side";
                break;
            case 4:
                description = "bottom, left side";
                break;
            case 5:
                description = "left side, top";
                break;
            case 6:
                description = "right side, top";
                break;
            case 7:
                description = "right side, bottom";
                break;
            case 8:
                description = "left side, bottom";
                break;
            default:
                description = "";
        }

        return description;
    }

    private String getResolutionDescription()
    {
        // '1' means no-unit, '2' means inch, '3' means centimeter. Default value is '2'(inch)
        String description;
        switch (this.getInt(TAG_RESOLUTION_UNIT))
        {
            case 1:
                description = "(No unit)";
                break;
            case 2:
                description = "Inches";
                break;
            case 3:
                description = "cm";
                break;
            default:
                description = "";
        }
        return description;
    }
}

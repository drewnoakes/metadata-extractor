/*
 * Created by dnoakes on 25-Nov-2002 20:41:00 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 */
public class ExifDirectory extends Directory
{
    // TODO do these tags belong in the exif directory?
    public static final int TAG_SUB_IFDS = 0x014A;
    public static final int TAG_GPS_INFO = 0x8825;

    /**
     * The actual aperture value of lens when the image was taken. Unit is APEX.
     * To convert this value to ordinary F-number (F-stop), calculate this value's
     * power of root 2 (=1.4142). For example, if the ApertureValue is '5',
     * F-number is 1.4142^5 = F5.6.
     */
    public static final int TAG_APERTURE = 0x9202;
    /**
     * When image format is no compression, this value shows the number of bits
     * per component for each pixel. Usually this value is '8,8,8'.
     */
    public static final int TAG_BITS_PER_SAMPLE = 0x0102;
    /**
     * Shows compression method. '1' means no compression, '6' means JPEG
     * compression.
     */
    public static final int TAG_COMPRESSION = 0x0103;
    /**
     * Shows the color space of the image data components. '1' means monochrome,
     * '2' means RGB, '6' means YCbCr.
     */
    public static final int TAG_PHOTOMETRIC_INTERPRETATION = 0x0106;
    public static final int TAG_STRIP_OFFSETS = 0x0111;
    public static final int TAG_SAMPLES_PER_PIXEL = 0x0115;
    public static final int TAG_ROWS_PER_STRIP = 0x116;
    public static final int TAG_STRIP_BYTE_COUNTS = 0x0117;
    /**
     * When image format is no compression YCbCr, this value shows byte aligns of
     * YCbCr data. If value is '1', Y/Cb/Cr value is chunky format, contiguous for
     * each subsampling pixel. If value is '2', Y/Cb/Cr value is separated and
     * stored to Y plane/Cb plane/Cr plane format.
     */
    public static final int TAG_PLANAR_CONFIGURATION = 0x011C;
    public static final int TAG_YCBCR_SUBSAMPLING = 0x0212;
    public static final int TAG_IMAGE_DESCRIPTION = 0x010E;
    public static final int TAG_SOFTWARE = 0x0131;
    public static final int TAG_DATETIME = 0x0132;
    public static final int TAG_WHITE_POINT = 0x013E;
    public static final int TAG_PRIMARY_CHROMATICITIES = 0x013F;
    public static final int TAG_YCBCR_COEFFICIENTS = 0x0211;
    public static final int TAG_REFERENCE_BLACK_WHITE = 0x0214;
    public static final int TAG_COPYRIGHT = 0x8298;
    public static final int TAG_NEW_SUBFILE_TYPE = 0x00FE;
    public static final int TAG_SUBFILE_TYPE = 0x00FF;
    public static final int TAG_TRANSFER_FUNCTION = 0x012D;
    public static final int TAG_ARTIST = 0x013B;
    public static final int TAG_PREDICTOR = 0x013D;
    public static final int TAG_TILE_WIDTH = 0x0142;
    public static final int TAG_TILE_LENGTH = 0x0143;
    public static final int TAG_TILE_OFFSETS = 0x0144;
    public static final int TAG_TILE_BYTE_COUNTS = 0x0145;
    public static final int TAG_JPEG_TABLES = 0x015B;
    public static final int TAG_CFA_REPEAT_PATTERN_DIM = 0x828D;
    /** There are two definitions for CFA pattern, I don't know the difference... */
    public static final int TAG_CFA_PATTERN_2 = 0x828E;
    public static final int TAG_BATTERY_LEVEL = 0x828F;
    public static final int TAG_IPTC_NAA = 0x83BB;
    public static final int TAG_INTER_COLOR_PROFILE = 0x8773;
    public static final int TAG_SPECTRAL_SENSITIVITY = 0x8824;
    public static final int TAG_OECF = 0x8828;
    public static final int TAG_INTERLACE = 0x8829;
    public static final int TAG_TIME_ZONE_OFFSET = 0x882A;
    public static final int TAG_SELF_TIMER_MODE = 0x882B;
    public static final int TAG_FLASH_ENERGY = 0x920B;
    public static final int TAG_SPATIAL_FREQ_RESPONSE = 0x920C;
    public static final int TAG_NOISE = 0x920D;
    public static final int TAG_IMAGE_NUMBER = 0x9211;
    public static final int TAG_SECURITY_CLASSIFICATION = 0x9212;
    public static final int TAG_IMAGE_HISTORY = 0x9213;
    public static final int TAG_SUBJECT_LOCATION = 0x9214;
    /** There are two definitions for exposure index, I don't know the difference... */
    public static final int TAG_EXPOSURE_INDEX_2 = 0x9215;
    public static final int TAG_TIFF_EP_STANDARD_ID = 0x9216;
    public static final int TAG_FLASH_ENERGY_2 = 0xA20B;
    public static final int TAG_SPATIAL_FREQ_RESPONSE_2 = 0xA20C;
    public static final int TAG_SUBJECT_LOCATION_2 = 0xA214;
    public static final int TAG_MAKE = 0x010F;
    public static final int TAG_MODEL = 0x0110;
    public static final int TAG_ORIENTATION = 0x0112;
    public static final int TAG_X_RESOLUTION = 0x011A;
    public static final int TAG_Y_RESOLUTION = 0x011B;
    public static final int TAG_RESOLUTION_UNIT = 0x0128;
    public static final int TAG_THUMBNAIL_OFFSET = 0x0201;
    public static final int TAG_THUMBNAIL_LENGTH = 0x0202;
    public static final int TAG_YCBCR_POSITIONING = 0x0213;
    /**
     * Exposure time (reciprocal of shutter speed). Unit is second.
     */
    public static final int TAG_EXPOSURE_TIME = 0x829A;
    /**
     * The actual F-number(F-stop) of lens when the image was taken.
     */
    public static final int TAG_FNUMBER = 0x829D;
    /**
     * Exposure program that the camera used when image was taken. '1' means
     * manual control, '2' program normal, '3' aperture priority, '4' shutter
     * priority, '5' program creative (slow program), '6' program action
     * (high-speed program), '7' portrait mode, '8' landscape mode.
     */
    public static final int TAG_EXPOSURE_PROGRAM = 0x8822;
    public static final int TAG_ISO_EQUIVALENT = 0x8827;
    public static final int TAG_EXIF_VERSION = 0x9000;
    public static final int TAG_DATETIME_ORIGINAL = 0x9003;
    public static final int TAG_DATETIME_DIGITIZED = 0x9004;
    public static final int TAG_COMPONENTS_CONFIGURATION = 0x9101;
    /**
     * Average (rough estimate) compression level in JPEG bits per pixel.
     * */
    public static final int TAG_COMPRESSION_LEVEL = 0x9102;
    /**
     * Shutter speed by APEX value. To convert this value to ordinary 'Shutter Speed';
     * calculate this value's power of 2, then reciprocal. For example, if the
     * ShutterSpeedValue is '4', shutter speed is 1/(24)=1/16 second.
     */
    public static final int TAG_SHUTTER_SPEED = 0x9201;
    public static final int TAG_BRIGHTNESS_VALUE = 0x9203;
    public static final int TAG_EXPOSURE_BIAS = 0x9204;
    /**
     * Maximum aperture value of lens. You can convert to F-number by calculating
     * power of root 2 (same process of ApertureValue:0x9202).
     */
    public static final int TAG_MAX_APERTURE = 0x9205;
    public static final int TAG_SUBJECT_DISTANCE = 0x9206;
    /**
     * Exposure metering method. '0' means unknown, '1' average, '2' center
     * weighted average, '3' spot, '4' multi-spot, '5' multi-segment, '6' partial,
     * '255' other.
     */
    public static final int TAG_METERING_MODE = 0x9207;

    public static final int TAG_LIGHT_SOURCE = 0x9208;
    /**
     * White balance (aka light source). '0' means unknown, '1' daylight,
     * '2' fluorescent, '3' tungsten, '10' flash, '17' standard light A,
     * '18' standard light B, '19' standard light C, '20' D55, '21' D65,
     * '22' D75, '255' other.
     */
    public static final int TAG_WHITE_BALANCE = 0x9208;
    /**
     * '0' means flash did not fire, '1' flash fired, '5' flash fired but strobe
     * return light not detected, '7' flash fired and strobe return light
     * detected.
     */
    public static final int TAG_FLASH = 0x9209;
    /**
     * Focal length of lens used to take image. Unit is millimeter.
     */
    public static final int TAG_FOCAL_LENGTH = 0x920A;
    public static final int TAG_USER_COMMENT = 0x9286;
    public static final int TAG_SUBSECOND_TIME = 0x9290;
    public static final int TAG_SUBSECOND_TIME_ORIGINAL = 0x9291;
    public static final int TAG_SUBSECOND_TIME_DIGITIZED = 0x9292;
    public static final int TAG_FLASHPIX_VERSION = 0xA000;
    /**
     * Defines Color Space. DCF image must use sRGB color space so value is
     * always '1'. If the picture uses the other color space, value is
     * '65535':Uncalibrated.
     */
    public static final int TAG_COLOR_SPACE = 0xA001;
    public static final int TAG_EXIF_IMAGE_WIDTH = 0xA002;
    public static final int TAG_EXIF_IMAGE_HEIGHT = 0xA003;
    public static final int TAG_RELATED_SOUND_FILE = 0xA004;
    public static final int TAG_FOCAL_PLANE_X_RES = 0xA20E;
    public static final int TAG_FOCAL_PLANE_Y_RES = 0xA20F;
    /**
     * Unit of FocalPlaneXResoluton/FocalPlaneYResolution. '1' means no-unit,
     * '2' inch, '3' centimeter.
     *
     * Note: Some of Fujifilm's digicam(e.g.FX2700,FX2900,Finepix4700Z/40i etc)
     * uses value '3' so it must be 'centimeter', but it seems that they use a
     * '8.3mm?'(1/3in.?) to their ResolutionUnit. Fuji's BUG? Finepix4900Z has
     * been changed to use value '2' but it doesn't match to actual value also.
     */
    public static final int TAG_FOCAL_PLANE_UNIT = 0xA210;
    public static final int TAG_EXPOSURE_INDEX = 0xA215;
    public static final int TAG_SENSING_METHOD = 0xA217;
    public static final int TAG_FILE_SOURCE = 0xA300;
    public static final int TAG_SCENE_TYPE = 0xA301;
    public static final int TAG_CFA_PATTERN = 0xA302;

    public static final int TAG_THUMBNAIL_IMAGE_WIDTH = 0x0100;
    public static final int TAG_THUMBNAIL_IMAGE_HEIGHT = 0x0101;
    public static final int TAG_THUMBNAIL_DATA = 0xF001;

    // are these two exif values?
    public static final int TAG_FILL_ORDER = 0x010A;
    public static final int TAG_DOCUMENT_NAME = 0x010D;

    protected static final HashMap tagNameMap = new HashMap();

    static
    {
        tagNameMap.put(new Integer(TAG_FILL_ORDER), "Fill Order");
        tagNameMap.put(new Integer(TAG_DOCUMENT_NAME), "Document Name");
        tagNameMap.put(new Integer(0x1000), "Related Image File Format");
        tagNameMap.put(new Integer(0x1001), "Related Image Width");
        tagNameMap.put(new Integer(0x1002), "Related Image Length");
        tagNameMap.put(new Integer(0x0156), "Transfer Range");
        tagNameMap.put(new Integer(0x0200), "JPEG Proc");
        tagNameMap.put(new Integer(0x8769), "Exif Offset");
        tagNameMap.put(new Integer(TAG_COMPRESSION_LEVEL), "Compressed Bits Per Pixel");
        tagNameMap.put(new Integer(0x927C), "Maker Note");
        tagNameMap.put(new Integer(0xA005), "Interoperability Offset");

        tagNameMap.put(new Integer(TAG_NEW_SUBFILE_TYPE), "New Subfile Type");
        tagNameMap.put(new Integer(TAG_SUBFILE_TYPE), "Subfile Type");
        tagNameMap.put(new Integer(TAG_THUMBNAIL_IMAGE_WIDTH), "Thumbnail Image Width");
        tagNameMap.put(new Integer(TAG_THUMBNAIL_IMAGE_HEIGHT), "Thumbnail Image Height");
        tagNameMap.put(new Integer(TAG_BITS_PER_SAMPLE), "Bits Per Sample");
        tagNameMap.put(new Integer(TAG_COMPRESSION), "Compression");
        tagNameMap.put(new Integer(TAG_PHOTOMETRIC_INTERPRETATION), "Photometric Interpretation");
        tagNameMap.put(new Integer(TAG_IMAGE_DESCRIPTION), "Image Description");
        tagNameMap.put(new Integer(TAG_MAKE), "Make");
        tagNameMap.put(new Integer(TAG_MODEL), "Model");
        tagNameMap.put(new Integer(TAG_STRIP_OFFSETS), "Strip Offsets");
        tagNameMap.put(new Integer(TAG_ORIENTATION), "Orientation");
        tagNameMap.put(new Integer(TAG_SAMPLES_PER_PIXEL), "Samples Per Pixel");
        tagNameMap.put(new Integer(TAG_ROWS_PER_STRIP), "Rows Per Strip");
        tagNameMap.put(new Integer(TAG_STRIP_BYTE_COUNTS), "Strip Byte Counts");
        tagNameMap.put(new Integer(TAG_X_RESOLUTION), "X Resolution");
        tagNameMap.put(new Integer(TAG_Y_RESOLUTION), "Y Resolution");
        tagNameMap.put(new Integer(TAG_PLANAR_CONFIGURATION), "Planar Configuration");
        tagNameMap.put(new Integer(TAG_RESOLUTION_UNIT), "Resolution Unit");
        tagNameMap.put(new Integer(TAG_TRANSFER_FUNCTION), "Transfer Function");
        tagNameMap.put(new Integer(TAG_SOFTWARE), "Software");
        tagNameMap.put(new Integer(TAG_DATETIME), "Date/Time");
        tagNameMap.put(new Integer(TAG_ARTIST), "Artist");
        tagNameMap.put(new Integer(TAG_PREDICTOR), "Predictor");
        tagNameMap.put(new Integer(TAG_WHITE_POINT), "White Point");
        tagNameMap.put(new Integer(TAG_PRIMARY_CHROMATICITIES), "Primary Chromaticities");
        tagNameMap.put(new Integer(TAG_TILE_WIDTH), "Tile Width");
        tagNameMap.put(new Integer(TAG_TILE_LENGTH), "Tile Length");
        tagNameMap.put(new Integer(TAG_TILE_OFFSETS), "Tile Offsets");
        tagNameMap.put(new Integer(TAG_TILE_BYTE_COUNTS), "Tile Byte Counts");
        tagNameMap.put(new Integer(TAG_SUB_IFDS), "Sub IFDs");
        tagNameMap.put(new Integer(TAG_JPEG_TABLES), "JPEG Tables");
        tagNameMap.put(new Integer(TAG_THUMBNAIL_OFFSET), "Thumbnail Offset");
        tagNameMap.put(new Integer(TAG_THUMBNAIL_LENGTH), "Thumbnail Length");
        tagNameMap.put(new Integer(TAG_THUMBNAIL_DATA), "Thumbnail Data");
        tagNameMap.put(new Integer(TAG_YCBCR_COEFFICIENTS), "YCbCr Coefficients");
        tagNameMap.put(new Integer(TAG_YCBCR_SUBSAMPLING), "YCbCr Sub-Sampling");
        tagNameMap.put(new Integer(TAG_YCBCR_POSITIONING), "YCbCr Positioning");
        tagNameMap.put(new Integer(TAG_REFERENCE_BLACK_WHITE), "Reference Black/White");
        tagNameMap.put(new Integer(TAG_CFA_REPEAT_PATTERN_DIM), "CFA Repeat Pattern Dim");
        tagNameMap.put(new Integer(TAG_CFA_PATTERN_2), "CFA Pattern");
        tagNameMap.put(new Integer(TAG_BATTERY_LEVEL), "Battery Level");
        tagNameMap.put(new Integer(TAG_COPYRIGHT), "Copyright");
        tagNameMap.put(new Integer(TAG_EXPOSURE_TIME), "Exposure Time");
        tagNameMap.put(new Integer(TAG_FNUMBER), "F-Number");
        tagNameMap.put(new Integer(TAG_IPTC_NAA), "IPTC/NAA");
        tagNameMap.put(new Integer(TAG_INTER_COLOR_PROFILE), "Inter Color Profile");
        tagNameMap.put(new Integer(TAG_EXPOSURE_PROGRAM), "Exposure Program");
        tagNameMap.put(new Integer(TAG_SPECTRAL_SENSITIVITY), "Spectral Sensitivity");
        tagNameMap.put(new Integer(TAG_GPS_INFO), "GPS Info");
        tagNameMap.put(new Integer(TAG_ISO_EQUIVALENT), "ISO Speed Ratings");
        tagNameMap.put(new Integer(TAG_OECF), "OECF");
        tagNameMap.put(new Integer(TAG_INTERLACE), "Interlace");
        tagNameMap.put(new Integer(TAG_TIME_ZONE_OFFSET), "Time Zone Offset");
        tagNameMap.put(new Integer(TAG_SELF_TIMER_MODE), "Self Timer Mode");
        tagNameMap.put(new Integer(TAG_EXIF_VERSION), "Exif Version");
        tagNameMap.put(new Integer(TAG_DATETIME_ORIGINAL), "Date/Time Original");
        tagNameMap.put(new Integer(TAG_DATETIME_DIGITIZED), "Date/Time Digitized");
        tagNameMap.put(new Integer(TAG_COMPONENTS_CONFIGURATION), "Components Configuration");
        tagNameMap.put(new Integer(TAG_SHUTTER_SPEED), "Shutter Speed Value");
        tagNameMap.put(new Integer(TAG_APERTURE), "Aperture Value");
        tagNameMap.put(new Integer(TAG_BRIGHTNESS_VALUE), "Brightness Value");
        tagNameMap.put(new Integer(TAG_EXPOSURE_BIAS), "Exposure Bias Value");
        tagNameMap.put(new Integer(TAG_MAX_APERTURE), "Max Aperture Value");
        tagNameMap.put(new Integer(TAG_SUBJECT_DISTANCE), "Subject Distance");
        tagNameMap.put(new Integer(TAG_METERING_MODE), "Metering Mode");
        tagNameMap.put(new Integer(TAG_WHITE_BALANCE), "Light Source");
        tagNameMap.put(new Integer(TAG_FLASH), "Flash");
        tagNameMap.put(new Integer(TAG_FOCAL_LENGTH), "Focal Length");
        tagNameMap.put(new Integer(TAG_FLASH_ENERGY), "Flash Energy");
        tagNameMap.put(new Integer(TAG_SPATIAL_FREQ_RESPONSE), "Spatial Frequency Response");
        tagNameMap.put(new Integer(TAG_NOISE), "Noise");
        tagNameMap.put(new Integer(TAG_IMAGE_NUMBER), "Image Number");
        tagNameMap.put(new Integer(TAG_SECURITY_CLASSIFICATION), "Security Classification");
        tagNameMap.put(new Integer(TAG_IMAGE_HISTORY), "Image History");
        tagNameMap.put(new Integer(TAG_SUBJECT_LOCATION), "Subject Location");
        tagNameMap.put(new Integer(TAG_EXPOSURE_INDEX), "Exposure Index");
        tagNameMap.put(new Integer(TAG_TIFF_EP_STANDARD_ID), "TIFF/EP Standard ID");
        tagNameMap.put(new Integer(TAG_USER_COMMENT), "User Comment");
        tagNameMap.put(new Integer(TAG_SUBSECOND_TIME), "Sub-Sec Time");
        tagNameMap.put(new Integer(TAG_SUBSECOND_TIME_ORIGINAL), "Sub-Sec Time Original");
        tagNameMap.put(new Integer(TAG_SUBSECOND_TIME_DIGITIZED), "Sub-Sec Time Digitized");
        tagNameMap.put(new Integer(TAG_FLASHPIX_VERSION), "FlashPix Version");
        tagNameMap.put(new Integer(TAG_COLOR_SPACE), "Color Space");
        tagNameMap.put(new Integer(TAG_EXIF_IMAGE_WIDTH), "Exif Image Width");
        tagNameMap.put(new Integer(TAG_EXIF_IMAGE_HEIGHT), "Exif Image Height");
        tagNameMap.put(new Integer(TAG_RELATED_SOUND_FILE), "Related Sound File");
        // 0x920B in TIFF/EP
        tagNameMap.put(new Integer(TAG_FLASH_ENERGY_2), "Flash Energy");
        // 0x920C in TIFF/EP
        tagNameMap.put(new Integer(TAG_SPATIAL_FREQ_RESPONSE_2), "Spatial Frequency Response");
        // 0x920E in TIFF/EP
        tagNameMap.put(new Integer(TAG_FOCAL_PLANE_X_RES), "Focal Plane X Resolution");
        // 0x920F in TIFF/EP
        tagNameMap.put(new Integer(TAG_FOCAL_PLANE_Y_RES), "Focal Plane Y Resolution");
        // 0x9210 in TIFF/EP
        tagNameMap.put(new Integer(TAG_FOCAL_PLANE_UNIT), "Focal Plane Resolution Unit");
        // 0x9214 in TIFF/EP
        tagNameMap.put(new Integer(TAG_SUBJECT_LOCATION_2), "Subject Location");
        // 0x9215 in TIFF/EP
        tagNameMap.put(new Integer(TAG_EXPOSURE_INDEX_2), "Exposure Index");
        // 0x9217 in TIFF/EP
        tagNameMap.put(new Integer(TAG_SENSING_METHOD), "Sensing Method");
        tagNameMap.put(new Integer(TAG_FILE_SOURCE), "File Source");
        tagNameMap.put(new Integer(TAG_SCENE_TYPE), "Scene Type");
        tagNameMap.put(new Integer(TAG_CFA_PATTERN), "CFA Pattern");
    }

    public ExifDirectory()
    {
        this.setDescriptor(new ExifDescriptor(this));
    }

    public String getName()
    {
        return "Exif";
    }

    protected HashMap getTagNameMap()
    {
        return tagNameMap;
    }

    public byte[] getThumbnailData() throws MetadataException
    {
        if (!containsThumbnail())
            return null;
        
        return this.getByteArray(ExifDirectory.TAG_THUMBNAIL_DATA);
    }

    public void writeThumbnail(String filename) throws MetadataException, IOException
    {
        byte[] data = getThumbnailData();

        if (data==null)
        {
            throw new MetadataException("No thumbnail data exists.");
        }

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(filename);
            stream.write(data);
        } finally {
            if (stream!=null)
                stream.close();
        }
    }

    public boolean containsThumbnail()
    {
        return containsTag(ExifDirectory.TAG_THUMBNAIL_DATA);
    }
}

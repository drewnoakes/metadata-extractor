/*
 * ExifTagValues.java
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
 * Created on 6 May 2002, 16:53
 * Modified 04 Aug 2002 Drew Noakes
 * - Renamed a few constants
 * - Reordered tags in numeric order, making it easier to spot omissions
 * - Added 149 new tags, including camera make specific tags
 */

package com.drew.imaging.exif;

/**
 * Interface of constant values defining 32-bit tag values for each type of
 * data described in the Exif header.
 * <p>
 * @author Drew Noakes drew.noakes@drewnoakes.com
 */
public interface ExifTagValues
{
    public static final int TAG_INTEROP_INDEX = 0x0001;
    public static final int TAG_INTEROP_VERSION = 0x0002;
    public static final int TAG_RELATED_IMAGE_FILE_FORMAT = 0x1000;
    public static final int TAG_RELATED_IMAGE_WIDTH = 0x1001;
    public static final int TAG_RELATED_IMAGE_LENGTH = 0x1002;
    public static final int TAG_THUMBNAIL_IMAGE_WIDTH = 0x0100;
    public static final int TAG_THUMBNAIL_IMAGE_HEIGHT = 0x0101;

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
    public static final int TAG_SUB_IFDS = 0x014A;
    public static final int TAG_JPEG_TABLES = 0x015B;
    public static final int TAG_CFA_REPEAT_PATTERN_DIM = 0x828D;
    /** There are two definitions for CFA pattern, I don't know the difference... */
    public static final int TAG_CFA_PATTERN_2 = 0x828E;
    public static final int TAG_BATTERY_LEVEL = 0x828F;
    public static final int TAG_IPTC_NAA = 0x83BB;
    public static final int TAG_INTER_COLOR_PROFILE = 0x8773;
    public static final int TAG_SPECTRAL_SENSITIVITY = 0x8824;
    public static final int TAG_GPS_INFO = 0x8825;
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

    public static final int TAG_EXIF_OFFSET = 0x8769;

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

    /**
     * The actual aperture value of lens when the image was taken. Unit is APEX.
     * To convert this value to ordinary F-number (F-stop), calculate this value's
     * power of root 2 (=1.4142). For example, if the ApertureValue is '5',
     * F-number is 1.4142^5 = F5.6.
     */
    public static final int TAG_APERTURE = 0x9202;

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

    public static final int TAG_MAKER_NOTE = 0x927C;
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
    public static final int TAG_INTEROP_OFFSET = 0xA005;
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

    //// CAMERA SPECIFIC TAGS
    public static final int TAG_OLYMPUS_SPECIAL_MODE = 0x0200;
    public static final int TAG_OLYMPUS_JPEG_QUALITY = 0x0201;
    public static final int TAG_OLYMPUS_MACRO = 0x0202;
    public static final int TAG_OLYMPUS_UNKNOWN_1 = 0x0203;
    public static final int TAG_OLYMPUS_DIGI_ZOOM_RATIO = 0x0204;
    public static final int TAG_OLYMPUS_UNKNOWN_2 = 0x0205;
    public static final int TAG_OLYMPUS_UNKNOWN_3 = 0x0206;
    public static final int TAG_OLYMPUS_FIRMWARE_VERSION = 0x0207;
    public static final int TAG_OLYMPUS_PICT_INFO = 0x0208;
    public static final int TAG_OLYMPUS_CAMERA_ID = 0x0209;
    public static final int TAG_OLYMPUS_DATA_DUMP = 0x0F00;

    public static final int TAG_NIKON_ESERIES_UNKNOWN_1 = 0x0002;
    public static final int TAG_NIKON_ESERIES_QUALITY = 0x0003;
    public static final int TAG_NIKON_ESERIES_COLOR_MODE = 0x0004;
    public static final int TAG_NIKON_ESERIES_IMAGE_ADJUSTMENT = 0x0005;
    public static final int TAG_NIKON_ESERIES_CCD_SENSITIVITY = 0x0006;
    public static final int TAG_NIKON_ESERIES_WHITE_BALANCE = 0x0007;
    public static final int TAG_NIKON_ESERIES_FOCUS = 0x0008;
    public static final int TAG_NIKON_ESERIES_UNKNOWN_2 = 0x0009;
    public static final int TAG_NIKON_ESERIES_DIGITAL_ZOOM = 0x000A;
    public static final int TAG_NIKON_ESERIES_FISHEYE_CONVERTER = 0x000B;
    public static final int TAG_NIKON_ESERIES_UNKNOWN_3 = 0x0F00;

    public static final int TAG_NIKON_D1_UNKNOWN_1 = 0x0001;
    public static final int TAG_NIKON_D1_ISO_SETTING = 0x0002;
    public static final int TAG_NIKON_D1_COLOR_MODE = 0x0003;
    public static final int TAG_NIKON_D1_QUALITY = 0x0004;
    public static final int TAG_NIKON_D1_WHITE_BALANCE = 0x0005;
    public static final int TAG_NIKON_D1_IMAGE_SHARPENING = 0x0006;
    public static final int TAG_NIKON_D1_FOCUS_MODE = 0x0007;
    public static final int TAG_NIKON_D1_FLASH_SETTING = 0x0008;
    public static final int TAG_NIKON_D1_UNKNOWN_2 = 0x000A;
    public static final int TAG_NIKON_D1_ISO_SELECTION = 0x000F;
    public static final int TAG_NIKON_D1_IMAGE_ADJUSTMENT = 0x0080;
    public static final int TAG_NIKON_D1_ADAPTER = 0x0082;
    public static final int TAG_NIKON_D1_MANUAL_FOCUS_DISTANCE = 0x0085;
    public static final int TAG_NIKON_D1_DIGITAL_ZOOM = 0x0086;
    public static final int TAG_NIKON_D1_AF_FOCUS_POSITION = 0x0088;
    public static final int TAG_NIKON_D1_DATA_DUMP = 0x0010;

    public static final int TAG_CASIO_RECORDING_MODE = 0x0001;
    public static final int TAG_CASIO_QUALITY = 0x0002;
    public static final int TAG_CASIO_FOCUSING_MODE = 0x0003;
    public static final int TAG_CASIO_FLASH_MODE = 0x0004;
    public static final int TAG_CASIO_FLASH_INTENSITY = 0x0005;
    public static final int TAG_CASIO_OBJECT_DISTANCE = 0x0006;
    public static final int TAG_CASIO_WHITE_BALANCE = 0x0007;
    public static final int TAG_CASIO_UNKNOWN_1 = 0x0008;
    public static final int TAG_CASIO_UNKNOWN_2 = 0x0009;
    public static final int TAG_CASIO_DIGITAL_ZOOM = 0x000A;
    public static final int TAG_CASIO_SHARPNESS = 0x000B;
    public static final int TAG_CASIO_CONTRAST = 0x000C;
    public static final int TAG_CASIO_SATURATION = 0x000D;
    public static final int TAG_CASIO_UNKNOWN_3 = 0x000E;
    public static final int TAG_CASIO_UNKNOWN_4 = 0x000F;
    public static final int TAG_CASIO_UNKNOWN_5 = 0x0010;
    public static final int TAG_CASIO_UNKNOWN_6 = 0x0011;
    public static final int TAG_CASIO_UNKNOWN_7 = 0x0012;
    public static final int TAG_CASIO_UNKNOWN_8 = 0x0013;
    public static final int TAG_CASIO_CCD_SENSITIVITY = 0x0014;

    public static final int TAG_FUJIFILM_MAKERNOTE_VERSION = 0x0000;
    public static final int TAG_FUJIFILM_QUALITY = 0x1000;
    public static final int TAG_FUJIFILM_SHARPNESS = 0x1001;
    public static final int TAG_FUJIFILM_WHITE_BALANCE = 0x1002;
    public static final int TAG_FUJIFILM_COLOR = 0x1003;
    public static final int TAG_FUJIFILM_TONE = 0x1004;
    public static final int TAG_FUJIFILM_FLASH_MODE = 0x1010;
    public static final int TAG_FUJIFILM_FLASH_STRENGTH = 0x1011;
    public static final int TAG_FUJIFILM_MACRO = 0x1020;
    public static final int TAG_FUJIFILM_FOCUS_MODE = 0x1021;
    public static final int TAG_FUJIFILM_SLOW_SYNCHRO = 0x1030;
    public static final int TAG_FUJIFILM_PICTURE_MODE = 0x1031;
    public static final int TAG_FUJIFILM_UNKNOWN_1 = 0x1032;
    public static final int TAG_FUJIFILM_CONTINUOUS_TAKING_OR_AUTO_BRACKETTING = 0x1100;
    public static final int TAG_FUJIFILM_UNKNOWN_2 = 0x1200;
    public static final int TAG_FUJIFILM_BLUR_WARNING = 0x1300;
    public static final int TAG_FUJIFILM_FOCUS_WARNING = 0x1301;
    public static final int TAG_FUJIFILM_AE_WARNING = 0x1302;

    // CANON cameras have some funny bespoke fields that need further processing...
    public static final int TAG_CANON_IMAGE_TYPE = 0x0006;
    public static final int TAG_CANON_FIRMWARE_VERSION = 0x0007;
    public static final int TAG_CANON_IMAGE_NUMBER = 0x0008;
    public static final int TAG_CANON_OWNER_NAME = 0x0009;
    public static final int TAG_CANON_UNKNOWN_1 = 0x000D;
    public static final int TAG_CANON_CUSTOM_FUNCTIONS = 0x000F;
}
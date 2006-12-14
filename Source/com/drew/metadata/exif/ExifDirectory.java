/*
 * ExifDirectory.java
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
 * Created by dnoakes on 25-Nov-2002 20:41:00 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Describes Exif tags such as Camera Model, Date Taken, Aperture and Shutter Speed.
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
     * Shows compression method for Thumbnail.
     * 1 = Uncompressed
     * 2 = CCITT 1D
     * 3 = T4/Group 3 Fax
     * 4 = T6/Group 4 Fax
     * 5 = LZW
     * 6 = JPEG (old-style)
     * 7 = JPEG
     * 8 = Adobe Deflate
     * 9 = JBIG B&W
     * 10 = JBIG Color
     * 32766 = Next
     * 32771 = CCIRLEW
     * 32773 = PackBits
     * 32809 = Thunderscan
     * 32895 = IT8CTPAD
     * 32896 = IT8LW
     * 32897 = IT8MP
     * 32898 = IT8BL
     * 32908 = PixarFilm
     * 32909 = PixarLog
     * 32946 = Deflate
     * 32947 = DCS
     * 34661 = JBIG
     * 34676 = SGILog
     * 34677 = SGILog24
     * 34712 = JPEG 2000
     * 34713 = Nikon NEF Compressed
     */
    public static final int TAG_COMPRESSION = 0x0103;
    public static final int COMPRESSION_NONE = 1;
    public static final int COMPRESSION_JPEG = 6;

    /**
     * Shows the color space of the image data components.
     * 0 = WhiteIsZero
     * 1 = BlackIsZero
     * 2 = RGB
     * 3 = RGB Palette
     * 4 = Transparency Mask
     * 5 = CMYK
     * 6 = YCbCr
     * 8 = CIELab
     * 9 = ICCLab
     * 10 = ITULab
     * 32803 = Color Filter Array
     * 32844 = Pixar LogL
     * 32845 = Pixar LogLuv
     * 34892 = Linear Raw
     */
    public static final int TAG_PHOTOMETRIC_INTERPRETATION = 0x0106;
    /**
     * 1 = No dithering or halftoning
     * 2 = Ordered dither or halftone
     * 3 = Randomized dither
     */
    public static final int TAG_THRESHOLDING = 0x0107;
    public static final int PHOTOMETRIC_INTERPRETATION_MONOCHROME = 1;
    public static final int PHOTOMETRIC_INTERPRETATION_RGB = 2;
    public static final int PHOTOMETRIC_INTERPRETATION_YCBCR = 6;

    /** The position in the file of raster data. */
    public static final int TAG_STRIP_OFFSETS = 0x0111;
    /** Each pixel is composed of this many samples. */
    public static final int TAG_SAMPLES_PER_PIXEL = 0x0115;
    /** The raster is codified by a single block of data holding this many rows. */
    public static final int TAG_ROWS_PER_STRIP = 0x116;
    /** The size of the raster data in bytes. */
    public static final int TAG_STRIP_BYTE_COUNTS = 0x0117;
    public static final int TAG_MIN_SAMPLE_VALUE = 0x0118;
    public static final int TAG_MAX_SAMPLE_VALUE = 0x0119;
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

    /**
     * The new subfile type tag.
     * 0 = Full-resolution Image
     * 1 = Reduced-resolution image
     * 2 = Single page of multi-page image
     * 3 = Single page of multi-page reduced-resolution image
     * 4 = Transparency mask
     * 5 = Transparency mask of reduced-resolution image
     * 6 = Transparency mask of multi-page image
     * 7 = Transparency mask of reduced-resolution multi-page image
     */
    public static final int TAG_NEW_SUBFILE_TYPE = 0x00FE;
    /**
     * The old subfile type tag.
     * 1 = Full-resolution image (Main image)
     * 2 = Reduced-resolution image (Thumbnail)
     * 3 = Single page of multi-page image
     */
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
    public static final int TAG_PAGE_NAME = 0x011D;
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
     * The actual aperture value of lens when the image was taken. To convert this
     * value to ordinary f-number(f-stop), calculate the value's power of root 2
     * (=1.4142). For example, if the ApertureValue is '5', f-number is 1.41425^5 = F5.6.
     */
    public static final int TAG_MAX_APERTURE = 0x9205;
    /**
     * Indicates the distance the autofocus camera is focused to.  Tends to be less accurate as distance increases.
     */
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
     * 0x0  = 0000000 = No Flash
     * 0x1  = 0000001 = Fired
     * 0x5  = 0000101 = Fired, Return not detected
     * 0x7  = 0000111 = Fired, Return detected
     * 0x9  = 0001001 = On
     * 0xd  = 0001101 = On, Return not detected
     * 0xf  = 0001111 = On, Return detected
     * 0x10 = 0010000 = Off
     * 0x18 = 0011000 = Auto, Did not fire
     * 0x19 = 0011001 = Auto, Fired
     * 0x1d = 0011101 = Auto, Fired, Return not detected
     * 0x1f = 0011111 = Auto, Fired, Return detected
     * 0x20 = 0100000 = No flash function
     * 0x41 = 1000001 = Fired, Red-eye reduction
     * 0x45 = 1000101 = Fired, Red-eye reduction, Return not detected
     * 0x47 = 1000111 = Fired, Red-eye reduction, Return detected
     * 0x49 = 1001001 = On, Red-eye reduction
     * 0x4d = 1001101 = On, Red-eye reduction, Return not detected
     * 0x4f = 1001111 = On, Red-eye reduction, Return detected
     * 0x59 = 1011001 = Auto, Fired, Red-eye reduction
     * 0x5d = 1011101 = Auto, Fired, Red-eye reduction, Return not detected
     * 0x5f = 1011111 = Auto, Fired, Red-eye reduction, Return detected
     *        6543210 (positions)
     *
     * This is a bitmask.
     * 0 = flash fired
     * 1 = return detected
     * 2 = return able to be detected
     * 3 = unknown
     * 4 = auto used
     * 5 = unknown
     * 6 = red eye reduction used
     */
    public static final int TAG_FLASH = 0x9209;
    /**
     * Focal length of lens used to take image.  Unit is millimeter.
     * Nice digital cameras actually save the focal length as a function of how far they are zoomed in.
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

    // these tags new with Exif 2.2 (?) [A401 - A4
    /**
     * This tag indicates the use of special processing on image data, such as rendering
     * geared to output. When special processing is performed, the reader is expected to
     * disable or minimize any further processing.
     * Tag = 41985 (A401.H)
     * Type = SHORT
     * Count = 1
     * Default = 0
     *   0 = Normal process
     *   1 = Custom process
     *   Other = reserved
     */
    public static final int TAG_CUSTOM_RENDERED = 0xA401;

    /**
     * This tag indicates the exposure mode set when the image was shot. In auto-bracketing
     * mode, the camera shoots a series of frames of the same scene at different exposure settings.
     * Tag = 41986 (A402.H)
     * Type = SHORT
     * Count = 1
     * Default = none
     *   0 = Auto exposure
     *   1 = Manual exposure
     *   2 = Auto bracket
     *   Other = reserved
     */
    public static final int TAG_EXPOSURE_MODE = 0xA402;

    /**
     * This tag indicates the white balance mode set when the image was shot.
     * Tag = 41987 (A403.H)
     * Type = SHORT
     * Count = 1
     * Default = none
     *   0 = Auto white balance
     *   1 = Manual white balance
     *   Other = reserved
     */
    public static final int TAG_WHITE_BALANCE_MODE = 0xA403;

    /**
     * This tag indicates the digital zoom ratio when the image was shot. If the
     * numerator of the recorded value is 0, this indicates that digital zoom was
     * not used.
     * Tag = 41988 (A404.H)
     * Type = RATIONAL
     * Count = 1
     * Default = none
     */
    public static final int TAG_DIGITAL_ZOOM_RATIO = 0xA404;

    /**
     * This tag indicates the equivalent focal length assuming a 35mm film camera,
     * in mm. A value of 0 means the focal length is unknown. Note that this tag
     * differs from the FocalLength tag.
     * Tag = 41989 (A405.H)
     * Type = SHORT
     * Count = 1
     * Default = none
     */
    public static final int TAG_35MM_FILM_EQUIV_FOCAL_LENGTH = 0xA405;

    /**
     * This tag indicates the type of scene that was shot. It can also be used to
     * record the mode in which the image was shot. Note that this differs from
     * the scene type (SceneType) tag.
     * Tag = 41990 (A406.H)
     * Type = SHORT
     * Count = 1
     * Default = 0
     *   0 = Standard
     *   1 = Landscape
     *   2 = Portrait
     *   3 = Night scene
     *   Other = reserved
     */
    public static final int TAG_SCENE_CAPTURE_TYPE = 0xA406;

    /**
     * This tag indicates the degree of overall image gain adjustment.
     * Tag = 41991 (A407.H)
     * Type = SHORT
     * Count = 1
     * Default = none
     *   0 = None
     *   1 = Low gain up
     *   2 = High gain up
     *   3 = Low gain down
     *   4 = High gain down
     *   Other = reserved
     */
    public static final int TAG_GAIN_CONTROL = 0xA407;

    /**
     * This tag indicates the direction of contrast processing applied by the camera
     * when the image was shot.
     * Tag = 41992 (A408.H)
     * Type = SHORT
     * Count = 1
     * Default = 0
     *   0 = Normal
     *   1 = Soft
     *   2 = Hard
     *   Other = reserved
     */
    public static final int TAG_CONTRAST = 0xA408;

    /**
     * This tag indicates the direction of saturation processing applied by the camera
     * when the image was shot.
     * Tag = 41993 (A409.H)
     * Type = SHORT
     * Count = 1
     * Default = 0
     *   0 = Normal
     *   1 = Low saturation
     *   2 = High saturation
     *   Other = reserved
     */
    public static final int TAG_SATURATION = 0xA409;

    /**
     * This tag indicates the direction of sharpness processing applied by the camera
     * when the image was shot.
     * Tag = 41994 (A40A.H)
     * Type = SHORT
     * Count = 1
     * Default = 0
     *   0 = Normal
     *   1 = Soft
     *   2 = Hard
     *   Other = reserved
     */
    public static final int TAG_SHARPNESS = 0xA40A;

    // TODO support this tag (I haven't seen a camera's actual implementation of this yet)

    /**
     * This tag indicates information on the picture-taking conditions of a particular
     * camera model. The tag is used only to indicate the picture-taking conditions in
     * the reader.
     * Tag = 41995 (A40B.H)
     * Type = UNDEFINED
     * Count = Any
     * Default = none
     *
     * The information is recorded in the format shown below. The data is recorded
     * in Unicode using SHORT type for the number of display rows and columns and
     * UNDEFINED type for the camera settings. The Unicode (UCS-2) string including
     * Signature is NULL terminated. The specifics of the Unicode string are as given
     * in ISO/IEC 10464-1.
     *
     *      Length  Type        Meaning
     *      ------+-----------+------------------
     *      2       SHORT       Display columns
     *      2       SHORT       Display rows
     *      Any     UNDEFINED   Camera setting-1
     *      Any     UNDEFINED   Camera setting-2
     *      :       :           :
     *      Any     UNDEFINED   Camera setting-n
     */
    public static final int TAG_DEVICE_SETTING_DESCRIPTION = 0xA40B;

    /**
     * This tag indicates the distance to the subject.
     * Tag = 41996 (A40C.H)
     * Type = SHORT
     * Count = 1
     * Default = none
     *   0 = unknown
     *   1 = Macro
     *   2 = Close view
     *   3 = Distant view
     *   Other = reserved
     */
    public static final int TAG_SUBJECT_DISTANCE_RANGE = 0xA40C;

    /**
     * The image title, as used by Windows XP.
     */
    public static final int TAG_WIN_TITLE = 0x9C9B;

    /**
     * The image comment, as used by Windows XP.
     */
    public static final int TAG_WIN_COMMENT = 0x9C9C;

    /**
     * The image author, as used by Windows XP (called Artist in the Windows shell).
     */
    public static final int TAG_WIN_AUTHOR = 0x9C9D;

    /**
     * The image keywords, as used by Windows XP.
     */
    public static final int TAG_WIN_KEYWORDS = 0x9C9E;

    /**
     * The image subject, as used by Windows XP.
     */
    public static final int TAG_WIN_SUBJECT = 0x9C9F;

    /**
     * This tag indicates an identifier assigned uniquely to each image. It is
     * recorded as an ASCII string equivalent to hexadecimal notation and 128-bit
     * fixed length.
     * Tag = 42016 (A420.H)
     * Type = ASCII
     * Count = 33
     * Default = none
     */
    public static final int TAG_IMAGE_UNIQUE_ID = 0xA420;

    public static final int TAG_THUMBNAIL_IMAGE_WIDTH = 0x0100;
    public static final int TAG_THUMBNAIL_IMAGE_HEIGHT = 0x0101;
    public static final int TAG_THUMBNAIL_DATA = 0xF001;

    /**
     * 1 = Normal
     * 2 = Reversed
     */
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
        tagNameMap.put(new Integer(TAG_THRESHOLDING), "Thresholding");
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
        tagNameMap.put(new Integer(TAG_PAGE_NAME), "Page Name");
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

        tagNameMap.put(new Integer(TAG_CUSTOM_RENDERED), "Custom Rendered");
        tagNameMap.put(new Integer(TAG_EXPOSURE_MODE), "Exposure Mode");
        tagNameMap.put(new Integer(TAG_WHITE_BALANCE_MODE), "White Balance");
        tagNameMap.put(new Integer(TAG_DIGITAL_ZOOM_RATIO), "Digital Zoom Ratio");
        tagNameMap.put(new Integer(TAG_35MM_FILM_EQUIV_FOCAL_LENGTH), "Focal Length 35");
        tagNameMap.put(new Integer(TAG_SCENE_CAPTURE_TYPE), "Scene Capture Type");
        tagNameMap.put(new Integer(TAG_GAIN_CONTROL), "Gain Control");
        tagNameMap.put(new Integer(TAG_CONTRAST), "Contrast");
        tagNameMap.put(new Integer(TAG_SATURATION), "Saturation");
        tagNameMap.put(new Integer(TAG_SHARPNESS), "Sharpness");
        tagNameMap.put(new Integer(TAG_DEVICE_SETTING_DESCRIPTION), "Device Setting Description");
        tagNameMap.put(new Integer(TAG_SUBJECT_DISTANCE_RANGE), "Subject Distance Range");

        tagNameMap.put(new Integer(TAG_WIN_AUTHOR), "Windows XP Author");
        tagNameMap.put(new Integer(TAG_WIN_COMMENT), "Windows XP Comment");
        tagNameMap.put(new Integer(TAG_WIN_KEYWORDS), "Windows XP Keywords");
        tagNameMap.put(new Integer(TAG_WIN_SUBJECT), "Windows XP Subject");
        tagNameMap.put(new Integer(TAG_WIN_TITLE), "Windows XP Title");

        tagNameMap.put(new Integer(TAG_MIN_SAMPLE_VALUE), "Minimum sample value");
        tagNameMap.put(new Integer(TAG_MAX_SAMPLE_VALUE), "Maximum sample value");
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
            throw new MetadataException("No thumbnail data exists.");

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(filename);
            stream.write(data);
        } finally {
            if (stream!=null)
                stream.close();
        }
    }

/*
    // This thumbnail extraction code is not complete, and is included to assist anyone who feels like looking into
    // it.  Please share any progress with the original author, and hence the community.  Thanks.

    /**
     *
     * @return
     * @throws MetadataException
     * /
    public Image getThumbnailImage() throws MetadataException
    {
        if (!containsThumbnail())
            return null;

        int compression = 0;
        try {
        	compression = this.getInt(ExifDirectory.TAG_COMPRESSION);
        } catch (Throwable e) {
        	this.addError("Unable to determine thumbnail type " + e.getMessage());
        }

        final byte[] thumbnailBytes = getThumbnailData();

        if (compression == ExifDirectory.COMPRESSION_JPEG)
        {
            // JPEG Thumbnail
            // operate directly on thumbnailBytes
//            try {
//                int offset = this.getInt(ExifDirectory.TAG_THUMBNAIL_OFFSET);
//                int length = this.getInt(ExifDirectory.TAG_THUMBNAIL_LENGTH);
//                byte[] result = new byte[length];
//                for (int i = 0; i<result.length; i++) {
//                    result[i] = _data[tiffHeaderOffset + offset + i];
//                }
//                this.setByteArray(ExifDirectory.TAG_THUMBNAIL_DATA, result);
//            } catch (Throwable e) {
//                this.addError("Unable to extract thumbnail: " + e.getMessage());
//            }
            // TODO decode the JPEG bytes as an image
            return null; //new Image();
        }
        else if (compression == ExifDirectory.COMPRESSION_NONE)
        {
            // uncompressed thumbnail (raw RGB data)
        	if (!this.containsTag(ExifDirectory.TAG_PHOTOMETRIC_INTERPRETATION))
	            return null;

        	try
            {
        		// If the image is RGB format, then convert it to a bitmap
                final int photometricInterpretation = this.getInt(ExifDirectory.TAG_PHOTOMETRIC_INTERPRETATION);
                if (photometricInterpretation == ExifDirectory.PHOTOMETRIC_INTERPRETATION_RGB)
                {
                    // RGB
                    Image image = createImageFromRawRgb(thumbnailBytes);
                    return image;
        		}
                else if (photometricInterpretation == ExifDirectory.PHOTOMETRIC_INTERPRETATION_YCBCR)
                {
                    // YCbCr
                    Image image = createImageFromRawYCbCr(thumbnailBytes);
                    return image;
        		}
                else if (photometricInterpretation == ExifDirectory.PHOTOMETRIC_INTERPRETATION_MONOCHROME)
                {
                    // Monochrome
                    // TODO
                    return null;
                }
	        } catch (Throwable e) {
	            this.addError("Unable to extract thumbnail: " + e.getMessage());
	        }
        }
        return null;
    }

    /**
     * Handle the YCbCr thumbnail encoding used by Ricoh RDC4200/4300, Fuji DS-7/300 and DX-5/7/9 cameras.
     *
     * At DX-5/7/9, YCbCrSubsampling(0x0212) has values of '2,1', PlanarConfiguration(0x011c) has a value '1'. So the
     * data align of this image is below.
     *
     * Y(0,0),Y(1,0),Cb(0,0),Cr(0,0), Y(2,0),Y(3,0),Cb(2,0),Cr(3.0), Y(4,0),Y(5,0),Cb(4,0),Cr(4,0). . . .
     *
     * The numerics in parenthesis are pixel coordinates. DX series' YCbCrCoefficients(0x0211) has values '0.299/0.587/0.114',
     * ReferenceBlackWhite(0x0214) has values '0,255,128,255,128,255'. Therefore to convert from Y/Cb/Cr to RGB is;
     *
     * B(0,0)=(Cb-128)*(2-0.114*2)+Y(0,0)
     * R(0,0)=(Cr-128)*(2-0.299*2)+Y(0,0)
     * G(0,0)=(Y(0,0)-0.114*B(0,0)-0.299*R(0,0))/0.587
     *
     * Horizontal subsampling is a value '2', so you can calculate B(1,0)/R(1,0)/G(1,0) by using the Y(1,0) and Cr(0,0)/Cb(0,0).
     * Repeat this conversion by value of ImageWidth(0x0100) and ImageLength(0x0101).
     *
     * @param thumbnailBytes
     * @return
     * @throws com.drew.metadata.MetadataException
     * /
    private Image createImageFromRawYCbCr(byte[] thumbnailBytes) throws MetadataException
    {
        /*
            Y  =  0.257R + 0.504G + 0.098B + 16
            Cb = -0.148R - 0.291G + 0.439B + 128
            Cr =  0.439R - 0.368G - 0.071B + 128

            G = 1.164(Y-16) - 0.391(Cb-128) - 0.813(Cr-128)
            R = 1.164(Y-16) + 1.596(Cr-128)
            B = 1.164(Y-16) + 2.018(Cb-128)

            R, G and B range from 0 to 255.
            Y ranges from 16 to 235.
            Cb and Cr range from 16 to 240.

            http://www.faqs.org/faqs/graphics/colorspace-faq/
        * /

        int length = thumbnailBytes.length; // this.getInt(ExifDirectory.TAG_STRIP_BYTE_COUNTS);
        final int imageWidth = this.getInt(ExifDirectory.TAG_THUMBNAIL_IMAGE_WIDTH);
        final int imageHeight = this.getInt(ExifDirectory.TAG_THUMBNAIL_IMAGE_HEIGHT);
//        final int headerLength = 54;
//        byte[] result = new byte[length + headerLength];
//        // Add a windows BMP header described:
//        // http://www.onicos.com/staff/iz/formats/bmp.html
//        result[0] = 'B';
//        result[1] = 'M'; // File Type identifier
//        result[3] = (byte)(result.length / 256);
//        result[2] = (byte)result.length;
//        result[10] = (byte)headerLength;
//        result[14] = 40; // MS Windows BMP header
//        result[18] = (byte)imageWidth;
//        result[22] = (byte)imageHeight;
//        result[26] = 1;  // 1 Plane
//        result[28] = 24; // Colour depth
//        result[34] = (byte)length;
//        result[35] = (byte)(length / 256);

        final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        // order is YCbCr and image is upside down, bitmaps are BGR
////        for (int i = headerLength, dataOffset = length; i<result.length; i += 3, dataOffset -= 3)
//        {
//            final int y =  thumbnailBytes[dataOffset - 2] & 0xFF;
//            final int cb = thumbnailBytes[dataOffset - 1] & 0xFF;
//            final int cr = thumbnailBytes[dataOffset] & 0xFF;
//            if (y<16 || y>235 || cb<16 || cb>240 || cr<16 || cr>240)
//                "".toString();
//
//            int g = (int)(1.164*(y-16) - 0.391*(cb-128) - 0.813*(cr-128));
//            int r = (int)(1.164*(y-16) + 1.596*(cr-128));
//            int b = (int)(1.164*(y-16) + 2.018*(cb-128));
//
////            result[i] = (byte)b;
////            result[i + 1] = (byte)g;
////            result[i + 2] = (byte)r;
//
//            // TODO compose the image here
//            image.setRGB(1, 2, 3);
//        }

        return image;
    }

    /**
     * Creates a thumbnail image in (Windows) BMP format from raw RGB data.
     * @param thumbnailBytes
     * @return
     * @throws com.drew.metadata.MetadataException
     * /
    private Image createImageFromRawRgb(byte[] thumbnailBytes) throws MetadataException
    {
        final int length = thumbnailBytes.length; // this.getInt(ExifDirectory.TAG_STRIP_BYTE_COUNTS);
        final int imageWidth = this.getInt(ExifDirectory.TAG_THUMBNAIL_IMAGE_WIDTH);
        final int imageHeight = this.getInt(ExifDirectory.TAG_THUMBNAIL_IMAGE_HEIGHT);
//        final int headerlength = 54;
//        final byte[] result = new byte[length + headerlength];
//        // Add a windows BMP header described:
//        // http://www.onicos.com/staff/iz/formats/bmp.html
//        result[0] = 'B';
//        result[1] = 'M'; // File Type identifier
//        result[3] = (byte)(result.length / 256);
//        result[2] = (byte)result.length;
//        result[10] = (byte)headerlength;
//        result[14] = 40; // MS Windows BMP header
//        result[18] = (byte)imageWidth;
//        result[22] = (byte)imageHeight;
//        result[26] = 1;  // 1 Plane
//        result[28] = 24; // Colour depth
//        result[34] = (byte)length;
//        result[35] = (byte)(length / 256);

        final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        // order is RGB and image is upside down, bitmaps are BGR
//        for (int i = headerlength, dataOffset = length; i<result.length; i += 3, dataOffset -= 3)
//        {
//            byte b = thumbnailBytes[dataOffset - 2];
//            byte g = thumbnailBytes[dataOffset - 1];
//            byte r = thumbnailBytes[dataOffset];
//
//            // TODO compose the image here
//            image.setRGB(1, 2, 3);
//        }

        return image;
    }
*/

    public boolean containsThumbnail()
    {
        return containsTag(ExifDirectory.TAG_THUMBNAIL_DATA);
    }
}

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
 * Modified 29 Oct 2002 (v1.2)
 * - Stored IFD directories in separate tag-spaces
 * - iterator() now returns an Iterator over a list of TagValue objects
 * - More get*Description() methods to detail GPS tags, among others
 * - Put spaces between words of tag name for presentation reasons (they had no
 *   significance in compound form)
 */
package com.drew.imaging.exif;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Result from an exif extraction operation, containing all tags, their
 * values and support for retrieving them.
 * @author  Drew Noakes http://drewnoakes.com
 */
public final class ImageInfo implements ExifTagValues
{
    /**
     * Static Map for lookup of Exif tag names (such as 'Model' or 'XResolution')
     * by Integer directory and tag codes.
     */
    private static final HashMap tagNameMap = new HashMap();

    /**
     * Static Map for lookup of directory names (such as 'Exif' or 'Interoperability')
     * by Integer directory codes.
     */
    private static final HashMap directoryNameMap = new HashMap();

    static
    {
        directoryNameMap.put(new Integer(IFD_EXIF), "Exif");
        directoryNameMap.put(new Integer(IFD_GPS), "GPS");
        directoryNameMap.put(new Integer(IFD_INTEROP), "Interoperability");
        directoryNameMap.put(new Integer(IFD_MAKERNOTE_CANON), "CanonMakernote");
        directoryNameMap.put(new Integer(IFD_MAKERNOTE_OLYPMUS), "OlympusMakernote");
        directoryNameMap.put(new Integer(IFD_MAKERNOTE_NIKON_TYPE1), "NikonMakernote");
        directoryNameMap.put(new Integer(IFD_MAKERNOTE_NIKON_TYPE2), "NikonMakernote");
        directoryNameMap.put(new Integer(IFD_MAKERNOTE_CASIO), "CasioMakernote");
        directoryNameMap.put(new Integer(IFD_MAKERNOTE_FUJIFILM), "FujiFilmMakernote");
    }

    static
    {
        HashMap interopTagMap = new HashMap();
        interopTagMap.put(new Integer(TAG_INTEROP_INDEX), "Interoperability Index");
        interopTagMap.put(new Integer(TAG_INTEROP_VERSION), "Interoperability Version");
        interopTagMap.put(new Integer(TAG_RELATED_IMAGE_FILE_FORMAT), "Related Image File Format");
        interopTagMap.put(new Integer(TAG_RELATED_IMAGE_WIDTH), "Related Image Width");
        interopTagMap.put(new Integer(TAG_RELATED_IMAGE_LENGTH), "Related Image Length");
        tagNameMap.put(new Integer(IFD_INTEROP), interopTagMap);

        HashMap exifTagMap = new HashMap();
        exifTagMap.put(new Integer(0x00fe), "New Subfile Type");
        exifTagMap.put(new Integer(0x00ff), "Subfile Type");
        exifTagMap.put(new Integer(0x0100), "Thumbnail Image Width");
        exifTagMap.put(new Integer(0x0101), "Thumbnail Image Height");
        exifTagMap.put(new Integer(0x0102), "Bits Per Sample");
        exifTagMap.put(new Integer(0x0103), "Compression");
        exifTagMap.put(new Integer(0x0106), "Photometric Interpretation");
        exifTagMap.put(new Integer(0x010A), "Fill Order");
        exifTagMap.put(new Integer(0x010D), "Document Name");
        exifTagMap.put(new Integer(0x010E), "Image Description");
        exifTagMap.put(new Integer(0x010F), "Make");
        exifTagMap.put(new Integer(0x0110), "Model");
        exifTagMap.put(new Integer(0x0111), "Strip Offsets");
        exifTagMap.put(new Integer(0x0112), "Orientation");
        exifTagMap.put(new Integer(0x0115), "Samples Per Pixel");
        exifTagMap.put(new Integer(0x0116), "Rows Per Strip");
        exifTagMap.put(new Integer(0x0117), "Strip Byte Counts");
        exifTagMap.put(new Integer(0x011A), "X Resolution");
        exifTagMap.put(new Integer(0x011B), "Y Resolution");
        exifTagMap.put(new Integer(0x011C), "Planar Configuration");
        exifTagMap.put(new Integer(0x0128), "Resolution Unit");
        exifTagMap.put(new Integer(0x012D), "Transfer Function");
        exifTagMap.put(new Integer(0x0131), "Software");
        exifTagMap.put(new Integer(0x0132), "Date/Time");
        exifTagMap.put(new Integer(0x013B), "Artist");
        exifTagMap.put(new Integer(0x013d), "Predictor");
        exifTagMap.put(new Integer(0x013E), "White Point");
        exifTagMap.put(new Integer(0x013F), "Primary Chromaticities");
        exifTagMap.put(new Integer(0x0142), "Tile Width");
        exifTagMap.put(new Integer(0x0143), "Tile Length");
        exifTagMap.put(new Integer(0x0144), "Tile Offsets");
        exifTagMap.put(new Integer(0x0145), "Tile Byte Counts");
        exifTagMap.put(new Integer(0x014a), "Sub IFDs");
        exifTagMap.put(new Integer(0x015b), "JPEG Tables");
        exifTagMap.put(new Integer(0x0156), "Transfer Range");
        exifTagMap.put(new Integer(0x0200), "JPEG Proc");
        exifTagMap.put(new Integer(0x0201), "Thumbnail Offset");
        exifTagMap.put(new Integer(0x0202), "Thumbnail Length");
        exifTagMap.put(new Integer(0x0211), "YCbCr Coefficients");
        exifTagMap.put(new Integer(0x0212), "YCbCr Sub-Sampling");
        exifTagMap.put(new Integer(0x0213), "YCbCr Positioning");
        exifTagMap.put(new Integer(0x0214), "Reference Black/White");
        exifTagMap.put(new Integer(0x1000), "Related Image File Format");
        exifTagMap.put(new Integer(0x1001), "Related Image Width");
        exifTagMap.put(new Integer(0x1002), "Related Image Length");
        exifTagMap.put(new Integer(0x828D), "CFA Repeat Pattern Dim");
        exifTagMap.put(new Integer(0x828E), "CFA Pattern");
        exifTagMap.put(new Integer(0x828F), "Battery Level");
        exifTagMap.put(new Integer(0x8298), "Copyright");
        exifTagMap.put(new Integer(0x829A), "Exposure Time");
        exifTagMap.put(new Integer(0x829D), "F-Number");
        exifTagMap.put(new Integer(0x83BB), "IPTC/NAA");
        exifTagMap.put(new Integer(0x8769), "Exif Offset");
        exifTagMap.put(new Integer(0x8773), "Inter Color Profile");
        exifTagMap.put(new Integer(0x8822), "Exposure Program");
        exifTagMap.put(new Integer(0x8824), "Spectral Sensitivity");
        exifTagMap.put(new Integer(0x8825), "GPS Info");
        exifTagMap.put(new Integer(0x8827), "ISO Speed Ratings");
        exifTagMap.put(new Integer(0x8828), "OECF");
        exifTagMap.put(new Integer(0x8829), "Interlace");
        exifTagMap.put(new Integer(0x882a), "Time Zone Offset");
        exifTagMap.put(new Integer(0x882b), "Self Timer Mode");
        exifTagMap.put(new Integer(0x9000), "Exif Version");
        exifTagMap.put(new Integer(0x9003), "Date/Time Original");
        exifTagMap.put(new Integer(0x9004), "Date/Time Digitized");
        exifTagMap.put(new Integer(0x9101), "Components Configuration");
        exifTagMap.put(new Integer(0x9102), "Compressed Bits Per Pixel");
        exifTagMap.put(new Integer(0x9201), "Shutter Speed Value");
        exifTagMap.put(new Integer(0x9202), "Aperture Value");
        exifTagMap.put(new Integer(0x9203), "Brightness Value");
        exifTagMap.put(new Integer(0x9204), "Exposure Bias Value");
        exifTagMap.put(new Integer(0x9205), "Max Aperture Value");
        exifTagMap.put(new Integer(0x9206), "Subject Distance");
        exifTagMap.put(new Integer(0x9207), "Metering Mode");
        exifTagMap.put(new Integer(0x9208), "Light Source");
        exifTagMap.put(new Integer(0x9209), "Flash");
        exifTagMap.put(new Integer(0x920A), "Focal Length");
        exifTagMap.put(new Integer(0x920B), "Flash Energy");
        exifTagMap.put(new Integer(0x920c), "Spatial Frequency Response");
        exifTagMap.put(new Integer(0x920d), "Noise");
        exifTagMap.put(new Integer(0x9211), "Image Number");
        exifTagMap.put(new Integer(0x9212), "Security Classification");
        exifTagMap.put(new Integer(0x9213), "Image History");
        exifTagMap.put(new Integer(0x9214), "Subject Location");
        exifTagMap.put(new Integer(0x9215), "Exposure Index");
        exifTagMap.put(new Integer(0x9216), "TIFF/EP Standard ID");
        exifTagMap.put(new Integer(0x927C), "Maker Note");
        exifTagMap.put(new Integer(0x9286), "User Comment");
        exifTagMap.put(new Integer(0x9290), "Sub-Sec Time");
        exifTagMap.put(new Integer(0x9291), "Sub-Sec Time Original");
        exifTagMap.put(new Integer(0x9292), "Sub-Sec Time Digitized");
        exifTagMap.put(new Integer(0xA000), "FlashPix Version");
        exifTagMap.put(new Integer(0xA001), "Color Space");
        exifTagMap.put(new Integer(0xA002), "Exif Image Width");
        exifTagMap.put(new Integer(0xA003), "Exif Image Height");
        exifTagMap.put(new Integer(0xA004), "Related Sound File");
        exifTagMap.put(new Integer(0xA005), "Interoperability Offset");
        // 0x920B in TIFF/EP
        exifTagMap.put(new Integer(0xA20B), "Flash Energy");
        // 0x920C in TIFF/EP
        exifTagMap.put(new Integer(0xA20C), "Spatial Frequency Response");
        // 0x920E in TIFF/EP
        exifTagMap.put(new Integer(0xA20E), "Focal Plane X Resolution");
        // 0x920F in TIFF/EP
        exifTagMap.put(new Integer(0xA20F), "Focal Plane Y Resolution");
        // 0x9210 in TIFF/EP
        exifTagMap.put(new Integer(0xA210), "Focal Plane Resolution Unit");
        // 0x9214 in TIFF/EP
        exifTagMap.put(new Integer(0xA214), "Subject Location");
        // 0x9215 in TIFF/EP
        exifTagMap.put(new Integer(0xA215), "Exposure Index");
        // 0x9217 in TIFF/EP
        exifTagMap.put(new Integer(0xA217), "Sensing Method");
        exifTagMap.put(new Integer(0xA300), "File Source");
        exifTagMap.put(new Integer(0xA301), "Scene Type");
        exifTagMap.put(new Integer(0xA302), "CFA Pattern");
        tagNameMap.put(new Integer(IFD_EXIF), exifTagMap);

        // gps tags added by Colin Britton

        HashMap gpsTagMap = new HashMap();
        //GPS tag version GPSVersionID 0 0 BYTE 4
        gpsTagMap.put(new Integer(0x0000), "GPS Version ID");
        //North or South Latitude GPSLatitudeRef 1 1 ASCII 2
        gpsTagMap.put(new Integer(0x0001), "GPS Latitude Ref");
        //Latitude GPSLatitude 2 2 RATIONAL 3
        gpsTagMap.put(new Integer(0x0002), "GPS Latitude");
        //East or West Longitude GPSLongitudeRef 3 3 ASCII 2
        gpsTagMap.put(new Integer(0x0003), "GPS Longitude Ref");
        //Longitude GPSLongitude 4 4 RATIONAL 3
        gpsTagMap.put(new Integer(0x0004), "GPS Longitude");
        //Altitude reference GPSAltitudeRef 5 5 BYTE 1
        gpsTagMap.put(new Integer(0x0005), "GPS Altitude Ref");
        //Altitude GPSAltitude 6 6 RATIONAL 1
        gpsTagMap.put(new Integer(0x0006), "GPS Altitude");
        //GPS time (atomic clock) GPSTimeStamp 7 7 RATIONAL 3
        gpsTagMap.put(new Integer(0x0007), "GPS Time-Stamp");
        //GPS satellites used for measurement GPSSatellites 8 8 ASCII Any
        gpsTagMap.put(new Integer(0x0008), "GPS Satellites");
        //GPS receiver status GPSStatus 9 9 ASCII 2
        gpsTagMap.put(new Integer(0x0009), "GPS Status");
        //GPS measurement mode GPSMeasureMode 10 A ASCII 2
        gpsTagMap.put(new Integer(0x000A), "GPS Measure Mode");
        //Measurement precision GPSDOP 11 B RATIONAL 1
        gpsTagMap.put(new Integer(0x000B), "GPS DOP");
        //Speed unit GPSSpeedRef 12 C ASCII 2
        gpsTagMap.put(new Integer(0x000C), "GPS Speed Ref");
        //Speed of GPS receiver GPSSpeed 13 D RATIONAL 1
        gpsTagMap.put(new Integer(0x000D), "GPS Speed");
        //Reference for direction of movement GPSTrackRef 14 E ASCII 2
        gpsTagMap.put(new Integer(0x000E), "GPS Track Ref");
        //Direction of movement GPSTrack 15 F RATIONAL 1
        gpsTagMap.put(new Integer(0x000F), "GPS Track");
        //Reference for direction of image GPSImgDirectionRef 16 10 ASCII 2
        gpsTagMap.put(new Integer(0x0010), "GPS Img Direction Ref");
        //Direction of image GPSImgDirection 17 11 RATIONAL 1
        gpsTagMap.put(new Integer(0x0011), "GPS Img Direction");
        //Geodetic survey data used GPSMapDatum 18 12 ASCII Any
        gpsTagMap.put(new Integer(0x0012), "GPS Map Datum");
        //Reference for latitude of destination GPSDestLatitudeRef 19 13 ASCII 2
        gpsTagMap.put(new Integer(0x0013), "GPS Dest Latitude Ref");
        //Latitude of destination GPSDestLatitude 20 14 RATIONAL 3
        gpsTagMap.put(new Integer(0x0014), "GPS Dest Latitude");
        //Reference for longitude of destination GPSDestLongitudeRef 21 15 ASCII 2
        gpsTagMap.put(new Integer(0x0015), "GPS Dest Longitude Ref");
        //Longitude of destination GPSDestLongitude 22 16 RATIONAL 3
        gpsTagMap.put(new Integer(0x0016), "GPS Dest Longitude");
        //Reference for bearing of destination GPSDestBearingRef 23 17 ASCII 2
        gpsTagMap.put(new Integer(0x0017), "GPS Dest Bearing Ref");
        //Bearing of destination GPSDestBearing 24 18 RATIONAL 1
        gpsTagMap.put(new Integer(0x0018), "GPS Dest Bearing");
        //Reference for distance to destination GPSDestDistanceRef 25 19 ASCII 2
        gpsTagMap.put(new Integer(0x0019), "GPS Dest Distance Ref");
        //Distance to destination GPSDestDistance 26 1A RATIONAL 1
        gpsTagMap.put(new Integer(0x001A), "GPS Dest Distance");
        tagNameMap.put(new Integer(IFD_GPS), gpsTagMap);

        HashMap olympusMakernoteTagMap = new HashMap();
        olympusMakernoteTagMap.put(new Integer(TAG_OLYMPUS_SPECIAL_MODE), "Special Mode");
        olympusMakernoteTagMap.put(new Integer(TAG_OLYMPUS_JPEG_QUALITY), "Jpeg Quality");
        olympusMakernoteTagMap.put(new Integer(TAG_OLYMPUS_MACRO), "Macro");
        olympusMakernoteTagMap.put(new Integer(TAG_OLYMPUS_UNKNOWN_1), "Makernote Unknow n1");
        olympusMakernoteTagMap.put(new Integer(TAG_OLYMPUS_DIGI_ZOOM_RATIO), "DigiZoom Ratio");
        olympusMakernoteTagMap.put(new Integer(TAG_OLYMPUS_UNKNOWN_2), "Makernote Unknown 2");
        olympusMakernoteTagMap.put(new Integer(TAG_OLYMPUS_UNKNOWN_3), "Makernote Unknown 3");
        olympusMakernoteTagMap.put(new Integer(TAG_OLYMPUS_FIRMWARE_VERSION), "Firmware Version");
        olympusMakernoteTagMap.put(new Integer(TAG_OLYMPUS_PICT_INFO), "Pict Info");
        olympusMakernoteTagMap.put(new Integer(TAG_OLYMPUS_CAMERA_ID), "Camera Id");
        olympusMakernoteTagMap.put(new Integer(TAG_OLYMPUS_DATA_DUMP), "Data Dump");
        tagNameMap.put(new Integer(IFD_MAKERNOTE_OLYPMUS), olympusMakernoteTagMap);

        HashMap canonMakernoteTagMap = new HashMap();
        canonMakernoteTagMap.put(new Integer(TAG_CANON_CUSTOM_FUNCTIONS), "Custom Functions");
        canonMakernoteTagMap.put(new Integer(TAG_CANON_FIRMWARE_VERSION), "Firmware Version");
        canonMakernoteTagMap.put(new Integer(TAG_CANON_IMAGE_NUMBER), "Image Number");
        canonMakernoteTagMap.put(new Integer(TAG_CANON_IMAGE_TYPE), "Image Type");
        canonMakernoteTagMap.put(new Integer(TAG_CANON_OWNER_NAME), "Owner Name");
        canonMakernoteTagMap.put(new Integer(TAG_CANON_UNKNOWN_1), "Makernote Unknown 1");
        tagNameMap.put(new Integer(IFD_MAKERNOTE_CANON), canonMakernoteTagMap);

        HashMap nikonType1MakernoteTagMap = new HashMap();
        nikonType1MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE1_CCD_SENSITIVITY), "CCD Sensitivity");
        nikonType1MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE1_COLOR_MODE), "Color Mode");
        nikonType1MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE1_DIGITAL_ZOOM), "Digital Zoom");
        nikonType1MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE1_FISHEYE_CONVERTER), "Fisheye Converter");
        nikonType1MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE1_FOCUS), "Focus");
        nikonType1MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE1_IMAGE_ADJUSTMENT), "Image Adjustment");
        nikonType1MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE1_QUALITY), "Quality");
        nikonType1MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE1_UNKNOWN_1), "Makernote Unknown 1");
        nikonType1MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE1_UNKNOWN_2), "Makernote Unknown 2");
        nikonType1MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE1_UNKNOWN_3), "Makernote Unknown 3");
        nikonType1MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE1_WHITE_BALANCE), "White Balance");
        tagNameMap.put(new Integer(IFD_MAKERNOTE_NIKON_TYPE1), nikonType1MakernoteTagMap);

        HashMap nikonType2MakernoteTagMap = new HashMap();
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_ADAPTER), "Adapter");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_AF_FOCUS_POSITION), "AF Focus Position");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_COLOR_MODE), "Color Mode");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_DATA_DUMP), "Data Dump");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_DIGITAL_ZOOM), "Digital Zoom");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_FLASH_SETTING), "Flash Setting");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_FOCUS_MODE), "Focus Mode");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_IMAGE_ADJUSTMENT), "Image Adjustment");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_IMAGE_SHARPENING), "Image Sharpening");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_ISO_SELECTION), "ISO Selection");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_ISO_SETTING), "ISO Setting");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_MANUAL_FOCUS_DISTANCE), "Focus Distance");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_QUALITY), "Quality");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_1), "Makernote Unknown 1");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_UNKNOWN_2), "Makernote Unknown 2");
        nikonType2MakernoteTagMap.put(new Integer(TAG_NIKON_TYPE2_WHITE_BALANCE), "White Balance");
        tagNameMap.put(new Integer(IFD_MAKERNOTE_NIKON_TYPE2), nikonType2MakernoteTagMap);

        HashMap casioMakernoteTagMap = new HashMap();
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_CCD_SENSITIVITY), "CCD Sensitivity");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_CONTRAST), "Contrast");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_DIGITAL_ZOOM), "Digital Zoom");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_FLASH_INTENSITY), "Flash Intensity");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_FLASH_MODE), "Flash Mode");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_FOCUSING_MODE), "Focussing Mode");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_OBJECT_DISTANCE), "Object Distance");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_QUALITY), "Quality");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_RECORDING_MODE), "Recording Mode");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_SATURATION), "Saturation");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_SHARPNESS), "Sharpness");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_UNKNOWN_1), "Makernote Unknown 1");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_UNKNOWN_2), "Makernote Unknown 2");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_UNKNOWN_3), "Makernote Unknown 3");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_UNKNOWN_4), "Makernote Unknown 4");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_UNKNOWN_5), "Makernote Unknown 5");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_UNKNOWN_6), "Makernote Unknown 6");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_UNKNOWN_7), "Makernote Unknown 7");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_UNKNOWN_8), "Makernote Unknown 8");
        casioMakernoteTagMap.put(new Integer(TAG_CASIO_WHITE_BALANCE), "White Balance");
        tagNameMap.put(new Integer(IFD_MAKERNOTE_CASIO), casioMakernoteTagMap);

        HashMap fujiffilmMakernoteTagMap = new HashMap();
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_AE_WARNING), "AE Warning");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_BLUR_WARNING), "Blur Warning");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_COLOR), "Color");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_CONTINUOUS_TAKING_OR_AUTO_BRACKETTING), "Continuous Taking Or Auto Bracketting");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_FLASH_MODE), "Flash Mode");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_FOCUS_WARNING), "Focus Warning");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_MACRO), "Macro");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_MAKERNOTE_VERSION), "Makernote Version");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_PICTURE_MODE), "Picture Mode");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_QUALITY), "Quality");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_SHARPNESS), "Sharpness");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_SLOW_SYNCHRO), "Slow Synchro");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_TONE), "Tone");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_UNKNOWN_1), "Makernote Unknown 1");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_UNKNOWN_2), "Makernote Unknown 2");
        fujiffilmMakernoteTagMap.put(new Integer(TAG_FUJIFILM_WHITE_BALANCE), "White Balance");
        tagNameMap.put(new Integer(IFD_MAKERNOTE_FUJIFILM), fujiffilmMakernoteTagMap);
    }

    /**
     * Image attributes are stored here, hashed by the Integer representation of
     * the 'tagType'.
     */
    private HashMap directoryMap = new HashMap();

    /**
     * List of TagValue objects set against this object.  Keeping a list handy makes
     * creation of an Iterator and counting tags simple.
     */
    private final ArrayList definedTagList = new ArrayList();

    /**
     * Used when presenting tag values for rational values.
     */
    private boolean allowDecimalRepresentationOfRationals;

    /**
     * Creates a new instance of ImageInfo.  Package private.
     */
    public ImageInfo()
    {
        allowDecimalRepresentationOfRationals = false;
    }

    /**
     * Dictates whether rational values will be represented in decimal format in instances
     * where decimal notation is elegant (such as 1/2 -> 0.5, but not 1/3).
     * @param allowed true if decimal notation is desired
     */
    public void setAllowDecimalRepresentationOfRationals(boolean allowed)
    {
        this.allowDecimalRepresentationOfRationals = allowed;
    }

    /**
     * Sets an int value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag as an int
     */
    public void setInt(int directoryType, int tagType, int value)
    {
        addObject(directoryType, tagType, new Integer(value));
    }

    /**
     * Sets a double value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag as a double
     */
    public void setDouble(int directoryType, int tagType, double value)
    {
        addObject(directoryType, tagType, new Double(value));
    }

    /**
     * Sets a float value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag as a float
     */
    public void setFloat(int directoryType, int tagType, float value)
    {
        addObject(directoryType, tagType, new Float(value));
    }

    /**
     * Sets an int value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag as a String
     */
    public void setString(int directoryType, int tagType, String value)
    {
        addObject(directoryType, tagType, value);
    }

    /**
     * Sets an int value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag as a boolean
     */
    public void setBoolean(int directoryType, int tagType, boolean value)
    {
        addObject(directoryType, tagType, new Boolean(value));
    }

    /**
     * Sets a long value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag as a long
     */
    public void setLong(int directoryType, int tagType, long value)
    {
        addObject(directoryType, tagType, new Long(value));
    }

    /**
     * Sets a java.util.Date value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag as a java.util.Date
     */
    public void setDate(int directoryType, int tagType, java.util.Date value)
    {
        addObject(directoryType, tagType, value);
    }

    /**
     * Sets a Rational value for the specified Exif tag.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param numerator the rational number's numerator
     * @param denominator the rational number's denominator
     */
    public void setRational(int directoryType, int tagType, int numerator, int denominator)
    {
        addObject(directoryType, tagType, new Rational(numerator, denominator));
    }

    /**
     * Private helper method, containing common functionality for all 'add'
     * methods.
     * @param tagType the tag's value as an int (e.g. 0x010E)
     * @param value the value for the specified tag
     */
    private void addObject(int directoryType, int tagType, Object value)
    {
        if (value == null) {
            return;
        }

        Integer directory = new Integer(directoryType);
        HashMap directoryValueMap = (HashMap)directoryMap.get(directory);
        if (directoryValueMap == null) {
            directoryValueMap = new HashMap();
            directoryMap.put(directory, directoryValueMap);
        }
        Integer tag = new Integer(tagType);
        if (!directoryValueMap.containsKey(tag)) {
            definedTagList.add(new TagValue(directoryType, tagType, this));
        }
        directoryValueMap.put(tag, value);
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
    public boolean containsTag(int directoryType, int tagType)
    {
        HashMap directoryValueMap = (HashMap)directoryMap.get(new Integer(directoryType));
        if (directoryValueMap == null) {
            return false;
        }
        return directoryValueMap.containsKey(new Integer(tagType));
    }

    private Object getObject(int directoryType, int tagType)
    {
        HashMap directoryValueMap = (HashMap)directoryMap.get(new Integer(directoryType));
        if (directoryValueMap == null) {
            return null;
        }
        return directoryValueMap.get(new Integer(tagType));
    }

    /**
     * Returns the specified tag's value as an int, if possible.
     */
    public int getInt(int directoryType, int tagType)
    {
        Object o = getObject(directoryType, tagType);
        if (o == null) {
            throw new RuntimeException("Tag " + getTagName(directoryType, tagType) + " has not been set -- check using containsTag() first");
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
    public double getDouble(int directoryType, int tagType)
    {
        Object o = getObject(directoryType, tagType);
        if (o == null) {
            throw new RuntimeException("Tag " + getTagName(directoryType, tagType) + " has not been set -- check using containsTag() first");
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
    public float getFloat(int directoryType, int tagType)
    {
        Object o = getObject(directoryType, tagType);
        if (o == null) {
            throw new RuntimeException("Tag " + getTagName(directoryType, tagType) + " has not been set -- check using containsTag() first");
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
    public long getLong(int directoryType, int tagType)
    {
        Object o = getObject(directoryType, tagType);
        if (o == null) {
            throw new RuntimeException("Tag " + getTagName(directoryType, tagType) + " has not been set -- check using containsTag() first");
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
    public boolean getBoolean(int directoryType, int tagType)
    {
        Object o = getObject(directoryType, tagType);
        if (o == null) {
            throw new RuntimeException("Tag " + getTagName(directoryType, tagType) + " has not been set -- check using containsTag() first");
        } else if (o instanceof Boolean) {
            return ((Boolean)o).booleanValue();
        } else if (o instanceof String) {
            return Boolean.getBoolean((String)o);
        } else if (o instanceof Number) {
            return (((Number)o).doubleValue() != 0);
        } else {
            throw new RuntimeException("Requested tag cannot be cast to boolean");
        }
    }

    /**
     * Returns the specified tag's value as a java.util.Date, if possible.
     */
    public java.util.Date getDate(int directoryType, int tagType)
    {
        Object o = getObject(directoryType, tagType);
        if (o == null) {
            throw new RuntimeException("Tag " + getTagName(directoryType, tagType) + " has not been set -- check using containsTag() first");
        } else if (o instanceof java.util.Date) {
            return (java.util.Date)o;
        } else {
            throw new RuntimeException("Requested tag cannot be cast to java.util.Date");
        }
    }

    /**
     * Returns the specified tag's value as a Rational, if possible.
     */
    public Rational getRational(int directoryType, int tagType)
    {
        Object o = getObject(directoryType, tagType);
        if (o == null) {
            throw new RuntimeException("Tag " + getTagName(directoryType, tagType) + " has not been set -- check using containsTag() first");
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
    public String getString(int directoryType, int tagType)
    {
        Object o = getObject(directoryType, tagType);
        if (o == null) {
            return null;
        } else {
            return o.toString();
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
    public String getDescription(int directoryType, int tagType)
    {
        if (!containsTag(directoryType, tagType)) {
            return null;
        }

        // TODO this is begging for some refactoring

        switch (directoryType) {
            case IFD_GPS:
                switch (tagType) {
                    case TAG_GPS_ALTITUDE:
                        return getGpsAltitudeDescription();
                    case TAG_GPS_ALTITUDE_REF:
                        return getGpsAltitudeRefDescription();
                    case TAG_GPS_STATUS:
                        return getGpsStatusDescription();
                    case TAG_GPS_MEASURE_MODE:
                        return getGpsMeasureModeDescription();
                    case TAG_GPS_SPEED_REF:
                        return getGpsSpeedRefDescription();
                    case TAG_GPS_TRACK_REF:
                    case TAG_GPS_IMG_DIRECTION_REF:
                    case TAG_GPS_DEST_BEARING_REF:
                        return getGpsDirectionReferenceDescription(tagType);
                    case TAG_GPS_TRACK:
                    case TAG_GPS_IMG_DIRECTION:
                    case TAG_GPS_DEST_BEARING:
                        return getGpsDirectionDescription(tagType);
                    case TAG_GPS_DEST_DISTANCE_REF:
                        return getGpsDestinationReferenceDescription();
                    default:
                        return getString(directoryType, tagType);
                }
            case IFD_INTEROP:
                switch (tagType) {
                    case TAG_INTEROP_INDEX:
                        return getInteropIndexDescription();
                    default:
                        return getString(directoryType, tagType);
                }
            case IFD_EXIF:
                switch (tagType) {
                    case TAG_ORIENTATION:
                        return getOrientationDescription();
                    case TAG_RESOLUTION_UNIT:
                        return getResolutionDescription();
                    case TAG_YCBCR_POSITIONING:
                        return getYCbCrPositioningDescription();
                    case TAG_EXPOSURE_TIME:
                        return getExposureTimeDescription();
                    case TAG_SHUTTER_SPEED:
                        return getShutterSpeedDescription();
                    case TAG_FNUMBER:
                        return getFNumberDescription();
                    case TAG_X_RESOLUTION:
                        return getXResolutionDescription();
                    case TAG_Y_RESOLUTION:
                        return getYResolutionDescription();
                    case TAG_THUMBNAIL_OFFSET:
                        return getThumbnailOffsetDescription();
                    case TAG_THUMBNAIL_LENGTH:
                        return getThumbnailLengthDescription();
                    case TAG_COMPRESSION_LEVEL:
                        return getCompressionLevelDescription();
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
                    case TAG_FOCAL_PLANE_UNIT:
                        return getFocalPlaneResolutionUnitDescription();
                    case TAG_FOCAL_PLANE_X_RES:
                        return getFocalPlaneXResolutionDescription();
                    case TAG_FOCAL_PLANE_Y_RES:
                        return getFocalPlaneYResolutionDescription();
                    case TAG_THUMBNAIL_IMAGE_WIDTH:
                        return getThumbnailImageWidthDescription();
                    case TAG_THUMBNAIL_IMAGE_HEIGHT:
                        return getThumbnailImageHeightDescription();
                    case TAG_BITS_PER_SAMPLE:
                        return getBitsPerSampleDescription();
                    case TAG_COMPRESSION:
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
                    default:
                        return getString(directoryType, tagType);
                }
            default:
                return getString(directoryType, tagType);
        }
    }

    private String getInteropIndexDescription()
    {
        if (!containsTag(IFD_INTEROP, TAG_INTEROP_INDEX)) return null;
        String interopIndex = getString(IFD_INTEROP, TAG_INTEROP_INDEX).trim();
        if ("R98".equalsIgnoreCase(interopIndex)) {
            return "Recommended Exif Interoperability Rules (ExifR98)";
        } else {
            return "Unknown (" + interopIndex + ")";
        }
    }

    private String getGpsDestinationReferenceDescription()
    {
        if (!containsTag(IFD_GPS, TAG_GPS_DEST_DISTANCE_REF)) return null;
        String destRef = getString(IFD_GPS, TAG_GPS_DEST_DISTANCE_REF).trim();
        if ("K".equalsIgnoreCase(destRef)) {
            return "kilometers";
        } else if ("M".equalsIgnoreCase(destRef)) {
            return "miles";
        } else if ("N".equalsIgnoreCase(destRef)) {
            return "knots";
        } else {
            return "Unknown (" + destRef + ")";
        }
    }

    private String getGpsDirectionDescription(int tagType)
    {
        if (!containsTag(IFD_GPS, tagType)) return null;
        String gpsDirection = getString(IFD_GPS, tagType).trim();
        return gpsDirection + " degrees";
    }

    private String getGpsDirectionReferenceDescription(int tagType)
    {
        if (!containsTag(IFD_GPS, tagType)) return null;
        String gpsDistRef = getString(IFD_GPS, tagType).trim();
        if ("T".equalsIgnoreCase(gpsDistRef)) {
            return "True direction";
        } else if ("M".equalsIgnoreCase(gpsDistRef)) {
            return "Magnetic direction";
        } else {
            return "Unknown (" + gpsDistRef + ")";
        }
    }

    private String getGpsSpeedRefDescription()
    {
        if (!containsTag(IFD_GPS, TAG_GPS_SPEED_REF)) return null;
        String gpsSpeedRef = getString(IFD_GPS, TAG_GPS_SPEED_REF).trim();
        if ("K".equalsIgnoreCase(gpsSpeedRef)) {
            return "kph";
        } else if ("M".equalsIgnoreCase(gpsSpeedRef)) {
            return "mph";
        } else if ("N".equalsIgnoreCase(gpsSpeedRef)) {
            return "knots";
        } else {
            return "Unknown (" + gpsSpeedRef + ")";
        }
    }

    private String getGpsMeasureModeDescription()
    {
        if (!containsTag(IFD_GPS, TAG_GPS_MEASURE_MODE)) return null;
        String gpsSpeedMeasureMode = getString(IFD_GPS, TAG_GPS_MEASURE_MODE).trim();
        if ("2".equalsIgnoreCase(gpsSpeedMeasureMode)) {
            return "2-dimensional measurement";
        } else if ("3".equalsIgnoreCase(gpsSpeedMeasureMode)) {
            return "3-dimensional measurement";
        } else {
            return "Unknown (" + gpsSpeedMeasureMode + ")";
        }
    }

    private String getGpsStatusDescription()
    {
        if (!containsTag(IFD_GPS, TAG_GPS_STATUS)) return null;
        String gpsStatus = getString(IFD_GPS, TAG_GPS_STATUS).trim();
        if ("A".equalsIgnoreCase(gpsStatus)) {
            return "Measurement in progess";
        } else if ("V".equalsIgnoreCase(gpsStatus)) {
            return "Measurement Interoperability";
        } else {
            return "Unknown (" + gpsStatus + ")";
        }
    }

    private String getGpsAltitudeRefDescription()
    {
        if (!containsTag(IFD_GPS, TAG_GPS_ALTITUDE_REF)) return null;
        int alititudeRef = getInt(IFD_GPS, TAG_GPS_ALTITUDE_REF);
        if (alititudeRef == 0) {
            return "Sea level";
        } else {
            return "Unknown (" + alititudeRef + ")";
        }
    }

    private String getGpsAltitudeDescription()
    {
        if (!containsTag(IFD_GPS, TAG_GPS_ALTITUDE)) return null;
        String alititude = getRational(IFD_GPS, TAG_GPS_ALTITUDE).toSimpleString(true);
        return alititude + " metres";
    }

    private String getSceneTypeDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_SCENE_TYPE)) return null;
        int sceneType = getInt(IFD_EXIF, TAG_SCENE_TYPE);
        if (sceneType == 1) {
            return "Directly photographed image";
        } else {
            return "Unknown (" + sceneType + ")";
        }
    }

    private String getFileSourceDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_FILE_SOURCE)) return null;
        int fileSource = getInt(IFD_EXIF, TAG_FILE_SOURCE);
        if (fileSource == 3) {
            return "Digital Still Camera (DSC)";
        } else {
            return "Unknown (" + fileSource + ")";
        }
    }

    private String getExposureBiasDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_EXPOSURE_BIAS)) return null;
        Rational exposureBias = getRational(IFD_EXIF, TAG_EXPOSURE_BIAS);
        return exposureBias.toSimpleString(true);
    }

    private String getMaxApertureValueDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_MAX_APERTURE)) return null;
        double apertureApex = getDouble(IFD_EXIF, TAG_MAX_APERTURE);
        double rootTwo = Math.sqrt(2);
        double fStop = Math.pow(rootTwo, apertureApex);
        java.text.DecimalFormat formatter = new DecimalFormat("0.#");
        return "F" + formatter.format(fStop);
    }

    private String getApertureValueDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_APERTURE)) return null;
        double apertureApex = getDouble(IFD_EXIF, TAG_APERTURE);
        double rootTwo = Math.sqrt(2);
        double fStop = Math.pow(rootTwo, apertureApex);
        java.text.DecimalFormat formatter = new DecimalFormat("0.#");
        return "F" + formatter.format(fStop);
    }

    private String getExposureProgramDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_EXPOSURE_PROGRAM)) return null;
        // '1' means manual control, '2' program normal, '3' aperture priority,
        // '4' shutter priority, '5' program creative (slow program),
        // '6' program action(high-speed program), '7' portrait mode, '8' landscape mode.
        switch (this.getInt(IFD_EXIF, TAG_EXPOSURE_PROGRAM)) {
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
                return "Unknown program (" + this.getInt(IFD_EXIF, TAG_EXPOSURE_PROGRAM) + ")";
        }
    }

    private String getYCbCrSubsamplingDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_YCBCR_SUBSAMPLING)) return null;
        return getString(IFD_EXIF, TAG_YCBCR_SUBSAMPLING) + " samples";
    }

    private String getPlanarConfigurationDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_PLANAR_CONFIGURATION)) return null;
        // When image format is no compression YCbCr, this value shows byte aligns of YCbCr
        // data. If value is '1', Y/Cb/Cr value is chunky format, contiguous for each subsampling
        // pixel. If value is '2', Y/Cb/Cr value is separated and stored to Y plane/Cb plane/Cr
        // plane format.

        switch (this.getInt(IFD_EXIF, TAG_PLANAR_CONFIGURATION)) {
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
        if (!containsTag(IFD_EXIF, TAG_SAMPLES_PER_PIXEL)) return null;
        return getString(IFD_EXIF, TAG_SAMPLES_PER_PIXEL) + " samples/pixel";
    }

    private String getRowsPerStripDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_ROWS_PER_STRIP)) return null;
        return getString(IFD_EXIF, TAG_ROWS_PER_STRIP) + " rows/strip";
    }

    private String getStripByteCountsDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_STRIP_BYTE_COUNTS)) return null;
        return getString(IFD_EXIF, TAG_STRIP_BYTE_COUNTS) + " bytes";
    }

    private String getPhotometricInterpretationDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_COMPRESSION)) return null;
        // Shows the color space of the image data components. '1' means monochrome,
        // '2' means RGB, '6' means YCbCr.
        switch (this.getInt(IFD_EXIF, TAG_COMPRESSION)) {
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

    private String getCompressionDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_COMPRESSION)) return null;
        // '1' means no compression, '6' means JPEG compression.
        switch (this.getInt(IFD_EXIF, TAG_COMPRESSION)) {
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
        if (!containsTag(IFD_EXIF, TAG_BITS_PER_SAMPLE)) return null;
        return getString(IFD_EXIF, TAG_BITS_PER_SAMPLE) + " bits/component/pixel";
    }

    private String getThumbnailImageWidthDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_THUMBNAIL_IMAGE_WIDTH)) return null;
        return getString(IFD_EXIF, TAG_THUMBNAIL_IMAGE_WIDTH) + " pixels";
    }

    private String getThumbnailImageHeightDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_THUMBNAIL_IMAGE_HEIGHT)) return null;
        return getString(IFD_EXIF, TAG_THUMBNAIL_IMAGE_HEIGHT) + " pixels";
    }

    private String getFocalPlaneXResolutionDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_FOCAL_PLANE_X_RES)) return null;
        Rational rational = getRational(IFD_EXIF, TAG_FOCAL_PLANE_X_RES);
        return rational.getReciprocal().toSimpleString(allowDecimalRepresentationOfRationals) + " " +
                getFocalPlaneResolutionUnitDescription().toLowerCase();
    }

    private String getFocalPlaneYResolutionDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_COMPRESSION)) return null;
        Rational rational = getRational(IFD_EXIF, TAG_FOCAL_PLANE_Y_RES);
        return rational.getReciprocal().toSimpleString(allowDecimalRepresentationOfRationals) + " " +
                getFocalPlaneResolutionUnitDescription().toLowerCase();
    }

    private String getFocalPlaneResolutionUnitDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_FOCAL_PLANE_UNIT)) return null;
        // Unit of FocalPlaneXResoluton/FocalPlaneYResolution. '1' means no-unit,
        // '2' inch, '3' centimeter.
        switch (this.getInt(IFD_EXIF, TAG_FOCAL_PLANE_UNIT)) {
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

    private String getExifImageWidthDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_EXIF_IMAGE_WIDTH)) return null;
        return getInt(IFD_EXIF, TAG_EXIF_IMAGE_WIDTH) + " pixels";
    }

    private String getExifImageHeightDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_EXIF_IMAGE_HEIGHT)) return null;
        return getInt(IFD_EXIF, TAG_EXIF_IMAGE_HEIGHT) + " pixels";
    }

    private String getColorSpaceDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_COLOR_SPACE)) return null;
        int colorSpace = getInt(IFD_EXIF, TAG_COLOR_SPACE);
        if (colorSpace == 1) {
            return "sRGB";
        } else if (colorSpace == 65535) {
            return "Undefined";
        } else {
            return "Unknown";
        }
    }

    private String getFocalLengthDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_FOCAL_LENGTH)) return null;
        java.text.DecimalFormat formatter = new DecimalFormat("0.0##");
        Rational focalLength = getRational(IFD_EXIF, TAG_FOCAL_LENGTH);
        return formatter.format(focalLength.doubleValue()) + " mm";
    }

    private String getFlashDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_FLASH)) return null;
        // '0' means flash did not fire, '1' flash fired, '5' flash fired but strobe return
        // light not detected, '7' flash fired and strobe return light detected.
        switch (this.getInt(IFD_EXIF, TAG_FLASH)) {
            case 0:
                return "No flash fired";
            case 1:
                return "Flash fired";
            case 5:
                return "Flash fired but strobe return light not detected";
            case 7:
                return "flash fired and strobe return light detected";
            default:
                return "Unknown (" + this.getInt(IFD_EXIF, TAG_FLASH) + ")";
        }
    }

    private String getWhiteBalanceDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_WHITE_BALANCE)) return null;
        // '0' means unknown, '1' daylight, '2' fluorescent, '3' tungsten, '10' flash,
        // '17' standard light A, '18' standard light B, '19' standard light C, '20' D55,
        // '21' D65, '22' D75, '255' other.
        switch (this.getInt(IFD_EXIF, TAG_WHITE_BALANCE)) {
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
                return "Unknown (" + this.getInt(IFD_EXIF, TAG_WHITE_BALANCE) + ")";
        }
    }

    private String getMeteringModeDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_METERING_MODE)) return null;
        // '0' means unknown, '1' average, '2' center weighted average, '3' spot
        // '4' multi-spot, '5' multi-segment, '6' partial, '255' other
        int meteringMode = this.getInt(IFD_EXIF, TAG_METERING_MODE);
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

    private String getSubjectDistanceDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_SUBJECT_DISTANCE)) return null;
        Rational distance = getRational(IFD_EXIF, TAG_SUBJECT_DISTANCE);
        java.text.DecimalFormat formatter = new DecimalFormat("0.0##");
        return formatter.format(distance.doubleValue()) + " metres";
    }

    private String getCompressionLevelDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_COMPRESSION_LEVEL)) return null;
        Rational compressionRatio = getRational(IFD_EXIF, TAG_COMPRESSION_LEVEL);
        String ratio = compressionRatio.toSimpleString(allowDecimalRepresentationOfRationals);
        if (compressionRatio.isInteger() && compressionRatio.intValue() == 1) {
            return ratio + " bit/pixel";
        } else {
            return ratio + " bits/pixel";
        }
    }

    private String getThumbnailLengthDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_THUMBNAIL_LENGTH)) return null;
        return getString(IFD_EXIF, TAG_THUMBNAIL_LENGTH) + " bytes";
    }

    private String getThumbnailOffsetDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_THUMBNAIL_OFFSET)) return null;
        return getString(IFD_EXIF, TAG_THUMBNAIL_OFFSET) + " bytes";
    }

    private String getYResolutionDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_Y_RESOLUTION)) return null;
        Rational inverseResolution = getRational(IFD_EXIF, TAG_Y_RESOLUTION);
        return inverseResolution.getReciprocal().toSimpleString(allowDecimalRepresentationOfRationals) + " " +
                getResolutionDescription().toLowerCase();
    }

    private String getXResolutionDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_X_RESOLUTION)) return null;
        Rational inverseResolution = getRational(IFD_EXIF, TAG_X_RESOLUTION);
        return inverseResolution.getReciprocal().toSimpleString(allowDecimalRepresentationOfRationals) + " " +
                getResolutionDescription().toLowerCase();
    }

    private String getExposureTimeDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_EXPOSURE_TIME)) return null;
        return getString(IFD_EXIF, TAG_EXPOSURE_TIME) + " sec";
    }

    private String getShutterSpeedDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_SHUTTER_SPEED)) return null;
        // Incorrect math bug fixed by Hendrik Wördehoff - 20 Sep 2002
        int apexValue = getInt(IFD_EXIF, TAG_SHUTTER_SPEED);
        int apexPower = (int)(Math.pow(2.0, apexValue) + 0.5);
        return "1/" + apexPower + " sec";
    }

    private String getFNumberDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_FNUMBER)) return null;
        Rational fNumber = getRational(IFD_EXIF, TAG_FNUMBER);
        java.text.DecimalFormat formatter = new DecimalFormat("0.#");
        return "F" + formatter.format(fNumber.doubleValue());
    }

    private String getYCbCrPositioningDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_YCBCR_POSITIONING)) return null;
        int yCbCrPosition = this.getInt(IFD_EXIF, TAG_YCBCR_POSITIONING);
        switch (yCbCrPosition) {
            case 1:
                return "Center of pixel array";
            case 2:
                return "Datum point";
            default:
                return String.valueOf(yCbCrPosition);
        }
    }

    private String getOrientationDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_ORIENTATION)) return null;
        int orientation = this.getInt(IFD_EXIF, TAG_ORIENTATION);
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

    private String getResolutionDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_RESOLUTION_UNIT)) return null;
        // '1' means no-unit, '2' means inch, '3' means centimeter. Default value is '2'(inch)
        int resolutionUnit = this.getInt(IFD_EXIF, TAG_RESOLUTION_UNIT);
        switch (resolutionUnit) {
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

    private String getSensingMethodDescription()
    {
        if (!containsTag(IFD_EXIF, TAG_SENSING_METHOD)) return null;
        // '1' Not defined, '2' One-chip color area sensor, '3' Two-chip color area sensor
        // '4' Three-chip color area sensor, '5' Color sequential area sensor
        // '7' Trilinear sensor '8' Color sequential linear sensor,  'Other' reserved
        int sensingMethod = this.getInt(IFD_EXIF, TAG_SENSING_METHOD);
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

    public static String getTagName(int directoryType, int tagType)
    {
        HashMap tagsInDirectory = (HashMap)tagNameMap.get(new Integer(directoryType));
        if (tagsInDirectory == null) {
            return null;
        }
        String tagName = (String)tagsInDirectory.get(new Integer(tagType));
        if (tagName == null) {
            return "Unknown tag (" + getDirectoryName(directoryType) +
                    " 0x" + Integer.toHexString(tagType) + ")";
        } else {
            return tagName;
        }
    }

    public static String getDirectoryName(int directoryType)
    {
        return (String)directoryNameMap.get(new Integer(directoryType));
    }
}

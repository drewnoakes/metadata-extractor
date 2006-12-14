/*
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
 * Created by dnoakes on 27-Nov-2002 10:10:47 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * The Olympus makernote is used by many manufacturers (Konica, Minolta and Epson...), and as such contains some tags
 * that appear specific to those manufacturers. 
 */
public class OlympusMakernoteDirectory extends Directory
{
    /**
     * Used by Konica / Minolta cameras.
     */
    public static final int TAG_OLYMPUS_MAKERNOTE_VERSION = 0x0000;

    /**
     * Used by Konica / Minolta cameras.
     */
    public static final int TAG_OLYMPUS_CAMERA_SETTINGS_1 = 0x0001;

    /**
     * Alternate Camera Settings Tag. Used by Konica / Minolta cameras.
     */
    public static final int TAG_OLYMPUS_CAMERA_SETTINGS_2 = 0x0003;

    /**
     * Used by Konica / Minolta cameras.
     */
    public static final int TAG_OLYMPUS_COMPRESSED_IMAGE_SIZE = 0x0040;

    /**
     * Used by Konica / Minolta cameras.
     */
    public static final int TAG_OLYMPUS_MINOLTA_THUMBNAIL_OFFSET_1 = 0x0081;

    /**
     * Alternate Thumbnail Offset. Used by Konica / Minolta cameras.
     */
    public static final int TAG_OLYMPUS_MINOLTA_THUMBNAIL_OFFSET_2 = 0x0088;

    /**
     * Length of thumbnail in bytes. Used by Konica / Minolta cameras.
     */
    public static final int TAG_OLYMPUS_MINOLTA_THUMBNAIL_LENGTH = 0x0089;

    /**
     * Used by Konica / Minolta cameras
     * 0 = Natural Colour
     * 1 = Black & White
     * 2 = Vivid colour
     * 3 = Solarization
     * 4 = AdobeRGB
     */
    public static final int TAG_OLYMPUS_COLOUR_MODE = 0x0101;

    /**
     * Used by Konica / Minolta cameras.
     * 0 = Raw
     * 1 = Super Fine
     * 2 = Fine
     * 3 = Standard
     * 4 = Extra Fine
     */
    public static final int TAG_OLYMPUS_IMAGE_QUALITY_1 = 0x0102;

    /**
     * Not 100% sure about this tag.
     *
     * Used by Konica / Minolta cameras.
     * 0 = Raw
     * 1 = Super Fine
     * 2 = Fine
     * 3 = Standard
     * 4 = Extra Fine
     */
    public static final int TAG_OLYMPUS_IMAGE_QUALITY_2 = 0x0103;


    /**
     * Three values:
     * Value 1: 0=Normal, 2=Fast, 3=Panorama
     * Value 2: Sequence Number Value 3:
     * 1 = Panorama Direction: Left to Right
     * 2 = Panorama Direction: Right to Left
     * 3 = Panorama Direction: Bottom to Top
     * 4 = Panorama Direction: Top to Bottom
     */
    public static final int TAG_OLYMPUS_SPECIAL_MODE = 0x0200;

    /**
     * 1 = Standard Quality
     * 2 = High Quality
     * 3 = Super High Quality
     */
    public static final int TAG_OLYMPUS_JPEG_QUALITY = 0x0201;

    /**
     * 0 = Normal (Not Macro)
     * 1 = Macro
     */
    public static final int TAG_OLYMPUS_MACRO_MODE = 0x0202;

    /**
     *
     */
    public static final int TAG_OLYMPUS_UNKNOWN_1 = 0x0203;

    /**
     * Zoom Factor (0 or 1 = normal)
     */
    public static final int TAG_OLYMPUS_DIGI_ZOOM_RATIO = 0x0204;

    /**
     *
     */
    public static final int TAG_OLYMPUS_UNKNOWN_2 = 0x0205;

    /**
     *
     */
    public static final int TAG_OLYMPUS_UNKNOWN_3 = 0x0206;

    /**
     *
     */
    public static final int TAG_OLYMPUS_FIRMWARE_VERSION = 0x0207;

    /**
     *
     */
    public static final int TAG_OLYMPUS_PICT_INFO = 0x0208;

    /**
     *
     */
    public static final int TAG_OLYMPUS_CAMERA_ID = 0x0209;

    /**
     * Used by Epson cameras
     * Units = pixels
     */
    public static final int TAG_OLYMPUS_IMAGE_WIDTH = 0x020B;

    /**
     * Used by Epson cameras
     * Units = pixels
     */
    public static final int TAG_OLYMPUS_IMAGE_HEIGHT = 0x020C;

    /**
     * A string. Used by Epson cameras.
     */
    public static final int TAG_OLYMPUS_ORIGINAL_MANUFACTURER_MODEL = 0x020D;

    /**
     * See the PIM specification here:
     * http://www.ozhiker.com/electronics/pjmt/jpeg_info/pim.html
     */
    public static final int TAG_OLYMPUS_PRINT_IMAGE_MATCHING_INFO = 0x0E00;

    /**
     *
     */
    public static final int TAG_OLYMPUS_DATA_DUMP = 0x0F00;

    /**
     *
     */
    public static final int TAG_OLYMPUS_FLASH_MODE = 0x1004;

    /**
     *
     */
    public static final int TAG_OLYMPUS_BRACKET = 0x1006;

    /**
     *
     */
    public static final int TAG_OLYMPUS_FOCUS_MODE = 0x100B;

    /**
     *
     */
    public static final int TAG_OLYMPUS_FOCUS_DISTANCE = 0x100C;

    /**
     *
     */
    public static final int TAG_OLYMPUS_ZOOM = 0x100D;

    /**
     *
     */
    public static final int TAG_OLYMPUS_MACRO_FOCUS = 0x100E;

    /**
     *
     */
    public static final int TAG_OLYMPUS_SHARPNESS = 0x100F;

    /**
     *
     */
    public static final int TAG_OLYMPUS_COLOUR_MATRIX = 0x1011;

    /**
     *
     */
    public static final int TAG_OLYMPUS_BLACK_LEVEL = 0x1012;

    /**
     *
     */
    public static final int TAG_OLYMPUS_WHITE_BALANCE = 0x1015;

    /**
     *
     */
    public static final int TAG_OLYMPUS_RED_BIAS = 0x1017;

    /**
     *
     */
    public static final int TAG_OLYMPUS_BLUE_BIAS = 0x1018;

    /**
     *
     */
    public static final int TAG_OLYMPUS_SERIAL_NUMBER = 0x101A;

    /**
     *
     */
    public static final int TAG_OLYMPUS_FLASH_BIAS = 0x1023;

    /**
     *
     */
    public static final int TAG_OLYMPUS_CONTRAST = 0x1029;

    /**
     *
     */
    public static final int TAG_OLYMPUS_SHARPNESS_FACTOR = 0x102A;

    /**
     *
     */
    public static final int TAG_OLYMPUS_COLOUR_CONTROL = 0x102B;

    /**
     *
     */
    public static final int TAG_OLYMPUS_VALID_BITS = 0x102C;

    /**
     *
     */
    public static final int TAG_OLYMPUS_CORING_FILTER = 0x102D;

    /**
     *
     */
    public static final int TAG_OLYMPUS_FINAL_WIDTH = 0x102E;

    /**
     *
     */
    public static final int TAG_OLYMPUS_FINAL_HEIGHT = 0x102F;

    /**
     *
     */
    public static final int TAG_OLYMPUS_COMPRESSION_RATIO = 0x1034;

    protected static final HashMap tagNameMap = new HashMap();

    static
    {
        tagNameMap.put(new Integer(TAG_OLYMPUS_SPECIAL_MODE), "Special Mode");
        tagNameMap.put(new Integer(TAG_OLYMPUS_JPEG_QUALITY), "Jpeg Quality");
        tagNameMap.put(new Integer(TAG_OLYMPUS_MACRO_MODE), "Macro");
        tagNameMap.put(new Integer(TAG_OLYMPUS_UNKNOWN_1), "Makernote Unknown 1");
        tagNameMap.put(new Integer(TAG_OLYMPUS_DIGI_ZOOM_RATIO), "DigiZoom Ratio");
        tagNameMap.put(new Integer(TAG_OLYMPUS_UNKNOWN_2), "Makernote Unknown 2");
        tagNameMap.put(new Integer(TAG_OLYMPUS_UNKNOWN_3), "Makernote Unknown 3");
        tagNameMap.put(new Integer(TAG_OLYMPUS_FIRMWARE_VERSION), "Firmware Version");
        tagNameMap.put(new Integer(TAG_OLYMPUS_PICT_INFO), "Pict Info");
        tagNameMap.put(new Integer(TAG_OLYMPUS_CAMERA_ID), "Camera Id");
        tagNameMap.put(new Integer(TAG_OLYMPUS_DATA_DUMP), "Data Dump");
        tagNameMap.put(new Integer(TAG_OLYMPUS_MAKERNOTE_VERSION), "Makernote Version");
        tagNameMap.put(new Integer(TAG_OLYMPUS_CAMERA_SETTINGS_1), "Camera Settings");
        tagNameMap.put(new Integer(TAG_OLYMPUS_CAMERA_SETTINGS_2), "Camera Settings");
        tagNameMap.put(new Integer(TAG_OLYMPUS_COMPRESSED_IMAGE_SIZE), "Compressed Image Size");
        tagNameMap.put(new Integer(TAG_OLYMPUS_MINOLTA_THUMBNAIL_OFFSET_1), "Thumbnail Offset");
        tagNameMap.put(new Integer(TAG_OLYMPUS_MINOLTA_THUMBNAIL_OFFSET_2), "Thumbnail Offset");
        tagNameMap.put(new Integer(TAG_OLYMPUS_MINOLTA_THUMBNAIL_LENGTH), "Thumbnail Length");
        tagNameMap.put(new Integer(TAG_OLYMPUS_COLOUR_MODE), "Colour Mode");
        tagNameMap.put(new Integer(TAG_OLYMPUS_IMAGE_QUALITY_1), "Image Quality");
        tagNameMap.put(new Integer(TAG_OLYMPUS_IMAGE_QUALITY_2), "Image Quality");
        tagNameMap.put(new Integer(TAG_OLYMPUS_IMAGE_HEIGHT), "Image Height");
        tagNameMap.put(new Integer(TAG_OLYMPUS_ORIGINAL_MANUFACTURER_MODEL), "Original Manufacturer Model");
        tagNameMap.put(new Integer(TAG_OLYMPUS_PRINT_IMAGE_MATCHING_INFO), "Print Image Matching (PIM) Info");
        tagNameMap.put(new Integer(TAG_OLYMPUS_FLASH_MODE), "Flash Mode");
        tagNameMap.put(new Integer(TAG_OLYMPUS_BRACKET), "Bracket");
        tagNameMap.put(new Integer(TAG_OLYMPUS_FOCUS_MODE), "Focus Mode");
        tagNameMap.put(new Integer(TAG_OLYMPUS_FOCUS_DISTANCE), "Focus Distance");
        tagNameMap.put(new Integer(TAG_OLYMPUS_ZOOM), "Zoom");
        tagNameMap.put(new Integer(TAG_OLYMPUS_MACRO_FOCUS), "Macro Focus");
        tagNameMap.put(new Integer(TAG_OLYMPUS_SHARPNESS), "Sharpness");
        tagNameMap.put(new Integer(TAG_OLYMPUS_COLOUR_MATRIX), "Colour Matrix");
        tagNameMap.put(new Integer(TAG_OLYMPUS_BLACK_LEVEL), "Black Level");
        tagNameMap.put(new Integer(TAG_OLYMPUS_WHITE_BALANCE), "White Balance");
        tagNameMap.put(new Integer(TAG_OLYMPUS_RED_BIAS), "Red Bias");
        tagNameMap.put(new Integer(TAG_OLYMPUS_BLUE_BIAS), "Blue Bias");
        tagNameMap.put(new Integer(TAG_OLYMPUS_SERIAL_NUMBER), "Serial Number");
        tagNameMap.put(new Integer(TAG_OLYMPUS_FLASH_BIAS), "Flash Bias");
        tagNameMap.put(new Integer(TAG_OLYMPUS_CONTRAST), "Contrast");
        tagNameMap.put(new Integer(TAG_OLYMPUS_SHARPNESS_FACTOR), "Sharpness Factor");
        tagNameMap.put(new Integer(TAG_OLYMPUS_COLOUR_CONTROL), "Colour Control");
        tagNameMap.put(new Integer(TAG_OLYMPUS_VALID_BITS), "Valid Bits");
        tagNameMap.put(new Integer(TAG_OLYMPUS_CORING_FILTER), "Coring Filter");
        tagNameMap.put(new Integer(TAG_OLYMPUS_FINAL_WIDTH), "Final Width");
        tagNameMap.put(new Integer(TAG_OLYMPUS_FINAL_HEIGHT), "Final Height");
        tagNameMap.put(new Integer(TAG_OLYMPUS_COMPRESSION_RATIO), "Compression Ratio");
    }

    public OlympusMakernoteDirectory()
    {
        this.setDescriptor(new OlympusMakernoteDescriptor(this));
    }

    public String getName()
    {
        return "Olympus Makernote";
    }

    protected HashMap getTagNameMap()
    {
        return tagNameMap;
    }
}

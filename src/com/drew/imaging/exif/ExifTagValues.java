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
    public static final int TAG_EXIF_OFFSET = 0x8769;
    public static final int TAG_INTEROP_OFFSET = 0xa005;

    public static final int TAG_MAKE = 0x010F;
    public static final int TAG_MODEL = 0x0110;

    public static final int TAG_EXPOSURETIME = 0x829A;
    public static final int TAG_FNUMBER = 0x829D;

    public static final int TAG_SHUTTERSPEED = 0x9201;
    public static final int TAG_APERTURE = 0x9202;
    public static final int TAG_MAXAPERTURE = 0x9205;
    public static final int TAG_FOCALLENGTH = 0x920A;

    public static final int TAG_DATETIME_ORIGINAL = 0x9003;
    public static final int TAG_USERCOMMENT = 0x9286;

    public static final int TAG_SUBJECT_DISTANCE = 0x9206;
    public static final int TAG_FLASH = 0x9209;

    public static final int TAG_FOCALPLANEXRES = 0xa20E;
    public static final int TAG_FOCALPLANEUNITS = 0xa210;
    public static final int TAG_EXIF_IMAGEWIDTH = 0xA002;
    public static final int TAG_EXIF_IMAGELENGTH = 0xA003;

    // the following is added 05-jan-2001 vcs
    public static final int TAG_EXPOSURE_BIAS = 0x9204;
    public static final int TAG_WHITEBALANCE = 0x9208;
    public static final int TAG_METERING_MODE = 0x9207;
    public static final int TAG_EXPOSURE_PROGRAM = 0x8822;
    public static final int TAG_ISO_EQUIVALENT = 0x8827;
    public static final int TAG_COMPRESSION_LEVEL = 0x9102;

    public static final int TAG_THUMBNAIL_OFFSET = 0x0201;
    public static final int TAG_THUMBNAIL_LENGTH = 0x0202;

    // these added by drew
    public static final int TAG_ORIENTATION = 0x0112;
    public static final int TAG_X_RESOLUTION = 0x011A;
    public static final int TAG_Y_RESOLUTION = 0x011B;
    public static final int TAG_RESOLUTION_UNIT = 0x0128;
    public static final int TAG_YCBCR_POSITIONING = 0x0213;
}
/*
 * EXIFExtractor.java
 *
 * This class based upon code from Jhead, a C program for extracting and
 * manipulating the Exif data within files written by Matthias Wandel.
 *   http://www.sentex.net/~mwandel/jhead/
 *
 * Jhead is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  Similarly, I release this Java version under the
 * same license, though I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.  Unlike
 * Jhead, this code (as it stands) only supports reading of Exif data - no
 * manipulation, and no thumbnail stuff.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   drew.noakes@drewnoakes.com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created on 28 April 2002, 23:54
 * Modified 04 Aug 2002
 * - Renamed constants to be inline with changes to ExifTagValues interface
 * - Substituted usage of JDK 1.4 features (java.nio package)
 * Modified 29 Oct 2002 (v1.2)
 * - Proper traversing of Exif file structure and complete refactor & tidy of
 *   the codebase (a few unnoticed bugs removed)
 * - Reads makernote data for 6 families of camera (5 makes)
 * - Tags now stored in directories... use the IFD_* constants to refer to the
 *   image file directory you require (Exif, Interop, GPS and Makernote*) --
 *   this avoids collisions where two tags share the same code
 * - Takes componentCount of unknown tags into account
 * - Now understands GPS tags (thanks to Colin Briton for his help with this)
 * - Some other bug fixes, pointed out by users around the world.  Thanks!
 */
package com.drew.imaging.exif;

import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegProcessingException;

import java.io.File;
import java.util.Iterator;

/**
 * Extracts Exif data from a JPEG header segment, providing information about the
 * camera/scanner/capture device (if available).  Information is encapsulated in
 * an <code>ImageInfo</code> object.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ExifExtractor implements ExifTagValues
{
    /**
     * The JPEG segment as an array of bytes.
     */
    private byte[] data;

    /**
     * Represents the native byte ordering used in the JPEG segment.  If true,
     * then we're using Motorolla ordering (Big endian), else we're using Intel
     * ordering (Little endian).
     */
    private boolean motorollaByteOrder;

    /**
     * Bean instance to store information about the image and camera/scanner/capture
     * device.
     */
    private ImageInfo info;

    /**
     * The number of bytes used per format descriptor.
     */
    static int[] BYTES_PER_FORMAT = {0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8};

    /**
     * The number of formats known.
     */
    private static final int MAX_FORMAT_CODE = 12;

    // the format enumeration
    private static final int FMT_BYTE = 1;
    private static final int FMT_STRING = 2;
    private static final int FMT_USHORT = 3;
    private static final int FMT_ULONG = 4;
    private static final int FMT_URATIONAL = 5;
    private static final int FMT_SBYTE = 6;
    private static final int FMT_UNDEFINED = 7;
    private static final int FMT_SSHORT = 8;
    private static final int FMT_SLONG = 9;
    private static final int FMT_SRATIONAL = 10;
    private static final int FMT_SINGLE = 11;
    private static final int FMT_DOUBLE = 12;

    public static final int TIFF_HEADER_START_OFFSET = 6;

    public ExifExtractor(File file) throws JpegProcessingException
    {
        this(new JpegSegmentReader(file).readSegment(JpegSegmentReader.SEGMENT_MARKER_APP1));
    }

    /**
     * Creates an ExifExtractor for the given JPEG header segment.
     */
    public ExifExtractor(byte[] data)
    {
        this.data = data;
        this.info = new ImageInfo();
    }

    /**
     * Creates an ExifExtractor for the given <code>JPEGDecodeParam</code> object.
     */
    public ExifExtractor(JPEGDecodeParam param)
    {
        if (param == null) {
            throw new IllegalArgumentException("param cannot be null");
        }

        /* We should only really be seeing Exif in data[0]... the 2D array exists
         * because markers can theoretically appear multiple times in the file.
         */
        byte[][] data = param.getMarkerData(JPEGDecodeParam.APP1_MARKER);
        if (data != null) {
            this.data = data[0];
        }
        this.info = new ImageInfo();
    }

    /**
     * Performs the Exif data extraction, returning an instance of
     * <code>ImageInfo</code>.
     * @throws ExifProcessingException for bad/unexpected Exif data
     */
    public ImageInfo extract() throws ExifProcessingException
    {
        if (data == null) {
            return null;
        }
        if (data.length<12)
        {
            throw new ExifProcessingException("exif data must contain at least 12 bytes");
        }

        if (!"Exif\0\0".equals(new String(data, 0, 6))) {
            throw new ExifProcessingException("Exif data segment doesn't begin with 'Exif'");
        }

        String byteOrderIdentifier = new String(data, 6, 2);
        setByteOrder(byteOrderIdentifier);

        // Check the next two values for correctness.
        if (get16Bits(8) != 0x2a) {
            throw new ExifProcessingException("Invalid Exif start.  Should have 0x2A at offset 8 in Exif header.");
        }

        int firstDirectoryOffset = get32Bits(10) + TIFF_HEADER_START_OFFSET;

        // First directory normally starts 14 bytes in -- presume 0th IFD is Exif type directory
        processDirectory(IFD_EXIF, firstDirectoryOffset);

        return info;
    }

    private void setByteOrder(String byteOrderIdentifier) throws ExifProcessingException
    {
        if ("MM".equals(byteOrderIdentifier)) {
            motorollaByteOrder = true;
        } else if ("II".equals(byteOrderIdentifier)) {
            motorollaByteOrder = false;
        } else {
            throw new ExifProcessingException("Unclear distinction between Motorola/Intel byte ordering");
        }
    }

    /**
     * Process one of the nested EXIF directories.
     */
    private void processDirectory(int directoryType, int dirStartOffset) throws ExifProcessingException
    {
        // First two bytes in the IFD are the tag count
        int dirTagCount = get16Bits(dirStartOffset);

        validateDirectoryLength(dirStartOffset);

        // Handle each tag in this directory
        for (int dirEntry = 0; dirEntry < dirTagCount; dirEntry++) {
            int dirEntryOffset = calculateDirectoryEntryOffset(dirStartOffset, dirEntry);
            int tagType = get16Bits(dirEntryOffset);
            int formatCode = get16Bits(dirEntryOffset + 2);
            validateFormatCode(formatCode);

            // 4 bytes indicating number of formatCode type data for this tag
            int componentCount = get32Bits(dirEntryOffset + 4);
            int byteCount = componentCount * BYTES_PER_FORMAT[formatCode];
            int tagValueOffset = calculateTagValueOffset(byteCount, dirEntryOffset);

            // Calculate the value as an offset for cases where the tag is represents directory
            int subdirOffset = TIFF_HEADER_START_OFFSET + get32Bits(tagValueOffset);

            switch (tagType) {
                case TAG_EXIF_OFFSET:
                    processDirectory(IFD_EXIF, subdirOffset);
                    continue;
                case TAG_INTEROP_OFFSET:
                    processDirectory(IFD_INTEROP, subdirOffset);
                    continue;
                case TAG_GPS_INFO_OFFSET:
                    processDirectory(IFD_GPS, subdirOffset);
                    continue;
                case TAG_MAKER_NOTE:
                    processMakerNote(tagValueOffset);
                    continue;
                default:
                    processTag(directoryType, tagType, tagValueOffset, componentCount, formatCode);
                    break;
            }
        }
        // At the end of each IFD is an optional link to the next IFD.  This link is after
        // the 2-byte tag count, and after 12 bytes for each of these tags, hence
        int nextDirectoryOffset = get32Bits(dirStartOffset + 2 + 12 * dirTagCount);
        if (nextDirectoryOffset != 0) {
            nextDirectoryOffset += TIFF_HEADER_START_OFFSET;
            if (nextDirectoryOffset >= data.length) {
                // Note this could have been caused by jhead 1.3 cropping too much
                throw new ExifProcessingException("Last 4 bytes of IFD reference another IFD with an address that is out of bounds");
            }
            // the next directory is of same type as this one
            processDirectory(directoryType, nextDirectoryOffset);
        }
    }

    private void processMakerNote(int subdirOffset) throws ExifProcessingException
    {
        String cameraModel = info.getString(IFD_EXIF, TAG_MAKE);
        if ("OLYMP".equals(new String(data, subdirOffset, 5))) {
            processDirectory(IFD_MAKERNOTE_OLYPMUS, subdirOffset + 8);
        } else if ("NIKON".equalsIgnoreCase(cameraModel)) {
            if ("Nikon".equals(new String(data, subdirOffset, 5))) {
                processDirectory(IFD_MAKERNOTE_NIKON_TYPE1, subdirOffset + 8);
            } else {
                processDirectory(IFD_MAKERNOTE_NIKON_TYPE2, subdirOffset);
            }
        } else if ("Canon".equalsIgnoreCase(cameraModel)) {
            processDirectory(IFD_MAKERNOTE_CANON, subdirOffset);
        } else if ("Casio".equalsIgnoreCase(cameraModel)) {
            processDirectory(IFD_MAKERNOTE_CASIO, subdirOffset);
        } else if ("FUJIFILM".equals(new String(data, subdirOffset, 8)) || "Fujifilm".equalsIgnoreCase(cameraModel)) {
            boolean byteOrderBefore = motorollaByteOrder;
            // bug in fujifilm makernote ifd means we temporarily use Intel byte ordering
            motorollaByteOrder = false;
            // the 4 bytes after "FUJIFILM" in the makernote point to the start of the makernote
            // IFD, though the offset is relative to the start of the makernote, not the TIFF
            // header (like everywhere else)
            int ifdStart = subdirOffset + get32Bits(subdirOffset + 8);
            processDirectory(IFD_MAKERNOTE_FUJIFILM, ifdStart);
            motorollaByteOrder = byteOrderBefore;
        }
    }

    private void validateDirectoryLength(int dirStartOffset) throws ExifProcessingException
    {
        int dirTagCount = get16Bits(dirStartOffset);
        int dirLength = (2 + (12 * dirTagCount) + 4);
        if (dirLength + dirStartOffset + TIFF_HEADER_START_OFFSET >= data.length) {
            // Note: Files that had thumbnails trimmed with jhead 1.3 or earlier might trigger this.
            throw new ExifProcessingException("Illegally sized directory");
        }
    }

    private void processTag(int directoryType, int tagType, int tagValueOffset, int componentCount, int formatCode) throws ExifProcessingException
    {
        // If the format is rational, give it priority and treat it uniquely...
        if (componentCount == 1 && (formatCode == FMT_SRATIONAL || formatCode == FMT_URATIONAL)) {
            info.setRational(directoryType, tagType, get32Bits(tagValueOffset), get32Bits(tagValueOffset + 4));
            return;
        }

        switch (directoryType) {
            case IFD_EXIF:
                processExifTag(tagType, tagValueOffset, componentCount, formatCode);
                break;
            case IFD_GPS:
                processGpsTag(tagType, tagValueOffset, componentCount, formatCode);
                break;
            case IFD_INTEROP:
                processInteropTag(tagType, tagValueOffset, componentCount, formatCode);
                break;
            case IFD_MAKERNOTE_OLYPMUS:
                processOlympusMakernoteTag(tagType, tagValueOffset, componentCount, formatCode);
                break;
            case IFD_MAKERNOTE_CANON:
                processCanonMakernoteTag(tagType, tagValueOffset, componentCount, formatCode);
                break;
            case IFD_MAKERNOTE_NIKON_TYPE1:
                processNikonType1MakernoteTag(tagType, tagValueOffset, componentCount, formatCode);
                break;
            case IFD_MAKERNOTE_NIKON_TYPE2:
                processNikonType2MakernoteTag(tagType, tagValueOffset, componentCount, formatCode);
                break;
            case IFD_MAKERNOTE_CASIO:
                processCasioMakernoteTag(tagType, tagValueOffset, componentCount, formatCode);
                break;
            case IFD_MAKERNOTE_FUJIFILM:
                processFujifilmMakernoteTag(tagType, tagValueOffset, componentCount, formatCode);
                break;
            default:
                throw new ExifProcessingException("Unknown directory type: " + Integer.toHexString(directoryType));
        }

    }

    private void processFujifilmMakernoteTag(int tagType, int tagValueOffset, int componentCount, int formatCode)
    {
        switch (tagType) {
            default:
                String formattedString = convertTagToString(tagValueOffset, formatCode, componentCount);
                if (formattedString != null) {
                    info.setString(IFD_MAKERNOTE_FUJIFILM, tagType, formattedString);
                }
                break;
        }
    }

    private void processCasioMakernoteTag(int tagType, int tagValueOffset, int componentCount, int formatCode)
    {
        switch (tagType) {
            default:
                String formattedString = convertTagToString(tagValueOffset, formatCode, componentCount);
                if (formattedString != null) {
                    info.setString(IFD_MAKERNOTE_CASIO, tagType, formattedString);
                }
                break;
        }
    }

    private void processNikonType1MakernoteTag(int tagType, int tagValueOffset, int componentCount, int formatCode)
    {
        switch (tagType) {
            default:
                String formattedString = convertTagToString(tagValueOffset, formatCode, componentCount);
                if (formattedString != null) {
                    info.setString(IFD_MAKERNOTE_NIKON_TYPE1, tagType, formattedString);
                }
                break;
        }
    }

    private void processNikonType2MakernoteTag(int tagType, int tagValueOffset, int componentCount, int formatCode)
    {
        switch (tagType) {
            default:
                String formattedString = convertTagToString(tagValueOffset, formatCode, componentCount);
                if (formattedString != null) {
                    info.setString(IFD_MAKERNOTE_NIKON_TYPE2, tagType, formattedString);
                }
                break;
        }
    }

    private void processCanonMakernoteTag(int tagType, int tagValueOffset, int componentCount, int formatCode)
    {
        switch (tagType) {
            // Both of these camera states contain loads of information, masked into the individual bits of the tags
            // To extract these, can either define new tags and store them here, or work out the retrieval from the
            // ImageInfo side
            case TAG_CANON_CAMERA_STATE_1:
            case TAG_CANON_CAMERA_STATE_2:
                int makernoteByteCount = data[tagValueOffset];
                info.setString(IFD_MAKERNOTE_CANON, tagType, new String(data, tagValueOffset, makernoteByteCount));
                break;
            default:
                String formattedString = convertTagToString(tagValueOffset, formatCode, componentCount);
                if (formattedString != null) {
                    info.setString(IFD_MAKERNOTE_CANON, tagType, formattedString);
                }
                break;
        }
    }

    private void processOlympusMakernoteTag(int tagType, int tagValueOffset, int componentCount, int formatCode)
    {
        switch (tagType) {
            case TAG_OLYMPUS_SPECIAL_MODE:
                int mode = (int)convertTagToNumber(tagValueOffset, formatCode);
                int sequenceNumber = (int)convertTagToNumber(tagValueOffset + BYTES_PER_FORMAT[formatCode], formatCode);
                int panoramaDirection = (int)convertTagToNumber(tagValueOffset + (BYTES_PER_FORMAT[formatCode] * 2), formatCode);
                StringBuffer specialModeStr = new StringBuffer();
                switch (mode) {
                    case 0:
                        specialModeStr.append("Normal mode");
                    case 1:
                        specialModeStr.append("Unknown mode");
                    case 2:
                        specialModeStr.append("Fast mode");
                    case 3:
                        specialModeStr.append("Panorama mode");
                }
                specialModeStr.append(", " + sequenceNumber + " in sequence");
                switch (panoramaDirection) {
                    case 1:
                        specialModeStr.append(", panorama direction left to right");
                    case 2:
                        specialModeStr.append(", panorama direction right to left");
                    case 3:
                        specialModeStr.append(", panorama direction bottom to top");
                    case 4:
                        specialModeStr.append(", panorama direction top to bottom");
                }
                info.setString(IFD_MAKERNOTE_OLYPMUS, tagType, specialModeStr.toString());
                break;

            case TAG_OLYMPUS_CAMERA_ID:
                info.setString(IFD_MAKERNOTE_OLYPMUS, tagType, readString(tagValueOffset, 32));
                break;

            default:
                String formattedString = convertTagToString(tagValueOffset, formatCode, componentCount);
                if (formattedString != null) {
                    info.setString(IFD_MAKERNOTE_OLYPMUS, tagType, formattedString);
                }
                break;
        }
    }

    private void processExifTag(int tagType, int tagValueOffset, int componentCount, int formatCode)
    {
        switch (tagType) {
            case TAG_MAKE:
                info.setString(IFD_EXIF, tagType, readString(tagValueOffset, 31));
                break;

            case TAG_MODEL:
                info.setString(IFD_EXIF, tagType, readString(tagValueOffset, 39));
                break;

            case TAG_DATETIME_ORIGINAL:
                info.setString(IFD_EXIF, tagType, readString(tagValueOffset, 19));
                break;

            case TAG_USER_COMMENT:
                // Olympus has this padded with trailing spaces.  Remove these first.
                // ArrayIndexOutOfBoundsException bug fixed by Hendrik Wördehoff - 20 Sep 2002
                int byteCount = componentCount * BYTES_PER_FORMAT[formatCode];
                for (int i = byteCount - 1; i >= 0; i--) {
                    if (data[tagValueOffset + i] == ' ') {
                        data[tagValueOffset + i] = (byte)'\0';
                    } else {
                        break;
                    }
                }
                // Copy the comment
                if ("ASCII".equals(new String(data, tagValueOffset, 5))) {
                    for (int i = 5; i < 10; i++) {
                        byte b = data[tagValueOffset + i];
                        if (b != '\0' && b != ' ') {
                            info.setString(IFD_EXIF, TAG_USER_COMMENT, readString(tagValueOffset + i, 199));
                            break;
                        }
                    }
                } else {
                    info.setString(IFD_EXIF, TAG_USER_COMMENT, readString(tagValueOffset, 199));
                }
                break;

                // This value is usually Rational, so is handled above... however for completeness
                // this is included
            case TAG_X_RESOLUTION:
            case TAG_Y_RESOLUTION:
                info.setDouble(IFD_EXIF, tagType, convertTagToNumber(tagValueOffset, formatCode));
                break;

                // More relevant info always comes earlier, so only use this field if we don't
                // have appropriate aperture information yet.
            case TAG_APERTURE:
            case TAG_MAX_APERTURE:
                if (!info.containsTag(IFD_EXIF, TAG_FNUMBER)) {
                    info.setFloat(IFD_EXIF, TAG_FNUMBER, (float)Math.exp(convertTagToNumber(tagValueOffset, formatCode) * Math.log(2) * 0.5));
                }
                break;

                // Simplest way of expressing exposure time, so I trust it most (overwrite previously computd value if there is one)
            case TAG_EXPOSURE_TIME:
                // Indicates the distance the autofocus camera is focused to.  Tends to be less accurate as distance increases.
            case TAG_SUBJECT_DISTANCE:
                // Nice digital cameras actually save the focal length as a function of how far they are zoomed in.
            case TAG_FOCAL_LENGTH:
                // Simplest way of expressing aperture, so I trust it the most. (overwrite previously computed value if there is one)
            case TAG_FNUMBER:
            case TAG_EXPOSURE_BIAS:
                info.setFloat(IFD_EXIF, tagType, (float)convertTagToNumber(tagValueOffset, formatCode));
                break;

                // More complicated way of expressing exposure time, so only use this value if we don't already have it from somewhere else.
            case TAG_SHUTTER_SPEED:
                if (!info.containsTag(IFD_EXIF, TAG_EXPOSURE_TIME)) {
                    info.setFloat(IFD_EXIF, TAG_EXPOSURE_TIME, (float)(1 / Math.exp(convertTagToNumber(tagValueOffset, formatCode) * Math.log(2))));
                }
                break;

            case TAG_RESOLUTION_UNIT:
            case TAG_ORIENTATION:
            case TAG_PLANAR_CONFIGURATION:
            case TAG_SAMPLES_PER_PIXEL:
            case TAG_FLASH:
            case TAG_EXIF_IMAGE_HEIGHT:
            case TAG_EXIF_IMAGE_WIDTH:
            case TAG_WHITE_BALANCE:
            case TAG_METERING_MODE:
            case TAG_EXPOSURE_PROGRAM:
            case TAG_COMPRESSION_LEVEL:
            case TAG_YCBCR_POSITIONING:
                info.setInt(IFD_EXIF, tagType, (int)convertTagToNumber(tagValueOffset, formatCode));
                break;

            case TAG_FLASHPIX_VERSION:
            case TAG_EXIF_VERSION:
                info.setString(IFD_EXIF, tagType, convertBytesToVersionString(tagValueOffset));
                break;

            case TAG_YCBCR_SUBSAMPLING:
                int position1 = (int)convertTagToNumber(tagValueOffset, formatCode);
                int position2 = (int)convertTagToNumber(tagValueOffset + BYTES_PER_FORMAT[formatCode], formatCode);
                String positioning;
                if (position1 == 2 && position2 == 1) {
                    positioning = "YCbCr4:2:2";
                } else if (position1 == 2 && position2 == 2) {
                    positioning = "YCbCr4:2:0";
                } else {
                    positioning = "(Unknown)";
                }
                info.setString(IFD_EXIF, tagType, positioning);
                break;

            case TAG_ISO_EQUIVALENT:
                int isoEquiv = (int)convertTagToNumber(tagValueOffset, formatCode);
                if (isoEquiv < 50) {
                    isoEquiv *= 200;
                }
                info.setInt(IFD_EXIF, tagType, isoEquiv);
                break;

            case TAG_REFERENCE_BLACK_WHITE:
                int blackR = (int)convertTagToNumber(tagValueOffset, formatCode);
                int whiteR = (int)convertTagToNumber((tagValueOffset + 8), formatCode);
                int blackG = (int)convertTagToNumber((tagValueOffset + 16), formatCode);
                int whiteG = (int)convertTagToNumber((tagValueOffset + 24), formatCode);
                int blackB = (int)convertTagToNumber((tagValueOffset + 32), formatCode);
                int whiteB = (int)convertTagToNumber((tagValueOffset + 40), formatCode);
                String pos = "[" + blackR + "," + blackG + "," + blackB + "] " +
                        "[" + whiteR + "," + whiteG + "," + whiteB + "]";
                info.setString(IFD_EXIF, tagType, pos);
                break;

            case TAG_BITS_PER_SAMPLE:
                info.setString(IFD_EXIF, tagType, convertTagToString(tagValueOffset, formatCode, componentCount));
                break;

            case TAG_COMPONENTS_CONFIGURATION:
                String[] components = {"", "Y", "Cb", "Cr", "R", "G", "B"};
                StringBuffer componentConfig = new StringBuffer();
                for (int i = 0; i < 4; i++) {
                    int j = data[tagValueOffset + i];
                    if (j > 0 && j < components.length) {
                        componentConfig.append(components[j]);
                    }
                }
                info.setString(IFD_EXIF, tagType, componentConfig.toString());
                break;

            default:
                String formattedString = convertTagToString(tagValueOffset, formatCode, componentCount);
                if (formattedString != null) {
                    info.setString(IFD_EXIF, tagType, formattedString);
                }
                break;
        }
    }

    /**
     * Takes a series of 4 bytes from the specified offset, and converts these to a
     * well-known version number, where possible.  For example, (hex) 30 32 31 30 == 2.10).
     * @param tagValueOffset the offset at which the first version byte exists
     * @return the version as a string of form 2.10
     */
    private String convertBytesToVersionString(int tagValueOffset)
    {
        StringBuffer version = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            if (i == 2) version.append('.');
            String digit = new String(data, tagValueOffset + i, 1);
            if (i == 0 && "0".equals(digit)) continue;
            version.append(digit);
        }
        return version.toString();
    }

    private void processGpsTag(int tagType, int tagValueOffset, int componentCount, int formatCode)
    {
        switch (tagType) {
            case TAG_GPS_VERSION_ID:
                info.setString(IFD_GPS, tagType, convertTagToString(tagValueOffset, formatCode, componentCount));
//                // need to move to read the 4 seperate bytes to make format 2.x.x.x
//                String IDformattedString = String.valueOf(get32Bits(tagValueOffset));
//                if (IDformattedString != null) {
//                    info.setString(IFD_GPS, tagType, IDformattedString);
//                }
                break;

                // ASCII
            case TAG_GPS_SPEED_REF:
            case TAG_GPS_MEASURE_MODE:
            case TAG_GPS_LONGITUDE_REF:
            case TAG_GPS_TRACK_REF:
            case TAG_GPS_LATITUDE_REF:
            case TAG_GPS_STATUS:
            case TAG_GPS_IMG_DIRECTION_REF:
            case TAG_GPS_DEST_LATITUDE_REF:
            case TAG_GPS_DEST_LONGITUDE_REF:
            case TAG_GPS_DEST_BEARING_REF:
            case TAG_GPS_DEST_DISTANCE_REF:
                info.setString(IFD_GPS, tagType, readString(tagValueOffset, componentCount));
                break;

                // three rational numbers -- displayed in H"MM"SS.ss
            case TAG_GPS_LONGITUDE:
            case TAG_GPS_LATITUDE:
                int deg = (int)convertTagToNumber(tagValueOffset, formatCode);
                float min = (float)convertTagToNumber((tagValueOffset + 8), formatCode);
                float sec = (float)convertTagToNumber((tagValueOffset + 16), formatCode);
                String pos = String.valueOf(deg) + "\"" + String.valueOf(min) + "'" + String.valueOf(sec);
                info.setString(IFD_GPS, tagType, pos);
                break;

                // time in hour, min, sec
            case TAG_GPS_TIME_STAMP:
                String gpsTime = String.valueOf(get32Bits(tagValueOffset)) + ":" + String.valueOf(get32Bits(tagValueOffset + 8)) + ":" + String.valueOf(get32Bits(tagValueOffset + 16)) + " UTC";
                info.setString(IFD_GPS, tagType, gpsTime);
                break;

            default:
                String formattedString = convertTagToString(tagValueOffset, formatCode, componentCount);
                if (formattedString != null) {
                    info.setString(IFD_GPS, tagType, formattedString);
                }
                break;
        }
    }

    private void processInteropTag(int tagType, int tagValueOffset, int componentCount, int formatCode)
    {
        switch (tagType) {
            case TAG_INTEROP_VERSION:
                info.setString(IFD_INTEROP, tagType, convertBytesToVersionString(tagValueOffset));
                break;
            default:
                String formattedString = convertTagToString(tagValueOffset, formatCode, componentCount);
                if (formattedString != null) {
                    info.setString(IFD_INTEROP, tagType, formattedString);
                }
                break;
        }
    }

    private int calculateTagValueOffset(int byteCount, int dirEntryOffset) throws ExifProcessingException
    {
        if (byteCount > 4) {
            // If its bigger than 4 bytes, the dir entry contains an offset.
            int offsetVal = get32Bits(dirEntryOffset + 8);
            if (offsetVal + byteCount > data.length) {
                // Bogus pointer offset and / or bytecount value
                throw new ExifProcessingException("Illegal pointer offset value in EXIF");
            }
            return TIFF_HEADER_START_OFFSET + offsetVal;
        } else {
            // 4 bytes or less and value is in the dir entry itself
            return dirEntryOffset + 8;
        }
    }

    /**
     * Throws an exception if the format code is out of bounds.
     * @param formatCode the format code
     * @throws ExifProcessingException is the format code is out of bounds
     */
    private void validateFormatCode(int formatCode) throws ExifProcessingException
    {
        if (formatCode < 0 || formatCode > MAX_FORMAT_CODE) {
            throw new ExifProcessingException("Illegal format code in EXIF dir");
        }
    }

    /**
     * Creates a String from the data buffer starting at the specified offset,
     * and ending where byte=='\0' or where length==maxLength.
     */
    private String readString(int offset, int maxLength)
    {
        int length = 0;
        while (data[offset + length] != '\0' && length < maxLength) {
            length++;
        }
        return new String(data, offset, length);
    }

    /**
     * Fashions the value at the given tagValueOffset into String format, assuming the
     * provided format code.
     * <p>
     * Format codes are <code>FMT_BYTE</code>, <code>FMT_URATIONAL</code>,
     * <code>FMT_DOUBLE</code>, etc...
     */
    private String convertTagToString(int tagValueOffset, int formatCode, int componentCount)
    {
        // strings are an exception among format codes
        if (formatCode == FMT_STRING) {
            return readString(tagValueOffset, componentCount);
        }

        // concat componentCount instances of data formatted accordingly
        StringBuffer sbuff = new StringBuffer();
        for (int i = 0; i < componentCount; i++) {
            if (i > 0) sbuff.append(' ');
            int offsetShift = BYTES_PER_FORMAT[formatCode] * i;
//          sbuff.append(convertTagToNumber(tagValueOffset + offsetShift, formatCode));
            switch (formatCode) {
                case FMT_SBYTE:
                case FMT_BYTE:
                case FMT_UNDEFINED:
                    sbuff.append(data[tagValueOffset + offsetShift]);
                    break;
                case FMT_SSHORT:
                case FMT_USHORT:
                    sbuff.append(get16Bits(tagValueOffset + offsetShift));
                    break;
                case FMT_ULONG:
                case FMT_SLONG:
                    sbuff.append(get32Bits(tagValueOffset + offsetShift));
                    break;
                case FMT_URATIONAL:
                case FMT_SRATIONAL:
                    sbuff.append(get32Bits(tagValueOffset + offsetShift) + "/" + get32Bits(tagValueOffset + 4 + offsetShift));
                    break;
                case FMT_SINGLE:
                case FMT_DOUBLE:
                    sbuff.append((double)data[tagValueOffset + offsetShift]);
                    break;
                default:
                    // this format code is screwey... simply return null and deal with
                    // presentation elsewhere
                    return null;
            }
        }
        return sbuff.toString();
    }

    /**
     * Evaluates the value at the given offset, assuming the provided format code.  Values are
     * returned as <code>double</code> primitives, which should be cast to the desired type.
     * <p>
     * Format codes are <code>FMT_BYTE</code>, <code>FMT_URATIONAL</code>, <code>FMT_DOUBLE</code>,
     * etc...
     */
    private double convertTagToNumber(int offset, int formatCode)
    {
        double value = 0;
        switch (formatCode) {
            case FMT_SBYTE:
            case FMT_BYTE:
            case FMT_SINGLE:
            case FMT_DOUBLE:
                value = data[offset];
                break;
            case FMT_USHORT:
            case FMT_SSHORT:
                value = get16Bits(offset);
                break;
            case FMT_SLONG:
            case FMT_ULONG:
                value = get32Bits(offset);
                break;
            case FMT_URATIONAL:
            case FMT_SRATIONAL:
                int numerator = get32Bits(offset);
                int denominator = get32Bits(offset + 4);
                if (denominator == 0) {
                    value = 0;
                } else {
                    value = (double)numerator / (double)denominator;
                }
                break;
        }
        return value;
    }

    /**
     * Determine the offset at which a given InteropArray entry begins within the specified IFD.
     * @param ifdStartOffset the offset at which the IFD starts
     * @param entryNumber the zero-based entry number
     */
    private int calculateDirectoryEntryOffset(int ifdStartOffset, int entryNumber)
    {
        return (ifdStartOffset + 2 + (12 * entryNumber));
    }

    /**
     * Get a 16 bit value from file's native byte order.  Between 0x0000 and 0xFFFF.
     */
    private int get16Bits(int offset)
    {
        if (offset < 0 || offset >= data.length) {
            throw new ArrayIndexOutOfBoundsException("attempt to read data outside of exif segment (index " + offset + " where max index is " + (data.length - 1) + ")");
        }
        if (motorollaByteOrder) {
            // Motorola big first
            return (data[offset] << 8 & 0xFF00) | (data[offset + 1] & 0xFF);
        } else {
            // Intel ordering
            return (data[offset + 1] << 8 & 0xFF00) | (data[offset] & 0xFF);
        }
    }

    /**
     * Get a 32 bit value from file's native byte order.
     */
    private int get32Bits(int offset)
    {
        if (offset < 0 || offset >= data.length) {
            throw new ArrayIndexOutOfBoundsException("attempt to read data outside of exif segment (index " + offset + " where max index is " + (data.length - 1) + ")");
        }
        // TODO report this bug in IntelliJ -- 0xFF000000 too large ???
        if (motorollaByteOrder) {
            // Motorola big first
            return (data[offset] << 24 & 0xFF000000) |
                    (data[offset + 1] << 16 & 0xFF0000) |
                    (data[offset + 2] << 8 & 0xFF00) |
                    (data[offset + 3] & 0xFF);
        } else {
            // Intel ordering
            return (data[offset + 3] << 24 & 0xFF000000) |
                    (data[offset + 2] << 16 & 0xFF0000) |
                    (data[offset + 1] << 8 & 0xFF00) |
                    (data[offset] & 0xFF);
        }
    }

    /**
     * Entry point for testing and comand line usage.
     */
    public static void main(String[] args) throws Exception
    {
        // expecting one argument exactly
        if (args.length != 1) {
            printUsage();
            System.exit(1);
        }
        // create a file from the argument, and make sure it's valid and readable
        String fileName = args[0];
        File file = new File(fileName);
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            System.out.println("Cannot read from the specified file");
            printUsage();
            System.exit(1);
        }
        // load the data and extract exif info
        ImageInfo info = new ExifExtractor(file).extract();
        // iterate over the exif data and print to System.out
        Iterator i = info.getTagIterator();
        while (i.hasNext()) {
            TagValue tag = (TagValue)i.next();
            System.out.println(tag.getDirectoryName() + " " + tag.getTagTypeHex() + " " + tag.getTagName() + " " + tag.getDescription());
        }
    }

    /**
     * Prints an explanatory message describing usage to the console.
     */
    private static void printUsage()
    {
        System.out.println("Usage:");
        System.out.println("\tjava com.drew.imaging.exif.ExifExtractor <filename>");
        System.out.println("\tjava -jar exifExtractor.jar <filename>");
        System.out.println();
    }
}

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
 * Modified 27 Nov 2002 (v2.0)
 * - Renamed to ExifReader
 * - Moved to new package com.drew.metadata.exif
 */
package com.drew.metadata.exif;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.lang.Rational;
import com.drew.metadata.*;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Extracts Exif data from a JPEG header segment, providing information about the
 * camera/scanner/capture device (if available).  Information is encapsulated in
 * an <code>Metadata</code> object.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ExifReader implements MetadataReader
{
    /**
     * The JPEG segment as an array of bytes.
     */
    private byte[] _data;

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
    private Metadata _metadata;

    /**
     * The number of bytes used per format descriptor.
     */
    static final int[] BYTES_PER_FORMAT = {0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8};

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

    public static final int TAG_EXIF_OFFSET = 0x8769;
    public static final int TAG_INTEROP_OFFSET = 0xA005;
    public static final int TAG_GPS_INFO_OFFSET = 0x8825;
    public static final int TAG_MAKER_NOTE = 0x927C;

    public static final int TIFF_HEADER_START_OFFSET = 6;

    /**
     *
     * @param file
     * @throws JpegProcessingException
     * @throws FileNotFoundException
     */
    public ExifReader(File file) throws JpegProcessingException, FileNotFoundException
    {
        this(new JpegSegmentReader(file).readSegment(JpegSegmentReader.SEGMENT_APP1));
    }

    /**
     * Creates an ExifReader for the given JPEG header segment.
     */
    public ExifReader(byte[] data)
    {
        _data = data;
    }

    /**
     * Performs the Exif data extraction, returning a new instance of <code>Metadata</code>.
     */
    public Metadata extract()
    {
        return extract(new Metadata());
    }

    /**
     * Performs the Exif data extraction, adding found values to the specified
     * instance of <code>Metadata</code>.
     */
    public Metadata extract(Metadata metadata)
    {
        _metadata = metadata;
        if (_data == null) {
            return _metadata;
        }

        // once we know there's some data, create the directory and start working on it
        Directory directory = _metadata.getDirectory(ExifDirectory.class);
        if (_data.length <= 14) {
            directory.addError("Exif data segment must contain at least 14 bytes");
            return _metadata;
        }
        if (!"Exif\0\0".equals(new String(_data, 0, 6))) {
            directory.addError("Exif data segment doesn't begin with 'Exif'");
            return _metadata;
        }

        // this should be either "MM" or "II"
        String byteOrderIdentifier = new String(_data, 6, 2);
        if (!setByteOrder(byteOrderIdentifier)) {
            directory.addError("Unclear distinction between Motorola/Intel byte ordering");
            return _metadata;
        }

        // Check the next two values for correctness.
        if (get16Bits(8) != 0x2a) {
            directory.addError("Invalid Exif start - should have 0x2A at offset 8 in Exif header");
            return _metadata;
        }

        int firstDirectoryOffset = get32Bits(10) + TIFF_HEADER_START_OFFSET;

        // David Ekholm sent an digital camera image that has this problem
        if (firstDirectoryOffset >= _data.length - 1) {
            directory.addError("First exif directory offset is beyond end of Exif data segment");
            // First directory normally starts 14 bytes in -- try it here and catch another error in the worst case
            firstDirectoryOffset = 14;
        }

        // 0th IFD (we merge with Exif IFD)
        processDirectory(directory, firstDirectoryOffset);

        // after the extraction process, if we have the correct tags, we may be able to extract thumbnail information
        extractThumbnail(directory);

        return _metadata;
    }

    private void extractThumbnail(Directory exifDirectory)
    {
        if (!(exifDirectory instanceof ExifDirectory)) {
            return;
        }
        if (!exifDirectory.containsTag(ExifDirectory.TAG_THUMBNAIL_LENGTH) ||
                !exifDirectory.containsTag(ExifDirectory.TAG_THUMBNAIL_OFFSET)) {
            return;
        }
        int offset, length;
        try {
            offset = exifDirectory.getInt(ExifDirectory.TAG_THUMBNAIL_OFFSET);
            length = exifDirectory.getInt(ExifDirectory.TAG_THUMBNAIL_LENGTH);
            byte[] result = new byte[length];
            for (int i = 0; i < result.length; i++) {
                result[i] = _data[TIFF_HEADER_START_OFFSET + offset + i];
            }
            exifDirectory.setByteArray(ExifDirectory.TAG_THUMBNAIL_DATA, result);
        } catch (Throwable e) {
            exifDirectory.addError("Unable to extract thumbnail: " + e.getMessage());
        }
    }

    private boolean setByteOrder(String byteOrderIdentifier)
    {
        if ("MM".equals(byteOrderIdentifier)) {
            motorollaByteOrder = true;
        } else if ("II".equals(byteOrderIdentifier)) {
            motorollaByteOrder = false;
        } else {
            return false;
        }
        return true;
    }

    /**
     * Process one of the nested EXIF directories.
     */
    private void processDirectory(Directory directory, int dirStartOffset)
    {
//        if (dirStartOffset>=_data.length) {
//            return;
//        }

        // First two bytes in the IFD are the tag count
        int dirTagCount = get16Bits(dirStartOffset);

        if (!isDirectoryLengthValid(dirStartOffset)) {
            directory.addError("Illegally sized directory");
            return;
        }

        // Handle each tag in this directory
        for (int dirEntry = 0; dirEntry < dirTagCount; dirEntry++) {
            int dirEntryOffset = calculateDirectoryEntryOffset(dirStartOffset, dirEntry);
            int tagType = get16Bits(dirEntryOffset);
            int formatCode = get16Bits(dirEntryOffset + 2);
            if (formatCode < 0 || formatCode > MAX_FORMAT_CODE) {
                directory.addError("Invalid format code: " + formatCode);
                continue;
            }

            // 4 bytes indicating number of formatCode type data for this tag
            int componentCount = get32Bits(dirEntryOffset + 4);
            int byteCount = componentCount * BYTES_PER_FORMAT[formatCode];
            int tagValueOffset = calculateTagValueOffset(byteCount, dirEntryOffset);
            if (tagValueOffset<0) {
                directory.addError("Illegal pointer offset value in EXIF");
                continue;
            }

            // Calculate the value as an offset for cases where the tag is represents directory
            int subdirOffset = TIFF_HEADER_START_OFFSET + get32Bits(tagValueOffset);

            switch (tagType) {
                case TAG_EXIF_OFFSET:
                    processDirectory(_metadata.getDirectory(ExifDirectory.class), subdirOffset);
                    continue;
                case TAG_INTEROP_OFFSET:
                    processDirectory(_metadata.getDirectory(ExifInteropDirectory.class), subdirOffset);
                    continue;
                case TAG_GPS_INFO_OFFSET:
                    processDirectory(_metadata.getDirectory(GpsDirectory.class), subdirOffset);
                    continue;
                case TAG_MAKER_NOTE:
                    processMakerNote(tagValueOffset);
                    continue;
                default:
                    processTag(directory, tagType, tagValueOffset, componentCount, formatCode);
                    break;
            }
        }
        // At the end of each IFD is an optional link to the next IFD.  This link is after
        // the 2-byte tag count, and after 12 bytes for each of these tags, hence
        int nextDirectoryOffset = get32Bits(dirStartOffset + 2 + 12 * dirTagCount);
        if (nextDirectoryOffset != 0) {
            nextDirectoryOffset += TIFF_HEADER_START_OFFSET;
            if (nextDirectoryOffset >= _data.length) {
                // Last 4 bytes of IFD reference another IFD with an address that is out of bounds
                // Note this could have been caused by jhead 1.3 cropping too much
                return;
            }
            // the next directory is of same type as this one
            processDirectory(directory, nextDirectoryOffset);
        }
    }

    private void processMakerNote(int subdirOffset)
    {
        // Determine the camera model and makernote format
        Directory exifDirectory = _metadata.getDirectory(ExifDirectory.class);
        if (exifDirectory == null) {
            return;
        }
        String cameraModel = exifDirectory.getString(ExifDirectory.TAG_MAKE);
        if ("OLYMP".equals(new String(_data, subdirOffset, 5))) {
            // Olympus Makernote
            processDirectory(_metadata.getDirectory(OlympusMakernoteDirectory.class), subdirOffset + 8);
        } else if ("NIKON".equalsIgnoreCase(cameraModel)) {
            if ("Nikon".equals(new String(_data, subdirOffset, 5))) {
                // Nikon type 1 Makernote
                processDirectory(_metadata.getDirectory(NikonType1MakernoteDirectory.class), subdirOffset + 8);
            } else {
                // Nikon type 2 Makernote
                processDirectory(_metadata.getDirectory(NikonType2MakernoteDirectory.class), subdirOffset);
            }
        } else if ("Canon".equalsIgnoreCase(cameraModel)) {
            // Canon Makernote
            processDirectory(_metadata.getDirectory(CanonMakernoteDirectory.class), subdirOffset);
        } else if ("Casio".equalsIgnoreCase(cameraModel)) {
            // Casio Makernote
            processDirectory(_metadata.getDirectory(CasioMakernoteDirectory.class), subdirOffset);
        } else if ("FUJIFILM".equals(new String(_data, subdirOffset, 8)) || "Fujifilm".equalsIgnoreCase(cameraModel)) {
            // Fujifile Makernote
            boolean byteOrderBefore = motorollaByteOrder;
            // bug in fujifilm makernote ifd means we temporarily use Intel byte ordering
            motorollaByteOrder = false;
            // the 4 bytes after "FUJIFILM" in the makernote point to the start of the makernote
            // IFD, though the offset is relative to the start of the makernote, not the TIFF
            // header (like everywhere else)
            int ifdStart = subdirOffset + get32Bits(subdirOffset + 8);
            processDirectory(_metadata.getDirectory(FujiFilmMakernoteDirectory.class), ifdStart);
            motorollaByteOrder = byteOrderBefore;
        }
    }

    private boolean isDirectoryLengthValid(int dirStartOffset)
    {
        int dirTagCount = get16Bits(dirStartOffset);
        int dirLength = (2 + (12 * dirTagCount) + 4);
        if (dirLength + dirStartOffset + TIFF_HEADER_START_OFFSET >= _data.length) {
            // Note: Files that had thumbnails trimmed with jhead 1.3 or earlier might trigger this
            return false;
        }
        return true;
    }

    private void processTag(Directory directory, int tagType, int tagValueOffset, int componentCount, int formatCode)
    {
        // Directory simply stores raw values
        // The display side uses a Descriptor class per directory to turn the raw values into 'pretty' descriptions
        switch (formatCode) {
            case FMT_UNDEFINED:
            case FMT_STRING:
                String s = readString(tagValueOffset, componentCount);
                directory.setString(tagType, s);
                break;
            case FMT_SRATIONAL:
            case FMT_URATIONAL:
                if (componentCount == 1) {
                    Rational rational = new Rational(get32Bits(tagValueOffset), get32Bits(tagValueOffset + 4));
                    directory.setRational(tagType, rational);
                } else {
                    Rational[] rationals = new Rational[componentCount];
                    for (int i = 0; i < componentCount; i++) {
                        rationals[i] = new Rational(get32Bits(tagValueOffset + (8 * i)), get32Bits(tagValueOffset + 4 + (8 * i)));
                    }
                    directory.setRationalArray(tagType, rationals);
                }
                break;
            case FMT_SBYTE:
            case FMT_BYTE:
                if (componentCount == 1) {
                    // this may need to be a byte, but I think casting to int is fine
                    int b = _data[tagValueOffset];
                    directory.setInt(tagType, b);
                } else {
                    int[] bytes = new int[componentCount];
                    for (int i = 0; i < componentCount; i++) {
                        bytes[i] = _data[tagValueOffset];
                    }
                    directory.setIntArray(tagType, bytes);
                }
                break;
            case FMT_SINGLE:
            case FMT_DOUBLE:
                if (componentCount == 1) {
                    int i = _data[tagValueOffset];
                    directory.setInt(tagType, i);
                } else {
                    int[] ints = new int[componentCount];
                    for (int i = 0; i < componentCount; i++) {
                        ints[i] = _data[tagValueOffset];
                    }
                    directory.setIntArray(tagType, ints);
                }
                break;
            case FMT_USHORT:
            case FMT_SSHORT:
                if (componentCount == 1) {
                    int i = get16Bits(tagValueOffset);
                    directory.setInt(tagType, i);
                } else {
                    int[] ints = new int[componentCount];
                    for (int i = 0; i < componentCount; i++) {
                        ints[i] = get16Bits(tagValueOffset + (i * 2));
                    }
                    directory.setIntArray(tagType, ints);
                }
                break;
            case FMT_SLONG:
            case FMT_ULONG:
                if (componentCount == 1) {
                    int i = get32Bits(tagValueOffset);
                    directory.setInt(tagType, i);
                } else {
                    int[] ints = new int[componentCount];
                    for (int i = 0; i < componentCount; i++) {
                        ints[i] = get32Bits(tagValueOffset + (i * 4));
                    }
                    directory.setIntArray(tagType, ints);
                }
                break;
            default:
                directory.addError("unknown format code " + formatCode);
        }
    }
/*
    private void processExifTag(int tagType, int tagValueOffset, int componentCount, int formatCode)
    {
        switch (tagType) {
// TODO test this still works
            case TAG_USER_COMMENT:
                // Olympus has this padded with trailing spaces.  Remove these first.
                // ArrayIndexOutOfBoundsException bug fixed by Hendrik Wördehoff - 20 Sep 2002
                int byteCount = componentCount * BYTES_PER_FORMAT[formatCode];
                for (int i = byteCount - 1; i >= 0; i--) {
                    if (_data[tagValueOffset + i] == ' ') {
                        _data[tagValueOffset + i] = (byte)'\0';
                    } else {
                        break;
                    }
                }
                // Copy the comment
                if ("ASCII".equals(new String(_data, tagValueOffset, 5))) {
                    for (int i = 5; i < 10; i++) {
                        byte b = _data[tagValueOffset + i];
                        if (b != '\0' && b != ' ') {
                            _metadata.setString(DIRECTORY_EXIF_EXIF, TAG_USER_COMMENT, readString(tagValueOffset + i, 199));
                            break;
                        }
                    }
                } else {
                    _metadata.setString(DIRECTORY_EXIF_EXIF, TAG_USER_COMMENT, readString(tagValueOffset, 199));
                }
                break;

// TODO work out what to do with this calculation
                // More relevant info always comes earlier, so only use this field if we don't
                // have appropriate aperture information yet.
            case TAG_APERTURE:
            case TAG_MAX_APERTURE:
                if (!_metadata.containsTag(DIRECTORY_EXIF_EXIF, TAG_FNUMBER)) {
                    _metadata.setFloat(DIRECTORY_EXIF_EXIF, TAG_FNUMBER, (float)Math.exp(convertTagToNumber(tagValueOffset, formatCode) * Math.log(2) * 0.5));
                }
                break;

// TODO copy these comments somewhere
                // Simplest way of expressing exposure time, so I trust it most (overwrite previously computd value if there is one)
            case TAG_EXPOSURE_TIME:
                // Indicates the distance the autofocus camera is focused to.  Tends to be less accurate as distance increases.
            case TAG_SUBJECT_DISTANCE:
                // Nice digital cameras actually save the focal length as a function of how far they are zoomed in.
            case TAG_FOCAL_LENGTH:
                // Simplest way of expressing aperture, so I trust it the most. (overwrite previously computed value if there is one)
            case TAG_FNUMBER:
            case TAG_EXPOSURE_BIAS:
                _metadata.setFloat(DIRECTORY_EXIF_EXIF, tagType, (float)convertTagToNumber(tagValueOffset, formatCode));
                break;

// TODO work out what to do with this calculation
                // More complicated way of expressing exposure time, so only use this value if we don't already have it from somewhere else.
            case TAG_SHUTTER_SPEED:
                if (!_metadata.containsTag(DIRECTORY_EXIF_EXIF, TAG_EXPOSURE_TIME)) {
                    _metadata.setFloat(DIRECTORY_EXIF_EXIF, TAG_EXPOSURE_TIME, (float)(1 / Math.exp(convertTagToNumber(tagValueOffset, formatCode) * Math.log(2))));
                }
                break;
        }
    }
*/

    private int calculateTagValueOffset(int byteCount, int dirEntryOffset)
    {
        if (byteCount > 4) {
            // If its bigger than 4 bytes, the dir entry contains an offset.
            // TODO if we're reading FujiFilm makernote tags, the offset is relative to the start of the makernote itself, not the TIFF segment
            int offsetVal = get32Bits(dirEntryOffset + 8);
            if (offsetVal + byteCount > _data.length) {
                // Bogus pointer offset and / or bytecount value
                return -1; // signal error
            }
            return TIFF_HEADER_START_OFFSET + offsetVal;
        } else {
            // 4 bytes or less and value is in the dir entry itself
            return dirEntryOffset + 8;
        }
    }

    /**
     * Creates a String from the _data buffer starting at the specified offset,
     * and ending where byte=='\0' or where length==maxLength.
     */
    private String readString(int offset, int maxLength)
    {
        int length = 0;
        while ((offset + length) < _data.length && _data[offset + length] != '\0' && length < maxLength) {
            length++;
        }
        return new String(_data, offset, length);
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
        if (offset < 0 || offset >= _data.length) {
            throw new ArrayIndexOutOfBoundsException("attempt to read data outside of exif segment (index " + offset + " where max index is " + (_data.length - 1) + ")");
        }
        if (motorollaByteOrder) {
            // Motorola big first
            return (_data[offset] << 8 & 0xFF00) | (_data[offset + 1] & 0xFF);
        } else {
            // Intel ordering
            return (_data[offset + 1] << 8 & 0xFF00) | (_data[offset] & 0xFF);
        }
    }

    /**
     * Get a 32 bit value from file's native byte order.
     */
    private int get32Bits(int offset)
    {
        if (offset < 0 || offset >= _data.length) {
            throw new ArrayIndexOutOfBoundsException("attempt to read data outside of exif segment (index " + offset + " where max index is " + (_data.length - 1) + ")");
        }
        // TODO report this bug in IntelliJ -- 0xFF000000 too large ???
        if (motorollaByteOrder) {
            // Motorola big first
            return (_data[offset] << 24 & 0xFF000000) |
                    (_data[offset + 1] << 16 & 0xFF0000) |
                    (_data[offset + 2] << 8 & 0xFF00) |
                    (_data[offset + 3] & 0xFF);
        } else {
            // Intel ordering
            return (_data[offset + 3] << 24 & 0xFF000000) |
                    (_data[offset + 2] << 16 & 0xFF0000) |
                    (_data[offset + 1] << 8 & 0xFF00) |
                    (_data[offset] & 0xFF);
        }
    }
}

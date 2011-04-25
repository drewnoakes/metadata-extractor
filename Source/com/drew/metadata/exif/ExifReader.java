/*
 * Early versions of this class were inspired by Jhead, a C program for extracting and
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
 * manipulation.
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
 * Modified since, however changes have not been logged.  See release notes for
 * library-wide modifications.
 */
package com.drew.metadata.exif;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;

/**
 * Decodes Exif binary data, populating a <code>Metadata</code> object with tag values in <code>ExifDirectory</code>,
 * <code>GpsDirectory</code> and one of the many camera makernote directories.
 * @author Drew Noakes http://drewnoakes.com
 */
public class ExifReader implements MetadataReader
{
    // TODO we assume Exif is always in the first instance of APP1 data which may not always be the case
    // TODO simplify this type by removing some of the state fields (data, directory, etc) and do everything on the stack, passing around args

    /**
     * The number of bytes used per format descriptor.
     */
    private static final int[] BYTES_PER_FORMAT = {0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8};

    /**
     * The number of formats known.
     */
    private static final int MAX_FORMAT_CODE = 12;

    // Format types
    // Note: Cannot use the DataFormat enumeration in the case statement that uses these tags.
    //       Is there a better way?
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
     * The Exif segment as an array of bytes.
     */
    private final byte[] _data;

    /**
     * Represents the native byte ordering used in the JPEG segment.  If true,
     * then we're using Motorolla ordering (Big endian), else we're using Intel
     * ordering (Little endian).
     */
    private boolean _isMotorollaByteOrder;

    /**
     * The Exif directory used (loaded lazily)
     */
    private ExifDirectory _exifDirectory = null;

    /**
     * Creates an ExifReader for a JpegSegmentData object.  This will process the @JpegSegmentReader.SEGMENT_APP1 segment.
     * @param segmentData the collection of Jpeg segment data from which this reader will take data.
     */
    public ExifReader(JpegSegmentData segmentData)
    {
        this(segmentData.getSegment(JpegSegmentReader.SEGMENT_APP1));
    }

    /**
     * Creates an ExifReader for a Jpeg jpegFile.
     * @param jpegFile
     * @throws JpegProcessingException
     */
    public ExifReader(File jpegFile) throws JpegProcessingException
    {
        this(new JpegSegmentReader(jpegFile).readSegment(JpegSegmentReader.SEGMENT_APP1));
    }

    /**
     * Creates an ExifReader for a Jpeg stream.
     * @param jpegInputStream JPEG stream. Stream will be closed.
     * @throws JpegProcessingException
     */
    public ExifReader(InputStream jpegInputStream) throws JpegProcessingException
    {
        this(new JpegSegmentReader(jpegInputStream).readSegment(JpegSegmentReader.SEGMENT_APP1));
    }

    /**
     * Creates an ExifReader for the given Exif data segment.
     * @param data The byte[] from which Exif data should be read.  This value may be null.
     */
    public ExifReader(byte[] data)
    {
        _data = data;
    }

    /**
     * Performs the Exif data extraction, adding found values to the specified
     * instance of <code>Metadata</code>.
     * @param metadata The Metadata object into which extracted values should be merged.
     */
    public void extract(Metadata metadata)
    {
        if (_data==null)
            return;

        ExifDirectory directory = getExifDirectory(metadata);
        // check for the header length
        if (_data.length<=14) {
            directory.addError("Exif data segment must contain at least 14 bytes");
            return;
        }

        // check for the header preamble
        if (!"Exif\0\0".equals(new String(_data, 0, 6))) {
            directory.addError("Exif data segment doesn't begin with 'Exif'");
            return;
        }

        extractIFD(metadata, TIFF_HEADER_START_OFFSET);
    }

    private ExifDirectory getExifDirectory(Metadata metadata)
    {
    	if (_exifDirectory == null) {
    		_exifDirectory = (ExifDirectory) metadata.getDirectory(ExifDirectory.class);
    	}
    	return _exifDirectory;
    }

    /**
     * Performs the Exif data extraction on a Tiff/Raw, adding found values to the specified
     * instance of <code>Metadata</code>.
     * @param metadata The Metadata object into which extracted values should be merged.
     */
    public void extractTiff(Metadata metadata)
    {
    	extractIFD(metadata, 0);
    }

    private void extractIFD(Metadata metadata, int tiffHeaderOffset)
    {
        if (_data==null)
            return;

    	ExifDirectory directory = getExifDirectory(metadata);

        // this should be either "MM" or "II"
        String byteOrderIdentifier = new String(_data, tiffHeaderOffset, 2);
        if (!setByteOrder(byteOrderIdentifier)) {
            directory.addError("Unclear distinction between Motorola/Intel byte ordering: " + byteOrderIdentifier);
            return;
        }

        // Check the next two values for correctness.
        if (get16Bits(2+tiffHeaderOffset)!=0x2a) {
            directory.addError("Invalid Exif start - should have 0x2A at offset 8 in Exif header");
            return;
        }

        int firstDirectoryOffset = get32Bits(4+tiffHeaderOffset) + tiffHeaderOffset;

        // David Ekholm sent a digital camera image that has this problem
        if (firstDirectoryOffset>=_data.length - 1) {
            directory.addError("First exif directory offset is beyond end of Exif data segment");
            // First directory normally starts 14 bytes in -- try it here and catch another error in the worst case
            firstDirectoryOffset = 14;
        }

        HashSet<Integer> processedDirectoryOffsets = new HashSet<Integer>();

        // 0th IFD (we merge with Exif IFD)
        processDirectory(directory, processedDirectoryOffsets, firstDirectoryOffset, tiffHeaderOffset, metadata);

        // after the extraction process, if we have the correct tags, we may be able to store thumbnail information
        storeThumbnailBytes(directory, tiffHeaderOffset);
    }

    private void storeThumbnailBytes(ExifDirectory exifDirectory, int tiffHeaderOffset)
    {
        if (!exifDirectory.containsTag(ExifDirectory.TAG_COMPRESSION))
        	return;

        if (!exifDirectory.containsTag(ExifDirectory.TAG_THUMBNAIL_LENGTH) ||
            !exifDirectory.containsTag(ExifDirectory.TAG_THUMBNAIL_OFFSET))
            return;

        try {
            int offset = exifDirectory.getInt(ExifDirectory.TAG_THUMBNAIL_OFFSET);
            int length = exifDirectory.getInt(ExifDirectory.TAG_THUMBNAIL_LENGTH);

            if (length < 1 || (tiffHeaderOffset + offset + length) > _data.length) {
                exifDirectory.addError("Bad thumbnail length detected.");
                return;
            }

            byte[] result = new byte[length];
            System.arraycopy(_data, tiffHeaderOffset + offset, result, 0, result.length);
            exifDirectory.setByteArray(ExifDirectory.TAG_THUMBNAIL_DATA, result);
        } catch (Throwable e) {
            exifDirectory.addError("Unable to extract thumbnail: " + e.getMessage());
        }
    }

    /**
     * Sets the _isMotorollaByteOrder flag to true or false, depending upon the file's byte order
     * string.  If the string cannot be interpreted, false is returned.
     * @param byteOrderIdentifier a two-character string; either "MM" for Motorolla or "II" for Intel.
     * @return true if successful, otherwise false.
     */
    private boolean setByteOrder(String byteOrderIdentifier)
    {
        if ("MM".equals(byteOrderIdentifier)) {
            _isMotorollaByteOrder = true;
        } else if ("II".equals(byteOrderIdentifier)) {
            _isMotorollaByteOrder = false;
        } else {
            return false;
        }
        return true;
    }

    /**
     * Process one of the nested Tiff IFD directories.
     * 2 bytes: number of tags
     * for each tag
     *   2 bytes: tag type
     *   2 bytes: format code
     *   4 bytes: component count
     */
    private void processDirectory(Directory directory, HashSet<Integer> processedDirectoryOffsets, int dirStartOffset, int tiffHeaderOffset, final Metadata metadata)
    {
        // check for directories we've already visited to avoid stack overflows when recursive/cyclic directory structures exist
        if (processedDirectoryOffsets.contains(new Integer(dirStartOffset)))
            return;

        // remember that we've visited this directory so that we don't visit it again later
        processedDirectoryOffsets.add(dirStartOffset);

        if (dirStartOffset>=_data.length || dirStartOffset<0) {
            directory.addError("Ignored directory marked to start outside data segment");
            return;
        }

        // First two bytes in the IFD are the number of tags in this directory
        int dirTagCount = get16Bits(dirStartOffset);

        int dirLength = (2 + (12 * dirTagCount) + 4);
        if (dirLength + dirStartOffset > _data.length) {
            directory.addError("Illegally sized directory");
            return;
        }

        // Handle each tag in this directory
        for (int tagNumber = 0; tagNumber<dirTagCount; tagNumber++) {
            final int tagOffset = calculateTagOffset(dirStartOffset, tagNumber);

            // 2 bytes for the tag type
            final int tagType = get16Bits(tagOffset);

            // 2 bytes for the format code
            final int formatCode = get16Bits(tagOffset + 2);
            if (formatCode<1 || formatCode>MAX_FORMAT_CODE) {
                directory.addError("Invalid format code: " + formatCode);
                continue;
            }

            // 4 bytes dictate the number of components in this tag's data
            final int componentCount = get32Bits(tagOffset + 4);
            if (componentCount<0) {
                directory.addError("Negative component count in EXIF");
                continue;
            }
            // each component may have more than one byte... calculate the total number of bytes
            final int byteCount = componentCount * BYTES_PER_FORMAT[formatCode];
            final int tagValueOffset = calculateTagValueOffset(byteCount, tagOffset, tiffHeaderOffset);
            if (tagValueOffset<0 || tagValueOffset > _data.length) {
                directory.addError("Illegal pointer offset value in EXIF");
                continue;
            }

            // Check that this tag isn't going to allocate outside the bounds of the data array.
            // This addresses an uncommon OutOfMemoryError.
            if (byteCount < 0 || tagValueOffset + byteCount > _data.length)
            {
                directory.addError("Illegal number of bytes: " + byteCount);
                continue;
            }

            // Calculate the value as an offset for cases where the tag represents directory
            final int subdirOffset = tiffHeaderOffset + get32Bits(tagValueOffset);

            switch (tagType) {
                case TAG_EXIF_OFFSET:
                    processDirectory(metadata.getDirectory(ExifDirectory.class), processedDirectoryOffsets, subdirOffset, tiffHeaderOffset, metadata);
                    continue;
                case TAG_INTEROP_OFFSET:
                    processDirectory(metadata.getDirectory(ExifInteropDirectory.class), processedDirectoryOffsets, subdirOffset, tiffHeaderOffset, metadata);
                    continue;
                case TAG_GPS_INFO_OFFSET:
                    processDirectory(metadata.getDirectory(GpsDirectory.class), processedDirectoryOffsets, subdirOffset, tiffHeaderOffset, metadata);
                    continue;
                case TAG_MAKER_NOTE:
                    processMakerNote(tagValueOffset, processedDirectoryOffsets, tiffHeaderOffset, metadata);
                    continue;
                default:
                    processTag(directory, tagType, tagValueOffset, componentCount, formatCode);
                    break;
            }
        }

        // at the end of each IFD is an optional link to the next IFD
        final int finalTagOffset = calculateTagOffset(dirStartOffset, dirTagCount);
        int nextDirectoryOffset = get32Bits(finalTagOffset);
        if (nextDirectoryOffset!=0) {
            nextDirectoryOffset += tiffHeaderOffset;
            if (nextDirectoryOffset>=_data.length) {
                // Last 4 bytes of IFD reference another IFD with an address that is out of bounds
                // Note this could have been caused by jhead 1.3 cropping too much
                return;
            } else if (nextDirectoryOffset < dirStartOffset) {
                // Last 4 bytes of IFD reference another IFD with an address that is before the start of this directory
                return;
            }
            // the next directory is of same type as this one
            processDirectory(directory, processedDirectoryOffsets, nextDirectoryOffset, tiffHeaderOffset, metadata);
        }
    }

    private void processMakerNote(int subdirOffset, HashSet<Integer> processedDirectoryOffsets, int tiffHeaderOffset, final Metadata metadata)
    {
        // Determine the camera model and makernote format
        Directory exifDirectory = metadata.getDirectory(ExifDirectory.class);

        if (exifDirectory==null)
            return;

        String cameraModel = exifDirectory.getString(ExifDirectory.TAG_MAKE);
        final String firstTwoChars = new String(_data, subdirOffset, 2);
        final String firstThreeChars = new String(_data, subdirOffset, 3);
        final String firstFourChars = new String(_data, subdirOffset, 4);
        final String firstFiveChars = new String(_data, subdirOffset, 5);
        final String firstSixChars = new String(_data, subdirOffset, 6);
        final String firstSevenChars = new String(_data, subdirOffset, 7);
        final String firstEightChars = new String(_data, subdirOffset, 8);
        if ("OLYMP".equals(firstFiveChars) || "EPSON".equals(firstFiveChars) || "AGFA".equals(firstFourChars))
        {
            // Olympus Makernote
            // Epson and Agfa use Olympus maker note standard, see:
            //     http://www.ozhiker.com/electronics/pjmt/jpeg_info/
            processDirectory(metadata.getDirectory(OlympusMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 8, tiffHeaderOffset, metadata);
        }
        else if (cameraModel!=null && cameraModel.trim().toUpperCase().startsWith("NIKON"))
        {
            if ("Nikon".equals(firstFiveChars))
            {
                /* There are two scenarios here:
                 * Type 1:                  **
                 * :0000: 4E 69 6B 6F 6E 00 01 00-05 00 02 00 02 00 06 00 Nikon...........
                 * :0010: 00 00 EC 02 00 00 03 00-03 00 01 00 00 00 06 00 ................
                 * Type 3:                  **
                 * :0000: 4E 69 6B 6F 6E 00 02 00-00 00 4D 4D 00 2A 00 00 Nikon....MM.*...
                 * :0010: 00 08 00 1E 00 01 00 07-00 00 00 04 30 32 30 30 ............0200
                 */
                if (_data[subdirOffset+6]==1)
                    processDirectory(metadata.getDirectory(NikonType1MakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 8, tiffHeaderOffset, metadata);
                else if (_data[subdirOffset+6]==2)
                    processDirectory(metadata.getDirectory(NikonType2MakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 18, subdirOffset + 10, metadata);
                else
                    exifDirectory.addError("Unsupported makernote data ignored.");
            }
            else
            {
                // The IFD begins with the first MakerNote byte (no ASCII name).  This occurs with CoolPix 775, E990 and D1 models.
                processDirectory(metadata.getDirectory(NikonType2MakernoteDirectory.class), processedDirectoryOffsets, subdirOffset, tiffHeaderOffset, metadata);
            }
        }
        else if ("SONY CAM".equals(firstEightChars) || "SONY DSC".equals(firstEightChars))
        {
            processDirectory(metadata.getDirectory(SonyMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 12, tiffHeaderOffset, metadata);
        }
        else if ("KDK".equals(firstThreeChars))
        {
            processDirectory(metadata.getDirectory(KodakMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 20, tiffHeaderOffset, metadata);
        }
        else if ("Canon".equalsIgnoreCase(cameraModel))
        {
            processDirectory(metadata.getDirectory(CanonMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset, tiffHeaderOffset, metadata);
        }
        else if (cameraModel!=null && cameraModel.toUpperCase().startsWith("CASIO"))
        {
            if ("QVC\u0000\u0000\u0000".equals(firstSixChars))
                processDirectory(metadata.getDirectory(CasioType2MakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 6, tiffHeaderOffset, metadata);
            else
                processDirectory(metadata.getDirectory(CasioType1MakernoteDirectory.class), processedDirectoryOffsets, subdirOffset, tiffHeaderOffset, metadata);
        }
        else if ("FUJIFILM".equals(firstEightChars) || "Fujifilm".equalsIgnoreCase(cameraModel))
        {
            // TODO make this field a passed parameter, to avoid threading issues
            boolean byteOrderBefore = _isMotorollaByteOrder;
            // bug in fujifilm makernote ifd means we temporarily use Intel byte ordering
            _isMotorollaByteOrder = false;
            // the 4 bytes after "FUJIFILM" in the makernote point to the start of the makernote
            // IFD, though the offset is relative to the start of the makernote, not the TIFF
            // header (like everywhere else)
            int ifdStart = subdirOffset + get32Bits(subdirOffset + 8);
            processDirectory(metadata.getDirectory(FujifilmMakernoteDirectory.class), processedDirectoryOffsets, ifdStart, tiffHeaderOffset, metadata);
            _isMotorollaByteOrder = byteOrderBefore;
        }
        else if (cameraModel!=null && cameraModel.toUpperCase().startsWith("MINOLTA"))
        {
            // Cases seen with the model starting with MINOLTA in capitals seem to have a valid Olympus makernote
            // area that commences immediately.
            processDirectory(metadata.getDirectory(OlympusMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset, tiffHeaderOffset, metadata);
        }
        else if ("KC".equals(firstTwoChars) || "MINOL".equals(firstFiveChars) || "MLY".equals(firstThreeChars) || "+M+M+M+M".equals(firstEightChars))
        {
            // This Konica data is not understood.  Header identified in accordance with information at this site:
            // http://www.ozhiker.com/electronics/pjmt/jpeg_info/minolta_mn.html
            // TODO determine how to process the information described at the above website
            exifDirectory.addError("Unsupported Konica/Minolta data ignored.");
        }
        else if ("KYOCERA".equals(firstSevenChars))
        {
            // http://www.ozhiker.com/electronics/pjmt/jpeg_info/kyocera_mn.html
            processDirectory(metadata.getDirectory(KyoceraMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 22, tiffHeaderOffset, metadata);
        }
        else if ("Panasonic\u0000\u0000\u0000".equals(new String(_data, subdirOffset, 12)))
        {
            // NON-Standard TIFF IFD Data using Panasonic Tags. There is no Next-IFD pointer after the IFD
            // Offsets are relative to the start of the TIFF header at the beginning of the EXIF segment
            // more information here: http://www.ozhiker.com/electronics/pjmt/jpeg_info/panasonic_mn.html
            processDirectory(metadata.getDirectory(PanasonicMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 12, tiffHeaderOffset, metadata);
        }
        else if ("AOC\u0000".equals(firstFourChars))
        {
            // NON-Standard TIFF IFD Data using Casio Type 2 Tags
            // IFD has no Next-IFD pointer at end of IFD, and
            // Offsets are relative to the start of the current IFD tag, not the TIFF header
            // Observed for:
            // - Pentax ist D
            processDirectory(metadata.getDirectory(CasioType2MakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 6, subdirOffset, metadata);
        }
        else if (cameraModel!=null && (cameraModel.toUpperCase().startsWith("PENTAX") || cameraModel.toUpperCase().startsWith("ASAHI")))
        {
            // NON-Standard TIFF IFD Data using Pentax Tags
            // IFD has no Next-IFD pointer at end of IFD, and
            // Offsets are relative to the start of the current IFD tag, not the TIFF header
            // Observed for:
            // - PENTAX Optio 330
            // - PENTAX Optio 430
            processDirectory(metadata.getDirectory(PentaxMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset, subdirOffset, metadata);
        }
        else
        {
            // TODO how to store makernote data when it's not from a supported camera model?
            // this is difficult as the starting offset is not known.  we could look for it...
            exifDirectory.addError("Unsupported makernote data ignored.");
        }
    }

    private void processTag(Directory directory, int tagType, int tagValueOffset, int componentCount, int formatCode)
    {
        // Directory simply stores raw values
        // The display side uses a Descriptor class per directory to turn the raw values into 'pretty' descriptions
        switch (formatCode)
        {
            case FMT_UNDEFINED:
                // this includes exif user comments
                final byte[] tagBytes = new byte[componentCount];
                final int byteCount = componentCount * BYTES_PER_FORMAT[formatCode];
                System.arraycopy(_data, tagValueOffset, tagBytes, 0, byteCount);
                directory.setByteArray(tagType, tagBytes);
                break;
            case FMT_STRING:
                directory.setString(tagType, readString(tagValueOffset, componentCount));
                break;
            case FMT_SRATIONAL:
            case FMT_URATIONAL:
                if (componentCount==1) {
                    Rational rational = new Rational(get32Bits(tagValueOffset), get32Bits(tagValueOffset + 4));
                    directory.setRational(tagType, rational);
                } else if (componentCount > 1) {
                    Rational[] rationals = new Rational[componentCount];
                    for (int i = 0; i<componentCount; i++)
                        rationals[i] = new Rational(get32Bits(tagValueOffset + (8 * i)), get32Bits(tagValueOffset + 4 + (8 * i)));
                    directory.setRationalArray(tagType, rationals);
                }
                break;
            case FMT_SBYTE:
            case FMT_BYTE:
                if (componentCount==1) {
                    // this may need to be a byte, but I think casting to int is fine
                    int b = _data[tagValueOffset];
                    directory.setInt(tagType, b);
                } else {
                    int[] bytes = new int[componentCount];
                    for (int i = 0; i<componentCount; i++)
                        bytes[i] = _data[tagValueOffset + i];
                    directory.setIntArray(tagType, bytes);
                }
                break;
            case FMT_SINGLE:
            case FMT_DOUBLE:
                if (componentCount==1) {
                    int i = _data[tagValueOffset];
                    directory.setInt(tagType, i);
                } else {
                    int[] ints = new int[componentCount];
                    for (int i = 0; i<componentCount; i++)
                        ints[i] = _data[tagValueOffset + i];
                    directory.setIntArray(tagType, ints);
                }
                break;
            case FMT_USHORT:
            case FMT_SSHORT:
                if (componentCount==1) {
                    int i = get16Bits(tagValueOffset);
                    directory.setInt(tagType, i);
                } else {
                    int[] ints = new int[componentCount];
                    for (int i = 0; i<componentCount; i++)
                        ints[i] = get16Bits(tagValueOffset + (i * 2));
                    directory.setIntArray(tagType, ints);
                }
                break;
            case FMT_SLONG:
            case FMT_ULONG:
                if (componentCount==1) {
                    int i = get32Bits(tagValueOffset);
                    directory.setInt(tagType, i);
                } else {
                    int[] ints = new int[componentCount];
                    for (int i = 0; i<componentCount; i++)
                        ints[i] = get32Bits(tagValueOffset + (i * 4));
                    directory.setIntArray(tagType, ints);
                }
                break;
            default:
                directory.addError("Unknown format code " + formatCode + " for tag " + tagType);
        }
    }

    private int calculateTagValueOffset(int byteCount, int dirEntryOffset, int tiffHeaderOffset)
    {
        if (byteCount>4) {
            // If its bigger than 4 bytes, the dir entry contains an offset.
            // dirEntryOffset must be passed, as some makernote implementations (e.g. FujiFilm) incorrectly use an
            // offset relative to the start of the makernote itself, not the TIFF segment.
            final int offsetVal = get32Bits(dirEntryOffset + 8);
            if (offsetVal + byteCount>_data.length) {
                // Bogus pointer offset and / or bytecount value
                return -1; // signal error
            }
            return tiffHeaderOffset + offsetVal;
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
        while ((offset + length)<_data.length && _data[offset + length]!='\0' && length<maxLength)
            length++;

        return new String(_data, offset, length);
    }

    /**
     * Determine the offset at which a given InteropArray entry begins within the specified IFD.
     * @param dirStartOffset the offset at which the IFD starts
     * @param entryNumber the zero-based entry number
     */
    private int calculateTagOffset(int dirStartOffset, int entryNumber)
    {
        // add 2 bytes for the tag count
        // each entry is 12 bytes, so we skip 12 * the number seen so far
        return dirStartOffset + 2 + (12 * entryNumber);
    }

    /**
     * Get a 16 bit value from file's native byte order.  Between 0x0000 and 0xFFFF.
     */
    private int get16Bits(int offset)
    {
        if (offset<0 || offset+2>_data.length)
            throw new ArrayIndexOutOfBoundsException("attempt to read data outside of exif segment (index " + offset + " where max index is " + (_data.length - 1) + ")");

        if (_isMotorollaByteOrder) {
            // Motorola - MSB first
            return (_data[offset] << 8 & 0xFF00) | (_data[offset + 1] & 0xFF);
        } else {
            // Intel ordering - LSB first
            return (_data[offset + 1] << 8 & 0xFF00) | (_data[offset] & 0xFF);
        }
    }

    /**
     * Get a 32 bit value from file's native byte order.
     */
    private int get32Bits(int offset)
    {
        if (offset<0 || offset+4>_data.length)
            throw new ArrayIndexOutOfBoundsException("attempt to read data outside of exif segment (index " + offset + " where max index is " + (_data.length - 1) + ")");

        if (_isMotorollaByteOrder) {
            // Motorola - MSB first
            return (_data[offset] << 24 & 0xFF000000) |
                    (_data[offset + 1] << 16 & 0xFF0000) |
                    (_data[offset + 2] << 8 & 0xFF00) |
                    (_data[offset + 3] & 0xFF);
        } else {
            // Intel ordering - LSB first
            return (_data[offset + 3] << 24 & 0xFF000000) |
                    (_data[offset + 2] << 16 & 0xFF0000) |
                    (_data[offset + 1] << 8 & 0xFF00) |
                    (_data[offset] & 0xFF);
        }
    }
}

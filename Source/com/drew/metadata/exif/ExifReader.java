/*
 * Copyright 2002-2013 Drew Noakes
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.metadata.exif;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.BufferBoundsException;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Decodes Exif binary data, populating a {@link Metadata} object with tag values in {@link ExifSubIFDDirectory},
 * {@link ExifThumbnailDirectory}, {@link ExifInteropDirectory}, {@link GpsDirectory} and one of the many camera makernote directories.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class ExifReader implements JpegSegmentMetadataReader
{
    // TODO extract a reusable TiffReader from this class with hooks for special tag handling and subdir following
    
    /** The number of bytes used per format descriptor. */
    @NotNull
    private static final int[] BYTES_PER_FORMAT = { 0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8 };

    /** The number of formats known. */
    private static final int MAX_FORMAT_CODE = 12;

    // Format types
    // TODO use an enum for these?
    /** An 8-bit unsigned integer. */
    private static final int FMT_BYTE = 1;
    /** A fixed-length character string. */
    private static final int FMT_STRING = 2;
    /** An unsigned 16-bit integer. */
    private static final int FMT_USHORT = 3;
    /** An unsigned 32-bit integer. */
    private static final int FMT_ULONG = 4;
    private static final int FMT_URATIONAL = 5;
    /** An 8-bit signed integer. */
    private static final int FMT_SBYTE = 6;
    private static final int FMT_UNDEFINED = 7;
    /** A signed 16-bit integer. */
    private static final int FMT_SSHORT = 8;
    /** A signed 32-bit integer. */
    private static final int FMT_SLONG = 9;
    private static final int FMT_SRATIONAL = 10;
    /** A 32-bit floating point number. */
    private static final int FMT_SINGLE = 11;
    /** A 64-bit floating point number. */
    private static final int FMT_DOUBLE = 12;

    /** This tag is a pointer to the Exif SubIFD. */
    public static final int TAG_EXIF_SUB_IFD_OFFSET = 0x8769;
    /** This tag is a pointer to the Exif Interop IFD. */
    public static final int TAG_INTEROP_OFFSET = 0xA005;
    /** This tag is a pointer to the Exif GPS IFD. */
    public static final int TAG_GPS_INFO_OFFSET = 0x8825;
    /** This tag is a pointer to the Exif Makernote IFD. */
    public static final int TAG_MAKER_NOTE_OFFSET = 0x927C;

    public static final int TIFF_HEADER_START_OFFSET = 6;

    @NotNull
    @Override
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Arrays.asList(JpegSegmentType.APP1);
    }

    @Override
    public boolean canProcess(@NotNull byte[] segmentBytes, @NotNull JpegSegmentType segmentType)
    {
        return segmentBytes.length > 3 && "EXIF".equalsIgnoreCase(new String(segmentBytes, 0, 4));
    }

    @Override
    public void extract(@NotNull byte[] segmentBytes, @NotNull Metadata metadata, @NotNull JpegSegmentType segmentType)
    {
        extract(new ByteArrayReader(segmentBytes), metadata);
    }

    /**
     * Performs the Exif data extraction, adding found values to the specified
     * instance of {@link Metadata}.
     *
     * @param reader   The buffer reader from which Exif data should be read.
     * @param metadata The Metadata object into which extracted values should be merged.
     */
    public void extract(@NotNull final RandomAccessReader reader, @NotNull Metadata metadata)
    {
        final ExifSubIFDDirectory directory = metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);

        // check for the header length
        try {
            if (reader.getLength() <= 14) {
                directory.addError("Exif data segment must contain at least 14 bytes");
                return;
            }
        } catch (BufferBoundsException e) {
            directory.addError("Unable to read Exif data: " + e.getMessage());
            return;
        }

        // check for the header preamble
        try {
            if (!reader.getString(0, 6).equals("Exif\0\0")) {
                directory.addError("Exif data segment doesn't begin with 'Exif'");
                return;
            }

            extractIFD(metadata, metadata.getOrCreateDirectory(ExifIFD0Directory.class), TIFF_HEADER_START_OFFSET, reader);
        } catch (BufferBoundsException e) {
            directory.addError("Exif data segment ended prematurely");
        }
    }

    /**
     * Performs the Exif data extraction on a TIFF/RAW, adding found values to the specified
     * instance of {@link Metadata}.
     *
     * @param reader   The {@link RandomAccessReader} from which TIFF data should be read.
     * @param metadata The Metadata object into which extracted values should be merged.
     */
    public void extractTiff(@NotNull RandomAccessReader reader, @NotNull Metadata metadata)
    {
        final ExifIFD0Directory directory = metadata.getOrCreateDirectory(ExifIFD0Directory.class);

        try {
            extractIFD(metadata, directory, 0, reader);
        } catch (BufferBoundsException e) {
            directory.addError("Exif data segment ended prematurely");
        }
    }

    private void extractIFD(@NotNull Metadata metadata, @NotNull final ExifIFD0Directory directory, int tiffHeaderOffset, @NotNull RandomAccessReader reader) throws BufferBoundsException
    {
        // this should be either "MM" or "II"
        String byteOrderIdentifier = reader.getString(tiffHeaderOffset, 2);

        if ("MM".equals(byteOrderIdentifier)) {
            reader.setMotorolaByteOrder(true);
        } else if ("II".equals(byteOrderIdentifier)) {
            reader.setMotorolaByteOrder(false);
        } else {
            directory.addError("Unclear distinction between Motorola/Intel byte ordering: " + byteOrderIdentifier);
            return;
        }

        // Check the next two values for correctness.
        final int tiffMarker = reader.getUInt16(2 + tiffHeaderOffset);

        final int standardTiffMarker = 0x002A;
        final int olympusRawTiffMarker = 0x4F52; // for ORF files
        final int panasonicRawTiffMarker = 0x0055; // for RW2 files

        if (tiffMarker != standardTiffMarker && tiffMarker != olympusRawTiffMarker && tiffMarker != panasonicRawTiffMarker) {
            directory.addError("Unexpected TIFF marker after byte order identifier: 0x" + Integer.toHexString(tiffMarker));
            return;
        }

        int firstDirectoryOffset = reader.getInt32(4 + tiffHeaderOffset) + tiffHeaderOffset;

        // David Ekholm sent a digital camera image that has this problem
        // TODO getLength should be avoided as it causes RandomAccessStreamReader to read to the end of the stream
        if (firstDirectoryOffset >= reader.getLength() - 1) {
            directory.addError("First Exif directory offset is beyond end of Exif data segment");
            // First directory normally starts 14 bytes in -- try it here and catch another error in the worst case
            firstDirectoryOffset = 14;
        }

        Set<Integer> processedDirectoryOffsets = new HashSet<Integer>();

        processDirectory(directory, processedDirectoryOffsets, firstDirectoryOffset, tiffHeaderOffset, metadata, reader);

        // after the extraction process, if we have the correct tags, we may be able to store thumbnail information
        ExifThumbnailDirectory thumbnailDirectory = metadata.getDirectory(ExifThumbnailDirectory.class);
        if (thumbnailDirectory!=null && thumbnailDirectory.containsTag(ExifThumbnailDirectory.TAG_THUMBNAIL_COMPRESSION)) {
            Integer offset = thumbnailDirectory.getInteger(ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET);
            Integer length = thumbnailDirectory.getInteger(ExifThumbnailDirectory.TAG_THUMBNAIL_LENGTH);
            if (offset != null && length != null) {
                try {
                    byte[] thumbnailData = reader.getBytes(tiffHeaderOffset + offset, length);
                    thumbnailDirectory.setThumbnailData(thumbnailData);
                } catch (BufferBoundsException ex) {
                    directory.addError("Invalid thumbnail data specification: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Process one of the nested Tiff IFD directories.
     * <p/>
     * Header
     * 2 bytes: number of tags
     * <p/>
     * Then for each tag
     * 2 bytes: tag type
     * 2 bytes: format code
     * 4 bytes: component count
     */
    private void processDirectory(@NotNull Directory directory, @NotNull Set<Integer> processedDirectoryOffsets, int dirStartOffset, int tiffHeaderOffset, @NotNull final Metadata metadata, @NotNull final RandomAccessReader reader) throws BufferBoundsException
    {
        // check for directories we've already visited to avoid stack overflows when recursive/cyclic directory structures exist
        if (processedDirectoryOffsets.contains(Integer.valueOf(dirStartOffset)))
            return;

        // remember that we've visited this directory so that we don't visit it again later
        processedDirectoryOffsets.add(dirStartOffset);

        if (dirStartOffset >= reader.getLength() || dirStartOffset < 0) {
            directory.addError("Ignored directory marked to start outside data segment");
            return;
        }

        // First two bytes in the IFD are the number of tags in this directory
        int dirTagCount = reader.getUInt16(dirStartOffset);

        int dirLength = (2 + (12 * dirTagCount) + 4);
        if (dirLength + dirStartOffset > reader.getLength()) {
            directory.addError("Illegally sized directory");
            return;
        }

        // Handle each tag in this directory
        for (int tagNumber = 0; tagNumber < dirTagCount; tagNumber++) {
            final int tagOffset = calculateTagOffset(dirStartOffset, tagNumber);

            // 2 bytes for the tag type
            final int tagType = reader.getUInt16(tagOffset);

            // 2 bytes for the format code
            final int formatCode = reader.getUInt16(tagOffset + 2);
            if (formatCode < 1 || formatCode > MAX_FORMAT_CODE) {
                // This error suggests that we are processing at an incorrect index and will generate
                // rubbish until we go out of bounds (which may be a while).  Exit now.
                directory.addError("Invalid TIFF tag format code: " + formatCode);
                return;
            }

            // 4 bytes dictate the number of components in this tag's data
            final int componentCount = reader.getInt32(tagOffset + 4);
            if (componentCount < 0) {
                directory.addError("Negative TIFF tag component count");
                continue;
            }
            // each component may have more than one byte... calculate the total number of bytes
            final int byteCount = componentCount * BYTES_PER_FORMAT[formatCode];
            final int tagValueOffset;
            if (byteCount > 4) {
                // If it's bigger than 4 bytes, the dir entry contains an offset.
                // dirEntryOffset must be passed, as some makernote implementations (e.g. FujiFilm) incorrectly use an
                // offset relative to the start of the makernote itself, not the TIFF segment.
                final int offsetVal = reader.getInt32(tagOffset + 8);
                if (offsetVal + byteCount > reader.getLength()) {
                    // Bogus pointer offset and / or byteCount value
                    directory.addError("Illegal TIFF tag pointer offset");
                    continue;
                }
                tagValueOffset = tiffHeaderOffset + offsetVal;
            } else {
                // 4 bytes or less and value is in the dir entry itself
                tagValueOffset = tagOffset + 8;
            }

            if (tagValueOffset < 0 || tagValueOffset > reader.getLength()) {
                directory.addError("Illegal TIFF tag pointer offset");
                continue;
            }

            // Check that this tag isn't going to allocate outside the bounds of the data array.
            // This addresses an uncommon OutOfMemoryError.
            if (byteCount < 0 || tagValueOffset + byteCount > reader.getLength()) {
                directory.addError("Illegal number of bytes: " + byteCount);
                continue;
            }

            switch (tagType) {
                case TAG_EXIF_SUB_IFD_OFFSET: {
                    final int subdirOffset = tiffHeaderOffset + reader.getInt32(tagValueOffset);
                    processDirectory(metadata.getOrCreateDirectory(ExifSubIFDDirectory.class), processedDirectoryOffsets, subdirOffset, tiffHeaderOffset, metadata, reader);
                    continue;
                }
                case TAG_INTEROP_OFFSET: {
                    final int subdirOffset = tiffHeaderOffset + reader.getInt32(tagValueOffset);
                    processDirectory(metadata.getOrCreateDirectory(ExifInteropDirectory.class), processedDirectoryOffsets, subdirOffset, tiffHeaderOffset, metadata, reader);
                    continue;
                }
                case TAG_GPS_INFO_OFFSET: {
                    final int subdirOffset = tiffHeaderOffset + reader.getInt32(tagValueOffset);
                    processDirectory(metadata.getOrCreateDirectory(GpsDirectory.class), processedDirectoryOffsets, subdirOffset, tiffHeaderOffset, metadata, reader);
                    continue;
                }
                case TAG_MAKER_NOTE_OFFSET: {
                    processMakerNote(tagValueOffset, processedDirectoryOffsets, tiffHeaderOffset, metadata, reader);
                    continue;
                }
                default: {
                    processTag(directory, tagType, tagValueOffset, componentCount, formatCode, reader);
                    break;
                }
            }
        }

        // at the end of each IFD is an optional link to the next IFD
        final int finalTagOffset = calculateTagOffset(dirStartOffset, dirTagCount);
        int nextDirectoryOffset = reader.getInt32(finalTagOffset);
        if (nextDirectoryOffset != 0) {
            nextDirectoryOffset += tiffHeaderOffset;
            if (nextDirectoryOffset >= reader.getLength()) {
                // Last 4 bytes of IFD reference another IFD with an address that is out of bounds
                // Note this could have been caused by jhead 1.3 cropping too much
                return;
            } else if (nextDirectoryOffset < dirStartOffset) {
                // Last 4 bytes of IFD reference another IFD with an address that is before the start of this directory
                return;
            }
            // TODO in Exif, the only known 'follower' IFD is the thumbnail one, however this may not be the case
            final ExifThumbnailDirectory nextDirectory = metadata.getOrCreateDirectory(ExifThumbnailDirectory.class);
            processDirectory(nextDirectory, processedDirectoryOffsets, nextDirectoryOffset, tiffHeaderOffset, metadata, reader);
        }
    }

    private void processMakerNote(int subdirOffset, @NotNull Set<Integer> processedDirectoryOffsets, int tiffHeaderOffset, @NotNull final Metadata metadata, @NotNull RandomAccessReader reader) throws BufferBoundsException
    {
        // Determine the camera model and makernote format
        Directory ifd0Directory = metadata.getDirectory(ExifIFD0Directory.class);

        if (ifd0Directory==null)
            return;

        String cameraModel = ifd0Directory.getString(ExifIFD0Directory.TAG_MAKE);

        //final String firstTwoChars = reader.getString(subdirOffset, 2);
        final String firstThreeChars = reader.getString(subdirOffset, 3);
        final String firstFourChars = reader.getString(subdirOffset, 4);
        final String firstFiveChars = reader.getString(subdirOffset, 5);
        final String firstSixChars = reader.getString(subdirOffset, 6);
        final String firstSevenChars = reader.getString(subdirOffset, 7);
        final String firstEightChars = reader.getString(subdirOffset, 8);
        final String firstTwelveChars = reader.getString(subdirOffset, 12);

        if ("OLYMP".equals(firstFiveChars) || "EPSON".equals(firstFiveChars) || "AGFA".equals(firstFourChars)) {
            // Olympus Makernote
            // Epson and Agfa use Olympus maker note standard: http://www.ozhiker.com/electronics/pjmt/jpeg_info/
            processDirectory(metadata.getOrCreateDirectory(OlympusMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 8, tiffHeaderOffset, metadata, reader);
        } else if (cameraModel != null && cameraModel.trim().toUpperCase().startsWith("NIKON")) {
            if ("Nikon".equals(firstFiveChars)) {
                /* There are two scenarios here:
                 * Type 1:                  **
                 * :0000: 4E 69 6B 6F 6E 00 01 00-05 00 02 00 02 00 06 00 Nikon...........
                 * :0010: 00 00 EC 02 00 00 03 00-03 00 01 00 00 00 06 00 ................
                 * Type 3:                  **
                 * :0000: 4E 69 6B 6F 6E 00 02 00-00 00 4D 4D 00 2A 00 00 Nikon....MM.*...
                 * :0010: 00 08 00 1E 00 01 00 07-00 00 00 04 30 32 30 30 ............0200
                 */
                switch (reader.getUInt8(subdirOffset + 6)) {
                    case 1:
                        processDirectory(metadata.getOrCreateDirectory(NikonType1MakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 8, tiffHeaderOffset, metadata, reader);
                        break;
                    case 2:
                        processDirectory(metadata.getOrCreateDirectory(NikonType2MakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 18, subdirOffset + 10, metadata, reader);
                        break;
                    default:
                        ifd0Directory.addError("Unsupported Nikon makernote data ignored.");
                        break;
                }
            } else {
                // The IFD begins with the first MakerNote byte (no ASCII name).  This occurs with CoolPix 775, E990 and D1 models.
                processDirectory(metadata.getOrCreateDirectory(NikonType2MakernoteDirectory.class), processedDirectoryOffsets, subdirOffset, tiffHeaderOffset, metadata, reader);
            }
        } else if ("SONY CAM".equals(firstEightChars) || "SONY DSC".equals(firstEightChars)) {
            processDirectory(metadata.getOrCreateDirectory(SonyType1MakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 12, tiffHeaderOffset, metadata, reader);
        } else if ("SEMC MS\u0000\u0000\u0000\u0000\u0000".equals(firstTwelveChars)) {
            // force MM for this directory
            boolean isMotorola = reader.isMotorolaByteOrder();
            reader.setMotorolaByteOrder(true);
            // skip 12 byte header + 2 for "MM" + 6
            processDirectory(metadata.getOrCreateDirectory(SonyType6MakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 20, tiffHeaderOffset, metadata, reader);
            reader.setMotorolaByteOrder(isMotorola);
        } else if ("SIGMA\u0000\u0000\u0000".equals(firstEightChars) || "FOVEON\u0000\u0000".equals(firstEightChars)) {
            processDirectory(metadata.getOrCreateDirectory(SigmaMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 10, tiffHeaderOffset, metadata, reader);
        } else if ("KDK".equals(firstThreeChars)) {
            processDirectory(metadata.getOrCreateDirectory(KodakMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 20, tiffHeaderOffset, metadata, reader);
        } else if ("Canon".equalsIgnoreCase(cameraModel)) {
            processDirectory(metadata.getOrCreateDirectory(CanonMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset, tiffHeaderOffset, metadata, reader);
        } else if (cameraModel != null && cameraModel.toUpperCase().startsWith("CASIO")) {
            if ("QVC\u0000\u0000\u0000".equals(firstSixChars))
                processDirectory(metadata.getOrCreateDirectory(CasioType2MakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 6, tiffHeaderOffset, metadata, reader);
            else
                processDirectory(metadata.getOrCreateDirectory(CasioType1MakernoteDirectory.class), processedDirectoryOffsets, subdirOffset, tiffHeaderOffset, metadata, reader);
        } else if ("FUJIFILM".equals(firstEightChars) || "Fujifilm".equalsIgnoreCase(cameraModel)) {
            boolean byteOrderBefore = reader.isMotorolaByteOrder();
            // bug in fujifilm makernote ifd means we temporarily use Intel byte ordering
            reader.setMotorolaByteOrder(false);
            // the 4 bytes after "FUJIFILM" in the makernote point to the start of the makernote
            // IFD, though the offset is relative to the start of the makernote, not the TIFF
            // header (like everywhere else)
            int ifdStart = subdirOffset + reader.getInt32(subdirOffset + 8);
            processDirectory(metadata.getOrCreateDirectory(FujifilmMakernoteDirectory.class), processedDirectoryOffsets, ifdStart, tiffHeaderOffset, metadata, reader);
            reader.setMotorolaByteOrder(byteOrderBefore);
        } else if (cameraModel != null && cameraModel.toUpperCase().startsWith("MINOLTA")) {
            // Cases seen with the model starting with MINOLTA in capitals seem to have a valid Olympus makernote
            // area that commences immediately.
            processDirectory(metadata.getOrCreateDirectory(OlympusMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset, tiffHeaderOffset, metadata, reader);
        } else if ("KYOCERA".equals(firstSevenChars)) {
            // http://www.ozhiker.com/electronics/pjmt/jpeg_info/kyocera_mn.html
            processDirectory(metadata.getOrCreateDirectory(KyoceraMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 22, tiffHeaderOffset, metadata, reader);
        } else if ("Panasonic\u0000\u0000\u0000".equals(reader.getString(subdirOffset, 12))) {
            // NON-Standard TIFF IFD Data using Panasonic Tags. There is no Next-IFD pointer after the IFD
            // Offsets are relative to the start of the TIFF header at the beginning of the EXIF segment
            // more information here: http://www.ozhiker.com/electronics/pjmt/jpeg_info/panasonic_mn.html
            processDirectory(metadata.getOrCreateDirectory(PanasonicMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 12, tiffHeaderOffset, metadata, reader);
        } else if ("AOC\u0000".equals(firstFourChars)) {
            // NON-Standard TIFF IFD Data using Casio Type 2 Tags
            // IFD has no Next-IFD pointer at end of IFD, and
            // Offsets are relative to the start of the current IFD tag, not the TIFF header
            // Observed for:
            // - Pentax ist D
            processDirectory(metadata.getOrCreateDirectory(CasioType2MakernoteDirectory.class), processedDirectoryOffsets, subdirOffset + 6, subdirOffset, metadata, reader);
        } else if (cameraModel != null && (cameraModel.toUpperCase().startsWith("PENTAX") || cameraModel.toUpperCase().startsWith("ASAHI"))) {
            // NON-Standard TIFF IFD Data using Pentax Tags
            // IFD has no Next-IFD pointer at end of IFD, and
            // Offsets are relative to the start of the current IFD tag, not the TIFF header
            // Observed for:
            // - PENTAX Optio 330
            // - PENTAX Optio 430
            processDirectory(metadata.getOrCreateDirectory(PentaxMakernoteDirectory.class), processedDirectoryOffsets, subdirOffset, subdirOffset, metadata, reader);
//        } else if ("KC".equals(firstTwoChars) || "MINOL".equals(firstFiveChars) || "MLY".equals(firstThreeChars) || "+M+M+M+M".equals(firstEightChars)) {
//            // This Konica data is not understood.  Header identified in accordance with information at this site:
//            // http://www.ozhiker.com/electronics/pjmt/jpeg_info/minolta_mn.html
//            // TODO add support for minolta/konica cameras
//            exifDirectory.addError("Unsupported Konica/Minolta data ignored.");
        } else {
            // TODO how to store makernote data when it's not from a supported camera model?
            // this is difficult as the starting offset is not known.  we could look for it...
        }
    }

    private void processTag(@NotNull Directory directory, int tagType, int tagValueOffset, int componentCount, int formatCode, @NotNull final RandomAccessReader reader) throws BufferBoundsException
    {
        // Directory simply stores raw values
        // The display side uses a Descriptor class per directory to turn the raw values into 'pretty' descriptions
        switch (formatCode) {
            case FMT_UNDEFINED:
                // this includes exif user comments
                directory.setByteArray(tagType, reader.getBytes(tagValueOffset, componentCount));
                break;
            case FMT_STRING:
                String string = reader.getNullTerminatedString(tagValueOffset, componentCount);
                directory.setString(tagType, string);
/*
                // special handling for certain known tags, proposed by Yuri Binev but left out for now,
                // as it gives the false impression that the image was captured in the same timezone
                // in which the string is parsed
                if (tagType==ExifSubIFDDirectory.TAG_DATETIME ||
                    tagType==ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL ||
                    tagType==ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED) {
                    String[] datePatterns = {
                        "yyyy:MM:dd HH:mm:ss",
                        "yyyy:MM:dd HH:mm",
                        "yyyy-MM-dd HH:mm:ss",
                        "yyyy-MM-dd HH:mm"};
                    for (String datePattern : datePatterns) {
                        try {
                            DateFormat parser = new SimpleDateFormat(datePattern);
                            Date date = parser.parse(string);
                            directory.setDate(tagType, date);
                            break;
                        } catch (ParseException ex) {
                            // simply try the next pattern
                        }
                    }
                }
*/
                break;
            case FMT_SRATIONAL:
                if (componentCount == 1) {
                    directory.setRational(tagType, new Rational(reader.getInt32(tagValueOffset), reader.getInt32(tagValueOffset + 4)));
                } else if (componentCount > 1) {
                    Rational[] rationals = new Rational[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        rationals[i] = new Rational(reader.getInt32(tagValueOffset + (8 * i)), reader.getInt32(tagValueOffset + 4 + (8 * i)));
                    directory.setRationalArray(tagType, rationals);
                }
                break;
            case FMT_URATIONAL:
                if (componentCount == 1) {
                    directory.setRational(tagType, new Rational(reader.getUInt32(tagValueOffset), reader.getUInt32(tagValueOffset + 4)));
                } else if (componentCount > 1) {
                    Rational[] rationals = new Rational[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        rationals[i] = new Rational(reader.getUInt32(tagValueOffset + (8 * i)), reader.getUInt32(tagValueOffset + 4 + (8 * i)));
                    directory.setRationalArray(tagType, rationals);
                }
                break;
            case FMT_SINGLE:
                if (componentCount == 1) {
                    directory.setFloat(tagType, reader.getFloat32(tagValueOffset));
                } else {
                    float[] floats = new float[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        floats[i] = reader.getFloat32(tagValueOffset + (i * 4));
                    directory.setFloatArray(tagType, floats);
                }
                break;
            case FMT_DOUBLE:
                if (componentCount == 1) {
                    directory.setDouble(tagType, reader.getDouble64(tagValueOffset));
                } else {
                    double[] doubles = new double[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        doubles[i] = reader.getDouble64(tagValueOffset + (i * 4));
                    directory.setDoubleArray(tagType, doubles);
                }
                break;

            //
            // Note that all integral types are stored as int32 internally (the largest supported by TIFF)
            //

            case FMT_SBYTE:
                if (componentCount == 1) {
                    directory.setInt(tagType, reader.getInt8(tagValueOffset));
                } else {
                    int[] bytes = new int[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        bytes[i] = reader.getInt8(tagValueOffset + i);
                    directory.setIntArray(tagType, bytes);
                }
                break;
            case FMT_BYTE:
                if (componentCount == 1) {
                    directory.setInt(tagType, reader.getUInt8(tagValueOffset));
                } else {
                    int[] bytes = new int[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        bytes[i] = reader.getUInt8(tagValueOffset + i);
                    directory.setIntArray(tagType, bytes);
                }
                break;
            case FMT_USHORT:
                if (componentCount == 1) {
                    int i = reader.getUInt16(tagValueOffset);
                    directory.setInt(tagType, i);
                } else {
                    int[] ints = new int[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        ints[i] = reader.getUInt16(tagValueOffset + (i * 2));
                    directory.setIntArray(tagType, ints);
                }
                break;
            case FMT_SSHORT:
                if (componentCount == 1) {
                    int i = reader.getInt16(tagValueOffset);
                    directory.setInt(tagType, i);
                } else {
                    int[] ints = new int[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        ints[i] = reader.getInt16(tagValueOffset + (i * 2));
                    directory.setIntArray(tagType, ints);
                }
                break;
            case FMT_SLONG:
            case FMT_ULONG:
                // NOTE 'long' in this case means 32 bit, not 64
                if (componentCount == 1) {
                    int i = reader.getInt32(tagValueOffset);
                    directory.setInt(tagType, i);
                } else {
                    int[] ints = new int[componentCount];
                    for (int i = 0; i < componentCount; i++)
                        ints[i] = reader.getInt32(tagValueOffset + (i * 4));
                    directory.setIntArray(tagType, ints);
                }
                break;
            default:
                directory.addError("Unknown format code " + formatCode + " for tag " + tagType);
        }
    }

    /**
     * Determine the offset at which a given InteropArray entry begins within the specified IFD.
     *
     * @param dirStartOffset the offset at which the IFD starts
     * @param entryNumber    the zero-based entry number
     */
    private int calculateTagOffset(int dirStartOffset, int entryNumber)
    {
        // add 2 bytes for the tag count
        // each entry is 12 bytes, so we skip 12 * the number seen so far
        return dirStartOffset + 2 + (12 * entryNumber);
    }
}

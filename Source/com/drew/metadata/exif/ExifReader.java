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
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.metadata.exif;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.imaging.tiff.TiffProcessingException;
import com.drew.imaging.tiff.TiffReader;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.makernotes.*;

import java.io.IOException;
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

    /**
     * The offset at which the TIFF data actually starts. This may be necessary when, for example, processing
     * JPEG Exif data from APP0 which has a 6-byte preamble before starting the TIFF data.
     */
    private static final String JPEG_EXIF_SEGMENT_PREAMBLE = "Exif\0\0";

    private boolean _storeThumbnailBytes = true;

    public boolean isStoreThumbnailBytes()
    {
        return _storeThumbnailBytes;
    }

    public void setStoreThumbnailBytes(boolean storeThumbnailBytes)
    {
        _storeThumbnailBytes = storeThumbnailBytes;
    }

    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Arrays.asList(JpegSegmentType.APP1);
    }

    public boolean canProcess(@NotNull final byte[] segmentBytes, @NotNull final JpegSegmentType segmentType)
    {
        return segmentBytes.length >= JPEG_EXIF_SEGMENT_PREAMBLE.length() && new String(segmentBytes, 0, JPEG_EXIF_SEGMENT_PREAMBLE.length()).equalsIgnoreCase(JPEG_EXIF_SEGMENT_PREAMBLE);
    }

    public void extract(@NotNull final byte[] segmentBytes, @NotNull final Metadata metadata, @NotNull final JpegSegmentType segmentType)
    {
        if (segmentBytes == null)
            throw new NullPointerException("segmentBytes cannot be null");
        if (metadata == null)
            throw new NullPointerException("metadata cannot be null");
        if (segmentType == null)
            throw new NullPointerException("segmentType cannot be null");

        try {
            ByteArrayReader reader = new ByteArrayReader(segmentBytes);

            //
            // Check for the header preamble
            //
            try {
                if (!reader.getString(0, JPEG_EXIF_SEGMENT_PREAMBLE.length()).equals(JPEG_EXIF_SEGMENT_PREAMBLE)) {
                    // TODO what do to with this error state?
                    System.err.println("Invalid JPEG Exif segment preamble");
                    return;
                }
            } catch (IOException e) {
                // TODO what do to with this error state?
                e.printStackTrace(System.err);
                return;
            }

            //
            // Read the TIFF-formatted Exif data
            //
            new TiffReader().processTiff(
                reader,
                new ExifTiffHandler(metadata, _storeThumbnailBytes),
                JPEG_EXIF_SEGMENT_PREAMBLE.length()
            );

        } catch (TiffProcessingException e) {
            // TODO what do to with this error state?
            e.printStackTrace(System.err);
        } catch (IOException e) {
            // TODO what do to with this error state?
            e.printStackTrace(System.err);
        }
    }

    /**
     * Performs the Exif data extraction on a TIFF/RAW, adding found values to the specified
     * instance of {@link Metadata}.
     *
     * @param reader   The {@link RandomAccessReader} from which TIFF data should be read.
     * @param metadata The Metadata object into which extracted values should be merged.
     */
    @Deprecated
    public void extractTiff(@NotNull final RandomAccessReader reader, @NotNull final Metadata metadata)
    {
        final ExifIFD0Directory directory = metadata.getOrCreateDirectory(ExifIFD0Directory.class);

        try {
            extractTiff(reader, metadata, directory, 0);
        } catch (IOException e) {
            directory.addError("IO problem: " + e.getMessage());
        }
    }

    @Deprecated
    private static void extractTiff(@NotNull final RandomAccessReader reader,
                                    @NotNull final Metadata metadata,
                                    @NotNull final Directory firstDirectory,
                                    final int tiffHeaderOffset) throws IOException
    {
        // this should be either "MM" or "II"
        String byteOrderIdentifier = reader.getString(tiffHeaderOffset, 2);

        if ("MM".equals(byteOrderIdentifier)) {
            reader.setMotorolaByteOrder(true);
        } else if ("II".equals(byteOrderIdentifier)) {
            reader.setMotorolaByteOrder(false);
        } else {
            firstDirectory.addError("Unclear distinction between Motorola/Intel byte ordering: " + byteOrderIdentifier);
            return;
        }

        // Check the next two values for correctness.
        final int tiffMarker = reader.getUInt16(2 + tiffHeaderOffset);

        final int standardTiffMarker = 0x002A;
        final int olympusRawTiffMarker = 0x4F52; // for ORF files
        final int panasonicRawTiffMarker = 0x0055; // for RW2 files

        if (tiffMarker != standardTiffMarker && tiffMarker != olympusRawTiffMarker && tiffMarker != panasonicRawTiffMarker) {
            firstDirectory.addError("Unexpected TIFF marker after byte order identifier: 0x" + Integer.toHexString(tiffMarker));
            return;
        }

        int firstIfdOffset = reader.getInt32(4 + tiffHeaderOffset) + tiffHeaderOffset;

        // David Ekholm sent a digital camera image that has this problem
        // TODO getLength should be avoided as it causes RandomAccessStreamReader to read to the end of the stream
        if (firstIfdOffset >= reader.getLength() - 1) {
            firstDirectory.addError("First Exif directory offset is beyond end of Exif data segment");
            // First directory normally starts 14 bytes in -- try it here and catch another error in the worst case
            firstIfdOffset = 14;
        }

        Set<Integer> processedIfdOffsets = new HashSet<Integer>();
        processIFD(firstDirectory, processedIfdOffsets, firstIfdOffset, tiffHeaderOffset, metadata, reader);

        // after the extraction process, if we have the correct tags, we may be able to store thumbnail information
        ExifThumbnailDirectory thumbnailDirectory = metadata.getDirectory(ExifThumbnailDirectory.class);
        if (thumbnailDirectory != null && thumbnailDirectory.containsTag(ExifThumbnailDirectory.TAG_THUMBNAIL_COMPRESSION)) {
            Integer offset = thumbnailDirectory.getInteger(ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET);
            Integer length = thumbnailDirectory.getInteger(ExifThumbnailDirectory.TAG_THUMBNAIL_LENGTH);
            if (offset != null && length != null) {
                try {
                    byte[] thumbnailData = reader.getBytes(tiffHeaderOffset + offset, length);
                    thumbnailDirectory.setThumbnailData(thumbnailData);
                } catch (IOException ex) {
                    firstDirectory.addError("Invalid thumbnail data specification: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Processes a TIFF IFD, storing tag values in the specified {@link Directory}.
     * <p/>
     * IFD Header:
     * <ul>
     *     <li><b>2 bytes</b> number of tags</li>
     * </ul>
     * Tag structure:
     * <ul>
     *     <li><b>2 bytes</b> tag type</li>
     *     <li><b>2 bytes</b> format code (values 1 to 12, inclusive)</li>
     *     <li><b>4 bytes</b> component count</li>
     *     <li><b>4 bytes</b> inline value, or offset pointer if too large to fit in four bytes</li>
     * </ul>
     *
     * @param directory the {@link Directory} to write extracted values into
     * @param processedIfdOffsets the set of visited IFD offsets, to avoid revisiting the same IFD in an endless loop
     * @param ifdOffset the offset within <code>reader</code> at which the IFD data starts
     * @param tiffHeaderOffset the offset within <code>reader</code> at which the TIFF header starts
     */
    @Deprecated
    private static void processIFD(@NotNull final Directory directory,
                                   @NotNull final Set<Integer> processedIfdOffsets,
                                   final int ifdOffset,
                                   final int tiffHeaderOffset,
                                   @NotNull final Metadata metadata,
                                   @NotNull final RandomAccessReader reader) throws IOException
    {
        // check for directories we've already visited to avoid stack overflows when recursive/cyclic directory structures exist
        if (processedIfdOffsets.contains(Integer.valueOf(ifdOffset)))
            return;

        // remember that we've visited this directory so that we don't visit it again later
        processedIfdOffsets.add(ifdOffset);

        if (ifdOffset >= reader.getLength() || ifdOffset < 0) {
            directory.addError("Ignored IFD marked to start outside data segment");
            return;
        }

        // First two bytes in the IFD are the number of tags in this directory
        int dirTagCount = reader.getUInt16(ifdOffset);

        int dirLength = (2 + (12 * dirTagCount) + 4);
        if (dirLength + ifdOffset > reader.getLength()) {
            directory.addError("Illegally sized IFD");
            return;
        }

        // Handle each tag in this directory
        for (int tagNumber = 0; tagNumber < dirTagCount; tagNumber++) {
            final int tagOffset = calculateTagOffset(ifdOffset, tagNumber);

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
                // dirEntryOffset must be passed, as some makernote implementations (e.g. Fujifilm) incorrectly use an
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
                directory.addError("Illegal number of bytes for TIFF tag data: " + byteCount);
                continue;
            }

            //
            // Special handling for certain known tags that point to or contain other chunks of data to be processed
            //
            if (tagType == ExifIFD0Directory.TAG_EXIF_SUB_IFD_OFFSET && directory instanceof ExifIFD0Directory) {
                if (byteCount != 4) {
                    directory.addError("Exif SubIFD Offset tag should have a component count of four (bytes) for the offset.");
                } else {
                    final int subDirOffset = tiffHeaderOffset + reader.getInt32(tagValueOffset);
                    processIFD(metadata.getOrCreateDirectory(ExifSubIFDDirectory.class), processedIfdOffsets, subDirOffset, tiffHeaderOffset, metadata, reader);
                }
            } else if (tagType == ExifSubIFDDirectory.TAG_INTEROP_OFFSET && directory instanceof ExifSubIFDDirectory) {
                if (byteCount != 4) {
                    directory.addError("Exif Interop Offset tag should have a component count of four (bytes) for the offset.");
                } else {
                    final int subDirOffset = tiffHeaderOffset + reader.getInt32(tagValueOffset);
                    processIFD(metadata.getOrCreateDirectory(ExifInteropDirectory.class), processedIfdOffsets, subDirOffset, tiffHeaderOffset, metadata, reader);
                }
            } else if (tagType == ExifIFD0Directory.TAG_GPS_INFO_OFFSET && directory instanceof ExifIFD0Directory) {
                if (byteCount != 4) {
                    directory.addError("Exif GPS Info Offset tag should have a component count of four (bytes) for the offset.");
                } else {
                    final int subDirOffset = tiffHeaderOffset + reader.getInt32(tagValueOffset);
                    processIFD(metadata.getOrCreateDirectory(GpsDirectory.class), processedIfdOffsets, subDirOffset, tiffHeaderOffset, metadata, reader);
                }
            } else if (tagType == ExifSubIFDDirectory.TAG_MAKERNOTE && directory instanceof ExifSubIFDDirectory) {
                // The makernote tag contains the encoded makernote data directly.
                // Pass the offset to this tag's value. Manufacturer/Model-specific logic will be used to
                // determine the correct offset for further processing.
                processMakernote(tagValueOffset, processedIfdOffsets, tiffHeaderOffset, metadata, reader);
            } else {
                processTag(directory, tagType, tagValueOffset, componentCount, formatCode, reader);
            }
        }

        // at the end of each IFD is an optional link to the next IFD
        final int finalTagOffset = calculateTagOffset(ifdOffset, dirTagCount);
        int nextDirectoryOffset = reader.getInt32(finalTagOffset);
        if (nextDirectoryOffset != 0) {
            nextDirectoryOffset += tiffHeaderOffset;
            if (nextDirectoryOffset >= reader.getLength()) {
                // Last 4 bytes of IFD reference another IFD with an address that is out of bounds
                // Note this could have been caused by jhead 1.3 cropping too much
                return;
            } else if (nextDirectoryOffset < ifdOffset) {
                // Last 4 bytes of IFD reference another IFD with an address that is before the start of this directory
                return;
            }
            // TODO in Exif, the only known 'follower' IFD is the thumbnail one, however this may not be the case
            final ExifThumbnailDirectory nextDirectory = metadata.getOrCreateDirectory(ExifThumbnailDirectory.class);
            processIFD(nextDirectory, processedIfdOffsets, nextDirectoryOffset, tiffHeaderOffset, metadata, reader);
        }
    }

    @Deprecated
    private static void processMakernote(final int makernoteOffset,
                                         final @NotNull Set<Integer> processedIfdOffsets,
                                         final int tiffHeaderOffset,
                                         final @NotNull Metadata metadata,
                                         final @NotNull RandomAccessReader reader) throws IOException
    {
        // Determine the camera model and makernote format
        Directory ifd0Directory = metadata.getDirectory(ExifIFD0Directory.class);

        if (ifd0Directory == null)
            return;

        String cameraMake = ifd0Directory.getString(ExifIFD0Directory.TAG_MAKE);

        final String firstThreeChars = reader.getString(makernoteOffset, 3);
        final String firstFourChars = reader.getString(makernoteOffset, 4);
        final String firstFiveChars = reader.getString(makernoteOffset, 5);
        final String firstSixChars = reader.getString(makernoteOffset, 6);
        final String firstSevenChars = reader.getString(makernoteOffset, 7);
        final String firstEightChars = reader.getString(makernoteOffset, 8);
        final String firstTwelveChars = reader.getString(makernoteOffset, 12);

        boolean byteOrderBefore = reader.isMotorolaByteOrder();

        if ("OLYMP".equals(firstFiveChars) || "EPSON".equals(firstFiveChars) || "AGFA".equals(firstFourChars)) {
            // Olympus Makernote
            // Epson and Agfa use Olympus makernote standard: http://www.ozhiker.com/electronics/pjmt/jpeg_info/
            processIFD(metadata.getOrCreateDirectory(OlympusMakernoteDirectory.class), processedIfdOffsets, makernoteOffset + 8, tiffHeaderOffset, metadata, reader);
        } else if (cameraMake != null && cameraMake.trim().toUpperCase().startsWith("NIKON")) {
            if ("Nikon".equals(firstFiveChars)) {
                /* There are two scenarios here:
                 * Type 1:                  **
                 * :0000: 4E 69 6B 6F 6E 00 01 00-05 00 02 00 02 00 06 00 Nikon...........
                 * :0010: 00 00 EC 02 00 00 03 00-03 00 01 00 00 00 06 00 ................
                 * Type 3:                  **
                 * :0000: 4E 69 6B 6F 6E 00 02 00-00 00 4D 4D 00 2A 00 00 Nikon....MM.*...
                 * :0010: 00 08 00 1E 00 01 00 07-00 00 00 04 30 32 30 30 ............0200
                 */
                switch (reader.getUInt8(makernoteOffset + 6)) {
                    case 1:
                        processIFD(metadata.getOrCreateDirectory(NikonType1MakernoteDirectory.class), processedIfdOffsets, makernoteOffset + 8, tiffHeaderOffset, metadata, reader);
                        break;
                    case 2:
                        processIFD(metadata.getOrCreateDirectory(NikonType2MakernoteDirectory.class), processedIfdOffsets, makernoteOffset + 18, makernoteOffset + 10, metadata, reader);
                        break;
                    default:
                        ifd0Directory.addError("Unsupported Nikon makernote data ignored.");
                        break;
                }
            } else {
                // The IFD begins with the first Makernote byte (no ASCII name).  This occurs with CoolPix 775, E990 and D1 models.
                processIFD(metadata.getOrCreateDirectory(NikonType2MakernoteDirectory.class), processedIfdOffsets, makernoteOffset, tiffHeaderOffset, metadata, reader);
            }
        } else if ("SONY CAM".equals(firstEightChars) || "SONY DSC".equals(firstEightChars)) {
            processIFD(metadata.getOrCreateDirectory(SonyType1MakernoteDirectory.class), processedIfdOffsets, makernoteOffset + 12, tiffHeaderOffset, metadata, reader);
        } else if ("SEMC MS\u0000\u0000\u0000\u0000\u0000".equals(firstTwelveChars)) {
            // force MM for this directory
            reader.setMotorolaByteOrder(true);
            // skip 12 byte header + 2 for "MM" + 6
            processIFD(metadata.getOrCreateDirectory(SonyType6MakernoteDirectory.class), processedIfdOffsets, makernoteOffset + 20, tiffHeaderOffset, metadata, reader);
        } else if ("SIGMA\u0000\u0000\u0000".equals(firstEightChars) || "FOVEON\u0000\u0000".equals(firstEightChars)) {
            processIFD(metadata.getOrCreateDirectory(SigmaMakernoteDirectory.class), processedIfdOffsets, makernoteOffset + 10, tiffHeaderOffset, metadata, reader);
        } else if ("KDK".equals(firstThreeChars)) {
            reader.setMotorolaByteOrder(firstSevenChars.equals("KDK INFO"));
            processKodakMakernote(metadata.getOrCreateDirectory(KodakMakernoteDirectory.class), makernoteOffset, reader);
        } else if ("Canon".equalsIgnoreCase(cameraMake)) {
            processIFD(metadata.getOrCreateDirectory(CanonMakernoteDirectory.class), processedIfdOffsets, makernoteOffset, tiffHeaderOffset, metadata, reader);
        } else if (cameraMake != null && cameraMake.toUpperCase().startsWith("CASIO")) {
            if ("QVC\u0000\u0000\u0000".equals(firstSixChars)) {
                processIFD(metadata.getOrCreateDirectory(CasioType2MakernoteDirectory.class), processedIfdOffsets, makernoteOffset + 6, tiffHeaderOffset, metadata, reader);
            } else {
                processIFD(metadata.getOrCreateDirectory(CasioType1MakernoteDirectory.class), processedIfdOffsets, makernoteOffset, tiffHeaderOffset, metadata, reader);
            }
        } else if ("FUJIFILM".equals(firstEightChars) || "Fujifilm".equalsIgnoreCase(cameraMake)) {
            // Note that this also applies to certain Leica cameras, such as the Digilux-4.3
            reader.setMotorolaByteOrder(false);
            // the 4 bytes after "FUJIFILM" in the makernote point to the start of the makernote
            // IFD, though the offset is relative to the start of the makernote, not the TIFF
            // header (like everywhere else)
            int ifdStart = makernoteOffset + reader.getInt32(makernoteOffset + 8);
            processIFD(metadata.getOrCreateDirectory(FujifilmMakernoteDirectory.class), processedIfdOffsets, ifdStart, makernoteOffset, metadata, reader);
        } else if (cameraMake != null && cameraMake.toUpperCase().startsWith("MINOLTA")) {
            // Cases seen with the model starting with MINOLTA in capitals seem to have a valid Olympus makernote
            // area that commences immediately.
            processIFD(metadata.getOrCreateDirectory(OlympusMakernoteDirectory.class), processedIfdOffsets, makernoteOffset, tiffHeaderOffset, metadata, reader);
        } else if ("KYOCERA".equals(firstSevenChars)) {
            // http://www.ozhiker.com/electronics/pjmt/jpeg_info/kyocera_mn.html
            processIFD(metadata.getOrCreateDirectory(KyoceraMakernoteDirectory.class), processedIfdOffsets, makernoteOffset + 22, tiffHeaderOffset, metadata, reader);
        } else if ("LEICA".equals(firstFiveChars)) {
            reader.setMotorolaByteOrder(false);
            if ("Leica Camera AG".equals(cameraMake)) {
                processIFD(metadata.getOrCreateDirectory(LeicaMakernoteDirectory.class), processedIfdOffsets, makernoteOffset + 8, tiffHeaderOffset, metadata, reader);
            } else if ("LEICA".equals(cameraMake)) {
                // Some Leica cameras use Panasonic makernote tags
                processIFD(metadata.getOrCreateDirectory(PanasonicMakernoteDirectory.class), processedIfdOffsets, makernoteOffset + 8, tiffHeaderOffset, metadata, reader);
            }
        } else if ("Panasonic\u0000\u0000\u0000".equals(reader.getString(makernoteOffset, 12))) {
            // NON-Standard TIFF IFD Data using Panasonic Tags. There is no Next-IFD pointer after the IFD
            // Offsets are relative to the start of the TIFF header at the beginning of the EXIF segment
            // more information here: http://www.ozhiker.com/electronics/pjmt/jpeg_info/panasonic_mn.html
            processIFD(metadata.getOrCreateDirectory(PanasonicMakernoteDirectory.class), processedIfdOffsets, makernoteOffset + 12, tiffHeaderOffset, metadata, reader);
        } else if ("AOC\u0000".equals(firstFourChars)) {
            // NON-Standard TIFF IFD Data using Casio Type 2 Tags
            // IFD has no Next-IFD pointer at end of IFD, and
            // Offsets are relative to the start of the current IFD tag, not the TIFF header
            // Observed for:
            // - Pentax ist D
            processIFD(metadata.getOrCreateDirectory(CasioType2MakernoteDirectory.class), processedIfdOffsets, makernoteOffset + 6, makernoteOffset, metadata, reader);
        } else if (cameraMake != null && (cameraMake.toUpperCase().startsWith("PENTAX") || cameraMake.toUpperCase().startsWith("ASAHI"))) {
            // NON-Standard TIFF IFD Data using Pentax Tags
            // IFD has no Next-IFD pointer at end of IFD, and
            // Offsets are relative to the start of the current IFD tag, not the TIFF header
            // Observed for:
            // - PENTAX Optio 330
            // - PENTAX Optio 430
            processIFD(metadata.getOrCreateDirectory(PentaxMakernoteDirectory.class), processedIfdOffsets, makernoteOffset, makernoteOffset, metadata, reader);
//        } else if ("KC".equals(firstTwoChars) || "MINOL".equals(firstFiveChars) || "MLY".equals(firstThreeChars) || "+M+M+M+M".equals(firstEightChars)) {
//            // This Konica data is not understood.  Header identified in accordance with information at this site:
//            // http://www.ozhiker.com/electronics/pjmt/jpeg_info/minolta_mn.html
//            // TODO add support for minolta/konica cameras
//            exifDirectory.addError("Unsupported Konica/Minolta data ignored.");
        } else if ("SANYO\0\1\0".equals(firstEightChars)) {
            processIFD(metadata.getOrCreateDirectory(SanyoMakernoteDirectory.class), processedIfdOffsets, makernoteOffset + 8, makernoteOffset, metadata, reader);
        } else {
            // The makernote is not comprehended by this library.
            // If you are reading this and believe a particular camera's image should be processed, get in touch.
        }

        reader.setMotorolaByteOrder(byteOrderBefore);
    }

    @Deprecated
    private static void processKodakMakernote(@NotNull final KodakMakernoteDirectory directory, final int tagValueOffset, @NotNull final RandomAccessReader reader)
    {
        // Kodak's makernote is not in IFD format. It has values at fixed offsets.
        int dataOffset = tagValueOffset + 8;
        try {
            directory.setString(KodakMakernoteDirectory.TAG_KODAK_MODEL, reader.getString(dataOffset, 8));
            directory.setInt(KodakMakernoteDirectory.TAG_QUALITY, reader.getUInt8(dataOffset + 9));
            directory.setInt(KodakMakernoteDirectory.TAG_BURST_MODE, reader.getUInt8(dataOffset + 10));
            directory.setInt(KodakMakernoteDirectory.TAG_IMAGE_WIDTH, reader.getUInt16(dataOffset + 12));
            directory.setInt(KodakMakernoteDirectory.TAG_IMAGE_HEIGHT, reader.getUInt16(dataOffset + 14));
            directory.setInt(KodakMakernoteDirectory.TAG_YEAR_CREATED, reader.getUInt16(dataOffset + 16));
            directory.setByteArray(KodakMakernoteDirectory.TAG_MONTH_DAY_CREATED, reader.getBytes(dataOffset + 18, 2));
            directory.setByteArray(KodakMakernoteDirectory.TAG_TIME_CREATED, reader.getBytes(dataOffset + 20, 4));
            directory.setInt(KodakMakernoteDirectory.TAG_BURST_MODE_2, reader.getUInt16(dataOffset + 24));
            directory.setInt(KodakMakernoteDirectory.TAG_SHUTTER_MODE, reader.getUInt8(dataOffset + 27));
            directory.setInt(KodakMakernoteDirectory.TAG_METERING_MODE, reader.getUInt8(dataOffset + 28));
            directory.setInt(KodakMakernoteDirectory.TAG_SEQUENCE_NUMBER, reader.getUInt8(dataOffset + 29));
            directory.setInt(KodakMakernoteDirectory.TAG_F_NUMBER, reader.getUInt16(dataOffset + 30));
            directory.setLong(KodakMakernoteDirectory.TAG_EXPOSURE_TIME, reader.getUInt32(dataOffset + 32));
            directory.setInt(KodakMakernoteDirectory.TAG_EXPOSURE_COMPENSATION, reader.getInt16(dataOffset + 36));
            directory.setInt(KodakMakernoteDirectory.TAG_FOCUS_MODE, reader.getUInt8(dataOffset + 56));
            directory.setInt(KodakMakernoteDirectory.TAG_WHITE_BALANCE, reader.getUInt8(dataOffset + 64));
            directory.setInt(KodakMakernoteDirectory.TAG_FLASH_MODE, reader.getUInt8(dataOffset + 92));
            directory.setInt(KodakMakernoteDirectory.TAG_FLASH_FIRED, reader.getUInt8(dataOffset + 93));
            directory.setInt(KodakMakernoteDirectory.TAG_ISO_SETTING, reader.getUInt16(dataOffset + 94));
            directory.setInt(KodakMakernoteDirectory.TAG_ISO, reader.getUInt16(dataOffset + 96));
            directory.setInt(KodakMakernoteDirectory.TAG_TOTAL_ZOOM, reader.getUInt16(dataOffset + 98));
            directory.setInt(KodakMakernoteDirectory.TAG_DATE_TIME_STAMP, reader.getUInt16(dataOffset + 100));
            directory.setInt(KodakMakernoteDirectory.TAG_COLOR_MODE, reader.getUInt16(dataOffset + 102));
            directory.setInt(KodakMakernoteDirectory.TAG_DIGITAL_ZOOM, reader.getUInt16(dataOffset + 104));
            directory.setInt(KodakMakernoteDirectory.TAG_SHARPNESS, reader.getInt8(dataOffset + 107));
        } catch (IOException ex) {
            directory.addError("Error processing Kodak makernote data: " + ex.getMessage());
        }
    }

    @Deprecated
    private static void processTag(@NotNull final Directory directory,
                                   final int tagType,
                                   final int tagValueOffset,
                                   final int componentCount,
                                   final int formatCode,
                                   @NotNull final RandomAccessReader reader) throws IOException
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
     * @param ifdStartOffset the offset at which the IFD starts
     * @param entryNumber    the zero-based entry number
     */
    @Deprecated
    private static int calculateTagOffset(int ifdStartOffset, int entryNumber)
    {
        // add 2 bytes for the tag count
        // each entry is 12 bytes, so we skip 12 * the number seen so far
        return ifdStartOffset + 2 + (12 * entryNumber);
    }
}

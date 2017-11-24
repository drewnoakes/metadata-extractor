/*
 * Copyright 2002-2017 Drew Noakes
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

import com.drew.imaging.tiff.TiffProcessingException;
import com.drew.imaging.tiff.TiffReader;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.BufferBoundsException;
import com.drew.lang.Charsets;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.StringValue;
import com.drew.metadata.exif.makernotes.*;
import com.drew.metadata.iptc.IptcReader;
import com.drew.metadata.tiff.DirectoryTiffHandler;
import com.drew.metadata.xmp.XmpReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

/**
 * Implementation of {@link com.drew.imaging.tiff.TiffHandler} used for handling TIFF tags according to the Exif
 * standard.
 * <p>
 * Includes support for camera manufacturer makernotes.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ExifTiffHandler extends DirectoryTiffHandler
{
    public ExifTiffHandler(@NotNull Metadata metadata, @Nullable Directory parentDirectory)
    {
        super(metadata, parentDirectory);
    }

    public void setTiffMarker(int marker) throws TiffProcessingException
    {
        final int standardTiffMarker = 0x002A;
        final int olympusRawTiffMarker = 0x4F52; // for ORF files
        final int olympusRawTiffMarker2 = 0x5352; // for ORF files
        final int panasonicRawTiffMarker = 0x0055; // for RW2 files

        switch (marker) {
            case standardTiffMarker:
            case olympusRawTiffMarker:      // TODO implement an IFD0, if there is one
            case olympusRawTiffMarker2:     // TODO implement an IFD0, if there is one
                pushDirectory(ExifIFD0Directory.class);
                break;
            case panasonicRawTiffMarker:
                pushDirectory(PanasonicRawIFD0Directory.class);
                break;
            default:
                throw new TiffProcessingException(String.format("Unexpected TIFF marker: 0x%X", marker));
        }
    }

    public boolean tryEnterSubIfd(int tagId)
    {
        if (tagId == ExifDirectoryBase.TAG_SUB_IFD_OFFSET) {
            pushDirectory(ExifSubIFDDirectory.class);
            return true;
        }

        if (_currentDirectory instanceof ExifIFD0Directory || _currentDirectory instanceof PanasonicRawIFD0Directory) {
            if (tagId == ExifIFD0Directory.TAG_EXIF_SUB_IFD_OFFSET) {
                pushDirectory(ExifSubIFDDirectory.class);
                return true;
            }

            if (tagId == ExifIFD0Directory.TAG_GPS_INFO_OFFSET) {
                pushDirectory(GpsDirectory.class);
                return true;
            }
        }

        if (_currentDirectory instanceof ExifSubIFDDirectory) {
            if (tagId == ExifSubIFDDirectory.TAG_INTEROP_OFFSET) {
                pushDirectory(ExifInteropDirectory.class);
                return true;
            }
        }

        if (_currentDirectory instanceof OlympusMakernoteDirectory) {
            // Note: these also appear in customProcessTag because some are IFD pointers while others begin immediately
            // for the same directories
            switch(tagId) {
                case OlympusMakernoteDirectory.TAG_EQUIPMENT:
                    pushDirectory(OlympusEquipmentMakernoteDirectory.class);
                    return true;
                case OlympusMakernoteDirectory.TAG_CAMERA_SETTINGS:
                    pushDirectory(OlympusCameraSettingsMakernoteDirectory.class);
                    return true;
                case OlympusMakernoteDirectory.TAG_RAW_DEVELOPMENT:
                    pushDirectory(OlympusRawDevelopmentMakernoteDirectory.class);
                    return true;
                case OlympusMakernoteDirectory.TAG_RAW_DEVELOPMENT_2:
                    pushDirectory(OlympusRawDevelopment2MakernoteDirectory.class);
                    return true;
                case OlympusMakernoteDirectory.TAG_IMAGE_PROCESSING:
                    pushDirectory(OlympusImageProcessingMakernoteDirectory.class);
                    return true;
                case OlympusMakernoteDirectory.TAG_FOCUS_INFO:
                    pushDirectory(OlympusFocusInfoMakernoteDirectory.class);
                    return true;
                case OlympusMakernoteDirectory.TAG_RAW_INFO:
                    pushDirectory(OlympusRawInfoMakernoteDirectory.class);
                    return true;
                case OlympusMakernoteDirectory.TAG_MAIN_INFO:
                    pushDirectory(OlympusMakernoteDirectory.class);
                    return true;
            }
        }

        return false;
    }

    public boolean hasFollowerIfd()
    {
        // In Exif, the only known 'follower' IFD is the thumbnail one, however this may not be the case.
        // UPDATE: In multipage TIFFs, the 'follower' IFD points to the next image in the set
        if (_currentDirectory instanceof ExifIFD0Directory || _currentDirectory instanceof ExifImageDirectory) {
            // If the PageNumber tag is defined, assume this is a multipage TIFF or similar
            // TODO: Find better ways to know which follower Directory should be used
            if (_currentDirectory.containsTag(ExifDirectoryBase.TAG_PAGE_NUMBER))
                pushDirectory(ExifImageDirectory.class);
            else
                pushDirectory(ExifThumbnailDirectory.class);
            return true;
        }

        // The Canon EOS 7D (CR2) has three chained/following thumbnail IFDs
        if (_currentDirectory instanceof ExifThumbnailDirectory)
            return true;

        // This should not happen, as Exif doesn't use follower IFDs apart from that above.
        // NOTE have seen the CanonMakernoteDirectory IFD have a follower pointer, but it points to invalid data.
        return false;
    }

    @Nullable
    public Long tryCustomProcessFormat(final int tagId, final int formatCode, final long componentCount)
    {
        if (formatCode == 13)
            return componentCount * 4;

        // an unknown (0) formatCode needs to be potentially handled later as a highly custom directory tag
        if (formatCode == 0)
            return 0L;

        return null;
    }

    public boolean customProcessTag(final int tagOffset,
                                    final @NotNull Set<Integer> processedIfdOffsets,
                                    final int tiffHeaderOffset,
                                    final @NotNull RandomAccessReader reader,
                                    final int tagId,
                                    final int byteCount) throws IOException
    {
        assert(_currentDirectory != null);

        // Some 0x0000 tags have a 0 byteCount. Determine whether it's bad.
        if (tagId == 0) {
            if (_currentDirectory.containsTag(tagId)) {
                // Let it go through for now. Some directories handle it, some don't
                return false;
            }

            // Skip over 0x0000 tags that don't have any associated bytes. No idea what it contains in this case, if anything.
            if (byteCount == 0)
                return true;
        }

        // Custom processing for the Makernote tag
        if (tagId == ExifSubIFDDirectory.TAG_MAKERNOTE && _currentDirectory instanceof ExifSubIFDDirectory) {
            return processMakernote(tagOffset, processedIfdOffsets, tiffHeaderOffset, reader);
        }

        // Custom processing for embedded IPTC data
        if (tagId == ExifSubIFDDirectory.TAG_IPTC_NAA && _currentDirectory instanceof ExifIFD0Directory) {
            // NOTE Adobe sets type 4 for IPTC instead of 7
            if (reader.getInt8(tagOffset) == 0x1c) {
                final byte[] iptcBytes = reader.getBytes(tagOffset, byteCount);
                new IptcReader().extract(new SequentialByteArrayReader(iptcBytes), _metadata, iptcBytes.length, _currentDirectory);
                return true;
            }
            return false;
        }

        // Custom processing for embedded XMP data
        if (tagId == ExifSubIFDDirectory.TAG_APPLICATION_NOTES && _currentDirectory instanceof ExifIFD0Directory) {
            new XmpReader().extract(reader.getNullTerminatedBytes(tagOffset, byteCount), _metadata, _currentDirectory);
            return true;
        }

        if (handlePrintIM(_currentDirectory, tagId))
        {
            PrintIMDirectory printIMDirectory = new PrintIMDirectory();
            printIMDirectory.setParent(_currentDirectory);
            _metadata.addDirectory(printIMDirectory);
            processPrintIM(printIMDirectory, tagOffset, reader, byteCount);
            return true;
        }

        // Note: these also appear in tryEnterSubIfd because some are IFD pointers while others begin immediately
        // for the same directories
        if (_currentDirectory instanceof OlympusMakernoteDirectory) {
            switch (tagId) {
                case OlympusMakernoteDirectory.TAG_EQUIPMENT:
                    pushDirectory(OlympusEquipmentMakernoteDirectory.class);
                    TiffReader.processIfd(this, reader, processedIfdOffsets, tagOffset, tiffHeaderOffset);
                    return true;
                case OlympusMakernoteDirectory.TAG_CAMERA_SETTINGS:
                    pushDirectory(OlympusCameraSettingsMakernoteDirectory.class);
                    TiffReader.processIfd(this, reader, processedIfdOffsets, tagOffset, tiffHeaderOffset);
                    return true;
                case OlympusMakernoteDirectory.TAG_RAW_DEVELOPMENT:
                    pushDirectory(OlympusRawDevelopmentMakernoteDirectory.class);
                    TiffReader.processIfd(this, reader, processedIfdOffsets, tagOffset, tiffHeaderOffset);
                    return true;
                case OlympusMakernoteDirectory.TAG_RAW_DEVELOPMENT_2:
                    pushDirectory(OlympusRawDevelopment2MakernoteDirectory.class);
                    TiffReader.processIfd(this, reader, processedIfdOffsets, tagOffset, tiffHeaderOffset);
                    return true;
                case OlympusMakernoteDirectory.TAG_IMAGE_PROCESSING:
                    pushDirectory(OlympusImageProcessingMakernoteDirectory.class);
                    TiffReader.processIfd(this, reader, processedIfdOffsets, tagOffset, tiffHeaderOffset);
                    return true;
                case OlympusMakernoteDirectory.TAG_FOCUS_INFO:
                    pushDirectory(OlympusFocusInfoMakernoteDirectory.class);
                    TiffReader.processIfd(this, reader, processedIfdOffsets, tagOffset, tiffHeaderOffset);
                    return true;
                case OlympusMakernoteDirectory.TAG_RAW_INFO:
                    pushDirectory(OlympusRawInfoMakernoteDirectory.class);
                    TiffReader.processIfd(this, reader, processedIfdOffsets, tagOffset, tiffHeaderOffset);
                    return true;
                case OlympusMakernoteDirectory.TAG_MAIN_INFO:
                    pushDirectory(OlympusMakernoteDirectory.class);
                    TiffReader.processIfd(this, reader, processedIfdOffsets, tagOffset, tiffHeaderOffset);
                    return true;
            }
        }

        if (_currentDirectory instanceof PanasonicRawIFD0Directory) {
            // these contain binary data with specific offsets, and can't be processed as regular ifd's.
            // The binary data is broken into 'fake' tags and there is a pattern.
            switch (tagId) {
                case PanasonicRawIFD0Directory.TagWbInfo:
                    PanasonicRawWbInfoDirectory dirWbInfo = new PanasonicRawWbInfoDirectory();
                    dirWbInfo.setParent(_currentDirectory);
                    _metadata.addDirectory(dirWbInfo);
                    processBinary(dirWbInfo, tagOffset, reader, byteCount, false, 2);
                    return true;
                case PanasonicRawIFD0Directory.TagWbInfo2:
                    PanasonicRawWbInfo2Directory dirWbInfo2 = new PanasonicRawWbInfo2Directory();
                    dirWbInfo2.setParent(_currentDirectory);
                    _metadata.addDirectory(dirWbInfo2);
                    processBinary(dirWbInfo2, tagOffset, reader, byteCount, false, 3);
                    return true;
                case PanasonicRawIFD0Directory.TagDistortionInfo:
                    PanasonicRawDistortionDirectory dirDistort = new PanasonicRawDistortionDirectory();
                    dirDistort.setParent(_currentDirectory);
                    _metadata.addDirectory(dirDistort);
                    processBinary(dirDistort, tagOffset, reader, byteCount, true, 1);
                    return true;
            }
        }

        // Panasonic RAW sometimes contains an embedded version of the data as a JPG file.
        if (tagId == PanasonicRawIFD0Directory.TagJpgFromRaw && _currentDirectory instanceof PanasonicRawIFD0Directory) {
            byte[] jpegrawbytes = reader.getBytes(tagOffset, byteCount);

            // Extract information from embedded image since it is metadata-rich
            ByteArrayInputStream jpegmem = new ByteArrayInputStream(jpegrawbytes);
            try {
                Metadata jpegDirectory = JpegMetadataReader.readMetadata(jpegmem);
                for (Directory directory : jpegDirectory.getDirectories()) {
                    directory.setParent(_currentDirectory);
                    _metadata.addDirectory(directory);
                }
                return true;
            } catch (JpegProcessingException e) {
                _currentDirectory.addError("Error processing JpgFromRaw: " + e.getMessage());
            } catch (IOException e) {
                _currentDirectory.addError("Error reading JpgFromRaw: " + e.getMessage());
            }
        }

        return false;
    }

    private static void processBinary(@NotNull final Directory directory, final int tagValueOffset, @NotNull final RandomAccessReader reader, final int byteCount, final Boolean isSigned, final int arrayLength) throws IOException
    {
        // expects signed/unsigned int16 (for now)
        //int byteSize = isSigned ? sizeof(short) : sizeof(ushort);
        int byteSize = 2;

        // 'directory' is assumed to contain tags that correspond to the byte position unless it's a set of bytes
        for (int i = 0; i < byteCount; i++) {
            if (directory.hasTagName(i)) {
                // only process this tag if the 'next' integral tag exists. Otherwise, it's a set of bytes
                if (i < byteCount - 1 && directory.hasTagName(i + 1)) {
                    if (isSigned)
                        directory.setObject(i, reader.getInt16(tagValueOffset + (i* byteSize)));
                    else
                        directory.setObject(i, reader.getUInt16(tagValueOffset + (i* byteSize)));
                } else {
                    // the next arrayLength bytes are a multi-byte value
                    if (isSigned) {
                        short[] val = new short[arrayLength];
                        for (int j = 0; j<val.length; j++)
                            val[j] = reader.getInt16(tagValueOffset + ((i + j) * byteSize));
                        directory.setObjectArray(i, val);
                    } else {
                        int[] val = new int[arrayLength];
                        for (int j = 0; j<val.length; j++)
                            val[j] = reader.getUInt16(tagValueOffset + ((i + j) * byteSize));
                        directory.setObjectArray(i, val);
                    }

                    i += arrayLength - 1;
                }
            }
        }
    }

    /** Read a given number of bytes from the stream
     *
     * This method is employed to "suppress" attempts to read beyond end of the
     * file as may happen at the beginning of processMakernote when we read
     * increasingly longer camera makes.
     *
     * Instead of failing altogether in this context we return an empty string
     * which will fail all sensible attempts to compare to makes while avoiding
     * a full-on failure.
     */
    @NotNull
    private static String getReaderString(final @NotNull RandomAccessReader reader, final int makernoteOffset, final int bytesRequested) throws IOException
    {
        try {
            return reader.getString(makernoteOffset, bytesRequested, Charsets.UTF_8);
        } catch(BufferBoundsException e) {
            return "";
        }
    }

    private boolean processMakernote(final int makernoteOffset,
                                     final @NotNull Set<Integer> processedIfdOffsets,
                                     final int tiffHeaderOffset,
                                     final @NotNull RandomAccessReader reader) throws IOException
    {
        assert(_currentDirectory != null);

        // Determine the camera model and makernote format.
        Directory ifd0Directory = _metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

        String cameraMake = ifd0Directory == null ? null : ifd0Directory.getString(ExifIFD0Directory.TAG_MAKE);

        final String firstTwoChars    = getReaderString(reader, makernoteOffset, 2);
        final String firstThreeChars  = getReaderString(reader, makernoteOffset, 3);
        final String firstFourChars   = getReaderString(reader, makernoteOffset, 4);
        final String firstFiveChars   = getReaderString(reader, makernoteOffset, 5);
        final String firstSixChars    = getReaderString(reader, makernoteOffset, 6);
        final String firstSevenChars  = getReaderString(reader, makernoteOffset, 7);
        final String firstEightChars  = getReaderString(reader, makernoteOffset, 8);
        final String firstNineChars   = getReaderString(reader, makernoteOffset, 9);
        final String firstTenChars    = getReaderString(reader, makernoteOffset, 10);
        final String firstTwelveChars = getReaderString(reader, makernoteOffset, 12);

        boolean byteOrderBefore = reader.isMotorolaByteOrder();

        if ("OLYMP\0".equals(firstSixChars) || "EPSON".equals(firstFiveChars) || "AGFA".equals(firstFourChars)) {
            // Olympus Makernote
            // Epson and Agfa use Olympus makernote standard: http://www.ozhiker.com/electronics/pjmt/jpeg_info/
            pushDirectory(OlympusMakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 8, tiffHeaderOffset);
        } else if ("OLYMPUS\0II".equals(firstTenChars)) {
            // Olympus Makernote (alternate)
            // Note that data is relative to the beginning of the makernote
            // http://exiv2.org/makernote.html
            pushDirectory(OlympusMakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 12, makernoteOffset);
        } else if (cameraMake != null && cameraMake.toUpperCase().startsWith("MINOLTA")) {
            // Cases seen with the model starting with MINOLTA in capitals seem to have a valid Olympus makernote
            // area that commences immediately.
            pushDirectory(OlympusMakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset, tiffHeaderOffset);
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
                        pushDirectory(NikonType1MakernoteDirectory.class);
                        TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 8, tiffHeaderOffset);
                        break;
                    case 2:
                        pushDirectory(NikonType2MakernoteDirectory.class);
                        TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 18, makernoteOffset + 10);
                        break;
                    default:
                        _currentDirectory.addError("Unsupported Nikon makernote data ignored.");
                        break;
                }
            } else {
                // The IFD begins with the first Makernote byte (no ASCII name).  This occurs with CoolPix 775, E990 and D1 models.
                pushDirectory(NikonType2MakernoteDirectory.class);
                TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset, tiffHeaderOffset);
            }
        } else if ("SONY CAM".equals(firstEightChars) || "SONY DSC".equals(firstEightChars)) {
            pushDirectory(SonyType1MakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 12, tiffHeaderOffset);
        // Do this check LAST after most other Sony checks
        } else if (cameraMake != null && cameraMake.startsWith("SONY") &&
                !Arrays.equals(reader.getBytes(makernoteOffset, 2), new byte[]{ 0x01, 0x00 }) ) {
            // The IFD begins with the first Makernote byte (no ASCII name). Used in SR2 and ARW images
            pushDirectory(SonyType1MakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset, tiffHeaderOffset);
        } else if ("SEMC MS\u0000\u0000\u0000\u0000\u0000".equals(firstTwelveChars)) {
            // force MM for this directory
            reader.setMotorolaByteOrder(true);
            // skip 12 byte header + 2 for "MM" + 6
            pushDirectory(SonyType6MakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 20, tiffHeaderOffset);
        } else if ("SIGMA\u0000\u0000\u0000".equals(firstEightChars) || "FOVEON\u0000\u0000".equals(firstEightChars)) {
            pushDirectory(SigmaMakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 10, tiffHeaderOffset);
        } else if ("KDK".equals(firstThreeChars)) {
            reader.setMotorolaByteOrder(firstSevenChars.equals("KDK INFO"));
            KodakMakernoteDirectory directory = new KodakMakernoteDirectory();
            _metadata.addDirectory(directory);
            processKodakMakernote(directory, makernoteOffset, reader);
        } else if ("Canon".equalsIgnoreCase(cameraMake)) {
            pushDirectory(CanonMakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset, tiffHeaderOffset);
        } else if (cameraMake != null && cameraMake.toUpperCase().startsWith("CASIO")) {
            if ("QVC\u0000\u0000\u0000".equals(firstSixChars)) {
                pushDirectory(CasioType2MakernoteDirectory.class);
                TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 6, tiffHeaderOffset);
            } else {
                pushDirectory(CasioType1MakernoteDirectory.class);
                TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset, tiffHeaderOffset);
            }
        } else if ("FUJIFILM".equals(firstEightChars) || "Fujifilm".equalsIgnoreCase(cameraMake)) {
            // Note that this also applies to certain Leica cameras, such as the Digilux-4.3
            reader.setMotorolaByteOrder(false);
            // the 4 bytes after "FUJIFILM" in the makernote point to the start of the makernote
            // IFD, though the offset is relative to the start of the makernote, not the TIFF
            // header (like everywhere else)
            int ifdStart = makernoteOffset + reader.getInt32(makernoteOffset + 8);
            pushDirectory(FujifilmMakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, ifdStart, makernoteOffset);
        } else if ("KYOCERA".equals(firstSevenChars)) {
            // http://www.ozhiker.com/electronics/pjmt/jpeg_info/kyocera_mn.html
            pushDirectory(KyoceraMakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 22, tiffHeaderOffset);
        } else if ("LEICA".equals(firstFiveChars)) {
            reader.setMotorolaByteOrder(false);

            // used by the X1/X2/X VARIO/T
            // (X1 starts with "LEICA\0\x01\0", Make is "LEICA CAMERA AG")
            // (X2 starts with "LEICA\0\x05\0", Make is "LEICA CAMERA AG")
            // (X VARIO starts with "LEICA\0\x04\0", Make is "LEICA CAMERA AG")
            // (T (Typ 701) starts with "LEICA\0\0x6", Make is "LEICA CAMERA AG")
            // (X (Typ 113) starts with "LEICA\0\0x7", Make is "LEICA CAMERA AG")

            if ("LEICA\0\u0001\0".equals(firstEightChars) ||
                "LEICA\0\u0004\0".equals(firstEightChars) ||
                "LEICA\0\u0005\0".equals(firstEightChars) ||
                "LEICA\0\u0006\0".equals(firstEightChars) ||
                "LEICA\0\u0007\0".equals(firstEightChars))
            {
                pushDirectory(LeicaType5MakernoteDirectory.class);
                TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 8, makernoteOffset);
            } else if ("Leica Camera AG".equals(cameraMake)) {
                pushDirectory(LeicaMakernoteDirectory.class);
                TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 8, tiffHeaderOffset);
            } else if ("LEICA".equals(cameraMake)) {
                // Some Leica cameras use Panasonic makernote tags
                pushDirectory(PanasonicMakernoteDirectory.class);
                TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 8, tiffHeaderOffset);
            } else {
                return false;
            }
        } else if ("Panasonic\u0000\u0000\u0000".equals(firstTwelveChars)) {
            // NON-Standard TIFF IFD Data using Panasonic Tags. There is no Next-IFD pointer after the IFD
            // Offsets are relative to the start of the TIFF header at the beginning of the EXIF segment
            // more information here: http://www.ozhiker.com/electronics/pjmt/jpeg_info/panasonic_mn.html
            pushDirectory(PanasonicMakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 12, tiffHeaderOffset);
        } else if ("AOC\u0000".equals(firstFourChars)) {
            // NON-Standard TIFF IFD Data using Casio Type 2 Tags
            // IFD has no Next-IFD pointer at end of IFD, and
            // Offsets are relative to the start of the current IFD tag, not the TIFF header
            // Observed for:
            // - Pentax ist D
            pushDirectory(CasioType2MakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 6, makernoteOffset);
        } else if (cameraMake != null && (cameraMake.toUpperCase().startsWith("PENTAX") || cameraMake.toUpperCase().startsWith("ASAHI"))) {
            // NON-Standard TIFF IFD Data using Pentax Tags
            // IFD has no Next-IFD pointer at end of IFD, and
            // Offsets are relative to the start of the current IFD tag, not the TIFF header
            // Observed for:
            // - PENTAX Optio 330
            // - PENTAX Optio 430
            pushDirectory(PentaxMakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset, makernoteOffset);
//        } else if ("KC".equals(firstTwoChars) || "MINOL".equals(firstFiveChars) || "MLY".equals(firstThreeChars) || "+M+M+M+M".equals(firstEightChars)) {
//            // This Konica data is not understood.  Header identified in accordance with information at this site:
//            // http://www.ozhiker.com/electronics/pjmt/jpeg_info/minolta_mn.html
//            // TODO add support for minolta/konica cameras
//            exifDirectory.addError("Unsupported Konica/Minolta data ignored.");
        } else if ("SANYO\0\1\0".equals(firstEightChars)) {
            pushDirectory(SanyoMakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 8, makernoteOffset);
        } else if (cameraMake != null && cameraMake.toLowerCase().startsWith("ricoh")) {
            if (firstTwoChars.equals("Rv") || firstThreeChars.equals("Rev")) {
                // This is a textual format, where the makernote bytes look like:
                //   Rv0103;Rg1C;Bg18;Ll0;Ld0;Aj0000;Bn0473800;Fp2E00:������������������������������
                //   Rv0103;Rg1C;Bg18;Ll0;Ld0;Aj0000;Bn0473800;Fp2D05:������������������������������
                //   Rv0207;Sf6C84;Rg76;Bg60;Gg42;Ll0;Ld0;Aj0004;Bn0B02900;Fp10B8;Md6700;Ln116900086D27;Sv263:0000000000000000000000��
                // This format is currently unsupported
                return false;
            } else if (firstFiveChars.equalsIgnoreCase("Ricoh")) {
                // Always in Motorola byte order
                reader.setMotorolaByteOrder(true);
                pushDirectory(RicohMakernoteDirectory.class);
                TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 8, makernoteOffset);
            }
        } else if (firstTenChars.equals("Apple iOS\0")) {
            // Always in Motorola byte order
            boolean orderBefore = reader.isMotorolaByteOrder();
            reader.setMotorolaByteOrder(true);
            pushDirectory(AppleMakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 14, makernoteOffset);
            reader.setMotorolaByteOrder(orderBefore);
        } else if (reader.getUInt16(makernoteOffset) == ReconyxHyperFireMakernoteDirectory.MAKERNOTE_VERSION) {
            ReconyxHyperFireMakernoteDirectory directory = new ReconyxHyperFireMakernoteDirectory();
            _metadata.addDirectory(directory);
            processReconyxHyperFireMakernote(directory, makernoteOffset, reader);
        } else if (firstNineChars.equalsIgnoreCase("RECONYXUF")) {
            ReconyxUltraFireMakernoteDirectory directory = new ReconyxUltraFireMakernoteDirectory();
            _metadata.addDirectory(directory);
            processReconyxUltraFireMakernote(directory, makernoteOffset, reader);
        } else if ("SAMSUNG".equals(cameraMake)) {
            // Only handles Type2 notes correctly. Others aren't implemented, and it's complex to determine which ones to use
            pushDirectory(SamsungType2MakernoteDirectory.class);
            TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset, tiffHeaderOffset);
        } else {
            // The makernote is not comprehended by this library.
            // If you are reading this and believe a particular camera's image should be processed, get in touch.
            return false;
        }

        reader.setMotorolaByteOrder(byteOrderBefore);
        return true;
    }

    private static boolean handlePrintIM(@NotNull final Directory directory, final int tagId)
    {
        if (tagId == ExifDirectoryBase.TAG_PRINT_IMAGE_MATCHING_INFO)
            return true;

        if (tagId == 0x0E00) {
            // Tempting to say every tagid of 0x0E00 is a PIM tag, but can't be 100% sure
            if (directory instanceof CasioType2MakernoteDirectory ||
                directory instanceof KyoceraMakernoteDirectory ||
                directory instanceof NikonType2MakernoteDirectory ||
                directory instanceof OlympusMakernoteDirectory ||
                directory instanceof PanasonicMakernoteDirectory ||
                directory instanceof PentaxMakernoteDirectory ||
                directory instanceof RicohMakernoteDirectory ||
                directory instanceof SanyoMakernoteDirectory ||
                directory instanceof SonyType1MakernoteDirectory)
                return true;
        }

        return false;
    }

    /// <summary>
    /// Process PrintIM IFD
    /// </summary>
    /// <remarks>
    /// Converted from Exiftool version 10.33 created by Phil Harvey
    /// http://www.sno.phy.queensu.ca/~phil/exiftool/
    /// lib\Image\ExifTool\PrintIM.pm
    /// </remarks>
    private static void processPrintIM(@NotNull final PrintIMDirectory directory, final int tagValueOffset, @NotNull final RandomAccessReader reader, final int byteCount) throws IOException
    {
        Boolean resetByteOrder = null;

        if (byteCount == 0) {
            directory.addError("Empty PrintIM data");
            return;
        }

        if (byteCount <= 15) {
            directory.addError("Bad PrintIM data");
            return;
        }

        String header = reader.getString(tagValueOffset, 12, Charsets.UTF_8);

        if (!header.startsWith("PrintIM")) {
            directory.addError("Invalid PrintIM header");
            return;
        }

        // check size of PrintIM block
        int num = reader.getUInt16(tagValueOffset + 14);

        if (byteCount < 16 + num * 6) {
            // size is too big, maybe byte ordering is wrong
            resetByteOrder = reader.isMotorolaByteOrder();
            reader.setMotorolaByteOrder(!reader.isMotorolaByteOrder());
            num = reader.getUInt16(tagValueOffset + 14);
            if (byteCount < 16 + num * 6) {
                directory.addError("Bad PrintIM size");
                return;
            }
        }

        directory.setObject(PrintIMDirectory.TagPrintImVersion, header.substring(8, 12));

        for (int n = 0; n < num; n++) {
            int pos = tagValueOffset + 16 + n * 6;
            int tag = reader.getUInt16(pos);
            long val = reader.getUInt32(pos + 2);

            directory.setObject(tag, val);
        }

        if (resetByteOrder != null)
            reader.setMotorolaByteOrder(resetByteOrder);
    }

    private static void processKodakMakernote(@NotNull final KodakMakernoteDirectory directory, final int tagValueOffset, @NotNull final RandomAccessReader reader)
    {
        // Kodak's makernote is not in IFD format. It has values at fixed offsets.
        int dataOffset = tagValueOffset + 8;
        try {
            directory.setStringValue(KodakMakernoteDirectory.TAG_KODAK_MODEL, reader.getStringValue(dataOffset, 8, Charsets.UTF_8));
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

    private static void processReconyxHyperFireMakernote(@NotNull final ReconyxHyperFireMakernoteDirectory directory, final int makernoteOffset, @NotNull final RandomAccessReader reader) throws IOException
    {
        directory.setObject(ReconyxHyperFireMakernoteDirectory.TAG_MAKERNOTE_VERSION, reader.getUInt16(makernoteOffset));

        int major = reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_FIRMWARE_VERSION);
        int minor = reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_FIRMWARE_VERSION + 2);
        int revision = reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_FIRMWARE_VERSION + 4);
        String buildYear = String.format("%04X", reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_FIRMWARE_VERSION + 6));
        String buildDate = String.format("%04X", reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_FIRMWARE_VERSION + 8));
        String buildYearAndDate = buildYear + buildDate;
        Integer build;
        try {
            build = Integer.parseInt(buildYearAndDate);
        } catch (NumberFormatException e) {
            build = null;
        }

        if (build != null) {
            directory.setString(ReconyxHyperFireMakernoteDirectory.TAG_FIRMWARE_VERSION, String.format("%d.%d.%d.%s", major, minor, revision, build));
        } else {
            directory.setString(ReconyxHyperFireMakernoteDirectory.TAG_FIRMWARE_VERSION, String.format("%d.%d.%d", major, minor, revision));
            directory.addError("Error processing Reconyx HyperFire makernote data: build '" + buildYearAndDate + "' is not in the expected format and will be omitted from Firmware Version.");
        }

        directory.setString(ReconyxHyperFireMakernoteDirectory.TAG_TRIGGER_MODE, String.valueOf((char)reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_TRIGGER_MODE)));
        directory.setIntArray(ReconyxHyperFireMakernoteDirectory.TAG_SEQUENCE,
                      new int[]
                      {
                          reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_SEQUENCE),
                          reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_SEQUENCE + 2)
                      });

        int eventNumberHigh = reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_EVENT_NUMBER);
        int eventNumberLow = reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_EVENT_NUMBER + 2);
        directory.setInt(ReconyxHyperFireMakernoteDirectory.TAG_EVENT_NUMBER, (eventNumberHigh << 16) + eventNumberLow);

        int seconds = reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_DATE_TIME_ORIGINAL);
        int minutes = reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_DATE_TIME_ORIGINAL + 2);
        int hour = reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_DATE_TIME_ORIGINAL + 4);
        int month = reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_DATE_TIME_ORIGINAL + 6);
        int day = reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_DATE_TIME_ORIGINAL + 8);
        int year = reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_DATE_TIME_ORIGINAL + 10);

        if ((seconds >= 0 && seconds < 60) &&
            (minutes >= 0 && minutes < 60) &&
            (hour >= 0 && hour < 24) &&
            (month >= 1 && month < 13) &&
            (day >= 1 && day < 32) &&
            (year >= 1 && year <= 9999)) {
            directory.setString(ReconyxHyperFireMakernoteDirectory.TAG_DATE_TIME_ORIGINAL,
                    String.format("%4d:%2d:%2d %2d:%2d:%2d", year, month, day, hour, minutes, seconds));
        } else {
            directory.addError("Error processing Reconyx HyperFire makernote data: Date/Time Original " + year + "-" + month + "-" + day + " " + hour + ":" + minutes + ":" + seconds + " is not a valid date/time.");
        }

        directory.setInt(ReconyxHyperFireMakernoteDirectory.TAG_MOON_PHASE, reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_MOON_PHASE));
        directory.setInt(ReconyxHyperFireMakernoteDirectory.TAG_AMBIENT_TEMPERATURE_FAHRENHEIT, reader.getInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_AMBIENT_TEMPERATURE_FAHRENHEIT));
        directory.setInt(ReconyxHyperFireMakernoteDirectory.TAG_AMBIENT_TEMPERATURE, reader.getInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_AMBIENT_TEMPERATURE));
        //directory.setByteArray(ReconyxHyperFireMakernoteDirectory.TAG_SERIAL_NUMBER, reader.getBytes(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_SERIAL_NUMBER, 28));
        directory.setStringValue(ReconyxHyperFireMakernoteDirectory.TAG_SERIAL_NUMBER, new StringValue(reader.getBytes(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_SERIAL_NUMBER, 28), Charsets.UTF_16LE));
        // two unread bytes: the serial number's terminating null

        directory.setInt(ReconyxHyperFireMakernoteDirectory.TAG_CONTRAST, reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_CONTRAST));
        directory.setInt(ReconyxHyperFireMakernoteDirectory.TAG_BRIGHTNESS, reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_BRIGHTNESS));
        directory.setInt(ReconyxHyperFireMakernoteDirectory.TAG_SHARPNESS, reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_SHARPNESS));
        directory.setInt(ReconyxHyperFireMakernoteDirectory.TAG_SATURATION, reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_SATURATION));
        directory.setInt(ReconyxHyperFireMakernoteDirectory.TAG_INFRARED_ILLUMINATOR, reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_INFRARED_ILLUMINATOR));
        directory.setInt(ReconyxHyperFireMakernoteDirectory.TAG_MOTION_SENSITIVITY, reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_MOTION_SENSITIVITY));
        directory.setDouble(ReconyxHyperFireMakernoteDirectory.TAG_BATTERY_VOLTAGE, reader.getUInt16(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_BATTERY_VOLTAGE) / 1000.0);
        directory.setString(ReconyxHyperFireMakernoteDirectory.TAG_USER_LABEL, reader.getNullTerminatedString(makernoteOffset + ReconyxHyperFireMakernoteDirectory.TAG_USER_LABEL, 44, Charsets.UTF_8));
    }

    private static void processReconyxUltraFireMakernote(@NotNull final ReconyxUltraFireMakernoteDirectory directory, final int makernoteOffset, @NotNull final RandomAccessReader reader) throws IOException
    {
        directory.setString(ReconyxUltraFireMakernoteDirectory.TAG_LABEL, reader.getString(makernoteOffset, 9, Charsets.UTF_8));
        /*uint makernoteID = ByteConvert.FromBigEndianToNative(reader.GetUInt32(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TagMakernoteID));
        directory.Set(ReconyxUltraFireMakernoteDirectory.TagMakernoteID, makernoteID);
        if (makernoteID != ReconyxUltraFireMakernoteDirectory.MAKERNOTE_ID) {
            directory.addError("Error processing Reconyx UltraFire makernote data: unknown Makernote ID 0x" + makernoteID.ToString("x8"));
            return;
        }
        directory.Set(ReconyxUltraFireMakernoteDirectory.TagMakernoteSize, ByteConvert.FromBigEndianToNative(reader.GetUInt32(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TagMakernoteSize)));
        uint makernotePublicID = ByteConvert.FromBigEndianToNative(reader.GetUInt32(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TagMakernotePublicID));
        directory.Set(ReconyxUltraFireMakernoteDirectory.TagMakernotePublicID, makernotePublicID);
        if (makernotePublicID != ReconyxUltraFireMakernoteDirectory.MAKERNOTE_PUBLIC_ID) {
            directory.addError("Error processing Reconyx UltraFire makernote data: unknown Makernote Public ID 0x" + makernotePublicID.ToString("x8"));
            return;
        }*/
        //directory.Set(ReconyxUltraFireMakernoteDirectory.TagMakernotePublicSize, ByteConvert.FromBigEndianToNative(reader.GetUInt16(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TagMakernotePublicSize)));

        //directory.Set(ReconyxUltraFireMakernoteDirectory.TagCameraVersion, ProcessReconyxUltraFireVersion(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TagCameraVersion, reader));
        //directory.Set(ReconyxUltraFireMakernoteDirectory.TagUibVersion, ProcessReconyxUltraFireVersion(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TagUibVersion, reader));
        //directory.Set(ReconyxUltraFireMakernoteDirectory.TagBtlVersion, ProcessReconyxUltraFireVersion(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TagBtlVersion, reader));
        //directory.Set(ReconyxUltraFireMakernoteDirectory.TagPexVersion, ProcessReconyxUltraFireVersion(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TagPexVersion, reader));

        directory.setString(ReconyxUltraFireMakernoteDirectory.TAG_EVENT_TYPE, reader.getString(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TAG_EVENT_TYPE, 1, Charsets.UTF_8));
        directory.setIntArray(ReconyxUltraFireMakernoteDirectory.TAG_SEQUENCE,
                      new int[]
                      {
                          reader.getByte(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TAG_SEQUENCE),
                          reader.getByte(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TAG_SEQUENCE + 1)
                      });
        //directory.Set(ReconyxUltraFireMakernoteDirectory.TagEventNumber, ByteConvert.FromBigEndianToNative(reader.GetUInt32(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TagEventNumber)));

        byte seconds = reader.getByte(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TAG_DATE_TIME_ORIGINAL);
        byte minutes = reader.getByte(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TAG_DATE_TIME_ORIGINAL + 1);
        byte hour = reader.getByte(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TAG_DATE_TIME_ORIGINAL + 2);
        byte day = reader.getByte(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TAG_DATE_TIME_ORIGINAL + 3);
        byte month = reader.getByte(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TAG_DATE_TIME_ORIGINAL + 4);
        /*ushort year = ByteConvert.FromBigEndianToNative(reader.GetUInt16(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TagDateTimeOriginal + 5));
        if ((seconds >= 0 && seconds < 60) &&
            (minutes >= 0 && minutes < 60) &&
            (hour >= 0 && hour < 24) &&
            (month >= 1 && month < 13) &&
            (day >= 1 && day < 32) &&
            (year >= 1 && year <= 9999)) {
            directory.Set(ReconyxUltraFireMakernoteDirectory.TAG_DATE_TIME_ORIGINAL, new DateTime(year, month, day, hour, minutes, seconds, DateTimeKind.Unspecified));
        } else {
            directory.addError("Error processing Reconyx UltraFire makernote data: Date/Time Original " + year + "-" + month + "-" + day + " " + hour + ":" + minutes + ":" + seconds + " is not a valid date/time.");
        }*/
        //directory.Set(ReconyxUltraFireMakernoteDirectory.TagDayOfWeek, reader.GetByte(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TagDayOfWeek));

        directory.setInt(ReconyxUltraFireMakernoteDirectory.TAG_MOON_PHASE, reader.getByte(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TAG_MOON_PHASE));
        //directory.Set(ReconyxUltraFireMakernoteDirectory.TagAmbientTemperatureFahrenheit, ByteConvert.FromBigEndianToNative(reader.GetInt16(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TagAmbientTemperatureFahrenheit)));
        //directory.Set(ReconyxUltraFireMakernoteDirectory.TagAmbientTemperature, ByteConvert.FromBigEndianToNative(reader.GetInt16(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TagAmbientTemperature)));

        directory.setInt(ReconyxUltraFireMakernoteDirectory.TAG_FLASH, reader.getByte(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TAG_FLASH));
        //directory.Set(ReconyxUltraFireMakernoteDirectory.TagBatteryVoltage, ByteConvert.FromBigEndianToNative(reader.GetUInt16(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TagBatteryVoltage)) / 1000.0);
        directory.setStringValue(ReconyxUltraFireMakernoteDirectory.TAG_SERIAL_NUMBER, new StringValue(reader.getBytes(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TAG_SERIAL_NUMBER, 14), Charsets.UTF_8));
        // unread byte: the serial number's terminating null
        directory.setString(ReconyxUltraFireMakernoteDirectory.TAG_USER_LABEL, reader.getNullTerminatedString(makernoteOffset + ReconyxUltraFireMakernoteDirectory.TAG_USER_LABEL, 20, Charsets.UTF_8));
    }
}


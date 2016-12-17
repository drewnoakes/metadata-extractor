/*
 * Copyright 2002-2016 Drew Noakes
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
import com.drew.lang.Charsets;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.makernotes.*;
import com.drew.metadata.iptc.IptcReader;
import com.drew.metadata.tiff.DirectoryTiffHandler;
import com.drew.metadata.xmp.XmpReader;

import java.io.IOException;
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
    private final boolean _storeThumbnailBytes;

    public ExifTiffHandler(@NotNull Metadata metadata, boolean storeThumbnailBytes, @Nullable Directory parentDirectory)
    {
        super(metadata, ExifIFD0Directory.class);
        _storeThumbnailBytes = storeThumbnailBytes;

        if (parentDirectory != null)
            _currentDirectory.setParent(parentDirectory);
    }

    public void setTiffMarker(int marker) throws TiffProcessingException
    {
        final int standardTiffMarker = 0x002A;
        final int olympusRawTiffMarker = 0x4F52; // for ORF files
        final int olympusRawTiffMarker2 = 0x5352; // for ORF files
        final int panasonicRawTiffMarker = 0x0055; // for RW2 files

        if (marker != standardTiffMarker && marker != olympusRawTiffMarker && marker != olympusRawTiffMarker2 && marker != panasonicRawTiffMarker) {
            throw new TiffProcessingException("Unexpected TIFF marker: 0x" + Integer.toHexString(marker));
        }
    }

    public boolean tryEnterSubIfd(int tagId)
    {
        if (tagId == ExifDirectoryBase.TAG_SUB_IFD_OFFSET) {
            pushDirectory(ExifSubIFDDirectory.class);
            return true;
        }

        if (_currentDirectory instanceof ExifIFD0Directory) {
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
            }
        }

        return false;
    }

    public boolean hasFollowerIfd()
    {
        // In Exif, the only known 'follower' IFD is the thumbnail one, however this may not be the case.
        if (_currentDirectory instanceof ExifIFD0Directory) {
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
        if(formatCode == 0)
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
        // Some 0x0000 tags have a 0 byteCount. Determine whether it's bad.
        if (tagId == 0)
        {
            if (_currentDirectory.containsTag(tagId))
            {
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

        if(_currentDirectory instanceof OlympusMakernoteDirectory)
        {
            switch (tagId)
            {
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
            }
        }

        return false;
    }

    public void completed(@NotNull final RandomAccessReader reader, final int tiffHeaderOffset)
    {
        if (_storeThumbnailBytes) {
            // after the extraction process, if we have the correct tags, we may be able to store thumbnail information
            ExifThumbnailDirectory thumbnailDirectory = _metadata.getFirstDirectoryOfType(ExifThumbnailDirectory.class);
            if (thumbnailDirectory != null && thumbnailDirectory.containsTag(ExifThumbnailDirectory.TAG_COMPRESSION)) {
                Integer offset = thumbnailDirectory.getInteger(ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET);
                Integer length = thumbnailDirectory.getInteger(ExifThumbnailDirectory.TAG_THUMBNAIL_LENGTH);
                if (offset != null && length != null) {
                    try {
                        byte[] thumbnailData = reader.getBytes(tiffHeaderOffset + offset, length);
                        thumbnailDirectory.setThumbnailData(thumbnailData);
                    } catch (IOException ex) {
                        thumbnailDirectory.addError("Invalid thumbnail data specification: " + ex.getMessage());
                    }
                }
            }
        }
    }

    private boolean processMakernote(final int makernoteOffset,
                                     final @NotNull Set<Integer> processedIfdOffsets,
                                     final int tiffHeaderOffset,
                                     final @NotNull RandomAccessReader reader) throws IOException
    {
        // Determine the camera model and makernote format.
        Directory ifd0Directory = _metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

        String cameraMake = ifd0Directory == null ? null : ifd0Directory.getString(ExifIFD0Directory.TAG_MAKE);

        final String firstTwoChars    = reader.getString(makernoteOffset, 2, Charsets.UTF_8);
        final String firstThreeChars  = reader.getString(makernoteOffset, 3, Charsets.UTF_8);
        final String firstFourChars   = reader.getString(makernoteOffset, 4, Charsets.UTF_8);
        final String firstFiveChars   = reader.getString(makernoteOffset, 5, Charsets.UTF_8);
        final String firstSixChars    = reader.getString(makernoteOffset, 6, Charsets.UTF_8);
        final String firstSevenChars  = reader.getString(makernoteOffset, 7, Charsets.UTF_8);
        final String firstEightChars  = reader.getString(makernoteOffset, 8, Charsets.UTF_8);
        final String firstTenChars    = reader.getString(makernoteOffset, 10, Charsets.UTF_8);
        final String firstTwelveChars = reader.getString(makernoteOffset, 12, Charsets.UTF_8);

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
            if ("Leica Camera AG".equals(cameraMake)) {
                pushDirectory(LeicaMakernoteDirectory.class);
                TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 8, tiffHeaderOffset);
            } else if ("LEICA".equals(cameraMake)) {
                // Some Leica cameras use Panasonic makernote tags
                pushDirectory(PanasonicMakernoteDirectory.class);
                TiffReader.processIfd(this, reader, processedIfdOffsets, makernoteOffset + 8, tiffHeaderOffset);
            } else {
                return false;
            }
        } else if ("Panasonic\u0000\u0000\u0000".equals(reader.getString(makernoteOffset, 12, Charsets.UTF_8))) {
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
        } else {
            // The makernote is not comprehended by this library.
            // If you are reading this and believe a particular camera's image should be processed, get in touch.
            return false;
        }

        reader.setMotorolaByteOrder(byteOrderBefore);
        return true;
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
}


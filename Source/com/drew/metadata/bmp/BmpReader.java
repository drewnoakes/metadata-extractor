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
package com.drew.metadata.bmp;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.Charsets;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.ErrorDirectory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.bmp.BmpHeaderDirectory.ColorSpaceType;
import com.drew.metadata.icc.IccReader;

import java.io.IOException;

/**
 * Reader for Windows and OS/2 bitmap files.
 * <p>
 * References:
 * <ul>
 *   <li><a href="https://web.archive.org/web/20170302205626/http://fileformats.archiveteam.org/wiki/BMP">Fileformats Wiki BMP overview</a></li>
 *   <li><a href="http://web.archive.org/web/20170303000822/http://netghost.narod.ru/gff/graphics/summary/micbmp.htm">Graphics File Formats encyclopedia Windows bitmap description</a></li>
 *   <li><a href="https://web.archive.org/web/20170302205330/http://netghost.narod.ru/gff/graphics/summary/os2bmp.htm">Graphics File Formats encyclopedia OS/2 bitmap description</a></li>
 *   <li><a href="https://web.archive.org/web/20170302205457/http://www.fileformat.info/format/os2bmp/spec/902d5c253f2a43ada39c2b81034f27fd/view.htm">OS/2 bitmap specification</a></li>
 *   <li><a href="https://msdn.microsoft.com/en-us/library/dd183392(v=vs.85).aspx">Microsoft Bitmap Structures</a></li>
 * </ul>
 *
 * @author Drew Noakes https://drewnoakes.com
 * @author Nadahar
 */
public class BmpReader
{
    // Possible "magic bytes" indicating bitmap type:
    /**
     * "BM" - Windows or OS/2 bitmap
     */
    public static final int BITMAP = 0x4D42;
    /**
     * "BA" - OS/2 Bitmap array (multiple bitmaps)
     */
    public static final int OS2_BITMAP_ARRAY = 0x4142;
    /**
     * "IC" - OS/2 Icon
     */
    public static final int OS2_ICON = 0x4349;
    /**
     * "CI" - OS/2 Color icon
     */
    public static final int OS2_COLOR_ICON = 0x4943;
    /**
     * "CP" - OS/2 Color pointer
     */
    public static final int OS2_COLOR_POINTER = 0x5043;
    /**
     * "PT" - OS/2 Pointer
     */
    public static final int OS2_POINTER = 0x5450;

    public void extract(@NotNull final SequentialReader reader, final @NotNull Metadata metadata)
    {
        reader.setMotorolaByteOrder(false);

        // BITMAP INFORMATION HEADER
        //
        // The first four bytes of the header give the size, which is a discriminator of the actual header format.
        // See this for more information http://en.wikipedia.org/wiki/BMP_file_format

        readFileHeader(reader, metadata, true);
    }

    protected void readFileHeader(@NotNull final SequentialReader reader, final @NotNull Metadata metadata, boolean allowArray) {
        /*
         * There are two possible headers a file can start with. If the magic
         * number is OS/2 Bitmap Array (0x4142) the OS/2 Bitmap Array Header
         * will follow. In all other cases the file header will follow, which
         * is identical for Windows and OS/2.
         *
         * File header:
         *
         *    WORD   FileType;      - File type identifier
         *    DWORD  FileSize;      - Size of the file in bytes
         *    WORD   XHotSpot;      - X coordinate of hotspot for pointers
         *    WORD   YHotSpot;      - Y coordinate of hotspot for pointers
         *    DWORD  BitmapOffset;  - Starting position of image data in bytes
         *
         * OS/2 Bitmap Array Header:
         *
         *     WORD  Type;          - Header type, always 4142h ("BA")
         *     DWORD Size;          - Size of this header
         *     DWORD OffsetToNext;  - Offset to next OS2BMPARRAYFILEHEADER
         *     WORD  ScreenWidth;   - Width of the image display in pixels
         *     WORD  ScreenHeight;  - Height of the image display in pixels
         *
         */

        final int magicNumber;
        try {
            magicNumber = reader.getUInt16();
        } catch (IOException e) {
            metadata.addDirectory(new ErrorDirectory("Couldn't determine bitmap type: " + e.getMessage()));
            return;
        }

        Directory directory = null;
        try {
            switch (magicNumber) {
                case OS2_BITMAP_ARRAY:
                    if (!allowArray) {
                        addError("Invalid bitmap file - nested arrays not allowed", metadata);
                        return;
                    }
                    reader.skip(4); // Size
                    long nextHeaderOffset = reader.getUInt32();
                    reader.skip(2 + 2); // Screen Resolution
                    readFileHeader(reader, metadata, false);
                    if (nextHeaderOffset == 0) {
                        return; // No more bitmaps
                    }
                    if (reader.getPosition() > nextHeaderOffset) {
                        addError("Invalid next header offset", metadata);
                        return;
                    }
                    reader.skip(nextHeaderOffset - reader.getPosition());
                    readFileHeader(reader, metadata, true);
                    break;
                case BITMAP:
                case OS2_ICON:
                case OS2_COLOR_ICON:
                case OS2_COLOR_POINTER:
                case OS2_POINTER:
                    directory = new BmpHeaderDirectory();
                    metadata.addDirectory(directory);
                    directory.setInt(BmpHeaderDirectory.TAG_BITMAP_TYPE, magicNumber);
                    // skip past the rest of the file header
                    reader.skip(4 + 2 + 2 + 4);
                    readBitmapHeader(reader, (BmpHeaderDirectory) directory, metadata);
                    break;
                default:
                    metadata.addDirectory(new ErrorDirectory("Invalid BMP magic number 0x" + Integer.toHexString(magicNumber)));
                    return;
            }
        } catch (IOException e) {
            if (directory == null) {
                 addError("Unable to read BMP file header", metadata);
            } else {
                directory.addError("Unable to read BMP file header");
            }
        }
    }

    protected void readBitmapHeader(@NotNull final SequentialReader reader, final @NotNull BmpHeaderDirectory directory, final @NotNull Metadata metadata) {
        /*
         * BITMAPCOREHEADER (12 bytes):
         *
         *    DWORD Size              - Size of this header in bytes
         *    SHORT Width             - Image width in pixels
         *    SHORT Height            - Image height in pixels
         *    WORD  Planes            - Number of color planes
         *    WORD  BitsPerPixel      - Number of bits per pixel
         *
         * OS21XBITMAPHEADER (12 bytes):
         *
         *    DWORD  Size             - Size of this structure in bytes
         *    WORD   Width            - Bitmap width in pixels
         *    WORD   Height           - Bitmap height in pixel
         *      WORD   NumPlanes        - Number of bit planes (color depth)
         *    WORD   BitsPerPixel     - Number of bits per pixel per plane
         *
         * OS22XBITMAPHEADER (16/64 bytes):
         *
         *    DWORD  Size             - Size of this structure in bytes
         *    DWORD  Width            - Bitmap width in pixels
         *    DWORD  Height           - Bitmap height in pixel
         *      WORD   NumPlanes        - Number of bit planes (color depth)
         *    WORD   BitsPerPixel     - Number of bits per pixel per plane
         *
         *    - Short version ends here -
         *
         *    DWORD  Compression      - Bitmap compression scheme
         *    DWORD  ImageDataSize    - Size of bitmap data in bytes
         *    DWORD  XResolution      - X resolution of display device
         *    DWORD  YResolution      - Y resolution of display device
         *    DWORD  ColorsUsed       - Number of color table indices used
         *    DWORD  ColorsImportant  - Number of important color indices
         *    WORD   Units            - Type of units used to measure resolution
         *    WORD   Reserved         - Pad structure to 4-byte boundary
         *    WORD   Recording        - Recording algorithm
         *    WORD   Rendering        - Halftoning algorithm used
         *    DWORD  Size1            - Reserved for halftoning algorithm use
         *    DWORD  Size2            - Reserved for halftoning algorithm use
         *    DWORD  ColorEncoding    - Color model used in bitmap
         *    DWORD  Identifier       - Reserved for application use
         *
         * BITMAPINFOHEADER (40 bytes), BITMAPV2INFOHEADER (52 bytes), BITMAPV3INFOHEADER (56 bytes),
         * BITMAPV4HEADER (108 bytes) and BITMAPV5HEADER (124 bytes):
         *
         *    DWORD Size              - Size of this header in bytes
         *    LONG  Width             - Image width in pixels
         *    LONG  Height            - Image height in pixels
         *    WORD  Planes            - Number of color planes
         *    WORD  BitsPerPixel      - Number of bits per pixel
         *    DWORD Compression       - Compression methods used
         *    DWORD SizeOfBitmap      - Size of bitmap in bytes
         *    LONG  HorzResolution    - Horizontal resolution in pixels per meter
         *    LONG  VertResolution    - Vertical resolution in pixels per meter
         *    DWORD ColorsUsed        - Number of colors in the image
         *    DWORD ColorsImportant   - Minimum number of important colors
         *
         *    - BITMAPINFOHEADER ends here -
         *
         *    DWORD RedMask           - Mask identifying bits of red component
         *    DWORD GreenMask         - Mask identifying bits of green component
         *    DWORD BlueMask          - Mask identifying bits of blue component
         *
         *    - BITMAPV2INFOHEADER ends here -
         *
         *    DWORD AlphaMask         - Mask identifying bits of alpha component
         *
         *    - BITMAPV3INFOHEADER ends here -
         *
         *    DWORD CSType            - Color space type
         *    LONG  RedX              - X coordinate of red endpoint
         *    LONG  RedY              - Y coordinate of red endpoint
         *    LONG  RedZ              - Z coordinate of red endpoint
         *    LONG  GreenX            - X coordinate of green endpoint
         *    LONG  GreenY            - Y coordinate of green endpoint
         *    LONG  GreenZ            - Z coordinate of green endpoint
         *    LONG  BlueX             - X coordinate of blue endpoint
         *    LONG  BlueY             - Y coordinate of blue endpoint
         *    LONG  BlueZ             - Z coordinate of blue endpoint
         *    DWORD GammaRed          - Gamma red coordinate scale value
         *    DWORD GammaGreen        - Gamma green coordinate scale value
         *    DWORD GammaBlue         - Gamma blue coordinate scale value
         *
         *    - BITMAPV4HEADER ends here -
         *
         *    DWORD Intent            - Rendering intent for bitmap
         *    DWORD ProfileData       - Offset of the profile data relative to BITMAPV5HEADER
         *    DWORD ProfileSize       - Size, in bytes, of embedded profile data
         *    DWORD Reserved          - Shall be zero
         *
         */

        try {
            int bitmapType = directory.getInt(BmpHeaderDirectory.TAG_BITMAP_TYPE);
            long headerOffset = reader.getPosition();
            int headerSize = reader.getInt32();

            directory.setInt(BmpHeaderDirectory.TAG_HEADER_SIZE, headerSize);

            /*
             * Known header type sizes:
             *
             *  12 - BITMAPCOREHEADER or OS21XBITMAPHEADER
             *  16 - OS22XBITMAPHEADER (short)
             *  40 - BITMAPINFOHEADER
             *  52 - BITMAPV2INFOHEADER
             *  56 - BITMAPV3INFOHEADER
             *  64 - OS22XBITMAPHEADER (full)
             * 108 - BITMAPV4HEADER
             * 124 - BITMAPV5HEADER
             *
             */

            if (headerSize == 12 && bitmapType == BITMAP) { //BITMAPCOREHEADER
                /*
                 * There's no way to tell BITMAPCOREHEADER and OS21XBITMAPHEADER
                 * apart for the "standard" bitmap type. The difference is only
                 * that BITMAPCOREHEADER has signed width and height while
                 * in OS21XBITMAPHEADER they are unsigned. Since BITMAPCOREHEADER,
                 * the Windows version, is most common, read them as signed.
                 */
                directory.setInt(BmpHeaderDirectory.TAG_IMAGE_WIDTH, reader.getInt16());
                directory.setInt(BmpHeaderDirectory.TAG_IMAGE_HEIGHT, reader.getInt16());
                directory.setInt(BmpHeaderDirectory.TAG_COLOUR_PLANES, reader.getUInt16());
                directory.setInt(BmpHeaderDirectory.TAG_BITS_PER_PIXEL, reader.getUInt16());
            } else if (headerSize == 12) { // OS21XBITMAPHEADER
                directory.setInt(BmpHeaderDirectory.TAG_IMAGE_WIDTH, reader.getUInt16());
                directory.setInt(BmpHeaderDirectory.TAG_IMAGE_HEIGHT, reader.getUInt16());
                directory.setInt(BmpHeaderDirectory.TAG_COLOUR_PLANES, reader.getUInt16());
                directory.setInt(BmpHeaderDirectory.TAG_BITS_PER_PIXEL, reader.getUInt16());
            } else if (headerSize == 16 || headerSize == 64) { // OS22XBITMAPHEADER
                directory.setInt(BmpHeaderDirectory.TAG_IMAGE_WIDTH, reader.getInt32());
                directory.setInt(BmpHeaderDirectory.TAG_IMAGE_HEIGHT, reader.getInt32());
                directory.setInt(BmpHeaderDirectory.TAG_COLOUR_PLANES, reader.getUInt16());
                directory.setInt(BmpHeaderDirectory.TAG_BITS_PER_PIXEL, reader.getUInt16());
                if (headerSize > 16) {
                    directory.setInt(BmpHeaderDirectory.TAG_COMPRESSION, reader.getInt32());
                    reader.skip(4); // skip the pixel data length
                    directory.setInt(BmpHeaderDirectory.TAG_X_PIXELS_PER_METER, reader.getInt32());
                    directory.setInt(BmpHeaderDirectory.TAG_Y_PIXELS_PER_METER, reader.getInt32());
                    directory.setInt(BmpHeaderDirectory.TAG_PALETTE_COLOUR_COUNT, reader.getInt32());
                    directory.setInt(BmpHeaderDirectory.TAG_IMPORTANT_COLOUR_COUNT, reader.getInt32());
                    reader.skip(
                        2 + // Skip Units, can only be 0 (pixels per meter)
                        2 + // Skip padding
                        2   // Skip Recording, can only be 0 (left to right, bottom to top)
                    );
                    directory.setInt(BmpHeaderDirectory.TAG_RENDERING, reader.getUInt16());
                    reader.skip(4 + 4); // Skip Size1 and Size2
                    directory.setInt(BmpHeaderDirectory.TAG_COLOR_ENCODING, reader.getInt32());
                    reader.skip(4); // Skip Identifier
                }
            } else if (
                headerSize == 40 || headerSize == 52 || headerSize == 56 ||
                headerSize == 108 || headerSize == 124
            ) { // BITMAPINFOHEADER V1-5
                directory.setInt(BmpHeaderDirectory.TAG_IMAGE_WIDTH, reader.getInt32());
                directory.setInt(BmpHeaderDirectory.TAG_IMAGE_HEIGHT, reader.getInt32());
                directory.setInt(BmpHeaderDirectory.TAG_COLOUR_PLANES, reader.getUInt16());
                directory.setInt(BmpHeaderDirectory.TAG_BITS_PER_PIXEL, reader.getUInt16());
                directory.setInt(BmpHeaderDirectory.TAG_COMPRESSION, reader.getInt32());
                // skip the pixel data length
                reader.skip(4);
                directory.setInt(BmpHeaderDirectory.TAG_X_PIXELS_PER_METER, reader.getInt32());
                directory.setInt(BmpHeaderDirectory.TAG_Y_PIXELS_PER_METER, reader.getInt32());
                directory.setInt(BmpHeaderDirectory.TAG_PALETTE_COLOUR_COUNT, reader.getInt32());
                directory.setInt(BmpHeaderDirectory.TAG_IMPORTANT_COLOUR_COUNT, reader.getInt32());
                if (headerSize == 40) { // BITMAPINFOHEADER end
                    return;
                }
                directory.setLong(BmpHeaderDirectory.TAG_RED_MASK, reader.getUInt32());
                directory.setLong(BmpHeaderDirectory.TAG_GREEN_MASK, reader.getUInt32());
                directory.setLong(BmpHeaderDirectory.TAG_BLUE_MASK, reader.getUInt32());
                if (headerSize == 52) { // BITMAPV2INFOHEADER end
                    return;
                }
                directory.setLong(BmpHeaderDirectory.TAG_ALPHA_MASK, reader.getUInt32());
                if (headerSize == 56) { // BITMAPV3INFOHEADER end
                    return;
                }
                long csType = reader.getUInt32();
                directory.setLong(BmpHeaderDirectory.TAG_COLOR_SPACE_TYPE, csType);
                reader.skip(36); // Skip color endpoint coordinates
                directory.setLong(BmpHeaderDirectory.TAG_GAMMA_RED, reader.getUInt32());
                directory.setLong(BmpHeaderDirectory.TAG_GAMMA_GREEN, reader.getUInt32());
                directory.setLong(BmpHeaderDirectory.TAG_GAMMA_BLUE, reader.getUInt32());
                if (headerSize == 108) { // BITMAPV4HEADER end
                    return;
                }
                directory.setInt(BmpHeaderDirectory.TAG_INTENT, reader.getInt32());
                if (csType == ColorSpaceType.PROFILE_EMBEDDED.getValue() || csType == ColorSpaceType.PROFILE_LINKED.getValue()) {
                    long profileOffset = reader.getUInt32();
                    int profileSize = reader.getInt32();
                    if (reader.getPosition() > headerOffset + profileOffset) {
                        directory.addError("Invalid profile data offset 0x" + Long.toHexString(headerOffset + profileOffset));
                        return;
                    }
                    reader.skip(headerOffset + profileOffset - reader.getPosition());
                    if (csType == ColorSpaceType.PROFILE_LINKED.getValue()) {
                        directory.setString(BmpHeaderDirectory.TAG_LINKED_PROFILE, reader.getNullTerminatedString(profileSize, Charsets.WINDOWS_1252));
                    } else {
                        ByteArrayReader randomAccessReader = new ByteArrayReader(reader.getBytes(profileSize));
                        new IccReader().extract(randomAccessReader, metadata, directory);
                    }
                } else {
                    reader.skip(
                        4 + // Skip ProfileData offset
                        4 + // Skip ProfileSize
                        4   // Skip Reserved
                    );
                }
            } else {
                directory.addError("Unexpected DIB header size: " + headerSize);
            }
        } catch (IOException e) {
            directory.addError("Unable to read BMP header");
        } catch (MetadataException e) {
            directory.addError("Internal error");
        }
    }

    protected void addError(@NotNull String errorMessage, final @NotNull Metadata metadata) {
        ErrorDirectory directory = metadata.getFirstDirectoryOfType(ErrorDirectory.class);
        if (directory == null) {
            metadata.addDirectory(new ErrorDirectory(errorMessage));
        } else {
            directory.addError(errorMessage);
        }
    }
}

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
package com.drew.metadata.gif;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.Charsets;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import com.drew.metadata.Directory;
import com.drew.metadata.ErrorDirectory;
import com.drew.metadata.Metadata;
import com.drew.metadata.StringValue;
import com.drew.metadata.icc.IccReader;
import com.drew.metadata.xmp.XmpReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Reader of GIF encoded data.
 *
 * Resources:
 * <ul>
 *     <li>https://wiki.whatwg.org/wiki/GIF</li>
 *     <li>https://www.w3.org/Graphics/GIF/spec-gif89a.txt</li>
 *     <li>http://web.archive.org/web/20100929230301/http://www.etsimo.uniovi.es/gifanim/gif87a.txt</li>
 * </ul>
 *
 * @author Drew Noakes https://drewnoakes.com
 * @author Kevin Mott https://github.com/kwhopper
 */
public class GifReader
{
    private static final String GIF_87A_VERSION_IDENTIFIER = "87a";
    private static final String GIF_89A_VERSION_IDENTIFIER = "89a";

    public void extract(@NotNull final SequentialReader reader, final @NotNull Metadata metadata)
    {
        reader.setMotorolaByteOrder(false);

        GifHeaderDirectory header;
        try {
            header = ReadGifHeader(reader);
            metadata.addDirectory(header);
        } catch (IOException ex) {
            metadata.addDirectory(new ErrorDirectory("IOException processing GIF data"));
            return;
        }

        if(header.hasErrors())
            return;

        try {
            // Skip over any global colour table
            Integer globalColorTableSize = header.getInteger(GifHeaderDirectory.TAG_COLOR_TABLE_SIZE);
            if (globalColorTableSize != null)
            {
                // Colour table has R/G/B byte triplets
                reader.skip(3 * globalColorTableSize);
            }

            // After the header comes a sequence of blocks
            while (true)
            {
                byte marker;
                try {
                    marker = reader.getInt8();
                } catch (IOException ex) {
                    return;
                }

                switch (marker)
                {
                    case (byte)'!': // 0x21
                    {
                        metadata.addDirectory(ReadGifExtensionBlock(reader));
                        break;
                    }
                    case (byte)',': // 0x2c
                    {
                        metadata.addDirectory(ReadImageBlock(reader));

                        // skip image data blocks
                        SkipBlocks(reader);
                        break;
                    }
                    case (byte)';': // 0x3b
                    {
                        // terminator
                        return;
                    }
                    default:
                    {
                        // Anything other than these types is unexpected.
                        // GIF87a spec says to keep reading until a separator is found.
                        // GIF89a spec says file is corrupt.
                        return;
                    }
                }
            }
        } catch (IOException e) {
            metadata.addDirectory(new ErrorDirectory("IOException processing GIF data"));
        }
    }

    private static GifHeaderDirectory ReadGifHeader(@NotNull final SequentialReader reader) throws IOException
    {
        // FILE HEADER
        //
        // 3 - signature: "GIF"
        // 3 - version: either "87a" or "89a"
        //
        // LOGICAL SCREEN DESCRIPTOR
        //
        // 2 - pixel width
        // 2 - pixel height
        // 1 - screen and color map information flags (0 is LSB)
        //       0-2  Size of the global color table
        //       3    Color table sort flag (89a only)
        //       4-6  Color resolution
        //       7    Global color table flag
        // 1 - background color index
        // 1 - pixel aspect ratio

        GifHeaderDirectory headerDirectory = new GifHeaderDirectory();

        //try {
        String signature = reader.getString(3);

        if (!signature.equals("GIF"))
        {
            headerDirectory.addError("Invalid GIF file signature");
            return headerDirectory;
        }

        String version = reader.getString(3);

        if (!version.equals(GIF_87A_VERSION_IDENTIFIER) && !version.equals(GIF_89A_VERSION_IDENTIFIER)) {
            headerDirectory.addError("Unexpected GIF version");
            return headerDirectory;
        }

        headerDirectory.setString(GifHeaderDirectory.TAG_GIF_FORMAT_VERSION, version);

        // LOGICAL SCREEN DESCRIPTOR

        headerDirectory.setInt(GifHeaderDirectory.TAG_IMAGE_WIDTH, reader.getUInt16());
        headerDirectory.setInt(GifHeaderDirectory.TAG_IMAGE_HEIGHT, reader.getUInt16());

        short flags = reader.getUInt8();

        // First three bits = (BPP - 1)
        int colorTableSize = 1 << ((flags & 7) + 1);
        int bitsPerPixel = ((flags & 0x70) >> 4) + 1;
        boolean hasGlobalColorTable = (flags & 0xf) != 0;

        headerDirectory.setInt(GifHeaderDirectory.TAG_COLOR_TABLE_SIZE, colorTableSize);

        if (version.equals(GIF_89A_VERSION_IDENTIFIER)) {
            boolean isColorTableSorted = (flags & 8) != 0;
            headerDirectory.setBoolean(GifHeaderDirectory.TAG_IS_COLOR_TABLE_SORTED, isColorTableSorted);
        }

        headerDirectory.setInt(GifHeaderDirectory.TAG_BITS_PER_PIXEL, bitsPerPixel);
        headerDirectory.setBoolean(GifHeaderDirectory.TAG_HAS_GLOBAL_COLOR_TABLE, hasGlobalColorTable);

        headerDirectory.setInt(GifHeaderDirectory.TAG_BACKGROUND_COLOR_INDEX, reader.getUInt8());

        int aspectRatioByte = reader.getUInt8();
        if (aspectRatioByte != 0) {
            float pixelAspectRatio = (float)((aspectRatioByte + 15d) / 64d);
            headerDirectory.setFloat(GifHeaderDirectory.TAG_PIXEL_ASPECT_RATIO, pixelAspectRatio);
        }

        /*} catch (IOException e) {
            headerDirectory.addError("Unable to read GIF header");
        }*/

        return headerDirectory;
    }

    private static Directory ReadGifExtensionBlock(SequentialReader reader) throws IOException
    {
        byte extensionLabel = reader.getInt8();
        short blockSizeBytes = reader.getUInt8();
        long blockStartPos = reader.getPosition();

        Directory directory;
        switch (extensionLabel)
        {
            case (byte) 0x01:
                directory = ReadPlainTextBlock(reader, blockSizeBytes);
                break;
            case (byte) 0xf9:
                directory = ReadControlBlock(reader, blockSizeBytes);
                break;
            case (byte) 0xfe:
                directory = ReadCommentBlock(reader, blockSizeBytes);
                break;
            case (byte) 0xff:
                directory = ReadApplicationExtensionBlock(reader, blockSizeBytes);
                break;
            default:
                directory = new ErrorDirectory(String.format("Unsupported GIF extension block with type 0x%02X.", extensionLabel));
                break;
        }

        long skipCount = blockStartPos + blockSizeBytes - reader.getPosition();
        if (skipCount > 0)
            reader.skip(skipCount);

        return directory;
    }

    private static Directory ReadPlainTextBlock(SequentialReader reader, int blockSizeBytes) throws IOException
    {
        // It seems this extension is deprecated. If somebody finds an image with this in it, could implement here.
        // Just skip the entire block for now.

        if (blockSizeBytes != 12)
            return new ErrorDirectory(String.format("Invalid GIF plain text block size. Expected 12, got %d.", blockSizeBytes));

        // skip 'blockSizeBytes' bytes
        reader.skip(12);

        // keep reading and skipping until a 0 byte is reached
        SkipBlocks(reader);

        return null;
    }

    private static GifCommentDirectory ReadCommentBlock(SequentialReader reader, int blockSizeBytes) throws IOException
    {
        byte[] buffer = GatherBytes(reader, blockSizeBytes);
        return new GifCommentDirectory(new StringValue(buffer, Charsets.ASCII));
    }

    @Nullable
    private static Directory ReadApplicationExtensionBlock(SequentialReader reader, int blockSizeBytes) throws IOException
    {
        if (blockSizeBytes != 11)
            return new ErrorDirectory(String.format("Invalid GIF application extension block size. Expected 11, got %d.", blockSizeBytes));

        String extensionType = reader.getString(blockSizeBytes, Charsets.UTF_8);

        if (extensionType.equals("XMP DataXMP"))
        {
            // XMP data extension
            byte[] xmpBytes = GatherBytes(reader);
            return new XmpReader().extract(xmpBytes, 0, xmpBytes.length - 257);
        }
        else if (extensionType.equals("ICCRGBG1012"))
        {
            // ICC profile extension
            byte[] iccBytes = GatherBytes(reader, reader.getByte());
            return iccBytes.length != 0
                ? new IccReader().extract(new ByteArrayReader(iccBytes)) //, metadata)
                : null;
        }
        else if (extensionType.equals("NETSCAPE2.0"))
        {
            reader.skip(2);
            // Netscape's animated GIF extension
            // Iteration count (0 means infinite)
            int iterationCount = reader.getUInt16();
            // Skip terminator
            reader.skip(1);
            GifAnimationDirectory animationDirectory = new GifAnimationDirectory();
            animationDirectory.setInt(GifAnimationDirectory.TagIterationCount, iterationCount);
            return animationDirectory;
        }
        else
        {
            SkipBlocks(reader);
            return null;
        }
    }

    private static GifControlDirectory ReadControlBlock(SequentialReader reader, int blockSizeBytes) throws IOException
    {
        if (blockSizeBytes < 4)
            blockSizeBytes = 4;

        GifControlDirectory directory = new GifControlDirectory();

        reader.skip(1);

        directory.setInt(GifControlDirectory.TagDelay, reader.getUInt16());

        if (blockSizeBytes > 3)
            reader.skip(blockSizeBytes - 3);

        // skip 0x0 block terminator
        reader.getByte();

        return directory;
    }

    private static GifImageDirectory ReadImageBlock(SequentialReader reader) throws IOException
    {
        GifImageDirectory imageDirectory = new GifImageDirectory();

        imageDirectory.setInt(GifImageDirectory.TagLeft, reader.getUInt16());
        imageDirectory.setInt(GifImageDirectory.TagTop, reader.getUInt16());
        imageDirectory.setInt(GifImageDirectory.TagWidth, reader.getUInt16());
        imageDirectory.setInt(GifImageDirectory.TagHeight, reader.getUInt16());

        byte flags = reader.getByte();
        boolean hasColorTable = (flags & 0x7) != 0;
        boolean isInterlaced = (flags & 0x40) != 0;
        boolean isColorTableSorted = (flags & 0x20) != 0;

        imageDirectory.setBoolean(GifImageDirectory.TagHasLocalColourTable, hasColorTable);
        imageDirectory.setBoolean(GifImageDirectory.TagIsInterlaced, isInterlaced);

        if (hasColorTable)
        {
            imageDirectory.setBoolean(GifImageDirectory.TagIsColorTableSorted, isColorTableSorted);

            int bitsPerPixel = (flags & 0x7) + 1;
            imageDirectory.setInt(GifImageDirectory.TagLocalColourTableBitsPerPixel, bitsPerPixel);

            // skip color table
            reader.skip(3 * (2 << (flags & 0x7)));
        }

        // skip "LZW Minimum Code Size" byte
        reader.getByte();

        return imageDirectory;
    }

    private static byte[] GatherBytes(SequentialReader reader) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[257];

        while (true)
        {
            byte b = reader.getByte();
            if (b == 0)
                return bytes.toByteArray();

            int bInt = (int)(b & 0xFF);

            buffer[0] = b;
            reader.getBytes(buffer, 1, bInt);
            bytes.write(buffer, 0, bInt + 1);
        }
    }

    private static byte[] GatherBytes(SequentialReader reader, int firstLength) throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int length = firstLength;

        while (length > 0)
        {
            buffer.write(reader.getBytes(length), 0, length);

            length = reader.getByte();
        }

        return buffer.toByteArray();
    }

    private static void SkipBlocks(SequentialReader reader) throws IOException
    {
        while (true)
        {
            short length = reader.getUInt8();

            if (length == 0)
                return;

            reader.skip(length);
        }
    }
}

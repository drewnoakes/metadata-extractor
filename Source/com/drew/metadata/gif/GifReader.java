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
package com.drew.metadata.gif;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.Charsets;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import com.drew.metadata.*;
import com.drew.metadata.gif.GifControlDirectory.DisposalMethod;
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
            header = readGifHeader(reader);
            metadata.addDirectory(header);
        } catch (IOException ex) {
            metadata.addDirectory(new ErrorDirectory("IOException processing GIF data"));
            return;
        }

        if(header.hasErrors())
            return;

        try {
            // Skip over any global colour table if GlobalColorTable is present.
            Integer globalColorTableSize = null;
            try {
                boolean hasGlobalColorTable = header.getBoolean(GifHeaderDirectory.TAG_HAS_GLOBAL_COLOR_TABLE);
                if(hasGlobalColorTable) {
                    globalColorTableSize = header.getInteger(GifHeaderDirectory.TAG_COLOR_TABLE_SIZE);
                }
            } catch (MetadataException e) {
                // This exception should never occur here.
                metadata.addDirectory(new ErrorDirectory("GIF did not had hasGlobalColorTable bit."));
            }
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
                        readGifExtensionBlock(reader, metadata);
                        break;
                    }
                    case (byte)',': // 0x2c
                    {
                        metadata.addDirectory(readImageBlock(reader));

                        // skip image data blocks
                        skipBlocks(reader);
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
                        metadata.addDirectory(new ErrorDirectory("Unknown gif block marker found."));
                        return;
                    }
                }
            }
        } catch (IOException e) {
            metadata.addDirectory(new ErrorDirectory("IOException processing GIF data"));
        }
    }

    private static GifHeaderDirectory readGifHeader(@NotNull final SequentialReader reader) throws IOException
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
        boolean hasGlobalColorTable = (flags >> 7) != 0;

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

        return headerDirectory;
    }

    private static void readGifExtensionBlock(SequentialReader reader, Metadata metadata) throws IOException
    {
        byte extensionLabel = reader.getInt8();
        short blockSizeBytes = reader.getUInt8();
        long blockStartPos = reader.getPosition();

        switch (extensionLabel)
        {
            case (byte) 0x01:
                Directory plainTextBlock = readPlainTextBlock(reader, blockSizeBytes);
                if (plainTextBlock != null)
                    metadata.addDirectory(plainTextBlock);
                break;
            case (byte) 0xf9:
                metadata.addDirectory(readControlBlock(reader, blockSizeBytes));
                break;
            case (byte) 0xfe:
                metadata.addDirectory(readCommentBlock(reader, blockSizeBytes));
                break;
            case (byte) 0xff:
                readApplicationExtensionBlock(reader, blockSizeBytes, metadata);
                break;
            default:
                metadata.addDirectory(new ErrorDirectory(String.format("Unsupported GIF extension block with type 0x%02X.", extensionLabel)));
                break;
        }

        long skipCount = blockStartPos + blockSizeBytes - reader.getPosition();
        if (skipCount > 0)
            reader.skip(skipCount);
    }

    @Nullable
    private static Directory readPlainTextBlock(SequentialReader reader, int blockSizeBytes) throws IOException
    {
        // It seems this extension is deprecated. If somebody finds an image with this in it, could implement here.
        // Just skip the entire block for now.

        if (blockSizeBytes != 12)
            return new ErrorDirectory(String.format("Invalid GIF plain text block size. Expected 12, got %d.", blockSizeBytes));

        // skip 'blockSizeBytes' bytes
        reader.skip(12);

        // keep reading and skipping until a 0 byte is reached
        skipBlocks(reader);

        return null;
    }

    private static GifCommentDirectory readCommentBlock(SequentialReader reader, int blockSizeBytes) throws IOException
    {
        byte[] buffer = gatherBytes(reader, blockSizeBytes);
        return new GifCommentDirectory(new StringValue(buffer, Charsets.ASCII));
    }

    private static void readApplicationExtensionBlock(SequentialReader reader, int blockSizeBytes, Metadata metadata) throws IOException
    {
        if (blockSizeBytes != 11)
        {
            metadata.addDirectory(new ErrorDirectory(String.format("Invalid GIF application extension block size. Expected 11, got %d.", blockSizeBytes)));
            return;
        }

        String extensionType = reader.getString(blockSizeBytes, Charsets.UTF_8);

        if (extensionType.equals("XMP DataXMP"))
        {
            // XMP data extension
            byte[] xmpBytes = gatherBytes(reader);
            new XmpReader().extract(xmpBytes, 0, xmpBytes.length - 257, metadata, null);
        }
        else if (extensionType.equals("ICCRGBG1012"))
        {
            // ICC profile extension
            byte[] iccBytes = gatherBytes(reader, ((int) reader.getByte()) & 0xff);
            if (iccBytes.length != 0)
                new IccReader().extract(new ByteArrayReader(iccBytes), metadata);
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
            animationDirectory.setInt(GifAnimationDirectory.TAG_ITERATION_COUNT, iterationCount);
            metadata.addDirectory(animationDirectory);
        }
        else
        {
            skipBlocks(reader);
        }
    }

    private static GifControlDirectory readControlBlock(SequentialReader reader, int blockSizeBytes) throws IOException
    {
        if (blockSizeBytes < 4)
            blockSizeBytes = 4;

        GifControlDirectory directory = new GifControlDirectory();

        short packedFields = reader.getUInt8();
        directory.setObject(GifControlDirectory.TAG_DISPOSAL_METHOD, DisposalMethod.typeOf((packedFields >> 2) & 7));
        directory.setBoolean(GifControlDirectory.TAG_USER_INPUT_FLAG, (packedFields & 2) >> 1 == 1);
        directory.setBoolean(GifControlDirectory.TAG_TRANSPARENT_COLOR_FLAG, (packedFields & 1) == 1);
        directory.setInt(GifControlDirectory.TAG_DELAY, reader.getUInt16());
        directory.setInt(GifControlDirectory.TAG_TRANSPARENT_COLOR_INDEX, reader.getUInt8());

        // skip 0x0 block terminator
        reader.skip(1);

        return directory;
    }

    private static GifImageDirectory readImageBlock(SequentialReader reader) throws IOException
    {
        GifImageDirectory imageDirectory = new GifImageDirectory();

        imageDirectory.setInt(GifImageDirectory.TAG_LEFT, reader.getUInt16());
        imageDirectory.setInt(GifImageDirectory.TAG_TOP, reader.getUInt16());
        imageDirectory.setInt(GifImageDirectory.TAG_WIDTH, reader.getUInt16());
        imageDirectory.setInt(GifImageDirectory.TAG_HEIGHT, reader.getUInt16());

        byte flags = reader.getByte();
        boolean hasColorTable = (flags >> 7) != 0;
        boolean isInterlaced = (flags & 0x40) != 0;

        imageDirectory.setBoolean(GifImageDirectory.TAG_HAS_LOCAL_COLOUR_TABLE, hasColorTable);
        imageDirectory.setBoolean(GifImageDirectory.TAG_IS_INTERLACED, isInterlaced);

        if (hasColorTable)
        {
            boolean isColorTableSorted = (flags & 0x20) != 0;
            imageDirectory.setBoolean(GifImageDirectory.TAG_IS_COLOR_TABLE_SORTED, isColorTableSorted);

            int bitsPerPixel = (flags & 0x7) + 1;
            imageDirectory.setInt(GifImageDirectory.TAG_LOCAL_COLOUR_TABLE_BITS_PER_PIXEL, bitsPerPixel);

            // skip color table
            reader.skip(3 * (2 << (flags & 0x7)));
        }

        // skip "LZW Minimum Code Size" byte
        reader.getByte();

        return imageDirectory;
    }

    private static byte[] gatherBytes(SequentialReader reader) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[257];

        while (true)
        {
            byte b = reader.getByte();
            if (b == 0)
                return bytes.toByteArray();

            int bInt = b & 0xFF;

            buffer[0] = b;
            reader.getBytes(buffer, 1, bInt);
            bytes.write(buffer, 0, bInt + 1);
        }
    }

    private static byte[] gatherBytes(SequentialReader reader, int firstLength) throws IOException
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int length = firstLength;

        while (length > 0)
        {
            buffer.write(reader.getBytes(length), 0, length);

            length = reader.getByte() & 0xff;
        }

        return buffer.toByteArray();
    }

    private static void skipBlocks(SequentialReader reader) throws IOException
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

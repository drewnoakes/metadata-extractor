package com.drew.metadata.gif;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class GifReader
{
    private static final String GIF_87A_VERSION_IDENTIFIER = "87a";
    private static final String GIF_89A_VERSION_IDENTIFIER = "89a";

    public void extract(@NotNull final SequentialReader reader, final @NotNull Metadata metadata)
    {
        GifHeaderDirectory directory = new GifHeaderDirectory();
        metadata.addDirectory(directory);

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

        reader.setMotorolaByteOrder(false);

        try {
            String signature = reader.getString(3);

            if (!signature.equals("GIF"))
            {
                directory.addError("Invalid GIF file signature");
                return;
            }

            String version = reader.getString(3);

            if (!version.equals(GIF_87A_VERSION_IDENTIFIER) && !version.equals(GIF_89A_VERSION_IDENTIFIER)) {
                directory.addError("Unexpected GIF version");
                return;
            }

            directory.setString(GifHeaderDirectory.TAG_GIF_FORMAT_VERSION, version);
            directory.setInt(GifHeaderDirectory.TAG_IMAGE_WIDTH, reader.getUInt16());
            directory.setInt(GifHeaderDirectory.TAG_IMAGE_HEIGHT, reader.getUInt16());

            short flags = reader.getUInt8();

            // First three bits = (BPP - 1)
            int colorTableSize = 1 << ((flags & 7) + 1);
            directory.setInt(GifHeaderDirectory.TAG_COLOR_TABLE_SIZE, colorTableSize);

            if (version.equals(GIF_89A_VERSION_IDENTIFIER)) {
                boolean isColorTableSorted = (flags & 8) != 0;
                directory.setBoolean(GifHeaderDirectory.TAG_IS_COLOR_TABLE_SORTED, isColorTableSorted);
            }

            int bitsPerPixel = ((flags & 0x70) >> 4) + 1;
            directory.setInt(GifHeaderDirectory.TAG_BITS_PER_PIXEL, bitsPerPixel);

            boolean hasGlobalColorTable = (flags & 0xf) != 0;
            directory.setBoolean(GifHeaderDirectory.TAG_HAS_GLOBAL_COLOR_TABLE, hasGlobalColorTable);

            directory.setInt(GifHeaderDirectory.TAG_TRANSPARENT_COLOR_INDEX, reader.getUInt8());

            int aspectRatioByte = reader.getUInt8();
            if (aspectRatioByte != 0) {
                float pixelAspectRatio = (float)((aspectRatioByte + 15d) / 64d);
                directory.setFloat(GifHeaderDirectory.TAG_PIXEL_ASPECT_RATIO, pixelAspectRatio);
            }

        } catch (IOException e) {
            directory.addError("Unable to read BMP header");
        }
    }
}

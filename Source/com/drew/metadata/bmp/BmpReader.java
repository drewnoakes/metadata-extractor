package com.drew.metadata.bmp;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class BmpReader
{
    public void extract(@NotNull final SequentialReader reader, final @NotNull Metadata metadata)
    {
        BmpHeaderDirectory directory = new BmpHeaderDirectory();
        metadata.addDirectory(directory);

        // FILE HEADER
        //
        // 2 - magic number (0x42 0x4D = "BM")
        // 4 - size of BMP file in bytes
        // 2 - reserved
        // 2 - reserved
        // 4 - the offset of the pixel array
        //
        // BITMAP INFORMATION HEADER
        //
        // The first four bytes of the header give the size, which is a discriminator of the actual header format.
        // See this for more information http://en.wikipedia.org/wiki/BMP_file_format
        //
        // BITMAPINFOHEADER (size = 40)
        //
        // 4 - size of header
        // 4 - pixel width (signed)
        // 4 - pixel height (signed)
        // 2 - number of colour planes (must be set to 1)
        // 2 - number of bits per pixel
        // 4 - compression being used (needs decoding)
        // 4 - pixel data length (not total file size, just pixel array)
        // 4 - horizontal resolution, pixels/meter (signed)
        // 4 - vertical resolution, pixels/meter (signed)
        // 4 - number of colours in the palette (0 means no palette)
        // 4 - number of important colours (generally ignored)
        //
        // BITMAPCOREHEADER (size = 12)
        //
        // 4 - size of header
        // 2 - pixel width
        // 2 - pixel height
        // 2 - number of colour planes (must be set to 1)
        // 2 - number of bits per pixel
        //
        // COMPRESSION VALUES
        //
        // 0 = None
        // 1 = RLE 8-bit/pixel
        // 2 = RLE 4-bit/pixel
        // 3 = Bit field (or Huffman 1D if BITMAPCOREHEADER2 (size 64))
        // 4 = JPEG (or RLE-24 if BITMAPCOREHEADER2 (size 64))
        // 5 = PNG
        // 6 = Bit field

        reader.setMotorolaByteOrder(false);

        try {
            final int magicNumber = reader.getUInt16();

            if (magicNumber != 0x4D42)
            {
                directory.addError("Invalid BMP magic number");
                return;
            }

            // skip past the rest of the file header
            reader.skip(4 + 2 + 2 + 4);

            int headerSize = reader.getInt32();

            directory.setInt(BmpHeaderDirectory.TAG_HEADER_SIZE, headerSize);

            // We expect the header size to be either 40 (BITMAPINFOHEADER) or 12 (BITMAPCOREHEADER)
            if (headerSize == 40) {
                // BITMAPINFOHEADER
                directory.setInt(BmpHeaderDirectory.TAG_IMAGE_WIDTH, reader.getInt32());
                directory.setInt(BmpHeaderDirectory.TAG_IMAGE_HEIGHT, reader.getInt32());
                directory.setInt(BmpHeaderDirectory.TAG_COLOUR_PLANES, reader.getInt16());
                directory.setInt(BmpHeaderDirectory.TAG_BITS_PER_PIXEL, reader.getInt16());
                directory.setInt(BmpHeaderDirectory.TAG_COMPRESSION, reader.getInt32());
                // skip the pixel data length
                reader.skip(4);
                directory.setInt(BmpHeaderDirectory.TAG_X_PIXELS_PER_METER, reader.getInt32());
                directory.setInt(BmpHeaderDirectory.TAG_Y_PIXELS_PER_METER, reader.getInt32());
                directory.setInt(BmpHeaderDirectory.TAG_PALETTE_COLOUR_COUNT, reader.getInt32());
                directory.setInt(BmpHeaderDirectory.TAG_IMPORTANT_COLOUR_COUNT, reader.getInt32());
            } else if (headerSize == 12) {
                directory.setInt(BmpHeaderDirectory.TAG_IMAGE_WIDTH, reader.getInt16());
                directory.setInt(BmpHeaderDirectory.TAG_IMAGE_HEIGHT, reader.getInt16());
                directory.setInt(BmpHeaderDirectory.TAG_COLOUR_PLANES, reader.getInt16());
                directory.setInt(BmpHeaderDirectory.TAG_BITS_PER_PIXEL, reader.getInt16());
            } else {
                directory.addError("Unexpected DIB header size: " + headerSize);
            }
        } catch (IOException e) {
            directory.addError("Unable to read BMP header");
        }
    }
}

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
package com.drew.metadata.ico;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;

/**
 * Reads ICO (Windows Icon) file metadata.
 * <ul>
 * <li>https://en.wikipedia.org/wiki/ICO_(file_format)</li>
 * </ul>
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class IcoReader
{
    public void extract(@NotNull final SequentialReader reader, @NotNull final Metadata metadata)
    {
        reader.setMotorolaByteOrder(false);

        int type;
        int imageCount;

        // Read header (ICONDIR structure)
        try {
            int reserved = reader.getUInt16();

            if (reserved != 0) {
                IcoDirectory directory = new IcoDirectory();
                directory.addError("Invalid header bytes");
                metadata.addDirectory(directory);
                return;
            }

            type = reader.getUInt16();

            if (type != 1 && type != 2) {
                IcoDirectory directory = new IcoDirectory();
                directory.addError("Invalid type " + type + " -- expecting 1 or 2");
                metadata.addDirectory(directory);
                return;
            }

            imageCount = reader.getUInt16();

            if (imageCount == 0) {
                IcoDirectory directory = new IcoDirectory();
                directory.addError("Image count cannot be zero");
                metadata.addDirectory(directory);
                return;
            }

        } catch (IOException ex) {
            IcoDirectory directory = new IcoDirectory();
            directory.addError("Exception reading ICO file metadata: " + ex.getMessage());
            metadata.addDirectory(directory);
            return;
        }

        // Read each embedded image
        for (int imageIndex = 0; imageIndex < imageCount; imageIndex++) {
            IcoDirectory directory = new IcoDirectory();
            try {
                directory.setInt(IcoDirectory.TAG_IMAGE_TYPE, type);

                directory.setInt(IcoDirectory.TAG_IMAGE_WIDTH, reader.getUInt8());
                directory.setInt(IcoDirectory.TAG_IMAGE_HEIGHT, reader.getUInt8());
                directory.setInt(IcoDirectory.TAG_COLOUR_PALETTE_SIZE, reader.getUInt8());
                // Ignore this byte (normally zero, though .NET's System.Drawing.Icon.Save method writes 255)
                reader.getUInt8();
                if (type == 1) {
                    // Icon
                    directory.setInt(IcoDirectory.TAG_COLOUR_PLANES, reader.getUInt16());
                    directory.setInt(IcoDirectory.TAG_BITS_PER_PIXEL, reader.getUInt16());
                } else {
                    // Cursor
                    directory.setInt(IcoDirectory.TAG_CURSOR_HOTSPOT_X, reader.getUInt16());
                    directory.setInt(IcoDirectory.TAG_CURSOR_HOTSPOT_Y, reader.getUInt16());
                }
                directory.setLong(IcoDirectory.TAG_IMAGE_SIZE_BYTES, reader.getUInt32());
                directory.setLong(IcoDirectory.TAG_IMAGE_OFFSET_BYTES, reader.getUInt32());
            } catch (IOException ex) {
                directory.addError("Exception reading ICO file metadata: " + ex.getMessage());
            }
            metadata.addDirectory(directory);
        }
    }
}

/*
 * Copyright 2002-2019 Drew Noakes and contributors
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

package com.drew.metadata.photoshop;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;

/**
 * Reads metadata stored within PSD file format data.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class PsdReader
{
    public void extract(@NotNull final SequentialReader reader, @NotNull final Metadata metadata)
    {
        PsdHeaderDirectory directory = new PsdHeaderDirectory();
        metadata.addDirectory(directory);

        // FILE HEADER SECTION

        try {
            final int signature = reader.getInt32();
            if (signature != 0x38425053) // "8BPS"
            {
                directory.addError("Invalid PSD file signature");
                return;
            }

            final int version = reader.getUInt16();
            if (version != 1 && version != 2)
            {
                directory.addError("Invalid PSD file version (must be 1 or 2)");
                return;
            }

            // 6 reserved bytes are skipped here.  They should be zero.
            reader.skip(6);

            final int channelCount = reader.getUInt16();
            directory.setInt(PsdHeaderDirectory.TAG_CHANNEL_COUNT, channelCount);

            // even though this is probably an unsigned int, the max height in practice is 300,000
            final int imageHeight = reader.getInt32();
            directory.setInt(PsdHeaderDirectory.TAG_IMAGE_HEIGHT, imageHeight);

            // even though this is probably an unsigned int, the max width in practice is 300,000
            final int imageWidth = reader.getInt32();
            directory.setInt(PsdHeaderDirectory.TAG_IMAGE_WIDTH, imageWidth);

            final int bitsPerChannel = reader.getUInt16();
            directory.setInt(PsdHeaderDirectory.TAG_BITS_PER_CHANNEL, bitsPerChannel);

            final int colorMode = reader.getUInt16();
            directory.setInt(PsdHeaderDirectory.TAG_COLOR_MODE, colorMode);
        } catch (IOException e) {
            directory.addError("Unable to read PSD header");
            return;
        }

        // COLOR MODE DATA SECTION

        try {
            long sectionLength = reader.getUInt32();

            /*
             * Only indexed color and duotone (see the mode field in the File header section) have color mode data.
             * For all other modes, this section is just the 4-byte length field, which is set to zero.
             *
             * Indexed color images: length is 768; color data contains the color table for the image,
             *                       in non-interleaved order.
             * Duotone images: color data contains the duotone specification (the format of which is not documented).
             *                 Other applications that read Photoshop files can treat a duotone image as a gray	image,
             *                 and just preserve the contents of the duotone information when reading and writing the
             *                 file.
             */

            reader.skip(sectionLength);
        } catch (IOException e) {
            return;
        }

        // IMAGE RESOURCES SECTION

        try {
            long sectionLength = reader.getUInt32();

            assert(sectionLength <= Integer.MAX_VALUE);

            new PhotoshopReader().extract(reader, (int)sectionLength, metadata);
        } catch (IOException e) {
            // ignore
        }

        // LAYER AND MASK INFORMATION SECTION (skipped)

        // IMAGE DATA SECTION (skipped)
    }
}

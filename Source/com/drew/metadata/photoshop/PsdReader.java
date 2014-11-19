/*
 * Copyright 2002-2014 Drew Noakes
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

import com.drew.lang.RandomAccessReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;

import java.io.IOException;

/**
 * Reads metadata stored within PSD file format data.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class PsdReader implements MetadataReader
{
    public void extract(@NotNull final RandomAccessReader reader, final @NotNull Metadata metadata)
    {
        final PsdHeaderDirectory directory = metadata.getOrCreateDirectory(PsdHeaderDirectory.class);

        try {
            final int signature = reader.getInt32(0);
            if (signature != 0x38425053)
            {
                directory.addError("Invalid PSD file signature");
                return;
            }

            final int version = reader.getUInt16(4);
            if (version != 1 && version != 2)
            {
                directory.addError("Invalid PSD file version (must be 1 or 2)");
                return;
            }

            // 6 reserved bytes are skipped here.  They should be zero.

            final int channelCount = reader.getUInt16(12);
            directory.setInt(PsdHeaderDirectory.TAG_CHANNEL_COUNT, channelCount);

            // even though this is probably an unsigned int, the max height in practice is 300,000
            final int imageHeight = reader.getInt32(14);
            directory.setInt(PsdHeaderDirectory.TAG_IMAGE_HEIGHT, imageHeight);

            // even though this is probably an unsigned int, the max width in practice is 300,000
            final int imageWidth = reader.getInt32(18);
            directory.setInt(PsdHeaderDirectory.TAG_IMAGE_WIDTH, imageWidth);

            final int bitsPerChannel = reader.getUInt16(22);
            directory.setInt(PsdHeaderDirectory.TAG_BITS_PER_CHANNEL, bitsPerChannel);

            final int colorMode = reader.getUInt16(24);
            directory.setInt(PsdHeaderDirectory.TAG_COLOR_MODE, colorMode);
        } catch (IOException e) {
            directory.addError("Unable to read PSD header");
        }
    }
}

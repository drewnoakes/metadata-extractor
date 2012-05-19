/*
 * Copyright 2002-2012 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */

package com.drew.metadata.adobe;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;

/**
 * Decodes Adobe formatted data stored in JPEG files, normally in the APPE (App14) segment.
 *
 * @author Philip, Drew Noakes http://drewnoakes.com
 */
public class AdobeJpegReader implements MetadataReader
{
    public void extract(@NotNull byte[] data, @NotNull Metadata metadata)
    {
        final Directory directory = metadata.getOrCreateDirectory(AdobeJpegDirectory.class);

        if (data.length != 12) {
            directory.addError(String.format("Adobe JPEG data is expected to be 12 bytes long, not %d.", data.length));
            return;
        }

        try {
            BufferReader reader = new BufferReader(data);
            reader.setMotorolaByteOrder(false);

            if (!reader.getString(0, 5).equals("Adobe")) {
                directory.addError("Invalid Adobe JPEG data header.");
                return;
            }

            directory.setInt(AdobeJpegDirectory.TAG_DCT_ENCODE_VERSION, reader.getUInt16(5));
            directory.setInt(AdobeJpegDirectory.TAG_APP14_FLAGS0, reader.getUInt16(7));
            directory.setInt(AdobeJpegDirectory.TAG_APP14_FLAGS1, reader.getUInt16(9));
            directory.setInt(AdobeJpegDirectory.TAG_COLOR_TRANSFORM, reader.getInt8(11));
        } catch (BufferBoundsException ex) {
            directory.addError("Exif data segment ended prematurely");
        }
    }
}

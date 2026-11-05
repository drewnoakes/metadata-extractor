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
 *    https://github.com/drewnoakes/metadata-extractor/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.imaging.cr3;

import com.drew.imaging.isobmff.IsoBox;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.cr3.Cr3Directory;

import java.io.IOException;

/**
 * Reads the header of a {@code THMB} or {@code PRVW} record: the dimensions of its JPEG and,
 * when the JPEG fits inside the box and the input, where it lies. The JPEG is skipped.
 * <p>
 * Both records start the JPEG 16 bytes into the payload, after a header that holds the width,
 * height and JPEG length at record-specific positions.
 */
final class Cr3JpegRecord
{
    private static final int JPEG_START = 16;

    private Cr3JpegRecord() {}

    static void read(@NotNull IsoBox box, @NotNull SequentialReader payload, int dimensionsAt, int lengthAt,
                     @NotNull Cr3Directory directory, int widthTag, int heightTag, int offsetTag, int lengthTag)
        throws IOException
    {
        payload.skip(dimensionsAt);
        directory.setInt(widthTag, payload.getUInt16());
        directory.setInt(heightTag, payload.getUInt16());
        if (box.payloadSize != -1 && box.payloadSize < JPEG_START)
            return;
        payload.skip(lengthAt - dimensionsAt - 4);
        long length = payload.getUInt32();
        if (length == 0)
            return;
        if (box.payloadSize != -1 && length > box.payloadSize - JPEG_START) {
            directory.addError(box.type + " JPEG length " + length + " exceeds its box");
            return;
        }
        payload.skip(JPEG_START - lengthAt - 4 + length);
        directory.setLong(offsetTag, box.payloadOffset + JPEG_START);
        directory.setLong(lengthTag, length);
    }
}

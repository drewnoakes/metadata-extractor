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
package com.drew.imaging.cr3;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.cr3.Cr3Directory;

import java.io.IOException;

/**
 * Parses the Canon preview UUID ({@code eaf42b5e-1c98-4b88-b9fb-b7dc406e4d16}).
 * <p>
 * The UUID payload has an 8-byte preamble followed by a standard {@code PRVW} box.
 * Decodes the {@code PRVW} header to extract preview image dimensions and stores
 * them on {@link Cr3Directory}. The JPEG payload is discarded.
 */
class Cr3PreviewUuidHandler
{
    private static final int PREAMBLE_SIZE = 8;
    private static final int BOX_HEADER_SIZE = 8; // size(4) + type(4)

    private final Cr3Directory directory;

    Cr3PreviewUuidHandler(@NotNull Cr3Directory directory)
    {
        this.directory = directory;
    }

    /**
     * Parses the full preview UUID payload, skipping the preamble and PRVW box
     * header to reach the PRVW dimension fields.
     * <p>
     * PRVW payload layout (big-endian, after preamble + box header):
     * <pre>
     *   uint32 unknown/flags
     *   uint16 unknown
     *   uint16 width
     *   uint16 height
     *   ...
     *   byte[] jpeg_data
     * </pre>
     */
    void parsePayload(@NotNull byte[] uuidPayload)
    {
        try {
            SequentialByteArrayReader reader = new SequentialByteArrayReader(uuidPayload);
            reader.skip(PREAMBLE_SIZE + BOX_HEADER_SIZE + 6); // preamble + PRVW box header + uint32 flags + uint16 unknown
            int width  = reader.getUInt16();
            int height = reader.getUInt16();
            directory.setInt(Cr3Directory.TAG_PREVIEW_WIDTH, width);
            directory.setInt(Cr3Directory.TAG_PREVIEW_HEIGHT, height);
        } catch (IOException e) {
            directory.addError("Error reading PRVW dimensions: " + e.getMessage());
        }
    }
}

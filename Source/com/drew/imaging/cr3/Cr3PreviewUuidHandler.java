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
import com.drew.imaging.isobmff.IsoBoxIterator;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.cr3.Cr3BoxTypes;
import com.drew.metadata.cr3.Cr3Directory;

import java.io.IOException;

/**
 * Reads the Canon preview UUID ({@code eaf42b5e-1c98-4b88-b9fb-b7dc406e4d16}): an 8-byte
 * preamble followed by a {@code PRVW} box with the preview's dimensions and JPEG.
 * <p>
 * PRVW payload layout (big-endian):
 * <pre>
 *   uint32 unknown/flags
 *   uint16 unknown
 *   uint16 width
 *   uint16 height
 *   uint16 unknown
 *   uint32 jpeg_size
 *   byte[] jpeg_data
 * </pre>
 */
class Cr3PreviewUuidHandler
{
    private static final int PREAMBLE_SIZE = 8;

    private final Cr3Directory directory;

    Cr3PreviewUuidHandler(@NotNull Cr3Directory directory)
    {
        this.directory = directory;
    }

    /** Reads the preview UUID that is the current box of {@code boxes}. */
    void process(@NotNull IsoBoxIterator boxes) throws IOException
    {
        boxes.payload().skip(PREAMBLE_SIZE);
        IsoBoxIterator children = boxes.children();
        for (IsoBox box = children.next(); box != null; box = children.next()) {
            if (Cr3BoxTypes.BOX_PREVIEW.equals(box.type)) {
                Cr3JpegRecord.read(box, children.payload(), 6, 12, directory,
                    Cr3Directory.TAG_PREVIEW_WIDTH, Cr3Directory.TAG_PREVIEW_HEIGHT,
                    Cr3Directory.TAG_PREVIEW_OFFSET, Cr3Directory.TAG_PREVIEW_LENGTH);
                return;
            }
        }
    }
}

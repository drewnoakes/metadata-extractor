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
package com.drew.imaging.isobmff;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Walks an ISO Base Media File Format (ISOBMFF) box sequence, dispatching each box
 * to an {@link IsoBoxVisitor}.
 * <p>
 * Handles standard 32-bit sizes, 64-bit large sizes ({@code size == 1}), EOF-terminated
 * boxes ({@code size == 0}), and {@code uuid} boxes whose 16-byte usertype is parsed into
 * {@link IsoBox#usertype} before any payload is read.
 */
public final class IsoBoxWalker
{
    private IsoBoxWalker() {}

    /**
     * Starts a walk over the boxes in {@code inputStream} using the given visitor.
     * The stream is read with Motorola (big-endian) byte order, which is mandatory for ISOBMFF.
     */
    public static void walk(@NotNull InputStream inputStream, @NotNull IsoBoxVisitor visitor)
    {
        com.drew.lang.StreamReader reader = new com.drew.lang.StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);
        walk(reader, -1L, visitor);
    }

    /**
     * Recursively walks boxes from the current position in {@code reader} until
     * {@code containerEnd} bytes have been consumed (or EOF when {@code containerEnd == -1}).
     */
    static void walk(@NotNull SequentialReader reader, long containerEnd, @NotNull IsoBoxVisitor visitor)
    {
        try {
            while (containerEnd == -1 || reader.getPosition() < containerEnd) {
                IsoBox box;
                try {
                    box = new IsoBox(reader);
                } catch (IOException e) {
                    // Clean EOF when reading the next box header: normal for open-ended containers.
                    if (containerEnd == -1) break;
                    visitor.addError(e.getMessage());
                    break;
                }

                if (box.payloadSize < 0 && box.size != -1) {
                    visitor.addError("Box payload size is negative for type: " + box.type);
                    break;
                }

                long payloadEnd = (box.payloadSize == -1) ? -1L : reader.getPosition() + box.payloadSize;

                if (visitor.shouldRecurse(box)) {
                    walk(reader, payloadEnd, visitor.processContainer(box));
                    // After recursion, ensure the reader is positioned at the end of this box.
                    // The inner walk may have stopped early (e.g. on an error), which would leave
                    // the reader in the middle of the container and corrupt all subsequent sibling
                    // box reads (e.g. the top-level XMP UUID after moov would be missed).
                    if (payloadEnd != -1) {
                        long remaining = payloadEnd - reader.getPosition();
                        if (remaining > 0)
                            reader.skip(remaining);
                    }
                } else if (visitor.shouldVisit(box)) {
                    if (box.payloadSize == -1) {
                        visitor.addError("Cannot visit EOF-terminated box: " + box.type);
                        break;
                    } else if (box.payloadSize > Integer.MAX_VALUE) {
                        visitor.addError("Box payload too large to read into memory: " + box.type);
                        reader.skip(box.payloadSize);
                    } else {
                        byte[] payload = reader.getBytes((int) box.payloadSize);
                        try {
                            visitor.visit(box, payload);
                        } catch (IOException e) {
                            // A single bad box must not abort the entire walk.
                            visitor.addError("Error visiting box '" + box.type + "': " + e.getMessage());
                        }
                    }
                } else {
                    if (box.payloadSize > 0) {
                        reader.skip(box.payloadSize);
                    } else if (box.payloadSize == -1) {
                        break; // EOF-terminated box, nothing more to do
                    }
                }
            }
        } catch (IOException e) {
            visitor.addError(e.getMessage());
        }
    }
}

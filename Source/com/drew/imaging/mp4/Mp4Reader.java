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
package com.drew.imaging.mp4;

import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.mp4.Mp4Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Payton Garland
 */
public class Mp4Reader
{
    /**
     * The maximum number of levels of nested containers to process. Real files nest only a few
     * levels deep (such as moov/trak/mdia/minf/stbl), so this limit is only reached by malformed
     * files, which would otherwise exhaust the stack.
     */
    private static final int MAX_BOX_DEPTH = 100;

    private Mp4Reader() {}

    public static void extract(@NotNull InputStream inputStream, @NotNull Mp4Handler<?> handler)
    {
        StreamReader reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);

        Mp4Context context = new Mp4Context();

        processBoxes(reader, -1, 0, handler, context);
    }

    private static void processBoxes(StreamReader reader, long atomEnd, int depth, Mp4Handler<?> handler, Mp4Context context)
    {
        try {
            if (depth > MAX_BOX_DEPTH) {
                // Skip this box's contents rather than recurring into them. Only the top level
                // call passes -1 for atomEnd, and it passes zero for depth, so atomEnd is known here.
                handler.addError("Box nesting too deep.");
                reader.skip(atomEnd - reader.getPosition());
                return;
            }

            while (atomEnd == -1 || reader.getPosition() < atomEnd) {

                long boxSize = reader.getUInt32();

                String boxType = reader.getString(4);

                boolean isLargeSize = boxSize == 1;

                if (isLargeSize) {
                    boxSize = reader.getInt64();
                }

                int headerSize = isLargeSize ? 16 : 8;

                if (boxSize > Integer.MAX_VALUE) {
                    handler.addError("Box size too large.");
                    break;
                }

                if (boxSize < headerSize) {
                    handler.addError("Box size too small.");
                    break;
                }

                // Determine if fourCC is container/atom and process accordingly.
                // Unknown atoms will be skipped

                if (handler.shouldAcceptContainer(boxType)) {
                    // Recur, to process nested boxes within container box
                    processBoxes(reader, boxSize + reader.getPosition() - headerSize, depth + 1, handler.processContainer(boxType, boxSize, context), context);
                } else if (handler.shouldAcceptBox(boxType)) {
                    handler = handler.processBox(boxType, reader.getBytes((int)(boxSize - headerSize)), boxSize, context);
                } else {
                    reader.skip(boxSize - headerSize);
                }
            }
        } catch (IOException e) {
            handler.addError(e.getMessage());
        }
    }
}

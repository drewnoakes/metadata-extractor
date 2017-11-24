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
package com.drew.imaging.png;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.io.IOException;
import java.util.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PngChunkReader
{
    private static final byte[] PNG_SIGNATURE_BYTES = {(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

    public Iterable<PngChunk> extract(@NotNull final SequentialReader reader, @Nullable final Set<PngChunkType> desiredChunkTypes) throws PngProcessingException, IOException
    {
        //
        // PNG DATA STREAM
        //
        // Starts with a PNG SIGNATURE, followed by a sequence of CHUNKS.
        //
        // PNG SIGNATURE
        //
        //   Always composed of these bytes: 89 50 4E 47 0D 0A 1A 0A
        //
        // CHUNK
        //
        //   4 - length of the data field (unsigned, but always within 31 bytes), may be zero
        //   4 - chunk type, restricted to [65,90] and [97,122] (A-Za-z)
        //   * - data field
        //   4 - CRC calculated from chunk type and chunk data, but not length
        //
        // CHUNK TYPES
        //
        //   Critical Chunk Types:
        //
        //     IHDR - image header, always the first chunk in the data stream
        //     PLTE - palette table, associated with indexed PNG images
        //     IDAT - image data chunk, of which there may be many
        //     IEND - image trailer, always the last chunk in the data stream
        //
        //   Ancillary Chunk Types:
        //
        //     Transparency information:  tRNS
        //     Colour space information:  cHRM, gAMA, iCCP, sBIT, sRGB
        //     Textual information:       iTXt, tEXt, zTXt
        //     Miscellaneous information: bKGD, hIST, pHYs, sPLT
        //     Time information:          tIME
        //

        reader.setMotorolaByteOrder(true); // network byte order

        if (!Arrays.equals(PNG_SIGNATURE_BYTES, reader.getBytes(PNG_SIGNATURE_BYTES.length))) {
            throw new PngProcessingException("PNG signature mismatch");
        }

        boolean seenImageHeader = false;
        boolean seenImageTrailer = false;

        List<PngChunk> chunks = new ArrayList<PngChunk>();
        Set<PngChunkType> seenChunkTypes = new HashSet<PngChunkType>();

        while (!seenImageTrailer) {
            // Process the next chunk.
            int chunkDataLength = reader.getInt32();

            if (chunkDataLength < 0)
                throw new PngProcessingException("PNG chunk length exceeds maximum");

            PngChunkType chunkType = new PngChunkType(reader.getBytes(4));

            boolean willStoreChunk = desiredChunkTypes == null || desiredChunkTypes.contains(chunkType);

            byte[] chunkData = reader.getBytes(chunkDataLength);

            // Skip the CRC bytes at the end of the chunk
            // TODO consider verifying the CRC value to determine if we're processing bad data
            reader.skip(4);

            if (willStoreChunk && seenChunkTypes.contains(chunkType) && !chunkType.areMultipleAllowed()) {
                throw new PngProcessingException(String.format("Observed multiple instances of PNG chunk '%s', for which multiples are not allowed", chunkType));
            }

            if (chunkType.equals(PngChunkType.IHDR)) {
                seenImageHeader = true;
            } else if (!seenImageHeader) {
                throw new PngProcessingException(String.format("First chunk should be '%s', but '%s' was observed", PngChunkType.IHDR, chunkType));
            }

            if (chunkType.equals(PngChunkType.IEND)) {
                seenImageTrailer = true;
            }

            if (willStoreChunk) {
                chunks.add(new PngChunk(chunkType, chunkData));
            }

            seenChunkTypes.add(chunkType);
        }

        return chunks;
    }
}

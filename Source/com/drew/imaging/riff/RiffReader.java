/*
 * Copyright 2002-2015 Drew Noakes
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
package com.drew.imaging.riff;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;

/**
 * Processes RIFF-formatted data, calling into client code via that {@link RiffHandler} interface.
 * <p></p>
 * For information on this file format, see:
 * <ul>
 *     <li>http://en.wikipedia.org/wiki/Resource_Interchange_File_Format</li>
 *     <li>https://developers.google.com/speed/webp/docs/riff_container</li>
 *     <li>https://www.daubnet.com/en/file-format-riff</li>
 * </ul>
 * @author Drew Noakes https://drewnoakes.com
 */
public class RiffReader
{
    /**
     * Processes a RIFF data sequence.
     *
     * @param reader the {@link SequentialReader} from which the data should be read
     * @param handler the {@link RiffHandler} that will coordinate processing and accept read values
     * @throws RiffProcessingException if an error occurred during the processing of RIFF data that could not be
     *                                 ignored or recovered from
     * @throws IOException an error occurred while accessing the required data
     */
    public void processRiff(@NotNull final SequentialReader reader,
                            @NotNull final RiffHandler handler) throws RiffProcessingException, IOException
    {
        // RIFF files are always little-endian
        reader.setMotorolaByteOrder(false);

        // PROCESS FILE HEADER

        final String fileFourCC = reader.getString(4);

        if (!fileFourCC.equals("RIFF"))
            throw new RiffProcessingException("Invalid RIFF header: " + fileFourCC);

        // The total size of the chunks that follow plus 4 bytes for the 'WEBP' FourCC
        final int fileSize = reader.getInt32();
        int sizeLeft = fileSize;

        final String identifier = reader.getString(4);
        sizeLeft -= 4;

        if (!handler.shouldAcceptRiffIdentifier(identifier))
            return;

        // PROCESS CHUNKS

        while (sizeLeft != 0) {
            final String chunkFourCC = reader.getString(4);
            final int chunkSize = reader.getInt32();
            sizeLeft -= 8;

            // NOTE we fail a negative chunk size here (greater than 0x7FFFFFFF) as Java cannot
            // allocate arrays larger than this.
            if (chunkSize < 0 || sizeLeft < chunkSize)
                throw new RiffProcessingException("Invalid RIFF chunk size");

            if (handler.shouldAcceptChunk(chunkFourCC)) {
                // TODO is it feasible to avoid copying the chunk here, and to pass the sequential reader to the handler?
                handler.processChunk(chunkFourCC, reader.getBytes(chunkSize));
            } else {
                reader.skip(chunkSize);
            }

            sizeLeft -= chunkSize;

            // Skip any padding byte added to keep chunks aligned to even numbers of bytes
            if (chunkSize % 2 == 1) {
                reader.getInt8();
                sizeLeft--;
            }
        }
    }
}

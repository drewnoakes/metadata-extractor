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
package com.drew.imaging.heif;

import com.drew.lang.SequentialReader;
import com.drew.lang.StreamReader;
import com.drew.metadata.heif.HeifBoxTypes;
import com.drew.metadata.heif.HeifContainerTypes;
import com.drew.metadata.heif.HeifDirectory;
import com.drew.metadata.heif.boxes.Box;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HeifReader {
    private static final Set<String> ACCEPTABLE_PRE_META_BOX_TYPES =
        new HashSet<String>(Arrays.asList(HeifBoxTypes.BOX_FILE_TYPE, HeifContainerTypes.BOX_METADATA));

    public void extract(InputStream inputStream, HeifHandler<?> handler) {
        // We need to read through the input stream to find the meta box which will tell us what handler to use

        // The meta box is not necessarily the first box, so we need to mark the input stream (if we can)
        // so we can re-read the stream with the proper handler if necessary

        try {
            boolean markSupported = false;
            if (inputStream.markSupported()) {
                markSupported = true;
                inputStream.mark(inputStream.available() + 1); // +1 since we're going to read past the end of the stream by 1 byte
            }

            StreamReader reader = new StreamReader(inputStream);
            reader.setMotorolaByteOrder(true);

            processTopLevelBoxes(inputStream, reader, -1, handler, markSupported);
        } catch (IOException e) {
            // Any errors should have been added to the directory
        }
    }

    private void processTopLevelBoxes(InputStream inputStream,
                                      SequentialReader reader,
                                      long atomEnd,
                                      HeifHandler<?> handler,
                                      boolean markSupported) throws IOException {
        boolean foundMetaBox = false;
        boolean needToReset = false;
        try {
            while (atomEnd == -1 || reader.getPosition() < atomEnd) {

                Box box = new Box(reader);

                if (!foundMetaBox && !ACCEPTABLE_PRE_META_BOX_TYPES.contains(box.type)) {
                    // If we hit a box that needs a more specific handler (like mdat) without yet hitting the meta box,
                    // we'll need to reset the stream and use the correct handler once we find it
                    needToReset = true;
                }

                if (HeifContainerTypes.BOX_METADATA.equalsIgnoreCase(box.type)) {
                    foundMetaBox = true;
                }

                handler = processBox(reader, box, handler);
            }
        } catch (IOException e) {
            // Currently, reader relies on IOException to end
        }

        if (needToReset && markSupported) {
            inputStream.reset();
            reader = new StreamReader(inputStream);
            processBoxes(reader, -1, handler);
        } else if (needToReset) {
            HeifDirectory heifDirectory = handler.metadata.getFirstDirectoryOfType(HeifDirectory.class);
            if (heifDirectory != null) {
                heifDirectory.addError("Unable to extract Exif data because inputStream was not resettable and 'meta' was not first box");
            }
        }
    }

    private HeifHandler<?> processBoxes(SequentialReader reader, long atomEnd, HeifHandler<?> handler) {
        try {
            while (atomEnd == -1 || reader.getPosition() < atomEnd) {

                Box box = new Box(reader);

                handler = processBox(reader, box, handler);
            }
        } catch (IOException e) {
            // Currently, reader relies on IOException to end
        }
        return handler;
    }

    private HeifHandler<?> processBox(SequentialReader reader, Box box, HeifHandler<?> handler) throws IOException {
        if (handler.shouldAcceptContainer(box)) {
            handler.processContainer(box, reader);
            handler = processBoxes(reader, box.size + reader.getPosition() - 8, handler);
        } else if (handler.shouldAcceptBox(box)) {
            handler = handler.processBox(box, reader.getBytes((int) box.size - 8));
        } else if (box.size > 1) {
            reader.skip(box.size - 8);
        }
        return handler;
    }
}

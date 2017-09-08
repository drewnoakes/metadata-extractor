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
package com.drew.imaging.mp4;

import com.drew.lang.StreamReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.boxes.Box;

import java.io.IOException;
import java.io.InputStream;

public class Mp4Reader
{
    public void extract(Metadata metadata, InputStream inputStream, Mp4Handler handler)
    {
        StreamReader reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);

        processBoxes(reader, -1, handler);
    }

    private void processBoxes(StreamReader reader, long atomEnd, Mp4Handler mp4Handler)
    {
        try {
            while (atomEnd == -1 || reader.getPosition() < atomEnd) {

                Box box = new Box(reader);

                // Determine if fourCC is container/atom and process accordingly.
                // Unknown atoms will be skipped

                if (mp4Handler.shouldAcceptContainer(box)) {
                    processBoxes(reader, box.size + reader.getPosition() - 8, mp4Handler.processContainer(box));
                } else if (mp4Handler.shouldAcceptBox(box)) {
                    mp4Handler = mp4Handler.processBox(box, reader.getBytes((int)box.size - 8));
                } else {
                    if (box.size > 1) {
                        reader.skip(box.size - 8);
                    } else if (box.size == -1) {
                        break;
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }
}

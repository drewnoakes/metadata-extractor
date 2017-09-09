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
package com.drew.imaging.quicktime;

import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mov.atoms.Atom;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Payton Garland
 */
public class QuickTimeReader
{
    private QuickTimeReader() {}

    public static void extract(@NotNull InputStream inputStream, @NotNull QuickTimeHandler handler)
    {
        StreamReader reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);

        processAtoms(reader, -1, handler);
    }

    private static void processAtoms(StreamReader reader, long atomEnd, QuickTimeHandler handler)
    {
        try {
            while (atomEnd == -1 || reader.getPosition() < atomEnd) {

                Atom atom = new Atom(reader);

                // Determine if fourCC is container/atom and process accordingly.
                // Unknown atoms will be skipped

                if (handler.shouldAcceptContainer(atom)) {
                    processAtoms(reader, atom.size + reader.getPosition() - 8, handler.processContainer(atom));
                } else if (handler.shouldAcceptAtom(atom)) {
                    handler = handler.processAtom(atom, reader.getBytes((int)atom.size - 8));
                } else if (atom.size > 1) {
                    reader.skip(atom.size - 8);
                } else if (atom.size == -1) {
                    break;
                }
            }
        } catch (IOException e) {
            handler.addError(e.getMessage());
        }
    }
}

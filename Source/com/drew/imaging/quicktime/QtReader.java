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
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtDirectory;
import com.drew.metadata.mov.atoms.Atom;

import java.io.IOException;
import java.io.InputStream;

public class QtReader
{
    public void extract(Metadata metadata, InputStream inputStream, QtHandler handler)
    {
        QtDirectory directory = new QtDirectory();
        metadata.addDirectory(directory);

        StreamReader reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);

        processAtoms(reader, -1, handler);
    }

    private void processAtoms(StreamReader reader, long atomEnd, QtHandler qtHandler)
    {
        try {
            while (atomEnd == -1 || reader.getPosition() < atomEnd) {

                Atom atom = new Atom(reader);

                // Determine if fourCC is container/atom and process accordingly.
                // Unknown atoms will be skipped

                if (qtHandler.shouldAcceptContainer(atom)) {
                    processAtoms(reader, atom.size + reader.getPosition() - 8, qtHandler.processContainer(atom));
                } else if (qtHandler.shouldAcceptAtom(atom)) {
                    qtHandler = qtHandler.processAtom(atom, reader.getBytes((int)atom.size - 8));
                } else if (atom.size > 1) {
                    reader.skip(atom.size - 8);
                } else if (atom.size == -1) {
                    break;
                }
            }
        } catch (IOException ignored) {
        }
    }
}

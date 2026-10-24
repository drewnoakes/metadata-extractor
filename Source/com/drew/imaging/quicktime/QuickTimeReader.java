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
package com.drew.imaging.quicktime;

import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.mov.QuickTimeContext;
import com.drew.metadata.mov.atoms.Atom;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Payton Garland
 */
public class QuickTimeReader
{
    /**
     * The maximum number of levels of nested containers to process. Real files nest only a few
     * levels deep (such as moov/trak/mdia/minf/stbl), so this limit is only reached by malformed
     * files, which would otherwise exhaust the stack.
     */
    private static final int MAX_ATOM_DEPTH = 100;

    private QuickTimeReader() {}

    public static void extract(@NotNull InputStream inputStream, @NotNull QuickTimeHandler<?> handler)
    {
        StreamReader reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);

        QuickTimeContext context = new QuickTimeContext();

        processAtoms(reader, -1, 0, handler, context);
    }

    private static void processAtoms(StreamReader reader, long atomEnd, int depth, QuickTimeHandler<?> handler, QuickTimeContext context)
    {
        try {
            if (depth > MAX_ATOM_DEPTH) {
                // Skip this atom's contents rather than recurring into them. Only the top level
                // call passes -1 for atomEnd, and it passes zero for depth, so atomEnd is known here.
                handler.addError("Atom nesting too deep.");
                reader.skip(atomEnd - reader.getPosition());
                return;
            }

            while (atomEnd == -1 || reader.getPosition() < atomEnd) {

                Atom atom = new Atom(reader);

                // Determine if fourCC is container/atom and process accordingly.
                // Unknown atoms will be skipped

                if (atom.size > Integer.MAX_VALUE) {
                    handler.addError("Atom size too large.");
                    break;
                }

                if (atom.size < atom.headerSize) {
                    handler.addError("Atom size too small.");
                    break;
                }

                if (handler.shouldAcceptContainer(atom)) {
                    processAtoms(reader, atom.size + reader.getPosition() - atom.headerSize, depth + 1, handler.processContainer(atom, context), context);
                } else if (handler.shouldAcceptAtom(atom)) {
                    handler = handler.processAtom(atom, reader.getBytes((int)(atom.size - atom.headerSize)), context);
                } else if (atom.size > atom.headerSize) {
                    reader.skip(atom.size - atom.headerSize);
                } else if (atom.size == -1) {
                    break;
                }
            }
        } catch (IOException e) {
            handler.addError(e.getMessage());
        }
    }
}

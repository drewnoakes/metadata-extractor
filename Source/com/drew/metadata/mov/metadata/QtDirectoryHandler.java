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
package com.drew.metadata.mov.metadata;

import com.drew.imaging.quicktime.QtHandler;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtAtomTypes;
import com.drew.metadata.mov.QtContainerTypes;
import com.drew.metadata.mov.QtMetadataHandler;
import com.drew.metadata.mov.atoms.Atom;

import java.io.IOException;

public class QtDirectoryHandler extends QtMetadataHandler
{
    private String currentData;

    public QtDirectoryHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected boolean shouldAcceptAtom(Atom atom)
    {
        return atom.type.equals(QtAtomTypes.ATOM_DATA);
    }

    @Override
    protected boolean shouldAcceptContainer(Atom atom)
    {
        return QtMetadataDirectory._tagIntegerMap.containsKey(atom)
            || atom.type.equals(QtContainerTypes.ATOM_METADATA_LIST);
    }

    @Override
    protected QtHandler processAtom(@NotNull Atom atom, @NotNull byte[] payload) throws IOException
    {
        if (payload != null) {
            SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
            if (atom.type.equals(QtAtomTypes.ATOM_DATA) && currentData != null) {
                processData(payload, reader);
            } else {
                currentData = new String(reader.getBytes(4));
            }
        } else {
            if (QtMetadataDirectory._tagIntegerMap.containsKey(atom)) {
                currentData = atom.type;
            } else {
                currentData = null;
            }
        }
        return this;
    }

    @Override
    protected void processData(@NotNull byte[] payload, @NotNull SequentialByteArrayReader reader) throws IOException
    {
        int typeIndicator = reader.getInt32();
        int localeIndicator = reader.getInt32();
        String value = new String(reader.getBytes(payload.length - 8));
        directory.setString(QtMetadataDirectory._tagIntegerMap.get(currentData), value);
    }

    @Override
    protected void processKeys(@NotNull SequentialByteArrayReader reader) throws IOException
    {
        // Do nothing
    }
}

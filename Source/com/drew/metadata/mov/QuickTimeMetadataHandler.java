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
package com.drew.metadata.mov;

import com.drew.imaging.quicktime.QuickTimeHandler;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.atoms.Atom;
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory;

import java.io.IOException;

/**
 * @author Payton Garland
 */
public abstract class QuickTimeMetadataHandler extends QuickTimeHandler
{
    public QuickTimeMetadataHandler(Metadata metadata)
    {
        super(metadata);
    }

    @NotNull
    @Override
    protected QuickTimeDirectory getDirectory()
    {
        return new QuickTimeMetadataDirectory();
    }

    @Override
    protected boolean shouldAcceptAtom(@NotNull Atom atom)
    {
        return atom.type.equals(QuickTimeAtomTypes.ATOM_HANDLER)
            || atom.type.equals(QuickTimeAtomTypes.ATOM_KEYS)
            || atom.type.equals(QuickTimeAtomTypes.ATOM_DATA);
    }

    @Override
    protected boolean shouldAcceptContainer(@NotNull Atom atom)
    {
        return atom.type.equals(QuickTimeContainerTypes.ATOM_METADATA_LIST);
    }

    @Override
    protected QuickTimeHandler processAtom(@NotNull Atom atom, @Nullable byte[] payload) throws IOException
    {
        if (payload != null) {
            SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
            if (atom.type.equals(QuickTimeAtomTypes.ATOM_KEYS)) {
                processKeys(reader);
            } else if (atom.type.equals(QuickTimeAtomTypes.ATOM_DATA)) {
                processData(payload, reader);
            }
        }
        return this;
    }

    protected abstract void processKeys(@NotNull SequentialByteArrayReader reader) throws IOException;

    protected abstract void processData(@NotNull byte[] payload, @NotNull SequentialByteArrayReader reader) throws IOException;
}

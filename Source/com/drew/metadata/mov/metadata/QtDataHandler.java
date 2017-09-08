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
import com.drew.lang.ByteUtil;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtAtomTypes;
import com.drew.metadata.mov.QtContainerTypes;
import com.drew.metadata.mov.QtMetadataHandler;
import com.drew.metadata.mov.atoms.Atom;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class QtDataHandler extends QtMetadataHandler
{
    private int currentIndex = 0;
    private ArrayList<String> keys = new ArrayList<String>();

    public QtDataHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected boolean shouldAcceptAtom(@NotNull Atom atom)
    {
        return atom.type.equals(QtAtomTypes.ATOM_HANDLER)
            || atom.type.equals(QtAtomTypes.ATOM_KEYS)
            || atom.type.equals(QtAtomTypes.ATOM_DATA);
    }

    @Override
    protected boolean shouldAcceptContainer(@NotNull Atom atom)
    {
        return atom.type.equals(QtContainerTypes.ATOM_METADATA_LIST)
            || ByteUtil.getInt32(atom.type.getBytes(), 0, true) <= keys.size();
    }

    @Override
    protected QtHandler processAtom(@NotNull Atom atom, @Nullable byte[] payload) throws IOException
    {
        if (payload != null) {
            SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
            if (atom.type.equals(QtAtomTypes.ATOM_KEYS)) {
                processKeys(reader);
            } else if (atom.type.equals(QtAtomTypes.ATOM_DATA)) {
                processData(payload, reader);
            }
        } else {
            int numValue = ByteUtil.getInt32(atom.type.getBytes(), 0, true);
            if (numValue > 0 && numValue < keys.size() + 1) {
                currentIndex = numValue - 1;
            }
        }
        return this;
    }

    @Override
    protected void processKeys(@NotNull SequentialByteArrayReader reader) throws IOException
    {
        // Version 1-byte and Flags 3-bytes
        reader.skip(4);
        int entryCount = reader.getInt32();
        for (int i = 0; i < entryCount; i++) {
            int keySize = reader.getInt32();
            String keyNamespace = new String(reader.getBytes(4));
            String keyValue = new String(reader.getBytes(keySize - 8));
            keys.add(keyValue);
        }
    }

    @Override
    protected void processData(@NotNull byte[] payload, @NotNull SequentialByteArrayReader reader) throws IOException
    {
        int typeIndicator = reader.getInt32();
        int localeIndicator = reader.getInt32();
        String value = new String(reader.getBytes(payload.length - 8));
        directory.setString(QtMetadataDirectory._tagIntegerMap.get(keys.get(currentIndex)), value);
    }
}

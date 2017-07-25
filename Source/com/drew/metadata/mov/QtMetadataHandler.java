package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.util.ArrayList;

public abstract class QtMetadataHandler extends QtHandler
{
    private int currentIndex = 0;
    private ArrayList<String> keys = new ArrayList<String>();

    public QtMetadataHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    public QtDirectory getDirectory()
    {
        return new QtMetadataDirectory();
    }

    @Override
    public boolean shouldAcceptAtom(String fourCC)
    {
        return fourCC.equals(QtAtomTypes.ATOM_HANDLER)
            || fourCC.equals(QtAtomTypes.ATOM_KEYS)
            || fourCC.equals(QtAtomTypes.ATOM_DATA);
    }

    @Override
    public boolean shouldAcceptContainer(String fourCC)
    {
        return fourCC.equals(QtContainerTypes.ATOM_METADATA_LIST)
            || ByteUtil.getInt32(fourCC.getBytes(), 0, true) < keys.size();
    }

    @Override
    public QtHandler processAtom(@NotNull String fourCC, @NotNull byte[] payload) throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_KEYS)) {
            processKeys(reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_DATA)){
            processData(payload, reader);
        }
        return this;
    }

    abstract void processKeys(@NotNull SequentialByteArrayReader reader) throws IOException;

    abstract void processData(@NotNull byte[] payload, @NotNull SequentialByteArrayReader reader) throws IOException;
}

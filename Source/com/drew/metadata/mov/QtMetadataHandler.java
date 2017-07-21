package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public abstract class QtMetadataHandler implements QtHandler
{
    private int currentIndex = 0;
    private ArrayList<String> keys = new ArrayList<String>();

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
    public QtHandler processAtom(@NotNull String fourCC, @NotNull byte[] payload, @NotNull QtDirectory directory) throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_KEYS)) {
            processKeys(reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_DATA)){
            processData(directory, payload, reader);
        }
        return this;
    }

    abstract void processKeys(@NotNull SequentialByteArrayReader reader) throws IOException;

    abstract void processData(@NotNull QtDirectory directory, @NotNull byte[] payload, @NotNull SequentialByteArrayReader reader) throws IOException;
}

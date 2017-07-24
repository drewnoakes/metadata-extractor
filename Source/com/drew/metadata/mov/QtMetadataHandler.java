package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
import com.drew.lang.SequentialByteArrayReader;

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
    public QtHandler processAtom(String fourCC, byte[] payload, QtDirectory directory) throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_KEYS)) {
            processKeys(reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_DATA)){
            processData(payload, directory, reader);
        }
        return this;
    }

    abstract void processKeys(SequentialByteArrayReader reader) throws IOException;

    abstract void processData(byte[] payload, QtDirectory directory, SequentialByteArrayReader reader) throws IOException;
}

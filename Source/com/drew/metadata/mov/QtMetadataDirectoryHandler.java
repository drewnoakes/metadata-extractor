package com.drew.metadata.mov;

import com.drew.lang.SequentialByteArrayReader;

import java.io.IOException;

public class QtMetadataDirectoryHandler implements QtHandler
{
    private String currentData;

    @Override
    public boolean shouldAcceptAtom(String fourCC)
    {
        return fourCC.equals(QtAtomTypes.ATOM_DATA);
    }

    @Override
    public boolean shouldAcceptContainer(String fourCC)
    {
        System.out.println(fourCC);
        return QtDirectory._tagIntegerMap.containsKey(fourCC)
            || fourCC.equals(QtContainerTypes.ATOM_METADATA_LIST);
    }

    @Override
    public QtHandler processAtom(String fourCC, byte[] payload, QtDirectory directory) throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_DATA) && currentData != null){
            int typeIndicator = reader.getInt32();
            int localeIndicator = reader.getInt32();
            String value = new String(reader.getBytes(payload.length - 8));
            directory.setString(QtDirectory._tagIntegerMap.get(currentData), value);
        } else {
            currentData = new String(reader.getBytes(4));
        }
        return this;
    }

    @Override
    public QtHandler processContainer(String fourCC)
    {
        if (QtDirectory._tagIntegerMap.containsKey(fourCC)) {
            currentData = fourCC;
        } else {
            currentData = null;
        }
        return this;
    }
}

package com.drew.metadata.mov;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;

public class QtMetadataDirectoryHandler extends QtMetadataHandler
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
        return QtDirectory._tagIntegerMap.containsKey(fourCC)
            || fourCC.equals(QtContainerTypes.ATOM_METADATA_LIST);
    }

    @Override
    public QtHandler processAtom(@NotNull String fourCC, @NotNull byte[] payload, @NotNull QtDirectory directory) throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_DATA) && currentData != null){
            processData(directory, payload, reader);
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

    @Override
    public void processData(@NotNull QtDirectory directory, @NotNull byte[] payload, @NotNull SequentialByteArrayReader reader) throws IOException
    {
        int typeIndicator = reader.getInt32();
        int localeIndicator = reader.getInt32();
        String value = new String(reader.getBytes(payload.length - 8));
        directory.setString(QtDirectory._tagIntegerMap.get(currentData), value);
    }

    @Override
    void processKeys(@NotNull SequentialByteArrayReader reader) throws IOException
    {
        // Do nothing
    }
}

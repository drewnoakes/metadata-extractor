package com.drew.metadata.mov.metadata;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtAtomTypes;
import com.drew.metadata.mov.QtContainerTypes;
import com.drew.metadata.mov.QtHandler;
import com.drew.metadata.mov.QtMetadataHandler;

import java.io.IOException;

public class QtDirectoryhandler extends QtMetadataHandler
{
    private String currentData;

    public QtDirectoryhandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected boolean shouldAcceptAtom(String fourCC)
    {
        return fourCC.equals(QtAtomTypes.ATOM_DATA);
    }

    @Override
    protected boolean shouldAcceptContainer(String fourCC)
    {
        return QtMetadataDirectory._tagIntegerMap.containsKey(fourCC)
            || fourCC.equals(QtContainerTypes.ATOM_METADATA_LIST);
    }

    @Override
    protected QtHandler processAtom(@NotNull String fourCC, @NotNull byte[] payload) throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_DATA) && currentData != null){
            processData(payload, reader);
        } else {
            currentData = new String(reader.getBytes(4));
        }
        return this;
    }

    @Override
    protected QtHandler processContainer(String fourCC)
    {
        if (QtMetadataDirectory._tagIntegerMap.containsKey(fourCC)) {
            currentData = fourCC;
        } else {
            currentData = null;
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

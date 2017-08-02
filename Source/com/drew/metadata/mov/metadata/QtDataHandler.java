package com.drew.metadata.mov.metadata;

import com.drew.metadata.mov.QtHandler;
import com.drew.lang.ByteUtil;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtAtomTypes;
import com.drew.metadata.mov.QtContainerTypes;
import com.drew.metadata.mov.QtMetadataHandler;

import java.io.IOException;
import java.util.ArrayList;

public class QtDataHandler extends QtMetadataHandler
{
    private int currentIndex = 0;
    private ArrayList<String> keys = new ArrayList<String>();

    public QtDataHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected boolean shouldAcceptAtom(String fourCC)
    {
        return fourCC.equals(QtAtomTypes.ATOM_HANDLER)
            || fourCC.equals(QtAtomTypes.ATOM_KEYS)
            || fourCC.equals(QtAtomTypes.ATOM_DATA);
    }

    @Override
    protected boolean shouldAcceptContainer(String fourCC)
    {
        return fourCC.equals(QtContainerTypes.ATOM_METADATA_LIST)
            || ByteUtil.getInt32(fourCC.getBytes(), 0, true) <= keys.size();
    }

    @Override
    protected QtHandler processAtom(@NotNull String fourCC, @NotNull byte[] payload) throws IOException
    {
        if (payload != null) {
            SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
            if (fourCC.equals(QtAtomTypes.ATOM_KEYS)) {
                processKeys(reader);
            } else if (fourCC.equals(QtAtomTypes.ATOM_DATA)) {
                processData(payload, reader);
            }
        } else {
            int numValue = ByteUtil.getInt32(fourCC.getBytes(), 0, true);
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

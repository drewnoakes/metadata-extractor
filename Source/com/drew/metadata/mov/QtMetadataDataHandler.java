package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class QtMetadataDataHandler extends QtMetadataHandler
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

    @Override
    public QtHandler processContainer(String fourCC)
    {
        int numValue = ByteUtil.getInt32(fourCC.getBytes(), 0, true);
        if (numValue > 0 && numValue < keys.size() + 1) {
            currentIndex = numValue - 1;
        }
        return this;
    }

    @Override
    public void processKeys(@NotNull SequentialByteArrayReader reader) throws IOException
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
    public void processData(@NotNull QtDirectory directory, @NotNull byte[] payload, @NotNull SequentialByteArrayReader reader) throws IOException
    {
        int typeIndicator = reader.getInt32();
        int localeIndicator = reader.getInt32();
        String value = new String(reader.getBytes(payload.length - 8));
        directory.setString(QtDirectory._tagIntegerMap.get(keys.get(currentIndex)), value);
    }
}

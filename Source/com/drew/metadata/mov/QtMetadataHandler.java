package com.drew.metadata.mov;

import com.drew.lang.SequentialByteArrayReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class QtMetadataHandler implements QtHandler
{
    private int currentIndex = 0;
    private String handler;
    private ArrayList<String> keys = new ArrayList<String>();
    private String[] values = null;

    //TODO: Find better way to handle list (without handling atom reading internally)

    @Override
    public boolean shouldAcceptAtom(String fourCC)
    {
        return fourCC.equals(QtAtomTypes.ATOM_HANDLER)
            || fourCC.equals(QtAtomTypes.ATOM_KEYS)
            || fourCC.equals(QtAtomTypes.ATOM_DATA)
            || fourCC.equals(QtAtomTypes.ATOM_METADATA_LIST); // Metadata list is not an atom, it's a container
    }

    @Override
    public boolean shouldAcceptContainer(String fourCC)
    {
        return false;
    }

    @Override
    public QtHandler processAtom(String fourCC, byte[] payload, QtDirectory directory) throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_HANDLER)) {
            int versionAndFlags = reader.getInt32();
            int predefined = reader.getInt32();
            handler = new String(reader.getBytes(4));
            reader.skip(12); // Reserved
            int nameSize = reader.getInt16();
            String name = reader.getString(nameSize);
        } else if (fourCC.equals(QtAtomTypes.ATOM_KEYS)) {
            // Version 1-byte and Flags 3-bytes
            reader.skip(4);
            int entryCount = reader.getInt32();
            for (int i = 0; i < entryCount; i++) {
                int keySize = reader.getInt32();
                String keyNamespace = new String(reader.getBytes(4));
                String keyValue = new String(reader.getBytes(keySize - 8));
                keys.add(keyValue);
            }
            values = new String[keys.size()];
            System.out.println(Arrays.toString(keys.toArray()));
            System.out.println(handler);
        } else if (fourCC.equals(QtAtomTypes.ATOM_DATA)){

        } else if (fourCC.equals(QtAtomTypes.ATOM_METADATA_LIST)) {
            processMetadataItem(payload, reader);
        }
        return this;
    }

    private void processMetadataItem(byte[] payload, SequentialByteArrayReader reader) throws IOException {
        System.out.println(new String(payload));
        for (String key : keys) {
            long keySize = reader.getInt32();
            if (keySize == 1) {
                keySize = reader.getInt64();
            }
            int keyIndex = reader.getInt32();
            long pos = 0;
            while (pos < keySize) {
                int size = reader.getInt32();
                String fourCC = new String(reader.getBytes(4));
                if (fourCC.equals(QtAtomTypes.ATOM_DATA)) {
                    processData(reader.getBytes(size), reader);
                } else {
                    reader.skip(size);
                }
                pos += size + 4;
            }
        }
    }

    private void processData(byte[] payload, SequentialByteArrayReader reader) throws IOException {
        int typeIndicator = reader.getInt32();
        if (((typeIndicator & 0xFF000000) >> 12) == 0) {
            System.out.println("Well-known type: " + (typeIndicator & 0x00FFFFFF));
        }
        int localeIndicator = reader.getInt32();
        String value = new String(reader.getBytes(payload.length - 8));
        System.out.println(keys.get(currentIndex) + ": " + value);
    }

    @Override
    public QtHandler processContainer(String fourCC)
    {
        return this;
    }
}

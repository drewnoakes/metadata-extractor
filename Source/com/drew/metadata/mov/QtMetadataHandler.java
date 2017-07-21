package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
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
        return fourCC.equals(QtAtomTypes.ATOM_HANDLER);
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
            if (handler.equals("mdir")) {
                return new QtMetadataDirectoryHandler();
            } else if (handler.equals("mdta")) {
                return new QtMetadataDataHandler();
            }
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
}

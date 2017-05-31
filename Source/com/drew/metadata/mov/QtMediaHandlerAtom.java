package com.drew.metadata.mov;

import java.io.IOException;

public class QtMediaHandlerAtom extends QtAtom implements QtLeafAtom {
    private String handlerType;

    public QtMediaHandlerAtom(long size, String type, long offset)
    {
        super(size, type, offset);
    }

    public void getMetadata(QtDataSource source) throws IOException
    {
        byte[] buffer = new byte[4];

        source.reset();
        source.skip(offset + 16);

        source.read(buffer);
        handlerType = new String(buffer);
    }

    public void populateMetadata(FileInfo fileId)
    {
        // do nothing
    }

    public String getHandlerType()
    {
        return handlerType;
    }

    public String toString()
    {
        return new String(type + " (" + size + " bytes)(offset:" + offset + ")\n Handler Type: " + handlerType);
    }
}

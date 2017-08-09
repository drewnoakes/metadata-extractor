package com.drew.metadata.indd.structs;

import com.drew.lang.SequentialReader;

import java.io.IOException;

/**
 * @author Payton Garland
 */
public class ContiguousObjectStreamHeaderOrTrailer
{
    char[] fGUID;
    long fObjectUID;
    long fObjectClassID;
    long fStreamLength;
    long fChecksum;

    public ContiguousObjectStreamHeaderOrTrailer(SequentialReader reader) throws IOException
    {
        fGUID = reader.getString(16).toCharArray();

        boolean motorolaByteOrder = reader.isMotorolaByteOrder() == true ? true : false;

        reader.setMotorolaByteOrder(false);
        fObjectUID = reader.getUInt32();
        fObjectClassID = reader.getUInt32();
        fStreamLength = reader.getUInt32();
        fChecksum = reader.getUInt32();

        reader.setMotorolaByteOrder(motorolaByteOrder);
    }

    public char[] getfGUID()
    {
        return fGUID;
    }

    public long getfObjectUID()
    {
        return fObjectUID;
    }

    public long getfObjectClassID()
    {
        return fObjectClassID;
    }

    public long getfStreamLength()
    {
        return fStreamLength;
    }

    public long getfChecksum()
    {
        return fChecksum;
    }
}

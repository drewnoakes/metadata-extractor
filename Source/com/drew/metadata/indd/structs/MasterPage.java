package com.drew.metadata.indd.structs;

import com.drew.lang.SequentialReader;
import com.drew.metadata.indd.InddDirectory;

import java.io.IOException;

/**
 * @author Payton Garland
 */
public class MasterPage
{
    byte[] fGUID;
    String fMagicBytes;
    char fObjectStreamEndian;
    long fSequenceNumber;
    long fFilePages;

    public MasterPage(SequentialReader reader) throws IOException
    {
        fGUID = reader.getBytes(16);
        fMagicBytes = reader.getString(8);
        fObjectStreamEndian = (char)reader.getUInt8();
        reader.skip(239); // Irrelevant

        reader.setMotorolaByteOrder(false);

        fSequenceNumber = reader.getInt64();
        reader.skip(8); // Irrelevant
        fFilePages = reader.getUInt32();
        reader.skip(3812); // Irrelevant

        if (fObjectStreamEndian == 1) {
            reader.setMotorolaByteOrder(false);
        } else if (fObjectStreamEndian == 2) {
            reader.setMotorolaByteOrder(true);
        }
    }

    public void addMetadata(InddDirectory directory)
    {
        directory.setByteArray(InddDirectory.TAG_DATABASE_ID, fGUID);
        directory.setString(InddDirectory.TAG_DATABASE_TYPE, fMagicBytes);
        directory.setLong(InddDirectory.TAG_SEQUENCE_NUMBER, fSequenceNumber);
        directory.setLong(InddDirectory.TAG_FILE_PAGES, fFilePages);
    }

    public byte[] getfGUID()
    {
        return fGUID;
    }

    public String getfMagicBytes()
    {
        return fMagicBytes;
    }

    public char getfObjectStreamEndian()
    {
        return fObjectStreamEndian;
    }

    public long getfSequenceNumber()
    {
        return fSequenceNumber;
    }

    public long getfFilePages()
    {
        return fFilePages;
    }
}

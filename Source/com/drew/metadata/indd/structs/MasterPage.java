package com.drew.metadata.indd.structs;

import com.drew.imaging.ImageProcessingException;
import com.drew.lang.SequentialReader;
import com.drew.metadata.indd.InddDirectory;

import java.io.IOException;
import java.util.Arrays;

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

    public MasterPage(SequentialReader reader) throws IOException, ImageProcessingException
    {
        fGUID = reader.getBytes(16);

        if (!Arrays.equals(fGUID, new byte[]{0x06, 0x06, (byte)0xED, (byte)0xF5, (byte)0xD8, 0x1D, 0x46, (byte)0xE5, (byte)0xBD, 0x31, (byte)0xEF, (byte)0xE7, (byte)0xFE, 0x74, (byte)0xB7, 0x1D}))
        {
            throw new ImageProcessingException("Not an INDD file");
        }

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

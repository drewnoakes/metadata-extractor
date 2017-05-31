package com.drew.metadata.mov;

import java.io.IOException;

public class QtSampleDescriptionAtom extends QtAtom implements QtLeafAtom {
    protected int version;
    protected String flags;
    protected int numberOfEntries;
    protected int tableSize;
    protected String dataFormat;
    protected int dataReferenceIndex;
    protected int encoderVersion;
    protected int encoderRevision;
    protected String encoderVendor;

    public QtSampleDescriptionAtom(long size, String type, long offset)
    {
        super(size, type, offset);
    }

    public void getMetadata(QtDataSource source) throws IOException
    {
        byte[] buffer = new byte[4];
        byte[] shortBuffer = new byte[2];

        source.reset();
        source.skip(offset + 8);

        version = source.readByte();

        buffer = new byte[3];
        source.read(buffer);
        flags = "Flags 0x" + buffer[0] + buffer[1] + buffer[2];

        buffer = new byte[4];
        source.read(buffer);
        numberOfEntries = ByteUtil.getInt32(buffer, 0, true);

//		for (int i=0; i<numberOfEntries; i++)
//		{

        source.read(buffer);
        tableSize = ByteUtil.getInt32(buffer, 0, true);

        source.read(buffer);
        dataFormat = new String(buffer);

        buffer = new byte[6];
        source.read(buffer);   // skip over reserved field

        source.read(shortBuffer);
        dataReferenceIndex = ByteUtil.getInt16(shortBuffer, 0, true);

        source.read(shortBuffer);
        encoderVersion = ByteUtil.getInt16(shortBuffer, 0, true);

        source.read(shortBuffer);
        encoderRevision = ByteUtil.getInt16(shortBuffer, 0, true);

        buffer = new byte[4];
        source.read(buffer);
        encoderVendor = new String(buffer);

    }

    public void populateMetadata(FileInfo fileId)
    {
        // do nothing - we aren't interested in any of these values
    }

    public String toString()
    {
        return new String(type + " (" + size + " bytes)(offset:" + offset + ") " +
            "\n Version: " + version +
            "\n Flags: " + flags +
            "\n Number of Entries: " + numberOfEntries +
            "\n Table size: " + tableSize +
            "\n Data Format: " + dataFormat +
            "\n Data reference index: " + dataReferenceIndex +
            "\n Encoder version: " + encoderVersion +
            "\n Encoder revision: " + encoderRevision +
            "\n Encoder vendor: " + encoderVendor
        );
    }
}

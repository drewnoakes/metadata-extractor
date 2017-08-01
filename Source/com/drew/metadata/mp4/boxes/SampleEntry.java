package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;

import java.io.IOException;

public class SampleEntry extends FullBox
{
    long numberOfEntries;
    long sampleDescriptionSize;
    String format;
    int dataReferenceIndex;

    public SampleEntry(SequentialReader reader) throws IOException
    {
        super(reader);

        numberOfEntries = reader.getUInt32();
        sampleDescriptionSize = reader.getUInt32();
        format = reader.getString(4);
        reader.skip(6); // Reserved
        dataReferenceIndex = reader.getUInt16();
    }
}

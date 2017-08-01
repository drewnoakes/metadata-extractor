package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;

import java.io.IOException;
import java.util.ArrayList;

public class SampleDescriptionAtom extends FullAtom
{
    long numberOfEntries;

    public SampleDescriptionAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);

        numberOfEntries = reader.getUInt32();
    }

    class SampleDescription
    {
        long sampleDescriptionSize;
        String dataFormat;
        int dataReferenceIndex;

        public SampleDescription(SequentialReader reader) throws IOException
        {
            sampleDescriptionSize = reader.getUInt32();
            dataFormat = reader.getString(4);
            reader.skip(6); // Reserved
            dataReferenceIndex = reader.getUInt16();
        }
    }
}

package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;

import java.io.IOException;
import java.util.ArrayList;

public abstract class SampleDescriptionAtom<T extends SampleDescription> extends FullAtom
{
    long numberOfEntries;
    ArrayList<T> sampleDescriptions;

    public SampleDescriptionAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);

        numberOfEntries = reader.getUInt32();
        sampleDescriptions = new ArrayList<T>();
        for (int i = 0; i < numberOfEntries; i++) {
            sampleDescriptions.add(getSampleDescription(reader));
        }
    }

    abstract T getSampleDescription(SequentialReader reader) throws IOException;
}

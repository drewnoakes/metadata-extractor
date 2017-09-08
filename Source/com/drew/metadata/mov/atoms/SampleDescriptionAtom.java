package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-25691
 */
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

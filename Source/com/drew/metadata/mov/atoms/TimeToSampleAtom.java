package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.QtHandlerFactory;
import com.drew.metadata.mov.media.QtVideoDirectory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-BBCGFJII
 */
public class TimeToSampleAtom extends FullAtom
{
    long numberOfEntries;
    ArrayList<Entry> entries;
    long sampleCount;
    long sampleDuration;

    public TimeToSampleAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);

        numberOfEntries = reader.getUInt32();
        entries = new ArrayList<Entry>();
        for (int i = 0; i < numberOfEntries; i++) {
            entries.add(new Entry(reader));
        }
        sampleCount = reader.getUInt32();
        sampleDuration = reader.getUInt32();
    }

    class Entry
    {
        long sampleCount;
        long sampleDuration;

        public Entry(SequentialReader reader) throws IOException
        {
            sampleCount = reader.getUInt32();
            sampleDuration = reader.getUInt32();
        }
    }

    public void addMetadata(QtVideoDirectory directory)
    {
        float frameRate = (float) QtHandlerFactory.HANDLER_PARAM_TIME_SCALE/(float)sampleDuration;
        directory.setFloat(QtVideoDirectory.TAG_FRAME_RATE, frameRate);
    }
}

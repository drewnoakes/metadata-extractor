package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.media.QtMusicDirectory;

import java.io.IOException;
import java.util.ArrayList;

public class MusicSampleDescriptionAtom extends SampleDescriptionAtom<MusicSampleDescriptionAtom.MusicSampleDescription>
{
    ArrayList<MusicSampleDescription> sampleDescriptions;

    public MusicSampleDescriptionAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);
    }

    @Override
    MusicSampleDescription getSampleDescription(SequentialReader reader) throws IOException
    {
        return new MusicSampleDescription(reader);
    }

    public void addMetadata(QtMusicDirectory directory)
    {
        // Do nothing
    }

    class MusicSampleDescription extends SampleDescription
    {
        long flags;

        public MusicSampleDescription(SequentialReader reader) throws IOException
        {
            super(reader);

            flags = reader.getUInt32();
        }
    }
}

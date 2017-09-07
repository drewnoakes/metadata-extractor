package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.media.QtMusicDirectory;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-57445
 */
public class MusicSampleDescriptionAtom extends SampleDescriptionAtom<MusicSampleDescriptionAtom.MusicSampleDescription>
{
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

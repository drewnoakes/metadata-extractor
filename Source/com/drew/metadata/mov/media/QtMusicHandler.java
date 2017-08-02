package com.drew.metadata.mov.media;

import com.drew.lang.SequentialReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtMediaHandler;
import com.drew.metadata.mov.atoms.MusicSampleDescriptionAtom;

import java.io.IOException;

public class QtMusicHandler extends QtMediaHandler<QtMusicDirectory>
{
    public QtMusicHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected QtMusicDirectory getDirectory()
    {
        return directory;
    }

    @Override
    protected String getMediaInformation()
    {
        return null;
    }

    @Override
    protected void processSampleDescription(SequentialReader reader) throws IOException
    {
        MusicSampleDescriptionAtom atom = new MusicSampleDescriptionAtom(reader, baseAtom);
        atom.addMetadata(directory);
    }

    @Override
    protected void processMediaInformation(SequentialReader reader) throws IOException
    {
        // Not yet implemented
    }

    @Override
    protected void processTimeToSample(SequentialReader reader) throws IOException
    {
        // Not yet implemented
    }
}

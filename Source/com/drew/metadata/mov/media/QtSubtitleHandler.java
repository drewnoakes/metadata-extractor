package com.drew.metadata.mov.media;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtMediaHandler;
import com.drew.metadata.mov.atoms.Atom;
import com.drew.metadata.mov.atoms.SubtitleSampleDescriptionAtom;

import java.io.IOException;

public class QtSubtitleHandler extends QtMediaHandler<QtSubtitleDirectory>
{
    public QtSubtitleHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected QtSubtitleDirectory getDirectory()
    {
        return new QtSubtitleDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        // Not yet implemented
        return null;
    }

    @Override
    protected void processSampleDescription(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        SubtitleSampleDescriptionAtom subtitleSampleDescriptionAtom = new SubtitleSampleDescriptionAtom(reader, atom);
        subtitleSampleDescriptionAtom.addMetadata(directory);
    }

    @Override
    protected void processMediaInformation(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        // Not yet implemented
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        // Not yet implemented
    }
}

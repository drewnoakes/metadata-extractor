package com.drew.metadata.mov.media;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtAtomTypes;
import com.drew.metadata.mov.QtMediaHandler;
import com.drew.metadata.mov.atoms.Atom;
import com.drew.metadata.mov.atoms.TimeToSampleAtom;
import com.drew.metadata.mov.atoms.VideoInformationMediaHeaderAtom;
import com.drew.metadata.mov.atoms.VideoSampleDescriptionAtom;

import java.io.IOException;

public class QtVideoHandler extends QtMediaHandler<QtVideoDirectory>
{
    public QtVideoHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected String getMediaInformation()
    {
        return QtAtomTypes.ATOM_VIDEO_MEDIA_INFO;
    }

    @Override
    protected QtVideoDirectory getDirectory()
    {
        return new QtVideoDirectory();
    }

    @Override
    public void processSampleDescription(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        VideoSampleDescriptionAtom videoSampleDescriptionAtom = new VideoSampleDescriptionAtom(reader, atom);
        videoSampleDescriptionAtom.addMetadata(directory);
    }

    @Override
    public void processMediaInformation(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        VideoInformationMediaHeaderAtom videoInformationMediaHeaderAtom = new VideoInformationMediaHeaderAtom(reader, atom);
        videoInformationMediaHeaderAtom.addMetadata(directory);
    }

    @Override
    public void processTimeToSample(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        TimeToSampleAtom timeToSampleAtom = new TimeToSampleAtom(reader, atom);
        timeToSampleAtom.addMetadata(directory);
    }
}

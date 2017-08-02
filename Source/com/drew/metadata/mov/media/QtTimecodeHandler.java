package com.drew.metadata.mov.media;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtAtomTypes;
import com.drew.metadata.mov.QtMediaHandler;
import com.drew.metadata.mov.atoms.Atom;
import com.drew.metadata.mov.atoms.TimecodeInformationMediaAtom;
import com.drew.metadata.mov.atoms.TimecodeSampleDescriptionAtom;

import java.io.IOException;

public class QtTimecodeHandler extends QtMediaHandler<QtTimecodeDirectory>
{
    public QtTimecodeHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected QtTimecodeDirectory getDirectory()
    {
        return new QtTimecodeDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        return QtAtomTypes.ATOM_TIMECODE_MEDIA_INFO;
    }

    @Override
    public void processSampleDescription(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        TimecodeSampleDescriptionAtom timecodeSampleDescriptionAtom = new TimecodeSampleDescriptionAtom(reader, atom);
        timecodeSampleDescriptionAtom.addMetadata(directory);
    }

    @Override
    public void processMediaInformation(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        TimecodeInformationMediaAtom timecodeInformationMediaAtom = new TimecodeInformationMediaAtom(reader, atom);
        timecodeInformationMediaAtom.addMetadata(directory);
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        // Do nothing
    }
}

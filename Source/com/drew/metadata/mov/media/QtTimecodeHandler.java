package com.drew.metadata.mov.media;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtAtomTypes;
import com.drew.metadata.mov.QtDirectory;
import com.drew.metadata.mov.QtMediaHandler;
import com.drew.metadata.mov.atoms.TimecodeMediaInformationAtom;
import com.drew.metadata.mov.atoms.TimecodeSampleDescriptionAtom;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-BBCGABGG
 */
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
    public void processSampleDescription(@NotNull SequentialReader reader) throws IOException
    {
        TimecodeSampleDescriptionAtom atom = new TimecodeSampleDescriptionAtom(reader, baseAtom);
        atom.addMetadata(directory);
    }

    @Override
    public void processMediaInformation(@NotNull SequentialReader reader) throws IOException
    {
        TimecodeMediaInformationAtom atom = new TimecodeMediaInformationAtom(reader, baseAtom);
        atom.addMetadata(directory);
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader) throws IOException
    {
        // Do nothing
    }
}

package com.drew.metadata.mov;

import com.drew.lang.SequentialByteArrayReader;

import java.io.IOException;

public class QtTimecodeMediaHandler extends QtMediaHandler
{
    @Override
    String getMediaInformation() {
        return QtAtomTypes.ATOM_TIMECODE_MEDIA_INFO;
    }

    @Override
    public void processSampleDescription(QtDirectory directory, SequentialByteArrayReader reader) throws IOException
    {

    }

    @Override
    public void processMediaInformation(QtDirectory directory, SequentialByteArrayReader reader) throws IOException
    {

    }
}

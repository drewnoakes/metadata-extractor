package com.drew.metadata.mov;

import com.drew.lang.SequentialByteArrayReader;

import java.io.IOException;

public class QtTimecodeMediaHandler implements QtMediaHandler
{
    @Override
    public boolean shouldAcceptAtom(String fourCC)
    {
        return fourCC.equals(QtAtomTypes.ATOM_SOUND_MEDIA_INFO)
            || fourCC.equals(QtAtomTypes.ATOM_SAMPLE_DESCRIPTION);
    }

    @Override
    public boolean shouldAcceptContainer(String fourCC)
    {
        return fourCC.equals(QtContainerTypes.ATOM_SAMPLE_TABLE)
            || fourCC.equals(QtContainerTypes.ATOM_MEDIA_INFORMATION);
    }

    @Override
    public QtHandler processAtom(String fourCC, byte[] payload, QtDirectory directory) throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_SOUND_MEDIA_INFO)) {
            processMediaInformation(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_SAMPLE_DESCRIPTION)) {
            processSampleDescription(directory, reader);
        }
        return this;
    }

    @Override
    public QtHandler processContainer(String fourCC)
    {
        return this;
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

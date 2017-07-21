package com.drew.metadata.mov;

import com.drew.lang.SequentialByteArrayReader;

import java.io.IOException;

public abstract class QtMediaHandler implements QtHandler
{
    @Override
    public boolean shouldAcceptAtom(String fourCC)
    {
        return fourCC.equals(getMediaInformation())
            || fourCC.equals(QtAtomTypes.ATOM_SAMPLE_DESCRIPTION)
            || fourCC.equals(QtAtomTypes.ATOM_TIME_TO_SAMPLE);
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
        if (fourCC.equals(getMediaInformation())) {
            processMediaInformation(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_SAMPLE_DESCRIPTION)) {
            processSampleDescription(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_TIME_TO_SAMPLE)) {
            processTimeToSample(directory, reader);
        }
        return this;
    }

    @Override
    public QtHandler processContainer(String fourCC)
    {
        return this;
    }

    abstract String getMediaInformation();

    abstract void processSampleDescription(QtDirectory directory, SequentialByteArrayReader reader) throws IOException;

    abstract void processMediaInformation(QtDirectory directory, SequentialByteArrayReader reader) throws IOException;

    abstract void processTimeToSample(QtDirectory directory, SequentialByteArrayReader reader) throws IOException;
}

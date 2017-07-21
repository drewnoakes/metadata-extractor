package com.drew.metadata.mov;

import com.drew.lang.SequentialByteArrayReader;

import java.io.IOException;

public interface QtMediaHandler extends QtHandler
{
    public void processSampleDescription(QtDirectory directory, SequentialByteArrayReader reader) throws IOException;

    public void processMediaInformation(QtDirectory directory, SequentialByteArrayReader reader) throws IOException;
}

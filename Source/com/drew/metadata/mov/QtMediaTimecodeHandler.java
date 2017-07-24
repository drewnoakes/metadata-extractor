package com.drew.metadata.mov;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialByteArrayReader;

import java.io.IOException;

public class QtMediaTimecodeHandler extends QtMediaHandler
{
    @Override
    String getMediaInformation()
    {
        return QtAtomTypes.ATOM_TIMECODE_MEDIA_INFO;
    }

    @Override
    public void processSampleDescription(QtDirectory directory, ByteArrayReader reader) throws IOException
    {
        int numberOfEntries = reader.getInt32(4);
        int sampleDescriptionSize = reader.getInt32(8);
        int dataReferenceIndex = reader.getInt16(14);
        int flags = reader.getInt32(18);
        int timeScale = reader.getInt32(22);
        int frameDuration = reader.getInt32(26);
        int numberOfFrames = reader.getInt8(30);
        // Source reference...
    }

    @Override
    public void processMediaInformation(QtDirectory directory, ByteArrayReader reader) throws IOException
    {
        // Do nothing
    }

    @Override
    void processTimeToSample(QtDirectory directory, ByteArrayReader reader) throws IOException
    {
        // Do nothing
    }
}

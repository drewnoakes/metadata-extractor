package com.drew.metadata.mov;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;

public class QtMediaTimecodeHandler extends QtMediaHandler
{
    @Override
    String getMediaInformation()
    {
        return QtAtomTypes.ATOM_TIMECODE_MEDIA_INFO;
    }

    /**
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-BBCGABGG
     */
    @Override
    public void processSampleDescription(@NotNull QtDirectory directory, @NotNull ByteArrayReader reader) throws IOException
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
    public void processMediaInformation(@NotNull QtDirectory directory, @NotNull ByteArrayReader reader) throws IOException
    {
        // Do nothing
    }

    /**
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-BBCGFJII
     */
    @Override
    void processTimeToSample(@NotNull QtDirectory directory, @NotNull ByteArrayReader reader) throws IOException
    {
        // Do nothing
    }
}

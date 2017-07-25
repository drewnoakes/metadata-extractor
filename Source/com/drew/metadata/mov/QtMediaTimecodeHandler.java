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
        // Begin general structure
        int versionAndFlags = reader.getInt32(0);
        int numberOfEntries = reader.getInt32(4);
        int sampleDescriptionSize = reader.getInt32(8);
        String dataFormat = reader.getString(12, 4, "UTF-8");
        // 6-bytes of reserved space set to 0
        int dataReferenceIndex = reader.getInt16(22);
        // End general structure

        if (!dataFormat.equals("tmcd")) {
            directory.addError("Timecode sample description atom has incorrect data format, no data extracted");
        }

        // 32-bits reserved space set to 0
        int flags = reader.getInt32(28);
        int timeScale = reader.getInt32(32);
        int frameDuration = reader.getInt32(36);
        int numberOfFrames = reader.getInt8(40);
        // 8-bits reserved space set to 0
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

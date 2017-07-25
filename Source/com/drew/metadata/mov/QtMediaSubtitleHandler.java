package com.drew.metadata.mov;

import com.drew.lang.ByteArrayReader;

import java.io.IOException;

public class QtMediaSubtitleHandler extends QtMediaHandler
{
    @Override
    String getMediaInformation()
    {
        // Not yet implemented
        return null;
    }

    @Override
    void processSampleDescription(QtDirectory directory, ByteArrayReader reader) throws IOException
    {
        // Begin general structure
        int versionAndFlags = reader.getInt32(0);
        int numberOfEntries = reader.getInt32(4);
        int sampleDescriptionSize = reader.getInt32(8);
        String dataFormat = reader.getString(12, 4, "UTF-8");
        // 6-bytes of reserved space set to 0
        int dataReferenceIndex = reader.getInt16(22);
        // End general structure

        int displayFlags = reader.getInt32(24);
        // 8-bits reserved space set to 1
        // 8-bits reserved space set to -1
        // 32-bits reserved space set to 0
        long defaultTextBox = reader.getInt64(30);
        // 32-bits reserved space set to 0
        int fontIdentifier = reader.getInt16(42);
        int fontFace = reader.getInt8(44);
        int fontSize = reader.getInt8(45);
        int foregroundColor = reader.getInt32(46);
        // font table atom 'ftab' not currently parsed
    }

    @Override
    void processMediaInformation(QtDirectory directory, ByteArrayReader reader) throws IOException
    {
        // Not yet implemented
    }

    @Override
    void processTimeToSample(QtDirectory directory, ByteArrayReader reader) throws IOException
    {
        // Not yet implemented
    }
}

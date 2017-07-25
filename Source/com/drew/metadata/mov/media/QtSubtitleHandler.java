package com.drew.metadata.mov.media;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtDirectory;
import com.drew.metadata.mov.QtMediaHandler;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-SW81
 */
public class QtSubtitleHandler extends QtMediaHandler
{
    public QtSubtitleHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected QtDirectory getDirectory()
    {
        return new QtSubtitleDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        // Not yet implemented
        return null;
    }

    @Override
    protected void processSampleDescription(@NotNull ByteArrayReader reader) throws IOException
    {
        // Begin general structure
        int versionAndFlags = reader.getInt32(0);
        int numberOfEntries = reader.getInt32(4);
        int sampleDescriptionSize = reader.getInt32(8);
        String dataFormat = reader.getString(12, 4, "UTF-8");
        // 6-bytes of reserved space set to 0
        int dataReferenceIndex = reader.getInt16(22);
        // End general structure

        if (!dataFormat.equals("tx3g")) {
            directory.addError("Subtitle sample description atom has incorrect data format, no data extracted");
            return;
        }

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
    protected void processMediaInformation(@NotNull ByteArrayReader reader) throws IOException
    {
        // Not yet implemented
    }

    @Override
    protected void processTimeToSample(@NotNull ByteArrayReader reader) throws IOException
    {
        // Not yet implemented
    }
}
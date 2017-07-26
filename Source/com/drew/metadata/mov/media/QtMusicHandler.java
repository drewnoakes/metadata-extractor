package com.drew.metadata.mov.media;

import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtDirectory;
import com.drew.metadata.mov.QtMediaHandler;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-BBCJIGHH
 *
 * @author Payton Garland
 */
public class QtMusicHandler extends QtMediaHandler
{
    public QtMusicHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected QtMusicDirectory getDirectory()
    {
        return new QtMusicDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        return null;
    }

    @Override
    protected void processSampleDescription(ByteArrayReader reader) throws IOException
    {
        // Begin general structure
        int versionAndFlags = reader.getInt32(0);
        int numberOfEntries = reader.getInt32(4);
        int sampleDescriptionSize = reader.getInt32(8);
        String dataFormat = reader.getString(12, 4, "UTF-8");
        // 6-bytes of reserved space set to 0
        int dataReferenceIndex = reader.getInt16(22);
        // End general structure

        if (!dataFormat.equals("musi")) {
            directory.addError("Music sample description atom has incorrect data format, no data extracted");
        }

        int flags = reader.getInt32(24);
        // QuickTime music formatted data may follow
    }

    @Override
    protected void processMediaInformation(ByteArrayReader reader) throws IOException
    {
        // Not yet implemented
    }

    @Override
    protected void processTimeToSample(ByteArrayReader reader) throws IOException
    {
        // Not yet implemented
    }
}

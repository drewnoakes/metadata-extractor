package com.drew.metadata.mov.media;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtDirectory;
import com.drew.metadata.mov.QtMediaHandler;

import java.io.IOException;

public class QtMusicHandler extends QtMediaHandler<QtMusicDirectory>
{
    public QtMusicHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected QtMusicDirectory getDirectory()
    {
        return directory;
    }

    @Override
    protected String getMediaInformation()
    {
        return null;
    }

    @Override
    protected void processSampleDescription(SequentialReader reader) throws IOException
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
    protected void processMediaInformation(SequentialReader reader) throws IOException
    {
        // Not yet implemented
    }

    @Override
    protected void processTimeToSample(SequentialReader reader) throws IOException
    {
        // Not yet implemented
    }
}

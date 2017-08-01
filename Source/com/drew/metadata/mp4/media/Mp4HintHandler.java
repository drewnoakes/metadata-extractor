package com.drew.metadata.mp4.media;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4BoxTypes;
import com.drew.metadata.mp4.Mp4MediaHandler;
import com.drew.metadata.mp4.boxes.HintMediaHeaderBox;

import java.io.IOException;

public class Mp4HintHandler extends Mp4MediaHandler<Mp4HintDirectory>
{

    public Mp4HintHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected Mp4HintDirectory getDirectory()
    {
        return new Mp4HintDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        return Mp4BoxTypes.BOX_HINT_MEDIA_INFO;
    }

    @Override
    protected void processSampleDescription(@NotNull SequentialReader reader) throws IOException
    {

    }

    @Override
    protected void processMediaInformation(@NotNull SequentialReader reader) throws IOException
    {
        HintMediaHeaderBox box = new HintMediaHeaderBox(reader, atom);
        box.addMetadata(directory);
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader) throws IOException
    {

    }
}

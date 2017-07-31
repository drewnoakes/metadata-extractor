package com.drew.metadata.mp4.media;

import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4ContainerTypes;
import com.drew.metadata.mp4.Mp4MediaHandler;

import java.io.IOException;

public class Mp4MetaHandler extends Mp4MediaHandler
{
    public Mp4MetaHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected Directory getDirectory()
    {
        return null;
    }

    @Override
    protected String getMediaInformation()
    {
        return Mp4ContainerTypes.BOX_MEDIA_NULL;
    }

    @Override
    protected void processSampleDescription(ByteArrayReader reader) throws IOException
    {

    }

    @Override
    protected void processMediaInformation(ByteArrayReader reader) throws IOException
    {

    }

    @Override
    protected void processTimeToSample(ByteArrayReader reader) throws IOException
    {

    }
}

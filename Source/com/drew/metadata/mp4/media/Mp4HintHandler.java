package com.drew.metadata.mp4.media;

import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4BoxTypes;
import com.drew.metadata.mp4.Mp4MediaHandler;

import java.io.IOException;

public class Mp4HintHandler extends Mp4MediaHandler
{

    public Mp4HintHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected String getMediaInformation()
    {
        return Mp4BoxTypes.ATOM_HINT_MEDIA_INFO;
    }

    @Override
    protected void processSampleDescription(ByteArrayReader reader) throws IOException
    {

    }

    @Override
    protected void processMediaInformation(ByteArrayReader reader) throws IOException
    {
        int maxPDUsize = reader.getUInt16(4);
        int avgPDUsize = reader.getUInt16(6);
        long maxbitrate = reader.getUInt32(8);
        long avgbitrate = reader.getUInt32(12);
    }

    @Override
    protected void processTimeToSample(ByteArrayReader reader) throws IOException
    {

    }

    @Override
    protected Directory getDirectory() {
        return new Mp4HintDirectory();
    }
}

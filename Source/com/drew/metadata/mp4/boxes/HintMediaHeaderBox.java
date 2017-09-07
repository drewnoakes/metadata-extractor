package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mp4.media.Mp4HintDirectory;

import java.io.IOException;

/**
 * ISO/IED 14496-12:2015 pg.169
 */
public class HintMediaHeaderBox extends FullBox
{
    int maxPDUsize;
    int avgPDUsize;
    long maxbitrate;
    long avgbitrate;

    public HintMediaHeaderBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        maxPDUsize = reader.getUInt16();
        avgPDUsize = reader.getUInt16();
        maxbitrate = reader.getUInt32();
        avgbitrate = reader.getUInt32();
    }

    public void addMetadata(Mp4HintDirectory directory)
    {
        directory.setInt(Mp4HintDirectory.TAG_MAX_PDU_SIZE, maxPDUsize);
        directory.setInt(Mp4HintDirectory.TAG_AVERAGE_PDU_SIZE, avgPDUsize);
        directory.setLong(Mp4HintDirectory.TAG_MAX_BITRATE, maxbitrate);
        directory.setLong(Mp4HintDirectory.TAG_AVERAGE_BITRATE, avgbitrate);
    }
}

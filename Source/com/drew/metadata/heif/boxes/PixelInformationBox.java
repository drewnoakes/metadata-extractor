package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.heif.HeifDirectory;

import java.io.IOException;

/**
 * ISO/IEC 23008-12:2017 pg.13
 */
public class PixelInformationBox extends FullBox
{
    int numChannels;
    int[] channels;

    public PixelInformationBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        numChannels = reader.getUInt8();
        channels = new int[numChannels];
        for (int i = 0; i < channels.length; i++) {
            channels[i] = reader.getUInt8();
        }
    }

    public void addMetadata(HeifDirectory directory)
    {
        if (!directory.containsTag(HeifDirectory.TAG_BITS_PER_CHANNEL)) {
            directory.setIntArray(HeifDirectory.TAG_BITS_PER_CHANNEL, channels);
        }
    }
}

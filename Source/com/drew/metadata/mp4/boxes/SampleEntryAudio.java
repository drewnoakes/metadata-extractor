package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mp4.media.Mp4SoundDirectory;

import java.io.IOException;

public class SampleEntryAudio extends SampleEntry
{
    int channelcount;
    int samplesize;
    long samplerate;

    public SampleEntryAudio(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        reader.skip(8); // Reserved
        channelcount = reader.getUInt16();
        samplesize = reader.getInt16();
        reader.skip(2); // Pre-defined
        reader.skip(2); // Reserved
        samplerate = reader.getUInt32();
        // ChannelLayout()
        // DownMix and/or DRC boxes
        // More boxes as needed
    }

    public void addMetadata(Mp4SoundDirectory directory)
    {
        directory.setInt(Mp4SoundDirectory.TAG_NUMBER_OF_CHANNELS, channelcount);
        directory.setInt(Mp4SoundDirectory.TAG_AUDIO_SAMPLE_SIZE, samplesize);
        directory.setLong(Mp4SoundDirectory.TAG_AUDIO_SAMPLE_RATE, samplerate);
    }
}

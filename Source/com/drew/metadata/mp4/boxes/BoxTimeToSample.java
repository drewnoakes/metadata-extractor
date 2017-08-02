package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mp4.Mp4HandlerFactory;
import com.drew.metadata.mp4.media.Mp4SoundDirectory;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;

import java.io.IOException;
import java.util.ArrayList;

public class BoxTimeToSample extends BoxFull
{
    long entryCount;
    ArrayList<EntryCount> entries;

    public BoxTimeToSample(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        entryCount = reader.getUInt32();
        entries = new ArrayList<EntryCount>();
        for (int i = 0; i < entryCount; i++) {
            entries.add(new EntryCount(reader.getUInt32(), reader.getUInt32()));
        }
    }

    public void addMetadata(Mp4VideoDirectory directory)
    {
        float frameRate = (float) Mp4HandlerFactory.HANDLER_PARAM_TIME_SCALE/(float)entries.get(0).sampleDelta;
        directory.setFloat(Mp4VideoDirectory.TAG_FRAME_RATE, frameRate);
    }

    public void addMetadata(Mp4SoundDirectory directory)
    {
        directory.setDouble(Mp4SoundDirectory.TAG_AUDIO_SAMPLE_RATE, Mp4HandlerFactory.HANDLER_PARAM_TIME_SCALE);
    }

    class EntryCount
    {
        long sampleCount;
        long sampleDelta;

        public EntryCount(long sampleCount, long sampleDelta)
        {
            this.sampleCount = sampleCount;
            this.sampleDelta = sampleDelta;
        }
    }
}

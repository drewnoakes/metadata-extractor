package com.drew.metadata.mp4.media;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4BoxTypes;
import com.drew.metadata.mp4.Mp4MediaHandler;
import com.drew.metadata.mp4.boxes.AudioSampleEntry;
import com.drew.metadata.mp4.boxes.Box;
import com.drew.metadata.mp4.boxes.SoundMediaHeaderBox;
import com.drew.metadata.mp4.boxes.TimeToSampleBox;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-BBCGEBEH
 */
public class Mp4SoundHandler extends Mp4MediaHandler<Mp4SoundDirectory>
{
    public Mp4SoundHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected Mp4SoundDirectory getDirectory()
    {
        return new Mp4SoundDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        return Mp4BoxTypes.BOX_SOUND_MEDIA_INFO;
    }

    @Override
    public void processSampleDescription(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {
        AudioSampleEntry audioSampleEntry = new AudioSampleEntry(reader, box);
        audioSampleEntry.addMetadata(directory);
    }

    @Override
    public void processMediaInformation(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {
        SoundMediaHeaderBox soundMediaHeaderBox = new SoundMediaHeaderBox(reader, box);
        soundMediaHeaderBox.addMetadata(directory);
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {
        TimeToSampleBox timeToSampleBox = new TimeToSampleBox(reader, box);
        timeToSampleBox.addMetadata(directory);
    }
}

package com.drew.metadata.mp4.media;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4BoxTypes;
import com.drew.metadata.mp4.Mp4MediaHandler;
import com.drew.metadata.mp4.boxes.AudioSampleEntry;
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
    public void processSampleDescription(@NotNull SequentialReader reader) throws IOException
    {
        AudioSampleEntry box = new AudioSampleEntry(reader, baseAtom);
        box.addMetadata(directory);
    }

    @Override
    public void processMediaInformation(@NotNull SequentialReader reader) throws IOException
    {
        SoundMediaHeaderBox box = new SoundMediaHeaderBox(reader, baseAtom);
        box.addMetadata(directory);
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader) throws IOException
    {
        TimeToSampleBox box = new TimeToSampleBox(reader, baseAtom);
        box.addMetadata(directory);
    }
}

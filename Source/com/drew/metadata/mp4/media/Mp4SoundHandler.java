package com.drew.metadata.mp4.media;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4BoxTypes;
import com.drew.metadata.mp4.Mp4MediaHandler;
import com.drew.metadata.mp4.boxes.SampleEntryAudio;
import com.drew.metadata.mp4.boxes.BoxHeaderMediaSound;
import com.drew.metadata.mp4.boxes.BoxTimeToSample;

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
        SampleEntryAudio box = new SampleEntryAudio(reader, baseAtom);
        box.addMetadata(directory);
    }

    @Override
    public void processMediaInformation(@NotNull SequentialReader reader) throws IOException
    {
        BoxHeaderMediaSound box = new BoxHeaderMediaSound(reader, baseAtom);
        box.addMetadata(directory);
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader) throws IOException
    {
        BoxTimeToSample box = new BoxTimeToSample(reader, baseAtom);
        box.addMetadata(directory);
    }
}

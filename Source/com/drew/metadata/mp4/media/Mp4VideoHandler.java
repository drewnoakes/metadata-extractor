package com.drew.metadata.mp4.media;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtAtomTypes;
import com.drew.metadata.mp4.Mp4BoxTypes;
import com.drew.metadata.mp4.Mp4MediaHandler;
import com.drew.metadata.mp4.boxes.Box;
import com.drew.metadata.mp4.boxes.TimeToSampleBox;
import com.drew.metadata.mp4.boxes.VideoMediaHeaderBox;
import com.drew.metadata.mp4.boxes.VisualSampleEntry;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-74522
 */
public class Mp4VideoHandler extends Mp4MediaHandler<Mp4VideoDirectory>
{
    public Mp4VideoHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected String getMediaInformation()
    {
        return Mp4BoxTypes.BOX_VIDEO_MEDIA_INFO;
    }

    @Override
    protected Mp4VideoDirectory getDirectory()
    {
        return new Mp4VideoDirectory();
    }

    @Override
    public void processSampleDescription(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {
        VisualSampleEntry visualSampleEntry = new VisualSampleEntry(reader, box);
        visualSampleEntry.addMetadata(directory);
    }

    /**
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-25642
     */
    @Override
    public void processMediaInformation(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {
        VideoMediaHeaderBox videoMediaHeaderBox = new VideoMediaHeaderBox(reader, box);
        videoMediaHeaderBox.addMetadata(directory);
    }

    /**
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-BBCGFJII
     */
    @Override
    public void processTimeToSample(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {
        TimeToSampleBox timeToSampleBox = new TimeToSampleBox(reader, box);
        timeToSampleBox.addMetadata(directory);
    }
}

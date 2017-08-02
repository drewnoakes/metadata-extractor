package com.drew.metadata.mp4.media;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtAtomTypes;
import com.drew.metadata.mp4.Mp4MediaHandler;
import com.drew.metadata.mp4.boxes.BoxTimeToSample;
import com.drew.metadata.mp4.boxes.BoxHeaderMediaVideo;
import com.drew.metadata.mp4.boxes.SampleEntryVisual;

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
        return QtAtomTypes.ATOM_VIDEO_MEDIA_INFO;
    }

    @Override
    protected Mp4VideoDirectory getDirectory()
    {
        return new Mp4VideoDirectory();
    }

    @Override
    public void processSampleDescription(@NotNull SequentialReader reader) throws IOException
    {
        SampleEntryVisual box = new SampleEntryVisual(reader, baseAtom);
        box.addMetadata(directory);
    }

    /**
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-25642
     */
    @Override
    public void processMediaInformation(@NotNull SequentialReader reader) throws IOException
    {
        BoxHeaderMediaVideo box = new BoxHeaderMediaVideo(reader, baseAtom);
        box.addMetadata(directory);
    }

    /**
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-BBCGFJII
     */
    @Override
    public void processTimeToSample(@NotNull SequentialReader reader) throws IOException
    {
        BoxTimeToSample box = new BoxTimeToSample(reader, baseAtom);
        box.addMetadata(directory);
    }
}

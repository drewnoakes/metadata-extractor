package com.drew.metadata.mov.media;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtMediaHandler;
import com.drew.metadata.mov.atoms.AtomSampleDescriptionSubtitle;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-SW81
 */
public class QtSubtitleHandler extends QtMediaHandler<QtSubtitleDirectory>
{
    public QtSubtitleHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected QtSubtitleDirectory getDirectory()
    {
        return new QtSubtitleDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        // Not yet implemented
        return null;
    }

    @Override
    protected void processSampleDescription(@NotNull SequentialReader reader) throws IOException
    {
        AtomSampleDescriptionSubtitle atom = new AtomSampleDescriptionSubtitle(reader, baseAtom);
        atom.addMetadata(directory);
    }

    @Override
    protected void processMediaInformation(@NotNull SequentialReader reader) throws IOException
    {
        // Not yet implemented
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader) throws IOException
    {
        // Not yet implemented
    }
}

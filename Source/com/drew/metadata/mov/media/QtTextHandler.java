package com.drew.metadata.mov.media;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtAtomTypes;
import com.drew.metadata.mov.QtContainerTypes;
import com.drew.metadata.mov.QtMediaHandler;
import com.drew.metadata.mov.atoms.TextSampleDescriptionAtom;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-BBCJAJEA
 */
public class QtTextHandler extends QtMediaHandler<QtTextDirectory>
{
    public QtTextHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected QtTextDirectory getDirectory()
    {
        return new QtTextDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        return QtAtomTypes.ATOM_BASE_MEDIA_INFO;
    }

    @Override
    protected void processSampleDescription(@NotNull SequentialReader reader) throws IOException
    {
        TextSampleDescriptionAtom atom = new TextSampleDescriptionAtom(reader, baseAtom);
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

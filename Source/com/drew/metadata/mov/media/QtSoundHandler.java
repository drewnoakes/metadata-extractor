package com.drew.metadata.mov.media;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.*;
import com.drew.metadata.mov.atoms.AtomHeaderMediaSoundInformation;
import com.drew.metadata.mov.atoms.AtomSampleDescriptionSound;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-BBCGEBEH
 */
public class QtSoundHandler extends QtMediaHandler<QtSoundDirectory>
{
    public QtSoundHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected QtSoundDirectory getDirectory()
    {
        return new QtSoundDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        return QtAtomTypes.ATOM_SOUND_MEDIA_INFO;
    }

    @Override
    public void processSampleDescription(@NotNull SequentialReader reader) throws IOException
    {
        AtomSampleDescriptionSound atom = new AtomSampleDescriptionSound(reader, baseAtom);
        atom.addMetadata(directory);
    }

    @Override
    public void processMediaInformation(@NotNull SequentialReader reader) throws IOException
    {
        AtomHeaderMediaSoundInformation atom = new AtomHeaderMediaSoundInformation(reader, baseAtom);
        atom.addMetadata(directory);
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader) throws IOException
    {
        directory.setDouble(QtSoundDirectory.TAG_AUDIO_SAMPLE_RATE, QtHandlerFactory.HANDLER_PARAM_TIME_SCALE);
    }
}

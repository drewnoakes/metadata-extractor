package com.drew.metadata.mov.media;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.*;
import com.drew.metadata.mov.atoms.Atom;
import com.drew.metadata.mov.atoms.SoundInformationMediaHeaderAtom;
import com.drew.metadata.mov.atoms.SoundSampleDescriptionAtom;

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
    public void processSampleDescription(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        SoundSampleDescriptionAtom soundSampleDescriptionAtom = new SoundSampleDescriptionAtom(reader, atom);
        soundSampleDescriptionAtom.addMetadata(directory);
    }

    @Override
    public void processMediaInformation(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        SoundInformationMediaHeaderAtom soundInformationMediaHeaderAtom = new SoundInformationMediaHeaderAtom(reader, atom);
        soundInformationMediaHeaderAtom.addMetadata(directory);
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        directory.setDouble(QtSoundDirectory.TAG_AUDIO_SAMPLE_RATE, QtHandlerFactory.HANDLER_PARAM_TIME_SCALE);
    }
}

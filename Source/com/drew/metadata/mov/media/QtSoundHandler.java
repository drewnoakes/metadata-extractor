package com.drew.metadata.mov.media;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.*;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-BBCGEBEH
 */
public class QtSoundHandler extends QtMediaHandler
{
    public QtSoundHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected QtDirectory getDirectory()
    {
        return new QtSoundDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        return QtAtomTypes.ATOM_SOUND_MEDIA_INFO;
    }

    @Override
    public void processSampleDescription(@NotNull ByteArrayReader reader) throws IOException
    {
        String dataFormat = new String(reader.getBytes(12, 4));
        int numberOfChannels = reader.getInt16(32);
        int sampleSizeBits = reader.getInt16(34);

        directory.setString(QtSoundDirectory.TAG_AUDIO_FORMAT, QtDictionary.lookup(QtSoundDirectory.TAG_AUDIO_FORMAT, dataFormat));
        directory.setInt(QtSoundDirectory.TAG_NUMBER_OF_CHANNELS, numberOfChannels);
        directory.setInt(QtSoundDirectory.TAG_AUDIO_SAMPLE_SIZE, sampleSizeBits);
    }

    @Override
    public void processMediaInformation(@NotNull ByteArrayReader reader) throws IOException
    {
        int balance = reader.getInt16(4);
        double integerPortion = balance & 0xFFFF0000;
        double fractionPortion = (balance & 0x0000FFFF) / Math.pow(2, 4);
        directory.setDouble(QtSoundDirectory.TAG_SOUND_BALANCE, integerPortion + fractionPortion);
    }

    @Override
    protected void processTimeToSample(@NotNull ByteArrayReader reader) throws IOException
    {
        directory.setDouble(QtSoundDirectory.TAG_AUDIO_SAMPLE_RATE, QtHandlerFactory.HANDLER_PARAM_TIME_SCALE);
    }
}

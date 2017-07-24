package com.drew.metadata.mov;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;

public class QtMediaSoundHandler extends QtMediaHandler
{
    @Override
    String getMediaInformation()
    {
        return QtAtomTypes.ATOM_SOUND_MEDIA_INFO;
    }

    /**
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-BBCGGHJH
     */
    @Override
    public void processSampleDescription(@NotNull QtDirectory directory, @NotNull ByteArrayReader reader) throws IOException
    {
        String dataFormat = new String(reader.getBytes(12, 4));
        int numberOfChannels = reader.getInt16(32);
        int sampleSizeBits = reader.getInt16(34);

        directory.setString(QtDirectory.TAG_AUDIO_FORMAT, QtDictionary.lookup(QtDirectory.TAG_AUDIO_FORMAT, dataFormat));
        directory.setInt(QtDirectory.TAG_NUMBER_OF_CHANNELS, numberOfChannels);
        directory.setInt(QtDirectory.TAG_AUDIO_SAMPLE_SIZE, sampleSizeBits);
    }

    /**
     * Extracts data from the Sound Media Information Header
     *
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-33020
     */
    @Override
    public void processMediaInformation(@NotNull QtDirectory directory, @NotNull ByteArrayReader reader) throws IOException
    {
        int balance = reader.getInt16(4);
        double integerPortion = balance & 0xFFFF0000;
        double fractionPortion = (balance & 0x0000FFFF) / Math.pow(2, 4);
        directory.setDouble(QtDirectory.TAG_SOUND_BALANCE, integerPortion + fractionPortion);
    }

    /**
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-BBCGFJII
     */
    @Override
    void processTimeToSample(@NotNull QtDirectory directory, @NotNull ByteArrayReader reader) throws IOException
    {
        directory.setDouble(QtDirectory.TAG_AUDIO_SAMPLE_RATE, QtHandlerFactory.HANDLER_PARAM_TIME_SCALE);
    }
}

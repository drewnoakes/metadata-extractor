package com.drew.metadata.mp4.media;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.*;
import com.drew.metadata.mp4.Mp4HandlerFactory;
import com.drew.metadata.mp4.Mp4MediaHandler;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-BBCGEBEH
 */
public class Mp4SoundHandler extends Mp4MediaHandler
{
    public Mp4SoundHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected Directory getDirectory()
    {
        return new Mp4SoundDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        return QtAtomTypes.ATOM_SOUND_MEDIA_INFO;
    }

    @Override
    public void processSampleDescription(@NotNull ByteArrayReader reader) throws IOException
    {
        // Date format exists, but is not listed in ISO standard documentation
        // Skip 8-bytes SampleEntry
        // Skip 8-bytes reserved set to 0
        int channelCount = reader.getUInt16(32);
        int sampleSize = reader.getUInt16(34);
        // Skip 16-bits pre-defined set to 0
        // Skip 16-bits reserved set to 0
        long sampleRate = reader.getUInt32(38);

        directory.setInt(Mp4SoundDirectory.TAG_NUMBER_OF_CHANNELS, channelCount);
        directory.setInt(Mp4SoundDirectory.TAG_AUDIO_SAMPLE_SIZE, sampleSize);
        directory.setLong(Mp4SoundDirectory.TAG_AUDIO_SAMPLE_RATE, sampleRate);
    }

    @Override
    public void processMediaInformation(@NotNull ByteArrayReader reader) throws IOException
    {
        int balance = reader.getInt16(4);
        double integerPortion = balance & 0xFFFF0000;
        double fractionPortion = (balance & 0x0000FFFF) / Math.pow(2, 4);
        directory.setDouble(Mp4SoundDirectory.TAG_SOUND_BALANCE, integerPortion + fractionPortion);
    }

    @Override
    protected void processTimeToSample(@NotNull ByteArrayReader reader) throws IOException
    {
        directory.setDouble(Mp4SoundDirectory.TAG_AUDIO_SAMPLE_RATE, Mp4HandlerFactory.HANDLER_PARAM_TIME_SCALE);
    }
}

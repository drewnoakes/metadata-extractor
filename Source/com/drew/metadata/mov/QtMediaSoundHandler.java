package com.drew.metadata.mov;

import com.drew.lang.SequentialByteArrayReader;

import java.io.IOException;

public class QtMediaSoundHandler extends QtMediaHandler
{
    @Override
    String getMediaInformation()
    {
        return QtAtomTypes.ATOM_SOUND_MEDIA_INFO;
    }

    @Override
    public void processSampleDescription(QtDirectory directory, SequentialByteArrayReader reader) throws IOException
    {
        reader.skip(4);
        int numberOfEntries = reader.getInt32();
        int sampleSize = reader.getInt32();
        String dataFormat = new String(reader.getBytes(4));
        reader.skip(6); // 6-bytes reserved
        int dataReference = reader.getInt16();
        int version = reader.getInt16();
        int revisionLevel;
        int vendor;
        switch (version) {
            case (0):
            case (1):
                revisionLevel = reader.getInt16();
                vendor = reader.getInt32();
                int numberOfChannels = reader.getInt16();
                int sampleSizeBits = reader.getInt16();
                int compressionId = reader.getInt16();
                int packetSize = reader.getInt16();
                int sampleRate = reader.getInt32();

                directory.setString(QtDirectory.TAG_AUDIO_FORMAT, QtDictionary.lookup(QtDirectory.TAG_AUDIO_FORMAT, dataFormat));
                directory.setInt(QtDirectory.TAG_NUMBER_OF_CHANNELS, numberOfChannels);
                directory.setInt(QtDirectory.TAG_AUDIO_SAMPLE_SIZE, sampleSizeBits);
                break;
            case (2):
                revisionLevel = reader.getInt32();
                vendor = reader.getInt32();
                reader.skip(16);
                long audioSampleRate = reader.getInt64();
                int numAudioChannels = reader.getInt32();
                reader.skip(4);
                int constBitsPerChannel = reader.getInt32();
                int formatSpecificFlags = reader.getInt32();
                int constBytesPerAudioPacket = reader.getInt32();
                int constLPCMFramesPerAudioPacket = reader.getInt32();

                directory.setInt(QtDirectory.TAG_NUMBER_OF_CHANNELS, numAudioChannels);
                directory.setLong(QtDirectory.TAG_AUDIO_SAMPLE_RATE, audioSampleRate);
                break;
        }
    }

    @Override
    public void processMediaInformation(QtDirectory directory, SequentialByteArrayReader reader) throws IOException
    {
        reader.skip(4);
        int balance = reader.getInt16();
        double integerPortion = balance & 0xFFFF0000;
        double fractionPortion = (balance & 0x0000FFFF) / Math.pow(2, 4);
        directory.setDouble(QtDirectory.TAG_SOUND_BALANCE, integerPortion + fractionPortion);
    }

    @Override
    void processTimeToSample(QtDirectory directory, SequentialByteArrayReader reader) throws IOException
    {
        directory.setDouble(QtDirectory.TAG_AUDIO_SAMPLE_RATE, QtHandlerFactory.HANDLER_PARAM_TIME_SCALE);
    }
}

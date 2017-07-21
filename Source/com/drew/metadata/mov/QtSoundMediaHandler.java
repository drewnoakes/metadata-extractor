package com.drew.metadata.mov;

import com.drew.lang.SequentialByteArrayReader;

import java.io.IOException;

public class QtSoundMediaHandler extends QtMediaHandler
{
    @Override
    String getMediaInformation() {
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
        switch (version) {
            case (0):
            case (1):
                int revisionLevel = reader.getInt16();
                int vendor = reader.getInt32();
                int numberOfChannels = reader.getInt16();
                int sampleSizeBits = reader.getInt16();
                int compressionId = reader.getInt16();
                int packetSize = reader.getInt16();
                int sampleRate = reader.getInt32();

                directory.setString(QtDirectory.TAG_AUDIO_FORMAT, QtDictionary.lookup(QtDirectory.TAG_AUDIO_FORMAT, dataFormat));
                directory.setInt(QtDirectory.TAG_NUMBER_OF_CHANNELS, numberOfChannels);
                directory.setInt(QtDirectory.TAG_SAMPLE_SIZE, sampleSizeBits);
                directory.setInt(QtDirectory.TAG_SAMPLE_RATE, sampleRate);
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
}

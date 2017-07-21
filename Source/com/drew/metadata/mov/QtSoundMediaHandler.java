package com.drew.metadata.mov;

import com.drew.lang.SequentialByteArrayReader;

import java.io.IOException;

public class QtSoundMediaHandler implements QtMediaHandler
{

    @Override
    public boolean shouldAcceptAtom(String fourCC)
    {
        return fourCC.equals(QtAtomTypes.ATOM_SOUND_MEDIA_INFO)
            || fourCC.equals(QtAtomTypes.ATOM_SAMPLE_DESCRIPTION);
    }

    @Override
    public boolean shouldAcceptContainer(String fourCC)
    {
        return fourCC.equals(QtContainerTypes.ATOM_SAMPLE_TABLE)
            || fourCC.equals(QtContainerTypes.ATOM_MEDIA_INFORMATION);
    }

    @Override
    public QtHandler processAtom(String fourCC, byte[] payload, QtDirectory directory) throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_SOUND_MEDIA_INFO)) {
            processMediaInformation(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_SAMPLE_DESCRIPTION)) {
            processSampleDescription(directory, reader);
        }
        return this;
    }

    @Override
    public QtHandler processContainer(String fourCC)
    {
        return this;
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

package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.QtDictionary;
import com.drew.metadata.mov.media.QtSoundDirectory;

import java.io.IOException;

public class SoundSampleDescriptionAtom extends SampleDescriptionAtom<SoundSampleDescriptionAtom.SoundSampleDescription>
{

    public SoundSampleDescriptionAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);
    }

    @Override
    SoundSampleDescription getSampleDescription(SequentialReader reader) throws IOException
    {
        return new SoundSampleDescription(reader);
    }

    public void addMetadata(QtSoundDirectory directory)
    {
        SoundSampleDescription description = sampleDescriptions.get(0);

        directory.setString(QtSoundDirectory.TAG_AUDIO_FORMAT, QtDictionary.lookup(QtSoundDirectory.TAG_AUDIO_FORMAT, description.dataFormat));
        directory.setInt(QtSoundDirectory.TAG_NUMBER_OF_CHANNELS, description.numberOfChannels);
        directory.setInt(QtSoundDirectory.TAG_AUDIO_SAMPLE_SIZE, description.sampleSize);
    }

    class SoundSampleDescription extends SampleDescription
    {
        int version;
        int revisionLevel;
        int vendor;
        int numberOfChannels;
        int sampleSize;
        int compressionID;
        int packetSize;
        long sampleRate;

        public SoundSampleDescription(SequentialReader reader) throws IOException
        {
            super(reader);

            version = reader.getUInt16();
            revisionLevel = reader.getUInt16();
            vendor = reader.getInt32();
            numberOfChannels = reader.getUInt16();
            sampleSize = reader.getUInt16();
            compressionID = reader.getUInt16();
            packetSize = reader.getUInt16();
            sampleRate = reader.getUInt32();
        }
    }
}

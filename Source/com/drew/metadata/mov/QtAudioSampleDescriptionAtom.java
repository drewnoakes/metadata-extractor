package com.drew.metadata.mov;

import java.io.IOException;

public class QtAudioSampleDescriptionAtom {
    private int channels;
    private int sampleSize;
    private int compressionId;
    private int packetSize;
    private long sampleRate;
    private String audioCodec;

    QtAudioSampleDescriptionAtom(QtSampleDescriptionAtom atom)
    {
        super(atom.getSize(), atom.getType(), atom.getOffset());
    }

    public void getMetadata(QtDataSource source) throws IOException
    {
        byte[] buffer = new byte[4];
        byte[] shortBuffer = new byte[2];

        source.reset();
        source.skip(offset + 8);

        version = source.readByte();

        buffer = new byte[3];
        source.read(buffer);
        flags = "Flags 0x" + buffer[0] + buffer[1] + buffer[2];

        buffer = new byte[4];
        source.read(buffer);
        numberOfEntries = ByteUtil.getInt32(buffer, 0, true);

//		for (int i=0; i<numberOfEntries; i++)
//		{

        source.read(buffer);
        tableSize = ByteUtil.getInt32(buffer, 0, true);

        source.read(buffer);
        dataFormat = new String(buffer);


        buffer = new byte[6];
        source.read(buffer);   // skip over reserved field

        source.read(shortBuffer);
        dataReferenceIndex = ByteUtil.getInt16(shortBuffer, 0, true);

        source.read(shortBuffer);
        encoderVersion = ByteUtil.getInt16(shortBuffer, 0, true);

        source.read(shortBuffer);
        encoderRevision = ByteUtil.getInt16(shortBuffer, 0, true);

        buffer = new byte[4];
        source.read(buffer);
        encoderVendor = new String(buffer);

        encoderVendor = "0";

        source.read(shortBuffer);
        channels = ByteUtil.getInt16(shortBuffer, 0, true);

        source.read(shortBuffer);
        sampleSize = ByteUtil.getInt16(shortBuffer, 0, true);

        source.read(shortBuffer);
        compressionId = ByteUtil.getInt16(shortBuffer, 0, true);

        source.read(shortBuffer);
        packetSize = ByteUtil.getInt16(shortBuffer, 0, true);

        source.read(buffer);
        sampleRate = ((long)ByteUtil.getUnsignedInt32(buffer, 0, true) >> 16);    // fixed point 16

        audioCodec = dataFormat;
        if (CodecTypes.audioCodecs.containsKey(dataFormat))
        {
            audioCodec = CodecTypes.audioCodecs.get(dataFormat);
        }
    }

    public void populateMetadata(FileInfo fileId)
    {
        fileId.addMetadata(StandardMetadata.CHANNELS, channels);
        fileId.addMetadata(StandardMetadata.SAMPLE_SIZE, sampleSize);
        fileId.addMetadata(StandardMetadata.SAMPLE_RATE, sampleRate);
        fileId.addMetadata(StandardMetadata.AUDIO_CODEC, audioCodec);
    }


    public String toString()
    {
        return new String(type + " (" + size + " bytes)(offset:" + offset + ") " +
            "\n Version: " + version +
            "\n Flags: " + flags +
            "\n Number of Entries: " + numberOfEntries +
            "\n Table size: " + tableSize +
            "\n Data Format: " + dataFormat +
            "\n Data reference index: " + dataReferenceIndex +
            "\n Encoder version: " + encoderVersion +
            "\n Encoder revision: " + encoderRevision +
            "\n Encoder vendor: " + encoderVendor +
            " Audio codec: " + audioCodec +
            "\n channels: " + channels +
            "\n sampleSize: " + sampleSize +
            "\n compression Id: " + compressionId +
            "\n packet size: " + packetSize +
            "\n sample rate: " + sampleRate
        );
    }
}

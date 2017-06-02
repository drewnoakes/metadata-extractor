package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.io.IOException;

public class QtVideoSampleDescriptionAtom extends QtSampleDescriptionAtom {
    private String videoCodec;
    private int temporalQuality;
    private int spatialQuality;
    private int frameWidth;
    private int frameHeight;
    private float resolutionX;
    private float resolutionY;
    private int dataSize;
    private int frameCount;
    private String compressorName;
    private int pixelColorDepth;

    QtVideoSampleDescriptionAtom(QtSampleDescriptionAtom atom)
    {
        super(atom.getSize(), atom.getType(), atom.getOffset());
    }

    public void getMetadata(QtDataSource source) throws IOException
    {
        byte[] buffer = new byte[4];
        byte[] shortBuffer = new byte[2];
        long filePointer = offset + 8;

        source.reset();
        source.skip(offset + 8);

        version = source.readByte();
        filePointer++;

        buffer = new byte[3];
        source.read(buffer);
        flags = "Flags 0x" + buffer[0] + buffer[1] + buffer[2];
        filePointer += 3;

        buffer = new byte[4];
        source.read(buffer);
        numberOfEntries = ByteUtil.getInt32(buffer, 0, true);
        filePointer += 4;

//		for (int i=0; i<numberOfEntries; i++)
//		{

        source.read(buffer);
        tableSize = ByteUtil.getInt32(buffer, 0, true);
        filePointer +=4;

        source.read(buffer);
        dataFormat = new String(buffer);
        filePointer +=4;

        buffer = new byte[6];
        source.read(buffer);   // skip over reserved field
        filePointer += 6;

        source.read(shortBuffer);
        dataReferenceIndex = ByteUtil.getInt16(shortBuffer, 0, true);

        source.read(shortBuffer);
        encoderVersion = ByteUtil.getInt16(shortBuffer, 0, true);

        source.read(shortBuffer);
        encoderRevision = ByteUtil.getInt16(shortBuffer, 0, true);
        filePointer += 6;

        buffer = new byte[4];
        source.read(buffer);
        encoderVendor = new String(buffer);

        source.read(buffer);
        temporalQuality = ByteUtil.getInt32(buffer, 0, true);

        source.read(buffer);
        spatialQuality = ByteUtil.getInt32(buffer, 0, true);
        filePointer += 12;

        source.read(shortBuffer);
        frameWidth = ByteUtil.getInt16(shortBuffer, 0, true);

        source.read(shortBuffer);
        frameHeight = ByteUtil.getInt16(shortBuffer, 0, true);
        filePointer += 4;

        source.read(buffer);
        resolutionX = ((long)ByteUtil.getUnsignedInt32(buffer, 0, true) >> 16);     // fixed point 16

        source.read(buffer);
        resolutionY = ((long)ByteUtil.getUnsignedInt32(buffer, 0, true) >> 16);     // fixed point 16


        source.read(buffer);
        dataSize = ByteUtil.getInt32(buffer, 0, true);
        filePointer +=12;

        source.read(shortBuffer);
        frameCount = ByteUtil.getInt16(shortBuffer, 0, true);
        filePointer += 2;

        int compressorNameLength = source.readByte();
        filePointer++;

        buffer = new byte[compressorNameLength];
        source.read(buffer);
        compressorName = new String(buffer);
        filePointer += compressorNameLength;

        source.reset();
        source.skip(filePointer - compressorNameLength + 31);  // compressor name is stored in a 32 bit pascal int, 1 byte size, 31 bytes data

        source.read(shortBuffer);
        pixelColorDepth = ByteUtil.getInt16(shortBuffer, 0, true);

        videoCodec = dataFormat;
        if (CodecTypes.videoCodecs.containsKey(dataFormat))
        {
            videoCodec = CodecTypes.videoCodecs.get(dataFormat);
        }
    }

    public void populateMetadata(Directory directory) throws MetadataException
    {
        directory.setInt(QtDirectory.TAG_SCREEN_WIDTH_PX, frameWidth);
        directory.setInt(QtDirectory.TAG_SCREEN_HEIGHT_PX, frameHeight);
        directory.setString(QtDirectory.TAG_VIDEO_CODEC, videoCodec);
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
            " Video codec: " + videoCodec +
            "\n temporalQuality: " + temporalQuality +
            "\n spatialQuality: " + spatialQuality +
            "\n frameWidth: " + frameWidth +
            "\n frameHeight: " + frameHeight +
            "\n resolutionX: " + resolutionX +
            "\n resolutionY: " + resolutionY +
            "\n Data size: " + dataSize +
            "\n Frame count: " + frameCount +
            "\n Compressor name: " + compressorName +
            "\n Pixel color depth: " + pixelColorDepth
        );
    }
}


package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.QtDictionary;
import com.drew.metadata.mov.media.QtVideoDirectory;

import java.io.IOException;
import java.util.ArrayList;

public class VideoSampleDescriptionAtom extends SampleDescriptionAtom<VideoSampleDescriptionAtom.VideoSampleDescription>
{
    public VideoSampleDescriptionAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);
    }

    @Override
    VideoSampleDescription getSampleDescription(SequentialReader reader) throws IOException
    {
        return new VideoSampleDescription(reader);
    }

    public void addMetadata(QtVideoDirectory directory)
    {
        VideoSampleDescription sampleDescription = sampleDescriptions.get(0);

        QtDictionary.setLookup(QtVideoDirectory.TAG_VENDOR, sampleDescription.vendor, directory);
        QtDictionary.setLookup(QtVideoDirectory.TAG_COMPRESSION_TYPE, sampleDescription.dataFormat, directory);

        directory.setLong(QtVideoDirectory.TAG_TEMPORAL_QUALITY, sampleDescription.temporalQuality);
        directory.setLong(QtVideoDirectory.TAG_SPATIAL_QUALITY, sampleDescription.spatialQuality);
        directory.setInt(QtVideoDirectory.TAG_WIDTH, sampleDescription.width);
        directory.setInt(QtVideoDirectory.TAG_HEIGHT, sampleDescription.height);
        directory.setString(QtVideoDirectory.TAG_COMPRESSOR_NAME, sampleDescription.compressorName.trim());

        directory.setInt(QtVideoDirectory.TAG_DEPTH, sampleDescription.depth);
        directory.setInt(QtVideoDirectory.TAG_COLOR_TABLE, sampleDescription.colorTableID);

        double horizontalInteger = (sampleDescription.horizontalResolution & 0xFFFF0000) >> 16;
        double horizontalFraction = (sampleDescription.horizontalResolution & 0xFFFF) / Math.pow(2, 4);
        directory.setDouble(QtVideoDirectory.TAG_HORIZONTAL_RESOLUTION, horizontalInteger + horizontalFraction);

        double verticalInteger = (sampleDescription.verticalResolution & 0xFFFF0000) >> 16;
        double verticalFraction = (sampleDescription.verticalResolution & 0xFFFF) / Math.pow(2, 4);
        directory.setDouble(QtVideoDirectory.TAG_VERTICAL_RESOLUTION, verticalInteger + verticalFraction);
    }

    class VideoSampleDescription extends SampleDescription
    {
        int version;
        int revisionLevel;
        String vendor;
        long temporalQuality;
        long spatialQuality;
        int width;
        int height;
        long horizontalResolution;
        long verticalResolution;
        long dataSize;
        int frameCount;
        String compressorName;
        int depth;
        int colorTableID;

        public VideoSampleDescription(SequentialReader reader) throws IOException
        {
            super(reader);

            version = reader.getUInt16();
            revisionLevel = reader.getUInt16();
            vendor = reader.getString(4);
            temporalQuality = reader.getUInt32();
            spatialQuality = reader.getUInt32();
            width = reader.getUInt16();
            height = reader.getUInt16();
            horizontalResolution = reader.getUInt32();
            verticalResolution = reader.getUInt32();
            dataSize = reader.getUInt32();
            frameCount = reader.getUInt16();
            compressorName = reader.getString(reader.getUInt8());
            depth = reader.getUInt16();
            colorTableID = reader.getInt16();
        }
    }
}

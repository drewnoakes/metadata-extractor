/*
 * Copyright 2002-2017 Drew Noakes
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.QuickTimeDictionary;
import com.drew.metadata.mov.media.QuickTimeVideoDirectory;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-BBCGICBJ
 *
 * @author Payton Garland
 */
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

    public void addMetadata(QuickTimeVideoDirectory directory)
    {
        VideoSampleDescription sampleDescription = sampleDescriptions.get(0);

        QuickTimeDictionary.setLookup(QuickTimeVideoDirectory.TAG_VENDOR, sampleDescription.vendor, directory);
        QuickTimeDictionary.setLookup(QuickTimeVideoDirectory.TAG_COMPRESSION_TYPE, sampleDescription.dataFormat, directory);

        directory.setLong(QuickTimeVideoDirectory.TAG_TEMPORAL_QUALITY, sampleDescription.temporalQuality);
        directory.setLong(QuickTimeVideoDirectory.TAG_SPATIAL_QUALITY, sampleDescription.spatialQuality);
        directory.setInt(QuickTimeVideoDirectory.TAG_WIDTH, sampleDescription.width);
        directory.setInt(QuickTimeVideoDirectory.TAG_HEIGHT, sampleDescription.height);
        directory.setString(QuickTimeVideoDirectory.TAG_COMPRESSOR_NAME, sampleDescription.compressorName.trim());

        directory.setInt(QuickTimeVideoDirectory.TAG_DEPTH, sampleDescription.depth);
        directory.setInt(QuickTimeVideoDirectory.TAG_COLOR_TABLE, sampleDescription.colorTableID);

        double horizontalInteger = (sampleDescription.horizontalResolution & 0xFFFF0000) >> 16;
        double horizontalFraction = (sampleDescription.horizontalResolution & 0xFFFF) / Math.pow(2, 4);
        directory.setDouble(QuickTimeVideoDirectory.TAG_HORIZONTAL_RESOLUTION, horizontalInteger + horizontalFraction);

        double verticalInteger = (sampleDescription.verticalResolution & 0xFFFF0000) >> 16;
        double verticalFraction = (sampleDescription.verticalResolution & 0xFFFF) / Math.pow(2, 4);
        directory.setDouble(QuickTimeVideoDirectory.TAG_VERTICAL_RESOLUTION, verticalInteger + verticalFraction);
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

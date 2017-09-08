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
package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;

import java.io.IOException;

/**
 * ISO/IED 14496-12:2015 pg.156
 */
public class VisualSampleEntry extends SampleEntry
{
    int version;
    int revisionLevel;
    String vendor;
    int temporalQuality;
    int spatialQuality;
    int width;
    int height;
    long horizresolution;
    long vertresolution;
    int frameCount;
    String compressorname;
    int depth;

    public VisualSampleEntry(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        version = reader.getInt16();
        revisionLevel = reader.getInt16();
        vendor = reader.getString(4);
        temporalQuality = reader.getInt32();
        spatialQuality = reader.getInt32();
        width = reader.getUInt16();
        height = reader.getUInt16();
        horizresolution = reader.getUInt32();
        vertresolution = reader.getUInt32();
        reader.skip(4); // Reserved
        frameCount = reader.getUInt16();
        compressorname = reader.getString(32);
        depth = reader.getUInt16();
        reader.skip(2); // Pre-defined
    }

    public void addMetadata(Mp4VideoDirectory directory)
    {
        directory.setInt(Mp4VideoDirectory.TAG_WIDTH, width);
        directory.setInt(Mp4VideoDirectory.TAG_HEIGHT, height);
        directory.setString(Mp4VideoDirectory.TAG_COMPRESSION_TYPE, compressorname.trim());
        directory.setInt(Mp4VideoDirectory.TAG_DEPTH, depth);

        // Calculate horizontal res
        double horizontalInteger = (horizresolution & 0xFFFF0000) >> 16;
        double horizontalFraction = (horizresolution & 0xFFFF) / Math.pow(2, 4);
        directory.setDouble(Mp4VideoDirectory.TAG_HORIZONTAL_RESOLUTION, horizontalInteger + horizontalFraction);

        double verticalInteger = (vertresolution & 0xFFFF0000) >> 16;
        double verticalFraction = (vertresolution & 0xFFFF) / Math.pow(2, 4);
        directory.setDouble(Mp4VideoDirectory.TAG_VERTICAL_RESOLUTION, verticalInteger + verticalFraction);
    }
}

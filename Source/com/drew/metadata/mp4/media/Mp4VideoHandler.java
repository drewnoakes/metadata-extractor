/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
package com.drew.metadata.mp4.media;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4BoxTypes;
import com.drew.metadata.mp4.Mp4Context;
import com.drew.metadata.mp4.Mp4Dictionary;
import com.drew.metadata.mp4.Mp4MediaHandler;

import java.io.IOException;
import java.util.ArrayList;

public class Mp4VideoHandler extends Mp4MediaHandler<Mp4VideoDirectory>
{
    public Mp4VideoHandler(Metadata metadata, Mp4Context context)
    {
        super(metadata, context);
    }

    @Override
    protected String getMediaInformation()
    {
        return Mp4BoxTypes.BOX_VIDEO_MEDIA_INFO;
    }

    @NotNull
    @Override
    protected Mp4VideoDirectory getDirectory()
    {
        return new Mp4VideoDirectory();
    }

    @Override
    public void processSampleDescription(@NotNull SequentialReader reader) throws IOException
    {
        // ISO/IED 14496-12:2015 pg.7

        reader.skip(4); // 1 byte header, 3 bytes flags

        // ISO/IED 14496-12:2015 pg.33

        long numberOfEntries = reader.getUInt32();
        long sampleDescriptionSize = reader.getUInt32();
        String format = reader.getString(4);
        reader.skip(6); // Reserved
        int dataReferenceIndex = reader.getUInt16();

        // ISO/IED 14496-12:2015 pg.156

        int version = reader.getInt16();
        int revisionLevel = reader.getInt16();
        String vendor = reader.getString(4);
        int temporalQuality = reader.getInt32();
        int spatialQuality = reader.getInt32();
        int width = reader.getUInt16();
        int height = reader.getUInt16();
        long horizresolution = reader.getUInt32();
        long vertresolution = reader.getUInt32();
        reader.skip(4); // Reserved
        int frameCount = reader.getUInt16();
        String compressorname = reader.getString(32);
        int depth = reader.getUInt16();
        reader.skip(2); // Pre-defined

        // TODO review this
        Mp4Dictionary.setLookup(Mp4VideoDirectory.TAG_COMPRESSION_TYPE, format, directory);

        directory.setInt(Mp4VideoDirectory.TAG_WIDTH, width);
        directory.setInt(Mp4VideoDirectory.TAG_HEIGHT, height);

        String compressorName = compressorname.trim();
        if (!compressorName.isEmpty()) {
            directory.setString(Mp4VideoDirectory.TAG_COMPRESSOR_NAME, compressorName);
        }

        directory.setInt(Mp4VideoDirectory.TAG_DEPTH, depth);

        // Calculate horizontal res
        double horizontalInteger = (horizresolution & 0xFFFF0000) >> 16;
        double horizontalFraction = (horizresolution & 0xFFFF) / Math.pow(2, 4);
        directory.setDouble(Mp4VideoDirectory.TAG_HORIZONTAL_RESOLUTION, horizontalInteger + horizontalFraction);

        double verticalInteger = (vertresolution & 0xFFFF0000) >> 16;
        double verticalFraction = (vertresolution & 0xFFFF) / Math.pow(2, 4);
        directory.setDouble(Mp4VideoDirectory.TAG_VERTICAL_RESOLUTION, verticalInteger + verticalFraction);
    }

    @Override
    public void processMediaInformation(@NotNull SequentialReader reader) throws IOException
    {
        // ISO/IED 14496-12:2015 pg.7

        reader.skip(4); // 1 byte header, 3 bytes flags

        // ISO/IED 14496-12:2015 pg.155

        int graphicsMode = reader.getUInt16();
        int[] opcolor = new int[]{reader.getUInt16(), reader.getUInt16(), reader.getUInt16()};

        directory.setIntArray(Mp4VideoDirectory.TAG_OPCOLOR, opcolor);
        directory.setInt(Mp4VideoDirectory.TAG_GRAPHICS_MODE, graphicsMode);
    }

    @Override
    public void processTimeToSample(@NotNull SequentialReader reader, Mp4Context context) throws IOException
    {
        // ISO/IED 14496-12:2015 pg.7

        reader.skip(4); // 1 byte header, 3 bytes flags

        // ISO/IED 14496-12:2015 pg.37

        float sampleCount = 0;

        long entryCount = reader.getUInt32();

        for (int i = 0; i < entryCount; i++) {
            sampleCount += reader.getUInt32();
            reader.skip(4); // sample delta
        }

        if (context.timeScale != null && context.duration != null) {
            float frameRate = (float)context.timeScale / ((float)context.duration / sampleCount);
            directory.setFloat(Mp4VideoDirectory.TAG_FRAME_RATE, frameRate);
        }
    }
}

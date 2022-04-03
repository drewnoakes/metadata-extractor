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
import com.drew.metadata.mp4.Mp4MediaHandler;

import java.io.IOException;

public class Mp4HintHandler extends Mp4MediaHandler<Mp4HintDirectory>
{
    public Mp4HintHandler(Metadata metadata, Mp4Context context)
    {
        super(metadata, context);
    }

    @NotNull
    @Override
    protected Mp4HintDirectory getDirectory()
    {
        return new Mp4HintDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        return Mp4BoxTypes.BOX_HINT_MEDIA_INFO;
    }

    @Override
    protected void processSampleDescription(@NotNull SequentialReader reader)
    {
    }

    @Override
    protected void processMediaInformation(@NotNull SequentialReader reader) throws IOException
    {
        // ISO/IED 14496-12:2015 pg.169

        int version = reader.getUInt8();
        byte[] flags = reader.getBytes(3);

        int maxPduSize = reader.getUInt16();
        int avgPduSize = reader.getUInt16();
        long maxBitrate = reader.getUInt32();
        long avgBitrate = reader.getUInt32();

        directory.setInt(Mp4HintDirectory.TAG_MAX_PDU_SIZE, maxPduSize);
        directory.setInt(Mp4HintDirectory.TAG_AVERAGE_PDU_SIZE, avgPduSize);
        directory.setLong(Mp4HintDirectory.TAG_MAX_BITRATE, maxBitrate);
        directory.setLong(Mp4HintDirectory.TAG_AVERAGE_BITRATE, avgBitrate);
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader, Mp4Context context) throws IOException
    {
    }
}

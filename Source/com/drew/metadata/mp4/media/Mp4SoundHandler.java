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

public class Mp4SoundHandler extends Mp4MediaHandler<Mp4SoundDirectory>
{
    public Mp4SoundHandler(Metadata metadata, Mp4Context context)
    {
        super(metadata, context);
    }

    @NotNull
    @Override
    protected Mp4SoundDirectory getDirectory()
    {
        return new Mp4SoundDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        return Mp4BoxTypes.BOX_SOUND_MEDIA_INFO;
    }

    @Override
    public void processSampleDescription(@NotNull SequentialReader reader) throws IOException
    {
        // ISO/IED 14496-12:2015 pg.7

        int version = reader.getUInt8();
        byte[] flags = reader.getBytes(3);

        // ISO/IED 14496-12:2015 pg.33

        long numberOfEntries = reader.getUInt32();
        long sampleDescriptionSize = reader.getUInt32();
        String format = reader.getString(4);
        reader.skip(6); // Reserved
        int dataReferenceIndex = reader.getUInt16();

        // ISO/IED 14496-12:2015 pg.161

        reader.skip(8); // Reserved
        int channelcount = reader.getUInt16();
        int samplesize = reader.getInt16();
        reader.skip(2); // Pre-defined
        reader.skip(2); // Reserved
        long samplerate = reader.getUInt32();
        // ChannelLayout()
        // DownMix and/or DRC boxes
        // More boxes as needed

        // TODO review this
        Mp4Dictionary.setLookup(Mp4SoundDirectory.TAG_AUDIO_FORMAT, format, directory);

        directory.setInt(Mp4SoundDirectory.TAG_NUMBER_OF_CHANNELS, channelcount);
        directory.setInt(Mp4SoundDirectory.TAG_AUDIO_SAMPLE_SIZE, samplesize);
    }

    @Override
    public void processMediaInformation(@NotNull SequentialReader reader) throws IOException
    {
        // ISO/IED 14496-12:2015 pg.7

        int version = reader.getUInt8();
        byte[] flags = reader.getBytes(3);

        // ISO/IED 14496-12:2015 pg.159

        int balance = reader.getInt16();
        reader.skip(2); // Reserved

        double integer = balance & 0xFFFF0000;
        double fraction = (balance & 0x0000FFFF) / Math.pow(2, 4);
        directory.setDouble(Mp4SoundDirectory.TAG_SOUND_BALANCE, integer + fraction);
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader, Mp4Context context) throws IOException
    {
        // ISO/IED 14496-12:2015 pg.7

        int version = reader.getUInt8();
        byte[] flags = reader.getBytes(3);

        // ISO/IED 14496-12:2015 pg.37

        long entryCount = reader.getUInt32();
        ArrayList<EntryCount> entries = new ArrayList<EntryCount>();
        for (int i = 0; i < entryCount; i++) {
            entries.add(new EntryCount(reader.getUInt32(), reader.getUInt32()));
        }

        if (context.timeScale != null) {
            directory.setDouble(Mp4SoundDirectory.TAG_AUDIO_SAMPLE_RATE, context.timeScale);
        }
    }

    static class EntryCount
    {
        long sampleCount;
        long sampleDelta;

        public EntryCount(long sampleCount, long sampleDelta)
        {
            this.sampleCount = sampleCount;
            this.sampleDelta = sampleDelta;
        }
    }
}

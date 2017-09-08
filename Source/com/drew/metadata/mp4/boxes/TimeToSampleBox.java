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
import com.drew.metadata.mp4.Mp4HandlerFactory;
import com.drew.metadata.mp4.media.Mp4SoundDirectory;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * ISO/IED 14496-12:2015 pg.37
 */
public class TimeToSampleBox extends FullBox
{
    long entryCount;
    ArrayList<EntryCount> entries;

    public TimeToSampleBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        entryCount = reader.getUInt32();
        entries = new ArrayList<EntryCount>();
        for (int i = 0; i < entryCount; i++) {
            entries.add(new EntryCount(reader.getUInt32(), reader.getUInt32()));
        }
    }

    public void addMetadata(Mp4VideoDirectory directory)
    {
        float frameRate = (float) Mp4HandlerFactory.HANDLER_PARAM_TIME_SCALE/(float)entries.get(0).sampleDelta;
        directory.setFloat(Mp4VideoDirectory.TAG_FRAME_RATE, frameRate);
    }

    public void addMetadata(Mp4SoundDirectory directory)
    {
        directory.setDouble(Mp4SoundDirectory.TAG_AUDIO_SAMPLE_RATE, Mp4HandlerFactory.HANDLER_PARAM_TIME_SCALE);
    }

    class EntryCount
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

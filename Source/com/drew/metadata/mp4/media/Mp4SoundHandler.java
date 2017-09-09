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
package com.drew.metadata.mp4.media;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4BoxTypes;
import com.drew.metadata.mp4.Mp4MediaHandler;
import com.drew.metadata.mp4.boxes.AudioSampleEntry;
import com.drew.metadata.mp4.boxes.Box;
import com.drew.metadata.mp4.boxes.SoundMediaHeaderBox;
import com.drew.metadata.mp4.boxes.TimeToSampleBox;

import java.io.IOException;

public class Mp4SoundHandler extends Mp4MediaHandler<Mp4SoundDirectory>
{
    public Mp4SoundHandler(Metadata metadata)
    {
        super(metadata);
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
    public void processSampleDescription(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {
        AudioSampleEntry audioSampleEntry = new AudioSampleEntry(reader, box);
        audioSampleEntry.addMetadata(directory);
    }

    @Override
    public void processMediaInformation(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {
        SoundMediaHeaderBox soundMediaHeaderBox = new SoundMediaHeaderBox(reader, box);
        soundMediaHeaderBox.addMetadata(directory);
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {
        TimeToSampleBox timeToSampleBox = new TimeToSampleBox(reader, box);
        timeToSampleBox.addMetadata(directory);
    }
}

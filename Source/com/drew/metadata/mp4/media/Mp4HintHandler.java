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
import com.drew.metadata.mp4.boxes.Box;
import com.drew.metadata.mp4.boxes.HintMediaHeaderBox;

import java.io.IOException;

public class Mp4HintHandler extends Mp4MediaHandler<Mp4HintDirectory>
{
    public Mp4HintHandler(Metadata metadata)
    {
        super(metadata);
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
    protected void processSampleDescription(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {

    }

    @Override
    protected void processMediaInformation(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {
        HintMediaHeaderBox hintMediaHeaderBox = new HintMediaHeaderBox(reader, box);
        hintMediaHeaderBox.addMetadata(directory);
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {

    }
}

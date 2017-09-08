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
package com.drew.metadata.mp4;

import com.drew.imaging.mp4.Mp4Handler;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.boxes.Box;
import com.drew.metadata.mp4.media.Mp4MediaDirectory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public abstract class Mp4MediaHandler<T extends Mp4MediaDirectory> extends Mp4Handler<T>
{
    public Mp4MediaHandler(Metadata metadata)
    {
        super(metadata);
        if (Mp4HandlerFactory.HANDLER_PARAM_CREATION_TIME != null && Mp4HandlerFactory.HANDLER_PARAM_MODIFICATION_TIME != null) {
            // Get creation/modification times
            Calendar calendar = Calendar.getInstance();
            calendar.set(1904, 0, 1, 0, 0, 0);      // January 1, 1904  -  Macintosh Time Epoch
            Date date = calendar.getTime();
            long macToUnixEpochOffset = date.getTime();
            String creationTimeStamp = new Date(Mp4HandlerFactory.HANDLER_PARAM_CREATION_TIME * 1000 + macToUnixEpochOffset).toString();
            String modificationTimeStamp = new Date(Mp4HandlerFactory.HANDLER_PARAM_MODIFICATION_TIME * 1000 + macToUnixEpochOffset).toString();
            String language = Mp4HandlerFactory.HANDLER_PARAM_LANGUAGE;
            directory.setString(Mp4MediaDirectory.TAG_CREATION_TIME, creationTimeStamp);
            directory.setString(Mp4MediaDirectory.TAG_MODIFICATION_TIME, modificationTimeStamp);
            directory.setString(Mp4MediaDirectory.TAG_LANGUAGE_CODE, language);
        }
    }

    @Override
    public boolean shouldAcceptBox(@NotNull Box box)
    {
        return box.type.equals(getMediaInformation())
            || box.type.equals(Mp4BoxTypes.BOX_SAMPLE_DESCRIPTION)
            || box.type.equals(Mp4BoxTypes.BOX_TIME_TO_SAMPLE);
    }

    @Override
    public boolean shouldAcceptContainer(@NotNull Box box)
    {
        return box.type.equals(Mp4ContainerTypes.BOX_SAMPLE_TABLE)
            || box.type.equals(Mp4ContainerTypes.BOX_MEDIA_INFORMATION);
    }

    @Override
    public Mp4Handler processBox(@NotNull Box box, @Nullable byte[] payload) throws IOException
    {
        if (payload != null) {
            SequentialReader reader = new SequentialByteArrayReader(payload);
            if (box.type.equals(getMediaInformation())) {
                processMediaInformation(reader, box);
            } else if (box.type.equals(Mp4BoxTypes.BOX_SAMPLE_DESCRIPTION)) {
                processSampleDescription(reader, box);
            } else if (box.type.equals(Mp4BoxTypes.BOX_TIME_TO_SAMPLE)) {
                processTimeToSample(reader, box);
            }
        }
        return this;
    }

    protected abstract String getMediaInformation();

    protected abstract void processSampleDescription(@NotNull SequentialReader reader, @NotNull Box box) throws IOException;

    protected abstract void processMediaInformation(@NotNull SequentialReader reader, @NotNull Box box) throws IOException;

    protected abstract void processTimeToSample(@NotNull SequentialReader reader, @NotNull Box box) throws IOException;
}

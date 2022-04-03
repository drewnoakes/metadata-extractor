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
package com.drew.metadata.mp4;

import com.drew.imaging.mp4.Mp4Handler;
import com.drew.lang.DateUtil;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.media.Mp4MediaDirectory;

import java.io.IOException;

public abstract class Mp4MediaHandler<T extends Mp4MediaDirectory> extends Mp4Handler<T>
{
    public Mp4MediaHandler(Metadata metadata, Mp4Context context)
    {
        super(metadata);

        // Get creation/modification times
        if (context.creationTime != null) {
            directory.setDate(
                Mp4MediaDirectory.TAG_CREATION_TIME,
                DateUtil.get1Jan1904EpochDate(context.creationTime)
            );
        }

        if (context.modificationTime != null) {
            directory.setDate(
                Mp4MediaDirectory.TAG_MODIFICATION_TIME,
                DateUtil.get1Jan1904EpochDate(context.modificationTime)
            );
        }

        if (context.language != null) {
            directory.setString(Mp4MediaDirectory.TAG_LANGUAGE_CODE, context.language);
        }
    }

    @Override
    public boolean shouldAcceptBox(@NotNull String type)
    {
        return type.equals(getMediaInformation())
            || type.equals(Mp4BoxTypes.BOX_SAMPLE_DESCRIPTION)
            || type.equals(Mp4BoxTypes.BOX_TIME_TO_SAMPLE);
    }

    @Override
    public boolean shouldAcceptContainer(@NotNull String type)
    {
        return type.equals(Mp4ContainerTypes.BOX_SAMPLE_TABLE)
            || type.equals(Mp4ContainerTypes.BOX_MEDIA_INFORMATION);
    }

    @Override
    public Mp4Handler<?> processBox(@NotNull String type, @Nullable byte[] payload, long boxSize, Mp4Context context) throws IOException
    {
        if (payload != null) {
            SequentialReader reader = new SequentialByteArrayReader(payload);
            if (type.equals(getMediaInformation())) {
                processMediaInformation(reader);
            } else if (type.equals(Mp4BoxTypes.BOX_SAMPLE_DESCRIPTION)) {
                processSampleDescription(reader);
            } else if (type.equals(Mp4BoxTypes.BOX_TIME_TO_SAMPLE)) {
                processTimeToSample(reader, context);
            }
        }
        return this;
    }

    protected abstract String getMediaInformation();

    protected abstract void processSampleDescription(@NotNull SequentialReader reader) throws IOException;

    protected abstract void processMediaInformation(@NotNull SequentialReader reader) throws IOException;

    protected abstract void processTimeToSample(@NotNull SequentialReader reader, Mp4Context context) throws IOException;
}

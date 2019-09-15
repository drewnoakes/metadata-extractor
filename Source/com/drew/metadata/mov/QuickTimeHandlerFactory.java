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
package com.drew.metadata.mov;

import com.drew.imaging.quicktime.QuickTimeHandler;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.media.*;
import com.drew.metadata.mov.metadata.QuickTimeDataHandler;
import com.drew.metadata.mov.metadata.QuickTimeDirectoryHandler;

/**
 * @author Payton Garland
 */
public class QuickTimeHandlerFactory
{
    private static final String HANDLER_METADATA_DIRECTORY      = "mdir";
    private static final String HANDLER_METADATA_DATA           = "mdta";
    private static final String HANDLER_SOUND_MEDIA             = "soun";
    private static final String HANDLER_VIDEO_MEDIA             = "vide";
    private static final String HANDLER_TIMECODE_MEDIA          = "tmcd";
    private static final String HANDLER_TEXT_MEDIA              = "text";
    private static final String HANDLER_SUBTITLE_MEDIA          = "sbtl";
    private static final String HANDLER_MUSIC_MEDIA             = "musi";

    private QuickTimeHandler caller;

    public QuickTimeHandlerFactory(QuickTimeHandler caller)
    {
        this.caller = caller;
    }

    public QuickTimeHandler getHandler(String type, Metadata metadata, QuickTimeContext context)
    {
        if (type.equals(HANDLER_METADATA_DIRECTORY)) {
            return new QuickTimeDirectoryHandler(metadata);
        } else if (type.equals(HANDLER_METADATA_DATA)) {
            return new QuickTimeDataHandler(metadata);
        } else if (type.equals(HANDLER_SOUND_MEDIA)) {
            return new QuickTimeSoundHandler(metadata, context);
        } else if (type.equals(HANDLER_VIDEO_MEDIA)) {
            return new QuickTimeVideoHandler(metadata, context);
        } else if (type.equals(HANDLER_TIMECODE_MEDIA)) {
            return new QuickTimeTimecodeHandler(metadata, context);
        } else if (type.equals(HANDLER_TEXT_MEDIA)) {
            return new QuickTimeTextHandler(metadata, context);
        } else if (type.equals(HANDLER_SUBTITLE_MEDIA)) {
            return new QuickTimeSubtitleHandler(metadata, context);
        } else if (type.equals(HANDLER_MUSIC_MEDIA)) {
            return new QuickTimeMusicHandler(metadata, context);
        }
        return caller;
    }
}

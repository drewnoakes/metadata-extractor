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
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.boxes.HandlerBox;
import com.drew.metadata.mp4.media.*;

public class Mp4HandlerFactory
{
    private static final String HANDLER_SOUND_MEDIA             = "soun";
    private static final String HANDLER_VIDEO_MEDIA             = "vide";
    private static final String HANDLER_HINT_MEDIA              = "hint";
    private static final String HANDLER_TEXT_MEDIA              = "text";
    private static final String HANDLER_META_MEDIA              = "meta";

    private Mp4Handler caller;

    public static Long HANDLER_PARAM_TIME_SCALE              = null;
    public static Long HANDLER_PARAM_CREATION_TIME           = null;
    public static Long HANDLER_PARAM_MODIFICATION_TIME       = null;
    public static Long HANDLER_PARAM_DURATION                = null;
    public static String HANDLER_PARAM_LANGUAGE              = null;

    public Mp4HandlerFactory(Mp4Handler caller)
    {
        this.caller = caller;
    }

    public Mp4Handler getHandler(HandlerBox box, Metadata metadata)
    {
        String type = box.getHandlerType();
        if (type.equals(HANDLER_SOUND_MEDIA)) {
            return new Mp4SoundHandler(metadata);
        } else if (type.equals(HANDLER_VIDEO_MEDIA)) {
            return new Mp4VideoHandler(metadata);
        } else if (type.equals(HANDLER_HINT_MEDIA)) {
            return new Mp4HintHandler(metadata);
        } else if (type.equals(HANDLER_TEXT_MEDIA)) {
            return new Mp4TextHandler(metadata);
        } else if (type.equals(HANDLER_META_MEDIA)) {
            return new Mp4MetaHandler(metadata);
        }
        return caller;
    }
}

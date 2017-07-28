package com.drew.metadata.mov;

import com.drew.imaging.quicktime.QtHandler;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.media.*;
import com.drew.metadata.mov.metadata.QtDataHandler;
import com.drew.metadata.mov.metadata.QtDirectoryHandler;

public class QtHandlerFactory
{
    private static final String HANDLER_METADATA_DIRECTORY      = "mdir";
    private static final String HANDLER_METADATA_DATA           = "mdta";
    private static final String HANDLER_SOUND_MEDIA             = "soun";
    private static final String HANDLER_VIDEO_MEDIA             = "vide";
    private static final String HANDLER_TIMECODE_MEDIA          = "tmcd";
    private static final String HANDLER_TEXT_MEDIA              = "text";
    private static final String HANDLER_SUBTITLE_MEDIA          = "sbtl";
    private static final String HANDLER_MUSIC_MEDIA             = "musi";

    private QtHandler caller;

    public static Integer HANDLER_PARAM_TIME_SCALE              = null;

    public QtHandlerFactory(QtHandler caller)
    {
        this.caller = caller;
    }

    public QtHandler getHandler(String type, Metadata metadata)
    {
        if (type.equals(HANDLER_METADATA_DIRECTORY)) {
            return new QtDirectoryHandler(metadata);
        } else if (type.equals(HANDLER_METADATA_DATA)) {
            return new QtDataHandler(metadata);
        } else if (type.equals(HANDLER_SOUND_MEDIA)) {
            return new QtSoundHandler(metadata);
        } else if (type.equals(HANDLER_VIDEO_MEDIA)) {
            return new QtVideoHandler(metadata);
        } else if (type.equals(HANDLER_TIMECODE_MEDIA)) {
            return new QtTimecodeHandler(metadata);
        } else if (type.equals(HANDLER_TEXT_MEDIA)) {
            return new QtTextHandler(metadata);
        } else if (type.equals(HANDLER_SUBTITLE_MEDIA)) {
            return new QtSubtitleHandler(metadata);
        } else if (type.equals(HANDLER_MUSIC_MEDIA)) {
            return new QtMusicHandler(metadata);
        }
        return caller;
    }
}

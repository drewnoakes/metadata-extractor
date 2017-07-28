package com.drew.metadata.mp4;

import com.drew.imaging.quicktime.QtHandler;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.media.Mp4SoundHandler;
import com.drew.metadata.mp4.media.Mp4VideoHandler;

public class Mp4HandlerFactory
{
    private static final String HANDLER_SOUND_MEDIA             = "soun";
    private static final String HANDLER_VIDEO_MEDIA             = "vide";

    private QtHandler caller;

    public static Integer HANDLER_PARAM_TIME_SCALE              = null;

    public Mp4HandlerFactory(QtHandler caller)
    {
        this.caller = caller;
    }

    public QtHandler getHandler(String type, Metadata metadata)
    {
        if (type.equals(HANDLER_SOUND_MEDIA)) {
            return new Mp4SoundHandler(metadata);
        } else if (type.equals(HANDLER_VIDEO_MEDIA)) {
            return new Mp4VideoHandler(metadata);
        }
        return caller;
    }
}

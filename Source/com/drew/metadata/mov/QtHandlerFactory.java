package com.drew.metadata.mov;

public class QtHandlerFactory
{
    private static final String HANDLER_METADATA_DIRECTORY      = "mdir";
    private static final String HANDLER_METADATA_DATA           = "mdta";
    private static final String HANDLER_SOUND_MEDIA             = "soun";
    private static final String HANDLER_VIDEO_MEDIA             = "vide";
    private static final String HANDLER_TIMECODE_MEDIA          = "tmcd";
    private static final String HANDLER_TEXT_MEDIA              = "text";

    private QtHandler caller;

    public static Integer HANDLER_PARAM_TIME_SCALE              = null;

    public QtHandlerFactory(QtHandler caller)
    {
        this.caller = caller;
    }

    public QtHandler getHandler(String type)
    {
        if (type.equals(HANDLER_METADATA_DIRECTORY)) {
            return new QtMetadataDirectoryHandler();
        } else if (type.equals(HANDLER_METADATA_DATA)) {
            return new QtMetadataDataHandler();
        } else if (type.equals(HANDLER_SOUND_MEDIA)) {
            return new QtMediaSoundHandler();
        } else if (type.equals(HANDLER_VIDEO_MEDIA)) {
            return new QtMediaVideoHandler();
        } else if (type.equals(HANDLER_TIMECODE_MEDIA)) {
            return new QtMediaTimecodeHandler();
        } else if (type.equals(HANDLER_TEXT_MEDIA)) {
            return new QtMediaTextHandler();
        }
        return caller;
    }
}

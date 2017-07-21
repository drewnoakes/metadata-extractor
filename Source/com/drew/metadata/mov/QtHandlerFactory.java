package com.drew.metadata.mov;

public class QtHandlerFactory
{
    private static final String HANDLER_METADATA_DIRECTORY = "mdir";
    private static final String HANDLER_METADATA_DATA = "mdta";
    private static final String HANDLER_SOUND_MEDIA = "soun";
    private static final String HANDLER_VIDEO_MEDIA = "vide";

    private QtHandler caller;

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
            return new QtSoundMediaHandler();
        } else if (type.equals(HANDLER_VIDEO_MEDIA)) {
            return new QtVideoMediaHandler();
        }
        return caller;
    }
}

package com.drew.metadata.heif;

import com.drew.imaging.heif.HeifHandler;
import com.drew.metadata.Metadata;
import com.drew.metadata.heif.boxes.HandlerBox;

public class HeifHandlerFactory
{
    private static final String HANDLER_PICTURE             = "pict";

    private HeifHandler caller;

    public HeifHandlerFactory(HeifHandler caller)
    {
        this.caller = caller;
    }

    public HeifHandler getHandler(HandlerBox box, Metadata metadata)
    {
        String type = box.getHandlerType();
        if (type.equals(HANDLER_PICTURE)) {
            return new HeifPictureHandler(metadata);
        }
        return caller;
    }
}

package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mp4.Mp4HandlerFactory;

import java.io.IOException;

public class BoxHeaderMedia extends BoxFull
{
    long creationTime;
    long modificationTime;
    long timescale;
    long duration;
    String language;

    public BoxHeaderMedia(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        if (version == 1) {
            creationTime = reader.getInt64();
            modificationTime = reader.getInt64();
            timescale = reader.getInt32();
            duration = reader.getInt64();
        } else {
            creationTime = reader.getUInt32();
            modificationTime = reader.getUInt32();
            timescale = reader.getUInt32();
            duration = reader.getUInt32();
        }
        int languageBits = reader.getInt16();
        language = new String(new char[]{(char)(((languageBits & 0x7C00) >> 10) + 0x60),
            (char)(((languageBits & 0x03E0) >> 5) + 0x60),
            (char)((languageBits & 0x001F) + 0x60)});

        Mp4HandlerFactory.HANDLER_PARAM_CREATION_TIME = creationTime;
        Mp4HandlerFactory.HANDLER_PARAM_MODIFICATION_TIME = modificationTime;
        Mp4HandlerFactory.HANDLER_PARAM_TIME_SCALE = timescale;
        Mp4HandlerFactory.HANDLER_PARAM_DURATION = duration;
        Mp4HandlerFactory.HANDLER_PARAM_LANGUAGE = language;
    }
}

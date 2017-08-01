package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;

import java.io.IOException;
import java.nio.charset.Charset;

public class HandlerBox extends FullBox
{
    String handlerType;

    public String getHandlerType()
    {
        return handlerType;
    }

    String name;

    public HandlerBox(SequentialReader reader, long size) throws IOException
    {
        super(reader);

        reader.skip(4); // Pre-defined
        handlerType = reader.getString(4);
        reader.skip(12); // Reserved
        name = reader.getNullTerminatedString((int)size, Charset.defaultCharset());
    }
}

package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * ISO/IED 14496-12:2015 pg.30
 */
public class HandlerBox extends FullBox
{
    String handlerType;

    public String getHandlerType()
    {
        return handlerType;
    }

    String name;

    public HandlerBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        reader.skip(4); // Pre-defined
        handlerType = reader.getString(4);
        reader.skip(12); // Reserved
        name = reader.getNullTerminatedString((int)size, Charset.defaultCharset());
    }
}

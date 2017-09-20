package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialReader;

import java.io.IOException;

/**
 * @author Payton Garland
 */
public class MetaBox extends FullBox
{
    HandlerBox handlerBox;

    public MetaBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        handlerBox = new HandlerBox(reader, box);
    }
}

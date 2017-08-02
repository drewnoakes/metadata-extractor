package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;

import java.io.IOException;

public class BoxFull extends Box
{
    protected int version;
    protected byte[] flags;

    public BoxFull(SequentialReader reader, Box box) throws IOException
    {
        super(box);

        version = reader.getUInt8();
        flags = reader.getBytes(3);
    }
}

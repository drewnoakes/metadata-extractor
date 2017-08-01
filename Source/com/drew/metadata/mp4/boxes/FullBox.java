package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;

import java.io.IOException;

public class FullBox
{
    protected int version;
    protected byte[] flags;

    public FullBox(SequentialReader reader) throws IOException
    {
        version = reader.getUInt8();
        flags = reader.getBytes(3);
    }
}

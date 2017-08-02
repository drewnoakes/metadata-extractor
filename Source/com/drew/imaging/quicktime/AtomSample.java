package com.drew.imaging.quicktime;

import com.drew.lang.SequentialReader;
import com.drew.metadata.Directory;

import java.io.IOException;

public abstract class AtomSample
{
    protected long size;
    protected String type;

    public AtomSample(SequentialReader reader) throws IOException
    {
        this.size = reader.getUInt32();
        this.type = reader.getString(4);
    }

    public AtomSample(AtomSample atomSample)
    {
        this.size = atomSample.size;
        this.type = atomSample.type;
    }
}

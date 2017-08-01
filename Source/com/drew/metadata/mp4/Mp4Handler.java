package com.drew.metadata.mp4;

import com.drew.imaging.quicktime.QtHandlerSample;
import com.drew.lang.SequentialReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.boxes.Box;

import java.io.IOException;

public abstract class Mp4Handler<T extends Directory> extends QtHandlerSample<T, Box>
{
    public Mp4Handler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected Box getAtom(SequentialReader reader) throws IOException
    {
        baseAtom = new Box(reader);
        return baseAtom;
    }
}

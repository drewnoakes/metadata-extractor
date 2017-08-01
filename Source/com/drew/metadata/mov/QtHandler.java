package com.drew.metadata.mov;

import com.drew.imaging.quicktime.QtHandlerSample;
import com.drew.lang.SequentialReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtDirectory;
import com.drew.metadata.mov.atoms.Atom;

import java.io.IOException;

public abstract class QtHandler<T extends QtDirectory> extends QtHandlerSample<T, Atom>
{
    public QtHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected Atom getAtom(SequentialReader reader) throws IOException
    {
        baseAtom = new Atom(reader);
        return baseAtom;
    }
}

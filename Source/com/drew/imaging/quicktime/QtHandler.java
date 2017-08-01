package com.drew.imaging.quicktime;

import com.drew.lang.SequentialReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtDirectory;
import com.drew.metadata.mov.atoms.Atom;

import java.io.IOException;

public abstract class QtHandler extends QtHandlerSample<QtDirectory, Atom>
{
    public QtHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected Atom getAtom(SequentialReader reader) throws IOException
    {
        return new Atom(reader);
    }
}

package com.drew.metadata.mov.atoms;

import com.drew.imaging.quicktime.AtomSample;
import com.drew.lang.SequentialReader;

import java.io.IOException;

public class Atom extends AtomSample
{
    public Atom(SequentialReader reader) throws IOException
    {
        super(reader);

        if (size == 1) {
            size = reader.getInt64();
        } else if (size == 0) {
            size = -1;
        }
    }

    public Atom(Atom atom)
    {
        super(atom);
    }
}

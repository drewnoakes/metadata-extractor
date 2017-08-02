package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;

import java.io.IOException;

public class FullAtom extends Atom
{
    int version;
    byte[] flags;

    public FullAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(atom);

        version = reader.getUInt8();
        flags = reader.getBytes(3);
    }
}

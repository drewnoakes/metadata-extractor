package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap1/qtff1.html#//apple_ref/doc/uid/TP40000939-CH203-38195
 */
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

package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.QuickTimeDirectory;

import java.io.IOException;

public class XmpAtom extends Atom
{
    private String xmp;

    public XmpAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(atom);

        xmp = reader.getString(reader.available());
    }

    public void addMetadata(QuickTimeDirectory directory)
    {
        directory.setString(QuickTimeDirectory.TAG_ADOBE_XMP, xmp);
    }
}

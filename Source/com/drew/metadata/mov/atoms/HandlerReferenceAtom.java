package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;

import java.io.IOException;

public class HandlerReferenceAtom extends FullAtom
{
    public String getComponentType()
    {
        return componentType;
    }

    String componentType;
    String componentSubtype;
    String componentName;

    public HandlerReferenceAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);

        componentType = reader.getString(4);
        componentSubtype = reader.getString(4);
        reader.skip(4); // Reserved
        reader.skip(4); // Reserved
        reader.skip(4); // Reserved
        componentName = reader.getString(reader.getUInt8());
    }
}

package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-BBCIBHFD
 */
public class HandlerReferenceAtom extends FullAtom
{
    public String getComponentType()
    {
        return componentSubtype;
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

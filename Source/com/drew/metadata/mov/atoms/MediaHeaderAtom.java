package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.QtHandlerFactory;

import java.io.IOException;

/**
 * Extracts data from the 'moov' atom's movie header marked by the fourCC 'mvhd'
 * Index 0 is after size and type
 *
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-32947
 */
public class MediaHeaderAtom extends FullAtom
{
    long creationTime;
    long modificationTime;
    long timescale;
    long duration;
    int language;
    int quality;

    public MediaHeaderAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);

        creationTime = reader.getUInt32();
        modificationTime = reader.getUInt32();
        timescale = reader.getUInt32();
        duration = reader.getUInt32();
        language = reader.getUInt16();
        quality = reader.getUInt16();

        QtHandlerFactory.HANDLER_PARAM_CREATION_TIME = creationTime;
        QtHandlerFactory.HANDLER_PARAM_MODIFICATION_TIME = modificationTime;
        QtHandlerFactory.HANDLER_PARAM_TIME_SCALE = timescale;
        QtHandlerFactory.HANDLER_PARAM_DURATION = duration;
    }
}

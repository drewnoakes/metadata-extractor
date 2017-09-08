package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-BBCHHGBH
 */
public class SampleDescription
{
    long sampleDescriptionSize;
    String dataFormat;
    int dataReferenceIndex;

    public SampleDescription(SequentialReader reader) throws IOException
    {
        sampleDescriptionSize = reader.getUInt32();
        dataFormat = reader.getString(4);
        reader.skip(6); // Reserved
        dataReferenceIndex = reader.getUInt16();
    }
}

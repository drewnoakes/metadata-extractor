package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.media.QtTimecodeDirectory;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-57409
 */
public class TimecodeSampleDescriptionAtom extends SampleDescriptionAtom<TimecodeSampleDescriptionAtom.TimecodeSampleDescription>
{
    public TimecodeSampleDescriptionAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);
    }

    @Override
    TimecodeSampleDescription getSampleDescription(SequentialReader reader) throws IOException
    {
        return new TimecodeSampleDescription(reader);
    }

    public void addMetadata(QtTimecodeDirectory directory)
    {
        TimecodeSampleDescription description = sampleDescriptions.get(0);

        directory.setBoolean(QtTimecodeDirectory.TAG_DROP_FRAME, ((description.flags & 0x0001) == 0x0001) ? true : false);
        directory.setBoolean(QtTimecodeDirectory.TAG_24_HOUR_MAX, ((description.flags & 0x0002) == 0x0002) ? true : false);
        directory.setBoolean(QtTimecodeDirectory.TAG_NEGATIVE_TIMES_OK, ((description.flags & 0x0004) == 0x0004) ? true : false);
        directory.setBoolean(QtTimecodeDirectory.TAG_COUNTER, ((description.flags & 0x0008) == 0x0008) ? true : false);
    }

    class TimecodeSampleDescription extends SampleDescription
    {
        int flags;
        int timeScale;
        int frameDuration;
        int numberOfFrames;

        public TimecodeSampleDescription(SequentialReader reader) throws IOException
        {
            super(reader);

            reader.skip(4); // Reserved
            flags = reader.getInt32();
            timeScale = reader.getInt32();
            frameDuration = reader.getInt32();
            numberOfFrames = reader.getInt8();
            reader.skip(1); // Reserved
            // Source reference...
        }
    }
}

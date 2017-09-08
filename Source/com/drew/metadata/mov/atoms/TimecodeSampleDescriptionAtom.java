/*
 * Copyright 2002-2017 Drew Noakes
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.media.QuickTimeTimecodeDirectory;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-57409
 *
 * @author Payton Garland
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

    public void addMetadata(QuickTimeTimecodeDirectory directory)
    {
        TimecodeSampleDescription description = sampleDescriptions.get(0);

        directory.setBoolean(QuickTimeTimecodeDirectory.TAG_DROP_FRAME,        (description.flags & 0x0001) == 0x0001);
        directory.setBoolean(QuickTimeTimecodeDirectory.TAG_24_HOUR_MAX,       (description.flags & 0x0002) == 0x0002);
        directory.setBoolean(QuickTimeTimecodeDirectory.TAG_NEGATIVE_TIMES_OK, (description.flags & 0x0004) == 0x0004);
        directory.setBoolean(QuickTimeTimecodeDirectory.TAG_COUNTER,           (description.flags & 0x0008) == 0x0008);
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

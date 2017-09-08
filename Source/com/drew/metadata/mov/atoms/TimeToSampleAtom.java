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
import com.drew.metadata.mov.QuickTimeHandlerFactory;
import com.drew.metadata.mov.media.QuickTimeVideoDirectory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-BBCGFJII
 *
 * @author Payton Garland
 */
public class TimeToSampleAtom extends FullAtom
{
    long numberOfEntries;
    ArrayList<Entry> entries;
    long sampleCount;
    long sampleDuration;

    public TimeToSampleAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);

        numberOfEntries = reader.getUInt32();
        entries = new ArrayList<Entry>();
        for (int i = 0; i < numberOfEntries; i++) {
            entries.add(new Entry(reader));
        }
        sampleCount = reader.getUInt32();
        sampleDuration = reader.getUInt32();
    }

    class Entry
    {
        long sampleCount;
        long sampleDuration;

        public Entry(SequentialReader reader) throws IOException
        {
            sampleCount = reader.getUInt32();
            sampleDuration = reader.getUInt32();
        }
    }

    public void addMetadata(QuickTimeVideoDirectory directory)
    {
        float frameRate = (float) QuickTimeHandlerFactory.HANDLER_PARAM_TIME_SCALE/(float)sampleDuration;
        directory.setFloat(QuickTimeVideoDirectory.TAG_FRAME_RATE, frameRate);
    }
}

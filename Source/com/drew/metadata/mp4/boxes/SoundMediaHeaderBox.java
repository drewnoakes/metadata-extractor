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
package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mp4.media.Mp4SoundDirectory;

import java.io.IOException;

/**
 * ISO/IED 14496-12:2015 pg.159
 */
public class SoundMediaHeaderBox extends FullBox
{
    int balance;

    public SoundMediaHeaderBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        balance = reader.getInt16();
        reader.skip(2); // Reserved
    }

    public void addMetadata(Mp4SoundDirectory directory)
    {
        double integer = balance & 0xFFFF0000;
        double fraction = (balance & 0x0000FFFF) / Math.pow(2, 4);
        directory.setDouble(Mp4SoundDirectory.TAG_SOUND_BALANCE, integer + fraction);
    }
}

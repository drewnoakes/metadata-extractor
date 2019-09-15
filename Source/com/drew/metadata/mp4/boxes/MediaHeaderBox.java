/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
import com.drew.metadata.mp4.Mp4Context;
import com.drew.metadata.mp4.Mp4HandlerFactory;

import java.io.IOException;

/**
 * ISO/IED 14496-12:2015 pg.29
 */
public class MediaHeaderBox extends FullBox
{
    public MediaHeaderBox(SequentialReader reader, Box box, Mp4Context context) throws IOException
    {
        super(reader, box);

        if (version == 1) {
            context.creationTime = reader.getInt64();
            context.modificationTime = reader.getInt64();
            context.timeScale = (long)reader.getInt32();
            context.duration = reader.getInt64();
        } else {
            context.creationTime = reader.getUInt32();
            context.modificationTime = reader.getUInt32();
            context.timeScale = reader.getUInt32();
            context.duration = reader.getUInt32();
        }

        int languageBits = reader.getInt16();

        context.language = new String(new char[]
            {
                (char)(((languageBits & 0x7C00) >> 10) + 0x60),
                (char)(((languageBits & 0x03E0) >> 5) + 0x60),
                (char)((languageBits & 0x001F) + 0x60)
            });
    }
}

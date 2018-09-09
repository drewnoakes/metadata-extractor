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

import com.drew.lang.Charsets;
import com.drew.lang.ReaderInfo;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * ISO/IED 14496-12:2015 pg.30
 */
public class HandlerBox extends FullBox
{
    String handlerType;

    public String getHandlerType()
    {
        return handlerType;
    }

    String name;

    public HandlerBox(ReaderInfo reader, Box box) throws IOException
    {
        super(reader, box);

        reader.skip(4); // Pre-defined
        handlerType = reader.getString(4, Charsets.UTF_8);
        reader.skip(12); // Reserved
        name = reader.getNullTerminatedString((int)size - 32, Charset.defaultCharset());
    }
}

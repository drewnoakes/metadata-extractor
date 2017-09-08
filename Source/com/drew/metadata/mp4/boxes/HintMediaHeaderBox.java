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
import com.drew.metadata.mp4.media.Mp4HintDirectory;

import java.io.IOException;

/**
 * ISO/IED 14496-12:2015 pg.169
 */
public class HintMediaHeaderBox extends FullBox
{
    int maxPDUsize;
    int avgPDUsize;
    long maxbitrate;
    long avgbitrate;

    public HintMediaHeaderBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        maxPDUsize = reader.getUInt16();
        avgPDUsize = reader.getUInt16();
        maxbitrate = reader.getUInt32();
        avgbitrate = reader.getUInt32();
    }

    public void addMetadata(Mp4HintDirectory directory)
    {
        directory.setInt(Mp4HintDirectory.TAG_MAX_PDU_SIZE, maxPDUsize);
        directory.setInt(Mp4HintDirectory.TAG_AVERAGE_PDU_SIZE, avgPDUsize);
        directory.setLong(Mp4HintDirectory.TAG_MAX_BITRATE, maxbitrate);
        directory.setLong(Mp4HintDirectory.TAG_AVERAGE_BITRATE, avgbitrate);
    }
}

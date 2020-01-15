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
package com.drew.imaging.mp3;

import com.drew.imaging.FileType;
import com.drew.imaging.TypeChecker;

public class MpegAudioTypeChecker implements TypeChecker
{
    /*
        MPEG Audio http://www.mp3-tech.org/programmer/frame_header.html

        Bits
        11  Frame sync (all bits must be set)
        2   MPEG Audio version ID
            00 - MPEG Version 2.5 (later extension of MPEG 2)
            01 - Reserved
            10 - MPEG Version 2 (ISO/IEC 13818-3)
            11 - MPEG Version 1 (ISO/IEC 11172-3)
        2   Layer description
            00 - Reserved
            01 - Layer III
            10 - Layer II
            11 - Layer I

        Additional bits contain more information, but are not required for file type identification.
     */
    @Override
    public int getByteCount()
    {
        return 3;
    }

    @Override
    public FileType checkType(byte[] bytes)
    {
        // MPEG audio requires the first 11 bits to be set
        if (bytes[0] != (byte)0xFF || (bytes[1] & 0xE0) != 0xE0)
            return FileType.Unknown;

        // The MPEG Audio version ID value of 01 is reserved
        int version = (bytes[1] >> 3) & 3;
        if (version == 1)
            return FileType.Unknown;

        // The layer description value of 00 is reserved
        int layerDescription = (bytes[1] >> 1) & 3;
        if (layerDescription == 0)
            return FileType.Unknown;

        // The bitrate index value of 1111 is disallowed
        int bitrateIndex = bytes[2] >> 4;
        if (bitrateIndex == 0x0F)
            return FileType.Unknown;

        return FileType.Mp3;
    }
}

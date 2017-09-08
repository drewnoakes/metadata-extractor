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
package com.drew.metadata.mp4.media;

import com.drew.lang.annotations.NotNull;

import java.util.HashMap;

public class Mp4HintDirectory extends Mp4MediaDirectory
{
    public static final int TAG_MAX_PDU_SIZE = 101;
    public static final int TAG_AVERAGE_PDU_SIZE = 102;
    public static final int TAG_MAX_BITRATE = 103;
    public static final int TAG_AVERAGE_BITRATE = 104;

    public Mp4HintDirectory()
    {
        this.setDescriptor(new Mp4HintDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        Mp4MediaDirectory.addMp4MediaTags(_tagNameMap);
        _tagNameMap.put(TAG_MAX_PDU_SIZE, "Max PDU Size");
        _tagNameMap.put(TAG_AVERAGE_PDU_SIZE, "Average PDU Size");
        _tagNameMap.put(TAG_MAX_BITRATE, "Max Bitrate");
        _tagNameMap.put(TAG_AVERAGE_BITRATE, "Average Bitrate");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "MP4 Hint";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

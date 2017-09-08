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

public class Mp4MetaDirectory extends Mp4MediaDirectory
{
    public Mp4MetaDirectory()
    {
        this.setDescriptor(new Mp4MetaDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        Mp4MediaDirectory.addMp4MediaTags(_tagNameMap);
    }

    @NotNull
    @Override
    public String getName()
    {
        return "MP4 Metadata";
    }

    @NotNull
    @Override
    protected HashMap<Integer, String> getTagNameMap()
    {
        return null;
    }
}

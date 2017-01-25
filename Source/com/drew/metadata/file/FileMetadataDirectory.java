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
package com.drew.metadata.file;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class FileMetadataDirectory extends Directory
{
    public static final int TAG_FILE_NAME = 1;
    public static final int TAG_FILE_SIZE = 2;
    public static final int TAG_FILE_MODIFIED_DATE = 3;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_FILE_NAME, "File Name");
        _tagNameMap.put(TAG_FILE_SIZE, "File Size");
        _tagNameMap.put(TAG_FILE_MODIFIED_DATE, "File Modified Date");
    }

    public FileMetadataDirectory()
    {
        this.setDescriptor(new FileMetadataDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "File";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

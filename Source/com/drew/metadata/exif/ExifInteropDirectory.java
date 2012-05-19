/*
 * Copyright 2002-2012 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes Exif interoperability tags.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class ExifInteropDirectory extends Directory
{
    public static final int TAG_INTEROP_INDEX = 0x0001;
    public static final int TAG_INTEROP_VERSION = 0x0002;
    public static final int TAG_RELATED_IMAGE_FILE_FORMAT = 0x1000;
    public static final int TAG_RELATED_IMAGE_WIDTH = 0x1001;
    public static final int TAG_RELATED_IMAGE_LENGTH = 0x1002;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_INTEROP_INDEX, "Interoperability Index");
        _tagNameMap.put(TAG_INTEROP_VERSION, "Interoperability Version");
        _tagNameMap.put(TAG_RELATED_IMAGE_FILE_FORMAT, "Related Image File Format");
        _tagNameMap.put(TAG_RELATED_IMAGE_WIDTH, "Related Image Width");
        _tagNameMap.put(TAG_RELATED_IMAGE_LENGTH, "Related Image Length");
    }

    public ExifInteropDirectory()
    {
        this.setDescriptor(new ExifInteropDescriptor(this));
    }

    @NotNull
    public String getName()
    {
        return "Interoperability";
    }

    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

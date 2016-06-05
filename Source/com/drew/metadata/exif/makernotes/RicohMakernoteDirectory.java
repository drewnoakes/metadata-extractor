/*
 * Copyright 2002-2016 Drew Noakes
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
package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes tags specific to Ricoh cameras.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class RicohMakernoteDirectory extends Directory
{
    public static final int TAG_MAKERNOTE_DATA_TYPE = 0x0001;
    public static final int TAG_VERSION = 0x0002;
    public static final int TAG_PRINT_IMAGE_MATCHING_INFO = 0x0E00;
    public static final int TAG_RICOH_CAMERA_INFO_MAKERNOTE_SUB_IFD_POINTER = 0x2001;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_MAKERNOTE_DATA_TYPE, "Makernote Data Type");
        _tagNameMap.put(TAG_VERSION, "Version");
        _tagNameMap.put(TAG_PRINT_IMAGE_MATCHING_INFO, "Print Image Matching (PIM) Info");
        _tagNameMap.put(TAG_RICOH_CAMERA_INFO_MAKERNOTE_SUB_IFD_POINTER, "Ricoh Camera Info Makernote Sub-IFD");
    }

    public RicohMakernoteDirectory()
    {
        this.setDescriptor(new RicohMakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Ricoh Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

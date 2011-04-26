/*
 * Copyright 2002-2011 Drew Noakes
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

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes tags specific to Panasonic cameras.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class PanasonicMakernoteDirectory extends Directory
{
    public static final int TAG_PANASONIC_QUALITY_MODE = 0x0001;
    public static final int TAG_PANASONIC_VERSION = 0x0002;
    /**
     * 1 = On
     * 2 = Off
     */
    public static final int TAG_PANASONIC_MACRO_MODE = 0x001C;
    /**
     * 1 = Normal
     * 2 = Portrait
     * 9 = Macro 
     */
    public static final int TAG_PANASONIC_RECORD_MODE = 0x001F;
    public static final int TAG_PANASONIC_PRINT_IMAGE_MATCHING_INFO = 0x0E00;

    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_PANASONIC_QUALITY_MODE, "Quality Mode");
        _tagNameMap.put(TAG_PANASONIC_VERSION, "Version");
        _tagNameMap.put(TAG_PANASONIC_MACRO_MODE, "Macro Mode");
        _tagNameMap.put(TAG_PANASONIC_RECORD_MODE, "Record Mode");
        _tagNameMap.put(TAG_PANASONIC_PRINT_IMAGE_MATCHING_INFO, "Print Image Matching (PIM) Info");
    }

    public PanasonicMakernoteDirectory()
    {
        this.setDescriptor(new PanasonicMakernoteDescriptor(this));
    }

    public String getName()
    {
        return "Panasonic Makernote";
    }

    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

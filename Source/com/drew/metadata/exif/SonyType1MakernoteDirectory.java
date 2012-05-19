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
 * Describes tags specific to Sony cameras that use the Sony Type 1 makernote tags.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class SonyType1MakernoteDirectory extends Directory
{
    public static final int TAG_PRINT_IMAGE_MATCHING_INFO = 0x0E00;
    public static final int TAG_PREVIEW_IMAGE = 0x2001;
    public static final int TAG_COLOR_MODE_SETTING = 0xb020;
    public static final int TAG_COLOR_TEMPERATURE = 0xb021;
    public static final int TAG_SCENE_MODE = 0xb023;
    public static final int TAG_ZONE_MATCHING = 0xb024;
    public static final int TAG_DYNAMIC_RANGE_OPTIMISER = 0xb025;
    public static final int TAG_IMAGE_STABILISATION = 0xb026;
    public static final int TAG_LENS_ID = 0xb027;
    public static final int TAG_MINOLTA_MAKER_NOTE = 0xb028;
    public static final int TAG_COLOR_MODE = 0xb029;
    public static final int TAG_MACRO = 0xb040;
    public static final int TAG_EXPOSURE_MODE = 0xb041;
    public static final int TAG_QUALITY = 0xb047;
    public static final int TAG_ANTI_BLUR = 0xb04B;
    public static final int TAG_LONG_EXPOSURE_NOISE_REDUCTION = 0xb04E;
    public static final int TAG_NO_PRINT = 0xFFFF;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_PRINT_IMAGE_MATCHING_INFO, "Print Image Matching Info");
        _tagNameMap.put(TAG_PREVIEW_IMAGE, "Preview Image");
        _tagNameMap.put(TAG_COLOR_MODE_SETTING, "Color Mode Setting");
        _tagNameMap.put(TAG_COLOR_TEMPERATURE, "Color Temperature");
        _tagNameMap.put(TAG_SCENE_MODE, "Scene Mode");
        _tagNameMap.put(TAG_ZONE_MATCHING, "Zone Matching");
        _tagNameMap.put(TAG_DYNAMIC_RANGE_OPTIMISER, "Dynamic Range Optimizer");
        _tagNameMap.put(TAG_IMAGE_STABILISATION, "Image Stabilisation");
        _tagNameMap.put(TAG_LENS_ID, "Lens ID");
        _tagNameMap.put(TAG_MINOLTA_MAKER_NOTE, "Minolta Maker Note");
        _tagNameMap.put(TAG_COLOR_MODE, "Color Mode");
        _tagNameMap.put(TAG_MACRO, "Macro");
        _tagNameMap.put(TAG_EXPOSURE_MODE, "Exposure Mode");
        _tagNameMap.put(TAG_QUALITY, "Quality");
        _tagNameMap.put(TAG_ANTI_BLUR, "Anti Blur");
        _tagNameMap.put(TAG_LONG_EXPOSURE_NOISE_REDUCTION, "Long Exposure Noise Reduction");
        _tagNameMap.put(TAG_NO_PRINT, "No Print");
    }

    public SonyType1MakernoteDirectory()
    {
        this.setDescriptor(new SonyType1MakernoteDescriptor(this));
    }

    @NotNull
    public String getName()
    {
        return "Sony Makernote";
    }

    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

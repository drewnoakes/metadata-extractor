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
package com.drew.metadata.mp4.media;

import com.drew.lang.annotations.NotNull;

import java.util.HashMap;

public class Mp4VideoDirectory extends Mp4MediaDirectory
{
    // Video Sample Description Atom
    public static final int TAG_VENDOR                                  = 201;
    public static final int TAG_TEMPORAL_QUALITY                        = 202;
    public static final int TAG_SPATIAL_QUALITY                         = 203;
    public static final int TAG_WIDTH                                   = 204;
    public static final int TAG_HEIGHT                                  = 205;
    public static final int TAG_HORIZONTAL_RESOLUTION                   = 206;
    public static final int TAG_VERTICAL_RESOLUTION                     = 207;
    public static final int TAG_COMPRESSOR_NAME                         = 208;
    public static final int TAG_DEPTH                                   = 209;
    public static final int TAG_COMPRESSION_TYPE                        = 210;

    // Video Media Information Header Atom
    public static final int TAG_GRAPHICS_MODE                           = 211;
    public static final int TAG_OPCOLOR                                 = 212;
    public static final int TAG_COLOR_TABLE                             = 213;
    public static final int TAG_FRAME_RATE                              = 214;

    public Mp4VideoDirectory()
    {
        this.setDescriptor(new Mp4VideoDescriptor(this));
    }

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        Mp4MediaDirectory.addMp4MediaTags(_tagNameMap);
        _tagNameMap.put(TAG_VENDOR, "Vendor");
        _tagNameMap.put(TAG_TEMPORAL_QUALITY, "Temporal Quality");
        _tagNameMap.put(TAG_SPATIAL_QUALITY, "Spatial Quality");
        _tagNameMap.put(TAG_WIDTH, "Width");
        _tagNameMap.put(TAG_HEIGHT, "Height");
        _tagNameMap.put(TAG_HORIZONTAL_RESOLUTION, "Horizontal Resolution");
        _tagNameMap.put(TAG_VERTICAL_RESOLUTION, "Vertical Resolution");
        _tagNameMap.put(TAG_COMPRESSOR_NAME, "Compressor Name");
        _tagNameMap.put(TAG_DEPTH, "Depth");
        _tagNameMap.put(TAG_COMPRESSION_TYPE, "Compression Type");

        _tagNameMap.put(TAG_GRAPHICS_MODE, "Graphics Mode");
        _tagNameMap.put(TAG_OPCOLOR, "Opcolor");
        _tagNameMap.put(TAG_COLOR_TABLE, "Color Table");
        _tagNameMap.put(TAG_FRAME_RATE, "Frame Rate");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "MP4 Video";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

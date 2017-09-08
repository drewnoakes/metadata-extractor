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

public class Mp4VideoDirectory extends Mp4MediaDirectory
{
    // Video Sample Description Atom
    public static final int TAG_VENDOR                                  = 101;
    public static final int TAG_TEMPORAL_QUALITY                        = 102;
    public static final int TAG_SPATIAL_QUALITY                         = 103;
    public static final int TAG_WIDTH                                   = 104;
    public static final int TAG_HEIGHT                                  = 105;
    public static final int TAG_HORIZONTAL_RESOLUTION                   = 106;
    public static final int TAG_VERTICAL_RESOLUTION                     = 107;
    public static final int TAG_COMPRESSOR_NAME                         = 108;
    public static final int TAG_DEPTH                                   = 109;
    public static final int TAG_COMPRESSION_TYPE                        = 110;

    // Video Media Information Header Atom
    public static final int TAG_GRAPHICS_MODE                           = 111;
    public static final int TAG_OPCOLOR                                 = 112;
    public static final int TAG_COLOR_TABLE                             = 113;
    public static final int TAG_FRAME_RATE                              = 114;

    public Mp4VideoDirectory()
    {
        this.setDescriptor(new Mp4VideoDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

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

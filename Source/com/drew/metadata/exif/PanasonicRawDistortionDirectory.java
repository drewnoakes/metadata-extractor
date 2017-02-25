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

package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * These tags can be found in Panasonic/Leica RAW, RW2 and RWL images. The index values are 'fake' but
 * chosen specifically to make processing easier
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class PanasonicRawDistortionDirectory extends Directory
{
    // 0 and 1 are checksums

    public static final int TagDistortionParam02 = 2;

    public static final int TagDistortionParam04 = 4;
    public static final int TagDistortionScale = 5;

    public static final int TagDistortionCorrection = 7;
    public static final int TagDistortionParam08 = 8;
    public static final int TagDistortionParam09 = 9;

    public static final int TagDistortionParam11 = 11;
    public static final int TagDistortionN = 12;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TagDistortionParam02, "Distortion Param 2");
        _tagNameMap.put(TagDistortionParam04, "Distortion Param 4");
        _tagNameMap.put(TagDistortionScale, "Distortion Scale");
        _tagNameMap.put(TagDistortionCorrection, "Distortion Correction");
        _tagNameMap.put(TagDistortionParam08, "Distortion Param 8");
        _tagNameMap.put(TagDistortionParam09, "Distortion Param 9");
        _tagNameMap.put(TagDistortionParam11, "Distortion Param 11");
        _tagNameMap.put(TagDistortionN, "Distortion N");
    }

    public PanasonicRawDistortionDirectory()
    {
        this.setDescriptor(new PanasonicRawDistortionDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "PanasonicRaw DistortionInfo";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

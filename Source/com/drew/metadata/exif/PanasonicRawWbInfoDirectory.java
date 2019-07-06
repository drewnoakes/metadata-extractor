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
public class PanasonicRawWbInfoDirectory extends Directory
{
    public static final int TagNumWbEntries = 0;

    public static final int TagWbType1 = 1;
    public static final int TagWbRbLevels1 = 2;

    public static final int TagWbType2 = 4;
    public static final int TagWbRbLevels2 = 5;

    public static final int TagWbType3 = 7;
    public static final int TagWbRbLevels3 = 8;

    public static final int TagWbType4 = 10;
    public static final int TagWbRbLevels4 = 11;

    public static final int TagWbType5 = 13;
    public static final int TagWbRbLevels5 = 14;

    public static final int TagWbType6 = 16;
    public static final int TagWbRbLevels6 = 17;

    public static final int TagWbType7 = 19;
    public static final int TagWbRbLevels7 = 20;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TagNumWbEntries, "Num WB Entries");
        _tagNameMap.put(TagWbType1, "WB Type 1");
        _tagNameMap.put(TagWbRbLevels1, "WB RGB Levels 1");
        _tagNameMap.put(TagWbType2, "WB Type 2");
        _tagNameMap.put(TagWbRbLevels2, "WB RGB Levels 2");
        _tagNameMap.put(TagWbType3, "WB Type 3");
        _tagNameMap.put(TagWbRbLevels3, "WB RGB Levels 3");
        _tagNameMap.put(TagWbType4, "WB Type 4");
        _tagNameMap.put(TagWbRbLevels4, "WB RGB Levels 4");
        _tagNameMap.put(TagWbType5, "WB Type 5");
        _tagNameMap.put(TagWbRbLevels5, "WB RGB Levels 5");
        _tagNameMap.put(TagWbType6, "WB Type 6");
        _tagNameMap.put(TagWbRbLevels6, "WB RGB Levels 6");
        _tagNameMap.put(TagWbType7, "WB Type 7");
        _tagNameMap.put(TagWbRbLevels7, "WB RGB Levels 7");
    }

    public PanasonicRawWbInfoDirectory()
    {
        this.setDescriptor(new PanasonicRawWbInfoDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "PanasonicRaw WbInfo";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

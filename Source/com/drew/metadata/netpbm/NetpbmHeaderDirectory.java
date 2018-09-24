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

package com.drew.metadata.netpbm;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;
/**
 *
 * @author Drew Noakes https://drewnoakes.com
 * @author Kevin Mott https://github.com/kwhopper
 */
public class NetpbmHeaderDirectory extends Directory
{
    public static final int TAG_FORMAT_TYPE = 1;
    public static final int TAG_WIDTH = 2;
    public static final int TAG_HEIGHT = 3;
    public static final int TAG_MAXIMUM_VALUE = 4;
    
    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_FORMAT_TYPE, "Format Type");
        _tagNameMap.put(TAG_WIDTH, "Width");
        _tagNameMap.put(TAG_HEIGHT, "Height");
        _tagNameMap.put(TAG_MAXIMUM_VALUE, "Maximum Value");
    }
    
    public NetpbmHeaderDirectory()
    {
        this.setDescriptor(new NetpbmHeaderDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Netpbm";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

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
package com.drew.metadata;

import com.drew.lang.annotations.NotNull;

import java.util.HashMap;

/**
 * A mock implementation of Directory used in unit testing.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class MockDirectory extends Directory
{
    private final HashMap<Integer, String> _tagNameMap;

    public MockDirectory()
    {
        this._tagNameMap = new HashMap<Integer, String>();
    }

    @Override
    @NotNull
    public String getName()
    {
        return "";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

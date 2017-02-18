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
package com.drew.metadata.jfxx;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.util.HashMap;

/**
 * Directory of tags and values for the SOF0 JFXX segment.
 *
 * @author Drew Noakes
 */
@SuppressWarnings("WeakerAccess")
public class JfxxDirectory extends Directory
{
    public static final int TAG_EXTENSION_CODE = 5;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_EXTENSION_CODE, "Extension Code");
    }

    public JfxxDirectory()
    {
        this.setDescriptor(new JfxxDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "JFXX";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    public int getExtensionCode() throws MetadataException
    {
        return getInt(JfxxDirectory.TAG_EXTENSION_CODE);
    }
}

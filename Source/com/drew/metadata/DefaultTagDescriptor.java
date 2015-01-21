/*
 * Copyright 2002-2015 Drew Noakes
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

/**
 * A default implementation of the abstract TagDescriptor.  As this class is not coded with awareness of any metadata
 * tags, it simply reports tag names using the format 'Unknown tag 0x00' (with the corresponding tag number in hex)
 * and gives descriptions using the default string representation of the value.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class DefaultTagDescriptor extends TagDescriptor<Directory>
{
    public DefaultTagDescriptor(@NotNull Directory directory)
    {
        super(directory);
    }

    /**
     * Gets a best-effort tag name using the format 'Unknown tag 0x00' (with the corresponding tag type in hex).
     * @param tagType the tag type identifier.
     * @return a string representation of the tag name.
     */
    @NotNull
    public String getTagName(int tagType)
    {
        String hex = Integer.toHexString(tagType).toUpperCase();
        while (hex.length() < 4) hex = "0" + hex;
        return "Unknown tag 0x" + hex;
    }
}

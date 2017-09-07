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
package com.drew.metadata.jpeg;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.jpeg.HuffmanTablesDirectory.*;

/**
 * Provides a human-readable string version of the tag stored in a {@link HuffmanTablesDirectory}.
 *
 * <ul>
 *   <li>https://en.wikipedia.org/wiki/Huffman_coding</li>
 *   <li>http://stackoverflow.com/a/4954117</li>
 * </ul>
 *
 * @author Nadahar
 */
@SuppressWarnings("WeakerAccess")
public class HuffmanTablesDescriptor extends TagDescriptor<HuffmanTablesDirectory>
{
    public HuffmanTablesDescriptor(@NotNull HuffmanTablesDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_NUMBER_OF_TABLES:
                return getNumberOfTablesDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getNumberOfTablesDescription()
    {
        Integer value = _directory.getInteger(TAG_NUMBER_OF_TABLES);
        if (value == null)
            return null;
        return value + (value == 1 ? " Huffman table" : " Huffman tables");
    }
}

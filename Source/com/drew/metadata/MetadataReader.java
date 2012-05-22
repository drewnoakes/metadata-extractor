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
package com.drew.metadata;

import com.drew.lang.BufferReader;
import com.drew.lang.annotations.NotNull;

/**
 * Interface through which all classes responsible for decoding a particular type of metadata may be called.
 * Note that the data source is not specified on this interface.  Instead it is suggested that implementations
 * take their data within a constructor.  Constructors might be overloaded to allow for different sources, such as
 * files, streams and byte arrays.  As such, instances of implementations of this interface would be single-use and
 * not thread-safe.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public interface MetadataReader
{
    /**
     * Extract metadata from the source and merge it into an existing Metadata object.
     *
     * @param reader   The reader from which the metadata should be extracted.
     * @param metadata The Metadata object into which extracted values should be merged.
     */
    public void extract(@NotNull final BufferReader reader, @NotNull final Metadata metadata);
}

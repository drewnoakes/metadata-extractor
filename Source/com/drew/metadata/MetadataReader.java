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
package com.drew.metadata;

import com.drew.lang.RandomAccessReader;
import com.drew.lang.annotations.NotNull;

/**
 * Defines an object capable of processing a particular type of metadata from a {@link RandomAccessReader}.
 * <p>
 * Instances of this interface must be thread-safe and reusable.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public interface MetadataReader
{
    /**
     * Extracts metadata from <code>reader</code> and merges it into the specified {@link Metadata} object.
     *
     * @param reader   The {@link RandomAccessReader} from which the metadata should be extracted.
     * @param metadata The {@link Metadata} object into which extracted values should be merged.
     */
    void extract(@NotNull final RandomAccessReader reader, @NotNull final Metadata metadata);
}

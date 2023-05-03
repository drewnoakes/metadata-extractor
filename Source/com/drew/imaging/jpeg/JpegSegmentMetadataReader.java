/*
 * Copyright 2002-2022 Drew Noakes and contributors
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
package com.drew.imaging.jpeg;

import java.io.IOException;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

/**
 * Defines an object that extracts metadata from in JPEG segments.
 */
public interface JpegSegmentMetadataReader
{
    /**
     * Gets the set of JPEG segment types that this reader is interested in.
     */
    @NotNull
    Iterable<JpegSegmentType> getSegmentTypes();

    /**
     * Extracts metadata from all instances of a particular JPEG segment type.
     *
     * @param segments A sequence of byte arrays from which the metadata should be extracted. These are in the order
     *                 encountered in the original file.
     * @param metadata The {@link Metadata} object into which extracted values should be merged.
     * @param segmentType The {@link JpegSegmentType} being read.
     * @throws IOException an error occurred while accessing the required data
     */
    void readJpegSegments(@NotNull final Iterable<byte[]> segments, @NotNull final Metadata metadata, @NotNull final JpegSegmentType segmentType) throws IOException;
}

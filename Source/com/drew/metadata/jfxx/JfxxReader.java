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
package com.drew.metadata.jfxx;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataContext;
import com.drew.metadata.MetadataReader;

import java.io.IOException;
import java.util.Collections;

/**
 * Reader for JFXX (JFIF extensions) data, found in the APP0 JPEG segment.
 *
 * <ul>
 *   <li>http://en.wikipedia.org/wiki/JPEG_File_Interchange_Format</li>
 *   <li>http://www.w3.org/Graphics/JPEG/jfif3.pdf</li>
 * </ul>
 *
 * @author Drew Noakes
 */
public class JfxxReader implements JpegSegmentMetadataReader, MetadataReader
{
    public static final String PREAMBLE = "JFXX";

    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Collections.singletonList(JpegSegmentType.APP0);
    }

    public void readJpegSegments(@NotNull Iterable<byte[]> segments, @NotNull Metadata metadata, @NotNull JpegSegmentType segmentType, @NotNull MetadataContext context)
    {
        for (byte[] segmentBytes : segments) {
            // Skip segments not starting with the required header
            if (segmentBytes.length >= PREAMBLE.length() && PREAMBLE.equals(new String(segmentBytes, 0, PREAMBLE.length())))
                extract(new ByteArrayReader(segmentBytes), metadata, context);
        }
    }

    @Override
    public void extract(RandomAccessReader reader, Metadata metadata)
    {
        extract(reader, metadata, new MetadataContext());
    }

    /**
     * Performs the JFXX data extraction, adding found values to the specified
     * instance of {@link Metadata}.
     */
    public void extract(@NotNull final RandomAccessReader reader, @NotNull final Metadata metadata, @NotNull final MetadataContext context)
    {
        JfxxDirectory directory = new JfxxDirectory(context);
        metadata.addDirectory(directory);

        try {
            // For JFXX, the tag number is also the offset into the segment

            directory.setInt(JfxxDirectory.TAG_EXTENSION_CODE, reader.getUInt8(JfxxDirectory.TAG_EXTENSION_CODE));
        } catch (IOException me) {
            directory.addError(me.getMessage());
        }
    }
}

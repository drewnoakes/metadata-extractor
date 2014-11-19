/*
 * Copyright 2002-2013 Drew Noakes
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
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.metadata.jfif;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.util.Arrays;

/**
 * Reader for JFIF data, found in the APP0 JPEG segment.
 * <p/>
 * More info at: http://en.wikipedia.org/wiki/JPEG_File_Interchange_Format
 *
 * @author Yuri Binev, Drew Noakes, Markus Meyer
 */
public class JfifReader implements JpegSegmentMetadataReader
{
    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Arrays.asList(JpegSegmentType.APP0);
    }

    public boolean canProcess(@NotNull byte[] segmentBytes, @NotNull JpegSegmentType segmentType)
    {
        return segmentBytes.length > 3 && "JFIF".equals(new String(segmentBytes, 0, 4));
    }

    public void extract(@NotNull byte[] segmentBytes, @NotNull Metadata metadata, @NotNull JpegSegmentType segmentType)
    {
        extract(new ByteArrayReader(segmentBytes), metadata);
    }

    /**
     * Performs the Jfif data extraction, adding found values to the specified
     * instance of {@link Metadata}.
     */
    public void extract(@NotNull final RandomAccessReader reader, @NotNull final Metadata metadata)
    {
        JfifDirectory directory = metadata.getOrCreateDirectory(JfifDirectory.class);

        try {
            // For JFIF, the tag number is also the offset into the segment

            int ver = reader.getUInt16(JfifDirectory.TAG_VERSION);
            directory.setInt(JfifDirectory.TAG_VERSION, ver);

            int units = reader.getUInt8(JfifDirectory.TAG_UNITS);
            directory.setInt(JfifDirectory.TAG_UNITS, units);

            int height = reader.getUInt16(JfifDirectory.TAG_RESX);
            directory.setInt(JfifDirectory.TAG_RESX, height);

            int width = reader.getUInt16(JfifDirectory.TAG_RESY);
            directory.setInt(JfifDirectory.TAG_RESY, width);

        } catch (IOException me) {
            directory.addError(me.getMessage());
        }
    }
}

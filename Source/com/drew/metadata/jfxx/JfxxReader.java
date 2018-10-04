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

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.imaging.jpeg.JpegSegment;
import com.drew.lang.ReaderInfo;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

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
public class JfxxReader implements JpegSegmentMetadataReader //, MetadataReader
{
    public static final String JPEG_SEGMENT_ID = "JFXX";
    public static final String JPEG_SEGMENT_PREAMBLE = "JFXX";

    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Collections.singletonList(JpegSegmentType.APP0);
    }

    public void readJpegSegments(@NotNull Iterable<JpegSegment> segments, @NotNull Metadata metadata) throws IOException
    {
        for (JpegSegment segment : segments) {
            // Skip segments not starting with the required header
            if (segment.getReader().getLength() >= JPEG_SEGMENT_PREAMBLE.length() && segment.getPreamble() == JPEG_SEGMENT_ID)
                extract(segment.getReader(), metadata);
        }
    }

    /**
     * Performs the JFXX data extraction, adding found values to the specified
     * instance of {@link Metadata}.
     */
    public void extract(@NotNull ReaderInfo reader, @NotNull final Metadata metadata)
    {
        JfxxDirectory directory = new JfxxDirectory();
        metadata.addDirectory(directory);

        try {
            // For JFXX, the tag number is also the offset into the segment

            directory.setInt(JfxxDirectory.TAG_EXTENSION_CODE, reader.getUInt8(JfxxDirectory.TAG_EXTENSION_CODE));
        } catch (IOException me) {
            directory.addError(me.getMessage());
        }
    }
}

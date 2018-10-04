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

package com.drew.metadata.adobe;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.imaging.jpeg.JpegSegment;
import com.drew.lang.Charsets;
import com.drew.lang.ReaderInfo;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.util.Collections;

/**
 * Decodes Adobe formatted data stored in JPEG files, normally in the APPE (App14) segment.
 *
 * @author Philip
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class AdobeJpegReader implements JpegSegmentMetadataReader
{
    public static final String JPEG_SEGMENT_ID = "Adobe";
    public static final String JPEG_SEGMENT_PREAMBLE = "Adobe";

    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Collections.singletonList(JpegSegmentType.APPE);
    }

    public void readJpegSegments(@NotNull Iterable<JpegSegment> segments, @NotNull Metadata metadata) throws IOException
    {
        for (JpegSegment segment : segments) {
            if (segment.getReader().getLength() == 12 && JPEG_SEGMENT_ID.equals(segment.getPreamble()))
                extract(segment.getReader().Clone(), metadata);
        }
    }

    public void extract(@NotNull ReaderInfo reader, @NotNull Metadata metadata)
    {
        Directory directory = new AdobeJpegDirectory();
        metadata.addDirectory(directory);

        try {
            reader.setMotorolaByteOrder(false);

            if (!reader.getString(JPEG_SEGMENT_PREAMBLE.length(), Charsets.UTF_8).equals(JPEG_SEGMENT_PREAMBLE)) {
                directory.addError("Invalid Adobe JPEG data header.");
                return;
            }

            directory.setInt(AdobeJpegDirectory.TAG_DCT_ENCODE_VERSION, reader.getUInt16());
            directory.setInt(AdobeJpegDirectory.TAG_APP14_FLAGS0, reader.getUInt16());
            directory.setInt(AdobeJpegDirectory.TAG_APP14_FLAGS1, reader.getUInt16());
            directory.setInt(AdobeJpegDirectory.TAG_COLOR_TRANSFORM, reader.getInt8());
        } catch (IOException ex) {
            directory.addError("IO exception processing data: " + ex.getMessage());
        }
    }
}

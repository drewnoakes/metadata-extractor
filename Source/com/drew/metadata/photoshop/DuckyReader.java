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
package com.drew.metadata.photoshop;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.Charsets;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.util.Collections;

/**
 * Reads Photoshop "ducky" segments, created during Save-for-Web.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class DuckyReader implements JpegSegmentMetadataReader
{
    @NotNull
    private static final String JPEG_SEGMENT_PREAMBLE = "Ducky";

    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Collections.singletonList(JpegSegmentType.APPC);
    }

    public void readJpegSegments(@NotNull Iterable<byte[]> segments, @NotNull Metadata metadata, @NotNull JpegSegmentType segmentType)
    {
        final int preambleLength = JPEG_SEGMENT_PREAMBLE.length();

        for (byte[] segmentBytes : segments) {
            // Ensure data starts with the necessary preamble
            if (segmentBytes.length < preambleLength || !JPEG_SEGMENT_PREAMBLE.equals(new String(segmentBytes, 0, preambleLength)))
                continue;

            extract(
                new SequentialByteArrayReader(segmentBytes, preambleLength),
                metadata);
        }
    }

    public void extract(@NotNull final SequentialReader reader, @NotNull final Metadata metadata)
    {
        DuckyDirectory directory = new DuckyDirectory();
        metadata.addDirectory(directory);

        try
        {
            while (true)
            {
                int tag = reader.getUInt16();

                // End of Segment is marked with zero
                if (tag == 0)
                    break;

                int length = reader.getUInt16();

                switch (tag)
                {
                    case DuckyDirectory.TAG_QUALITY:
                    {
                        if (length != 4)
                        {
                            directory.addError("Unexpected length for the quality tag");
                            return;
                        }
                        directory.setInt(tag, reader.getInt32());
                        break;
                    }
                    case DuckyDirectory.TAG_COMMENT:
                    case DuckyDirectory.TAG_COPYRIGHT:
                    {
                        reader.skip(4);
                        directory.setStringValue(tag, reader.getStringValue(length - 4, Charsets.UTF_16BE));
                        break;
                    }
                    default:
                    {
                        // Unexpected tag
                        directory.setByteArray(tag, reader.getBytes(length));
                        break;
                    }
                }
            }
        }
        catch (IOException e)
        {
            directory.addError(e.getMessage());
        }
    }
}

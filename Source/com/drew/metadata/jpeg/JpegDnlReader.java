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

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.ErrorDirectory;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.util.Collections;

/**
 * Decodes JPEG DNL data, adjusting the image height with information missing from the JPEG SOFx segment.
 *
 * @author Nadahar
 */
public class JpegDnlReader implements JpegSegmentMetadataReader
{
    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Collections.singletonList(JpegSegmentType.DNL);
    }

    public void readJpegSegments(@NotNull Iterable<byte[]> segments, @NotNull Metadata metadata, @NotNull JpegSegmentType segmentType)
    {
        for (byte[] segmentBytes : segments) {
            extract(segmentBytes, metadata, segmentType);
        }
    }

    public void extract(byte[] segmentBytes, Metadata metadata, JpegSegmentType segmentType)
    {
        JpegDirectory directory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
        if (directory == null) {
            ErrorDirectory errorDirectory = new ErrorDirectory();
            metadata.addDirectory(errorDirectory);
            errorDirectory.addError("DNL segment found without SOFx - illegal JPEG format");
            return;
        }

        SequentialReader reader = new SequentialByteArrayReader(segmentBytes);

        try {
            // Only set height from DNL if it's not already defined
            Integer i = directory.getInteger(JpegDirectory.TAG_IMAGE_HEIGHT);
            if (i == null || i == 0) {
                directory.setInt(JpegDirectory.TAG_IMAGE_HEIGHT, reader.getUInt16());
            }
        } catch (IOException ex) {
            directory.addError(ex.getMessage());
        }
    }
}

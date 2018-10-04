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
package com.drew.metadata.exif;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.imaging.jpeg.JpegSegment;
import com.drew.imaging.tiff.TiffProcessingException;
import com.drew.imaging.tiff.TiffReader;
import com.drew.lang.ReaderInfo;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.util.Collections;

/**
 * Decodes Exif binary data, populating a {@link Metadata} object with tag values in {@link ExifSubIFDDirectory},
 * {@link ExifThumbnailDirectory}, {@link ExifInteropDirectory}, {@link GpsDirectory} and one of the many camera
 * makernote directories.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class ExifReader implements JpegSegmentMetadataReader
{
    public static final String JPEG_SEGMENT_ID = "Exif";
    /** Exif data stored in JPEG files' APP1 segment are preceded by this six character preamble. */
    public static final String JPEG_SEGMENT_PREAMBLE = "Exif\0\0";

    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Collections.singletonList(JpegSegmentType.APP1);
    }

    @Override
    public void readJpegSegments(@NotNull final Iterable<JpegSegment> segments, @NotNull final Metadata metadata) throws IOException
    {
        //assert(segmentType == JpegSegmentType.APP1);
        for (JpegSegment segment : segments) {
            // Filter any segments containing unexpected preambles
            if (segment.getReader().getLength() >= JPEG_SEGMENT_PREAMBLE.length() && JPEG_SEGMENT_ID.equals(segment.getPreamble()))
                extract(segment.getReader().Clone(JPEG_SEGMENT_PREAMBLE.length(), segment.getReader().getLength() - JPEG_SEGMENT_PREAMBLE.length()), metadata);
        }
    }

    /** Reads TIFF formatted Exif data within a {@link ReaderInfo}. */
    public void extract(@NotNull final ReaderInfo reader, @NotNull final Metadata metadata)
    {
        extract(reader, metadata, null);
    }

    /** Reads TIFF formatted Exif data within a {@link ReaderInfo}. */
    public void extract(@NotNull ReaderInfo reader, @NotNull final Metadata metadata, @Nullable Directory parentDirectory)
    {
        ExifTiffHandler exifTiffHandler = new ExifTiffHandler(metadata, parentDirectory);

        try {
            // Read the TIFF-formatted Exif data
            new TiffReader().processTiff(
                    reader, 
                    exifTiffHandler
            );
        } catch (TiffProcessingException e) {
            exifTiffHandler.error("Exception processing TIFF data: " + e.getMessage());
            // TODO what do to with this error state?
            e.printStackTrace(System.err);
        } catch (IOException e) {
            exifTiffHandler.error("Exception processing TIFF data: " + e.getMessage());
            // TODO what do to with this error state?
            e.printStackTrace(System.err);
        }
    }
}

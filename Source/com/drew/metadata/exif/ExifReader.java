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
package com.drew.metadata.exif;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.imaging.tiff.TiffProcessingException;
import com.drew.imaging.tiff.TiffReader;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessReader;
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
    /** Exif data stored in JPEG files' APP1 segment are preceded by this six character preamble "Exif\0\0". */
    public static final String JPEG_SEGMENT_PREAMBLE = "Exif\0\0";

    @Override
    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Collections.singletonList(JpegSegmentType.APP1);
    }

    @Override
    public void readJpegSegments(@NotNull final Iterable<byte[]> segments, @NotNull final Metadata metadata, @NotNull final JpegSegmentType segmentType)
            throws IOException
    {
        assert(segmentType == JpegSegmentType.APP1);

        for (byte[] segmentBytes : segments) {
            // Segment must have the expected preamble
            if (startsWithJpegExifPreamble(segmentBytes)) {
                extract(new ByteArrayReader(segmentBytes, JPEG_SEGMENT_PREAMBLE.length()), metadata, JPEG_SEGMENT_PREAMBLE.length());
            }
        }
    }

    /** Indicates whether 'bytes' starts with 'JpegSegmentPreamble'. */
    public static boolean startsWithJpegExifPreamble(byte[] bytes)
    {
        return bytes.length >= JPEG_SEGMENT_PREAMBLE.length() &&
            new String(bytes, 0, JPEG_SEGMENT_PREAMBLE.length()).equals(JPEG_SEGMENT_PREAMBLE);
    }

    /** Reads TIFF formatted Exif data a specified offset within a {@link RandomAccessReader}. */
    public void extract(@NotNull final RandomAccessReader reader, @NotNull final Metadata metadata, int preambleLength)
    {
        extract(reader, metadata, null, preambleLength);
    }

    /** Reads TIFF formatted Exif data at a specified offset within a {@link RandomAccessReader}. */
    public void extract(@NotNull final RandomAccessReader reader, @NotNull final Metadata metadata, @Nullable Directory parentDirectory, int exifStartOffset)
    {
        ExifTiffHandler exifTiffHandler = new ExifTiffHandler(metadata, parentDirectory, exifStartOffset);

        try {
            // Read the TIFF-formatted Exif data
            new TiffReader().processTiff(
                reader,
                exifTiffHandler
            );
        } catch (TiffProcessingException e) {
            exifTiffHandler.error("Exception processing TIFF data: " + e.getMessage());
        } catch (IOException e) {
            exifTiffHandler.error("Exception processing TIFF data: " + e.getMessage());
        }
    }
}

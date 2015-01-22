/*
 * Copyright 2002-2015 Drew Noakes
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
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.util.Arrays;

/**
 * Decodes Exif binary data, populating a {@link Metadata} object with tag values in {@link ExifSubIFDDirectory},
 * {@link ExifThumbnailDirectory}, {@link ExifInteropDirectory}, {@link GpsDirectory} and one of the many camera
 * makernote directories.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ExifReader implements JpegSegmentMetadataReader
{
    /**
     * The offset at which the TIFF data actually starts. This may be necessary when, for example, processing
     * JPEG Exif data from APP0 which has a 6-byte preamble before starting the TIFF data.
     */
    private static final String JPEG_EXIF_SEGMENT_PREAMBLE = "Exif\0\0";

    private boolean _storeThumbnailBytes = true;

    public boolean isStoreThumbnailBytes()
    {
        return _storeThumbnailBytes;
    }

    public void setStoreThumbnailBytes(boolean storeThumbnailBytes)
    {
        _storeThumbnailBytes = storeThumbnailBytes;
    }

    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Arrays.asList(JpegSegmentType.APP1);
    }

    public boolean canProcess(@NotNull final byte[] segmentBytes, @NotNull final JpegSegmentType segmentType)
    {
        return segmentBytes.length >= JPEG_EXIF_SEGMENT_PREAMBLE.length() && new String(segmentBytes, 0, JPEG_EXIF_SEGMENT_PREAMBLE.length()).equalsIgnoreCase(JPEG_EXIF_SEGMENT_PREAMBLE);
    }

    public void extract(@NotNull final byte[] segmentBytes, @NotNull final Metadata metadata, @NotNull final JpegSegmentType segmentType)
    {
        if (segmentBytes == null)
            throw new NullPointerException("segmentBytes cannot be null");
        if (metadata == null)
            throw new NullPointerException("metadata cannot be null");
        if (segmentType == null)
            throw new NullPointerException("segmentType cannot be null");

        try {
            ByteArrayReader reader = new ByteArrayReader(segmentBytes);

            //
            // Check for the header preamble
            //
            try {
                if (!reader.getString(0, JPEG_EXIF_SEGMENT_PREAMBLE.length()).equals(JPEG_EXIF_SEGMENT_PREAMBLE)) {
                    // TODO what do to with this error state?
                    System.err.println("Invalid JPEG Exif segment preamble");
                    return;
                }
            } catch (IOException e) {
                // TODO what do to with this error state?
                e.printStackTrace(System.err);
                return;
            }

            //
            // Read the TIFF-formatted Exif data
            //
            new TiffReader().processTiff(
                reader,
                new ExifTiffHandler(metadata, _storeThumbnailBytes),
                JPEG_EXIF_SEGMENT_PREAMBLE.length()
            );

        } catch (TiffProcessingException e) {
            // TODO what do to with this error state?
            e.printStackTrace(System.err);
        } catch (IOException e) {
            // TODO what do to with this error state?
            e.printStackTrace(System.err);
        }
    }
}

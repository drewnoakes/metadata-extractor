package com.drew.imaging.jpeg;

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
     */
    void readJpegSegments(@NotNull final Iterable<byte[]> segments, @NotNull final Metadata metadata, @NotNull final JpegSegmentType segmentType);
}

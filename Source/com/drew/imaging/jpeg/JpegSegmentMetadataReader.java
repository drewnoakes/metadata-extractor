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
    public Iterable<JpegSegmentType> getSegmentTypes();

    /**
     * Gets a value indicating whether the supplied byte data can be processed by this reader. This is not a guarantee
     * that no errors will occur, but rather a best-effort indication of whether the parse is likely to succeed.
     * Implementations are expected to check things such as the opening bytes, data length, etc.
     */
    public boolean canProcess(@NotNull final byte[] segmentBytes, @NotNull final JpegSegmentType segmentType);

    /**
     * Extracts metadata from a JPEG segment's byte array and merges it into the specified {@link Metadata} object.
     *
     * @param segmentBytes The byte array from which the metadata should be extracted.
     * @param metadata The {@link Metadata} object into which extracted values should be merged.
     * @param segmentType The {@link JpegSegmentType} being read.
     */
    public void extract(@NotNull final byte[] segmentBytes, @NotNull final Metadata metadata, @NotNull final JpegSegmentType segmentType);
}

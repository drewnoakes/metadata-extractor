/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
package com.drew.imaging.jpeg;

import com.drew.lang.SequentialReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Performs read functions of JPEG files, returning specific file segments.
 * <p>
 * JPEG files are composed of a sequence of consecutive JPEG 'segments'. Each is identified by one of a set of byte
 * values, modelled in the {@link JpegSegmentType} enumeration. Use <code>readSegments</code> to read out the some
 * or all segments into a {@link JpegSegmentData} object, from which the raw JPEG segment byte arrays may be accessed.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class JpegSegmentReader
{
    /**
     * The 0xFF byte that signals the start of a segment.
     */
    private static final byte SEGMENT_IDENTIFIER = (byte) 0xFF;

    /**
     * Private, because this segment crashes my algorithm, and searching for it doesn't work (yet).
     */
    private static final byte SEGMENT_SOS = (byte) 0xDA;

    /**
     * Private, because one wouldn't search for it.
     */
    private static final byte MARKER_EOI = (byte) 0xD9;

    /**
     * Processes the provided JPEG data, and extracts the specified JPEG segments into a {@link JpegSegmentData} object.
     * <p>
     * Will not return SOS (start of scan) or EOI (end of image) segments.
     *
     * @param file a {@link File} from which the JPEG data will be read.
     * @param segmentTypes the set of JPEG segments types that are to be returned. If this argument is <code>null</code>
     *                     then all found segment types are returned.
     */
    @NotNull
    public static JpegSegmentData readSegments(@NotNull File file, @Nullable Iterable<JpegSegmentType> segmentTypes) throws JpegProcessingException, IOException
    {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return readSegments(new StreamReader(stream), segmentTypes);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * Processes the provided JPEG data, and extracts the specified JPEG segments into a {@link JpegSegmentData} object.
     * <p>
     * Will not return SOS (start of scan) or EOI (end of image) segments.
     *
     * @param reader a {@link SequentialReader} from which the JPEG data will be read. It must be positioned at the
     *               beginning of the JPEG data stream.
     * @param segmentTypes the set of JPEG segments types that are to be returned. If this argument is <code>null</code>
     *                     then all found segment types are returned.
     */
    @NotNull
    public static JpegSegmentData readSegments(@NotNull final SequentialReader reader, @Nullable Iterable<JpegSegmentType> segmentTypes) throws JpegProcessingException, IOException
    {
        // Must be big-endian
        assert (reader.isMotorolaByteOrder());

        // first two bytes should be JPEG magic number
        final int magicNumber = reader.getUInt16();
        if (magicNumber != 0xFFD8) {
            throw new JpegProcessingException("JPEG data is expected to begin with 0xFFD8 (ÿØ) not 0x" + Integer.toHexString(magicNumber));
        }

        Set<Byte> segmentTypeBytes = null;
        if (segmentTypes != null) {
            segmentTypeBytes = new HashSet<Byte>();
            for (JpegSegmentType segmentType : segmentTypes) {
                segmentTypeBytes.add(segmentType.byteValue);
            }
        }

        JpegSegmentData segmentData = new JpegSegmentData();

        do {
            // Find the segment marker. Markers are zero or more 0xFF bytes, followed
            // by a 0xFF and then a byte not equal to 0x00 or 0xFF.

            byte segmentIdentifier = reader.getInt8();
            byte segmentType = reader.getInt8();

            // Read until we have a 0xFF byte followed by a byte that is not 0xFF or 0x00
            while (segmentIdentifier != SEGMENT_IDENTIFIER || segmentType == SEGMENT_IDENTIFIER || segmentType == 0) {
            	segmentIdentifier = segmentType;
            	segmentType = reader.getInt8();
            }

            if (segmentType == SEGMENT_SOS) {
                // The 'Start-Of-Scan' segment's length doesn't include the image data, instead would
                // have to search for the two bytes: 0xFF 0xD9 (EOI).
                // It comes last so simply return at this point
                return segmentData;
            }

            if (segmentType == MARKER_EOI) {
                // the 'End-Of-Image' segment -- this should never be found in this fashion
                return segmentData;
            }

            // next 2-bytes are <segment-size>: [high-byte] [low-byte]
            int segmentLength = reader.getUInt16();

            // segment length includes size bytes, so subtract two
            segmentLength -= 2;

            if (segmentLength < 0)
                throw new JpegProcessingException("JPEG segment size would be less than zero");

            // Check whether we are interested in this segment
            if (segmentTypeBytes == null || segmentTypeBytes.contains(segmentType)) {
                byte[] segmentBytes = reader.getBytes(segmentLength);
                assert (segmentLength == segmentBytes.length);
                segmentData.addSegment(segmentType, segmentBytes);
            } else {
                // Skip this segment
                if (!reader.trySkip(segmentLength)) {
                    // If skipping failed, just return the segments we found so far
                    return segmentData;
                }
            }

        } while (true);
    }

    private JpegSegmentReader() throws Exception
    {
        throw new Exception("Not intended for instantiation.");
    }
}

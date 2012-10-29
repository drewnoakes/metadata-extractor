/*
 * Copyright 2002-2012 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.imaging.jpeg;

import com.drew.lang.SequentialReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Performs read functions of JPEG files, returning specific file segments.
 *
 * @author  Drew Noakes http://drewnoakes.com
 */
public class JpegSegmentReader
{
    // TODO allow caller to specify which segments they're interested in, and only return data from that set
    // TODO add a findAvailableSegments() method that scans input without extracting bytes

    /**
     * Private, because this segment crashes my algorithm, and searching for it doesn't work (yet).
     */
    private static final byte SEGMENT_SOS = (byte)0xDA;

    /**
     * Private, because one wouldn't search for it.
     */
    private static final byte MARKER_EOI = (byte)0xD9;

    public static JpegSegmentData readSegments(String fileName) throws IOException, JpegProcessingException
    {
        return readSegments(new File(fileName));
    }

    /**
     * Creates a JpegSegmentReader for a specific file.
     * @param file the JPEG file to read segments from
     */
    @SuppressWarnings({ "ConstantConditions" })
    public static JpegSegmentData readSegments(@NotNull File file) throws JpegProcessingException, IOException
    {
        if (file==null)
            throw new NullPointerException();

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return readSegments(new StreamReader(inputStream));
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    /**
     * Creates a JpegSegmentReader for an InputStream.
     * @param inputStream the InputStream containing JPEG data
     */
    @SuppressWarnings({ "ConstantConditions" })
    public static JpegSegmentData readSegments(@NotNull InputStream inputStream) throws JpegProcessingException
    {
        if (inputStream==null)
            throw new NullPointerException();

        try {
            return readSegments(new StreamReader(inputStream));
        } catch (IOException e) {
            throw new JpegProcessingException(e);
        }
    }

    @NotNull
    public static JpegSegmentData readSegments(@NotNull final SequentialReader reader) throws JpegProcessingException, IOException
    {
        // Must be big-endian
        assert(reader.isMotorolaByteOrder());

        JpegSegmentData segmentData = new JpegSegmentData();

        // first two bytes should be JPEG magic number
        final int magicNumber = reader.getUInt16();
        if (magicNumber != 0xFFD8)
            throw new JpegProcessingException("JPEG data is expected to begin with 0xFFD8 (ÿØ) not 0x" + Integer.toHexString(magicNumber));

        do {
            // next byte is the segment identifier: 0xFF
            final short segmentIdentifier = reader.getUInt8();

            if (segmentIdentifier != 0xFF)
                throw new JpegProcessingException("Expected JPEG segment start identifier 0xFF, not 0x" + Integer.toHexString(segmentIdentifier));

            // next byte is the segment type
            byte segmentType = reader.getInt8();

            // next 2-bytes are <segment-size>: [high-byte] [low-byte]
            int segmentLength = reader.getUInt16();

            // segment length includes size bytes, so subtract two
            segmentLength -= 2;

            if (segmentLength < 0)
                throw new JpegProcessingException("JPEG segment size would be less than zero");

            byte[] segmentBytes = reader.getBytes(segmentLength);
            assert(segmentLength == segmentBytes.length);

            switch (segmentType) {
                case SEGMENT_SOS:
                    // The 'Start-Of-Scan' segment's length doesn't include the image data, instead would
                    // have to search for the two bytes: 0xFF 0xD9 (EOI).
                    // It comes last so simply return at this point
                    return segmentData;
                case MARKER_EOI:
                    // the 'End-Of-Image' segment -- this should never be found in this fashion
                    return segmentData;
                default:
                    segmentData.addSegment(segmentType, segmentBytes);
            }

        } while (true);
    }

    private JpegSegmentReader() throws Exception
    {
        throw new Exception("Not intended for instantiation.");
    }
}

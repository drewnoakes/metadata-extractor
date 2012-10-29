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
import com.drew.lang.annotations.Nullable;

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
    public static JpegSegmentReader fromFile(String fileName) throws IOException, JpegProcessingException
    {
        return fromFile(new File(fileName));
    }

    public static JpegSegmentReader fromFile(File file) throws IOException, JpegProcessingException
    {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return new JpegSegmentReader(inputStream);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    // TODO add a findAvailableSegments() method
    // TODO add more segment identifiers
    // TODO add a getSegmentDescription() method, returning for example 'App1 application data segment, commonly containing Exif data'

    @NotNull
    private final JpegSegmentData _segmentData;

    /**
     * Private, because this segment crashes my algorithm, and searching for it doesn't work (yet).
     */
    private static final byte SEGMENT_SOS = (byte)0xDA;

    /**
     * Private, because one wouldn't search for it.
     */
    private static final byte MARKER_EOI = (byte)0xD9;

    /** APP0 JPEG segment identifier -- JFIF data (also JFXX apparently). */
    public static final byte SEGMENT_APP0 = (byte)0xE0;
    /** APP1 JPEG segment identifier -- where Exif data is kept.  XMP data is also kept in here, though usually in a second instance. */
    public static final byte SEGMENT_APP1 = (byte)0xE1;
    /** APP2 JPEG segment identifier. */
    public static final byte SEGMENT_APP2 = (byte)0xE2;
    /** APP3 JPEG segment identifier. */
    public static final byte SEGMENT_APP3 = (byte)0xE3;
    /** APP4 JPEG segment identifier. */
    public static final byte SEGMENT_APP4 = (byte)0xE4;
    /** APP5 JPEG segment identifier. */
    public static final byte SEGMENT_APP5 = (byte)0xE5;
    /** APP6 JPEG segment identifier. */
    public static final byte SEGMENT_APP6 = (byte)0xE6;
    /** APP7 JPEG segment identifier. */
    public static final byte SEGMENT_APP7 = (byte)0xE7;
    /** APP8 JPEG segment identifier. */
    public static final byte SEGMENT_APP8 = (byte)0xE8;
    /** APP9 JPEG segment identifier. */
    public static final byte SEGMENT_APP9 = (byte)0xE9;
    /** APPA (App10) JPEG segment identifier -- can hold Unicode comments. */
    public static final byte SEGMENT_APPA = (byte)0xEA;
    /** APPB (App11) JPEG segment identifier. */
    public static final byte SEGMENT_APPB = (byte)0xEB;
    /** APPC (App12) JPEG segment identifier. */
    public static final byte SEGMENT_APPC = (byte)0xEC;
    /** APPD (App13) JPEG segment identifier -- IPTC data in here. */
    public static final byte SEGMENT_APPD = (byte)0xED;
    /** APPE (App14) JPEG segment identifier. */
    public static final byte SEGMENT_APPE = (byte)0xEE;
    /** APPF (App15) JPEG segment identifier. */
    public static final byte SEGMENT_APPF = (byte)0xEF;
    /** Start Of Image segment identifier. */
    public static final byte SEGMENT_SOI = (byte)0xD8;
    /** Define Quantization Table segment identifier. */
    public static final byte SEGMENT_DQT = (byte)0xDB;
    /** Define Huffman Table segment identifier. */
    public static final byte SEGMENT_DHT = (byte)0xC4;
    /** Start-of-Frame Zero segment identifier. */
    public static final byte SEGMENT_SOF0 = (byte)0xC0;
    /** JPEG comment segment identifier. */
    public static final byte SEGMENT_COM = (byte)0xFE;

    /**
     * Creates a JpegSegmentReader for a specific file.
     * @param file the JPEG file to read segments from
     */
    @SuppressWarnings({ "ConstantConditions" })
    public JpegSegmentReader(@NotNull File file) throws JpegProcessingException, IOException
    {
        if (file==null)
            throw new NullPointerException();

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            _segmentData = readSegments(new StreamReader(inputStream));
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
    public JpegSegmentReader(@NotNull InputStream inputStream) throws JpegProcessingException
    {
        if (inputStream==null)
            throw new NullPointerException();

        try {
            _segmentData = readSegments(new StreamReader(inputStream));
        } catch (IOException e) {
            throw new JpegProcessingException(e);
        }
    }

    /**
     * Reads the first instance of a given JPEG segment, returning the contents as
     * a byte array.
     *
     * @param segmentMarker the byte identifier for the desired segment
     * @return the byte array if found, else null
     */
    @Nullable
    public byte[] readSegment(byte segmentMarker)
    {
        return readSegment(segmentMarker, 0);
    }

    /**
     * Reads the Nth instance of a given JPEG segment, returning the contents as a byte array.
     * 
     * @param segmentMarker the byte identifier for the desired segment
     * @param occurrence the occurrence of the specified segment within the JPEG file
     * @return the byte array if found, else null
     */
    @Nullable
    public byte[] readSegment(byte segmentMarker, int occurrence)
    {
        return _segmentData.getSegment(segmentMarker, occurrence);
    }

    /**
     * Returns all instances of a given JPEG segment.  If no instances exist, an empty sequence is returned.
     *
     * @param segmentMarker a number which identifies the type of JPEG segment being queried
     * @return zero or more byte arrays, each holding the data of a JPEG segment
     */
    @NotNull
    public Iterable<byte[]> readSegments(byte segmentMarker)
    {
        return _segmentData.getSegments(segmentMarker);
    }

    /**
     * Returns the number of segments having the specified JPEG segment marker.
     *
     * @param segmentMarker the JPEG segment identifying marker.
     * @return the count of matching segments.
     */
    public final int getSegmentCount(byte segmentMarker)
    {
        return _segmentData.getSegmentCount(segmentMarker);
    }

    /**
     * Returns the JpegSegmentData object used by this reader.
     *
     * @return the JpegSegmentData object.
     */
    @NotNull
    public final JpegSegmentData getSegmentData()
    {
        return _segmentData;
    }

    @NotNull
    private JpegSegmentData readSegments(@NotNull final SequentialReader reader) throws JpegProcessingException, IOException
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

            // next byte is <segment-marker>
            byte thisSegmentMarker = reader.getInt8();

            // next 2-bytes are <segment-size>: [high-byte] [low-byte]
            int segmentLength = reader.getUInt16();

            // segment length includes size bytes, so subtract two
            segmentLength -= 2;

            if (segmentLength < 0)
                throw new JpegProcessingException("JPEG segment size would be less than zero");

            byte[] segmentBytes = reader.getBytes(segmentLength);
            assert(segmentLength == segmentBytes.length);

            switch (thisSegmentMarker) {
                case SEGMENT_SOS:
                    // The 'Start-Of-Scan' segment's length doesn't include the image data, instead would
                    // have to search for the two bytes: 0xFF 0xD9 (EOI).
                    // It comes last so simply return at this point
                    return segmentData;
                case MARKER_EOI:
                    // the 'End-Of-Image' segment -- this should never be found in this fashion
                    return segmentData;
                default:
                    segmentData.addSegment(thisSegmentMarker, segmentBytes);
            }

        } while (true);
    }
}

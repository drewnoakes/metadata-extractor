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

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.io.*;

/**
 * Performs read functions of Jpeg files, returning specific file segments.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class JpegSegmentReader
{
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

    /** APP0 Jpeg segment identifier -- JFIF data (also JFXX apparently). */
    public static final byte SEGMENT_APP0 = (byte)0xE0;
    /** APP1 Jpeg segment identifier -- where Exif data is kept.  XMP data is also kept in here, though usually in a second instance. */
    public static final byte SEGMENT_APP1 = (byte)0xE1;
    /** APP2 Jpeg segment identifier. */
    public static final byte SEGMENT_APP2 = (byte)0xE2;
    /** APP3 Jpeg segment identifier. */
    public static final byte SEGMENT_APP3 = (byte)0xE3;
    /** APP4 Jpeg segment identifier. */
    public static final byte SEGMENT_APP4 = (byte)0xE4;
    /** APP5 Jpeg segment identifier. */
    public static final byte SEGMENT_APP5 = (byte)0xE5;
    /** APP6 Jpeg segment identifier. */
    public static final byte SEGMENT_APP6 = (byte)0xE6;
    /** APP7 Jpeg segment identifier. */
    public static final byte SEGMENT_APP7 = (byte)0xE7;
    /** APP8 Jpeg segment identifier. */
    public static final byte SEGMENT_APP8 = (byte)0xE8;
    /** APP9 Jpeg segment identifier. */
    public static final byte SEGMENT_APP9 = (byte)0xE9;
    /** APPA (App10) Jpeg segment identifier -- can hold Unicode comments. */
    public static final byte SEGMENT_APPA = (byte)0xEA;
    /** APPB (App11) Jpeg segment identifier. */
    public static final byte SEGMENT_APPB = (byte)0xEB;
    /** APPC (App12) Jpeg segment identifier. */
    public static final byte SEGMENT_APPC = (byte)0xEC;
    /** APPD (App13) Jpeg segment identifier -- IPTC data in here. */
    public static final byte SEGMENT_APPD = (byte)0xED;
    /** APPE (App14) Jpeg segment identifier. */
    public static final byte SEGMENT_APPE = (byte)0xEE;
    /** APPF (App15) Jpeg segment identifier. */
    public static final byte SEGMENT_APPF = (byte)0xEF;
    /** Start Of Image segment identifier. */
    public static final byte SEGMENT_SOI = (byte)0xD8;
    /** Define Quantization Table segment identifier. */
    public static final byte SEGMENT_DQT = (byte)0xDB;
    /** Define Huffman Table segment identifier. */
    public static final byte SEGMENT_DHT = (byte)0xC4;
    /** Start-of-Frame Zero segment identifier. */
    public static final byte SEGMENT_SOF0 = (byte)0xC0;
    /** Jpeg comment segment identifier. */
    public static final byte SEGMENT_COM = (byte)0xFE;

    /**
     * Creates a JpegSegmentReader for a specific file.
     * @param file the Jpeg file to read segments from
     */
    @SuppressWarnings({ "ConstantConditions" })
    public JpegSegmentReader(@NotNull File file) throws JpegProcessingException, IOException
    {
        if (file==null)
            throw new NullPointerException();

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            _segmentData = readSegments(new BufferedInputStream(inputStream), false);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    /**
     * Creates a JpegSegmentReader for a byte array.
     * @param fileContents the byte array containing Jpeg data
     */
    @SuppressWarnings({ "ConstantConditions" })
    public JpegSegmentReader(@NotNull byte[] fileContents) throws JpegProcessingException
    {
        if (fileContents==null)
            throw new NullPointerException();

        BufferedInputStream stream = new BufferedInputStream(new ByteArrayInputStream(fileContents));
        _segmentData = readSegments(stream, false);
    }

    /**
     * Creates a JpegSegmentReader for an InputStream.
     * @param inputStream the InputStream containing Jpeg data
     */
    @SuppressWarnings({ "ConstantConditions" })
    public JpegSegmentReader(@NotNull InputStream inputStream, boolean waitForBytes) throws JpegProcessingException
    {
        if (inputStream==null)
            throw new NullPointerException();

        BufferedInputStream bufferedInputStream = inputStream instanceof BufferedInputStream
                ? (BufferedInputStream)inputStream
                : new BufferedInputStream(inputStream);

        _segmentData = readSegments(bufferedInputStream, waitForBytes);
    }

    /**
     * Reads the first instance of a given Jpeg segment, returning the contents as
     * a byte array.
     * @param segmentMarker the byte identifier for the desired segment
     * @return the byte array if found, else null
     */
    @Nullable
    public byte[] readSegment(byte segmentMarker)
    {
        return readSegment(segmentMarker, 0);
    }

    /**
     * Reads the Nth instance of a given Jpeg segment, returning the contents as a byte array.
     * 
     * @param segmentMarker the byte identifier for the desired segment
     * @param occurrence the occurrence of the specified segment within the jpeg file
     * @return the byte array if found, else null
     */
    @Nullable
    public byte[] readSegment(byte segmentMarker, int occurrence)
    {
        return _segmentData.getSegment(segmentMarker, occurrence);
    }

    /**
     * Returns all instances of a given Jpeg segment.  If no instances exist, an empty sequence is returned.
     *
     * @param segmentMarker a number which identifies the type of Jpeg segment being queried
     * @return zero or more byte arrays, each holding the data of a Jpeg segment
     */
    @NotNull
    public Iterable<byte[]> readSegments(byte segmentMarker)
    {
        return _segmentData.getSegments(segmentMarker);
    }

    /**
     * Returns the number of segments having the specified JPEG segment marker.
     * @param segmentMarker the JPEG segment identifying marker.
     * @return the count of matching segments.
     */
    public final int getSegmentCount(byte segmentMarker)
    {
        return _segmentData.getSegmentCount(segmentMarker);
    }

    /**
     * Returns the JpegSegmentData object used by this reader.
     * @return the JpegSegmentData object.
     */
    @NotNull
    public final JpegSegmentData getSegmentData()
    {
        return _segmentData;
    }

    @NotNull
    private JpegSegmentData readSegments(@NotNull final BufferedInputStream jpegInputStream, boolean waitForBytes) throws JpegProcessingException
    {
        JpegSegmentData segmentData = new JpegSegmentData();

        try {
            int offset = 0;
            // first two bytes should be jpeg magic number
            byte[] headerBytes = new byte[2];
            if (jpegInputStream.read(headerBytes, 0, 2)!=2)
                throw new JpegProcessingException("not a jpeg file");
            final boolean hasValidHeader = (headerBytes[0] & 0xFF) == 0xFF && (headerBytes[1] & 0xFF) == 0xD8;
            if (!hasValidHeader)
                throw new JpegProcessingException("not a jpeg file");

            offset += 2;
            do {
                // need four bytes from stream for segment header before continuing
                if (!checkForBytesOnStream(jpegInputStream, 4, waitForBytes))
                    throw new JpegProcessingException("stream ended before segment header could be read");

                // next byte is 0xFF
                byte segmentIdentifier = (byte)(jpegInputStream.read() & 0xFF);
                if ((segmentIdentifier & 0xFF) != 0xFF) {
                    throw new JpegProcessingException("expected jpeg segment start identifier 0xFF at offset " + offset + ", not 0x" + Integer.toHexString(segmentIdentifier & 0xFF));
                }
                offset++;
                // next byte is <segment-marker>
                byte thisSegmentMarker = (byte)(jpegInputStream.read() & 0xFF);
                offset++;
                // next 2-bytes are <segment-size>: [high-byte] [low-byte]
                byte[] segmentLengthBytes = new byte[2];
                if (jpegInputStream.read(segmentLengthBytes, 0, 2) != 2)
                    throw new JpegProcessingException("Jpeg data ended unexpectedly.");
                offset += 2;
                int segmentLength = ((segmentLengthBytes[0] << 8) & 0xFF00) | (segmentLengthBytes[1] & 0xFF);
                // segment length includes size bytes, so subtract two
                segmentLength -= 2;
                if (!checkForBytesOnStream(jpegInputStream, segmentLength, waitForBytes))
                    throw new JpegProcessingException("segment size would extend beyond file stream length");
                if (segmentLength < 0)
                    throw new JpegProcessingException("segment size would be less than zero");
                byte[] segmentBytes = new byte[segmentLength];
                if (jpegInputStream.read(segmentBytes, 0, segmentLength) != segmentLength)
                    throw new JpegProcessingException("Jpeg data ended unexpectedly.");
                offset += segmentLength;
                if ((thisSegmentMarker & 0xFF) == (SEGMENT_SOS & 0xFF)) {
                    // The 'Start-Of-Scan' segment's length doesn't include the image data, instead would
                    // have to search for the two bytes: 0xFF 0xD9 (EOI).
                    // It comes last so simply return at this point
                    return segmentData;
                } else if ((thisSegmentMarker & 0xFF) == (MARKER_EOI & 0xFF)) {
                    // the 'End-Of-Image' segment -- this should never be found in this fashion
                    return segmentData;
                } else {
                    segmentData.addSegment(thisSegmentMarker, segmentBytes);
                }
            } while (true);
        } catch (IOException ioe) {
            throw new JpegProcessingException("IOException processing Jpeg file: " + ioe.getMessage(), ioe);
        } finally {
            try {
                if (jpegInputStream != null) {
                    jpegInputStream.close();
                }
            } catch (IOException ioe) {
                throw new JpegProcessingException("IOException processing Jpeg file: " + ioe.getMessage(), ioe);
            }
        }
    }

    private boolean checkForBytesOnStream(@NotNull BufferedInputStream stream, int bytesNeeded, boolean waitForBytes) throws IOException
    {
        // NOTE  waiting is essential for network streams where data can be delayed, but it is not necessary for byte[] or filesystems

        if (!waitForBytes)
            return bytesNeeded <= stream.available();

        int count = 40; // * 100ms = approx 4 seconds
        while (count > 0) {
            if (bytesNeeded <= stream.available())
               return true;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // continue
            }
            count--;
        }
        return false;
    }
}

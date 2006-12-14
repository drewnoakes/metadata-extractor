/*
 * JpegSegmentReader.java
 *
 * This class written by Drew Noakes, in accordance with the Jpeg specification.
 *
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 04-Nov-2002 00:54:00 using IntelliJ IDEA
 */
package com.drew.imaging.jpeg;

import java.io.*;

/**
 * Performs read functions of Jpeg files, returning specific file segments.
 * TODO add a findAvailableSegments() method
 * TODO add more segment identifiers
 * TODO add a getSegmentDescription() method, returning for example 'App1 application data segment, commonly containing Exif data'
 * @author  Drew Noakes http://drewnoakes.com
 */
public class JpegSegmentReader
{
    // Jpeg data can be sourced from either a file, byte[] or InputStream

    /** Jpeg file */
    private final File _file;
    /** Jpeg data as byte array */
    private final byte[] _data;
    /** Jpeg data as an InputStream */
    private final InputStream _stream;

    private JpegSegmentData _segmentData;

    /**
     * Private, because this segment crashes my algorithm, and searching for
     * it doesn't work (yet).
     */
    private static final byte SEGMENT_SOS = (byte)0xDA;

    /**
     * Private, because one wouldn't search for it.
     */
    private static final byte MARKER_EOI = (byte)0xD9;

    /** APP0 Jpeg segment identifier -- Jfif data. */
    public static final byte SEGMENT_APP0 = (byte)0xE0;
    /** APP1 Jpeg segment identifier -- where Exif data is kept. */
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
    /** APPA Jpeg segment identifier -- can hold Unicode comments. */
    public static final byte SEGMENT_APPA = (byte)0xEA;
    /** APPB Jpeg segment identifier. */
    public static final byte SEGMENT_APPB = (byte)0xEB;
    /** APPC Jpeg segment identifier. */
    public static final byte SEGMENT_APPC = (byte)0xEC;
    /** APPD Jpeg segment identifier -- IPTC data in here. */
    public static final byte SEGMENT_APPD = (byte)0xED;
    /** APPE Jpeg segment identifier. */
    public static final byte SEGMENT_APPE = (byte)0xEE;
    /** APPF Jpeg segment identifier. */
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
    public JpegSegmentReader(File file) throws JpegProcessingException
    {
        _file = file;
        _data = null;
        _stream = null;

        readSegments();
    }

    /**
     * Creates a JpegSegmentReader for a byte array.
     * @param fileContents the byte array containing Jpeg data
     */
    public JpegSegmentReader(byte[] fileContents) throws JpegProcessingException
    {
        _file = null;
        _data = fileContents;
        _stream = null;

        readSegments();
    }

    /**
     * Creates a JpegSegmentReader for an InputStream.
     * @param in the InputStream containing Jpeg data
     */
    public JpegSegmentReader(InputStream in) throws JpegProcessingException
    {
        _stream = in;
        _file = null;
        _data = null;
        
        readSegments();
    }

    /**
     * Creates a JpegSegmentReader for a JpegSegmentData.
     * @param segmentData an object containing prepared JPEG segment data.
     */
    public JpegSegmentReader(JpegSegmentData segmentData)
    {
        _file = null;
        _data = null;
        _stream = null;

        _segmentData = segmentData;
    }

    /**
     * Reads the first instance of a given Jpeg segment, returning the contents as
     * a byte array.
     * @param segmentMarker the byte identifier for the desired segment
     * @return the byte array if found, else null
     * @throws JpegProcessingException for any problems processing the Jpeg data,
     *         including inner IOExceptions
     */
    public byte[] readSegment(byte segmentMarker) throws JpegProcessingException
    {
        return readSegment(segmentMarker, 0);
    }

    /**
     * Reads the first instance of a given Jpeg segment, returning the contents as
     * a byte array.
     * @param segmentMarker the byte identifier for the desired segment
     * @param occurrence the occurrence of the specified segment within the jpeg file
     * @return the byte array if found, else null
     */
    public byte[] readSegment(byte segmentMarker, int occurrence)
    {
        return _segmentData.getSegment(segmentMarker, occurrence);
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
    public final JpegSegmentData getSegmentData()
    {
        return _segmentData;
    }

    private void readSegments() throws JpegProcessingException
    {
        _segmentData = new JpegSegmentData();

        BufferedInputStream inStream = getJpegInputStream();
        try {
            int offset = 0;
            // first two bytes should be jpeg magic number
            if (!isValidJpegHeaderBytes(inStream)) {
                throw new JpegProcessingException("not a jpeg file");
            }
            offset += 2;
            do {
                // next byte is 0xFF
                byte segmentIdentifier = (byte)(inStream.read() & 0xFF);
                if ((segmentIdentifier & 0xFF) != 0xFF) {
                    throw new JpegProcessingException("expected jpeg segment start identifier 0xFF at offset " + offset + ", not 0x" + Integer.toHexString(segmentIdentifier & 0xFF));
                }
                offset++;
                // next byte is <segment-marker>
                byte thisSegmentMarker = (byte)(inStream.read() & 0xFF);
                offset++;
                // next 2-bytes are <segment-size>: [high-byte] [low-byte]
                byte[] segmentLengthBytes = new byte[2];
                inStream.read(segmentLengthBytes, 0, 2);
                offset += 2;
                int segmentLength = ((segmentLengthBytes[0] << 8) & 0xFF00) | (segmentLengthBytes[1] & 0xFF);
                // segment length includes size bytes, so subtract two
                segmentLength -= 2;
                if (segmentLength > inStream.available())
                    throw new JpegProcessingException("segment size would extend beyond file stream length");
                else if (segmentLength < 0)
                    throw new JpegProcessingException("segment size would be less than zero");
                byte[] segmentBytes = new byte[segmentLength];
                inStream.read(segmentBytes, 0, segmentLength);
                offset += segmentLength;
                if ((thisSegmentMarker & 0xFF) == (SEGMENT_SOS & 0xFF)) {
                    // The 'Start-Of-Scan' segment's length doesn't include the image data, instead would
                    // have to search for the two bytes: 0xFF 0xD9 (EOI).
                    // It comes last so simply return at this point
                    return;
                } else if ((thisSegmentMarker & 0xFF) == (MARKER_EOI & 0xFF)) {
                    // the 'End-Of-Image' segment -- this should never be found in this fashion
                    return;
                } else {
                    _segmentData.addSegment(thisSegmentMarker, segmentBytes);
                }
                // didn't find the one we're looking for, loop through to the next segment
            } while (true);
        } catch (IOException ioe) {
            //throw new JpegProcessingException("IOException processing Jpeg file", ioe);
            throw new JpegProcessingException("IOException processing Jpeg file: " + ioe.getMessage(), ioe);
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException ioe) {
                //throw new JpegProcessingException("IOException processing Jpeg file", ioe);
                throw new JpegProcessingException("IOException processing Jpeg file: " + ioe.getMessage(), ioe);
            }
        }
    }

    /**
     * Private helper method to create a BufferedInputStream of Jpeg data from whichever
     * data source was specified upon construction of this instance.
     * @return a a BufferedInputStream of Jpeg data
     * @throws JpegProcessingException for any problems obtaining the stream
     */
    private BufferedInputStream getJpegInputStream() throws JpegProcessingException
    {
        if (_stream!=null) {
            if (_stream instanceof BufferedInputStream) {
                return (BufferedInputStream) _stream;
            } else {
                return new BufferedInputStream(_stream);
            }
        }
        InputStream inputStream;
        if (_data == null) {
            try {
                inputStream = new FileInputStream(_file);
            } catch (FileNotFoundException e) {
                throw new JpegProcessingException("Jpeg file does not exist", e);
            }
        } else {
            inputStream = new ByteArrayInputStream(_data);
        }
        return new BufferedInputStream(inputStream);
    }

    /**
     * Helper method that validates the Jpeg file's magic number.
     * @param fileStream the InputStream to read bytes from, which must be positioned
     *        at its start (i.e. no bytes read yet)
     * @return true if the magic number is Jpeg (0xFFD8)
     * @throws IOException for any problem in reading the file
     */
    private boolean isValidJpegHeaderBytes(InputStream fileStream) throws IOException
    {
        byte[] header = new byte[2];
        fileStream.read(header, 0, 2);
        return (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8;
    }
}
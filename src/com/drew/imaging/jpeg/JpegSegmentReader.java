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
 *   drew@drewnoakes.com
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
    /**
     * The file passed to the constructor.
     */
    private final File _file;

    /**
     * Private, because this segment crashes my algorithm, and searching for
     * it doesn't work (yet).
     */
    private static byte SEGMENT_MARKER_SOS = (byte)0xDA;

    /**
     * Private, because one wouldn't search for it.
     */
    private static byte SEGMENT_MARKER_EOI = (byte)0xD9;

    /**
     * APP0 Jpeg segment identifier -- Jfif data.
     */
    public static byte SEGMENT_MARKER_APP0 = (byte)0xE0;

    /**
     * APP1 Jpeg segment identifier -- where Exif data is kept.
     */
    public static byte SEGMENT_MARKER_APP1 = (byte)0xE1;

    /** APP2 Jpeg segment identifier. */
    public static byte SEGMENT_MARKER_APP2 = (byte)0xE2;
    /** APP3 Jpeg segment identifier. */
    public static byte SEGMENT_MARKER_APP3 = (byte)0xE3;
    /** APP4 Jpeg segment identifier. */
    public static byte SEGMENT_MARKER_APP4 = (byte)0xE4;
    /** APP5 Jpeg segment identifier. */
    public static byte SEGMENT_MARKER_APP5 = (byte)0xE5;
    /** APP6 Jpeg segment identifier. */
    public static byte SEGMENT_MARKER_APP6 = (byte)0xE6;
    /** APP7 Jpeg segment identifier. */
    public static byte SEGMENT_MARKER_APP7 = (byte)0xE7;
    /** APP8 Jpeg segment identifier. */
    public static byte SEGMENT_MARKER_APP8 = (byte)0xE8;
    /** APP9 Jpeg segment identifier. */
    public static byte SEGMENT_MARKER_APP9 = (byte)0xE9;
    /** APPA Jpeg segment identifier. */
    public static byte SEGMENT_MARKER_APPA = (byte)0xEA;
    /** APPB Jpeg segment identifier. */
    public static byte SEGMENT_MARKER_APPB = (byte)0xEB;
    /** APPC Jpeg segment identifier. */
    public static byte SEGMENT_MARKER_APPC = (byte)0xEC;
    /** APPD Jpeg segment identifier -- IPTC data in here. */
    public static byte SEGMENT_MARKER_APPD = (byte)0xED;
    /** APPE Jpeg segment identifier. */
    public static byte SEGMENT_MARKER_APPE = (byte)0xEE;
    /** APPF Jpeg segment identifier. */
    public static byte SEGMENT_MARKER_APPF = (byte)0xEF;

    /**
     * Start Of Image segment identifier.
     */
    public static byte SEGMENT_MARKER_SOI = (byte)0xD8;

    /**
     * Define Quantization Table segment identifier.
     */
    public static byte SEGMENT_MARKER_DQT = (byte)0xDB;

    /**
     * Define Huffman Table segment identifier.
     */
    public static byte SEGMENT_MARKER_DHT = (byte)0xC4;

    /**
     * Start-of-Frame Zero segment identifier.
     */
    public static byte SEGMENT_MARKER_SOF0 = (byte)0xC0;

    /**
     * Creates a JpegSegmentReader for a specific file.
     * @param file the Jpeg file to read segments from
     */
    public JpegSegmentReader(File file)
    {
        _file = file;
    }

    /**
     * Determines if the file passed to the constructor of this class is a Jpeg file.  Note
     * that this implementation simply checks the magic number, and doesn't detect corrupt
     * files.
     * @return true, if a legal jpeg file, otherwise false
     * @throws IOException for any problem in reading the file
     */
    public boolean isJpeg() throws IOException
    {
        BufferedInputStream fileStream = new BufferedInputStream(new FileInputStream(_file));
        try {
            return isValidJpegHeaderBytes(fileStream);
        } finally {
            fileStream.close();
        }
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
        return ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8);
    }

    /**
     * Reads the first instance of a given Jpeg segment, returning the contents as
     * a byte array.
     * TODO allow user to specify the subsequent instances of a segment marker, or return a 2D array (overload of method)
     * @param segmentMarker the byte identifier for the desired segment
     * @return the byte array if found, else null
     * @throws JpegProcessingException for any problems processing the Jpeg data,
     *         including inner IOExceptions
     */
    public byte[] readSegment(int segmentMarker) throws JpegProcessingException
    {
        InputStream fileStream = null;
        int offset = 0;
        try {
            fileStream = new BufferedInputStream(new FileInputStream(_file));
            // first two bytes should be jpeg magic number
            if (!isValidJpegHeaderBytes(fileStream)) {
                throw new IOException("not a jpeg file");
            }
            offset += 2;
            do {
                // next byte is 0xFF
                byte segmentIdentifier = (byte)(fileStream.read() & 0xFF);
                if ((segmentIdentifier & 0xFF) != 0xFF) {
                    throw new JpegProcessingException("expected jpeg segment start identifier 0xFF at offset " + offset + ", not 0x" + Integer.toHexString(segmentIdentifier & 0xFF));
                }
                offset++;
                // next byte is <segment-marker>
                byte thisSegmentMarker = (byte)(fileStream.read() & 0xFF);
                offset++;
                // next 2-bytes are <segment-size>: [high-byte] [low-byte]
                byte[] segmentLengthBytes = new byte[2];
                fileStream.read(segmentLengthBytes, 0, 2);
                offset += 2;
                int segmentLength = ((segmentLengthBytes[0] << 8) & 0xFF00) | (segmentLengthBytes[1] & 0xFF);
                // segment length includes size bytes, so subtract two
                segmentLength -= 2;
                if (segmentLength > fileStream.available()) {
                    throw new JpegProcessingException("segment size would extend beyond file stream length");
                }
                byte[] segmentBytes = new byte[segmentLength];
                fileStream.read(segmentBytes, 0, segmentLength);
                offset += segmentLength;
                if ((thisSegmentMarker & 0xFF) == (segmentMarker & 0xFF)) {
                    return segmentBytes;
                } else if ((thisSegmentMarker & 0xFF) == (SEGMENT_MARKER_SOS & 0xFF)) {
                    // The 'Start-Of-Scan' segment's length doesn't include the image data, instead would
                    // have to search for the two bytes: 0xFF 0xD9 (EOI).
                    // It comes last so simply return at this point
                    return null;
                } else if ((thisSegmentMarker & 0xFF) == (SEGMENT_MARKER_EOI & 0xFF)) {
                    // the 'End-Of-Image' segment -- this should never be found in this fashion
                    return null;
                }
                // didn't find the one we're looking for, loop through to the next segment
            } while (true);
        } catch (IOException ioe) {
            throw new JpegProcessingException("IOException processing Jpeg file", ioe);
        } finally {
            try {
                if (fileStream != null) fileStream.close();
            } catch (IOException ioe) {
                throw new JpegProcessingException("IOException processing Jpeg file", ioe);
            }
        }
    }
}

/*
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
 * Created by Drew Noakes on Apr 25, 2011 using IntelliJ IDEA.
 */
package com.drew.metadata.jfif;

import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.MetadataReader;

/**
 * Reader for JFIF data, found in the APP0 Jpeg segment.
 * <p/>
 * More info at: http://en.wikipedia.org/wiki/JPEG_File_Interchange_Format
 *
 * @author YB, Drew Noakes
 */
public class JfifReader implements MetadataReader
{
    // TODO add unit tests for JFIF data

    /**
     * The SOF0 data segment.
     */
    private final byte[] _data;

//    /**
//     * Creates a new JfifReader for the specified Jfif jpegFile.
//     */
//    public JfifReader(File jpegFile) throws JpegProcessingException
//    {
//        this(new JpegSegmentReader(jpegFile).readSegment(JpegSegmentReader.SEGMENT_APP0));
//    }
//
//    /** Creates a JfifReader for a JFIF stream.
//     *
//     * @param is JFIF stream. Stream will be closed.
//     */
//    public JfifReader(InputStream is) throws JpegProcessingException
//    {
//        this(new JpegSegmentReader(is).readSegment(JpegSegmentReader.SEGMENT_APP0));
//    }

    /**
     * Initialises a new JfifReader for a given byte array.
     * @param data the byte array to read Jfif data from
     */
    public JfifReader(byte[] data)
    {
        _data = data;
    }

    /**
     * Performs the Jfif data extraction, adding found values to the specified
     * instance of <code>Metadata</code>.
     */
    public void extract(Metadata metadata)
    {
        if (_data==null)
            return;

        JfifDirectory directory = (JfifDirectory)metadata.getDirectory(JfifDirectory.class);

        try {
            int ver = get32Bits(JfifDirectory.TAG_JFIF_VERSION);
            directory.setInt(JfifDirectory.TAG_JFIF_VERSION, ver);

            int units = get16Bits(JfifDirectory.TAG_JFIF_UNITS);
            directory.setInt(JfifDirectory.TAG_JFIF_UNITS, units);

            int height = get32Bits(JfifDirectory.TAG_JFIF_RESX);
            directory.setInt(JfifDirectory.TAG_JFIF_RESX, height);

            int width = get32Bits(JfifDirectory.TAG_JFIF_RESY);
            directory.setInt(JfifDirectory.TAG_JFIF_RESY, width);

        } catch (MetadataException me) {
            directory.addError("MetadataException: " + me);
        }
    }

    /**
     * Returns an int calculated from two bytes of data at the specified offset (MSB, LSB).
     * @param offset position within the data buffer to read first byte
     * @return the 32 bit int value, between 0x0000 and 0xFFFF
     */
    private int get32Bits(int offset) throws MetadataException
    {
        if (offset+1>=_data.length) {
            throw new MetadataException("Attempt to read bytes from outside Jfif segment data buffer");
        }

        return ((_data[offset] & 255) << 8) | (_data[offset + 1] & 255);
    }

    /**
     * Returns an int calculated from one byte of data at the specified offset.
     * @param offset position within the data buffer to read byte
     * @return the 16 bit int value, between 0x00 and 0xFF
     */
    private int get16Bits(int offset) throws MetadataException
    {
        if (offset>=_data.length) {
            throw new MetadataException("Attempt to read bytes from outside Jfif segment data buffer");
        }

        return (_data[offset] & 255);
    }
}
/*
 * Created by dnoakes on 12-Nov-2002 19:00:03 using IntelliJ IDEA.
 */
package com.drew.metadata.iptc;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.metadata.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

/**
 *
 */
public class IptcReader implements MetadataReader
{
/*
    public static final int DIRECTORY_IPTC = 2;

    public static final int ENVELOPE_RECORD = 1;
    public static final int APPLICATION_RECORD_2 = 2;
    public static final int APPLICATION_RECORD_3 = 3;
    public static final int APPLICATION_RECORD_4 = 4;
    public static final int APPLICATION_RECORD_5 = 5;
    public static final int APPLICATION_RECORD_6 = 6;
    public static final int PRE_DATA_RECORD = 7;
    public static final int DATA_RECORD = 8;
    public static final int POST_DATA_RECORD = 9;
*/
    /**
     * The Iptc data segment.
     */
    private final byte[] _data;

    /**
     * Creates a new IptcReader for the specified Jpeg jpegFile.
     */
    public IptcReader(File jpegFile) throws JpegProcessingException, FileNotFoundException
    {
        this(new JpegSegmentReader(jpegFile).readSegment(JpegSegmentReader.SEGMENT_APPD));
    }

    public IptcReader(byte[] data)
    {
        _data = data;
    }

    /**
     * Performs the Exif data extraction, returning a new instance of <code>Metadata</code>.
     * @throws com.drew.metadata.iptc.IptcProcessingException for bad/unexpected Exif data
     */
    public Metadata extract() throws MetadataException
    {
        return extract(new Metadata());
    }

    /**
     * Performs the Exif data extraction, adding found values to the specified
     * instance of <code>Metadata</code>.
     * @throws com.drew.metadata.iptc.IptcProcessingException for bad/unexpected Exif data
     */
    public Metadata extract(Metadata metadata) throws MetadataException
    {
        if (_data == null) {
            return metadata;
        }

        // find start of data
        int offset = 0;
        while (offset < _data.length - 1 && get32Bits(offset) != 0x1c02) {
            offset++;
        }

        Directory directory = metadata.getDirectory(IptcDirectory.class);

        // for each tag
        while (offset < _data.length) {
            // identifies start of a tag
            if (_data[offset] != 0x1c) {
                break;
            }
            // we need at least five bytes left to read a tag
            if ((offset + 5) >= _data.length) {
                break;
            }

            offset++;

            int directoryType = _data[offset++];
            int tagType = _data[offset++];
            int tagByteCount = get32Bits(offset);

            if (tagByteCount < 0) {
                System.out.println("tagByteCount < 0 0x" + Integer.toHexString(tagByteCount));
            }
            offset += 2;
            if ((offset + tagByteCount) > _data.length) {
                System.err.println("data for tag extends beyond end of iptc segment");
                break;
            }

            processTag(directory, directoryType, tagType, offset, tagByteCount);
            offset += tagByteCount;
        }

        return metadata;
    }

    /**
     * Returns an int calculated from two bytes of data at the specified offset (MSB, LSB).
     * @param offset position within the data buffer to read first byte
     * @return the 32 bit int value, between 0x0000 and 0xFFFF
     */
    private int get32Bits(int offset) throws MetadataException
    {
        if (offset >= _data.length) {
            throw new MetadataException("Attempt to read bytes from outside Iptc data buffer");
        }
        return ((_data[offset] & 255) << 8) | (_data[offset + 1] & 255);
    }

    /**
     * This method serves as marsheller of objects for dataset. It converts from IPTC
     * octets to relevant java object.
     */
    private void processTag(Directory directory, int directoryType, int tagType, int offset, int tagByteCount)
    {
        int tagIdentifier = tagType | (directoryType << 8);

        switch (tagIdentifier) {
            case IptcDirectory.TAG_RECORD_VERSION:
                // short
                short shortValue = (short)((_data[offset] << 8) | _data[offset + 1]);
                directory.setInt(tagIdentifier, shortValue);
                return;
            case IptcDirectory.TAG_URGENCY:
                // byte
                directory.setInt(tagIdentifier, _data[offset]);
                return;
            case IptcDirectory.TAG_RELEASE_DATE:
            case IptcDirectory.TAG_DATE_CREATED:
                // Date object
                if (tagByteCount >= 8) {
                    String dateStr = new String(_data, offset, tagByteCount);
                    try {
                        int year = Integer.parseInt(dateStr.substring(0, 4));
                        int month = Integer.parseInt(dateStr.substring(4, 6)) - 1;
                        int day = Integer.parseInt(dateStr.substring(6, 8));
                        Date date = (new java.util.GregorianCalendar(year, month, day)).getTime();
                        directory.setDate(tagIdentifier, date);
                        return;
                    } catch (NumberFormatException e) {
                        // fall through and we'll store whatever was there as a String
                    }
                }
            case IptcDirectory.TAG_RELEASE_TIME:
            case IptcDirectory.TAG_TIME_CREATED:
                // time...
            default:
                // fall through
        }
        // If no special handling by now, treat it as a string
        String str;
        if (tagByteCount < 1) {
            str = "";
        } else {
            str = new String(_data, offset, tagByteCount);
        }
        if (directory.containsTag(tagIdentifier)) {
            String[] oldStrings;
            String[] newStrings;
            try {
                oldStrings = directory.getStringArray(tagIdentifier);
            } catch (MetadataException e) {
                oldStrings = null;
            }
            if (oldStrings == null) {
                newStrings = new String[1];
            } else {
                newStrings = new String[oldStrings.length + 1];
                for (int i = 0; i < oldStrings.length; i++) {
                    newStrings[i] = oldStrings[i];
                }
            }
            newStrings[newStrings.length - 1] = str;
            directory.setStringArray(tagIdentifier, newStrings);
        } else {
            directory.setString(tagIdentifier, str);
        }
    }
}

/*
 * Copyright 2002-2011 Drew Noakes
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
package com.drew.metadata.iptc;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.MetadataReader;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Decodes IPTC binary data, populating a <code>Metadata</code> object with tag values in an <code>IptcDirectory</code>.
 *
 * @author Drew Noakes http://drewnoakes.com
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
    /** The Iptc data segment. */
    @Nullable
    private final byte[] _data;

    /** Creates an IptcReader for the given IPTC data segment. */
    public IptcReader(@Nullable byte[] data)
    {
        _data = data;
    }

    /** Performs the Exif data extraction, adding found values to the specified instance of <code>Metadata</code>. */
    public void extract(@NotNull Metadata metadata)
    {
        if (_data == null)
            return;

        Directory directory = metadata.getDirectory(IptcDirectory.class);

        // find start of data
        int offset = 0;
        try {
            while (offset < _data.length - 1 && get32Bits(offset) != 0x1c02) {
                offset++;
            }
        } catch (MetadataException e) {
            directory.addError("Couldn't find start of Iptc data (invalid segment)");
            return;
        }

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

            int directoryType;
            int tagType;
            int tagByteCount;
            try {
                directoryType = _data[offset++];
                tagType = _data[offset++];
                tagByteCount = get32Bits(offset);
            } catch (MetadataException e) {
                directory.addError("Iptc data segment ended mid-way through tag descriptor");
                return;
            }
            offset += 2;
            if ((offset + tagByteCount) > _data.length) {
                directory.addError("data for tag extends beyond end of iptc segment");
                break;
            }

            processTag(directory, directoryType, tagType, offset, tagByteCount);
            offset += tagByteCount;
        }
    }

    /**
     * Returns an int calculated from two bytes of data at the specified offset (MSB, LSB).
     *
     * @param offset position within the data buffer to read first byte
     * @return the 32 bit int value, between 0x0000 and 0xFFFF
     */
    private int get32Bits(int offset) throws MetadataException
    {
        assert (_data != null);
        if (offset >= _data.length) {
            throw new MetadataException("Attempt to read bytes from outside Iptc data buffer");
        }
        return ((_data[offset] & 255) << 8) | (_data[offset + 1] & 255);
    }

    /**
     * This method serves as marshaller of objects for dataset. It converts from IPTC
     * octets to relevant java object.
     */
    private void processTag(@NotNull Directory directory, int directoryType, int tagType, int offset, int tagByteCount)
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

        // If we haven't returned yet, treat it as a string
        String str;
        if (tagByteCount < 1) {
            str = "";
        } else {
            try {
//              str = new String(_data, offset, tagByteCount);
                str = new String(_data, offset, tagByteCount, System.getProperty("file.encoding"));
            } catch (UnsupportedEncodingException ex) {
                directory.addError("Unable to decode a string for the IPTC tag " + Integer.toHexString(tagType));
                str = "";
            }
        }

        if (directory.containsTag(tagIdentifier)) {
            // this fancy string[] business avoids using an ArrayList for performance reasons
            String[] oldStrings = directory.getStringArray(tagIdentifier);
            String[] newStrings;
            if (oldStrings == null) {
                newStrings = new String[1];
            } else {
                newStrings = new String[oldStrings.length + 1];
                System.arraycopy(oldStrings, 0, newStrings, 0, oldStrings.length);
            }
            newStrings[newStrings.length - 1] = str;
            directory.setStringArray(tagIdentifier, newStrings);
        } else {
            directory.setString(tagIdentifier, str);
        }
    }
}

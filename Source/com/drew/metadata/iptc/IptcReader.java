/*
 * Copyright 2002-2014 Drew Noakes
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
package com.drew.metadata.iptc;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

/**
 * Decodes IPTC binary data, populating a {@link Metadata} object with tag values in an {@link IptcDirectory}.
 * <p/>
 * http://www.iptc.org/std/IIM/4.1/specification/IIMV4.1.pdf
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class IptcReader implements JpegSegmentMetadataReader
{
    // TODO consider breaking the IPTC section up into multiple directories and providing segregation of each IPTC directory
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

    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Arrays.asList(JpegSegmentType.APPD);
    }

    public boolean canProcess(@NotNull final byte[] segmentBytes, @NotNull final JpegSegmentType segmentType)
    {
        // Check whether the first byte resembles
        return segmentBytes.length != 0 && segmentBytes[0] == 0x1c;
    }

    public void extract(@NotNull final byte[] segmentBytes, @NotNull final Metadata metadata, @NotNull final JpegSegmentType segmentType)
    {
        extract(new SequentialByteArrayReader(segmentBytes), metadata, segmentBytes.length);
    }

    /**
     * Performs the IPTC data extraction, adding found values to the specified instance of {@link Metadata}.
     */
    public void extract(@NotNull final SequentialReader reader, @NotNull final Metadata metadata, final long length)
    {
        final IptcDirectory directory = metadata.getOrCreateDirectory(IptcDirectory.class);

        int offset = 0;

        // for each tag
        while (offset < length) {

            // identifies start of a tag
            short startByte;
            try {
                startByte = reader.getUInt8();
                offset++;
            } catch (final IOException e) {
                directory.addError("Unable to read starting byte of IPTC tag");
                return;
            }

            if (startByte != 0x1c) {
                directory.addError("Invalid start to IPTC tag");
                return;
            }

            // we need at least five bytes left to read a tag
            if (offset + 5 >= length) {
                directory.addError("Too few bytes remain for a valid IPTC tag");
                return;
            }

            int directoryType;
            int tagType;
            int tagByteCount;
            try {
                directoryType = reader.getUInt8();
                tagType = reader.getUInt8();
                tagByteCount = reader.getUInt16();
                offset += 4;
            } catch (final IOException e) {
                directory.addError("IPTC data segment ended mid-way through tag descriptor");
                return;
            }

            if (offset + tagByteCount > length) {
                directory.addError("Data for tag extends beyond end of IPTC segment");
                return;
            }

            try {
                processTag(reader, directory, directoryType, tagType, tagByteCount);
            } catch (final IOException e) {
                directory.addError("Error processing IPTC tag");
                return;
            }

            offset += tagByteCount;
        }
    }

	private static void processTag(@NotNull final SequentialReader reader, @NotNull final Directory directory, final int directoryType,
			final int tagType, final int tagByteCount) throws IOException
    {
        final int tagIdentifier = tagType | (directoryType << 8);

        String string = null;

        switch (tagIdentifier) {
            case IptcDirectory.TAG_APPLICATION_RECORD_VERSION:
                // short
                final int shortValue = reader.getUInt16();
                reader.skip(tagByteCount - 2);
                directory.setInt(tagIdentifier, shortValue);
                return;
            case IptcDirectory.TAG_URGENCY:
                // byte
                directory.setInt(tagIdentifier, reader.getUInt8());
                reader.skip(tagByteCount - 1);
                return;
            case IptcDirectory.TAG_RELEASE_DATE:
            case IptcDirectory.TAG_DATE_CREATED:
                // Date object
                if (tagByteCount >= 8) {
                    string = reader.getString(tagByteCount);
                    try {
                        final int year = Integer.parseInt(string.substring(0, 4));
                        final int month = Integer.parseInt(string.substring(4, 6)) - 1;
                        final int day = Integer.parseInt(string.substring(6, 8));
                        final Date date = new java.util.GregorianCalendar(year, month, day).getTime();
                        directory.setDate(tagIdentifier, date);
                        return;
                    } catch (final NumberFormatException e) {
                        // fall through and we'll process the 'string' value below
                    }
                } else {
                    reader.skip(tagByteCount);
                }
            case IptcDirectory.TAG_RELEASE_TIME:
            case IptcDirectory.TAG_TIME_CREATED:
                // time...
            default:
                // fall through
        }

        // If we haven't returned yet, treat it as a string
        // NOTE that there's a chance we've already loaded the value as a string above, but failed to parse the value
        if (string == null) {
            string = reader.getString(tagByteCount, System.getProperty("file.encoding")); // "ISO-8859-1"
        }

        if (directory.containsTag(tagIdentifier)) {
            // this fancy string[] business avoids using an ArrayList for performance reasons
            final String[] oldStrings = directory.getStringArray(tagIdentifier);
            String[] newStrings;
            if (oldStrings == null) {
                newStrings = new String[1];
            } else {
                newStrings = new String[oldStrings.length + 1];
                System.arraycopy(oldStrings, 0, newStrings, 0, oldStrings.length);
            }
            newStrings[newStrings.length - 1] = string;
            directory.setStringArray(tagIdentifier, newStrings);
        } else {
            directory.setString(tagIdentifier, string);
        }
    }
}

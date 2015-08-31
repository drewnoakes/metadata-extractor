/*
 * Copyright 2002-2015 Drew Noakes
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

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.DateUtil;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

/**
 * Decodes IPTC binary data, populating a {@link Metadata} object with tag values in an {@link IptcDirectory}.
 * <p>
 * http://www.iptc.org/std/IIM/4.1/specification/IIMV4.1.pdf
 *
 * @author Drew Noakes https://drewnoakes.com
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
        return Collections.singletonList(JpegSegmentType.APPD);
    }

    public void readJpegSegments(@NotNull Iterable<byte[]> segments, @NotNull Metadata metadata, @NotNull JpegSegmentType segmentType)
    {
        for (byte[] segmentBytes : segments) {
            // Ensure data starts with the IPTC marker byte
            if (segmentBytes.length != 0 && segmentBytes[0] == 0x1c) {
                extract(new SequentialByteArrayReader(segmentBytes), metadata, segmentBytes.length);
            }
        }
    }

    /**
     * Performs the IPTC data extraction, adding found values to the specified instance of {@link Metadata}.
     */
    public void extract(@NotNull final SequentialReader reader, @NotNull final Metadata metadata, long length)
    {
        IptcDirectory directory = new IptcDirectory();
        metadata.addDirectory(directory);

        int offset = 0;

        // for each tag
        while (offset < length) {

            // identifies start of a tag
            short startByte;
            try {
                startByte = reader.getUInt8();
                offset++;
            } catch (IOException e) {
                directory.addError("Unable to read starting byte of IPTC tag");
                return;
            }

            if (startByte != 0x1c) {
                // NOTE have seen images where there was one extra byte at the end, giving
                // offset==length at this point, which is not worth logging as an error.
                if (offset != length)
                    directory.addError("Invalid IPTC tag marker at offset " + (offset - 1) + ". Expected '0x1c' but got '0x" + Integer.toHexString(startByte) + "'.");
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
                // TODO support Extended DataSet Tag (see 1.5(c), p14, IPTC-IIMV4.2.pdf)
                tagByteCount = reader.getUInt16();
                offset += 4;
            } catch (IOException e) {
                directory.addError("IPTC data segment ended mid-way through tag descriptor");
                return;
            }

            if (offset + tagByteCount > length) {
                directory.addError("Data for tag extends beyond end of IPTC segment");
                return;
            }

            try {
                processTag(reader, directory, directoryType, tagType, tagByteCount);
            } catch (IOException e) {
                directory.addError("Error processing IPTC tag");
                return;
            }

            offset += tagByteCount;
        }
    }

    private void processTag(@NotNull SequentialReader reader, @NotNull Directory directory, int directoryType, int tagType, int tagByteCount) throws IOException
    {
        int tagIdentifier = tagType | (directoryType << 8);

        // Some images have been seen that specify a zero byte tag, which cannot be of much use.
        // We elect here to completely ignore the tag. The IPTC specification doesn't mention
        // anything about the interpretation of this situation.
        // https://raw.githubusercontent.com/wiki/drewnoakes/metadata-extractor/docs/IPTC-IIMV4.2.pdf
        if (tagByteCount == 0) {
            directory.setString(tagIdentifier, "");
            return;
        }

        String string = null;

        switch (tagIdentifier) {
            case IptcDirectory.TAG_CODED_CHARACTER_SET:
                byte[] bytes = reader.getBytes(tagByteCount);
                String charset = Iso2022Converter.convertISO2022CharsetToJavaCharset(bytes);
                if (charset == null) {
                    // Unable to determine the charset, so fall through and treat tag as a regular string
                    string = new String(bytes);
                    break;
                }
                directory.setString(tagIdentifier, charset);
                return;
            case IptcDirectory.TAG_ENVELOPE_RECORD_VERSION:
            case IptcDirectory.TAG_APPLICATION_RECORD_VERSION:
            case IptcDirectory.TAG_FILE_VERSION:
            case IptcDirectory.TAG_ARM_VERSION:
            case IptcDirectory.TAG_PROGRAM_VERSION:
                // short
                if (tagByteCount >= 2) {
                    int shortValue = reader.getUInt16();
                    reader.skip(tagByteCount - 2);
                    directory.setInt(tagIdentifier, shortValue);
                    return;
                }
                break;
            case IptcDirectory.TAG_URGENCY:
                // byte
                directory.setInt(tagIdentifier, reader.getUInt8());
                reader.skip(tagByteCount - 1);
                return;
            case IptcDirectory.TAG_RELEASE_DATE:
            case IptcDirectory.TAG_DATE_CREATED:
            case IptcDirectory.TAG_DIGITAL_DATE_CREATED:
                // Date object
                if (tagByteCount >= 8) {
                    string = reader.getString(tagByteCount);
                    assert(string.length() >= 8);
                    try {
                        int year = Integer.parseInt(string.substring(0, 4));
                        int month = Integer.parseInt(string.substring(4, 6)) - 1;
                        int day = Integer.parseInt(string.substring(6, 8));
                        if (DateUtil.isValidDate(year, month, day)) {
                            Date date = new java.util.GregorianCalendar(year, month, day).getTime();
                            directory.setDate(tagIdentifier, date);
                            return;
                        }
                    } catch (NumberFormatException e) {
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
            String encoding = directory.getString(IptcDirectory.TAG_CODED_CHARACTER_SET);
            if (encoding != null) {
                string = reader.getString(tagByteCount, encoding);
            } else {
                byte[] bytes = reader.getBytes(tagByteCount);
                encoding = Iso2022Converter.guessEncoding(bytes);
                string = encoding != null ? new String(bytes, encoding) : new String(bytes);
            }
        }

        if (directory.containsTag(tagIdentifier)) {
            // this fancy string[] business avoids using an ArrayList for performance reasons
            String[] oldStrings = directory.getStringArray(tagIdentifier);
            String[] newStrings;
            if (oldStrings == null) {
                // TODO hitting this block means any prior value(s) are discarded
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

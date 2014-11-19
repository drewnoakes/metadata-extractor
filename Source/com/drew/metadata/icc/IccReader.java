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
package com.drew.metadata.icc;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

/**
 * Reads an ICC profile.
 * <ul>
 * <li>http://en.wikipedia.org/wiki/ICC_profile</li>
 * <li>http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/ICC_Profile.html</li>
 * </ul>
 *
 * @author Yuri Binev
 * @author Drew Noakes
 */
public class IccReader implements JpegSegmentMetadataReader
{
    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Arrays.asList(JpegSegmentType.APP2);
    }

    public boolean canProcess(@NotNull final byte[] segmentBytes, @NotNull final JpegSegmentType segmentType)
    {
        return segmentBytes.length > 10 && "ICC_PROFILE".equalsIgnoreCase(new String(segmentBytes, 0, 11));
    }

    public void extract(@NotNull final byte[] segmentBytes, @NotNull final Metadata metadata, @NotNull final JpegSegmentType segmentType)
    {
        // skip the first 14 bytes
        final byte[] iccProfileBytes = new byte[segmentBytes.length - 14];
        System.arraycopy(segmentBytes, 14, iccProfileBytes, 0, segmentBytes.length - 14);

        extract(new ByteArrayReader(iccProfileBytes), metadata);
    }

    public void extract(@NotNull final RandomAccessReader reader, @NotNull final Metadata metadata)
    {
        // TODO review whether the 'tagPtr' values below really do require ICC processing to work with a RandomAccessReader

        final IccDirectory directory = metadata.getOrCreateDirectory(IccDirectory.class);

        try {
            directory.setInt(IccDirectory.TAG_PROFILE_BYTE_COUNT, reader.getInt32(IccDirectory.TAG_PROFILE_BYTE_COUNT));

            // For these tags, the int value of the tag is in fact it's offset within the buffer.
            set4ByteString(directory, IccDirectory.TAG_CMM_TYPE, reader);
            setInt32(directory, IccDirectory.TAG_PROFILE_VERSION, reader);
            set4ByteString(directory, IccDirectory.TAG_PROFILE_CLASS, reader);
            set4ByteString(directory, IccDirectory.TAG_COLOR_SPACE, reader);
            set4ByteString(directory, IccDirectory.TAG_PROFILE_CONNECTION_SPACE, reader);
            setDate(directory, IccDirectory.TAG_PROFILE_DATETIME, reader);
            set4ByteString(directory, IccDirectory.TAG_SIGNATURE, reader);
            set4ByteString(directory, IccDirectory.TAG_PLATFORM, reader);
            setInt32(directory, IccDirectory.TAG_CMM_FLAGS, reader);
            set4ByteString(directory, IccDirectory.TAG_DEVICE_MAKE, reader);

            final int temp = reader.getInt32(IccDirectory.TAG_DEVICE_MODEL);
            if (temp != 0) {
                if (temp <= 0x20202020) {
                    directory.setInt(IccDirectory.TAG_DEVICE_MODEL, temp);
                } else {
                    directory.setString(IccDirectory.TAG_DEVICE_MODEL, getStringFromInt32(temp));
                }
            }

            setInt32(directory, IccDirectory.TAG_RENDERING_INTENT, reader);
            setInt64(directory, IccDirectory.TAG_DEVICE_ATTR, reader);

            final float[] xyz = new float[]{
                    reader.getS15Fixed16(IccDirectory.TAG_XYZ_VALUES),
                    reader.getS15Fixed16(IccDirectory.TAG_XYZ_VALUES + 4),
                    reader.getS15Fixed16(IccDirectory.TAG_XYZ_VALUES + 8)
            };
            directory.setObject(IccDirectory.TAG_XYZ_VALUES, xyz);

            // Process 'ICC tags'
            final int tagCount = reader.getInt32(IccDirectory.TAG_TAG_COUNT);
            directory.setInt(IccDirectory.TAG_TAG_COUNT, tagCount);

            for (int i = 0; i < tagCount; i++) {
                final int pos = IccDirectory.TAG_TAG_COUNT + 4 + i * 12;
                final int tagType = reader.getInt32(pos);
                final int tagPtr = reader.getInt32(pos + 4);
                final int tagLen = reader.getInt32(pos + 8);
                final byte[] b = reader.getBytes(tagPtr, tagLen);
                directory.setByteArray(tagType, b);
            }
        } catch (final IOException ex) {
            directory.addError("Exception reading ICC profile: " + ex.getMessage());
        }
    }

	private static void set4ByteString(@NotNull final Directory directory, final int tagType, @NotNull final RandomAccessReader reader)
			throws IOException
    {
        final int i = reader.getInt32(tagType);
        if (i != 0)
            directory.setString(tagType, getStringFromInt32(i));
    }

	private static void setInt32(@NotNull final Directory directory, final int tagType, @NotNull final RandomAccessReader reader)
			throws IOException
    {
        final int i = reader.getInt32(tagType);
        if (i != 0)
            directory.setInt(tagType, i);
    }

    @SuppressWarnings({"SameParameterValue"})
	private static void setInt64(@NotNull final Directory directory, final int tagType, @NotNull final RandomAccessReader reader)
			throws IOException
    {
        final long l = reader.getInt64(tagType);
        if (l != 0)
            directory.setLong(tagType, l);
    }

    @SuppressWarnings({"SameParameterValue", "MagicConstant"})
	private static void setDate(@NotNull final IccDirectory directory, final int tagType, @NotNull final RandomAccessReader reader)
			throws IOException
    {
        final int y = reader.getUInt16(tagType);
        final int m = reader.getUInt16(tagType + 2);
        final int d = reader.getUInt16(tagType + 4);
        final int h = reader.getUInt16(tagType + 6);
        final int M = reader.getUInt16(tagType + 8);
        final int s = reader.getUInt16(tagType + 10);

//        final Date value = new Date(Date.UTC(y - 1900, m - 1, d, h, M, s));
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(y, m, d, h, M, s);
        final Date value = calendar.getTime();

        directory.setDate(tagType, value);
    }

    @NotNull
    public static String getStringFromInt32(final int d)
    {
        // MSB
        final byte[] b = new byte[] {
                (byte) ((d & 0xFF000000) >> 24),
                (byte) ((d & 0x00FF0000) >> 16),
                (byte) ((d & 0x0000FF00) >> 8),
                (byte) ((d & 0x000000FF))
        };
        return new String(b);
    }
}

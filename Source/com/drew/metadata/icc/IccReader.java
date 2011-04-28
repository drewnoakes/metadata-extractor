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
package com.drew.metadata.icc;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.MetadataReader;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Reads an ICC profile.
 * <ul>
 * <li>http://en.wikipedia.org/wiki/ICC_profile</li>
 * <li>http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/ICC_Profile.html</li>
 * </ul>
 *
 * @author Yuri Binev, Drew Noakes
 */
public class IccReader implements MetadataReader
{
    @NotNull
    private final byte[] _data;

    public IccReader(@NotNull byte[] data)
    {
        if (data == null)
            throw new NullPointerException();

        _data = data;
    }

    public void extract(@NotNull Metadata metadata)
    {
        IccDirectory directory = metadata.getOrCreateDirectory(IccDirectory.class);
        directory.setByteArray(IccDirectory.TAG_ICC_PROFILE_BYTES, _data);

        try {
            directory.setInt(IccDirectory.TAG_ICC_PROFILE_BYTE_COUNT, getInt32(_data, IccDirectory.TAG_ICC_PROFILE_BYTE_COUNT));

            // For these tags, the int value of the tag is in fact it's offset within the buffer.
            set4ByteString(_data, directory, IccDirectory.TAG_ICC_CMM_TYPE);
            setInt32(_data, directory, IccDirectory.TAG_ICC_PROFILE_VERSION);
            set4ByteString(_data, directory, IccDirectory.TAG_ICC_PROFILE_CLASS);
            set4ByteString(_data, directory, IccDirectory.TAG_ICC_COLOR_SPACE);
            set4ByteString(_data, directory, IccDirectory.TAG_ICC_PROFILE_CONNECTION_SPACE);
            setDate(directory, _data, IccDirectory.TAG_ICC_PROFILE_DATETIME);
            set4ByteString(_data, directory, IccDirectory.TAG_ICC_SIGNATURE);
            set4ByteString(_data, directory, IccDirectory.TAG_ICC_PLATFORM);
            setInt32(_data, directory, IccDirectory.TAG_ICC_CMM_FLAGS);
            set4ByteString(_data, directory, IccDirectory.TAG_ICC_DEVICE_MAKE);

            int temp = getInt32(_data, IccDirectory.TAG_ICC_DEVICE_MODEL);
            if (temp != 0) {
                if (temp <= 0x20202020)
                    directory.setInt(IccDirectory.TAG_ICC_DEVICE_MODEL, temp);
                else
                    directory.setString(IccDirectory.TAG_ICC_DEVICE_MODEL, getStringFromInt32(temp));
            }

            setInt32(_data, directory, IccDirectory.TAG_ICC_RENDERING_INTENT);
            setInt64(_data, directory, IccDirectory.TAG_ICC_DEVICE_ATTR);

            float[] xyz = new float[] {
                    getS15Fixed16(IccDirectory.TAG_ICC_XYZ_VALUES),
                    getS15Fixed16(IccDirectory.TAG_ICC_XYZ_VALUES + 4),
                    getS15Fixed16(IccDirectory.TAG_ICC_XYZ_VALUES + 8)
            };
            directory.setObject(IccDirectory.TAG_ICC_XYZ_VALUES, xyz);

            // Process 'ICC tags'
            int tagCount = getInt32(_data, IccDirectory.TAG_ICC_TAG_COUNT);
            directory.setInt(IccDirectory.TAG_ICC_TAG_COUNT, tagCount);

            for (int i = 0; i < tagCount; i++) {
                int pos = 128 + 4 + i*12;
                int tagType = getInt32(_data, pos);
                int tagPtr = getInt32(_data, pos + 4);
                int tagLen = getInt32(_data, pos + 8);
                if (tagPtr + tagLen > _data.length)
                    throw new MetadataException("Tag '" + getStringFromInt32(tagType) + "' data outside segment data buffer");
                byte[] b = new byte[tagLen];
                System.arraycopy(_data, tagPtr, b, 0, tagLen);
                directory.setByteArray(tagType, b);
            }
        } catch (Exception e) {
            directory.addError(String.format("Reading ICC Header %s:%s", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    private static void set4ByteString(byte[] buffer, Directory directory, int tagType) throws MetadataException
    {
        int i = getInt32(buffer, tagType);
        if (i != 0)
            directory.setString(tagType, getStringFromInt32(i));
    }

    private static void setInt32(byte[] buffer, Directory directory, int tagType) throws MetadataException
    {
        int i = getInt32(buffer, tagType);
        if (i != 0)
            directory.setInt(tagType, i);
    }

    private static void setInt64(byte[] buffer, Directory directory, int tagType) throws MetadataException
    {
        long l = getInt64(buffer, tagType);
        if (l != 0)
            directory.setLong(tagType, l);
    }

    private void setDate(final IccDirectory directory, final byte[] buffer, final int tagType) throws MetadataException
    {
        final int y = getInt16(buffer, tagType);
        final int m = getInt16(buffer, tagType + 2);
        final int d = getInt16(buffer, tagType + 4);
        final int h = getInt16(buffer, tagType + 6);
        final int M = getInt16(buffer, tagType + 8);
        final int s = getInt16(buffer, tagType + 10);

//        final Date value = new Date(Date.UTC(y - 1900, m - 1, d, h, M, s));
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(y, m, d, h, M, s);
        final Date value = calendar.getTime();

        directory.setDate(tagType, value);
    }

    private float getS15Fixed16(int offset) throws MetadataException
    {
        return getS15Fixed16(_data, offset);
    }

    public static float getS15Fixed16(@NotNull byte[] b, int offset) throws MetadataException
    {
        if (offset + 3 >= b.length)
            throw new MetadataException("Not enough bytes in buffer to read float32.");
        
        float res = (b[offset    ] & 255) << 8
                  + (b[offset + 1] & 255);
        int d =     (b[offset + 2] & 255) << 8
                  + (b[offset + 3] & 255);

        return res + d / 65536.0f;
    }

    public static long getInt64(@NotNull byte[] b, int offset) throws MetadataException
    {
        if (offset + 8 > b.length)
            throw new MetadataException("Not enough bytes in buffer to read int64.");

        return ((long)(b[offset + 7] & 0xFF)) |
               ((long)(b[offset + 6] & 0xFF)) << 8 |
               ((long)(b[offset + 5] & 0xFF)) << 16 |
               ((long)(b[offset + 4] & 0xFF)) << 24 |
               ((long)(b[offset + 3] & 0xFF)) << 32 |
               ((long)(b[offset + 2] & 0xFF)) << 40 |
               ((long)(b[offset + 1] & 0xFF)) << 48 |
               ((long)(b[offset    ] & 0xFF)) << 56;
    }

    public static int getInt32(@NotNull byte[] b, int offset) throws MetadataException
    {
        if (offset + 4 > b.length)
            throw new MetadataException("Not enough bytes in buffer to read int32.");

        return b[offset + 3] & 0xFF |
              (b[offset + 2] & 0xFF) << 8 |
              (b[offset + 1] & 0xFF) << 16 |
              (b[offset    ] & 0xFF) << 24;
    }

    public static int getInt16(@NotNull byte[] b, int offset) throws MetadataException
    {
        if (offset + 2 > b.length)
            throw new MetadataException("Not enough bytes in buffer to read int16.");

        return (b[offset + 1] & 0xFF) |
               (b[offset    ] & 0xFF) << 8;
    }

    @NotNull
    public static String getStringFromInt32(int d)
    {
        byte[] b = new byte[] {
                (byte)((d & 0xFF000000) >> 24),
                (byte)((d & 0x00FF0000) >> 16),
                (byte)((d & 0x0000FF00) >> 8),
                (byte)((d & 0x000000FF))
        };
        return new String(b);
    }

}

/*
 * Copyright 2002-2017 Drew Noakes
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

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.imaging.jpeg.JpegSegment;
import com.drew.lang.ReaderInfo;
import com.drew.lang.DateUtil;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.util.Collections;

/**
 * Reads an ICC profile.
 * <p>
 * More information about ICC:
 * <ul>
 * <li>http://en.wikipedia.org/wiki/ICC_profile</li>
 * <li>http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/ICC_Profile.html</li>
 * <li>https://developer.apple.com/library/mac/samplecode/ImageApp/Listings/ICC_h.html</li>
 * </ul>
 *
 * @author Yuri Binev
 * @author Drew Noakes https://drewnoakes.com
 */
public class IccReader implements JpegSegmentMetadataReader
{
    public static final String JPEG_SEGMENT_ID = "ICC";
    public static final String JPEG_SEGMENT_PREAMBLE = "ICC_PROFILE";
        
    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Collections.singletonList(JpegSegmentType.APP2);
    }

    public void readJpegSegments(@NotNull Iterable<JpegSegment> segments, @NotNull Metadata metadata) throws IOException
    {
        // NOTE the header is 14 bytes, while "ICC_PROFILE" is 11
        final int preambleLength = 14;

        // ICC data can be spread across multiple JPEG segments.
        // We concat them together in this buffer for later processing.
        byte[] buffer = null;

        for (JpegSegment segment : segments) {
            // Skip any segments that do not contain the required preamble
            if (segment.getReader().getLength() < preambleLength || !JPEG_SEGMENT_ID.equalsIgnoreCase(segment.getPreamble()))
                continue;

            // NOTE we ignore three bytes here -- are they useful for anything?

            ReaderInfo segmentReader = segment.getReader();
            // Grow the buffer
            if (buffer == null) {
                buffer = new byte[(int)segmentReader.getLength() - preambleLength];
                // skip the first 14 bytes
                System.arraycopy(segmentReader.toArray(), preambleLength, buffer, 0, (int)segmentReader.getLength() - preambleLength);
            } else {
                byte[] newBuffer = new byte[buffer.length + (int)segmentReader.getLength() - preambleLength];
                System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                System.arraycopy(segmentReader.toArray(), preambleLength, newBuffer, buffer.length, (int)segmentReader.getLength() - preambleLength);
                buffer = newBuffer;
            }
        }

        if (buffer != null)
            extract(ReaderInfo.createFromArray(buffer), metadata);
    }

    public void extract(@NotNull ReaderInfo reader, @NotNull final Metadata metadata) throws IOException
    {
        extract(reader, metadata, null);
    }

    public void extract(@NotNull ReaderInfo reader, @NotNull final Metadata metadata, @Nullable Directory parentDirectory) throws IOException
    {
        if (!reader.isMotorolaByteOrder())
            reader = reader.Clone(false);

        // TODO review whether the 'tagPtr' values below really do require RandomAccessReader or whether SequentialReader may be used instead

        IccDirectory directory = new IccDirectory();

        if (parentDirectory != null)
            directory.setParent(parentDirectory);

        try {
            int profileByteCount = reader.getInt32(IccDirectory.TAG_PROFILE_BYTE_COUNT);
            directory.setInt(IccDirectory.TAG_PROFILE_BYTE_COUNT, profileByteCount);

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

            int temp = reader.getInt32(IccDirectory.TAG_DEVICE_MODEL);
            if (temp != 0) {
                if (temp <= 0x20202020) {
                    directory.setInt(IccDirectory.TAG_DEVICE_MODEL, temp);
                } else {
                    directory.setString(IccDirectory.TAG_DEVICE_MODEL, getStringFromInt32(temp));
                }
            }

            setInt32(directory, IccDirectory.TAG_RENDERING_INTENT, reader);
            setInt64(directory, IccDirectory.TAG_DEVICE_ATTR, reader);

            float[] xyz = new float[]{
                    reader.getS15Fixed16(IccDirectory.TAG_XYZ_VALUES),
                    reader.getS15Fixed16(IccDirectory.TAG_XYZ_VALUES + 4),
                    reader.getS15Fixed16(IccDirectory.TAG_XYZ_VALUES + 8)
            };
            directory.setObject(IccDirectory.TAG_XYZ_VALUES, xyz);

            // Process 'ICC tags'
            int tagCount = reader.getInt32(IccDirectory.TAG_TAG_COUNT);
            directory.setInt(IccDirectory.TAG_TAG_COUNT, tagCount);

            for (int i = 0; i < tagCount; i++) {
                int pos = IccDirectory.TAG_TAG_COUNT + 4 + i * 12;
                int tagType = reader.getInt32(pos);
                int tagPtr = reader.getInt32(pos + 4);
                int tagLen = reader.getInt32(pos + 8);
                byte[] b = reader.getBytes(tagPtr, tagLen);
                directory.setByteArray(tagType, b);
            }
        } catch (IOException ex) {
            directory.addError("Exception reading ICC profile: " + ex.getMessage());
        }

        metadata.addDirectory(directory);
    }

    private void set4ByteString(@NotNull Directory directory, int tagType, @NotNull ReaderInfo reader) throws IOException
    {
        int i = reader.getInt32(tagType);
        if (i != 0)
            directory.setString(tagType, getStringFromInt32(i));
    }

    private void setInt32(@NotNull Directory directory, int tagType, @NotNull ReaderInfo reader) throws IOException
    {
        int i = reader.getInt32(tagType);
        if (i != 0)
            directory.setInt(tagType, i);
    }

    @SuppressWarnings({"SameParameterValue"})
    private void setInt64(@NotNull Directory directory, int tagType, @NotNull ReaderInfo reader) throws IOException
    {
        long l = reader.getInt64(tagType);
        if (l != 0)
            directory.setLong(tagType, l);
    }

    @SuppressWarnings({"SameParameterValue", "MagicConstant"})
    private void setDate(@NotNull final IccDirectory directory, final int tagType, @NotNull ReaderInfo reader) throws IOException
    {
        final int y = reader.getUInt16(tagType);
        final int m = reader.getUInt16(tagType + 2);
        final int d = reader.getUInt16(tagType + 4);
        final int h = reader.getUInt16(tagType + 6);
        final int M = reader.getUInt16(tagType + 8);
        final int s = reader.getUInt16(tagType + 10);

        if (DateUtil.isValidDate(y, m - 1, d) && DateUtil.isValidTime(h, M, s))
        {
            String dateString = String.format("%04d:%02d:%02d %02d:%02d:%02d", y, m, d, h, M, s);
            directory.setString(tagType, dateString);
        }
        else
        {
            directory.addError(String.format(
                "ICC data describes an invalid date/time: year=%d month=%d day=%d hour=%d minute=%d second=%d",
                y, m, d, h, M, s));
        }
    }

    @NotNull
    public static String getStringFromInt32(int d)
    {
        // MSB
        byte[] b = new byte[] {
                (byte) ((d & 0xFF000000) >> 24),
                (byte) ((d & 0x00FF0000) >> 16),
                (byte) ((d & 0x0000FF00) >> 8),
                (byte) ((d & 0x000000FF))
        };
        return new String(b);
    }
}

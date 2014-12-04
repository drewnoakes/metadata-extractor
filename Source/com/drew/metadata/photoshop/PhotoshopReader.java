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
package com.drew.metadata.photoshop;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;
import com.drew.metadata.iptc.IptcReader;

import java.io.IOException;
import java.util.Arrays;

/**
 * Reads metadata created by Photoshop and stored in the APPD segment of JPEG files.
 * Note that IPTC data may be stored within this segment, in which case this reader will
 * create both a {@link PhotoshopDirectory} and a {@link com.drew.metadata.iptc.IptcDirectory}.
 *
 * @author Yuri Binev, Drew Noakes https://drewnoakes.com
 */
public class PhotoshopReader implements JpegSegmentMetadataReader, MetadataReader
{
    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Arrays.asList(JpegSegmentType.APPD);
    }

    public boolean canProcess(@NotNull byte[] segmentBytes, @NotNull JpegSegmentType segmentType)
    {
        return segmentBytes.length > 12 && "Photoshop 3.0".equals(new String(segmentBytes, 0, 13));
    }

    public void extract(@NotNull byte[] segmentBytes, @NotNull Metadata metadata, @NotNull JpegSegmentType segmentType)
    {
        extract(new ByteArrayReader(segmentBytes), metadata);
    }

    public void extract(@NotNull final RandomAccessReader reader, final @NotNull Metadata metadata)
    {
        final PhotoshopDirectory directory = metadata.getOrCreateDirectory(PhotoshopDirectory.class);

        int pos;
        try {
            pos = reader.getString(0, 13).equals("Photoshop 3.0") ? 14 : 0;
        } catch (IOException e) {
            directory.addError("Unable to read header");
            return;
        }

        long length;
        try {
            length = reader.getLength();
        } catch (IOException e) {
            directory.addError("Unable to read Photoshop data: " + e.getMessage());
            return;
        }

        while (pos < length) {
            try {
                // 4 bytes for the signature.  Should always be "8BIM".
                //String signature = new String(data, pos, 4);
                pos += 4;

                // 2 bytes for the resource identifier (tag type).
                int tagType = reader.getUInt16(pos); // segment type
                pos += 2;

                // A variable number of bytes holding a pascal string (two leading bytes for length).
                int descriptionLength = reader.getUInt16(pos);
                pos += 2;
                // Some basic bounds checking
                if (descriptionLength < 0 || descriptionLength + pos > length)
                    return;
                //String description = new String(data, pos, descriptionLength);
                pos += descriptionLength;
                // The number of bytes is padded with a trailing zero, if needed, to make the size even.
                if (pos % 2 != 0)
                    pos++;

                // 4 bytes for the size of the resource data that follows.
                int byteCount = reader.getInt32(pos);
                pos += 4;
                // The resource data.
                byte[] tagBytes = reader.getBytes(pos, byteCount);
                pos += byteCount;
                // The number of bytes is padded with a trailing zero, if needed, to make the size even.
                if (pos % 2 != 0)
                    pos++;

                directory.setByteArray(tagType, tagBytes);

                // TODO allow rebasing the reader with a new zero-point, rather than copying data here
                if (tagType == PhotoshopDirectory.TAG_IPTC)
                    new IptcReader().extract(new SequentialByteArrayReader(tagBytes), metadata, tagBytes.length);

                if (tagType >= 0x0fa0 && tagType <= 0x1387)
                    PhotoshopDirectory._tagNameMap.put(tagType, String.format("Plug-in %d Data", tagType - 0x0fa0 + 1));
            } catch (IOException ex) {
                directory.addError(ex.getMessage());
                return;
            }
        }
    }
}

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
package com.drew.metadata.photoshop;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;
import com.drew.metadata.iptc.IptcReader;

/** @author Yuri Binev, Drew Noakes http://drewnoakes.com */
public class PhotoshopReader implements MetadataReader
{
    @NotNull
    private final byte[] _data;

    public PhotoshopReader(@NotNull byte[] data)
    {
        if (data==null)
            throw new NullPointerException();
        _data = data;
    }

    public void extract(@NotNull Metadata metadata)
    {
        final PhotoshopDirectory directory = metadata.getOrCreateDirectory(PhotoshopDirectory.class);
        final BufferReader reader = new BufferReader(_data);

        int pos = new String(_data, 0, 13).equals("Photoshop 3.0") ? 14 : 0;

        while (pos < _data.length) {
            try {
                // 4 bytes for the signature.  Should always be "8BIM".
                String signature = new String(_data, pos, 4);
                pos += 4;

                // 2 bytes for the resource identifier (tag type).
                int tagType = reader.getUInt16(pos); // segment type
                pos += 2;

                // A variable number of bytes holding a pascal string (two leading bytes for length).
                int descriptionLength = reader.getUInt16(pos);
                pos += 2;
                // Some basic bounds checking
                if (descriptionLength < 0 || descriptionLength + pos > _data.length)
                    return;
                String description = new String(_data, pos, descriptionLength);
                pos += descriptionLength;
                // The number of bytes is padded with a trailing zero, if needed, to make the size even.
                if (pos % 2 == 1)
                    pos++;

                // 4 bytes for the size of the resource data that follows.
                int byteCount = reader.getInt32(pos);
                pos += 4;
                // The resource data.
                byte[] tagBytes = reader.getBytes(pos, byteCount);
                pos += byteCount;
                // The number of bytes is padded with a trailing zero, if needed, to make the size even.
                if (pos % 2 == 1)
                    pos++;

                directory.setByteArray(tagType, tagBytes);

                if (tagType == PhotoshopDirectory.TAG_PHOTOSHOP_IPTC)
                    new IptcReader(tagBytes).extract(metadata);

                if (tagType >= 0x0fa0 && tagType <= 0x1387)
                    PhotoshopDirectory._tagNameMap.put(tagType, String.format("Plug-in %d Data", tagType - 0x0fa0 + 1));
            } catch (BufferBoundsException ex) {
                directory.addError(ex.getMessage());
                return;
            }
        }
    }
}

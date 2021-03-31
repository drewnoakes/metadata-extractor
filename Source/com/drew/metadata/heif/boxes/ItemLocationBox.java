/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialReader;

import java.io.IOException;
import java.util.*;

/**
 * ISO/IEC 14496-12:2015 pg.77-80
 */
public class ItemLocationBox extends FullBox
{
    int indexSize;
    int offsetSize;
    int lengthSize;
    int baseOffsetSize;
    long itemCount;
    long itemID;
    int constructionMethod;
    int dataReferenceIndex;
    long baseOffset;
    int extentCount;
    SortedSet<Extent> extents = new TreeSet<Extent>(new Comparator<Extent>() {
        @Override
        public int compare(Extent left, Extent right) {
            return (left.offset < right.offset) ? -1 : ((left.offset == right.offset) ? 0 : 1);
        }
    });

    public ItemLocationBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        int holder = reader.getUInt8();
        offsetSize = (holder & 0xF0) >> 4;
        lengthSize = (holder & 0x0F);

        holder = reader.getUInt8();
        baseOffsetSize = (holder & 0xF0) >> 4;
        if ((version == 1) || (version == 2)) {
            indexSize = (holder & 0x0F);
        } else {
            // Reserved
        }
        if (version < 2) {
            itemCount = reader.getUInt16();
        } else if (version == 2) {
            itemCount = reader.getUInt32();
        }
        for (int i = 0; i < itemCount; i++) {
            if (version < 2) {
                itemID = reader.getUInt16();
            } else if (version == 2) {
                itemID = reader.getUInt32();
            }
            if ((version == 1) || (version == 2)) {
                holder = reader.getUInt16();
                constructionMethod = (holder & 0x000F);
            }
            dataReferenceIndex = reader.getUInt16();
            if (baseOffsetSize == 4) {
                baseOffset = reader.getInt32();
            } else if (baseOffsetSize == 8){
                baseOffset = reader.getInt64();
            } else {
                baseOffset = 0;
            }
            extentCount = reader.getUInt16();

            Long extentIndex = null;
            long extentOffset;
            long extentLength;
            for (int j = 0; j < extentCount; j++) {
                if ((version == 1) || (version == 2) && (indexSize > 0)) {
                    extentIndex = getIntFromUnknownByte(indexSize, reader);
                }
                extentOffset = getIntFromUnknownByte(offsetSize, reader);
                extentLength = getIntFromUnknownByte(lengthSize, reader);
                extents.add(new Extent(itemID, extentIndex, extentOffset + baseOffset, extentLength));
            }
        }
    }

    public Long getIntFromUnknownByte(int variable, SequentialReader reader) throws IOException
    {
        switch(variable) {
            case (1):
                return (long)reader.getUInt8();
            case (2):
                return (long)reader.getUInt16();
            case (4):
                return (long)reader.getUInt32();
            case (8):
                return reader.getInt64();
            default:
                return null;
        }
    }

    public static class Extent
    {
        long itemId;
        Long index;
        long offset;
        long length;

        public Extent(long itemId, Long index, long offset, long length) {
            this.itemId = itemId;
            this.index = index;
            this.offset = offset;
            this.length = length;
        }

        public long getOffset() {
            return offset;
        }

        public long getLength() {
            return length;
        }

        public long getItemId() {
            return itemId;
        }
    }

    public SortedSet<Extent> getExtents() {
        return extents;
    }
}

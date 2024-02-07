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

import com.drew.lang.Charsets;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.metadata.heif.HeifDirectory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ISO/IEC 14496-12:2015 pg.81-83
 */
public class ItemInfoBox extends FullBox
{
    long entryCount;
    Map<Long, ItemInfoEntry> entries;

    public ItemInfoBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        if (version == 0) {
            entryCount = reader.getUInt16();
        } else {
            entryCount = reader.getUInt32();
        }
        entries = new HashMap<Long, ItemInfoEntry>();
        for (int i = 1; i <= entryCount; i++)
        {
            Box entryBox = new Box(reader);
            SequentialByteArrayReader byteReader = new SequentialByteArrayReader(reader.getBytes((int)entryBox.size - 8));
            ItemInfoEntry itemInfoEntry = new ItemInfoEntry(byteReader, entryBox);
            entries.put(itemInfoEntry.itemID, itemInfoEntry);
        }
    }

    public static class ItemInfoEntry extends FullBox
    {
        long itemID;
        long itemProtectionIndex;
        String itemName;
        String contentType;
        String contentEncoding;
        String extensionType;
        String itemType;
        String itemUriType;

        public ItemInfoEntry(SequentialReader reader, Box box) throws IOException
        {
            super(reader, box);

            // 4 Bytes for length, 4 Bytes for type. Reader is indexed from AFTER type but box.size INCLUDES the aforementioned 8 bytes
            int headerLength = 8;

            if ((version == 0) || (version == 1)) {
                itemID = reader.getUInt16();
                itemProtectionIndex = reader.getUInt16();
                itemName = reader.getNullTerminatedString((int)(box.size - reader.getPosition() - headerLength), Charsets.UTF_8, false);
                contentType = reader.getNullTerminatedString((int)(box.size - reader.getPosition() - headerLength), Charsets.UTF_8, false);
                if (box.size - reader.getPosition() - headerLength > 0) {
                    extensionType = reader.getNullTerminatedString((int) (box.size - reader.getPosition() - headerLength), Charsets.UTF_8, false);
                }
            }
            if (version == 1) {
                if (box.size - 28 >= 4) {
                    contentEncoding = reader.getString(4);
                }
            }
            if (version >= 2) {
                if (version == 2) {
                    itemID = reader.getUInt16();
                } else if (version == 3) {
                    itemID = reader.getUInt32();
                }
                itemProtectionIndex = reader.getUInt16();
                itemType = reader.getString(4);

                itemName = reader.getNullTerminatedString((int)(box.size - reader.getPosition() - headerLength), Charsets.UTF_8, false);
                if (itemType.equals("mime")) {
                    contentType = reader.getNullTerminatedString((int)(box.size - reader.getPosition() - headerLength), Charsets.UTF_8, false);
                    if (box.size - reader.getPosition() - headerLength > 0) {
                        contentEncoding = reader.getNullTerminatedString((int)(box.size - reader.getPosition() - headerLength), Charsets.UTF_8, false);
                    }
                } else if (itemType.equals("uri ")) {
                    itemUriType = reader.getString((int)(box.size - reader.getPosition() - headerLength));
                }
            }
        }

        public String getItemType() {
            return itemType;
        }
    }

    public void addMetadata(HeifDirectory directory)
    {

    }

    public ItemInfoEntry getEntry(final long id) {
        return entries.get(id);
    }
}

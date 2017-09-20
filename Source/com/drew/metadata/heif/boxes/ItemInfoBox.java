package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.metadata.heif.HeifDirectory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class ItemInfoBox extends FullBox
{
    long entryCount;
    ArrayList<ItemInfoEntry> entries;

    public ItemInfoBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        if (version == 0) {
            entryCount = reader.getUInt16();
        } else {
            entryCount = reader.getUInt32();
        }
        entries = new ArrayList<ItemInfoEntry>();
        for (int i = 1; i <= entryCount; i++)
        {
            Box entryBox = new Box(reader);
            SequentialByteArrayReader byteReader = new SequentialByteArrayReader(reader.getBytes((int)entryBox.size - 8));
            entries.add(new ItemInfoEntry(byteReader, entryBox));
        }
    }

    class ItemInfoEntry extends FullBox
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

            if ((version == 0) || (version == 1)) {
                itemID = reader.getUInt16();
                itemProtectionIndex = reader.getUInt16();
                itemName = reader.getString(4);
                contentType = reader.getString(4);
                if (box.size - 28 > 0) {
                    extensionType = reader.getString((int)box.size - 28);
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

                itemName = reader.getString(4);
                if (itemType.equals("mime")) {
                    contentType = reader.getString(4);
                } else if (itemType.equals("uri ")) {
                    itemUriType = reader.getString(4);
                }
            }
        }
    }

    public void addMetadata(HeifDirectory directory)
    {

    }
}

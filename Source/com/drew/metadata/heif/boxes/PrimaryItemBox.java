package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialReader;

import java.io.IOException;

/**
 * @author Payton Garland
 */
public class PrimaryItemBox extends FullBox
{
    long itemID;

    public PrimaryItemBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        if (version == 0) {
            itemID = reader.getUInt16();
        } else {
            itemID = reader.getUInt32();
        }
    }
}

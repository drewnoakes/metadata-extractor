package com.drew.metadata.mp4.boxes;

import com.drew.imaging.quicktime.AtomSample;
import com.drew.lang.SequentialReader;

import java.io.IOException;

public class Box extends AtomSample
{
    String usertype;

    public Box(SequentialReader reader) throws IOException
    {
        super(reader);

        if (size == 1) {
            size = reader.getInt64();
        } else if (size == 0) {
            size = -1;
        }
        if (type.equals("uuid")) {
            usertype = reader.getString(16);
        }
    }

    public Box(Box box)
    {
        super(box);

        this.usertype = box.usertype;
    }
}

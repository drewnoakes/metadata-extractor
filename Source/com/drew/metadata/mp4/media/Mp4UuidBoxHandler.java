package com.drew.metadata.mp4.media;

import com.drew.imaging.mp4.Mp4Handler;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4BoxTypes;
import com.drew.metadata.mp4.Mp4Context;
import com.drew.metadata.mp4.boxes.Box;
import com.drew.metadata.mp4.boxes.UuidBox;

import java.io.IOException;

public class Mp4UuidBoxHandler<T extends Mp4UuidBoxDirectory> extends Mp4Handler<Mp4UuidBoxDirectory> {

    public Mp4UuidBoxHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected Mp4UuidBoxDirectory getDirectory()
    {
        return new Mp4UuidBoxDirectory();
    }

    @Override
    protected boolean shouldAcceptBox(Box box)
    {
        return box.type.equals(Mp4BoxTypes.BOX_USER_DEFINED);
    }

    @Override
    protected boolean shouldAcceptContainer(Box box)
    {
        return false;
    }

    @Override
    public Mp4Handler processBox(Box box, byte[] payload, Mp4Context context) throws IOException
    {
        if (payload != null) {
            SequentialReader reader = new SequentialByteArrayReader(payload);
            UuidBox userBox = new UuidBox(reader, box);
            userBox.addMetadata(directory);
        }

        return this;
    }
}

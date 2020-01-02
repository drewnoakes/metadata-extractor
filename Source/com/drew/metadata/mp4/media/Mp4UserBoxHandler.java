package com.drew.metadata.mp4.media;

import com.drew.imaging.mp4.Mp4Handler;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4Context;
import com.drew.metadata.mp4.boxes.Box;
import com.drew.metadata.mp4.boxes.UserBox;

import java.io.IOException;

public class Mp4UserBoxHandler<T extends Mp4UserBoxDirectory> extends Mp4Handler<Mp4UserBoxDirectory> {

    public Mp4UserBoxHandler(Metadata metadata) {
        super(metadata);
    }

    @Override
    protected Mp4UserBoxDirectory getDirectory() {
        return new Mp4UserBoxDirectory();
    }

    @Override
    protected boolean shouldAcceptBox(Box box) {
        return box.type.equals("uuid");
    }

    @Override
    protected boolean shouldAcceptContainer(Box box) {
        return false;
    }

    @Override
    public Mp4Handler processBox(Box box, byte[] payload, Mp4Context context) throws IOException {
        if (payload != null) {
            SequentialReader reader = new SequentialByteArrayReader(payload);
            UserBox userBox = new UserBox(reader, box);
            userBox.addMetadata(directory);
        }

        return this;
    }
}

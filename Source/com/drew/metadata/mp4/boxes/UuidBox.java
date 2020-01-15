package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mp4.Mp4BoxTypes;
import com.drew.metadata.mp4.Mp4Directory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import static com.drew.metadata.mp4.media.Mp4UuidBoxDirectory.*;

public class UuidBox extends Box {

    private byte[] userData;

    public UuidBox(SequentialReader reader, Box box) throws IOException
    {
        super(box);

        if (type.equals(Mp4BoxTypes.BOX_USER_DEFINED)) {
            usertype = getUuid(reader.getBytes(16));
        }

        userData = reader.getBytes(reader.available());
    }

    public void addMetadata(Mp4Directory directory)
    {
        directory.setString(TAG_UUID, usertype);
        directory.setByteArray(TAG_USER_DATA, userData);
    }

    private String getUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        UUID uuid = new UUID(bb.getLong(), bb.getLong());

        return uuid.toString();
    }
}

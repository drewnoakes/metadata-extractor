package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.heif.HeifDirectory;

import java.io.IOException;

/**
 * @author Payton Garland
 */
public class ImageSpatialExtentsProperty extends FullBox
{
    long width;
    long height;

    public ImageSpatialExtentsProperty(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        width = reader.getUInt32();
        height = reader.getUInt32();
    }

    public void addMetadata(HeifDirectory directory)
    {
        if (!directory.containsTag(HeifDirectory.TAG_IMAGE_WIDTH) && !directory.containsTag(HeifDirectory.TAG_IMAGE_HEIGHT)) {
            directory.setLong(HeifDirectory.TAG_IMAGE_WIDTH, width);
            directory.setLong(HeifDirectory.TAG_IMAGE_HEIGHT, height);
        }
    }
}

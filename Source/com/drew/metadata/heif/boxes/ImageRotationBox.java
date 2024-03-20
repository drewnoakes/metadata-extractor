package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.heif.HeifDirectory;

import java.io.IOException;

/**
 * ISO/IEC 23008-12:2017 pg.15
 */
public class ImageRotationBox extends Box
{
    int angle;

    public ImageRotationBox(SequentialReader reader, Box box) throws IOException
    {
        super(box);

        // First 6 bits are reserved
        angle = reader.getUInt8() & 0x03;
    }

    public void addMetadata(HeifDirectory directory)
    {
        // There may be several images in the HEIF file, what we'd like to return is the rotation
        // of the "main" image. We assume that if there are several images, they all have the same
        // rotation (and as such we can just take the first rotation that we encounter).
        // That is a bold assumption, but should be right most of the time.
        // In the wild, the rotation attribute is mainly used for efficiency on cameras, as it avoids
        // having to rotate the whole framebuffer (the camera just records the wanted rotation). There
        // is no reason a priori to expect the camera to not use this optimization for each image in the
        // HEIF file.
        if (!directory.containsTag(HeifDirectory.TAG_IMAGE_ROTATION)) {
            directory.setInt(HeifDirectory.TAG_IMAGE_ROTATION, angle);
        }
    }
}

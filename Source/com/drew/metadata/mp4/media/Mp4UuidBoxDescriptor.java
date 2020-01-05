package com.drew.metadata.mp4.media;

import com.drew.metadata.TagDescriptor;

public class Mp4UuidBoxDescriptor extends TagDescriptor<Mp4UuidBoxDirectory> {

    public Mp4UuidBoxDescriptor(Mp4UuidBoxDirectory directory)
    {
        super(directory);
    }
}

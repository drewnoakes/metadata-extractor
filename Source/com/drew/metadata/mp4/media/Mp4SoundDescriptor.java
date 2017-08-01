package com.drew.metadata.mp4.media;

import com.drew.metadata.TagDescriptor;
import com.drew.metadata.mov.QtDescriptor;
import com.drew.metadata.mov.QtDirectory;

public class Mp4SoundDescriptor extends TagDescriptor<Mp4SoundDirectory>
{
    public Mp4SoundDescriptor(Mp4SoundDirectory directory)
    {
        super(directory);
    }
}

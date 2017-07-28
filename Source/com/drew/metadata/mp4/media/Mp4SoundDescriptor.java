package com.drew.metadata.mp4.media;

import com.drew.metadata.mov.QtDescriptor;
import com.drew.metadata.mov.QtDirectory;

public class Mp4SoundDescriptor extends QtDescriptor<Mp4SoundDirectory>
{
    public Mp4SoundDescriptor(QtDirectory directory)
    {
        super(directory);
    }
}

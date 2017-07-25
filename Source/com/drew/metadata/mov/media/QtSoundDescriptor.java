package com.drew.metadata.mov.media;

import com.drew.metadata.mov.QtDescriptor;
import com.drew.metadata.mov.QtDirectory;

public class QtSoundDescriptor extends QtDescriptor<QtSoundDirectory>
{
    public QtSoundDescriptor(QtDirectory directory)
    {
        super(directory);
    }
}

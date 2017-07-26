package com.drew.metadata.mov.media;

import com.drew.metadata.TagDescriptor;
import com.drew.metadata.mov.QtDescriptor;
import com.drew.metadata.mov.QtDirectory;

/**
 * @author Payton Garland
 */
public class QtSoundDescriptor extends TagDescriptor<QtSoundDirectory>
{
    public QtSoundDescriptor(QtSoundDirectory directory)
    {
        super(directory);
    }
}

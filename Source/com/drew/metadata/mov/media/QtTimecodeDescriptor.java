package com.drew.metadata.mov.media;

import com.drew.metadata.TagDescriptor;
import com.drew.metadata.mov.QtDescriptor;
import com.drew.metadata.mov.QtDirectory;

/**
 * @author Payton Garland
 */
public class QtTimecodeDescriptor extends TagDescriptor<QtTimecodeDirectory>
{

    public QtTimecodeDescriptor(QtTimecodeDirectory directory)
    {
        super(directory);
    }
}

package com.drew.metadata.mov.media;

import com.drew.metadata.mov.QtDescriptor;
import com.drew.metadata.mov.QtDirectory;

public class QtTimecodeDescriptor extends QtDescriptor<QtTimecodeDirectory>
{

    public QtTimecodeDescriptor(QtDirectory directory)
    {
        super(directory);
    }
}

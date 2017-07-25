package com.drew.metadata.mov.media;

import com.drew.metadata.mov.QtDescriptor;
import com.drew.metadata.mov.QtDirectory;

public class QtSubtitleDescriptor extends QtDescriptor<QtSubtitleDirectory>
{
    public QtSubtitleDescriptor(QtDirectory directory)
    {
        super(directory);
    }
}

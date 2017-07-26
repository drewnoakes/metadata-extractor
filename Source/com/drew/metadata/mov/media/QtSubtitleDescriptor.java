package com.drew.metadata.mov.media;

import com.drew.metadata.TagDescriptor;
import com.drew.metadata.mov.QtDescriptor;
import com.drew.metadata.mov.QtDirectory;

/**
 * @author Payton Garland
 */
public class QtSubtitleDescriptor extends TagDescriptor<QtSubtitleDirectory>
{
    public QtSubtitleDescriptor(QtSubtitleDirectory directory)
    {
        super(directory);
    }
}

package com.drew.metadata.mov.media;

import com.drew.metadata.TagDescriptor;
import com.drew.metadata.mov.QtDescriptor;
import com.drew.metadata.mov.QtDirectory;

/**
 * @author Payton Garland
 */
public class QtTextDescriptor extends TagDescriptor<QtTextDirectory>
{
    public QtTextDescriptor(QtTextDirectory directory)
    {
        super(directory);
    }
}

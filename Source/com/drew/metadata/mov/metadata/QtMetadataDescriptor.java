package com.drew.metadata.mov.metadata;

import com.drew.metadata.mov.QtDescriptor;
import com.drew.metadata.mov.QtDirectory;
import com.drew.metadata.mov.metadata.QtMetadataDirectory;

public class QtMetadataDescriptor extends QtDescriptor
{
    public QtMetadataDescriptor(QtDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        return super.getDescription(tagType);
    }
}

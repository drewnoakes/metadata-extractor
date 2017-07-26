package com.drew.metadata.mov.metadata;

import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;
import com.drew.metadata.mov.QtDescriptor;
import com.drew.metadata.mov.QtDirectory;
import com.drew.metadata.mov.metadata.QtMetadataDirectory;

/**
 * @author Payton Garland
 */
public class QtMetadataDescriptor extends TagDescriptor<Directory>
{
    public QtMetadataDescriptor(Directory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        return super.getDescription(tagType);
    }
}

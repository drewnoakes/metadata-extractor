package com.drew.metadata.mp3;

import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * @author Payton Garland
 */
public class Mp3Descriptor extends TagDescriptor<Mp3Directory>
{

    public Mp3Descriptor(Mp3Directory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        return super.getDescription(tagType);
    }

}

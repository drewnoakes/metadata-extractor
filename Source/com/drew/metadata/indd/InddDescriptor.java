package com.drew.metadata.indd;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

/**
 * @author Payton Garland
 */
public class InddDescriptor extends TagDescriptor<InddDirectory>
{
    public InddDescriptor(@NotNull InddDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            default:
                return super.getDescription(tagType);
        }
    }
}

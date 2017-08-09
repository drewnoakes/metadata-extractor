package com.drew.metadata.xmp.handlers;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

/**
 * @author Payton Garland
 */
public class InddXmpDescriptor extends TagDescriptor<InddXmpDirectory>
{
    public InddXmpDescriptor(@NotNull InddXmpDirectory directory)
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

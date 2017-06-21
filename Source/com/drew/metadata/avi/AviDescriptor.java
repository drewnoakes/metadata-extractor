package com.drew.metadata.avi;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * @author Payton Garland
 */
public class AviDescriptor extends TagDescriptor<AviDirectory>
{
    public AviDescriptor(@NotNull AviDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            default:
                return super.getDescription(tagType);
        }
    }
}

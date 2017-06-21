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
            case (AviDirectory.TAG_WIDTH):
            case (AviDirectory.TAG_HEIGHT):
                return getSizeDescription(tagType);
        }
        return super.getDescription(tagType);
    }

    public String getSizeDescription(int tagType)
    {
        return _directory.getString(tagType) + " pixels";
    }
}

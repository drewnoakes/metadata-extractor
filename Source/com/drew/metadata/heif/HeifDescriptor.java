package com.drew.metadata.heif;

import com.drew.metadata.TagDescriptor;

public class HeifDescriptor extends TagDescriptor<HeifDirectory>
{

    public HeifDescriptor(HeifDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case HeifDirectory.TAG_IMAGE_WIDTH:
            case HeifDirectory.TAG_IMAGE_HEIGHT:
                return getPixelDescription(tagType);
            default:
                return super.getDescription(tagType);
        }
    }

    public String getPixelDescription(int tagType)
    {
        return _directory.getString(tagType) + " pixels";
    }
}

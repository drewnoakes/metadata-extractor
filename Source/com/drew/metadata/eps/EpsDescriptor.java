package com.drew.metadata.eps;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.eps.EpsDirectory.*;

/**
 * @author Payton Garland
 */
public class EpsDescriptor extends TagDescriptor<EpsDirectory>
{

    public EpsDescriptor(@NotNull EpsDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case (TAG_IMAGE_WIDTH):
            case (TAG_IMAGE_HEIGHT):
                return getPixelDescription(tagType);
            case (TAG_TIFF_PREVIEW_SIZE):
            case (TAG_TIFF_PREVIEW_OFFSET):
                return getByteDescription(tagType);
            default:
                return _directory.getString(tagType);
        }
    }

    public String getPixelDescription(int tagType)
    {
        return _directory.getString(tagType) + " pixels";
    }

    public String getByteDescription(int tagType)
    {
        return _directory.getString(tagType) + " bytes";
    }

}

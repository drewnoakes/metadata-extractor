package com.drew.metadata.mov;

import com.drew.lang.annotations.NotNull;

public class QtVideoDescriptor extends QtDescriptor<QtVideoDirectory>
{
    public QtVideoDescriptor(@NotNull QtVideoDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case (QtVideoDirectory.TAG_HEIGHT):
            case (QtVideoDirectory.TAG_WIDTH):
                return getPixelDescription(tagType);
            case (QtVideoDirectory.TAG_DEPTH):
                return getDepthDescription(tagType);
            case (QtVideoDirectory.TAG_COLOR_TABLE):
                return getColorTableDescription(tagType);
            default:
                return super.getDescription(tagType);
        }
    }

    private String getPixelDescription(int tagType)
    {
        return _directory.getString(tagType) + " pixels";
    }

    private String getDepthDescription(int tagType)
    {
        int depth = _directory.getInteger(tagType);
        switch (depth) {
            case (40):
            case (36):
            case (34):
                return (depth - 32) + "-bit grayscale";
            default:
                return Integer.toString(depth);
        }
    }

    private String getColorTableDescription(int tagType)
    {
        int colorTableId = _directory.getInteger(tagType);

        switch (colorTableId) {
            case (-1):
                if (_directory.getInteger(QtVideoDirectory.TAG_DEPTH) < 16) {
                    return "Default";
                } else {
                    return "None";
                }
            case (0):
                return "Color table within file";
            default:
                return Integer.toString(colorTableId);
        }
    }
}

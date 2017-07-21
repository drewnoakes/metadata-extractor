package com.drew.metadata.mov;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class QtDescriptor extends TagDescriptor<QtDirectory> {

    public QtDescriptor(@NotNull QtDirectory directory) { super(directory); }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case (QtDirectory.TAG_MAJOR_BRAND):
                return getMajorBrandDescription(tagType);
            case (QtDirectory.TAG_COMPATIBLE_BRANDS):
                return getCompatibleBrandsDescription(tagType);
            case (QtDirectory.TAG_HEIGHT):
            case (QtDirectory.TAG_WIDTH):
                return getPixelDescription(tagType);
            case (QtDirectory.TAG_DEPTH):
                return getDepthDescription(tagType);
            case (QtDirectory.TAG_COLOR_TABLE):
                return getColorTableDescription(tagType);
            default:
                return _directory.getString(tagType);
        }
    }

    private String getMajorBrandDescription(int tagType)
    {
        String majorBrandKey = new String(_directory.getByteArray(tagType));
        String majorBrandValue = QtDictionary.lookup(QtDirectory.TAG_MAJOR_BRAND, majorBrandKey);
        if (majorBrandValue != null) {
            return majorBrandValue;
        } else {
            return majorBrandKey;
        }
    }

    private String getCompatibleBrandsDescription(int tagType)
    {
        String[] compatibleBrandKeys = _directory.getStringArray(tagType);
        ArrayList<String> compatibleBrandsValues = new ArrayList<String>();
        for (String compatibleBrandsKey : compatibleBrandKeys) {
            String compatibleBrandsValue = QtDictionary.lookup(QtDirectory.TAG_MAJOR_BRAND, compatibleBrandsKey);
            if (compatibleBrandsValue != null) {
                compatibleBrandsValues.add(compatibleBrandsValue);
            } else {
                compatibleBrandsValues.add(compatibleBrandsKey);
            }
        }
        return Arrays.toString(compatibleBrandsValues.toArray());
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
                if (_directory.getInteger(QtDirectory.TAG_DEPTH) < 16) {
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

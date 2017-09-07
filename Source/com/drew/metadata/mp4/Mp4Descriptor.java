package com.drew.metadata.mp4;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

import java.util.ArrayList;
import java.util.Arrays;

public class Mp4Descriptor<T extends Directory> extends TagDescriptor<Mp4Directory> {

    public Mp4Descriptor(@NotNull Mp4Directory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case (Mp4Directory.TAG_MAJOR_BRAND):
                return getMajorBrandDescription(tagType);
            case (Mp4Directory.TAG_COMPATIBLE_BRANDS):
                return getCompatibleBrandsDescription(tagType);
            default:
                return _directory.getString(tagType);
        }
    }

    private String getMajorBrandDescription(int tagType)
    {
        String majorBrandKey = new String(_directory.getByteArray(tagType));
        String majorBrandValue = Mp4Dictionary.lookup(Mp4Directory.TAG_MAJOR_BRAND, majorBrandKey);
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
            String compatibleBrandsValue = Mp4Dictionary.lookup(Mp4Directory.TAG_MAJOR_BRAND, compatibleBrandsKey);
            if (compatibleBrandsValue != null) {
                compatibleBrandsValues.add(compatibleBrandsValue);
            } else {
                compatibleBrandsValues.add(compatibleBrandsKey);
            }
        }
        return Arrays.toString(compatibleBrandsValues.toArray());
    }
}

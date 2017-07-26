package com.drew.metadata.mov;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Payton Garland
 */
public class QtDescriptor<T extends Directory> extends TagDescriptor<QtDirectory> {

    public QtDescriptor(@NotNull QtDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case (QtDirectory.TAG_MAJOR_BRAND):
                return getMajorBrandDescription(tagType);
            case (QtDirectory.TAG_COMPATIBLE_BRANDS):
                return getCompatibleBrandsDescription(tagType);
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
            compatibleBrandsValues.add(compatibleBrandsValue);
        }
        return Arrays.toString(compatibleBrandsValues.toArray());
    }
}

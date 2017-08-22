package com.drew.metadata.zip;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

/**
 * @author Payton Garland
 */
public class ZipDescriptor extends TagDescriptor<ZipDirectory>
{
    public ZipDescriptor(@NotNull ZipDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType) {
        switch (tagType) {
            case (ZipDirectory.TAG_UNCOMPRESSED_SIZE):
            case (ZipDirectory.TAG_COMPRESSED_SIZE):
            case (ZipDirectory.TAG_ENCRYPTION):
            case (ZipDirectory.TAG_COMPRESSION):
            case (ZipDirectory.TAG_COMPATIBILITY):
            case (ZipDirectory.TAG_FILE_NAME):
            case (ZipDirectory.TAG_MOD_DATE):
            case (ZipDirectory.TAG_MOD_TIME):
                return getStringArrayDescription(tagType);
            default:
                return super.getDescription(tagType);
        }
    }

    public String getStringArrayDescription(int tagType) {
        String strings = "";
        for (String string : _directory.getStringArray(tagType)) {
            strings += string + ", ";
        }
        if (!strings.equals(""))
            strings = strings.substring(0, strings.length() - 2);
        return strings;
    }
}

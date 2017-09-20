package com.drew.metadata.heif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

public class HeifDirectory extends Directory
{
    public static final int TAG_MAJOR_BRAND                             = 1;
    public static final int TAG_MINOR_VERSION                           = 2;
    public static final int TAG_COMPATIBLE_BRANDS                       = 3;

    public static final int TAG_IMAGE_WIDTH                             = 4;
    public static final int TAG_IMAGE_HEIGHT                            = 5;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_MAJOR_BRAND, "Major Brand");
        _tagNameMap.put(TAG_MINOR_VERSION, "Minor Version");
        _tagNameMap.put(TAG_COMPATIBLE_BRANDS, "Compatible Brands");
        _tagNameMap.put(TAG_IMAGE_WIDTH, "Width");
        _tagNameMap.put(TAG_IMAGE_HEIGHT, "Height");
    }

    public HeifDirectory()
    {
        this.setDescriptor(new HeifDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "HEIF";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

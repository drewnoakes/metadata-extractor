package com.drew.metadata.png;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class PngDirectory extends Directory
{
    public static final int TAG_IMAGE_WIDTH = 1;
    public static final int TAG_IMAGE_HEIGHT = 2;
    public static final int TAG_BITS_PER_SAMPLE = 3;
    public static final int TAG_COLOR_TYPE = 4;
    public static final int TAG_COMPRESSION_TYPE = 5;
    public static final int TAG_FILTER_METHOD = 6;
    public static final int TAG_INTERLACE_METHOD = 7;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_IMAGE_HEIGHT, "Image Height");
        _tagNameMap.put(TAG_IMAGE_WIDTH, "Image Width");
        _tagNameMap.put(TAG_BITS_PER_SAMPLE, "Bits Per Sample");
        _tagNameMap.put(TAG_COLOR_TYPE, "Color Type");
        _tagNameMap.put(TAG_COMPRESSION_TYPE, "Compression Type");
        _tagNameMap.put(TAG_FILTER_METHOD, "Filter Method");
        _tagNameMap.put(TAG_INTERLACE_METHOD, "Interlace Method");
    }

    public PngDirectory()
    {
        this.setDescriptor(new PngDescriptor(this));
    }

    @NotNull
    public String getName()
    {
        return "PNG";
    }

    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

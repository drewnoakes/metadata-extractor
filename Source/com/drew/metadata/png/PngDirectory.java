package com.drew.metadata.png;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Drew Noakes https://drewnoakes.com
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
    public static final int TAG_PALETTE_SIZE = 8;
    public static final int TAG_PALETTE_HAS_TRANSPARENCY = 9;
    public static final int TAG_SRGB_RENDERING_INTENT = 10;
    public static final int TAG_GAMMA = 11;
    public static final int TAG_PROFILE_NAME = 12;
    public static final int TAG_TEXTUAL_DATA = 13;
    public static final int TAG_LAST_MODIFICATION_TIME = 14;
    public static final int TAG_BACKGROUND_COLOR = 15;

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
        _tagNameMap.put(TAG_PALETTE_SIZE, "Palette Size");
        _tagNameMap.put(TAG_PALETTE_HAS_TRANSPARENCY, "Palette Has Transparency");
        _tagNameMap.put(TAG_SRGB_RENDERING_INTENT, "sRGB Rendering Intent");
        _tagNameMap.put(TAG_GAMMA, "Image Gamma");
        _tagNameMap.put(TAG_PROFILE_NAME, "ICC Profile Name");
        _tagNameMap.put(TAG_TEXTUAL_DATA, "Textual Data");
        _tagNameMap.put(TAG_LAST_MODIFICATION_TIME, "Last Modification Time");
        _tagNameMap.put(TAG_BACKGROUND_COLOR, "Background Color");
    }

    public PngDirectory()
    {
        this.setDescriptor(new PngDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "PNG";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

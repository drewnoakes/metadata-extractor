package com.drew.metadata.mkv;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

import java.util.HashMap;

public class MkvVideoDirectory extends MkvTrackDirectory
{
    public static final int TAG_PIXEL_HEIGHT = ElementIDs.PIXEL_HEIGHT;
    public static final int TAG_PIXEL_WIDTH = ElementIDs.PIXEL_WIDTH;
    public static final int TAG_FLAG_INTERLACED = ElementIDs.FLAG_INTERLACED;
    public static final int TAG_CHROMA_SITING_HORZ = ElementIDs.CHROMA_SITING_HORZ;
    public static final int TAG_CHROMA_SITING_VERT = ElementIDs.CHROMA_SITING_VERT;
    public static final int TAG_TRANSFER_CHARACTERISTICS = ElementIDs.TRANSFER_CHARACTERISTICS;
    public static final int TAG_MATRIX_COEFFICIENTS = ElementIDs.MATRIX_COEFFICIENTS;
    public static final int TAG_PRIMARIES = ElementIDs.PRIMARIES;
    public static final int TAG_RANGE = ElementIDs.RANGE;
    public static final int TAG_DISPLAY_WIDTH = ElementIDs.DISPLAY_WIDTH;
    public static final int TAG_DISPLAY_HEIGHT = ElementIDs.DISPLAY_HEIGHT;
    public static final int TAG_DISPLAY_UNIT = ElementIDs.DISPLAY_UNIT;
    /** Nominal frame rate derived from DefaultDuration, not a measured average. */
    public static final int TAG_FRAME_RATE = 1;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        addTrackTags(_tagNameMap);
        _tagNameMap.put(TAG_PIXEL_HEIGHT, "Pixel Height");
        _tagNameMap.put(TAG_PIXEL_WIDTH, "Pixel Width");
        _tagNameMap.put(TAG_FLAG_INTERLACED, "Interlaced");
        _tagNameMap.put(TAG_CHROMA_SITING_HORZ, "Horizontal Chroma Siting");
        _tagNameMap.put(TAG_CHROMA_SITING_VERT, "Vertical Chroma Siting");
        _tagNameMap.put(TAG_TRANSFER_CHARACTERISTICS, "Colour Transfer Characteristics");
        _tagNameMap.put(TAG_MATRIX_COEFFICIENTS, "Colour Matrix Coefficients");
        _tagNameMap.put(TAG_PRIMARIES, "Colour Primaries");
        _tagNameMap.put(TAG_RANGE, "Colour Range");
        _tagNameMap.put(TAG_DISPLAY_WIDTH, "Display Width");
        _tagNameMap.put(TAG_DISPLAY_HEIGHT, "Display Height");
        _tagNameMap.put(TAG_DISPLAY_UNIT, "Display Unit");
        _tagNameMap.put(TAG_FRAME_RATE, "Nominal Frame Rate");
    }

    public MkvVideoDirectory()
    {
        setDescriptor(new MkvVideoDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Matroska Video";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

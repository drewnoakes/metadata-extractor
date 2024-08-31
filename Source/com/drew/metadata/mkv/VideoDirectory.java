package com.drew.metadata.mkv;

import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

import java.util.HashMap;

import static com.drew.metadata.mkv.ElementIDs.*;

public class VideoDirectory extends Directory
{
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<>();

    static
    {
        _tagNameMap.put(TRACK_NUMBER, "Track number");
        _tagNameMap.put(TRACK_UID, "Track UID");
        _tagNameMap.put(TRACK_TYPE, "Track type");
        _tagNameMap.put(TAG_LACING, "Tag lacing");
        _tagNameMap.put(CODEC_ID, "Codec ID");
        _tagNameMap.put(LANGUAGE, "Language");
        _tagNameMap.put(LANGUAGE_BCP47, "Language BCP47");
        _tagNameMap.put(DEFAULT_DURATION, "Default duration");
        _tagNameMap.put(PIXEL_HEIGHT, "Pixel height");
        _tagNameMap.put(PIXEL_WIDTH, "Pixel width");
        _tagNameMap.put(FLAG_INTERLACED, "Interalced");
        _tagNameMap.put(CHROMA_SITING_HORZ, "Chroma siting horizontal");
        _tagNameMap.put(CHROMA_SITING_VERT, "Chroma siting vertical");
        _tagNameMap.put(TRANSFER_CHARACTERISTICS, "Color transfer characteristics");
        _tagNameMap.put(MATRIX_COEFFICIENTS, "Color matrix coefficients");
        _tagNameMap.put(PRIMARIES, "Color primaries");
        _tagNameMap.put(RANGE, "Color range");
        _tagNameMap.put(DISPLAY_WIDTH, "Display width");
        _tagNameMap.put(DISPLAY_HEIGHT, "Display height");
    }

    public VideoDirectory()
    {
        this.setDescriptor(new TagDescriptor<>(this));
    }

    @Override
    public String getName()
    {
        return "Video";
    }

    @Override
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

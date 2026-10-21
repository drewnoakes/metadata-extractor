package com.drew.metadata.mkv;

import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;
import com.drew.lang.annotations.NotNull;

import java.util.HashMap;

public class MkvSegmentDirectory extends Directory
{
    public static final int TAG_TIMESTAMP_SCALE = ElementIDs.TIMESTAMP_SCALE;
    public static final int TAG_MUXING_APP = ElementIDs.MUXING_APP;
    public static final int TAG_WRITING_APP = ElementIDs.WRITING_APP;
    public static final int TAG_DURATION = ElementIDs.DURATION;
    public static final int TAG_SEGMENT_UUID = ElementIDs.SEGMENT_UUID;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_TIMESTAMP_SCALE, "Timestamp Scale");
        _tagNameMap.put(TAG_MUXING_APP, "Muxing Application");
        _tagNameMap.put(TAG_WRITING_APP, "Writing Application");
        _tagNameMap.put(TAG_DURATION, "Duration");
        _tagNameMap.put(TAG_SEGMENT_UUID, "Segment UUID");
    }

    public MkvSegmentDirectory()
    {
        setDescriptor(new TagDescriptor<MkvSegmentDirectory>(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Matroska Segment";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

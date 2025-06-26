package com.drew.metadata.mkv;

import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

import java.util.HashMap;

import static com.drew.metadata.mkv.ElementIDs.*;

public class SegmentInfoDirectory extends Directory
{
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<>();

    static
    {
        _tagNameMap.put(TIMESTAMP_SCALE, "Timestamp scale");
        _tagNameMap.put(MUXING_APP, "Muxing app");
        _tagNameMap.put(WRITING_APP, "Writing app");
        _tagNameMap.put(DURATION, "Duration");
    }

    public SegmentInfoDirectory()
    {
        this.setDescriptor(new TagDescriptor<>(this));
    }

    @Override
    public String getName()
    {
        return "Segment";
    }

    @Override
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

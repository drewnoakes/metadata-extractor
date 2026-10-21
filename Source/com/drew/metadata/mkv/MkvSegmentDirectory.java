/*
 * Copyright 2026 Drew Noakes and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.drew.metadata.mkv;

import com.drew.metadata.Directory;
import com.drew.lang.annotations.NotNull;

import java.util.HashMap;

public class MkvSegmentDirectory extends Directory
{
    public static final int TAG_TIMESTAMP_SCALE = ElementIDs.TIMESTAMP_SCALE;
    public static final int TAG_MUXING_APP = ElementIDs.MUXING_APP;
    public static final int TAG_WRITING_APP = ElementIDs.WRITING_APP;
    public static final int TAG_DURATION = ElementIDs.DURATION;
    public static final int TAG_SEGMENT_UUID = ElementIDs.SEGMENT_UUID;
    public static final int TAG_DURATION_SECONDS = 1;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_TIMESTAMP_SCALE, "Timestamp Scale");
        _tagNameMap.put(TAG_MUXING_APP, "Muxing Application");
        _tagNameMap.put(TAG_WRITING_APP, "Writing Application");
        _tagNameMap.put(TAG_DURATION, "Duration (Segment Ticks)");
        _tagNameMap.put(TAG_SEGMENT_UUID, "Segment UUID");
        _tagNameMap.put(TAG_DURATION_SECONDS, "Duration");
    }

    public MkvSegmentDirectory()
    {
        setDescriptor(new MkvSegmentDescriptor(this));
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

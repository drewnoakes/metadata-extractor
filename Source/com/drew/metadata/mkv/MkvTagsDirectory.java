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

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

import java.util.HashMap;

/**
 * One Tags element, parented to its segment. Later Tags elements supersede
 * earlier ones in live streams; each update is retained separately in file order.
 */
public class MkvTagsDirectory extends Directory
{
    public static final int TAG_UPDATE_NUMBER = 1;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_UPDATE_NUMBER, "Tag Update Number");
    }

    public MkvTagsDirectory()
    {
        setDescriptor(new TagDescriptor<MkvTagsDirectory>(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Matroska Tags";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

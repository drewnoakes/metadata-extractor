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

import java.util.HashMap;

/**
 * The targets shared by one Tag's SimpleTag descendants.
 * Target UIDs are stored as Number arrays, including single values. Large
 * unsigned UIDs use BigInteger. An absent UID list targets the whole segment;
 * a zero UID explicitly targets all elements of the corresponding kind.
 */
public class MkvTagDirectory extends Directory
{
    public static final int TAG_TARGET_TYPE_VALUE = ElementIDs.TARGET_TYPE_VALUE;
    public static final int TAG_TARGET_TYPE = ElementIDs.TARGET_TYPE;
    public static final int TAG_TRACK_UIDS = ElementIDs.TAG_TRACK_UID;
    public static final int TAG_EDITION_UIDS = ElementIDs.TAG_EDITION_UID;
    public static final int TAG_CHAPTER_UIDS = ElementIDs.TAG_CHAPTER_UID;
    public static final int TAG_ATTACHMENT_UIDS = ElementIDs.TAG_ATTACHMENT_UID;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_TARGET_TYPE_VALUE, "Target Type Value");
        _tagNameMap.put(TAG_TARGET_TYPE, "Target Type");
        _tagNameMap.put(TAG_TRACK_UIDS, "Target Track UIDs");
        _tagNameMap.put(TAG_EDITION_UIDS, "Target Edition UIDs");
        _tagNameMap.put(TAG_CHAPTER_UIDS, "Target Chapter UIDs");
        _tagNameMap.put(TAG_ATTACHMENT_UIDS, "Target Attachment UIDs");
    }

    public MkvTagDirectory()
    {
        setDescriptor(new MkvTagDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Matroska Tag Targets";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

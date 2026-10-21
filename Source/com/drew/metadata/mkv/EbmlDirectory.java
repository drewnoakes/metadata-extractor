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
import com.drew.metadata.TagDescriptor;
import com.drew.lang.annotations.NotNull;

import java.util.HashMap;

public class EbmlDirectory extends Directory
{
    public static final int TAG_EBML_VERSION = ElementIDs.EBML_VERSION;
    public static final int TAG_EBML_READ_VERSION = ElementIDs.EBML_READ_VERSION;
    public static final int TAG_EBML_MAX_ID_LENGTH = ElementIDs.EBML_MAX_ID_LENGTH;
    public static final int TAG_EBML_MAX_SIZE_LENGTH = ElementIDs.EBML_MAX_SIZE_LENGTH;
    public static final int TAG_DOC_TYPE = ElementIDs.DOCTYPE;
    public static final int TAG_DOC_TYPE_VERSION = ElementIDs.DOCTYPE_VERSION;
    public static final int TAG_DOC_TYPE_READ_VERSION = ElementIDs.DOCTYPE_READ_VERSION;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_EBML_VERSION, "Version");
        _tagNameMap.put(TAG_EBML_READ_VERSION, "Read Version");
        _tagNameMap.put(TAG_EBML_MAX_ID_LENGTH, "Maximum ID Length");
        _tagNameMap.put(TAG_EBML_MAX_SIZE_LENGTH, "Maximum Size Length");
        _tagNameMap.put(TAG_DOC_TYPE, "Document Type");
        _tagNameMap.put(TAG_DOC_TYPE_VERSION, "Document Type Version");
        _tagNameMap.put(TAG_DOC_TYPE_READ_VERSION, "Document Type Read Version");
    }

    public EbmlDirectory()
    {
        this.setDescriptor(new TagDescriptor<Directory>(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "EBML";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

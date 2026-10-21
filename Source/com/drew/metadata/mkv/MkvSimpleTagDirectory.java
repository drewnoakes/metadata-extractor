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
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * One SimpleTag. Its parent is either its target group or its containing
 * SimpleTag. Repeated names are separate directories, never overwritten.
 */
public class MkvSimpleTagDirectory extends Directory
{
    public static final int TAG_NAME = ElementIDs.TAG_NAME;
    public static final int TAG_STRING = ElementIDs.TAG_STRING;
    public static final int TAG_BINARY = ElementIDs.TAG_BINARY;
    public static final int TAG_LANGUAGE = ElementIDs.TAG_LANGUAGE;
    public static final int TAG_LANGUAGE_BCP47 = ElementIDs.TAG_LANGUAGE_BCP47;
    public static final int TAG_DEFAULT = ElementIDs.TAG_DEFAULT;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_NAME, "Name");
        _tagNameMap.put(TAG_STRING, "String Value");
        _tagNameMap.put(TAG_BINARY, "Binary Value");
        _tagNameMap.put(TAG_LANGUAGE, "Language");
        _tagNameMap.put(TAG_LANGUAGE_BCP47, "Language BCP47");
        _tagNameMap.put(TAG_DEFAULT, "Default Language");
    }

    public MkvSimpleTagDirectory()
    {
        setDescriptor(new MkvSimpleTagDescriptor(this));
    }

    @NotNull
    public String getLanguage()
    {
        String language = getString(TAG_LANGUAGE_BCP47);
        if (language == null)
            language = getString(TAG_LANGUAGE);
        return language == null ? "und" : language;
    }

    @Nullable
    public MkvTagDirectory getTargets()
    {
        Directory parent = getParent();
        while (parent != null) {
            if (parent instanceof MkvTagDirectory)
                return (MkvTagDirectory)parent;
            parent = parent.getParent();
        }
        return null;
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Matroska Simple Tag";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

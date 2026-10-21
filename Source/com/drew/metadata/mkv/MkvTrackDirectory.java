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
 * Metadata common to Matroska tracks. Unsigned 64-bit identifiers exceeding
 * {@link Long#MAX_VALUE} are stored as {@link java.math.BigInteger}.
 */
public class MkvTrackDirectory extends Directory
{
    public static final int TAG_TRACK_NUMBER = ElementIDs.TRACK_NUMBER;
    public static final int TAG_TRACK_UID = ElementIDs.TRACK_UID;
    public static final int TAG_TRACK_TYPE = ElementIDs.TRACK_TYPE;
    public static final int TAG_FLAG_LACING = ElementIDs.TAG_LACING;
    public static final int TAG_CODEC_ID = ElementIDs.CODEC_ID;
    public static final int TAG_LANGUAGE = ElementIDs.LANGUAGE;
    public static final int TAG_LANGUAGE_BCP47 = ElementIDs.LANGUAGE_BCP47;
    public static final int TAG_DEFAULT_DURATION = ElementIDs.DEFAULT_DURATION;
    public static final int TAG_CODEC_DELAY = ElementIDs.CODEC_DELAY;
    public static final int TAG_SEEK_PRE_ROLL = ElementIDs.SEEK_PRE_ROLL;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        addTrackTags(_tagNameMap);
    }

    public MkvTrackDirectory()
    {
        setDescriptor(new MkvTrackDescriptor<MkvTrackDirectory>(this));
    }

    protected static void addTrackTags(@NotNull HashMap<Integer, String> map)
    {
        map.put(TAG_TRACK_NUMBER, "Track Number");
        map.put(TAG_TRACK_UID, "Track UID");
        map.put(TAG_TRACK_TYPE, "Track Type");
        map.put(TAG_FLAG_LACING, "Flag Lacing");
        map.put(TAG_CODEC_ID, "Codec ID");
        map.put(TAG_LANGUAGE, "Language");
        map.put(TAG_LANGUAGE_BCP47, "Language BCP47");
        map.put(TAG_DEFAULT_DURATION, "Default Duration");
        map.put(TAG_CODEC_DELAY, "Codec Delay");
        map.put(TAG_SEEK_PRE_ROLL, "Seek Pre-roll");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Matroska Track";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

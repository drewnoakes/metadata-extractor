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
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.mkv.MkvTagDirectory.*;

public class MkvTagDescriptor extends TagDescriptor<MkvTagDirectory>
{
    public MkvTagDescriptor(@NotNull MkvTagDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        if (tagType == TAG_TARGET_TYPE_VALUE) {
            Long value = _directory.getLongObject(tagType);
            if (value == null)
                return null;
            if (value == 10) return "Shot";
            if (value == 20) return "Subtrack / Movement / Scene";
            if (value == 30) return "Track / Song / Chapter";
            if (value == 40) return "Part / Session";
            if (value == 50) return "Album / Opera / Concert / Movie / Episode";
            if (value == 60) return "Edition / Issue / Volume / Opus / Season / Sequel";
            if (value == 70) return "Collection";
        }
        Object value = _directory.getObject(tagType);
        if (value instanceof Number[]) {
            String all = tagType == TAG_TRACK_UIDS ? "All Tracks"
                : tagType == TAG_EDITION_UIDS ? "All Editions"
                : tagType == TAG_CHAPTER_UIDS ? "All Chapters" : "All Attachments";
            StringBuilder text = new StringBuilder();
            for (Number uid : (Number[])value) {
                if (text.length() != 0)
                    text.append(", ");
                text.append(uid.toString().equals("0") ? all : uid.toString());
            }
            return text.toString();
        }
        return super.getDescription(tagType);
    }
}

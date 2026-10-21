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

import static com.drew.metadata.mkv.MkvTrackDirectory.*;

public class MkvTrackDescriptor<T extends MkvTrackDirectory> extends TagDescriptor<T>
{
    public MkvTrackDescriptor(@NotNull T directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_FLAG_LACING:
                return getIndexedDescription(tagType, "No", "Yes");
            case TAG_DEFAULT_DURATION:
            case TAG_CODEC_DELAY:
            case TAG_SEEK_PRE_ROLL:
                return getFormattedString(tagType, "%s ns");
            case TAG_TRACK_TYPE:
                Long type = _directory.getLongObject(tagType);
                if (type == null)
                    return null;
                if (type == 1) return "Video";
                if (type == 2) return "Audio";
                if (type == 3) return "Complex";
                if (type == 16) return "Logo";
                if (type == 17) return "Subtitle";
                if (type == 18) return "Buttons";
                if (type == 32) return "Control";
                if (type == 33) return "Metadata";
                return "Unknown (" + type + ")";
            default:
                return super.getDescription(tagType);
        }
    }
}

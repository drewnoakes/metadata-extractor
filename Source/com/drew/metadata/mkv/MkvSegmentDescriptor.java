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

import java.nio.ByteBuffer;
import java.util.UUID;

import static com.drew.metadata.mkv.MkvSegmentDirectory.*;

public class MkvSegmentDescriptor extends TagDescriptor<MkvSegmentDirectory>
{
    public MkvSegmentDescriptor(@NotNull MkvSegmentDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_DURATION:
                return getFormattedString(tagType, "%s segment ticks");
            case TAG_DURATION_SECONDS:
                return getFormattedString(tagType, "%s seconds");
            case TAG_TIMESTAMP_SCALE:
                return getFormattedString(tagType, "%s ns");
            case TAG_SEGMENT_UUID:
                byte[] bytes = _directory.getByteArray(tagType);
                if (bytes != null && bytes.length == 16) {
                    ByteBuffer buffer = ByteBuffer.wrap(bytes);
                    return new UUID(buffer.getLong(), buffer.getLong()).toString();
                }
                return super.getDescription(tagType);
            default:
                return super.getDescription(tagType);
        }
    }
}

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

import static com.drew.metadata.mkv.MkvAudioDirectory.*;

public class MkvAudioDescriptor extends MkvTrackDescriptor<MkvAudioDirectory>
{
    public MkvAudioDescriptor(@NotNull MkvAudioDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        if (tagType == TAG_SAMPLING_FREQUENCY)
            return getFormattedString(tagType, "%s Hz");
        if (tagType == TAG_BIT_DEPTH)
            return getFormattedString(tagType, "%s bits");
        return super.getDescription(tagType);
    }
}

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

public class MkvSimpleTagDescriptor extends TagDescriptor<MkvSimpleTagDirectory>
{
    public MkvSimpleTagDescriptor(@NotNull MkvSimpleTagDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        if (tagType == MkvSimpleTagDirectory.TAG_DEFAULT)
            return getIndexedDescription(tagType, "No", "Yes");
        return super.getDescription(tagType);
    }
}

/*
 *Copyright (c) Drew Noakes and contributors. All Rights Reserved. Licensed under the Apache License, Version 2.0. See LICENSE in the project root for license information.
 */
package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 *  <summary>
 *  Provides human-readable string representations of tag values stored in a <see cref="DjiMakernoteDirectory"/>.
 *  </summary>
 *  <remarks>Using information from https://metacpan.org/pod/distribution/Image-ExifTool/lib/Image/ExifTool/TagNames.pod#DJI-Tags</remarks>
 *  <author>SeanJay Zeng, adapted from Drew Noakes https://drewnoakes.com</author>
 */
@SuppressWarnings("WeakerAccess")
public class DjiMakernoteDescriptor extends TagDescriptor<DjiMakernoteDirectory>
{

    public DjiMakernoteDescriptor(@NotNull DjiMakernoteDirectory directory) {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        return super.getDescription(tagType);
    }
}

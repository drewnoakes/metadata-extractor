/*
 *   Copyright (c) Drew Noakes and contributors. All Rights Reserved. Licensed under the Apache License, Version 2.0. See LICENSE in the project root for license information.
 */
package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 *  <summary>Describes tags specific to DJI aircraft cameras.</summary>
 *  <remarks>Using information from https://metacpan.org/pod/distribution/Image-ExifTool/lib/Image/ExifTool/TagNames.pod#DJI-Tags</remarks>
 *  <author>SeanJay Zeng, adapted from Drew Noakes https://drewnoakes.com</author>
 */
@SuppressWarnings("WeakerAccess")
public class DjiMakernoteDirectory extends Directory
{
    public static final int TAG_MAKE = 0x0001;
    public static final int TAG_SPEED_X = 0x0003;
    public static final int TAG_SPEED_Y = 0x0004;
    public static final int TAG_SPEED_Z = 0x0005;
    public static final int TAG_AIRCRAFT_PITCH = 0x0006;
    public static final int TAG_AIRCRAFT_YAW = 0x0007;
    public static final int TAG_AIRCRAFT_ROLL = 0x0008;
    public static final int TAG_CAMERA_PITCH = 0x0009;
    public static final int TAG_CAMERA_YAW = 0x000A;
    public static final int TAG_CAMERA_ROLL = 0x000B;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_MAKE, "Make" );
        _tagNameMap.put(TAG_SPEED_X, "Aircraft X Speed" );
        _tagNameMap.put(TAG_SPEED_Y, "Aircraft Y Speed" );
        _tagNameMap.put(TAG_SPEED_Z, "Aircraft Z Speed" );
        _tagNameMap.put(TAG_AIRCRAFT_PITCH, "Aircraft Pitch" );
        _tagNameMap.put(TAG_AIRCRAFT_YAW, "Aircraft Yaw" );
        _tagNameMap.put(TAG_AIRCRAFT_ROLL, "Aircraft Roll" );
        _tagNameMap.put(TAG_CAMERA_PITCH, "Camera Pitch" );
        _tagNameMap.put(TAG_CAMERA_YAW, "Camera Yaw" );
        _tagNameMap.put(TAG_CAMERA_ROLL, "Camera Roll");
    }

    public DjiMakernoteDirectory()
    {
        this.setDescriptor(new DjiMakernoteDescriptor(this));
    }

    @Override
    public String getName()
    {
        return "DJI Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

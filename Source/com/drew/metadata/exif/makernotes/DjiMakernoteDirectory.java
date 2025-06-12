/*
 * Copyright 2002-2019 Drew Noakes and contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes tags specific to DJI aircraft cameras.
 * <p>
 * Using information from <a href="https://metacpan.org/pod/distribution/Image-ExifTool/lib/Image/ExifTool/TagNames.pod#DJI-Tags">DJI Tags Documentation</a>.
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
        _tagNameMap.put(TAG_MAKE, "Make");
        _tagNameMap.put(TAG_SPEED_X, "Aircraft X Speed");
        _tagNameMap.put(TAG_SPEED_Y, "Aircraft Y Speed");
        _tagNameMap.put(TAG_SPEED_Z, "Aircraft Z Speed");
        _tagNameMap.put(TAG_AIRCRAFT_PITCH, "Aircraft Pitch");
        _tagNameMap.put(TAG_AIRCRAFT_YAW, "Aircraft Yaw");
        _tagNameMap.put(TAG_AIRCRAFT_ROLL, "Aircraft Roll");
        _tagNameMap.put(TAG_CAMERA_PITCH, "Camera Pitch");
        _tagNameMap.put(TAG_CAMERA_YAW, "Camera Yaw");
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

/*
 * Copyright 2002-2017 Drew Noakes
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
 * Describes tags specific to Sanyo cameras.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class SanyoMakernoteDirectory extends Directory
{
    public static final int TAG_MAKERNOTE_OFFSET = 0x00ff;

    public static final int TAG_SANYO_THUMBNAIL = 0x0100;

    public static final int TAG_SPECIAL_MODE = 0x0200;
    public static final int TAG_SANYO_QUALITY = 0x0201;
    public static final int TAG_MACRO = 0x0202;
    public static final int TAG_DIGITAL_ZOOM = 0x0204;
    public static final int TAG_SOFTWARE_VERSION = 0x0207;
    public static final int TAG_PICT_INFO = 0x0208;
    public static final int TAG_CAMERA_ID = 0x0209;
    public static final int TAG_SEQUENTIAL_SHOT = 0x020e;
    public static final int TAG_WIDE_RANGE = 0x020f;
    public static final int TAG_COLOR_ADJUSTMENT_MODE = 0x0210;
    public static final int TAG_QUICK_SHOT = 0x0213;
    public static final int TAG_SELF_TIMER = 0x0214;
    public static final int TAG_VOICE_MEMO = 0x0216;
    public static final int TAG_RECORD_SHUTTER_RELEASE = 0x0217;
    public static final int TAG_FLICKER_REDUCE = 0x0218;
    public static final int TAG_OPTICAL_ZOOM_ON = 0x0219;
    public static final int TAG_DIGITAL_ZOOM_ON = 0x021b;
    public static final int TAG_LIGHT_SOURCE_SPECIAL = 0x021d;
    public static final int TAG_RESAVED = 0x021e;
    public static final int TAG_SCENE_SELECT = 0x021f;
    public static final int TAG_MANUAL_FOCUS_DISTANCE_OR_FACE_INFO = 0x0223;
    public static final int TAG_SEQUENCE_SHOT_INTERVAL = 0x0224;
    public static final int TAG_FLASH_MODE = 0x0225;

    public static final int TAG_PRINT_IMAGE_MATCHING_INFO = 0x0E00;

    public static final int TAG_DATA_DUMP = 0x0f00;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_MAKERNOTE_OFFSET, "Makernote Offset");

        _tagNameMap.put(TAG_SANYO_THUMBNAIL, "Sanyo Thumbnail");

        _tagNameMap.put(TAG_SPECIAL_MODE, "Special Mode");
        _tagNameMap.put(TAG_SANYO_QUALITY, "Sanyo Quality");
        _tagNameMap.put(TAG_MACRO, "Macro");
        _tagNameMap.put(TAG_DIGITAL_ZOOM, "Digital Zoom");
        _tagNameMap.put(TAG_SOFTWARE_VERSION, "Software Version");
        _tagNameMap.put(TAG_PICT_INFO, "Pict Info");
        _tagNameMap.put(TAG_CAMERA_ID, "Camera ID");
        _tagNameMap.put(TAG_SEQUENTIAL_SHOT, "Sequential Shot");
        _tagNameMap.put(TAG_WIDE_RANGE, "Wide Range");
        _tagNameMap.put(TAG_COLOR_ADJUSTMENT_MODE, "Color Adjustment Node");
        _tagNameMap.put(TAG_QUICK_SHOT, "Quick Shot");
        _tagNameMap.put(TAG_SELF_TIMER, "Self Timer");
        _tagNameMap.put(TAG_VOICE_MEMO, "Voice Memo");
        _tagNameMap.put(TAG_RECORD_SHUTTER_RELEASE, "Record Shutter Release");
        _tagNameMap.put(TAG_FLICKER_REDUCE, "Flicker Reduce");
        _tagNameMap.put(TAG_OPTICAL_ZOOM_ON, "Optical Zoom On");
        _tagNameMap.put(TAG_DIGITAL_ZOOM_ON, "Digital Zoom On");
        _tagNameMap.put(TAG_LIGHT_SOURCE_SPECIAL, "Light Source Special");
        _tagNameMap.put(TAG_RESAVED, "Resaved");
        _tagNameMap.put(TAG_SCENE_SELECT, "Scene Select");
        _tagNameMap.put(TAG_MANUAL_FOCUS_DISTANCE_OR_FACE_INFO, "Manual Focus Distance or Face Info");
        _tagNameMap.put(TAG_SEQUENCE_SHOT_INTERVAL, "Sequence Shot Interval");
        _tagNameMap.put(TAG_FLASH_MODE, "Flash Mode");

        _tagNameMap.put(TAG_PRINT_IMAGE_MATCHING_INFO, "Print IM");

        _tagNameMap.put(TAG_DATA_DUMP, "Data Dump");
    }

    public SanyoMakernoteDirectory()
    {
        this.setDescriptor(new SanyoMakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Sanyo Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

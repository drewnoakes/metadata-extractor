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
 * Describes tags specific certain 'newer' Samsung cameras.
 * <p>
 * Tag reference from: http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/Samsung.html
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class SamsungType2MakernoteDirectory extends Directory
{
    // This list is incomplete
    public static final int TagMakerNoteVersion = 0x001;
    public static final int TagDeviceType = 0x0002;
    public static final int TagSamsungModelId = 0x0003;

    public static final int TagCameraTemperature = 0x0043;

    public static final int TagFaceDetect = 0x0100;
    public static final int TagFaceRecognition = 0x0120;
    public static final int TagFaceName = 0x0123;

    // following tags found only in SRW images
    public static final int TagFirmwareName = 0xa001;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TagMakerNoteVersion, "Maker Note Version");
        _tagNameMap.put(TagDeviceType, "Device Type");
        _tagNameMap.put(TagSamsungModelId, "Model Id");

        _tagNameMap.put(TagCameraTemperature, "Camera Temperature");

        _tagNameMap.put(TagFaceDetect, "Face Detect");
        _tagNameMap.put(TagFaceRecognition, "Face Recognition");
        _tagNameMap.put(TagFaceName, "Face Name");
        _tagNameMap.put(TagFirmwareName, "Firmware Name");
    }

    public SamsungType2MakernoteDirectory()
    {
        this.setDescriptor(new SamsungType2MakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Samsung Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

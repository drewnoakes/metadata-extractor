/*
 * Copyright 2002-2015 Drew Noakes
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
 * The Olympus focus info makernote is used by many manufacturers (Epson, Konica, Minolta and Agfa...), and as such contains some tags
 * that appear specific to those manufacturers.
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class OlympusFocusInfoMakernoteDirectory extends Directory
{
    public static final int TagFocusInfoVersion = 0x0000;
    public static final int TagAutoFocus = 0x0209;
    public static final int TagSceneDetect = 0x0210;
    public static final int TagSceneArea = 0x0211;
    public static final int TagSceneDetectData = 0x0212;

    public static final int TagZoomStepCount = 0x0300;
    public static final int TagFocusStepCount = 0x0301;
    public static final int TagFocusStepInfinity = 0x0303;
    public static final int TagFocusStepNear = 0x0304;
    public static final int TagFocusDistance = 0x0305;
    public static final int TagAfPoint = 0x0308;
    // 0x031a Continuous AF parameters?
    public static final int TagAfInfo = 0x0328;    // ifd

    public static final int TagExternalFlash = 0x1201;
    public static final int TagExternalFlashGuideNumber = 0x1203;
    public static final int TagExternalFlashBounce = 0x1204;
    public static final int TagExternalFlashZoom = 0x1205;
    public static final int TagInternalFlash = 0x1208;
    public static final int TagManualFlash = 0x1209;
    public static final int TagMacroLed = 0x120A;

    public static final int TagSensorTemperature = 0x1500;

    public static final int TagImageStabilization = 0x1600;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TagFocusInfoVersion, "Focus Info Version");
        _tagNameMap.put(TagAutoFocus, "Auto Focus");
        _tagNameMap.put(TagSceneDetect, "Scene Detect");
        _tagNameMap.put(TagSceneArea, "Scene Area");
        _tagNameMap.put(TagSceneDetectData, "Scene Detect Data");
        _tagNameMap.put(TagZoomStepCount, "Zoom Step Count");
        _tagNameMap.put(TagFocusStepCount, "Focus Step Count");
        _tagNameMap.put(TagFocusStepInfinity, "Focus Step Infinity");
        _tagNameMap.put(TagFocusStepNear, "Focus Step Near");
        _tagNameMap.put(TagFocusDistance, "Focus Distance");
        _tagNameMap.put(TagAfPoint, "AF Point");
        _tagNameMap.put(TagAfInfo, "AF Info");
        _tagNameMap.put(TagExternalFlash, "External Flash");
        _tagNameMap.put(TagExternalFlashGuideNumber, "External Flash Guide Number");
        _tagNameMap.put(TagExternalFlashBounce, "External Flash Bounce");
        _tagNameMap.put(TagExternalFlashZoom, "External Flash Zoom");
        _tagNameMap.put(TagInternalFlash, "Internal Flash");
        _tagNameMap.put(TagManualFlash, "Manual Flash");
        _tagNameMap.put(TagMacroLed, "Macro LED");
        _tagNameMap.put(TagSensorTemperature, "Sensor Temperature");
        _tagNameMap.put(TagImageStabilization, "Image Stabilization");
    }

    public OlympusFocusInfoMakernoteDirectory()
    {
        this.setDescriptor(new OlympusFocusInfoMakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Olympus Focus Info";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

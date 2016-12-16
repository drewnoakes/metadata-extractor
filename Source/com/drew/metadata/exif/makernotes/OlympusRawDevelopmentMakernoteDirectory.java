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
 * The Olympus raw development makernote is used by many manufacturers (Epson, Konica, Minolta and Agfa...), and as such contains some tags
 * that appear specific to those manufacturers.
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class OlympusRawDevelopmentMakernoteDirectory extends Directory
{
    public static final int TagRawDevVersion = 0x0000;
    public static final int TagRawDevExposureBiasValue = 0x0100;
    public static final int TagRawDevWhiteBalanceValue = 0x0101;
    public static final int TagRawDevWbFineAdjustment = 0x0102;
    public static final int TagRawDevGrayPoint = 0x0103;
    public static final int TagRawDevSaturationEmphasis = 0x0104;
    public static final int TagRawDevMemoryColorEmphasis = 0x0105;
    public static final int TagRawDevContrastValue = 0x0106;
    public static final int TagRawDevSharpnessValue = 0x0107;
    public static final int TagRawDevColorSpace = 0x0108;
    public static final int TagRawDevEngine = 0x0109;
    public static final int TagRawDevNoiseReduction = 0x010a;
    public static final int TagRawDevEditStatus = 0x010b;
    public static final int TagRawDevSettings = 0x010c;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TagRawDevVersion, "Raw Dev Version");
        _tagNameMap.put(TagRawDevExposureBiasValue, "Raw Dev Exposure Bias Value");
        _tagNameMap.put(TagRawDevWhiteBalanceValue, "Raw Dev White Balance Value");
        _tagNameMap.put(TagRawDevWbFineAdjustment, "Raw Dev WB Fine Adjustment");
        _tagNameMap.put(TagRawDevGrayPoint, "Raw Dev Gray Point");
        _tagNameMap.put(TagRawDevSaturationEmphasis, "Raw Dev Saturation Emphasis");
        _tagNameMap.put(TagRawDevMemoryColorEmphasis, "Raw Dev Memory Color Emphasis");
        _tagNameMap.put(TagRawDevContrastValue, "Raw Dev Contrast Value");
        _tagNameMap.put(TagRawDevSharpnessValue, "Raw Dev Sharpness Value");
        _tagNameMap.put(TagRawDevColorSpace, "Raw Dev Color Space");
        _tagNameMap.put(TagRawDevEngine, "Raw Dev Engine");
        _tagNameMap.put(TagRawDevNoiseReduction, "Raw Dev Noise Reduction");
        _tagNameMap.put(TagRawDevEditStatus, "Raw Dev Edit Status");
        _tagNameMap.put(TagRawDevSettings, "Raw Dev Settings");
    }

    public OlympusRawDevelopmentMakernoteDirectory()
    {
        this.setDescriptor(new OlympusRawDevelopmentMakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Olympus Raw Development";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

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
 * The Olympus raw development 2 makernote is used by many manufacturers (Epson, Konica, Minolta and Agfa...), and as such contains some tags
 * that appear specific to those manufacturers.
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class OlympusRawDevelopment2MakernoteDirectory extends Directory
{    
    public static final int TagRawDevVersion = 0x0000;
    public static final int TagRawDevExposureBiasValue = 0x0100;
    public static final int TagRawDevWhiteBalance = 0x0101;
    public static final int TagRawDevWhiteBalanceValue = 0x0102;
    public static final int TagRawDevWbFineAdjustment = 0x0103;
    public static final int TagRawDevGrayPoint = 0x0104;
    public static final int TagRawDevContrastValue = 0x0105;
    public static final int TagRawDevSharpnessValue = 0x0106;
    public static final int TagRawDevSaturationEmphasis = 0x0107;
    public static final int TagRawDevMemoryColorEmphasis = 0x0108;
    public static final int TagRawDevColorSpace = 0x0109;
    public static final int TagRawDevNoiseReduction = 0x010a;
    public static final int TagRawDevEngine = 0x010b;
    public static final int TagRawDevPictureMode = 0x010c;
    public static final int TagRawDevPmSaturation = 0x010d;
    public static final int TagRawDevPmContrast = 0x010e;
    public static final int TagRawDevPmSharpness = 0x010f;
    public static final int TagRawDevPmBwFilter = 0x0110;
    public static final int TagRawDevPmPictureTone = 0x0111;
    public static final int TagRawDevGradation = 0x0112;
    public static final int TagRawDevSaturation3 = 0x0113;
    public static final int TagRawDevAutoGradation = 0x0119;
    public static final int TagRawDevPmNoiseFilter = 0x0120;
    public static final int TagRawDevArtFilter = 0x0121;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {        
        _tagNameMap.put(TagRawDevVersion, "Raw Dev Version");
        _tagNameMap.put(TagRawDevExposureBiasValue, "Raw Dev Exposure Bias Value");
        _tagNameMap.put(TagRawDevWhiteBalance, "Raw Dev White Balance");
        _tagNameMap.put(TagRawDevWhiteBalanceValue, "Raw Dev White Balance Value");
        _tagNameMap.put(TagRawDevWbFineAdjustment, "Raw Dev WB Fine Adjustment");
        _tagNameMap.put(TagRawDevGrayPoint, "Raw Dev Gray Point");
        _tagNameMap.put(TagRawDevContrastValue, "Raw Dev Contrast Value");
        _tagNameMap.put(TagRawDevSharpnessValue, "Raw Dev Sharpness Value");
        _tagNameMap.put(TagRawDevSaturationEmphasis, "Raw Dev Saturation Emphasis");
        _tagNameMap.put(TagRawDevMemoryColorEmphasis, "Raw Dev Memory Color Emphasis");
        _tagNameMap.put(TagRawDevColorSpace, "Raw Dev Color Space");
        _tagNameMap.put(TagRawDevNoiseReduction, "Raw Dev Noise Reduction");
        _tagNameMap.put(TagRawDevEngine, "Raw Dev Engine");
        _tagNameMap.put(TagRawDevPictureMode, "Raw Dev Picture Mode");
        _tagNameMap.put(TagRawDevPmSaturation, "Raw Dev PM Saturation");
        _tagNameMap.put(TagRawDevPmContrast, "Raw Dev PM Contrast");
        _tagNameMap.put(TagRawDevPmSharpness, "Raw Dev PM Sharpness");
        _tagNameMap.put(TagRawDevPmBwFilter, "Raw Dev PM BW Filter");
        _tagNameMap.put(TagRawDevPmPictureTone, "Raw Dev PM Picture Tone");
        _tagNameMap.put(TagRawDevGradation, "Raw Dev Gradation");
        _tagNameMap.put(TagRawDevSaturation3, "Raw Dev Saturation 3");
        _tagNameMap.put(TagRawDevAutoGradation, "Raw Dev Auto Gradation");
        _tagNameMap.put(TagRawDevPmNoiseFilter, "Raw Dev PM Noise Filter");
        _tagNameMap.put(TagRawDevArtFilter, "Raw Dev Art Filter");
    }

    public OlympusRawDevelopment2MakernoteDirectory()
    {
        this.setDescriptor(new OlympusRawDevelopment2MakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Olympus Raw Development 2";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

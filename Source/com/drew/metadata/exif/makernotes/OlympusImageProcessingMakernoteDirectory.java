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
 * The Olympus image processing makernote is used by many manufacturers (Epson, Konica, Minolta and Agfa...), and as such contains some tags
 * that appear specific to those manufacturers.
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class OlympusImageProcessingMakernoteDirectory extends Directory
{
    public static final int TagImageProcessingVersion = 0x0000;
    public static final int TagWbRbLevels = 0x0100;
    // 0x0101 - in-camera AutoWB unless it is all 0's or all 256's (ref IB)
    public static final int TagWbRbLevels3000K = 0x0102;
    public static final int TagWbRbLevels3300K = 0x0103;
    public static final int TagWbRbLevels3600K = 0x0104;
    public static final int TagWbRbLevels3900K = 0x0105;
    public static final int TagWbRbLevels4000K = 0x0106;
    public static final int TagWbRbLevels4300K = 0x0107;
    public static final int TagWbRbLevels4500K = 0x0108;
    public static final int TagWbRbLevels4800K = 0x0109;
    public static final int TagWbRbLevels5300K = 0x010a;
    public static final int TagWbRbLevels6000K = 0x010b;
    public static final int TagWbRbLevels6600K = 0x010c;
    public static final int TagWbRbLevels7500K = 0x010d;
    public static final int TagWbRbLevelsCwB1 = 0x010e;
    public static final int TagWbRbLevelsCwB2 = 0x010f;
    public static final int TagWbRbLevelsCwB3 = 0x0110;
    public static final int TagWbRbLevelsCwB4 = 0x0111;
    public static final int TagWbGLevel3000K = 0x0113;
    public static final int TagWbGLevel3300K = 0x0114;
    public static final int TagWbGLevel3600K = 0x0115;
    public static final int TagWbGLevel3900K = 0x0116;
    public static final int TagWbGLevel4000K = 0x0117;
    public static final int TagWbGLevel4300K = 0x0118;
    public static final int TagWbGLevel4500K = 0x0119;
    public static final int TagWbGLevel4800K = 0x011a;
    public static final int TagWbGLevel5300K = 0x011b;
    public static final int TagWbGLevel6000K = 0x011c;
    public static final int TagWbGLevel6600K = 0x011d;
    public static final int TagWbGLevel7500K = 0x011e;
    public static final int TagWbGLevel = 0x011f;
    // 0x0121 = WB preset for flash (about 6000K) (ref IB)
    // 0x0125 = WB preset for underwater (ref IB)

    public static final int TagColorMatrix = 0x0200;
    // color matrices (ref 11):
    // 0x0201-0x020d are sRGB color matrices
    // 0x020e-0x021a are Adobe RGB color matrices
    // 0x021b-0x0227 are ProPhoto RGB color matrices
    // 0x0228 and 0x0229 are ColorMatrix for E-330
    // 0x0250-0x0252 are sRGB color matrices
    // 0x0253-0x0255 are Adobe RGB color matrices
    // 0x0256-0x0258 are ProPhoto RGB color matrices

    public static final int TagEnhancer = 0x0300;
    public static final int TagEnhancerValues = 0x0301;
    public static final int TagCoringFilter = 0x0310;
    public static final int TagCoringValues = 0x0311;
    public static final int TagBlackLevel2 = 0x0600;
    public static final int TagGainBase = 0x0610;
    public static final int TagValidBits = 0x0611;
    public static final int TagCropLeft = 0x0612;
    public static final int TagCropTop = 0x0613;
    public static final int TagCropWidth = 0x0614;
    public static final int TagCropHeight = 0x0615;
    public static final int TagUnknownBlock1 = 0x0635;
    public static final int TagUnknownBlock2 = 0x0636;

    // 0x0800 LensDistortionParams, float[9] (ref 11)
    // 0x0801 LensShadingParams, int16u[16] (ref 11)
    public static final int TagSensorCalibration = 0x0805;

    public static final int TagNoiseReduction2 = 0x1010;
    public static final int TagDistortionCorrection2 = 0x1011;
    public static final int TagShadingCompensation2 = 0x1012;
    public static final int TagMultipleExposureMode = 0x101c;
    public static final int TagUnknownBlock3 = 0x1103;
    public static final int TagUnknownBlock4 = 0x1104;
    public static final int TagAspectRatio = 0x1112;
    public static final int TagAspectFrame = 0x1113;
    public static final int TagFacesDetected = 0x1200;
    public static final int TagFaceDetectArea = 0x1201;
    public static final int TagMaxFaces = 0x1202;
    public static final int TagFaceDetectFrameSize = 0x1203;
    public static final int TagFaceDetectFrameCrop = 0x1207;
    public static final int TagCameraTemperature = 0x1306;

    public static final int TagKeystoneCompensation = 0x1900;
    public static final int TagKeystoneDirection = 0x1901;
    // 0x1905 - focal length (PH, E-M1)
    public static final int TagKeystoneValue = 0x1906;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TagImageProcessingVersion, "Image Processing Version");
        _tagNameMap.put(TagWbRbLevels, "WB RB Levels");
        _tagNameMap.put(TagWbRbLevels3000K, "WB RB Levels 3000K");
        _tagNameMap.put(TagWbRbLevels3300K, "WB RB Levels 3300K");
        _tagNameMap.put(TagWbRbLevels3600K, "WB RB Levels 3600K");
        _tagNameMap.put(TagWbRbLevels3900K, "WB RB Levels 3900K");
        _tagNameMap.put(TagWbRbLevels4000K, "WB RB Levels 4000K");
        _tagNameMap.put(TagWbRbLevels4300K, "WB RB Levels 4300K");
        _tagNameMap.put(TagWbRbLevels4500K, "WB RB Levels 4500K");
        _tagNameMap.put(TagWbRbLevels4800K, "WB RB Levels 4800K");
        _tagNameMap.put(TagWbRbLevels5300K, "WB RB Levels 5300K");
        _tagNameMap.put(TagWbRbLevels6000K, "WB RB Levels 6000K");
        _tagNameMap.put(TagWbRbLevels6600K, "WB RB Levels 6600K");
        _tagNameMap.put(TagWbRbLevels7500K, "WB RB Levels 7500K");
        _tagNameMap.put(TagWbRbLevelsCwB1, "WB RB Levels CWB1");
        _tagNameMap.put(TagWbRbLevelsCwB2, "WB RB Levels CWB2");
        _tagNameMap.put(TagWbRbLevelsCwB3, "WB RB Levels CWB3");
        _tagNameMap.put(TagWbRbLevelsCwB4, "WB RB Levels CWB4");
        _tagNameMap.put(TagWbGLevel3000K, "WB G Level 3000K");
        _tagNameMap.put(TagWbGLevel3300K, "WB G Level 3300K");
        _tagNameMap.put(TagWbGLevel3600K, "WB G Level 3600K");
        _tagNameMap.put(TagWbGLevel3900K, "WB G Level 3900K");
        _tagNameMap.put(TagWbGLevel4000K, "WB G Level 4000K");
        _tagNameMap.put(TagWbGLevel4300K, "WB G Level 4300K");
        _tagNameMap.put(TagWbGLevel4500K, "WB G Level 4500K");
        _tagNameMap.put(TagWbGLevel4800K, "WB G Level 4800K");
        _tagNameMap.put(TagWbGLevel5300K, "WB G Level 5300K");
        _tagNameMap.put(TagWbGLevel6000K, "WB G Level 6000K");
        _tagNameMap.put(TagWbGLevel6600K, "WB G Level 6600K");
        _tagNameMap.put(TagWbGLevel7500K, "WB G Level 7500K");
        _tagNameMap.put(TagWbGLevel, "WB G Level");

        _tagNameMap.put(TagColorMatrix, "Color Matrix");

        _tagNameMap.put(TagEnhancer, "Enhancer");
        _tagNameMap.put(TagEnhancerValues, "Enhancer Values");
        _tagNameMap.put(TagCoringFilter, "Coring Filter");
        _tagNameMap.put(TagCoringValues, "Coring Values");
        _tagNameMap.put(TagBlackLevel2, "Black Level 2");
        _tagNameMap.put(TagGainBase, "Gain Base");
        _tagNameMap.put(TagValidBits, "Valid Bits");
        _tagNameMap.put(TagCropLeft, "Crop Left");
        _tagNameMap.put(TagCropTop, "Crop Top");
        _tagNameMap.put(TagCropWidth, "Crop Width");
        _tagNameMap.put(TagCropHeight, "Crop Height");
        _tagNameMap.put(TagUnknownBlock1, "Unknown Block 1");
        _tagNameMap.put(TagUnknownBlock2, "Unknown Block 2");

        _tagNameMap.put(TagSensorCalibration, "Sensor Calibration");

        _tagNameMap.put(TagNoiseReduction2, "Noise Reduction 2");
        _tagNameMap.put(TagDistortionCorrection2, "Distortion Correction 2");
        _tagNameMap.put(TagShadingCompensation2, "Shading Compensation 2");
        _tagNameMap.put(TagMultipleExposureMode, "Multiple Exposure Mode");
        _tagNameMap.put(TagUnknownBlock3, "Unknown Block 3");
        _tagNameMap.put(TagUnknownBlock4, "Unknown Block 4");
        _tagNameMap.put(TagAspectRatio, "Aspect Ratio");
        _tagNameMap.put(TagAspectFrame, "Aspect Frame");
        _tagNameMap.put(TagFacesDetected, "Faces Detected");
        _tagNameMap.put(TagFaceDetectArea, "Face Detect Area");
        _tagNameMap.put(TagMaxFaces, "Max Faces");
        _tagNameMap.put(TagFaceDetectFrameSize, "Face Detect Frame Size");
        _tagNameMap.put(TagFaceDetectFrameCrop, "Face Detect Frame Crop");
        _tagNameMap.put(TagCameraTemperature , "Camera Temperature");
        _tagNameMap.put(TagKeystoneCompensation, "Keystone Compensation");
        _tagNameMap.put(TagKeystoneDirection, "Keystone Direction");
        _tagNameMap.put(TagKeystoneValue, "Keystone Value");
    }

    public OlympusImageProcessingMakernoteDirectory()
    {
        this.setDescriptor(new OlympusImageProcessingMakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Olympus Image Processing";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

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

    public static final int TagOrientationInfo = 0x0011;

    public static final int TagSmartAlbumColor = 0x0020;
    public static final int TagPictureWizard = 0x0021;

    public static final int TagLocalLocationName = 0x0030;

    public static final int TagPreviewIfd = 0x0035;

    public static final int TagRawDataByteOrder = 0x0040;
    public static final int TagWhiteBalanceSetup = 0x0041;

    public static final int TagCameraTemperature = 0x0043;

    public static final int TagRawDataCFAPattern = 0x0050;

    public static final int TagFaceDetect = 0x0100;
    public static final int TagFaceRecognition = 0x0120;
    public static final int TagFaceName = 0x0123;

    // following tags found only in SRW images
    public static final int TagFirmwareName = 0xa001;
    public static final int TagSerialNumber = 0xa002;
    public static final int TagLensType = 0xa003;
    public static final int TagLensFirmware = 0xa004;
    public static final int TagInternalLensSerialNumber = 0xa005;

    public static final int TagSensorAreas = 0xa010;
    public static final int TagColorSpace = 0xa011;
    public static final int TagSmartRange = 0xa012;
    public static final int TagExposureCompensation = 0xa013;
    public static final int TagISO = 0xa014;

    public static final int TagExposureTime = 0xa018;
    public static final int TagFNumber = 0xa019;

    public static final int TagFocalLengthIn35mmFormat = 0xa01a;

    public static final int TagEncryptionKey = 0xa020;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TagMakerNoteVersion, "Maker Note Version");
        _tagNameMap.put(TagDeviceType, "Device Type");
        _tagNameMap.put(TagSamsungModelId, "Model Id");

        _tagNameMap.put(TagOrientationInfo, "Orientation Info");

        _tagNameMap.put(TagSmartAlbumColor, "Smart Album Color");
        _tagNameMap.put(TagPictureWizard, "Picture Wizard");

        _tagNameMap.put(TagLocalLocationName, "Local Location Name");

        _tagNameMap.put(TagPreviewIfd, "Preview IFD");

        _tagNameMap.put(TagRawDataByteOrder, "Raw Data Byte Order");
        _tagNameMap.put(TagWhiteBalanceSetup, "White Balance Setup");

        _tagNameMap.put(TagCameraTemperature, "Camera Temperature");

        _tagNameMap.put(TagRawDataCFAPattern, "Raw Data CFA Pattern");

        _tagNameMap.put(TagFaceDetect, "Face Detect");
        _tagNameMap.put(TagFaceRecognition, "Face Recognition");
        _tagNameMap.put(TagFaceName, "Face Name");

        _tagNameMap.put(TagFirmwareName, "Firmware Name");
        _tagNameMap.put(TagSerialNumber, "Serial Number");
        _tagNameMap.put(TagLensType, "Lens Type");
        _tagNameMap.put(TagLensFirmware, "Lens Firmware");
        _tagNameMap.put(TagInternalLensSerialNumber, "Internal Lens Serial Number");

        _tagNameMap.put(TagSensorAreas, "Sensor Areas");
        _tagNameMap.put(TagColorSpace, "Color Space");
        _tagNameMap.put(TagSmartRange, "Smart Range");
        _tagNameMap.put(TagExposureCompensation, "Exposure Compensation");
        _tagNameMap.put(TagISO, "ISO");

        _tagNameMap.put(TagExposureTime, "Exposure Time");
        _tagNameMap.put(TagFNumber, "F-Number");

        _tagNameMap.put(TagFocalLengthIn35mmFormat, "Focal Length in 35mm Format");

        _tagNameMap.put(TagEncryptionKey, "Encryption Key");
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

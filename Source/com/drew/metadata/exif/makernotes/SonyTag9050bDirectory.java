package com.drew.metadata.exif.makernotes;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;

public class SonyTag9050bDirectory extends SonyEncodedDataDirectoryBase
{
    public static final int TAG_SHUTTER = 0x0026;
    public static final int TAG_FLASH_STATUS = 0x0039;
    public static final int TAG_SHUTTER_COUNT = 0x003a;
    public static final int TAG_SONY_EXPOSURE_TIME = 0x0046;
    public static final int TAG_SONY_F_NUMBER = 0x0048;
    public static final int TAG_RELEASE_MODE_2 = 0x006d;
    public static final int TAG_INTERNAL_SERIAL_NUMBER = 0x0088;
    public static final int TAG_LENS_MOUNT = 0x0105;
    public static final int TAG_LENS_FORMAT = 0x0106;
    public static final int TAG_LENS_TYPE_2 = 0x0107;
    public static final int TAG_DISTORTION_CORR_PARAMS_PRESENT = 0x010b;
    public static final int TAG_APS_C_SIZE_CAPTURE = 0x0114;
    public static final int TAG_LENS_SPEC_FEATURES = 0x0116;
    public static final int TAG_SHUTTER_COUNT_3 = 0x019f;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_SHUTTER, "Shutter");
        _tagNameMap.put(TAG_FLASH_STATUS, "Flash Status");
        _tagNameMap.put(TAG_SHUTTER_COUNT, "Shutter Count");
        _tagNameMap.put(TAG_SONY_EXPOSURE_TIME, "Sony Exposure Time");
        _tagNameMap.put(TAG_SONY_F_NUMBER, "Sony F Number");
        _tagNameMap.put(TAG_RELEASE_MODE_2, "Release Mode 2");
        _tagNameMap.put(TAG_INTERNAL_SERIAL_NUMBER, "Internal Serial Number");
        _tagNameMap.put(TAG_LENS_MOUNT, "Lens Mount");
        _tagNameMap.put(TAG_LENS_FORMAT, "Lens Format");
        _tagNameMap.put(TAG_LENS_TYPE_2, "Lens Type 2");
        _tagNameMap.put(TAG_DISTORTION_CORR_PARAMS_PRESENT, "Distortion Corr Params Present");
        _tagNameMap.put(TAG_APS_C_SIZE_CAPTURE, "APS-C Size Capture");
        _tagNameMap.put(TAG_LENS_SPEC_FEATURES, "Lens Spec Features");
        _tagNameMap.put(TAG_SHUTTER_COUNT_3, "Shutter Count 3");
    }

    public SonyTag9050bDirectory()
    {
        this.setDescriptor(new SonyTag9050bDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Sony 9050B";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    public static SonyTag9050bDirectory read(byte[] bytes)
    {
        SonyTag9050bDirectory dir = new SonyTag9050bDirectory();

        try {
            // First, decipher the bytes
            decipherInPlace(bytes);

            ByteArrayReader reader = new ByteArrayReader(bytes);
            reader.setMotorolaByteOrder(false);

            // Shutter
            int offset = TAG_SHUTTER;
            int shutter0 = reader.getUInt16(offset);
            int shutter1 = reader.getUInt16(offset + 2);
            int shutter2 = reader.getUInt16(offset + 4);
            dir.setIntArray(TAG_SHUTTER, new int[]{shutter0, shutter1, shutter2});

            // FlashStatus
            offset = TAG_FLASH_STATUS;
            int flashStatus = reader.getUInt8(offset);
            dir.setInt(TAG_FLASH_STATUS, flashStatus);

            // ShutterCount
            offset = TAG_SHUTTER_COUNT;
            long shutterCount = reader.getUInt32(offset);
            dir.setLong(TAG_SHUTTER_COUNT, shutterCount);

            // SonyExposureTime
            offset = TAG_SONY_EXPOSURE_TIME;
            int expTime = reader.getUInt16(offset);
            float expTimeFlt = (float)Math.pow(2, 16 - (expTime / 256));
            DecimalFormat format = new DecimalFormat("0.#############");
            format.setRoundingMode(RoundingMode.HALF_UP);
            dir.setFloat(TAG_SONY_EXPOSURE_TIME, expTimeFlt);

            // SonyFNumber
            offset = TAG_SONY_F_NUMBER;
            int fNumber = reader.getUInt16(offset);
            dir.setInt(TAG_SONY_F_NUMBER, fNumber);

            // ReleaseMode2
            // ReleaseMode2

            offset = TAG_INTERNAL_SERIAL_NUMBER;
            int serialNum0 = reader.getUInt8(offset);
            int serialNum1 = reader.getUInt8(offset + 1);
            int serialNum2 = reader.getUInt8(offset + 2);
            int serialNum3 = reader.getUInt8(offset + 3);
            int serialNum4 = reader.getUInt8(offset + 4);
            int serialNum5 = reader.getUInt8(offset + 5);
            int[] serialNumber =
                new int[]{serialNum0, serialNum1, serialNum2, serialNum3, serialNum4, serialNum5};
            dir.setIntArray(TAG_INTERNAL_SERIAL_NUMBER, serialNumber);

            // LensMount
            offset = TAG_LENS_MOUNT;
            int lensMount = reader.getUInt8(offset);
            dir.setInt(TAG_LENS_MOUNT, lensMount);

            // LensFormat
            offset = TAG_LENS_FORMAT;
            int lensFormat = reader.getUInt8(offset);
            dir.setInt(TAG_LENS_FORMAT, lensFormat);

            // LensType2
            offset = TAG_LENS_TYPE_2;
            int lensType2 = reader.getUInt16(offset);
            dir.setInt(TAG_LENS_TYPE_2, lensType2);

            // DistortionCorrParamsPresent
            offset = TAG_DISTORTION_CORR_PARAMS_PRESENT;
            int distortCorrParamsPresent = reader.getUInt8(offset);
            dir.setInt(TAG_DISTORTION_CORR_PARAMS_PRESENT, distortCorrParamsPresent);

            // APS-CSizeCapture
            offset = TAG_APS_C_SIZE_CAPTURE;
            int apsCSizeCapture = reader.getUInt8(offset);
            dir.setInt(TAG_APS_C_SIZE_CAPTURE, apsCSizeCapture);

            // LensSpecFeatures
            offset = TAG_LENS_SPEC_FEATURES;
            byte[] lensSpecFeatures = reader.getBytes(offset, 2);
            dir.setByteArray(TAG_APS_C_SIZE_CAPTURE, lensSpecFeatures);

            // ShutterCount3
            // APS-CSizeCapture
            // LensSpecFeatures

        } catch (IOException e) {
            dir.addError(e.getMessage());
        }

        return dir;
    }
}

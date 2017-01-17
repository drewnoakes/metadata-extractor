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
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

import java.util.HashMap;

import static com.drew.metadata.exif.makernotes.SonyType1MakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link SonyType1MakernoteDirectory}.
 * Thanks to David Carson for the initial version of this class.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class SonyType1MakernoteDescriptor extends TagDescriptor<SonyType1MakernoteDirectory>
{
    public SonyType1MakernoteDescriptor(@NotNull SonyType1MakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_IMAGE_QUALITY:
                return getImageQualityDescription();
            case TAG_FLASH_EXPOSURE_COMP:
                return getFlashExposureCompensationDescription();
            case TAG_TELECONVERTER:
                return getTeleconverterDescription();
            case TAG_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case TAG_COLOR_TEMPERATURE:
                return getColorTemperatureDescription();
            case TAG_SCENE_MODE:
                return getSceneModeDescription();
            case TAG_ZONE_MATCHING:
                return getZoneMatchingDescription();
            case TAG_DYNAMIC_RANGE_OPTIMISER:
                return getDynamicRangeOptimizerDescription();
            case TAG_IMAGE_STABILISATION:
                return getImageStabilizationDescription();
            case TAG_LENS_ID:
                return getLensIDDescription();
            case TAG_COLOR_MODE:
                return getColorModeDescription();
            case TAG_MACRO:
                return getMacroDescription();
            case TAG_EXPOSURE_MODE:
                return getExposureModeDescription();
            case TAG_JPEG_QUALITY:
                return getJpegQualityDescription();
            case TAG_ANTI_BLUR:
                return getAntiBlurDescription();
            case TAG_LONG_EXPOSURE_NOISE_REDUCTION_OR_FOCUS_MODE:
                return getLongExposureNoiseReductionDescription();
            case TAG_HIGH_ISO_NOISE_REDUCTION:
                return getHighIsoNoiseReductionDescription();
            case TAG_PICTURE_EFFECT:
                return getPictureEffectDescription();
            case TAG_SOFT_SKIN_EFFECT:
                return getSoftSkinEffectDescription();
            case TAG_VIGNETTING_CORRECTION:
                return getVignettingCorrectionDescription();
            case TAG_LATERAL_CHROMATIC_ABERRATION:
                return getLateralChromaticAberrationDescription();
            case TAG_DISTORTION_CORRECTION:
                return getDistortionCorrectionDescription();
            case TAG_AUTO_PORTRAIT_FRAMED:
                return getAutoPortraitFramedDescription();
            case TAG_FOCUS_MODE:
                return getFocusModeDescription();
            case TAG_AF_POINT_SELECTED:
                return getAFPointSelectedDescription();
            case TAG_SONY_MODEL_ID:
                return getSonyModelIdDescription();
            case TAG_AF_MODE:
                return getAFModeDescription();
            case TAG_AF_ILLUMINATOR:
                return getAFIlluminatorDescription();
            case TAG_FLASH_LEVEL:
                return getFlashLevelDescription();
            case TAG_RELEASE_MODE:
                return getReleaseModeDescription();
            case TAG_SEQUENCE_NUMBER:
                return getSequenceNumberDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getImageQualityDescription()
    {
        return getIndexedDescription(TAG_IMAGE_QUALITY,
            "RAW",
            "Super Fine",
            "Fine",
            "Standard",
            "Economy",
            "Extra Fine",
            "RAW + JPEG",
            "Compressed RAW",
            "Compressed RAW + JPEG");
    }

    @Nullable
    public String getFlashExposureCompensationDescription()
    {
        return getFormattedInt(TAG_FLASH_EXPOSURE_COMP, "%d EV");
    }

    @Nullable
    public String getTeleconverterDescription()
    {
        Integer value = _directory.getInteger(TAG_TELECONVERTER);
        if (value == null)
            return null;
        switch (value) {
            case 0x00: return "None";
            case 0x48: return "Minolta/Sony AF 2x APO (D)";
            case 0x50: return "Minolta AF 2x APO II";
            case 0x60: return "Minolta AF 2x APO";
            case 0x88: return "Minolta/Sony AF 1.4x APO (D)";
            case 0x90: return "Minolta AF 1.4x APO II";
            case 0xa0: return "Minolta AF 1.4x APO";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        Integer value = _directory.getInteger(TAG_WHITE_BALANCE);
        if (value == null)
            return null;
        switch (value) {
            case 0x00: return "Auto";
            case 0x01: return "Color Temperature/Color Filter";
            case 0x10: return "Daylight";
            case 0x20: return "Cloudy";
            case 0x30: return "Shade";
            case 0x40: return "Tungsten";
            case 0x50: return "Flash";
            case 0x60: return "Fluorescent";
            case 0x70: return "Custom";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getColorTemperatureDescription()
    {
        Integer value = _directory.getInteger(TAG_COLOR_TEMPERATURE);
        if (value == null)
            return null;
        if (value == 0)
            return "Auto";
        int kelvin = ((value & 0x00FF0000) >> 8) | ((value & 0xFF000000) >> 24);
        return String.format("%d K", kelvin);
    }

    @Nullable
    public String getZoneMatchingDescription()
    {
        return getIndexedDescription(TAG_ZONE_MATCHING,
            "ISO Setting Used", "High Key", "Low Key");
    }

    @Nullable
    public String getDynamicRangeOptimizerDescription()
    {
        Integer value = _directory.getInteger(TAG_DYNAMIC_RANGE_OPTIMISER);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Off";
            case 1: return "Standard";
            case 2: return "Advanced Auto";
            case 3: return "Auto";
            case 8: return "Advanced LV1";
            case 9: return "Advanced LV2";
            case 10: return "Advanced LV3";
            case 11: return "Advanced LV4";
            case 12: return "Advanced LV5";
            case 16: return "LV1";
            case 17: return "LV2";
            case 18: return "LV3";
            case 19: return "LV4";
            case 20: return "LV5";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getImageStabilizationDescription()
    {
        Integer value = _directory.getInteger(TAG_IMAGE_STABILISATION);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Off";
            case 1: return "On";
            default: return "N/A";
        }
    }

    @Nullable
    public String getColorModeDescription()
    {
        Integer value = _directory.getInteger(TAG_COLOR_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Standard";
            case 1: return "Vivid";
            case 2: return "Portrait";
            case 3: return "Landscape";
            case 4: return "Sunset";
            case 5: return "Night Portrait";
            case 6: return "Black & White";
            case 7: return "Adobe RGB";
            case 12: case 100: return "Neutral";
            case 13: case 101: return "Clear";
            case 14: case 102: return "Deep";
            case 15: case 103: return "Light";
            case 16: return "Autumn";
            case 17: return "Sepia";
            case 104: return "Night View";
            case 105: return "Autumn Leaves";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getMacroDescription()
    {
        Integer value = _directory.getInteger(TAG_MACRO);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Off";
            case 1: return "On";
            case 2: return "Magnifying Glass/Super Macro";
            case 0xFFFF: return "N/A";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getExposureModeDescription()
    {
        Integer value = _directory.getInteger(TAG_EXPOSURE_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Program";
            case 1: return "Portrait";
            case 2: return "Beach";
            case 3: return "Sports";
            case 4: return "Snow";
            case 5: return "Landscape";
            case 6: return "Auto";
            case 7: return "Aperture Priority";
            case 8: return "Shutter Priority";
            case 9: return "Night Scene / Twilight";
            case 10: return "Hi-Speed Shutter";
            case 11: return "Twilight Portrait";
            case 12: return "Soft Snap/Portrait";
            case 13: return "Fireworks";
            case 14: return "Smile Shutter";
            case 15: return "Manual";
            case 18: return "High Sensitivity";
            case 19: return "Macro";
            case 20: return "Advanced Sports Shooting";
            case 29: return "Underwater";
            case 33: return "Food";
            case 34: return "Panorama";
            case 35: return "Handheld Night Shot";
            case 36: return "Anti Motion Blur";
            case 37: return "Pet";
            case 38: return "Backlight Correction HDR";
            case 39: return "Superior Auto";
            case 40: return "Background Defocus";
            case 41: return "Soft Skin";
            case 42: return "3D Image";
            case 0xFFFF: return "N/A";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getJpegQualityDescription()
    {
        Integer value = _directory.getInteger(TAG_JPEG_QUALITY);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Normal";
            case 1: return "Fine";
            case 2: return "Extra Fine";
            case 0xFFFF: return "N/A";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getAntiBlurDescription()
    {
        Integer value = _directory.getInteger(TAG_ANTI_BLUR);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Off";
            case 1: return "On (Continuous)";
            case 2: return "On (Shooting)";
            case 0xFFFF: return "N/A";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getLongExposureNoiseReductionDescription()
    {
        Integer value = _directory.getInteger(TAG_LONG_EXPOSURE_NOISE_REDUCTION_OR_FOCUS_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Off";
            case 1: return "On";
            case 0xFFFF: return "N/A";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getHighIsoNoiseReductionDescription()
    {
        Integer value = _directory.getInteger(TAG_HIGH_ISO_NOISE_REDUCTION);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Off";
            case 1: return "On";
            case 2: return "Normal";
            case 3: return "High";
            case 0x100: return "Auto";
            case 0xffff: return "N/A";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getPictureEffectDescription()
    {
        Integer value = _directory.getInteger(TAG_PICTURE_EFFECT);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Off";
            case 1: return "Toy Camera";
            case 2: return "Pop Color";
            case 3: return "Posterization";
            case 4: return "Posterization B/W";
            case 5: return "Retro Photo";
            case 6: return "Soft High Key";
            case 7: return "Partial Color (red)";
            case 8: return "Partial Color (green)";
            case 9: return "Partial Color (blue)";
            case 10: return "Partial Color (yellow)";
            case 13: return "High Contrast Monochrome";
            case 16: return "Toy Camera (normal)";
            case 17: return "Toy Camera (cool)";
            case 18: return "Toy Camera (warm)";
            case 19: return "Toy Camera (green)";
            case 20: return "Toy Camera (magenta)";
            case 32: return "Soft Focus (low)";
            case 33: return "Soft Focus";
            case 34: return "Soft Focus (high)";
            case 48: return "Miniature (auto)";
            case 49: return "Miniature (top)";
            case 50: return "Miniature (middle horizontal)";
            case 51: return "Miniature (bottom)";
            case 52: return "Miniature (left)";
            case 53: return "Miniature (middle vertical)";
            case 54: return "Miniature (right)";
            case 64: return "HDR Painting (low)";
            case 65: return "HDR Painting";
            case 66: return "HDR Painting (high)";
            case 80: return "Rich-tone Monochrome";
            case 97: return "Water Color";
            case 98: return "Water Color 2";
            case 112: return "Illustration (low)";
            case 113: return "Illustration";
            case 114: return "Illustration (high)";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getSoftSkinEffectDescription()
    {
        return getIndexedDescription(TAG_SOFT_SKIN_EFFECT, "Off", "Low", "Mid", "High");
    }

    @Nullable
    public String getVignettingCorrectionDescription()
    {
        Integer value = _directory.getInteger(TAG_VIGNETTING_CORRECTION);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Off";
            case 2: return "Auto";
            case 0xffffffff: return "N/A";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getLateralChromaticAberrationDescription()
    {
        Integer value = _directory.getInteger(TAG_LATERAL_CHROMATIC_ABERRATION);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Off";
            case 2: return "Auto";
            case 0xffffffff: return "N/A";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getDistortionCorrectionDescription()
    {
        Integer value = _directory.getInteger(TAG_DISTORTION_CORRECTION);
        if (value == null)
            return null;
        switch (value) {
            case 0: return "Off";
            case 2: return "Auto";
            case 0xffffffff: return "N/A";
            default: return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getAutoPortraitFramedDescription()
    {
        return getIndexedDescription(TAG_AUTO_PORTRAIT_FRAMED, "No", "Yes");
    }

    @Nullable
    public String getFocusModeDescription()
    {
        return getIndexedDescription(TAG_FOCUS_MODE,
            "Manual", null, "AF-A", "AF-C", "AF-S", null, "DMF", "AF-D");
    }

    @Nullable
    public String getAFPointSelectedDescription()
    {
        return getIndexedDescription(TAG_AF_POINT_SELECTED,
            "Auto", // 0
            "Center", // 1
            "Top", // 2
            "Upper-right", // 3
            "Right", // 4
            "Lower-right", // 5
            "Bottom", // 6
            "Lower-left", // 7
            "Left", // 8
            "Upper-left	  	", // 9
            "Far Right", // 10
            "Far Left", // 11
            "Upper-middle", // 12
            "Near Right", // 13
            "Lower-middle", // 14
            "Near Left", // 15
            "Upper Far Right", // 16
            "Lower Far Right", // 17
            "Lower Far Left", // 18
            "Upper Far Left" // 19
        );
    }

    @Nullable
    public String getSonyModelIdDescription()
    {
        Integer value = _directory.getInteger(TAG_SONY_MODEL_ID);

        if (value == null)
            return null;

        switch (value) {
            case 2: return "DSC-R1";
            case 256: return "DSLR-A100";
            case 257: return "DSLR-A900";
            case 258: return "DSLR-A700";
            case 259: return "DSLR-A200";
            case 260: return "DSLR-A350";
            case 261: return "DSLR-A300";
            case 262: return "DSLR-A900 (APS-C mode)";
            case 263: return "DSLR-A380/A390";
            case 264: return "DSLR-A330";
            case 265: return "DSLR-A230";
            case 266: return "DSLR-A290";
            case 269: return "DSLR-A850";
            case 270: return "DSLR-A850 (APS-C mode)";
            case 273: return "DSLR-A550";
            case 274: return "DSLR-A500";
            case 275: return "DSLR-A450";
            case 278: return "NEX-5";
            case 279: return "NEX-3";
            case 280: return "SLT-A33";
            case 281: return "SLT-A55V";
            case 282: return "DSLR-A560";
            case 283: return "DSLR-A580";
            case 284: return "NEX-C3";
            case 285: return "SLT-A35";
            case 286: return "SLT-A65V";
            case 287: return "SLT-A77V";
            case 288: return "NEX-5N";
            case 289: return "NEX-7";
            case 290: return "NEX-VG20E";
            case 291: return "SLT-A37";
            case 292: return "SLT-A57";
            case 293: return "NEX-F3";
            case 294: return "SLT-A99V";
            case 295: return "NEX-6";
            case 296: return "NEX-5R";
            case 297: return "DSC-RX100";
            case 298: return "DSC-RX1";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSceneModeDescription()
    {
        Integer value = _directory.getInteger(TAG_SCENE_MODE);

        if (value == null)
            return null;

        switch (value) {
            case 0: return "Standard";
            case 1: return "Portrait";
            case 2: return "Text";
            case 3: return "Night Scene";
            case 4: return "Sunset";
            case 5: return "Sports";
            case 6: return "Landscape";
            case 7: return "Night Portrait";
            case 8: return "Macro";
            case 9: return "Super Macro";
            case 16: return "Auto";
            case 17: return "Night View/Portrait";
            case 18: return "Sweep Panorama";
            case 19: return "Handheld Night Shot";
            case 20: return "Anti Motion Blur";
            case 21: return "Cont. Priority AE";
            case 22: return "Auto+";
            case 23: return "3D Sweep Panorama";
            case 24: return "Superior Auto";
            case 25: return "High Sensitivity";
            case 26: return "Fireworks";
            case 27: return "Food";
            case 28: return "Pet";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getAFModeDescription()
    {
        Integer value = _directory.getInteger(TAG_AF_MODE);

        if (value == null)
            return null;

        switch (value) {
            case 0: return "Default";
            case 1: return "Multi";
            case 2: return "Center";
            case 3: return "Spot";
            case 4: return "Flexible Spot";
            case 6: return "Touch";
            case 14: return "Manual Focus";
            case 15: return "Face Detected";
            case 0xffff: return "n/a";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getAFIlluminatorDescription()
    {
        Integer value = _directory.getInteger(TAG_AF_ILLUMINATOR);

        if (value == null)
            return null;

        switch (value) {
            case 0: return "Off";
            case 1: return "Auto";
            case 0xffff: return "n/a";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFlashLevelDescription()
    {
        Integer value = _directory.getInteger(TAG_FLASH_LEVEL);

        if (value == null)
            return null;

        switch (value) {
            case -32768: return "Low";
            case -3: return "-3/3";
            case -2: return "-2/3";
            case -1: return "-1/3";
            case 0: return "Normal";
            case 1: return "+1/3";
            case 2: return "+2/3";
            case 3: return "+3/3";
            case 128: return "n/a";
            case 32767: return "High";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getReleaseModeDescription()
    {
        Integer value = _directory.getInteger(TAG_RELEASE_MODE);

        if (value == null)
            return null;

        switch (value) {
            case 0: return "Normal";
            case 2: return "Continuous";
            case 5: return "Exposure Bracketing";
            case 6: return "White Balance Bracketing";
            case 65535: return "n/a";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSequenceNumberDescription()
    {
        Integer value = _directory.getInteger(TAG_RELEASE_MODE);

        if (value == null)
            return null;

        switch (value) {
            case 0: return "Single";
            case 65535: return "n/a";
            default:
                return value.toString();
        }
    }

    @Nullable
    public String getLensIDDescription()
    {
        Float value;
        try
        {
            value = _directory.getFloat(TAG_LENS_ID);
        }
        catch (MetadataException e)
        {
            return null;
        }

        return _lensTypeById.get(value);
    }

    private static final HashMap<Float, String> _lensTypeById = new HashMap<Float, String>();

    static {
        // http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/Sony.html
        _lensTypeById.put(0f, "Minolta AF 28-85mm F3.5-4.5 New");
        _lensTypeById.put(1f, "Minolta AF 80-200mm F2.8 HS-APO G");
        _lensTypeById.put(2f, "Minolta AF 28-70mm F2.8 G");
        _lensTypeById.put(3f, "Minolta AF 28-80mm F4-5.6");
        _lensTypeById.put(4f, "Minolta AF 85mm F1.4G");
        _lensTypeById.put(5f, "Minolta AF 35-70mm F3.5-4.5 [II]");
        _lensTypeById.put(6f, "Minolta AF 24-85mm F3.5-4.5 [New]");
        _lensTypeById.put(7f, "Minolta AF 100-300mm F4.5-5.6 APO [New] or 100-400mm or Sigma Lens");
        _lensTypeById.put(7.1f, "Minolta AF 100-400mm F4.5-6.7 APO");
        _lensTypeById.put(7.2f, "Sigma AF 100-300mm F4 EX DG IF");
        _lensTypeById.put(8f, "Minolta AF 70-210mm F4.5-5.6 [II]");
        _lensTypeById.put(9f, "Minolta AF 50mm F3.5 Macro");
        _lensTypeById.put(10f, "Minolta AF 28-105mm F3.5-4.5 [New]");
        _lensTypeById.put(11f, "Minolta AF 300mm F4 HS-APO G");
        _lensTypeById.put(12f, "Minolta AF 100mm F2.8 Soft Focus");
        _lensTypeById.put(13f, "Minolta AF 75-300mm F4.5-5.6 (New or II)");
        _lensTypeById.put(14f, "Minolta AF 100-400mm F4.5-6.7 APO");
        _lensTypeById.put(15f, "Minolta AF 400mm F4.5 HS-APO G");
        _lensTypeById.put(16f, "Minolta AF 17-35mm F3.5 G");
        _lensTypeById.put(17f, "Minolta AF 20-35mm F3.5-4.5");
        _lensTypeById.put(18f, "Minolta AF 28-80mm F3.5-5.6 II");
        _lensTypeById.put(19f, "Minolta AF 35mm F1.4 G");
        _lensTypeById.put(20f, "Minolta/Sony 135mm F2.8 [T4.5] STF");
        _lensTypeById.put(22f, "Minolta AF 35-80mm F4-5.6 II");
        _lensTypeById.put(23f, "Minolta AF 200mm F4 Macro APO G");
        _lensTypeById.put(24f, "Minolta/Sony AF 24-105mm F3.5-4.5 (D) or Sigma or Tamron Lens");
        _lensTypeById.put(24.1f, "Sigma 18-50mm F2.8");
        _lensTypeById.put(24.2f, "Sigma 17-70mm F2.8-4.5 (D)");
        _lensTypeById.put(24.3f, "Sigma 20-40mm F2.8 EX DG Aspherical IF");
        _lensTypeById.put(24.4f, "Sigma 18-200mm F3.5-6.3 DC");
        _lensTypeById.put(24.5f, "Sigma DC 18-125mm F4-5f6 D");
        _lensTypeById.put(24.6f, "Tamron SP AF 28-75mm F2.8 XR Di LD Aspherical [IF] Macro");
        _lensTypeById.put(25f, "Minolta AF 100-300mm F4.5-5.6 APO (D) or Sigma Lens");
        _lensTypeById.put(25.1f, "Sigma 100-300mm F4 EX (APO (D) or D IF)");
        _lensTypeById.put(25.2f, "Sigma 70mm F2.8 EX DG Macro");
        _lensTypeById.put(25.3f, "Sigma 20mm F1.8 EX DG Aspherical RF");
        _lensTypeById.put(25.4f, "Sigma 30mm F1.4 EX DC");
        _lensTypeById.put(25.5f, "Sigma 24mm F1.8 EX DG ASP Macro");
        _lensTypeById.put(27f, "Minolta AF 85mm F1.4 G (D)");
        _lensTypeById.put(28f, "Minolta/Sony AF 100mm F2.8 Macro (D) or Tamron Lens");
        _lensTypeById.put(28.1f, "Tamron SP AF 90mm F2.8 Di Macro");
        _lensTypeById.put(28.2f, "Tamron SP AF 180mm F3.5 Di LD [IF] Macro");
        _lensTypeById.put(29f, "Minolta/Sony AF 75-300mm F4.5-5.6 (D)");
        _lensTypeById.put(30f, "Minolta AF 28-80mm F3.5-5.6 (D) or Sigma Lens");
        _lensTypeById.put(30.1f, "Sigma AF 10-20mm F4-5.6 EX DC");
        _lensTypeById.put(30.2f, "Sigma AF 12-24mm F4.5-5.6 EX DG");
        _lensTypeById.put(30.3f, "Sigma 28-70mm EX DG F2.8");
        _lensTypeById.put(30.4f, "Sigma 55-200mm F4-5.6 DC");
        _lensTypeById.put(31f, "Minolta/Sony AF 50mm F2.8 Macro (D) or F3.5");
        _lensTypeById.put(31.1f, "Minolta/Sony AF 50mm F3.5 Macro");
        _lensTypeById.put(32f, "Minolta/Sony AF 300mm F2.8 G or 1.5x Teleconverter");
        _lensTypeById.put(33f, "Minolta/Sony AF 70-200mm F2.8 G");
        _lensTypeById.put(35f, "Minolta AF 85mm F1.4 G (D) Limited");
        _lensTypeById.put(36f, "Minolta AF 28-100mm F3.5-5.6 (D)");
        _lensTypeById.put(38f, "Minolta AF 17-35mm F2.8-4 (D)");
        _lensTypeById.put(39f, "Minolta AF 28-75mm F2.8 (D)");
        _lensTypeById.put(40f, "Minolta/Sony AF DT 18-70mm F3.5-5.6 (D)");
        _lensTypeById.put(41f, "Minolta/Sony AF DT 11-18mm F4.5-5.6 (D) or Tamron Lens");
        _lensTypeById.put(41.1f, "Tamron SP AF 11-18mm F4.5-5.6 Di II LD Aspherical IF");
        _lensTypeById.put(42f, "Minolta/Sony AF DT 18-200mm F3.5-6.3 (D)");
        _lensTypeById.put(43f, "Sony 35mm F1.4 G (SAL35F14G)");
        _lensTypeById.put(44f, "Sony 50mm F1.4 (SAL50F14)");
        _lensTypeById.put(45f, "Carl Zeiss Planar T* 85mm F1.4 ZA (SAL85F14Z)");
        _lensTypeById.put(46f, "Carl Zeiss Vario-Sonnar T* DT 16-80mm F3.5-4.5 ZA (SAL1680Z)");
        _lensTypeById.put(47f, "Carl Zeiss Sonnar T* 135mm F1.8 ZA (SAL135F18Z)");
        _lensTypeById.put(48f, "Carl Zeiss Vario-Sonnar T* 24-70mm F2.8 ZA SSM (SAL2470Z) or ZA SSM II");
        _lensTypeById.put(48.1f, "Carl Zeiss Vario-Sonnar T* 24-70mm F2.8 ZA SSM II (SAL2470Z2)");
        _lensTypeById.put(49f, "Sony DT 55-200mm F4-5.6 (SAL55200)");
        _lensTypeById.put(50f, "Sony DT 18-250mm F3.5-6.3 (SAL18250)");
        _lensTypeById.put(51f, "Sony DT 16-105mm F3.5-5.6 (SAL16105)");
        _lensTypeById.put(52f, "Sony 70-300mm F4.5-5.6 G SSM (SAL70300G) or G SSM II or Tamron Lens");
        _lensTypeById.put(52.1f, "Sony 70-300mm F4.5-5.6 G SSM II (SAL70300G2)");
        _lensTypeById.put(52.2f, "Tamron SP 70-300mm F4-5.6 Di USD");
        _lensTypeById.put(53f, "Sony 70-400mm F4-5.6 G SSM (SAL70400G)");
        _lensTypeById.put(54f, "Carl Zeiss Vario-Sonnar T* 16-35mm F2.8 ZA SSM (SAL1635Z) or ZA SSM II");
        _lensTypeById.put(54.1f, "Carl Zeiss Vario-Sonnar T* 16-35mm F2.8 ZA SSM II (SAL1635Z2)");
        _lensTypeById.put(55f, "Sony DT 18-55mm F3.5-5.6 SAM (SAL1855) or SAM II");
        _lensTypeById.put(55.1f, "Sony DT 18-55mm F3.5-5.6 SAM II (SAL18552)");
        _lensTypeById.put(56f, "Sony DT 55-200mm F4-5.6 SAM (SAL55200-2)");
        _lensTypeById.put(57f, "Sony DT 50mm F1.8 SAM (SAL50F18) or Tamron Lens or Commlite CM-EF-NEX adapter");
        _lensTypeById.put(57.1f, "Tamron SP AF 60mm F2 Di II LD [IF] Macro 1:1");
        _lensTypeById.put(57.2f, "Tamron 18-270mm F3.5-6.3 Di II PZD");
        _lensTypeById.put(58f, "Sony DT 30mm F2.8 Macro SAM (SAL30M28)");
        _lensTypeById.put(59f, "Sony 28-75mm F2.8 SAM (SAL2875)");
        _lensTypeById.put(60f, "Carl Zeiss Distagon T* 24mm F2 ZA SSM (SAL24F20Z)");
        _lensTypeById.put(61f, "Sony 85mm F2.8 SAM (SAL85F28)");
        _lensTypeById.put(62f, "Sony DT 35mm F1.8 SAM (SAL35F18)");
        _lensTypeById.put(63f, "Sony DT 16-50mm F2.8 SSM (SAL1650)");
        _lensTypeById.put(64f, "Sony 500mm F4 G SSM (SAL500F40G)");
        _lensTypeById.put(65f, "Sony DT 18-135mm F3.5-5.6 SAM (SAL18135)");
        _lensTypeById.put(66f, "Sony 300mm F2.8 G SSM II (SAL300F28G2)");
        _lensTypeById.put(67f, "Sony 70-200mm F2.8 G SSM II (SAL70200G2)");
        _lensTypeById.put(68f, "Sony DT 55-300mm F4.5-5.6 SAM (SAL55300)");
        _lensTypeById.put(69f, "Sony 70-400mm F4-5.6 G SSM II (SAL70400G2)");
        _lensTypeById.put(70f, "Carl Zeiss Planar T* 50mm F1.4 ZA SSM (SAL50F14Z)");
        _lensTypeById.put(128f, "Tamron or Sigma Lens (128)");
        _lensTypeById.put(128.1f, "Tamron AF 18-200mm F3.5-6.3 XR Di II LD Aspherical [IF] Macro");
        _lensTypeById.put(128.2f, "Tamron AF 28-300mm F3.5-6.3 XR Di LD Aspherical [IF] Macro");
        _lensTypeById.put(128.3f, "Tamron 80-300mm F3.5-6.3");
        _lensTypeById.put(128.4f, "Tamron AF 28-200mm F3.8-5.6 XR Di Aspherical [IF] Macro");
        _lensTypeById.put(128.5f, "Tamron SP AF 17-35mm F2.8-4 Di LD Aspherical IF");
        _lensTypeById.put(128.6f, "Sigma AF 50-150mm F2.8 EX DC APO HSM II");
        _lensTypeById.put(128.7f, "Sigma 10-20mm F3.5 EX DC HSM");
        _lensTypeById.put(128.8f, "Sigma 70-200mm F2.8 II EX DG APO MACRO HSM");
        _lensTypeById.put(128.9f, "Sigma 10mm F2.8 EX DC HSM Fisheye");
        _lensTypeById.put(128.10f, "Sigma 50mm F1.4 EX DG HSM");
        _lensTypeById.put(128.11f, "Sigma 85mm F1.4 EX DG HSM");
        _lensTypeById.put(128.12f, "Sigma 24-70mm F2.8 IF EX DG HSM");
        _lensTypeById.put(128.13f, "Sigma 18-250mm F3.5-6.3 DC OS HSM");
        _lensTypeById.put(128.14f, "Sigma 17-50mm F2.8 EX DC HSM");
        _lensTypeById.put(128.15f, "Sigma 17-70mm F2.8-4 DC Macro HSM");
        _lensTypeById.put(128.16f, "Sigma 150mm F2.8 EX DG OS HSM APO Macro");
        _lensTypeById.put(128.17f, "Sigma 150-500mm F5-6.3 APO DG OS HSM");
        _lensTypeById.put(128.18f, "Tamron AF 28-105mm F4-5.6 [IF]");
        _lensTypeById.put(128.19f, "Sigma 35mm F1.4 DG HSM");
        _lensTypeById.put(128.20f, "Sigma 18-35mm F1.8 DC HSM");
        _lensTypeById.put(128.21f, "Sigma 50-500mm F4.5-6.3 APO DG OS HSM");
        _lensTypeById.put(128.22f, "Sigma 24-105mm F4 DG HSM | Art 013");
        _lensTypeById.put(129f, "Tamron Lens (129)");
        _lensTypeById.put(129.1f, "Tamron 200-400mm F5.6 LD");
        _lensTypeById.put(129.2f, "Tamron 70-300mm F4-5.6 LD");
        _lensTypeById.put(131f, "Tamron 20-40mm F2.7-3.5 SP Aspherical IF");
        _lensTypeById.put(135f, "Vivitar 28-210mm F3.5-5.6");
        _lensTypeById.put(136f, "Tokina EMZ M100 AF 100mm F3.5");
        _lensTypeById.put(137f, "Cosina 70-210mm F2.8-4 AF");
        _lensTypeById.put(138f, "Soligor 19-35mm F3.5-4.5");
        _lensTypeById.put(139f, "Tokina AF 28-300mm F4-6.3");
        _lensTypeById.put(142f, "Voigtlander 70-300mm F4.5-5.6");
        _lensTypeById.put(146f, "Voigtlander Macro APO-Lanthar 125mm F2.5 SL");
        _lensTypeById.put(194f, "Tamron SP AF 17-50mm F2.8 XR Di II LD Aspherical [IF]");
        _lensTypeById.put(203f, "Tamron SP 70-200mm F2.8 Di USD");
        _lensTypeById.put(204f, "Tamron SP 24-70mm F2.8 Di USD");
        _lensTypeById.put(212f, "Tamron 28-300mm F3.5-6.3 Di PZD");
        _lensTypeById.put(213f, "Tamron 16-300mm F3.5-6.3 Di II PZD Macro");
        _lensTypeById.put(214f, "Tamron SP 150-600mm F5-6.3 Di USD");
        _lensTypeById.put(215f, "Tamron SP 15-30mm F2.8 Di USD");
        _lensTypeById.put(224f, "Tamron SP 90mm F2.8 Di Macro 1:1 USD");
        _lensTypeById.put(255f, "Tamron Lens (255)");
        _lensTypeById.put(255.1f, "Tamron SP AF 17-50mm F2.8 XR Di II LD Aspherical");
        _lensTypeById.put(255.2f, "Tamron AF 18-250mm F3.5-6.3 XR Di II LD");
        _lensTypeById.put(255.3f, "Tamron AF 55-200mm F4-5.6 Di II LD Macro");
        _lensTypeById.put(255.4f, "Tamron AF 70-300mm F4-5.6 Di LD Macro 1:2");
        _lensTypeById.put(255.5f, "Tamron SP AF 200-500mm F5.0-6.3 Di LD IF");
        _lensTypeById.put(255.6f, "Tamron SP AF 10-24mm F3.5-4.5 Di II LD Aspherical IF");
        _lensTypeById.put(255.7f, "Tamron SP AF 70-200mm F2.8 Di LD IF Macro");
        _lensTypeById.put(255.8f, "Tamron SP AF 28-75mm F2.8 XR Di LD Aspherical IF");
        _lensTypeById.put(255.9f, "Tamron AF 90-300mm F4.5-5.6 Telemacro");
        _lensTypeById.put(1868f, "Sigma MC-11 SA-E Mount Converter with not-supported Sigma lens");
        _lensTypeById.put(2550f, "Minolta AF 50mm F1.7");
        _lensTypeById.put(2551f, "Minolta AF 35-70mm F4 or Other Lens");
        _lensTypeById.put(2551.1f, "Sigma UC AF 28-70mm F3.5-4.5");
        _lensTypeById.put(2551.2f, "Sigma AF 28-70mm F2.8");
        _lensTypeById.put(2551.3f, "Sigma M-AF 70-200mm F2.8 EX Aspherical");
        _lensTypeById.put(2551.4f, "Quantaray M-AF 35-80mm F4-5.6");
        _lensTypeById.put(2551.5f, "Tokina 28-70mm F2.8-4.5 AF");
        _lensTypeById.put(2552f, "Minolta AF 28-85mm F3.5-4.5 or Other Lens");
        _lensTypeById.put(2552.1f, "Tokina 19-35mm F3.5-4.5");
        _lensTypeById.put(2552.2f, "Tokina 28-70mm F2.8 AT-X");
        _lensTypeById.put(2552.3f, "Tokina 80-400mm F4.5-5.6 AT-X AF II 840");
        _lensTypeById.put(2552.4f, "Tokina AF PRO 28-80mm F2.8 AT-X 280");
        _lensTypeById.put(2552.5f, "Tokina AT-X PRO [II] AF 28-70mm F2.6-2.8 270");
        _lensTypeById.put(2552.6f, "Tamron AF 19-35mm F3.5-4.5");
        _lensTypeById.put(2552.7f, "Angenieux AF 28-70mm F2.6");
        _lensTypeById.put(2552.8f, "Tokina AT-X 17 AF 17mm F3.5");
        _lensTypeById.put(2552.9f, "Tokina 20-35mm F3.5-4.5 II AF");
        _lensTypeById.put(2553f, "Minolta AF 28-135mm F4-4.5 or Sigma Lens");
        _lensTypeById.put(2553.1f, "Sigma ZOOM-alpha 35-135mm F3.5-4.5");
        _lensTypeById.put(2553.2f, "Sigma 28-105mm F2.8-4 Aspherical");
        _lensTypeById.put(2553.3f, "Sigma 28-105mm F4-5.6 UC");
        _lensTypeById.put(2554f, "Minolta AF 35-105mm F3.5-4.5");
        _lensTypeById.put(2555f, "Minolta AF 70-210mm F4 Macro or Sigma Lens");
        _lensTypeById.put(2555.1f, "Sigma 70-210mm F4-5.6 APO");
        _lensTypeById.put(2555.2f, "Sigma M-AF 70-200mm F2.8 EX APO");
        _lensTypeById.put(2555.3f, "Sigma 75-200mm F2.8-3.5");
        _lensTypeById.put(2556f, "Minolta AF 135mm F2.8");
        _lensTypeById.put(2557f, "Minolta/Sony AF 28mm F2.8");
        _lensTypeById.put(2558f, "Minolta AF 24-50mm F4");
        _lensTypeById.put(2560f, "Minolta AF 100-200mm F4.5");
        _lensTypeById.put(2561f, "Minolta AF 75-300mm F4.5-5.6 or Sigma Lens");
        _lensTypeById.put(2561.1f, "Sigma 70-300mm F4-5.6 DL Macro");
        _lensTypeById.put(2561.2f, "Sigma 300mm F4 APO Macro");
        _lensTypeById.put(2561.3f, "Sigma AF 500mm F4.5 APO");
        _lensTypeById.put(2561.4f, "Sigma AF 170-500mm F5-6.3 APO Aspherical");
        _lensTypeById.put(2561.5f, "Tokina AT-X AF 300mm F4");
        _lensTypeById.put(2561.6f, "Tokina AT-X AF 400mm F5.6 SD");
        _lensTypeById.put(2561.7f, "Tokina AF 730 II 75-300mm F4.5-5.6");
        _lensTypeById.put(2561.8f, "Sigma 800mm F5.6 APO");
        _lensTypeById.put(2561.9f, "Sigma AF 400mm F5.6 APO Macro");
        _lensTypeById.put(2562f, "Minolta AF 50mm F1.4 [New]");
        _lensTypeById.put(2563f, "Minolta AF 300mm F2.8 APO or Sigma Lens");
        _lensTypeById.put(2563.1f, "Sigma AF 50-500mm F4-6.3 EX DG APO");
        _lensTypeById.put(2563.2f, "Sigma AF 170-500mm F5-6.3 APO Aspherical");
        _lensTypeById.put(2563.3f, "Sigma AF 500mm F4.5 EX DG APO");
        _lensTypeById.put(2563.4f, "Sigma 400mm F5.6 APO");
        _lensTypeById.put(2564f, "Minolta AF 50mm F2.8 Macro or Sigma Lens");
        _lensTypeById.put(2564.1f, "Sigma 50mm F2.8 EX Macro");
        _lensTypeById.put(2565f, "Minolta AF 600mm F4 APO");
        _lensTypeById.put(2566f, "Minolta AF 24mm F2.8 or Sigma Lens");
        _lensTypeById.put(2566.1f, "Sigma 17-35mm F2.8-4 EX Aspherical");
        _lensTypeById.put(2572f, "Minolta/Sony AF 500mm F8 Reflex");
        _lensTypeById.put(2578f, "Minolta/Sony AF 16mm F2.8 Fisheye or Sigma Lens");
        _lensTypeById.put(2578.1f, "Sigma 8mm F4 EX [DG] Fisheye");
        _lensTypeById.put(2578.2f, "Sigma 14mm F3.5");
        _lensTypeById.put(2578.3f, "Sigma 15mm F2.8 Fisheye");
        _lensTypeById.put(2579f, "Minolta/Sony AF 20mm F2.8 or Tokina Lens");
        _lensTypeById.put(2579.1f, "Tokina AT-X Pro DX 11-16mm F2.8");
        _lensTypeById.put(2581f, "Minolta AF 100mm F2.8 Macro [New] or Sigma or Tamron Lens");
        _lensTypeById.put(2581.1f, "Sigma AF 90mm F2.8 Macro");
        _lensTypeById.put(2581.2f, "Sigma AF 105mm F2.8 EX [DG] Macro");
        _lensTypeById.put(2581.3f, "Sigma 180mm F5.6 Macro");
        _lensTypeById.put(2581.4f, "Sigma 180mm F3.5 EX DG Macro");
        _lensTypeById.put(2581.5f, "Tamron 90mm F2.8 Macro");
        _lensTypeById.put(2585f, "Minolta AF 35-105mm F3.5-4.5 New or Tamron Lens");
        _lensTypeById.put(2585.1f, "Beroflex 35-135mm F3.5-4.5");
        _lensTypeById.put(2585.2f, "Tamron 24-135mm F3.5-5.6");
        _lensTypeById.put(2588f, "Minolta AF 70-210mm F3.5-4.5");
        _lensTypeById.put(2589f, "Minolta AF 80-200mm F2.8 APO or Tokina Lens");
        _lensTypeById.put(2589.1f, "Tokina 80-200mm F2.8");
        _lensTypeById.put(2590f, "Minolta AF 200mm F2.8 G APO + Minolta AF 1.4x APO or Other Lens + 1.4x");
        _lensTypeById.put(2590.1f, "Minolta AF 600mm F4 HS-APO G + Minolta AF 1.4x APO");
        _lensTypeById.put(2591f, "Minolta AF 35mm F1.4");
        _lensTypeById.put(2592f, "Minolta AF 85mm F1.4 G (D)");
        _lensTypeById.put(2593f, "Minolta AF 200mm F2.8 APO");
        _lensTypeById.put(2594f, "Minolta AF 3x-1x F1.7-2.8 Macro");
        _lensTypeById.put(2596f, "Minolta AF 28mm F2");
        _lensTypeById.put(2597f, "Minolta AF 35mm F2 [New]");
        _lensTypeById.put(2598f, "Minolta AF 100mm F2");
        _lensTypeById.put(2601f, "Minolta AF 200mm F2.8 G APO + Minolta AF 2x APO or Other Lens + 2x");
        _lensTypeById.put(2601.1f, "Minolta AF 600mm F4 HS-APO G + Minolta AF 2x APO");
        _lensTypeById.put(2604f, "Minolta AF 80-200mm F4.5-5.6");
        _lensTypeById.put(2605f, "Minolta AF 35-80mm F4-5.6");
        _lensTypeById.put(2606f, "Minolta AF 100-300mm F4.5-5.6");
        _lensTypeById.put(2607f, "Minolta AF 35-80mm F4-5.6");
        _lensTypeById.put(2608f, "Minolta AF 300mm F2.8 HS-APO G");
        _lensTypeById.put(2609f, "Minolta AF 600mm F4 HS-APO G");
        _lensTypeById.put(2612f, "Minolta AF 200mm F2.8 HS-APO G");
        _lensTypeById.put(2613f, "Minolta AF 50mm F1.7 New");
        _lensTypeById.put(2615f, "Minolta AF 28-105mm F3.5-4.5 xi");
        _lensTypeById.put(2616f, "Minolta AF 35-200mm F4.5-5.6 xi");
        _lensTypeById.put(2618f, "Minolta AF 28-80mm F4-5.6 xi");
        _lensTypeById.put(2619f, "Minolta AF 80-200mm F4.5-5.6 xi");
        _lensTypeById.put(2620f, "Minolta AF 28-70mm F2.8 G");
        _lensTypeById.put(2621f, "Minolta AF 100-300mm F4.5-5.6 xi");
        _lensTypeById.put(2624f, "Minolta AF 35-80mm F4-5.6 Power Zoom");
        _lensTypeById.put(2628f, "Minolta AF 80-200mm F2.8 HS-APO G");
        _lensTypeById.put(2629f, "Minolta AF 85mm F1.4 New");
        _lensTypeById.put(2631f, "Minolta/Sony AF 100-300mm F4.5-5.6 APO");
        _lensTypeById.put(2632f, "Minolta AF 24-50mm F4 New");
        _lensTypeById.put(2638f, "Minolta AF 50mm F2.8 Macro New");
        _lensTypeById.put(2639f, "Minolta AF 100mm F2.8 Macro");
        _lensTypeById.put(2641f, "Minolta/Sony AF 20mm F2.8 New");
        _lensTypeById.put(2642f, "Minolta AF 24mm F2.8 New");
        _lensTypeById.put(2644f, "Minolta AF 100-400mm F4.5-6.7 APO");
        _lensTypeById.put(2662f, "Minolta AF 50mm F1.4 New");
        _lensTypeById.put(2667f, "Minolta AF 35mm F2 New");
        _lensTypeById.put(2668f, "Minolta AF 28mm F2 New");
        _lensTypeById.put(2672f, "Minolta AF 24-105mm F3.5-4.5 (D)");
        _lensTypeById.put(3046f, "Metabones Canon EF Speed Booster");
        _lensTypeById.put(4567f, "Tokina 70-210mm F4-5.6");
        _lensTypeById.put(4571f, "Vivitar 70-210mm F4.5-5.6");
        _lensTypeById.put(4574f, "2x Teleconverter or Tamron or Tokina Lens");
        _lensTypeById.put(4574.1f, "Tamron SP AF 90mm F2.5");
        _lensTypeById.put(4574.2f, "Tokina RF 500mm F8.0 x2");
        _lensTypeById.put(4574.3f, "Tokina 300mm F2.8 x2");
        _lensTypeById.put(4575f, "1.4x Teleconverter");
        _lensTypeById.put(4585f, "Tamron SP AF 300mm F2.8 LD IF");
        _lensTypeById.put(4586f, "Tamron SP AF 35-105mm F2.8 LD Aspherical IF");
        _lensTypeById.put(4587f, "Tamron AF 70-210mm F2.8 SP LD");
        _lensTypeById.put(4812f, "Metabones Canon EF Speed Booster Ultra");
        _lensTypeById.put(6118f, "Canon EF Adapter");
        _lensTypeById.put(6528f, "Sigma 16mm F2.8 Filtermatic Fisheye");
        _lensTypeById.put(6553f, "E-Mount, T-Mount, Other Lens or no lens");
        _lensTypeById.put(6553.1f, "Sony E 16mm F2.8");
        _lensTypeById.put(6553.2f, "Sony E 18-55mm F3.5-5.6 OSS");
        _lensTypeById.put(6553.3f, "Sony E 55-210mm F4.5-6.3 OSS");
        _lensTypeById.put(6553.4f, "Sony E 18-200mm F3.5-6.3 OSS");
        _lensTypeById.put(6553.5f, "Sony E 30mm F3.5 Macro");
        _lensTypeById.put(6553.6f, "Sony E 24mm F1.8 ZA");
        _lensTypeById.put(6553.7f, "Sony E 50mm F1.8 OSS");
        _lensTypeById.put(6553.8f, "Sony E 16-70mm F4 ZA OSS");
        _lensTypeById.put(6553.9f, "Sony E 10-18mm F4 OSS");
        _lensTypeById.put(6553.10f, "Sony E PZ 16-50mm F3.5-5.6 OSS");
        _lensTypeById.put(6553.11f, "Sony FE 35mm F2.8 ZA");
        _lensTypeById.put(6553.12f, "Sony FE 24-70mm F4 ZA OSS");
        _lensTypeById.put(6553.13f, "Sony E 18-200mm F3.5-6.3 OSS LE");
        _lensTypeById.put(6553.14f, "Sony E 20mm F2.8");
        _lensTypeById.put(6553.15f, "Sony E 35mm F1.8 OSS");
        _lensTypeById.put(6553.16f, "Sony E PZ 18-105mm F4 G OSS");
        _lensTypeById.put(6553.17f, "Sony FE 90mm F2.8 Macro G OSS");
        _lensTypeById.put(6553.18f, "Sony E 18-50mm F4-5.6");
        _lensTypeById.put(6553.19f, "Sony E PZ 18-200mm F3.5-6.3 OSS");
        _lensTypeById.put(6553.20f, "Sony FE 55mm F1.8 ZA");
        _lensTypeById.put(6553.21f, "Sony FE 70-200mm F4 G OSS");
        _lensTypeById.put(6553.22f, "Sony FE 16-35mm F4 ZA OSS");
        _lensTypeById.put(6553.23f, "Sony FE 50mm F2.8 Macro");
        _lensTypeById.put(6553.24f, "Sony FE 28-70mm F3.5-5.6 OSS");
        _lensTypeById.put(6553.25f, "Sony FE 35mm F1.4 ZA");
        _lensTypeById.put(6553.26f, "Sony FE 24-240mm F3.5-6.3 OSS");
        _lensTypeById.put(6553.27f, "Sony FE 28mm F2");
        _lensTypeById.put(6553.28f, "Sony FE PZ 28-135mm F4 G OSS");
        _lensTypeById.put(6553.29f, "Sony FE 24-70mm F2.8 GM");
        _lensTypeById.put(6553.30f, "Sony FE 50mm F1.4 ZA");
        _lensTypeById.put(6553.31f, "Sony FE 85mm F1.4 GM");
        _lensTypeById.put(6553.32f, "Sony FE 50mm F1.8");
        _lensTypeById.put(6553.33f, "Sony FE 21mm F2.8 (SEL28F20 + SEL075UWC)");
        _lensTypeById.put(6553.34f, "Sony FE 16mm F3.5 Fisheye (SEL28F20 + SEL057FEC)");
        _lensTypeById.put(6553.35f, "Sony FE 70-300mm F4.5-5.6 G OSS");
        _lensTypeById.put(6553.36f, "Sony FE 70-200mm F2.8 GM OSS");
        _lensTypeById.put(6553.37f, "Sony FE 70-200mm F2.8 GM OSS + 1.4X Teleconverter");
        _lensTypeById.put(6553.38f, "Sony FE 70-200mm F2.8 GM OSS + 2X Teleconverter");
        _lensTypeById.put(6553.39f, "Samyang AF 50mm F1.4 FE");
        _lensTypeById.put(6553.40f, "Samyang AF 14mm F2.8 FE");
        _lensTypeById.put(6553.41f, "Sigma 19mm F2.8 [EX] DN");
        _lensTypeById.put(6553.42f, "Sigma 30mm F2.8 [EX] DN");
        _lensTypeById.put(6553.43f, "Sigma 60mm F2.8 DN");
        _lensTypeById.put(6553.44f, "Sigma 30mm F1.4 DC DN | C 016");
        _lensTypeById.put(6553.45f, "Tamron 18-200mm F3.5-6.3 Di III VC");
        _lensTypeById.put(6553.46f, "Zeiss Touit 12mm F2.8");
        _lensTypeById.put(6553.47f, "Zeiss Touit 32mm F1.8");
        _lensTypeById.put(6553.48f, "Zeiss Touit 50mm F2.8 Macro");
        _lensTypeById.put(6553.49f, "Zeiss Batis 25mm F2");
        _lensTypeById.put(6553.50f, "Zeiss Batis 85mm F1.8");
        _lensTypeById.put(6553.51f, "Zeiss Batis 18mm F2.8");
        _lensTypeById.put(6553.52f, "Zeiss Loxia 50mm F2");
        _lensTypeById.put(6553.53f, "Zeiss Loxia 35mm F2");
        _lensTypeById.put(6553.54f, "Zeiss Loxia 21mm F2.8");
        _lensTypeById.put(6553.55f, "Zeiss Loxia 85mm F2.4");
        _lensTypeById.put(6553.56f, "Arax MC 35mm F2.8 Tilt+Shift");
        _lensTypeById.put(6553.57f, "Arax MC 80mm F2.8 Tilt+Shift");
        _lensTypeById.put(6553.58f, "Zenitar MF 16mm F2.8 Fisheye M42");
        _lensTypeById.put(6553.59f, "Samyang 500mm Mirror F8.0");
        _lensTypeById.put(6553.60f, "Pentacon Auto 135mm F2.8");
        _lensTypeById.put(6553.61f, "Pentacon Auto 29mm F2.8");
        _lensTypeById.put(6553.62f, "Helios 44-2 58mm F2.0");
        _lensTypeById.put(18688f, "Sigma MC-11 SA-E Mount Converter with not-supported Sigma lens");
        _lensTypeById.put(25501f, "Minolta AF 50mm F1.7");
        _lensTypeById.put(25511f, "Minolta AF 35-70mm F4 or Other Lens");
        _lensTypeById.put(25511.1f, "Sigma UC AF 28-70mm F3.5-4.5");
        _lensTypeById.put(25511.2f, "Sigma AF 28-70mm F2.8");
        _lensTypeById.put(25511.3f, "Sigma M-AF 70-200mm F2.8 EX Aspherical");
        _lensTypeById.put(25511.4f, "Quantaray M-AF 35-80mm F4-5.6");
        _lensTypeById.put(25511.5f, "Tokina 28-70mm F2.8-4.5 AF");
        _lensTypeById.put(25521f, "Minolta AF 28-85mm F3.5-4.5 or Other Lens");
        _lensTypeById.put(25521.1f, "Tokina 19-35mm F3.5-4.5");
        _lensTypeById.put(25521.2f, "Tokina 28-70mm F2.8 AT-X");
        _lensTypeById.put(25521.3f, "Tokina 80-400mm F4.5-5.6 AT-X AF II 840");
        _lensTypeById.put(25521.4f, "Tokina AF PRO 28-80mm F2.8 AT-X 280");
        _lensTypeById.put(25521.5f, "Tokina AT-X PRO [II] AF 28-70mm F2.6-2.8 270");
        _lensTypeById.put(25521.6f, "Tamron AF 19-35mm F3.5-4.5");
        _lensTypeById.put(25521.7f, "Angenieux AF 28-70mm F2.6");
        _lensTypeById.put(25521.8f, "Tokina AT-X 17 AF 17mm F3.5");
        _lensTypeById.put(25521.9f, "Tokina 20-35mm F3.5-4.5 II AF");
        _lensTypeById.put(25531f, "Minolta AF 28-135mm F4-4.5 or Sigma Lens");
        _lensTypeById.put(25531.1f, "Sigma ZOOM-alpha 35-135mm F3.5-4.5");
        _lensTypeById.put(25531.2f, "Sigma 28-105mm F2.8-4 Aspherical");
        _lensTypeById.put(25531.3f, "Sigma 28-105mm F4-5.6 UC");
        _lensTypeById.put(25541f, "Minolta AF 35-105mm F3.5-4.5");
        _lensTypeById.put(25551f, "Minolta AF 70-210mm F4 Macro or Sigma Lens");
        _lensTypeById.put(25551.1f, "Sigma 70-210mm F4-5.6 APO");
        _lensTypeById.put(25551.2f, "Sigma M-AF 70-200mm F2.8 EX APO");
        _lensTypeById.put(25551.3f, "Sigma 75-200mm F2.8-3.5");
        _lensTypeById.put(25561f, "Minolta AF 135mm F2.8");
        _lensTypeById.put(25571f, "Minolta/Sony AF 28mm F2.8");
        _lensTypeById.put(25581f, "Minolta AF 24-50mm F4");
        _lensTypeById.put(25601f, "Minolta AF 100-200mm F4.5");
        _lensTypeById.put(25611f, "Minolta AF 75-300mm F4.5-5.6 or Sigma Lens");
        _lensTypeById.put(25611.1f, "Sigma 70-300mm F4-5.6 DL Macro");
        _lensTypeById.put(25611.2f, "Sigma 300mm F4 APO Macro");
        _lensTypeById.put(25611.3f, "Sigma AF 500mm F4.5 APO");
        _lensTypeById.put(25611.4f, "Sigma AF 170-500mm F5-6.3 APO Aspherical");
        _lensTypeById.put(25611.5f, "Tokina AT-X AF 300mm F4");
        _lensTypeById.put(25611.6f, "Tokina AT-X AF 400mm F5.6 SD");
        _lensTypeById.put(25611.7f, "Tokina AF 730 II 75-300mm F4.5-5.6");
        _lensTypeById.put(25611.8f, "Sigma 800mm F5.6 APO");
        _lensTypeById.put(25611.9f, "Sigma AF 400mm F5.6 APO Macro");
        _lensTypeById.put(25621f, "Minolta AF 50mm F1.4 [New]");
        _lensTypeById.put(25631f, "Minolta AF 300mm F2.8 APO or Sigma Lens");
        _lensTypeById.put(25631.1f, "Sigma AF 50-500mm F4-6.3 EX DG APO");
        _lensTypeById.put(25631.2f, "Sigma AF 170-500mm F5-6.3 APO Aspherical");
        _lensTypeById.put(25631.3f, "Sigma AF 500mm F4.5 EX DG APO");
        _lensTypeById.put(25631.4f, "Sigma 400mm F5.6 APO");
        _lensTypeById.put(25641f, "Minolta AF 50mm F2.8 Macro or Sigma Lens");
        _lensTypeById.put(25641.1f, "Sigma 50mm F2.8 EX Macro");
        _lensTypeById.put(25651f, "Minolta AF 600mm F4 APO");
        _lensTypeById.put(25661f, "Minolta AF 24mm F2.8 or Sigma Lens");
        _lensTypeById.put(25661.1f, "Sigma 17-35mm F2.8-4 EX Aspherical");
        _lensTypeById.put(25721f, "Minolta/Sony AF 500mm F8 Reflex");
        _lensTypeById.put(25781f, "Minolta/Sony AF 16mm F2.8 Fisheye or Sigma Lens");
        _lensTypeById.put(25781.1f, "Sigma 8mm F4 EX [DG] Fisheye");
        _lensTypeById.put(25781.2f, "Sigma 14mm F3.5");
        _lensTypeById.put(25781.3f, "Sigma 15mm F2.8 Fisheye");
        _lensTypeById.put(25791f, "Minolta/Sony AF 20mm F2.8 or Tokina Lens");
        _lensTypeById.put(25791.1f, "Tokina AT-X Pro DX 11-16mm F2.8");
        _lensTypeById.put(25811f, "Minolta AF 100mm F2.8 Macro [New] or Sigma or Tamron Lens");
        _lensTypeById.put(25811.1f, "Sigma AF 90mm F2.8 Macro");
        _lensTypeById.put(25811.2f, "Sigma AF 105mm F2.8 EX [DG] Macro");
        _lensTypeById.put(25811.3f, "Sigma 180mm F5.6 Macro");
        _lensTypeById.put(25811.4f, "Sigma 180mm F3.5 EX DG Macro");
        _lensTypeById.put(25811.5f, "Tamron 90mm F2.8 Macro");
        _lensTypeById.put(25851f, "Beroflex 35-135mm F3.5-4.5");
        _lensTypeById.put(25858f, "Minolta AF 35-105mm F3.5-4.5 New or Tamron Lens");
        _lensTypeById.put(25858.1f, "Tamron 24-135mm F3.5-5.6");
        _lensTypeById.put(25881f, "Minolta AF 70-210mm F3.5-4.5");
        _lensTypeById.put(25891f, "Minolta AF 80-200mm F2.8 APO or Tokina Lens");
        _lensTypeById.put(25891.1f, "Tokina 80-200mm F2.8");
        _lensTypeById.put(25901f, "Minolta AF 200mm F2.8 G APO + Minolta AF 1.4x APO or Other Lens + 1.4x");
        _lensTypeById.put(25901.1f, "Minolta AF 600mm F4 HS-APO G + Minolta AF 1.4x APO");
        _lensTypeById.put(25911f, "Minolta AF 35mm F1.4");
        _lensTypeById.put(25921f, "Minolta AF 85mm F1.4 G (D)");
        _lensTypeById.put(25931f, "Minolta AF 200mm F2.8 APO");
        _lensTypeById.put(25941f, "Minolta AF 3x-1x F1.7-2.8 Macro");
        _lensTypeById.put(25961f, "Minolta AF 28mm F2");
        _lensTypeById.put(25971f, "Minolta AF 35mm F2 [New]");
        _lensTypeById.put(25981f, "Minolta AF 100mm F2");
        _lensTypeById.put(26011f, "Minolta AF 200mm F2.8 G APO + Minolta AF 2x APO or Other Lens + 2x");
        _lensTypeById.put(26011.1f, "Minolta AF 600mm F4 HS-APO G + Minolta AF 2x APO");
        _lensTypeById.put(26041f, "Minolta AF 80-200mm F4.5-5.6");
        _lensTypeById.put(26051f, "Minolta AF 35-80mm F4-5.6");
        _lensTypeById.put(26061f, "Minolta AF 100-300mm F4.5-5.6");
        _lensTypeById.put(26071f, "Minolta AF 35-80mm F4-5.6");
        _lensTypeById.put(26081f, "Minolta AF 300mm F2.8 HS-APO G");
        _lensTypeById.put(26091f, "Minolta AF 600mm F4 HS-APO G");
        _lensTypeById.put(26121f, "Minolta AF 200mm F2.8 HS-APO G");
        _lensTypeById.put(26131f, "Minolta AF 50mm F1.7 New");
        _lensTypeById.put(26151f, "Minolta AF 28-105mm F3.5-4.5 xi");
        _lensTypeById.put(26161f, "Minolta AF 35-200mm F4.5-5.6 xi");
        _lensTypeById.put(26181f, "Minolta AF 28-80mm F4-5.6 xi");
        _lensTypeById.put(26191f, "Minolta AF 80-200mm F4.5-5.6 xi");
        _lensTypeById.put(26201f, "Minolta AF 28-70mm F2.8 G");
        _lensTypeById.put(26211f, "Minolta AF 100-300mm F4.5-5.6 xi");
        _lensTypeById.put(26241f, "Minolta AF 35-80mm F4-5.6 Power Zoom");
        _lensTypeById.put(26281f, "Minolta AF 80-200mm F2.8 HS-APO G");
        _lensTypeById.put(26291f, "Minolta AF 85mm F1.4 New");
        _lensTypeById.put(26311f, "Minolta/Sony AF 100-300mm F4.5-5.6 APO");
        _lensTypeById.put(26321f, "Minolta AF 24-50mm F4 New");
        _lensTypeById.put(26381f, "Minolta AF 50mm F2.8 Macro New");
        _lensTypeById.put(26391f, "Minolta AF 100mm F2.8 Macro");
        _lensTypeById.put(26411f, "Minolta/Sony AF 20mm F2.8 New");
        _lensTypeById.put(26421f, "Minolta AF 24mm F2.8 New");
        _lensTypeById.put(26441f, "Minolta AF 100-400mm F4.5-6.7 APO");
        _lensTypeById.put(26621f, "Minolta AF 50mm F1.4 New");
        _lensTypeById.put(26671f, "Minolta AF 35mm F2 New");
        _lensTypeById.put(26681f, "Minolta AF 28mm F2 New");
        _lensTypeById.put(26721f, "Minolta AF 24-105mm F3.5-4.5 (D)");
        _lensTypeById.put(30464f, "Metabones Canon EF Speed Booster");
        _lensTypeById.put(45671f, "Tokina 70-210mm F4-5.6");
        _lensTypeById.put(45711f, "Vivitar 70-210mm F4.5-5.6");
        _lensTypeById.put(45741f, "2x Teleconverter or Tamron or Tokina Lens");
        _lensTypeById.put(45741.1f, "Tamron SP AF 90mm F2.5");
        _lensTypeById.put(45741.2f, "Tokina RF 500mm F8.0 x2");
        _lensTypeById.put(45741.3f, "Tokina 300mm F2.8 x2");
        _lensTypeById.put(45751f, "1.4x Teleconverter");
        _lensTypeById.put(45851f, "Tamron SP AF 300mm F2.8 LD IF");
        _lensTypeById.put(45861f, "Tamron SP AF 35-105mm F2.8 LD Aspherical IF");
        _lensTypeById.put(45871f, "Tamron AF 70-210mm F2.8 SP LD");
        _lensTypeById.put(48128f, "Metabones Canon EF Speed Booster Ultra");
        _lensTypeById.put(61184f, "Canon EF Adapter");
        _lensTypeById.put(65280f, "Sigma 16mm F2.8 Filtermatic Fisheye");
        _lensTypeById.put(65535f, "E-Mount, T-Mount, Other Lens or no lens");
        _lensTypeById.put(65535.1f, "Sony E 16mm F2.8");
        _lensTypeById.put(65535.2f, "Sony E 18-55mm F3.5-5.6 OSS");
        _lensTypeById.put(65535.3f, "Sony E 55-210mm F4.5-6.3 OSS");
        _lensTypeById.put(65535.4f, "Sony E 18-200mm F3.5-6.3 OSS");
        _lensTypeById.put(65535.5f, "Sony E 30mm F3.5 Macro");
        _lensTypeById.put(65535.6f, "Sony E 24mm F1.8 ZA");
        _lensTypeById.put(65535.7f, "Sony E 50mm F1.8 OSS");
        _lensTypeById.put(65535.8f, "Sony E 16-70mm F4 ZA OSS");
        _lensTypeById.put(65535.9f, "Sony E 10-18mm F4 OSS");
        _lensTypeById.put(65535.10f, "Sony E PZ 16-50mm F3.5-5.6 OSS");
        _lensTypeById.put(65535.11f, "Sony FE 35mm F2.8 ZA");
        _lensTypeById.put(65535.12f, "Sony FE 24-70mm F4 ZA OSS");
        _lensTypeById.put(65535.13f, "Sony E 18-200mm F3.5-6.3 OSS LE");
        _lensTypeById.put(65535.14f, "Sony E 20mm F2.8");
        _lensTypeById.put(65535.15f, "Sony E 35mm F1.8 OSS");
        _lensTypeById.put(65535.16f, "Sony E PZ 18-105mm F4 G OSS");
        _lensTypeById.put(65535.17f, "Sony FE 90mm F2.8 Macro G OSS");
        _lensTypeById.put(65535.18f, "Sony E 18-50mm F4-5.6");
        _lensTypeById.put(65535.19f, "Sony E PZ 18-200mm F3.5-6.3 OSS");
        _lensTypeById.put(65535.20f, "Sony FE 55mm F1.8 ZA");
        _lensTypeById.put(65535.21f, "Sony FE 70-200mm F4 G OSS");
        _lensTypeById.put(65535.22f, "Sony FE 16-35mm F4 ZA OSS");
        _lensTypeById.put(65535.23f, "Sony FE 50mm F2.8 Macro");
        _lensTypeById.put(65535.24f, "Sony FE 28-70mm F3.5-5.6 OSS");
        _lensTypeById.put(65535.25f, "Sony FE 35mm F1.4 ZA");
        _lensTypeById.put(65535.26f, "Sony FE 24-240mm F3.5-6.3 OSS");
        _lensTypeById.put(65535.27f, "Sony FE 28mm F2");
        _lensTypeById.put(65535.28f, "Sony FE PZ 28-135mm F4 G OSS");
        _lensTypeById.put(65535.29f, "Sony FE 24-70mm F2.8 GM");
        _lensTypeById.put(65535.30f, "Sony FE 50mm F1.4 ZA");
        _lensTypeById.put(65535.31f, "Sony FE 85mm F1.4 GM");
        _lensTypeById.put(65535.32f, "Sony FE 50mm F1.8");
        _lensTypeById.put(65535.33f, "Sony FE 21mm F2.8 (SEL28F20 + SEL075UWC)");
        _lensTypeById.put(65535.34f, "Sony FE 16mm F3.5 Fisheye (SEL28F20 + SEL057FEC)");
        _lensTypeById.put(65535.35f, "Sony FE 70-300mm F4.5-5.6 G OSS");
        _lensTypeById.put(65535.36f, "Sony FE 70-200mm F2.8 GM OSS");
        _lensTypeById.put(65535.37f, "Sony FE 70-200mm F2.8 GM OSS + 1.4X Teleconverter");
        _lensTypeById.put(65535.38f, "Sony FE 70-200mm F2.8 GM OSS + 2X Teleconverter");
        _lensTypeById.put(65535.39f, "Samyang AF 50mm F1.4 FE");
        _lensTypeById.put(65535.40f, "Samyang AF 14mm F2.8 FE");
        _lensTypeById.put(65535.41f, "Sigma 19mm F2.8 [EX] DN");
        _lensTypeById.put(65535.42f, "Sigma 30mm F2.8 [EX] DN");
        _lensTypeById.put(65535.43f, "Sigma 60mm F2.8 DN");
        _lensTypeById.put(65535.44f, "Sigma 30mm F1.4 DC DN | C 016");
        _lensTypeById.put(65535.45f, "Tamron 18-200mm F3.5-6.3 Di III VC");
        _lensTypeById.put(65535.46f, "Zeiss Touit 12mm F2.8");
        _lensTypeById.put(65535.47f, "Zeiss Touit 32mm F1.8");
        _lensTypeById.put(65535.48f, "Zeiss Touit 50mm F2.8 Macro");
        _lensTypeById.put(65535.49f, "Zeiss Batis 25mm F2");
        _lensTypeById.put(65535.50f, "Zeiss Batis 85mm F1.8");
        _lensTypeById.put(65535.51f, "Zeiss Batis 18mm F2.8");
        _lensTypeById.put(65535.52f, "Zeiss Loxia 50mm F2");
        _lensTypeById.put(65535.53f, "Zeiss Loxia 35mm F2");
        _lensTypeById.put(65535.54f, "Zeiss Loxia 21mm F2.8");
        _lensTypeById.put(65535.55f, "Zeiss Loxia 85mm F2.4");
        _lensTypeById.put(65535.56f, "Arax MC 35mm F2.8 Tilt+Shift");
        _lensTypeById.put(65535.57f, "Arax MC 80mm F2.8 Tilt+Shift");
        _lensTypeById.put(65535.58f, "Zenitar MF 16mm F2.8 Fisheye M42");
        _lensTypeById.put(65535.59f, "Samyang 500mm Mirror F8.0");
        _lensTypeById.put(65535.60f, "Pentacon Auto 135mm F2.8");
        _lensTypeById.put(65535.61f, "Pentacon Auto 29mm F2.8");
        _lensTypeById.put(65535.62f, "Helios 44-2 58mm F2.0");
    }
}

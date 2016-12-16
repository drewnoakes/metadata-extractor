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

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.text.DecimalFormat;
import java.util.HashMap;

import static com.drew.metadata.exif.makernotes.OlympusCameraSettingsMakernoteDirectory.*;

/**
 * Provides human-readable String representations of tag values stored in a {@link OlympusCameraSettingsMakernoteDirectory}.
 * <p>
 * Some Description functions and the Extender and Lens types lists converted from Exiftool version 10.10 created by Phil Harvey
 * http://www.sno.phy.queensu.ca/~phil/exiftool/
 * lib\Image\ExifTool\Olympus.pm
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class OlympusCameraSettingsMakernoteDescriptor extends TagDescriptor<OlympusCameraSettingsMakernoteDirectory>
{
    public OlympusCameraSettingsMakernoteDescriptor(@NotNull OlympusCameraSettingsMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TagCameraSettingsVersion:
                return getCameraSettingsVersionDescription();
            case TagPreviewImageValid:
                return getPreviewImageValidDescription();

            case TagExposureMode:
                return getExposureModeDescription();
            case TagAeLock:
                return getAeLockDescription();
            case TagMeteringMode:
                return getMeteringModeDescription();
            case TagExposureShift:
                return getExposureShiftDescription();
            case TagNdFilter:
                return getNdFilterDescription();

            case TagMacroMode:
                return getMacroModeDescription();
            case TagFocusMode:
                return getFocusModeDescription();
            case TagFocusProcess:
                return getFocusProcessDescription();
            case TagAfSearch:
                return getAfSearchDescription();
            case TagAfAreas:
                return getAfAreasDescription();
            case TagAfPointSelected:
                return getAfPointSelectedDescription();
            case TagAfFineTune:
                return getAfFineTuneDescription();

            case TagFlashMode:
                return getFlashModeDescription();
            case TagFlashRemoteControl:
                return getFlashRemoteControlDescription();
            case TagFlashControlMode:
                return getFlashControlModeDescription();
            case TagFlashIntensity:
                return getFlashIntensityDescription();
            case TagManualFlashStrength:
                return getManualFlashStrengthDescription();

            case TagWhiteBalance2:
                return getWhiteBalance2Description();
            case TagWhiteBalanceTemperature:
                return getWhiteBalanceTemperatureDescription();
            case TagCustomSaturation:
                return getCustomSaturationDescription();
            case TagModifiedSaturation:
                return getModifiedSaturationDescription();
            case TagContrastSetting:
                return getContrastSettingDescription();
            case TagSharpnessSetting:
                return getSharpnessSettingDescription();
            case TagColorSpace:
                return getColorSpaceDescription();
            case TagSceneMode:
                return getSceneModeDescription();
            case TagNoiseReduction:
                return getNoiseReductionDescription();
            case TagDistortionCorrection:
                return getDistortionCorrectionDescription();
            case TagShadingCompensation:
                return getShadingCompensationDescription();
            case TagGradation:
                return getGradationDescription();
            case TagPictureMode:
                return getPictureModeDescription();
            case TagPictureModeSaturation:
                return getPictureModeSaturationDescription();
            case TagPictureModeContrast:
                return getPictureModeContrastDescription();
            case TagPictureModeSharpness:
                return getPictureModeSharpnessDescription();
            case TagPictureModeBWFilter:
                return getPictureModeBWFilterDescription();
            case TagPictureModeTone:
                return getPictureModeToneDescription();
            case TagNoiseFilter:
                return getNoiseFilterDescription();
            case TagArtFilter:
                return getArtFilterDescription();
            case TagMagicFilter:
                return getMagicFilterDescription();
            case TagPictureModeEffect:
                return getPictureModeEffectDescription();
            case TagToneLevel:
                return getToneLevelDescription();
            case TagArtFilterEffect:
                return getArtFilterEffectDescription();
            case TagColorCreatorEffect:
                return getColorCreatorEffectDescription();

            case TagDriveMode:
                return getDriveModeDescription();
            case TagPanoramaMode:
                return getPanoramaModeDescription();
            case TagImageQuality2:
                return getImageQuality2Description();
            case TagImageStabilization:
                return getImageStabilizationDescription();

            case TagStackedImage:
                return getStackedImageDescription();

            case TagManometerPressure:
                return getManometerPressureDescription();
            case TagManometerReading:
                return getManometerReadingDescription();
            case TagExtendedWBDetect:
                return getExtendedWBDetectDescription();
            case TagRollAngle:
                return getRollAngleDescription();
            case TagPitchAngle:
                return getPitchAngleDescription();
            case TagDateTimeUtc:
                return getDateTimeUTCDescription();

            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getCameraSettingsVersionDescription()
    {
        return getVersionBytesDescription(TagCameraSettingsVersion, 4);
    }

    @Nullable
    public String getPreviewImageValidDescription()
    {
        return getIndexedDescription(TagPreviewImageValid,
            "No", "Yes");
    }

    @Nullable
    public String getExposureModeDescription()
    {
        return getIndexedDescription(TagExposureMode, 1,
            "Manual", "Program", "Aperture-priority AE", "Shutter speed priority", "Program-shift");
    }

    @Nullable
    public String getAeLockDescription()
    {
        return getIndexedDescription(TagAeLock,
            "Off", "On");
    }

    @Nullable
    public String getMeteringModeDescription()
    {
        Integer value = _directory.getInteger(TagMeteringMode);
        if (value == null)
            return null;

        switch (value) {
            case 2:
                return "Center-weighted average";
            case 3:
                return "Spot";
            case 5:
                return "ESP";
            case 261:
                return "Pattern+AF";
            case 515:
                return "Spot+Highlight control";
            case 1027:
                return "Spot+Shadow control";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getExposureShiftDescription()
    {
        return getRationalOrDoubleString(TagExposureShift);
    }

    @Nullable
    public String getNdFilterDescription()
    {
        return getIndexedDescription(TagNdFilter, "Off", "On");
    }

    @Nullable
    public String getMacroModeDescription()
    {
        return getIndexedDescription(TagMacroMode, "Off", "On", "Super Macro");
    }

    @Nullable
    public String getFocusModeDescription()
    {
        int[] values = _directory.getIntArray(TagFocusMode);
        if (values == null) {
            // check if it's only one value long also
            Integer value = _directory.getInteger(TagFocusMode);
            if (value == null)
                return null;

            values = new int[]{value};
        }

        if (values.length == 0)
            return null;

        StringBuilder sb = new StringBuilder();
        switch (values[0]) {
            case 0:
                sb.append("Single AF");
                break;
            case 1:
                sb.append("Sequential shooting AF");
                break;
            case 2:
                sb.append("Continuous AF");
                break;
            case 3:
                sb.append("Multi AF");
                break;
            case 4:
                sb.append("Face detect");
                break;
            case 10:
                sb.append("MF");
                break;
            default:
                sb.append("Unknown (" + values[0] + ")");
                break;
        }

        if (values.length > 1) {
            sb.append("; ");
            int value1 = values[1];

            if (value1 == 0) {
                sb.append("(none)");
            } else {
                if (( value1       & 1) > 0) sb.append("S-AF, ");
                if (((value1 >> 2) & 1) > 0) sb.append("C-AF, ");
                if (((value1 >> 4) & 1) > 0) sb.append("MF, ");
                if (((value1 >> 5) & 1) > 0) sb.append("Face detect, ");
                if (((value1 >> 6) & 1) > 0) sb.append("Imager AF, ");
                if (((value1 >> 7) & 1) > 0) sb.append("Live View Magnification Frame, ");
                if (((value1 >> 8) & 1) > 0) sb.append("AF sensor, ");

                sb.setLength(sb.length() - 2);
            }
        }

        return sb.toString();
    }

    @Nullable
    public String getFocusProcessDescription()
    {
        int[] values = _directory.getIntArray(TagFocusProcess);
        if (values == null) {
            // check if it's only one value long also
            Integer value = _directory.getInteger(TagFocusProcess);
            if (value == null)
                return null;

            values = new int[]{value};
        }

        if (values.length == 0)
            return null;

        StringBuilder sb = new StringBuilder();

        switch (values[0]) {
            case 0:
                sb.append("AF not used");
                break;
            case 1:
                sb.append("AF used");
                break;
            default:
                sb.append("Unknown (" + values[0] + ")");
                break;
        }

        if (values.length > 1)
            sb.append("; " + values[1]);

        return sb.toString();
    }

    @Nullable
    public String getAfSearchDescription()
    {
        return getIndexedDescription(TagAfSearch, "Not Ready", "Ready");
    }

    /// <summary>
    /// coordinates range from 0 to 255
    /// </summary>
    /// <returns></returns>
    @Nullable
    public String getAfAreasDescription()
    {
        Object obj = _directory.getObject(TagAfAreas);
        if (obj == null || !(obj instanceof long[]))
            return null;

        StringBuilder sb = new StringBuilder();
        for (long point : (long[]) obj) {
            if (point == 0L)
                continue;
            if (sb.length() != 0)
                sb.append(", ");

            if (point == 0x36794285L)
                sb.append("Left ");
            else if (point == 0x79798585L)
                sb.append("Center ");
            else if (point == 0xBD79C985L)
                sb.append("Right ");

            sb.append(String.format("(%d/255,%d/255)-(%d/255,%d/255)",
                (point >> 24) & 0xFF,
                (point >> 16) & 0xFF,
                (point >> 8) & 0xFF,
                point & 0xFF));
        }

        return sb.length() == 0 ? null : sb.toString();
    }

    /// <summary>
    /// coordinates expressed as a percent
    /// </summary>
    /// <returns></returns>
    @Nullable
    public String getAfPointSelectedDescription()
    {
        Rational[] values = _directory.getRationalArray(TagAfPointSelected);
        if (values == null)
            return "n/a";

        if (values.length < 4)
            return null;

        int index = 0;
        if (values.length == 5 && values[0].longValue() == 0)
            index = 1;

        int p1 = (int)(values[index].doubleValue() * 100);
        int p2 = (int)(values[index + 1].doubleValue() * 100);
        int p3 = (int)(values[index + 2].doubleValue() * 100);
        int p4 = (int)(values[index + 3].doubleValue() * 100);

        if(p1 + p2 + p3 + p4 == 0)
            return "n/a";

        return String.format("(%d%%,%d%%) (%d%%,%d%%)", p1, p2, p3, p4);
    }

    @Nullable
    public String getAfFineTuneDescription()
    {
        return getIndexedDescription(TagAfFineTune, "Off", "On");
    }

    @Nullable
    public String getFlashModeDescription()
    {
        Integer value = _directory.getInteger(TagFlashMode);
        if (value == null)
            return null;

        if (value == 0)
            return "Off";

        StringBuilder sb = new StringBuilder();
        int v = value;

        if (( v       & 1) != 0) sb.append("On, ");
        if (((v >> 1) & 1) != 0) sb.append("Fill-in, ");
        if (((v >> 2) & 1) != 0) sb.append("Red-eye, ");
        if (((v >> 3) & 1) != 0) sb.append("Slow-sync, ");
        if (((v >> 4) & 1) != 0) sb.append("Forced On, ");
        if (((v >> 5) & 1) != 0) sb.append("2nd Curtain, ");

        return sb.substring(0, sb.length() - 2);
    }

    @Nullable
    public String getFlashRemoteControlDescription()
    {
        Integer value = _directory.getInteger(TagFlashRemoteControl);
        if (value == null)
            return null;

        switch (value) {
            case 0:
                return "Off";
            case 0x01:
                return "Channel 1, Low";
            case 0x02:
                return "Channel 2, Low";
            case 0x03:
                return "Channel 3, Low";
            case 0x04:
                return "Channel 4, Low";
            case 0x09:
                return "Channel 1, Mid";
            case 0x0a:
                return "Channel 2, Mid";
            case 0x0b:
                return "Channel 3, Mid";
            case 0x0c:
                return "Channel 4, Mid";
            case 0x11:
                return "Channel 1, High";
            case 0x12:
                return "Channel 2, High";
            case 0x13:
                return "Channel 3, High";
            case 0x14:
                return "Channel 4, High";

            default:
                return "Unknown (" + value + ")";
        }
    }

    /// <summary>
    /// 3 or 4 values
    /// </summary>
    /// <returns></returns>
    @Nullable
    public String getFlashControlModeDescription()
    {
        int[] values = _directory.getIntArray(TagFlashControlMode);
        if (values == null)
            return null;

        if (values.length == 0)
            return null;

        StringBuilder sb = new StringBuilder();

        switch (values[0]) {
            case 0:
                sb.append("Off");
                break;
            case 3:
                sb.append("TTL");
                break;
            case 4:
                sb.append("Auto");
                break;
            case 5:
                sb.append("Manual");
                break;
            default:
                sb.append("Unknown (").append(values[0]).append(")");
                break;
        }

        for (int i = 1; i < values.length; i++)
            sb.append("; ").append(values[i]);

        return sb.toString();
    }

    /// <summary>
    /// 3 or 4 values
    /// </summary>
    /// <returns></returns>
    @Nullable
    public String getFlashIntensityDescription()
    {
        Rational[] values = _directory.getRationalArray(TagFlashIntensity);
        if (values == null || values.length == 0)
            return null;

        if (values.length == 3) {
            if (values[0].getDenominator() == 0 && values[1].getDenominator() == 0 && values[2].getDenominator() == 0)
                return "n/a";
        } else if (values.length == 4) {
            if (values[0].getDenominator() == 0 && values[1].getDenominator() == 0 && values[2].getDenominator() == 0 && values[3].getDenominator() == 0)
                return "n/a (x4)";
        }

        StringBuilder sb = new StringBuilder();
        for (Rational t : values)
            sb.append(t).append(", ");

        return sb.substring(0, sb.length() - 2);
    }

    @Nullable
    public String getManualFlashStrengthDescription()
    {
        Rational[] values = _directory.getRationalArray(TagManualFlashStrength);
        if (values == null || values.length == 0)
            return "n/a";

        if (values.length == 3) {
            if (values[0].getDenominator() == 0 && values[1].getDenominator() == 0 && values[2].getDenominator() == 0)
                return "n/a";
        } else if (values.length == 4) {
            if (values[0].getDenominator() == 0 && values[1].getDenominator() == 0 && values[2].getDenominator() == 0 && values[3].getDenominator() == 0)
                return "n/a (x4)";
        }

        StringBuilder sb = new StringBuilder();
        for (Rational t : values)
            sb.append(t).append(", ");

        return sb.substring(0, sb.length() - 2);
    }

    @Nullable
    public String getWhiteBalance2Description()
    {
        Integer value = _directory.getInteger(TagWhiteBalance2);
        if (value == null)
            return null;

        switch (value) {
            case 0:
                return "Auto";
            case 1:
                return "Auto (Keep Warm Color Off)";
            case 16:
                return "7500K (Fine Weather with Shade)";
            case 17:
                return "6000K (Cloudy)";
            case 18:
                return "5300K (Fine Weather)";
            case 20:
                return "3000K (Tungsten light)";
            case 21:
                return "3600K (Tungsten light-like)";
            case 22:
                return "Auto Setup";
            case 23:
                return "5500K (Flash)";
            case 33:
                return "6600K (Daylight fluorescent)";
            case 34:
                return "4500K (Neutral white fluorescent)";
            case 35:
                return "4000K (Cool white fluorescent)";
            case 36:
                return "White Fluorescent";
            case 48:
                return "3600K (Tungsten light-like)";
            case 67:
                return "Underwater";
            case 256:
                return "One Touch WB 1";
            case 257:
                return "One Touch WB 2";
            case 258:
                return "One Touch WB 3";
            case 259:
                return "One Touch WB 4";
            case 512:
                return "Custom WB 1";
            case 513:
                return "Custom WB 2";
            case 514:
                return "Custom WB 3";
            case 515:
                return "Custom WB 4";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getWhiteBalanceTemperatureDescription()
    {
        Integer value = _directory.getInteger(TagWhiteBalanceTemperature);
        if (value == null)
            return null;
        if (value == 0)
            return "Auto";
        return value.toString();
    }

    @Nullable
    public String getCustomSaturationDescription()
    {
        // TODO: if model is /^E-1\b/  then
        // $a-=$b; $c-=$b;
        // return "CS$a (min CS0, max CS$c)"
        return getValueMinMaxDescription(TagCustomSaturation);
    }

    @Nullable
    public String getModifiedSaturationDescription()
    {
        return getIndexedDescription(TagModifiedSaturation,
            "Off", "CM1 (Red Enhance)", "CM2 (Green Enhance)", "CM3 (Blue Enhance)", "CM4 (Skin Tones)");
    }

    @Nullable
    public String getContrastSettingDescription()
    {
        return getValueMinMaxDescription(TagContrastSetting);
    }

    @Nullable
    public String getSharpnessSettingDescription()
    {
        return getValueMinMaxDescription(TagSharpnessSetting);
    }

    @Nullable
    public String getColorSpaceDescription()
    {
        return getIndexedDescription(TagColorSpace,
            "sRGB", "Adobe RGB", "Pro Photo RGB");
    }

    @Nullable
    public String getSceneModeDescription()
    {
        Integer value = _directory.getInteger(TagSceneMode);
        if (value == null)
            return null;

        switch (value) {
            case 0:
                return "Standard";
            case 6:
                return "Auto";
            case 7:
                return "Sport";
            case 8:
                return "Portrait";
            case 9:
                return "Landscape+Portrait";
            case 10:
                return "Landscape";
            case 11:
                return "Night Scene";
            case 12:
                return "Self Portrait";
            case 13:
                return "Panorama";
            case 14:
                return "2 in 1";
            case 15:
                return "Movie";
            case 16:
                return "Landscape+Portrait";
            case 17:
                return "Night+Portrait";
            case 18:
                return "Indoor";
            case 19:
                return "Fireworks";
            case 20:
                return "Sunset";
            case 21:
                return "Beauty Skin";
            case 22:
                return "Macro";
            case 23:
                return "Super Macro";
            case 24:
                return "Food";
            case 25:
                return "Documents";
            case 26:
                return "Museum";
            case 27:
                return "Shoot & Select";
            case 28:
                return "Beach & Snow";
            case 29:
                return "Self Portrait+Timer";
            case 30:
                return "Candle";
            case 31:
                return "Available Light";
            case 32:
                return "Behind Glass";
            case 33:
                return "My Mode";
            case 34:
                return "Pet";
            case 35:
                return "Underwater Wide1";
            case 36:
                return "Underwater Macro";
            case 37:
                return "Shoot & Select1";
            case 38:
                return "Shoot & Select2";
            case 39:
                return "High Key";
            case 40:
                return "Digital Image Stabilization";
            case 41:
                return "Auction";
            case 42:
                return "Beach";
            case 43:
                return "Snow";
            case 44:
                return "Underwater Wide2";
            case 45:
                return "Low Key";
            case 46:
                return "Children";
            case 47:
                return "Vivid";
            case 48:
                return "Nature Macro";
            case 49:
                return "Underwater Snapshot";
            case 50:
                return "Shooting Guide";
            case 54:
                return "Face Portrait";
            case 57:
                return "Bulb";
            case 59:
                return "Smile Shot";
            case 60:
                return "Quick Shutter";
            case 63:
                return "Slow Shutter";
            case 64:
                return "Bird Watching";
            case 65:
                return "Multiple Exposure";
            case 66:
                return "e-Portrait";
            case 67:
                return "Soft Background Shot";
            case 142:
                return "Hand-held Starlight";
            case 154:
                return "HDR";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getNoiseReductionDescription()
    {
        Integer value = _directory.getInteger(TagNoiseReduction);
        if (value == null)
            return null;

        if (value == 0)
            return "(none)";

        StringBuilder sb = new StringBuilder();
        int v = value;

        if ((v & 1) != 0) sb.append("Noise Reduction, ");
        if (((v >> 1) & 1) != 0) sb.append("Noise Filter, ");
        if (((v >> 2) & 1) != 0) sb.append("Noise Filter (ISO Boost), ");
        if (((v >> 3) & 1) != 0) sb.append("Auto, ");

        return sb.length() != 0
            ? sb.substring(0, sb.length() - 2)
            : "(none)";
    }

    @Nullable
    public String getDistortionCorrectionDescription()
    {
        return getIndexedDescription(TagDistortionCorrection, "Off", "On");
    }

    @Nullable
    public String getShadingCompensationDescription()
    {
        return getIndexedDescription(TagShadingCompensation, "Off", "On");
    }

    /// <summary>
    /// 3 or 4 values
    /// </summary>
    /// <returns></returns>
    @Nullable
    public String getGradationDescription()
    {
        int[] values = _directory.getIntArray(TagGradation);
        if (values == null || values.length < 3)
            return null;

        String join = String.format("%d %d %d", values[0], values[1], values[2]);

        String ret;
        if (join.equals("0 0 0")) {
            ret = "n/a";
        } else if (join.equals("-1 -1 1")) {
            ret = "Low Key";
        } else if (join.equals("0 -1 1")) {
            ret = "Normal";
        } else if (join.equals("1 -1 1")) {
            ret = "High Key";
        } else {
            ret = "Unknown (" + join + ")";
        }

        if (values.length > 3) {
            if (values[3] == 0)
                ret += "; User-Selected";
            else if (values[3] == 1)
                ret += "; Auto-Override";
        }

        return ret;
    }

    /// <summary>
    /// 1 or 2 values
    /// </summary>
    /// <returns></returns>
    @Nullable
    public String getPictureModeDescription()
    {
        int[] values = _directory.getIntArray(TagPictureMode);
        if (values == null) {
            // check if it's only one value long also
            Integer value = _directory.getInteger(TagNoiseReduction);
            if (value == null)
                return null;

            values = new int[]{value};
        }

        if (values.length == 0)
            return null;

        StringBuilder sb = new StringBuilder();
        switch (values[0]) {
            case 1:
                sb.append("Vivid");
                break;
            case 2:
                sb.append("Natural");
                break;
            case 3:
                sb.append("Muted");
                break;
            case 4:
                sb.append("Portrait");
                break;
            case 5:
                sb.append("i-Enhance");
                break;
            case 256:
                sb.append("Monotone");
                break;
            case 512:
                sb.append("Sepia");
                break;
            default:
                sb.append("Unknown (").append(values[0]).append(")");
                break;
        }

        if (values.length > 1)
            sb.append("; ").append(values[1]);

        return sb.toString();
    }

    @Nullable
    public String getPictureModeSaturationDescription()
    {
        return getValueMinMaxDescription(TagPictureModeSaturation);
    }

    @Nullable
    public String getPictureModeContrastDescription()
    {
        return getValueMinMaxDescription(TagPictureModeContrast);
    }

    @Nullable
    public String getPictureModeSharpnessDescription()
    {
        return getValueMinMaxDescription(TagPictureModeSharpness);
    }

    @Nullable
    public String getPictureModeBWFilterDescription()
    {
        return getIndexedDescription(TagPictureModeBWFilter,
            "n/a", "Neutral", "Yellow", "Orange", "Red", "Green");
    }

    @Nullable
    public String getPictureModeToneDescription()
    {
        return getIndexedDescription(TagPictureModeTone,
            "n/a", "Neutral", "Sepia", "Blue", "Purple", "Green");
    }

    @Nullable
    public String getNoiseFilterDescription()
    {
        int[] values = _directory.getIntArray(TagNoiseFilter);
        if (values == null)
            return null;

        String join = String.format("%d %d %d", values[0], values[1], values[2]);

        if (join.equals("0 0 0"))
            return "n/a";
        if (join.equals("-2 -2 1"))
            return "Off";
        if (join.equals("-1 -2 1"))
            return "Low";
        if (join.equals("0 -2 1"))
            return "Standard";
        if (join.equals("1 -2 1"))
            return "High";
        return "Unknown (" + join + ")";
    }

    @Nullable
    public String getArtFilterDescription()
    {
        return getFiltersDescription(TagArtFilter);
    }

    @Nullable
    public String getMagicFilterDescription()
    {
        return getFiltersDescription(TagMagicFilter);
    }

    @Nullable
    public String getPictureModeEffectDescription()
    {
        int[] values = _directory.getIntArray(TagPictureModeEffect);
        if (values == null)
            return null;

        String key = String.format("%d %d %d", values[0], values[1], values[2]);
        if (key.equals("0 0 0"))
            return "n/a";
        if (key.equals("-1 -1 1"))
            return "Low";
        if (key.equals("0 -1 1"))
            return "Standard";
        if (key.equals("1 -1 1"))
            return "High";
        return "Unknown (" + key + ")";
    }

    @Nullable
    public String getToneLevelDescription()
    {
        int[] values = _directory.getIntArray(TagToneLevel);
        if (values == null || values.length == 0)
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i == 0 || i == 4 || i == 8 || i == 12 || i == 16 || i == 20 || i == 24)
                sb.append(_toneLevelType.get(values[i])).append("; ");
            else
                sb.append(values[i]).append("; ");
        }

        return sb.substring(0, sb.length() - 2);
    }

    @Nullable
    public String getArtFilterEffectDescription()
    {
        int[] values = _directory.getIntArray(TagArtFilterEffect);
        if (values == null)
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i == 0) {
                sb.append((_filters.containsKey(values[i]) ? _filters.get(values[i]) : "[unknown]")).append("; ");
            } else if (i == 3) {
                sb.append("Partial Color ").append(values[i]).append("; ");
            } else if (i == 4) {
                switch (values[i]) {
                    case 0x0000:
                        sb.append("No Effect");
                        break;
                    case 0x8010:
                        sb.append("Star Light");
                        break;
                    case 0x8020:
                        sb.append("Pin Hole");
                        break;
                    case 0x8030:
                        sb.append("Frame");
                        break;
                    case 0x8040:
                        sb.append("Soft Focus");
                        break;
                    case 0x8050:
                        sb.append("White Edge");
                        break;
                    case 0x8060:
                        sb.append("B&W");
                        break;
                    default:
                        sb.append("Unknown (").append(values[i]).append(")");
                        break;
                }
                sb.append("; ");
            } else if (i == 6) {
                switch (values[i]) {
                    case 0:
                        sb.append("No Color Filter");
                        break;
                    case 1:
                        sb.append("Yellow Color Filter");
                        break;
                    case 2:
                        sb.append("Orange Color Filter");
                        break;
                    case 3:
                        sb.append("Red Color Filter");
                        break;
                    case 4:
                        sb.append("Green Color Filter");
                        break;
                    default:
                        sb.append("Unknown (").append(values[i]).append(")");
                        break;
                }
                sb.append("; ");
            } else {
                sb.append(values[i]).append("; ");
            }
        }

        return sb.substring(0, sb.length() - 2);
    }

    @Nullable
    public String getColorCreatorEffectDescription()
    {
        int[] values = _directory.getIntArray(TagColorCreatorEffect);
        if (values == null)
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i == 0) {
                sb.append("Color ").append(values[i]).append("; ");
            } else if (i == 3) {
                sb.append("Strength ").append(values[i]).append("; ");
            } else {
                sb.append(values[i]).append("; ");
            }
        }

        return sb.substring(0, sb.length() - 2);
    }

    /// <summary>
    /// 2 or 3 numbers: 1. Mode, 2. Shot number, 3. Mode bits
    /// </summary>
    /// <returns></returns>
    @Nullable
    public String getDriveModeDescription()
    {
        int[] values = _directory.getIntArray(TagDriveMode);
        if (values == null)
            return null;

        if (values.length == 0 || values[0] == 0)
            return "Single Shot";

        StringBuilder a = new StringBuilder();

        if (values[0] == 5 && values.length >= 3) {
            int c = values[2];
            if (( c       & 1) > 0) a.append("AE");
            if (((c >> 1) & 1) > 0) a.append("WB");
            if (((c >> 2) & 1) > 0) a.append("FL");
            if (((c >> 3) & 1) > 0) a.append("MF");
            if (((c >> 6) & 1) > 0) a.append("Focus");

            a.append(" Bracketing");
        } else {
            switch (values[0]) {
                case 1:
                    a.append("Continuous Shooting");
                    break;
                case 2:
                    a.append("Exposure Bracketing");
                    break;
                case 3:
                    a.append("White Balance Bracketing");
                    break;
                case 4:
                    a.append("Exposure+WB Bracketing");
                    break;
                default:
                    a.append("Unknown (").append(values[0]).append(")");
                    break;
            }
        }

        a.append(", Shot ").append(values[1]);

        return a.toString();
    }

    /// <summary>
    /// 2 numbers: 1. Mode, 2. Shot number
    /// </summary>
    /// <returns></returns>
    @Nullable
    public String getPanoramaModeDescription()
    {
        int[] values = _directory.getIntArray(TagPanoramaMode);
        if (values == null)
            return null;

        if (values.length == 0 || values[0] == 0)
            return "Off";

        String a;
        switch (values[0]) {
            case 1:
                a = "Left to Right";
                break;
            case 2:
                a = "Right to Left";
                break;
            case 3:
                a = "Bottom to Top";
                break;
            case 4:
                a = "Top to Bottom";
                break;
            default:
                a = "Unknown (" + values[0] + ")";
                break;
        }

        return String.format("%s, Shot %d", a, values[1]);
    }

    @Nullable
    public String getImageQuality2Description()
    {
        return getIndexedDescription(TagImageQuality2, 1,
            "SQ", "HQ", "SHQ", "RAW", "SQ (5)");
    }

    @Nullable
    public String getImageStabilizationDescription()
    {
        return getIndexedDescription(TagImageStabilization,
            "Off", "On, Mode 1", "On, Mode 2", "On, Mode 3", "On, Mode 4");
    }

    @Nullable
    public String getStackedImageDescription()
    {
        int[] values = _directory.getIntArray(TagStackedImage);
        if (values == null || values.length < 2)
            return null;

        int v1 = values[0];
        int v2 = values[1];

        if (v1 == 0 && v2 == 0)
            return "No";
        if (v1 == 9 && v2 == 8)
            return "Focus-stacked (8 images)";

        return String.format("Unknown (%d %d)", v1, v2);
    }

    /// <remarks>
    /// TODO: need better image examples to test this function
    /// </remarks>
    /// <returns></returns>
    @Nullable
    public String getManometerPressureDescription()
    {
        Integer value = _directory.getInteger(TagManometerPressure);
        if (value == null)
            return null;

        return String.format("%s kPa", new DecimalFormat("#.##").format(value / 10.0));
    }

    /// <remarks>
    /// TODO: need better image examples to test this function
    /// </remarks>
    /// <returns></returns>
    @Nullable
    public String getManometerReadingDescription()
    {
        int[] values = _directory.getIntArray(TagManometerReading);
        if (values == null || values.length < 2)
            return null;

        DecimalFormat format = new DecimalFormat("#.##");
        return String.format("%s m, %s ft",
            format.format(values[0] / 10.0),
            format.format(values[1] / 10.0));
    }

    @Nullable
    public String getExtendedWBDetectDescription()
    {
        return getIndexedDescription(TagExtendedWBDetect, "Off", "On");
    }

    /// <summary>
    /// converted to degrees of clockwise camera rotation
    /// </summary>
    /// <remarks>
    /// TODO: need better image examples to test this function
    /// </remarks>
    /// <returns></returns>
    @Nullable
    public String getRollAngleDescription()
    {
        int[] values = _directory.getIntArray(TagRollAngle);
        if (values == null || values.length < 2)
            return null;

        String ret = values[0] != 0
            ? Double.toString(-values[0] / 10.0)
            : "n/a";

        return String.format("%s %d", ret, values[1]);
    }

    /// <summary>
    /// converted to degrees of upward camera tilt
    /// </summary>
    /// <remarks>
    /// TODO: need better image examples to test this function
    /// </remarks>
    /// <returns></returns>
    @Nullable
    public String getPitchAngleDescription()
    {
        int[] values = _directory.getIntArray(TagPitchAngle);
        if (values == null || values.length < 2)
            return null;

        // (second value is 0 if level gauge is off)
        String ret = values[0] != 0
            ? Double.toString(values[0] / 10.0)
            : "n/a";

        return String.format("%s %d", ret, values[1]);
    }

    @Nullable
    public String getDateTimeUTCDescription()
    {
        Object value = _directory.getObject(TagDateTimeUtc);
        if (value == null)
            return null;
        return value.toString();
    }

    @Nullable
    private String getValueMinMaxDescription(int tagId)
    {
        int[] values = _directory.getIntArray(tagId);
        if (values == null || values.length < 3)
            return null;

        return String.format("%d (min %d, max %d)", values[0], values[1], values[2]);
    }

    @Nullable
    private String getFiltersDescription(int tagId)
    {
        int[] values = _directory.getIntArray(tagId);
        if (values == null || values.length == 0)
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i == 0)
                sb.append(_filters.containsKey(values[i]) ? _filters.get(values[i]) : "[unknown]");
            else
                sb.append(values[i]);
            sb.append("; ");
        }

        return sb.substring(0, sb.length() - 2);
    }

    private static final HashMap<Integer, String> _toneLevelType = new HashMap<Integer, String>();
    // ArtFilter, ArtFilterEffect and MagicFilter values
    private static final HashMap<Integer, String> _filters = new HashMap<Integer, String>();

    static {
        _filters.put(0, "Off");
        _filters.put(1, "Soft Focus");
        _filters.put(2, "Pop Art");
        _filters.put(3, "Pale & Light Color");
        _filters.put(4, "Light Tone");
        _filters.put(5, "Pin Hole");
        _filters.put(6, "Grainy Film");
        _filters.put(9, "Diorama");
        _filters.put(10, "Cross Process");
        _filters.put(12, "Fish Eye");
        _filters.put(13, "Drawing");
        _filters.put(14, "Gentle Sepia");
        _filters.put(15, "Pale & Light Color II");
        _filters.put(16, "Pop Art II");
        _filters.put(17, "Pin Hole II");
        _filters.put(18, "Pin Hole III");
        _filters.put(19, "Grainy Film II");
        _filters.put(20, "Dramatic Tone");
        _filters.put(21, "Punk");
        _filters.put(22, "Soft Focus 2");
        _filters.put(23, "Sparkle");
        _filters.put(24, "Watercolor");
        _filters.put(25, "Key Line");
        _filters.put(26, "Key Line II");
        _filters.put(27, "Miniature");
        _filters.put(28, "Reflection");
        _filters.put(29, "Fragmented");
        _filters.put(31, "Cross Process II");
        _filters.put(32, "Dramatic Tone II");
        _filters.put(33, "Watercolor I");
        _filters.put(34, "Watercolor II");
        _filters.put(35, "Diorama II");
        _filters.put(36, "Vintage");
        _filters.put(37, "Vintage II");
        _filters.put(38, "Vintage III");
        _filters.put(39, "Partial Color");
        _filters.put(40, "Partial Color II");
        _filters.put(41, "Partial Color III");

        _toneLevelType.put(0, "0");
        _toneLevelType.put(-31999, "Highlights ");
        _toneLevelType.put(-31998, "Shadows ");
        _toneLevelType.put(-31997, "Midtones ");
    }
}

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

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.makernotes.FujifilmMakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link FujifilmMakernoteDirectory}.
 * <p>
 * Fujifilm added their Makernote tag from the Year 2000's models (e.g.Finepix1400,
 * Finepix4700). It uses IFD format and start from ASCII character 'FUJIFILM', and next 4
 * bytes (value 0x000c) points the offset to first IFD entry.
 * <pre><code>
 * :0000: 46 55 4A 49 46 49 4C 4D-0C 00 00 00 0F 00 00 00 :0000: FUJIFILM........
 * :0010: 07 00 04 00 00 00 30 31-33 30 00 10 02 00 08 00 :0010: ......0130......
 * </code></pre>
 * There are two big differences to the other manufacturers.
 * <ul>
 * <li>Fujifilm's Exif data uses Motorola align, but Makernote ignores it and uses Intel align.</li>
 * <li>
 * The other manufacturer's Makernote counts the "offset to data" from the first byte of TIFF header
 * (same as the other IFD), but Fujifilm counts it from the first byte of Makernote itself.
 * </li>
 * </ul>
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class FujifilmMakernoteDescriptor extends TagDescriptor<FujifilmMakernoteDirectory>
{
    public FujifilmMakernoteDescriptor(@NotNull FujifilmMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_MAKERNOTE_VERSION:
                return getMakernoteVersionDescription();
            case TAG_SHARPNESS:
                return getSharpnessDescription();
            case TAG_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case TAG_COLOR_SATURATION:
                return getColorSaturationDescription();
            case TAG_TONE:
                return getToneDescription();
            case TAG_CONTRAST:
                return getContrastDescription();
            case TAG_NOISE_REDUCTION:
                return getNoiseReductionDescription();
            case TAG_HIGH_ISO_NOISE_REDUCTION:
                return getHighIsoNoiseReductionDescription();
            case TAG_FLASH_MODE:
                return getFlashModeDescription();
            case TAG_FLASH_EV:
                return getFlashExposureValueDescription();
            case TAG_MACRO:
                return getMacroDescription();
            case TAG_FOCUS_MODE:
                return getFocusModeDescription();
            case TAG_SLOW_SYNC:
                return getSlowSyncDescription();
            case TAG_PICTURE_MODE:
                return getPictureModeDescription();
            case TAG_EXR_AUTO:
                return getExrAutoDescription();
            case TAG_EXR_MODE:
                return getExrModeDescription();
            case TAG_AUTO_BRACKETING:
                return getAutoBracketingDescription();
            case TAG_FINE_PIX_COLOR:
                return getFinePixColorDescription();
            case TAG_BLUR_WARNING:
                return getBlurWarningDescription();
            case TAG_FOCUS_WARNING:
                return getFocusWarningDescription();
            case TAG_AUTO_EXPOSURE_WARNING:
                return getAutoExposureWarningDescription();
            case TAG_DYNAMIC_RANGE:
                return getDynamicRangeDescription();
            case TAG_FILM_MODE:
                return getFilmModeDescription();
            case TAG_DYNAMIC_RANGE_SETTING:
                return getDynamicRangeSettingDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    private String getMakernoteVersionDescription()
    {
        return getVersionBytesDescription(TAG_MAKERNOTE_VERSION, 2);
    }

    @Nullable
    public String getSharpnessDescription()
    {
        final Integer value = _directory.getInteger(TAG_SHARPNESS);
        if (value == null)
            return null;
        switch (value) {
            case 1: return "Softest";
            case 2: return "Soft";
            case 3: return "Normal";
            case 4: return "Hard";
            case 5: return "Hardest";
            case 0x82: return "Medium Soft";
            case 0x84: return "Medium Hard";
            case 0x8000: return "Film Simulation";
            case 0xFFFF: return "N/A";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        final Integer value = _directory.getInteger(TAG_WHITE_BALANCE);
        if (value == null)
            return null;
        switch (value) {
            case 0x000: return "Auto";
            case 0x100: return "Daylight";
            case 0x200: return "Cloudy";
            case 0x300: return "Daylight Fluorescent";
            case 0x301: return "Day White Fluorescent";
            case 0x302: return "White Fluorescent";
            case 0x303: return "Warm White Fluorescent";
            case 0x304: return "Living Room Warm White Fluorescent";
            case 0x400: return "Incandescence";
            case 0x500: return "Flash";
            case 0xf00: return "Custom White Balance";
            case 0xf01: return "Custom White Balance 2";
            case 0xf02: return "Custom White Balance 3";
            case 0xf03: return "Custom White Balance 4";
            case 0xf04: return "Custom White Balance 5";
            case 0xff0: return "Kelvin";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getColorSaturationDescription()
    {
        final Integer value = _directory.getInteger(TAG_COLOR_SATURATION);
        if (value == null)
            return null;
        switch (value) {
            case 0x000: return "Normal";
            case 0x080: return "Medium High";
            case 0x100: return "High";
            case 0x180: return "Medium Low";
            case 0x200: return "Low";
            case 0x300: return "None (B&W)";
            case 0x301: return "B&W Green Filter";
            case 0x302: return "B&W Yellow Filter";
            case 0x303: return "B&W Blue Filter";
            case 0x304: return "B&W Sepia";
            case 0x8000: return "Film Simulation";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getToneDescription()
    {
        final Integer value = _directory.getInteger(TAG_TONE);
        if (value == null)
            return null;
        switch (value) {
            case 0x000: return "Normal";
            case 0x080: return "Medium High";
            case 0x100: return "High";
            case 0x180: return "Medium Low";
            case 0x200: return "Low";
            case 0x300: return "None (B&W)";
            case 0x8000: return "Film Simulation";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getContrastDescription()
    {
        final Integer value = _directory.getInteger(TAG_CONTRAST);
        if (value == null)
            return null;
        switch (value) {
            case 0x000: return "Normal";
            case 0x100: return "High";
            case 0x300: return "Low";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getNoiseReductionDescription()
    {
        final Integer value = _directory.getInteger(TAG_NOISE_REDUCTION);
        if (value == null)
            return null;
        switch (value) {
            case 0x040: return "Low";
            case 0x080: return "Normal";
            case 0x100: return "N/A";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getHighIsoNoiseReductionDescription()
    {
        final Integer value = _directory.getInteger(TAG_HIGH_ISO_NOISE_REDUCTION);
        if (value == null)
            return null;
        switch (value) {
            case 0x000: return "Normal";
            case 0x100: return "Strong";
            case 0x200: return "Weak";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFlashModeDescription()
    {
        return getIndexedDescription(
            TAG_FLASH_MODE,
            "Auto",
            "On",
            "Off",
            "Red-eye Reduction",
            "External"
        );
    }

    @Nullable
    public String getFlashExposureValueDescription()
    {
        Rational value = _directory.getRational(TAG_FLASH_EV);
        return value == null ? null : value.toSimpleString(false) + " EV (Apex)";
    }

    @Nullable
    public String getMacroDescription()
    {
        return getIndexedDescription(TAG_MACRO, "Off", "On");
    }

    @Nullable
    public String getFocusModeDescription()
    {
        return getIndexedDescription(TAG_FOCUS_MODE, "Auto Focus", "Manual Focus");
    }

    @Nullable
    public String getSlowSyncDescription()
    {
        return getIndexedDescription(TAG_SLOW_SYNC, "Off", "On");
    }

    @Nullable
    public String getPictureModeDescription()
    {
        final Integer value = _directory.getInteger(TAG_PICTURE_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 0x000: return "Auto";
            case 0x001: return "Portrait scene";
            case 0x002: return "Landscape scene";
            case 0x003: return "Macro";
            case 0x004: return "Sports scene";
            case 0x005: return "Night scene";
            case 0x006: return "Program AE";
            case 0x007: return "Natural Light";
            case 0x008: return "Anti-blur";
            case 0x009: return "Beach & Snow";
            case 0x00a: return "Sunset";
            case 0x00b: return "Museum";
            case 0x00c: return "Party";
            case 0x00d: return "Flower";
            case 0x00e: return "Text";
            case 0x00f: return "Natural Light & Flash";
            case 0x010: return "Beach";
            case 0x011: return "Snow";
            case 0x012: return "Fireworks";
            case 0x013: return "Underwater";
            case 0x014: return "Portrait with Skin Correction";
            // skip 0x015
            case 0x016: return "Panorama";
            case 0x017: return "Night (Tripod)";
            case 0x018: return "Pro Low-light";
            case 0x019: return "Pro Focus";
            // skip 0x01a
            case 0x01b: return "Dog Face Detection";
            case 0x01c: return "Cat Face Detection";
            case 0x100: return "Aperture priority AE";
            case 0x200: return "Shutter priority AE";
            case 0x300: return "Manual exposure";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getExrAutoDescription()
    {
        return getIndexedDescription(TAG_EXR_AUTO, "Auto", "Manual");
    }

    @Nullable
    public String getExrModeDescription()
    {
        final Integer value = _directory.getInteger(TAG_EXR_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 0x100: return "HR (High Resolution)";
            case 0x200: return "SN (Signal to Noise Priority)";
            case 0x300: return "DR (Dynamic Range Priority)";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getAutoBracketingDescription()
    {
        return getIndexedDescription(
            TAG_AUTO_BRACKETING,
            "Off",
            "On",
            "No Flash & Flash"
        );
    }

    @Nullable
    public String getFinePixColorDescription()
    {
        final Integer value = _directory.getInteger(TAG_FINE_PIX_COLOR);
        if (value == null)
            return null;
        switch (value) {
            case 0x00: return "Standard";
            case 0x10: return "Chrome";
            case 0x30: return "B&W";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getBlurWarningDescription()
    {
        return getIndexedDescription(
            TAG_BLUR_WARNING,
            "No Blur Warning",
            "Blur warning"
        );
    }

    @Nullable
    public String getFocusWarningDescription()
    {
        return getIndexedDescription(
            TAG_FOCUS_WARNING,
            "Good Focus",
            "Out Of Focus"
        );
    }

    @Nullable
    public String getAutoExposureWarningDescription()
    {
        return getIndexedDescription(
            TAG_AUTO_EXPOSURE_WARNING,
            "AE Good",
            "Over Exposed"
        );
    }

    @Nullable
    public String getDynamicRangeDescription()
    {
        return getIndexedDescription(
            TAG_DYNAMIC_RANGE,
            1,
            "Standard",
            null,
            "Wide"
        );
    }

    @Nullable
    public String getFilmModeDescription()
    {
        final Integer value = _directory.getInteger(TAG_FILM_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 0x000: return "F0/Standard (Provia) ";
            case 0x100: return "F1/Studio Portrait";
            case 0x110: return "F1a/Studio Portrait Enhanced Saturation";
            case 0x120: return "F1b/Studio Portrait Smooth Skin Tone (Astia)";
            case 0x130: return "F1c/Studio Portrait Increased Sharpness";
            case 0x200: return "F2/Fujichrome (Velvia)";
            case 0x300: return "F3/Studio Portrait Ex";
            case 0x400: return "F4/Velvia";
            case 0x500: return "Pro Neg. Std";
            case 0x501: return "Pro Neg. Hi";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getDynamicRangeSettingDescription()
    {
        final Integer value = _directory.getInteger(TAG_DYNAMIC_RANGE_SETTING);
        if (value == null)
            return null;
        switch (value) {
            case 0x000: return "Auto (100-400%)";
            case 0x001: return "Manual";
            case 0x100: return "Standard (100%)";
            case 0x200: return "Wide 1 (230%)";
            case 0x201: return "Wide 2 (400%)";
            case 0x8000: return "Film Simulation";
            default:
                return "Unknown (" + value + ")";
        }
    }
}

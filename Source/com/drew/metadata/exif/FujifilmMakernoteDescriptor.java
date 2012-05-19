/*
 * Copyright 2002-2012 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.metadata.exif;

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string representations of tag values stored in a <code>FujifilmMakernoteDirectory</code>.
 * <p/>
 * Fujifilm's digicam added the MakerNote tag from the Year2000's model (e.g.Finepix1400,
 * Finepix4700). It uses IFD format and start from ASCII character 'FUJIFILM', and next 4
 * bytes(value 0x000c) points the offset to first IFD entry. Example of actual data
 * structure is shown below.
 * <p/>
 * <pre><code>
 * :0000: 46 55 4A 49 46 49 4C 4D-0C 00 00 00 0F 00 00 00 :0000: FUJIFILM........
 * :0010: 07 00 04 00 00 00 30 31-33 30 00 10 02 00 08 00 :0010: ......0130......
 * </code></pre>
 * <p/>
 * There are two big differences to the other manufacturers.
 * - Fujifilm's Exif data uses Motorola align, but MakerNote ignores it and uses Intel
 *   align.
 * - The other manufacturer's MakerNote counts the "offset to data" from the first byte
 *   of TIFF header (same as the other IFD), but Fujifilm counts it from the first byte
 *   of MakerNote itself.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class FujifilmMakernoteDescriptor extends TagDescriptor<FujifilmMakernoteDirectory>
{
    public FujifilmMakernoteDescriptor(@NotNull FujifilmMakernoteDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case FujifilmMakernoteDirectory.TAG_FUJIFILM_SHARPNESS:
                return getSharpnessDescription();
            case FujifilmMakernoteDirectory.TAG_FUJIFILM_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case FujifilmMakernoteDirectory.TAG_FUJIFILM_COLOR_SATURATION:
                return getColorDescription();
            case FujifilmMakernoteDirectory.TAG_FUJIFILM_TONE:
                return getToneDescription();
            case FujifilmMakernoteDirectory.TAG_FUJIFILM_FLASH_MODE:
                return getFlashModeDescription();
            case FujifilmMakernoteDirectory.TAG_FUJIFILM_FLASH_STRENGTH:
                return getFlashStrengthDescription();
            case FujifilmMakernoteDirectory.TAG_FUJIFILM_MACRO:
                return getMacroDescription();
            case FujifilmMakernoteDirectory.TAG_FUJIFILM_FOCUS_MODE:
                return getFocusModeDescription();
            case FujifilmMakernoteDirectory.TAG_FUJIFILM_SLOW_SYNCH:
                return getSlowSyncDescription();
            case FujifilmMakernoteDirectory.TAG_FUJIFILM_PICTURE_MODE:
                return getPictureModeDescription();
            case FujifilmMakernoteDirectory.TAG_FUJIFILM_CONTINUOUS_TAKING_OR_AUTO_BRACKETTING:
                return getContinuousTakingOrAutoBrackettingDescription();
            case FujifilmMakernoteDirectory.TAG_FUJIFILM_BLUR_WARNING:
                return getBlurWarningDescription();
            case FujifilmMakernoteDirectory.TAG_FUJIFILM_FOCUS_WARNING:
                return getFocusWarningDescription();
            case FujifilmMakernoteDirectory.TAG_FUJIFILM_AE_WARNING:
                return getAutoExposureWarningDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getSharpnessDescription()
    {
        final Integer value = _directory.getInteger(FujifilmMakernoteDirectory.TAG_FUJIFILM_SHARPNESS);
        if (value==null)
            return null;
        switch (value) {
            case 1:
                return "Softest";
            case 2:
                return "Soft";
            case 3:
                return "Normal";
            case 4:
                return "Hard";
            case 5:
                return "Hardest";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        final Integer value = _directory.getInteger(FujifilmMakernoteDirectory.TAG_FUJIFILM_WHITE_BALANCE);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Auto";
            case 256:
                return "Daylight";
            case 512:
                return "Cloudy";
            case 768:
                return "DaylightColor-fluorescence";
            case 769:
                return "DaywhiteColor-fluorescence";
            case 770:
                return "White-fluorescence";
            case 1024:
                return "Incandescence";
            case 3840:
                return "Custom white balance";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getColorDescription()
    {
        final Integer value = _directory.getInteger(FujifilmMakernoteDirectory.TAG_FUJIFILM_COLOR_SATURATION);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Normal (STD)";
            case 256:
                return "High (HARD)";
            case 512:
                return "Low (ORG)";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getToneDescription()
    {
        final Integer value = _directory.getInteger(FujifilmMakernoteDirectory.TAG_FUJIFILM_TONE);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Normal (STD)";
            case 256:
                return "High (HARD)";
            case 512:
                return "Low (ORG)";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFlashModeDescription()
    {
        final Integer value = _directory.getInteger(FujifilmMakernoteDirectory.TAG_FUJIFILM_FLASH_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Auto";
            case 1:
                return "On";
            case 2:
                return "Off";
            case 3:
                return "Red-eye reduction";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFlashStrengthDescription()
    {
        Rational value = _directory.getRational(FujifilmMakernoteDirectory.TAG_FUJIFILM_FLASH_STRENGTH);
        if (value==null)
            return null;
        return value.toSimpleString(false) + " EV (Apex)";
    }

    @Nullable
    public String getMacroDescription()
    {
        return getOnOffDescription(FujifilmMakernoteDirectory.TAG_FUJIFILM_MACRO);
    }

    @Nullable
    public String getFocusModeDescription()
    {
        final Integer value = _directory.getInteger(FujifilmMakernoteDirectory.TAG_FUJIFILM_FOCUS_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Auto focus";
            case 1:
                return "Manual focus";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSlowSyncDescription()
    {
        return getOnOffDescription(FujifilmMakernoteDirectory.TAG_FUJIFILM_SLOW_SYNCH);
    }

    @Nullable
    public String getPictureModeDescription()
    {
        final Integer value = _directory.getInteger(FujifilmMakernoteDirectory.TAG_FUJIFILM_PICTURE_MODE);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Auto";
            case 1:
                return "Portrait scene";
            case 2:
                return "Landscape scene";
            case 4:
                return "Sports scene";
            case 5:
                return "Night scene";
            case 6:
                return "Program AE";
            case 256:
                return "Aperture priority AE";
            case 512:
                return "Shutter priority AE";
            case 768:
                return "Manual exposure";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getContinuousTakingOrAutoBrackettingDescription()
    {
        return getOnOffDescription(FujifilmMakernoteDirectory.TAG_FUJIFILM_CONTINUOUS_TAKING_OR_AUTO_BRACKETTING);
    }

    @Nullable
    public String getBlurWarningDescription()
    {
        final Integer value = _directory.getInteger(FujifilmMakernoteDirectory.TAG_FUJIFILM_BLUR_WARNING);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "No blur warning";
            case 1:
                return "Blur warning";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFocusWarningDescription()
    {
        final Integer value = _directory.getInteger(FujifilmMakernoteDirectory.TAG_FUJIFILM_FOCUS_WARNING);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Auto focus good";
            case 1:
                return "Out of focus";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getAutoExposureWarningDescription()
    {
        final Integer value = _directory.getInteger(FujifilmMakernoteDirectory.TAG_FUJIFILM_AE_WARNING);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "AE good";
            case 1:
                return "Over exposed (>1/1000s @ F11)";
            default:
                return "Unknown (" + value + ")";
        }
    }


    @Nullable
    private String getOnOffDescription(final int tagType)
    {
        final Integer value = _directory.getInteger(tagType);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Off";
            case 1:
                return "On";
            default:
                return "Unknown (" + value + ")";
        }
    }
}

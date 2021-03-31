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
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.makernotes.SanyoMakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link com.drew.metadata.exif.makernotes.SonyType6MakernoteDirectory}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class SanyoMakernoteDescriptor extends TagDescriptor<SanyoMakernoteDirectory>
{
    public SanyoMakernoteDescriptor(@NotNull SanyoMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_SANYO_QUALITY:
                return getSanyoQualityDescription();
            case TAG_MACRO:
                return getMacroDescription();
            case TAG_DIGITAL_ZOOM:
                return getDigitalZoomDescription();
            case TAG_SEQUENTIAL_SHOT:
                return getSequentialShotDescription();
            case TAG_WIDE_RANGE:
                return getWideRangeDescription();
            case TAG_COLOR_ADJUSTMENT_MODE:
                return getColorAdjustmentModeDescription();
            case TAG_QUICK_SHOT:
                return getQuickShotDescription();
            case TAG_SELF_TIMER:
                return getSelfTimerDescription();
            case TAG_VOICE_MEMO:
                return getVoiceMemoDescription();
            case TAG_RECORD_SHUTTER_RELEASE:
                return getRecordShutterDescription();
            case TAG_FLICKER_REDUCE:
                return getFlickerReduceDescription();
            case TAG_OPTICAL_ZOOM_ON:
                return getOptimalZoomOnDescription();
            case TAG_DIGITAL_ZOOM_ON:
                return getDigitalZoomOnDescription();
            case TAG_LIGHT_SOURCE_SPECIAL:
                return getLightSourceSpecialDescription();
            case TAG_RESAVED:
                return getResavedDescription();
            case TAG_SCENE_SELECT:
                return getSceneSelectDescription();
            case TAG_SEQUENCE_SHOT_INTERVAL:
                return getSequenceShotIntervalDescription();
            case TAG_FLASH_MODE:
                return getFlashModeDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getSanyoQualityDescription()
    {
        Integer value = _directory.getInteger(TAG_SANYO_QUALITY);
        if (value == null)
            return null;
        switch (value) {
            case 0x0: return "Normal/Very Low";
            case 0x1: return "Normal/Low";
            case 0x2: return "Normal/Medium Low";
            case 0x3: return "Normal/Medium";
            case 0x4: return "Normal/Medium High";
            case 0x5: return "Normal/High";
            case 0x6: return "Normal/Very High";
            case 0x7: return "Normal/Super High";
            case 0x100: return "Fine/Very Low";
            case 0x101: return "Fine/Low";
            case 0x102: return "Fine/Medium Low";
            case 0x103: return "Fine/Medium";
            case 0x104: return "Fine/Medium High";
            case 0x105: return "Fine/High";
            case 0x106: return "Fine/Very High";
            case 0x107: return "Fine/Super High";
            case 0x200: return "Super Fine/Very Low";
            case 0x201: return "Super Fine/Low";
            case 0x202: return "Super Fine/Medium Low";
            case 0x203: return "Super Fine/Medium";
            case 0x204: return "Super Fine/Medium High";
            case 0x205: return "Super Fine/High";
            case 0x206: return "Super Fine/Very High";
            case 0x207: return "Super Fine/Super High";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    private String getMacroDescription()
    {
        return getIndexedDescription(TAG_MACRO, "Normal", "Macro", "View", "Manual");
    }

    @Nullable
    private String getDigitalZoomDescription()
    {
        return getDecimalRational(TAG_DIGITAL_ZOOM, 3);
    }

    @Nullable
    private String getSequentialShotDescription()
    {
        return getIndexedDescription(TAG_SEQUENTIAL_SHOT, "None", "Standard", "Best", "Adjust Exposure");
    }

    @Nullable
    private String getWideRangeDescription()
    {
        return getIndexedDescription(TAG_WIDE_RANGE, "Off", "On");
    }

    @Nullable
    private String getColorAdjustmentModeDescription()
    {
        return getIndexedDescription(TAG_COLOR_ADJUSTMENT_MODE, "Off", "On");
    }

    @Nullable
    private String getQuickShotDescription()
    {
        return getIndexedDescription(TAG_QUICK_SHOT, "Off", "On");
    }

    @Nullable
    private String getSelfTimerDescription()
    {
        return getIndexedDescription(TAG_SELF_TIMER, "Off", "On");
    }

    @Nullable
    private String getVoiceMemoDescription()
    {
        return getIndexedDescription(TAG_VOICE_MEMO, "Off", "On");
    }

    @Nullable
    private String getRecordShutterDescription()
    {
        return getIndexedDescription(TAG_RECORD_SHUTTER_RELEASE, "Record while down", "Press start, press stop");
    }

    @Nullable
    private String getFlickerReduceDescription()
    {
        return getIndexedDescription(TAG_FLICKER_REDUCE, "Off", "On");
    }

    @Nullable
    private String getOptimalZoomOnDescription()
    {
        return getIndexedDescription(TAG_OPTICAL_ZOOM_ON, "Off", "On");
    }

    @Nullable
    private String getDigitalZoomOnDescription()
    {
        return getIndexedDescription(TAG_DIGITAL_ZOOM_ON, "Off", "On");
    }

    @Nullable
    private String getLightSourceSpecialDescription()
    {
        return getIndexedDescription(TAG_LIGHT_SOURCE_SPECIAL, "Off", "On");
    }

    @Nullable
    private String getResavedDescription()
    {
        return getIndexedDescription(TAG_RESAVED, "No", "Yes");
    }

    @Nullable
    private String getSceneSelectDescription()
    {
        return getIndexedDescription(TAG_SCENE_SELECT,
            "Off", "Sport", "TV", "Night", "User 1", "User 2", "Lamp");
    }

    @Nullable
    private String getSequenceShotIntervalDescription()
    {
        return getIndexedDescription(TAG_SEQUENCE_SHOT_INTERVAL,
            "5 frames/sec", "10 frames/sec", "15 frames/sec", "20 frames/sec");
    }

    @Nullable
    private String getFlashModeDescription()
    {
        return getIndexedDescription(TAG_FLASH_MODE,
            "Auto", "Force", "Disabled", "Red eye");
    }
}

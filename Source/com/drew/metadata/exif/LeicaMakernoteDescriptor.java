/*
 * Copyright 2002-2013 Drew Noakes
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

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string representations of tag values stored in a {@link LeicaMakernoteDirectory}.
 * <p/>
 * Tag reference from: http://gvsoft.homedns.org/exif/makernote-leica-type1.html
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class LeicaMakernoteDescriptor extends TagDescriptor<LeicaMakernoteDirectory>
{
    public LeicaMakernoteDescriptor(@NotNull LeicaMakernoteDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case LeicaMakernoteDirectory.TAG_QUALITY:
                return getQualityDescription();
            case LeicaMakernoteDirectory.TAG_USER_PROFILE:
                return getUserProfileDescription();
//            case LeicaMakernoteDirectory.TAG_SERIAL:
//                return getSerialNumberDescription();
            case LeicaMakernoteDirectory.TAG_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case LeicaMakernoteDirectory.TAG_EXTERNAL_SENSOR_BRIGHTNESS_VALUE:
                return getExternalSensorBrightnessValueDescription();
            case LeicaMakernoteDirectory.TAG_MEASURED_LV:
                return getMeasuredLvDescription();
            case LeicaMakernoteDirectory.TAG_APPROXIMATE_F_NUMBER:
                return getApproximateFNumberDescription();
            case LeicaMakernoteDirectory.TAG_CAMERA_TEMPERATURE:
                return getCameraTemperatureDescription();
            case LeicaMakernoteDirectory.TAG_WB_RED_LEVEL:
            case LeicaMakernoteDirectory.TAG_WB_BLUE_LEVEL:
            case LeicaMakernoteDirectory.TAG_WB_GREEN_LEVEL:
                return getSimplifiedRational(tagType);
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    private String getCameraTemperatureDescription()
    {
        return getFormattedInt(LeicaMakernoteDirectory.TAG_CAMERA_TEMPERATURE, "%d C");
    }

    @Nullable
    private String getApproximateFNumberDescription()
    {
        return getSimplifiedRational(LeicaMakernoteDirectory.TAG_APPROXIMATE_F_NUMBER);
    }

    @Nullable
    private String getMeasuredLvDescription()
    {
        return getSimplifiedRational(LeicaMakernoteDirectory.TAG_MEASURED_LV);
    }

    @Nullable
    private String getExternalSensorBrightnessValueDescription()
    {
        return getSimplifiedRational(LeicaMakernoteDirectory.TAG_EXTERNAL_SENSOR_BRIGHTNESS_VALUE);
    }

    @Nullable
    private String getWhiteBalanceDescription()
    {
        return getIndexedDescription(LeicaMakernoteDirectory.TAG_WHITE_BALANCE,
            "Auto or Manual",
            "Daylight",
            "Fluorescent",
            "Tungsten",
            "Flash",
            "Cloudy",
            "Shadow"
        );
    }

    @Nullable
    private String getUserProfileDescription()
    {
        return getIndexedDescription(LeicaMakernoteDirectory.TAG_QUALITY, 1,
            "User Profile 1",
            "User Profile 2",
            "User Profile 3",
            "User Profile 0 (Dynamic)"
        );
    }

    @Nullable
    private String getQualityDescription()
    {
        return getIndexedDescription(LeicaMakernoteDirectory.TAG_QUALITY, 1, "Fine", "Basic");
    }
}

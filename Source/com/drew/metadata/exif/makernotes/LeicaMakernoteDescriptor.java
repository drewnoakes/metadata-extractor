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
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.makernotes.LeicaMakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link LeicaMakernoteDirectory}.
 * <p>
 * Tag reference from: http://gvsoft.homedns.org/exif/makernote-leica-type1.html
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class LeicaMakernoteDescriptor extends TagDescriptor<LeicaMakernoteDirectory>
{
    public LeicaMakernoteDescriptor(@NotNull LeicaMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_QUALITY:
                return getQualityDescription();
            case TAG_USER_PROFILE:
                return getUserProfileDescription();
//            case TAG_SERIAL:
//                return getSerialNumberDescription();
            case TAG_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case TAG_EXTERNAL_SENSOR_BRIGHTNESS_VALUE:
                return getExternalSensorBrightnessValueDescription();
            case TAG_MEASURED_LV:
                return getMeasuredLvDescription();
            case TAG_APPROXIMATE_F_NUMBER:
                return getApproximateFNumberDescription();
            case TAG_CAMERA_TEMPERATURE:
                return getCameraTemperatureDescription();
            case TAG_WB_RED_LEVEL:
            case TAG_WB_BLUE_LEVEL:
            case TAG_WB_GREEN_LEVEL:
                return getSimpleRational(tagType);
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    private String getCameraTemperatureDescription()
    {
        return getFormattedInt(TAG_CAMERA_TEMPERATURE, "%d C");
    }

    @Nullable
    private String getApproximateFNumberDescription()
    {
        return getSimpleRational(TAG_APPROXIMATE_F_NUMBER);
    }

    @Nullable
    private String getMeasuredLvDescription()
    {
        return getSimpleRational(TAG_MEASURED_LV);
    }

    @Nullable
    private String getExternalSensorBrightnessValueDescription()
    {
        return getSimpleRational(TAG_EXTERNAL_SENSOR_BRIGHTNESS_VALUE);
    }

    @Nullable
    private String getWhiteBalanceDescription()
    {
        return getIndexedDescription(TAG_WHITE_BALANCE,
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
        return getIndexedDescription(TAG_QUALITY, 1,
            "User Profile 1",
            "User Profile 2",
            "User Profile 3",
            "User Profile 0 (Dynamic)"
        );
    }

    @Nullable
    private String getQualityDescription()
    {
        return getIndexedDescription(TAG_QUALITY, 1, "Fine", "Basic");
    }
}

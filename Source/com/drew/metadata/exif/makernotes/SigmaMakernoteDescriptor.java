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

import static com.drew.metadata.exif.makernotes.SigmaMakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link SigmaMakernoteDirectory}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class SigmaMakernoteDescriptor extends TagDescriptor<SigmaMakernoteDirectory>
{
    public SigmaMakernoteDescriptor(@NotNull SigmaMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_EXPOSURE_MODE:
                return getExposureModeDescription();
            case TAG_METERING_MODE:
                return getMeteringModeDescription();
        }
        return super.getDescription(tagType);
    }

    @Nullable
    private String getMeteringModeDescription()
    {
        String value = _directory.getString(TAG_METERING_MODE);
        if (value == null || value.length() == 0)
            return null;
        switch (value.charAt(0)) {
            case '8': return "Multi Segment";
            case 'A': return "Average";
            case 'C': return "Center Weighted Average";
            default:
                return value;
        }
    }

    @Nullable
    private String getExposureModeDescription()
    {
        String value = _directory.getString(TAG_EXPOSURE_MODE);
        if (value == null || value.length() == 0)
            return null;
        switch (value.charAt(0)) {
            case 'A': return "Aperture Priority AE";
            case 'M': return "Manual";
            case 'P': return "Program AE";
            case 'S': return "Shutter Speed Priority AE";
            default:
                return value;
        }
    }
}

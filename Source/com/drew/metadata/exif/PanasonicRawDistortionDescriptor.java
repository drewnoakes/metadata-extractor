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

package com.drew.metadata.exif;

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.PanasonicRawDistortionDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link PanasonicRawDistortionDirectory}.
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class PanasonicRawDistortionDescriptor extends TagDescriptor<PanasonicRawDistortionDirectory>
{
    public PanasonicRawDistortionDescriptor(@NotNull PanasonicRawDistortionDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TagDistortionParam02:
                return getDistortionParam02Description();
            case TagDistortionParam04:
                return getDistortionParam04Description();
            case TagDistortionScale:
                return getDistortionScaleDescription();
            case TagDistortionCorrection:
                return getDistortionCorrectionDescription();
            case TagDistortionParam08:
                return getDistortionParam08Description();
            case TagDistortionParam09:
                return getDistortionParam09Description();
            case TagDistortionParam11:
                return getDistortionParam11Description();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getWbTypeDescription(int tagType)
    {
        Integer wbtype = _directory.getInteger(tagType);
        if (wbtype == null)
            return null;

        return super.getLightSourceDescription(wbtype.shortValue());
    }

    @Nullable
    public String getDistortionParam02Description()
    {
        Integer value = _directory.getInteger(TagDistortionParam02);
        if (value == null)
            return null;

        return new Rational(value, 32678).toString();
    }

    @Nullable
    public String getDistortionParam04Description()
    {
        Integer value = _directory.getInteger(TagDistortionParam04);
        if (value == null)
            return null;

        return new Rational(value, 32678).toString();
    }

    @Nullable
    public String getDistortionScaleDescription()
    {
        Integer value = _directory.getInteger(TagDistortionScale);
        if (value == null)
            return null;

        //return (1 / (1 + value / 32768)).toString();
        return Integer.toString(1 / (1 + value / 32768));
    }

    @Nullable
    public String getDistortionCorrectionDescription()
    {
        Integer value = _directory.getInteger(TagDistortionCorrection);
        if (value == null)
            return null;

        // (have seen the upper 4 bits set for GF5 and GX1, giving a value of -4095 - PH)
        int mask = 0x000f;
        switch (value & mask)
        {
            case 0:
                return "Off";
            case 1:
                return "On";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getDistortionParam08Description()
    {
        Integer value = _directory.getInteger(TagDistortionParam08);
        if (value == null)
            return null;

        return new Rational(value, 32678).toString();
    }

    @Nullable
    public String getDistortionParam09Description()
    {
        Integer value = _directory.getInteger(TagDistortionParam09);
        if (value == null)
            return null;

        return new Rational(value, 32678).toString();
    }

    @Nullable
    public String getDistortionParam11Description()
    {
        Integer value = _directory.getInteger(TagDistortionParam11);
        if (value == null)
            return null;

        return new Rational(value, 32678).toString();
    }
}

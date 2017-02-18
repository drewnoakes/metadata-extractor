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

import static com.drew.metadata.exif.makernotes.OlympusFocusInfoMakernoteDirectory.*;

/**
 * Provides human-readable String representations of tag values stored in a {@link OlympusFocusInfoMakernoteDirectory}.
 * <p>
 * Some Description functions converted from Exiftool version 10.10 created by Phil Harvey
 * http://www.sno.phy.queensu.ca/~phil/exiftool/
 * lib\Image\ExifTool\Olympus.pm
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class OlympusFocusInfoMakernoteDescriptor extends TagDescriptor<OlympusFocusInfoMakernoteDirectory>
{
    public OlympusFocusInfoMakernoteDescriptor(@NotNull OlympusFocusInfoMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TagFocusInfoVersion:
                return getFocusInfoVersionDescription();
            case TagAutoFocus:
                return getAutoFocusDescription();
            case TagFocusDistance:
                return getFocusDistanceDescription();
            case TagAfPoint:
                return getAfPointDescription();
            case TagExternalFlash:
                return getExternalFlashDescription();
            case TagExternalFlashBounce:
                return getExternalFlashBounceDescription();
            case TagExternalFlashZoom:
                return getExternalFlashZoomDescription();
            case TagManualFlash:
                return getManualFlashDescription();
            case TagMacroLed:
                return getMacroLedDescription();
            case TagSensorTemperature:
                return getSensorTemperatureDescription();
            case TagImageStabilization:
                return getImageStabilizationDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getFocusInfoVersionDescription()
    {
        return getVersionBytesDescription(TagFocusInfoVersion, 4);
    }

    @Nullable
    public String getAutoFocusDescription()
    {
        return getIndexedDescription(TagAutoFocus,
            "Off", "On");
    }

    @Nullable
    public String getFocusDistanceDescription()
    {
        Rational value = _directory.getRational(TagFocusDistance);
        if (value == null)
            return "inf";
        if (value.getNumerator() == 0xFFFFFFFFL || value.getNumerator() == 0x00000000L)
            return "inf";

        return value.getNumerator() / 1000.0 + " m";
    }

    @Nullable
    public String getAfPointDescription()
    {
        Integer value = _directory.getInteger(TagAfPoint);
        if (value == null)
            return null;

        return value.toString();
    }

    @Nullable
    public String getExternalFlashDescription()
    {
        int[] values = _directory.getIntArray(TagExternalFlash);
        if (values == null || values.length < 2)
            return null;

        String join = String.format("%d %d", (short)values[0], (short)values[1]);

        if(join.equals("0 0"))
            return "Off";
        else if(join.equals("1 0"))
            return "On";
        else
            return "Unknown (" + join + ")";
    }

    @Nullable
    public String getExternalFlashBounceDescription()
    {
        return getIndexedDescription(TagExternalFlashBounce,
                "Bounce or Off", "Direct");
    }

    @Nullable
    public String getExternalFlashZoomDescription()
    {
        int[] values = _directory.getIntArray(TagExternalFlashZoom);
        if (values == null)
        {
            // check if it's only one value long also
            Integer value = _directory.getInteger(TagExternalFlashZoom);
            if(value == null)
                return null;

            values = new int[1];
            values[0] = value;
        }

        if (values.length == 0)
            return null;

        String join = String.format("%d", (short)values[0]);
        if(values.length > 1)
            join += " " + String.format("%d", (short)values[1]);

        if(join.equals("0"))
            return "Off";
        else if(join.equals("1"))
            return "On";
        else if(join.equals("0 0"))
            return "Off";
        else if(join.equals("1 0"))
            return "On";
        else
            return "Unknown (" + join + ")";

    }

    @Nullable
    public String getManualFlashDescription()
    {
        int[] values = _directory.getIntArray(TagManualFlash);
        if (values == null)
            return null;

        if ((short)values[0] == 0)
            return "Off";

        if ((short)values[1] == 1)
            return "Full";
        return "On (1/" + (short)values[1] + " strength)";
    }

    @Nullable
    public String getMacroLedDescription()
    {
        return getIndexedDescription(TagMacroLed,
                "Off", "On");
    }

    /// <remarks>
    /// <para>TODO: Complete when Camera Model is available.</para>
    /// <para>There are differences in how to interpret this tag that can only be reconciled by knowing the model.</para>
    /// </remarks>
    @Nullable
    public String getSensorTemperatureDescription()
    {
        return _directory.getString(TagSensorTemperature);
    }

    @Nullable
    public String getImageStabilizationDescription()
    {
        byte[] values = _directory.getByteArray(TagImageStabilization);
        if (values == null)
            return null;

        if((values[0] | values[1] | values[2] | values[3]) == 0x0)
            return "Off";
        return "On, " + ((values[43] & 1) > 0 ? "Mode 1" : "Mode 2");
    }
}

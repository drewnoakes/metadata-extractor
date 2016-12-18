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

import static com.drew.metadata.exif.makernotes.OlympusRawInfoMakernoteDirectory.*;

/**
 * Provides human-readable String representations of tag values stored in a {@link OlympusRawInfoMakernoteDirectory}.
 * <p>
 * Some Description functions converted from Exiftool version 10.33 created by Phil Harvey
 * http://www.sno.phy.queensu.ca/~phil/exiftool/
 * lib\Image\ExifTool\Olympus.pm
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class OlympusRawInfoMakernoteDescriptor extends TagDescriptor<OlympusRawInfoMakernoteDirectory>
{
    public OlympusRawInfoMakernoteDescriptor(@NotNull OlympusRawInfoMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TagRawInfoVersion:
                return getVersionBytesDescription(TagRawInfoVersion, 4);
            case TagColorMatrix2:
                return getColorMatrix2Description();
            case TagYCbCrCoefficients:
                return getYCbCrCoefficientsDescription();
            case TagLightSource:
                return getOlympusLightSourceDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getColorMatrix2Description()
    {
        int[] values = _directory.getIntArray(TagColorMatrix2);
        if (values == null)
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append((short)values[i]);
            if (i < values.length - 1)
                sb.append(" ");
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    @Nullable
    public String getYCbCrCoefficientsDescription()
    {
        int[] values = _directory.getIntArray(TagYCbCrCoefficients);
        if (values == null)
            return null;

        Rational[] ret = new Rational[values.length / 2];
        for(int i = 0; i < values.length / 2; i++)
        {
            ret[i] = new Rational((short)values[2*i], (short)values[2*i + 1]);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ret.length; i++) {
            sb.append(ret[i].doubleValue());
            if (i < ret.length - 1)
                sb.append(" ");
        }
        return sb.length() == 0 ? null : sb.toString();
    }
    
    @Nullable
    public String getOlympusLightSourceDescription()
    {
        Integer value = _directory.getInteger(TagLightSource);
        if (value == null)
            return null;

        switch (value.shortValue())
        {
            case 0:
                return "Unknown";
            case 16:
                return "Shade";
            case 17:
                return "Cloudy";
            case 18:
                return "Fine Weather";
            case 20:
                return "Tungsten (Incandescent)";
            case 22:
                return "Evening Sunlight";
            case 33:
                return "Daylight Fluorescent";
            case 34:
                return "Day White Fluorescent";
            case 35:
                return "Cool White Fluorescent";
            case 36:
                return "White Fluorescent";
            case 256:
                return "One Touch White Balance";
            case 512:
                return "Custom 1-4";
            default:
                return "Unknown (" + value + ")";
        }
    }
}

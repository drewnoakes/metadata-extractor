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

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.makernotes.OlympusImageProcessingMakernoteDirectory.*;

/**
 * Provides human-readable String representations of tag values stored in a {@link OlympusImageProcessingMakernoteDirectory}.
 * <p>
 * Some Description functions converted from Exiftool version 10.33 created by Phil Harvey
 * http://www.sno.phy.queensu.ca/~phil/exiftool/
 * lib\Image\ExifTool\Olympus.pm
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class OlympusImageProcessingMakernoteDescriptor extends TagDescriptor<OlympusImageProcessingMakernoteDirectory>
{
    public OlympusImageProcessingMakernoteDescriptor(@NotNull OlympusImageProcessingMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TagImageProcessingVersion:
                return getImageProcessingVersionDescription();
            case TagColorMatrix:
                return getColorMatrixDescription();
            case TagNoiseReduction2:
                return getNoiseReduction2Description();
            case TagDistortionCorrection2:
                return getDistortionCorrection2Description();
            case TagShadingCompensation2:
                return getShadingCompensation2Description();
            case TagMultipleExposureMode:
                return getMultipleExposureModeDescription();
            case TagAspectRatio:
                return getAspectRatioDescription();
            case TagKeystoneCompensation:
                return getKeystoneCompensationDescription();
            case TagKeystoneDirection:
                return getKeystoneDirectionDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getImageProcessingVersionDescription()
    {
        return getVersionBytesDescription(TagImageProcessingVersion, 4);
    }

    @Nullable
    public String getColorMatrixDescription()
    {
        int[] obj = _directory.getIntArray(TagColorMatrix);
        if (obj == null)
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < obj.length; i++) {
            if (i != 0)
                sb.append(" ");
            sb.append((short)obj[i]);
        }
        return sb.toString();
    }

    @Nullable
    public String getNoiseReduction2Description()
    {
        Integer value = _directory.getInteger(TagNoiseReduction2);
        if (value == null)
            return null;

        if (value == 0)
            return "(none)";

        StringBuilder sb = new StringBuilder();
        short v = value.shortValue();

        if (( v       & 1) != 0) sb.append("Noise Reduction, ");
        if (((v >> 1) & 1) != 0) sb.append("Noise Filter, ");
        if (((v >> 2) & 1) != 0) sb.append("Noise Filter (ISO Boost), ");

        return sb.substring(0, sb.length() - 2);
    }

    @Nullable
    public String getDistortionCorrection2Description()
    {
        return getIndexedDescription(TagDistortionCorrection2, "Off", "On");
    }

    @Nullable
    public String getShadingCompensation2Description()
    {
        return getIndexedDescription(TagShadingCompensation2, "Off", "On");
    }

    @Nullable
    public String getMultipleExposureModeDescription()
    {
        int[] values = _directory.getIntArray(TagMultipleExposureMode);
        if (values == null)
        {
            // check if it's only one value long also
            Integer value = _directory.getInteger(TagMultipleExposureMode);
            if(value == null)
                return null;

            values = new int[1];
            values[0] = value;
        }

        if (values.length == 0)
            return null;

        StringBuilder sb = new StringBuilder();

        switch ((short)values[0])
        {
            case 0:
                sb.append("Off");
                break;
            case 2:
                sb.append("On (2 frames)");
                break;
            case 3:
                sb.append("On (3 frames)");
                break;
            default:
                sb.append("Unknown (").append((short)values[0]).append(")");
                break;
        }

        if (values.length > 1)
            sb.append("; ").append((short)values[1]);

        return sb.toString();
    }

    @Nullable
    public String getAspectRatioDescription()
    {
        byte[] values = _directory.getByteArray(TagAspectRatio);
        if (values == null || values.length < 2)
            return null;

        String join = String.format("%d %d", values[0], values[1]);

        String ret;
        if(join.equals("1 1"))
            ret = "4:3";
        else if(join.equals("1 4"))
            ret = "1:1";
        else if(join.equals("2 1"))
            ret = "3:2 (RAW)";
        else if(join.equals("2 2"))
            ret = "3:2";
        else if(join.equals("3 1"))
            ret = "16:9 (RAW)";
        else if(join.equals("3 3"))
            ret = "16:9";
        else if(join.equals("4 1"))
            ret = "1:1 (RAW)";
        else if(join.equals("4 4"))
            ret = "6:6";
        else if(join.equals("5 5"))
            ret = "5:4";
        else if(join.equals("6 6"))
            ret = "7:6";
        else if(join.equals("7 7"))
            ret = "6:5";
        else if(join.equals("8 8"))
            ret = "7:5";
        else if(join.equals("9 1"))
            ret = "3:4 (RAW)";
        else if(join.equals("9 9"))
            ret = "3:4";
        else
            ret = "Unknown (" + join + ")";

        return ret;
    }

    @Nullable
    public String getKeystoneCompensationDescription()
    {
        byte[] values = _directory.getByteArray(TagKeystoneCompensation);
        if (values == null || values.length < 2)
            return null;

        String join = String.format("%d %d", values[0], values[1]);

        String ret;
        if(join.equals("0 0"))
            ret = "Off";
        else if(join.equals("0 1"))
            ret = "On";
        else
            ret = "Unknown (" + join + ")";

        return ret;
    }

    @Nullable
    public String getKeystoneDirectionDescription()
    {
        return getIndexedDescription(TagKeystoneDirection, "Vertical", "Horizontal");
    }
}
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
package com.drew.metadata.xmp;

import com.drew.imaging.PhotographicConversions;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.text.DecimalFormat;

/**
 * Contains all logic for the presentation of xmp data, as stored in Xmp-Segment.  Use
 * this class to provide human-readable descriptions of tag values.
 *
 * @author Torsten Skadell, Drew Noakes https://drewnoakes.com
 */
public class XmpDescriptor extends TagDescriptor<XmpDirectory>
{
    // TODO some of these methods look similar to those found in Exif*Descriptor... extract common functionality from both

    @NotNull
    private static final java.text.DecimalFormat SimpleDecimalFormatter = new DecimalFormat("0.#");

    public XmpDescriptor(@NotNull XmpDirectory directory)
    {
        super(directory);
    }

    /** Do some simple formatting, dependant upon tagType */
    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case XmpDirectory.TAG_MAKE:
            case XmpDirectory.TAG_MODEL:
                return _directory.getString(tagType);
            case XmpDirectory.TAG_EXPOSURE_TIME:
                return getExposureTimeDescription();
            case XmpDirectory.TAG_EXPOSURE_PROGRAM:
                return getExposureProgramDescription();
            case XmpDirectory.TAG_SHUTTER_SPEED:
                return getShutterSpeedDescription();
            case XmpDirectory.TAG_F_NUMBER:
                return getFNumberDescription();
            case XmpDirectory.TAG_LENS:
            case XmpDirectory.TAG_LENS_INFO:
            case XmpDirectory.TAG_CAMERA_SERIAL_NUMBER:
            case XmpDirectory.TAG_FIRMWARE:
                return _directory.getString(tagType);
            case XmpDirectory.TAG_FOCAL_LENGTH:
                return getFocalLengthDescription();
            case XmpDirectory.TAG_APERTURE_VALUE:
                return getApertureValueDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    /** Do a simple formatting like ExifSubIFDDescriptor.java */
    @Nullable
    public String getExposureTimeDescription()
    {
        final String value = _directory.getString(XmpDirectory.TAG_EXPOSURE_TIME);
        if (value==null)
            return null;
        return value + " sec";
    }

    /** This code is from ExifSubIFDDescriptor.java */
    @Nullable
    public String getExposureProgramDescription()
    {
        // '1' means manual control, '2' program normal, '3' aperture priority,
        // '4' shutter priority, '5' program creative (slow program),
        // '6' program action(high-speed program), '7' portrait mode, '8' landscape mode.
        final Integer value = _directory.getInteger(XmpDirectory.TAG_EXPOSURE_PROGRAM);
        if (value==null)
            return null;
        switch (value) {
            case 1:
                return "Manual control";
            case 2:
                return "Program normal";
            case 3:
                return "Aperture priority";
            case 4:
                return "Shutter priority";
            case 5:
                return "Program creative (slow program)";
            case 6:
                return "Program action (high-speed program)";
            case 7:
                return "Portrait mode";
            case 8:
                return "Landscape mode";
            default:
                return "Unknown program (" + value + ")";
        }
    }


    /** This code is from ExifSubIFDDescriptor.java */
    @Nullable
    public String getShutterSpeedDescription()
    {
        final Float value = _directory.getFloatObject(XmpDirectory.TAG_SHUTTER_SPEED);
        if (value==null)
            return null;

        // thanks to Mark Edwards for spotting and patching a bug in the calculation of this
        // description (spotted bug using a Canon EOS 300D)
        // thanks also to Gli Blr for spotting this bug
        if (value <= 1) {
            float apexPower = (float) (1 / (Math.exp(value * Math.log(2))));
            long apexPower10 = Math.round((double) apexPower * 10.0);
            float fApexPower = (float) apexPower10 / 10.0f;
            return fApexPower + " sec";
        } else {
            int apexPower = (int) ((Math.exp(value * Math.log(2))));
            return "1/" + apexPower + " sec";
        }
    }

    /** Do a simple formatting like ExifSubIFDDescriptor.java */
    @Nullable
    public String getFNumberDescription()
    {
        final Rational value = _directory.getRational(XmpDirectory.TAG_F_NUMBER);
        if (value==null)
            return null;
        return "F" + SimpleDecimalFormatter.format(value.doubleValue());
    }

    /** This code is from ExifSubIFDDescriptor.java */
    @Nullable
    public String getFocalLengthDescription()
    {
        final Rational value = _directory.getRational(XmpDirectory.TAG_FOCAL_LENGTH);
        if (value==null)
            return null;
        java.text.DecimalFormat formatter = new DecimalFormat("0.0##");
        return formatter.format(value.doubleValue()) + " mm";
    }

    /** This code is from ExifSubIFDDescriptor.java */
    @Nullable
    public String getApertureValueDescription()
    {
        final Double value = _directory.getDoubleObject(XmpDirectory.TAG_APERTURE_VALUE);
        if (value==null)
            return null;
        double fStop = PhotographicConversions.apertureToFStop(value);
        return "F" + SimpleDecimalFormatter.format(fStop);
    }
}

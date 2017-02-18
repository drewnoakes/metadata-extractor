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
import com.drew.metadata.StringValue;
import com.drew.metadata.TagDescriptor;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.drew.metadata.exif.makernotes.ReconyxHyperFireMakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link ReconyxHyperFireMakernoteDirectory}.
 *
 * @author Todd West http://cascadescarnivoreproject.blogspot.com
 */
@SuppressWarnings("WeakerAccess")
public class ReconyxHyperFireMakernoteDescriptor extends TagDescriptor<ReconyxHyperFireMakernoteDirectory>
{
    public ReconyxHyperFireMakernoteDescriptor(@NotNull ReconyxHyperFireMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_MAKERNOTE_VERSION:
                return String.format("%d", _directory.getInteger(tagType));
            case TAG_FIRMWARE_VERSION:
                return _directory.getString(tagType);
            case TAG_TRIGGER_MODE:
                return _directory.getString(tagType);
            case TAG_SEQUENCE:
                int[] sequence = _directory.getIntArray(tagType);
                if (sequence == null)
                    return null;
                return String.format("%d/%d", sequence[0], sequence[1]);
            case TAG_EVENT_NUMBER:
                return String.format("%d", _directory.getInteger(tagType));
            case TAG_MOTION_SENSITIVITY:
                return String.format("%d", _directory.getInteger(tagType));
            case TAG_BATTERY_VOLTAGE:
                Double value = _directory.getDoubleObject(tagType);
                DecimalFormat formatter = new DecimalFormat("0.000");
                return value == null ? null : formatter.format(value);
            case TAG_DATE_TIME_ORIGINAL:
                String date = _directory.getString(tagType);
                try {
                    DateFormat parser = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                    return parser.format(parser.parse(date));
                } catch (ParseException e) {
                    return null;
                }
            case TAG_MOON_PHASE:
                return getIndexedDescription(tagType, "New", "Waxing Crescent", "First Quarter", "Waxing Gibbous", "Full", "Waning Gibbous", "Last Quarter", "Waning Crescent");
            case TAG_AMBIENT_TEMPERATURE_FAHRENHEIT:
            case TAG_AMBIENT_TEMPERATURE:
                return String.format("%d", _directory.getInteger(tagType));
            case TAG_SERIAL_NUMBER:
                // default is UTF_16LE
                StringValue svalue = _directory.getStringValue(tagType);
                if(svalue == null)
                    return null;
                return svalue.toString();
            case TAG_CONTRAST:
            case TAG_BRIGHTNESS:
            case TAG_SHARPNESS:
            case TAG_SATURATION:
                return String.format("%d", _directory.getInteger(tagType));
            case TAG_INFRARED_ILLUMINATOR:
                return getIndexedDescription(tagType, "Off", "On");
            case TAG_USER_LABEL:
                return _directory.getString(tagType);
            default:
                return super.getDescription(tagType);
        }
    }
}

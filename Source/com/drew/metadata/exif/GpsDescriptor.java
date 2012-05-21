/*
 * Copyright 2002-2012 Drew Noakes
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

import com.drew.lang.GeoLocation;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.text.DecimalFormat;

/**
 * Provides human-readable string representations of tag values stored in a <code>GpsDirectory</code>.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class GpsDescriptor extends TagDescriptor<GpsDirectory>
{
    public GpsDescriptor(@NotNull GpsDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case GpsDirectory.TAG_GPS_VERSION_ID:
                return getGpsVersionIdDescription();
            case GpsDirectory.TAG_GPS_ALTITUDE:
                return getGpsAltitudeDescription();
            case GpsDirectory.TAG_GPS_ALTITUDE_REF:
                return getGpsAltitudeRefDescription();
            case GpsDirectory.TAG_GPS_STATUS:
                return getGpsStatusDescription();
            case GpsDirectory.TAG_GPS_MEASURE_MODE:
                return getGpsMeasureModeDescription();
            case GpsDirectory.TAG_GPS_SPEED_REF:
                return getGpsSpeedRefDescription();
            case GpsDirectory.TAG_GPS_TRACK_REF:
            case GpsDirectory.TAG_GPS_IMG_DIRECTION_REF:
            case GpsDirectory.TAG_GPS_DEST_BEARING_REF:
                return getGpsDirectionReferenceDescription(tagType);
            case GpsDirectory.TAG_GPS_TRACK:
            case GpsDirectory.TAG_GPS_IMG_DIRECTION:
            case GpsDirectory.TAG_GPS_DEST_BEARING:
                return getGpsDirectionDescription(tagType);
            case GpsDirectory.TAG_GPS_DEST_DISTANCE_REF:
                return getGpsDestinationReferenceDescription();
            case GpsDirectory.TAG_GPS_TIME_STAMP:
                return getGpsTimeStampDescription();
            case GpsDirectory.TAG_GPS_LONGITUDE:
                // three rational numbers -- displayed in HH"MM"SS.ss
                return getGpsLongitudeDescription();
            case GpsDirectory.TAG_GPS_LATITUDE:
                // three rational numbers -- displayed in HH"MM"SS.ss
                return getGpsLatitudeDescription();
            case GpsDirectory.TAG_GPS_DIFFERENTIAL:
                return getGpsDifferentialDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    private String getGpsVersionIdDescription()
    {
        return convertBytesToVersionString(_directory.getIntArray(GpsDirectory.TAG_GPS_VERSION_ID), 1);
    }

    @Nullable
    public String getGpsLatitudeDescription()
    {
        GeoLocation location = _directory.getGeoLocation();

        if (location == null)
            return null;

        return GeoLocation.decimalToDegreesMinutesSecondsString(location.getLatitude());
    }

    @Nullable
    public String getGpsLongitudeDescription()
    {
        GeoLocation location = _directory.getGeoLocation();

        if (location == null)
            return null;

        return GeoLocation.decimalToDegreesMinutesSecondsString(location.getLongitude());
    }

    @Nullable
    public String getGpsTimeStampDescription()
    {
        // time in hour, min, sec
        int[] timeComponents = _directory.getIntArray(GpsDirectory.TAG_GPS_TIME_STAMP);
        if (timeComponents==null)
            return null;
        StringBuilder description = new StringBuilder();
        description.append(timeComponents[0]);
        description.append(":");
        description.append(timeComponents[1]);
        description.append(":");
        description.append(timeComponents[2]);
        description.append(" UTC");
        return description.toString();
    }

    @Nullable
    public String getGpsDestinationReferenceDescription()
    {
        final String value = _directory.getString(GpsDirectory.TAG_GPS_DEST_DISTANCE_REF);
        if (value==null)
            return null;
        String distanceRef = value.trim();
        if ("K".equalsIgnoreCase(distanceRef)) {
            return "kilometers";
        } else if ("M".equalsIgnoreCase(distanceRef)) {
            return "miles";
        } else if ("N".equalsIgnoreCase(distanceRef)) {
            return "knots";
        } else {
            return "Unknown (" + distanceRef + ")";
        }
    }

    @Nullable
    public String getGpsDirectionDescription(int tagType)
    {
        Rational angle = _directory.getRational(tagType);
        // provide a decimal version of rational numbers in the description, to avoid strings like "35334/199 degrees"
        String value = angle != null
                ? new DecimalFormat("0.##").format(angle.doubleValue())
                : _directory.getString(tagType);
        if (value==null || value.trim().length()==0)
            return null;
        return value.trim() + " degrees";
    }

    @Nullable
    public String getGpsDirectionReferenceDescription(int tagType)
    {
        final String value = _directory.getString(tagType);
        if (value==null)
            return null;
        String gpsDistRef = value.trim();
        if ("T".equalsIgnoreCase(gpsDistRef)) {
            return "True direction";
        } else if ("M".equalsIgnoreCase(gpsDistRef)) {
            return "Magnetic direction";
        } else {
            return "Unknown (" + gpsDistRef + ")";
        }
    }

    @Nullable
    public String getGpsSpeedRefDescription()
    {
        final String value = _directory.getString(GpsDirectory.TAG_GPS_SPEED_REF);
        if (value==null)
            return null;
        String gpsSpeedRef = value.trim();
        if ("K".equalsIgnoreCase(gpsSpeedRef)) {
            return "kph";
        } else if ("M".equalsIgnoreCase(gpsSpeedRef)) {
            return "mph";
        } else if ("N".equalsIgnoreCase(gpsSpeedRef)) {
            return "knots";
        } else {
            return "Unknown (" + gpsSpeedRef + ")";
        }
    }

    @Nullable
    public String getGpsMeasureModeDescription()
    {
        final String value = _directory.getString(GpsDirectory.TAG_GPS_MEASURE_MODE);
        if (value==null)
            return null;
        String gpsSpeedMeasureMode = value.trim();
        if ("2".equalsIgnoreCase(gpsSpeedMeasureMode)) {
            return "2-dimensional measurement";
        } else if ("3".equalsIgnoreCase(gpsSpeedMeasureMode)) {
            return "3-dimensional measurement";
        } else {
            return "Unknown (" + gpsSpeedMeasureMode + ")";
        }
    }

    @Nullable
    public String getGpsStatusDescription()
    {
        final String value = _directory.getString(GpsDirectory.TAG_GPS_STATUS);
        if (value==null)
            return null;
        String gpsStatus = value.trim();
        if ("A".equalsIgnoreCase(gpsStatus)) {
            return "Active (Measurement in progress)";
        } else if ("V".equalsIgnoreCase(gpsStatus)) {
            return "Void (Measurement Interoperability)";
        } else {
            return "Unknown (" + gpsStatus + ")";
        }
    }

    @Nullable
    public String getGpsAltitudeRefDescription()
    {
        Integer value = _directory.getInteger(GpsDirectory.TAG_GPS_ALTITUDE_REF);
        if (value==null)
            return null;
        if (value == 0)
            return "Sea level";
        if (value == 1)
            return "Below sea level";
        return "Unknown (" + value + ")";
    }

    @Nullable
    public String getGpsAltitudeDescription()
    {
        final Rational value = _directory.getRational(GpsDirectory.TAG_GPS_ALTITUDE);
        if (value==null)
            return null;
        return value.intValue() + " metres";
    }

    @Nullable
    public String getGpsDifferentialDescription()
    {
        final Integer value = _directory.getInteger(GpsDirectory.TAG_GPS_DIFFERENTIAL);
        if (value==null)
            return null;
        if (value == 0)
            return "No Correction";
        if (value == 1)
            return "Differential Corrected";
        return "Unknown (" + value + ")";
    }

    @Nullable
    public String getDegreesMinutesSecondsDescription()
    {
        GeoLocation location = _directory.getGeoLocation();

        if (location == null)
            return null;

        return location.toDMSString();
    }
}

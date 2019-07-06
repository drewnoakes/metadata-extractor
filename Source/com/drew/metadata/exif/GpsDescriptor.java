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
package com.drew.metadata.exif;

import com.drew.lang.GeoLocation;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.text.DecimalFormat;

import static com.drew.metadata.exif.GpsDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link GpsDirectory}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class GpsDescriptor extends TagDescriptor<GpsDirectory>
{
    public GpsDescriptor(@NotNull GpsDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_VERSION_ID:
                return getGpsVersionIdDescription();
            case TAG_ALTITUDE:
                return getGpsAltitudeDescription();
            case TAG_ALTITUDE_REF:
                return getGpsAltitudeRefDescription();
            case TAG_STATUS:
                return getGpsStatusDescription();
            case TAG_MEASURE_MODE:
                return getGpsMeasureModeDescription();
            case TAG_DOP:
                return getGpsDopDescription();
            case TAG_SPEED_REF:
                return getGpsSpeedRefDescription();
            case TAG_SPEED:
                return getGpsSpeedDescription();
            case TAG_TRACK_REF:
            case TAG_IMG_DIRECTION_REF:
            case TAG_DEST_BEARING_REF:
                return getGpsDirectionReferenceDescription(tagType);
            case TAG_TRACK:
            case TAG_IMG_DIRECTION:
            case TAG_DEST_BEARING:
                return getGpsDirectionDescription(tagType);
            case TAG_DEST_LATITUDE:
                return getGpsDestLatitudeDescription();
            case TAG_DEST_LONGITUDE:
                return getGpsDestLongitudeDescription();
            case TAG_DEST_DISTANCE_REF:
                return getGpsDestinationReferenceDescription();
            case TAG_DEST_DISTANCE:
                return getGpsDestDistanceDescription();
            case TAG_TIME_STAMP:
                return getGpsTimeStampDescription();
            case TAG_LONGITUDE:
                // three rational numbers -- displayed in HH"MM"SS.ss
                return getGpsLongitudeDescription();
            case TAG_LATITUDE:
                // three rational numbers -- displayed in HH"MM"SS.ss
                return getGpsLatitudeDescription();
            case TAG_PROCESSING_METHOD:
                return getGpsProcessingMethodDescription();
            case TAG_AREA_INFORMATION:
                return getGpsAreaInformationDescription();
            case TAG_DIFFERENTIAL:
                return getGpsDifferentialDescription();
            case TAG_H_POSITIONING_ERROR:
                return getGpsHPositioningErrorDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    private String getGpsVersionIdDescription()
    {
        return getVersionBytesDescription(TAG_VERSION_ID, 1);
    }

    @Nullable
    public String getGpsLatitudeDescription()
    {
        GeoLocation location = _directory.getGeoLocation();
        return location == null ? null : GeoLocation.decimalToDegreesMinutesSecondsString(location.getLatitude());
    }

    @Nullable
    public String getGpsLongitudeDescription()
    {
        GeoLocation location = _directory.getGeoLocation();
        return location == null ? null : GeoLocation.decimalToDegreesMinutesSecondsString(location.getLongitude());
    }

    @Nullable
    public String getGpsTimeStampDescription()
    {
        // time in hour, min, sec
        Rational[] timeComponents = _directory.getRationalArray(TAG_TIME_STAMP);
        DecimalFormat df = new DecimalFormat("00.000");
        return timeComponents == null
            ? null
            : String.format("%02d:%02d:%s UTC",
                timeComponents[0].intValue(),
                timeComponents[1].intValue(),
                df.format(timeComponents[2].doubleValue()));
    }

    @Nullable
    public String getGpsDestLatitudeDescription()
    {
        Rational[] latitudes = _directory.getRationalArray(TAG_DEST_LATITUDE);
        String latitudeRef = _directory.getString(TAG_DEST_LATITUDE_REF);

        if (latitudes == null || latitudes.length != 3 || latitudeRef == null)
            return null;

        Double lat = GeoLocation.degreesMinutesSecondsToDecimal(
            latitudes[0], latitudes[1], latitudes[2], latitudeRef.equalsIgnoreCase("S"));

        return lat == null ? null : GeoLocation.decimalToDegreesMinutesSecondsString(lat);
    }

    @Nullable
    public String getGpsDestLongitudeDescription()
    {
        Rational[] longitudes = _directory.getRationalArray(TAG_LONGITUDE);
        String longitudeRef = _directory.getString(TAG_LONGITUDE_REF);

        if (longitudes == null || longitudes.length != 3 || longitudeRef == null)
            return null;

        Double lon = GeoLocation.degreesMinutesSecondsToDecimal(
            longitudes[0], longitudes[1], longitudes[2], longitudeRef.equalsIgnoreCase("W"));

        return lon == null ? null : GeoLocation.decimalToDegreesMinutesSecondsString(lon);
    }

    @Nullable
    public String getGpsDestinationReferenceDescription()
    {
        final String value = _directory.getString(TAG_DEST_DISTANCE_REF);
        if (value == null)
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
    public String getGpsDestDistanceDescription()
    {
        final Rational value = _directory.getRational(TAG_DEST_DISTANCE);
        if (value == null)
            return null;
        final String unit = getGpsDestinationReferenceDescription();
        return String.format("%s %s",
            new DecimalFormat("0.##").format(value.doubleValue()),
            unit == null ? "unit" : unit.toLowerCase());
    }

    @Nullable
    public String getGpsDirectionDescription(int tagType)
    {
        Rational angle = _directory.getRational(tagType);
        // provide a decimal version of rational numbers in the description, to avoid strings like "35334/199 degrees"
        String value = angle != null
            ? new DecimalFormat("0.##").format(angle.doubleValue())
            : _directory.getString(tagType);
        return value == null || value.trim().length() == 0 ? null : value.trim() + " degrees";
    }

    @Nullable
    public String getGpsDirectionReferenceDescription(int tagType)
    {
        final String value = _directory.getString(tagType);
        if (value == null)
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
    public String getGpsDopDescription()
    {
        final Rational value = _directory.getRational(TAG_DOP);
        return value == null ? null : new DecimalFormat("0.##").format(value.doubleValue());
    }

    @Nullable
    public String getGpsSpeedRefDescription()
    {
        final String value = _directory.getString(TAG_SPEED_REF);
        if (value == null)
            return null;
        String gpsSpeedRef = value.trim();
        if ("K".equalsIgnoreCase(gpsSpeedRef)) {
            return "km/h";
        } else if ("M".equalsIgnoreCase(gpsSpeedRef)) {
            return "mph";
        } else if ("N".equalsIgnoreCase(gpsSpeedRef)) {
            return "knots";
        } else {
            return "Unknown (" + gpsSpeedRef + ")";
        }
    }

    @Nullable
    public String getGpsSpeedDescription()
    {
        final Rational value = _directory.getRational(TAG_SPEED);
        if (value == null)
            return null;
        final String unit = getGpsSpeedRefDescription();
        return String.format("%s %s",
            new DecimalFormat("0.##").format(value.doubleValue()),
            unit == null ? "unit" : unit.toLowerCase());
    }

    @Nullable
    public String getGpsMeasureModeDescription()
    {
        final String value = _directory.getString(TAG_MEASURE_MODE);
        if (value == null)
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
        final String value = _directory.getString(TAG_STATUS);
        if (value == null)
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
        return getIndexedDescription(TAG_ALTITUDE_REF, "Sea level", "Below sea level");
    }

    @Nullable
    public String getGpsAltitudeDescription()
    {
        final Rational value = _directory.getRational(TAG_ALTITUDE);
        return value == null ? null : new DecimalFormat("0.##").format(value.doubleValue()) + " metres";
    }

    @Nullable
    public String getGpsProcessingMethodDescription()
    {
        return getEncodedTextDescription(TAG_PROCESSING_METHOD);
    }

    @Nullable
    public String getGpsAreaInformationDescription()
    {
        return getEncodedTextDescription(TAG_AREA_INFORMATION);
    }

    @Nullable
    public String getGpsDifferentialDescription()
    {
        return getIndexedDescription(TAG_DIFFERENTIAL, "No Correction", "Differential Corrected");
    }

    @Nullable
    public String getGpsHPositioningErrorDescription()
    {
        final Rational value = _directory.getRational(TAG_H_POSITIONING_ERROR);
        return value == null ? null : new DecimalFormat("0.##").format(value.doubleValue()) + " metres";
    }

    @Nullable
    public String getDegreesMinutesSecondsDescription()
    {
        GeoLocation location = _directory.getGeoLocation();
        return location == null ? null : location.toDMSString();
    }
}

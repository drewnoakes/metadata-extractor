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

import com.drew.lang.GeoLocation;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Describes Exif tags that contain Global Positioning System (GPS) data.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class GpsDirectory extends ExifDirectoryBase
{
    /** GPS tag version GPSVersionID 0 0 BYTE 4 */
    public static final int TAG_VERSION_ID = 0x0000;
    /** North or South Latitude GPSLatitudeRef 1 1 ASCII 2 */
    public static final int TAG_LATITUDE_REF = 0x0001;
    /** Latitude GPSLatitude 2 2 RATIONAL 3 */
    public static final int TAG_LATITUDE = 0x0002;
    /** East or West Longitude GPSLongitudeRef 3 3 ASCII 2 */
    public static final int TAG_LONGITUDE_REF = 0x0003;
    /** Longitude GPSLongitude 4 4 RATIONAL 3 */
    public static final int TAG_LONGITUDE = 0x0004;
    /** Altitude reference GPSAltitudeRef 5 5 BYTE 1 */
    public static final int TAG_ALTITUDE_REF = 0x0005;
    /** Altitude GPSAltitude 6 6 RATIONAL 1 */
    public static final int TAG_ALTITUDE = 0x0006;
    /** GPS time (atomic clock) GPSTimeStamp 7 7 RATIONAL 3 */
    public static final int TAG_TIME_STAMP = 0x0007;
    /** GPS satellites used for measurement GPSSatellites 8 8 ASCII Any */
    public static final int TAG_SATELLITES = 0x0008;
    /** GPS receiver status GPSStatus 9 9 ASCII 2 */
    public static final int TAG_STATUS = 0x0009;
    /** GPS measurement mode GPSMeasureMode 10 A ASCII 2 */
    public static final int TAG_MEASURE_MODE = 0x000A;
    /** Measurement precision GPSDOP 11 B RATIONAL 1 */
    public static final int TAG_DOP = 0x000B;
    /** Speed unit GPSSpeedRef 12 C ASCII 2 */
    public static final int TAG_SPEED_REF = 0x000C;
    /** Speed of GPS receiver GPSSpeed 13 D RATIONAL 1 */
    public static final int TAG_SPEED = 0x000D;
    /** Reference for direction of movement GPSTrackRef 14 E ASCII 2 */
    public static final int TAG_TRACK_REF = 0x000E;
    /** Direction of movement GPSTrack 15 F RATIONAL 1 */
    public static final int TAG_TRACK = 0x000F;
    /** Reference for direction of image GPSImgDirectionRef 16 10 ASCII 2 */
    public static final int TAG_IMG_DIRECTION_REF = 0x0010;
    /** Direction of image GPSImgDirection 17 11 RATIONAL 1 */
    public static final int TAG_IMG_DIRECTION = 0x0011;
    /** Geodetic survey data used GPSMapDatum 18 12 ASCII Any */
    public static final int TAG_MAP_DATUM = 0x0012;
    /** Reference for latitude of destination GPSDestLatitudeRef 19 13 ASCII 2 */
    public static final int TAG_DEST_LATITUDE_REF = 0x0013;
    /** Latitude of destination GPSDestLatitude 20 14 RATIONAL 3 */
    public static final int TAG_DEST_LATITUDE = 0x0014;
    /** Reference for longitude of destination GPSDestLongitudeRef 21 15 ASCII 2 */
    public static final int TAG_DEST_LONGITUDE_REF = 0x0015;
    /** Longitude of destination GPSDestLongitude 22 16 RATIONAL 3 */
    public static final int TAG_DEST_LONGITUDE = 0x0016;
    /** Reference for bearing of destination GPSDestBearingRef 23 17 ASCII 2 */
    public static final int TAG_DEST_BEARING_REF = 0x0017;
    /** Bearing of destination GPSDestBearing 24 18 RATIONAL 1 */
    public static final int TAG_DEST_BEARING = 0x0018;
    /** Reference for distance to destination GPSDestDistanceRef 25 19 ASCII 2 */
    public static final int TAG_DEST_DISTANCE_REF = 0x0019;
    /** Distance to destination GPSDestDistance 26 1A RATIONAL 1 */
    public static final int TAG_DEST_DISTANCE = 0x001A;

    /** Values of "GPS", "CELLID", "WLAN" or "MANUAL" by the EXIF spec. */
    public static final int TAG_PROCESSING_METHOD = 0x001B;
    public static final int TAG_AREA_INFORMATION = 0x001C;
    public static final int TAG_DATE_STAMP = 0x001D;
    public static final int TAG_DIFFERENTIAL = 0x001E;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        addExifTagNames(_tagNameMap);

        _tagNameMap.put(TAG_VERSION_ID, "GPS Version ID");
        _tagNameMap.put(TAG_LATITUDE_REF, "GPS Latitude Ref");
        _tagNameMap.put(TAG_LATITUDE, "GPS Latitude");
        _tagNameMap.put(TAG_LONGITUDE_REF, "GPS Longitude Ref");
        _tagNameMap.put(TAG_LONGITUDE, "GPS Longitude");
        _tagNameMap.put(TAG_ALTITUDE_REF, "GPS Altitude Ref");
        _tagNameMap.put(TAG_ALTITUDE, "GPS Altitude");
        _tagNameMap.put(TAG_TIME_STAMP, "GPS Time-Stamp");
        _tagNameMap.put(TAG_SATELLITES, "GPS Satellites");
        _tagNameMap.put(TAG_STATUS, "GPS Status");
        _tagNameMap.put(TAG_MEASURE_MODE, "GPS Measure Mode");
        _tagNameMap.put(TAG_DOP, "GPS DOP");
        _tagNameMap.put(TAG_SPEED_REF, "GPS Speed Ref");
        _tagNameMap.put(TAG_SPEED, "GPS Speed");
        _tagNameMap.put(TAG_TRACK_REF, "GPS Track Ref");
        _tagNameMap.put(TAG_TRACK, "GPS Track");
        _tagNameMap.put(TAG_IMG_DIRECTION_REF, "GPS Img Direction Ref");
        _tagNameMap.put(TAG_IMG_DIRECTION, "GPS Img Direction");
        _tagNameMap.put(TAG_MAP_DATUM, "GPS Map Datum");
        _tagNameMap.put(TAG_DEST_LATITUDE_REF, "GPS Dest Latitude Ref");
        _tagNameMap.put(TAG_DEST_LATITUDE, "GPS Dest Latitude");
        _tagNameMap.put(TAG_DEST_LONGITUDE_REF, "GPS Dest Longitude Ref");
        _tagNameMap.put(TAG_DEST_LONGITUDE, "GPS Dest Longitude");
        _tagNameMap.put(TAG_DEST_BEARING_REF, "GPS Dest Bearing Ref");
        _tagNameMap.put(TAG_DEST_BEARING, "GPS Dest Bearing");
        _tagNameMap.put(TAG_DEST_DISTANCE_REF, "GPS Dest Distance Ref");
        _tagNameMap.put(TAG_DEST_DISTANCE, "GPS Dest Distance");
        _tagNameMap.put(TAG_PROCESSING_METHOD, "GPS Processing Method");
        _tagNameMap.put(TAG_AREA_INFORMATION, "GPS Area Information");
        _tagNameMap.put(TAG_DATE_STAMP, "GPS Date Stamp");
        _tagNameMap.put(TAG_DIFFERENTIAL, "GPS Differential");
    }

    public GpsDirectory()
    {
        this.setDescriptor(new GpsDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "GPS";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    /**
     * Parses various tags in an attempt to obtain a single object representing the latitude and longitude
     * at which this image was captured.
     *
     * @return The geographical location of this image, if possible, otherwise null
     */
    @Nullable
    public GeoLocation getGeoLocation()
    {
        Rational[] latitudes = getRationalArray(TAG_LATITUDE);
        Rational[] longitudes = getRationalArray(TAG_LONGITUDE);
        String latitudeRef = getString(TAG_LATITUDE_REF);
        String longitudeRef = getString(TAG_LONGITUDE_REF);

        // Make sure we have the required values
        if (latitudes == null || latitudes.length != 3)
            return null;
        if (longitudes == null || longitudes.length != 3)
            return null;
        if (latitudeRef == null || longitudeRef == null)
            return null;

        Double lat = GeoLocation.degreesMinutesSecondsToDecimal(latitudes[0], latitudes[1], latitudes[2], latitudeRef.equalsIgnoreCase("S"));
        Double lon = GeoLocation.degreesMinutesSecondsToDecimal(longitudes[0], longitudes[1], longitudes[2], longitudeRef.equalsIgnoreCase("W"));

        // This can return null, in cases where the conversion was not possible
        if (lat == null || lon == null)
            return null;

        return new GeoLocation(lat, lon);
    }

    /**
     * Parses the date stamp tag and the time stamp tag to obtain a single Date object representing the
     * date and time when this image was captured.
     *
     * @return A Date object representing when this image was captured, if possible, otherwise null
     */
    @Nullable
    public Date getGpsDate()
    {
        String date = getString(TAG_DATE_STAMP);
        Rational[] timeComponents = getRationalArray(TAG_TIME_STAMP);

        // Make sure we have the required values
        if (date == null)
            return null;
        if (timeComponents == null || timeComponents.length != 3)
            return null;

        String dateTime = String.format(Locale.US, "%s %02d:%02d:%02.3f UTC",
            date, timeComponents[0].intValue(), timeComponents[1].intValue(), timeComponents[2].doubleValue());
        try {
            DateFormat parser = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.S z");
            return parser.parse(dateTime);
        } catch (ParseException e) {
            return null;
        }
    }
}

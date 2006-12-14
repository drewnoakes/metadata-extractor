/*
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 26-Nov-2002 11:00:52 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes Exif tags that contain Global Positioning System (GPS) data.
 */
public class GpsDirectory extends Directory
{
    /** GPS tag version GPSVersionID 0 0 BYTE 4 */
    public static final int TAG_GPS_VERSION_ID = 0x0000;
    /** North or South Latitude GPSLatitudeRef 1 1 ASCII 2 */
    public static final int TAG_GPS_LATITUDE_REF = 0x0001;
    /** Latitude GPSLatitude 2 2 RATIONAL 3 */
    public static final int TAG_GPS_LATITUDE = 0x0002;
    /** East or West Longitude GPSLongitudeRef 3 3 ASCII 2 */
    public static final int TAG_GPS_LONGITUDE_REF = 0x0003;
    /** Longitude GPSLongitude 4 4 RATIONAL 3 */
    public static final int TAG_GPS_LONGITUDE = 0x0004;
    /** Altitude reference GPSAltitudeRef 5 5 BYTE 1 */
    public static final int TAG_GPS_ALTITUDE_REF = 0x0005;
    /** Altitude GPSAltitude 6 6 RATIONAL 1 */
    public static final int TAG_GPS_ALTITUDE = 0x0006;
    /** GPS time (atomic clock) GPSTimeStamp 7 7 RATIONAL 3 */
    public static final int TAG_GPS_TIME_STAMP = 0x0007;
    /** GPS satellites used for measurement GPSSatellites 8 8 ASCII Any */
    public static final int TAG_GPS_SATELLITES = 0x0008;
    /** GPS receiver status GPSStatus 9 9 ASCII 2 */
    public static final int TAG_GPS_STATUS = 0x0009;
    /** GPS measurement mode GPSMeasureMode 10 A ASCII 2 */
    public static final int TAG_GPS_MEASURE_MODE = 0x000A;
    /** Measurement precision GPSDOP 11 B RATIONAL 1 */
    public static final int TAG_GPS_DOP = 0x000B;
    /** Speed unit GPSSpeedRef 12 C ASCII 2 */
    public static final int TAG_GPS_SPEED_REF = 0x000C;
    /** Speed of GPS receiver GPSSpeed 13 D RATIONAL 1 */
    public static final int TAG_GPS_SPEED = 0x000D;
    /** Reference for direction of movement GPSTrackRef 14 E ASCII 2 */
    public static final int TAG_GPS_TRACK_REF = 0x000E;
    /** Direction of movement GPSTrack 15 F RATIONAL 1 */
    public static final int TAG_GPS_TRACK = 0x000F;
    /** Reference for direction of image GPSImgDirectionRef 16 10 ASCII 2 */
    public static final int TAG_GPS_IMG_DIRECTION_REF = 0x0010;
    /** Direction of image GPSImgDirection 17 11 RATIONAL 1 */
    public static final int TAG_GPS_IMG_DIRECTION = 0x0011;
    /** Geodetic survey data used GPSMapDatum 18 12 ASCII Any */
    public static final int TAG_GPS_MAP_DATUM = 0x0012;
    /** Reference for latitude of destination GPSDestLatitudeRef 19 13 ASCII 2 */
    public static final int TAG_GPS_DEST_LATITUDE_REF = 0x0013;
    /** Latitude of destination GPSDestLatitude 20 14 RATIONAL 3 */
    public static final int TAG_GPS_DEST_LATITUDE = 0x0014;
    /** Reference for longitude of destination GPSDestLongitudeRef 21 15 ASCII 2 */
    public static final int TAG_GPS_DEST_LONGITUDE_REF = 0x0015;
    /** Longitude of destination GPSDestLongitude 22 16 RATIONAL 3 */
    public static final int TAG_GPS_DEST_LONGITUDE = 0x0016;
    /** Reference for bearing of destination GPSDestBearingRef 23 17 ASCII 2 */
    public static final int TAG_GPS_DEST_BEARING_REF = 0x0017;
    /** Bearing of destination GPSDestBearing 24 18 RATIONAL 1 */
    public static final int TAG_GPS_DEST_BEARING = 0x0018;
    /** Reference for distance to destination GPSDestDistanceRef 25 19 ASCII 2 */
    public static final int TAG_GPS_DEST_DISTANCE_REF = 0x0019;
    /** Distance to destination GPSDestDistance 26 1A RATIONAL 1 */
    public static final int TAG_GPS_DEST_DISTANCE = 0x001A;

    protected static final HashMap tagNameMap = new HashMap();

    static
    {
        tagNameMap.put(new Integer(TAG_GPS_VERSION_ID), "GPS Version ID");
        tagNameMap.put(new Integer(TAG_GPS_LATITUDE_REF), "GPS Latitude Ref");
        tagNameMap.put(new Integer(TAG_GPS_LATITUDE), "GPS Latitude");
        tagNameMap.put(new Integer(TAG_GPS_LONGITUDE_REF), "GPS Longitude Ref");
        tagNameMap.put(new Integer(TAG_GPS_LONGITUDE), "GPS Longitude");
        tagNameMap.put(new Integer(TAG_GPS_ALTITUDE_REF), "GPS Altitude Ref");
        tagNameMap.put(new Integer(TAG_GPS_ALTITUDE), "GPS Altitude");
        tagNameMap.put(new Integer(TAG_GPS_TIME_STAMP), "GPS Time-Stamp");
        tagNameMap.put(new Integer(TAG_GPS_SATELLITES), "GPS Satellites");
        tagNameMap.put(new Integer(TAG_GPS_STATUS), "GPS Status");
        tagNameMap.put(new Integer(TAG_GPS_MEASURE_MODE), "GPS Measure Mode");
        tagNameMap.put(new Integer(TAG_GPS_DOP), "GPS DOP");
        tagNameMap.put(new Integer(TAG_GPS_SPEED_REF), "GPS Speed Ref");
        tagNameMap.put(new Integer(TAG_GPS_SPEED), "GPS Speed");
        tagNameMap.put(new Integer(TAG_GPS_TRACK_REF), "GPS Track Ref");
        tagNameMap.put(new Integer(TAG_GPS_TRACK), "GPS Track");
        tagNameMap.put(new Integer(TAG_GPS_IMG_DIRECTION_REF), "GPS Img Direction Ref");
        tagNameMap.put(new Integer(TAG_GPS_IMG_DIRECTION_REF), "GPS Img Direction");
        tagNameMap.put(new Integer(TAG_GPS_MAP_DATUM), "GPS Map Datum");
        tagNameMap.put(new Integer(TAG_GPS_DEST_LATITUDE_REF), "GPS Dest Latitude Ref");
        tagNameMap.put(new Integer(TAG_GPS_DEST_LATITUDE), "GPS Dest Latitude");
        tagNameMap.put(new Integer(TAG_GPS_DEST_LONGITUDE_REF), "GPS Dest Longitude Ref");
        tagNameMap.put(new Integer(TAG_GPS_DEST_LONGITUDE), "GPS Dest Longitude");
        tagNameMap.put(new Integer(TAG_GPS_DEST_BEARING_REF), "GPS Dest Bearing Ref");
        tagNameMap.put(new Integer(TAG_GPS_DEST_BEARING), "GPS Dest Bearing");
        tagNameMap.put(new Integer(TAG_GPS_DEST_DISTANCE_REF), "GPS Dest Distance Ref");
        tagNameMap.put(new Integer(TAG_GPS_DEST_DISTANCE), "GPS Dest Distance");
    }

    public GpsDirectory()
    {
        this.setDescriptor(new GpsDescriptor(this));
    }

    public String getName()
    {
        return "GPS";
    }

    protected HashMap getTagNameMap()
    {
        return tagNameMap;
    }
}

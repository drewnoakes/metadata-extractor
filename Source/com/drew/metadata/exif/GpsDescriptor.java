/*
 * Copyright 2002-2011 Drew Noakes
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

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

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
                return _directory.getString(tagType);
        }
    }

    @Nullable
    public String getGpsLatitudeDescription()
    {
        return getDegreesMinutesSecondsDescription(GpsDirectory.TAG_GPS_LATITUDE);
    }

    @Nullable
    public String getGpsLongitudeDescription()
    {
        return getDegreesMinutesSecondsDescription(GpsDirectory.TAG_GPS_LONGITUDE);
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
        final String value = _directory.getString(tagType);
        if (value==null)
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

    /**
     * New version. Should really be called getDegreesMinutesSecondsDescription
     *
     * @author David Ekholm
     */
    @Nullable
    public String getDegreesMinutesSecondsDescription(int tagType)
    {
        Rational[] values = _directory.getRationalArray(tagType);

        if (values==null)
            return null;
        
        Rational degs = values[0];
        Rational mins = values[1];
        Rational secs = values[2];
        //System.out.println("The three rationals: " + degs + " " + mins + " " + secs);

        // Protect against modern Nikon software writing GPS coordinates using extremely large nominators and denominators
        if (degs.intValue() != degs.floatValue() || mins.intValue() != mins.floatValue()) {
            // Find out least common denominator of the three
            long lcd = degs.getDenominator();
            if (lcd == 0) { // Yes, some cameras put 0/0 here
                return "";
            }
            if (mins.getNumerator() != 0) {
                lcd = calcLCD(lcd, mins.getDenominator());
            }
            if (secs.getNumerator() != 0) {
                lcd = calcLCD(lcd, secs.getDenominator());
            }

            long asSecsNum = 3600L * degs.getNumerator() * (lcd / degs.getDenominator());
            if (mins.getNumerator() != 0) {
                asSecsNum += 60L * mins.getNumerator() * (lcd / mins.getDenominator());
            }
            if (secs.getNumerator() != 0) {
                asSecsNum += secs.getNumerator() * (lcd / secs.getDenominator());
            }
            //System.out.println("asSecsNum: " + asSecsNum);
            //System.out.println("lcd: " + lcd);

            degs = new Rational(asSecsNum / lcd / 3600L, 1);
            mins = new Rational((asSecsNum - 3600L * degs.getNumerator() * lcd) / 60L / lcd, 1);
            secs = new Rational(asSecsNum - 3600L * degs.getNumerator() * lcd - 60L * mins.getNumerator() * lcd, lcd);
        }
        return "" + degs.intValue() + "°" + mins.intValue() + "'" + secs.floatValue() + "\"";
    }

    /** Greatest Common Divisor using Euclides */
    private long calcGCD(long nr1, long nr2)
    {
        long temp;
        while (nr2 != 0) {
            temp = nr2;
            nr2 = nr1 % temp;
            nr1 = temp;
        }
        return nr1;
    }

    /** Least common denominator */
    private long calcLCD(long nr1, long nr2)
    {
        return (nr1 * nr2) / calcGCD(nr1, nr2);
    }
}

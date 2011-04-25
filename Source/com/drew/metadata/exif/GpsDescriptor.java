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
 * Created by dnoakes on 12-Nov-2002 22:27:52 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string representations of tag values stored in a <code>GpsDirectory</code>.
 */
public class GpsDescriptor extends TagDescriptor
{
    public GpsDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
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

    public String getGpsLatitudeDescription() throws MetadataException
    {
        if (!_directory.containsTag(GpsDirectory.TAG_GPS_LATITUDE)) return null;
        return getDegreesMinutesSecondsDescription(GpsDirectory.TAG_GPS_LATITUDE);
    }

    public String getGpsLongitudeDescription() throws MetadataException
    {
        if (!_directory.containsTag(GpsDirectory.TAG_GPS_LONGITUDE)) return null;
        return getDegreesMinutesSecondsDescription(GpsDirectory.TAG_GPS_LONGITUDE);
    }

    public String getGpsTimeStampDescription() throws MetadataException
    {
        // time in hour, min, sec
        if (!_directory.containsTag(GpsDirectory.TAG_GPS_TIME_STAMP)) return null;
        int[] timeComponents = _directory.getIntArray(GpsDirectory.TAG_GPS_TIME_STAMP);
        StringBuffer description = new StringBuffer();
        description.append(timeComponents[0]);
        description.append(":");
        description.append(timeComponents[1]);
        description.append(":");
        description.append(timeComponents[2]);
        description.append(" UTC");
        return description.toString();
    }

    public String getGpsDestinationReferenceDescription()
    {
        if (!_directory.containsTag(GpsDirectory.TAG_GPS_DEST_DISTANCE_REF)) return null;
        String destRef = _directory.getString(GpsDirectory.TAG_GPS_DEST_DISTANCE_REF).trim();
        if ("K".equalsIgnoreCase(destRef)) {
            return "kilometers";
        } else if ("M".equalsIgnoreCase(destRef)) {
            return "miles";
        } else if ("N".equalsIgnoreCase(destRef)) {
            return "knots";
        } else {
            return "Unknown (" + destRef + ")";
        }
    }

    public String getGpsDirectionDescription(int tagType)
    {
        if (!_directory.containsTag(tagType)) return null;
        return _directory.getString(tagType).trim() + " degrees";
    }

    public String getGpsDirectionReferenceDescription(int tagType)
    {
        if (!_directory.containsTag(tagType)) return null;
        String gpsDistRef = _directory.getString(tagType).trim();
        if ("T".equalsIgnoreCase(gpsDistRef)) {
            return "True direction";
        } else if ("M".equalsIgnoreCase(gpsDistRef)) {
            return "Magnetic direction";
        } else {
            return "Unknown (" + gpsDistRef + ")";
        }
    }

    public String getGpsSpeedRefDescription()
    {
        if (!_directory.containsTag(GpsDirectory.TAG_GPS_SPEED_REF)) return null;
        String gpsSpeedRef = _directory.getString(GpsDirectory.TAG_GPS_SPEED_REF).trim();
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

    public String getGpsMeasureModeDescription()
    {
        if (!_directory.containsTag(GpsDirectory.TAG_GPS_MEASURE_MODE)) return null;
        String gpsSpeedMeasureMode = _directory.getString(GpsDirectory.TAG_GPS_MEASURE_MODE).trim();
        if ("2".equalsIgnoreCase(gpsSpeedMeasureMode)) {
            return "2-dimensional measurement";
        } else if ("3".equalsIgnoreCase(gpsSpeedMeasureMode)) {
            return "3-dimensional measurement";
        } else {
            return "Unknown (" + gpsSpeedMeasureMode + ")";
        }
    }

    public String getGpsStatusDescription()
    {
        if (!_directory.containsTag(GpsDirectory.TAG_GPS_STATUS)) return null;
        String gpsStatus = _directory.getString(GpsDirectory.TAG_GPS_STATUS).trim();
        if ("A".equalsIgnoreCase(gpsStatus)) {
            return "Active (Measurement in progress)";
        } else if ("V".equalsIgnoreCase(gpsStatus)) {
            return "Void (Measurement Interoperability)";
        } else {
            return "Unknown (" + gpsStatus + ")";
        }
    }

    public String getGpsAltitudeRefDescription() throws MetadataException
    {
        if (!_directory.containsTag(GpsDirectory.TAG_GPS_ALTITUDE_REF)) return null;
        int altitudeRef = _directory.getInt(GpsDirectory.TAG_GPS_ALTITUDE_REF);
        if (altitudeRef == 0)
            return "Sea level";
        if (altitudeRef == 1)
            return "Below sea level";
        return "Unknown (" + altitudeRef + ")";
    }

    public String getGpsAltitudeDescription() throws MetadataException
    {
        if (!_directory.containsTag(GpsDirectory.TAG_GPS_ALTITUDE)) return null;
//      String altitude = _directory.getRational(GpsDirectory.TAG_GPS_ALTITUDE).toSimpleString(true);
        return "" + _directory.getRational(GpsDirectory.TAG_GPS_ALTITUDE).intValue() + " metres";
    }

    public String getGpsDifferentialDescription() throws MetadataException
    {
        if (!_directory.containsTag(GpsDirectory.TAG_GPS_DIFFERENTIAL)) return null;
        int differential = _directory.getInt(GpsDirectory.TAG_GPS_DIFFERENTIAL);
        if (differential == 0)
            return "No Correction";
        if (differential == 1)
            return "Differential Corrected";
        return "Unknown (" + differential + ")";
    }

    /**
     * New version. Should really be called getDegreesMinutesSecondsDescription
     * @author David Ekholm
     */
    public String getDegreesMinutesSecondsDescription(int tagType) throws MetadataException
    {
        Rational[] r = _directory.getRationalArray(tagType);
        Rational degs = r[0];
        Rational mins = r[1];
        Rational secs = r[2];
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

    /**
     * Greatest Common Divisor using Euclides
     */
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

    /**
     * Least common denominator
     */
    private long calcLCD(long nr1, long nr2) {
        return (nr1 * nr2) / calcGCD(nr1, nr2);
    }
}

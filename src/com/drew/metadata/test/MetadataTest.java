/*
 * MetadataTest.java
 *
 * Test class written by Drew Noakes.
 *
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   drew@drewnoakes.com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 26-Oct-2002 18:35:12 using IntelliJ IDEA.
 */
package com.drew.metadata.test;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.GpsDirectory;
import junit.framework.TestCase;

/**
 * JUnit test case for class Metadata.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class MetadataTest extends TestCase
{
    public MetadataTest(String s)
    {
        super(s);
    }

    public void testSetAndGetSingleTag() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        directory.setInt(ExifDirectory.TAG_APERTURE, 1);
        assertEquals(1, directory.getInt(ExifDirectory.TAG_APERTURE));
    }

    public void testSetSameTagMultpleTimes() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        directory.setInt(ExifDirectory.TAG_APERTURE, 1);
        directory.setInt(ExifDirectory.TAG_APERTURE, 2);
        assertEquals("setting the tag with a different value should override old value",
                2, directory.getInt(ExifDirectory.TAG_APERTURE));
    }

    public void testGetDirectory() throws Exception
    {
        Metadata metadata = new Metadata();
        assertTrue(metadata.getDirectory(ExifDirectory.class) instanceof ExifDirectory);
    }

    public void testSetAndGetMultipleTagsInSingleDirectory() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory exifDir = metadata.getDirectory(ExifDirectory.class);
        exifDir.setString(ExifDirectory.TAG_APERTURE, "Tag Value");
        exifDir.setString(ExifDirectory.TAG_BATTERY_LEVEL, "Another tag");
        assertEquals("Tag Value", exifDir.getString(ExifDirectory.TAG_APERTURE));
        assertEquals("Another tag", exifDir.getString(ExifDirectory.TAG_BATTERY_LEVEL));
    }

    public void testSetAndGetMultipleTagsInMultilpeDirectories() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory exifDir = metadata.getDirectory(ExifDirectory.class);
        Directory gpsDir = metadata.getDirectory(GpsDirectory.class);
        exifDir.setString(ExifDirectory.TAG_APERTURE, "ExifAperture");
        exifDir.setString(ExifDirectory.TAG_BATTERY_LEVEL, "ExifBatteryLevel");
        gpsDir.setString(GpsDirectory.TAG_GPS_ALTITUDE, "GpsAltitude");
        gpsDir.setString(GpsDirectory.TAG_GPS_DEST_BEARING, "GpsDestBearing");
        assertEquals("ExifAperture", exifDir.getString(ExifDirectory.TAG_APERTURE));
        assertEquals("ExifBatteryLevel", exifDir.getString(ExifDirectory.TAG_BATTERY_LEVEL));
        assertEquals("GpsAltitude", gpsDir.getString(GpsDirectory.TAG_GPS_ALTITUDE));
        assertEquals("GpsDestBearing", gpsDir.getString(GpsDirectory.TAG_GPS_DEST_BEARING));
    }

/*
    public void testCountTags() throws Exception
    {
        Metadata info = new Metadata();
        assertEquals(0, info.countTags());

        info.setString(ExifReader.DIRECTORY_EXIF_EXIF, ExifDirectory.TAG_APERTURE, "ExifAperture");
        assertEquals(1, info.countTags());
        info.setString(ExifReader.DIRECTORY_EXIF_EXIF, ExifDirectory.TAG_BATTERY_LEVEL, "ExifBatteryLevel");
        assertEquals(2, info.countTags());
        info.setString(ExifReader.DIRECTORY_EXIF_GPS, GpsDirectory.TAG_GPS_ALTITUDE, "GpsAltitude");
        assertEquals(3, info.countTags());
        info.setString(ExifReader.DIRECTORY_EXIF_GPS, GpsDirectory.TAG_GPS_DEST_BEARING, "GpsDestBearing");
        assertEquals(4, info.countTags());
    }
*/

    public void testContainsTag() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory exifDir = metadata.getDirectory(ExifDirectory.class);
        assertTrue(!exifDir.containsTag(ExifDirectory.TAG_APERTURE));
        exifDir.setString(ExifDirectory.TAG_APERTURE, "Tag Value");
        assertTrue(exifDir.containsTag(ExifDirectory.TAG_APERTURE));
    }

    public void testGetNonExistantTag() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory exifDir = metadata.getDirectory(ExifDirectory.class);
        assertEquals(null, exifDir.getString(ExifDirectory.TAG_APERTURE));
    }
}

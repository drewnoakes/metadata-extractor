/*
 * ImageInfoTest.java
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
package com.drew.imaging.exif.test;

import com.drew.imaging.exif.ExifTagValues;
import com.drew.imaging.exif.ImageInfo;
import junit.framework.TestCase;

/**
 * JUnit test case for class ImageInfo.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ImageInfoTest extends TestCase implements ExifTagValues
{
    public ImageInfoTest(String s)
    {
        super(s);
    }

    public void testSetAndGetSingleTag() throws Exception
    {
        ImageInfo info = new ImageInfo();
        info.setString(IFD_EXIF, TAG_APERTURE, "Aperture");
        assertEquals("Aperture", info.getString(IFD_EXIF, TAG_APERTURE));

        info.setString(IFD_EXIF, TAG_APERTURE, "Replaced");
        assertEquals("setting existing tag should replace value", "Replaced", info.getString(IFD_EXIF, TAG_APERTURE));
    }

    public void testSetAndGetMultipleTagsInSingleDirectory() throws Exception
    {
        ImageInfo info = new ImageInfo();
        info.setString(IFD_EXIF, TAG_APERTURE, "Tag Value");
        info.setString(IFD_EXIF, TAG_BATTERY_LEVEL, "Another tag");
        assertEquals("Tag Value", info.getString(IFD_EXIF, TAG_APERTURE));
        assertEquals("Another tag", info.getString(IFD_EXIF, TAG_BATTERY_LEVEL));
    }

    public void testSetAndGetMultipleTagsInMultilpeDirectories() throws Exception
    {
        ImageInfo info = new ImageInfo();
        info.setString(IFD_EXIF, TAG_APERTURE, "ExifAperture");
        info.setString(IFD_EXIF, TAG_BATTERY_LEVEL, "ExifBatteryLevel");
        info.setString(IFD_GPS, TAG_GPS_ALTITUDE, "GpsAltitude");
        info.setString(IFD_GPS, TAG_GPS_DEST_BEARING, "GpsDestBearing");
        assertEquals("ExifAperture", info.getString(IFD_EXIF, TAG_APERTURE));
        assertEquals("ExifBatteryLevel", info.getString(IFD_EXIF, TAG_BATTERY_LEVEL));
        assertEquals("GpsAltitude", info.getString(IFD_GPS, TAG_GPS_ALTITUDE));
        assertEquals("GpsDestBearing", info.getString(IFD_GPS, TAG_GPS_DEST_BEARING));
    }

    public void testCountTags() throws Exception
    {
        ImageInfo info = new ImageInfo();
        assertEquals(0, info.countTags());

        info.setString(IFD_EXIF, TAG_APERTURE, "ExifAperture");
        assertEquals(1, info.countTags());
        info.setString(IFD_EXIF, TAG_BATTERY_LEVEL, "ExifBatteryLevel");
        assertEquals(2, info.countTags());
        info.setString(IFD_GPS, TAG_GPS_ALTITUDE, "GpsAltitude");
        assertEquals(3, info.countTags());
        info.setString(IFD_GPS, TAG_GPS_DEST_BEARING, "GpsDestBearing");
        assertEquals(4, info.countTags());
    }

    public void testContainsTag() throws Exception
    {
        ImageInfo info = new ImageInfo();
        assertTrue(!info.containsTag(IFD_EXIF, TAG_APERTURE));
        info.setString(IFD_EXIF, TAG_APERTURE, "Tag Value");
        assertTrue(info.containsTag(IFD_EXIF, TAG_APERTURE));
    }

    public void testGetNonExistantTag() throws Exception
    {
        ImageInfo info = new ImageInfo();
        assertEquals(null, info.getString(IFD_EXIF, TAG_APERTURE));
    }
}

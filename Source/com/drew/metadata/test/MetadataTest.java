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
package com.drew.metadata.test;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.lang.NullOutputStream;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Iterator;

/**
 * JUnit test case for class Metadata.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class MetadataTest
{
    @Test
    public void testSetAndGetSingleTag() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        directory.setInt(ExifDirectory.TAG_APERTURE, 1);
        Assert.assertEquals(1, directory.getInt(ExifDirectory.TAG_APERTURE));
    }

    @Test
    public void testSetSameTagMultipleTimes() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        directory.setInt(ExifDirectory.TAG_APERTURE, 1);
        directory.setInt(ExifDirectory.TAG_APERTURE, 2);
        Assert.assertEquals("setting the tag with a different value should override old value",
                2, directory.getInt(ExifDirectory.TAG_APERTURE));
    }

    @Test
    public void testGetDirectory() throws Exception
    {
        Metadata metadata = new Metadata();
        Assert.assertTrue(metadata.getDirectory(ExifDirectory.class) instanceof ExifDirectory);
    }

    @Test
    public void testSetAndGetMultipleTagsInSingleDirectory() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory exifDir = metadata.getDirectory(ExifDirectory.class);
        exifDir.setString(ExifDirectory.TAG_APERTURE, "Tag Value");
        exifDir.setString(ExifDirectory.TAG_BATTERY_LEVEL, "Another tag");
        Assert.assertEquals("Tag Value", exifDir.getString(ExifDirectory.TAG_APERTURE));
        Assert.assertEquals("Another tag", exifDir.getString(ExifDirectory.TAG_BATTERY_LEVEL));
    }

    @Test
    public void testSetAndGetMultipleTagsInMultipleDirectories() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory exifDir = metadata.getDirectory(ExifDirectory.class);
        Directory gpsDir = metadata.getDirectory(GpsDirectory.class);
        exifDir.setString(ExifDirectory.TAG_APERTURE, "ExifAperture");
        exifDir.setString(ExifDirectory.TAG_BATTERY_LEVEL, "ExifBatteryLevel");
        gpsDir.setString(GpsDirectory.TAG_GPS_ALTITUDE, "GpsAltitude");
        gpsDir.setString(GpsDirectory.TAG_GPS_DEST_BEARING, "GpsDestBearing");
        Assert.assertEquals("ExifAperture", exifDir.getString(ExifDirectory.TAG_APERTURE));
        Assert.assertEquals("ExifBatteryLevel", exifDir.getString(ExifDirectory.TAG_BATTERY_LEVEL));
        Assert.assertEquals("GpsAltitude", gpsDir.getString(GpsDirectory.TAG_GPS_ALTITUDE));
        Assert.assertEquals("GpsDestBearing", gpsDir.getString(GpsDirectory.TAG_GPS_DEST_BEARING));
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

    @Test
    public void testContainsTag() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory exifDir = metadata.getDirectory(ExifDirectory.class);
        Assert.assertTrue(!exifDir.containsTag(ExifDirectory.TAG_APERTURE));
        exifDir.setString(ExifDirectory.TAG_APERTURE, "Tag Value");
        Assert.assertTrue(exifDir.containsTag(ExifDirectory.TAG_APERTURE));
    }

    @Test
    public void testGetNonExistentTag() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory exifDir = metadata.getDirectory(ExifDirectory.class);
        Assert.assertEquals(null, exifDir.getString(ExifDirectory.TAG_APERTURE));
    }

    @Test
    public void testHasErrors() throws Exception
    {
        Metadata metadata = JpegMetadataReader.readMetadata(new File("Source/com/drew/metadata/exif/test/badExif.jpg"));
        Assert.assertTrue("exif error", metadata.getDirectory(ExifDirectory.class).hasErrors());
        metadata = JpegMetadataReader.readMetadata(new File("Source/com/drew/metadata/exif/test/withExif.jpg"));
        Assert.assertTrue("no errors", !metadata.getDirectory(ExifDirectory.class).hasErrors());
    }

    @Test
    public void testGetErrors() throws Exception
    {
        Metadata metadata = JpegMetadataReader.readMetadata(new File("Source/com/drew/metadata/exif/test/badExif.jpg"));
        Iterator<String> errors = metadata.getDirectory(ExifDirectory.class).getErrors().iterator();
        Assert.assertTrue(errors.hasNext());
        String error = errors.next();
        Assert.assertEquals("Exif data segment must contain at least 14 bytes", error);
        Assert.assertTrue(!errors.hasNext());
    }

    @Test
    public void testGetErrorCount() throws Exception
    {
        Metadata metadata = JpegMetadataReader.readMetadata(new File("Source/com/drew/metadata/exif/test/badExif.jpg"));
        Assert.assertEquals(1, metadata.getDirectory(ExifDirectory.class).getErrorCount());
    }

    @Test
    public void testMetadataSerializable() throws Exception
    {
        Metadata metadata = JpegMetadataReader.readMetadata(new File("Source/com/drew/metadata/test/withIptcExifGps.jpg"));
        new ObjectOutputStream(new NullOutputStream()).writeObject(metadata);
    }

    @Test
    public void testSerializeAndRestore() throws Exception
    {
        Metadata metadataWrite = JpegMetadataReader.readMetadata(new File("Source/com/drew/metadata/test/withIptcExifGps.jpg"));
        Metadata metadataRead;
        File ser = File.createTempFile("test", "ser");
        try {
            // write the ser object
            new ObjectOutputStream(new FileOutputStream(ser)).writeObject(metadataWrite);
            // read the ser object
            metadataRead = (Metadata)new ObjectInputStream(new FileInputStream(ser)).readObject();
            // make sure they're equivalent
            // TODO should compare the two objects via iteration of directories and tags
            Assert.assertTrue(metadataRead.containsDirectory(ExifDirectory.class));
            Assert.assertTrue(metadataRead.containsDirectory(IptcDirectory.class));
        } finally {
            ser.delete();
        }
    }
}

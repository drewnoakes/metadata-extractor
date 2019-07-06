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

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import org.junit.Test;

import java.io.IOException;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ExifSubIFDDirectory}, {@link ExifIFD0Directory}, {@link ExifThumbnailDirectory} and
 * {@link GpsDirectory}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("ConstantConditions")
public class ExifDirectoryTest
{
    @Test
    public void testGetDirectoryName() throws Exception
    {
        Directory subIFDDirectory = new ExifSubIFDDirectory();
        Directory ifd0Directory = new ExifIFD0Directory();
        Directory thumbDirectory = new ExifThumbnailDirectory();
        Directory gpsDirectory = new GpsDirectory();

        assertFalse(subIFDDirectory.hasErrors());
        assertFalse(ifd0Directory.hasErrors());
        assertFalse(thumbDirectory.hasErrors());
        assertFalse(gpsDirectory.hasErrors());

        assertEquals("Exif IFD0", ifd0Directory.getName());
        assertEquals("Exif SubIFD", subIFDDirectory.getName());
        assertEquals("Exif Thumbnail", thumbDirectory.getName());
        assertEquals("GPS", gpsDirectory.getName());
    }

    @Test
    public void testDateTime() throws JpegProcessingException, IOException, MetadataException
    {
        Metadata metadata = ExifReaderTest.processBytes("Tests/Data/nikonMakernoteType2a.jpg.app1");

        ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        ExifSubIFDDirectory exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        assertNotNull(exifIFD0Directory);
        assertNotNull(exifSubIFDDirectory);

        assertEquals("2003:10:15 10:37:08", exifIFD0Directory.getString(ExifIFD0Directory.TAG_DATETIME));
        assertEquals("80", exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_SUBSECOND_TIME));
        assertEquals("2003:10:15 10:37:08", exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
        assertEquals("80", exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_SUBSECOND_TIME_ORIGINAL));
        assertEquals("2003:10:15 10:37:08", exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED));
        assertEquals("80", exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_SUBSECOND_TIME_DIGITIZED));

        assertEquals(1066214228800L, exifIFD0Directory.getDate(
            ExifIFD0Directory.TAG_DATETIME,
            exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_SUBSECOND_TIME),
            null
        ).getTime());
        assertEquals(1066210628800L, exifIFD0Directory.getDate(
            ExifIFD0Directory.TAG_DATETIME,
            exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_SUBSECOND_TIME),
            TimeZone.getTimeZone("GMT+0100")
        ).getTime());

        assertEquals(1066214228800L, exifSubIFDDirectory.getDateModified().getTime());
        assertEquals(1066210628800L, exifSubIFDDirectory.getDateModified(TimeZone.getTimeZone("GMT+0100")).getTime());
        assertEquals(1066214228800L, exifSubIFDDirectory.getDateOriginal().getTime());
        assertEquals(1066210628800L, exifSubIFDDirectory.getDateOriginal(TimeZone.getTimeZone("GMT+0100")).getTime());
        assertEquals(1066214228800L, exifSubIFDDirectory.getDateDigitized().getTime());
        assertEquals(1066210628800L, exifSubIFDDirectory.getDateDigitized(TimeZone.getTimeZone("GMT+0100")).getTime());
    }

    @Test
    public void testResolution() throws JpegProcessingException, IOException, MetadataException
    {
        Metadata metadata = ExifReaderTest.processBytes("Tests/Data/withUncompressedRGBThumbnail.jpg.app1");

        ExifThumbnailDirectory thumbnailDirectory = metadata.getFirstDirectoryOfType(ExifThumbnailDirectory.class);
        assertNotNull(thumbnailDirectory);
        assertEquals(72, thumbnailDirectory.getInt(ExifThumbnailDirectory.TAG_X_RESOLUTION));

        ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        assertNotNull(exifIFD0Directory);
        assertEquals(216, exifIFD0Directory.getInt(ExifIFD0Directory.TAG_X_RESOLUTION));
    }

    @Test
    public void testGeoLocation() throws IOException, MetadataException
    {
        Metadata metadata = ExifReaderTest.processBytes("Tests/Data/withExifAndIptc.jpg.app1.0");

        GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
        assertNotNull(gpsDirectory);
        GeoLocation geoLocation = gpsDirectory.getGeoLocation();
        assertEquals(54.989666666666665, geoLocation.getLatitude(), 0.001);
        assertEquals(-1.9141666666666666, geoLocation.getLongitude(), 0.001);
    }

    @Test
    public void testGpsDate() throws IOException, MetadataException
    {
        Metadata metadata = ExifReaderTest.processBytes("Tests/Data/withPanasonicFaces.jpg.app1");

        GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
        assertNotNull(gpsDirectory);
        assertEquals("2010:06:24", gpsDirectory.getString(GpsDirectory.TAG_DATE_STAMP));
        assertEquals("10/1 17/1 21/1", gpsDirectory.getString(GpsDirectory.TAG_TIME_STAMP));
        assertEquals(1277374641000L, gpsDirectory.getGpsDate().getTime());
    }
}

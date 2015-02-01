/*
 * Copyright 2002-2015 Drew Noakes
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
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ExifSubIFDDirectory}, {@link ExifIFD0Directory}, {@link ExifThumbnailDirectory}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ExifDirectoryTest
{
    @Test
    public void testGetDirectoryName() throws Exception
    {
        Directory subIFDDirectory = new ExifSubIFDDirectory();
        Directory ifd0Directory = new ExifIFD0Directory();
        Directory thumbDirectory = new ExifThumbnailDirectory();

        assertFalse(subIFDDirectory.hasErrors());
        assertFalse(ifd0Directory.hasErrors());
        assertFalse(thumbDirectory.hasErrors());

        assertEquals("Exif IFD0", ifd0Directory.getName());
        assertEquals("Exif SubIFD", subIFDDirectory.getName());
        assertEquals("Exif Thumbnail", thumbDirectory.getName());
    }

    @Test
    public void testGetThumbnailData() throws Exception
    {
        ExifThumbnailDirectory directory = ExifReaderTest.processBytes("Tests/Data/withExif.jpg.app1", ExifThumbnailDirectory.class);

        byte[] thumbData = directory.getThumbnailData();
        assertNotNull(thumbData);
        try {
            // attempt to read the thumbnail -- it should be a legal Jpeg file
            JpegSegmentReader.readSegments(new SequentialByteArrayReader(thumbData), null);
        } catch (JpegProcessingException e) {
            fail("Unable to construct JpegSegmentReader from thumbnail data");
        }
    }

    @Test
    public void testWriteThumbnail() throws Exception
    {
        ExifThumbnailDirectory directory = ExifReaderTest.processBytes("Tests/Data/manuallyAddedThumbnail.jpg.app1", ExifThumbnailDirectory.class);

        assertTrue(directory.hasThumbnailData());

        File thumbnailFile = File.createTempFile("thumbnail", ".jpg");
        try {
            directory.writeThumbnail(thumbnailFile.getAbsolutePath());
            File file = new File(thumbnailFile.getAbsolutePath());
            assertEquals(2970, file.length());
            assertTrue(file.exists());
        } finally {
            if (!thumbnailFile.delete())
                fail("Unable to delete temp thumbnail file.");
        }
    }

//    @Test
//    public void testContainsThumbnail()
//    {
//        ExifSubIFDDirectory exifDirectory = new ExifSubIFDDirectory();
//
//        assertTrue(!exifDirectory.hasThumbnailData());
//
//        exifDirectory.setObject(ExifSubIFDDirectory.TAG_THUMBNAIL_DATA, "foo");
//
//        assertTrue(exifDirectory.hasThumbnailData());
//    }

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
}

/*
 * Copyright 2002-2012 Drew Noakes
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

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class ExifDirectoryTest
{
    @Test
    public void testGetDirectoryName() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory subIFDDirectory = metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        Directory ifd0Directory = metadata.getOrCreateDirectory(ExifIFD0Directory.class);
        Directory thumbDirectory = metadata.getOrCreateDirectory(ExifThumbnailDirectory.class);

        assertFalse(subIFDDirectory.hasErrors());

        assertEquals("Exif IFD0", ifd0Directory.getName());
        assertEquals("Exif SubIFD", subIFDDirectory.getName());
        assertEquals("Exif Thumbnail", thumbDirectory.getName());
    }

    @Test
    public void testGetThumbnailData() throws Exception
    {
        ExifThumbnailDirectory directory = ExifReaderTest.processExifBytes("Tests/data/withExif.jpg.app1", ExifThumbnailDirectory.class);

        byte[] thumbData = directory.getThumbnailData();
        assertNotNull(thumbData);
        try {
            // attempt to read the thumbnail -- it should be a legal Jpeg file
            JpegSegmentReader.readSegments(new SequentialByteArrayReader(thumbData), null);
        } catch (JpegProcessingException e) {
            Assert.fail("Unable to construct JpegSegmentReader from thumbnail data");
        }
    }

    @Test
    public void testWriteThumbnail() throws Exception
    {
        ExifThumbnailDirectory directory = ExifReaderTest.processExifBytes("Tests/data/manuallyAddedThumbnail.jpg.app1", ExifThumbnailDirectory.class);

        assertTrue(directory.hasThumbnailData());

        File thumbnailFile = File.createTempFile("thumbnail", ".jpg");
        try {
            directory.writeThumbnail(thumbnailFile.getAbsolutePath());
            File file = new File(thumbnailFile.getAbsolutePath());
            assertEquals(2970, file.length());
            assertTrue(file.exists());
        } finally {
            if (!thumbnailFile.delete())
                Assert.fail("Unable to delete temp thumbnail file.");
        }
    }

//    @Test
//    public void testContainsThumbnail()
//    {
//        ExifSubIFDDirectory exifDirectory = new ExifSubIFDDirectory();
//
//        Assert.assertTrue(!exifDirectory.hasThumbnailData());
//
//        exifDirectory.setObject(ExifSubIFDDirectory.TAG_THUMBNAIL_DATA, "foo");
//
//        Assert.assertTrue(exifDirectory.hasThumbnailData());
//    }

    @Test
    public void testResolution() throws JpegProcessingException, IOException, MetadataException
    {
        File file = new File("Tests/com/drew/metadata/exif/withUncompressedRGBThumbnail.jpg");
        Metadata metadata = JpegMetadataReader.readMetadata(file);

        ExifThumbnailDirectory thumbnailDirectory = metadata.getDirectory(ExifThumbnailDirectory.class);
        assertNotNull(thumbnailDirectory);
        assertEquals(72, thumbnailDirectory.getInt(ExifThumbnailDirectory.TAG_X_RESOLUTION));
        
        ExifIFD0Directory exifIFD0Directory = metadata.getDirectory(ExifIFD0Directory.class);
        assertNotNull(exifIFD0Directory);
        assertEquals(216, exifIFD0Directory.getInt(ExifIFD0Directory.TAG_X_RESOLUTION));
    }
}

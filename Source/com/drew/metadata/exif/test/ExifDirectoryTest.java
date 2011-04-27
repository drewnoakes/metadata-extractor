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
package com.drew.metadata.exif.test;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class ExifDirectoryTest
{
    @Test
    public void testGetDirectoryName() throws Exception
    {
        Metadata metadata = new Metadata();
        ExifDirectory directory = metadata.getOrCreateDirectory(ExifDirectory.class);
        Assert.assertEquals("Exif", directory.getName());
    }

    @Test
    public void testGetThumbnailData() throws Exception
    {
        File file = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        Metadata metadata = JpegMetadataReader.readMetadata(file);
        ExifDirectory exifDirectory = metadata.getOrCreateDirectory(ExifDirectory.class);
        Assert.assertTrue(exifDirectory.containsTag(ExifDirectory.TAG_THUMBNAIL_DATA));
        byte[] thumbData = exifDirectory.getThumbnailData();
        Assert.assertNotNull(thumbData);
        try {
            // attempt to read the thumbnail -- it should be a legal Jpeg file
            new JpegSegmentReader(thumbData);
        } catch (JpegProcessingException e) {
            Assert.fail("Unable to construct JpegSegmentReader from thumbnail data");
        }
    }

    @Test
    public void testWriteThumbnail() throws Exception
    {
        File file = new File("Source/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg");
        Metadata metadata = JpegMetadataReader.readMetadata(file);
        ExifDirectory exifDirectory = metadata.getOrCreateDirectory(ExifDirectory.class);
        Assert.assertTrue(exifDirectory.containsTag(ExifDirectory.TAG_THUMBNAIL_DATA));

        File thumbnailFile = File.createTempFile("thumbnail", ".jpg");
        try {
            exifDirectory.writeThumbnail(thumbnailFile.getAbsolutePath());
            Assert.assertTrue(new File(thumbnailFile.getAbsolutePath()).exists());
        } finally {
            thumbnailFile.delete();
        }
    }

    @Test
    public void testContainsThumbnail()
    {
        ExifDirectory exifDirectory = new ExifDirectory();

        Assert.assertTrue(!exifDirectory.containsThumbnail());

        exifDirectory.setObject(ExifDirectory.TAG_THUMBNAIL_DATA, "foo");

        Assert.assertTrue(exifDirectory.containsThumbnail());
    }
}

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
 * Created by dnoakes on 25-Nov-2002 20:47:31 using IntelliJ IDEA.
 */
package com.drew.metadata.exif.test;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import junit.framework.TestCase;

import java.io.File;

/**
 *
 */
public class ExifDirectoryTest extends TestCase
{
    public ExifDirectoryTest(String s)
    {
        super(s);
    }

    public void testGetDirectoryName() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        assertEquals("Exif", directory.getName());
    }

    public void testGetThumbnailData() throws Exception
    {
        File file = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        Metadata metadata = JpegMetadataReader.readMetadata(file);
        ExifDirectory exifDirectory = (ExifDirectory)metadata.getDirectory(ExifDirectory.class);
        assertTrue(exifDirectory.containsTag(ExifDirectory.TAG_THUMBNAIL_DATA));
        byte[] thumbData = exifDirectory.getThumbnailData();
        try {
            // attempt to read the thumbnail -- it should be a legal Jpeg file
            new JpegSegmentReader(thumbData);
        } catch (JpegProcessingException e) {
            fail("Unable to construct JpegSegmentReader from thumbnail data");
        }
    }

    public void testWriteThumbnail() throws Exception
    {
        File file = new File("Source/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg");
        Metadata metadata = JpegMetadataReader.readMetadata(file);
        ExifDirectory exifDirectory = (ExifDirectory)metadata.getDirectory(ExifDirectory.class);
        assertTrue(exifDirectory.containsTag(ExifDirectory.TAG_THUMBNAIL_DATA));

        File thumbnailFile = File.createTempFile("thumbnail", ".jpg");
        try {
            exifDirectory.writeThumbnail(thumbnailFile.getAbsolutePath());
            assertTrue(new File(thumbnailFile.getAbsolutePath()).exists());
        } finally {
            thumbnailFile.delete();
        }
    }

    public void testContainsThumbnail()
    {
        ExifDirectory exifDirectory = new ExifDirectory();

        assertTrue(!exifDirectory.containsThumbnail());

        exifDirectory.setObject(ExifDirectory.TAG_THUMBNAIL_DATA, "foo");

        assertTrue(exifDirectory.containsThumbnail());
    }
}

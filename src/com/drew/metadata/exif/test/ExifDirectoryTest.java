/*
 * Created by dnoakes on 25-Nov-2002 20:47:31 using IntelliJ IDEA.
 */
package com.drew.metadata.exif.test;

import com.drew.imaging.jpeg.JpegMetadataReader;
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
        File file = new File("src/com/drew/metadata/exif/test/withExif.jpg");
        Metadata metadata = JpegMetadataReader.readMetadata(file);
        ExifDirectory exifDirectory = (ExifDirectory)metadata.getDirectory(ExifDirectory.class);
        assertTrue(exifDirectory.containsTag(ExifDirectory.TAG_THUMBNAIL_DATA));
        byte[] thumbData = exifDirectory.getThumbnailData();
        JpegSegmentReader segmentReader = new JpegSegmentReader(thumbData);
        assertTrue(segmentReader.isJpeg());
    }
}

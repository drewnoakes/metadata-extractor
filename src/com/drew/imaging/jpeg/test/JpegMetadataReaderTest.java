/*
 * Created by dnoakes on 12-Nov-2002 18:52:05 using IntelliJ IDEA.
 */
package com.drew.imaging.jpeg.test;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import junit.framework.TestCase;

import java.io.File;

/**
 *
 */
public class JpegMetadataReaderTest extends TestCase
{
    public JpegMetadataReaderTest(String s)
    {
        super(s);
    }

    public void testExtractMetadata() throws Exception
    {
        File withExif = new File("src/com/drew/metadata/exif/test/withExif.jpg");
        Metadata metadata = JpegMetadataReader.readMetadata(withExif);
        assertTrue(metadata.containsDirectory(ExifDirectory.class));
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        assertEquals("80", directory.getString(ExifDirectory.TAG_ISO_EQUIVALENT));
    }
}

/*
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
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 12-Nov-2002 18:52:05 using IntelliJ IDEA.
 */
package com.drew.imaging.jpeg.test;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import junit.framework.TestCase;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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
        File withExif = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        Metadata metadata = JpegMetadataReader.readMetadata(withExif);
        assertTrue(metadata.containsDirectory(ExifDirectory.class));
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        assertEquals("80", directory.getString(ExifDirectory.TAG_ISO_EQUIVALENT));
    }

    public void testExtractMetadataUsingInputStream() throws Exception
    {
        File withExif = new File("Source/com/drew/metadata/exif/test/withExif.jpg");
        InputStream in = new BufferedInputStream(new FileInputStream((withExif)));
        Metadata metadata = JpegMetadataReader.readMetadata(in);
        assertTrue(metadata.containsDirectory(ExifDirectory.class));
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        assertEquals("80", directory.getString(ExifDirectory.TAG_ISO_EQUIVALENT));
    }
}

/*
 * ExifProcessorTest.java
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
 * Created by dnoakes on 26-Oct-2002 19:15:16 using IntelliJ IDEA.
 */
package com.drew.metadata.exif.test;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.ExifReader;
import junit.framework.TestCase;

import java.io.File;

/**
 * JUnit test case for class ExifReader.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ExifProcessorTest extends TestCase
{
    public ExifProcessorTest(String s)
    {
        super(s);
    }

    public void testLoadFujiFilmJpeg() throws Exception
    {
        String jpegWithExif = "src/com/drew/metadata/exif/test/withExif.jpg";
        Metadata metadata = new ExifReader(new File(jpegWithExif)).extract();
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        assertEquals("80", directory.getDescription(ExifDirectory.TAG_ISO_EQUIVALENT));
        // TODO decide if this should still be returned -- it was being calculated upon setting of a related tag
//      assertEquals("F9", directory.getDescription(ExifDirectory.TAG_APERTURE));
    }

    public void testLoadJpegWithoutExifData() throws Exception
    {
        String jpegNoExif = "src/com/drew/metadata/exif/test/noExif.jpg";
        Metadata metadata = new ExifReader(new File(jpegNoExif)).extract();
        assertTrue(!metadata.containsDirectory(ExifDirectory.class));
    }

    public void testLoadJpegWithBadExifData() throws Exception
    {
        // This test used to ensure an exception was thrown when loading a particular jpeg
        // The intention has since changed, and the API should only throw exceptions in completely
        // fatal situations.  Now, the Metadata object returned has no new tags.
        String jpegBadExif = "src/com/drew/metadata/exif/test/badExif.jpg"; // Exif data segment doesn't begin with 'Exif'
        Metadata metadata = new ExifReader(new File(jpegBadExif)).extract();
        assertEquals(0, metadata.getDirectory(ExifDirectory.class).getTagCount());
    }
}

/*
 * ExifExtractorTest.java
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
package com.drew.imaging.exif.test;

import com.drew.imaging.exif.*;
import junit.framework.TestCase;

import java.io.File;

/**
 * JUnit test case for class ExifExtractor.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ExifExtractorTest extends TestCase
{
    public ExifExtractorTest(String s)
    {
        super(s);
    }

    public void testLoadFujiFilmJpeg() throws Exception
    {
        ImageInfo info = new ExifExtractor(new File("src/com/drew/imaging/exif/test/withExif.jpg")).extract();
        assertTrue(info.countTags() > 0);
    }

    public void testLoadJpegWithoutExifData() throws Exception
    {
        ImageInfo info = new ExifExtractor(new File("src/com/drew/imaging/exif/test/noExif.jpg")).extract();
        assertNull(info);
    }

    public void testLoadJpegWithBadExifData() throws Exception
    {
        try {
            new ExifExtractor(new File("src/com/drew/imaging/exif/test/badExif.jpg")).extract();
            fail("expected exception for bad exif segment");
        } catch (ExifProcessingException epe) {
            assertEquals("Exif data segment doesn't begin with 'Exif'", epe.getMessage());
        }
    }
}

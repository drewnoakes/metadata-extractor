/*
 * ExifReaderTest.java
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

import com.drew.lang.Rational;
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
public class ExifReaderTest extends TestCase
{
    public ExifReaderTest(String s)
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

    public void testCrashRegressionTest() throws Exception
    {
        // this image was created via a resize in ACDSee
        // it seems to have a reference to an IFD starting outside the data segment
        // i've noticed that ACDSee reports a Comment for this image, yet ExifReader doesn't report one
        String fileName = "src/com/drew/metadata/exif/test/crash01.jpg";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        assertTrue(metadata.getDirectory(ExifDirectory.class).getTagCount() > 0);
    }

    public void testUserComment() throws Exception
    {
        String fileName = "src/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        assertEquals("Here we add a EXIF comment", directory.getString(ExifDirectory.TAG_USER_COMMENT));
    }

    public void testThumbnailOffset() throws Exception
    {
        String fileName = "src/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        assertEquals(192, directory.getInt(ExifDirectory.TAG_THUMBNAIL_OFFSET));
    }

    public void testThumbnailLength() throws Exception
    {
        String fileName = "src/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        assertEquals(2970, directory.getInt(ExifDirectory.TAG_THUMBNAIL_LENGTH));
    }

    public void testDateTime() throws Exception
    {
        String fileName = "src/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        assertEquals("2002:11:27 18:00:35", directory.getString(ExifDirectory.TAG_DATETIME));
    }

    public void testXResolution() throws Exception
    {
        String fileName = "src/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        Rational rational = directory.getRational(ExifDirectory.TAG_X_RESOLUTION);
        assertEquals(72, rational.getNumerator());
        assertEquals(1, rational.getDenominator());
    }

    public void testYResolution() throws Exception
    {
        String fileName = "src/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        Rational rational = directory.getRational(ExifDirectory.TAG_Y_RESOLUTION);
        assertEquals(72, rational.getNumerator());
        assertEquals(1, rational.getDenominator());
    }

    public void testCompression() throws Exception
    {
        String fileName = "src/com/drew/metadata/exif/test/manuallyAddedThumbnail.jpg";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        // 6 means JPEG compression
        assertEquals(6, directory.getInt(ExifDirectory.TAG_COMPRESSION));
    }
}

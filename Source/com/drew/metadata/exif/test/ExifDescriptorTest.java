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
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 26-Oct-2002 19:15:16 using IntelliJ IDEA.
 */
package com.drew.metadata.exif.test;

import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDescriptor;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.ExifReader;
import junit.framework.TestCase;

import java.io.File;

/**
 * JUnit test case for class ExifDescriptor.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ExifDescriptorTest extends TestCase
{
    public ExifDescriptorTest(String s)
    {
        super(s);
    }

    public void testXResolutionDescription() throws Exception
    {
        ExifDirectory directory = new ExifDirectory();
        directory.setRational(ExifDirectory.TAG_X_RESOLUTION, new Rational(72, 1));
        // 2 is for 'Inch'
        directory.setInt(ExifDirectory.TAG_RESOLUTION_UNIT, 2);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        assertEquals("72 dots per inch", descriptor.getDescription(ExifDirectory.TAG_X_RESOLUTION));
    }

    public void testYResolutionDescription() throws Exception
    {
        ExifDirectory directory = new ExifDirectory();
        directory.setRational(ExifDirectory.TAG_Y_RESOLUTION, new Rational(50, 1));
        // 3 is for 'cm'
        directory.setInt(ExifDirectory.TAG_RESOLUTION_UNIT, 3);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        assertEquals("50 dots per cm", descriptor.getDescription(ExifDirectory.TAG_Y_RESOLUTION));
    }

    public void testUserCommentDescription_EmptyEncoding() throws Exception
    {
        byte[] commentBytes = "\0\0\0\0\0\0\0\0This is a comment".getBytes();
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        assertEquals("This is a comment", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    public void testUserCommentDescription_AsciiHeaderExtendedAsciiEncoding() throws Exception
    {
        byte[] commentBytes = "ASCII\0\0This is a comment with extended characters זרו ֶ״ֵ".getBytes();
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        assertEquals("This is a comment with extended characters זרו ֶ״ֵ", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    public void testUserCommentDescription_AsciiHeaderAsciiEncoding() throws Exception
    {
        byte[] commentBytes = "ASCII\0\0This is a comment".getBytes();
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        assertEquals("This is a comment", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    public void testUserCommentDescription_BlankAscii() throws Exception
    {
        byte[] commentBytes = "ASCII\0\0\0          ".getBytes();
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        assertEquals("", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    public void testUserCommentDescription_ZeroLengthAscii1() throws Exception
    {
        // the 10-byte encoding region is only partially full
        byte[] commentBytes = "ASCII\0\0\0".getBytes();
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        assertEquals("ASCII", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    public void testUserCommentDescription_ZeroLengthAscii2() throws Exception
    {
        // fill the 10-byte encoding region
        byte[] commentBytes = "ASCII\0\0\0\0\0".getBytes();
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        assertEquals("", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    public void testUnicodeComment_ActualBytes() throws Exception
    {
        byte[] commentBytes = new byte[] { 85, 78, 73, 67, 79, 68, 69, 0, 84, 0, 104, 0, 105, 0, 115, 0, 32, 0, 109, 0, 97, 0, 114, 0, 109, 0, 111, 0, 116, 0, 32, 0, 105, 0, 115, 0, 32, 0, 103, 0, 101, 0, 116, 0, 116, 0, 105, 0, 110, 0, 103, 0, 32, 0, 99, 0, 108, 0, 111, 0, 115, 0, 101, 0, 46, 0, 46, 0, 46, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0 };
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        assertEquals("This marmot is getting close...", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    public void testUnicodeComment_Ascii() throws Exception
    {
        byte[] commentBytes = new byte[] { 65, 83, 67, 73, 73, 0, 0, 0, 73, 32, 97, 109, 32, 97, 32, 99, 111, 109, 109, 101, 110, 116, 46, 32, 89, 101, 121, 46, 0 };
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        assertEquals("I am a comment. Yey.", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    public void testWindowsXpFields() throws Exception
    {
        String fileName = "Source/com/drew/metadata/exif/test/windowsXpFields.jpg";
        Metadata metadata = new ExifReader(new File(fileName)).extract();
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        assertEquals("Testing artist", descriptor.getDescription(ExifDirectory.TAG_WIN_AUTHOR));
        assertEquals("Testing comments", descriptor.getDescription(ExifDirectory.TAG_WIN_COMMENT));
        assertEquals("Testing keywords", descriptor.getDescription(ExifDirectory.TAG_WIN_KEYWORDS));
        assertEquals("Testing subject", descriptor.getDescription(ExifDirectory.TAG_WIN_SUBJECT));
        assertEquals("Testing title", descriptor.getDescription(ExifDirectory.TAG_WIN_TITLE));
    }
}

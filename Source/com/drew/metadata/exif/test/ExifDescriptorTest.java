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

import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDescriptor;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.ExifReader;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

/**
 * JUnit test case for class ExifDescriptor.
 *
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ExifDescriptorTest
{
    @Test
    public void testXResolutionDescription() throws Exception
    {
        ExifDirectory directory = new ExifDirectory();
        directory.setRational(ExifDirectory.TAG_X_RESOLUTION, new Rational(72, 1));
        // 2 is for 'Inch'
        directory.setInt(ExifDirectory.TAG_RESOLUTION_UNIT, 2);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        Assert.assertEquals("72 dots per inch", descriptor.getDescription(ExifDirectory.TAG_X_RESOLUTION));
    }

    @Test
    public void testYResolutionDescription() throws Exception
    {
        ExifDirectory directory = new ExifDirectory();
        directory.setRational(ExifDirectory.TAG_Y_RESOLUTION, new Rational(50, 1));
        // 3 is for 'cm'
        directory.setInt(ExifDirectory.TAG_RESOLUTION_UNIT, 3);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        Assert.assertEquals("50 dots per cm", descriptor.getDescription(ExifDirectory.TAG_Y_RESOLUTION));
    }

    @Test
    public void testUserCommentDescription_EmptyEncoding() throws Exception
    {
        byte[] commentBytes = "\0\0\0\0\0\0\0\0This is a comment".getBytes();
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        Assert.assertEquals("This is a comment", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    @Test
    @Ignore
    public void testUserCommentDescription_AsciiHeaderExtendedAsciiEncoding() throws Exception
    {
        byte[] commentBytes = "ASCII\0\0This is a comment with extended characters ��� ���".getBytes();
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        Assert.assertEquals("This is a comment with extended characters ��� ���", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    @Test
    public void testUserCommentDescription_AsciiHeaderAsciiEncoding() throws Exception
    {
        byte[] commentBytes = "ASCII\0\0This is a comment".getBytes();
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        Assert.assertEquals("This is a comment", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    @Test
    public void testUserCommentDescription_BlankAscii() throws Exception
    {
        byte[] commentBytes = "ASCII\0\0\0          ".getBytes();
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        Assert.assertEquals("", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    @Test
    public void testUserCommentDescription_ZeroLengthAscii1() throws Exception
    {
        // the 10-byte encoding region is only partially full
        byte[] commentBytes = "ASCII\0\0\0".getBytes();
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        Assert.assertEquals("ASCII", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    @Test
    public void testUserCommentDescription_ZeroLengthAscii2() throws Exception
    {
        // fill the 10-byte encoding region
        byte[] commentBytes = "ASCII\0\0\0\0\0".getBytes();
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        Assert.assertEquals("", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    @Test
    public void testUnicodeComment_ActualBytes() throws Exception
    {
        byte[] commentBytes = new byte[] { 85, 78, 73, 67, 79, 68, 69, 0, 84, 0, 104, 0, 105, 0, 115, 0, 32, 0, 109, 0, 97, 0, 114, 0, 109, 0, 111, 0, 116, 0, 32, 0, 105, 0, 115, 0, 32, 0, 103, 0, 101, 0, 116, 0, 116, 0, 105, 0, 110, 0, 103, 0, 32, 0, 99, 0, 108, 0, 111, 0, 115, 0, 101, 0, 46, 0, 46, 0, 46, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0 };
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        Assert.assertEquals("This marmot is getting close...", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    @Test
    public void testUnicodeComment_Ascii() throws Exception
    {
        byte[] commentBytes = new byte[] { 65, 83, 67, 73, 73, 0, 0, 0, 73, 32, 97, 109, 32, 97, 32, 99, 111, 109, 109, 101, 110, 116, 46, 32, 89, 101, 121, 46, 0 };
        ExifDirectory directory = new ExifDirectory();
        directory.setByteArray(ExifDirectory.TAG_USER_COMMENT, commentBytes);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        Assert.assertEquals("I am a comment. Yey.", descriptor.getDescription(ExifDirectory.TAG_USER_COMMENT));
    }

    @Test
    public void testWindowsXpFields() throws Exception
    {
        String fileName = "Source/com/drew/metadata/exif/test/windowsXpFields.jpg";
        Metadata metadata = new Metadata();
        new ExifReader(new File(fileName)).extract(metadata);
        Directory directory = metadata.getDirectory(ExifDirectory.class);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        Assert.assertEquals("Testing artist", descriptor.getDescription(ExifDirectory.TAG_WIN_AUTHOR));
        Assert.assertEquals("Testing comments", descriptor.getDescription(ExifDirectory.TAG_WIN_COMMENT));
        Assert.assertEquals("Testing keywords", descriptor.getDescription(ExifDirectory.TAG_WIN_KEYWORDS));
        Assert.assertEquals("Testing subject", descriptor.getDescription(ExifDirectory.TAG_WIN_SUBJECT));
        Assert.assertEquals("Testing title", descriptor.getDescription(ExifDirectory.TAG_WIN_TITLE));
    }
}

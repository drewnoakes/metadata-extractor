/*
 * Copyright 2002-2015 Drew Noakes
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
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.metadata.exif;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * JUnit test case for class ExifSubIFDDescriptor.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ExifSubIFDDescriptorTest
{
    @Test
    public void testUserCommentDescription_EmptyEncoding() throws Exception
    {
        byte[] commentBytes = "\0\0\0\0\0\0\0\0This is a comment".getBytes();
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setByteArray(ExifSubIFDDirectory.TAG_USER_COMMENT, commentBytes);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
        assertEquals("This is a comment", descriptor.getDescription(ExifSubIFDDirectory.TAG_USER_COMMENT));
    }

    @Test
    public void testUserCommentDescription_AsciiHeaderAsciiEncoding() throws Exception
    {
        byte[] commentBytes = "ASCII\0\0This is a comment".getBytes();
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setByteArray(ExifSubIFDDirectory.TAG_USER_COMMENT, commentBytes);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
        assertEquals("This is a comment", descriptor.getDescription(ExifSubIFDDirectory.TAG_USER_COMMENT));
    }

    @Test
    public void testUserCommentDescription_BlankAscii() throws Exception
    {
        byte[] commentBytes = "ASCII\0\0\0          ".getBytes();
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setByteArray(ExifSubIFDDirectory.TAG_USER_COMMENT, commentBytes);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
        assertEquals("", descriptor.getDescription(ExifSubIFDDirectory.TAG_USER_COMMENT));
    }

    @Test
    public void testUserCommentDescription_ZeroLengthAscii1() throws Exception
    {
        // the 10-byte encoding region is only partially full
        byte[] commentBytes = "ASCII\0\0\0".getBytes();
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setByteArray(ExifSubIFDDirectory.TAG_USER_COMMENT, commentBytes);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
        assertEquals("ASCII", descriptor.getDescription(ExifSubIFDDirectory.TAG_USER_COMMENT));
    }

    @Test
    public void testUserCommentDescription_ZeroLengthAscii2() throws Exception
    {
        // fill the 10-byte encoding region
        byte[] commentBytes = "ASCII\0\0\0\0\0".getBytes();
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setByteArray(ExifSubIFDDirectory.TAG_USER_COMMENT, commentBytes);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
        assertEquals("", descriptor.getDescription(ExifSubIFDDirectory.TAG_USER_COMMENT));
    }

    @Test
    public void testUnicodeComment_ActualBytes() throws Exception
    {
        byte[] commentBytes = new byte[] { 85, 78, 73, 67, 79, 68, 69, 0, 84, 0, 104, 0, 105, 0, 115, 0, 32, 0, 109, 0, 97, 0, 114, 0, 109, 0, 111, 0, 116, 0, 32, 0, 105, 0, 115, 0, 32, 0, 103, 0, 101, 0, 116, 0, 116, 0, 105, 0, 110, 0, 103, 0, 32, 0, 99, 0, 108, 0, 111, 0, 115, 0, 101, 0, 46, 0, 46, 0, 46, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0 };
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setByteArray(ExifSubIFDDirectory.TAG_USER_COMMENT, commentBytes);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
        assertEquals("This marmot is getting close...", descriptor.getDescription(ExifSubIFDDirectory.TAG_USER_COMMENT));
    }

    @Test
    public void testUnicodeComment_Ascii() throws Exception
    {
        byte[] commentBytes = new byte[] { 65, 83, 67, 73, 73, 0, 0, 0, 73, 32, 97, 109, 32, 97, 32, 99, 111, 109, 109, 101, 110, 116, 46, 32, 89, 101, 121, 46, 0 };
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setByteArray(ExifSubIFDDirectory.TAG_USER_COMMENT, commentBytes);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
        assertEquals("I am a comment. Yey.", descriptor.getDescription(ExifSubIFDDirectory.TAG_USER_COMMENT));
    }
}

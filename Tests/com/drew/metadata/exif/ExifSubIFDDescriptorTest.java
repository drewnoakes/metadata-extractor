/*
 * Copyright 2002-2019 Drew Noakes and contributors
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

import com.drew.lang.Rational;
import org.junit.Test;

import static com.drew.metadata.exif.ExifSubIFDDirectory.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * JUnit test case for class ExifSubIFDDescriptor.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ExifSubIFDDescriptorTest
{
    @Test
    public void testExposureTimeDescription_Subsecond()
    {
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        Rational[] values = { new Rational(1, 25), new Rational(4, 100), new Rational(1, 2), new Rational(1, 125), new Rational(3, 10) };
        String[] descriptions = { "1/25 sec", "1/25 sec", "1/2 sec", "1/125 sec", "3/10 sec" };
        for (int i = 0; i < values.length; i++) {
            directory.setRational(TAG_EXPOSURE_TIME, values[i]);
            assertEquals(descriptions[i], directory.getDescription(TAG_EXPOSURE_TIME));
        }
    }

    @Test
    public void testExposureTimeDescription_OneSecondOrLonger()
    {
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setRational(TAG_EXPOSURE_TIME, new Rational(1, 1));
        assertEquals("1 sec", directory.getDescription(TAG_EXPOSURE_TIME));
        directory.setRational(TAG_EXPOSURE_TIME, new Rational(2, 1));
        assertEquals("2 sec", directory.getDescription(TAG_EXPOSURE_TIME));
        directory.setRational(TAG_EXPOSURE_TIME, new Rational(3, 2));
        assertEquals("1.5 sec", directory.getDescription(TAG_EXPOSURE_TIME));
    }

    @Test
    public void testExposureTimeDescription_Missing()
    {
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        assertNull(directory.getDescription(TAG_EXPOSURE_TIME));
    }

    @Test
    public void testExposureTimeRawString()
    {
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setRational(TAG_EXPOSURE_TIME, new Rational(1, 25));
        assertEquals("0.04", directory.getString(TAG_EXPOSURE_TIME));
    }

    @Test
    public void testLensSpecificationDescription_DecimalFocalLength()
    {
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setRationalArray(TAG_LENS_SPECIFICATION, new Rational[] {
            new Rational(17, 4), new Rational(17, 4), new Rational(9, 5), new Rational(9, 5)
        });
        assertEquals("4.25mm f/1.8", directory.getDescription(TAG_LENS_SPECIFICATION));
    }

    @Test
    public void testUserCommentDescription_EmptyEncoding() throws Exception
    {
        byte[] commentBytes = "\0\0\0\0\0\0\0\0This is a comment".getBytes();
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setByteArray(TAG_USER_COMMENT, commentBytes);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
        assertEquals("This is a comment", descriptor.getDescription(TAG_USER_COMMENT));
    }

    @Test
    public void testUserCommentDescription_AsciiHeaderAsciiEncoding() throws Exception
    {
        byte[] commentBytes = "ASCII\0\0This is a comment".getBytes();
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setByteArray(TAG_USER_COMMENT, commentBytes);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
        assertEquals("This is a comment", descriptor.getDescription(TAG_USER_COMMENT));
    }

    @Test
    public void testUserCommentDescription_BlankAscii() throws Exception
    {
        byte[] commentBytes = "ASCII\0\0\0          ".getBytes();
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setByteArray(TAG_USER_COMMENT, commentBytes);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
        assertEquals("", descriptor.getDescription(TAG_USER_COMMENT));
    }

    @Test
    public void testUserCommentDescription_ZeroLengthAscii1() throws Exception
    {
        // the 10-byte encoding region is only partially full
        byte[] commentBytes = "ASCII\0\0\0".getBytes();
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setByteArray(TAG_USER_COMMENT, commentBytes);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
        assertEquals("ASCII", descriptor.getDescription(TAG_USER_COMMENT));
    }

    @Test
    public void testUserCommentDescription_ZeroLengthAscii2() throws Exception
    {
        // fill the 10-byte encoding region
        byte[] commentBytes = "ASCII\0\0\0\0\0".getBytes();
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setByteArray(TAG_USER_COMMENT, commentBytes);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
        assertEquals("", descriptor.getDescription(TAG_USER_COMMENT));
    }

    @Test
    public void testUnicodeComment_ActualBytes() throws Exception
    {
        byte[] commentBytes = new byte[] { 85, 78, 73, 67, 79, 68, 69, 0, 84, 0, 104, 0, 105, 0, 115, 0, 32, 0, 109, 0, 97, 0, 114, 0, 109, 0, 111, 0, 116, 0, 32, 0, 105, 0, 115, 0, 32, 0, 103, 0, 101, 0, 116, 0, 116, 0, 105, 0, 110, 0, 103, 0, 32, 0, 99, 0, 108, 0, 111, 0, 115, 0, 101, 0, 46, 0, 46, 0, 46, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0, 32, 0 };
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setByteArray(TAG_USER_COMMENT, commentBytes);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
        assertEquals("This marmot is getting close...", descriptor.getDescription(TAG_USER_COMMENT));
    }

    @Test
    public void testUnicodeComment_Ascii() throws Exception
    {
        byte[] commentBytes = new byte[] { 65, 83, 67, 73, 73, 0, 0, 0, 73, 32, 97, 109, 32, 97, 32, 99, 111, 109, 109, 101, 110, 116, 46, 32, 89, 101, 121, 46, 0 };
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.setByteArray(TAG_USER_COMMENT, commentBytes);
        ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
        assertEquals("I am a comment. Yey.", descriptor.getDescription(TAG_USER_COMMENT));
    }
}

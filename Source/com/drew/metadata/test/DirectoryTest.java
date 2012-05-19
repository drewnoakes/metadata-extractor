/*
 * Copyright 2002-2012 Drew Noakes
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
package com.drew.metadata.test;

import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.GregorianCalendar;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class DirectoryTest
{
    // TODO write tests to validate type conversions from all underlying types

    private Directory _directory;

    @Before public void setup()
    {
        _directory = new MockDirectory();
    }

    @Test public void testSetAndGetMultipleTagsInSingleDirectory() throws Exception
    {
        _directory.setString(ExifSubIFDDirectory.TAG_APERTURE, "TAG_APERTURE");
        _directory.setString(ExifSubIFDDirectory.TAG_BATTERY_LEVEL, "TAG_BATTERY_LEVEL");
        Assert.assertEquals("TAG_APERTURE", _directory.getString(ExifSubIFDDirectory.TAG_APERTURE));
        Assert.assertEquals("TAG_BATTERY_LEVEL", _directory.getString(ExifSubIFDDirectory.TAG_BATTERY_LEVEL));
    }

    @Test public void testSetSameTagMultipleTimesOverwritesValue() throws Exception
    {
        _directory.setInt(ExifSubIFDDirectory.TAG_APERTURE, 1);
        _directory.setInt(ExifSubIFDDirectory.TAG_APERTURE, 2);
        Assert.assertEquals(2, _directory.getInt(ExifSubIFDDirectory.TAG_APERTURE));
    }

    @Test
    public void testUnderlyingInt() throws Exception
    {
        int value = 123;
        int tagType = 321;
        _directory.setInt(tagType, value);

        Assert.assertEquals(value, _directory.getInt(tagType));
        Assert.assertEquals(Integer.valueOf(value), _directory.getInteger(tagType));
        Assert.assertEquals((float)value, _directory.getFloat(tagType), 0.00001);
        Assert.assertEquals((double)value, _directory.getDouble(tagType), 0.00001);
        Assert.assertEquals((long)value, _directory.getLong(tagType));
        Assert.assertEquals(Integer.toString(value), _directory.getString(tagType));
        Assert.assertEquals(new Rational(value, 1), _directory.getRational(tagType));
        Assert.assertArrayEquals(new int[] { value }, _directory.getIntArray(tagType));
        Assert.assertArrayEquals(new byte[] { (byte)value }, _directory.getByteArray(tagType));
    }

    @Test public void testSetAndGetIntArray() throws Exception
    {
        int[] inputValues = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int tagType = 123;
        _directory.setIntArray(tagType, inputValues);
        int[] outputValues = _directory.getIntArray(tagType);
        Assert.assertNotNull(outputValues);
        Assert.assertEquals(inputValues.length, outputValues.length);
        for (int i = 0; i < inputValues.length; i++) {
            int inputValue = inputValues[i];
            int outputValue = outputValues[i];
            Assert.assertEquals(inputValue, outputValue);
        }
        Assert.assertEquals(inputValues, _directory.getIntArray(tagType));
        StringBuilder outputString = new StringBuilder();
        for (int i = 0; i < inputValues.length; i++) {
            int inputValue = inputValues[i];
            if (i > 0) {
                outputString.append(' ');
            }
            outputString.append(inputValue);
        }
        Assert.assertEquals(outputString.toString(), _directory.getString(tagType));
    }

    @Test
    public void testSetStringAndGetDate() throws Exception
    {
        String date1 = "2002:01:30 24:59:59";
        String date2 = "2002:01:30 24:59";
        String date3 = "2002-01-30 24:59:59";
        String date4 = "2002-01-30 24:59";
        _directory.setString(1, date1);
        _directory.setString(2, date2);
        _directory.setString(3, date3);
        _directory.setString(4, date4);
        Assert.assertEquals(date1, _directory.getString(1));
        Assert.assertEquals(new GregorianCalendar(2002, GregorianCalendar.JANUARY, 30, 24, 59, 59).getTime(), _directory.getDate(1));
        Assert.assertEquals(new GregorianCalendar(2002, GregorianCalendar.JANUARY, 30, 24, 59, 0).getTime(), _directory.getDate(2));
        Assert.assertEquals(new GregorianCalendar(2002, GregorianCalendar.JANUARY, 30, 24, 59, 59).getTime(), _directory.getDate(3));
        Assert.assertEquals(new GregorianCalendar(2002, GregorianCalendar.JANUARY, 30, 24, 59, 0).getTime(), _directory.getDate(4));
    }

    @Test
    public void testSetIntArrayGetByteArray() throws Exception
    {
        int[] ints = {1, 2, 3, 4, 5};
        _directory.setIntArray(1, ints);

        byte[] bytes = _directory.getByteArray(1);
        Assert.assertNotNull(bytes);
        Assert.assertEquals(ints.length, bytes.length);
        Assert.assertEquals(1, bytes[0]);
    }

    @Test
    public void testSetStringGetInt() throws Exception
    {
        byte[] bytes = { 0x01, 0x02, 0x03 };
        _directory.setString(1, new String(bytes));
        Assert.assertEquals(0x010203, _directory.getInt(1));
    }

    @Test
    public void testContainsTag() throws Exception
    {
        Assert.assertFalse(_directory.containsTag(ExifSubIFDDirectory.TAG_APERTURE));
        _directory.setString(ExifSubIFDDirectory.TAG_APERTURE, "Tag Value");
        Assert.assertTrue(_directory.containsTag(ExifSubIFDDirectory.TAG_APERTURE));
    }

    @Test
    public void testGetNonExistentTagIsNullForAllTypes() throws Exception
    {
        Assert.assertNull(_directory.getString(ExifSubIFDDirectory.TAG_APERTURE));
        Assert.assertNull(_directory.getInteger(ExifSubIFDDirectory.TAG_APERTURE));
        Assert.assertNull(_directory.getDoubleObject(ExifSubIFDDirectory.TAG_APERTURE));
        Assert.assertNull(_directory.getFloatObject(ExifSubIFDDirectory.TAG_APERTURE));
        Assert.assertNull(_directory.getByteArray(ExifSubIFDDirectory.TAG_APERTURE));
        Assert.assertNull(_directory.getDate(ExifSubIFDDirectory.TAG_APERTURE));
        Assert.assertNull(_directory.getIntArray(ExifSubIFDDirectory.TAG_APERTURE));
        Assert.assertNull(_directory.getLongObject(ExifSubIFDDirectory.TAG_APERTURE));
        Assert.assertNull(_directory.getObject(ExifSubIFDDirectory.TAG_APERTURE));
        Assert.assertNull(_directory.getRational(ExifSubIFDDirectory.TAG_APERTURE));
        Assert.assertNull(_directory.getRationalArray(ExifSubIFDDirectory.TAG_APERTURE));
        Assert.assertNull(_directory.getStringArray(ExifSubIFDDirectory.TAG_APERTURE));
    }
}

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
package com.drew.metadata.test;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import org.junit.Assert;
import org.junit.Test;

import java.util.GregorianCalendar;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class DirectoryTest
{
    @Test
    public void testSetAndGetInt() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getOrCreateDirectory(MockDirectory.class);
        int value = 321;
        int tagType = 123;
        directory.setInt(tagType, value);
        Assert.assertEquals(value, directory.getInt(tagType));
        Assert.assertEquals(Integer.toString(value), directory.getString(tagType));
    }

    @Test
    public void testSetAndGetIntArray() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getOrCreateDirectory(MockDirectory.class);
        int[] inputValues = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int tagType = 123;
        directory.setIntArray(tagType, inputValues);
        int[] outputValues = directory.getIntArray(tagType);
        Assert.assertNotNull(outputValues);
        Assert.assertEquals(inputValues.length, outputValues.length);
        for (int i = 0; i < inputValues.length; i++) {
            int inputValue = inputValues[i];
            int outputValue = outputValues[i];
            Assert.assertEquals(inputValue, outputValue);
        }
        Assert.assertEquals(inputValues, directory.getIntArray(tagType));
        StringBuilder outputString = new StringBuilder();
        for (int i = 0; i < inputValues.length; i++) {
            int inputValue = inputValues[i];
            if (i > 0) {
                outputString.append(' ');
            }
            outputString.append(inputValue);
        }
        Assert.assertEquals(outputString.toString(), directory.getString(tagType));
    }

    @Test
    public void testSetStringAndGetDate() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getOrCreateDirectory(MockDirectory.class);
        String date1 = "2002:01:30 24:59:59";
        String date2 = "2002:01:30 24:59";
        String date3 = "2002-01-30 24:59:59";
        String date4 = "2002-01-30 24:59";
        directory.setString(1, date1);
        directory.setString(2, date2);
        directory.setString(3, date3);
        directory.setString(4, date4);
        Assert.assertEquals(date1, directory.getString(1));
        Assert.assertEquals(new GregorianCalendar(2002, GregorianCalendar.JANUARY, 30, 24, 59, 59).getTime(), directory.getDate(1));
        Assert.assertEquals(new GregorianCalendar(2002, GregorianCalendar.JANUARY, 30, 24, 59, 0).getTime(), directory.getDate(2));
        Assert.assertEquals(new GregorianCalendar(2002, GregorianCalendar.JANUARY, 30, 24, 59, 59).getTime(), directory.getDate(3));
        Assert.assertEquals(new GregorianCalendar(2002, GregorianCalendar.JANUARY, 30, 24, 59, 0).getTime(), directory.getDate(4));
    }

    @Test
    public void testSetIntArrayGetByteArray() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getOrCreateDirectory(MockDirectory.class);
        int[] ints = {1, 2, 3, 4, 5};
        directory.setIntArray(1, ints);
        final byte[] bytes = directory.getByteArray(1);
        Assert.assertNotNull(bytes);
        Assert.assertEquals(ints.length, bytes.length);
        Assert.assertEquals(1, bytes[0]);
    }

    @Test
    public void testSetStringGetInt() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getOrCreateDirectory(MockDirectory.class);
        byte[] bytes = { 0x01, 0x02, 0x03 };
        directory.setString(1, new String(bytes));
        Assert.assertEquals(0x010203, directory.getInt(1));
    }
}
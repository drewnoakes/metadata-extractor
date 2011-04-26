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
import junit.framework.TestCase;

import java.util.GregorianCalendar;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class DirectoryTest extends TestCase
{
    public DirectoryTest(String s)
    {
        super(s);
    }

    public void testSetAndGetInt() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getDirectory(MockDirectory.class);
        int value = 321;
        int tagType = 123;
        directory.setInt(tagType, value);
        assertEquals(value, directory.getInt(tagType));
        assertEquals(Integer.toString(value), directory.getString(tagType));
    }

    public void testSetAndGetIntArray() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getDirectory(MockDirectory.class);
        int[] inputValues = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int tagType = 123;
        directory.setIntArray(tagType, inputValues);
        int[] outputValues = directory.getIntArray(tagType);
        assertEquals(inputValues.length, outputValues.length);
        for (int i = 0; i < inputValues.length; i++) {
            int inputValue = inputValues[i];
            int outputValue = outputValues[i];
            assertEquals(inputValue, outputValue);
        }
        assertEquals(inputValues, directory.getIntArray(tagType));
        StringBuffer outputString = new StringBuffer();
        for (int i = 0; i < inputValues.length; i++) {
            int inputValue = inputValues[i];
            if (i > 0) {
                outputString.append(' ');
            }
            outputString.append(inputValue);
        }
        assertEquals(outputString.toString(), directory.getString(tagType));
    }

    public void testSetStringAndGetDate() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getDirectory(MockDirectory.class);
        String date1 = "2002:01:30 24:59:59";
        String date2 = "2002:01:30 24:59";
        String date3 = "2002-01-30 24:59:59";
        String date4 = "2002-01-30 24:59";
        directory.setString(1, date1);
        directory.setString(2, date2);
        directory.setString(3, date3);
        directory.setString(4, date4);
        assertEquals(date1, directory.getString(1));
        assertEquals(new GregorianCalendar(2002, GregorianCalendar.JANUARY, 30, 24, 59, 59).getTime(), directory.getDate(1));
        assertEquals(new GregorianCalendar(2002, GregorianCalendar.JANUARY, 30, 24, 59, 0).getTime(), directory.getDate(2));
        assertEquals(new GregorianCalendar(2002, GregorianCalendar.JANUARY, 30, 24, 59, 59).getTime(), directory.getDate(3));
        assertEquals(new GregorianCalendar(2002, GregorianCalendar.JANUARY, 30, 24, 59, 0).getTime(), directory.getDate(4));
    }

    public void testSetIntArrayGetByteArray() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getDirectory(MockDirectory.class);
        int[] ints = {1, 2, 3, 4, 5};
        directory.setIntArray(1, ints);
        assertEquals(ints.length, directory.getByteArray(1).length);
        assertEquals(1, directory.getByteArray(1)[0]);
    }

    public void testSetStringGetInt() throws Exception
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getDirectory(MockDirectory.class);
        byte[] bytes = { 0x01, 0x02, 0x03 };
        directory.setString(1, new String(bytes));
        assertEquals(0x010203, directory.getInt(1));
    }
}
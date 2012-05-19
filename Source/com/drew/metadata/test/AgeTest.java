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

import com.drew.metadata.Age;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class AgeTest
{
    @Test
    public void testParse()
    {
        Age age = Age.fromPanasonicString("0031:07:15 00:00:00");
        Assert.assertNotNull(age);
        Assert.assertEquals(31, age.getYears());
        Assert.assertEquals(7, age.getMonths());
        Assert.assertEquals(15, age.getDays());
        Assert.assertEquals(0, age.getHours());
        Assert.assertEquals(0, age.getMinutes());
        Assert.assertEquals(0, age.getSeconds());
        Assert.assertEquals("0031:07:15 00:00:00", age.toString());
        Assert.assertEquals("31 years 7 months 15 days", age.toFriendlyString());
    }

    @SuppressWarnings({ "ObjectEqualsNull", "EqualsBetweenInconvertibleTypes", "NullableProblems" })
    @Test
    public void testEqualsAndHashCode()
    {
        Age age1 = new Age(10, 11, 12, 13, 14, 15);
        Age age2 = new Age(10, 11, 12, 13, 14, 15);
        Age age3 = new Age(0, 0, 0, 0, 0, 0);

        Assert.assertEquals(age1, age1);
        Assert.assertEquals(age1, age2);
        Assert.assertEquals(age2, age1);

        Assert.assertTrue(age1.equals(age1));
        Assert.assertTrue(age1.equals(age2));
        Assert.assertFalse(age1.equals(age3));
        Assert.assertFalse(age1.equals(null));
        Assert.assertFalse(age1.equals("Hello"));

        Assert.assertEquals(age1.hashCode(), age1.hashCode());
        Assert.assertEquals(age1.hashCode(), age2.hashCode());
        Assert.assertFalse(age1.hashCode()==age3.hashCode());
    }
}

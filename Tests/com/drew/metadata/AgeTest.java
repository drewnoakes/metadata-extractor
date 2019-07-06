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

package com.drew.metadata;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class AgeTest
{
    @Test
    public void testParse()
    {
        Age age = Age.fromPanasonicString("0031:07:15 00:00:00");
        assertNotNull(age);
        assertEquals(31, age.getYears());
        assertEquals(7, age.getMonths());
        assertEquals(15, age.getDays());
        assertEquals(0, age.getHours());
        assertEquals(0, age.getMinutes());
        assertEquals(0, age.getSeconds());
        assertEquals("0031:07:15 00:00:00", age.toString());
        assertEquals("31 years 7 months 15 days", age.toFriendlyString());
    }

    @SuppressWarnings({ "ObjectEqualsNull", "EqualsBetweenInconvertibleTypes", "NullableProblems" })
    @Test
    public void testEqualsAndHashCode()
    {
        Age age1 = new Age(10, 11, 12, 13, 14, 15);
        Age age2 = new Age(10, 11, 12, 13, 14, 15);
        Age age3 = new Age(0, 0, 0, 0, 0, 0);

        assertEquals(age1, age1);
        assertEquals(age1, age2);
        assertEquals(age2, age1);

        assertTrue(age1.equals(age1));
        assertTrue(age1.equals(age2));
        assertFalse(age1.equals(age3));
        assertFalse(age1.equals(null));
        assertFalse(age1.equals("Hello"));

        assertEquals(age1.hashCode(), age1.hashCode());
        assertEquals(age1.hashCode(), age2.hashCode());
        assertFalse(age1.hashCode() == age3.hashCode());
    }
}

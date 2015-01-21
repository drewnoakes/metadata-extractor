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
package com.drew.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class RationalTest
{
    @Test
    public void testCreateRational() throws Exception
    {
        Rational rational = new Rational(1, 3);
        assertEquals(1, rational.getNumerator());
        assertEquals(3, rational.getDenominator());
        assertEquals(1d / 3d, rational.doubleValue(), 0.0001);
    }

    @Test
    public void testToString() throws Exception
    {
        Rational rational = new Rational(1, 3);
        assertEquals("1/3", rational.toString());
    }

    @Test
    public void testToSimpleString() throws Exception
    {
        Rational third1 = new Rational(1, 3);
        Rational third2 = new Rational(2, 6);
        assertEquals("1/3", third1.toSimpleString(true));
        assertEquals("1/3", third2.toSimpleString(true));
        assertEquals(third1, third2);

        Rational twoThirds = new Rational(10, 15);
        assertEquals("2/3", twoThirds.toSimpleString(true));

        Rational two = new Rational(10, 5);
        assertTrue(two.isInteger());
        assertEquals("2", two.toSimpleString(true));
        assertEquals("2", two.toSimpleString(false));

        Rational twoFifths = new Rational(4, 10);
        assertEquals("0.4", twoFifths.toSimpleString(true));
        assertEquals("2/5", twoFifths.toSimpleString(false));

        Rational threeEighths = new Rational(3, 8);
        assertEquals("3/8", threeEighths.toSimpleString(true));

        Rational zero = new Rational(0, 8);
        assertTrue(zero.isInteger());
        assertEquals("0", zero.toSimpleString(true));
        assertEquals("0", zero.toSimpleString(false));

        zero = new Rational(0, 0);
        assertTrue(zero.isInteger());
        assertEquals("0", zero.toSimpleString(true));
        assertEquals("0", zero.toSimpleString(false));

        // not sure this is a nice presentation of rationals.  won't implement it for now.
//        Rational twoAndAHalf = new Rational(10,4);
//        assertEquals("2 1/2", twoAndAHalf.toSimpleString());
    }

    @Test
    public void testGetReciprocal() throws Exception
    {
        Rational rational = new Rational(1, 3);
        Rational reciprocal = rational.getReciprocal();
        assertEquals("new rational should be reciprocal", new Rational(3, 1), reciprocal);
        assertEquals("original reciprocal should remain unchanged", new Rational(1, 3), rational);
    }

    @Test
    public void testZeroOverZero() throws Exception
    {
        assertEquals(new Rational(0, 0), new Rational(0, 0).getReciprocal());
        assertEquals(0.0d, new Rational(0, 0).doubleValue(), 0.000000001);
        assertEquals(0, new Rational(0, 0).byteValue());
        assertEquals(0.0f, new Rational(0, 0).floatValue(), 0.000000001f);
        assertEquals(0, new Rational(0, 0).intValue());
        assertEquals(0L, new Rational(0, 0).longValue());
        assertTrue(new Rational(0, 0).isInteger());
    }
}

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
package com.drew.lang;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author  Drew Noakes http://drewnoakes.com
 */
public class RationalTest
{
    @Test
    public void testCreateRational() throws Exception
    {
        Rational rational = new Rational(1, 3);
        Assert.assertEquals(1, rational.getNumerator());
        Assert.assertEquals(3, rational.getDenominator());
        Assert.assertEquals(1d / 3d, rational.doubleValue(), 0.0001);
    }

    @Test
    public void testToString() throws Exception
    {
        Rational rational = new Rational(1, 3);
        Assert.assertEquals("1/3", rational.toString());
    }

    @Test
    public void testToSimpleString() throws Exception
    {
        Rational third1 = new Rational(1, 3);
        Rational third2 = new Rational(2, 6);
        Assert.assertEquals("1/3", third1.toSimpleString(true));
        Assert.assertEquals("1/3", third2.toSimpleString(true));
        Assert.assertEquals(third1, third2);

        Rational twoThirds = new Rational(10, 15);
        Assert.assertEquals("2/3", twoThirds.toSimpleString(true));

        Rational two = new Rational(10, 5);
        Assert.assertTrue(two.isInteger());
        Assert.assertEquals("2", two.toSimpleString(true));
        Assert.assertEquals("2", two.toSimpleString(false));

        Rational twoFifths = new Rational(4, 10);
        Assert.assertEquals("0.4", twoFifths.toSimpleString(true));
        Assert.assertEquals("2/5", twoFifths.toSimpleString(false));

        Rational threeEigths = new Rational(3, 8);
        Assert.assertEquals("3/8", threeEigths.toSimpleString(true));

        Rational zero = new Rational(0, 8);
        Assert.assertTrue(zero.isInteger());
        Assert.assertEquals("0", zero.toSimpleString(true));
        Assert.assertEquals("0", zero.toSimpleString(false));

        zero = new Rational(0, 0);
        Assert.assertTrue(zero.isInteger());
        Assert.assertEquals("0", zero.toSimpleString(true));
        Assert.assertEquals("0", zero.toSimpleString(false));

        // not sure this is a nice presentation of rationals.  won't implement it for now.
//        Rational twoAndAHalf = new Rational(10,4);
//        assertEquals("2 1/2", twoAndAHalf.toSimpleString());
    }

    @Test
    public void testGetReciprocal() throws Exception
    {
        Rational rational = new Rational(1, 3);
        Rational reciprocal = rational.getReciprocal();
        Assert.assertEquals("new rational should be reciprocal", new Rational(3, 1), reciprocal);
        Assert.assertEquals("original reciprocal should remain unchanged", new Rational(1, 3), rational);
    }
}

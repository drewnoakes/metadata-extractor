/*
 * RationalTest.java
 *
 * Test class written by Drew Noakes.
 *
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 26-Oct-2002 16:24:43 using IntelliJ IDEA.
 */
package com.drew.lang.test;

import com.drew.lang.Rational;
import junit.framework.TestCase;

/**
 *
 * @author  Drew Noakes http://drewnoakes.com
 */
public class RationalTest extends TestCase
{
    public RationalTest(String s)
    {
        super(s);
    }

    public void testCreateRational() throws Exception
    {
        Rational rational = new Rational(1, 3);
        assertEquals(1, rational.getNumerator());
        assertEquals(3, rational.getDenominator());
        assertEquals(new Double(1d / 3d), new Double(rational.doubleValue()));
    }

    public void testToString() throws Exception
    {
        Rational rational = new Rational(1, 3);
        assertEquals("1/3", rational.toString());
    }

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

        Rational threeEigths = new Rational(3, 8);
        assertEquals("3/8", threeEigths.toSimpleString(true));

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

    public void testGetReciprocal() throws Exception
    {
        Rational rational = new Rational(1, 3);
        Rational reciprocal = rational.getReciprocal();
        assertEquals("new rational should be reciprocal", new Rational(3, 1), reciprocal);
        assertEquals("origianl reciprocal should remain unchanged", new Rational(1, 3), rational);
    }
}

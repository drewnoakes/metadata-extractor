/*
 * Rational.java
 *
 * This class is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the 
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  Similarly, I release this Java version under the
 * same license, though I do ask that you leave this header in tact.
 * 
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   drew.noakes@drewnoakes.com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created on 6 May 2002, 18:06
 */

package com.drew.imaging.exif;

/**
 * Immutable class for holding a rational number without loss of precision.  Provides
 * a familiar representation via toString() in form <code>numerator/denominator</code>.
 * <p>
 * @author Drew Noakes drew.noakes@drewnoakes.com
 */
public class Rational extends java.lang.Number implements java.io.Serializable
{
    /**
     * Holds the numerator.
     */
    private final int numerator;

    /**
     * Holds the denominator.
     */
    private final int denominator;
    
    /**
     * Creates a new instance of Rational.  Rational objects are immutable, so
     * once you've set your numerator and denominator values here, you're stuck
     * with them!
     */
    public Rational(int numerator, int denominator)
    {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /**
     * Returns the value of the specified number as a <code>double</code>.
     * This may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>double</code>.
     */
    public double doubleValue()
    {
        return (double)numerator / (double)denominator;
    }
    
    /**
     * Returns the value of the specified number as a <code>float</code>.
     * This may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>float</code>.
     */
    public float floatValue()
    {
        return (float)numerator / (float)denominator;
    }
    
    /**
     * Returns the value of the specified number as a <code>byte</code>.
     * This may involve rounding or truncation.  This implementation simply
     * casts the result of <code>doubleValue()</code> to <code>byte</code>.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>byte</code>.
     */
    public final byte byteValue()
    {
        return (byte)doubleValue();
    }
    
    /**
     * Returns the value of the specified number as an <code>int</code>.
     * This may involve rounding or truncation.  This implementation simply
     * casts the result of <code>doubleValue()</code> to <code>int</code>.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>int</code>.
     */
    public final int intValue()
    {
        return (int)doubleValue();
    }
    
    /**
     * Returns the value of the specified number as a <code>long</code>.
     * This may involve rounding or truncation.  This implementation simply
     * casts the result of <code>doubleValue()</code> to <code>long</code>.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>long</code>.
     */
    public final long longValue()
    {
        return (long)doubleValue();
    }
    
    /**
     * Returns the value of the specified number as a <code>short</code>.
     * This may involve rounding or truncation.  This implementation simply
     * casts the result of <code>doubleValue()</code> to <code>short</code>.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type <code>short</code>.
     */
    public final short shortValue()
    {
        return (short)doubleValue();
    }
    
    /**
     * Returns a string representation of the object of form <code>numerator/denominator</code>.
     *
     * @return  a string representation of the object.
     */
    public String toString()
    {
        return numerator+"/"+denominator;
    }
    
    /**
     * Returns the denominator.
     */
    public final int getDenominator()
    {
        return this.denominator;
    }
    
    /**
     * Returns the numerator.
     */
    public final int getNumerator()
    {
        return this.numerator;
    }
}

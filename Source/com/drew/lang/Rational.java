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

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.io.Serializable;

/**
 * Immutable class for holding a rational number without loss of precision.  Provides
 * a familiar representation via toString() in form <code>numerator/denominator</code>.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class Rational extends java.lang.Number implements Serializable
{
    private static final long serialVersionUID = 510688928138848770L;

    /** Holds the numerator. */
    private final long _numerator;

    /** Holds the denominator. */
    private final long _denominator;

    /**
     * Creates a new instance of Rational.  Rational objects are immutable, so
     * once you've set your numerator and denominator values here, you're stuck
     * with them!
     */
    public Rational(long numerator, long denominator)
    {
        _numerator = numerator;
        _denominator = denominator;
    }

    /**
     * Returns the value of the specified number as a <code>double</code>.
     * This may involve rounding.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>double</code>.
     */
    public double doubleValue()
    {
        return (double) _numerator / (double) _denominator;
    }

    /**
     * Returns the value of the specified number as a <code>float</code>.
     * This may involve rounding.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>float</code>.
     */
    public float floatValue()
    {
        return (float) _numerator / (float) _denominator;
    }

    /**
     * Returns the value of the specified number as a <code>byte</code>.
     * This may involve rounding or truncation.  This implementation simply
     * casts the result of <code>doubleValue()</code> to <code>byte</code>.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>byte</code>.
     */
    public final byte byteValue()
    {
        return (byte) doubleValue();
    }

    /**
     * Returns the value of the specified number as an <code>int</code>.
     * This may involve rounding or truncation.  This implementation simply
     * casts the result of <code>doubleValue()</code> to <code>int</code>.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>int</code>.
     */
    public final int intValue()
    {
        return (int) doubleValue();
    }

    /**
     * Returns the value of the specified number as a <code>long</code>.
     * This may involve rounding or truncation.  This implementation simply
     * casts the result of <code>doubleValue()</code> to <code>long</code>.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>long</code>.
     */
    public final long longValue()
    {
        return (long) doubleValue();
    }

    /**
     * Returns the value of the specified number as a <code>short</code>.
     * This may involve rounding or truncation.  This implementation simply
     * casts the result of <code>doubleValue()</code> to <code>short</code>.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>short</code>.
     */
    public final short shortValue()
    {
        return (short) doubleValue();
    }


    /** Returns the denominator. */
    public final long getDenominator()
    {
        return this._denominator;
    }

    /** Returns the numerator. */
    public final long getNumerator()
    {
        return this._numerator;
    }

    /**
     * Returns the reciprocal value of this object as a new Rational.
     *
     * @return the reciprocal in a new object
     */
    @NotNull
    public Rational getReciprocal()
    {
        return new Rational(this._denominator, this._numerator);
    }

    /** Checks if this rational number is an Integer, either positive or negative. */
    public boolean isInteger()
    {
        return _denominator == 1 ||
                (_denominator != 0 && (_numerator % _denominator == 0)) ||
                (_denominator == 0 && _numerator == 0);
    }

    /**
     * Returns a string representation of the object of form <code>numerator/denominator</code>.
     *
     * @return a string representation of the object.
     */
    @NotNull
    public String toString()
    {
        return _numerator + "/" + _denominator;
    }

    /** Returns the simplest representation of this Rational's value possible. */
    @NotNull
    public String toSimpleString(boolean allowDecimal)
    {
        if (_denominator == 0 && _numerator != 0) {
            return toString();
        } else if (isInteger()) {
            return Integer.toString(intValue());
        } else if (_numerator != 1 && _denominator % _numerator == 0) {
            // common factor between denominator and numerator
            long newDenominator = _denominator / _numerator;
            return new Rational(1, newDenominator).toSimpleString(allowDecimal);
        } else {
            Rational simplifiedInstance = getSimplifiedInstance();
            if (allowDecimal) {
                String doubleString = Double.toString(simplifiedInstance.doubleValue());
                if (doubleString.length() < 5) {
                    return doubleString;
                }
            }
            return simplifiedInstance.toString();
        }
    }

    /**
     * Decides whether a brute-force simplification calculation should be avoided
     * by comparing the maximum number of possible calculations with some threshold.
     *
     * @return true if the simplification should be performed, otherwise false
     */
    private boolean tooComplexForSimplification()
    {
        double maxPossibleCalculations = (((double) (Math.min(_denominator, _numerator) - 1) / 5d) + 2);
        final int maxSimplificationCalculations = 1000;
        return maxPossibleCalculations > maxSimplificationCalculations;
    }

    /**
     * Compares two <code>Rational</code> instances, returning true if they are mathematically
     * equivalent.
     *
     * @param obj the Rational to compare this instance to.
     * @return true if instances are mathematically equivalent, otherwise false.  Will also
     *         return false if <code>obj</code> is not an instance of <code>Rational</code>.
     */
    @Override
    public boolean equals(@Nullable Object obj)
    {
        if (obj==null || !(obj instanceof Rational))
            return false;
        Rational that = (Rational) obj;
        return this.doubleValue() == that.doubleValue();
    }

    @Override
    public int hashCode()
    {
        return (23 * (int)_denominator) + (int)_numerator;
    }

    /**
     * <p>
     * Simplifies the Rational number.</p>
     * <p>
     * Prime number series: 1, 2, 3, 5, 7, 9, 11, 13, 17</p>
     * <p>
     * To reduce a rational, need to see if both numerator and denominator are divisible
     * by a common factor.  Using the prime number series in ascending order guarantees
     * the minimum number of checks required.</p>
     * <p>
     * However, generating the prime number series seems to be a hefty task.  Perhaps
     * it's simpler to check if both d & n are divisible by all numbers from 2 ->
     * (Math.min(denominator, numerator) / 2).  In doing this, one can check for 2
     * and 5 once, then ignore all even numbers, and all numbers ending in 0 or 5.
     * This leaves four numbers from every ten to check.</p>
     * <p>
     * Therefore, the max number of pairs of modulus divisions required will be:</p>
     * <code><pre>
     *    4   Math.min(denominator, numerator) - 1
     *   -- * ------------------------------------ + 2
     *   10                    2
     * <p/>
     *   Math.min(denominator, numerator) - 1
     * = ------------------------------------ + 2
     *                  5
     * </pre></code>
     *
     * @return a simplified instance, or if the Rational could not be simplified,
     *         returns itself (unchanged)
     */
    @NotNull
    public Rational getSimplifiedInstance()
    {
        if (tooComplexForSimplification()) {
            return this;
        }
        for (int factor = 2; factor <= Math.min(_denominator, _numerator); factor++) {
            if ((factor % 2 == 0 && factor > 2) || (factor % 5 == 0 && factor > 5)) {
                continue;
            }
            if (_denominator % factor == 0 && _numerator % factor == 0) {
                // found a common factor
                return new Rational(_numerator / factor, _denominator / factor);
            }
        }
        return this;
    }
}

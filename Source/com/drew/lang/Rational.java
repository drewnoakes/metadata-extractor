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

package com.drew.lang;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.io.Serializable;

/**
 * Immutable class for holding a rational number without loss of precision.  Provides
 * a familiar representation via {@link Rational#toString} in form <code>numerator/denominator</code>.
 *
 * Note that any value with a numerator of zero will be treated as zero, even if the
 * denominator is also zero.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class Rational extends java.lang.Number implements Comparable<Rational>, Serializable
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
    @Override
    public double doubleValue()
    {
        return _numerator == 0
            ? 0.0
            : (double) _numerator / (double) _denominator;
    }

    /**
     * Returns the value of the specified number as a <code>float</code>.
     * This may involve rounding.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>float</code>.
     */
    @Override
    public float floatValue()
    {
        return _numerator == 0
            ? 0.0f
            : (float) _numerator / (float) _denominator;
    }

    /**
     * Returns the value of the specified number as a <code>byte</code>.
     * This may involve rounding or truncation.  This implementation simply
     * casts the result of {@link Rational#doubleValue} to <code>byte</code>.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>byte</code>.
     */
    @Override
    public final byte byteValue()
    {
        return (byte) doubleValue();
    }

    /**
     * Returns the value of the specified number as an <code>int</code>.
     * This may involve rounding or truncation.  This implementation simply
     * casts the result of {@link Rational#doubleValue} to <code>int</code>.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>int</code>.
     */
    @Override
    public final int intValue()
    {
        return (int) doubleValue();
    }

    /**
     * Returns the value of the specified number as a <code>long</code>.
     * This may involve rounding or truncation.  This implementation simply
     * casts the result of {@link Rational#doubleValue} to <code>long</code>.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>long</code>.
     */
    @Override
    public final long longValue()
    {
        return (long) doubleValue();
    }

    /**
     * Returns the value of the specified number as a <code>short</code>.
     * This may involve rounding or truncation.  This implementation simply
     * casts the result of {@link Rational#doubleValue} to <code>short</code>.
     *
     * @return the numeric value represented by this object after conversion
     *         to type <code>short</code>.
     */
    @Override
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

    /** Checks if this {@link Rational} number is an Integer, either positive or negative. */
    public boolean isInteger()
    {
        return _denominator == 1 ||
                (_denominator != 0 && (_numerator % _denominator == 0)) ||
                (_denominator == 0 && _numerator == 0);
    }

    /** Checks if either the numerator or denominator are zero. */
    public boolean isZero()
    {
        return _numerator == 0 || _denominator == 0;
    }

    /**
     * Returns a string representation of the object of form <code>numerator/denominator</code>.
     *
     * @return a string representation of the object.
     */
    @Override
    @NotNull
    public String toString()
    {
        return _numerator + "/" + _denominator;
    }

    /** Returns the simplest representation of this {@link Rational}'s value possible. */
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
     * Compares two {@link Rational} instances, returning true if they are mathematically
     * equivalent (in consistence with {@link Rational#equals(Object)} method).
     *
     * @param that the {@link Rational} to compare this instance to.
     * @return the value {@code 0} if this {@link Rational} is
     *         equal to the argument {@link Rational} mathematically; a value less
     *         than {@code 0} if this {@link Rational} is less
     *         than the argument {@link Rational}; and a value greater
     *         than {@code 0} if this {@link Rational} is greater than the argument
     *         {@link Rational}.
     */
    public int compareTo(@NotNull Rational that) {
        return Double.compare(this.doubleValue(), that.doubleValue());
    }

    /**
     * Indicates whether this instance and <code>other</code> are numerically equal,
     * even if their representations differ.
     *
     * For example, 1/2 is equal to 10/20 by this method.
     * Similarly, 1/0 is equal to 100/0 by this method.
     * To test equal representations, use EqualsExact.
     *
     * @param other The rational value to compare with
     */
    public boolean equals(Rational other) {
        return other.doubleValue() == doubleValue();
    }

    /**
     * Indicates whether this instance and <code>other</code> have identical
     * Numerator and Denominator.
     * <p>
     * For example, 1/2 is not equal to 10/20 by this method.
     * Similarly, 1/0 is not equal to 100/0 by this method.
     * To test numerically equivalence, use Equals(Rational).</p>
     *
     * @param other The rational value to compare with
     */
    public boolean equalsExact(Rational other) {
        return getDenominator() == other.getDenominator() && getNumerator() == other.getNumerator();
    }

    /**
     * Compares two {@link Rational} instances, returning true if they are mathematically
     * equivalent.
     *
     * @param obj the {@link Rational} to compare this instance to.
     * @return true if instances are mathematically equivalent, otherwise false.  Will also
     *         return false if <code>obj</code> is not an instance of {@link Rational}.
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
     * Simplifies the representation of this {@link Rational} number.</p>
     * <p>
     * For example, 5/10 simplifies to 1/2 because both Numerator
     * and Denominator share a common factor of 5.</p>
     * <p>
     * Uses the Euclidean Algorithm to find the greatest common divisor.</p>
     *
     * @return A simplified instance if one exists, otherwise a copy of the original value.
     */
    @NotNull
    public Rational getSimplifiedInstance()
    {
        long gcd = GCD(_numerator, _denominator);

        return new Rational(_numerator / gcd, _denominator / gcd);
    }

    private static long GCD(long a, long b)
    {
        if (a < 0)
            a = -a;
        if (b < 0)
            b = -b;

        while (a != 0 && b != 0)
        {
            if (a > b)
                a %= b;
            else
                b %= a;
        }

        return a == 0 ? b : a;
    }
}

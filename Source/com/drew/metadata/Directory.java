/*
 * Copyright 2002-2014 Drew Noakes
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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.lang.annotations.SuppressWarnings;

/**
 * Abstract base class for all directory implementations, having methods for getting and setting tag values of various
 * data types.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public abstract class Directory
{
    /** Map of values hashed by type identifiers. */
    @NotNull
    protected final Map<Integer, Object> _tagMap = new HashMap<Integer, Object>();

    /**
     * A convenient list holding tag values in the order in which they were stored.
     * This is used for creation of an iterator, and for counting the number of
     * defined tags.
     */
    @NotNull
    protected final Collection<Tag> _definedTagList = new ArrayList<Tag>();

    @NotNull
    private final Collection<String> _errorList = new ArrayList<String>(4);

    /** The descriptor used to interpret tag values. */
    protected TagDescriptor _descriptor;

// ABSTRACT METHODS

    /**
     * Provides the name of the directory, for display purposes.  E.g. <code>Exif</code>
     *
     * @return the name of the directory
     */
    @NotNull
    public abstract String getName();

    /**
     * Provides the map of tag names, hashed by tag type identifier.
     *
     * @return the map of tag names
     */
    @NotNull
    protected abstract HashMap<Integer, String> getTagNameMap();

    protected Directory()
    {}

// VARIOUS METHODS

    /**
     * Indicates whether the specified tag type has been set.
     *
     * @param tagType the tag type to check for
     * @return true if a value exists for the specified tag type, false if not
     */
    @java.lang.SuppressWarnings({ "UnnecessaryBoxing" })
    public boolean containsTag(final int tagType)
    {
        return _tagMap.containsKey(Integer.valueOf(tagType));
    }

    /**
     * Returns an Iterator of Tag instances that have been set in this Directory.
     *
     * @return an Iterator of Tag instances
     */
    @NotNull
    public Collection<Tag> getTags()
    {
        return _definedTagList;
    }

    /**
     * Returns the number of tags set in this Directory.
     *
     * @return the number of tags set in this Directory
     */
    public int getTagCount()
    {
        return _definedTagList.size();
    }

    /**
     * Sets the descriptor used to interpret tag values.
     *
     * @param descriptor the descriptor used to interpret tag values
     */
    @java.lang.SuppressWarnings({ "ConstantConditions" })
    public void setDescriptor(@NotNull final TagDescriptor descriptor)
    {
        if (descriptor == null)
            throw new NullPointerException("cannot set a null descriptor");
        _descriptor = descriptor;
    }

    /**
     * Registers an error message with this directory.
     *
     * @param message an error message.
     */
    public void addError(@NotNull final String message)
    {
        _errorList.add(message);
    }

    /**
     * Gets a value indicating whether this directory has any error messages.
     *
     * @return true if the directory contains errors, otherwise false
     */
    public boolean hasErrors()
    {
        return _errorList.size() > 0;
    }

    /**
     * Used to iterate over any error messages contained in this directory.
     *
     * @return an iterable collection of error message strings.
     */
    @NotNull
    public Iterable<String> getErrors()
    {
        return _errorList;
    }

    /** Returns the count of error messages in this directory. */
    public int getErrorCount()
    {
        return _errorList.size();
    }

// TAG SETTERS

    /**
     * Sets an <code>int</code> value for the specified tag.
     *
     * @param tagType the tag's value as an int
     * @param value   the value for the specified tag as an int
     */
    public void setInt(final int tagType, final int value)
    {
        setObject(tagType, value);
    }

    /**
     * Sets an <code>int[]</code> (array) for the specified tag.
     *
     * @param tagType the tag identifier
     * @param ints    the int array to store
     */
    public void setIntArray(final int tagType, @NotNull final int[] ints)
    {
        setObjectArray(tagType, ints);
    }

    /**
     * Sets a <code>float</code> value for the specified tag.
     *
     * @param tagType the tag's value as an int
     * @param value   the value for the specified tag as a float
     */
    public void setFloat(final int tagType, final float value)
    {
        setObject(tagType, value);
    }

    /**
     * Sets a <code>float[]</code> (array) for the specified tag.
     *
     * @param tagType the tag identifier
     * @param floats  the float array to store
     */
    public void setFloatArray(final int tagType, @NotNull final float[] floats)
    {
        setObjectArray(tagType, floats);
    }

    /**
     * Sets a <code>double</code> value for the specified tag.
     *
     * @param tagType the tag's value as an int
     * @param value   the value for the specified tag as a double
     */
    public void setDouble(final int tagType, final double value)
    {
        setObject(tagType, value);
    }

    /**
     * Sets a <code>double[]</code> (array) for the specified tag.
     *
     * @param tagType the tag identifier
     * @param doubles the double array to store
     */
    public void setDoubleArray(final int tagType, @NotNull final double[] doubles)
    {
        setObjectArray(tagType, doubles);
    }

    /**
     * Sets a <code>String</code> value for the specified tag.
     *
     * @param tagType the tag's value as an int
     * @param value   the value for the specified tag as a String
     */
    @java.lang.SuppressWarnings({ "ConstantConditions" })
    public void setString(final int tagType, @NotNull final String value)
    {
        if (value == null)
            throw new NullPointerException("cannot set a null String");
        setObject(tagType, value);
    }

    /**
     * Sets a <code>String[]</code> (array) for the specified tag.
     *
     * @param tagType the tag identifier
     * @param strings the String array to store
     */
    public void setStringArray(final int tagType, @NotNull final String[] strings)
    {
        setObjectArray(tagType, strings);
    }

    /**
     * Sets a <code>boolean</code> value for the specified tag.
     *
     * @param tagType the tag's value as an int
     * @param value   the value for the specified tag as a boolean
     */
    public void setBoolean(final int tagType, final boolean value)
    {
        setObject(tagType, value);
    }

    /**
     * Sets a <code>long</code> value for the specified tag.
     *
     * @param tagType the tag's value as an int
     * @param value   the value for the specified tag as a long
     */
    public void setLong(final int tagType, final long value)
    {
        setObject(tagType, value);
    }

    /**
     * Sets a <code>java.util.Date</code> value for the specified tag.
     *
     * @param tagType the tag's value as an int
     * @param value   the value for the specified tag as a java.util.Date
     */
    public void setDate(final int tagType, @NotNull final java.util.Date value)
    {
        setObject(tagType, value);
    }

    /**
     * Sets a <code>Rational</code> value for the specified tag.
     *
     * @param tagType  the tag's value as an int
     * @param rational rational number
     */
    public void setRational(final int tagType, @NotNull final Rational rational)
    {
        setObject(tagType, rational);
    }

    /**
     * Sets a <code>Rational[]</code> (array) for the specified tag.
     *
     * @param tagType   the tag identifier
     * @param rationals the Rational array to store
     */
    public void setRationalArray(final int tagType, @NotNull final Rational[] rationals)
    {
        setObjectArray(tagType, rationals);
    }

    /**
     * Sets a <code>byte[]</code> (array) for the specified tag.
     *
     * @param tagType the tag identifier
     * @param bytes   the byte array to store
     */
    public void setByteArray(final int tagType, @NotNull final byte[] bytes)
    {
        setObjectArray(tagType, bytes);
    }

    /**
     * Sets a <code>Object</code> for the specified tag.
     *
     * @param tagType the tag's value as an int
     * @param value   the value for the specified tag
     * @throws NullPointerException if value is <code>null</code>
     */
    @java.lang.SuppressWarnings( { "ConstantConditions", "UnnecessaryBoxing" })
    public void setObject(final int tagType, @NotNull final Object value)
    {
        if (value == null)
            throw new NullPointerException("cannot set a null object");

        if (!_tagMap.containsKey(Integer.valueOf(tagType))) {
            _definedTagList.add(new Tag(tagType, this));
        }
//        else {
//            final Object oldValue = _tagMap.get(tagType);
//            if (!oldValue.equals(value))
//                addError(String.format("Overwritten tag 0x%s (%s).  Old=%s, New=%s", Integer.toHexString(tagType), getTagName(tagType), oldValue, value));
//        }
        _tagMap.put(tagType, value);
    }

    /**
     * Sets an array <code>Object</code> for the specified tag.
     *
     * @param tagType the tag's value as an int
     * @param array   the array of values for the specified tag
     */
    public void setObjectArray(final int tagType, @NotNull final Object array)
    {
        // for now, we don't do anything special -- this method might be a candidate for removal once the dust settles
        setObject(tagType, array);
    }

// TAG GETTERS

    /**
     * Returns the specified tag's value as an int, if possible.  Every attempt to represent the tag's value as an int
     * is taken.  Here is a list of the action taken depending upon the tag's original type:
     * <ul>
     * <li> int - Return unchanged.
     * <li> Number - Return an int value (real numbers are truncated).
     * <li> Rational - Truncate any fractional part and returns remaining int.
     * <li> String - Attempt to parse string as an int.  If this fails, convert the char[] to an int (using shifts and OR).
     * <li> Rational[] - Return int value of first item in array.
     * <li> byte[] - Return int value of first item in array.
     * <li> int[] - Return int value of first item in array.
     * </ul>
     *
     * @throws MetadataException if no value exists for tagType or if it cannot be converted to an int.
     */
    public int getInt(final int tagType) throws MetadataException
    {
        final Integer integer = getInteger(tagType);
        if (integer!=null)
            return integer;

        final Object o = getObject(tagType);
        if (o == null)
            throw new MetadataException("Tag '" + getTagName(tagType) + "' has not been set -- check using containsTag() first");
        throw new MetadataException("Tag '" + tagType + "' cannot be converted to int.  It is of type '" + o.getClass() + "'.");
    }

    /**
     * Returns the specified tag's value as an Integer, if possible.  Every attempt to represent the tag's value as an
     * Integer is taken.  Here is a list of the action taken depending upon the tag's original type:
     * <ul>
     * <li> int - Return unchanged
     * <li> Number - Return an int value (real numbers are truncated)
     * <li> Rational - Truncate any fractional part and returns remaining int
     * <li> String - Attempt to parse string as an int.  If this fails, convert the char[] to an int (using shifts and OR)
     * <li> Rational[] - Return int value of first item in array if length &gt; 0
     * <li> byte[] - Return int value of first item in array if length &gt; 0
     * <li> int[] - Return int value of first item in array if length &gt; 0
     * </ul>
     *
     * If the value is not found or cannot be converted to int, <code>null</code> is returned.
     */
    @Nullable
    public Integer getInteger(final int tagType)
    {
        final Object o = getObject(tagType);

        if (o == null)
            return null;

        if (o instanceof Number) {
            return ((Number)o).intValue();
        } else if (o instanceof String) {
            try {
                return Integer.parseInt((String)o);
            } catch (final NumberFormatException nfe) {
                // convert the char array to an int
                final String s = (String)o;
                final byte[] bytes = s.getBytes();
                long val = 0;
                for (final byte aByte : bytes) {
                    val = val << 8;
                    val += (aByte & 0xff);
                }
                return (int)val;
            }
        } else if (o instanceof Rational[]) {
            final Rational[] rationals = (Rational[])o;
            if (rationals.length == 1)
                return rationals[0].intValue();
        } else if (o instanceof byte[]) {
            final byte[] bytes = (byte[])o;
            if (bytes.length == 1)
                return (int)bytes[0];
        } else if (o instanceof int[]) {
            final int[] ints = (int[])o;
            if (ints.length == 1)
                return ints[0];
        }
        return null;
    }

    /**
     * Gets the specified tag's value as a String array, if possible.  Only supported
     * where the tag is set as String[], String, int[], byte[] or Rational[].
     *
     * @param tagType the tag identifier
     * @return the tag's value as an array of Strings. If the value is unset or cannot be converted, <code>null</code> is returned.
     */
    @Nullable
    public String[] getStringArray(final int tagType)
    {
        final Object o = getObject(tagType);
        if (o == null)
            return null;
        if (o instanceof String[])
            return (String[])o;
        if (o instanceof String)
            return new String[] { (String)o };
        if (o instanceof int[]) {
            final int[] ints = (int[])o;
            final String[] strings = new String[ints.length];
            for (int i = 0; i < strings.length; i++)
                strings[i] = Integer.toString(ints[i]);
            return strings;
        } else if (o instanceof byte[]) {
            final byte[] bytes = (byte[])o;
            final String[] strings = new String[bytes.length];
            for (int i = 0; i < strings.length; i++)
                strings[i] = Byte.toString(bytes[i]);
            return strings;
        } else if (o instanceof Rational[]) {
            final Rational[] rationals = (Rational[])o;
            final String[] strings = new String[rationals.length];
            for (int i = 0; i < strings.length; i++)
                strings[i] = rationals[i].toSimpleString(false);
            return strings;
        }
        return null;
    }

    /**
     * Gets the specified tag's value as an int array, if possible.  Only supported
     * where the tag is set as String, Integer, int[], byte[] or Rational[].
     *
     * @param tagType the tag identifier
     * @return the tag's value as an int array
     */
    @Nullable
    public int[] getIntArray(final int tagType)
    {
        final Object o = getObject(tagType);
        if (o == null)
            return null;
        if (o instanceof int[])
            return (int[])o;
        if (o instanceof Rational[]) {
            final Rational[] rationals = (Rational[])o;
            final int[] ints = new int[rationals.length];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = rationals[i].intValue();
            }
            return ints;
        }
        if (o instanceof short[]) {
            final short[] shorts = (short[])o;
            final int[] ints = new int[shorts.length];
            for (int i = 0; i < shorts.length; i++) {
                ints[i] = shorts[i];
            }
            return ints;
        }
        if (o instanceof byte[]) {
            final byte[] bytes = (byte[])o;
            final int[] ints = new int[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                ints[i] = bytes[i];
            }
            return ints;
        }
        if (o instanceof CharSequence) {
            final CharSequence str = (CharSequence)o;
            final int[] ints = new int[str.length()];
            for (int i = 0; i < str.length(); i++) {
                ints[i] = str.charAt(i);
            }
            return ints;
        }
        if (o instanceof Integer)
            return new int[] { (Integer)o };

        return null;
    }

    /**
     * Gets the specified tag's value as an byte array, if possible.  Only supported
     * where the tag is set as String, Integer, int[], byte[] or Rational[].
     *
     * @param tagType the tag identifier
     * @return the tag's value as a byte array
     */
    @Nullable
    public byte[] getByteArray(final int tagType)
    {
        final Object o = getObject(tagType);
        if (o == null) {
            return null;
        } else if (o instanceof Rational[]) {
            final Rational[] rationals = (Rational[])o;
            final byte[] bytes = new byte[rationals.length];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = rationals[i].byteValue();
            }
            return bytes;
        } else if (o instanceof byte[]) {
            return (byte[])o;
        } else if (o instanceof int[]) {
            final int[] ints = (int[])o;
            final byte[] bytes = new byte[ints.length];
            for (int i = 0; i < ints.length; i++) {
                bytes[i] = (byte)ints[i];
            }
            return bytes;
        } else if (o instanceof short[]) {
            final short[] shorts = (short[])o;
            final byte[] bytes = new byte[shorts.length];
            for (int i = 0; i < shorts.length; i++) {
                bytes[i] = (byte)shorts[i];
            }
            return bytes;
        } else if (o instanceof CharSequence) {
            final CharSequence str = (CharSequence)o;
            final byte[] bytes = new byte[str.length()];
            for (int i = 0; i < str.length(); i++) {
                bytes[i] = (byte)str.charAt(i);
            }
            return bytes;
        }
        if (o instanceof Integer)
            return new byte[] { ((Integer)o).byteValue() };

        return null;
    }

    /** Returns the specified tag's value as a double, if possible. */
    public double getDouble(final int tagType) throws MetadataException
    {
        final Double value = getDoubleObject(tagType);
        if (value!=null)
            return value;
        final Object o = getObject(tagType);
        if (o == null)
            throw new MetadataException("Tag '" + getTagName(tagType) + "' has not been set -- check using containsTag() first");
        throw new MetadataException("Tag '" + tagType + "' cannot be converted to a double.  It is of type '" + o.getClass() + "'.");
    }
    /** Returns the specified tag's value as a Double.  If the tag is not set or cannot be converted, <code>null</code> is returned. */
    @Nullable
    public Double getDoubleObject(final int tagType)
    {
        final Object o = getObject(tagType);
        if (o == null)
            return null;
        if (o instanceof String) {
            try {
                return Double.parseDouble((String)o);
            } catch (final NumberFormatException nfe) {
                return null;
            }
        }
        if (o instanceof Number)
            return ((Number)o).doubleValue();

        return null;
    }

    /** Returns the specified tag's value as a float, if possible. */
    public float getFloat(final int tagType) throws MetadataException
    {
        final Float value = getFloatObject(tagType);
        if (value!=null)
            return value;
        final Object o = getObject(tagType);
        if (o == null)
            throw new MetadataException("Tag '" + getTagName(tagType) + "' has not been set -- check using containsTag() first");
        throw new MetadataException("Tag '" + tagType + "' cannot be converted to a float.  It is of type '" + o.getClass() + "'.");
    }

    /** Returns the specified tag's value as a float.  If the tag is not set or cannot be converted, <code>null</code> is returned. */
    @Nullable
    public Float getFloatObject(final int tagType)
    {
        final Object o = getObject(tagType);
        if (o == null)
            return null;
        if (o instanceof String) {
            try {
                return Float.parseFloat((String)o);
            } catch (final NumberFormatException nfe) {
                return null;
            }
        }
        if (o instanceof Number)
            return ((Number)o).floatValue();
        return null;
    }

    /** Returns the specified tag's value as a long, if possible. */
    public long getLong(final int tagType) throws MetadataException
    {
        final Long value = getLongObject(tagType);
        if (value!=null)
            return value;
        final Object o = getObject(tagType);
        if (o == null)
            throw new MetadataException("Tag '" + getTagName(tagType) + "' has not been set -- check using containsTag() first");
        throw new MetadataException("Tag '" + tagType + "' cannot be converted to a long.  It is of type '" + o.getClass() + "'.");
    }

    /** Returns the specified tag's value as a long.  If the tag is not set or cannot be converted, <code>null</code> is returned. */
    @Nullable
    public Long getLongObject(final int tagType)
    {
        final Object o = getObject(tagType);
        if (o == null)
            return null;
        if (o instanceof String) {
            try {
                return Long.parseLong((String)o);
            } catch (final NumberFormatException nfe) {
                return null;
            }
        }
        if (o instanceof Number)
            return ((Number)o).longValue();
        return null;
    }

    /** Returns the specified tag's value as a boolean, if possible. */
    public boolean getBoolean(final int tagType) throws MetadataException
    {
        final Boolean value = getBooleanObject(tagType);
        if (value!=null)
            return value;
        final Object o = getObject(tagType);
        if (o == null)
            throw new MetadataException("Tag '" + getTagName(tagType) + "' has not been set -- check using containsTag() first");
        throw new MetadataException("Tag '" + tagType + "' cannot be converted to a boolean.  It is of type '" + o.getClass() + "'.");
    }

    /** Returns the specified tag's value as a boolean.  If the tag is not set or cannot be converted, <code>null</code> is returned. */
    @Nullable
    @SuppressWarnings(value = "NP_BOOLEAN_RETURN_NULL", justification = "keep API interface consistent")
    public Boolean getBooleanObject(final int tagType)
    {
        final Object o = getObject(tagType);
        if (o == null)
            return null;
        if (o instanceof Boolean)
            return (Boolean)o;
        if (o instanceof String) {
            try {
                return Boolean.getBoolean((String)o);
            } catch (final NumberFormatException nfe) {
                return null;
            }
        }
        if (o instanceof Number)
            return (((Number)o).doubleValue() != 0);
        return null;
    }

    /**
     * Returns the specified tag's value as a java.util.Date.  If the value is unset or cannot be converted, <code>null</code> is returned.
     * <p/>
     * If the underlying value is a {@link String}, then attempts will be made to parse the string as though it is in
     * the current {@link TimeZone}.  If the {@link TimeZone} is known, call the overload that accepts one as an argument.
     */
    @Nullable
    public java.util.Date getDate(final int tagType)
    {
        return getDate(tagType, null);
    }

    /**
     * Returns the specified tag's value as a java.util.Date.  If the value is unset or cannot be converted, <code>null</code> is returned.
     * <p/>
     * If the underlying value is a {@link String}, then attempts will be made to parse the string as though it is in
     * the {@link TimeZone} represented by the {@code timeZone} parameter (if it is non-null).  Note that this parameter
     * is only considered if the underlying value is a string and parsing occurs, otherwise it has no effect.
     */
    @Nullable
    public java.util.Date getDate(final int tagType, @Nullable final TimeZone timeZone)
    {
        final Object o = getObject(tagType);

        if (o == null)
            return null;

        if (o instanceof java.util.Date)
            return (java.util.Date)o;

        if (o instanceof String) {
            // This seems to cover all known Exif date strings
            // Note that "    :  :     :  :  " is a valid date string according to the Exif spec (which means 'unknown date'): http://www.awaresystems.be/imaging/tiff/tifftags/privateifd/exif/datetimeoriginal.html
            final String datePatterns[] = {
                    "yyyy:MM:dd HH:mm:ss",
                    "yyyy:MM:dd HH:mm",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd HH:mm",
                    "yyyy.MM.dd HH:mm:ss",
                    "yyyy.MM.dd HH:mm" };
            final String dateString = (String)o;
            for (final String datePattern : datePatterns) {
                try {
                    final DateFormat parser = new SimpleDateFormat(datePattern);
                    if (timeZone != null)
                        parser.setTimeZone(timeZone);
					if (parser.parse(dateString).getTime() < 0) {
						return null;
					}
                    return parser.parse(dateString);
                } catch (final ParseException ex) {
                    // simply try the next pattern
                }
            }
        }
        return null;
    }

    /** Returns the specified tag's value as a Rational.  If the value is unset or cannot be converted, <code>null</code> is returned. */
    @Nullable
    public Rational getRational(final int tagType)
    {
        final Object o = getObject(tagType);

        if (o == null)
            return null;

        if (o instanceof Rational)
            return (Rational)o;
        if (o instanceof Integer)
            return new Rational((Integer)o, 1);
        if (o instanceof Long)
            return new Rational((Long)o, 1);

        // NOTE not doing conversions for real number types

        return null;
    }

    /** Returns the specified tag's value as an array of Rational.  If the value is unset or cannot be converted, <code>null</code> is returned. */
    @Nullable
    public Rational[] getRationalArray(final int tagType)
    {
        final Object o = getObject(tagType);
        if (o == null)
            return null;

        if (o instanceof Rational[])
            return (Rational[])o;

        return null;
    }

    /**
     * Returns the specified tag's value as a String.  This value is the 'raw' value.  A more presentable decoding
     * of this value may be obtained from the corresponding Descriptor.
     *
     * @return the String representation of the tag's value, or
     *         <code>null</code> if the tag hasn't been defined.
     */
    @Nullable
    public String getString(final int tagType)
    {
        final Object o = getObject(tagType);
        if (o == null)
            return null;

        if (o instanceof Rational)
            return ((Rational)o).toSimpleString(true);

        if (o.getClass().isArray()) {
            // handle arrays of objects and primitives
            final int arrayLength = Array.getLength(o);
            final Class<?> componentType = o.getClass().getComponentType();
            final boolean isObjectArray = Object.class.isAssignableFrom(componentType);
            final boolean isFloatArray = componentType.getName().equals("float");
            final boolean isDoubleArray = componentType.getName().equals("double");
            final boolean isIntArray = componentType.getName().equals("int");
            final boolean isLongArray = componentType.getName().equals("long");
            final boolean isByteArray = componentType.getName().equals("byte");
            final boolean isShortArray = componentType.getName().equals("short");
            final StringBuilder string = new StringBuilder();
            for (int i = 0; i < arrayLength; i++) {
                if (i != 0)
                    string.append(' ');
                if (isObjectArray)
                    string.append(Array.get(o, i).toString());
                else if (isIntArray)
                    string.append(Array.getInt(o, i));
                else if (isShortArray)
                    string.append(Array.getShort(o, i));
                else if (isLongArray)
                    string.append(Array.getLong(o, i));
                else if (isFloatArray)
                    string.append(Array.getFloat(o, i));
                else if (isDoubleArray)
                    string.append(Array.getDouble(o, i));
                else if (isByteArray)
                    string.append(Array.getByte(o, i));
                else
                    addError("Unexpected array component type: " + componentType.getName());
            }
            return string.toString();
        }

        // Note that several cameras leave trailing spaces (Olympus, Nikon) but this library is intended to show
        // the actual data within the file.  It is not inconceivable that whitespace may be significant here, so we
        // do not trim.  Also, if support is added for writing data back to files, this may cause issues.
        // We leave trimming to the presentation layer.
        return o.toString();
    }

    @Nullable
    public String getString(final int tagType, final String charset)
    {
        final byte[] bytes = getByteArray(tagType);
        if (bytes==null)
            return null;
        try {
            return new String(bytes, charset);
        } catch (final UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Returns the object hashed for the particular tag type specified, if available.
     *
     * @param tagType the tag type identifier
     * @return the tag's value as an Object if available, else <code>null</code>
     */
    @java.lang.SuppressWarnings({ "UnnecessaryBoxing" })
    @Nullable
    public Object getObject(final int tagType)
    {
        return _tagMap.get(Integer.valueOf(tagType));
    }

// OTHER METHODS

    /**
     * Returns the name of a specified tag as a String.
     *
     * @param tagType the tag type identifier
     * @return the tag's name as a String
     */
    @NotNull
    public String getTagName(final int tagType)
    {
        final HashMap<Integer, String> nameMap = getTagNameMap();
        if (!nameMap.containsKey(tagType)) {
            String hex = Integer.toHexString(tagType);
            while (hex.length() < 4) {
                hex = "0" + hex;
            }
            return "Unknown tag (0x" + hex + ")";
        }
        return nameMap.get(tagType);
    }

    /**
     * Provides a description of a tag's value using the descriptor set by
     * <code>setDescriptor(Descriptor)</code>.
     *
     * @param tagType the tag type identifier
     * @return the tag value's description as a String
     */
    @Nullable
    public String getDescription(final int tagType)
    {
        assert(_descriptor != null);
        return _descriptor.getDescription(tagType);
    }
}

/*
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
 * Created by dnoakes on 25-Nov-2002 20:30:39 using IntelliJ IDEA.
 */
package com.drew.metadata;

import com.drew.lang.Rational;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract base class for all directory implementations, having methods for getting and setting tag values of various
 * data types.
 */
public abstract class Directory implements Serializable
{
    /**
     * Map of values hashed by type identifiers.
     */
    protected final HashMap _tagMap;

    /**
     * The descriptor used to interperet tag values.
     */
    protected TagDescriptor _descriptor;

    /**
     * A convenient list holding tag values in the order in which they were stored.
     * This is used for creation of an iterator, and for counting the number of
     * defined tags.
     */
    protected final List _definedTagList;

    private List _errorList;

// ABSTRACT METHODS

    /**
     * Provides the name of the directory, for display purposes.  E.g. <code>Exif</code>
     * @return the name of the directory
     */
    public abstract String getName();

    /**
     * Provides the map of tag names, hashed by tag type identifier.
     * @return the map of tag names
     */
    protected abstract HashMap getTagNameMap();

// CONSTRUCTORS

    /**
     * Creates a new Directory.
     */
    public Directory()
    {
        _tagMap = new HashMap();
        _definedTagList = new ArrayList();
    }

// VARIOUS METHODS

    /**
     * Indicates whether the specified tag type has been set.
     * @param tagType the tag type to check for
     * @return true if a value exists for the specified tag type, false if not
     */
    public boolean containsTag(int tagType)
    {
        return _tagMap.containsKey(new Integer(tagType));
    }

    /**
     * Returns an Iterator of Tag instances that have been set in this Directory.
     * @return an Iterator of Tag instances
     */
    public Iterator getTagIterator()
    {
        return _definedTagList.iterator();
    }

    /**
     * Returns the number of tags set in this Directory.
     * @return the number of tags set in this Directory
     */
    public int getTagCount()
    {
        return _definedTagList.size();
    }

    /**
     * Sets the descriptor used to interperet tag values.
     * @param descriptor the descriptor used to interperet tag values
     */
    public void setDescriptor(TagDescriptor descriptor)
    {
        if (descriptor==null) {
            throw new NullPointerException("cannot set a null descriptor");
        }
        _descriptor = descriptor;
    }

    /**
     * Registers an error message with this directory.
     * @param message an error message.
     */
    public void addError(String message)
    {
        if (_errorList==null) {
            _errorList = new ArrayList();
        }
        _errorList.add(message);
    }

    /**
     * Gets a value indicating whether this directory has any error messages.
     * @return true if the directory contains errors, otherwise false
     */
    public boolean hasErrors()
    {
        return (_errorList!=null && _errorList.size()>0);
    }

    /**
     * Used to iterate over any error messages contained in this directory.
     * @return an iterator over the error message strings.
     */
    public Iterator getErrors()
    {
        return _errorList.iterator();
    }

    /**
     * Returns the count of error messages in this directory.
     */
    public int getErrorCount()
    {
        return _errorList.size();
    }

// TAG SETTERS

    /**
     * Sets an <code>int</code> value for the specified tag.
     * @param tagType the tag's value as an int
     * @param value the value for the specified tag as an int
     */
    public void setInt(int tagType, int value)
    {
        setObject(tagType, new Integer(value));
    }

    /**
     * Sets a <code>double</code> value for the specified tag.
     * @param tagType the tag's value as an int
     * @param value the value for the specified tag as a double
     */
    public void setDouble(int tagType, double value)
    {
        setObject(tagType, new Double(value));
    }

    /**
     * Sets a <code>float</code> value for the specified tag.
     * @param tagType the tag's value as an int
     * @param value the value for the specified tag as a float
     */
    public void setFloat(int tagType, float value)
    {
        setObject(tagType, new Float(value));
    }

    /**
     * Sets a <code>String</code> value for the specified tag.
     * @param tagType the tag's value as an int
     * @param value the value for the specified tag as a String
     */
    public void setString(int tagType, String value)
    {
        setObject(tagType, value);
    }

    /**
     * Sets a <code>boolean</code> value for the specified tag.
     * @param tagType the tag's value as an int
     * @param value the value for the specified tag as a boolean
     */
    public void setBoolean(int tagType, boolean value)
    {
        setObject(tagType, new Boolean(value));
    }

    /**
     * Sets a <code>long</code> value for the specified tag.
     * @param tagType the tag's value as an int
     * @param value the value for the specified tag as a long
     */
    public void setLong(int tagType, long value)
    {
        setObject(tagType, new Long(value));
    }

    /**
     * Sets a <code>java.util.Date</code> value for the specified tag.
     * @param tagType the tag's value as an int
     * @param value the value for the specified tag as a java.util.Date
     */
    public void setDate(int tagType, java.util.Date value)
    {
        setObject(tagType, value);
    }

    /**
     * Sets a <code>Rational</code> value for the specified tag.
     * @param tagType the tag's value as an int
     * @param rational rational number
     */
    public void setRational(int tagType, Rational rational)
    {
        setObject(tagType, rational);
    }

    /**
     * Sets a <code>Rational[]</code> (array) for the specified tag.
     * @param tagType the tag identifier
     * @param rationals the Rational array to store
     */
    public void setRationalArray(int tagType, Rational[] rationals)
    {
        setObjectArray(tagType, rationals);
    }

    /**
     * Sets an <code>int[]</code> (array) for the specified tag.
     * @param tagType the tag identifier
     * @param ints the int array to store
     */
    public void setIntArray(int tagType, int[] ints)
    {
        setObjectArray(tagType, ints);
    }

    /**
     * Sets a <code>byte[]</code> (array) for the specified tag.
     * @param tagType the tag identifier
     * @param bytes the byte array to store
     */
    public void setByteArray(int tagType, byte[] bytes)
    {
        setObjectArray(tagType, bytes);
    }

    /**
     * Sets a <code>String[]</code> (array) for the specified tag.
     * @param tagType the tag identifier
     * @param strings the String array to store
     */
    public void setStringArray(int tagType, String[] strings)
    {
        setObjectArray(tagType, strings);
    }

    /**
     * Sets a <code>Object</code> for the specified tag.
     * @param tagType the tag's value as an int
     * @param value the value for the specified tag
     * @throws NullPointerException if value is <code>null</code>
     */
    public void setObject(int tagType, Object value)
    {
        if (value==null)
            throw new NullPointerException("cannot set a null object");

        Integer key = new Integer(tagType);
        if (!_tagMap.containsKey(key))
            _definedTagList.add(new Tag(tagType, this));
        _tagMap.put(key, value);
    }

    /**
     * Sets an array <code>Object</code> for the specified tag.
     * @param tagType the tag's value as an int
     * @param array the array of values for the specified tag
     */
    public void setObjectArray(int tagType, Object array)
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
     * @throws MetadataException if no value exists for tagType or if it cannot be cast to an int.
     */
    public int getInt(int tagType) throws MetadataException
    {
        Object o = getObject(tagType);
        if (o==null) {
            throw new MetadataException("Tag " + getTagName(tagType) + " has not been set -- check using containsTag() first");
        } else if (o instanceof String) {
            try {
                return Integer.parseInt((String)o);
            } catch (NumberFormatException nfe) {
                // convert the char array to an int
                String s = (String)o;
                byte[] bytes = s.getBytes();
                long val = 0;
                for (int i = 0; i < bytes.length; i++) {
                    val = val << 8;
                    val += bytes[i];
                }
                return (int)val;
            }
        } else if (o instanceof Number) {
            return ((Number)o).intValue();
        } else if (o instanceof Rational[]) {
            Rational[] rationals = (Rational[])o;
            if (rationals.length==1)
                return rationals[0].intValue();
        } else if (o instanceof byte[]) {
            byte[] bytes = (byte[])o;
            if (bytes.length==1)
                return bytes[0];
        } else if (o instanceof int[]) {
            int[] ints = (int[])o;
            if (ints.length==1)
                return ints[0];
        }
        throw new MetadataException("Tag '" + tagType + "' cannot be cast to int.  It is of type '" + o.getClass() + "'.");
    }

    // TODO get Array methods need to return cloned data, to maintain this directory's integrity

    /**
     * Gets the specified tag's value as a String array, if possible.  Only supported
     * where the tag is set as String[], String, int[], byte[] or Rational[].
     * @param tagType the tag identifier
     * @return the tag's value as an array of Strings
     * @throws MetadataException if the tag has not been set or cannot be represented
     *         as a String[]
     */
    public String[] getStringArray(int tagType) throws MetadataException
    {
        Object o = getObject(tagType);
        if (o==null) {
            throw new MetadataException("Tag " + getTagName(tagType) + " has not been set -- check using containsTag() first");
        } else if (o instanceof String[]) {
            return (String[])o;
        } else if (o instanceof String) {
            String[] strings = {(String)o};
            return strings;
        } else if (o instanceof int[]) {
            int[] ints = (int[])o;
            String[] strings = new String[ints.length];
            for (int i = 0; i<strings.length; i++) {
                strings[i] = Integer.toString(ints[i]);
            }
            return strings;
        } else if (o instanceof byte[]) {
            byte[] bytes = (byte[])o;
            String[] strings = new String[bytes.length];
            for (int i = 0; i<strings.length; i++) {
                strings[i] = Byte.toString(bytes[i]);
            }
            return strings;
        } else if (o instanceof Rational[]) {
            Rational[] rationals = (Rational[])o;
            String[] strings = new String[rationals.length];
            for (int i = 0; i<strings.length; i++) {
                strings[i] = rationals[i].toSimpleString(false);
            }
            return strings;
        }
        throw new MetadataException("Tag '" + tagType + "' cannot be cast to an String array.  It is of type '" + o.getClass() + "'.");
    }

    /**
     * Gets the specified tag's value as an int array, if possible.  Only supported
     * where the tag is set as String, int[], byte[] or Rational[].
     * @param tagType the tag identifier
     * @return the tag's value as an int array
     * @throws MetadataException if the tag has not been set, or cannot be converted to
     *         an int array
     */
    public int[] getIntArray(int tagType) throws MetadataException
    {
        Object o = getObject(tagType);
        if (o==null) {
            throw new MetadataException("Tag " + getTagName(tagType) + " has not been set -- check using containsTag() first");
        } else if (o instanceof Rational[]) {
            Rational[] rationals = (Rational[])o;
            int[] ints = new int[rationals.length];
            for (int i = 0; i<ints.length; i++) {
                ints[i] = rationals[i].intValue();
            }
            return ints;
        } else if (o instanceof int[]) {
            return (int[])o;
        } else if (o instanceof byte[]) {
            byte[] bytes = (byte[])o;
            int[] ints = new int[bytes.length];
            for (int i = 0; i<bytes.length; i++) {
                byte b = bytes[i];
                ints[i] = b;
            }
            return ints;
        } else if (o instanceof String) {
            String str = (String)o;
            int[] ints = new int[str.length()];
            for (int i = 0; i<str.length(); i++) {
                ints[i] = str.charAt(i);
            }
            return ints;
        }
        throw new MetadataException("Tag '" + tagType + "' cannot be cast to an int array.  It is of type '" + o.getClass() + "'.");
    }

    /**
     * Gets the specified tag's value as an byte array, if possible.  Only supported
     * where the tag is set as String, int[], byte[] or Rational[].
     * @param tagType the tag identifier
     * @return the tag's value as a byte array
     * @throws MetadataException if the tag has not been set, or cannot be converted to
     *         a byte array
     */
    public byte[] getByteArray(int tagType) throws MetadataException
    {
        Object o = getObject(tagType);
        if (o==null) {
            throw new MetadataException("Tag " + getTagName(tagType) + " has not been set -- check using containsTag() first");
        } else if (o instanceof Rational[]) {
            Rational[] rationals = (Rational[])o;
            byte[] bytes = new byte[rationals.length];
            for (int i = 0; i<bytes.length; i++) {
                bytes[i] = rationals[i].byteValue();
            }
            return bytes;
        } else if (o instanceof byte[]) {
            return (byte[])o;
        } else if (o instanceof int[]) {
            int[] ints = (int[])o;
            byte[] bytes = new byte[ints.length];
            for (int i = 0; i<ints.length; i++) {
                bytes[i] = (byte)ints[i];
            }
            return bytes;
        } else if (o instanceof String) {
            String str = (String)o;
            byte[] bytes = new byte[str.length()];
            for (int i = 0; i<str.length(); i++) {
                bytes[i] = (byte)str.charAt(i);
            }
            return bytes;
        }
        throw new MetadataException("Tag '" + tagType + "' cannot be cast to a byte array.  It is of type '" + o.getClass() + "'.");
    }

    /**
     * Returns the specified tag's value as a double, if possible.
     */
    public double getDouble(int tagType) throws MetadataException
    {
        Object o = getObject(tagType);
        if (o==null) {
            throw new MetadataException("Tag " + getTagName(tagType) + " has not been set -- check using containsTag() first");
        } else if (o instanceof String) {
            try {
                return Double.parseDouble((String)o);
            } catch (NumberFormatException nfe) {
                throw new MetadataException("unable to parse string " + o + " as a double", nfe);
            }
        } else if (o instanceof Number) {
            return ((Number)o).doubleValue();
        }
        throw new MetadataException("Tag '" + tagType + "' cannot be cast to a double.  It is of type '" + o.getClass() + "'.");
    }

    /**
     * Returns the specified tag's value as a float, if possible.
     */
    public float getFloat(int tagType) throws MetadataException
    {
        Object o = getObject(tagType);
        if (o==null) {
            throw new MetadataException("Tag " + getTagName(tagType) + " has not been set -- check using containsTag() first");
        } else if (o instanceof String) {
            try {
                return Float.parseFloat((String)o);
            } catch (NumberFormatException nfe) {
                throw new MetadataException("unable to parse string " + o + " as a float", nfe);
            }
        } else if (o instanceof Number) {
            return ((Number)o).floatValue();
        }
        throw new MetadataException("Tag '" + tagType + "' cannot be cast to a float.  It is of type '" + o.getClass() + "'.");
    }

    /**
     * Returns the specified tag's value as a long, if possible.
     */
    public long getLong(int tagType) throws MetadataException
    {
        Object o = getObject(tagType);
        if (o==null) {
            throw new MetadataException("Tag " + getTagName(tagType) + " has not been set -- check using containsTag() first");
        } else if (o instanceof String) {
            try {
                return Long.parseLong((String)o);
            } catch (NumberFormatException nfe) {
                throw new MetadataException("unable to parse string " + o + " as a long", nfe);
            }
        } else if (o instanceof Number) {
            return ((Number)o).longValue();
        }
        throw new MetadataException("Tag '" + tagType + "' cannot be cast to a long.  It is of type '" + o.getClass() + "'.");
    }

    /**
     * Returns the specified tag's value as a boolean, if possible.
     */
    public boolean getBoolean(int tagType) throws MetadataException
    {
        Object o = getObject(tagType);
        if (o==null) {
            throw new MetadataException("Tag " + getTagName(tagType) + " has not been set -- check using containsTag() first");
        } else if (o instanceof Boolean) {
            return ((Boolean)o).booleanValue();
        } else if (o instanceof String) {
            try {
                return Boolean.getBoolean((String)o);
            } catch (NumberFormatException nfe) {
                throw new MetadataException("unable to parse string " + o + " as a boolean", nfe);
            }
        } else if (o instanceof Number) {
            return (((Number)o).doubleValue()!=0);
        }
        throw new MetadataException("Tag '" + tagType + "' cannot be cast to a boolean.  It is of type '" + o.getClass() + "'.");
    }

    /**
     * Returns the specified tag's value as a java.util.Date, if possible.
     */
    public java.util.Date getDate(int tagType) throws MetadataException
    {
        Object o = getObject(tagType);
        if (o==null) {
            throw new MetadataException("Tag " + getTagName(tagType) + " has not been set -- check using containsTag() first");
        } else if (o instanceof java.util.Date) {
            return (java.util.Date)o;
        } else if (o instanceof String) {
            // add new dateformat strings to make this method even smarter
            // so far, this seems to cover all known date strings
            // (for example, AM and PM strings are not supported...)
            String datePatterns[] = {
                "yyyy:MM:dd HH:mm:ss",
                "yyyy:MM:dd HH:mm",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm"};
            String dateString = (String)o;
            for (int i = 0; i<datePatterns.length; i++) {
                try {
                    DateFormat parser = new java.text.SimpleDateFormat(datePatterns[i]);
                    return parser.parse(dateString);
                } catch (java.text.ParseException ex) {
                    // simply try the next pattern
                }
            }
        }
        throw new MetadataException("Tag '" + tagType + "' cannot be cast to a java.util.Date.  It is of type '" + o.getClass() + "'.");
    }

    /**
     * Returns the specified tag's value as a Rational, if possible.
     */
    public Rational getRational(int tagType) throws MetadataException
    {
        Object o = getObject(tagType);
        if (o==null) {
            throw new MetadataException("Tag " + getTagName(tagType) + " has not been set -- check using containsTag() first");
        } else if (o instanceof Rational) {
            return (Rational)o;
        }
        throw new MetadataException("Tag '" + tagType + "' cannot be cast to a Rational.  It is of type '" + o.getClass() + "'.");
    }

    public Rational[] getRationalArray(int tagType) throws MetadataException
    {
        Object o = getObject(tagType);
        if (o==null) {
            throw new MetadataException("Tag " + getTagName(tagType) + " has not been set -- check using containsTag() first");
        } else if (o instanceof Rational[]) {
            return (Rational[])o;
        }
        throw new MetadataException("Tag '" + tagType + "' cannot be cast to a Rational array.  It is of type '" + o.getClass() + "'.");
    }

    /**
     * Returns the specified tag's value as a String.  This value is the 'raw' value.  A more presentable decoding
     * of this value may be obtained from the corresponding Descriptor.
     * @return the String reprensentation of the tag's value, or
     *         <code>null</code> if the tag hasn't been defined.
     */
    public String getString(int tagType)
    {
        Object o = getObject(tagType);
        if (o==null)
            return null;

        if (o instanceof Rational)
            return ((Rational)o).toSimpleString(true);

        if (o.getClass().isArray())
        {
            // handle arrays of objects and primitives
            int arrayLength = Array.getLength(o);
            // determine if this is an array of objects i.e. [Lcom.drew.blah
            boolean isObjectArray = o.getClass().toString().startsWith("class [L");
            StringBuffer sbuffer = new StringBuffer();
            for (int i = 0; i<arrayLength; i++)
            {
                if (i!=0)
                    sbuffer.append(' ');
                if (isObjectArray)
                    sbuffer.append(Array.get(o, i).toString());
                else
                    sbuffer.append(Array.getInt(o, i));
            }
            return sbuffer.toString();
        }

        return o.toString();
    }

    /**
     * Returns the object hashed for the particular tag type specified, if available.
     * @param tagType the tag type identifier
     * @return the tag's value as an Object if available, else null
     */
    public Object getObject(int tagType)
    {
        return _tagMap.get(new Integer(tagType));
    }

// OTHER METHODS

    /**
     * Returns the name of a specified tag as a String.
     * @param tagType the tag type identifier
     * @return the tag's name as a String
     */
    public String getTagName(int tagType)
    {
        Integer key = new Integer(tagType);
        HashMap nameMap = getTagNameMap();
        if (!nameMap.containsKey(key)) {
            String hex = Integer.toHexString(tagType);
            while (hex.length()<4) {
                hex = "0" + hex;
            }
            return "Unknown tag (0x" + hex + ")";
        }
        return (String)nameMap.get(key);
    }

    /**
     * Provides a description of a tag's value using the descriptor set by
     * <code>setDescriptor(Descriptor)</code>.
     * @param tagType the tag type identifier
     * @return the tag value's description as a String
     * @throws MetadataException if a descriptor hasn't been set, or if an error
     * occurs during calculation of the description within the Descriptor
     */
    public String getDescription(int tagType) throws MetadataException
    {
        if (_descriptor==null) {
            throw new MetadataException("a descriptor must be set using setDescriptor(...) before descriptions can be provided");
        }

        return _descriptor.getDescription(tagType);
    }
}

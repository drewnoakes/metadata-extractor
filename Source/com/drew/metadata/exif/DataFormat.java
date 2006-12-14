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
 */
package com.drew.metadata.exif;

import com.drew.metadata.MetadataException;

/**
 * An enumeration of data formats used in the TIFF IFDs.
 */
public class DataFormat
{
    public static final DataFormat BYTE = new DataFormat("BYTE", 1);
    public static final DataFormat STRING = new DataFormat("STRING", 2);
    public static final DataFormat USHORT = new DataFormat("USHORT", 3);
    public static final DataFormat ULONG = new DataFormat("ULONG", 4);
    public static final DataFormat URATIONAL = new DataFormat("URATIONAL", 5);
    public static final DataFormat SBYTE = new DataFormat("SBYTE", 6);
    public static final DataFormat UNDEFINED = new DataFormat("UNDEFINED", 7);
    public static final DataFormat SSHORT = new DataFormat("SSHORT", 8);
    public static final DataFormat SLONG = new DataFormat("SLONG", 9);
    public static final DataFormat SRATIONAL = new DataFormat("SRATIONAL", 10);
    public static final DataFormat SINGLE = new DataFormat("SINGLE", 11);
    public static final DataFormat DOUBLE = new DataFormat("DOUBLE", 12);

    private final String myName;
    private final int value;

    public static DataFormat fromValue(int value) throws MetadataException
    {
        switch (value)
        {
            case 1:  return BYTE;
            case 2:  return STRING;
            case 3:  return USHORT;
            case 4:  return ULONG;
            case 5:  return URATIONAL;
            case 6:  return SBYTE;
            case 7:  return UNDEFINED;
            case 8:  return SSHORT;
            case 9:  return SLONG;
            case 10: return SRATIONAL;
            case 11: return SINGLE;
            case 12: return DOUBLE;
        }

        throw new MetadataException("value '"+value+"' does not represent a known data format.");
    }

    private DataFormat(String name, int value)
    {
        myName = name;
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public String toString()
    {
        return myName;
    }
}

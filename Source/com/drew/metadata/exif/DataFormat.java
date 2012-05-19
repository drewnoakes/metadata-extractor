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
package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.MetadataException;

/**
 * An enumeration of data formats used in the TIFF IFDs.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class DataFormat
{
    @NotNull public static final DataFormat BYTE = new DataFormat("BYTE", 1);
    @NotNull public static final DataFormat STRING = new DataFormat("STRING", 2);
    @NotNull public static final DataFormat USHORT = new DataFormat("USHORT", 3);
    @NotNull public static final DataFormat ULONG = new DataFormat("ULONG", 4);
    @NotNull public static final DataFormat URATIONAL = new DataFormat("URATIONAL", 5);
    @NotNull public static final DataFormat SBYTE = new DataFormat("SBYTE", 6);
    @NotNull public static final DataFormat UNDEFINED = new DataFormat("UNDEFINED", 7);
    @NotNull public static final DataFormat SSHORT = new DataFormat("SSHORT", 8);
    @NotNull public static final DataFormat SLONG = new DataFormat("SLONG", 9);
    @NotNull public static final DataFormat SRATIONAL = new DataFormat("SRATIONAL", 10);
    @NotNull public static final DataFormat SINGLE = new DataFormat("SINGLE", 11);
    @NotNull public static final DataFormat DOUBLE = new DataFormat("DOUBLE", 12);

    @NotNull private final String _name;
    private final int _value;

    @NotNull
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

    private DataFormat(@NotNull String name, int value)
    {
        _name = name;
        _value = value;
    }

    public int getValue()
    {
        return _value;
    }

    @NotNull
    public String toString()
    {
        return _name;
    }
}

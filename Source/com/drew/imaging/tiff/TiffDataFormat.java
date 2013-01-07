/*
 * Copyright 2002-2013 Drew Noakes
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
package com.drew.imaging.tiff;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.MetadataException;

/**
 * An enumeration of data formats used by the TIFF specification.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class TiffDataFormat
{
    @NotNull public static final TiffDataFormat INT8_U = new TiffDataFormat("BYTE", 1, 1);
    @NotNull public static final TiffDataFormat STRING = new TiffDataFormat("STRING", 2, 1);
    @NotNull public static final TiffDataFormat INT16_U = new TiffDataFormat("USHORT", 3, 2);
    @NotNull public static final TiffDataFormat INT32_U = new TiffDataFormat("ULONG", 4, 4);
    @NotNull public static final TiffDataFormat RATIONAL_U = new TiffDataFormat("URATIONAL", 5, 8);
    @NotNull public static final TiffDataFormat INT8_S = new TiffDataFormat("SBYTE", 6, 1);
    @NotNull public static final TiffDataFormat UNDEFINED = new TiffDataFormat("UNDEFINED", 7, 1);
    @NotNull public static final TiffDataFormat INT16_S = new TiffDataFormat("SSHORT", 8, 2);
    @NotNull public static final TiffDataFormat INT32_S = new TiffDataFormat("SLONG", 9, 4);
    @NotNull public static final TiffDataFormat RATIONAL_S = new TiffDataFormat("SRATIONAL", 10, 8);
    @NotNull public static final TiffDataFormat SINGLE = new TiffDataFormat("SINGLE", 11, 4);
    @NotNull public static final TiffDataFormat DOUBLE = new TiffDataFormat("DOUBLE", 12, 8);

    @NotNull
    private final String _name;
    private final int _tiffFormatCode;
    private final int _componentSizeBytes;

    public static boolean isValidTiffFormatCode(int tiffFormatCode)
    {
        return tiffFormatCode > 0 && tiffFormatCode < 13;
    }

    @NotNull
    public static TiffDataFormat fromTiffFormatCode(int tiffFormatCode) throws MetadataException
    {
        switch (tiffFormatCode) {
            case 1: return INT8_U;
            case 2: return STRING;
            case 3: return INT16_U;
            case 4: return INT32_U;
            case 5: return RATIONAL_U;
            case 6: return INT8_S;
            case 7: return UNDEFINED;
            case 8: return INT16_S;
            case 9: return INT32_S;
            case 10: return RATIONAL_S;
            case 11: return SINGLE;
            case 12: return DOUBLE;
        }

        throw new MetadataException("Unknown TIFF format code: " + tiffFormatCode);
    }

    private TiffDataFormat(@NotNull String name, int tiffFormatCode, int componentSizeBytes)
    {
        _name = name;
        _tiffFormatCode = tiffFormatCode;
        _componentSizeBytes = componentSizeBytes;
    }

    public int getComponentSizeBytes()
    {
        return _componentSizeBytes;
    }

    public int getTiffFormatCode()
    {
        return _tiffFormatCode;
    }

    @NotNull
    public String toString()
    {
        return _name;
    }
}

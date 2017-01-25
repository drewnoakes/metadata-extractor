/*
 * Copyright 2002-2017 Drew Noakes
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
package com.drew.imaging.tiff;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

/**
 * An enumeration of data formats used by the TIFF specification.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class TiffDataFormat
{
    public static final int CODE_INT8_U = 1;
    public static final int CODE_STRING = 2;
    public static final int CODE_INT16_U = 3;
    public static final int CODE_INT32_U = 4;
    public static final int CODE_RATIONAL_U = 5;
    public static final int CODE_INT8_S = 6;
    public static final int CODE_UNDEFINED = 7;
    public static final int CODE_INT16_S = 8;
    public static final int CODE_INT32_S = 9;
    public static final int CODE_RATIONAL_S = 10;
    public static final int CODE_SINGLE = 11;
    public static final int CODE_DOUBLE = 12;

    @NotNull public static final TiffDataFormat INT8_U = new TiffDataFormat("BYTE", CODE_INT8_U, 1);
    @NotNull public static final TiffDataFormat STRING = new TiffDataFormat("STRING", CODE_STRING, 1);
    @NotNull public static final TiffDataFormat INT16_U = new TiffDataFormat("USHORT", CODE_INT16_U, 2);
    @NotNull public static final TiffDataFormat INT32_U = new TiffDataFormat("ULONG", CODE_INT32_U, 4);
    @NotNull public static final TiffDataFormat RATIONAL_U = new TiffDataFormat("URATIONAL", CODE_RATIONAL_U, 8);
    @NotNull public static final TiffDataFormat INT8_S = new TiffDataFormat("SBYTE", CODE_INT8_S, 1);
    @NotNull public static final TiffDataFormat UNDEFINED = new TiffDataFormat("UNDEFINED", CODE_UNDEFINED, 1);
    @NotNull public static final TiffDataFormat INT16_S = new TiffDataFormat("SSHORT", CODE_INT16_S, 2);
    @NotNull public static final TiffDataFormat INT32_S = new TiffDataFormat("SLONG", CODE_INT32_S, 4);
    @NotNull public static final TiffDataFormat RATIONAL_S = new TiffDataFormat("SRATIONAL", CODE_RATIONAL_S, 8);
    @NotNull public static final TiffDataFormat SINGLE = new TiffDataFormat("SINGLE", CODE_SINGLE, 4);
    @NotNull public static final TiffDataFormat DOUBLE = new TiffDataFormat("DOUBLE", CODE_DOUBLE, 8);

    @NotNull
    private final String _name;
    private final int _tiffFormatCode;
    private final int _componentSizeBytes;

    @Nullable
    public static TiffDataFormat fromTiffFormatCode(int tiffFormatCode)
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
        return null;
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

    @Override
    @NotNull
    public String toString()
    {
        return _name;
    }
}

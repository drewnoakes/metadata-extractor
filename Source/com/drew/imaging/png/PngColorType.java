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
package com.drew.imaging.png;

import com.drew.lang.annotations.NotNull;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PngColorType
{
    /**
     * Each pixel is a greyscale sample.
     */
    public static final PngColorType GREYSCALE = new PngColorType(0, "Greyscale", 1,2,4,8,16);

    /**
     * Each pixel is an R,G,B triple.
     */
    public static final PngColorType TRUE_COLOR = new PngColorType(2, "True Color", 8,16);

    /**
     * Each pixel is a palette index. Seeing this value indicates that a <code>PLTE</code> chunk shall appear.
     */
    public static final PngColorType INDEXED_COLOR = new PngColorType(3, "Indexed Color", 1,2,4,8);

    /**
     * Each pixel is a greyscale sample followed by an alpha sample.
     */
    public static final PngColorType GREYSCALE_WITH_ALPHA = new PngColorType(4, "Greyscale with Alpha", 8,16);

    /**
     * Each pixel is an R,G,B triple followed by an alpha sample.
     */
    public static final PngColorType TRUE_COLOR_WITH_ALPHA = new PngColorType(6, "True Color with Alpha", 8,16);

    @NotNull
    public static PngColorType fromNumericValue(int numericValue)
    {
        switch (numericValue) {
            case 0: return GREYSCALE;
            case 2: return TRUE_COLOR;
            case 3: return INDEXED_COLOR;
            case 4: return GREYSCALE_WITH_ALPHA;
            case 6: return TRUE_COLOR_WITH_ALPHA;
        }
        return new PngColorType(numericValue, "Unknown (" + numericValue + ")");
    }

    private final int _numericValue;
    @NotNull private final String _description;
    @NotNull private final int[] _allowedBitDepths;

    private PngColorType(int numericValue, @NotNull String description, @NotNull int... allowedBitDepths)
    {
        _numericValue = numericValue;
        _description = description;
        _allowedBitDepths = allowedBitDepths;
    }

    public int getNumericValue()
    {
        return _numericValue;
    }

    @NotNull
    public String getDescription()
    {
        return _description;
    }

    @NotNull
    public int[] getAllowedBitDepths()
    {
        return _allowedBitDepths;
    }
}

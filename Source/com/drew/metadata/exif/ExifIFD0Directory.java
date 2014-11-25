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

package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes Exif tags from the IFD0 directory.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ExifIFD0Directory extends Directory
{
    public static final int TAG_IMAGE_DESCRIPTION = 0x010E;
    public static final int TAG_MAKE = 0x010F;
    public static final int TAG_MODEL = 0x0110;
    public static final int TAG_ORIENTATION = 0x0112;
    public static final int TAG_X_RESOLUTION = 0x011A;
    public static final int TAG_Y_RESOLUTION = 0x011B;
    public static final int TAG_RESOLUTION_UNIT = 0x0128;
    public static final int TAG_SOFTWARE = 0x0131;
    public static final int TAG_DATETIME = 0x0132;
    public static final int TAG_ARTIST = 0x013B;
    public static final int TAG_WHITE_POINT = 0x013E;
    public static final int TAG_PRIMARY_CHROMATICITIES = 0x013F;

    public static final int TAG_YCBCR_COEFFICIENTS = 0x0211;
    public static final int TAG_YCBCR_POSITIONING = 0x0213;
    public static final int TAG_REFERENCE_BLACK_WHITE = 0x0214;


    /** This tag is a pointer to the Exif SubIFD. */
    public static final int TAG_EXIF_SUB_IFD_OFFSET = 0x8769;

    /** This tag is a pointer to the Exif GPS IFD. */
    public static final int TAG_GPS_INFO_OFFSET = 0x8825;

    public static final int TAG_COPYRIGHT = 0x8298;

    /** Non-standard, but in use. */
    public static final int TAG_TIME_ZONE_OFFSET = 0x882a;

    /** The image title, as used by Windows XP. */
    public static final int TAG_WIN_TITLE = 0x9C9B;
    /** The image comment, as used by Windows XP. */
    public static final int TAG_WIN_COMMENT = 0x9C9C;
    /** The image author, as used by Windows XP (called Artist in the Windows shell). */
    public static final int TAG_WIN_AUTHOR = 0x9C9D;
    /** The image keywords, as used by Windows XP. */
    public static final int TAG_WIN_KEYWORDS = 0x9C9E;
    /** The image subject, as used by Windows XP. */
    public static final int TAG_WIN_SUBJECT = 0x9C9F;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_IMAGE_DESCRIPTION, "Image Description");
        _tagNameMap.put(TAG_MAKE, "Make");
        _tagNameMap.put(TAG_MODEL, "Model");
        _tagNameMap.put(TAG_ORIENTATION, "Orientation");
        _tagNameMap.put(TAG_X_RESOLUTION, "X Resolution");
        _tagNameMap.put(TAG_Y_RESOLUTION, "Y Resolution");
        _tagNameMap.put(TAG_RESOLUTION_UNIT, "Resolution Unit");
        _tagNameMap.put(TAG_SOFTWARE, "Software");
        _tagNameMap.put(TAG_DATETIME, "Date/Time");
        _tagNameMap.put(TAG_ARTIST, "Artist");
        _tagNameMap.put(TAG_WHITE_POINT, "White Point");
        _tagNameMap.put(TAG_PRIMARY_CHROMATICITIES, "Primary Chromaticities");
        _tagNameMap.put(TAG_YCBCR_COEFFICIENTS, "YCbCr Coefficients");
        _tagNameMap.put(TAG_YCBCR_POSITIONING, "YCbCr Positioning");
        _tagNameMap.put(TAG_REFERENCE_BLACK_WHITE, "Reference Black/White");

        _tagNameMap.put(TAG_COPYRIGHT, "Copyright");

        _tagNameMap.put(TAG_TIME_ZONE_OFFSET, "Time Zone Offset");

        _tagNameMap.put(TAG_WIN_AUTHOR, "Windows XP Author");
        _tagNameMap.put(TAG_WIN_COMMENT, "Windows XP Comment");
        _tagNameMap.put(TAG_WIN_KEYWORDS, "Windows XP Keywords");
        _tagNameMap.put(TAG_WIN_SUBJECT, "Windows XP Subject");
        _tagNameMap.put(TAG_WIN_TITLE, "Windows XP Title");
    }

    public ExifIFD0Directory()
    {
        this.setDescriptor(new ExifIFD0Descriptor(this));
    }

    @NotNull
    public String getName()
    {
        return "Exif IFD0";
    }

    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}

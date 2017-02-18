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
package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Describes Exif tags from the SubIFD directory.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class ExifSubIFDDirectory extends ExifDirectoryBase
{
    /** This tag is a pointer to the Exif Interop IFD. */
    public static final int TAG_INTEROP_OFFSET = 0xA005;

    public ExifSubIFDDirectory()
    {
        this.setDescriptor(new ExifSubIFDDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        addExifTagNames(_tagNameMap);
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Exif SubIFD";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    /**
     * Parses the date/time tag and the subsecond tag to obtain a single Date object with milliseconds
     * representing the date and time when this image was captured.  Attempts will be made to parse the
     * values as though it is in the GMT {@link TimeZone}.
     *
     * @return A Date object representing when this image was captured, if possible, otherwise null
     */
    @Nullable
    public Date getDateOriginal()
    {
        return getDateOriginal(null);
    }

    /**
     * Parses the date/time tag and the subsecond tag to obtain a single Date object with milliseconds
     * representing the date and time when this image was captured.  Attempts will be made to parse the
     * values as though it is in the {@link TimeZone} represented by the {@code timeZone} parameter
     * (if it is non-null).
     *
     * @param timeZone the time zone to use
     * @return A Date object representing when this image was captured, if possible, otherwise null
     */
    @Nullable
    public Date getDateOriginal(@Nullable TimeZone timeZone)
    {
        return getDate(TAG_DATETIME_ORIGINAL, getString(TAG_SUBSECOND_TIME_ORIGINAL), timeZone);
    }

    /**
     * Parses the date/time tag and the subsecond tag to obtain a single Date object with milliseconds
     * representing the date and time when this image was digitized.  Attempts will be made to parse the
     * values as though it is in the GMT {@link TimeZone}.
     *
     * @return A Date object representing when this image was digitized, if possible, otherwise null
     */
    @Nullable
    public Date getDateDigitized()
    {
        return getDateDigitized(null);
    }

    /**
     * Parses the date/time tag and the subsecond tag to obtain a single Date object with milliseconds
     * representing the date and time when this image was digitized.  Attempts will be made to parse the
     * values as though it is in the {@link TimeZone} represented by the {@code timeZone} parameter
     * (if it is non-null).
     *
     * @param timeZone the time zone to use
     * @return A Date object representing when this image was digitized, if possible, otherwise null
     */
    @Nullable
    public Date getDateDigitized(@Nullable TimeZone timeZone)
    {
        return getDate(TAG_DATETIME_DIGITIZED, getString(TAG_SUBSECOND_TIME_DIGITIZED), timeZone);
    }
}

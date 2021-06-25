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
package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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

    public ExifSubIFDDirectory(@NotNull MetadataContext context)
    {
        this.setDescriptor(new ExifSubIFDDescriptor(this, context));
    }

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

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
     * Parses the date/time tag, the subsecond tag and the time offset tag to obtain a single Date
     * object with milliseconds representing the date and time when this image was modified.  If
     * the time offset tag does not exist, attempts will be made to parse the values as though it is
     * in the GMT {@link TimeZone}.
     *
     * @return A Date object representing when this image was modified, if possible, otherwise null
     */
    @Nullable
    public Date getDateModified()
    {
        return getDateModified(null);
    }

    /**
     * Parses the date/time tag, the subsecond tag and the time offset tag to obtain a single Date
     * object with milliseconds representing the date and time when this image was modified.  If
     * the time offset tag does not exist, attempts will be made to parse the values as though it is
     * in the {@link TimeZone} represented by the {@code timeZone} parameter (if it is non-null).
     *
     * @param timeZone the time zone to use
     * @return A Date object representing when this image was modified, if possible, otherwise null
     */
    @Nullable
    public Date getDateModified(@Nullable TimeZone timeZone)
    {
        Directory parent = getParent();
        if (parent instanceof ExifIFD0Directory) {
            TimeZone timeZoneModified = getTimeZone(TAG_TIME_ZONE);
            return parent.getDate(TAG_DATETIME, getString(TAG_SUBSECOND_TIME),
                (timeZoneModified != null) ? timeZoneModified : timeZone);
        } else {
            return null;
        }
    }

    /**
     * Parses the date/time tag, the subsecond tag and the time offset tag to obtain a single Date
     * object with milliseconds representing the date and time when this image was captured.  If
     * the time offset tag does not exist, attempts will be made to parse the values as though it is
     * in the GMT {@link TimeZone}.
     *
     * @return A Date object representing when this image was captured, if possible, otherwise null
     */
    @Nullable
    public Date getDateOriginal()
    {
        return getDateOriginal(null);
    }

    /**
     * Parses the date/time tag, the subsecond tag and the time offset tag to obtain a single Date
     * object with milliseconds representing the date and time when this image was captured.  If
     * the time offset tag does not exist, attempts will be made to parse the values as though it is
     * in the {@link TimeZone} represented by the {@code timeZone} parameter (if it is non-null).
     *
     * @param timeZone the time zone to use
     * @return A Date object representing when this image was captured, if possible, otherwise null
     */
    @Nullable
    public Date getDateOriginal(@Nullable TimeZone timeZone)
    {
        TimeZone timeZoneOriginal = getTimeZone(TAG_TIME_ZONE_ORIGINAL);
        return getDate(TAG_DATETIME_ORIGINAL, getString(TAG_SUBSECOND_TIME_ORIGINAL),
            (timeZoneOriginal != null) ? timeZoneOriginal : timeZone);
    }

    /**
     * Parses the date/time tag, the subsecond tag and the time offset tag to obtain a single Date
     * object with milliseconds representing the date and time when this image was digitized.  If
     * the time offset tag does not exist, attempts will be made to parse the values as though it is
     * in the GMT {@link TimeZone}.
     *
     * @return A Date object representing when this image was digitized, if possible, otherwise null
     */
    @Nullable
    public Date getDateDigitized()
    {
        return getDateDigitized(null);
    }

    /**
     * Parses the date/time tag, the subsecond tag and the time offset tag to obtain a single Date
     * object with milliseconds representing the date and time when this image was digitized.  If
     * the time offset tag does not exist, attempts will be made to parse the values as though it is
     * in the {@link TimeZone} represented by the {@code timeZone} parameter (if it is non-null).
     *
     * @param timeZone the time zone to use
     * @return A Date object representing when this image was digitized, if possible, otherwise null
     */
    @Nullable
    public Date getDateDigitized(@Nullable TimeZone timeZone)
    {
        TimeZone timeZoneDigitized = getTimeZone(TAG_TIME_ZONE_DIGITIZED);
        return getDate(TAG_DATETIME_DIGITIZED, getString(TAG_SUBSECOND_TIME_DIGITIZED),
            (timeZoneDigitized != null) ? timeZoneDigitized : timeZone);
    }

    @Nullable
    private TimeZone getTimeZone(int tagType)
    {
        String timeOffset = getString(tagType);
        if (timeOffset != null && timeOffset.matches("[\\+\\-]\\d\\d:\\d\\d")) {
            return TimeZone.getTimeZone("GMT" + timeOffset);
        } else {
            return null;
        }
    }
}

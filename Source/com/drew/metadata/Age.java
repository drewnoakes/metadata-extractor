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

package com.drew.metadata;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

/**
 * Represents an age in years, months, days, hours, minutes and seconds.
 * <p>
 * Used by certain Panasonic cameras which have face recognition features.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class Age
{
    private final int _years;
    private final int _months;
    private final int _days;
    private final int _hours;
    private final int _minutes;
    private final int _seconds;

    /**
     * Parses an age object from the string format used by Panasonic cameras:
     * <code>0031:07:15 00:00:00</code>
     *
     * @param s The String in format <code>0031:07:15 00:00:00</code>.
     * @return The parsed Age object, or null if the value could not be parsed
     */
    @Nullable
    public static Age fromPanasonicString(@NotNull String s)
    {
        if (s.length() != 19 || s.startsWith("9999:99:99"))
            return null;

        try {
            int years = Integer.parseInt(s.substring(0, 4));
            int months = Integer.parseInt(s.substring(5, 7));
            int days = Integer.parseInt(s.substring(8, 10));
            int hours = Integer.parseInt(s.substring(11, 13));
            int minutes = Integer.parseInt(s.substring(14, 16));
            int seconds = Integer.parseInt(s.substring(17, 19));

            return new Age(years, months, days, hours, minutes, seconds);
        }
        catch (NumberFormatException ignored)
        {
            return null;
        }
    }

    public Age(int years, int months, int days, int hours, int minutes, int seconds)
    {
        _years = years;
        _months = months;
        _days = days;
        _hours = hours;
        _minutes = minutes;
        _seconds = seconds;
    }

    public int getYears()
    {
        return _years;
    }

    public int getMonths()
    {
        return _months;
    }

    public int getDays()
    {
        return _days;
    }

    public int getHours()
    {
        return _hours;
    }

    public int getMinutes()
    {
        return _minutes;
    }

    public int getSeconds()
    {
        return _seconds;
    }

    @Override
    public String toString()
    {
        return String.format("%04d:%02d:%02d %02d:%02d:%02d", _years, _months, _days, _hours, _minutes, _seconds);
    }

    public String toFriendlyString()
    {
        StringBuilder result = new StringBuilder();
        appendAgePart(result, _years, "year");
        appendAgePart(result, _months, "month");
        appendAgePart(result, _days, "day");
        appendAgePart(result, _hours, "hour");
        appendAgePart(result, _minutes, "minute");
        appendAgePart(result, _seconds, "second");
        return result.toString();
    }

    private static void appendAgePart(StringBuilder result, final int num, final String singularName)
    {
        if (num == 0)
            return;
        if (result.length()!=0)
            result.append(' ');
        result.append(num).append(' ').append(singularName);
        if (num != 1)
            result.append('s');
    }

    @Override
    public boolean equals(@Nullable Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Age age = (Age)o;

        if (_days != age._days) return false;
        if (_hours != age._hours) return false;
        if (_minutes != age._minutes) return false;
        if (_months != age._months) return false;
        if (_seconds != age._seconds) return false;
        if (_years != age._years) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = _years;
        result = 31 * result + _months;
        result = 31 * result + _days;
        result = 31 * result + _hours;
        result = 31 * result + _minutes;
        result = 31 * result + _seconds;
        return result;
    }
}

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
package com.drew.lang;

import java.util.Date;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class DateUtil
{
    private static int[] _daysInMonth365 = new int[] {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /**
     * The offset (in milliseconds) to add to a MP4 date/time integer value to
     * align with Java's Epoch.
     */
    private static final long EPOCH_1_JAN_1904 = -2082844799175L;

    public static boolean isValidDate(int year, int month, int day)
    {
        if (year < 1 || year > 9999 || month < 0 || month > 11)
            return false;

        int daysInMonth = _daysInMonth365[month];
        if (month == 1)
        {
            boolean isLeapYear = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
            if (isLeapYear)
                daysInMonth++;
        }

        return day >= 1 && day <= daysInMonth;
    }

    public static boolean isValidTime(int hours, int minutes, int seconds)
    {
        return hours >= 0 && hours < 24
            && minutes >= 0 && minutes < 60
            && seconds >= 0 && seconds < 60;
    }

    public static Date get1Jan1904EpochDate(long seconds)
    {
        return new Date((seconds * 1000) + EPOCH_1_JAN_1904);
    }
}

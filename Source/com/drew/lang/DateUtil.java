package com.drew.lang;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class DateUtil
{
    private static int[] _daysInMonth365 = new int[] {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

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
}

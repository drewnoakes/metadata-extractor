// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp;

import java.util.Calendar;
import java.util.TimeZone;


/**
 * The <code>XMPDateTime</code>-class represents a point in time up to a resolution of nano
 * seconds. Dates and time in the serialized XMP are ISO 8601 strings. There are utility functions
 * to convert to the ISO format, a <code>Calendar</code> or get the Timezone. The fields of
 * <code>XMPDateTime</code> are:
 * <ul>
 * <li> month - The month in the range 1..12.
 * <li> day - The day of the month in the range 1..31.
 * <li> minute - The minute in the range 0..59.
 * <li> hour - The time zone hour in the range 0..23.
 * <li> minute - The time zone minute in the range 0..59.
 * <li> nanoSecond - The nano seconds within a second. <em>Note:</em> if the XMPDateTime is
 * converted into a calendar, the resolution is reduced to milli seconds.
 * <li> timeZone - a <code>TimeZone</code>-object.
 * </ul>
 * DateTime values are occasionally used in cases with only a date or only a time component. A date
 * without a time has zeros for all the time fields. A time without a date has zeros for all date
 * fields (year, month, and day).
 */
public interface XMPDateTime extends Comparable
{
	/** @return Returns the year, can be negative. */
	int getYear();
	
	/** @param year Sets the year */
	void setYear(int year);

	/** @return Returns The month in the range 1..12. */
	int getMonth();

	/** @param month Sets the month 1..12 */
	void setMonth(int month);
	
	/** @return Returns the day of the month in the range 1..31. */
	int getDay();
	
	/** @param day Sets the day 1..31 */
	void setDay(int day);

	/** @return Returns hour - The hour in the range 0..23. */
	int getHour();

	/** @param hour Sets the hour in the range 0..23. */
	void setHour(int hour);
	
	/** @return Returns the minute in the range 0..59. */ 
	int getMinute();

	/** @param minute Sets the minute in the range 0..59. */
	void setMinute(int minute);
	
	/** @return Returns the second in the range 0..59. */
	int getSecond();

	/** @param second Sets the second in the range 0..59. */
	void setSecond(int second);
	
	/**
	 * @return Returns milli-, micro- and nano seconds.
	 * 		   Nanoseconds within a second, often left as zero?
	 */
	int getNanoSecond();

	/**
	 * @param nanoSecond Sets the milli-, micro- and nano seconds.
	 *		Granularity goes down to milli seconds. 		   
	 */
	void setNanoSecond(int nanoSecond);
	
	/** @return Returns the time zone. */
	TimeZone getTimeZone();

	/** @param tz a time zone to set */
	void setTimeZone(TimeZone tz);
	
	/** 
	 * @return Returns a <code>Calendar</code> (only with milli second precision). <br>
	 *  		<em>Note:</em> the dates before Oct 15th 1585 (which normally fall into validity of 
	 *  		the Julian calendar) are also rendered internally as Gregorian dates. 
	 */
	Calendar getCalendar();
	
	/**
	 * @return Returns the ISO 8601 string representation of the date and time.
	 */
	String getISO8601String();
}
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.adobe.xmp.impl.XMPDateTimeImpl;


/**
 * A factory to create <code>XMPDateTime</code>-instances from a <code>Calendar</code> or an
 * ISO 8601 string or for the current time.
 * 
 * @since 16.02.2006
 */
public final class XMPDateTimeFactory
{
	/** The UTC TimeZone */
	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

	

	/** Private constructor */
	private XMPDateTimeFactory()
	{
		// EMPTY
	}


	/**
	 * Creates an <code>XMPDateTime</code> from a <code>Calendar</code>-object.
	 * 
	 * @param calendar a <code>Calendar</code>-object.
	 * @return An <code>XMPDateTime</code>-object.
	 */
	public static XMPDateTime createFromCalendar(Calendar calendar)
	{
		return new XMPDateTimeImpl(calendar);
	}

	
	/**
	 * Creates an <code>XMPDateTime</code>-object from initial values.
	 * @param year years
	 * @param month months from 1 to 12<br>
	 * <em>Note:</em> Remember that the month in {@link Calendar} is defined from 0 to 11.
	 * @param day days
	 * @param hour hours
	 * @param minute minutes
	 * @param second seconds
	 * @param nanoSecond nanoseconds 
	 * @return Returns an <code>XMPDateTime</code>-object.
	 */
	public static XMPDateTime create(int year, int month, int day, 
		int hour, int minute, int second, int nanoSecond)
	{
		XMPDateTime dt = new XMPDateTimeImpl();
		dt.setYear(year);
		dt.setMonth(month);
		dt.setDay(day);
		dt.setHour(hour);
		dt.setMinute(minute);
		dt.setSecond(second);
		dt.setNanoSecond(nanoSecond);
		return dt;
	}
	

	/**
	 * Creates an <code>XMPDateTime</code> from an ISO 8601 string.
	 * 
	 * @param strValue The ISO 8601 string representation of the date/time.
	 * @return An <code>XMPDateTime</code>-object.
	 * @throws XMPException When the ISO 8601 string is non-conform
	 */
	public static XMPDateTime createFromISO8601(String strValue) throws XMPException
	{
		return new XMPDateTimeImpl(strValue);
	}


	/**
	 * Obtain the current date and time.
	 * 
	 * @return Returns The returned time is UTC, properly adjusted for the local time zone. The
	 *         resolution of the time is not guaranteed to be finer than seconds.
	 */
	public static XMPDateTime getCurrentDateTime()
	{
		return new XMPDateTimeImpl(new GregorianCalendar());
	}


	/**
	 * Sets the local time zone without touching any other Any existing time zone value is replaced,
	 * the other date/time fields are not adjusted in any way.
	 * 
	 * @param dateTime the <code>XMPDateTime</code> variable containing the value to be modified.
	 * @return Returns an updated <code>XMPDateTime</code>-object.
	 */
	public static XMPDateTime setLocalTimeZone(XMPDateTime dateTime)
	{
		Calendar cal = dateTime.getCalendar();
		cal.setTimeZone(TimeZone.getDefault());
		return new XMPDateTimeImpl(cal);
	}


	/**
	 * Make sure a time is UTC. If the time zone is not UTC, the time is
	 * adjusted and the time zone set to be UTC.
	 * 
	 * @param dateTime
	 *            the <code>XMPDateTime</code> variable containing the time to
	 *            be modified.
	 * @return Returns an updated <code>XMPDateTime</code>-object.
	 */
	public static XMPDateTime convertToUTCTime(XMPDateTime dateTime)
	{
		long timeInMillis = dateTime.getCalendar().getTimeInMillis();
		GregorianCalendar cal = new GregorianCalendar(UTC);
		cal.setGregorianChange(new Date(Long.MIN_VALUE));		
		cal.setTimeInMillis(timeInMillis);
		return new XMPDateTimeImpl(cal);
	}


	/**
	 * Make sure a time is local. If the time zone is not the local zone, the time is adjusted and
	 * the time zone set to be local.
	 * 
	 * @param dateTime the <code>XMPDateTime</code> variable containing the time to be modified.
	 * @return Returns an updated <code>XMPDateTime</code>-object.
	 */
	public static XMPDateTime convertToLocalTime(XMPDateTime dateTime)
	{
		long timeInMillis = dateTime.getCalendar().getTimeInMillis();
		// has automatically local timezone
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTimeInMillis(timeInMillis);
		return new XMPDateTimeImpl(cal);
	}
}
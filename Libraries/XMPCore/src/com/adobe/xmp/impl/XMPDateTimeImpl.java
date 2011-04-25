// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPException;


/**
 * The implementation of <code>XMPDateTime</code>. Internally a <code>calendar</code> is used
 * plus an additional nano seconds field, because <code>Calendar</code> supports only milli
 * seconds. The <code>nanoSeconds</code> convers only the resolution beyond a milli second.
 * 
 * @since 16.02.2006
 */
public class XMPDateTimeImpl implements XMPDateTime 
{
	/** */
	private int year = 0;
	/** */
	private int month = 0;
	/** */
	private int day = 0;
	/** */
	private int hour = 0;
	/** */
	private int minute = 0;
	/** */
	private int second = 0;
	/** Use the unversal time as default */
	private TimeZone timeZone = TimeZone.getTimeZone("UTC");
	/**
	 * The nano seconds take micro and nano seconds, while the milli seconds are in the calendar.
	 */
	private int nanoSeconds;


	/**
	 * Creates an <code>XMPDateTime</code>-instance with the current time in the default time
	 * zone.
	 */
	public XMPDateTimeImpl()
	{
		// EMPTY
	}

	
	/**
	 * Creates an <code>XMPDateTime</code>-instance from a calendar.
	 * 
	 * @param calendar a <code>Calendar</code>
	 */
	public XMPDateTimeImpl(Calendar calendar)
	{
		// extract the date and timezone from the calendar provided
        Date date = calendar.getTime();
        TimeZone zone = calendar.getTimeZone();

        // put that date into a calendar the pretty much represents ISO8601
        // I use US because it is close to the "locale" for the ISO8601 spec
        GregorianCalendar intCalendar = 
        	(GregorianCalendar) Calendar.getInstance(Locale.US);
        intCalendar.setGregorianChange(new Date(Long.MIN_VALUE));       
        intCalendar.setTimeZone(zone);           
        intCalendar.setTime(date); 		
		
		this.year = intCalendar.get(Calendar.YEAR);
		this.month = intCalendar.get(Calendar.MONTH) + 1; // cal is from 0..12
		this.day = intCalendar.get(Calendar.DAY_OF_MONTH);
		this.hour = intCalendar.get(Calendar.HOUR_OF_DAY);
		this.minute = intCalendar.get(Calendar.MINUTE);
		this.second = intCalendar.get(Calendar.SECOND);
		this.nanoSeconds = intCalendar.get(Calendar.MILLISECOND) * 1000000;
		this.timeZone = intCalendar.getTimeZone();
	}

	
	/**
	 * Creates an <code>XMPDateTime</code>-instance from 
	 * a <code>Date</code> and a <code>TimeZone</code>.
	 * 
	 * @param date a date describing an absolute point in time
	 * @param timeZone a TimeZone how to interpret the date
	 */
	public XMPDateTimeImpl(Date date, TimeZone timeZone)
	{
		GregorianCalendar calendar = new GregorianCalendar(timeZone);
		calendar.setTime(date);
		this.year = calendar.get(Calendar.YEAR);
		this.month = calendar.get(Calendar.MONTH) + 1; // cal is from 0..12
		this.day = calendar.get(Calendar.DAY_OF_MONTH);
		this.hour = calendar.get(Calendar.HOUR_OF_DAY);
		this.minute = calendar.get(Calendar.MINUTE);
		this.second = calendar.get(Calendar.SECOND);
		this.nanoSeconds = calendar.get(Calendar.MILLISECOND) * 1000000;
		this.timeZone = timeZone;
	}

	
	/**
	 * Creates an <code>XMPDateTime</code>-instance from an ISO 8601 string.
	 * 
	 * @param strValue an ISO 8601 string
	 * @throws XMPException If the string is a non-conform ISO 8601 string, an exception is thrown
	 */
	public XMPDateTimeImpl(String strValue) throws XMPException
	{
		ISO8601Converter.parse(strValue, this);
	}


	/**
	 * @see XMPDateTime#getYear()
	 */
	public int getYear()
	{
		return year;
	}

	
	/**
	 * @see XMPDateTime#setYear(int)
	 */
	public void setYear(int year)
	{
		this.year = Math.min(Math.abs(year), 9999);
	}	
	

	/**
	 * @see XMPDateTime#getMonth()
	 */
	public int getMonth()
	{
		return month;
	}


	/**
	 * @see XMPDateTime#setMonth(int)
	 */
	public void setMonth(int month)
	{
		if (month < 1)
		{	
			this.month = 1;
		}
		else if (month > 12)
		{
			this.month = 12;
		}
		else
		{
			this.month = month;
		}
	}

	
	/**
	 * @see XMPDateTime#getDay()
	 */
	public int getDay()
	{
		return day;
	}


	/**
	 * @see XMPDateTime#setDay(int)
	 */
	public void setDay(int day)
	{
		if (day < 1)
		{	
			this.day = 1;
		}
		else if (day > 31)
		{
			this.day = 31;
		}
		else
		{
			this.day = day;
		}
	}
	
	
	/**
	 * @see XMPDateTime#getHour()
	 */
	public int getHour()
	{
		return hour;
	}

	
	/**
	 * @see XMPDateTime#setHour(int)
	 */
	public void setHour(int hour)
	{
		this.hour = Math.min(Math.abs(hour), 23);
	}


	/**
	 * @see XMPDateTime#getMinute()
	 */
	public int getMinute()
	{
		return minute;
	}

	
	/**
	 * @see XMPDateTime#setMinute(int)
	 */
	public void setMinute(int minute)
	{
		this.minute = Math.min(Math.abs(minute), 59);
	}

	
	/**
	 * @see XMPDateTime#getSecond()
	 */
	public int getSecond()
	{
		return second;
	}

	
	/**
	 * @see XMPDateTime#setSecond(int)
	 */
	public void setSecond(int second)
	{
		this.second = Math.min(Math.abs(second), 59);
	}


	/**
	 * @see XMPDateTime#getNanoSecond()
	 */
	public int getNanoSecond()
	{
		return nanoSeconds;
	}

	
	/**
	 * @see XMPDateTime#setNanoSecond(int)
	 */
	public void setNanoSecond(int nanoSecond)
	{
		this.nanoSeconds = nanoSecond;
	}
	

	/**
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(Object dt)
	{
		long d = getCalendar().getTimeInMillis()
				- ((XMPDateTime) dt).getCalendar().getTimeInMillis();
		if (d != 0)
		{
			return (int) (d % 2);
		}
		else
		{
			// if millis are equal, compare nanoseconds
			d = nanoSeconds - ((XMPDateTime) dt).getNanoSecond();
			return (int) (d % 2);
		}
	}


	/**
	 * @see XMPDateTime#getTimeZone()
	 */
	public TimeZone getTimeZone()
	{
		return timeZone;
	}


	/**
	 * @see XMPDateTime#setTimeZone(TimeZone)
	 */
	public void setTimeZone(TimeZone timeZone)
	{
		this.timeZone = timeZone;
	}


	/**
	 * @see XMPDateTime#getCalendar()
	 */
	public Calendar getCalendar()
	{
		GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance(Locale.US);
		calendar.setGregorianChange(new Date(Long.MIN_VALUE));
		calendar.setTimeZone(timeZone);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, nanoSeconds / 1000000);
		return calendar;
	}


	/**
	 * @see XMPDateTime#getISO8601String()
	 */
	public String getISO8601String()
	{
		return ISO8601Converter.render(this);
	}

	
	/**
	 * @return Returns the ISO string representation.
	 */
	public String toString()
	{
		return getISO8601String();
	}
}
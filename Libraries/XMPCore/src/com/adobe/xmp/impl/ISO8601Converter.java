// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.SimpleTimeZone;

import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPError;
import com.adobe.xmp.XMPException;


/**
 * Converts between ISO 8601 Strings and <code>Calendar</code> with millisecond resolution.
 *
 * @since   16.02.2006
 */
public final class ISO8601Converter
{
	/** Hides public constructor */
	private ISO8601Converter()
	{
		// EMPTY
	}

	
	/**
	 * Converts an ISO 8601 string to an <code>XMPDateTime</code>.
	 * 
	 * Parse a date according to ISO 8601 and
	 * http://www.w3.org/TR/NOTE-datetime:
	 * <ul>
	 * <li>YYYY
	 * <li>YYYY-MM
	 * <li>YYYY-MM-DD
	 * <li>YYYY-MM-DDThh:mmTZD
	 * <li>YYYY-MM-DDThh:mm:ssTZD
	 * <li>YYYY-MM-DDThh:mm:ss.sTZD
	 * </ul>
	 * 
	 * Data fields:
	 * <ul>
	 * <li>YYYY = four-digit year
	 * <li>MM = two-digit month (01=January, etc.)
	 * <li>DD = two-digit day of month (01 through 31)
	 * <li>hh = two digits of hour (00 through 23)
	 * <li>mm = two digits of minute (00 through 59)
	 * <li>ss = two digits of second (00 through 59)
	 * <li>s = one or more digits representing a decimal fraction of a second
	 * <li>TZD = time zone designator (Z or +hh:mm or -hh:mm)
	 * </ul>
	 * 
	 * Note that ISO 8601 does not seem to allow years less than 1000 or greater
	 * than 9999. We allow any year, even negative ones. The year is formatted
	 * as "%.4d".
	 * <p>
	 * <em>Note:</em> Tolerate missing TZD, assume is UTC. Photoshop 8 writes
	 * dates like this for exif:GPSTimeStamp.<br>
	 * <em>Note:</em> Tolerate missing date portion, in case someone foolishly
	 * writes a time-only value that way.
	 * 
	 * @param iso8601String a date string that is ISO 8601 conform.
	 * @return Returns a <code>Calendar</code>.
	 * @throws XMPException Is thrown when the string is non-conform.
	 */
	public static XMPDateTime parse(String iso8601String) throws XMPException
	{
		return parse(iso8601String, new XMPDateTimeImpl());
	}
	

	/**
	 * @param iso8601String a date string that is ISO 8601 conform.
	 * @param binValue an existing XMPDateTime to set with the parsed date
	 * @return Returns an XMPDateTime-object containing the ISO8601-date.
	 * @throws XMPException Is thrown when the string is non-conform.
	 */
	public static XMPDateTime parse(String iso8601String, XMPDateTime binValue) throws XMPException
	{
		ParameterAsserts.assertNotNull(iso8601String);
		
		ParseState input = new ParseState(iso8601String);
		int value;
		
		boolean timeOnly =  
			 input.ch(0) == 'T'  ||
			(input.length() >= 2  &&  input.ch(1) == ':'  ||
			(input.length() >= 3  &&  input.ch(2) == ':'));
		
		if (!timeOnly)
		{
			if (input.ch(0) == '-')
			{
				input.skip();
			}

			
			// Extract the year.
			value = input.gatherInt("Invalid year in date string", 9999);
			if (input.hasNext()  &&  input.ch() != '-')
			{
				throw new XMPException("Invalid date string, after year", XMPError.BADVALUE);
			}

			if (input.ch(0) == '-')
			{
				value = -value;
			}
			binValue.setYear(value);
			if (!input.hasNext())
			{
				return binValue;
			}
			input.skip();
			
			
			// Extract the month.
			value = input.gatherInt("Invalid month in date string", 12);
			if (input.hasNext()  &&  input.ch() != '-')
			{
				throw new XMPException("Invalid date string, after month", XMPError.BADVALUE);
			}
			binValue.setMonth(value);
			if (!input.hasNext())
			{
				return binValue;
			}
			input.skip();

			
			// Extract the day.
			value = input.gatherInt("Invalid day in date string", 31);
			if (input.hasNext()  &&  input.ch() != 'T')
			{
				throw new XMPException("Invalid date string, after day", XMPError.BADVALUE);
			}
			binValue.setDay(value);
			if (!input.hasNext())
			{
				return binValue;
			}
		}
		else
		{
			// set default day and month in the year 0000
			binValue.setMonth(1);
			binValue.setDay(1);
		}
			
		if (input.ch() == 'T')
		{
			input.skip();
		}
		else if (!timeOnly)
		{
			throw new XMPException("Invalid date string, missing 'T' after date",
					XMPError.BADVALUE);
		}

		
		// Extract the hour.
		value = input.gatherInt("Invalid hour in date string", 23);
		if (input.ch() != ':')
		{
			throw new XMPException("Invalid date string, after hour", XMPError.BADVALUE);
		}
		binValue.setHour(value);
		
		// Don't check for done, we have to work up to the time zone.
		input.skip();
		
		
		// Extract the minute.
		value = input.gatherInt("Invalid minute in date string", 59);
		if (input.hasNext()  &&
			input.ch() != ':' && input.ch() != 'Z' && input.ch() != '+' && input.ch() != '-')
		{
			throw new XMPException("Invalid date string, after minute", XMPError.BADVALUE);
		}
		binValue.setMinute(value);
		
		if (input.ch() == ':')
		{
			input.skip();
			value = input.gatherInt("Invalid whole seconds in date string", 59);
			if (input.hasNext()  &&  input.ch() != '.'  &&  input.ch() != 'Z'  && 
				input.ch() != '+' && input.ch() != '-')
			{
				throw new XMPException("Invalid date string, after whole seconds",
						XMPError.BADVALUE);
			}
			binValue.setSecond(value);
			if (input.ch() == '.')
			{
				input.skip();
				int digits = input.pos();
				value = input.gatherInt("Invalid fractional seconds in date string", 999999999);
				if (input.ch() != 'Z'  &&  input.ch() != '+'  &&  input.ch() != '-')
				{
					throw new XMPException("Invalid date string, after fractional second",
							XMPError.BADVALUE);
				}
				digits = input.pos() - digits;
				for (; digits > 9; --digits)
				{	
					value = value / 10;
				}	
				for (; digits < 9; ++digits)
				{	
					value = value * 10;
				}	
				binValue.setNanoSecond(value);
			}
		}
		
		int tzSign = 0;
		int tzHour = 0;
		int tzMinute = 0;
		if (input.ch() == 'Z')
		{
			input.skip();
		}
		else if (input.hasNext())
		{
			if (input.ch() == '+')
			{
				tzSign = 1;
			}
			else if (input.ch() == '-')
			{
				tzSign = -1;
			}
			else
			{
				throw new XMPException("Time zone must begin with 'Z', '+', or '-'",
						XMPError.BADVALUE);
			}

			input.skip();
			// Extract the time zone hour.
			tzHour = input.gatherInt("Invalid time zone hour in date string", 23);
			if (input.ch() != ':')
			{
				throw new XMPException("Invalid date string, after time zone hour",
						XMPError.BADVALUE);
			}
			input.skip();

			// Extract the time zone minute.
			tzMinute = input.gatherInt("Invalid time zone minute in date string", 59);
		}
		
		// create a corresponding TZ and set it time zone
		int offset = (tzHour * 3600 * 1000 + tzMinute * 60 * 1000) * tzSign;   
		binValue.setTimeZone(new SimpleTimeZone(offset, ""));
		

		if (input.hasNext())
		{
			throw new XMPException(
				"Invalid date string, extra chars at end", XMPError.BADVALUE);
		}
		
		return binValue;
	}

	
	/**
	 * Converts a <code>Calendar</code> into an ISO 8601 string.
	 * Format a date according to ISO 8601 and http://www.w3.org/TR/NOTE-datetime:
	 * <ul>
	 * <li>YYYY
	 * <li>YYYY-MM
	 * <li>YYYY-MM-DD
	 * <li>YYYY-MM-DDThh:mmTZD
	 * <li>YYYY-MM-DDThh:mm:ssTZD
	 * <li>YYYY-MM-DDThh:mm:ss.sTZD
	 * </ul>
	 * 
	 * Data fields:
	 * <ul>
	 * <li>YYYY = four-digit year
	 * <li>MM	 = two-digit month (01=January, etc.)
	 * <li>DD	 = two-digit day of month (01 through 31)
	 * <li>hh	 = two digits of hour (00 through 23)
	 * <li>mm	 = two digits of minute (00 through 59)
	 * <li>ss	 = two digits of second (00 through 59)
	 * <li>s	 = one or more digits representing a decimal fraction of a second
	 * <li>TZD	 = time zone designator (Z or +hh:mm or -hh:mm)
	 * </ul>
	 * <p>
	 * <em>Note:</em> ISO 8601 does not seem to allow years less than 1000 or greater than 9999. 
	 * We allow any year, even negative ones. The year is formatted as "%.4d".<p>
	 * <em>Note:</em> Fix for bug 1269463 (silently fix out of range values) included in parsing.
	 * The quasi-bogus "time only" values from Photoshop CS are not supported.
	 * 
	 * @param dateTime an XMPDateTime-object.
	 * @return Returns an ISO 8601 string.
	 */
	public static String render(XMPDateTime dateTime)
	{
		StringBuffer buffer = new StringBuffer();

		// year is rendered in any case, even 0000
		DecimalFormat df = new DecimalFormat("0000", new DecimalFormatSymbols(Locale.ENGLISH));
		buffer.append(df.format(dateTime.getYear()));
		if (dateTime.getMonth() == 0)
		{
			return buffer.toString();
		}

		// month
		df.applyPattern("'-'00");
		buffer.append(df.format(dateTime.getMonth()));
		if (dateTime.getDay() == 0)
		{
			return buffer.toString();
		}

		// day
		buffer.append(df.format(dateTime.getDay()));
		
		// time, rendered if any time field is not zero
		if (dateTime.getHour() != 0  ||
			dateTime.getMinute() != 0  ||
			dateTime.getSecond() != 0  ||
			dateTime.getNanoSecond() != 0  ||
			(dateTime.getTimeZone() != null  &&  dateTime.getTimeZone().getRawOffset() != 0))
		{
			// hours and minutes
			buffer.append('T');
			df.applyPattern("00");
			buffer.append(df.format(dateTime.getHour()));
			buffer.append(':');
			buffer.append(df.format(dateTime.getMinute()));
			
			// seconds and nanoseconds
			if (dateTime.getSecond() != 0 || dateTime.getNanoSecond() != 0)
			{
				double seconds = dateTime.getSecond() + dateTime.getNanoSecond() / 1e9d;

				df.applyPattern(":00.#########");
				buffer.append(df.format(seconds));
			}
			
			// time zone
			if (dateTime.getTimeZone() != null)
			{
				// used to calculate the time zone offset incl. Daylight Savings
				long timeInMillis = dateTime.getCalendar().getTimeInMillis();
				int offset = dateTime.getTimeZone().getOffset(timeInMillis);				
				if (offset == 0)
				{
					// UTC
					buffer.append('Z');
				}
				else
				{
					int thours = offset / 3600000;
					int tminutes = Math.abs(offset % 3600000 / 60000);
					df.applyPattern("+00;-00");
					buffer.append(df.format(thours));
					df.applyPattern(":00");
					buffer.append(df.format(tminutes));
				}	
			}
		}	
		return buffer.toString();
	}
	
	
}


/**
 * @since   22.08.2006
 */
class ParseState
{
	/** */
	private String str;
	/** */
	private int pos = 0;

	
	/**
	 * @param str initializes the parser container
	 */
	public ParseState(String str)
	{
		this.str = str;
	}
	
	
	/**
	 * @return Returns the length of the input.
	 */
	public int length()
	{
		return str.length();
	}


	/**
	 * @return Returns whether there are more chars to come.
	 */
	public boolean hasNext()
	{
		return pos < str.length();
	}

	
	/**
	 * @param index index of char
	 * @return Returns char at a certain index.
	 */
	public char ch(int index)
	{
		return index < str.length() ? 
			str.charAt(index) :
			0x0000;
	}

	
	/**
	 * @return Returns the current char or 0x0000 if there are no more chars.
	 */
	public char ch()
	{
		return pos < str.length() ? 
			str.charAt(pos) :
			0x0000;
	}
	
	
	/**
	 * Skips the next char.
	 */
	public void skip()
	{
		pos++;
	}

	
	/**
	 * @return Returns the current position.
	 */
	public int pos()
	{
		return pos;
	}
	
	
	/**
	 * Parses a integer from the source and sets the pointer after it.
	 * @param errorMsg Error message to put in the exception if no number can be found
	 * @param maxValue the max value of the number to return 
	 * @return Returns the parsed integer.
	 * @throws XMPException Thrown if no integer can be found.
	 */
	public int gatherInt(String errorMsg, int maxValue) throws XMPException
	{
		int value = 0;
		boolean success = false;
		char ch = ch(pos);
		while ('0' <= ch  &&  ch <= '9')
		{
			value = (value * 10) + (ch - '0');
			success = true;
			pos++;
			ch = ch(pos);
		}
		
		if (success)
		{
			if (value > maxValue)
			{
				return maxValue;
			}
			else if (value < 0)
			{
				return 0;
			}
			else
			{	
				return value;
			}	
		}
		else
		{
			throw new XMPException(errorMsg, XMPError.BADVALUE);
		}
	}
}	



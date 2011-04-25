// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;


/**
 * @since   22.08.2006
 */
public class FixASCIIControlsReader extends PushbackReader
{
	/** */
	private static final int STATE_START = 0;
	/** */
	private static final int STATE_AMP = 1;
	/** */
	private static final int STATE_HASH = 2;
	/** */
	private static final int STATE_HEX = 3;
	/** */
	private static final int STATE_DIG1 = 4;
	/** */
	private static final int STATE_ERROR = 5;
	/** */
	private static final int BUFFER_SIZE = 8;
	/** the state of the automaton */
	private int state = STATE_START;
	/** the result of the escaping sequence */
	private int control = 0;
	/** count the digits of the sequence */
	private int digits = 0; 
	
	/**
	 * The look-ahead size is 6 at maximum (&amp;#xAB;)
	 * @see PushbackReader#PushbackReader(Reader, int)
	 * @param in a Reader
	 */
	public FixASCIIControlsReader(Reader in)
	{
		super(in, BUFFER_SIZE);
	}

	
	/**
	 * @see Reader#read(char[], int, int)
	 */
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		int readAhead = 0;
		int read = 0;
		int pos = off;
		char[] readAheadBuffer = new char[BUFFER_SIZE];
		
		boolean available = true;
		while (available  &&  read < len)
		{
			available = super.read(readAheadBuffer, readAhead, 1) == 1;
			if (available)
			{
				char c = processChar(readAheadBuffer[readAhead]);
				if (state == STATE_START)
				{
					// replace control chars with space
					if (Utils.isControlChar(c))
					{	
						c = ' ';
					}	
					cbuf[pos++] = c;
					readAhead = 0;
					read++;
				}
				else if (state == STATE_ERROR)
				{
					unread(readAheadBuffer, 0, readAhead + 1);
					readAhead = 0;
				}
				else
				{
					readAhead++;
				}
			}
			else if (readAhead > 0)
			{
				// handles case when file ends within excaped sequence
				unread(readAheadBuffer, 0, readAhead);
				state = STATE_ERROR;
				readAhead = 0;
				available = true;
			}
		}
		
		
		return read > 0  ||  available ? read : -1; 
	}
	
	
	/**
	 * Processes numeric escaped chars to find out if they are a control character.
	 * @param ch a char
	 * @return Returns the char directly or as replacement for the escaped sequence.
	 */
	private char processChar(char ch)
	{
		switch (state)
		{
			case STATE_START:
				if (ch == '&')
				{
					state = STATE_AMP;
				}
				return ch;
				
			case STATE_AMP:
				if (ch == '#')
				{
					state = STATE_HASH;
				}
				else
				{
					state = STATE_ERROR;
				}	
				return ch;
				
			case STATE_HASH:
				if (ch == 'x')
				{
					control = 0;
					digits = 0;
					state = STATE_HEX;
				}
				else if ('0' <= ch  &&  ch <= '9')
				{	
					control = Character.digit(ch, 10);
					digits = 1;
					state = STATE_DIG1;
				}
				else
				{
					state = STATE_ERROR;
				}
				return ch;
				
			case STATE_DIG1:
				if ('0' <= ch  &&  ch <= '9')
				{	
					control = control * 10 + Character.digit(ch, 10);
					digits++;
					if (digits <= 5)
					{	
						state = STATE_DIG1;
					}
					else
					{
						state = STATE_ERROR; // sequence too long
					}
				}
				else if (ch == ';'  &&  Utils.isControlChar((char) control))
				{
					state = STATE_START;
					return (char) control;
				}
				else
				{
					state = STATE_ERROR;
				}	
				return ch;
				
			case STATE_HEX:
				if (('0' <= ch  &&  ch <= '9')  ||
					('a' <= ch  &&  ch <= 'f')  ||
					('A' <= ch  &&  ch <= 'F'))
				{	
					control = control * 16 + Character.digit(ch, 16);
					digits++;
					if (digits <= 4)
					{	
						state = STATE_HEX;
					}
					else
					{
						state = STATE_ERROR; // sequence too long
					}
				}
				else if (ch == ';'  &&   Utils.isControlChar((char) control))
				{
					state = STATE_START;
					return (char) control;
				}
				else
				{
					state = STATE_ERROR;
				}	
				return ch;

			case STATE_ERROR:
				state = STATE_START;
				return ch;
				
			default:
				// not reachable
				return ch;
		}
	}
}

// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp;

/**
 * This exception wraps all errors that occur in the XMP Toolkit.
 * 
 * @since   16.02.2006
 */
public class XMPException extends Exception
{
	/** the errorCode of the XMP toolkit */
	private int errorCode;


	/**
	 * Constructs an exception with a message and an error code. 
	 * @param message the message
	 * @param errorCode the error code
	 */
	public XMPException(String message, int errorCode)
	{
		super(message);
		this.errorCode = errorCode;
	}


	/**
	 * Constructs an exception with a message, an error code and a <code>Throwable</code>
	 * @param message the error message.
	 * @param errorCode the error code
	 * @param t the exception source
	 */
	public XMPException(String message, int errorCode, Throwable t)
	{
		super(message, t);
		this.errorCode = errorCode;
	}


	/**
	 * @return Returns the errorCode.
	 */
	public int getErrorCode()
	{
		return errorCode;
	}
}
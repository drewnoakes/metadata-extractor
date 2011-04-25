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
import java.io.OutputStream;


/**
 * An <code>OutputStream</code> that counts the written bytes.
 * 
 * @since   08.11.2006
 */
public final class CountOutputStream extends OutputStream
{
	/** the decorated output stream */
	private final OutputStream out;
	/** the byte counter */
	private int bytesWritten = 0;


	/**
	 * Constructor with providing the output stream to decorate.
	 * @param out an <code>OutputStream</code>
	 */
	CountOutputStream(OutputStream out)
	{
		this.out = out;
	}


	/**
	 * Counts the written bytes.
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public void write(byte[] buf, int off, int len) throws IOException
	{
		out.write(buf, off, len);
		bytesWritten += len;
	}


	/**
	 * Counts the written bytes.
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write(byte[] buf) throws IOException
	{
		out.write(buf);
		bytesWritten += buf.length;
	}


	/**
	 * Counts the written bytes.
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException
	{
		out.write(b);
		bytesWritten++;
	}


	/**
	 * @return the bytesWritten
	 */
	public int getBytesWritten()
	{
		return bytesWritten;
	}
}
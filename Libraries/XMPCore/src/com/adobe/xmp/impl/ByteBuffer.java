// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================



package com.adobe.xmp.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Byte buffer container including length of valid data.
 * 
 * @since   11.10.2006
 */
public class ByteBuffer
{
	/** */
	private byte[] buffer;
	/** */
	private int length;
	/** */
	private String encoding = null;
	

	/**
	 * @param initialCapacity the initial capacity for this buffer
	 */
	public ByteBuffer(int initialCapacity)
	{
		this.buffer = new byte[initialCapacity];
		this.length = 0;
	}

	
	/**
	 * @param buffer a byte array that will be wrapped with <code>ByteBuffer</code>.
	 */
	public ByteBuffer(byte[] buffer)
	{
		this.buffer = buffer;
		this.length = buffer.length;
	}
	
	
	/**
	 * @param buffer a byte array that will be wrapped with <code>ByteBuffer</code>.
	 * @param length the length of valid bytes in the array
	 */
	public ByteBuffer(byte[] buffer, int length)
	{
		if (length > buffer.length)
		{
			throw new ArrayIndexOutOfBoundsException("Valid length exceeds the buffer length.");
		}
		this.buffer = buffer;
		this.length = length;
	}

	
	/**
	 * Loads the stream into a buffer. 
	 * 
	 * @param in an InputStream
	 * @throws IOException If the stream cannot be read.
	 */
	public ByteBuffer(InputStream in) throws IOException
	{
		// load stream into buffer
		int chunk = 16384;
		this.length = 0;
		this.buffer = new byte[chunk];
		
		int read;
		while ((read = in.read(this.buffer, this.length, chunk)) > 0)
		{
			this.length += read;
			if (read == chunk)
			{
				ensureCapacity(length + chunk);
			}
			else
			{
				break;
			}
		}
	}	
	
	
	/**
	 * @param buffer a byte array that will be wrapped with <code>ByteBuffer</code>.
	 * @param offset the offset of the provided buffer.
	 * @param length the length of valid bytes in the array
	 */
	public ByteBuffer(byte[] buffer, int offset, int length)
	{
		if (length > buffer.length - offset)
		{
			throw new ArrayIndexOutOfBoundsException("Valid length exceeds the buffer length.");
		}
		this.buffer = new byte[length];
		System.arraycopy(buffer, offset, this.buffer, 0, length);
		this.length = length;
	}
	
	
	/**
	 * @return Returns a byte stream that is limited to the valid amount of bytes.
	 */
	public InputStream getByteStream()
	{
		return new ByteArrayInputStream(buffer, 0, length);
	}
	
	
	/**
	 * @return Returns the length, that means the number of valid bytes, of the buffer;
	 * the inner byte array might be bigger than that.
	 */
	public int length()
	{
		return length;
	}


//	/**
//	 * <em>Note:</em> Only the byte up to length are valid!
//	 * @return Returns the inner byte buffer.
//	 */
//	public byte[] getBuffer()
//	{
//		return buffer;
//	}

	
	/**
	 * @param index the index to retrieve the byte from
	 * @return Returns a byte from the buffer
	 */
	public byte byteAt(int index)
	{
		if (index < length)
		{	
			return buffer[index];
		}
		else
		{
			throw new IndexOutOfBoundsException("The index exceeds the valid buffer area");
		}
	}

	
	/**
	 * @param index the index to retrieve a byte as int or char.
	 * @return Returns a byte from the buffer
	 */
	public int charAt(int index)
	{
		if (index < length)
		{	
			return buffer[index] & 0xFF;
		}
		else
		{
			throw new IndexOutOfBoundsException("The index exceeds the valid buffer area");
		}
	}
	
	
	/**
	 * Appends a byte to the buffer.
	 * @param b a byte
	 */
	public void append(byte b)
	{
		ensureCapacity(length + 1);
		buffer[length++] = b;
	}
	
	
	/**
	 * Appends a byte array or part of to the buffer.
	 * 
	 * @param bytes a byte array
	 * @param offset an offset with
	 * @param len
	 */
	public void append(byte[] bytes, int offset, int len)
	{
		ensureCapacity(length + len);
		System.arraycopy(bytes, offset, buffer, length, len);
		length += len;
	}
	
	
	/**
	 * Append a byte array to the buffer 
	 * @param bytes a byte array
	 */
	public void append(byte[] bytes)
	{
		append(bytes, 0, bytes.length);
	}
	
	
	/**
	 * Append another buffer to this buffer. 
	 * @param anotherBuffer another <code>ByteBuffer</code>
	 */
	public void append(ByteBuffer anotherBuffer)
	{
		append(anotherBuffer.buffer, 0, anotherBuffer.length); 
	}
	
	
	/**
	 * Detects the encoding of the byte buffer, stores and returns it. 
	 * Only UTF-8, UTF-16LE/BE and UTF-32LE/BE are recognized.
	 * <em>Note:</em> UTF-32 flavors are not supported by Java, the XML-parser will complain.
	 *
	 * @return Returns the encoding string.
	 */
	public String getEncoding()
	{
		if (encoding == null)
		{	
			// needs four byte at maximum to determine encoding
			if (length < 2)
			{
				// only one byte length must be UTF-8
				encoding = "UTF-8";
			}
			else if (buffer[0] == 0)
			{
				// These cases are:
				//   00 nn -- -- - Big endian UTF-16
				//   00 00 00 nn - Big endian UTF-32
				//   00 00 FE FF - Big endian UTF 32
		
				if (length < 4  ||  buffer[1] != 0)
				{
					encoding =  "UTF-16BE";
				}
				else if ((buffer[2] & 0xFF) == 0xFE  &&  (buffer[3] & 0xFF) == 0xFF)
				{
					encoding = "UTF-32BE";
				}
				else
				{
					encoding = "UTF-32";
				}
			} 
			else if ((buffer[0] & 0xFF) < 0x80)
			{
				// These cases are:
				//   nn mm -- -- - UTF-8, includes EF BB BF case
				//   nn 00 -- -- - Little endian UTF-16
		
				if (buffer[1] != 0)
				{
					encoding = "UTF-8";
				}
				else if (length < 4  ||  buffer[2] != 0) 
				{
					encoding = "UTF-16LE";
				}
				else
				{
					encoding = "UTF-32LE";
				}
			}
			else
			{
				// These cases are:
				//   EF BB BF -- - UTF-8
				//   FE FF -- -- - Big endian UTF-16
				//   FF FE 00 00 - Little endian UTF-32
				//   FF FE -- -- - Little endian UTF-16
		
				if ((buffer[0] & 0xFF) == 0xEF)
				{
					encoding = "UTF-8";
				}
				else if ((buffer[0] & 0xFF) == 0xFE)
				{
					encoding = "UTF-16"; // in fact BE 
				}
				else if (length < 4  ||  buffer[2] != 0)
				{
					encoding = "UTF-16"; // in fact LE
				}
				else
				{
					encoding = "UTF-32"; // in fact LE
				}
			}
		}
		
		return encoding;
	}
	
	
	/**
	 * Ensures the requested capacity by increasing the buffer size when the
	 * current length is exceeded.
	 * 
	 * @param requestedLength requested new buffer length
	 */
	private void ensureCapacity(int requestedLength)
	{
		if (requestedLength > buffer.length)
		{
			byte[] oldBuf = buffer;
			buffer = new byte[oldBuf.length * 2];
			System.arraycopy(oldBuf, 0, buffer, 0, oldBuf.length);
		}
	}
}
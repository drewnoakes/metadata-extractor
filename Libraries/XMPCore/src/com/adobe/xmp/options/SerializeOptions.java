// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.options;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;


/**
 * Options for {@link XMPMetaFactory#serializeToBuffer(XMPMeta, SerializeOptions)}.
 * 
 * @since 24.01.2006
 */
public final class SerializeOptions extends Options
{
	/** Omit the XML packet wrapper. */
	public static final int OMIT_PACKET_WRAPPER = 0x0010;
	/** Mark packet as read-only. Default is a writeable packet. */
	public static final int READONLY_PACKET = 0x0020;
	/** Use a compact form of RDF. */
	public static final int USE_COMPACT_FORMAT = 0x0040;
	/**
	 * Include a padding allowance for a thumbnail image. If no <tt>xmp:Thumbnails</tt> property
	 * is present, the typical space for a JPEG thumbnail is used.
	 */
	public static final int INCLUDE_THUMBNAIL_PAD = 0x0100;
	/**
	 * The padding parameter provides the overall packet length. The actual amount of padding is
	 * computed. An exception is thrown if the packet exceeds this length with no padding.
	 */
	public static final int EXACT_PACKET_LENGTH = 0x0200;
	/** Sort the struct properties and qualifier before serializing */
	public static final int SORT = 0x1000;

	// ---------------------------------------------------------------------------------------------
	// encoding bit constants

	/** Bit indicating little endian encoding, unset is big endian */
	private static final int LITTLEENDIAN_BIT = 0x0001;
	/** Bit indication UTF16 encoding. */
	private static final int UTF16_BIT = 0x0002;
	/** UTF8 encoding; this is the default */
	public static final int ENCODE_UTF8 = 0;
	/** UTF16BE encoding */
	public static final int ENCODE_UTF16BE = UTF16_BIT;
	/** UTF16LE encoding */
	public static final int ENCODE_UTF16LE = UTF16_BIT | LITTLEENDIAN_BIT;
	/** */
	private static final int ENCODING_MASK = UTF16_BIT | LITTLEENDIAN_BIT;			

	/**
	 * The amount of padding to be added if a writeable XML packet is created. If zero is passed
	 * (the default) an appropriate amount of padding is computed.
	 */
	private int padding = 2048;
	/**
	 * The string to be used as a line terminator. If empty it defaults to; linefeed, U+000A, the
	 * standard XML newline.
	 */
	private String newline = "\n";
	/**
	 * The string to be used for each level of indentation in the serialized
	 * RDF. If empty it defaults to two ASCII spaces, U+0020.
	 */
	private String indent = "  ";
	/**
	 * The number of levels of indentation to be used for the outermost XML element in the
	 * serialized RDF. This is convenient when embedding the RDF in other text, defaults to 0.
	 */
	private int baseIndent = 0;
	/** Omits the Toolkit version attribute, not published, only used for Unit tests. */
	private boolean omitVersionAttribute = false;
	
	
	/**
	 * Default constructor.
	 */
	public SerializeOptions()
	{
		// reveal default constructor
	}

	
	/**
	 * Constructor using inital options
	 * @param options the inital options
	 * @throws XMPException Thrown if options are not consistant.
	 */
	public SerializeOptions(int options) throws XMPException
	{
		super(options);
	}


	/**
	 * @return Returns the option.
	 */
	public boolean getOmitPacketWrapper()
	{
		return getOption(OMIT_PACKET_WRAPPER);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public SerializeOptions setOmitPacketWrapper(boolean value)
	{
		setOption(OMIT_PACKET_WRAPPER, value);
		return this;
	}


	/**
	 * @return Returns the option.
	 */
	public boolean getReadOnlyPacket()
	{
		return getOption(READONLY_PACKET);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public SerializeOptions setReadOnlyPacket(boolean value)
	{
		setOption(READONLY_PACKET, value);
		return this;
	}


	/**
	 * @return Returns the option.
	 */
	public boolean getUseCompactFormat()
	{
		return getOption(USE_COMPACT_FORMAT);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public SerializeOptions setUseCompactFormat(boolean value)
	{
		setOption(USE_COMPACT_FORMAT, value);
		return this;
	}

	/**
	 * @return Returns the option.
	 */
	public boolean getIncludeThumbnailPad()
	{
		return getOption(INCLUDE_THUMBNAIL_PAD);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public SerializeOptions setIncludeThumbnailPad(boolean value)
	{
		setOption(INCLUDE_THUMBNAIL_PAD, value);
		return this;
	}


	/**
	 * @return Returns the option.
	 */
	public boolean getExactPacketLength()
	{
		return getOption(EXACT_PACKET_LENGTH);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public SerializeOptions setExactPacketLength(boolean value)
	{
		setOption(EXACT_PACKET_LENGTH, value);
		return this;
	}


	/**
	 * @return Returns the option.
	 */
	public boolean getSort()
	{
		return getOption(SORT);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public SerializeOptions setSort(boolean value)
	{
		setOption(SORT, value);
		return this;
	}
	

	/**
	 * @return Returns the option.
	 */
	public boolean getEncodeUTF16BE()
	{
		return (getOptions() & ENCODING_MASK) == ENCODE_UTF16BE;
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public SerializeOptions setEncodeUTF16BE(boolean value)
	{
		// clear unicode bits
		setOption(UTF16_BIT | LITTLEENDIAN_BIT, false);
		setOption(ENCODE_UTF16BE, value);
		return this;
	}


	/**
	 * @return Returns the option.
	 */
	public boolean getEncodeUTF16LE()
	{
		return (getOptions() & ENCODING_MASK) == ENCODE_UTF16LE;
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public SerializeOptions setEncodeUTF16LE(boolean value)
	{
		// clear unicode bits
		setOption(UTF16_BIT | LITTLEENDIAN_BIT, false);
		setOption(ENCODE_UTF16LE, value);
		return this;
	}


	/**
	 * @return Returns the baseIndent.
	 */
	public int getBaseIndent()
	{
		return baseIndent;
	}


	/**
	 * @param baseIndent
	 *            The baseIndent to set.
	 * @return Returns the instance to call more set-methods.
	 */
	public SerializeOptions setBaseIndent(int baseIndent)
	{
		this.baseIndent = baseIndent;
		return this;
	}


	/**
	 * @return Returns the indent.
	 */
	public String getIndent()
	{
		return indent;
	}


	/**
	 * @param indent
	 *            The indent to set.
	 * @return Returns the instance to call more set-methods.
	 */
	public SerializeOptions setIndent(String indent)
	{
		this.indent = indent;
		return this;
	}


	/**
	 * @return Returns the newline.
	 */
	public String getNewline()
	{
		return newline;
	}


	/**
	 * @param newline
	 *            The newline to set.
	 * @return Returns the instance to call more set-methods.
	 */
	public SerializeOptions setNewline(String newline)
	{
		this.newline = newline;
		return this;
	}


	/**
	 * @return Returns the padding.
	 */
	public int getPadding()
	{
		return padding;
	}


	/**
	 * @param padding
	 *            The padding to set.
	 * @return Returns the instance to call more set-methods.
	 */
	public SerializeOptions setPadding(int padding)
	{
		this.padding = padding;
		return this;
	}
	
	
	/**
	 * @return Returns whether the Toolkit version attribute shall be omitted.
	 * <em>Note:</em> This options can only be set by unit tests.
	 */
	public boolean getOmitVersionAttribute()
	{
		return omitVersionAttribute;
	}
	
	
	/**
	 * @return Returns the encoding as Java encoding String. 
	 */
	public String getEncoding()
	{
		if (getEncodeUTF16BE())
		{
			return "UTF-16BE";
		}
		else if (getEncodeUTF16LE())
		{
			return "UTF-16LE";
		}
		else
		{
			return "UTF-8";
		}
	}
	
	
	/**
	 * 
	 * @return Returns clone of this SerializeOptions-object with the same options set. 
	 * @throws CloneNotSupportedException Cannot happen in this place.  
	 */
	public Object clone() throws CloneNotSupportedException
	{
		SerializeOptions clone;
		try
		{
			clone = new SerializeOptions(getOptions());
			clone.setBaseIndent(baseIndent);
			clone.setIndent(indent);
			clone.setNewline(newline);
			clone.setPadding(padding);
			return clone;
		}
		catch (XMPException e)
		{
			// This cannot happen, the options are already checked in "this" object.
			return null;
		}
	}


	/**
	 * @see Options#defineOptionName(int)
	 */
	protected String defineOptionName(int option)
	{
		switch (option)
		{
			case OMIT_PACKET_WRAPPER : 		return "OMIT_PACKET_WRAPPER";
			case READONLY_PACKET :			return "READONLY_PACKET";
			case USE_COMPACT_FORMAT :		return "USE_COMPACT_FORMAT";
			case INCLUDE_THUMBNAIL_PAD :	return "INCLUDE_THUMBNAIL_PAD";
			case EXACT_PACKET_LENGTH :		return "EXACT_PACKET_LENGTH";
			case SORT :				return "NORMALIZED";
			default: 						return null;
		}
	}

	
	/**
	 * @see Options#getValidOptions()
	 */
	protected int getValidOptions()
	{
		return 
		OMIT_PACKET_WRAPPER |
		READONLY_PACKET |
		USE_COMPACT_FORMAT |
		INCLUDE_THUMBNAIL_PAD |
		EXACT_PACKET_LENGTH |
		SORT;
	}
}
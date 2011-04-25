// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl;

/**
 * @since   09.11.2006
 */
public class QName
{
	/** XML namespace prefix */
	private String prefix;
	/** XML localname */
	private String localName;

	
	/**
	 * Splits a qname into prefix and localname.
	 * @param qname a QName
	 */
	public QName(String qname)
	{
		int colon = qname.indexOf(':');
		
		if (colon >= 0)
		{
			prefix = qname.substring(0, colon);
			localName = qname.substring(colon + 1);
		}
		else
		{
			prefix = "";
			localName = qname;
		}
	}
	
	
	/** Constructor that initializes the fields
	 * @param prefix the prefix  
	 * @param localName the name
	 */
	public QName(String prefix, String localName)
	{
		this.prefix = prefix;
		this.localName = localName;
	}

	
	/**
	 * @return Returns whether the QName has a prefix.
	 */
	public boolean hasPrefix()
	{
		return prefix != null  &&  prefix.length() > 0;
	}
	

	/**
	 * @return the localName
	 */
	public String getLocalName()
	{
		return localName;
	}


	/**
	 * @return the prefix
	 */
	public String getPrefix()
	{
		return prefix;
	}
}
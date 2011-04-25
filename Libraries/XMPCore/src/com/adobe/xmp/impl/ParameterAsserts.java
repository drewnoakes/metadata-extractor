// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPError;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;


/**
 * @since   11.08.2006
 */
class ParameterAsserts implements XMPConst
{
	/**
	 * private constructor
	 */
	private ParameterAsserts()
	{
		// EMPTY
	}

	
	/**
	 * Asserts that an array name is set.
	 * @param arrayName an array name
	 * @throws XMPException Array name is null or empty
	 */
	public static void assertArrayName(String arrayName) throws XMPException
	{
		if (arrayName == null  ||  arrayName.length() == 0)
		{
			throw new XMPException("Empty array name", XMPError.BADPARAM);
		}
	}

	
	/**
	 * Asserts that a property name is set.
	 * @param propName a property name or path
	 * @throws XMPException Property name is null or empty
	 */
	public static void assertPropName(String propName) throws XMPException
	{
		if (propName == null  ||  propName.length() == 0)
		{
			throw new XMPException("Empty property name", XMPError.BADPARAM);
		}
	}

	
	/**
	 * Asserts that a schema namespace is set.
	 * @param schemaNS a schema namespace
	 * @throws XMPException Schema is null or empty
	 */
	public static void assertSchemaNS(String schemaNS) throws XMPException
	{
		if (schemaNS == null  ||  schemaNS.length() == 0)
		{
			throw new XMPException("Empty schema namespace URI", XMPError.BADPARAM);
		}
	}

	
	/**
	 * Asserts that a prefix is set.
	 * @param prefix a prefix
	 * @throws XMPException Prefix is null or empty
	 */
	public static void assertPrefix(String prefix) throws XMPException
	{
		if (prefix == null  ||  prefix.length() == 0)
		{
			throw new XMPException("Empty prefix", XMPError.BADPARAM);
		}
	}
	
	
	/**
	 * Asserts that a specific language is set.
	 * @param specificLang a specific lang
	 * @throws XMPException Specific language is null or empty
	 */
	public static void assertSpecificLang(String specificLang) throws XMPException
	{
		if (specificLang == null  ||  specificLang.length() == 0)
		{
			throw new XMPException("Empty specific language", XMPError.BADPARAM);
		}
	}

	
	/**
	 * Asserts that a struct name is set.
	 * @param structName a struct name
	 * @throws XMPException Struct name is null or empty
	 */
	public static void assertStructName(String structName) throws XMPException
	{
		if (structName == null  ||  structName.length() == 0)
		{
			throw new XMPException("Empty array name", XMPError.BADPARAM);
		}
	}


	/**
	 * Asserts that any string parameter is set.
	 * @param param any string parameter
	 * @throws XMPException Thrown if the parameter is null or has length 0.
	 */
	public static void assertNotNull(Object param) throws XMPException
	{
		if (param == null)
		{
			throw new XMPException("Parameter must not be null", XMPError.BADPARAM);
		}
		else if ((param instanceof String)  &&  ((String) param).length() == 0)
		{
			throw new XMPException("Parameter must not be null or empty", XMPError.BADPARAM);
		}
	}


	/**
	 * Asserts that the xmp object is of this implemention
	 * ({@link XMPMetaImpl}). 
	 * @param xmp the XMP object
	 * @throws XMPException A wrong implentaion is used.
	 */
	public static void assertImplementation(XMPMeta xmp) throws XMPException
	{
		if (xmp == null)
		{
			throw new XMPException("Parameter must not be null",
					XMPError.BADPARAM);
		}
		else if (!(xmp instanceof XMPMetaImpl))
		{
			throw new XMPException("The XMPMeta-object is not compatible with this implementation",
					XMPError.BADPARAM);
		}
	}
}
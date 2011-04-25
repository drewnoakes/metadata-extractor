// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.properties;

import com.adobe.xmp.options.AliasOptions;


/**
 * This interface is used to return info about an alias.
 * 
 * @since   27.01.2006
 */
public interface XMPAliasInfo
{
	/**
	 * @return Returns Returns the namespace URI for the base property.
	 */
	String getNamespace();


	/**
	 * @return Returns the default prefix for the given base property. 
	 */
	String getPrefix();

	
	/**
	 * @return Returns the path of the base property.
	 */
	String getPropName();

	
	/**
	 * @return Returns the kind of the alias. This can be a direct alias
	 *         (ARRAY), a simple property to an ordered array
	 *         (ARRAY_ORDERED), to an alternate array
	 *         (ARRAY_ALTERNATE) or to an alternate text array
	 *         (ARRAY_ALT_TEXT).
	 */
	AliasOptions getAliasForm();
}
// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.properties;

import com.adobe.xmp.options.PropertyOptions;


/**
 * This interface is used to return a property together with its path and namespace.
 * It is returned when properties are iterated with the <code>XMPIterator</code>.
 * 
 * @since   06.07.2006
 */
public interface XMPPropertyInfo extends XMPProperty
{
	/**
	 * @return Returns the namespace of the property
	 */
	String getNamespace();

	
	/**
	 * @return Returns the path of the property, but only if returned by the iterator.
	 */
	String getPath();

	
	/**
	 * @return Returns the value of the property.
	 */
	Object getValue();
	
	
	/**
	 * @return Returns the options of the property.
	 */
	PropertyOptions getOptions();
}

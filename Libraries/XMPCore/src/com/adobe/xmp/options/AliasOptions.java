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


/**
 * Options for XMPSchemaRegistryImpl#registerAlias.
 * 
 * @since 20.02.2006
 */
public final class AliasOptions extends Options
{
	/** This is a direct mapping. The actual data type does not matter. */
	public static final int PROP_DIRECT = 0;
	/** The actual is an unordered array, the alias is to the first element of the array. */
	public static final int PROP_ARRAY = PropertyOptions.ARRAY;
	/** The actual is an ordered array, the alias is to the first element of the array. */
	public static final int PROP_ARRAY_ORDERED = PropertyOptions.ARRAY_ORDERED;
	/** The actual is an alternate array, the alias is to the first element of the array. */
	public static final int PROP_ARRAY_ALTERNATE = PropertyOptions.ARRAY_ALTERNATE;
	/**
	 * The actual is an alternate text array, the alias is to the 'x-default' element of the array.
	 */
	public static final int PROP_ARRAY_ALT_TEXT = PropertyOptions.ARRAY_ALT_TEXT;

	
	/**
	 * @see Options#Options()
	 */
	public AliasOptions()
	{
		// EMPTY
	}

	
	/**
	 * @param options the options to init with
	 * @throws XMPException If options are not consistant
	 */
	public AliasOptions(int options) throws XMPException
	{
		super(options);
	}


	/**
	 * @return Returns if the alias is of the simple form.
	 */
	public boolean isSimple()
	{
		return getOptions() == PROP_DIRECT;
	}

	
	/**
	 * @return Returns the option.
	 */
	public boolean isArray()
	{
		return getOption(PROP_ARRAY);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public AliasOptions setArray(boolean value)
	{
		setOption(PROP_ARRAY, value);
		return this;
	}


	/**
	 * @return Returns the option.
	 */
	public boolean isArrayOrdered()
	{
		return getOption(PROP_ARRAY_ORDERED);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public AliasOptions setArrayOrdered(boolean value)
	{
		setOption(PROP_ARRAY | PROP_ARRAY_ORDERED, value);
		return this;
	}


	/**
	 * @return Returns the option.
	 */
	public boolean isArrayAlternate()
	{
		return getOption(PROP_ARRAY_ALTERNATE);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public AliasOptions setArrayAlternate(boolean value)
	{
		setOption(PROP_ARRAY | PROP_ARRAY_ORDERED | PROP_ARRAY_ALTERNATE, value);
		return this;
	}


	/**
	 * @return Returns the option.
	 */
	public boolean isArrayAltText()
	{
		return getOption(PROP_ARRAY_ALT_TEXT);
	}


	/**
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public AliasOptions setArrayAltText(boolean value)
	{
		setOption(PROP_ARRAY | PROP_ARRAY_ORDERED | 
			PROP_ARRAY_ALTERNATE | PROP_ARRAY_ALT_TEXT, value);
		return this;
	}


	/**
	 * @return returns a {@link PropertyOptions}s object
	 * @throws XMPException If the options are not consistant. 
	 */
	public PropertyOptions toPropertyOptions() throws XMPException
	{
		return new PropertyOptions(getOptions());
	}


	/**
	 * @see Options#defineOptionName(int)
	 */
	protected String defineOptionName(int option)
	{
		switch (option)
		{
			case PROP_DIRECT : 			return "PROP_DIRECT";
			case PROP_ARRAY :			return "ARRAY";
			case PROP_ARRAY_ORDERED :	return "ARRAY_ORDERED";
			case PROP_ARRAY_ALTERNATE :	return "ARRAY_ALTERNATE";
			case PROP_ARRAY_ALT_TEXT :	return "ARRAY_ALT_TEXT";
			default: 					return null;
		}
	}

	
	/**
	 * @see Options#getValidOptions()
	 */
	protected int getValidOptions()
	{
		return 
			PROP_DIRECT |
			PROP_ARRAY |
			PROP_ARRAY_ORDERED |
			PROP_ARRAY_ALTERNATE |
			PROP_ARRAY_ALT_TEXT;
	}
}	
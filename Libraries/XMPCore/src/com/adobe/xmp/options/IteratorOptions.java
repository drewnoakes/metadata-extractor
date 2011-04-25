// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.options;


/**
 * Options for <code>XMPIterator</code> construction.
 * 
 * @since 24.01.2006
 */
public final class IteratorOptions extends Options
{
	/** Just do the immediate children of the root, default is subtree. */
	public static final int JUST_CHILDREN = 0x0100;
	/** Just do the leaf nodes, default is all nodes in the subtree. */
	public static final int JUST_LEAFNODES = 0x0200;
	/** Return just the leaf part of the path, default is the full path. */
	public static final int JUST_LEAFNAME = 0x0400;
	/** Include aliases, default is just actual properties. <em>Note:</em> Not supported. 
	 *  @deprecated it is commonly preferred to work with the base properties */
	public static final int INCLUDE_ALIASES = 0x0800;
	/** Omit all qualifiers. */
	public static final int OMIT_QUALIFIERS = 0x1000;


	/**
	 * @return Returns whether the option is set.
	 */
	public boolean isJustChildren()
	{
		return getOption(JUST_CHILDREN);
	}


	/**
	 * @return Returns whether the option is set.
	 */
	public boolean isJustLeafname()
	{
		return getOption(JUST_LEAFNAME);
	}


	/**
	 * @return Returns whether the option is set.
	 */
	public boolean isJustLeafnodes()
	{
		return getOption(JUST_LEAFNODES);
	}


	/**
	 * @return Returns whether the option is set.
	 */
	public boolean isOmitQualifiers()
	{
		return getOption(OMIT_QUALIFIERS);
	}


	/**
	 * Sets the option and returns the instance.
	 * 
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public IteratorOptions setJustChildren(boolean value)
	{
		setOption(JUST_CHILDREN, value);
		return this;
	}


	/**
	 * Sets the option and returns the instance.
	 * 
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public IteratorOptions setJustLeafname(boolean value)
	{
		setOption(JUST_LEAFNAME, value);
		return this;
	}


	/**
	 * Sets the option and returns the instance.
	 * 
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public IteratorOptions setJustLeafnodes(boolean value)
	{
		setOption(JUST_LEAFNODES, value);
		return this;
	}


	/**
	 * Sets the option and returns the instance.
	 * 
	 * @param value the value to set
	 * @return Returns the instance to call more set-methods.
	 */
	public IteratorOptions setOmitQualifiers(boolean value)
	{
		setOption(OMIT_QUALIFIERS, value);
		return this;
	}


	/**
	 * @see Options#defineOptionName(int)
	 */
	protected String defineOptionName(int option)
	{
		switch (option)
		{
			case JUST_CHILDREN : 	return "JUST_CHILDREN";
			case JUST_LEAFNODES :	return "JUST_LEAFNODES";
			case JUST_LEAFNAME :	return "JUST_LEAFNAME";
			case OMIT_QUALIFIERS :	return "OMIT_QUALIFIERS";
			default: 				return null;
		}
	}


	/**
	 * @see Options#getValidOptions()
	 */
	protected int getValidOptions()
	{
		return 
			JUST_CHILDREN |
			JUST_LEAFNODES |
			JUST_LEAFNAME |
			OMIT_QUALIFIERS;
	}
}
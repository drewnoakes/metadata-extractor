// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl.xpath;


/**
 * A segment of a parsed <code>XMPPath</code>.
 *  
 * @since   23.06.2006
 */
public class XMPPathSegment
{
	/** name of the path segment */
	private String name;
	/** kind of the path segment */
	private int kind;
	/** flag if segment is an alias */
	private boolean alias;
	/** alias form if applicable */
	private int aliasForm;


	/**
	 * Constructor with initial values.
	 * 
	 * @param name the name of the segment
	 */
	public XMPPathSegment(String name)
	{
		this.name = name;
	}


	/**
	 * Constructor with initial values.
	 * 
	 * @param name the name of the segment
	 * @param kind the kind of the segment
	 */
	public XMPPathSegment(String name, int kind)
	{
		this.name = name;
		this.kind = kind;
	}


	/**
	 * @return Returns the kind.
	 */
	public int getKind()
	{
		return kind;
	}


	/**
	 * @param kind The kind to set.
	 */
	public void setKind(int kind)
	{
		this.kind = kind;
	}


	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}


	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 * @param alias the flag to set
	 */
	public void setAlias(boolean alias)
	{
		this.alias = alias;
	}


	/**
	 * @return Returns the alias.
	 */
	public boolean isAlias()
	{
		return alias;
	}
	
	
	/** 
	 * @return Returns the aliasForm if this segment has been created by an alias.
	 */ 
	public int getAliasForm()
	{
		return aliasForm;
	}

	
	/**
	 * @param aliasForm the aliasForm to set
	 */
	public void setAliasForm(int aliasForm)
	{
		this.aliasForm = aliasForm;
	}
	
	
	/**
	 * @see Object#toString()
	 */
	public String toString()
	{
		switch (kind)
		{
			case XMPPath.STRUCT_FIELD_STEP:
			case XMPPath.ARRAY_INDEX_STEP: 
			case XMPPath.QUALIFIER_STEP: 
			case XMPPath.ARRAY_LAST_STEP: 
				return name;
			case XMPPath.QUAL_SELECTOR_STEP: 
			case XMPPath.FIELD_SELECTOR_STEP: 
			return name;

		default:
			// no defined step
			return name;
		}
	}
}

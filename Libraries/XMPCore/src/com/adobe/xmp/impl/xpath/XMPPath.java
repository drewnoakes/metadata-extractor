// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl.xpath;

import java.util.ArrayList;
import java.util.List;


/**
 * Representates an XMP XMPPath with segment accessor methods.
 *
 * @since   28.02.2006
 */
public class XMPPath
{
	// Bits for XPathStepInfo options.
	
	/** Marks a struct field step , also for top level nodes (schema "fields"). */
	public static final int STRUCT_FIELD_STEP = 0x01;
	/** Marks a qualifier step. 
	 *  Note: Order is significant to separate struct/qual from array kinds! */
	public static final int QUALIFIER_STEP = 0x02; 		// 
	/** Marks an array index step */
	public static final int ARRAY_INDEX_STEP = 0x03;
	/** */
	public static final int ARRAY_LAST_STEP = 0x04;
	/** */
	public static final int QUAL_SELECTOR_STEP = 0x05;
	/** */
	public static final int FIELD_SELECTOR_STEP = 0x06;
	/** */
	public static final int SCHEMA_NODE = 0x80000000;	
	/** */
	public static final int STEP_SCHEMA = 0;
	/** */
	public static final int STEP_ROOT_PROP = 1;

	
	/** stores the segments of an XMPPath */
	private List segments = new ArrayList(5);
	
	
	/**
	 * Append a path segment
	 * 
	 * @param segment the segment to add
	 */
	public void add(XMPPathSegment segment)
	{	
		segments.add(segment);
	}

	
	/**
	 * @param index the index of the segment to return
	 * @return Returns a path segment.
	 */
	public XMPPathSegment getSegment(int index)
	{
		return (XMPPathSegment) segments.get(index);
	}
	
	
	/**
	 * @return Returns the size of the xmp path. 
	 */
	public int size()
	{
		return segments.size();
	}
	
	
	/**
	 * Serializes the normalized XMP-path.
	 * @see Object#toString()
	 */
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		int index = 1;
		while (index < size())
		{
			result.append(getSegment(index));
			if (index < size() - 1)
			{
				int kind = getSegment(index + 1).getKind(); 
				if (kind == STRUCT_FIELD_STEP  || 
					kind == QUALIFIER_STEP)
				{	
					// all but last and array indices
					result.append('/');
				}	
			}
			index++;			
		}
		
		return result.toString();
	}
}
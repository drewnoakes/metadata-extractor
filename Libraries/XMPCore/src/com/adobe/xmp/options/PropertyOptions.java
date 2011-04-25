// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.options;

import com.adobe.xmp.XMPError;
import com.adobe.xmp.XMPException;


/**
 * The property flags are used when properties are fetched from the <code>XMPMeta</code>-object
 * and provide more detailed information about the property.
 * 
 * @since   03.07.2006
 */
public final class PropertyOptions extends Options
{
	/** */
	public static final int NO_OPTIONS = 0x00000000;
	/** */
	public static final int URI = 0x00000002;
	/** */
	public static final int HAS_QUALIFIERS = 0x00000010;
	/** */
	public static final int QUALIFIER = 0x00000020;
	/** */
	public static final int HAS_LANGUAGE = 0x00000040;
	/** */
	public static final int HAS_TYPE = 0x00000080;
	/** */
	public static final int STRUCT = 0x00000100;
	/** */
	public static final int ARRAY = 0x00000200;
	/** */
	public static final int ARRAY_ORDERED = 0x00000400;
	/** */
	public static final int ARRAY_ALTERNATE = 0x00000800;
	/** */
	public static final int ARRAY_ALT_TEXT = 0x00001000;
	/** */
	public static final int SCHEMA_NODE = 0x80000000;
	/** may be used in the future */
	public static final int DELETE_EXISTING = 0x20000000;
	
	
	/**
	 * Default constructor
	 */
	public PropertyOptions()
	{
		// reveal default constructor
	}
	
	
	/**
	 * Intialization constructor
	 * 
	 * @param options the initialization options
	 * @throws XMPException If the options are not valid 
	 */
	public PropertyOptions(int options) throws XMPException
	{
		super(options);
	}

	
	/**
	 * @return Return whether the property value is a URI. It is serialized to RDF using the
	 *         <tt>rdf:resource</tt> attribute. Not mandatory for URIs, but considered RDF-savvy.
	 */
	public boolean isURI()
	{
		return getOption(URI);
	}

	
	/**
	 * @param value the value to set
	 * @return Returns this to enable cascaded options.
	 */
	public PropertyOptions setURI(boolean value)
	{
		setOption(URI, value);
		return this;
	}
	

	/**
	 * @return Return whether the property has qualifiers. These could be an <tt>xml:lang</tt>
	 *         attribute, an <tt>rdf:type</tt> property, or a general qualifier. See the
	 *         introductory discussion of qualified properties for more information.
	 */
	public boolean getHasQualifiers()
	{
		return getOption(HAS_QUALIFIERS);
	}

	
	/**
	 * @param value the value to set
	 * @return Returns this to enable cascaded options.
	 */
	public PropertyOptions setHasQualifiers(boolean value)
	{
		setOption(HAS_QUALIFIERS, value);
		return this;
	}
	

	/**
	 * @return Return whether this property is a qualifier for some other property. Note that if the
	 *         qualifier itself has a structured value, this flag is only set for the top node of
	 *         the qualifier's subtree. Qualifiers may have arbitrary structure, and may even have
	 *         qualifiers.
	 */
	public boolean isQualifier()
	{
		return getOption(QUALIFIER);
	}

	
	/**
	 * @param value the value to set
	 * @return Returns this to enable cascaded options.
	 */
	public PropertyOptions setQualifier(boolean value)
	{
		setOption(QUALIFIER, value);
		return this;
	}
	

	/** @return Return whether this property has an <tt>xml:lang</tt> qualifier. */
	public boolean getHasLanguage()
	{
		return getOption(HAS_LANGUAGE);
	}

	
	/**
	 * @param value the value to set
	 * @return Returns this to enable cascaded options.
	 */
	public PropertyOptions setHasLanguage(boolean value)
	{
		setOption(HAS_LANGUAGE, value);
		return this;
	}
	

	/** @return Return whether this property has an <tt>rdf:type</tt> qualifier. */
	public boolean getHasType()
	{
		return getOption(HAS_TYPE);
	}

	
	/**
	 * @param value the value to set
	 * @return Returns this to enable cascaded options.
	 */
	public PropertyOptions setHasType(boolean value)
	{
		setOption(HAS_TYPE, value);
		return this;
	}
	

	/** @return Return whether this property contains nested fields. */
	public boolean isStruct()
	{
		return getOption(STRUCT);
	}

	
	/**
	 * @param value the value to set
	 * @return Returns this to enable cascaded options.
	 */
	public PropertyOptions setStruct(boolean value)
	{
		setOption(STRUCT, value);
		return this;
	}
	

	/**
	 * @return Return whether this property is an array. By itself this indicates a general
	 *         unordered array. It is serialized using an <tt>rdf:Bag</tt> container.
	 */
	public boolean isArray()
	{
		return getOption(ARRAY);
	}


	/**
	 * @param value the value to set
	 * @return Returns this to enable cascaded options.
	 */
	public PropertyOptions setArray(boolean value)
	{
		setOption(ARRAY, value);
		return this;
	}
	

	/**
	 * @return Return whether this property is an ordered array. Appears in conjunction with
	 *         getPropValueIsArray(). It is serialized using an <tt>rdf:Seq</tt> container.
	 */
	public boolean isArrayOrdered()
	{
		return getOption(ARRAY_ORDERED);
	}


	/**
	 * @param value the value to set
	 * @return Returns this to enable cascaded options.
	 */
	public PropertyOptions setArrayOrdered(boolean value)
	{
		setOption(ARRAY_ORDERED, value);
		return this;
	}

	
	/**
	 * @return Return whether this property is an alternative array. Appears in conjunction with
	 *         getPropValueIsArray(). It is serialized using an <tt>rdf:Alt</tt> container.
	 */
	public boolean isArrayAlternate()
	{
		return getOption(ARRAY_ALTERNATE);
	}

	
	/**
	 * @param value the value to set
	 * @return Returns this to enable cascaded options.
	 */
	public PropertyOptions setArrayAlternate(boolean value)
	{
		setOption(ARRAY_ALTERNATE, value);
		return this;
	}
	

	/**
	 * @return Return whether this property is an alt-text array. Appears in conjunction with
	 *         getPropArrayIsAlternate(). It is serialized using an <tt>rdf:Alt</tt> container.
	 *         Each array element is a simple property with an <tt>xml:lang</tt> attribute.
	 */
	public boolean isArrayAltText()
	{
		return getOption(ARRAY_ALT_TEXT);
	}

	
	/**
	 * @param value the value to set
	 * @return Returns this to enable cascaded options.
	 */
	public PropertyOptions setArrayAltText(boolean value)
	{
		setOption(ARRAY_ALT_TEXT, value);
		return this;
	}
	

	/**
	 * @param value the value to set
	 * @return Returns this to enable cascaded options.
	 */
	
	
	/**
	 * @return Returns whether the SCHEMA_NODE option is set.
	 */
	public boolean isSchemaNode()
	{
		return getOption(SCHEMA_NODE);
	}


	/**
	 * @param value the option DELETE_EXISTING to set
	 * @return Returns this to enable cascaded options.
	 */
	public PropertyOptions setSchemaNode(boolean value)
	{
		setOption(SCHEMA_NODE, value);
		return this;
	}
	
	
	//-------------------------------------------------------------------------- convenience methods
	
	/**
	 * @return Returns whether the property is of composite type - an array or a struct.
	 */
	public boolean isCompositeProperty()
	{
		return (getOptions() & (ARRAY | STRUCT)) > 0;
	}

	
	/**
	 * @return Returns whether the property is of composite type - an array or a struct.
	 */
	public boolean isSimple()
	{
		return (getOptions() & (ARRAY | STRUCT)) == 0;
	}
	
	
	/**
	 * Compares two options set for array compatibility.
	 * 
	 * @param options other options
	 * @return Returns true if the array options of the sets are equal.
	 */
	public boolean equalArrayTypes(PropertyOptions options)
	{
		return
			isArray()			== options.isArray()  			&&
			isArrayOrdered()	== options.isArrayOrdered()  	&&
			isArrayAlternate()	== options.isArrayAlternate()	&&
			isArrayAltText()	== options.isArrayAltText();
	}
	
	
	
	/**
	 * Merges the set options of a another options object with this.
	 * If the other options set is null, this objects stays the same.
	 * @param options other options
	 * @throws XMPException If illegal options are provided 
	 */
	public void mergeWith(PropertyOptions options) throws XMPException
	{
		if (options != null)
		{	
			setOptions(getOptions() | options.getOptions());
		}
	}


	/**
	 * @return Returns true if only array options are set.
	 */
	public boolean isOnlyArrayOptions()
	{
		return (getOptions() & 
			~(ARRAY | ARRAY_ORDERED | ARRAY_ALTERNATE | ARRAY_ALT_TEXT)) == 0;
	}


	/**
	 * @see Options#getValidOptions()
	 */
	protected int getValidOptions()
	{
		return
			URI |
			HAS_QUALIFIERS |
			QUALIFIER |
			HAS_LANGUAGE |
			HAS_TYPE |
			STRUCT |
			ARRAY |
			ARRAY_ORDERED |
			ARRAY_ALTERNATE |
			ARRAY_ALT_TEXT |
			SCHEMA_NODE;			
	}

	
	/**
	 * @see Options#defineOptionName(int)
	 */
	protected String defineOptionName(int option)
	{
		switch (option)
		{
			case URI : 				return "URI";
			case HAS_QUALIFIERS :	return "HAS_QUALIFIER";
			case QUALIFIER :		return "QUALIFIER";
			case HAS_LANGUAGE :		return "HAS_LANGUAGE";
			case HAS_TYPE:			return "HAS_TYPE";
			case STRUCT :			return "STRUCT";
			case ARRAY :			return "ARRAY";
			case ARRAY_ORDERED :	return "ARRAY_ORDERED";
			case ARRAY_ALTERNATE :	return "ARRAY_ALTERNATE";
			case ARRAY_ALT_TEXT : 	return "ARRAY_ALT_TEXT";
			case SCHEMA_NODE : 		return "SCHEMA_NODE";
			default: 				return null;
		}
	}


	/**
	 * Checks that a node not a struct and array at the same time;
	 * and URI cannot be a struct.
	 * 
	 * @param options the bitmask to check.
	 * @throws XMPException Thrown if the options are not consistent.
	 */
	public void assertConsistency(int options) throws XMPException
	{
		if ((options & STRUCT) > 0  &&  (options & ARRAY) > 0)
		{
			throw new XMPException("IsStruct and IsArray options are mutually exclusive",
					XMPError.BADOPTIONS);
		}
		else if ((options & URI) > 0  &&  (options & (ARRAY | STRUCT)) > 0)
		{	
			throw new XMPException("Structs and arrays can't have \"value\" options",
				XMPError.BADOPTIONS);
		}
	}
}
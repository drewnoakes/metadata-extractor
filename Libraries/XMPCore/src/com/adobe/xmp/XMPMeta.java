// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp;

import java.util.Calendar;

import com.adobe.xmp.options.IteratorOptions;
import com.adobe.xmp.options.ParseOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPProperty;


/**
 * This class represents the set of XMP metadata as a DOM representation. It has methods to read and
 * modify all kinds of properties, create an iterator over all properties and serialize the metadata
 * to a String, byte-array or <code>OutputStream</code>.
 * 
 * @since 20.01.2006
 */
public interface XMPMeta extends Cloneable
{
	// ---------------------------------------------------------------------------------------------
	// Basic property manipulation functions

	/**
	 * The property value getter-methods all take a property specification: the first two parameters
	 * are always the top level namespace URI (the &quot;schema&quot; namespace) and the basic name
	 * of the property being referenced. See the introductory discussion of path expression usage
	 * for more information.
	 * <p>
	 * All of the functions return an object inherited from <code>PropertyBase</code> or
	 * <code>null</code> if the property does not exists. The result object contains the value of
	 * the property and option flags describing the property. Arrays and the non-leaf levels of
	 * nodes do not have values.
	 * <p>
	 * See {@link PropertyOptions} for detailed information about the options.
	 * <p>
	 * This is the simplest property getter, mainly for top level simple properties or after using
	 * the path composition functions in XMPPathFactory.
	 * 
	 * @param schemaNS The namespace URI for the property. May be <code>null</code> or the empty
	 *        string if the first component of the propName path contains a namespace prefix. The
	 *        URI must be for a registered namespace.
	 * @param propName The name of the property. May be a general path expression, must not be
	 *        <code>null</code> or the empty string. Using a namespace prefix on the first
	 *        component is optional. If present without a schemaNS value then the prefix specifies
	 *        the namespace. The prefix must be for a registered namespace. If both a schemaNS URI
	 *        and propName prefix are present, they must be corresponding parts of a registered
	 *        namespace.
	 * @return Returns a <code>XMPProperty</code> containing the value and the options or
	 *         <code>null</code> if the property does not exist.
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	XMPProperty getProperty(String schemaNS, String propName) throws XMPException;

	
	/**
	 * Provides access to items within an array. The index is passed as an integer, you need not
	 * worry about the path string syntax for array items, convert a loop index to a string, etc.
	 * 
	 * @param schemaNS The namespace URI for the array. Has the same usage as in getProperty.
	 * @param arrayName The name of the array. May be a general path expression, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as
	 *        propName in <code>getProperty()</code>.
	 * @param itemIndex The index of the desired item. Arrays in XMP are indexed from 1. The
	 *        constant {@link XMPConst#ARRAY_LAST_ITEM} always refers to the last existing array
	 *        item.
	 * @return Returns a <code>XMPProperty</code> containing the value and the options or
	 *         <code>null</code> if the property does not exist.
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	XMPProperty getArrayItem(String schemaNS, String arrayName, int itemIndex) throws XMPException;


	/**
	 * Returns the number of items in the array.
	 * 
	 * @param schemaNS The namespace URI for the array. Has the same usage as in getProperty.
	 * @param arrayName The name of the array. May be a general path expression, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as
	 *        propName in <code>getProperty()</code>.
	 * @return Returns the number of items in the array.
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	int countArrayItems(String schemaNS, String arrayName) throws XMPException;


	/**
	 * Provides access to fields within a nested structure. The namespace for the field is passed as
	 * a URI, you need not worry about the path string syntax.
	 * <p>
	 * The names of fields should be XML qualified names, that is within an XML namespace. The path
	 * syntax for a qualified name uses the namespace prefix. This is unreliable since the prefix is
	 * never guaranteed. The URI is the formal name, the prefix is just a local shorthand in a given
	 * sequence of XML text.
	 * 
	 * @param schemaNS The namespace URI for the struct. Has the same usage as in getProperty.
	 * @param structName The name of the struct. May be a general path expression, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as
	 *        propName in <code>getProperty()</code>.
	 * @param fieldNS The namespace URI for the field. Has the same URI and prefix usage as the
	 *        schemaNS parameter.
	 * @param fieldName The name of the field. Must be a single XML name, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as the
	 *        structName parameter.
	 * @return Returns a <code>XMPProperty</code> containing the value and the options or
	 *         <code>null</code> if the property does not exist. Arrays and non-leaf levels of
	 *         structs do not have values.
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	XMPProperty getStructField(
		String schemaNS,
		String structName,
		String fieldNS,
		String fieldName) throws XMPException;


	/**
	 * Provides access to a qualifier attached to a property. The namespace for the qualifier is
	 * passed as a URI, you need not worry about the path string syntax. In many regards qualifiers
	 * are like struct fields. See the introductory discussion of qualified properties for more
	 * information.
	 * <p>
	 * The names of qualifiers should be XML qualified names, that is within an XML namespace. The
	 * path syntax for a qualified name uses the namespace prefix. This is unreliable since the
	 * prefix is never guaranteed. The URI is the formal name, the prefix is just a local shorthand
	 * in a given sequence of XML text.
	 * <p>
	 * <em>Note:</em> Qualifiers are only supported for simple leaf properties at this time.
	 * 
	 * @param schemaNS The namespace URI for the struct. Has the same usage as in getProperty.
	 * @param propName The name of the property to which the qualifier is attached. May be a general
	 *        path expression, must not be <code>null</code> or the empty string. Has the same
	 *        namespace prefix usage as in <code>getProperty()</code>.
	 * @param qualNS The namespace URI for the qualifier. Has the same URI and prefix usage as the
	 *        schemaNS parameter.
	 * @param qualName The name of the qualifier. Must be a single XML name, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as the
	 *        propName parameter.
	 * @return Returns a <code>XMPProperty</code> containing the value and the options of the
	 *         qualifier or <code>null</code> if the property does not exist. The name of the
	 *         qualifier must be a single XML name, must not be <code>null</code> or the empty
	 *         string. Has the same namespace prefix usage as the propName parameter.
	 *         <p>
	 *         The value of the qualifier is only set if it has one (Arrays and non-leaf levels of
	 *         structs do not have values).
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	XMPProperty getQualifier(
		String schemaNS,
		String propName,
		String qualNS,
		String qualName) throws XMPException;

	

	// ---------------------------------------------------------------------------------------------
	// Functions for setting property values

	/**
	 * The property value <code>setters</code> all take a property specification, their
	 * differences are in the form of this. The first two parameters are always the top level
	 * namespace URI (the <code>schema</code> namespace) and the basic name of the property being
	 * referenced. See the introductory discussion of path expression usage for more information.
	 * <p>
	 * All of the functions take a string value for the property and option flags describing the
	 * property. The value must be Unicode in UTF-8 encoding. Arrays and non-leaf levels of structs
	 * do not have values. Empty arrays and structs may be created using appropriate option flags.
	 * All levels of structs that is assigned implicitly are created if necessary. appendArayItem
	 * implicitly creates the named array if necessary.
	 * <p>
	 * See {@link PropertyOptions} for detailed information about the options.
	 * <p>
	 * This is the simplest property setter, mainly for top level simple properties or after using
	 * the path composition functions in {@link XMPPathFactory}.
	 * 
	 * @param schemaNS The namespace URI for the property. Has the same usage as in getProperty.
	 * @param propName The name of the property. 
	 * 				   Has the same usage as in <code>getProperty()</code>.
	 * @param propValue the value for the property (only leaf properties have a value). 
	 *        Arrays and non-leaf levels of structs do not have values. 
	 *        Must be <code>null</code> if the value is not relevant.<br/>
	 *        The value is automatically detected: Boolean, Integer, Long, Double, XMPDateTime and
	 *        byte[] are handled, on all other <code>toString()</code> is called.   
	 *        
	 * @param options Option flags describing the property. See the earlier description.
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	void setProperty(
		String schemaNS,
		String propName, 
		Object propValue, 
		PropertyOptions options) throws XMPException;

	
	/**
	 * @see XMPMeta#setProperty(String, String, Object, PropertyOptions)
	 *  
	 * @param schemaNS The namespace URI
	 * @param propName The name of the property 
	 * @param propValue the value for the property   
	 * @throws XMPException Wraps all errors and exceptions
	 */
	void setProperty(
			String schemaNS,
			String propName, 
			Object propValue) throws XMPException;

	
	/**
	 * Replaces an item within an array. The index is passed as an integer, you need not worry about
	 * the path string syntax for array items, convert a loop index to a string, etc. The array
	 * passed must already exist. In normal usage the selected array item is modified. A new item is
	 * automatically appended if the index is the array size plus 1.
	 * 
	 * @param schemaNS The namespace URI for the array. Has the same usage as in getProperty.
	 * @param arrayName The name of the array. May be a general path expression, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as
	 *        propName in getProperty.
	 * @param itemIndex The index of the desired item. Arrays in XMP are indexed from 1. To address
	 *        the last existing item, use {@link XMPMeta#countArrayItems(String, String)} to find
	 *        out the length of the array.
	 * @param itemValue the new value of the array item. Has the same usage as propValue in
	 *        <code>setProperty()</code>.
	 * @param options the set options for the item. 
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	void setArrayItem(
		String schemaNS,
		String arrayName,
		int itemIndex,
		String itemValue,
		PropertyOptions options) throws XMPException;	

	
	/**
	 * @see XMPMeta#setArrayItem(String, String, int, String, PropertyOptions)
	 *  
	 * @param schemaNS The namespace URI
	 * @param arrayName The name of the array
	 * @param itemIndex The index to insert the new item
	 * @param itemValue the new value of the array item
	 * @throws XMPException Wraps all errors and exceptions
	 */
	void setArrayItem(
			String schemaNS,
			String arrayName,
			int itemIndex,
			String itemValue) throws XMPException;	

	
	/**
	 * Inserts an item into an array previous to the given index. The index is passed as an integer,
	 * you need not worry about the path string syntax for array items, convert a loop index to a
	 * string, etc. The array passed must already exist. In normal usage the selected array item is
	 * modified. A new item is automatically appended if the index is the array size plus 1.
	 * 
	 * @param schemaNS The namespace URI for the array. Has the same usage as in getProperty.
	 * @param arrayName The name of the array. May be a general path expression, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as
	 *        propName in getProperty.
	 * @param itemIndex The index to insert the new item. Arrays in XMP are indexed from 1. Use
	 * 		  <code>XMPConst.ARRAY_LAST_ITEM</code> to append items.
	 * @param itemValue the new value of the array item. Has the same usage as
	 *        propValue in <code>setProperty()</code>.
	 * @param options the set options that decide about the kind of the node.
 	 * @throws XMPException Wraps all errors and exceptions that may occur.
 	 */
	void insertArrayItem(
		String schemaNS,
		String arrayName,
		int itemIndex,
		String itemValue,
		PropertyOptions options) throws XMPException;	
	
	
	/**
	 * @see XMPMeta#insertArrayItem(String, String, int, String, PropertyOptions)
	 * 
	 * @param schemaNS The namespace URI for the array
	 * @param arrayName The name of the array
	 * @param itemIndex The index to insert the new item
	 * @param itemValue the value of the array item
	 * @throws XMPException Wraps all errors and exceptions
	 */
	void insertArrayItem(
			String schemaNS,
			String arrayName,
			int itemIndex,
			String itemValue) throws XMPException;	

	
	/**
	 * Simplifies the construction of an array by not requiring that you pre-create an empty array.
	 * The array that is assigned is created automatically if it does not yet exist. Each call to
	 * appendArrayItem() appends an item to the array. The corresponding parameters have the same
	 * use as setArrayItem(). The arrayOptions parameter is used to specify what kind of array. If
	 * the array exists, it must have the specified form.
	 * 
	 * @param schemaNS The namespace URI for the array. Has the same usage as in getProperty.
	 * @param arrayName The name of the array. May be a general path expression, must not be null or
	 *        the empty string. Has the same namespace prefix usage as propPath in getProperty.
	 * @param arrayOptions Option flags describing the array form. The only valid options are
	 *        <ul>
	 *        <li> {@link PropertyOptions#ARRAY},
	 *        <li> {@link PropertyOptions#ARRAY_ORDERED},
	 *        <li> {@link PropertyOptions#ARRAY_ALTERNATE} or
	 *        <li> {@link PropertyOptions#ARRAY_ALT_TEXT}.
	 *        </ul>
	 *        <em>Note:</em> the array options only need to be provided if the array is not 
	 *        already existing, otherwise you can set them to <code>null</code> or use
	 *        {@link XMPMeta#appendArrayItem(String, String, String)}.
	 * @param itemValue the value of the array item. Has the same usage as propValue in getProperty.
	 * @param itemOptions Option flags describing the item to append ({@link PropertyOptions})
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	void appendArrayItem(
		String schemaNS,
		String arrayName,
		PropertyOptions arrayOptions,
		String itemValue,
		PropertyOptions itemOptions) throws XMPException;

	
	/**
	 * @see XMPMeta#appendArrayItem(String, String, PropertyOptions, String, PropertyOptions)
	 * 
	 * @param schemaNS The namespace URI for the array
	 * @param arrayName The name of the array
	 * @param itemValue the value of the array item
	 * @throws XMPException Wraps all errors and exceptions
	 */
	void appendArrayItem(
			String schemaNS,
			String arrayName,
			String itemValue) throws XMPException;
	

	/**
	 * Provides access to fields within a nested structure. The namespace for the field is passed as
	 * a URI, you need not worry about the path string syntax. The names of fields should be XML
	 * qualified names, that is within an XML namespace. The path syntax for a qualified name uses
	 * the namespace prefix, which is unreliable because the prefix is never guaranteed. The URI is
	 * the formal name, the prefix is just a local shorthand in a given sequence of XML text.
	 * 
	 * @param schemaNS The namespace URI for the struct. Has the same usage as in getProperty.
	 * @param structName The name of the struct. May be a general path expression, must not be null
	 *        or the empty string. Has the same namespace prefix usage as propName in getProperty.
	 * @param fieldNS The namespace URI for the field. Has the same URI and prefix usage as the
	 *        schemaNS parameter.
	 * @param fieldName The name of the field. Must be a single XML name, must not be null or the
	 *        empty string. Has the same namespace prefix usage as the structName parameter.
	 * @param fieldValue the value of thefield, if the field has a value. 
	 *        Has the same usage as propValue in getProperty.
	 * @param options Option flags describing the field. See the earlier description.
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	void setStructField(
		String schemaNS,
		String structName,
		String fieldNS,
		String fieldName,
		String fieldValue,
		PropertyOptions options) throws XMPException;

	
	/**
	 * @see XMPMeta#setStructField(String, String, String, String, String, PropertyOptions)
	 * 
	 * @param schemaNS The namespace URI for the struct
	 * @param structName The name of the struct
	 * @param fieldNS The namespace URI for the field
	 * @param fieldName The name of the field
	 * @param fieldValue the value of the field
	 * @throws XMPException Wraps all errors and exceptions
	 */
	void setStructField(
			String schemaNS,
			String structName,
			String fieldNS,
			String fieldName,
			String fieldValue) throws XMPException;
	

	/**
	 * Provides access to a qualifier attached to a property. The namespace for the qualifier is
	 * passed as a URI, you need not worry about the path string syntax. In many regards qualifiers
	 * are like struct fields. See the introductory discussion of qualified properties for more
	 * information. The names of qualifiers should be XML qualified names, that is within an XML
	 * namespace. The path syntax for a qualified name uses the namespace prefix, which is
	 * unreliable because the prefix is never guaranteed. The URI is the formal name, the prefix is
	 * just a local shorthand in a given sequence of XML text. The property the qualifier
	 * will be attached has to exist.
	 * 
	 * @param schemaNS The namespace URI for the struct. Has the same usage as in getProperty.
	 * @param propName The name of the property to which the qualifier is attached. Has the same
	 *        usage as in getProperty.
	 * @param qualNS The namespace URI for the qualifier. Has the same URI and prefix usage as the
	 *        schemaNS parameter.
	 * @param qualName The name of the qualifier. Must be a single XML name, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as the
	 *        propName parameter.
	 * @param qualValue A pointer to the <code>null</code> terminated UTF-8 string that is the
	 *        value of the qualifier, if the qualifier has a value. Has the same usage as propValue
	 *        in getProperty.
	 * @param options Option flags describing the qualifier. See the earlier description.
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	void setQualifier(
		String schemaNS,
		String propName,
		String qualNS,
		String qualName,
		String qualValue,
		PropertyOptions options) throws XMPException;

	
	/**
	 * @see XMPMeta#setQualifier(String, String, String, String, String, PropertyOptions)
	 * 
	 * @param schemaNS The namespace URI for the struct
	 * @param propName The name of the property to which the qualifier is attached
	 * @param qualNS The namespace URI for the qualifier
	 * @param qualName The name of the qualifier
	 * @param qualValue the value of the qualifier
	 * @throws XMPException Wraps all errors and exceptions
	 */
	void setQualifier(
			String schemaNS,
			String propName,
			String qualNS,
			String qualName,
			String qualValue) throws XMPException;
	
	
	
	// ---------------------------------------------------------------------------------------------
	// Functions for deleting and detecting properties. These should be obvious from the
	// descriptions of the getters and setters.

	/**
	 * Deletes the given XMP subtree rooted at the given property. It is not an error if the
	 * property does not exist.
	 * 
	 * @param schemaNS The namespace URI for the property. Has the same usage as in
	 *        <code>getProperty()</code>.
	 * @param propName The name of the property. Has the same usage as in getProperty.
	 */
	void deleteProperty(String schemaNS, String propName);


	/**
	 * Deletes the given XMP subtree rooted at the given array item. It is not an error if the array
	 * item does not exist.
	 * 
	 * @param schemaNS The namespace URI for the array. Has the same usage as in getProperty.
	 * @param arrayName The name of the array. May be a general path expression, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as
	 *        propName in <code>getProperty()</code>.
	 * @param itemIndex The index of the desired item. Arrays in XMP are indexed from 1. The
	 *        constant <code>XMPConst.ARRAY_LAST_ITEM</code> always refers to the last
	 *        existing array item.
	 */
	void deleteArrayItem(String schemaNS, String arrayName, int itemIndex);


	/**
	 * Deletes the given XMP subtree rooted at the given struct field. It is not an error if the
	 * field does not exist.
	 * 
	 * @param schemaNS The namespace URI for the struct. Has the same usage as in
	 *        <code>getProperty()</code>.
	 * @param structName The name of the struct. May be a general path expression, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as
	 *        propName in getProperty.
	 * @param fieldNS The namespace URI for the field. Has the same URI and prefix usage as the
	 *        schemaNS parameter.
	 * @param fieldName The name of the field. Must be a single XML name, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as the
	 *        structName parameter.
	 */
	void deleteStructField(String schemaNS, String structName, String fieldNS, String fieldName);


	/**
	 * Deletes the given XMP subtree rooted at the given qualifier. It is not an error if the
	 * qualifier does not exist.
	 * 
	 * @param schemaNS The namespace URI for the struct. Has the same usage as in
	 *        <code>getProperty()</code>.
	 * @param propName The name of the property to which the qualifier is attached. Has the same
	 *        usage as in getProperty.
	 * @param qualNS The namespace URI for the qualifier. Has the same URI and prefix usage as the
	 *        schemaNS parameter.
	 * @param qualName The name of the qualifier. Must be a single XML name, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as the
	 *        propName parameter.
	 */
	void deleteQualifier(String schemaNS, String propName, String qualNS, String qualName);


	/**
	 * Returns whether the property exists.
	 * 
	 * @param schemaNS The namespace URI for the property. Has the same usage as in
	 *        <code>getProperty()</code>.
	 * @param propName The name of the property. 
	 * 		  Has the same usage as in <code>getProperty()</code>.
	 * @return Returns true if the property exists.
	 */
	boolean doesPropertyExist(String schemaNS, String propName);


	/**
	 * Tells if the array item exists.
	 * 
	 * @param schemaNS The namespace URI for the array. Has the same usage as in
	 *        <code>getProperty()</code>.
	 * @param arrayName The name of the array. May be a general path expression, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as
	 *        propName in <code>getProperty()</code>.
	 * @param itemIndex The index of the desired item. Arrays in XMP are indexed from 1. The
	 *        constant <code>XMPConst.ARRAY_LAST_ITEM</code> always refers to the last
	 *        existing array item.
	 * @return Returns <code>true</code> if the array exists, <code>false</code> otherwise.
	 */
	boolean doesArrayItemExist(String schemaNS, String arrayName, int itemIndex);


	/**
	 * DoesStructFieldExist tells if the struct field exists.
	 * 
	 * @param schemaNS The namespace URI for the struct. Has the same usage as in
	 *        <code>getProperty()</code>.
	 * @param structName The name of the struct. May be a general path expression, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as
	 *        propName in <code>getProperty()</code>.
	 * @param fieldNS The namespace URI for the field. Has the same URI and prefix usage as the
	 *        schemaNS parameter.
	 * @param fieldName The name of the field. Must be a single XML name, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as the
	 *        structName parameter.
	 * @return Returns true if the field exists.
	 */
	boolean doesStructFieldExist(
		String schemaNS, 
		String structName, 
		String fieldNS,
		String fieldName);


	/**
	 * DoesQualifierExist tells if the qualifier exists.
	 * 
	 * @param schemaNS The namespace URI for the struct. Has the same usage as in
	 *        <code>getProperty()</code>.
	 * @param propName The name of the property to which the qualifier is attached. Has the same
	 *        usage as in <code>getProperty()</code>.
	 * @param qualNS The namespace URI for the qualifier. Has the same URI and prefix usage as the
	 *        schemaNS parameter.
	 * @param qualName The name of the qualifier. Must be a single XML name, must not be
	 *        <code>null</code> or the empty string. Has the same namespace prefix usage as the
	 *        propName parameter.
	 * @return Returns true if the qualifier exists.
	 */
	boolean doesQualifierExist(String schemaNS, String propName, String qualNS, String qualName);


	// ---------------------------------------------------------------------------------------------
	// Specialized Get and Set functions

	/**
	 * These functions provide convenient support for localized text properties, including a number
	 * of special and obscure aspects. Localized text properties are stored in alt-text arrays. They
	 * allow multiple concurrent localizations of a property value, for example a document title or
	 * copyright in several languages. The most important aspect of these functions is that they
	 * select an appropriate array item based on one or two RFC 3066 language tags. One of these
	 * languages, the "specific" language, is preferred and selected if there is an exact match. For
	 * many languages it is also possible to define a "generic" language that may be used if there
	 * is no specific language match. The generic language must be a valid RFC 3066 primary subtag,
	 * or the empty string. For example, a specific language of "en-US" should be used in the US,
	 * and a specific language of "en-UK" should be used in England. It is also appropriate to use
	 * "en" as the generic language in each case. If a US document goes to England, the "en-US"
	 * title is selected by using the "en" generic language and the "en-UK" specific language. It is
	 * considered poor practice, but allowed, to pass a specific language that is just an RFC 3066
	 * primary tag. For example "en" is not a good specific language, it should only be used as a
	 * generic language. Passing "i" or "x" as the generic language is also considered poor practice
	 * but allowed. Advice from the W3C about the use of RFC 3066 language tags can be found at:
	 * http://www.w3.org/International/articles/language-tags/
	 * <p>
	 * <em>Note:</em> RFC 3066 language tags must be treated in a case insensitive manner. The XMP
	 * Toolkit does this by normalizing their capitalization:
	 * <ul>
	 * <li> The primary subtag is lower case, the suggested practice of ISO 639.
	 * <li> All 2 letter secondary subtags are upper case, the suggested practice of ISO 3166.
	 * <li> All other subtags are lower case. The XMP specification defines an artificial language,
	 * <li>"x-default", that is used to explicitly denote a default item in an alt-text array.
	 * </ul>
	 * The XMP toolkit normalizes alt-text arrays such that the x-default item is the first item.
	 * The SetLocalizedText function has several special features related to the x-default item, see
	 * its description for details. The selection of the array item is the same for GetLocalizedText
	 * and SetLocalizedText:
	 * <ul>
	 * <li> Look for an exact match with the specific language.
	 * <li> If a generic language is given, look for a partial match.
	 * <li> Look for an x-default item.
	 * <li> Choose the first item.
	 * </ul>
	 * A partial match with the generic language is where the start of the item's language matches
	 * the generic string and the next character is '-'. An exact match is also recognized as a
	 * degenerate case. It is fine to pass x-default as the specific language. In this case,
	 * selection of an x-default item is an exact match by the first rule, not a selection by the
	 * 3rd rule. The last 2 rules are fallbacks used when the specific and generic languages fail to
	 * produce a match. <code>getLocalizedText</code> returns information about a selected item in
	 * an alt-text array. The array item is selected according to the rules given above.
	 * 
	 * <em>Note:</em> In a future version of this API a method 
	 * 		using Java <code>java.lang.Locale</code> will be added.
	 * 
	 * @param schemaNS The namespace URI for the alt-text array. Has the same usage as in
	 *        <code>getProperty()</code>.
	 * @param altTextName The name of the alt-text array. May be a general path expression, must not
	 *        be <code>null</code> or the empty string. Has the same namespace prefix usage as
	 *        propName in <code>getProperty()</code>.
	 * @param genericLang The name of the generic language as an RFC 3066 primary subtag. May be
	 *        <code>null</code> or the empty string if no generic language is wanted.
	 * @param specificLang The name of the specific language as an RFC 3066 tag. Must not be
	 *        <code>null</code> or the empty string.
	 * @return Returns an <code>XMPProperty</code> containing the value, the actual language and 
	 * 		   the options if an appropriate alternate collection item exists, <code>null</code> 
	 * 		  if the property.
	 *         does not exist.
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	XMPProperty getLocalizedText(
		String schemaNS, 
		String altTextName,
		String genericLang,
		String specificLang) throws XMPException;


	/**
	 * Modifies the value of a selected item in an alt-text array. Creates an appropriate array item
	 * if necessary, and handles special cases for the x-default item. If the selected item is from
	 * a match with the specific language, the value of that item is modified. If the existing value
	 * of that item matches the existing value of the x-default item, the x-default item is also
	 * modified. If the array only has 1 existing item (which is not x-default), an x-default item
	 * is added with the given value. If the selected item is from a match with the generic language
	 * and there are no other generic matches, the value of that item is modified. If the existing
	 * value of that item matches the existing value of the x-default item, the x-default item is
	 * also modified. If the array only has 1 existing item (which is not x-default), an x-default
	 * item is added with the given value. If the selected item is from a partial match with the
	 * generic language and there are other partial matches, a new item is created for the specific
	 * language. The x-default item is not modified. If the selected item is from the last 2 rules
	 * then a new item is created for the specific language. If the array only had an x-default
	 * item, the x-default item is also modified. If the array was empty, items are created for the
	 * specific language and x-default.
	 * 
	 * <em>Note:</em> In a future version of this API a method 
	 * 		using Java <code>java.lang.Locale</code> will be added.
	 * 
	 * 
	 * @param schemaNS The namespace URI for the alt-text array. Has the same usage as in
	 *        <code>getProperty()</code>.
	 * @param altTextName The name of the alt-text array. May be a general path expression, must not
	 *        be <code>null</code> or the empty string. Has the same namespace prefix usage as
	 *        propName in <code>getProperty()</code>.
	 * @param genericLang The name of the generic language as an RFC 3066 primary subtag. May be
	 *        <code>null</code> or the empty string if no generic language is wanted.
	 * @param specificLang The name of the specific language as an RFC 3066 tag. Must not be
	 *        <code>null</code> or the empty string.
	 * @param itemValue A pointer to the <code>null</code> terminated UTF-8 string that is the new
	 *        value for the appropriate array item.
	 * @param options Option flags, none are defined at present.
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	void setLocalizedText(
		String schemaNS,
		String altTextName,
		String genericLang,
		String specificLang,
		String itemValue,
		PropertyOptions options) throws XMPException;

	
	/**
	 * @see XMPMeta#setLocalizedText(String, String, String, String, String, PropertyOptions)
	 * 
	 * @param schemaNS The namespace URI for the alt-text array
	 * @param altTextName The name of the alt-text array
	 * @param genericLang The name of the generic language
	 * @param specificLang The name of the specific language
	 * @param itemValue the new value for the appropriate array item
	 * @throws XMPException Wraps all errors and exceptions
	 */
	void setLocalizedText(
			String schemaNS,
			String altTextName,
			String genericLang,
			String specificLang,
			String itemValue) throws XMPException;

	
	
	// ---------------------------------------------------------------------------------------------
	// Functions accessing properties as binary values.

	
	/**
	 * These are very similar to <code>getProperty()</code> and <code>SetProperty()</code> above, 
	 * but the value is returned or provided in a literal form instead of as a UTF-8 string. 
	 * The path composition functions in <code>XMPPathFactory</code> may be used to compose an path 
	 * expression for fields in nested structures, items in arrays, or qualifiers.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>getProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @return Returns a <code>Boolean</code> value or <code>null</code> 
	 * 		   if the property does not exist.
	 * @throws XMPException Wraps all exceptions that may occur, 
	 * 		   especially conversion errors.
	 */
	Boolean getPropertyBoolean(String schemaNS, String propName) throws XMPException;
	

	/**
	 * Convenience method to retrieve the literal value of a property.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>getProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @return Returns an <code>Integer</code> value or <code>null</code> 
	 * 		   if the property does not exist.
	 * @throws XMPException Wraps all exceptions that may occur, 
	 * 		   especially conversion errors.
	 */
	Integer getPropertyInteger(String schemaNS, String propName) throws XMPException;
	

	/**
	 * Convenience method to retrieve the literal value of a property.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>getProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @return Returns a <code>Long</code> value or <code>null</code> 
	 * 		   if the property does not exist.
	 * @throws XMPException Wraps all exceptions that may occur, 
	 * 		   especially conversion errors.
	 */
	Long getPropertyLong(String schemaNS, String propName) throws XMPException;
	

	/**
	 * Convenience method to retrieve the literal value of a property.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>getProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @return Returns a <code>Double</code> value or <code>null</code> 
	 * 		   if the property does not exist.
	 * @throws XMPException Wraps all exceptions that may occur, 
	 * 		   especially conversion errors.
	 */
	Double getPropertyDouble(String schemaNS, String propName) throws XMPException;
	

	/**
	 * Convenience method to retrieve the literal value of a property.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>getProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @return Returns a <code>XMPDateTime</code>-object or <code>null</code> 
	 * 		   if the property does not exist.
	 * @throws XMPException Wraps all exceptions that may occur, 
	 * 		   especially conversion errors.
	 */
	XMPDateTime getPropertyDate(String schemaNS, String propName) throws XMPException;

	
	/**
	 * Convenience method to retrieve the literal value of a property.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>getProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @return Returns a Java <code>Calendar</code>-object or <code>null</code> 
	 * 		   if the property does not exist.
	 * @throws XMPException Wraps all exceptions that may occur, 
	 * 		   especially conversion errors.
	 */
	Calendar getPropertyCalendar(String schemaNS, String propName) throws XMPException;

	
	/**
	 * Convenience method to retrieve the literal value of a property.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>getProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @return Returns a <code>byte[]</code>-array contained the decoded base64 value 
	 * 		   or <code>null</code> if the property does not exist.
	 * @throws XMPException Wraps all exceptions that may occur, 
	 * 		   especially conversion errors.
	 */
	byte[] getPropertyBase64(String schemaNS, String propName) throws XMPException;

	
	/**
	 * Convenience method to retrieve the literal value of a property.
	 * <em>Note:</em> There is no <code>setPropertyString()</code>, 
	 * because <code>setProperty()</code> sets a string value.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>getProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @return Returns a <code>String</code> value or <code>null</code> 
	 * 		   if the property does not exist.
	 * @throws XMPException Wraps all exceptions that may occur, 
	 * 		   especially conversion errors.
	 */
	String getPropertyString(String schemaNS, String propName) throws XMPException;
	

	/**
	 * Convenience method to set a property to a literal <code>boolean</code> value.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>setProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @param  propValue the literal property value as <code>boolean</code>.
	 * @param  options options of the property to set (optional).
	 * @throws XMPException Wraps all exceptions that may occur.
	 */
	void setPropertyBoolean(
		String schemaNS,
		String propName,
		boolean propValue,
		PropertyOptions options) throws XMPException;

	
	/**
	 * @see XMPMeta#setPropertyBoolean(String, String, boolean, PropertyOptions)
	 *  
	 * @param  schemaNS The namespace URI for the property
	 * @param  propName The name of the property
	 * @param  propValue the literal property value as <code>boolean</code>
	 * @throws XMPException Wraps all exceptions
	 */
	void setPropertyBoolean(
			String schemaNS,
			String propName,
			boolean propValue) throws XMPException;
	
	
	/**
	 * Convenience method to set a property to a literal <code>int</code> value.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>setProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @param  propValue the literal property value as <code>int</code>.
	 * @param  options options of the property to set (optional).
	 * @throws XMPException Wraps all exceptions that may occur.
	 */
	void setPropertyInteger(
		String schemaNS,
		String propName,
		int propValue,
		PropertyOptions options) throws XMPException;

	
	/**
	 * @see XMPMeta#setPropertyInteger(String, String, int, PropertyOptions)
	 *  
	 * @param  schemaNS The namespace URI for the property
	 * @param  propName The name of the property
	 * @param  propValue the literal property value as <code>int</code>
	 * @throws XMPException Wraps all exceptions
	 */
	void setPropertyInteger(
			String schemaNS,
			String propName,
			int propValue) throws XMPException;
	

	/**
	 * Convenience method to set a property to a literal <code>long</code> value.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>setProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @param  propValue the literal property value as <code>long</code>.
	 * @param  options options of the property to set (optional).
	 * @throws XMPException Wraps all exceptions that may occur.
	 */
	void setPropertyLong(
		String schemaNS,
		String propName,
		long propValue,
		PropertyOptions options) throws XMPException;

	
	/**
	 * @see XMPMeta#setPropertyLong(String, String, long, PropertyOptions)
	 *  
	 * @param  schemaNS The namespace URI for the property
	 * @param  propName The name of the property
	 * @param  propValue the literal property value as <code>long</code>
	 * @throws XMPException Wraps all exceptions
	 */
	void setPropertyLong(
			String schemaNS,
			String propName,
			long propValue) throws XMPException;
	

	/**
	 * Convenience method to set a property to a literal <code>double</code> value.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>setProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @param  propValue the literal property value as <code>double</code>.
	 * @param  options options of the property to set (optional).
	 * @throws XMPException Wraps all exceptions that may occur.
	 */
	void setPropertyDouble(
		String schemaNS,
		String propName,
		double propValue,
		PropertyOptions options) throws XMPException;

	
	/**
	 * @see XMPMeta#setPropertyDouble(String, String, double, PropertyOptions)
	 *  
	 * @param  schemaNS The namespace URI for the property
	 * @param  propName The name of the property
	 * @param  propValue the literal property value as <code>double</code>
	 * @throws XMPException Wraps all exceptions
	 */
	void setPropertyDouble(
			String schemaNS,
			String propName,
			double propValue) throws XMPException;
	

	/**
	 * Convenience method to set a property with an XMPDateTime-object, 
	 * which is serialized to an ISO8601 date.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>setProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @param  propValue the property value as <code>XMPDateTime</code>.
	 * @param  options options of the property to set (optional).
	 * @throws XMPException Wraps all exceptions that may occur.
	 */
	void setPropertyDate(
		String schemaNS,
		String propName,
		XMPDateTime propValue,
		PropertyOptions options) throws XMPException;

	
	/**
	 * @see XMPMeta#setPropertyDate(String, String, XMPDateTime, PropertyOptions)
	 *  
	 * @param  schemaNS The namespace URI for the property
	 * @param  propName The name of the property
	 * @param  propValue the property value as <code>XMPDateTime</code>
	 * @throws XMPException Wraps all exceptions
	 */
	void setPropertyDate(
			String schemaNS,
			String propName,
			XMPDateTime propValue) throws XMPException;
	
	
	/**
	 * Convenience method to set a property with a Java Calendar-object, 
	 * which is serialized to an ISO8601 date.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>setProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @param  propValue the property value as Java <code>Calendar</code>.
	 * @param  options options of the property to set (optional).
	 * @throws XMPException Wraps all exceptions that may occur.
	 */
	void setPropertyCalendar(
		String schemaNS,
		String propName,
		Calendar propValue,
		PropertyOptions options) throws XMPException;

	
	/**
	 * @see XMPMeta#setPropertyCalendar(String, String, Calendar, PropertyOptions)
	 *  
	 * @param  schemaNS The namespace URI for the property
	 * @param  propName The name of the property
	 * @param  propValue the property value as <code>Calendar</code>
	 * @throws XMPException Wraps all exceptions
	 */
	void setPropertyCalendar(
			String schemaNS,
			String propName,
			Calendar propValue) throws XMPException;
	

	/**
	 * Convenience method to set a property from a binary <code>byte[]</code>-array, 
	 * which is serialized as base64-string.
	 * 
	 * @param  schemaNS The namespace URI for the property. Has the same usage as in
	 *         <code>setProperty()</code>.
	 * @param  propName The name of the property.
	 * 		   Has the same usage as in <code>getProperty()</code>.
	 * @param  propValue the literal property value as byte array.
	 * @param  options options of the property to set (optional).
	 * @throws XMPException Wraps all exceptions that may occur.
	 */
	void setPropertyBase64(
		String schemaNS,
		String propName,
		byte[] propValue,
		PropertyOptions options) throws XMPException;

	
	/**
	 * @see XMPMeta#setPropertyBase64(String, String, byte[], PropertyOptions)
	 *  
	 * @param  schemaNS The namespace URI for the property
	 * @param  propName The name of the property
	 * @param  propValue the literal property value as byte array
	 * @throws XMPException Wraps all exceptions
	 */
	void setPropertyBase64(
			String schemaNS,
			String propName,
			byte[] propValue) throws XMPException;
	

	/**
	 * Constructs an iterator for the properties within this XMP object.
	 * 
	 * @return Returns an <code>XMPIterator</code>.
	 * @see XMPMeta#iterator(String, String, IteratorOptions)
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	XMPIterator iterator() throws XMPException;


	/**
	 * Constructs an iterator for the properties within this XMP object using some options.
	 * 
	 * @param options Option flags to control the iteration.
	 * @return Returns an <code>XMPIterator</code>.
	 * @see XMPMeta#iterator(String, String, IteratorOptions)
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	XMPIterator iterator(IteratorOptions options) throws XMPException;


	/**
	 * Construct an iterator for the properties within an XMP object. The general operation of an
	 * XMP object iterator was. According to the parameters it iterates the entire data tree,
	 * properties within a specific schema, or a subtree rooted at a specific node.
	 * 
	 * @param schemaNS Optional schema namespace URI to restrict the iteration. Omitted (visit all
	 *        schema) by passing <code>null</code> or empty String.
	 * @param propName Optional property name to restrict the iteration. May be an arbitrary path
	 *        expression. Omitted (visit all properties) by passing <code>null</code> or empty
	 *        String. If no schema URI is given, it is ignored.
	 * @param options Option flags to control the iteration. See {@link IteratorOptions} for
	 *        details.
	 * @return Returns an <code>XMPIterator</code> for this <code>XMPMeta</code>-object
	 *         considering the given options.
	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	XMPIterator iterator(
		String schemaNS,
		String propName,
		IteratorOptions options) throws XMPException;


	/**
	 * This correlates to the about-attribute,
	 * returns the empty String if no name is set.
	 * 
	 * @return Returns the name of the XMP object.
	 */
	String getObjectName();


	/**
	 * @param name Sets the name of the XMP object.
	 */
	void setObjectName(String name);

	
	/**
	 * @return Returns the unparsed content of the &lt;?xpacket&gt; processing instruction.
	 * 		This contains normally the attribute-like elements 'begin="&lt;BOM&gt;"
	 *		id="W5M0MpCehiHzreSzNTczkc9d"' and possibly the deprecated elements 'bytes="1234"' or
	 * 		'encoding="XXX"'. If the parsed packet has not been wrapped into an xpacket,
	 * 		<code>null</code> is returned.   
	 */
	String getPacketHeader();
	

	/**
	 * Clones the complete metadata tree.
	 * 
	 * @return Returns a deep copy of this instance.
	 */
	Object clone();

	
	/**
	 * Sorts the complete datamodel according to the following rules:
	 * <ul>
	 * 		<li>Schema nodes are sorted by prefix. 
	 * 		<li>Properties at top level and within structs are sorted by full name, that is 
	 * 			prefix + local name.
	 * 		<li>Array items are not sorted, even if they have no certain order such as bags.
	 * 		<li>Qualifier are sorted, with the exception of "xml:lang" and/or "rdf:type" 
	 * 			that stay at the top of the list in that order.  
	 * </ul>
	 */
	void sort();
	
	
	/**
	 * Perform the normalization as a separate parsing step.
	 * Normally it is done during parsing, unless the parsing option
	 * {@link ParseOptions#OMIT_NORMALIZATION} is set to <code>true</code>.
	 * <em>Note:</em> It does no harm to call this method to an already normalized xmp object. 
	 * It was a PDF/A requirement to get hand on the unnormalized <code>XMPMeta</code> object.
	 * 
	 * @param options optional parsing options.
 	 * @throws XMPException Wraps all errors and exceptions that may occur.
	 */
	void normalize(ParseOptions options) throws XMPException;
	
	
	/**
	 * Renders this node and the tree unter this node in a human readable form.
	 * @return Returns a multiline string containing the dump.
	 */
	String dumpObject();
}
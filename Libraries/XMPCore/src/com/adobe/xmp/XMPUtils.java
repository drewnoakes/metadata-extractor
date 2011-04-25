// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp;

import com.adobe.xmp.impl.Base64;
import com.adobe.xmp.impl.ISO8601Converter;
import com.adobe.xmp.impl.XMPUtilsImpl;
import com.adobe.xmp.options.PropertyOptions;


/**
 * Utility methods for XMP. I included only those that are different from the
 * Java default conversion utilities.
 * 
 * @since 21.02.2006
 */
public class XMPUtils
{
	/** Private constructor */
	private XMPUtils()
	{
		// EMPTY
	}


	/**
	 * Create a single edit string from an array of strings.
	 * 
	 * @param xmp
	 *            The XMP object containing the array to be catenated.
	 * @param schemaNS
	 *            The schema namespace URI for the array. Must not be null or
	 *            the empty string.
	 * @param arrayName
	 *            The name of the array. May be a general path expression, must
	 *            not be null or the empty string. Each item in the array must
	 *            be a simple string value.
	 * @param separator
	 *            The string to be used to separate the items in the catenated
	 *            string. Defaults to &quot;; &quot;, ASCII semicolon and space
	 *            (U+003B, U+0020).
	 * @param quotes
	 *            The characters to be used as quotes around array items that
	 *            contain a separator. Defaults to &apos;&quot;&apos;
	 * @param allowCommas
	 *            Option flag to control the catenation.
	 * @return Returns the string containing the catenated array items.
	 * @throws XMPException Forwards the Exceptions from the metadata processing
	 */
	public static String catenateArrayItems(XMPMeta xmp, String schemaNS, String arrayName,
			String separator, String quotes, boolean allowCommas) throws XMPException
	{
		return XMPUtilsImpl
				.catenateArrayItems(xmp, schemaNS, arrayName, separator, quotes, allowCommas);
	}


	/**
	 * Separate a single edit string into an array of strings.
	 * 
	 * @param xmp
	 *            The XMP object containing the array to be updated.
	 * @param schemaNS
	 *            The schema namespace URI for the array. Must not be null or
	 *            the empty string.
	 * @param arrayName
	 *            The name of the array. May be a general path expression, must
	 *            not be null or the empty string. Each item in the array must
	 *            be a simple string value.
	 * @param catedStr
	 *            The string to be separated into the array items.
	 * @param arrayOptions Option flags to control the separation. 
	 * @param preserveCommas Flag if commas shall be preserved
	 * @throws XMPException Forwards the Exceptions from the metadata processing 
	 */
	public static void separateArrayItems(XMPMeta xmp, String schemaNS, String arrayName,
			String catedStr, PropertyOptions arrayOptions, boolean preserveCommas) 
				throws XMPException
	{
		XMPUtilsImpl.separateArrayItems(xmp, schemaNS, arrayName, catedStr, arrayOptions,
				preserveCommas);
	}


	/**
	 * Remove multiple properties from an XMP object.
	 * 
	 * RemoveProperties was created to support the File Info dialog's Delete
	 * button, and has been been generalized somewhat from those specific needs.
	 * It operates in one of three main modes depending on the schemaNS and
	 * propName parameters:
	 * 
	 * <ul>
	 * <li> Non-empty <code>schemaNS</code> and <code>propName</code> - The named property is
	 * removed if it is an external property, or if the 
	 * flag <code>doAllProperties</code> option is true. It does not matter whether the
	 * named property is an actual property or an alias.
	 * 
	 * <li> Non-empty <code>schemaNS</code> and empty <code>propName</code> - The all external
	 * properties in the named schema are removed. Internal properties are also
	 * removed if the flag <code>doAllProperties</code> option is set. In addition,
	 * aliases from the named schema will be removed if the flag <code>includeAliases</code> 
	 * option is set.
	 * 
	 * <li> Empty <code>schemaNS</code> and empty <code>propName</code> - All external properties in
	 * all schema are removed. Internal properties are also removed if the 
	 * flag <code>doAllProperties</code> option is passed. Aliases are implicitly handled
	 * because the associated actuals are internal if the alias is.
	 * </ul>
	 * 
	 * It is an error to pass an empty <code>schemaNS</code> and non-empty <code>propName</code>.
	 * 
	 * @param xmp
	 *            The XMP object containing the properties to be removed.
	 * 
	 * @param schemaNS
	 *            Optional schema namespace URI for the properties to be
	 *            removed.
	 * 
	 * @param propName
	 *            Optional path expression for the property to be removed.
	 * 
	 * @param doAllProperties Option flag to control the deletion: do internal properties in
	 *          addition to external properties.
	 *            
	 * @param includeAliases Option flag to control the deletion: 
	 * 			Include aliases in the "named schema" case above.
	 * 			<em>Note:</em> Currently not supported.
	 * @throws XMPException Forwards the Exceptions from the metadata processing 
	 */
	public static void removeProperties(XMPMeta xmp, String schemaNS, String propName,
			boolean doAllProperties, boolean includeAliases) throws XMPException
	{
		XMPUtilsImpl.removeProperties(xmp, schemaNS, propName, doAllProperties, includeAliases);
	}

	

	/**
	 * Alias without the new option <code>deleteEmptyValues</code>.
	 * @param source The source XMP object.
	 * @param dest The destination XMP object.
	 * @param doAllProperties Do internal properties in addition to external properties.
	 * @param replaceOldValues Replace the values of existing properties.
	 * @throws XMPException Forwards the Exceptions from the metadata processing 
	 */
	public static void appendProperties(XMPMeta source, XMPMeta dest, boolean doAllProperties,
			boolean replaceOldValues) throws XMPException
	{
		appendProperties(source, dest, doAllProperties, replaceOldValues, false);
	}
	

	/**
	 * <p>Append properties from one XMP object to another.
	 * 
	 * <p>XMPUtils#appendProperties was created to support the File Info dialog's Append button, and
	 * has been been generalized somewhat from those specific needs. It appends information from one
	 * XMP object (source) to another (dest). The default operation is to append only external
	 * properties that do not already exist in the destination. The flag 
	 * <code>doAllProperties</code> can be used to operate on all properties, external and internal.
	 * The flag <code>replaceOldValues</code> option can be used to replace the values 
	 * of existing properties. The notion of external
	 * versus internal applies only to top level properties. The keep-or-replace-old notion applies
	 * within structs and arrays as described below.
	 * <ul>
	 * <li>If <code>replaceOldValues</code> is true then the processing is restricted to the top 
	 * level properties. The processed properties from the source (according to 
	 * <code>doAllProperties</code>) are propagated to the destination, 
	 * replacing any existing values.Properties in the destination that are not in the source 
	 * are left alone.
	 *
	 * <li>If <code>replaceOldValues</code> is not passed then the processing is more complicated. 
	 * Top level properties are added to the destination if they do not already exist. 
	 * If they do exist but differ in form (simple/struct/array) then the destination is left alone.
	 * If the forms match, simple properties are left unchanged while structs and arrays are merged.
	 * 
	 * <li>If <code>deleteEmptyValues</code> is passed then an empty value in the source XMP causes
	 * the corresponding destination XMP property to be deleted. The default is to treat empty 
	 * values the same as non-empty values. An empty value is any of a simple empty string, an array
	 * with no items, or a struct with no fields. Qualifiers are ignored.
	 * </ul>
	 * 
	 * <p>The detailed behavior is defined by the following pseudo-code:
	 * <blockquote>
	 * <pre>
     *    appendProperties ( sourceXMP, destXMP, doAllProperties, 
     *    			replaceOldValues, deleteEmptyValues ):
     *       for all source schema (top level namespaces):
     *          for all top level properties in sourceSchema:
     *             if doAllProperties or prop is external:
     *                appendSubtree ( sourceNode, destSchema, replaceOldValues, deleteEmptyValues )
     * 
     *    appendSubtree ( sourceNode, destParent, replaceOldValues, deleteEmptyValues ):
     *        if deleteEmptyValues and source value is empty:
     *            delete the corresponding child from destParent
     *        else if sourceNode not in destParent (by name):
     *           copy sourceNode's subtree to destParent
     *        else if replaceOld:
     *            delete subtree from destParent
     *            copy sourceNode's subtree to destParent
     *        else:
     *            // Already exists in dest and not replacing, merge structs and arrays
     *            if sourceNode and destNode forms differ:
     *                return, leave the destNode alone
     *            else if form is a struct:
     *                for each field in sourceNode:
     *                    AppendSubtree ( sourceNode.field, destNode, replaceOldValues )
     *            else if form is an alt-text array:
     *                copy new items by "xml:lang" value into the destination
     *            else if form is an array:
     *                copy new items by value into the destination, ignoring order and duplicates
     * </pre>
	 * </blockquote>
	 *
	 * <p><em>Note:</em> appendProperties can be expensive if replaceOldValues is not passed and 
	 * the XMP contains large arrays. The array item checking described above is n-squared. 
	 * Each source item is checked to see if it already exists in the destination, 
	 * without regard to order or duplicates.
	 * <p>Simple items are compared by value and "xml:lang" qualifier, other qualifiers are ignored.
	 * Structs are recursively compared by field names, without regard to field order. Arrays are
	 * compared by recursively comparing all items.
	 *
	 * @param source The source XMP object.
	 * @param dest The destination XMP object.
	 * @param doAllProperties Do internal properties in addition to external properties.
	 * @param replaceOldValues Replace the values of existing properties.
	 * @param deleteEmptyValues Delete destination values if source property is empty.
	 * @throws XMPException Forwards the Exceptions from the metadata processing 
	 */
	public static void appendProperties(XMPMeta source, XMPMeta dest, boolean doAllProperties,
			boolean replaceOldValues, boolean deleteEmptyValues) throws XMPException
	{
		XMPUtilsImpl.appendProperties(source, dest, doAllProperties, replaceOldValues, 
			deleteEmptyValues);
	}


	/**
	 * Convert from string to Boolean.
	 * 
	 * @param value
	 *            The string representation of the Boolean.
	 * @return The appropriate boolean value for the string. The checked values
	 *         for <code>true</code> and <code>false</code> are:
	 *         <ul>
	 *    	    	<li>{@link XMPConst#TRUESTR} and {@link XMPConst#FALSESTR}
	 *    		    <li>&quot;t&quot; and &quot;f&quot;
	 *    		    <li>&quot;on&quot; and &quot;off&quot;
	 *    		    <li>&quot;yes&quot; and &quot;no&quot;
	 *   		  	<li>&quot;value <> 0&quot; and &quot;value == 0&quot;
	 *         </ul>
	 * @throws XMPException If an empty string is passed.
	 */
	public static boolean convertToBoolean(String value) throws XMPException
	{
		if (value == null  ||  value.length() == 0)
		{
			throw new XMPException("Empty convert-string", XMPError.BADVALUE);
		}
		value = value.toLowerCase();
		
		try
		{
			// First try interpretation as Integer (anything not 0 is true)
			return Integer.parseInt(value) != 0;
		}
		catch (NumberFormatException e)
		{
			return
				"true".equals(value)  ||
				"t".equals(value)  ||
				"on".equals(value)  ||
				"yes".equals(value);
		}	
	}

	
	/**
	 * Convert from boolean to string.
	 * 
	 * @param value
	 *            a boolean value
	 * @return The XMP string representation of the boolean. The values used are
	 *         given by the constnts {@link XMPConst#TRUESTR} and
	 *         {@link XMPConst#FALSESTR}.
	 */
	public static String convertFromBoolean(boolean value)
	{
		return value ? XMPConst.TRUESTR : XMPConst.FALSESTR;
	}


	/**
	 * Converts a string value to an <code>int</code>.
	 * 
	 * @param rawValue
	 *            the string value
	 * @return Returns an int.
	 * @throws XMPException
	 *             If the <code>rawValue</code> is <code>null</code> or empty or the
	 *             conversion fails.
	 */
	public static int convertToInteger(String rawValue) throws XMPException
	{
		try
		{
			if (rawValue == null  ||  rawValue.length() == 0)
			{
				throw new XMPException("Empty convert-string", XMPError.BADVALUE);
			}
			if (rawValue.startsWith("0x"))
			{
				return Integer.parseInt(rawValue.substring(2), 16);
			}	
			else
			{
				return Integer.parseInt(rawValue);
			}
		}
		catch (NumberFormatException e)
		{
			throw new XMPException("Invalid integer string", XMPError.BADVALUE);
		}
	}


	/**
	 * Convert from int to string.
	 * 
	 * @param value
	 *            an int value
	 * @return The string representation of the int.
	 */
	public static String convertFromInteger(int value)
	{
		return String.valueOf(value);
	}
	

	/**
	 * Converts a string value to a <code>long</code>.
	 * 
	 * @param rawValue
	 *            the string value
	 * @return Returns a long.
	 * @throws XMPException
	 *             If the <code>rawValue</code> is <code>null</code> or empty or the
	 *             conversion fails.
	 */
	public static long convertToLong(String rawValue) throws XMPException
	{
		try
		{
			if (rawValue == null  ||  rawValue.length() == 0)
			{
				throw new XMPException("Empty convert-string", XMPError.BADVALUE);
			}
			if (rawValue.startsWith("0x"))
			{	
				return Long.parseLong(rawValue.substring(2), 16);
			}	
			else
			{
				return Long.parseLong(rawValue);
			}
		}
		catch (NumberFormatException e)
		{
			throw new XMPException("Invalid long string", XMPError.BADVALUE);
		}
	}


	/**
	 * Convert from long to string.
	 * 
	 * @param value
	 *            a long value
	 * @return The string representation of the long.
	 */
	public static String convertFromLong(long value)
	{
		return String.valueOf(value);
	}

	
	/**
	 * Converts a string value to a <code>double</code>.
	 * 
	 * @param rawValue
	 *            the string value
	 * @return Returns a double.
	 * @throws XMPException
	 *             If the <code>rawValue</code> is <code>null</code> or empty or the
	 *             conversion fails.
	 */
	public static double convertToDouble(String rawValue) throws XMPException
	{
		try
		{
			if (rawValue == null  ||  rawValue.length() == 0)
			{
				throw new XMPException("Empty convert-string", XMPError.BADVALUE);
			}
			else
			{
				return Double.parseDouble(rawValue);
			}
		}
		catch (NumberFormatException e)
		{
			throw new XMPException("Invalid double string", XMPError.BADVALUE);
		}
	}

	
	/**
	 * Convert from long to string.
	 * 
	 * @param value
	 *            a long value
	 * @return The string representation of the long.
	 */
	public static String convertFromDouble(double value)
	{
		return String.valueOf(value);
	}
	

	/**
	 * Converts a string value to an <code>XMPDateTime</code>.
	 * 
	 * @param rawValue
	 *            the string value
	 * @return Returns an <code>XMPDateTime</code>-object.
	 * @throws XMPException
	 *             If the <code>rawValue</code> is <code>null</code> or empty or the
	 *             conversion fails.
	 */
	public static XMPDateTime convertToDate(String rawValue) throws XMPException
	{
		if (rawValue == null  ||  rawValue.length() == 0)
		{
			throw new XMPException("Empty convert-string", XMPError.BADVALUE);
		}
		else
		{	
			return ISO8601Converter.parse(rawValue);
		}	
	}
	
	
	/**
	 * Convert from <code>XMPDateTime</code> to string.
	 * 
	 * @param value
	 *            an <code>XMPDateTime</code>
	 * @return The string representation of the long.
	 */
	public static String convertFromDate(XMPDateTime value)
	{
		return ISO8601Converter.render(value);
	}
	
	
	 /**
	  * Convert from a byte array to a base64 encoded string.
	  * 
	  * @param buffer
	  *            the byte array to be converted
	  * @return Returns the base64 string.
	  */
	public static String encodeBase64(byte[] buffer)
	{
		return new String(Base64.encode(buffer));
	}


	/**
	 * Decode from Base64 encoded string to raw data.
	 * 
	 * @param base64String
	 *            a base64 encoded string
	 * @return Returns a byte array containg the decoded string.
	 * @throws XMPException Thrown if the given string is not property base64 encoded
	 */
	public static byte[] decodeBase64(String base64String) throws XMPException
	{
		try
		{
			return Base64.decode(base64String.getBytes());
		}
		catch (Throwable e)
		{
			throw new XMPException("Invalid base64 string", XMPError.BADVALUE, e);
		}
	}			
}
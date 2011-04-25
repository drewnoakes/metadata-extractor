// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp;

import com.adobe.xmp.impl.Utils;
import com.adobe.xmp.impl.xpath.XMPPath;
import com.adobe.xmp.impl.xpath.XMPPathParser;

/**
 * Utility services for the metadata object. It has only public static functions, you cannot create
 * an object. These are all functions that layer cleanly on top of the core XMP toolkit.
 * <p>
 * These functions provide support for composing path expressions to deeply nested properties. The
 * functions <code>XMPMeta</code> such as <code>getProperty()</code>,
 * <code>getArrayItem()</code> and <code>getStructField()</code> provide easy access to top
 * level simple properties, items in top level arrays, and fields of top level structs. They do not
 * provide convenient access to more complex things like fields several levels deep in a complex
 * struct, or fields within an array of structs, or items of an array that is a field of a struct.
 * These functions can also be used to compose paths to top level array items or struct fields so
 * that you can use the binary accessors like <code>getPropertyAsInteger()</code>.
 * <p>
 * You can use these functions is to compose a complete path expression, or all but the last
 * component. Suppose you have a property that is an array of integers within a struct. You can
 * access one of the array items like this:
 * <p>
 * <blockquote>
 * 
 * <pre>
 *      String path = XMPPathFactory.composeStructFieldPath (schemaNS, &quot;Struct&quot;, fieldNS,
 *          &quot;Array&quot;);
 *      String path += XMPPathFactory.composeArrayItemPath (schemaNS, &quot;Array&quot; index);
 *      PropertyInteger result = xmpObj.getPropertyAsInteger(schemaNS, path);
 * </pre>
 * 
 * </blockquote> You could also use this code if you want the string form of the integer:
 * <blockquote>
 * 
 * <pre>
 *      String path = XMPPathFactory.composeStructFieldPath (schemaNS, &quot;Struct&quot;, fieldNS,
 *          &quot;Array&quot;);
 *      PropertyText xmpObj.getArrayItem (schemaNS, path, index);
 * </pre>
 * 
 * </blockquote>
 * <p>
 * <em>Note:</em> It might look confusing that the schemaNS is passed in all of the calls above.
 * This is because the XMP toolkit keeps the top level &quot;schema&quot; namespace separate from
 * the rest of the path expression.
 * <em>Note:</em> These methods are much simpler than in the C++-API, they don't check the given
 * path or array indices.
 * 
 * @since 25.01.2006
 */
public final class XMPPathFactory
{
	/** Private constructor */
	private XMPPathFactory()
	{
		// EMPTY
	}


	/**
	 * Compose the path expression for an item in an array.
	 * 
	 * @param arrayName The name of the array. May be a general path expression, must not be
	 *        <code>null</code> or the empty string.
	 * @param itemIndex The index of the desired item. Arrays in XMP are indexed from 1.
	 * 		  0 and below means last array item and renders as <code>[last()]</code>.	
	 * 					
	 * @return Returns the composed path basing on fullPath. This will be of the form
	 *         <tt>ns:arrayName[i]</tt>, where &quot;ns&quot; is the prefix for schemaNS and
	 *         &quot;i&quot; is the decimal representation of itemIndex.
	 * @throws XMPException Throws exeption if index zero is used.
	 */
	public static String composeArrayItemPath(String arrayName, int itemIndex) throws XMPException
	{
		if (itemIndex > 0)
		{
			return arrayName + '[' + itemIndex + ']';
		}
		else  if (itemIndex == XMPConst.ARRAY_LAST_ITEM)
		{
			return arrayName + "[last()]";
		}
		else
		{
			throw new XMPException("Array index must be larger than zero", XMPError.BADINDEX);
		}
	}

	
	/**
	 * Compose the path expression for a field in a struct. The result can be added to the
	 * path of 
	 * 
	 * 
	 * @param fieldNS The namespace URI for the field. Must not be <code>null</code> or the empty
	 *        string.
	 * @param fieldName The name of the field. Must be a simple XML name, must not be
	 *        <code>null</code> or the empty string.
	 * @return Returns the composed path. This will be of the form
	 *         <tt>ns:structName/fNS:fieldName</tt>, where &quot;ns&quot; is the prefix for
	 *         schemaNS and &quot;fNS&quot; is the prefix for fieldNS.
	 * @throws XMPException Thrown if the path to create is not valid.
	 */
	public static String composeStructFieldPath(String fieldNS,
			String fieldName) throws XMPException
	{
		assertFieldNS(fieldNS);
		assertFieldName(fieldName);
		
		XMPPath fieldPath = XMPPathParser.expandXPath(fieldNS, fieldName);
		if (fieldPath.size() != 2) 
		{
			throw new XMPException("The field name must be simple", XMPError.BADXPATH);
		}
		
		return '/' + fieldPath.getSegment(XMPPath.STEP_ROOT_PROP).getName(); 
	}


	/**
	 * Compose the path expression for a qualifier.
	 * 
	 * @param qualNS The namespace URI for the qualifier. May be <code>null</code> or the empty
	 *        string if the qualifier is in the XML empty namespace.
	 * @param qualName The name of the qualifier. Must be a simple XML name, must not be
	 *        <code>null</code> or the empty string.
	 * @return Returns the composed path. This will be of the form
	 *         <tt>ns:propName/?qNS:qualName</tt>, where &quot;ns&quot; is the prefix for
	 *         schemaNS and &quot;qNS&quot; is the prefix for qualNS.
	 * @throws XMPException Thrown if the path to create is not valid.
	 */
	public static String composeQualifierPath(
			String qualNS,
			String qualName) throws XMPException
	{
		assertQualNS(qualNS);
		assertQualName(qualName);
		
		XMPPath qualPath = XMPPathParser.expandXPath(qualNS, qualName);
		if (qualPath.size() != 2)
		{
			throw new XMPException("The qualifier name must be simple", XMPError.BADXPATH);
		}

		return "/?" + qualPath.getSegment(XMPPath.STEP_ROOT_PROP).getName();
	}


	/**
	 * Compose the path expression to select an alternate item by language. The
	 * path syntax allows two forms of &quot;content addressing&quot; that may
	 * be used to select an item in an array of alternatives. The form used in
	 * ComposeLangSelector lets you select an item in an alt-text array based on
	 * the value of its <tt>xml:lang</tt> qualifier. The other form of content
	 * addressing is shown in ComposeFieldSelector. \note ComposeLangSelector
	 * does not supplant SetLocalizedText or GetLocalizedText. They should
	 * generally be used, as they provide extra logic to choose the appropriate
	 * language and maintain consistency with the 'x-default' value.
	 * ComposeLangSelector gives you an path expression that is explicitly and
	 * only for the language given in the langName parameter.
	 * 
	 * @param arrayName
	 *            The name of the array. May be a general path expression, must
	 *            not be <code>null</code> or the empty string.
	 * @param langName
	 *            The RFC 3066 code for the desired language.
	 * @return Returns the composed path. This will be of the form
	 *         <tt>ns:arrayName[@xml:lang='langName']</tt>, where
	 *         &quot;ns&quot; is the prefix for schemaNS.
	 */
	public static String composeLangSelector(String arrayName,
			String langName)
	{
		return arrayName + "[?xml:lang=\"" + Utils.normalizeLangValue(langName) + "\"]";
	}


	/**
	 * Compose the path expression to select an alternate item by a field's value. The path syntax
	 * allows two forms of &quot;content addressing&quot; that may be used to select an item in an
	 * array of alternatives. The form used in ComposeFieldSelector lets you select an item in an
	 * array of structs based on the value of one of the fields in the structs. The other form of
	 * content addressing is shown in ComposeLangSelector. For example, consider a simple struct
	 * that has two fields, the name of a city and the URI of an FTP site in that city. Use this to
	 * create an array of download alternatives. You can show the user a popup built from the values
	 * of the city fields. You can then get the corresponding URI as follows:
	 * <p>
	 * <blockquote>
	 * 
	 * <pre>
	 *      String path = composeFieldSelector ( schemaNS, &quot;Downloads&quot;, fieldNS, 
	 *          &quot;City&quot;, chosenCity ); 
	 *      XMPProperty prop = xmpObj.getStructField ( schemaNS, path, fieldNS, &quot;URI&quot; );
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @param arrayName The name of the array. May be a general path expression, must not be
	 *        <code>null</code> or the empty string.
	 * @param fieldNS The namespace URI for the field used as the selector. Must not be
	 *        <code>null</code> or the empty string.
	 * @param fieldName The name of the field used as the selector. Must be a simple XML name, must
	 *        not be <code>null</code> or the empty string. It must be the name of a field that is
	 *        itself simple.
	 * @param fieldValue The desired value of the field.
	 * @return Returns the composed path. This will be of the form
	 *         <tt>ns:arrayName[fNS:fieldName='fieldValue']</tt>, where &quot;ns&quot; is the
	 *         prefix for schemaNS and &quot;fNS&quot; is the prefix for fieldNS.
	 * @throws XMPException Thrown if the path to create is not valid.
	 */
	public static String composeFieldSelector(String arrayName, String fieldNS,
			String fieldName, String fieldValue) throws XMPException
	{
		XMPPath fieldPath = XMPPathParser.expandXPath(fieldNS, fieldName);
		if (fieldPath.size() != 2) 
		{
			throw new XMPException("The fieldName name must be simple", XMPError.BADXPATH);
		}
		
		return arrayName + '[' + fieldPath.getSegment(XMPPath.STEP_ROOT_PROP).getName() +
			"=\"" + fieldValue + "\"]"; 
	}
	
	
	/**
	 * ParameterAsserts that a qualifier namespace is set.
	 * @param qualNS a qualifier namespace
	 * @throws XMPException Qualifier schema is null or empty
	 */
	private static void assertQualNS(String qualNS) throws XMPException
	{
		if (qualNS == null  ||  qualNS.length() == 0)
		{
			throw new XMPException("Empty qualifier namespace URI", XMPError.BADSCHEMA);
		}
		
	}
	
	
	/**
	 * ParameterAsserts that a qualifier name is set.
	 * @param qualName a qualifier name or path
	 * @throws XMPException Qualifier name is null or empty
	 */
	private static void assertQualName(String qualName) throws XMPException
	{
		if (qualName == null  ||  qualName.length() == 0)
		{
			throw new XMPException("Empty qualifier name", XMPError.BADXPATH);
		}
	}

	
	/**
	 * ParameterAsserts that a struct field namespace is set.
	 * @param fieldNS a struct field namespace
	 * @throws XMPException Struct field schema is null or empty
	 */
	private static void assertFieldNS(String fieldNS) throws XMPException
	{
		if (fieldNS == null  ||  fieldNS.length() == 0)
		{
			throw new XMPException("Empty field namespace URI", XMPError.BADSCHEMA);
		}
		
	}
	
	
	/**
	 * ParameterAsserts that a struct field name is set.
	 * @param fieldName a struct field name or path
	 * @throws XMPException Struct field name is null or empty
	 */
	private static void assertFieldName(String fieldName) throws XMPException
	{
		if (fieldName == null  ||  fieldName.length() == 0)
		{
			throw new XMPException("Empty f name", XMPError.BADXPATH);
		}
	}
}
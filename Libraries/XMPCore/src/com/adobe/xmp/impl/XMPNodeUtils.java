// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl;

import java.util.GregorianCalendar;
import java.util.Iterator;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPDateTimeFactory;
import com.adobe.xmp.XMPError;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPUtils;
import com.adobe.xmp.impl.xpath.XMPPath;
import com.adobe.xmp.impl.xpath.XMPPathSegment;
import com.adobe.xmp.options.AliasOptions;
import com.adobe.xmp.options.PropertyOptions;


/**
 * Utilities for <code>XMPNode</code>.
 * 
 * @since   Aug 28, 2006
 */
public class XMPNodeUtils implements XMPConst
{
	/** */
	static final int CLT_NO_VALUES = 0;
	/** */
	static final int CLT_SPECIFIC_MATCH = 1;
	/** */
	static final int CLT_SINGLE_GENERIC = 2;
	/** */
	static final int CLT_MULTIPLE_GENERIC = 3;
	/** */
	static final int CLT_XDEFAULT = 4;
	/** */
	static final int CLT_FIRST_ITEM = 5;


	/**
	 * Private Constructor
	 */
	private XMPNodeUtils()
	{
		// EMPTY
	}

	
	/**
	 * Find or create a schema node if <code>createNodes</code> is false and
	 *  
	 * @param tree the root of the xmp tree. 
	 * @param namespaceURI a namespace
	 * @param createNodes a flag indicating if the node shall be created if not found.
	 * 		  <em>Note:</em> The namespace must be registered prior to this call.
	 * 
	 * @return Returns the schema node if found, <code>null</code> otherwise.
	 * 		   Note: If <code>createNodes</code> is <code>true</code>, it is <b>always</b>
	 * 		   returned a valid node. 
	 * @throws XMPException An exception is only thrown if an error occurred, not if a
	 *         		node was not found.
	 */
	static XMPNode findSchemaNode(XMPNode tree, String namespaceURI,
			boolean createNodes)
			throws XMPException
	{
		return findSchemaNode(tree, namespaceURI, null, createNodes);
	}
	
	
	/**
	 * Find or create a schema node if <code>createNodes</code> is true.
	 *  
	 * @param tree the root of the xmp tree. 
	 * @param namespaceURI a namespace
	 * @param suggestedPrefix If a prefix is suggested, the namespace is allowed to be registered.
	 * @param createNodes a flag indicating if the node shall be created if not found.
	 * 		  <em>Note:</em> The namespace must be registered prior to this call.
	 * 
	 * @return Returns the schema node if found, <code>null</code> otherwise.
	 * 		   Note: If <code>createNodes</code> is <code>true</code>, it is <b>always</b>
	 * 		   returned a valid node. 
	 * @throws XMPException An exception is only thrown if an error occurred, not if a
	 *         		node was not found.
	 */
	static XMPNode findSchemaNode(XMPNode tree, String namespaceURI, String suggestedPrefix,
			boolean createNodes)
			throws XMPException
	{
		assert tree.getParent() == null; // make sure that its the root
		XMPNode schemaNode = tree.findChildByName(namespaceURI);
		
		if (schemaNode == null  &&  createNodes)
		{
			schemaNode = new XMPNode(namespaceURI, 
				new PropertyOptions()
					.setSchemaNode(true));
			schemaNode.setImplicit(true);
			
			// only previously registered schema namespaces are allowed in the XMP tree.
			String prefix = XMPMetaFactory.getSchemaRegistry().getNamespacePrefix(namespaceURI);
			if (prefix == null)
			{	
				if (suggestedPrefix != null  &&  suggestedPrefix.length() != 0)
				{
					prefix = XMPMetaFactory.getSchemaRegistry().registerNamespace(namespaceURI,
							suggestedPrefix);
				}
				else
				{
					throw new XMPException("Unregistered schema namespace URI",
							XMPError.BADSCHEMA);
				}	
			}	
				
			schemaNode.setValue(prefix);
	
			tree.addChild(schemaNode);
		}
		
		return schemaNode;
	}

	
	/**
	 * Find or create a child node under a given parent node. If the parent node is no 
	 * Returns the found or created child node.
	 * 
	 * @param parent
	 *            the parent node
	 * @param childName
	 *            the node name to find
	 * @param createNodes
	 *            flag, if new nodes shall be created.
	 * @return Returns the found or created node or <code>null</code>.
	 * @throws XMPException Thrown if 
	 */
	static XMPNode findChildNode(XMPNode parent, String childName, boolean createNodes)
			throws XMPException
	{
		if (!parent.getOptions().isSchemaNode() && !parent.getOptions().isStruct())
		{
			if (!parent.isImplicit())
			{
				throw new XMPException("Named children only allowed for schemas and structs",
						XMPError.BADXPATH);
			}	
			else if (parent.getOptions().isArray())
			{
				throw new XMPException("Named children not allowed for arrays",
						XMPError.BADXPATH);
			}
			else if (createNodes)
			{	
				parent.getOptions().setStruct(true);
			}	
		}
			
		XMPNode childNode = parent.findChildByName(childName); 
		
		if (childNode == null  &&  createNodes)
		{
			PropertyOptions options = new PropertyOptions();
			childNode = new XMPNode(childName, options);
			childNode.setImplicit(true);
			parent.addChild(childNode);
		}
		
		assert childNode != null ||  !createNodes;
	
		return childNode;
	}


	/**
	 * Follow an expanded path expression to find or create a node.
	 * 
	 * @param xmpTree the node to begin the search. 
	 * @param xpath the complete xpath
	 * @param createNodes flag if nodes shall be created 
	 * 			(when called by <code>setProperty()</code>)
	 * @param leafOptions the options for the created leaf nodes (only when
	 *			<code>createNodes == true</code>).
	 * @return Returns the node if found or created or <code>null</code>.
	 * @throws XMPException An exception is only thrown if an error occurred, 
	 * 			not if a node was not found.
	 */
	static XMPNode findNode(XMPNode xmpTree, XMPPath xpath, boolean createNodes,
		PropertyOptions leafOptions) throws XMPException
	{
		// check if xpath is set.
		if (xpath == null  ||  xpath.size() == 0)
		{
			throw new XMPException("Empty XMPPath", XMPError.BADXPATH);
		}

		// Root of implicitly created subtree to possible delete it later. 
		// Valid only if leaf is new.
		XMPNode rootImplicitNode = null; 
		XMPNode currNode = null;
		
		// resolve schema step
		currNode = findSchemaNode(xmpTree,
			xpath.getSegment(XMPPath.STEP_SCHEMA).getName(), createNodes);
		if (currNode == null) 
		{
			return null;
		}
		else if (currNode.isImplicit())
		{
			currNode.setImplicit(false);	// Clear the implicit node bit.
			rootImplicitNode = currNode;	// Save the top most implicit node.
		}


		// Now follow the remaining steps of the original XMPPath.
		try
		{
			for (int i = 1; i < xpath.size(); i++)
			{
				currNode = followXPathStep(currNode, xpath.getSegment(i), createNodes);
				if (currNode == null) 
				{
					if (createNodes)
					{	
						// delete implicitly created nodes
						deleteNode(rootImplicitNode);
					}	
					return null;
				}
				else if (currNode.isImplicit())
				{
					// clear the implicit node flag
					currNode.setImplicit(false);

					// if node is an ALIAS (can be only in root step, auto-create array 
					// when the path has been resolved from a not simple alias type
					if (i == 1  &&  
						xpath.getSegment(i).isAlias()  &&
						xpath.getSegment(i).getAliasForm() != 0)
					{
						currNode.getOptions().setOption(xpath.getSegment(i).getAliasForm(), true);
					}
					// "CheckImplicitStruct" in C++
					else if (i < xpath.size() - 1  &&
						xpath.getSegment(i).getKind() == XMPPath.STRUCT_FIELD_STEP  &&	
						!currNode.getOptions().isCompositeProperty())
					{
						currNode.getOptions().setStruct(true);
					}					
					
					if (rootImplicitNode == null)
					{
						rootImplicitNode = currNode;	// Save the top most implicit node.
					}
				}
			}
		}
		catch (XMPException e)
		{
			// if new notes have been created prior to the error, delete them
			if (rootImplicitNode != null)
			{
				deleteNode(rootImplicitNode);
			}
			throw e;
		}
		
		
		if (rootImplicitNode != null)
		{
			// set options only if a node has been successful created
			currNode.getOptions().mergeWith(leafOptions);
			currNode.setOptions(currNode.getOptions());
		}
		
		return currNode;
	}


	/**
	 * Deletes the the given node and its children from its parent.
	 * Takes care about adjusting the flags.
	 * @param node the top-most node to delete.
	 */
	static void deleteNode(XMPNode node)
	{
		XMPNode parent = node.getParent();
		
		if (node.getOptions().isQualifier())
		{
			// root is qualifier
			parent.removeQualifier(node);
		}
		else
		{
			// root is NO qualifier
			parent.removeChild(node);
		}
		
		// delete empty Schema nodes
		if (!parent.hasChildren()  &&  parent.getOptions().isSchemaNode())
		{
			parent.getParent().removeChild(parent);
		}
	}


	/**
	 * This is setting the value of a leaf node.
	 * 
	 * @param node an XMPNode
	 * @param value a value
	 */
	static void setNodeValue(XMPNode node, Object value)
	{
		String strValue = serializeNodeValue(value);
		if (!(node.getOptions().isQualifier()  &&  XML_LANG.equals(node.getName()))) 
		{	
			node.setValue(strValue);
		}
		else
		{
			node.setValue(Utils.normalizeLangValue(strValue));
		}
	}
	
	
	/**
	 * Verifies the PropertyOptions for consistancy and updates them as needed. 
	 * If options are <code>null</code> they are created with default values.
	 *  
	 * @param options the <code>PropertyOptions</code>
	 * @param itemValue the node value to set
	 * @return Returns the updated options.
	 * @throws XMPException If the options are not consistant. 
	 */
	static PropertyOptions verifySetOptions(PropertyOptions options, Object itemValue)
			throws XMPException
	{
		// create empty and fix existing options
		if (options == null)
		{
			// set default options
			options = new PropertyOptions();
		}
		
		if (options.isArrayAltText())
		{
			options.setArrayAlternate(true);
		}
	
		if (options.isArrayAlternate())
		{
			options.setArrayOrdered(true);
		}
	
		if (options.isArrayOrdered())
		{
			options.setArray(true);
		}
	
		if (options.isCompositeProperty() && itemValue != null && itemValue.toString().length() > 0)
		{
			throw new XMPException("Structs and arrays can't have values",
				XMPError.BADOPTIONS);
		}
	
		options.assertConsistency(options.getOptions());
		
		return options;
	}


	/**
	 * Converts the node value to String, apply special conversions for defined
	 * types in XMP.
	 * 
	 * @param value
	 *            the node value to set
	 * @return Returns the String representation of the node value.
	 */
	static String serializeNodeValue(Object value)
	{
		String strValue;
		if (value == null)
		{
			strValue = null;
		}
		else if (value instanceof Boolean)
		{
			strValue = XMPUtils.convertFromBoolean(((Boolean) value).booleanValue());
		}
		else if (value instanceof Integer)
		{
			strValue = XMPUtils.convertFromInteger(((Integer) value).intValue());
		}
		else if (value instanceof Long)
		{
			strValue = XMPUtils.convertFromLong(((Long) value).longValue());
		}
		else if (value instanceof Double)
		{
			strValue = XMPUtils.convertFromDouble(((Double) value).doubleValue());
		}
		else if (value instanceof XMPDateTime)
		{
			strValue = XMPUtils.convertFromDate((XMPDateTime) value);
		}
		else if (value instanceof GregorianCalendar)
		{
			XMPDateTime dt = XMPDateTimeFactory.createFromCalendar((GregorianCalendar) value);
			strValue = XMPUtils.convertFromDate(dt);
		}
		else if (value instanceof byte[])
		{
			strValue = XMPUtils.encodeBase64((byte[]) value);
		}
		else
		{
			strValue = value.toString();
		}
	
		return strValue != null ? Utils.removeControlChars(strValue) : null;
	}
	
	
	/** 
	 * After processing by ExpandXPath, a step can be of these forms:
	 * <ul>
	 * 	<li>qualName - A top level property or struct field.
	 * <li>[index] - An element of an array.
	 * <li>[last()] - The last element of an array.
	 * <li>[qualName="value"] - An element in an array of structs, chosen by a field value.
	 * <li>[?qualName="value"] - An element in an array, chosen by a qualifier value.
	 * <li>?qualName - A general qualifier.
	 * </ul>
	 * Find the appropriate child node, resolving aliases, and optionally creating nodes.
	 * 
	 * @param parentNode the node to start to start from 
	 * @param nextStep the xpath segment 
	 * @param createNodes 
	 * @return returns the found or created XMPPath node 
	 * @throws XMPException 
	 */
	private static XMPNode followXPathStep(
				XMPNode parentNode, 
				XMPPathSegment nextStep,
				boolean createNodes) throws XMPException
	{
		XMPNode nextNode = null;
		int index = 0;
		int stepKind = nextStep.getKind();
		
		if (stepKind == XMPPath.STRUCT_FIELD_STEP)
		{
			nextNode = findChildNode(parentNode, nextStep.getName(), createNodes);
		}
		else if (stepKind == XMPPath.QUALIFIER_STEP)
		{
			nextNode = findQualifierNode(
				parentNode, nextStep.getName().substring(1), createNodes);
		}
		else
		{
			// This is an array indexing step. First get the index, then get the node.

			if (!parentNode.getOptions().isArray())
			{
				throw new XMPException("Indexing applied to non-array", XMPError.BADXPATH);
			}

			if (stepKind == XMPPath.ARRAY_INDEX_STEP)
			{
				index = findIndexedItem(parentNode, nextStep.getName(), createNodes);
			}
			else if (stepKind == XMPPath.ARRAY_LAST_STEP)
			{
				index = parentNode.getChildrenLength();
			}
			else if (stepKind == XMPPath.FIELD_SELECTOR_STEP)
			{
				String[] result = Utils.splitNameAndValue(nextStep.getName());
				String fieldName = result[0];
				String fieldValue = result[1];
				index = lookupFieldSelector(parentNode, fieldName, fieldValue);
			}
			else if (stepKind == XMPPath.QUAL_SELECTOR_STEP)
			{
				String[] result = Utils.splitNameAndValue(nextStep.getName());
				String qualName = result[0];
				String qualValue = result[1];
				index = lookupQualSelector(
					parentNode, qualName, qualValue, nextStep.getAliasForm());
			}
			else
			{
				throw new XMPException("Unknown array indexing step in FollowXPathStep",
						XMPError.INTERNALFAILURE);
			}

			if (1 <= index  &&  index <=  parentNode.getChildrenLength())
			{
				nextNode = parentNode.getChild(index);
			}
		}
	
		return nextNode;		
	}


	/**
	 * Find or create a qualifier node under a given parent node. Returns a pointer to the 
	 * qualifier node, and optionally an iterator for the node's position in 
	 * the parent's vector of qualifiers. The iterator is unchanged if no qualifier node (null) 
	 * is returned.
	 * <em>Note:</em> On entry, the qualName parameter must not have the leading '?' from the 
	 * XMPPath step.
	 * 
	 * @param parent the parent XMPNode 
	 * @param qualName the qualifier name
	 * @param createNodes flag if nodes shall be created
	 * @return Returns the qualifier node if found or created, <code>null</code> otherwise.
	 * @throws XMPException 
	 */
	private static XMPNode findQualifierNode(XMPNode parent, String qualName, boolean createNodes)
			throws XMPException
	{
		assert !qualName.startsWith("?");
		
		XMPNode qualNode = parent.findQualifierByName(qualName);
		
		if (qualNode == null  &&  createNodes)
		{
			qualNode = new XMPNode(qualName, null);
			qualNode.setImplicit(true);
	
			parent.addQualifier(qualNode);				
		}
		
		return qualNode;
	}
	
	
	/**
	 * @param arrayNode an array node
	 * @param segment the segment containing the array index
	 * @param createNodes flag if new nodes are allowed to be created.
	 * @return Returns the index or index = -1 if not found
	 * @throws XMPException Throws Exceptions
	 */
	private static int findIndexedItem(XMPNode arrayNode, String segment, boolean createNodes)
			throws XMPException
	{
		int index = 0;
	
		try
		{
			segment = segment.substring(1, segment.length() - 1);
			index = Integer.parseInt(segment);
			if (index < 1)
			{
				throw new XMPException("Array index must be larger than zero",
						XMPError.BADXPATH);
			}
		}
		catch (NumberFormatException e)
		{
			throw new XMPException("Array index not digits.", XMPError.BADXPATH);
		}
	
		if (createNodes  &&  index == arrayNode.getChildrenLength() + 1)
		{
			// Append a new last + 1 node.
			XMPNode newItem = new XMPNode(ARRAY_ITEM_NAME, null);
			newItem.setImplicit(true);
			arrayNode.addChild(newItem);
		}

		return index;
	}
	
	
	/**
	 * Searches for a field selector in a node:
	 * [fieldName="value] - an element in an array of structs, chosen by a field value.
	 * No implicit nodes are created by field selectors. 
	 * 
	 * @param arrayNode
	 * @param fieldName
	 * @param fieldValue
	 * @return Returns the index of the field if found, otherwise -1.
	 * @throws XMPException 
	 */
	private static int lookupFieldSelector(XMPNode arrayNode, String fieldName, String fieldValue)
		throws XMPException
	{
		int result = -1;
		
		for (int index = 1; index <= arrayNode.getChildrenLength()  &&  result < 0; index++)
		{
			XMPNode currItem = arrayNode.getChild(index);
	
			if (!currItem.getOptions().isStruct())
			{
				throw new XMPException("Field selector must be used on array of struct",
						XMPError.BADXPATH);
			}
	
			for (int f = 1; f <= currItem.getChildrenLength(); f++)
			{
				XMPNode currField = currItem.getChild(f);
				if (!fieldName.equals(currField.getName()))
				{
					continue;
				}
				if (fieldValue.equals(currField.getValue())) 
				{
					result = index;
					break;
				}
			}
		}
		
		return result;
	}
	
	
	/**
	 * Searches for a qualifier selector in a node:
	 * [?qualName="value"] - an element in an array, chosen by a qualifier value.
	 * No implicit nodes are created for qualifier selectors, 
	 * except for an alias to an x-default item.
	 * 
	 * @param arrayNode an array node 
	 * @param qualName the qualifier name
	 * @param qualValue the qualifier value
	 * @param aliasForm in case the qual selector results from an alias,
	 * 		  an x-default node is created if there has not been one. 
	 * @return Returns the index of th
	 * @throws XMPException 
	 */
	private static int lookupQualSelector(XMPNode arrayNode, String qualName, 
		String qualValue, int aliasForm) throws XMPException
	{
		if (XML_LANG.equals(qualName))
		{
			qualValue = Utils.normalizeLangValue(qualValue);
			int index = XMPNodeUtils.lookupLanguageItem(arrayNode, qualValue);
			if (index < 0  &&  (aliasForm & AliasOptions.PROP_ARRAY_ALT_TEXT) > 0)
			{	
				XMPNode langNode = new XMPNode(ARRAY_ITEM_NAME, null);
				XMPNode xdefault = new XMPNode(XML_LANG, X_DEFAULT, null);
				langNode.addQualifier(xdefault);
				arrayNode.addChild(1, langNode);
				return 1;
			}
			else
			{
				return index;
			}
		}
		else
		{
			for (int index = 1; index < arrayNode.getChildrenLength(); index++)
			{
				XMPNode currItem = arrayNode.getChild(index);
		
				for (Iterator it = currItem.iterateQualifier(); it.hasNext();)
				{
					XMPNode qualifier = (XMPNode) it.next();
					if (qualName.equals(qualifier.getName())  &&
						qualValue.equals(qualifier.getValue()))
					{
						return index;
					}
				}
			}
			return -1;
		}	
	}


	/**
	 * Make sure the x-default item is first. Touch up &quot;single value&quot;
	 * arrays that have a default plus one real language. This case should have
	 * the same value for both items. Older Adobe apps were hardwired to only
	 * use the &quot;x-default&quot; item, so we copy that value to the other
	 * item.
	 * 
	 * @param arrayNode
	 *            an alt text array node
	 */
	static void normalizeLangArray(XMPNode arrayNode)
	{
		if (!arrayNode.getOptions().isArrayAltText())
		{
			return;
		}
	
		// check if node with x-default qual is first place
		for (int i = 2; i <= arrayNode.getChildrenLength(); i++)
		{
			XMPNode child = arrayNode.getChild(i);
			if (child.hasQualifier() && X_DEFAULT.equals(child.getQualifier(1).getValue()))
			{
				// move node to first place
				try
				{
					arrayNode.removeChild(i);
					arrayNode.addChild(1, child);
				}
				catch (XMPException e)
				{
					// cannot occur, because same child is removed before
					assert false;
				}
				
				if (i == 2)
				{
					arrayNode.getChild(2).setValue(child.getValue());
				}
				break;
			}
		}
	}


	/**
	 * See if an array is an alt-text array. If so, make sure the x-default item
	 * is first.
	 * 
	 * @param arrayNode
	 *            the array node to check if its an alt-text array
	 */
	static void detectAltText(XMPNode arrayNode)
	{
		if (arrayNode.getOptions().isArrayAlternate() && arrayNode.hasChildren())
		{
			boolean isAltText = false;
			for (Iterator it = arrayNode.iterateChildren(); it.hasNext();)
			{
				XMPNode child = (XMPNode) it.next();
				if (child.getOptions().getHasLanguage())
				{
					isAltText = true;
					break;
				}
			}
	
			if (isAltText)
			{
				arrayNode.getOptions().setArrayAltText(true);
				normalizeLangArray(arrayNode);
			}
		}
	}


	/**
	 * Appends a language item to an alt text array.
	 * 
	 * @param arrayNode the language array
	 * @param itemLang the language of the item
	 * @param itemValue the content of the item
	 * @throws XMPException Thrown if a duplicate property is added
	 */
	static void appendLangItem(XMPNode arrayNode, String itemLang, String itemValue)
			throws XMPException
	{
		XMPNode newItem = new XMPNode(ARRAY_ITEM_NAME, itemValue, null);
		XMPNode langQual = new XMPNode(XML_LANG, itemLang, null);
		newItem.addQualifier(langQual);
	
		if (!X_DEFAULT.equals(langQual.getValue()))
		{
			arrayNode.addChild(newItem);
		}
		else
		{
			arrayNode.addChild(1, newItem);
		}
	}


	/**
	 * <ol>
	 * <li>Look for an exact match with the specific language.
	 * <li>If a generic language is given, look for partial matches.
	 * <li>Look for an "x-default"-item.
	 * <li>Choose the first item.
	 * </ol>
	 * 
	 * @param arrayNode
	 *            the alt text array node
	 * @param genericLang
	 *            the generic language
	 * @param specificLang
	 *            the specific language
	 * @return Returns the kind of match as an Integer and the found node in an
	 *         array.
	 * 
	 * @throws XMPException
	 */
	static Object[] chooseLocalizedText(XMPNode arrayNode, String genericLang, String specificLang)
			throws XMPException
	{
		// See if the array has the right form. Allow empty alt arrays,
		// that is what parsing returns.
		if (!arrayNode.getOptions().isArrayAltText())
		{
			throw new XMPException("Localized text array is not alt-text", XMPError.BADXPATH);
		}
		else if (!arrayNode.hasChildren())
		{
			return new Object[] { new Integer(XMPNodeUtils.CLT_NO_VALUES), null };
		}
	
		int foundGenericMatches = 0;
		XMPNode resultNode = null;
		XMPNode xDefault = null;
	
		// Look for the first partial match with the generic language.
		for (Iterator it = arrayNode.iterateChildren(); it.hasNext();)
		{
			XMPNode currItem = (XMPNode) it.next();
	
			// perform some checks on the current item
			if (currItem.getOptions().isCompositeProperty())
			{
				throw new XMPException("Alt-text array item is not simple", XMPError.BADXPATH);
			}
			else if (!currItem.hasQualifier()
					|| !XML_LANG.equals(currItem.getQualifier(1).getName()))
			{
				throw new XMPException("Alt-text array item has no language qualifier",
						XMPError.BADXPATH);
			}
	
			String currLang = currItem.getQualifier(1).getValue();
	
			// Look for an exact match with the specific language.
			if (specificLang.equals(currLang))
			{
				return new Object[] { new Integer(XMPNodeUtils.CLT_SPECIFIC_MATCH), currItem };
			}
			else if (genericLang != null && currLang.startsWith(genericLang))
			{
				if (resultNode == null)
				{
					resultNode = currItem;
				}
				// ! Don't return/break, need to look for other matches.
				foundGenericMatches++;
			}
			else if (X_DEFAULT.equals(currLang))
			{
				xDefault = currItem;
			}
		}
	
		// evaluate loop
		if (foundGenericMatches == 1)
		{
			return new Object[] { new Integer(XMPNodeUtils.CLT_SINGLE_GENERIC), resultNode };
		}
		else if (foundGenericMatches > 1)
		{
			return new Object[] { new Integer(XMPNodeUtils.CLT_MULTIPLE_GENERIC), resultNode };
		}
		else if (xDefault != null)
		{
			return new Object[] { new Integer(XMPNodeUtils.CLT_XDEFAULT), xDefault };
		}
		else
		{
			// Everything failed, choose the first item.
			return new Object[] { new Integer(XMPNodeUtils.CLT_FIRST_ITEM), arrayNode.getChild(1) };
		}
	}


	/**
	 * Looks for the appropriate language item in a text alternative array.item
	 * 
	 * @param arrayNode
	 *            an array node
	 * @param language
	 *            the requested language
	 * @return Returns the index if the language has been found, -1 otherwise.
	 * @throws XMPException
	 */
	static int lookupLanguageItem(XMPNode arrayNode, String language) throws XMPException
	{
		if (!arrayNode.getOptions().isArray())
		{
			throw new XMPException("Language item must be used on array", XMPError.BADXPATH);
		}
	
		for (int index = 1; index <= arrayNode.getChildrenLength(); index++)
		{
			XMPNode child = arrayNode.getChild(index);
			if (!child.hasQualifier() || !XML_LANG.equals(child.getQualifier(1).getName()))
			{
				continue;
			}
			else if (language.equals(child.getQualifier(1).getValue()))
			{
				return index;
			}
		}
	
		return -1;
	}
}

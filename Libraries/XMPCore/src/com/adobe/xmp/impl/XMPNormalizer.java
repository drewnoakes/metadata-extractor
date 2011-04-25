// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPError;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPUtils;
import com.adobe.xmp.impl.xpath.XMPPath;
import com.adobe.xmp.impl.xpath.XMPPathParser;
import com.adobe.xmp.options.ParseOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPAliasInfo;

/**
 * @since   Aug 18, 2006
 */
public class XMPNormalizer
{
	/** caches the correct dc-property array forms */
	private static Map dcArrayForms;
	/** init char tables */
	static
	{
		initDCArrays();
	}
	
	
	/**
	 * Hidden constructor
	 */
	private XMPNormalizer()
	{
		// EMPTY
	}

	
	/**
	 * Normalizes a raw parsed XMPMeta-Object
	 * @param xmp the raw metadata object
	 * @param options the parsing options
	 * @return Returns the normalized metadata object
	 * @throws XMPException Collects all severe processing errors. 
	 */
	static XMPMeta process(XMPMetaImpl xmp, ParseOptions options) throws XMPException 
	{
		XMPNode tree = xmp.getRoot();

		touchUpDataModel(xmp);
		moveExplicitAliases(tree, options);
		
		tweakOldXMP(tree);
		
		deleteEmptySchemas(tree);
		
		return xmp;
	}
	
	
	/**
	 * Tweak old XMP: Move an instance ID from rdf:about to the
	 * <em>xmpMM:InstanceID</em> property. An old instance ID usually looks
	 * like &quot;uuid:bac965c4-9d87-11d9-9a30-000d936b79c4&quot;, plus InDesign
	 * 3.0 wrote them like &quot;bac965c4-9d87-11d9-9a30-000d936b79c4&quot;. If
	 * the name looks like a UUID simply move it to <em>xmpMM:InstanceID</em>,
	 * don't worry about any existing <em>xmpMM:InstanceID</em>. Both will
	 * only be present when a newer file with the <em>xmpMM:InstanceID</em>
	 * property is updated by an old app that uses <em>rdf:about</em>.
	 * 
	 * @param tree the root of the metadata tree
	 * @throws XMPException Thrown if tweaking fails. 
	 */
	private static void tweakOldXMP(XMPNode tree) throws XMPException
	{
		if (tree.getName() != null  &&  tree.getName().length() >= Utils.UUID_LENGTH)
		{
			String nameStr = tree.getName().toLowerCase();
			if (nameStr.startsWith("uuid:"))
			{
				nameStr = nameStr.substring(5);
			}
			
			if (Utils.checkUUIDFormat(nameStr))
			{	
				// move UUID to xmpMM:InstanceID and remove it from the root node
				XMPPath path = XMPPathParser.expandXPath(XMPConst.NS_XMP_MM, "InstanceID");
				XMPNode idNode = XMPNodeUtils.findNode (tree, path, true, null);
				if (idNode != null)
				{
					idNode.setOptions(null);	// Clobber any existing xmpMM:InstanceID.
					idNode.setValue("uuid:" + nameStr);
					idNode.removeChildren();
					idNode.removeQualifiers();		
					tree.setName(null);
				}
				else
				{	
					throw new XMPException("Failure creating xmpMM:InstanceID",
							XMPError.INTERNALFAILURE);
				}	
			}
		}		
	}

	
	/**
	 * Visit all schemas to do general fixes and handle special cases.
	 * 
	 * @param xmp the metadata object implementation
	 * @throws XMPException Thrown if the normalisation fails.
	 */
	private static void touchUpDataModel(XMPMetaImpl xmp) throws XMPException
	{
		// make sure the DC schema is existing, because it might be needed within the normalization
		// if not touched it will be removed by removeEmptySchemas
		XMPNodeUtils.findSchemaNode(xmp.getRoot(), XMPConst.NS_DC, true);
		
		// Do the special case fixes within each schema.
		for (Iterator it = xmp.getRoot().iterateChildren(); it.hasNext();)
		{
			XMPNode currSchema = (XMPNode) it.next();
			if (XMPConst.NS_DC.equals(currSchema.getName()))
			{
				normalizeDCArrays(currSchema);
			}
			else if (XMPConst.NS_EXIF.equals(currSchema.getName()))
			{
				// Do a special case fix for exif:GPSTimeStamp.
				fixGPSTimeStamp(currSchema);
				XMPNode arrayNode = XMPNodeUtils.findChildNode(currSchema, "exif:UserComment",
						false);
				if (arrayNode != null)
				{
					repairAltText(arrayNode);
				}
			}
			else if (XMPConst.NS_DM.equals(currSchema.getName()))
			{
				// Do a special case migration of xmpDM:copyright to
				// dc:rights['x-default'].
				XMPNode dmCopyright = XMPNodeUtils.findChildNode(currSchema, "xmpDM:copyright",
						false);
				if (dmCopyright != null)
				{
					migrateAudioCopyright(xmp, dmCopyright);
				}
			}
			else if (XMPConst.NS_XMP_RIGHTS.equals(currSchema.getName()))
			{
				XMPNode arrayNode = XMPNodeUtils.findChildNode(currSchema, "xmpRights:UsageTerms",
						false);			
				if (arrayNode != null)
				{	
					repairAltText(arrayNode);
				}	
			}
		}
	}
	

	/**
	 * Undo the denormalization performed by the XMP used in Acrobat 5.<br> 
	 * If a Dublin Core array had only one item, it was serialized as a simple
	 * property. <br>
	 * The <code>xml:lang</code> attribute was dropped from an
	 * <code>alt-text</code> item if the language was <code>x-default</code>.
	 * 
	 * @param dcSchema the DC schema node
	 * @throws XMPException Thrown if normalization fails
	 */
	private static void normalizeDCArrays(XMPNode dcSchema) throws XMPException
	{
		for (int i = 1; i <= dcSchema.getChildrenLength(); i++)
		{
			XMPNode currProp = dcSchema.getChild(i);
			
			PropertyOptions arrayForm = (PropertyOptions) dcArrayForms.get(currProp.getName());
			if (arrayForm == null)
			{
				continue;
			}
			else if (currProp.getOptions().isSimple())
			{	
				// create a new array and add the current property as child, 
				// if it was formerly simple 
				XMPNode newArray = new XMPNode(currProp.getName(), arrayForm);
				currProp.setName(XMPConst.ARRAY_ITEM_NAME);
				newArray.addChild(currProp);
				dcSchema.replaceChild(i, newArray);
	
				// fix language alternatives
				if (arrayForm.isArrayAltText()  &&  !currProp.getOptions().getHasLanguage())
				{
					XMPNode newLang = new XMPNode(XMPConst.XML_LANG, XMPConst.X_DEFAULT, null);
					currProp.addQualifier(newLang);
				}
			}
			else
			{
				// clear array options and add corrected array form if it has been an array before
				currProp.getOptions().setOption(
					PropertyOptions.ARRAY  |
					PropertyOptions.ARRAY_ORDERED  |
					PropertyOptions.ARRAY_ALTERNATE  |
					PropertyOptions.ARRAY_ALT_TEXT,  
					false);
				currProp.getOptions().mergeWith(arrayForm);
				
				if (arrayForm.isArrayAltText())
				{
					// applying for "dc:description", "dc:rights", "dc:title"
					repairAltText(currProp);
				}
			}
		
		}
	}

	
	/**
	 * Make sure that the array is well-formed AltText. Each item must be simple
	 * and have an "xml:lang" qualifier. If repairs are needed, keep simple
	 * non-empty items by adding the "xml:lang" with value "x-repair".
	 * @param arrayNode the property node of the array to repair.
	 * @throws XMPException Forwards unexpected exceptions.
	 */
	private static void repairAltText(XMPNode arrayNode) throws XMPException
	{
		if (arrayNode == null  ||  
			!arrayNode.getOptions().isArray())
		{
			// Already OK or not even an array.
			return;	
		}

		// fix options
		arrayNode.getOptions().setArrayOrdered(true).setArrayAlternate(true).setArrayAltText(true);
		
		for (Iterator it = arrayNode.iterateChildren(); it.hasNext();)
		{
			XMPNode currChild = (XMPNode) it.next();
			if (currChild.getOptions().isCompositeProperty())
			{
				// Delete non-simple children.
				it.remove();
			} 
			else if (!currChild.getOptions().getHasLanguage())
			{
				String childValue = currChild.getValue();
				if (childValue == null  ||  childValue.length() == 0)
				{
					// Delete empty valued children that have no xml:lang.
					it.remove();
				}
				else
				{
					// Add an xml:lang qualifier with the value "x-repair".
					XMPNode repairLang = new XMPNode(XMPConst.XML_LANG, "x-repair", null);
					currChild.addQualifier(repairLang);
				}
			}
		}		
	}
	

	/**
	 * Visit all of the top level nodes looking for aliases. If there is
	 * no base, transplant the alias subtree. If there is a base and strict
	 * aliasing is on, make sure the alias and base subtrees match.
	 * 
	 * @param tree the root of the metadata tree
	 * @param options th parsing options
	 * @throws XMPException Forwards XMP errors
	 */
	private static void moveExplicitAliases(XMPNode tree, ParseOptions options)
			throws XMPException
	{
		if (!tree.getHasAliases())
		{
			return;
		}
		tree.setHasAliases(false);
		
		boolean strictAliasing = options.getStrictAliasing();

		for (Iterator schemaIt = tree.getUnmodifiableChildren().iterator(); schemaIt.hasNext();)
		{
			XMPNode currSchema = (XMPNode) schemaIt.next();
			if (!currSchema.getHasAliases())
			{
				continue;
			}
			
			for (Iterator propertyIt = currSchema.iterateChildren(); propertyIt.hasNext();)
			{
				XMPNode currProp = (XMPNode) propertyIt.next();
				
				if (!currProp.isAlias())
				{
					continue;
				}
				
				currProp.setAlias(false);
	
				// Find the base path, look for the base schema and root node.
				XMPAliasInfo info = XMPMetaFactory.getSchemaRegistry()
						.findAlias(currProp.getName());
				if (info != null)
				{	
					// find or create schema
					XMPNode baseSchema = XMPNodeUtils.findSchemaNode(tree, info
							.getNamespace(), null, true);
					baseSchema.setImplicit(false);
					
					XMPNode baseNode = XMPNodeUtils
							.findChildNode(baseSchema, 
								info.getPrefix() + info.getPropName(), false);
					if (baseNode == null)
					{
						if (info.getAliasForm().isSimple())
						{
							// A top-to-top alias, transplant the property.
							// change the alias property name to the base name
							String qname = info.getPrefix() + info.getPropName();
							currProp.setName(qname);
							baseSchema.addChild(currProp);
							// remove the alias property
							propertyIt.remove();
						}
						else
						{
							// An alias to an array item, 
							// create the array and transplant the property.
							baseNode = new XMPNode(info.getPrefix() + info.getPropName(), info
									.getAliasForm().toPropertyOptions());
							baseSchema.addChild(baseNode);
							transplantArrayItemAlias (propertyIt, currProp, baseNode);
						}
					
					} 
					else if (info.getAliasForm().isSimple())
					{
						// The base node does exist and this is a top-to-top alias.
						// Check for conflicts if strict aliasing is on. 
						// Remove and delete the alias subtree.
						if (strictAliasing)
						{
							compareAliasedSubtrees (currProp, baseNode, true);
						}
						
						propertyIt.remove();
					}
					else
					{
						// This is an alias to an array item and the array exists.
						// Look for the aliased item.
						// Then transplant or check & delete as appropriate.
						
						XMPNode  itemNode = null;
						if (info.getAliasForm().isArrayAltText())
						{
							int xdIndex = XMPNodeUtils.lookupLanguageItem(baseNode,
									XMPConst.X_DEFAULT); 
							if (xdIndex != -1)
							{
								itemNode = baseNode.getChild(xdIndex);
							}
						}
						else if (baseNode.hasChildren())
						{
							itemNode = baseNode.getChild(1);
						}
						
						if (itemNode == null)
						{
							transplantArrayItemAlias (propertyIt, currProp, baseNode);
						}
						else
						{
							if (strictAliasing)
							{
								compareAliasedSubtrees (currProp, itemNode, true);
							}
							
							propertyIt.remove();
						}
					}
				}
			}
			currSchema.setHasAliases(false);
		}
	}

	
	/**
	 * Moves an alias node of array form to another schema into an array
	 * @param propertyIt the property iterator of the old schema (used to delete the property)
	 * @param childNode the node to be moved
	 * @param baseArray the base array for the array item 
	 * @throws XMPException Forwards XMP errors
	 */
	private static void transplantArrayItemAlias(Iterator propertyIt, XMPNode childNode,
			XMPNode baseArray) throws XMPException
	{
		if (baseArray.getOptions().isArrayAltText())
		{
			if (childNode.getOptions().getHasLanguage())
			{
				throw new XMPException("Alias to x-default already has a language qualifier",
						XMPError.BADXMP);
			}
			
			XMPNode langQual = new XMPNode(XMPConst.XML_LANG, XMPConst.X_DEFAULT, null);
			childNode.addQualifier(langQual);
		}
	
		propertyIt.remove();
		childNode.setName(XMPConst.ARRAY_ITEM_NAME);
		baseArray.addChild(childNode);
	}
	
	
	/**
	 * Fixes the GPS Timestamp in EXIF.
	 * @param exifSchema the EXIF schema node
	 * @throws XMPException Thrown if the date conversion fails.
	 */
	private static void fixGPSTimeStamp(XMPNode exifSchema)
			throws XMPException
	{
		// Note: if dates are not found the convert-methods throws an exceptions,
		// 		 and this methods returns.
		XMPNode gpsDateTime = XMPNodeUtils.findChildNode(exifSchema, "exif:GPSTimeStamp", false);
		if (gpsDateTime == null)
		{
			return;
		}
		
		try
		{
			XMPDateTime binGPSStamp;
			XMPDateTime binOtherDate;
			
			binGPSStamp = XMPUtils.convertToDate(gpsDateTime.getValue());
			if (binGPSStamp.getYear() != 0  || 
				binGPSStamp.getMonth() != 0  ||
				binGPSStamp.getDay() != 0)
			{
				return;
			}
			
			XMPNode otherDate = XMPNodeUtils.findChildNode(exifSchema, "exif:DateTimeOriginal",
					false);
			if (otherDate == null)
			{
				otherDate = XMPNodeUtils.findChildNode(exifSchema, "exif:DateTimeDigitized", false);
			}
	
			binOtherDate = XMPUtils.convertToDate(otherDate.getValue());
			Calendar cal = binGPSStamp.getCalendar();
			cal.set(Calendar.YEAR, binOtherDate.getYear());
			cal.set(Calendar.MONTH, binOtherDate.getMonth());
			cal.set(Calendar.DAY_OF_MONTH, binOtherDate.getDay());
			binGPSStamp = new XMPDateTimeImpl(cal);
			gpsDateTime.setValue(XMPUtils.convertFromDate (binGPSStamp));
		}
		catch (XMPException e)
		{
			// Don't let a missing or bad date stop other things.
			return;
		}
	}



	/**
	 * Remove all empty schemas from the metadata tree that were generated during the rdf parsing.
	 * @param tree the root of the metadata tree
	 */
	private static void deleteEmptySchemas(XMPNode tree)
	{
		// Delete empty schema nodes. Do this last, other cleanup can make empty
		// schema.
	
		for (Iterator it = tree.iterateChildren(); it.hasNext();)
		{
			XMPNode schema = (XMPNode) it.next();
			if (!schema.hasChildren())
			{
				it.remove();
			}
		}
	}

	
	/**
	 * The outermost call is special. The names almost certainly differ. The
	 * qualifiers (and hence options) will differ for an alias to the x-default
	 * item of a langAlt array.
	 * 
	 * @param aliasNode the alias node
	 * @param baseNode the base node of the alias
	 * @param outerCall marks the outer call of the recursion
	 * @throws XMPException Forwards XMP errors 
	 */
	private static void compareAliasedSubtrees(XMPNode aliasNode, XMPNode baseNode,
			boolean outerCall) throws XMPException 
	{
		if (!aliasNode.getValue().equals(baseNode.getValue())  || 
			aliasNode.getChildrenLength() != baseNode.getChildrenLength())
		{
			throw new XMPException("Mismatch between alias and base nodes", XMPError.BADXMP);
		}
		
		if (
				!outerCall  &&
				(!aliasNode.getName().equals(baseNode.getName())  ||
				 !aliasNode.getOptions().equals(baseNode.getOptions())  ||
				 aliasNode.getQualifierLength() != baseNode.getQualifierLength())
		   ) 
	    {
			throw new XMPException("Mismatch between alias and base nodes", 
				XMPError.BADXMP);
		}
		
		for (Iterator an = aliasNode.iterateChildren(), 
					  bn = baseNode.iterateChildren();
			 an.hasNext() && bn.hasNext();)
		{
			XMPNode aliasChild = (XMPNode) an.next();
			XMPNode baseChild =  (XMPNode) bn.next();
			compareAliasedSubtrees (aliasChild, baseChild, false);
		}
	
		
		for (Iterator an = aliasNode.iterateQualifier(), 
					  bn = baseNode.iterateQualifier();
			 an.hasNext() && bn.hasNext();)
		{
			XMPNode aliasQual = (XMPNode) an.next();
			XMPNode baseQual =  (XMPNode) bn.next();
			compareAliasedSubtrees (aliasQual, baseQual, false);
		}
	}

	
	/**
	 * The initial support for WAV files mapped a legacy ID3 audio copyright
	 * into a new xmpDM:copyright property. This is special case code to migrate
	 * that into dc:rights['x-default']. The rules:
	 * 
	 * <pre>
	 * 1. If there is no dc:rights array, or an empty array -
	 *    Create one with dc:rights['x-default'] set from double linefeed and xmpDM:copyright.
	 * 
	 * 2. If there is a dc:rights array but it has no x-default item -
	 *    Create an x-default item as a copy of the first item then apply rule #3.
	 * 
	 * 3. If there is a dc:rights array with an x-default item, 
	 *    Look for a double linefeed in the value.
	 *     A. If no double linefeed, compare the x-default value to the xmpDM:copyright value.
	 *         A1. If they match then leave the x-default value alone.
	 *         A2. Otherwise, append a double linefeed and 
	 *             the xmpDM:copyright value to the x-default value.
	 *     B. If there is a double linefeed, compare the trailing text to the xmpDM:copyright value.
	 *         B1. If they match then leave the x-default value alone.
	 *         B2. Otherwise, replace the trailing x-default text with the xmpDM:copyright value.
	 * 
	 * 4. In all cases, delete the xmpDM:copyright property.
	 * </pre>
	 * 
	 * @param xmp the metadata object 
	 * @param dmCopyright the "dm:copyright"-property
	 */
	private static void	migrateAudioCopyright (XMPMeta xmp, XMPNode dmCopyright)
	{
		try 
		{
			XMPNode dcSchema = XMPNodeUtils.findSchemaNode(
				((XMPMetaImpl) xmp).getRoot(), XMPConst.NS_DC, true);
			
			String dmValue = dmCopyright.getValue();
			String doubleLF = "\n\n";
			
			XMPNode dcRightsArray = XMPNodeUtils.findChildNode (dcSchema, "dc:rights", false);
			
			if (dcRightsArray == null  ||  !dcRightsArray.hasChildren()) 
			{
				// 1. No dc:rights array, create from double linefeed and xmpDM:copyright.
				dmValue = doubleLF + dmValue;
				xmp.setLocalizedText(XMPConst.NS_DC, "rights", "", XMPConst.X_DEFAULT, dmValue,
						null);
			}
			else
			{
				int xdIndex = XMPNodeUtils.lookupLanguageItem(dcRightsArray, XMPConst.X_DEFAULT);
				
				if (xdIndex < 0)
				{
					// 2. No x-default item, create from the first item.
					String firstValue = dcRightsArray.getChild(1).getValue();
					xmp.setLocalizedText (XMPConst.NS_DC, "rights", "", XMPConst.X_DEFAULT, 
						firstValue, null);
					xdIndex = XMPNodeUtils.lookupLanguageItem(dcRightsArray, XMPConst.X_DEFAULT);
				}
							
				// 3. Look for a double linefeed in the x-default value.
				XMPNode defaultNode = dcRightsArray.getChild(xdIndex);
				String defaultValue = defaultNode.getValue();
				int lfPos = defaultValue.indexOf(doubleLF);
				
				if (lfPos < 0)
				{
					// 3A. No double LF, compare whole values.
					if (!dmValue.equals(defaultValue))
					{
						// 3A2. Append the xmpDM:copyright to the x-default
						// item.
						defaultNode.setValue(defaultValue + doubleLF + dmValue);
					}
				}
				else
				{
					// 3B. Has double LF, compare the tail.
					if (!defaultValue.substring(lfPos + 2).equals(dmValue))
					{
						// 3B2. Replace the x-default tail.
						defaultNode.setValue(defaultValue.substring(0, lfPos + 2) + dmValue);
					}
				}

			}
			
			// 4. Get rid of the xmpDM:copyright.
			dmCopyright.getParent().removeChild(dmCopyright);
		}
		catch (XMPException e)
		{
			// Don't let failures (like a bad dc:rights form) stop other
			// cleanup.
		}
	}
	
	
	/**
	 * Initializes the map that contains the known arrays, that are fixed by 
	 * {@link XMPNormalizer#normalizeDCArrays(XMPNode)}. 
	 */
	private static void initDCArrays()
	{
		dcArrayForms = new HashMap(); 
		
		// Properties supposed to be a "Bag".
		PropertyOptions bagForm = new PropertyOptions();
		bagForm.setArray(true);
		dcArrayForms.put("dc:contributor", bagForm);
		dcArrayForms.put("dc:language", bagForm);
		dcArrayForms.put("dc:publisher", bagForm);
		dcArrayForms.put("dc:relation", bagForm);
		dcArrayForms.put("dc:subject", bagForm);
		dcArrayForms.put("dc:type", bagForm);

		// Properties supposed to be a "Seq".
		PropertyOptions seqForm = new PropertyOptions();
		seqForm.setArray(true);
		seqForm.setArrayOrdered(true);
		dcArrayForms.put("dc:creator", seqForm);
		dcArrayForms.put("dc:date", seqForm);
		
		// Properties supposed to be an "Alt" in alternative-text form.
		PropertyOptions altTextForm = new PropertyOptions();
		altTextForm.setArray(true);
		altTextForm.setArrayOrdered(true);
		altTextForm.setArrayAlternate(true);
		altTextForm.setArrayAltText(true);
		dcArrayForms.put("dc:description", altTextForm);
		dcArrayForms.put("dc:rights", altTextForm);
		dcArrayForms.put("dc:title", altTextForm);
	}
}

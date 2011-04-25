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
import java.util.Iterator;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPError;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPPathFactory;
import com.adobe.xmp.XMPUtils;
import com.adobe.xmp.impl.xpath.XMPPath;
import com.adobe.xmp.impl.xpath.XMPPathParser;
import com.adobe.xmp.options.IteratorOptions;
import com.adobe.xmp.options.ParseOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPProperty;


/**
 * Implementation for {@link XMPMeta}.
 * 
 * @since 17.02.2006
 */
public class XMPMetaImpl implements XMPMeta, XMPConst
{
	/** Property values are Strings by default */
	private static final int VALUE_STRING = 0;
	/** */
	private static final int VALUE_BOOLEAN = 1;
	/** */
	private static final int VALUE_INTEGER = 2;
	/** */
	private static final int VALUE_LONG = 3;
	/** */
	private static final int VALUE_DOUBLE = 4;
	/** */
	private static final int VALUE_DATE = 5;
	/** */
	private static final int VALUE_CALENDAR = 6;
	/** */
	private static final int VALUE_BASE64 = 7;

	/** root of the metadata tree */
	private XMPNode tree;
	/** the xpacket processing instructions content */ 
	private String packetHeader = null;
	

	/**
	 * Constructor for an empty metadata object.
	 */
	public XMPMetaImpl()
	{
		// create root node
		tree = new XMPNode(null, null, null);
	}


	/**
	 * Constructor for a cloned metadata tree.
	 * 
	 * @param tree
	 *            an prefilled metadata tree which fulfills all
	 *            <code>XMPNode</code> contracts.
	 */
	public XMPMetaImpl(XMPNode tree)
	{
		this.tree = tree;
	}


	/**
	 * @see XMPMeta#appendArrayItem(String, String, PropertyOptions, String,
	 *      PropertyOptions)
	 */
	public void appendArrayItem(String schemaNS, String arrayName, PropertyOptions arrayOptions,
			String itemValue, PropertyOptions itemOptions) throws XMPException
	{
		ParameterAsserts.assertSchemaNS(schemaNS);
		ParameterAsserts.assertArrayName(arrayName);

		if (arrayOptions == null)
		{
			arrayOptions = new PropertyOptions();
		}
		if (!arrayOptions.isOnlyArrayOptions())
		{
			throw new XMPException("Only array form flags allowed for arrayOptions",
					XMPError.BADOPTIONS);
		}

		// Check if array options are set correctly.
		arrayOptions = XMPNodeUtils.verifySetOptions(arrayOptions, null);


		// Locate or create the array. If it already exists, make sure the array
		// form from the options
		// parameter is compatible with the current state.
		XMPPath arrayPath = XMPPathParser.expandXPath(schemaNS, arrayName);


		// Just lookup, don't try to create.
		XMPNode arrayNode = XMPNodeUtils.findNode(tree, arrayPath, false, null);

		if (arrayNode != null)
		{
			// The array exists, make sure the form is compatible. Zero
			// arrayForm means take what exists.
			if (!arrayNode.getOptions().isArray())
			{
				throw new XMPException("The named property is not an array", XMPError.BADXPATH);
			}
			// if (arrayOptions != null && !arrayOptions.equalArrayTypes(arrayNode.getOptions()))
			// {
			// throw new XMPException("Mismatch of existing and specified array form", BADOPTIONS);
			// }
		}
		else
		{
			// The array does not exist, try to create it.
			if (arrayOptions.isArray())
			{
				arrayNode = XMPNodeUtils.findNode(tree, arrayPath, true, arrayOptions);
				if (arrayNode == null)
				{
					throw new XMPException("Failure creating array node", XMPError.BADXPATH);
				}
			}
			else
			{
				// array options missing
				throw new XMPException("Explicit arrayOptions required to create new array",
						XMPError.BADOPTIONS);
			}
		}

		doSetArrayItem(arrayNode, ARRAY_LAST_ITEM, itemValue, itemOptions, true);
	}

	
	/**
	 * @see XMPMeta#appendArrayItem(String, String, String)
	 */
	public void appendArrayItem(String schemaNS, String arrayName, String itemValue)
			throws XMPException
	{
		appendArrayItem(schemaNS, arrayName, null, itemValue, null);
	}


	/**
	 * @throws XMPException
	 * @see XMPMeta#countArrayItems(String, String)
	 */
	public int countArrayItems(String schemaNS, String arrayName) throws XMPException
	{
		ParameterAsserts.assertSchemaNS(schemaNS);
		ParameterAsserts.assertArrayName(arrayName);

		XMPPath arrayPath = XMPPathParser.expandXPath(schemaNS, arrayName);
		XMPNode arrayNode = XMPNodeUtils.findNode(tree, arrayPath, false, null);

		if (arrayNode == null)
		{
			return 0;
		}

		if (arrayNode.getOptions().isArray())
		{
			return arrayNode.getChildrenLength();
		}
		else
		{
			throw new XMPException("The named property is not an array", XMPError.BADXPATH);
		}
	}


	/**
	 * @see XMPMeta#deleteArrayItem(String, String, int)
	 */
	public void deleteArrayItem(String schemaNS, String arrayName, int itemIndex)
	{
		try
		{
			ParameterAsserts.assertSchemaNS(schemaNS);
			ParameterAsserts.assertArrayName(arrayName);

			String itemPath = XMPPathFactory.composeArrayItemPath(arrayName, itemIndex);
			deleteProperty(schemaNS, itemPath);
		}
		catch (XMPException e)
		{
			// EMPTY, exceptions are ignored within delete
		}
	}


	/**
	 * @see XMPMeta#deleteProperty(String, String)
	 */
	public void deleteProperty(String schemaNS, String propName)
	{
		try
		{
			ParameterAsserts.assertSchemaNS(schemaNS);
			ParameterAsserts.assertPropName(propName);

			XMPPath expPath = XMPPathParser.expandXPath(schemaNS, propName);

			XMPNode propNode = XMPNodeUtils.findNode(tree, expPath, false, null);
			if (propNode != null)
			{
				XMPNodeUtils.deleteNode(propNode);
			}
		}
		catch (XMPException e)
		{
			// EMPTY, exceptions are ignored within delete
		}
	}


	/**
	 * @see XMPMeta#deleteQualifier(String, String, String, String)
	 */
	public void deleteQualifier(String schemaNS, String propName, String qualNS, String qualName)
	{
		try
		{
			// Note: qualNS and qualName are checked inside composeQualfierPath
			ParameterAsserts.assertSchemaNS(schemaNS);
			ParameterAsserts.assertPropName(propName);

			String qualPath = propName + XMPPathFactory.composeQualifierPath(qualNS, qualName);
			deleteProperty(schemaNS, qualPath);
		}
		catch (XMPException e)
		{
			// EMPTY, exceptions within delete are ignored
		}
	}


	/**
	 * @see XMPMeta#deleteStructField(String, String, String, String)
	 */
	public void deleteStructField(String schemaNS, String structName, String fieldNS,
			String fieldName)
	{
		try
		{
			// fieldNS and fieldName are checked inside composeStructFieldPath
			ParameterAsserts.assertSchemaNS(schemaNS);
			ParameterAsserts.assertStructName(structName);

			String fieldPath = structName
					+ XMPPathFactory.composeStructFieldPath(fieldNS, fieldName);
			deleteProperty(schemaNS, fieldPath);
		}
		catch (XMPException e)
		{
			// EMPTY, exceptions within delete are ignored
		}
	}


	/**
	 * @see XMPMeta#doesPropertyExist(String, String)
	 */
	public boolean doesPropertyExist(String schemaNS, String propName)
	{
		try
		{
			ParameterAsserts.assertSchemaNS(schemaNS);
			ParameterAsserts.assertPropName(propName);

			XMPPath expPath = XMPPathParser.expandXPath(schemaNS, propName);
			final XMPNode propNode = XMPNodeUtils.findNode(tree, expPath, false, null);
			return propNode != null;
		}
		catch (XMPException e)
		{
			return false;
		}
	}


	/**
	 * @see XMPMeta#doesArrayItemExist(String, String, int)
	 */
	public boolean doesArrayItemExist(String schemaNS, String arrayName, int itemIndex)
	{
		try
		{
			ParameterAsserts.assertSchemaNS(schemaNS);
			ParameterAsserts.assertArrayName(arrayName);

			String path = XMPPathFactory.composeArrayItemPath(arrayName, itemIndex);
			return doesPropertyExist(schemaNS, path);
		}
		catch (XMPException e)
		{
			return false;
		}
	}


	/**
	 * @see XMPMeta#doesStructFieldExist(String, String, String, String)
	 */
	public boolean doesStructFieldExist(String schemaNS, String structName, String fieldNS,
			String fieldName)
	{
		try
		{
			// fieldNS and fieldName are checked inside composeStructFieldPath()
			ParameterAsserts.assertSchemaNS(schemaNS);
			ParameterAsserts.assertStructName(structName);

			String path = XMPPathFactory.composeStructFieldPath(fieldNS, fieldName);
			return doesPropertyExist(schemaNS, structName + path);
		}
		catch (XMPException e)
		{
			return false;
		}
	}


	/**
	 * @see XMPMeta#doesQualifierExist(String, String, String, String)
	 */
	public boolean doesQualifierExist(String schemaNS, String propName, String qualNS,
			String qualName)
	{
		try
		{
			// qualNS and qualName are checked inside composeQualifierPath()
			ParameterAsserts.assertSchemaNS(schemaNS);
			ParameterAsserts.assertPropName(propName);

			String path = XMPPathFactory.composeQualifierPath(qualNS, qualName);
			return doesPropertyExist(schemaNS, propName + path);
		}
		catch (XMPException e)
		{
			return false;
		}
	}


	/**
	 * @see XMPMeta#getArrayItem(String, String, int)
	 */
	public XMPProperty getArrayItem(String schemaNS, String arrayName, int itemIndex)
			throws XMPException
	{
		ParameterAsserts.assertSchemaNS(schemaNS);
		ParameterAsserts.assertArrayName(arrayName);

		String itemPath = XMPPathFactory.composeArrayItemPath(arrayName, itemIndex);
		return getProperty(schemaNS, itemPath);
	}


	/**
	 * @throws XMPException
	 * @see XMPMeta#getLocalizedText(String, String, String, String)
	 */
	public XMPProperty getLocalizedText(String schemaNS, String altTextName, String genericLang,
			String specificLang) throws XMPException
	{
		ParameterAsserts.assertSchemaNS(schemaNS);
		ParameterAsserts.assertArrayName(altTextName);
		ParameterAsserts.assertSpecificLang(specificLang);

		genericLang = genericLang != null ? Utils.normalizeLangValue(genericLang) : null;
		specificLang = Utils.normalizeLangValue(specificLang);

		XMPPath arrayPath = XMPPathParser.expandXPath(schemaNS, altTextName);
		XMPNode arrayNode = XMPNodeUtils.findNode(tree, arrayPath, false, null);
		if (arrayNode == null)
		{
			return null;
		}

		Object[] result = XMPNodeUtils.chooseLocalizedText(arrayNode, genericLang, specificLang);
		int match = ((Integer) result[0]).intValue();
		final XMPNode itemNode = (XMPNode) result[1];

		if (match != XMPNodeUtils.CLT_NO_VALUES)
		{
			return new XMPProperty()
			{
				public Object getValue()
				{
					return itemNode.getValue();
				}


				public PropertyOptions getOptions()
				{
					return itemNode.getOptions();
				}


				public String getLanguage()
				{
					return itemNode.getQualifier(1).getValue();
				}


				public String toString()
				{
					return itemNode.getValue().toString();
				}
			};
		}
		else
		{
			return null;
		}
	}


	/**
	 * @see XMPMeta#setLocalizedText(String, String, String, String, String,
	 *      PropertyOptions)
	 */
	public void setLocalizedText(String schemaNS, String altTextName, String genericLang,
			String specificLang, String itemValue, PropertyOptions options) throws XMPException
	{
		ParameterAsserts.assertSchemaNS(schemaNS);
		ParameterAsserts.assertArrayName(altTextName);
		ParameterAsserts.assertSpecificLang(specificLang);

		genericLang = genericLang != null ? Utils.normalizeLangValue(genericLang) : null;
		specificLang = Utils.normalizeLangValue(specificLang);

		XMPPath arrayPath = XMPPathParser.expandXPath(schemaNS, altTextName);

		// Find the array node and set the options if it was just created.
		XMPNode arrayNode = XMPNodeUtils.findNode(tree, arrayPath, true, new PropertyOptions(
				PropertyOptions.ARRAY | PropertyOptions.ARRAY_ORDERED
						| PropertyOptions.ARRAY_ALTERNATE | PropertyOptions.ARRAY_ALT_TEXT));

		if (arrayNode == null)
		{
			throw new XMPException("Failed to find or create array node", XMPError.BADXPATH);
		}
		else if (!arrayNode.getOptions().isArrayAltText())
		{
			if (!arrayNode.hasChildren() && arrayNode.getOptions().isArrayAlternate())
			{
				arrayNode.getOptions().setArrayAltText(true);
			}
			else
			{
				throw new XMPException(
					"Specified property is no alt-text array", XMPError.BADXPATH);
			}
		}

		// Make sure the x-default item, if any, is first.
		boolean haveXDefault = false;
		XMPNode xdItem = null;

		for (Iterator it = arrayNode.iterateChildren(); it.hasNext();)
		{
			XMPNode currItem = (XMPNode) it.next();
			if (!currItem.hasQualifier()
					|| !XMPConst.XML_LANG.equals(currItem.getQualifier(1).getName()))
			{
				throw new XMPException("Language qualifier must be first", XMPError.BADXPATH);
			}
			else if (XMPConst.X_DEFAULT.equals(currItem.getQualifier(1).getValue()))
			{
				xdItem = currItem;
				haveXDefault = true;
				break;
			}
		}

		// Moves x-default to the beginning of the array
		if (xdItem != null  &&  arrayNode.getChildrenLength() > 1)
		{
			arrayNode.removeChild(xdItem);
			arrayNode.addChild(1, xdItem);
		}

		// Find the appropriate item.
		// chooseLocalizedText will make sure the array is a language
		// alternative.
		Object[] result = XMPNodeUtils.chooseLocalizedText(arrayNode, genericLang, specificLang);
		int match = ((Integer) result[0]).intValue();
		XMPNode itemNode = (XMPNode) result[1];

		boolean specificXDefault = XMPConst.X_DEFAULT.equals(specificLang);

		switch (match)
		{
		case XMPNodeUtils.CLT_NO_VALUES:

			// Create the array items for the specificLang and x-default, with
			// x-default first.
			XMPNodeUtils.appendLangItem(arrayNode, XMPConst.X_DEFAULT, itemValue);
			haveXDefault = true;
			if (!specificXDefault)
			{
				XMPNodeUtils.appendLangItem(arrayNode, specificLang, itemValue);
			}
			break;

		case XMPNodeUtils.CLT_SPECIFIC_MATCH:

			if (!specificXDefault)
			{
				// Update the specific item, update x-default if it matches the
				// old value.
				if (haveXDefault && xdItem != itemNode && xdItem != null
						&& xdItem.getValue().equals(itemNode.getValue()))
				{
					xdItem.setValue(itemValue);
				}
				// ! Do this after the x-default check!
				itemNode.setValue(itemValue);
			}
			else
			{
				// Update all items whose values match the old x-default value.
				assert  haveXDefault  &&  xdItem == itemNode;
				for (Iterator it = arrayNode.iterateChildren(); it.hasNext();)
				{
					XMPNode currItem = (XMPNode) it.next();
					if (currItem == xdItem
							|| !currItem.getValue().equals(
									xdItem != null ? xdItem.getValue() : null))
					{
						continue;
					}
					currItem.setValue(itemValue);
				}
				// And finally do the x-default item.
				if (xdItem != null)
				{	
					xdItem.setValue(itemValue);
				}	
			}
			break;

		case XMPNodeUtils.CLT_SINGLE_GENERIC:

			// Update the generic item, update x-default if it matches the old
			// value.
			if (haveXDefault && xdItem != itemNode && xdItem != null
					&& xdItem.getValue().equals(itemNode.getValue()))
			{
				xdItem.setValue(itemValue);
			}
			itemNode.setValue(itemValue); // ! Do this after
			// the x-default
			// check!
			break;

		case XMPNodeUtils.CLT_MULTIPLE_GENERIC:

			// Create the specific language, ignore x-default.
			XMPNodeUtils.appendLangItem(arrayNode, specificLang, itemValue);
			if (specificXDefault)
			{
				haveXDefault = true;
			}
			break;

		case XMPNodeUtils.CLT_XDEFAULT:

			// Create the specific language, update x-default if it was the only
			// item.
			if (xdItem != null  &&  arrayNode.getChildrenLength() == 1)
			{
				xdItem.setValue(itemValue);
			}
			XMPNodeUtils.appendLangItem(arrayNode, specificLang, itemValue);
			break;

		case XMPNodeUtils.CLT_FIRST_ITEM:

			// Create the specific language, don't add an x-default item.
			XMPNodeUtils.appendLangItem(arrayNode, specificLang, itemValue);
			if (specificXDefault)
			{
				haveXDefault = true;
			}
			break;

		default:
			// does not happen under normal circumstances
			throw new XMPException("Unexpected result from ChooseLocalizedText",
					XMPError.INTERNALFAILURE);

		}

		// Add an x-default at the front if needed.
		if (!haveXDefault && arrayNode.getChildrenLength() == 1)
		{
			XMPNodeUtils.appendLangItem(arrayNode, XMPConst.X_DEFAULT, itemValue);
		}
	}

	
	/**
	 * @see XMPMeta#setLocalizedText(String, String, String, String, String)
	 */
	public void setLocalizedText(String schemaNS, String altTextName, String genericLang,
			String specificLang, String itemValue) throws XMPException
	{
		setLocalizedText(schemaNS, altTextName, genericLang, specificLang, itemValue, null);
	}
	

	/**
	 * @throws XMPException
	 * @see XMPMeta#getProperty(String, String)
	 */
	public XMPProperty getProperty(String schemaNS, String propName) throws XMPException
	{
		return getProperty(schemaNS, propName, VALUE_STRING);
	}


	/**
	 * Returns a property, but the result value can be requested. It can be one
	 * of {@link XMPMetaImpl#VALUE_STRING}, {@link XMPMetaImpl#VALUE_BOOLEAN},
	 * {@link XMPMetaImpl#VALUE_INTEGER}, {@link XMPMetaImpl#VALUE_LONG},
	 * {@link XMPMetaImpl#VALUE_DOUBLE}, {@link XMPMetaImpl#VALUE_DATE},
	 * {@link XMPMetaImpl#VALUE_CALENDAR}, {@link XMPMetaImpl#VALUE_BASE64}.
	 * 
	 * @see XMPMeta#getProperty(String, String)
	 * @param schemaNS
	 *            a schema namespace
	 * @param propName
	 *            a property name or path
	 * @param valueType
	 *            the type of the value, see VALUE_...
	 * @return Returns an <code>XMPProperty</code>
	 * @throws XMPException
	 *             Collects any exception that occurs.
	 */
	protected XMPProperty getProperty(String schemaNS, String propName, int valueType)
			throws XMPException
	{
		ParameterAsserts.assertSchemaNS(schemaNS);
		ParameterAsserts.assertPropName(propName);

		final XMPPath expPath = XMPPathParser.expandXPath(schemaNS, propName);
		final XMPNode propNode = XMPNodeUtils.findNode(tree, expPath, false, null);

		if (propNode != null)
		{
			if (valueType != VALUE_STRING && propNode.getOptions().isCompositeProperty())
			{
				throw new XMPException("Property must be simple when a value type is requested",
						XMPError.BADXPATH);
			}

			final Object value = evaluateNodeValue(valueType, propNode);

			return new XMPProperty()
			{
				public Object getValue()
				{
					return value;
				}


				public PropertyOptions getOptions()
				{
					return propNode.getOptions();
				}


				public String getLanguage()
				{
					return null;
				}


				public String toString()
				{
					return value.toString();
				}
			};
		}
		else
		{
			return null;
		}
	}


	/**
	 * Returns a property, but the result value can be requested.
	 * 
	 * @see XMPMeta#getProperty(String, String)
	 * @param schemaNS
	 *            a schema namespace
	 * @param propName
	 *            a property name or path
	 * @param valueType
	 *            the type of the value, see VALUE_...
	 * @return Returns the node value as an object according to the
	 *         <code>valueType</code>.
	 * @throws XMPException
	 *             Collects any exception that occurs.
	 */
	protected Object getPropertyObject(String schemaNS, String propName, int valueType)
			throws XMPException
	{
		ParameterAsserts.assertSchemaNS(schemaNS);
		ParameterAsserts.assertPropName(propName);

		final XMPPath expPath = XMPPathParser.expandXPath(schemaNS, propName);
		final XMPNode propNode = XMPNodeUtils.findNode(tree, expPath, false, null);

		if (propNode != null)
		{
			if (valueType != VALUE_STRING && propNode.getOptions().isCompositeProperty())
			{
				throw new XMPException("Property must be simple when a value type is requested",
						XMPError.BADXPATH);
			}

			return evaluateNodeValue(valueType, propNode);
		}
		else
		{
			return null;
		}
	}


	/**
	 * @see XMPMeta#getPropertyBoolean(String, String)
	 */
	public Boolean getPropertyBoolean(String schemaNS, String propName) throws XMPException
	{
		return (Boolean) getPropertyObject(schemaNS, propName, VALUE_BOOLEAN);
	}


	/**
	 * @throws XMPException
	 * @see XMPMeta#setPropertyBoolean(String, String, boolean, PropertyOptions)
	 */
	public void setPropertyBoolean(String schemaNS, String propName, boolean propValue,
			PropertyOptions options) throws XMPException
	{
		setProperty(schemaNS, propName, propValue ? TRUESTR : FALSESTR, options);
	}

	
	/**
	 * @see XMPMeta#setPropertyBoolean(String, String, boolean)
	 */
	public void setPropertyBoolean(String schemaNS, String propName, boolean propValue)
			throws XMPException
	{
		setProperty(schemaNS, propName, propValue ? TRUESTR : FALSESTR, null);
	}
	

	/**
	 * @see XMPMeta#getPropertyInteger(String, String)
	 */
	public Integer getPropertyInteger(String schemaNS, String propName) throws XMPException
	{
		return (Integer) getPropertyObject(schemaNS, propName, VALUE_INTEGER);
	}


	/**
	 * @see XMPMeta#setPropertyInteger(String, String, int, PropertyOptions)
	 */
	public void setPropertyInteger(String schemaNS, String propName, int propValue,
			PropertyOptions options) throws XMPException
	{
		setProperty(schemaNS, propName, new Integer(propValue), options);
	}

	
	/**
	 * @see XMPMeta#setPropertyInteger(String, String, int)
	 */
	public void setPropertyInteger(String schemaNS, String propName, int propValue)
			throws XMPException
	{
		setProperty(schemaNS, propName, new Integer(propValue), null);
	}
	

	/**
	 * @see XMPMeta#getPropertyLong(String, String)
	 */
	public Long getPropertyLong(String schemaNS, String propName) throws XMPException
	{
		return (Long) getPropertyObject(schemaNS, propName, VALUE_LONG);
	}


	/**
	 * @see XMPMeta#setPropertyLong(String, String, long, PropertyOptions)
	 */
	public void setPropertyLong(String schemaNS, String propName, long propValue,
			PropertyOptions options) throws XMPException
	{
		setProperty(schemaNS, propName, new Long(propValue), options);
	}


	/**
	 * @see XMPMeta#setPropertyLong(String, String, long)
	 */
	public void setPropertyLong(String schemaNS, String propName, long propValue)
			throws XMPException
	{
		setProperty(schemaNS, propName, new Long(propValue), null);
	}
	

	/**
	 * @see XMPMeta#getPropertyDouble(String, String)
	 */
	public Double getPropertyDouble(String schemaNS, String propName) throws XMPException
	{
		return (Double) getPropertyObject(schemaNS, propName, VALUE_DOUBLE);
	}


	/**
	 * @see XMPMeta#setPropertyDouble(String, String, double, PropertyOptions)
	 */
	public void setPropertyDouble(String schemaNS, String propName, double propValue,
			PropertyOptions options) throws XMPException
	{
		setProperty(schemaNS, propName, new Double(propValue), options);
	}

	
	/**
	 * @see XMPMeta#setPropertyDouble(String, String, double)
	 */
	public void setPropertyDouble(String schemaNS, String propName, double propValue)
			throws XMPException
	{
		setProperty(schemaNS, propName, new Double(propValue), null);
	}
	

	/**
	 * @see XMPMeta#getPropertyDate(String, String)
	 */
	public XMPDateTime getPropertyDate(String schemaNS, String propName) throws XMPException
	{
		return (XMPDateTime) getPropertyObject(schemaNS, propName, VALUE_DATE);
	}


	/**
	 * @see XMPMeta#setPropertyDate(String, String, XMPDateTime,
	 *      PropertyOptions)
	 */
	public void setPropertyDate(String schemaNS, String propName, XMPDateTime propValue,
			PropertyOptions options) throws XMPException
	{
		setProperty(schemaNS, propName, propValue, options);
	}

	
	/**
	 * @see XMPMeta#setPropertyDate(String, String, XMPDateTime)
	 */
	public void setPropertyDate(String schemaNS, String propName, XMPDateTime propValue)
			throws XMPException
	{
		setProperty(schemaNS, propName, propValue, null);
	}
	

	/**
	 * @see XMPMeta#getPropertyCalendar(String, String)
	 */
	public Calendar getPropertyCalendar(String schemaNS, String propName) throws XMPException
	{
		return (Calendar) getPropertyObject(schemaNS, propName, VALUE_CALENDAR);
	}


	/**
	 * @see XMPMeta#setPropertyCalendar(String, String, Calendar,
	 *      PropertyOptions)
	 */
	public void setPropertyCalendar(String schemaNS, String propName, Calendar propValue,
			PropertyOptions options) throws XMPException
	{
		setProperty(schemaNS, propName, propValue, options);
	}

	
	/**
	 * @see XMPMeta#setPropertyCalendar(String, String, Calendar)
	 */
	public void setPropertyCalendar(String schemaNS, String propName, Calendar propValue)
			throws XMPException
	{
		setProperty(schemaNS, propName, propValue, null);
	}
	

	/**
	 * @see XMPMeta#getPropertyBase64(String, String)
	 */
	public byte[] getPropertyBase64(String schemaNS, String propName) throws XMPException
	{
		return (byte[]) getPropertyObject(schemaNS, propName, VALUE_BASE64);
	}


	/**
	 * @see XMPMeta#getPropertyString(String, String)
	 */
	public String getPropertyString(String schemaNS, String propName) throws XMPException
	{
		return (String) getPropertyObject(schemaNS, propName, VALUE_STRING);
	}


	/**
	 * @see XMPMeta#setPropertyBase64(String, String, byte[], PropertyOptions)
	 */
	public void setPropertyBase64(String schemaNS, String propName, byte[] propValue,
			PropertyOptions options) throws XMPException
	{
		setProperty(schemaNS, propName, propValue, options);
	}

	
	/**
	 * @see XMPMeta#setPropertyBase64(String, String, byte[])
	 */
	public void setPropertyBase64(String schemaNS, String propName, byte[] propValue)
			throws XMPException
	{
		setProperty(schemaNS, propName, propValue, null);
	}
	

	/**
	 * @throws XMPException
	 * @see XMPMeta#getQualifier(String, String, String, String)
	 */
	public XMPProperty getQualifier(String schemaNS, String propName, String qualNS, 
		String qualName) throws XMPException
	{
		// qualNS and qualName are checked inside composeQualfierPath
		ParameterAsserts.assertSchemaNS(schemaNS);
		ParameterAsserts.assertPropName(propName);

		String qualPath = propName + XMPPathFactory.composeQualifierPath(qualNS, qualName);
		return getProperty(schemaNS, qualPath);
	}


	/**
	 * @see XMPMeta#getStructField(String, String, String, String)
	 */
	public XMPProperty getStructField(String schemaNS, String structName, String fieldNS,
			String fieldName) throws XMPException
	{
		// fieldNS and fieldName are checked inside composeStructFieldPath
		ParameterAsserts.assertSchemaNS(schemaNS);
		ParameterAsserts.assertStructName(structName);

		String fieldPath = structName + XMPPathFactory.composeStructFieldPath(fieldNS, fieldName);
		return getProperty(schemaNS, fieldPath);
	}


	/**
	 * @throws XMPException
	 * @see XMPMeta#iterator()
	 */
	public XMPIterator iterator() throws XMPException
	{
		return iterator(null, null, null);
	}


	/**
	 * @see XMPMeta#iterator(IteratorOptions)
	 */
	public XMPIterator iterator(IteratorOptions options) throws XMPException
	{
		return iterator(null, null, options);
	}


	/**
	 * @see XMPMeta#iterator(String, String, IteratorOptions)
	 */
	public XMPIterator iterator(String schemaNS, String propName, IteratorOptions options)
			throws XMPException
	{
		return new XMPIteratorImpl(this, schemaNS, propName, options);
	}


	/**
	 * @throws XMPException
	 * @see XMPMeta#setArrayItem(String, String, int, String, PropertyOptions)
	 */
	public void setArrayItem(String schemaNS, String arrayName, int itemIndex, String itemValue,
			PropertyOptions options) throws XMPException
	{
		ParameterAsserts.assertSchemaNS(schemaNS);
		ParameterAsserts.assertArrayName(arrayName);

		// Just lookup, don't try to create.
		XMPPath arrayPath = XMPPathParser.expandXPath(schemaNS, arrayName);
		XMPNode arrayNode = XMPNodeUtils.findNode(tree, arrayPath, false, null);

		if (arrayNode != null)
		{
			doSetArrayItem(arrayNode, itemIndex, itemValue, options, false);
		}
		else
		{
			throw new XMPException("Specified array does not exist", XMPError.BADXPATH);
		}
	}

	
	/**
	 * @see XMPMeta#setArrayItem(String, String, int, String)
	 */
	public void setArrayItem(String schemaNS, String arrayName, int itemIndex, String itemValue)
			throws XMPException
	{
		setArrayItem(schemaNS, arrayName, itemIndex, itemValue, null);
	}
	

	/**
	 * @throws XMPException
	 * @see XMPMeta#insertArrayItem(String, String, int, String,
	 *      PropertyOptions)
	 */
	public void insertArrayItem(String schemaNS, String arrayName, int itemIndex, String itemValue,
			PropertyOptions options) throws XMPException
	{
		ParameterAsserts.assertSchemaNS(schemaNS);
		ParameterAsserts.assertArrayName(arrayName);

		// Just lookup, don't try to create.
		XMPPath arrayPath = XMPPathParser.expandXPath(schemaNS, arrayName);
		XMPNode arrayNode = XMPNodeUtils.findNode(tree, arrayPath, false, null);

		if (arrayNode != null)
		{
			doSetArrayItem(arrayNode, itemIndex, itemValue, options, true);
		}
		else
		{
			throw new XMPException("Specified array does not exist", XMPError.BADXPATH);
		}
	}

	
	/**
	 * @see XMPMeta#insertArrayItem(String, String, int, String)
	 */
	public void insertArrayItem(String schemaNS, String arrayName, int itemIndex, String itemValue)
			throws XMPException
	{
		insertArrayItem(schemaNS, arrayName, itemIndex, itemValue, null);
	}
	

	/**
	 * @throws XMPException
	 * @see XMPMeta#setProperty(String, String, Object, PropertyOptions)
	 */
	public void setProperty(String schemaNS, String propName, Object propValue,
			PropertyOptions options) throws XMPException
	{
		ParameterAsserts.assertSchemaNS(schemaNS);
		ParameterAsserts.assertPropName(propName);

		options = XMPNodeUtils.verifySetOptions(options, propValue);

		XMPPath expPath = XMPPathParser.expandXPath(schemaNS, propName);

		XMPNode propNode = XMPNodeUtils.findNode(tree, expPath, true, options);
		if (propNode != null)
		{
			setNode(propNode, propValue, options, false);
		}
		else
		{
			throw new XMPException("Specified property does not exist", XMPError.BADXPATH);
		}
	}

	
	/**
	 * @see XMPMeta#setProperty(String, String, Object)
	 */
	public void setProperty(String schemaNS, String propName, Object propValue) throws XMPException
	{
		setProperty(schemaNS, propName, propValue, null);
	}
	

	/**
	 * @throws XMPException
	 * @see XMPMeta#setQualifier(String, String, String, String, String,
	 *      PropertyOptions)
	 */
	public void setQualifier(String schemaNS, String propName, String qualNS, String qualName,
			String qualValue, PropertyOptions options) throws XMPException
	{
		ParameterAsserts.assertSchemaNS(schemaNS);
		ParameterAsserts.assertPropName(propName);

		if (!doesPropertyExist(schemaNS, propName))
		{
			throw new XMPException("Specified property does not exist!", XMPError.BADXPATH);
		}

		String qualPath = propName + XMPPathFactory.composeQualifierPath(qualNS, qualName);
		setProperty(schemaNS, qualPath, qualValue, options);
	}

	
	/**
	 * @see XMPMeta#setQualifier(String, String, String, String, String)
	 */
	public void setQualifier(String schemaNS, String propName, String qualNS, String qualName,
			String qualValue) throws XMPException
	{
		setQualifier(schemaNS, propName, qualNS, qualName, qualValue, null);
		
	}
	

	/**
	 * @see XMPMeta#setStructField(String, String, String, String, String,
	 *      PropertyOptions)
	 */
	public void setStructField(String schemaNS, String structName, String fieldNS,
			String fieldName, String fieldValue, PropertyOptions options) throws XMPException
	{
		ParameterAsserts.assertSchemaNS(schemaNS);
		ParameterAsserts.assertStructName(structName);

		String fieldPath = structName + XMPPathFactory.composeStructFieldPath(fieldNS, fieldName);
		setProperty(schemaNS, fieldPath, fieldValue, options);
	}

	
	/**
	 * @see XMPMeta#setStructField(String, String, String, String, String)
	 */
	public void setStructField(String schemaNS, String structName, String fieldNS,
			String fieldName, String fieldValue) throws XMPException
	{
		setStructField(schemaNS, structName, fieldNS, fieldName, fieldValue, null);
	}
	

	/**
	 * @see XMPMeta#getObjectName()
	 */
	public String getObjectName()
	{
		return tree.getName() != null ? tree.getName() : "";
	}

	
	/**
	 * @see XMPMeta#setObjectName(String)
	 */
	public void setObjectName(String name)
	{
		tree.setName(name);
	}

	
	/**
	 * @see XMPMeta#getPacketHeader()
	 */
	public String getPacketHeader()
	{
		return packetHeader;
	}

	
	/**
	 * Sets the packetHeader attributes, only used by the parser.
	 * @param packetHeader the processing instruction content
	 */
	public void setPacketHeader(String packetHeader)
	{
		this.packetHeader = packetHeader;
	}
	
	
	/**
	 * Performs a deep clone of the XMPMeta-object
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		XMPNode clonedTree = (XMPNode) tree.clone();
		return new XMPMetaImpl(clonedTree);
	}


	/**
	 * @see XMPMeta#dumpObject()
	 */
	public String dumpObject()
	{
		// renders tree recursively
		return getRoot().dumpNode(true);
	}

	
	/**
	 * @see XMPMeta#sort()
	 */
	public void sort()
	{
		this.tree.sort();
	}


	/**
	 * @see XMPMeta#normalize(ParseOptions)
	 */
	public void normalize(ParseOptions options) throws XMPException
	{
		if (options == null)
		{
			options = new ParseOptions();
		}
		XMPNormalizer.process(this, options);		
	}

	
	/**
	 * @return Returns the root node of the XMP tree.
	 */
	public XMPNode getRoot()
	{
		return tree;
	}



	// -------------------------------------------------------------------------------------
	// private


	/**
	 * Locate or create the item node and set the value. Note the index
	 * parameter is one-based! The index can be in the range [1..size + 1] or
	 * "last()", normalize it and check the insert flags. The order of the
	 * normalization checks is important. If the array is empty we end up with
	 * an index and location to set item size + 1.
	 * 
	 * @param arrayNode an array node
	 * @param itemIndex the index where to insert the item
	 * @param itemValue the item value
	 * @param itemOptions the options for the new item
	 * @param insert insert oder overwrite at index position?
	 * @throws XMPException
	 */
	private void doSetArrayItem(XMPNode arrayNode, int itemIndex, String itemValue,
			PropertyOptions itemOptions, boolean insert) throws XMPException
	{
		XMPNode itemNode = new XMPNode(ARRAY_ITEM_NAME, null);
		itemOptions = XMPNodeUtils.verifySetOptions(itemOptions, itemValue);

		// in insert mode the index after the last is allowed,
		// even ARRAY_LAST_ITEM points to the index *after* the last.
		int maxIndex = insert ? arrayNode.getChildrenLength() + 1 : arrayNode.getChildrenLength();
		if (itemIndex == ARRAY_LAST_ITEM)
		{
			itemIndex = maxIndex;
		}

		if (1 <= itemIndex && itemIndex <= maxIndex)
		{
			if (!insert)
			{
				arrayNode.removeChild(itemIndex);
			}
			arrayNode.addChild(itemIndex, itemNode);
			setNode(itemNode, itemValue, itemOptions, false);
		}
		else
		{
			throw new XMPException("Array index out of bounds", XMPError.BADINDEX);
		}
	}


	/**
	 * The internals for setProperty() and related calls, used after the node is
	 * found or created.
	 * 
	 * @param node
	 *            the newly created node
	 * @param value
	 *            the node value, can be <code>null</code>
	 * @param newOptions
	 *            options for the new node, must not be <code>null</code>.
	 * @param deleteExisting flag if the existing value is to be overwritten 
	 * @throws XMPException thrown if options and value do not correspond
	 */
	void setNode(XMPNode node, Object value, PropertyOptions newOptions, boolean deleteExisting)
			throws XMPException
	{
		if (deleteExisting)
		{
			node.clear();
		}

		// its checked by setOptions(), if the merged result is a valid options set
		node.getOptions().mergeWith(newOptions);

		if (!node.getOptions().isCompositeProperty())
		{
			// This is setting the value of a leaf node.
			XMPNodeUtils.setNodeValue(node, value);
		}
		else
		{
			if (value != null && value.toString().length() > 0)
			{
				throw new XMPException("Composite nodes can't have values", XMPError.BADXPATH);
			}

			node.removeChildren();
		}

	}


	/**
	 * Evaluates a raw node value to the given value type, apply special
	 * conversions for defined types in XMP.
	 * 
	 * @param valueType
	 *            an int indicating the value type
	 * @param propNode
	 *            the node containing the value
	 * @return Returns a literal value for the node.
	 * @throws XMPException
	 */
	private Object evaluateNodeValue(int valueType, final XMPNode propNode) throws XMPException
	{
		final Object value;
		String rawValue = propNode.getValue();
		switch (valueType)
		{
		case VALUE_BOOLEAN:
			value = new Boolean(XMPUtils.convertToBoolean(rawValue));
			break;
		case VALUE_INTEGER:
			value = new Integer(XMPUtils.convertToInteger(rawValue));
			break;
		case VALUE_LONG:
			value = new Long(XMPUtils.convertToLong(rawValue));
			break;
		case VALUE_DOUBLE:
			value = new Double(XMPUtils.convertToDouble(rawValue));
			break;
		case VALUE_DATE:
			value = XMPUtils.convertToDate(rawValue);
			break;
		case VALUE_CALENDAR:
			XMPDateTime dt = XMPUtils.convertToDate(rawValue);
			value = dt.getCalendar();
			break;
		case VALUE_BASE64:
			value = XMPUtils.decodeBase64(rawValue);
			break;
		case VALUE_STRING:
		default:
			// leaf values return empty string instead of null
			// for the other cases the converter methods provides a "null"
			// value.
			// a default value can only occur if this method is made public.
			value = rawValue != null || propNode.getOptions().isCompositeProperty() ? rawValue : "";
			break;
		}
		return value;
	}
}

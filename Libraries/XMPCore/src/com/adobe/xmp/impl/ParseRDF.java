// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPError;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.XMPSchemaRegistry;
import com.adobe.xmp.options.PropertyOptions;


/**
 * Parser for "normal" XML serialisation of RDF.  
 * 
 * @since   14.07.2006
 */
public class ParseRDF implements XMPError, XMPConst
{
	/** */
	public static final int RDFTERM_OTHER = 0;
	/** Start of coreSyntaxTerms. */
	public static final int RDFTERM_RDF = 1;
	/** */
	public static final int RDFTERM_ID = 2;
	/** */
	public static final int RDFTERM_ABOUT = 3;
	/** */
	public static final int RDFTERM_PARSE_TYPE = 4;
	/** */
	public static final int RDFTERM_RESOURCE = 5;
	/** */
	public static final int RDFTERM_NODE_ID = 6;
	/** End of coreSyntaxTerms */
	public static final int RDFTERM_DATATYPE = 7;
	/** Start of additions for syntax Terms. */
	public static final int RDFTERM_DESCRIPTION = 8; 
	/** End of of additions for syntaxTerms. */
	public static final int RDFTERM_LI = 9;
	/** Start of oldTerms. */
	public static final int RDFTERM_ABOUT_EACH = 10; 
	/** */
	public static final int RDFTERM_ABOUT_EACH_PREFIX = 11;
	/** End of oldTerms. */
	public static final int RDFTERM_BAG_ID = 12;
	/** */
	public static final int RDFTERM_FIRST_CORE = RDFTERM_RDF;
	/** */
	public static final int RDFTERM_LAST_CORE = RDFTERM_DATATYPE;
	/** ! Yes, the syntax terms include the core terms. */
	public static final int RDFTERM_FIRST_SYNTAX = RDFTERM_FIRST_CORE;
	/** */
	public static final int RDFTERM_LAST_SYNTAX = RDFTERM_LI;
	/** */
	public static final int RDFTERM_FIRST_OLD = RDFTERM_ABOUT_EACH;
	/** */
	public static final int RDFTERM_LAST_OLD = RDFTERM_BAG_ID;

	/** this prefix is used for default namespaces */
	public static final String DEFAULT_PREFIX = "_dflt";
	
	
	
	/**
	 * The main parsing method. The XML tree is walked through from the root node and and XMP tree
	 * is created. This is a raw parse, the normalisation of the XMP tree happens outside.
	 * 
	 * @param xmlRoot the XML root node
	 * @return Returns an XMP metadata object (not normalized)
	 * @throws XMPException Occurs if the parsing fails for any reason.
	 */
	static XMPMetaImpl parse(Node xmlRoot) throws XMPException
	{
		XMPMetaImpl xmp = new XMPMetaImpl();
		rdf_RDF(xmp, xmlRoot);
		return xmp;
	}
	
	
	/**
	 * Each of these parsing methods is responsible for recognizing an RDF
	 * syntax production and adding the appropriate structure to the XMP tree.
	 * They simply return for success, failures will throw an exception.
	 * 
	 * @param xmp the xmp metadata object that is generated
	 * @param rdfRdfNode the top-level xml node
	 * @throws XMPException thown on parsing errors
	 */
	static void rdf_RDF(XMPMetaImpl xmp, Node rdfRdfNode) throws XMPException
	{
		if (rdfRdfNode.hasAttributes())
		{
			rdf_NodeElementList (xmp, xmp.getRoot(), rdfRdfNode);
		}	
		else
		{	
			throw new XMPException("Invalid attributes of rdf:RDF element", BADRDF);
		}
	}
	
	
	/**
	 * 7.2.10 nodeElementList<br>
	 * ws* ( nodeElement ws* )*
	 * 
	 * Note: this method is only called from the rdf:RDF-node (top level)
	 * @param xmp the xmp metadata object that is generated
	 * @param xmpParent the parent xmp node
	 * @param rdfRdfNode the top-level xml node
	 * @throws XMPException thown on parsing errors
	 */
	private static void rdf_NodeElementList(XMPMetaImpl xmp, XMPNode xmpParent, Node rdfRdfNode) 
		throws XMPException
	{
		for (int i = 0; i < rdfRdfNode.getChildNodes().getLength(); i++)
		{
			Node child = rdfRdfNode.getChildNodes().item(i);
			// filter whitespaces (and all text nodes)
			if (!isWhitespaceNode(child))
			{	
				rdf_NodeElement  (xmp, xmpParent, child, true);
			}	
		} 
	}


	/**
 	 * 7.2.5 nodeElementURIs
	 * 		anyURI - ( coreSyntaxTerms | rdf:li | oldTerms )
	 *
 	 * 7.2.11 nodeElement
	 * 		start-element ( URI == nodeElementURIs,
	 * 		attributes == set ( ( idAttr | nodeIdAttr | aboutAttr )?, propertyAttr* ) )
	 * 		propertyEltList
	 * 		end-element()
	 * 
	 * A node element URI is rdf:Description or anything else that is not an RDF
	 * term.
	 * 
	 * @param xmp the xmp metadata object that is generated
	 * @param xmpParent the parent xmp node
	 * @param xmlNode the currently processed XML node
	 * @param isTopLevel Flag if the node is a top-level node
	 * @throws XMPException thown on parsing errors
	 */
	private static void rdf_NodeElement(XMPMetaImpl xmp, XMPNode xmpParent, Node xmlNode,
			boolean isTopLevel) throws XMPException
	{
		int nodeTerm = getRDFTermKind (xmlNode);
		if (nodeTerm != RDFTERM_DESCRIPTION  &&  nodeTerm != RDFTERM_OTHER)
		{
			throw new XMPException("Node element must be rdf:Description or typed node",
				BADRDF);
		}
		else if (isTopLevel  &&  nodeTerm == RDFTERM_OTHER)
		{
			throw new XMPException("Top level typed node not allowed", BADXMP);
		}
		else
		{
			rdf_NodeElementAttrs (xmp, xmpParent, xmlNode, isTopLevel);
			rdf_PropertyElementList (xmp, xmpParent, xmlNode, isTopLevel);
		}
		
	}


	/**
	 * 
	 * 7.2.7 propertyAttributeURIs
	 * 		anyURI - ( coreSyntaxTerms | rdf:Description | rdf:li | oldTerms )
	 *
	 * 7.2.11 nodeElement
	 * start-element ( URI == nodeElementURIs,
	 * 					attributes == set ( ( idAttr | nodeIdAttr | aboutAttr )?, propertyAttr* ) )
	 * 					propertyEltList
	 * 					end-element()
	 * 
	 * Process the attribute list for an RDF node element. A property attribute URI is 
	 * anything other than an RDF term. The rdf:ID and rdf:nodeID attributes are simply ignored, 
	 * as are rdf:about attributes on inner nodes.
	 * 
	 * @param xmp the xmp metadata object that is generated
	 * @param xmpParent the parent xmp node
	 * @param xmlNode the currently processed XML node
	 * @param isTopLevel Flag if the node is a top-level node
	 * @throws XMPException thown on parsing errors
	 */
	private static void rdf_NodeElementAttrs(XMPMetaImpl xmp, XMPNode xmpParent, Node xmlNode,
			boolean isTopLevel) throws XMPException
	{
		// Used to detect attributes that are mutually exclusive.
		int exclusiveAttrs = 0;	
	
		for (int i = 0; i < xmlNode.getAttributes().getLength(); i++)
		{
			Node attribute = xmlNode.getAttributes().item(i);
			
			// quick hack, ns declarations do not appear in C++
			// ignore "ID" without namespace
			if ("xmlns".equals(attribute.getPrefix())  ||
				(attribute.getPrefix() == null  &&  "xmlns".equals(attribute.getNodeName())))
			{
				continue;
			}
			
			int attrTerm = getRDFTermKind(attribute);

			switch (attrTerm)
			{
				case RDFTERM_ID:
				case RDFTERM_NODE_ID:
				case RDFTERM_ABOUT:
					if (exclusiveAttrs > 0)
					{
						throw new XMPException("Mutally exclusive about, ID, nodeID attributes",
								BADRDF);
					}
					
					exclusiveAttrs++;
	
					if (isTopLevel && (attrTerm == RDFTERM_ABOUT))
					{
						// This is the rdf:about attribute on a top level node. Set
						// the XMP tree name if
						// it doesn't have a name yet. Make sure this name matches
						// the XMP tree name.
						if (xmpParent.getName() != null && xmpParent.getName().length() > 0)
						{
							if (!xmpParent.getName().equals(attribute.getNodeValue()))
							{
								throw new XMPException("Mismatched top level rdf:about values",
										BADXMP);
							}
						}
						else
						{
							xmpParent.setName(attribute.getNodeValue());
						}
					}
					break;
	
				case RDFTERM_OTHER:
					addChildNode(xmp, xmpParent, attribute, attribute.getNodeValue(), isTopLevel);
					break;
	
				default:
					throw new XMPException("Invalid nodeElement attribute", BADRDF);
			}

		}		
	}


	/**
	 * 7.2.13 propertyEltList
	 * ws* ( propertyElt ws* )*
	 * 
	 * @param xmp the xmp metadata object that is generated
	 * @param xmpParent the parent xmp node
	 * @param xmlParent the currently processed XML node
	 * @param isTopLevel Flag if the node is a top-level node
	 * @throws XMPException thown on parsing errors
	 */
	private static void rdf_PropertyElementList(XMPMetaImpl xmp, XMPNode xmpParent, Node xmlParent,
			boolean isTopLevel) throws XMPException
	{
		for (int i = 0; i < xmlParent.getChildNodes().getLength(); i++)
		{
			Node currChild = xmlParent.getChildNodes().item(i);
			if (isWhitespaceNode(currChild))
			{
				continue;
			}	
			else if (currChild.getNodeType() != Node.ELEMENT_NODE)
			{
				throw new XMPException("Expected property element node not found", BADRDF);
			}
			else
			{	
				rdf_PropertyElement(xmp, xmpParent, currChild, isTopLevel);
			}	
		}
	}


	/**
	 * 7.2.14 propertyElt
	 * 
	 *		resourcePropertyElt | literalPropertyElt | parseTypeLiteralPropertyElt |
	 *		parseTypeResourcePropertyElt | parseTypeCollectionPropertyElt | 
	 *		parseTypeOtherPropertyElt | emptyPropertyElt
	 *
	 * 7.2.15 resourcePropertyElt
	 *		start-element ( URI == propertyElementURIs, attributes == set ( idAttr? ) )
	 *		ws* nodeElement ws*
	 *		end-element()
	 *
	 * 7.2.16 literalPropertyElt
	 *		start-element (
	 *			URI == propertyElementURIs, attributes == set ( idAttr?, datatypeAttr?) )
	 *		text()
	 *		end-element()
	 *
	 * 7.2.17 parseTypeLiteralPropertyElt
	 *		start-element (
	 *			URI == propertyElementURIs, attributes == set ( idAttr?, parseLiteral ) )
	 *		literal
	 *		end-element()
	 *
	 * 7.2.18 parseTypeResourcePropertyElt
	 *		start-element (
	 *			 URI == propertyElementURIs, attributes == set ( idAttr?, parseResource ) )
	 *		propertyEltList
	 *		end-element()
	 *
	 * 7.2.19 parseTypeCollectionPropertyElt
	 *		start-element (
	 *			URI == propertyElementURIs, attributes == set ( idAttr?, parseCollection ) )
	 *		nodeElementList
	 *		end-element()
	 *
	 * 7.2.20 parseTypeOtherPropertyElt
	 *		start-element ( URI == propertyElementURIs, attributes == set ( idAttr?, parseOther ) )
	 *		propertyEltList
	 *		end-element()
	 *
	 * 7.2.21 emptyPropertyElt
	 *		start-element ( URI == propertyElementURIs,
	 *			attributes == set ( idAttr?, ( resourceAttr | nodeIdAttr )?, propertyAttr* ) )
	 *		end-element()
	 *
	 * The various property element forms are not distinguished by the XML element name, 
	 * but by their attributes for the most part. The exceptions are resourcePropertyElt and 
	 * literalPropertyElt. They are distinguished by their XML element content.
	 *
	 * NOTE: The RDF syntax does not explicitly include the xml:lang attribute although it can 
	 * appear in many of these. We have to allow for it in the attibute counts below.	 
	 *  
	 * @param xmp the xmp metadata object that is generated
	 * @param xmpParent the parent xmp node
	 * @param xmlNode the currently processed XML node
	 * @param isTopLevel Flag if the node is a top-level node
	 * @throws XMPException thown on parsing errors
	 */
	private static void rdf_PropertyElement(XMPMetaImpl xmp, XMPNode xmpParent, Node xmlNode,
			boolean isTopLevel) throws XMPException
	{
		int nodeTerm = getRDFTermKind (xmlNode);
		if (!isPropertyElementName(nodeTerm)) 
		{
			throw new XMPException("Invalid property element name", BADRDF);
		}
		
		// remove the namespace-definitions from the list
		NamedNodeMap attributes = xmlNode.getAttributes();
		List nsAttrs = null;
		for (int i = 0; i < attributes.getLength(); i++)
		{
			Node attribute = attributes.item(i);
			if ("xmlns".equals(attribute.getPrefix())  ||
				(attribute.getPrefix() == null  &&  "xmlns".equals(attribute.getNodeName())))
			{
				if (nsAttrs == null)
				{
					nsAttrs = new ArrayList();
				}
				nsAttrs.add(attribute.getNodeName());
			}
		}
		if (nsAttrs != null)
		{
			for (Iterator it = nsAttrs.iterator(); it.hasNext();)
			{
				String ns = (String) it.next();
				attributes.removeNamedItem(ns);
			}
		}
		
		
		if (attributes.getLength() > 3)
		{
			// Only an emptyPropertyElt can have more than 3 attributes.
			rdf_EmptyPropertyElement(xmp, xmpParent, xmlNode, isTopLevel);
		} 
		else 
		{
			// Look through the attributes for one that isn't rdf:ID or xml:lang, 
			// it will usually tell what we should be dealing with. 
			// The called routines must verify their specific syntax!
	
			for (int i = 0; i < attributes.getLength(); i++)
			{
				Node attribute = attributes.item(i);
				String attrLocal = attribute.getLocalName();
				String attrNS = attribute.getNamespaceURI();
				String attrValue = attribute.getNodeValue();
				if (!(XML_LANG.equals(attribute.getNodeName())  &&
					!("ID".equals(attrLocal)  &&  NS_RDF.equals(attrNS))))  
				{
					if ("datatype".equals(attrLocal)  &&  NS_RDF.equals(attrNS))
					{
						rdf_LiteralPropertyElement (xmp, xmpParent, xmlNode, isTopLevel);
					}
					else if (!("parseType".equals(attrLocal)  &&  NS_RDF.equals(attrNS)))
					{
						rdf_EmptyPropertyElement (xmp, xmpParent, xmlNode, isTopLevel);
					}
					else if ("Literal".equals(attrValue))
					{
						rdf_ParseTypeLiteralPropertyElement();
					}
					else if ("Resource".equals(attrValue))
					{
						rdf_ParseTypeResourcePropertyElement(xmp, xmpParent, xmlNode, isTopLevel);
					}
					else if ("Collection".equals(attrValue))
					{
						rdf_ParseTypeCollectionPropertyElement();
					}
					else
					{
						rdf_ParseTypeOtherPropertyElement();
					}
		
					return;
				}
			}
			
			// Only rdf:ID and xml:lang, could be a resourcePropertyElt, a literalPropertyElt, 
			// or an emptyPropertyElt. Look at the child XML nodes to decide which.

			if (xmlNode.hasChildNodes())
			{
				for (int i = 0; i < xmlNode.getChildNodes().getLength(); i++)
				{
					Node currChild = xmlNode.getChildNodes().item(i);
					if (currChild.getNodeType() != Node.TEXT_NODE)
					{
						rdf_ResourcePropertyElement (xmp, xmpParent, xmlNode, isTopLevel);
						return;
					}
				}
				
				rdf_LiteralPropertyElement (xmp, xmpParent, xmlNode, isTopLevel);
			}
			else
			{
				rdf_EmptyPropertyElement (xmp, xmpParent, xmlNode, isTopLevel);
			}
		}		
	}

	
	/**
	 * 7.2.15 resourcePropertyElt
	 *		start-element ( URI == propertyElementURIs, attributes == set ( idAttr? ) )
	 *		ws* nodeElement ws*
	 *		end-element()
	 *
	 * This handles structs using an rdf:Description node, 
	 * arrays using rdf:Bag/Seq/Alt, and typedNodes. It also catches and cleans up qualified 
	 * properties written with rdf:Description and rdf:value.
	 * 
	 * @param xmp the xmp metadata object that is generated
	 * @param xmpParent the parent xmp node
	 * @param xmlNode the currently processed XML node
	 * @param isTopLevel Flag if the node is a top-level node
	 * @throws XMPException thown on parsing errors
	 */
	private static void rdf_ResourcePropertyElement(XMPMetaImpl xmp, XMPNode xmpParent,
			Node xmlNode, boolean isTopLevel) throws XMPException
	{
		if (isTopLevel  &&  "iX:changes".equals(xmlNode.getNodeName()))
		{
			// Strip old "punchcard" chaff which has on the prefix "iX:".
			return;	
		}
		
		XMPNode newCompound = addChildNode(xmp, xmpParent, xmlNode, "", isTopLevel);
		
		// walk through the attributes
		for (int i = 0; i < xmlNode.getAttributes().getLength(); i++)
		{
			Node attribute = xmlNode.getAttributes().item(i);
			if ("xmlns".equals(attribute.getPrefix())  ||
					(attribute.getPrefix() == null  &&  "xmlns".equals(attribute.getNodeName())))
			{
				continue;
			}
			
			String attrLocal = attribute.getLocalName();
			String attrNS = attribute.getNamespaceURI();
			if (XML_LANG.equals(attribute.getNodeName()))
			{
				addQualifierNode (newCompound, XML_LANG, attribute.getNodeValue());
			} 
			else if ("ID".equals(attrLocal)  &&  NS_RDF.equals(attrNS))
			{
				continue;	// Ignore all rdf:ID attributes.
			}
			else
			{
				throw new XMPException(
					"Invalid attribute for resource property element", BADRDF);
			}
		}

		// walk through the children
		
		Node currChild = null;
		boolean found = false;
		int i;
		for (i = 0; i < xmlNode.getChildNodes().getLength(); i++)
		{
			currChild = xmlNode.getChildNodes().item(i);
			if (!isWhitespaceNode(currChild))
			{
				if (currChild.getNodeType() == Node.ELEMENT_NODE  &&  !found)
				{
					boolean isRDF = NS_RDF.equals(currChild.getNamespaceURI());
					String childLocal = currChild.getLocalName();
					
					if (isRDF  &&  "Bag".equals(childLocal))
					{
						newCompound.getOptions().setArray(true);
					}
					else if (isRDF  &&  "Seq".equals(childLocal))
					{
						newCompound.getOptions().setArray(true).setArrayOrdered(true);
					}
					else if (isRDF  &&  "Alt".equals(childLocal))
					{
						newCompound.getOptions().setArray(true).setArrayOrdered(true)
								.setArrayAlternate(true);
					}
					else
					{
						newCompound.getOptions().setStruct(true);
						if (!isRDF  &&  !"Description".equals(childLocal))
						{
							String typeName = currChild.getNamespaceURI();
							if (typeName == null)
							{
								throw new XMPException(
										"All XML elements must be in a namespace", BADXMP);
							}
							typeName += ':' + childLocal;
							addQualifierNode (newCompound, "rdf:type", typeName);
						}
					}
	
					rdf_NodeElement (xmp, newCompound, currChild, false);
					
					if (newCompound.getHasValueChild())
					{
						fixupQualifiedNode (newCompound);
					} 
					else if (newCompound.getOptions().isArrayAlternate())
					{
						XMPNodeUtils.detectAltText(newCompound);
					}				
					
					found = true;
				}
				else if (found)
				{
					// found second child element
					throw new XMPException(
						"Invalid child of resource property element", BADRDF);
				}
				else
				{
					throw new XMPException(
						"Children of resource property element must be XML elements", BADRDF);
				}
			}
		}
		
		if (!found)
		{
			// didn't found any child elements
			throw new XMPException("Missing child of resource property element", BADRDF);
		}
	}	

	
	/**
	 * 7.2.16 literalPropertyElt
	 *		start-element ( URI == propertyElementURIs, 
	 *				attributes == set ( idAttr?, datatypeAttr?) )
	 *		text()
	 *		end-element()
	 *
	 * Add a leaf node with the text value and qualifiers for the attributes.
	 * @param xmp the xmp metadata object that is generated
	 * @param xmpParent the parent xmp node
	 * @param xmlNode the currently processed XML node
	 * @param isTopLevel Flag if the node is a top-level node
	 * @throws XMPException thown on parsing errors
	 */	
	private static void rdf_LiteralPropertyElement(XMPMetaImpl xmp, XMPNode xmpParent,
			Node xmlNode, boolean isTopLevel) throws XMPException
	{
		XMPNode newChild = addChildNode (xmp, xmpParent, xmlNode, null, isTopLevel);
		
		for (int i = 0; i < xmlNode.getAttributes().getLength(); i++)
		{
			Node attribute = xmlNode.getAttributes().item(i);
			if ("xmlns".equals(attribute.getPrefix())  ||
					(attribute.getPrefix() == null  &&  "xmlns".equals(attribute.getNodeName())))
			{
				continue;
			}
			
			String attrNS = attribute.getNamespaceURI();
			String attrLocal = attribute.getLocalName();
			if (XML_LANG.equals(attribute.getNodeName()))
			{
				addQualifierNode(newChild, XML_LANG, attribute.getNodeValue());
			} 
			else if (NS_RDF.equals(attrNS)  &&
					 ("ID".equals(attrLocal)  ||  "datatype".equals(attrLocal)))
			{
				continue;	// Ignore all rdf:ID and rdf:datatype attributes.
			}
			else
			{
				throw new XMPException(
					"Invalid attribute for literal property element", BADRDF);
			}
		}
		String textValue = "";
		for (int i = 0; i < xmlNode.getChildNodes().getLength(); i++)
		{
			Node child = xmlNode.getChildNodes().item(i);
			if (child.getNodeType() == Node.TEXT_NODE)
			{
				textValue += child.getNodeValue();
			}
			else
			{
				throw new XMPException("Invalid child of literal property element", BADRDF);
			}
		}
		newChild.setValue(textValue);
	}
	
	
	/**
	 * 7.2.17 parseTypeLiteralPropertyElt
	 *		start-element ( URI == propertyElementURIs,
	 *			attributes == set ( idAttr?, parseLiteral ) )
	 *		literal
	 *		end-element()
	 * 
	 * @throws XMPException thown on parsing errors
	 */
	private static void rdf_ParseTypeLiteralPropertyElement() throws XMPException
	{
		throw new XMPException("ParseTypeLiteral property element not allowed", BADXMP);
	}

	
	/**
	 * 7.2.18 parseTypeResourcePropertyElt
	 *		start-element ( URI == propertyElementURIs, 
	 *			attributes == set ( idAttr?, parseResource ) )
	 *		propertyEltList
	 *		end-element()
	 *
	 * Add a new struct node with a qualifier for the possible rdf:ID attribute. 
	 * Then process the XML child nodes to get the struct fields.
	 * 
	 * @param xmp the xmp metadata object that is generated
	 * @param xmpParent the parent xmp node
	 * @param xmlNode the currently processed XML node
	 * @param isTopLevel Flag if the node is a top-level node
	 * @throws XMPException thown on parsing errors
	 */
	private static void rdf_ParseTypeResourcePropertyElement(XMPMetaImpl xmp, XMPNode xmpParent,
			Node xmlNode, boolean isTopLevel) throws XMPException
	{
		XMPNode newStruct = addChildNode (xmp, xmpParent, xmlNode, "", isTopLevel);
		
		newStruct.getOptions().setStruct(true);

		for (int i = 0; i < xmlNode.getAttributes().getLength(); i++)
		{
			Node attribute = xmlNode.getAttributes().item(i);
			if ("xmlns".equals(attribute.getPrefix())  ||
					(attribute.getPrefix() == null  &&  "xmlns".equals(attribute.getNodeName())))
			{
				continue;
			}
			
			String attrLocal = attribute.getLocalName();
			String attrNS = attribute.getNamespaceURI();
			if (XML_LANG.equals(attribute.getNodeName()))
			{
				addQualifierNode (newStruct, XML_LANG, attribute.getNodeValue());
			}
			else if (NS_RDF.equals(attrNS)  &&
					 ("ID".equals(attrLocal)  ||  "parseType".equals(attrLocal)))
			{
				continue;	// The caller ensured the value is "Resource".
							// Ignore all rdf:ID attributes.
			} 
			else
			{
				throw new XMPException("Invalid attribute for ParseTypeResource property element",
						BADRDF);
			}
		}

		rdf_PropertyElementList (xmp, newStruct, xmlNode, false);

		if (newStruct.getHasValueChild())
		{
			fixupQualifiedNode (newStruct);
		}
	}

	
	/**
	 * 7.2.19 parseTypeCollectionPropertyElt
	 *		start-element ( URI == propertyElementURIs, 
	 *			attributes == set ( idAttr?, parseCollection ) )
	 *		nodeElementList
	 *		end-element()
	 *
	 * @throws XMPException thown on parsing errors
	 */
	private static void rdf_ParseTypeCollectionPropertyElement() throws XMPException
	{
		throw new XMPException("ParseTypeCollection property element not allowed", BADXMP);
	}


	/**
	 * 7.2.20 parseTypeOtherPropertyElt
	 *		start-element ( URI == propertyElementURIs, attributes == set ( idAttr?, parseOther ) )
	 *		propertyEltList
	 *		end-element()
	 * 
	 * @throws XMPException thown on parsing errors
	 */
	private static void rdf_ParseTypeOtherPropertyElement() throws XMPException
	{
		throw new XMPException("ParseTypeOther property element not allowed", BADXMP);
	}


	/**
	 * 7.2.21 emptyPropertyElt
	 *		start-element ( URI == propertyElementURIs,
	 *						attributes == set (
	 *							idAttr?, ( resourceAttr | nodeIdAttr )?, propertyAttr* ) )
	 *		end-element()
	 *
	 *	<ns:Prop1/>  <!-- a simple property with an empty value --> 
	 *	<ns:Prop2 rdf:resource="http: *www.adobe.com/"/> <!-- a URI value --> 
	 *	<ns:Prop3 rdf:value="..." ns:Qual="..."/> <!-- a simple qualified property --> 
	 *	<ns:Prop4 ns:Field1="..." ns:Field2="..."/> <!-- a struct with simple fields -->
	 *
	 * An emptyPropertyElt is an element with no contained content, just a possibly empty set of
	 * attributes. An emptyPropertyElt can represent three special cases of simple XMP properties: a
	 * simple property with an empty value (ns:Prop1), a simple property whose value is a URI
	 * (ns:Prop2), or a simple property with simple qualifiers (ns:Prop3). 
	 * An emptyPropertyElt can also represent an XMP struct whose fields are all simple and 
	 * unqualified (ns:Prop4).
	 *
	 * It is an error to use both rdf:value and rdf:resource - that can lead to invalid  RDF in the
	 * verbose form written using a literalPropertyElt.
	 *
	 * The XMP mapping for an emptyPropertyElt is a bit different from generic RDF, partly for 
	 * design reasons and partly for historical reasons. The XMP mapping rules are:
	 * <ol> 
	 *		<li> If there is an rdf:value attribute then this is a simple property
	 *				 with a text value.
	 *		All other attributes are qualifiers.
	 *		<li> If there is an rdf:resource attribute then this is a simple property 
	 *			with a URI value. 
	 *		All other attributes are qualifiers.
	 *		<li> If there are no attributes other than xml:lang, rdf:ID, or rdf:nodeID
	 *				then this is a simple 
	 *		property with an empty value. 
	 *		<li> Otherwise this is a struct, the attributes other than xml:lang, rdf:ID, 
	 *				or rdf:nodeID are fields. 
	 * </ol>
	 * 
	 * @param xmp the xmp metadata object that is generated
	 * @param xmpParent the parent xmp node
	 * @param xmlNode the currently processed XML node
	 * @param isTopLevel Flag if the node is a top-level node
	 * @throws XMPException thown on parsing errors
	 */
	private static void rdf_EmptyPropertyElement(XMPMetaImpl xmp, XMPNode xmpParent, Node xmlNode,
			boolean isTopLevel) throws XMPException
	{
		boolean hasPropertyAttrs = false;
		boolean hasResourceAttr = false;
		boolean hasNodeIDAttr = false;
		boolean hasValueAttr = false;
		
		Node valueNode = null;	// ! Can come from rdf:value or rdf:resource.
		
		if (xmlNode.hasChildNodes())
		{
			throw new XMPException(
					"Nested content not allowed with rdf:resource or property attributes",
					BADRDF);
		}
		
		// First figure out what XMP this maps to and remember the XML node for a simple value.
		for (int i = 0; i < xmlNode.getAttributes().getLength(); i++)
		{
			Node attribute = xmlNode.getAttributes().item(i);
			if ("xmlns".equals(attribute.getPrefix())  ||
					(attribute.getPrefix() == null  &&  "xmlns".equals(attribute.getNodeName())))
			{
				continue;
			}
			
			int attrTerm = getRDFTermKind (attribute);

			switch (attrTerm)
			{
				case RDFTERM_ID :
					// Nothing to do.
					break;

				case RDFTERM_RESOURCE :
					if (hasNodeIDAttr)
					{
						throw new XMPException(
							"Empty property element can't have both rdf:resource and rdf:nodeID",
							BADRDF);
					}
					else if (hasValueAttr)
					{
						throw new XMPException(
								"Empty property element can't have both rdf:value and rdf:resource",
								BADXMP);
					}

					hasResourceAttr = true;
					if (!hasValueAttr) 
					{
						valueNode = attribute;
					}	
					break;

				case RDFTERM_NODE_ID:
				if (hasResourceAttr)
				{
					throw new XMPException(
							"Empty property element can't have both rdf:resource and rdf:nodeID",
							BADRDF);
				}
				hasNodeIDAttr = true;
				break;

			case RDFTERM_OTHER:
				if ("value".equals(attribute.getLocalName())
						&& NS_RDF.equals(attribute.getNamespaceURI()))
				{
					if (hasResourceAttr)
					{
						throw new XMPException(
								"Empty property element can't have both rdf:value and rdf:resource",
								BADXMP);
					}
					hasValueAttr = true;
					valueNode = attribute;
				}
				else if (!XML_LANG.equals(attribute.getNodeName()))
				{
					hasPropertyAttrs = true;
				}
				break;

			default:
				throw new XMPException("Unrecognized attribute of empty property element",
						BADRDF);
			}
		}
		
		// Create the right kind of child node and visit the attributes again 
		// to add the fields or qualifiers.
		// ! Because of implementation vagaries, 
		//   the xmpParent is the tree root for top level properties.
		// ! The schema is found, created if necessary, by addChildNode.
		
		XMPNode childNode = addChildNode(xmp, xmpParent, xmlNode, "", isTopLevel);
		boolean childIsStruct = false;
		
		if (hasValueAttr || hasResourceAttr)
		{
			childNode.setValue(valueNode != null ? valueNode.getNodeValue() : "");
			if (!hasValueAttr)
			{
				// ! Might have both rdf:value and rdf:resource.
				childNode.getOptions().setURI(true);	
			}
		}
		else if (hasPropertyAttrs)
		{
			childNode.getOptions().setStruct(true);
			childIsStruct = true;
		}
		
		for (int i = 0; i < xmlNode.getAttributes().getLength(); i++)
		{
			Node attribute = xmlNode.getAttributes().item(i);
			if (attribute == valueNode  ||
				"xmlns".equals(attribute.getPrefix())  ||
				(attribute.getPrefix() == null  &&  "xmlns".equals(attribute.getNodeName())))
			{
				continue;	// Skip the rdf:value or rdf:resource attribute holding the value.
			}
			
			int attrTerm = getRDFTermKind (attribute);

			switch (attrTerm)
			{
				case RDFTERM_ID :
				case RDFTERM_NODE_ID :
					break;	// Ignore all rdf:ID and rdf:nodeID attributes.
					
				case RDFTERM_RESOURCE :
					addQualifierNode(childNode, "rdf:resource", attribute.getNodeValue());
					break;

				case RDFTERM_OTHER :
					if (!childIsStruct)
					{
						addQualifierNode(
							childNode, attribute.getNodeName(), attribute.getNodeValue());
					}
					else if (XML_LANG.equals(attribute.getNodeName()))
					{
						addQualifierNode (childNode, XML_LANG, attribute.getNodeValue());
					}
					else
					{
						addChildNode (xmp, childNode, attribute, attribute.getNodeValue(), false);
					}
					break;

				default :
					throw new XMPException("Unrecognized attribute of empty property element",
						BADRDF);
			}

		}		
	}


	/**
	 * Adds a child node.
	 *  
	 * @param xmp the xmp metadata object that is generated
	 * @param xmpParent the parent xmp node
	 * @param xmlNode the currently processed XML node
	 * @param value Node value	
	 * @param isTopLevel Flag if the node is a top-level node
	 * @return Returns the newly created child node.
	 * @throws XMPException thown on parsing errors
	 */
	private static XMPNode addChildNode(XMPMetaImpl xmp, XMPNode xmpParent, Node xmlNode,
			String value, boolean isTopLevel) throws XMPException
	{
		XMPSchemaRegistry registry = XMPMetaFactory.getSchemaRegistry();
		String namespace = xmlNode.getNamespaceURI();
		String childName;
		if (namespace != null)
		{
			if (NS_DC_DEPRECATED.equals(namespace))
			{
				// Fix a legacy DC namespace
				namespace = NS_DC;
			}
			
			String prefix = registry.getNamespacePrefix(namespace);
			if (prefix == null)
			{
				prefix = xmlNode.getPrefix() != null ? xmlNode.getPrefix() : DEFAULT_PREFIX;
				prefix = registry.registerNamespace(namespace, prefix);
			}
			childName = prefix + xmlNode.getLocalName();
		}
		else
		{
			throw new XMPException(
				"XML namespace required for all elements and attributes", BADRDF);
		}

		
		// create schema node if not already there
		PropertyOptions childOptions = new PropertyOptions();
		boolean isAlias = false;
		if (isTopLevel)
		{
			// Lookup the schema node, adjust the XMP parent pointer.
			// Incoming parent must be the tree root.
			XMPNode schemaNode = XMPNodeUtils.findSchemaNode(xmp.getRoot(), namespace,
				DEFAULT_PREFIX, true);
			schemaNode.setImplicit(false);	// Clear the implicit node bit.
			// need runtime check for proper 32 bit code.
			xmpParent = schemaNode;
			
			// If this is an alias set the alias flag in the node 
			// and the hasAliases flag in the tree.
			if (registry.findAlias(childName) != null)
			{
				isAlias = true;
				xmp.getRoot().setHasAliases(true);
				schemaNode.setHasAliases(true);
			}
		}

		
		// Make sure that this is not a duplicate of a named node.
		boolean isArrayItem  = "rdf:li".equals(childName);
		boolean isValueNode  = "rdf:value".equals(childName);

		// Create XMP node and so some checks
		XMPNode newChild = new XMPNode(
			childName, value, childOptions);
		newChild.setAlias(isAlias);
		
		// Add the new child to the XMP parent node, a value node first.
		if (!isValueNode)
		{
			xmpParent.addChild(newChild);
		}
		else
		{
			xmpParent.addChild(1, newChild);
		}
		
		
		if (isValueNode)
		{
			if (isTopLevel  ||  !xmpParent.getOptions().isStruct())
			{
				throw new XMPException("Misplaced rdf:value element", BADRDF);
			}	
			xmpParent.setHasValueChild(true);
		}
		
		if (isArrayItem)
		{
			if (!xmpParent.getOptions().isArray()) 
			{
				throw new XMPException("Misplaced rdf:li element", BADRDF);
			}	
			newChild.setName(ARRAY_ITEM_NAME);
		}
		
		return newChild;
	}

	
	/**
	 * Adds a qualifier node.
	 * 
	 * @param xmpParent the parent xmp node
	 * @param name the name of the qualifier which has to be 
	 * 		QName including the <b>default prefix</b>
	 * @param value the value of the qualifier
	 * @return Returns the newly created child node.
	 * @throws XMPException thown on parsing errors
	 */
	private static XMPNode addQualifierNode(XMPNode xmpParent, String name, String value)
			throws XMPException
	{
		boolean isLang = XML_LANG.equals(name);
	
		XMPNode newQual = null;

		// normalize value of language qualifiers
		newQual = new XMPNode(name, isLang ? Utils.normalizeLangValue(value) : value, null);
		xmpParent.addQualifier(newQual);
		
		return newQual;
	}
	

	/**
	 * The parent is an RDF pseudo-struct containing an rdf:value field. Fix the
	 * XMP data model. The rdf:value node must be the first child, the other
	 * children are qualifiers. The form, value, and children of the rdf:value
	 * node are the real ones. The rdf:value node's qualifiers must be added to
	 * the others.
	 * 
	 * @param xmpParent the parent xmp node
	 * @throws XMPException thown on parsing errors
	 */
	private static void fixupQualifiedNode(XMPNode xmpParent) throws XMPException
	{
		assert xmpParent.getOptions().isStruct()  &&  xmpParent.hasChildren();

		XMPNode valueNode = xmpParent.getChild(1);
		assert "rdf:value".equals(valueNode.getName());

		// Move the qualifiers on the value node to the parent. 
		// Make sure an xml:lang qualifier stays at the front.
		// Check for duplicate names between the value node's qualifiers and the parent's children. 
		// The parent's children are about to become qualifiers. Check here, between the groups. 
		// Intra-group duplicates are caught by XMPNode#addChild(...).
		if (valueNode.getOptions().getHasLanguage())
		{
			if (xmpParent.getOptions().getHasLanguage())
			{
				throw new XMPException("Redundant xml:lang for rdf:value element", 
					BADXMP);
			}
			XMPNode langQual = valueNode.getQualifier(1);
			valueNode.removeQualifier(langQual);
			xmpParent.addQualifier(langQual);
		}
		
		// Start the remaining copy after the xml:lang qualifier.		
		for (int i = 1; i <= valueNode.getQualifierLength(); i++)
		{
			XMPNode qualifier = valueNode.getQualifier(i);
			xmpParent.addQualifier(qualifier);
		}
		
		
		// Change the parent's other children into qualifiers. 
		// This loop starts at 1, child 0 is the rdf:value node.
		for (int i = 2; i <= xmpParent.getChildrenLength(); i++)
		{
			XMPNode qualifier = xmpParent.getChild(i);
			xmpParent.addQualifier(qualifier);
		}
		
		// Move the options and value last, other checks need the parent's original options. 
		// Move the value node's children to be the parent's children.
		assert xmpParent.getOptions().isStruct()  ||  xmpParent.getHasValueChild();
		
		xmpParent.setHasValueChild(false);
		xmpParent.getOptions().setStruct(false);
		xmpParent.getOptions().mergeWith(valueNode.getOptions());
		xmpParent.setValue(valueNode.getValue());
		
		xmpParent.removeChildren();
		for (Iterator it = valueNode.iterateChildren(); it.hasNext();)
		{
			XMPNode child = (XMPNode) it.next();
			xmpParent.addChild(child);
		}
	}		

	
	/**
	 * Checks if the node is a white space.
	 * @param node an XML-node
	 * @return Returns whether the node is a whitespace node, 
	 * 		i.e. a text node that contains only whitespaces.
	 */
	private static boolean isWhitespaceNode(Node node)
	{
		if (node.getNodeType() != Node.TEXT_NODE)
		{
			return false;
		}
		
		String value = node.getNodeValue();
		for (int i = 0; i < value.length(); i++)
		{
			if (!Character.isWhitespace(value.charAt(i)))
			{
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * 7.2.6 propertyElementURIs
	 *			anyURI - ( coreSyntaxTerms | rdf:Description | oldTerms )
	 * 
	 * @param term the term id
	 * @return Return true if the term is a property element name.
	 */
	private static boolean isPropertyElementName(int term)
	{
		if (term == RDFTERM_DESCRIPTION  ||  isOldTerm(term))
		{
			return false;
		}
		else
		{	
			return (!isCoreSyntaxTerm(term));
		}	
	}

	
	/**
	 * 7.2.4 oldTerms<br>
	 * rdf:aboutEach | rdf:aboutEachPrefix | rdf:bagID
	 * 
	 * @param term the term id
	 * @return Returns true if the term is an old term.
	 */
	private static boolean isOldTerm(int term)
	{
		return  RDFTERM_FIRST_OLD <= term  &&  term <= RDFTERM_LAST_OLD;
	}


	/**
	 * 7.2.2 coreSyntaxTerms<br>
	 * rdf:RDF | rdf:ID | rdf:about | rdf:parseType | rdf:resource | rdf:nodeID |
	 * rdf:datatype
	 * 
	 * @param term the term id
	 * @return Return true if the term is a core syntax term
	 */
	private static boolean isCoreSyntaxTerm(int term)
	{
		return  RDFTERM_FIRST_CORE <= term  &&  term <= RDFTERM_LAST_CORE;
	}


	/**
	 * Determines the ID for a certain RDF Term.
	 * Arranged to hopefully minimize the parse time for large XMP.
	 * 
	 * @param node an XML node 
	 * @return Returns the term ID.
	 */
	private static int getRDFTermKind(Node node)
	{
		String localName = node.getLocalName();
		String namespace = node.getNamespaceURI();
		
		if (
				namespace == null  && 
				("about".equals(localName) || "ID".equals(localName))  &&
				(node instanceof Attr)  &&
				NS_RDF.equals(((Attr) node).getOwnerElement().getNamespaceURI())
		   )
		{
			namespace = NS_RDF; 
		}
		
		if (NS_RDF.equals(namespace))
		{
			if ("li".equals(localName))
			{
				return RDFTERM_LI;
			}
			else if ("parseType".equals(localName))
			{
				return RDFTERM_PARSE_TYPE;
			}
			else if ("Description".equals(localName))
			{
				return RDFTERM_DESCRIPTION;
			}
			else if ("about".equals(localName))
			{
				return RDFTERM_ABOUT;
			}
			else if ("resource".equals(localName))
			{
				return RDFTERM_RESOURCE;
			}
			else if ("RDF".equals(localName))
			{
				return RDFTERM_RDF;
			}
			else if ("ID".equals(localName))
			{
				return RDFTERM_ID;
			}
			else if ("nodeID".equals(localName))
			{
				return RDFTERM_NODE_ID;
			}
			else if ("datatype".equals(localName))
			{
				return RDFTERM_DATATYPE;
			}
			else if ("aboutEach".equals(localName))
			{
				return RDFTERM_ABOUT_EACH;
			}
			else if ("aboutEachPrefix".equals(localName))
			{
				return RDFTERM_ABOUT_EACH_PREFIX;
			}
			else if ("bagID".equals(localName))
			{
				return RDFTERM_BAG_ID;
			}
		}
		
		return RDFTERM_OTHER;
	}
}
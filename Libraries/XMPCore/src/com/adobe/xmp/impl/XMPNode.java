//=================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPError;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.options.PropertyOptions;


/**
 * A node in the internally XMP tree, which can be a schema node, a property node, an array node,
 * an array item, a struct node or a qualifier node (without '?').
 * 
 * Possible improvements:
 * 
 * 1. The kind Node of node might be better represented by a class-hierarchy of different nodes.
 * 2. The array type should be an enum
 * 3. isImplicitNode should be removed completely and replaced by return values of fi.
 * 4. hasLanguage, hasType should be automatically maintained by XMPNode
 * 
 * @since 21.02.2006
 */
class XMPNode implements Comparable
{
	/** name of the node, contains different information depending of the node kind */
	private String name;
	/** value of the node, contains different information depending of the node kind */
	private String value;
	/** link to the parent node */
	private XMPNode parent;
	/** list of child nodes, lazy initialized */
	private List children = null; 
	/** list of qualifier of the node, lazy initialized */
	private List qualifier = null;
	/** options describing the kind of the node */
	private PropertyOptions options = null;
	
	// internal processing options
	
	/** flag if the node is implicitly created */
	private boolean implicit;
	/** flag if the node has aliases */
	private boolean hasAliases;
	/** flag if the node is an alias */
	private boolean alias;
	/** flag if the node has an "rdf:value" child node. */
	private boolean hasValueChild;
	
	
	
	/**
	 * Creates an <code>XMPNode</code> with initial values.
	 * 
	 * @param name the name of the node
	 * @param value the value of the node
	 * @param options the options of the node
	 */
	public XMPNode(String name, String value, PropertyOptions options)
	{
		this.name = name;
		this.value = value;
		this.options = options;
	}

	
	/**
	 * Constructor for the node without value.
	 * 
	 * @param name the name of the node
	 * @param options the options of the node
	 */
	public XMPNode(String name, PropertyOptions options)
	{
		this(name, null, options);
	}
	

	/**
	 * Resets the node.
	 */
	public void clear()
	{
		options = null;
		name = null;
		value = null;
		children = null;
		qualifier = null;
	}

	
	/**
	 * @return Returns the parent node.
	 */
	public XMPNode getParent()
	{
		return parent;
	}

	
	/**
	 * @param index an index [1..size]
	 * @return Returns the child with the requested index.
	 */
	public XMPNode getChild(int index)
	{
		return (XMPNode) getChildren().get(index - 1);
	}
	
	
	/**
	 * Adds a node as child to this node.
	 * @param node an XMPNode
	 * @throws XMPException 
	 */
	public void addChild(XMPNode node) throws XMPException
	{
		// check for duplicate properties
		assertChildNotExisting(node.getName());
		node.setParent(this);
		getChildren().add(node);
	}

	
	/**
	 * Adds a node as child to this node.
	 * @param index the index of the node <em>before</em> which the new one is inserted.
	 * <em>Note:</em> The node children are indexed from [1..size]! 
	 * An index of size + 1 appends a node.   
	 * @param node an XMPNode
	 * @throws XMPException 
	 */
	public void addChild(int index, XMPNode node) throws XMPException
	{
		assertChildNotExisting(node.getName());
		node.setParent(this);
		getChildren().add(index - 1, node);
	}

	
	/**
	 * Replaces a node with another one.
	 * @param index the index of the node that will be replaced.
	 * <em>Note:</em> The node children are indexed from [1..size]! 
	 * @param node the replacement XMPNode
	 */
	public void replaceChild(int index, XMPNode node)
	{
		node.setParent(this);
		getChildren().set(index - 1, node);
	}
	
	
	/**
	 * Removes a child at the requested index.
	 * @param itemIndex the index to remove [1..size] 
	 */
	public void removeChild(int itemIndex)
	{
		getChildren().remove(itemIndex - 1);
		cleanupChildren();
	}
	
	
	/**
	 * Removes a child node.
	 * If its a schema node and doesn't have any children anymore, its deleted.
	 * 
	 * @param node the child node to delete.
	 */
	public void removeChild(XMPNode node)
	{
		getChildren().remove(node);
		cleanupChildren();
	}


	/**
	 * Removes the children list if this node has no children anymore;
	 * checks if the provided node is a schema node and doesn't have any children anymore, 
	 * its deleted.
	 */
	protected void cleanupChildren()
	{
		if (children.isEmpty())
		{
			children = null;
		}
	}

	
	/**
	 * Removes all children from the node. 
	 */ 
	public void removeChildren()
	{
		children = null;
	}

	
	/**
	 * @return Returns the number of children without neccessarily creating a list.
	 */
	public int getChildrenLength()
	{
		return children != null ?
			children.size() :	
			0;
	}

	
	/**
	 * @param expr child node name to look for
	 * @return Returns an <code>XMPNode</code> if node has been found, <code>null</code> otherwise. 
	 */
	public XMPNode findChildByName(String expr)
	{
		return find(getChildren(), expr);
	}

	
	/**
	 * @param index an index [1..size]
	 * @return Returns the qualifier with the requested index.
	 */
	public XMPNode getQualifier(int index)
	{
		return (XMPNode) getQualifier().get(index - 1);
	}
	
	
	/**
	 * @return Returns the number of qualifier without neccessarily creating a list.
	 */
	public int getQualifierLength()
	{
		return qualifier != null ?
			qualifier.size() :	
			0;
	}
	
	
	/**
	 * Appends a qualifier to the qualifier list and sets respective options.
	 * @param qualNode a qualifier node.
	 * @throws XMPException 
	 */
	public void addQualifier(XMPNode qualNode) throws XMPException
	{
		assertQualifierNotExisting(qualNode.getName());
		qualNode.setParent(this);
		qualNode.getOptions().setQualifier(true);
		getOptions().setHasQualifiers(true);
		
		// contraints
		if (qualNode.isLanguageNode())
		{
			// "xml:lang" is always first and the option "hasLanguage" is set
			options.setHasLanguage(true);
			getQualifier().add(0, qualNode);
		}
		else if (qualNode.isTypeNode())
		{
			// "rdf:type" must be first or second after "xml:lang" and the option "hasType" is set
			options.setHasType(true);
			getQualifier().add(
				!options.getHasLanguage() ? 0 : 1,	
				qualNode);
		}
		else
		{
			// other qualifiers are appended
			getQualifier().add(qualNode);
		}	
	}

	
	/**
	 * Removes one qualifier node and fixes the options.
	 * @param qualNode qualifier to remove
	 */
	public void removeQualifier(XMPNode qualNode)
	{
		PropertyOptions opts = getOptions();
		if (qualNode.isLanguageNode())
		{
			// if "xml:lang" is removed, remove hasLanguage-flag too
			opts.setHasLanguage(false);
		}
		else if (qualNode.isTypeNode())
		{
			// if "rdf:type" is removed, remove hasType-flag too
			opts.setHasType(false);
		}
		
		getQualifier().remove(qualNode);
		if (qualifier.isEmpty())
		{
			opts.setHasQualifiers(false);
			qualifier = null;
		}
		
	}

	
	/**
	 * Removes all qualifiers from the node and sets the options appropriate. 
	 */ 
	public void removeQualifiers()
	{
		PropertyOptions opts = getOptions();
		// clear qualifier related options
		opts.setHasQualifiers(false);
		opts.setHasLanguage(false);
		opts.setHasType(false);
		qualifier = null;
	}


	/**
	 * @param expr qualifier node name to look for
	 * @return Returns a qualifier <code>XMPNode</code> if node has been found, 
	 * <code>null</code> otherwise. 
	 */
	public XMPNode findQualifierByName(String expr)
	{
		return find(qualifier, expr);
	}
	

	/**
	 * @return Returns whether the node has children.
	 */
	public boolean hasChildren()
	{
		return children != null  &&  children.size() > 0;
	}	
	

	/**
	 * @return Returns an iterator for the children.
	 * <em>Note:</em> take care to use it.remove(), as the flag are not adjusted in that case.
	 */
	public Iterator iterateChildren()
	{
		if (children != null)
		{
			return getChildren().iterator();
		}
		else
		{
			return Collections.EMPTY_LIST.listIterator();
		}
	}
	
	
	/**
	 * @return Returns whether the node has qualifier attached.
	 */
	public boolean hasQualifier()
	{
		return qualifier != null  &&  qualifier.size() > 0;
	}
	
	
	/**
	 * @return Returns an iterator for the qualifier.
	 * <em>Note:</em> take care to use it.remove(), as the flag are not adjusted in that case.
	 */
	public Iterator iterateQualifier()
	{
		if (qualifier != null)
		{
			final Iterator it = getQualifier().iterator();
			
			return new Iterator()
			{
				public boolean hasNext()
				{
					return it.hasNext();
				}

				public Object next()
				{
					return it.next();
				}

				public void remove()
				{
					throw new UnsupportedOperationException(
							"remove() is not allowed due to the internal contraints");
				}
				
			};
		}
		else
		{
			return Collections.EMPTY_LIST.iterator();
		}
	}
	
	
	/**
	 * Performs a <b>deep clone</b> of the node and the complete subtree.
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		PropertyOptions newOptions;
		try
		{
			newOptions = new PropertyOptions(getOptions().getOptions());
		}
		catch (XMPException e)
		{
			// cannot happen
			newOptions = new PropertyOptions();
		}
		
		XMPNode newNode = new XMPNode(name, value, newOptions);
		cloneSubtree(newNode);
		
		return newNode;
	}
	
	
	/**
	 * Performs a <b>deep clone</b> of the complete subtree (children and
	 * qualifier )into and add it to the destination node.
	 * 
	 * @param destination the node to add the cloned subtree
	 */
	public void cloneSubtree(XMPNode destination)
	{
		try
		{
			for (Iterator it = iterateChildren(); it.hasNext();)
			{
				XMPNode child = (XMPNode) it.next();
				destination.addChild((XMPNode) child.clone());
			}
			
			for (Iterator it = iterateQualifier(); it.hasNext();)
			{
				XMPNode qualifier = (XMPNode) it.next();
				destination.addQualifier((XMPNode) qualifier.clone());
			}
		}
		catch (XMPException e)
		{
			// cannot happen (duplicate childs/quals do not exist in this node)
			assert false;
		}
		
	}	
	
	
	/** 
	 * Renders this node and the tree unter this node in a human readable form.
	 * @param recursive Flag is qualifier and child nodes shall be rendered too
	 * @return Returns a multiline string containing the dump.
	 */
	public String dumpNode(boolean recursive)
	{
		StringBuffer result = new StringBuffer(512);
		this.dumpNode(result, recursive, 0, 0);
		return result.toString();
	}
	
	
	/**
	 * @see Comparable#compareTo(Object) 
	 */
	public int compareTo(Object xmpNode)
	{
		if (getOptions().isSchemaNode())
		{
			return this.value.compareTo(((XMPNode) xmpNode).getValue());
		}
		else
		{
			return this.name.compareTo(((XMPNode) xmpNode).getName());
		}	
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
	 * @return Returns the value.
	 */
	public String getValue()
	{
		return value;
	}


	/**
	 * @param value The value to set.
	 */
	public void setValue(String value)
	{
		this.value = value;
	}	

	
	/**
	 * @return Returns the options.
	 */
	public PropertyOptions getOptions()
	{
		if (options == null)
		{
			options = new PropertyOptions();
		}
		return options;
	}

	
	/**
	 * Updates the options of the node.
	 * @param options the options to set.
	 */
	public void setOptions(PropertyOptions options)
	{
		this.options = options;
	}

	
	/**
	 * @return Returns the implicit flag
	 */
	public boolean isImplicit()
	{
		return implicit;
	}


	/**
	 * @param implicit Sets the implicit node flag
	 */
	public void setImplicit(boolean implicit)
	{
		this.implicit = implicit;
	}
	
	
	/**
	 * @return Returns if the node contains aliases (applies only to schema nodes)
	 */
	public boolean getHasAliases()
	{
		return hasAliases;
	}


	/**
	 * @param hasAliases sets the flag that the node contains aliases
	 */
	public void setHasAliases(boolean hasAliases)
	{
		this.hasAliases = hasAliases;
	}	
	
	
	/**
	 * @return Returns if the node contains aliases (applies only to schema nodes)
	 */
	public boolean isAlias()
	{
		return alias;
	}


	/**
	 * @param alias sets the flag that the node is an alias
	 */
	public void setAlias(boolean alias)
	{
		this.alias = alias;
	}	
	
	
	/**
	 * @return the hasValueChild
	 */
	public boolean getHasValueChild()
	{
		return hasValueChild;
	}


	/**
	 * @param hasValueChild the hasValueChild to set
	 */
	public void setHasValueChild(boolean hasValueChild)
	{
		this.hasValueChild = hasValueChild;
	}
	
	
	
	/**
	 * Sorts the complete datamodel according to the following rules:
	 * <ul>
	 * 		<li>Nodes at one level are sorted by name, that is prefix + local name
	 * 		<li>Starting at the root node the children and qualifier are sorted recursively, 
	 * 			which the following exceptions.
	 * 		<li>Sorting will not be used for arrays.
	 * 		<li>Within qualifier "xml:lang" and/or "rdf:type" stay at the top in that order, 
	 * 			all others are sorted.  
	 * </ul>
	 */
	public void sort()
	{
		// sort qualifier
		if (hasQualifier())
		{
			XMPNode[] quals = (XMPNode[]) getQualifier()
				.toArray(new XMPNode[getQualifierLength()]);
			int sortFrom = 0;
			while (
					quals.length > sortFrom  &&
					(XMPConst.XML_LANG.equals(quals[sortFrom].getName())  ||
					 "rdf:type".equals(quals[sortFrom].getName()))
				  )		 
			{
				quals[sortFrom].sort();
				sortFrom++;
			}

			Arrays.sort(quals, sortFrom, quals.length);
			ListIterator it = qualifier.listIterator();
			for (int j = 0; j < quals.length; j++)
			{
				it.next();
				it.set(quals[j]);
				quals[j].sort();
			}
		}
		
		// sort children
		if (hasChildren())
		{	
			if (!getOptions().isArray())
			{
				Collections.sort(children);
			}
			for (Iterator it = iterateChildren(); it.hasNext();)
			{
				((XMPNode) it.next()).sort();
				
			}
		}
	}	
	
	
	
	//------------------------------------------------------------------------------ private methods

	
	/**
	 * Dumps this node and its qualifier and children recursively.
	 * <em>Note:</em> It creats empty options on every node.
	 *  
	 * @param result the buffer to append the dump.
	 * @param recursive Flag is qualifier and child nodes shall be rendered too
	 * @param indent the current indent level.
	 * @param index the index within the parent node (important for arrays) 
	 */
	private void dumpNode(StringBuffer result, boolean recursive, int indent, int index)
	{
		// write indent
		for (int i = 0; i < indent; i++)
		{
			result.append('\t');
		}
		
		// render Node
		if (parent != null)
		{
			if (getOptions().isQualifier())
			{
				result.append('?');
				result.append(name);
			}
			else if (getParent().getOptions().isArray())
			{
				result.append('[');
				result.append(index);
				result.append(']');
			}
			else
			{
				result.append(name);
			}
		}
		else
		{
			// applies only to the root node
			result.append("ROOT NODE");
			if (name != null  &&  name.length() > 0)
			{
				// the "about" attribute
				result.append(" (");
				result.append(name);
				result.append(')');
			}	
		}
		
		if (value != null  &&  value.length() > 0)
		{
			result.append(" = \"");
			result.append(value);
			result.append('"');
		}
		
		// render options if at least one is set
		if (getOptions().containsOneOf(0xffffffff))
		{
			result.append("\t(");
			result.append(getOptions().toString());
			result.append(" : ");
			result.append(getOptions().getOptionsString());
			result.append(')');
		}
		
		result.append('\n');
		
		// render qualifier
		if (recursive  &&  hasQualifier())
		{
			XMPNode[] quals = (XMPNode[]) getQualifier()
				.toArray(new XMPNode[getQualifierLength()]);
			int i = 0;
			while (quals.length > i  &&
					(XMPConst.XML_LANG.equals(quals[i].getName())  ||
					 "rdf:type".equals(quals[i].getName()))
				  )		 
			{
				i++;
			}
			Arrays.sort(quals, i, quals.length);
			for (i = 0; i < quals.length; i++)
			{
				XMPNode qualifier = quals[i];
				qualifier.dumpNode(result, recursive, indent + 2, i + 1);
			}
		}
		
		// render children
		if (recursive  &&  hasChildren())
		{	
			XMPNode[] children = (XMPNode[]) getChildren()
				.toArray(new XMPNode[getChildrenLength()]);
			if (!getOptions().isArray())
			{	
				Arrays.sort(children);
			}
			for (int i = 0; i < children.length; i++)
			{
				XMPNode child = children[i];
				child.dumpNode(result, recursive, indent + 1, i + 1);
			}
		}
	}
	
	
	/**
	 * @return Returns whether this node is a language qualifier. 
	 */
	private boolean isLanguageNode()
	{
		return XMPConst.XML_LANG.equals(name);
	}

	
	/**
	 * @return Returns whether this node is a type qualifier. 
	 */
	private boolean isTypeNode()
	{
		return "rdf:type".equals(name);
	}
	

	/**
	 * <em>Note:</em> This method should always be called when accessing 'children' to be sure
	 * that its initialized.
	 * @return Returns list of children that is lazy initialized.
	 */
	private List getChildren()
	{
		if (children == null)
		{
			children = new ArrayList(0);
		}
		return children;
	}

	
	/**
	 * @return Returns a read-only copy of child nodes list.
	 */
	public List getUnmodifiableChildren()
	{
		return Collections.unmodifiableList(new ArrayList(getChildren()));
	}
	
	
	/**
	 * @return Returns list of qualifier that is lazy initialized.
	 */
	private List getQualifier()
	{
		if (qualifier == null)
		{
			qualifier = new ArrayList(0);
		}
		return qualifier;
	}
	
	
	/**
	 * Sets the parent node, this is solely done by <code>addChild(...)</code>
	 * and <code>addQualifier()</code>.
	 * 
	 * @param parent
	 *            Sets the parent node.
	 */
	protected void setParent(XMPNode parent)
	{
		this.parent = parent;
	}

	
	/**
	 * Internal find.
	 * @param list the list to search in
	 * @param expr the search expression
	 * @return Returns the found node or <code>nulls</code>.
	 */
	private XMPNode find(List list, String expr)
	{
		
		if (list != null)
		{	
			for (Iterator it = list.iterator(); it.hasNext();)
			{
				XMPNode child = (XMPNode) it.next();
				if (child.getName().equals(expr))
				{
					return child;
				}
			}
		}	
		return null;
	}
	
	
	/**
	 * Checks that a node name is not existing on the same level, except for array items.
	 * @param childName the node name to check
	 * @throws XMPException Thrown if a node with the same name is existing.
	 */
	private void assertChildNotExisting(String childName) throws XMPException
	{
		if (!XMPConst.ARRAY_ITEM_NAME.equals(childName)  &&
			findChildByName(childName) != null)
		{
			throw new XMPException("Duplicate property or field node '" + childName + "'",
					XMPError.BADXMP);
		}
	}
	
	
	/**
	 * Checks that a qualifier name is not existing on the same level.
	 * @param qualifierName the new qualifier name
	 * @throws XMPException Thrown if a node with the same name is existing.
	 */
	private void assertQualifierNotExisting(String qualifierName) throws XMPException
	{
		if (!XMPConst.ARRAY_ITEM_NAME.equals(qualifierName)  &&
			findQualifierByName(qualifierName) != null)
		{
			throw new XMPException("Duplicate '" + qualifierName + "' qualifier", XMPError.BADXMP);
		}
	}
}
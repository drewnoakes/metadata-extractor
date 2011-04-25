// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.adobe.xmp.XMPError;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.impl.xpath.XMPPath;
import com.adobe.xmp.impl.xpath.XMPPathParser;
import com.adobe.xmp.options.IteratorOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPPropertyInfo;


/**
 * The <code>XMPIterator</code> implementation.
 * Iterates the XMP Tree according to a set of options.
 * During the iteration the XMPMeta-object must not be changed.
 * Calls to <code>skipSubtree()</code> / <code>skipSiblings()</code> will affect the iteration.
 *  
 * @since   29.06.2006
 */
public class XMPIteratorImpl implements XMPIterator
{
	/** stores the iterator options */
	private IteratorOptions options;
	/** the base namespace of the property path, will be changed during the iteration */ 
	private String baseNS = null;
	/** flag to indicate that skipSiblings() has been called. */
	protected boolean skipSiblings = false;
	/** flag to indicate that skipSiblings() has been called. */
	protected boolean skipSubtree = false;
	/** the node iterator doing the work */
	private Iterator nodeIterator = null;
	
	
	/**
	 * Constructor with optionsl initial values. If <code>propName</code> is provided, 
	 * <code>schemaNS</code> has also be provided.
	 * @param xmp the iterated metadata object.
	 * @param schemaNS the iteration is reduced to this schema (optional) 
	 * @param propPath the iteration is redurce to this property within the <code>schemaNS</code>
	 * @param options advanced iteration options, see {@link IteratorOptions}
	 * @throws XMPException If the node defined by the paramters is not existing. 
	 */
	public XMPIteratorImpl(XMPMetaImpl xmp, String schemaNS, String propPath,
			IteratorOptions options) throws XMPException
	{
		// make sure that options is defined at least with defaults
		this.options = options != null ? options : new IteratorOptions();
		
		// the start node of the iteration depending on the schema and property filter
		XMPNode startNode = null;
		String initialPath = null;
		boolean baseSchema = schemaNS != null  &&  schemaNS.length() > 0; 
		boolean baseProperty = propPath != null  &&  propPath.length() > 0; 
		
		if (!baseSchema  &&  !baseProperty)
		{
			// complete tree will be iterated
			startNode = xmp.getRoot();
		}
		else if (baseSchema  &&  baseProperty)
		{
			// Schema and property node provided
			XMPPath path = XMPPathParser.expandXPath(schemaNS, propPath);
			
			// base path is the prop path without the property leaf
			XMPPath basePath = new XMPPath();
			for (int i = 0; i < path.size() - 1; i++)
			{
				basePath.add(path.getSegment(i));
			}
			
			startNode = XMPNodeUtils.findNode(xmp.getRoot(), path, false, null);
			baseNS = schemaNS;
			initialPath = basePath.toString();
		}
		else if (baseSchema  &&  !baseProperty)
		{
			// Only Schema provided
			startNode = XMPNodeUtils.findSchemaNode(xmp.getRoot(), schemaNS, false);
		}
		else // !baseSchema  &&  baseProperty
		{
			// No schema but property provided -> error
			throw new XMPException("Schema namespace URI is required", XMPError.BADSCHEMA);
		}			

		
		// create iterator
		if (startNode != null)
		{
			if (!this.options.isJustChildren())
			{	
				nodeIterator = new NodeIterator(startNode, initialPath, 1);
			}
			else
			{
				nodeIterator = new NodeIteratorChildren(startNode, initialPath);
			}
		}
		else
		{
			// create null iterator
			nodeIterator = Collections.EMPTY_LIST.iterator();
		}
	}


	/**
	 * @see XMPIterator#skipSubtree()
	 */
	public void skipSubtree()
	{
		this.skipSubtree = true;
	}

	
	/**
	 * @see XMPIterator#skipSiblings()
	 */
	public void skipSiblings()
	{
		skipSubtree();
		this.skipSiblings = true;
	}
	

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext()
	{
		return nodeIterator.hasNext();
	}

	
	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next()
	{
		return nodeIterator.next();
	}

	
	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove()
	{
		throw new UnsupportedOperationException("The XMPIterator does not support remove().");
	}
	
	
	/**
	 * @return Exposes the options for inner class.
	 */
	protected IteratorOptions getOptions()
	{
		return options;
	}

	
	/**
	 * @return Exposes the options for inner class.
	 */
	protected String getBaseNS()
	{
		return baseNS;
	}

	
	/**
	 * @param baseNS sets the baseNS from the inner class.
	 */
	protected void setBaseNS(String baseNS)
	{
		this.baseNS = baseNS;
	}
	
	
	
	
	
	
	/**
	 * The <code>XMPIterator</code> implementation.
	 * It first returns the node itself, then recursivly the children and qualifier of the node.
	 * 
	 * @since   29.06.2006
	 */
	private class NodeIterator implements Iterator
	{
		/** iteration state */
		protected static final int ITERATE_NODE = 0;
		/** iteration state */
		protected static final int ITERATE_CHILDREN = 1;
		/** iteration state */
		protected static final int ITERATE_QUALIFIER = 2;
		
		/** the state of the iteration */
		private int state = ITERATE_NODE; 
		/** the currently visited node */
		private XMPNode visitedNode;
		/** the recursively accumulated path */
		private String path;
		/** the iterator that goes through the children and qualifier list */
		private Iterator childrenIterator = null;
		/** index of node with parent, only interesting for arrays */
		private int index = 0;
		/** the iterator for each child */
		private Iterator subIterator = Collections.EMPTY_LIST.iterator();
		/** the cached <code>PropertyInfo</code> to return */
		private XMPPropertyInfo returnProperty = null;

		
		/**
		 * Default constructor
		 */
		public NodeIterator()
		{
			// EMPTY
		}
		
		
		/**
		 * Constructor for the node iterator.
		 * @param visitedNode the currently visited node
		 * @param parentPath the accumulated path of the node
		 * @param index the index within the parent node (only for arrays)
		 */
		public NodeIterator(XMPNode visitedNode, String parentPath, int index)
		{
			this.visitedNode = visitedNode;
			this.state = NodeIterator.ITERATE_NODE;
			if (visitedNode.getOptions().isSchemaNode())
			{	
				setBaseNS(visitedNode.getName());
			}

			// for all but the root node and schema nodes
			path = accumulatePath(visitedNode, parentPath, index);
		}

		
		/**
		 * Prepares the next node to return if not already done. 
		 * 
		 * @see Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			if (returnProperty != null)
			{
				// hasNext has been called before
				return true;
			}
			
			// find next node
			if (state == ITERATE_NODE)
			{
				return reportNode();
			}
			else if (state == ITERATE_CHILDREN)
			{
				if (childrenIterator == null)
				{
					childrenIterator = visitedNode.iterateChildren();
				}
				
				boolean hasNext = iterateChildren(childrenIterator);
				
				if (!hasNext  &&  visitedNode.hasQualifier()  &&  !getOptions().isOmitQualifiers()) 
				{
					state = ITERATE_QUALIFIER;
					childrenIterator = null;
					hasNext = hasNext();
				}
				return hasNext;
			}
			else
			{
				if (childrenIterator == null)
				{
					childrenIterator = visitedNode.iterateQualifier();
				}
				
				return iterateChildren(childrenIterator);
			}
		}


		/**
		 * Sets the returnProperty as next item or recurses into <code>hasNext()</code>.
		 * @return Returns if there is a next item to return. 
		 */
		protected boolean reportNode()
		{
			state = ITERATE_CHILDREN;
			if (visitedNode.getParent() != null  &&
				(!getOptions().isJustLeafnodes()  ||  !visitedNode.hasChildren()))
			{	
				returnProperty = createPropertyInfo(visitedNode, getBaseNS(), path);
				return true;
			}
			else
			{
				return hasNext();
			}
		}


		/**
		 * Handles the iteration of the children or qualfier
		 * @param iterator an iterator
		 * @return Returns if there are more elements available.
		 */
		private boolean iterateChildren(Iterator iterator)
		{
			if (skipSiblings)
			{
				// setSkipSiblings(false);
				skipSiblings = false;
				subIterator = Collections.EMPTY_LIST.iterator();
			}
			
			// create sub iterator for every child,
			// if its the first child visited or the former child is finished 
			if ((!subIterator.hasNext())  &&  iterator.hasNext())
			{
				XMPNode child = (XMPNode) iterator.next();
				index++;
				subIterator = new NodeIterator(child, path, index);
			}
			
			if (subIterator.hasNext())
			{
				returnProperty = (XMPPropertyInfo) subIterator.next();
				return true;
			}
			else
			{
				return false;
			}
		}
	
		
		/**
		 * Calls hasNext() and returnes the prepared node. Afterwards its set to null.
		 * The existance of returnProperty indicates if there is a next node, otherwise
		 * an exceptio is thrown.
		 * 
		 * @see Iterator#next()
		 */
		public Object next()
		{
			if (hasNext())
			{
				XMPPropertyInfo result = returnProperty; 
				returnProperty = null;
				return result;
			}
			else
			{
				throw new NoSuchElementException("There are no more nodes to return");
			}
		}
	
		
		/**
		 * Not supported.
		 * @see Iterator#remove()
		 */
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
		
		/**
		 * @param currNode the node that will be added to the path.
		 * @param parentPath the path up to this node.
		 * @param currentIndex the current array index if an arrey is traversed
		 * @return Returns the updated path.
		 */
		protected String accumulatePath(XMPNode currNode, String parentPath, int currentIndex)
		{
			String separator;
			String segmentName;
			if (currNode.getParent() == null  ||  currNode.getOptions().isSchemaNode())
			{
				return null;
			}
			else if (currNode.getParent().getOptions().isArray())
			{
				separator = "";
				segmentName = "[" + String.valueOf(currentIndex) + "]";
			}
			else
			{	
				separator = "/";
				segmentName = currNode.getName();
			}
			
			
			if (parentPath == null  ||  parentPath.length() == 0)
			{
				return segmentName;
			}
			else if (getOptions().isJustLeafname())
			{
				return !segmentName.startsWith("?") ? 
					segmentName :
					segmentName.substring(1); // qualifier
			}
			else 
			{
				return parentPath + separator + segmentName;
			}
		}
			
		
		/**
		 * Creates a property info object from an <code>XMPNode</code>.
		 * @param node an <code>XMPNode</code>
		 * @param baseNS the base namespace to report
		 * @param path the full property path
		 * @return Returns a <code>XMPProperty</code>-object that serves representation of the node.
		 */
		protected XMPPropertyInfo createPropertyInfo(final XMPNode node, final String baseNS,
				final String path)
		{
			final Object value = node.getOptions().isSchemaNode() ? null : node.getValue();
			
			return new XMPPropertyInfo()
			{
				public String getNamespace()
				{
					return baseNS;
				}
	
				public String getPath()
				{
					return path;
				}
				
				public Object getValue()
				{
					return value;
				}
	
				public PropertyOptions getOptions()
				{
					return node.getOptions();
				}

				public String getLanguage()
				{
					// the language is not reported
					return null;
				}
			};
		}


		/**
		 * @return the childrenIterator
		 */
		protected  Iterator getChildrenIterator()
		{
			return childrenIterator;
		}


		/**
		 * @param childrenIterator the childrenIterator to set
		 */
		protected void setChildrenIterator(Iterator childrenIterator)
		{
			this.childrenIterator = childrenIterator;
		}

		
		/**
		 * @return Returns the returnProperty.
		 */
		protected XMPPropertyInfo getReturnProperty()
		{
			return returnProperty;
		}
		

		/**
		 * @param returnProperty the returnProperty to set
		 */
		protected  void setReturnProperty(XMPPropertyInfo returnProperty)
		{
			this.returnProperty = returnProperty;
		}
	}
	
	
	/**
	 * This iterator is derived from the default <code>NodeIterator</code>,
	 * and is only used for the option {@link IteratorOptions#JUST_CHILDREN}.
	 * 
	 * @since 02.10.2006
	 */
	private class NodeIteratorChildren extends NodeIterator
	{
		/** */
		private String parentPath;
		/** */
		private Iterator childrenIterator;
		/** */
		private int index = 0;
		
		
		/**
		 * Constructor 
		 * @param parentNode the node which children shall be iterated. 
		 * @param parentPath the full path of the former node without the leaf node.
		 */
		public NodeIteratorChildren(XMPNode parentNode, String parentPath)
		{
			if (parentNode.getOptions().isSchemaNode())
			{	
				setBaseNS(parentNode.getName());
			}
			this.parentPath = accumulatePath(parentNode, parentPath, 1);

			childrenIterator = parentNode.iterateChildren();
		}
		
		
		/**
		 * Prepares the next node to return if not already done. 
		 * 
		 * @see Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			if (getReturnProperty() != null)
			{
				// hasNext has been called before
				return true;
			}
			else if (skipSiblings)
			{
				return false;
			}
			else if (childrenIterator.hasNext())
			{
				XMPNode child = (XMPNode) childrenIterator.next();
				index++;
				
				String path = null;
				if (child.getOptions().isSchemaNode())
				{	
					setBaseNS(child.getName());
				}
				else if (child.getParent() != null)
				{
					// for all but the root node and schema nodes
					path = accumulatePath(child, parentPath, index);
				}

				// report next property, skip not-leaf nodes in case options is set
				if (!getOptions().isJustLeafnodes()  ||  !child.hasChildren())
				{	
					setReturnProperty(createPropertyInfo(child, getBaseNS(), path));
					return true;
				}
				else
				{
					return hasNext();
				}
			}
			else
			{
				return false;
			}
		}		
	}
}
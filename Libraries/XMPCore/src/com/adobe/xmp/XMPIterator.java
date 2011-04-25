// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp;

import java.util.Iterator;


/**
 * Interface for the <code>XMPMeta</code> iteration services.
 * <code>XMPIterator</code> provides a uniform means to iterate over the
 * schema and properties within an XMP object.
 * <p>
 * The iteration over the schema and properties within an XMP object is very
 * complex. It is helpful to have a thorough understanding of the XMP data tree.
 * One way to learn this is to create some complex XMP and examine the output of
 * <code>XMPMeta#toString</code>. This is also described in the XMP
 * Specification, in the XMP Data Model chapter.
 * <p>
 * The top of the XMP data tree is a single root node. This does not explicitly
 * appear in the dump and is never visited by an iterator (that is, it is never
 * returned from <code>XMPIterator#next()</code>). Beneath the root are
 * schema nodes. These are just collectors for top level properties in the same
 * namespace. They are created and destroyed implicitly. Beneath the schema
 * nodes are the property nodes. The nodes below a property node depend on its
 * type (simple, struct, or array) and whether it has qualifiers.
 * <p>
 * An <code>XMPIterator</code> is created by XMPMeta#interator() constructor
 * defines a starting point for the iteration and options that control how it
 * proceeds. By default the iteration starts at the root and visits all nodes
 * beneath it in a depth first manner. The root node is not visited, the first
 * visited node is a schema node. You can provide a schema name or property path
 * to select a different starting node. By default this visits the named root
 * node first then all nodes beneath it in a depth first manner.
 * <p>
 * The <code>XMPIterator#next()</code> method delivers the schema URI, path,
 * and option flags for the node being visited. If the node is simple it also
 * delivers the value. Qualifiers for this node are visited next. The fields of
 * a struct or items of an array are visited after the qualifiers of the parent.
 * <p>
 * The options to control the iteration are:
 * <ul>
 * <li>JUST_CHILDREN - Visit just the immediate children of the root. Skip
 * the root itself and all nodes below the immediate children. This omits the
 * qualifiers of the immediate children, the qualifier nodes being below what
 * they qualify, default is to visit the complete subtree.
 * <li>UST_LEAFNODES - Visit just the leaf property nodes and their
 * qualifiers.
 * <li>JUST_LEAFNAME - Return just the leaf component of the node names.
 * The default is to return the full xmp path.
 * <li>OMIT_QUALIFIERS - Do not visit the qualifiers.
 * <li>INCLUDE_ALIASES - Adds known alias properties to the properties in the iteration.
 * 		<em>Note:</em> Not supported in Java XMPCore! 
 * </ul>
 * <p>
 * <code>next()</code> returns <code>XMPPropertyInfo</code>-objects and throws
 * a <code>NoSuchElementException</code> if there are no more properties to
 * return.
 * 
 * @since 25.01.2006
 */
public interface XMPIterator extends Iterator
{
	/**
	 * Skip the subtree below the current node when <code>next()</code> is
	 * called.
	 */
	void skipSubtree();


	/**
	 * Skip the subtree below and remaining siblings of the current node when
	 * <code>next()</code> is called.
	 */
	void skipSiblings();
}
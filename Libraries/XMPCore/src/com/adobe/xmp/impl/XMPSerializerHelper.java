// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.options.SerializeOptions;


/**
 * Serializes the <code>XMPMeta</code>-object to an <code>OutputStream</code> according to the
 * <code>SerializeOptions</code>. 
 * 
 * @since   11.07.2006
 */
public class XMPSerializerHelper
{
	/**
	 * Static method to serialize the metadata object. For each serialisation, a new XMPSerializer
	 * instance is created, either XMPSerializerRDF or XMPSerializerPlain so thats its possible to 
	 * serialialize the same XMPMeta objects in two threads.
	 * 
	 * @param xmp a metadata implementation object
	 * @param out the output stream to serialize to
	 * @param options serialization options, can be <code>null</code> for default.
	 * @throws XMPException
	 */
	public static void serialize(XMPMetaImpl xmp, OutputStream out, 
		SerializeOptions options)
		throws XMPException
	{
		options = options != null ? options : new SerializeOptions();		
		
		// sort the internal data model on demand
		if (options.getSort())
		{
			xmp.sort();
		}
		new XMPSerializerRDF().serialize(xmp, out, options);
	}		
	

	/**
	 * Serializes an <code>XMPMeta</code>-object as RDF into a string.
	 * <em>Note:</em> Encoding is forced to UTF-16 when serializing to a
	 * string to ensure the correctness of &quot;exact packet size&quot;.
	 * 
	 * @param xmp a metadata implementation object
	 * @param options Options to control the serialization (see
	 *            {@link SerializeOptions}).
	 * @return Returns a string containing the serialized RDF.
	 * @throws XMPException on serializsation errors.
	 */
	public static String serializeToString(XMPMetaImpl xmp, SerializeOptions options)
		throws XMPException
	{
		// forces the encoding to be UTF-16 to get the correct string length
		options = options != null ? options : new SerializeOptions();		
		options.setEncodeUTF16BE(true);

		ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
		serialize(xmp, out, options);

		try
		{
			return out.toString(options.getEncoding());
		}
		catch (UnsupportedEncodingException e)
		{
			// cannot happen as UTF-8/16LE/BE is required to be implemented in
			// Java
			return out.toString();
		}
	}
	
	
	/**
	 * Serializes an <code>XMPMeta</code>-object as RDF into a byte buffer.
	 * 
	 * @param xmp a metadata implementation object
	 * @param options Options to control the serialization (see {@link SerializeOptions}).
	 * @return Returns a byte buffer containing the serialized RDF.
	 * @throws XMPException on serializsation errors.
	 */
	public static byte[] serializeToBuffer(XMPMetaImpl xmp, SerializeOptions options)
			throws XMPException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
		serialize(xmp, out, options);
		return out.toByteArray();
	}
}
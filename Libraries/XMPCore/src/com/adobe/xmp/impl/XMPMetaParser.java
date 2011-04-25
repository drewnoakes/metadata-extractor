// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.adobe.xmp.XMPConst;
import com.adobe.xmp.XMPError;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.options.ParseOptions;


/**
 * This class replaces the <code>ExpatAdapter.cpp</code> and does the
 * XML-parsing and fixes the prefix. After the parsing several normalisations
 * are applied to the XMPTree.
 * 
 * @since 01.02.2006
 */
public class XMPMetaParser
{
	/**  */
	private static final Object XMP_RDF = new Object();
	/** the DOM Parser Factory, options are set */ 
	private static DocumentBuilderFactory factory = createDocumentBuilderFactory();

	/**
	 * Hidden constructor, initialises the SAX parser handler.
	 */
	private XMPMetaParser()
	{
		// EMPTY
	}

	

	/**
	 * Parses the input source into an XMP metadata object, including
	 * de-aliasing and normalisation.
	 * 
	 * @param input the input can be an <code>InputStream</code>, a <code>String</code> or 
	 * 			a byte buffer containing the XMP packet.
	 * @param options the parse options
	 * @return Returns the resulting XMP metadata object
	 * @throws XMPException Thrown if parsing or normalisation fails.
	 */
	public static XMPMeta parse(Object input, ParseOptions options) throws XMPException
	{
		ParameterAsserts.assertNotNull(input);
		options = options != null ? options : new ParseOptions();

		Document document = parseXml(input, options);

		boolean xmpmetaRequired = options.getRequireXMPMeta();
		Object[] result = new Object[3];
		result = findRootNode(document, xmpmetaRequired, result);
		
		if (result != null  &&  result[1] == XMP_RDF)
		{
			XMPMetaImpl xmp = ParseRDF.parse((Node) result[0]);
			xmp.setPacketHeader((String) result[2]);
			
			// Check if the XMP object shall be normalized
			if (!options.getOmitNormalization())
			{
				return XMPNormalizer.process(xmp, options);
			}
			else
			{
				return xmp;
			}
		}
		else
		{
			// no appropriate root node found, return empty metadata object
			return new XMPMetaImpl();
		}
	}

	
	/**
	 * Parses the raw XML metadata packet considering the parsing options.
	 * Latin-1/ISO-8859-1 can be accepted when the input is a byte stream 
	 * (some old toolkits versions such packets). The stream is 
	 * then wrapped in another stream that converts Latin-1 to UTF-8.
	 * <p>
	 * If control characters shall be fixed, a reader is used that fixes the chars to spaces
	 * (if the input is a byte stream is has to be read as character stream).
	 * <p>   
	 * Both options reduce the performance of the parser.
	 *  
	 * @param input the input can be an <code>InputStream</code>, a <code>String</code> or 
	 * 			a byte buffer containing the XMP packet.
	 * @param options the parsing options
	 * @return Returns the parsed XML document or an exception.
	 * @throws XMPException Thrown if the parsing fails for different reasons
	 */
	private static Document parseXml(Object input, ParseOptions options)
			throws XMPException
	{
		if (input instanceof InputStream)
		{
			return parseXmlFromInputStream((InputStream) input, options);
		}
		else if (input instanceof byte[])
		{
			return parseXmlFromBytebuffer(new ByteBuffer((byte[]) input), options);
		} 
		else
		{
			return parseXmlFromString((String) input, options);
		}
	}
	

	/**
	 * Parses XML from an {@link InputStream},
	 * fixing the encoding (Latin-1 to UTF-8) and illegal control character optionally.
	 *  
	 * @param stream an <code>InputStream</code>
	 * @param options the parsing options
	 * @return Returns an XML DOM-Document.
	 * @throws XMPException Thrown when the parsing fails.
	 */
	private static Document parseXmlFromInputStream(InputStream stream, ParseOptions options)
			throws XMPException
	{
		if (!options.getAcceptLatin1()  &&  !options.getFixControlChars())
		{
			return parseInputSource(new InputSource(stream));
		}
		else
		{
			// load stream into bytebuffer
			try
			{
				ByteBuffer buffer = new ByteBuffer(stream);
				return parseXmlFromBytebuffer(buffer, options);
			}
			catch (IOException e)
			{
				throw new XMPException("Error reading the XML-file",
						XMPError.BADSTREAM, e);
			}
		}
	}

	
	/**
	 * Parses XML from a byte buffer, 
	 * fixing the encoding (Latin-1 to UTF-8) and illegal control character optionally.
	 * 
	 * @param buffer a byte buffer containing the XMP packet
	 * @param options the parsing options
	 * @return Returns an XML DOM-Document.
	 * @throws XMPException Thrown when the parsing fails.
	 */
	private static Document parseXmlFromBytebuffer(ByteBuffer buffer, ParseOptions options)
		throws XMPException
	{
		InputSource source = new InputSource(buffer.getByteStream());
		try
		{
			return parseInputSource(source);
		}
		catch (XMPException e)
		{
			if (e.getErrorCode() == XMPError.BADXML  ||
				e.getErrorCode() == XMPError.BADSTREAM)
			{
				if (options.getAcceptLatin1())
				{
					buffer = Latin1Converter.convert(buffer);
				}
				
				if (options.getFixControlChars())
				{
					try
					{
						String encoding = buffer.getEncoding();
						Reader fixReader = new FixASCIIControlsReader(
							new InputStreamReader(
								buffer.getByteStream(), encoding));
						return parseInputSource(new InputSource(fixReader));
					}
					catch (UnsupportedEncodingException e1)
					{
						// can normally not happen as the encoding is provided by a util function
						throw new XMPException("Unsupported Encoding",
								XMPError.INTERNALFAILURE, e);
					}
				}
				source = new InputSource(buffer.getByteStream());
				return parseInputSource(source);
			}
			else
			{
				throw e;
			}	
		}
	}


	/**
	 * Parses XML from a {@link String}, 
	 * fixing the illegal control character optionally.
	 *  
	 * @param input a <code>String</code> containing the XMP packet
	 * @param options the parsing options
	 * @return Returns an XML DOM-Document.
	 * @throws XMPException Thrown when the parsing fails.
	 */
	private static Document parseXmlFromString(String input, ParseOptions options)
			throws XMPException
	{
		InputSource source = new InputSource(new StringReader(input));
		try
		{
			return parseInputSource(source);
		}
		catch (XMPException e)
		{
			if (e.getErrorCode() == XMPError.BADXML  &&  options.getFixControlChars())
			{
				source = new InputSource(new FixASCIIControlsReader(new StringReader(input)));
				return parseInputSource(source);
			}
			else
			{
				throw e;
			}	
		}
	}

	
	/**
	 * Runs the XML-Parser. 
	 * @param source an <code>InputSource</code>
	 * @return Returns an XML DOM-Document.
	 * @throws XMPException Wraps parsing and I/O-exceptions into an XMPException.
	 */
	private static Document parseInputSource(InputSource source) throws XMPException
	{
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(null);
			return builder.parse(source);
		}
		catch (SAXException e)
		{
			throw new XMPException("XML parsing failure", XMPError.BADXML, e);
		}
		catch (ParserConfigurationException e)
		{
			throw new XMPException("XML Parser not correctly configured",
					XMPError.UNKNOWN, e);
		}
		catch (IOException e)
		{
			throw new XMPException("Error reading the XML-file", XMPError.BADSTREAM, e);
		}
	}
	

	/**
	 * Find the XML node that is the root of the XMP data tree. Generally this
	 * will be an outer node, but it could be anywhere if a general XML document
	 * is parsed (e.g. SVG). The XML parser counted all rdf:RDF and
	 * pxmp:XMP_Packet nodes, and kept a pointer to the last one. If there is
	 * more than one possible root use PickBestRoot to choose among them.
	 * <p>
	 * If there is a root node, try to extract the version of the previous XMP
	 * toolkit.
	 * <p>
	 * Pick the first x:xmpmeta among multiple root candidates. If there aren't
	 * any, pick the first bare rdf:RDF if that is allowed. The returned root is
	 * the rdf:RDF child if an x:xmpmeta element was chosen. The search is
	 * breadth first, so a higher level candiate is chosen over a lower level
	 * one that was textually earlier in the serialized XML.
	 * 
	 * @param root the root of the xml document
	 * @param xmpmetaRequired flag if the xmpmeta-tag is still required, might be set 
	 * 		initially to <code>true</code>, if the parse option "REQUIRE_XMP_META" is set
	 * @param result The result array that is filled during the recursive process.
	 * @return Returns an array that contains the result or <code>null</code>. 
	 * 		   The array contains:
	 * <ol>
	 * 		<li>the rdf:RDF-node
	 * 		<li>an object that is either XMP_RDF or XMP_PLAIN
	 * 		<li>a flag that is true if a <?xpacket..> processing instruction has been found
	 * 		<li>the body text of the xpacket-instruction.
	 * </ol>
	 * 
	 */
	private static Object[] findRootNode(Node root, boolean xmpmetaRequired, Object[] result)
	{
		// Look among this parent's content for x:xapmeta or x:xmpmeta.
		// The recursion for x:xmpmeta is broader than the strictly defined choice, 
		// but gives us smaller code.
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			root = children.item(i);
			if (Node.PROCESSING_INSTRUCTION_NODE == root.getNodeType()  &&
				((ProcessingInstruction) root).getTarget() == XMPConst.XMP_PI)
			{
				// Store the processing instructions content
				if (result != null)
				{	
					result[2] = ((ProcessingInstruction) root).getData();
				}	
			}
			else if (Node.TEXT_NODE != root.getNodeType()  &&  
				Node.PROCESSING_INSTRUCTION_NODE != root.getNodeType())
			{	
				String rootNS = root.getNamespaceURI();
				String rootLocal = root.getLocalName();
				if (
						(
							XMPConst.TAG_XMPMETA.equals(rootLocal)  ||  
							XMPConst.TAG_XAPMETA.equals(rootLocal)
						)  &&
						XMPConst.NS_X.equals(rootNS)
				   )
				{
					// by not passing the RequireXMPMeta-option, the rdf-Node will be valid
					return findRootNode(root, false, result);
				}
				else if (!xmpmetaRequired  &&
						"RDF".equals(rootLocal)  &&
						 XMPConst.NS_RDF.equals(rootNS))
				{	
					if (result != null)
					{	
						result[0] = root;
						result[1] = XMP_RDF;
					}	
					return result;
				}
				else
				{
					// continue searching
					Object[] newResult = findRootNode(root, xmpmetaRequired, result);
					if (newResult != null)
					{
						return newResult;
					}
					else
					{
						continue;
					}	
				}
			}	
		}

		// no appropriate node has been found
		return null;
		//     is extracted here in the C++ Toolkit		
	}

	
	/**
	 * @return Creates, configures and returnes the document builder factory for
	 *         the Metadata Parser.
	 */
	private static DocumentBuilderFactory createDocumentBuilderFactory()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(true);
		
		try
		{
			// honor System parsing limits, e.g.
			// System.setProperty("entityExpansionLimit", "10");
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		}
		catch (Exception e)
		{
			// Ignore IllegalArgumentException and ParserConfigurationException
			// in case the configured XML-Parser does not implement the feature.
		}		
		return factory;
	}
}
// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp;


/**
 * Common constants for the XMP Toolkit. 
 * 
 * @since 20.01.2006
 */
public interface XMPConst
{
	// ---------------------------------------------------------------------------------------------
	// Standard namespace URI constants


	// Standard namespaces

	/** The XML namespace for XML. */
	String NS_XML = "http://www.w3.org/XML/1998/namespace";
	/** The XML namespace for RDF. */
	String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	/** The XML namespace for the Dublin Core schema. */
	String NS_DC = "http://purl.org/dc/elements/1.1/";
	/** The XML namespace for the IPTC Core schema. */
	String NS_IPTCCORE = "http://iptc.org/std/Iptc4xmpCore/1.0/xmlns/";
	
	// Adobe standard namespaces

	/** The XML namespace Adobe XMP Metadata. */
	String NS_X = "adobe:ns:meta/";
	/** */
	String NS_IX = "http://ns.adobe.com/iX/1.0/";
	/** The XML namespace for the XMP "basic" schema. */
	String NS_XMP = "http://ns.adobe.com/xap/1.0/";
	/** The XML namespace for the XMP copyright schema. */
	String NS_XMP_RIGHTS = "http://ns.adobe.com/xap/1.0/rights/";
	/** The XML namespace for the XMP digital asset management schema. */
	String NS_XMP_MM = "http://ns.adobe.com/xap/1.0/mm/";
	/** The XML namespace for the job management schema. */
	String NS_XMP_BJ = "http://ns.adobe.com/xap/1.0/bj/";
	/** The XML namespace for the job management schema. */
	String NS_XMP_NOTE = "http://ns.adobe.com/xmp/note/";
	
	/** The XML namespace for the PDF schema. */
	String NS_PDF = "http://ns.adobe.com/pdf/1.3/";
	/** The XML namespace for the PDF schema. */
	String NS_PDFX = "http://ns.adobe.com/pdfx/1.3/";
	/** */
	String NS_PDFX_ID = "http://www.npes.org/pdfx/ns/id/";
	/** */
	String NS_PDFA_SCHEMA = "http://www.aiim.org/pdfa/ns/schema#";
	/** */
	String NS_PDFA_PROPERTY = "http://www.aiim.org/pdfa/ns/property#";
	/** */
	String NS_PDFA_TYPE = "http://www.aiim.org/pdfa/ns/type#";
	/** */
	String NS_PDFA_FIELD = "http://www.aiim.org/pdfa/ns/field#";
	/** */
	String NS_PDFA_ID = "http://www.aiim.org/pdfa/ns/id/";
	/** */
	String NS_PDFA_EXTENSION = "http://www.aiim.org/pdfa/ns/extension/";
	/** The XML namespace for the Photoshop custom schema. */
	String NS_PHOTOSHOP = "http://ns.adobe.com/photoshop/1.0/";
	/** The XML namespace for the Photoshop Album schema. */
	String NS_PSALBUM = "http://ns.adobe.com/album/1.0/";
	/** The XML namespace for Adobe's EXIF schema. */
	String NS_EXIF = "http://ns.adobe.com/exif/1.0/";
	/** */
	String NS_EXIF_AUX = "http://ns.adobe.com/exif/1.0/aux/";
	/** The XML namespace for Adobe's TIFF schema. */
	String NS_TIFF = "http://ns.adobe.com/tiff/1.0/";
	/** */
	String NS_PNG = "http://ns.adobe.com/png/1.0/";
	/** */
	String NS_JPEG = "http://ns.adobe.com/jpeg/1.0/";
	/** */
	String NS_JP2K = "http://ns.adobe.com/jp2k/1.0/";
	/** */
	String NS_CAMERARAW = "http://ns.adobe.com/camera-raw-settings/1.0/";
	/** */
	String NS_ADOBESTOCKPHOTO = "http://ns.adobe.com/StockPhoto/1.0/";
	/** */
	String NS_CREATOR_ATOM = "http://ns.adobe.com/creatorAtom/1.0/";
	/** */
	String NS_ASF = "http://ns.adobe.com/asf/1.0/";
	/** */
	String NS_WAV = "http://ns.adobe.com/xmp/wav/1.0/";
	
	
	// XMP namespaces that are Adobe private
	
	/** */
	String NS_DM = "http://ns.adobe.com/xmp/1.0/DynamicMedia/";
	/** */
	String NS_TRANSIENT = "http://ns.adobe.com/xmp/transient/1.0/";	
	/** legaciy dublin core NS, will be converted to NS_DC */
	String NS_DC_DEPRECATED = "http://purl.org/dc/1.1/";
	
	
	// XML namespace constants for qualifiers and structured property fields.

	/** The XML namespace for qualifiers of the xmp:Identifier property. */
	String TYPE_IDENTIFIERQUAL = "http://ns.adobe.com/xmp/Identifier/qual/1.0/";
	/** The XML namespace for fields of the Dimensions type. */
	String TYPE_DIMENSIONS = "http://ns.adobe.com/xap/1.0/sType/Dimensions#";
	/** */
	String TYPE_TEXT = "http://ns.adobe.com/xap/1.0/t/";
	/** */
	String TYPE_PAGEDFILE = "http://ns.adobe.com/xap/1.0/t/pg/";
	/** */
	String TYPE_GRAPHICS = "http://ns.adobe.com/xap/1.0/g/";
	/** The XML namespace for fields of a graphical image. Used for the Thumbnail type. */
	String TYPE_IMAGE = "http://ns.adobe.com/xap/1.0/g/img/";
	/** */
	String TYPE_FONT = "http://ns.adobe.com/xap/1.0/sType/Font#";
	/** The XML namespace for fields of the ResourceEvent type. */
	String TYPE_RESOURCEEVENT = "http://ns.adobe.com/xap/1.0/sType/ResourceEvent#";
	/** The XML namespace for fields of the ResourceRef type. */
	String TYPE_RESOURCEREF = "http://ns.adobe.com/xap/1.0/sType/ResourceRef#";
	/** The XML namespace for fields of the Version type. */
	String TYPE_ST_VERSION = "http://ns.adobe.com/xap/1.0/sType/Version#";
	/** The XML namespace for fields of the JobRef type. */
	String TYPE_ST_JOB = "http://ns.adobe.com/xap/1.0/sType/Job#";
	/** */
	String TYPE_MANIFESTITEM = "http://ns.adobe.com/xap/1.0/sType/ManifestItem#";

	

	// ---------------------------------------------------------------------------------------------
	// Basic types and constants

	/**
	 * The canonical true string value for Booleans in serialized XMP. Code that converts from the
	 * string to a bool should be case insensitive, and even allow "1".
	 */
	String TRUESTR = "True";
	/**
	 * The canonical false string value for Booleans in serialized XMP. Code that converts from the
	 * string to a bool should be case insensitive, and even allow "0".
	 */
	String FALSESTR = "False";
	/** Index that has the meaning to be always the last item in an array. */
	int ARRAY_LAST_ITEM = -1;
	/** Node name of an array item. */
	String ARRAY_ITEM_NAME = "[]";
	/** The x-default string for localized properties */
	String X_DEFAULT = "x-default";
	/** xml:lang qualfifier */
	String XML_LANG = "xml:lang";
	/** rdf:type qualfifier */
	String RDF_TYPE = "rdf:type";
	
	/** Processing Instruction (PI) for xmp packet */
	String XMP_PI = "xpacket";
	/** XMP meta tag version new */
	String TAG_XMPMETA = "xmpmeta";
	/** XMP meta tag version old */
	String TAG_XAPMETA = "xapmeta";
}
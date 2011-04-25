// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl;


import com.adobe.xmp.XMPConst;


/**
 * Utility functions for the XMPToolkit implementation.
 * 
 * @since 06.06.2006
 */
public class Utils implements XMPConst
{
	/** segments of a UUID */
	public static final int UUID_SEGMENT_COUNT = 4;
	/** length of a UUID */
	public static final int UUID_LENGTH = 32 + UUID_SEGMENT_COUNT;
	/** table of XML name start chars (<= 0xFF) */
	private  static boolean[] xmlNameStartChars;
	/** table of XML name chars (<= 0xFF) */
	private static boolean[] xmlNameChars;
	/** init char tables */
	static
	{
		initCharTables();
	}
	
	
	/**
	 * Private constructor
	 */
	private Utils()
	{
		// EMPTY
	}

	
	/**
	 * Normalize an xml:lang value so that comparisons are effectively case
	 * insensitive as required by RFC 3066 (which superceeds RFC 1766). The
	 * normalization rules:
	 * <ul>
	 * <li> The primary subtag is lower case, the suggested practice of ISO 639.
	 * <li> All 2 letter secondary subtags are upper case, the suggested
	 * practice of ISO 3166.
	 * <li> All other subtags are lower case.
	 * </ul>
	 * 
	 * @param value
	 *            raw value
	 * @return Returns the normalized value.
	 */
	public static String normalizeLangValue(String value)
	{
		// don't normalize x-default
		if (XMPConst.X_DEFAULT.equals(value))
		{
			return value;
		}	
		
		int subTag = 1;
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < value.length(); i++)
		{
			switch (value.charAt(i))
			{
			case '-':
			case '_':
				// move to next subtag and convert underscore to hyphen
				buffer.append('-');
				subTag++;
				break;
			case ' ':
				// remove spaces
				break;
			default:
				// convert second subtag to uppercase, all other to lowercase
				if (subTag != 2)
				{
					buffer.append(Character.toLowerCase(value.charAt(i)));
				}
				else
				{
					buffer.append(Character.toUpperCase(value.charAt(i)));
				}
			}

		}
		return buffer.toString();
	}


	/**
	 * Split the name and value parts for field and qualifier selectors:
	 * <ul>
	 * <li>[qualName="value"] - An element in an array of structs, chosen by a
	 * field value.
	 * <li>[?qualName="value"] - An element in an array, chosen by a qualifier
	 * value.
	 * </ul>
	 * The value portion is a string quoted by ''' or '"'. The value may contain
	 * any character including a doubled quoting character. The value may be
	 * empty. <em>Note:</em> It is assumed that the expression is formal
	 * correct
	 * 
	 * @param selector
	 *            the selector
	 * @return Returns an array where the first entry contains the name and the
	 *         second the value.
	 */
	static String[] splitNameAndValue(String selector)
	{
		// get the name
		int eq = selector.indexOf('=');
		int pos = 1;
		if (selector.charAt(pos) == '?')
		{
			pos++;
		}
		String name = selector.substring(pos, eq);

		// get the value
		pos = eq + 1;
		char quote = selector.charAt(pos);
		pos++;
		int end = selector.length() - 2; // quote and ]
		StringBuffer value = new StringBuffer(end - eq);
		while (pos < end)
		{
			value.append(selector.charAt(pos));
			pos++;
			if (selector.charAt(pos) == quote)
			{
				// skip one quote in value
				pos++;
			}
		}
		return new String[] { name, value.toString() };
	}
	

	/**
	 * 
	 * @param schema
	 *            a schema namespace
	 * @param prop
	 *            an XMP Property
	 * @return Returns true if the property is defined as &quot;Internal
	 *         Property&quot;, see XMP Specification.
	 */
	static boolean isInternalProperty(String schema, String prop)
	{
		boolean isInternal = false;

		if (NS_DC.equals(schema))
		{
			if ("dc:format".equals(prop) || "dc:language".equals(prop))
			{
				isInternal = true;
			}
		}
		else if (NS_XMP.equals(schema))
		{
			if ("xmp:BaseURL".equals(prop) || "xmp:CreatorTool".equals(prop)
					|| "xmp:Format".equals(prop) || "xmp:Locale".equals(prop)
					|| "xmp:MetadataDate".equals(prop) || "xmp:ModifyDate".equals(prop))
			{
				isInternal = true;
			}
		}
		else if (NS_PDF.equals(schema))
		{
			if ("pdf:BaseURL".equals(prop) || "pdf:Creator".equals(prop)
					|| "pdf:ModDate".equals(prop) || "pdf:PDFVersion".equals(prop)
					|| "pdf:Producer".equals(prop))
			{
				isInternal = true;
			}
		}
		else if (NS_TIFF.equals(schema))
		{
			isInternal = true;
			if ("tiff:ImageDescription".equals(prop) || "tiff:Artist".equals(prop)
					|| "tiff:Copyright".equals(prop))
			{
				isInternal = false;
			}
		}
		else if (NS_EXIF.equals(schema))
		{
			isInternal = true;
			if ("exif:UserComment".equals(prop))
			{
				isInternal = false;
			}
		}
		else if (NS_EXIF_AUX.equals(schema))
		{
			isInternal = true;
		}
		else if (NS_PHOTOSHOP.equals(schema))
		{
			if ("photoshop:ICCProfile".equals(prop))
			{
				isInternal = true;
			}
		}
		else if (NS_CAMERARAW.equals(schema))
		{
			if ("crs:Version".equals(prop) || "crs:RawFileName".equals(prop)
					|| "crs:ToneCurveName".equals(prop))
			{
				isInternal = true;
			}
		}
		else if (NS_ADOBESTOCKPHOTO.equals(schema))
		{
			isInternal = true;
		}
		else if (NS_XMP_MM.equals(schema))
		{
			isInternal = true;
		}
		else if (TYPE_TEXT.equals(schema))
		{
			isInternal = true;
		}
		else if (TYPE_PAGEDFILE.equals(schema))
		{
			isInternal = true;
		}
		else if (TYPE_GRAPHICS.equals(schema))
		{
			isInternal = true;
		}
		else if (TYPE_IMAGE.equals(schema))
		{
			isInternal = true;
		}
		else if (TYPE_FONT.equals(schema))
		{
			isInternal = true;
		}

		return isInternal;
	}


	/**
	 * Check some requirements for an UUID:
	 * <ul>
	 * <li>Length of the UUID is 32</li>
	 * <li>The Delimiter count is 4 and all the 4 delimiter are on their right
	 * position (8,13,18,23)</li>
	 * </ul>
	 * 
	 * 
	 * @param uuid uuid to test
	 * @return true - this is a well formed UUID, false - UUID has not the expected format
	 */

	static boolean checkUUIDFormat(String uuid)
	{
		boolean result = true;
		int delimCnt = 0;
		int delimPos = 0;

		if (uuid == null)
		{
			return false;
		}
		
		for (delimPos = 0; delimPos < uuid.length(); delimPos++)
		{
			if (uuid.charAt(delimPos) == '-')
			{
				delimCnt++;
				result = result  &&  
					(delimPos == 8 || delimPos == 13 || delimPos == 18 || delimPos == 23);
			}
		}

		return result && UUID_SEGMENT_COUNT == delimCnt && UUID_LENGTH == delimPos;
	}

	
	/**
	 * Simple check for valid XMLNames. Within ASCII range<br>
	 * ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6]<br>
	 * are accepted, above all characters (which is not entirely 
	 * correct according to the XML Spec.
	 *  
	 * @param name an XML Name
	 * @return Return <code>true</code> if the name is correct.
	 */
	public static boolean isXMLName(String name)
	{
		if (name.length() > 0  &&  !isNameStartChar(name.charAt(0)))
		{
			return false;
		}
		for (int i = 1; i < name.length(); i++)
		{
			if (!isNameChar(name.charAt(i)))
			{	
				return false;
			}
		}
		return true;
	}

	
	/**
	 * Checks if the value is a legal "unqualified" XML name, as
	 * defined in the XML Namespaces proposed recommendation.
	 * These are XML names, except that they must not contain a colon. 
	 * @param name the value to check
	 * @return Returns true if the name is a valid "unqualified" XML name.
	 */
	public static boolean isXMLNameNS(String name)
	{
		if (name.length() > 0  &&  (!isNameStartChar(name.charAt(0))  ||  name.charAt(0) == ':'))
		{
			return false;
		}
		for (int i = 1; i < name.length(); i++)
		{
			if (!isNameChar(name.charAt(i))  ||  name.charAt(i) == ':')
			{	
				return false;
			}
		}
		return true;
	}
	

	/**
	 * @param c  a char
	 * @return Returns true if the char is an ASCII control char.
	 */
	static boolean isControlChar(char c)
	{
		return (c <= 0x1F  ||  c == 0x7F)  &&
				c != 0x09  &&  c != 0x0A  &&  c != 0x0D;
	}

	
	/**
	 * Serializes the node value in XML encoding. Its used for tag bodies and
	 * attributes.<br>
	 * <em>Note:</em> The attribute is always limited by quotes,
	 * thats why <code>&amp;apos;</code> is never serialized.<br> 
	 * <em>Note:</em> Control chars are written unescaped, but if the user uses others than tab, LF
	 * and CR the resulting XML will become invalid.
	 * @param value a string
	 * @param forAttribute flag if string is attribute value (need to additional escape quotes)
	 * @param escapeWhitespaces Decides if LF, CR and TAB are escaped.
	 * @return Returns the value ready for XML output.
	 */
	public static String escapeXML(String value, boolean forAttribute, boolean escapeWhitespaces)
	{
		// quick check if character are contained that need special treatment
		boolean needsEscaping = false;
		for (int i = 0; i < value.length (); i++)
        {
            char c = value.charAt (i);
			if (
				 c == '<'  ||  c == '>'  ||  c == '&'  ||							    // XML chars
				(escapeWhitespaces  &&  (c == '\t'  ||  c == '\n'  ||  c == '\r'))  || 
				(forAttribute  &&  c == '"'))
			{
				needsEscaping = true;
				break;
			}
        }
		
		if (!needsEscaping)
		{
			// fast path
			return value;
		}
		else
		{
			// slow path with escaping
			StringBuffer buffer = new StringBuffer(value.length() * 4 / 3);
	        for (int i = 0; i < value.length (); i++)
	        {
	            char c = value.charAt (i);
	            if (!(escapeWhitespaces  &&  (c == '\t'  ||  c == '\n'  ||  c == '\r')))
	            {
	            	switch (c)
		            {	
	            		// we do what "Canonical XML" expects
	            		// AUDIT: &apos; not serialized as only outer qoutes are used
		              	case '<':	buffer.append("&lt;"); continue;
		              	case '>':	buffer.append("&gt;"); continue;
		              	case '&':	buffer.append("&amp;"); continue;
		              	case '"': 	buffer.append(forAttribute ? "&quot;" : "\""); continue;
		              	default:	buffer.append(c); continue;
		            }
		        }
	            else
	            {
	            	// write control chars escaped,
	            	// if there are others than tab, LF and CR the xml will become invalid.
	            	buffer.append("&#x");
	            	buffer.append(Integer.toHexString(c).toUpperCase());
	            	buffer.append(';');
	            }
	        }
	        return buffer.toString();
		}
	}	
	
	
	/**
	 * Replaces the ASCII control chars with a space.
	 * 
	 * @param value
	 *            a node value
	 * @return Returns the cleaned up value
	 */
	static String removeControlChars(String value)
	{
		StringBuffer buffer = new StringBuffer(value);
		for (int i = 0; i < buffer.length(); i++)
		{
			if (isControlChar(buffer.charAt(i)))
			{
				buffer.setCharAt(i, ' ');
			}
		}
		return buffer.toString();
	}

	
	/** 
	 * Simple check if a character is a valid XML start name char.
	 * Within ASCII range<br>
	 * ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6]<br>
	 * are accepted, above all characters (which is not entirely 
	 * correct according to the XML Spec)
	 *  
	 * @param ch a character
	 * @return Returns true if the character is a valid first char of an XML name.
	 */
	private static boolean isNameStartChar(char ch)
	{
		return ch > 0xFF  ||  xmlNameStartChars[ch];
	}

	
	/** 
	 * Simple check if a character is a valid XML name char
	 * (every char except the first one).
	 * Within ASCII range<br>
	 * ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6]<br>
	 * are accepted, above all characters (which is not entirely 
	 * correct according to the XML Spec)
	 * 
	 * @param ch a character
	 * @return Returns true if the character is a valid char of an XML name.
	 */
	private static boolean isNameChar(char ch)
	{
		return ch > 0xFF  ||  xmlNameChars[ch];
	}

	
	/**
	 * Initializes the char tables for later use.
	 */
	private static void initCharTables()
	{
		xmlNameChars = new boolean[0x0100];
		xmlNameStartChars = new boolean[0x0100];
		
		for (char ch = 0; ch < xmlNameChars.length; ch++)
		{
			xmlNameStartChars[ch] = 
				('a' <= ch  &&  ch <= 'z')  ||
				('A' <= ch  &&  ch <= 'Z')  ||
				ch == ':'  ||
				ch == '_'  ||
				(0xC0 <= ch  &&  ch <= 0xD6)  ||
				(0xD8 <= ch  &&  ch <= 0xF6);
			
			xmlNameChars[ch] =
				('a' <= ch  &&  ch <= 'z')  ||
				('A' <= ch  &&  ch <= 'Z')  ||
				('0' <= ch  &&  ch <= '9')  ||
				ch == ':'  ||
				ch == '_'  ||
				ch == '-'  ||
				ch == '.'  ||
				ch == 0xB7  ||
				(0xC0 <= ch  &&  ch <= 0xD6)  ||
				(0xD8 <= ch  &&  ch <= 0xF6);
		}
	}	
}
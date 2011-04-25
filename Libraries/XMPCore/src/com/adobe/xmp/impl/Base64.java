// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2001 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp.impl;




/**
 * A utility class to perform base64 encoding and decoding as specified
 * in RFC-1521. See also RFC 1421.
 *
 * @version     $Revision: 1.4 $
 */
public class  Base64
{
	/** marker for invalid bytes */
	private static final byte INVALID = -1; 
	/** marker for accepted whitespace bytes */
	private static final byte WHITESPACE = -2;
	/** marker for an equal symbol */
	private static final byte EQUAL = -3;
	
	/** */
    private static byte[] base64 = {
        (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D',   //  0 to  3
        (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H',   //  4 to  7
        (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L',   //  8 to 11
        (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P',   // 11 to 15
        (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T',   // 16 to 19
        (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X',   // 20 to 23
        (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b',   // 24 to 27
        (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',   // 28 to 31
        (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j',   // 32 to 35
        (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',   // 36 to 39
        (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r',   // 40 to 43
        (byte) 's', (byte) 't', (byte) 'u', (byte) 'v',   // 44 to 47
        (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',   // 48 to 51
        (byte) '0', (byte) '1', (byte) '2', (byte) '3',   // 52 to 55
        (byte) '4', (byte) '5', (byte) '6', (byte) '7',   // 56 to 59
        (byte) '8', (byte) '9', (byte) '+', (byte) '/'    // 60 to 63
    };
    /** */
    private static byte[] ascii = new byte[255];
    /** */
    static {
    	// not valid bytes
        for (int idx = 0; idx < 255; idx++)
		{
			ascii[idx] = INVALID;
		}
    	// valid bytes
		for (int idx = 0; idx < base64.length; idx++)
		{
			ascii[base64[idx]] = (byte) idx;
		}
		// whitespaces
		ascii[0x09] = WHITESPACE;
		ascii[0x0A] = WHITESPACE;
		ascii[0x0D] = WHITESPACE;
		ascii[0x20] = WHITESPACE;
		
		// trailing equals
		ascii[0x3d] = EQUAL;
    }

    
    /**
	 * Encode the given byte[].
	 * 
	 * @param src the source string.
	 * @return the base64-encoded data.
	 */
    public static final byte[] encode(byte[] src)
	{
    	return encode(src, 0);
	}
    	

    /**
	 * Encode the given byte[].
	 * 
	 * @param src the source string.
	 * @param lineFeed a linefeed is added after <code>linefeed</code> characters;
	 *            must be dividable by four; 0 means no linefeeds
	 * @return the base64-encoded data.
	 */
    public static final byte[] encode(byte[] src, int lineFeed)
	{
    	// linefeed must be dividable by 4
    	lineFeed = lineFeed / 4 * 4;
    	if (lineFeed < 0)
    	{
    		lineFeed = 0;
    	}
    	
		// determine code length
    	int codeLength = ((src.length + 2) / 3) * 4;
		if (lineFeed > 0)
		{
			codeLength += (codeLength - 1) / lineFeed;
		}
		
		byte[] dst = new byte[codeLength];
		int bits24;
		int bits6;
		//
		// Do 3-byte to 4-byte conversion + 0-63 to ascii printable conversion
		//
		int didx = 0;
		int sidx = 0;
		int lf = 0;
		while (sidx + 3 <= src.length)
		{
            bits24  = (src[sidx++] & 0xFF) << 16; 
            bits24 |= (src[sidx++] & 0xFF) <<  8; 
            bits24 |= (src[sidx++] & 0xFF) <<  0;
            bits6   = (bits24 & 0x00FC0000) >> 18; 
            dst[didx++] = base64[bits6];
            bits6   = (bits24 & 0x0003F000) >> 12; 
            dst[didx++] = base64[bits6];
            bits6   = (bits24 & 0x00000FC0) >>  6; 
            dst[didx++] = base64[bits6];
            bits6   = (bits24 & 0x0000003F);
            dst[didx++] = base64[bits6];
            
            lf += 4;
            if (didx < codeLength  &&  lineFeed > 0  &&  lf % lineFeed == 0)
            {
            	dst[didx++] = 0x0A;
            }
        }
        if (src.length - sidx == 2)
		{
            bits24  = (src[sidx    ] & 0xFF) << 16; 
            bits24 |= (src[sidx + 1] & 0xFF) <<  8;
            bits6 = (bits24 & 0x00FC0000) >> 18;
            dst[didx++] = base64[bits6]; 
            bits6 = (bits24 & 0x0003F000) >> 12; 
            dst[didx++] = base64[bits6]; 
            bits6 = (bits24 & 0x00000FC0) >>  6; 
            dst[didx++] = base64[bits6];
            dst[didx++] = (byte) '='; 
        }
        else if (src.length - sidx == 1)
		{
            bits24 = (src[sidx] & 0xFF) << 16;
            bits6  = (bits24 & 0x00FC0000) >> 18;
            dst[didx++] = base64[bits6];
            bits6  = (bits24 & 0x0003F000) >> 12; 
            dst[didx++] = base64[bits6];
            dst[didx++] = (byte) '='; 
            dst[didx++] = (byte) '='; 
        }
        return dst;
    }


    /**
     * Encode the given string.
     * @param src the source string.
     * @return the base64-encoded string.
     */
    public static final String encode(String src)
	{
		return new String(encode(src.getBytes()));
	}


    /**
	 * Decode the given byte[].
	 * 
	 * @param src
	 *            the base64-encoded data.
	 * @return the decoded data.
     * @throws IllegalArgumentException Thrown if the base 64 strings contains non-valid characters,
     * 		beside the bas64 chars, LF, CR, tab and space are accepted.
	 */
    public static final byte[] decode(byte[] src) throws IllegalArgumentException
	{
        //
        // Do ascii printable to 0-63 conversion.
        //
        int sidx;
        int srcLen = 0;
        for (sidx = 0; sidx < src.length; sidx++)
        {
        	byte val = ascii[src[sidx]];
        	if (val >= 0)
        	{	
        		src[srcLen++] = val;
        	}
        	else if (val == INVALID)
        	{
        		throw new IllegalArgumentException("Invalid base 64 string");
        	}
        }
    	
		//
		// Trim any padding.
		//
		while (srcLen > 0  &&  src[srcLen - 1] == EQUAL)
		{
			srcLen--;
		}
		byte[] dst = new byte[srcLen * 3 / 4];

		//
        // Do 4-byte to 3-byte conversion.
        //
        int didx;
        for (sidx = 0, didx = 0; didx < dst.length - 2; sidx += 4, didx += 3)
        {
            dst[didx    ] = (byte) (((src[sidx    ] << 2) & 0xFF)
                            | ((src[sidx + 1] >>> 4) & 0x03));
            dst[didx + 1] = (byte) (((src[sidx + 1] << 4) & 0xFF)
                            | ((src[sidx + 2] >>> 2) & 0x0F));
            dst[didx + 2] = (byte) (((src[sidx + 2] << 6) & 0xFF)
                            | ((src[sidx + 3]) & 0x3F));
        }
        if (didx < dst.length)
        {
            dst[didx]     = (byte) (((src[sidx    ] << 2) & 0xFF)
                            | ((src[sidx + 1] >>> 4) & 0x03));
        }
        if (++didx < dst.length)
        {
            dst[didx]     = (byte) (((src[sidx + 1] << 4) & 0xFF)
                            | ((src[sidx + 2] >>> 2) & 0x0F));
        }
        return dst;
    }


    /**
	 * Decode the given string.
	 * 
	 * @param src the base64-encoded string.
	 * @return the decoded string.
	 */
	public static final String decode(String src)
	{
		return new String(decode(src.getBytes()));
	}
}

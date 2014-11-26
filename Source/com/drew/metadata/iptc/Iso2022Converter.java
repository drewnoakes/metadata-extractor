package com.drew.metadata.iptc;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public final class Iso2022Converter 
{

    private Iso2022Converter() 
    {
    }
    
    private static final String ISO_8859_1 = "ISO-8859-1";
    private static final String UTF_8 = "UTF-8";
    private static final int LATIN_CAPITAL_A = 0x41;
    private static final int DOT = 0xe280a2;
    private static final int LATIN_CAPITAL_G = 0x47;
    private static final int PERCENT_SIGN = 0x25;
    private static final int ESC = 0x1B;

    public static void main(String[] args) 
    {

    }

    /**
     * Converts the given ISO2022 char set to a java char set.  
     * 
     * @param bytes the ISO2022 char set
     * @return the java char set name as a string or null if the converting was not possible
     */
    public static String convertISO2022CharsetToJavaCharset(byte[] bytes) 
    {
        if (bytes[0] == ESC && bytes[1] == PERCENT_SIGN && bytes[2] == LATIN_CAPITAL_G)
            return UTF_8;
        if (bytes[0] == ESC && (bytes[3] & 0xFF | ((bytes[2] & 0xFF) << 8) | ((bytes[1] & 0xFF ) <<16)) == DOT && bytes[4] == LATIN_CAPITAL_A)
            return ISO_8859_1;

        return null;
    }

    /**
     * This method tries to guess if the encoding is UTF-8, System.getProperty("file.encoding") or ISO-8859-1.
     * It's only purpose is to guess the encoding if and only if iptc tag coded character set is not set. If the
     * encoding is not UTF-8, the tag should be set. Otherwise it is bad practice. This method tries to
     * workaround this issue since some metadata manipulating tools do not prevent such bad practice.
     *  
     * About the reliability of this method: The check if some bytes are UTF-8 or not has a very high reliability.
     * The two other checks are less reliable.
     * 
     * @param bytes some text as bytes
     * @return the name of the encoding or null if none could be guessed
     */
    static String guessEncoding(byte[] bytes) {
        CharsetDecoder cs = Charset.forName(UTF_8).newDecoder();

        try {
            cs.decode(ByteBuffer.wrap(bytes));
            return UTF_8;
        } catch (CharacterCodingException e) {
        }
        
        cs = Charset.forName(System.getProperty("file.encoding")).newDecoder();
        try 
        {
            cs.decode(ByteBuffer.wrap(bytes));
            return System.getProperty("file.encoding");
        } catch (CharacterCodingException e) 
        {
        }
        
        cs = Charset.forName(ISO_8859_1).newDecoder();
        try 
        {
            cs.decode(ByteBuffer.wrap(bytes));
            return ISO_8859_1;
        } catch (CharacterCodingException e) 
        {
        }
        
        return null;
    }
    
    
}

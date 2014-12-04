package com.drew.metadata.iptc;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public final class Iso2022Converter
{
    private static final String ISO_8859_1 = "ISO-8859-1";
    private static final String UTF_8 = "UTF-8";

    private static final byte LATIN_CAPITAL_A = 0x41;
    private static final int DOT = 0xe280a2;
    private static final byte LATIN_CAPITAL_G = 0x47;
    private static final byte PERCENT_SIGN = 0x25;
    private static final byte ESC = 0x1B;

    /**
     * Converts the given ISO2022 char set to a Java charset name.
     *
     * @param bytes string data encoded using ISO2022
     * @return the Java charset name as a string, or <code>null</code> if the conversion was not possible
     */
    @Nullable
    public static String convertISO2022CharsetToJavaCharset(@NotNull final byte[] bytes)
    {
        if (bytes.length > 2 && bytes[0] == ESC && bytes[1] == PERCENT_SIGN && bytes[2] == LATIN_CAPITAL_G)
            return UTF_8;

        if (bytes.length > 3 && bytes[0] == ESC && (bytes[3] & 0xFF | ((bytes[2] & 0xFF) << 8) | ((bytes[1] & 0xFF) << 16)) == DOT && bytes[4] == LATIN_CAPITAL_A)
            return ISO_8859_1;

        return null;
    }

    /**
     * Attempts to guess the encoding of a string provided as a byte array.
     * <p/>
     * Encodings trialled are, in order:
     * <ul>
     *     <li>UTF-8</li>
     *     <li><code>System.getProperty("file.encoding")</code></li>
     *     <li>ISO-8859-1</li>
     * </ul>
     * <p/>
     * Its only purpose is to guess the encoding if and only if iptc tag coded character set is not set. If the
     * encoding is not UTF-8, the tag should be set. Otherwise it is bad practice. This method tries to
     * workaround this issue since some metadata manipulating tools do not prevent such bad practice.
     * <p/>
     * About the reliability of this method: The check if some bytes are UTF-8 or not has a very high reliability.
     * The two other checks are less reliable.
     *
     * @param bytes some text as bytes
     * @return the name of the encoding or null if none could be guessed
     */
    @Nullable
    static String guessEncoding(@NotNull final byte[] bytes)
    {
        String[] encodings = { UTF_8, System.getProperty("file.encoding"), ISO_8859_1 };

        for (String encoding : encodings)
        {
            CharsetDecoder cs = Charset.forName(encoding).newDecoder();

            try {
                cs.decode(ByteBuffer.wrap(bytes));
                return encoding;
            } catch (CharacterCodingException e) {
                // fall through...
            }
        }

        // No encodings succeeded. Return null.
        return null;
    }

    private Iso2022Converter()
    {}
}

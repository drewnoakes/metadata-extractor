package com.drew.metadata.iptc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Iso2022ConverterTest
{
    @Test
    public void testConvertISO2022CharsetToJavaCharset() throws Exception
    {
        assertEquals("UTF-8", Iso2022Converter.convertISO2022CharsetToJavaCharset(new byte[]{0x1B, 0x25, 0x47}));
        assertEquals("ISO-8859-1", Iso2022Converter.convertISO2022CharsetToJavaCharset(new byte[]{0x1B, (byte)0xE2, (byte)0x80, (byte)0xA2, 0x41}));
    }
}

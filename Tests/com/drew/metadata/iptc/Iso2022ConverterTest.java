/*
 * Copyright 2002-2016 Drew Noakes
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.metadata.iptc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Iso2022ConverterTest
{
    private static final byte[] CP1250_TEXT = new byte[] {0x1c, 0x02, 0x05, 0x00, 0x27, 0x46, 0x49, 0x4c, 0x4b, 0x4f, 0x3a, 0x20, 0x50, 0x72, 0x69, 0x20, (byte) 0x8a, 0x61, 0x6d, 0x6f};

	@Test
    public void testConvertISO2022CharsetToJavaCharset() throws Exception
    {
        assertEquals("UTF-8", Iso2022Converter.convertISO2022CharsetToJavaCharset(new byte[]{0x1B, 0x25, 0x47}));
        assertEquals("ISO-8859-1", Iso2022Converter.convertISO2022CharsetToJavaCharset(new byte[]{0x1B, (byte)0xE2, (byte)0x80, (byte)0xA2, 0x41}));
    }
    
    @Test
	public void testCP1250IPTCUndefined() throws Exception {
    	assertEquals("ISO-8859-1", Iso2022Converter.guessEncoding(CP1250_TEXT));
	}
    
    @Test
	public void testName() throws Exception {
    	System.setProperty(Iso2022Converter.ADDITIONAL_ENCODINGS_PARAM, "cp1250");
    	try {
    		assertEquals("cp1250", Iso2022Converter.guessEncoding(CP1250_TEXT));
    	} finally {
    		System.clearProperty(Iso2022Converter.ADDITIONAL_ENCODINGS_PARAM);
    	}
	}
    
}

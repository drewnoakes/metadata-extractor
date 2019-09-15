/*
 * Copyright 2002-2019 Drew Noakes and contributors
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Iso2022ConverterTest
{
    @Test
    public void testConvertISO2022CharsetToJavaCharset() throws Exception
    {
        assertEquals("UTF-8", Iso2022Converter.convertISO2022CharsetToJavaCharset(new byte[]{0x1B, 0x25, 0x47}));
        assertEquals("ISO-8859-1", Iso2022Converter.convertISO2022CharsetToJavaCharset(new byte[]{0x1B, 0x2E, 0x41}));
        assertEquals("ISO-8859-1", Iso2022Converter.convertISO2022CharsetToJavaCharset(new byte[]{0x1B, (byte)0xE2, (byte)0x80, (byte)0xA2, 0x41}));
    }
}

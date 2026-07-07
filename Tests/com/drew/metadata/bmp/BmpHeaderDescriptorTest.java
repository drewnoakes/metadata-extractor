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

package com.drew.metadata.bmp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class BmpHeaderDescriptorTest
{
    @Test
    public void testFormatHex_normalDigits()
    {
        assertEquals("0x000000FF", BmpHeaderDescriptor.formatHex(255, 8));
        assertEquals("0x0000FFFF", BmpHeaderDescriptor.formatHex(65535, 8));
    }

    @Test
    public void testFormatHex_largeDigitsDoesNotCauseOOM()
    {
        // A very large digits value should be capped, not cause OOM
        String result = BmpHeaderDescriptor.formatHex(Integer.MAX_VALUE, Integer.MAX_VALUE);
        assertNotNull(result);
        // Digits must be capped to 16 (max hex digits in a long)
        assertTrue("Result should not be excessively long", result.length() <= 18); // "0x" + 16 digits
    }

    @Test
    public void testFormatHex_digitsExceedingMaxAreCapped()
    {
        // digits > 16 should produce the same result as digits = 16
        String withLargeDigits = BmpHeaderDescriptor.formatHex(0xABCDL, 100);
        String withMaxDigits = BmpHeaderDescriptor.formatHex(0xABCDL, 16);
        assertEquals(withMaxDigits, withLargeDigits);
    }
}

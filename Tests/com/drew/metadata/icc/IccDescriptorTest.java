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

package com.drew.metadata.icc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link IccDescriptor}.
 */
public class IccDescriptorTest
{
    @Test
    public void testFormatDoubleAsString_ZeroPrecision()
    {
        assertEquals("0", IccDescriptor.formatDoubleAsString(0.4, 0, false));
        assertEquals("1", IccDescriptor.formatDoubleAsString(0.5, 0, false));
        assertEquals("2", IccDescriptor.formatDoubleAsString(1.6, 0, false));
        assertEquals("-1", IccDescriptor.formatDoubleAsString(-0.6, 0, false));
    }

    @Test
    public void testFormatDoubleAsString_NoTrailingZeroes()
    {
        assertEquals("0.0", IccDescriptor.formatDoubleAsString(0.0, 7, false));
        assertEquals("1.0", IccDescriptor.formatDoubleAsString(1.0, 7, false));
        assertEquals("0.5", IccDescriptor.formatDoubleAsString(0.5, 7, false));
        assertEquals("0.1", IccDescriptor.formatDoubleAsString(0.1, 7, false));
        assertEquals("0.1234567", IccDescriptor.formatDoubleAsString(0.1234567, 7, false));
        assertEquals("0.123", IccDescriptor.formatDoubleAsString(0.1230000, 7, false));
    }

    @Test
    public void testFormatDoubleAsString_WithTrailingZeroes()
    {
        assertEquals("0.0000000", IccDescriptor.formatDoubleAsString(0.0, 7, true));
        assertEquals("1.0000000", IccDescriptor.formatDoubleAsString(1.0, 7, true));
        assertEquals("0.5000000", IccDescriptor.formatDoubleAsString(0.5, 7, true));
        assertEquals("0.1234567", IccDescriptor.formatDoubleAsString(0.1234567, 7, true));
    }

    @Test
    public void testFormatDoubleAsString_NegativeValue()
    {
        assertEquals("-0.5", IccDescriptor.formatDoubleAsString(-0.5, 7, false));
        assertEquals("-1.0", IccDescriptor.formatDoubleAsString(-1.0, 7, false));
        assertEquals("-0.5000000", IccDescriptor.formatDoubleAsString(-0.5, 7, true));
    }

    @Test
    public void testFormatDoubleAsString_VaryingPrecision()
    {
        assertEquals("0.5", IccDescriptor.formatDoubleAsString(0.5, 1, false));
        assertEquals("0.5", IccDescriptor.formatDoubleAsString(0.5, 3, false));
        assertEquals("0.500", IccDescriptor.formatDoubleAsString(0.5, 3, true));
        assertEquals("0.12346", IccDescriptor.formatDoubleAsString(0.123456789, 5, false));
    }
}

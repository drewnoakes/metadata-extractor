/*
 * Copyright 2002-2015 Drew Noakes
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

package com.drew.lang;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Base class for testing implementations of {@link RandomAccessReader}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class RandomAccessTestBase
{
    protected abstract RandomAccessReader createReader(byte[] bytes);

    @Test
    public void testDefaultEndianness()
    {
        assertEquals(true, createReader(new byte[1]).isMotorolaByteOrder());
    }

    @Test
    public void testGetInt8() throws Exception
    {
        byte[] buffer = new byte[]{0x00, 0x01, (byte)0x7F, (byte)0xFF};
        RandomAccessReader reader = createReader(buffer);

        assertEquals((byte)0, reader.getInt8(0));
        assertEquals((byte)1, reader.getInt8(1));
        assertEquals((byte)127, reader.getInt8(2));
        assertEquals((byte)255, reader.getInt8(3));
    }

    @Test
    public void testGetUInt8() throws Exception
    {
        byte[] buffer = new byte[]{0x00, 0x01, (byte)0x7F, (byte)0xFF};
        RandomAccessReader reader = createReader(buffer);

        assertEquals(0, reader.getUInt8(0));
        assertEquals(1, reader.getUInt8(1));
        assertEquals(127, reader.getUInt8(2));
        assertEquals(255, reader.getUInt8(3));
    }

    @Test
    public void testGetUInt8_OutOfBounds()
    {
        try {
            RandomAccessReader reader = createReader(new byte[2]);
            reader.getUInt8(2);
            fail("Exception expected");
        } catch (IOException ex) {
            assertEquals("Attempt to read from beyond end of underlying data source (requested index: 2, requested count: 1, max index: 1)", ex.getMessage());
        }
    }

    @Test
    public void testGetInt16() throws Exception
    {
        assertEquals(-1, createReader(new byte[]{(byte)0xff, (byte)0xff}).getInt16(0));

        byte[] buffer = new byte[]{0x00, 0x01, (byte)0x7F, (byte)0xFF};
        RandomAccessReader reader = createReader(buffer);

        assertEquals((short)0x0001, reader.getInt16(0));
        assertEquals((short)0x017F, reader.getInt16(1));
        assertEquals((short)0x7FFF, reader.getInt16(2));

        reader.setMotorolaByteOrder(false);

        assertEquals((short)0x0100, reader.getInt16(0));
        assertEquals((short)0x7F01, reader.getInt16(1));
        assertEquals((short)0xFF7F, reader.getInt16(2));
    }

    @Test
    public void testGetUInt16() throws Exception
    {
        byte[] buffer = new byte[]{0x00, 0x01, (byte)0x7F, (byte)0xFF};
        RandomAccessReader reader = createReader(buffer);

        assertEquals(0x0001, reader.getUInt16(0));
        assertEquals(0x017F, reader.getUInt16(1));
        assertEquals(0x7FFF, reader.getUInt16(2));

        reader.setMotorolaByteOrder(false);

        assertEquals(0x0100, reader.getUInt16(0));
        assertEquals(0x7F01, reader.getUInt16(1));
        assertEquals(0xFF7F, reader.getUInt16(2));
    }

    @Test
    public void testGetUInt16_OutOfBounds()
    {
        try {
            RandomAccessReader reader = createReader(new byte[2]);
            reader.getUInt16(1);
            fail("Exception expected");
        } catch (IOException ex) {
            assertEquals("Attempt to read from beyond end of underlying data source (requested index: 1, requested count: 2, max index: 1)", ex.getMessage());
        }
    }

    @Test
    public void testGetInt32() throws Exception
    {
        assertEquals(-1, createReader(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff}).getInt32(0));

        byte[] buffer = new byte[]{0x00, 0x01, (byte)0x7F, (byte)0xFF, 0x02, 0x03, 0x04};
        RandomAccessReader reader = createReader(buffer);

        assertEquals(0x00017FFF, reader.getInt32(0));
        assertEquals(0x017FFF02, reader.getInt32(1));
        assertEquals(0x7FFF0203, reader.getInt32(2));
        assertEquals(0xFF020304, reader.getInt32(3));

        reader.setMotorolaByteOrder(false);

        assertEquals(0xFF7F0100, reader.getInt32(0));
        assertEquals(0x02FF7F01, reader.getInt32(1));
        assertEquals(0x0302FF7F, reader.getInt32(2));
        assertEquals(0x040302FF, reader.getInt32(3));
    }

    @Test
    public void testGetUInt32() throws Exception
    {
        assertEquals(4294967295L, createReader(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff}).getUInt32(0));

        byte[] buffer = new byte[]{0x00, 0x01, (byte)0x7F, (byte)0xFF, 0x02, 0x03, 0x04};
        RandomAccessReader reader = createReader(buffer);

        assertEquals(0x00017FFFL, reader.getUInt32(0));
        assertEquals(0x017FFF02L, reader.getUInt32(1));
        assertEquals(0x7FFF0203L, reader.getUInt32(2));
        assertEquals(0xFF020304L, reader.getUInt32(3));

        reader.setMotorolaByteOrder(false);

        assertEquals(4286513408L, reader.getUInt32(0));
        assertEquals(0x02FF7F01L, reader.getUInt32(1));
        assertEquals(0x0302FF7FL, reader.getUInt32(2));
        assertEquals(0x040302FFL, reader.getInt32(3));
    }

    @Test
    public void testGetInt32_OutOfBounds()
    {
        try {
            RandomAccessReader reader = createReader(new byte[3]);
            reader.getInt32(0);
            fail("Exception expected");
        } catch (IOException ex) {
            assertEquals("Attempt to read from beyond end of underlying data source (requested index: 0, requested count: 4, max index: 2)", ex.getMessage());
        }
    }

    @Test
    public void testGetInt64() throws IOException
    {
        byte[] buffer = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, (byte)0xFF};
        RandomAccessReader reader = createReader(buffer);

        assertEquals(0x0001020304050607L, reader.getInt64(0));
        assertEquals(0x01020304050607FFL, reader.getInt64(1));

        reader.setMotorolaByteOrder(false);

        assertEquals(0x0706050403020100L, reader.getInt64(0));
        assertEquals(0xFF07060504030201L, reader.getInt64(1));
    }

    @Test
    public void testGetInt64_OutOfBounds() throws Exception
    {
        try {
            RandomAccessReader reader = createReader(new byte[7]);
            reader.getInt64(0);
            fail("Exception expected");
        } catch (IOException ex) {
            assertEquals("Attempt to read from beyond end of underlying data source (requested index: 0, requested count: 8, max index: 6)", ex.getMessage());
        }
        try {
            RandomAccessReader reader = createReader(new byte[7]);
            reader.getInt64(-1);
            fail("Exception expected");
        } catch (IOException ex) {
            assertEquals("Attempt to read from buffer using a negative index (-1)", ex.getMessage());
        }
    }

    @Test
    public void testGetFloat32() throws Exception
    {
        final int nanBits = 0x7fc00000;
        assertTrue(Float.isNaN(Float.intBitsToFloat(nanBits)));

        byte[] buffer = new byte[]{0x7f, (byte)0xc0, 0x00, 0x00};
        RandomAccessReader reader = createReader(buffer);

        assertTrue(Float.isNaN(reader.getFloat32(0)));
    }

    @Test
    public void testGetFloat64() throws Exception
    {
        final long nanBits = 0xfff0000000000001L;
        assertTrue(Double.isNaN(Double.longBitsToDouble(nanBits)));

        byte[] buffer = new byte[]{(byte)0xff, (byte)0xf0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};
        RandomAccessReader reader = createReader(buffer);

        assertTrue(Double.isNaN(reader.getDouble64(0)));
    }

    @Test
    public void testGetNullTerminatedString() throws Exception
    {
        byte[] bytes = new byte[]{0x41, 0x42, 0x43, 0x44, 0x00, 0x45, 0x46, 0x47};
        RandomAccessReader reader = createReader(bytes);

        assertEquals("", reader.getNullTerminatedString(0, 0));
        assertEquals("A", reader.getNullTerminatedString(0, 1));
        assertEquals("AB", reader.getNullTerminatedString(0, 2));
        assertEquals("ABC", reader.getNullTerminatedString(0, 3));
        assertEquals("ABCD", reader.getNullTerminatedString(0, 4));
        assertEquals("ABCD", reader.getNullTerminatedString(0, 5));
        assertEquals("ABCD", reader.getNullTerminatedString(0, 6));

        assertEquals("BCD", reader.getNullTerminatedString(1, 3));
        assertEquals("BCD", reader.getNullTerminatedString(1, 4));
        assertEquals("BCD", reader.getNullTerminatedString(1, 5));

        assertEquals("", reader.getNullTerminatedString(4, 3));
    }

    @Test
    public void testGetString() throws Exception
    {
        byte[] bytes = new byte[]{0x41, 0x42, 0x43, 0x44, 0x00, 0x45, 0x46, 0x47};
        RandomAccessReader reader = createReader(bytes);

        assertEquals("", reader.getString(0, 0));
        assertEquals("A", reader.getString(0, 1));
        assertEquals("AB", reader.getString(0, 2));
        assertEquals("ABC", reader.getString(0, 3));
        assertEquals("ABCD", reader.getString(0, 4));
        assertEquals("ABCD\0", reader.getString(0, 5));
        assertEquals("ABCD\0E", reader.getString(0, 6));

        assertEquals("BCD", reader.getString(1, 3));
        assertEquals("BCD\0", reader.getString(1, 4));
        assertEquals("BCD\0E", reader.getString(1, 5));

        assertEquals("\0EF", reader.getString(4, 3));
    }

    @Test
    public void testIndexPlusCountExceedsIntMaxValue()
    {
        RandomAccessReader reader = createReader(new byte[10]);

        try {
            reader.getBytes(0x6FFFFFFF, 0x6FFFFFFF);
        } catch (IOException e) {
            assertEquals("Number of requested bytes summed with starting index exceed maximum range of signed 32 bit integers (requested index: 1879048191, requested count: 1879048191)", e.getMessage());
        }
    }

    @Test
    public void testOverflowBoundsCalculation()
    {
        RandomAccessReader reader = createReader(new byte[10]);

        try {
            reader.getBytes(5, 10);
        } catch (IOException e) {
            assertEquals("Attempt to read from beyond end of underlying data source (requested index: 5, requested count: 10, max index: 9)", e.getMessage());
        }
    }

    @Test
    public void testGetBytesEOF() throws Exception
    {
        createReader(new byte[50]).getBytes(0, 50);

        RandomAccessReader reader = createReader(new byte[50]);
        reader.getBytes(25, 25);

        try {
            createReader(new byte[50]).getBytes(0, 51);
            fail("Expecting exception");
        } catch (IOException ignored) {}
    }

    @Test
    public void testGetInt8EOF() throws Exception
    {
        createReader(new byte[1]).getInt8(0);

        RandomAccessReader reader = createReader(new byte[2]);
        reader.getInt8(0);
        reader.getInt8(1);

        try {
            reader = createReader(new byte[1]);
            reader.getInt8(0);
            reader.getInt8(1);
            fail("Expecting exception");
        } catch (IOException ignored) {}
    }
}

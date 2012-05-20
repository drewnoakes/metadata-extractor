/*
 * Copyright 2002-2012 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */

package com.drew.lang;

import junit.framework.Assert;
import org.junit.Test;

/** @author Drew Noakes http://drewnoakes.com */
public class BufferReaderTest
{
    @SuppressWarnings({ "ConstantConditions" })
    @Test(expected = NullPointerException.class)
    public void testConstructWithNullBufferThrows()
    {
        new BufferReader(null);
    }

    @Test
    public void testDefaultEndianness()
    {
        Assert.assertEquals(true, new BufferReader(new byte[1]).isMotorolaByteOrder());
    }

    @Test
    public void testGetUInt8() throws BufferBoundsException
    {
        byte[] buffer = new byte[] { 0x00, 0x01, (byte)0x7F, (byte)0xFF };
        BufferReader reader = new BufferReader(buffer);

        Assert.assertEquals(0, reader.getUInt8(0));
        Assert.assertEquals(1, reader.getUInt8(1));
        Assert.assertEquals(127, reader.getUInt8(2));
        Assert.assertEquals(255, reader.getUInt8(3));
    }

    @Test
    public void testGetUInt8_OutOfBounds()
    {
        try {
            BufferReader reader = new BufferReader(new byte[2]);
            reader.getUInt8(2);
            Assert.fail("Exception expected");
        } catch (BufferBoundsException ex) {
            Assert.assertEquals("Attempt to read 1 byte from beyond end of buffer (requested index: 2, max index: 1)", ex.getMessage());
        }
    }

    @Test
    public void testGetInt16() throws BufferBoundsException
    {
        Assert.assertEquals(-1, new BufferReader(new byte[]{(byte)0xff,(byte)0xff}).getInt16(0));

        byte[] buffer = new byte[] { 0x00, 0x01, (byte)0x7F, (byte)0xFF };
        BufferReader reader = new BufferReader(buffer);

        Assert.assertEquals((short)0x0001, reader.getInt16(0));
        Assert.assertEquals((short)0x017F, reader.getInt16(1));
        Assert.assertEquals((short)0x7FFF, reader.getInt16(2));

        reader.setMotorolaByteOrder(false);

        Assert.assertEquals((short)0x0100, reader.getInt16(0));
        Assert.assertEquals((short)0x7F01, reader.getInt16(1));
        Assert.assertEquals((short)0xFF7F, reader.getInt16(2));
    }

    @Test
    public void testGetUInt16() throws BufferBoundsException
    {
        byte[] buffer = new byte[] { 0x00, 0x01, (byte)0x7F, (byte)0xFF };
        BufferReader reader = new BufferReader(buffer);

        Assert.assertEquals(0x0001, reader.getUInt16(0));
        Assert.assertEquals(0x017F, reader.getUInt16(1));
        Assert.assertEquals(0x7FFF, reader.getUInt16(2));

        reader.setMotorolaByteOrder(false);

        Assert.assertEquals(0x0100, reader.getUInt16(0));
        Assert.assertEquals(0x7F01, reader.getUInt16(1));
        Assert.assertEquals(0xFF7F, reader.getUInt16(2));
    }

    @Test
    public void testGetUInt16_OutOfBounds()
    {
        try {
            BufferReader reader = new BufferReader(new byte[2]);
            reader.getUInt16(1);
            Assert.fail("Exception expected");
        } catch (BufferBoundsException ex) {
            Assert.assertEquals("Attempt to read 2 bytes from beyond end of buffer (requested index: 1, max index: 1)", ex.getMessage());
        }
    }

    @Test
    public void testGetInt32() throws BufferBoundsException
    {
        Assert.assertEquals(-1, new BufferReader(new byte[]{(byte)0xff,(byte)0xff, (byte)0xff,(byte)0xff}).getInt32(0));

        byte[] buffer = new byte[] { 0x00, 0x01, (byte)0x7F, (byte)0xFF, 0x02, 0x03, 0x04 };
        BufferReader reader = new BufferReader(buffer);

        Assert.assertEquals(0x00017FFF, reader.getInt32(0));
        Assert.assertEquals(0x017FFF02, reader.getInt32(1));
        Assert.assertEquals(0x7FFF0203, reader.getInt32(2));
        Assert.assertEquals(0xFF020304, reader.getInt32(3)); // equiv
        Assert.assertEquals(-16645372, reader.getInt32(3));  //

        reader.setMotorolaByteOrder(false);

        Assert.assertEquals(0xFF7F0100, reader.getInt32(0));
        Assert.assertEquals(0x02FF7F01, reader.getInt32(1));
        Assert.assertEquals(0x0302FF7F, reader.getInt32(2));
    }

    @Test
    public void testGetUInt32() throws BufferBoundsException
    {
        Assert.assertEquals(4294967295L, new BufferReader(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff}).getUInt32(0));

        byte[] buffer = new byte[] { 0x00, 0x01, (byte)0x7F, (byte)0xFF, 0x02, 0x03, 0x04 };
        BufferReader reader = new BufferReader(buffer);

        Assert.assertEquals(0x00017FFF, reader.getUInt32(0));
        Assert.assertEquals(0x017FFF02, reader.getUInt32(1));
        Assert.assertEquals(0x7FFF0203, reader.getUInt32(2));
        Assert.assertEquals(4278321924L, reader.getUInt32(3)); // equiv

        reader.setMotorolaByteOrder(false);

        Assert.assertEquals(4286513408L, reader.getUInt32(0));
        Assert.assertEquals(0x02FF7F01, reader.getUInt32(1));
        Assert.assertEquals(0x0302FF7F, reader.getUInt32(2));
    }

    @Test
    public void testGetInt32_OutOfBounds()
    {
        try {
            BufferReader reader = new BufferReader(new byte[3]);
            reader.getInt32(0);
            Assert.fail("Exception expected");
        } catch (BufferBoundsException ex) {
            Assert.assertEquals("Attempt to read 4 bytes from beyond end of buffer (requested index: 0, max index: 2)", ex.getMessage());
        }
    }

    @Test
    public void testGetInt64() throws BufferBoundsException
    {
        byte[] buffer = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, (byte)0xFF };
        BufferReader reader = new BufferReader(buffer);

        Assert.assertEquals(0x0001020304050607L, reader.getInt64(0));
        Assert.assertEquals(0x01020304050607FFL, reader.getInt64(1));

        reader.setMotorolaByteOrder(false);

        Assert.assertEquals(0x0706050403020100L, reader.getInt64(0));
        Assert.assertEquals(0xFF07060504030201L, reader.getInt64(1));
    }

    @Test
    public void testGetInt64_OutOfBounds()
    {
        try {
            BufferReader reader = new BufferReader(new byte[7]);
            reader.getInt64(0);
            Assert.fail("Exception expected");
        } catch (BufferBoundsException ex) {
            Assert.assertEquals("Attempt to read 8 bytes from beyond end of buffer (requested index: 0, max index: 6)", ex.getMessage());
        }
        try {
            BufferReader reader = new BufferReader(new byte[7]);
            reader.getInt64(-1);
            Assert.fail("Exception expected");
        } catch (BufferBoundsException ex) {
            Assert.assertEquals("Attempt to read from buffer using a negative index (-1)", ex.getMessage());
        }
    }

    @Test
    public void testGetFloat32() throws BufferBoundsException
    {
        final int nanBits = 0x7fc00000;
        Assert.assertEquals(Float.NaN, Float.intBitsToFloat(nanBits));

        byte[] buffer = new byte[] { 0x7f, (byte)0xc0, 0x00, 0x00 };
        BufferReader reader = new BufferReader(buffer);

        Assert.assertEquals(Float.NaN, reader.getFloat32(0));
    }

    @Test
    public void testGetFloat64() throws BufferBoundsException
    {
        final long nanBits = 0xfff0000000000001L;
        Assert.assertEquals(Double.NaN, Double.longBitsToDouble(nanBits));

        byte[] buffer = new byte[] { (byte)0xff, (byte)0xf0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 };
        BufferReader reader = new BufferReader(buffer);

        Assert.assertEquals(Double.NaN, reader.getDouble64(0));
    }

    @Test
    public void testGetNullTerminatedString() throws BufferBoundsException
    {
        byte[] bytes = new byte[]{ 0x41, 0x42, 0x43, 0x44, 0x00, 0x45, 0x46, 0x47 };
        BufferReader reader = new BufferReader(bytes);

        Assert.assertEquals("", reader.getNullTerminatedString(0, 0));
        Assert.assertEquals("A", reader.getNullTerminatedString(0, 1));
        Assert.assertEquals("AB", reader.getNullTerminatedString(0, 2));
        Assert.assertEquals("ABC", reader.getNullTerminatedString(0, 3));
        Assert.assertEquals("ABCD", reader.getNullTerminatedString(0, 4));
        Assert.assertEquals("ABCD", reader.getNullTerminatedString(0, 5));
        Assert.assertEquals("ABCD", reader.getNullTerminatedString(0, 6));

        Assert.assertEquals("BCD", reader.getNullTerminatedString(1, 3));
        Assert.assertEquals("BCD", reader.getNullTerminatedString(1, 4));
        Assert.assertEquals("BCD", reader.getNullTerminatedString(1, 5));

        Assert.assertEquals("", reader.getNullTerminatedString(4, 3));
    }

    @Test
    public void testGetString() throws BufferBoundsException
    {
        byte[] bytes = new byte[]{ 0x41, 0x42, 0x43, 0x44, 0x00, 0x45, 0x46, 0x47 };
        BufferReader reader = new BufferReader(bytes);

        Assert.assertEquals("", reader.getString(0, 0));
        Assert.assertEquals("A", reader.getString(0, 1));
        Assert.assertEquals("AB", reader.getString(0, 2));
        Assert.assertEquals("ABC", reader.getString(0, 3));
        Assert.assertEquals("ABCD", reader.getString(0, 4));
        Assert.assertEquals("ABCD\0", reader.getString(0, 5));
        Assert.assertEquals("ABCD\0E", reader.getString(0, 6));

        Assert.assertEquals("BCD", reader.getString(1, 3));
        Assert.assertEquals("BCD\0", reader.getString(1, 4));
        Assert.assertEquals("BCD\0E", reader.getString(1, 5));

        Assert.assertEquals("\0EF", reader.getString(4, 3));
    }

    @Test
    public void testOverflowBoundsCalculation()
    {
        byte[] bytes = new byte[10];
        BufferReader reader = new BufferReader(bytes);

        try {
            reader.getBytes(0x6FFFFFFF, 0x6FFFFFFF);
        } catch (BufferBoundsException e) {
            Assert.assertEquals("Attempt to read 1879048191 bytes from beyond end of buffer (requested index: 1879048191, max index: 9)", e.getMessage());
        }
    }
}

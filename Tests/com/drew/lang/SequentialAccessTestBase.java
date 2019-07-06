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

package com.drew.lang;

import org.junit.Test;

import java.io.EOFException;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Base class for testing implementations of {@link com.drew.lang.SequentialReader}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class SequentialAccessTestBase
{
    protected abstract SequentialReader createReader(byte[] bytes);

    @Test
    public void testDefaultEndianness()
    {
        assertEquals(true, createReader(new byte[1]).isMotorolaByteOrder());
    }

    @Test
    public void testGetInt8() throws IOException
    {
        byte[] buffer = new byte[]{0x00, 0x01, (byte)0x7F, (byte)0xFF};
        SequentialReader reader = createReader(buffer);

        assertEquals((byte)0, reader.getInt8());
        assertEquals((byte)1, reader.getInt8());
        assertEquals((byte)127, reader.getInt8());
        assertEquals((byte)255, reader.getInt8());
    }

    @Test
    public void testGetUInt8() throws IOException
    {
        byte[] buffer = new byte[]{0x00, 0x01, (byte)0x7F, (byte)0xFF};
        SequentialReader reader = createReader(buffer);

        assertEquals(0, reader.getUInt8());
        assertEquals(1, reader.getUInt8());
        assertEquals(127, reader.getUInt8());
        assertEquals(255, reader.getUInt8());
    }

    @Test
    public void testGetUInt8_OutOfBounds()
    {
        try {
            SequentialReader reader = createReader(new byte[1]);
            reader.getUInt8();
            reader.getUInt8();
            fail("Exception expected");
        } catch (IOException ex) {
            assertEquals("End of data reached.", ex.getMessage());
        }
    }

    @Test
    public void testGetInt16() throws IOException
    {
        assertEquals(-1, createReader(new byte[]{(byte)0xff, (byte)0xff}).getInt16());

        byte[] buffer = new byte[]{0x00, 0x01, (byte)0x7F, (byte)0xFF};
        SequentialReader reader = createReader(buffer);

        assertEquals((short)0x0001, reader.getInt16());
        assertEquals((short)0x7FFF, reader.getInt16());

        reader = createReader(buffer);
        reader.setMotorolaByteOrder(false);

        assertEquals((short)0x0100, reader.getInt16());
        assertEquals((short)0xFF7F, reader.getInt16());
    }

    @Test
    public void testGetUInt16() throws IOException
    {
        byte[] buffer = new byte[]{0x00, 0x01, (byte)0x7F, (byte)0xFF};
        SequentialReader reader = createReader(buffer);

        assertEquals(0x0001, reader.getUInt16());
        assertEquals(0x7FFF, reader.getUInt16());

        reader = createReader(buffer);
        reader.setMotorolaByteOrder(false);

        assertEquals(0x0100, reader.getUInt16());
        assertEquals(0xFF7F, reader.getUInt16());
    }

    @Test
    public void testGetUInt16_OutOfBounds()
    {
        try {
            SequentialReader reader = createReader(new byte[1]);
            reader.getUInt16();
            fail("Exception expected");
        } catch (IOException ex) {
            assertEquals("End of data reached.", ex.getMessage());
        }
    }

    @Test
    public void testGetInt32() throws IOException
    {
        assertEquals(-1, createReader(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff}).getInt32());

        byte[] buffer = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        SequentialReader reader = createReader(buffer);

        assertEquals(0x00010203, reader.getInt32());
        assertEquals(0x04050607, reader.getInt32());

        reader = createReader(buffer);
        reader.setMotorolaByteOrder(false);

        assertEquals(0x03020100, reader.getInt32());
        assertEquals(0x07060504, reader.getInt32());
    }

    @Test
    public void testGetUInt32() throws IOException
    {
        assertEquals(4294967295L, createReader(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff}).getUInt32());

        byte[] buffer = new byte[]{(byte)0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06};
        SequentialReader reader = createReader(buffer);

        assertEquals(0xFF000102L, reader.getUInt32());
        assertEquals(0x03040506L, reader.getUInt32());

        reader = createReader(buffer);
        reader.setMotorolaByteOrder(false);

        assertEquals(0x020100FFL, reader.getUInt32()); // 0x0010200FF
        assertEquals(0x06050403L, reader.getUInt32());
    }

    @Test
    public void testGetInt32_OutOfBounds()
    {
        try {
            SequentialReader reader = createReader(new byte[3]);
            reader.getInt32();
            fail("Exception expected");
        } catch (IOException ex) {
            assertEquals("End of data reached.", ex.getMessage());
        }
    }

    @Test
    public void testGetInt64() throws IOException
    {
        byte[] buffer = new byte[]{(byte)0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        SequentialReader reader = createReader(buffer);

        assertEquals(0xFF00010203040506L, reader.getInt64());

        reader = createReader(buffer);
        reader.setMotorolaByteOrder(false);

        assertEquals(0x06050403020100FFL, reader.getInt64());
    }

    @Test
    public void testGetInt64_OutOfBounds()
    {
        try {
            SequentialReader reader = createReader(new byte[7]);
            reader.getInt64();
            fail("Exception expected");
        } catch (IOException ex) {
            assertEquals("End of data reached.", ex.getMessage());
        }
    }

    @Test
    public void testGetFloat32() throws IOException
    {
        final int nanBits = 0x7fc00000;
        assertTrue(Float.isNaN(Float.intBitsToFloat(nanBits)));

        byte[] buffer = new byte[]{0x7f, (byte)0xc0, 0x00, 0x00};
        SequentialReader reader = createReader(buffer);

        assertTrue(Float.isNaN(reader.getFloat32()));
    }

    @Test
    public void testGetFloat64() throws IOException
    {
        final long nanBits = 0xfff0000000000001L;
        assertTrue(Double.isNaN(Double.longBitsToDouble(nanBits)));

        byte[] buffer = new byte[]{(byte)0xff, (byte)0xf0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};
        SequentialReader reader = createReader(buffer);

        assertTrue(Double.isNaN(reader.getDouble64()));
    }

    @Test
    public void testGetNullTerminatedString() throws IOException
    {
        byte[] bytes = new byte[]{0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47};

        // Test max length
        for (int i = 0; i < bytes.length; i++) {
            assertEquals("ABCDEFG".substring(0, i), createReader(bytes).getNullTerminatedString(i, Charsets.UTF_8));
        }

        assertEquals("", createReader(new byte[]{0}).getNullTerminatedString(10, Charsets.UTF_8));
        assertEquals("A", createReader(new byte[]{0x41, 0}).getNullTerminatedString(10, Charsets.UTF_8));
        assertEquals("AB", createReader(new byte[]{0x41, 0x42, 0}).getNullTerminatedString(10, Charsets.UTF_8));
        assertEquals("AB", createReader(new byte[]{0x41, 0x42, 0, 0x43}).getNullTerminatedString(10, Charsets.UTF_8));
    }

    @Test
    public void testGetString() throws IOException
    {
        byte[] bytes = new byte[]{0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47};
        String expected = new String(bytes);
        assertEquals(bytes.length, expected.length());

        for (int i = 0; i < bytes.length; i++) {
            assertEquals("ABCDEFG".substring(0, i), createReader(bytes).getString(i));
        }
    }

    @Test
    public void testGetBytes() throws IOException
    {
        byte[] bytes = {0, 1, 2, 3, 4, 5};

        for (int i = 0; i < bytes.length; i++) {
            SequentialReader reader = createReader(bytes);
            byte[] readBytes = reader.getBytes(i);
            for (int j = 0; j < i; j++) {
                assertEquals(bytes[j], readBytes[j]);
            }
        }
    }

    @Test
    public void testOverflowBoundsCalculation()
    {
        SequentialReader reader = createReader(new byte[10]);

        try {
            reader.getBytes(15);
        } catch (IOException e) {
            assertEquals("End of data reached.", e.getMessage());
        }
    }

    @Test
    public void testGetBytesEOF() throws Exception
    {
        createReader(new byte[50]).getBytes(50);

        SequentialReader reader = createReader(new byte[50]);
        reader.getBytes(25);
        reader.getBytes(25);

        try {
            createReader(new byte[50]).getBytes(51);
            fail("Expecting exception");
        } catch (EOFException ignored) {}
    }

    @Test
    public void testGetInt8EOF() throws Exception
    {
        createReader(new byte[1]).getInt8();

        SequentialReader reader = createReader(new byte[2]);
        reader.getInt8();
        reader.getInt8();

        try {
            reader = createReader(new byte[1]);
            reader.getInt8();
            reader.getInt8();
            fail("Expecting exception");
        } catch (EOFException ignored) {}
    }

    @Test
    public void testSkipEOF() throws Exception
    {
        createReader(new byte[1]).skip(1);

        SequentialReader reader = createReader(new byte[2]);
        reader.skip(1);
        reader.skip(1);

        try {
            reader = createReader(new byte[1]);
            reader.skip(1);
            reader.skip(1);
            fail("Expecting exception");
        } catch (EOFException ignored) {}
    }

    @Test
    public void testTrySkipEOF() throws Exception
    {
        assertTrue(createReader(new byte[1]).trySkip(1));

        SequentialReader reader = createReader(new byte[2]);
        assertTrue(reader.trySkip(1));
        assertTrue(reader.trySkip(1));
        assertFalse(reader.trySkip(1));
    }
}

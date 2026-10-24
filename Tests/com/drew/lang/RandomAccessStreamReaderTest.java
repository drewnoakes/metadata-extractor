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
 *    https://github.com/drewnoakes/metadata-extractor/
 *    https://github.com/drewnoakes/metadata-extractor
 */

package com.drew.lang;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class RandomAccessStreamReaderTest extends RandomAccessTestBase
{
    @SuppressWarnings({ "ConstantConditions" })
    @Test(expected = NullPointerException.class)
    public void testConstructWithNullBufferThrows()
    {
        new RandomAccessStreamReader(null);
    }
    
    @Test(expected = BufferBoundsException.class)
    public void testGetByteWithNegativeIndexThrowsBufferBoundsException() throws IOException
    {
        RandomAccessStreamReader reader = new RandomAccessStreamReader(
            new ByteArrayInputStream(new byte[]{1, 2, 3, 4, 5}));
        
        reader.getByte(-1);
    }
    
    @Test
    public void testAssertionReplacementWithProperExceptions() throws IOException
    {
        // This test verifies that our assertion fixes work properly without throwing AssertionError
        RandomAccessStreamReader reader = new RandomAccessStreamReader(
            new ByteArrayInputStream(new byte[]{1, 2, 3, 4, 5}), 2, 5);
        
        // Should work without assertions failing
        assertEquals(5, reader.getLength());
        assertEquals(1, reader.getByte(0));
        assertEquals(5, reader.getByte(4));
    }
    
    @Test
    public void testGetLengthWithEmptyStream() throws IOException
    {
        RandomAccessStreamReader reader = new RandomAccessStreamReader(
            new ByteArrayInputStream(new byte[0]));
        
        assertEquals(0, reader.getLength());
    }

    @Test
    public void testReadAfterStreamLengthMismatch() throws IOException
    {
        for (int streamLength : new int[]{6, 4, 0}) {
            assertReaderUsableAfterStreamLengthMismatch(new byte[]{1, 2, 3, 4, 5}, streamLength, 4);
        }
    }

    @Test
    public void testReadAfterStreamLengthMismatchAtChunkBoundary() throws IOException
    {
        assertReaderUsableAfterStreamLengthMismatch(new byte[]{1, 2, 3, 4}, 5, 4);
    }

    @Test
    public void testReadAfterEmptyStreamLengthMismatch() throws IOException
    {
        assertReaderUsableAfterStreamLengthMismatch(new byte[0], 1, 0);
    }

    private void assertReaderUsableAfterStreamLengthMismatch(byte[] bytes, int streamLength, int index) throws IOException
    {
        RandomAccessStreamReader reader = new RandomAccessStreamReader(
            new ByteArrayInputStream(bytes), 2, streamLength);

        try {
            reader.getByte(index);
            fail("Expected a stream length mismatch");
        } catch (IOException e) {
            assertEquals("Stream length mismatch: expected " + streamLength + " but observed " + bytes.length, e.getMessage());
        }

        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], reader.getByte(i));
        }
        assertArrayEquals(bytes, reader.getBytes(0, bytes.length));
        assertEquals(bytes.length, reader.getLength());

        try {
            reader.getByte(bytes.length);
            fail("Expected a bounds exception after the observed end");
        } catch (BufferBoundsException e) {
            assertEquals(new BufferBoundsException(bytes.length, 1, bytes.length).getMessage(), e.getMessage());
        }

        try {
            reader.getBytes(0, bytes.length + 1);
            fail("Expected a bounds exception for a read spanning the observed end");
        } catch (BufferBoundsException e) {
            assertEquals(new BufferBoundsException(0, bytes.length + 1, bytes.length).getMessage(), e.getMessage());
        }
    }

    @Override
    protected RandomAccessReader createReader(byte[] bytes)
    {
        return new RandomAccessStreamReader(new ByteArrayInputStream(bytes));
    }
}

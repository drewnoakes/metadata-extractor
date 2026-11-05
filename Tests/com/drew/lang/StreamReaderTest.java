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
import java.io.FilterInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class StreamReaderTest extends SequentialAccessTestBase
{
    @SuppressWarnings({"ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void testConstructWithNullStreamThrows()
    {
        new StreamReader(null);
    }

    @Override
    protected SequentialReader createReader(byte[] bytes)
    {
        return new StreamReader(new ByteArrayInputStream(bytes));
    }

    @Test
    public void testNullTerminatedBytesWithShortReads() throws IOException
    {
        for (boolean moveToMaxLength : new boolean[]{false, true}) {
            StreamReader reader = new StreamReader(new ByteArrayInputStream(new byte[]{0x41, 0, 7, 8, 9}) {
                @Override
                public int available()
                {
                    return 0;
                }

                @Override
                public synchronized int read(byte[] buffer, int offset, int length)
                {
                    return super.read(buffer, offset, Math.min(1, length));
                }
            });
            assertArrayEquals(new byte[]{0x41}, reader.getNullTerminatedBytes(4, moveToMaxLength));
            assertEquals(moveToMaxLength ? 4 : 2, reader.getPosition());
            assertEquals(moveToMaxLength ? 9 : 7, reader.getByte());
        }
    }

    @Test
    public void testNullTerminatedBytesPropagatesPaddingReadError() throws IOException
    {
        // The terminator lies within the first 1024-byte bulk read, which succeeds.
        // Any read after that throws, so the error must come from draining the padding.
        final int initialReadLength = 1024;
        byte[] data = new byte[initialReadLength + 1];
        data[0] = 0x41;
        final IOException error = new IOException("Padding read failed");
        StreamReader reader = new StreamReader(new FilterInputStream(new ByteArrayInputStream(data)) {
            private int _delivered;

            @Override
            public int read(byte[] buffer, int offset, int count) throws IOException
            {
                if (_delivered >= initialReadLength)
                    throw error;
                int bytesRead = super.read(buffer, offset, Math.min(count, initialReadLength - _delivered));
                if (bytesRead > 0)
                    _delivered += bytesRead;
                return bytesRead;
            }
        });
        try {
            reader.getNullTerminatedBytes(initialReadLength + 1, true);
            fail("Expected IOException while consuming padding");
        } catch (IOException e) {
            assertSame(error, e);
        }
        assertEquals(initialReadLength, reader.getPosition());
    }
}

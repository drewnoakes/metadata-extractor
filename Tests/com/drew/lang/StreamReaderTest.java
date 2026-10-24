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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

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

    @Test
    public void testGetBytesFromStreamReturningPartialReads() throws IOException
    {
        final byte[] bytes = new byte[150_000];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)(i * 7);
        }

        // Returns at most 7 bytes per read call, as some streams do
        InputStream trickle = new ByteArrayInputStream(bytes) {
            @Override
            public synchronized int read(byte[] b, int off, int len)
            {
                return super.read(b, off, Math.min(len, 7));
            }
        };

        SequentialReader reader = new StreamReader(trickle);
        assertArrayEquals(bytes, reader.getBytes(bytes.length));
        assertEquals(bytes.length, reader.getPosition());
    }

    @Override
    protected SequentialReader createReader(byte[] bytes)
    {
        return new StreamReader(new ByteArrayInputStream(bytes));
    }
}

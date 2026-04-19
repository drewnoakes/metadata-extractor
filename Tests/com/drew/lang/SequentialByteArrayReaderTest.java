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

import java.io.IOException;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class SequentialByteArrayReaderTest extends SequentialAccessTestBase
{
    @SuppressWarnings({"ConstantConditions"})
    @Test(expected = NullPointerException.class)
    public void testConstructWithNullStreamThrows()
    {
        new SequentialByteArrayReader(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBytesWithNegativeCountThrows() throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(new byte[10]);
        reader.getBytes(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBytesWithNegativeOffsetThrows() throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(new byte[10]);
        byte[] buffer = new byte[5];
        reader.getBytes(buffer, -1, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBytesWithNegativeCountInOverloadThrows() throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(new byte[10]);
        byte[] buffer = new byte[5];
        reader.getBytes(buffer, 0, -1);
    }

    @Override
    protected SequentialReader createReader(byte[] bytes)
    {
        return new SequentialByteArrayReader(bytes);
    }
}

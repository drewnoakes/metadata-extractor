/*
 * Copyright 2002-2016 Drew Noakes
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

import com.drew.lang.annotations.NotNull;

import java.io.IOException;

/**
 * Provides methods to read specific values from a byte array, with a consistent, checked exception structure for
 * issues.
 * <p>
 * By default, the reader operates with Motorola byte order (big endianness).  This can be changed by calling
 * <code>setMotorolaByteOrder(boolean)</code>.
 *
 * @author Drew Noakes https://drewnoakes.com
 * */
public class ByteArrayReader extends RandomAccessReader
{
    @NotNull
    private final byte[] _buffer;

    @SuppressWarnings({ "ConstantConditions" })
    @com.drew.lang.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Design intent")
    public ByteArrayReader(@NotNull byte[] buffer)
    {
        if (buffer == null)
            throw new NullPointerException();

        _buffer = buffer;
    }

    @Override
    public long getLength()
    {
        return _buffer.length;
    }

    @Override
    protected byte getByte(int index) throws IOException
    {
        return _buffer[index];
    }

    @Override
    protected void validateIndex(int index, int bytesRequested) throws IOException
    {
        if (!isValidIndex(index, bytesRequested))
            throw new BufferBoundsException(index, bytesRequested, _buffer.length);
    }

    @Override
    protected boolean isValidIndex(int index, int bytesRequested) throws IOException
    {
        return bytesRequested >= 0
            && index >= 0
            && (long)index + (long)bytesRequested - 1L < (long)_buffer.length;
    }

    @Override
    @NotNull
    public byte[] getBytes(int index, int count) throws IOException
    {
        validateIndex(index, count);

        byte[] bytes = new byte[count];
        System.arraycopy(_buffer, index, bytes, 0, count);
        return bytes;
    }
}

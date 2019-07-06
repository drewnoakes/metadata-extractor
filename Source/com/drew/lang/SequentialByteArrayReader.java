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

import com.drew.lang.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;

/**
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class SequentialByteArrayReader extends SequentialReader
{
    @NotNull
    private final byte[] _bytes;
    private int _index;

    @Override
    public long getPosition()
    {
        return _index;
    }

    public SequentialByteArrayReader(@NotNull byte[] bytes)
    {
        this(bytes, 0);
    }

    @SuppressWarnings("ConstantConditions")
    public SequentialByteArrayReader(@NotNull byte[] bytes, int baseIndex)
    {
        if (bytes == null)
            throw new NullPointerException();

        _bytes = bytes;
        _index = baseIndex;
    }

    @Override
    public byte getByte() throws IOException
    {
        if (_index >= _bytes.length) {
            throw new EOFException("End of data reached.");
        }
        return _bytes[_index++];
    }

    @NotNull
    @Override
    public byte[] getBytes(int count) throws IOException
    {
        if (_index + count > _bytes.length) {
            throw new EOFException("End of data reached.");
        }

        byte[] bytes = new byte[count];
        System.arraycopy(_bytes, _index, bytes, 0, count);
        _index += count;

        return bytes;
    }

    @Override
    public void getBytes(@NotNull byte[] buffer, int offset, int count) throws IOException
    {
        if (_index + count > _bytes.length) {
            throw new EOFException("End of data reached.");
        }

        System.arraycopy(_bytes, _index, buffer, offset, count);
        _index += count;
    }

    @Override
    public void skip(long n) throws IOException
    {
        if (n < 0) {
            throw new IllegalArgumentException("n must be zero or greater.");
        }

        if (_index + n > _bytes.length) {
            throw new EOFException("End of data reached.");
        }

        _index += n;
    }

    @Override
    public boolean trySkip(long n) throws IOException
    {
        if (n < 0) {
            throw new IllegalArgumentException("n must be zero or greater.");
        }

        _index += n;

        if (_index > _bytes.length) {
            _index = _bytes.length;
            return false;
        }

        return true;
    }

    @Override
    public int available() {
        return _bytes.length - _index;
    }
}

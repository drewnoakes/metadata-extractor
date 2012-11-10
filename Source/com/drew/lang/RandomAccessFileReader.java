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

import com.drew.lang.annotations.NotNull;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Provides methods to read specific values from a {@link RandomAccessFile}, with a consistent, checked exception structure for
 * issues.
 *
 * @author Drew Noakes http://drewnoakes.com
 * */
public class RandomAccessFileReader extends RandomAccessReader
{
    @NotNull
    private final RandomAccessFile _file;
    private final long _length;
    private int _currentIndex;

    @SuppressWarnings({ "ConstantConditions" })
    @com.drew.lang.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Design intent")
    public RandomAccessFileReader(@NotNull RandomAccessFile file) throws IOException
    {
        if (file == null)
            throw new NullPointerException();
        
        _file = file;
        _length = _file.length();
    }

    @Override
    public long getLength()
    {
        return _length;
    }

    @Override
    protected byte getByte(int index) throws BufferBoundsException
    {
        if (index != _currentIndex)
            seek(index);

        final int b;
        try {
            b = _file.read();
        } catch (IOException e) {
            throw new BufferBoundsException("IOException reading from file.", e);
        }

        if (b < 0)
            throw new BufferBoundsException("Unexpected end of file encountered.");
        assert(b <= 0xff);
        _currentIndex++;
        return (byte) b;
    }

    @Override
    @NotNull
    public byte[] getBytes(int index, int count) throws BufferBoundsException
    {
        validateIndex(index, count);

        if (index != _currentIndex)
            seek(index);

        byte[] bytes = new byte[count];
        final int bytesRead;
        try {
            bytesRead = _file.read(bytes);
            _currentIndex += bytesRead;
        } catch (IOException e) {
            throw new BufferBoundsException("Unexpected end of file encountered.", e);
        }
        if (bytesRead != count)
            throw new BufferBoundsException("Unexpected end of file encountered.");
        return bytes;
    }

    private void seek(final int index) throws BufferBoundsException
    {
        if (index == _currentIndex)
            return;

        try {
            _file.seek(index);
            _currentIndex = index;
        } catch (IOException e) {
            throw new BufferBoundsException("IOException seeking in file.", e);
        }
    }

    @Override
    protected boolean isValidIndex(int index, int bytesRequested) throws BufferBoundsException
    {
        return bytesRequested >= 0
                && index >= 0
                && (long)index + (long)bytesRequested - 1L < _length;
    }

    @Override
    protected void validateIndex(final int index, final int bytesRequested) throws BufferBoundsException
    {
        if (!isValidIndex(index, bytesRequested))
            throw new BufferBoundsException(index, bytesRequested, _length);
    }
}

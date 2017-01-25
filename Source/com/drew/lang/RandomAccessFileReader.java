/*
 * Copyright 2002-2017 Drew Noakes
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
import java.io.RandomAccessFile;

/**
 * Provides methods to read specific values from a {@link RandomAccessFile}, with a consistent, checked exception structure for
 * issues.
 *
 * @author Drew Noakes https://drewnoakes.com
 * */
public class RandomAccessFileReader extends RandomAccessReader
{
    @NotNull
    private final RandomAccessFile _file;
    private final long _length;
    private int _currentIndex;

    private final int _baseOffset;

    @SuppressWarnings({ "ConstantConditions" })
    @com.drew.lang.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Design intent")
    public RandomAccessFileReader(@NotNull RandomAccessFile file) throws IOException
    {
        this(file, 0);
    }

    @SuppressWarnings({ "ConstantConditions" })
    @com.drew.lang.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Design intent")
    public RandomAccessFileReader(@NotNull RandomAccessFile file, int baseOffset) throws IOException
    {
        if (file == null)
            throw new NullPointerException();

        _file = file;
        _baseOffset = baseOffset;
        _length = _file.length();
    }

    @Override
    public int toUnshiftedOffset(int localOffset)
    {
        return localOffset + _baseOffset;
    }

    @Override
    public long getLength()
    {
        return _length;
    }

    @Override
    public byte getByte(int index) throws IOException
    {
        if (index != _currentIndex)
            seek(index);

        final int b = _file.read();
        if (b < 0)
            throw new BufferBoundsException("Unexpected end of file encountered.");
        assert (b <= 0xff);
        _currentIndex++;
        return (byte)b;
    }

    @Override
    @NotNull
    public byte[] getBytes(int index, int count) throws IOException
    {
        validateIndex(index, count);

        if (index != _currentIndex)
            seek(index);

        byte[] bytes = new byte[count];
        final int bytesRead = _file.read(bytes);
        _currentIndex += bytesRead;
        if (bytesRead != count)
            throw new BufferBoundsException("Unexpected end of file encountered.");
        return bytes;
    }

    private void seek(final int index) throws IOException
    {
        if (index == _currentIndex)
            return;

        _file.seek(index);
        _currentIndex = index;
    }

    @Override
    protected boolean isValidIndex(int index, int bytesRequested) throws IOException
    {
        return bytesRequested >= 0
                && index >= 0
                && (long)index + (long)bytesRequested - 1L < _length;
    }

    @Override
    protected void validateIndex(final int index, final int bytesRequested) throws IOException
    {
        if (!isValidIndex(index, bytesRequested))
            throw new BufferBoundsException(index, bytesRequested, _length);
    }
}

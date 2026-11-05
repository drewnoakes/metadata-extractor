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
package com.drew.imaging.isobmff;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

/**
 * A view of another reader that ends at a box's payload end. Positions are those of the
 * underlying reader. Large reads grow their array in chunks, so a size that claims more than
 * the input holds costs no more memory than the input provides.
 */
final class BoundedSequentialReader extends SequentialReader
{
    private static final int CHUNK = 64 * 1024;

    @NotNull
    private final SequentialReader _reader;
    private final long _end;
    @NotNull
    private final IsoBox _box;

    BoundedSequentialReader(@NotNull SequentialReader reader, long end, @NotNull IsoBox box)
    {
        _reader = reader;
        _end = end;
        _box = box;
        setMotorolaByteOrder(reader.isMotorolaByteOrder());
    }

    private long remaining() throws IOException
    {
        return _end == -1 ? Long.MAX_VALUE : _end - _reader.getPosition();
    }

    /** End of input inside a payload of declared size is reported with the box it cuts off. */
    @NotNull
    private EOFException truncated(@NotNull EOFException e)
    {
        return _end == -1 ? e : IsoBoxIterator.truncated(_box);
    }

    private void require(long count) throws IOException
    {
        if (count > remaining())
            throw new IOException("Read of " + count + " bytes passes the end of the box payload");
    }

    @Override
    public long getPosition() throws IOException
    {
        return _reader.getPosition();
    }

    @Override
    public byte getByte() throws IOException
    {
        require(1);
        try {
            return _reader.getByte();
        } catch (EOFException e) {
            throw truncated(e);
        }
    }

    @Override
    @NotNull
    public byte[] getBytes(int count) throws IOException
    {
        require(count);
        try {
            if (count <= CHUNK)
                return _reader.getBytes(count);
            byte[] bytes = new byte[CHUNK];
            int read = 0;
            while (read < count) {
                if (read == bytes.length)
                    bytes = Arrays.copyOf(bytes, (int) Math.min(count, 2L * bytes.length));
                int n = bytes.length - read;
                _reader.getBytes(bytes, read, n);
                read += n;
            }
            return bytes;
        } catch (EOFException e) {
            throw truncated(e);
        }
    }

    @Override
    public void getBytes(@NotNull byte[] buffer, int offset, int count) throws IOException
    {
        require(count);
        try {
            _reader.getBytes(buffer, offset, count);
        } catch (EOFException e) {
            throw truncated(e);
        }
    }

    @Override
    public void skip(long n) throws IOException
    {
        require(n);
        if (!IsoBoxIterator.skipVerified(_reader, n))
            throw truncated(new EOFException("Unable to skip " + n + " bytes"));
    }

    @Override
    public boolean trySkip(long n) throws IOException
    {
        long remaining = remaining();
        if (n > remaining) {
            IsoBoxIterator.skipVerified(_reader, remaining);
            return false;
        }
        return IsoBoxIterator.skipVerified(_reader, n);
    }

    @Override
    public int available()
    {
        try {
            return (int) Math.min(_reader.available(), remaining());
        } catch (IOException e) {
            return 0;
        }
    }
}

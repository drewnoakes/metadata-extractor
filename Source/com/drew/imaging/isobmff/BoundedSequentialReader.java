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
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

/**
 * A view of another reader that ends at a box's payload end. Positions start at 0 where the
 * payload starts, whether it is read from the input or from memory.
 * <p>
 * A payload of up to {@value #SMALL_PAYLOAD} bytes is read in one piece on first access, so
 * reading its fields costs array accesses instead of calls on the input. If the input ends
 * inside it, what is there stays readable and reads beyond it report the truncation. Larger payloads are
 * read from the input as asked; large reads grow their array in chunks, so a size that claims
 * more than the input holds costs no more memory than the input provides.
 */
final class BoundedSequentialReader extends SequentialReader
{
    static final int SMALL_PAYLOAD = 16 * 1024;
    private static final int CHUNK = 64 * 1024;

    @NotNull
    private final SequentialReader _reader;
    private final long _start;
    private final long _end;
    @NotNull
    private final IsoBox _box;
    @Nullable
    private SequentialByteArrayReader _buffer;
    private long _bufferStart;
    @Nullable
    private byte[] _bufferArray;
    private boolean _decided;

    /** {@code start} and {@code end} are positions of {@code reader}; {@code end} is -1 for no limit. */
    BoundedSequentialReader(@NotNull SequentialReader reader, long start, long end, @NotNull IsoBox box)
    {
        _reader = reader;
        _start = start;
        _end = end;
        _box = box;
        setMotorolaByteOrder(reader.isMotorolaByteOrder());
    }

    /**
     * The rest of a payload that was read in one piece and not yet touched, without copying it,
     * or {@code null}. Consumes it.
     */
    @Nullable
    byte[] takeUntouched() throws IOException
    {
        source();
        if (_buffer == null || position() != _bufferStart || _bufferArray.length != _end - _bufferStart)
            return null;
        _buffer.skip(_bufferArray.length);
        return _bufferArray;
    }

    /**
     * The reader to hand out: the in-memory payload itself once it was read in one piece and is
     * complete, so reading past it means passing the payload end, not the end of input.
     */
    @NotNull
    SequentialReader direct() throws IOException
    {
        source();
        return _buffer != null && _bufferArray.length == _end - _bufferStart ? _buffer : this;
    }

    /**
     * The rest of the in-memory payload once read, from where reading has got to, with the
     * positions of the underlying reader, or {@code null}. For iterating the boxes inside it.
     */
    @Nullable
    SequentialReader inputView() throws IOException
    {
        if (_buffer == null)
            return null;
        return new OffsetByteArrayReader(_bufferArray, (int) (position() - _bufferStart), _bufferStart);
    }

    private SequentialReader source() throws IOException
    {
        if (!_decided) {
            _decided = true;
            long remaining = remaining();
            if (_end != -1 && remaining > 0 && remaining <= SMALL_PAYLOAD
                && !(_reader instanceof SequentialByteArrayReader)) {
                _bufferStart = _reader.getPosition();
                byte[] bytes = new byte[(int) remaining];
                int read = _reader.getBytesUpTo(bytes, 0, bytes.length);
                _bufferArray = read == bytes.length ? bytes : Arrays.copyOf(bytes, read);
                _buffer = new OffsetByteArrayReader(_bufferArray, 0, _bufferStart - _start);
                _buffer.setMotorolaByteOrder(isMotorolaByteOrder());
            }
        }
        return _buffer != null ? _buffer : _reader;
    }

    private long remaining() throws IOException
    {
        return _end == -1 ? Long.MAX_VALUE : _end - position();
    }

    /** End of input inside a payload of declared size is reported with the box it cuts off. */
    @NotNull
    private EOFException truncated(@NotNull EOFException e)
    {
        return _end == -1 ? new TruncatedInputException(String.valueOf(e.getMessage())) : IsoBoxIterator.truncated(_box);
    }

    private void require(long count) throws IOException
    {
        if (count > remaining())
            throw new EOFException("Read of " + count + " bytes passes the end of the box payload");
    }

    /** The position in the underlying reader. */
    private long position() throws IOException
    {
        return _buffer != null ? _buffer.getPosition() + _start : _reader.getPosition();
    }

    @Override
    public long getPosition() throws IOException
    {
        return position() - _start;
    }

    @Override
    public void setMotorolaByteOrder(boolean motorolaByteOrder)
    {
        super.setMotorolaByteOrder(motorolaByteOrder);
        if (_buffer != null)
            _buffer.setMotorolaByteOrder(motorolaByteOrder);
    }

    @Override
    public byte getByte() throws IOException
    {
        if (_buffer != null) {
            try {
                return _buffer.getByte();
            } catch (EOFException e) {
                require(1);
                throw truncated(e);
            }
        }
        require(1);
        try {
            return source().getByte();
        } catch (EOFException e) {
            throw truncated(e);
        }
    }

    @Override
    @NotNull
    public byte[] getBytes(int count) throws IOException
    {
        require(count);
        SequentialReader source = source();
        try {
            if (count <= CHUNK || _buffer != null)
                return source.getBytes(count);
            byte[] bytes = new byte[CHUNK];
            int read = 0;
            while (read < count) {
                if (read == bytes.length)
                    bytes = Arrays.copyOf(bytes, (int) Math.min(count, 2L * bytes.length));
                int n = bytes.length - read;
                source.getBytes(bytes, read, n);
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
            source().getBytes(buffer, offset, count);
        } catch (EOFException e) {
            throw truncated(e);
        }
    }

    @Override
    public int getBytesUpTo(@NotNull byte[] buffer, int offset, int count) throws IOException
    {
        return source().getBytesUpTo(buffer, offset, (int) Math.min(count, remaining()));
    }

    @Override
    public void skip(long n) throws IOException
    {
        if (n < 0)
            throw new IllegalArgumentException("n must be zero or greater.");
        require(n);
        SequentialReader source = source();
        if (_buffer != null) {
            try {
                source.skip(n);
            } catch (EOFException e) {
                throw truncated(e);
            }
            return;
        }
        if (!IsoBoxIterator.skipVerified(source, n))
            throw truncated(new EOFException("Unable to skip " + n + " bytes"));
    }

    @Override
    public boolean trySkip(long n) throws IOException
    {
        if (n < 0)
            throw new IllegalArgumentException("n must be zero or greater.");
        long remaining = remaining();
        SequentialReader source = source();
        if (_buffer != null)
            return source.trySkip(Math.min(n, remaining)) && n <= remaining;
        if (n > remaining) {
            IsoBoxIterator.skipVerified(source, remaining);
            return false;
        }
        return IsoBoxIterator.skipVerified(source, n);
    }

    /** Grows its array as bytes arrive, so a large {@code maxLengthBytes} costs nothing up front. */
    @Override
    @NotNull
    public byte[] getNullTerminatedBytes(int maxLengthBytes, boolean moveToMaxLength) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int read = 0;
        while (read < maxLengthBytes) {
            byte value = getByte();
            read++;
            if (value == 0)
                break;
            bytes.write(value);
        }
        if (moveToMaxLength && read < maxLengthBytes)
            skip(maxLengthBytes - read);
        return bytes.toByteArray();
    }

    @Override
    public int available()
    {
        try {
            return (int) Math.min(_buffer != null ? _buffer.available() : _reader.available(), remaining());
        } catch (IOException e) {
            return 0;
        }
    }
}

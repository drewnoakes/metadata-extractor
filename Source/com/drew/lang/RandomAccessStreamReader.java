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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class RandomAccessStreamReader implements RandomAccessReader
{
    private final static int _chunkLength = 2 * 1024;

    @NotNull
    private final InputStream _stream;

    private final ArrayList<byte[]> _chunks = new ArrayList<byte[]>();

    private boolean _isMotorolaByteOrder = true;
    private boolean _isStreamFinished;
    private int _streamLength;

    public RandomAccessStreamReader(@NotNull InputStream stream)
    {
        if (stream == null)
            throw new NullPointerException();

        _stream = stream;
    }

    /**
     * Reads to the end of the stream, in order to determine the total number of bytes.
     * In general, this is not a good idea for this implementation of {@link RandomAccessReader}.
     *
     * @return the length of the data source, in bytes.
     */
    @Override
    public long getLength() throws BufferBoundsException
    {
        isValidIndex(Integer.MAX_VALUE, 1);
        assert(_isStreamFinished);
        return _streamLength;
    }

    @Override
    public void setMotorolaByteOrder(boolean motorolaByteOrder)
    {
        _isMotorolaByteOrder = motorolaByteOrder;
    }

    @Override
    public boolean isMotorolaByteOrder()
    {
        return _isMotorolaByteOrder;
    }

    /**
     * Ensures that the buffered bytes extend to cover the specified index. If not, an attempt is made
     * to read to that point.
     * <p/>
     * If the stream ends before the point is reached, a {@link BufferBoundsException} is raised.
     *
     * @param index the index from which the required bytes start
     * @param bytesRequested the number of bytes which are required
     * @throws BufferBoundsException if the stream ends before the required number of bytes are acquired
     */
    private void validateIndex(int index, int bytesRequested) throws BufferBoundsException
    {
        if (index < 0) {
            throw new BufferBoundsException(String.format("Attempt to read from buffer using a negative index (%d)", index));
        } else if (bytesRequested < 0) {
            throw new BufferBoundsException("Number of requested bytes must be zero or greater");
        } else if ((long)index + bytesRequested - 1 > Integer.MAX_VALUE) {
            throw new BufferBoundsException("Number of requested bytes summed with starting index exceed maximum range of signed 32 bit integers");
        }

        if (!isValidIndex(index, bytesRequested)) {
            assert(_isStreamFinished);
            // TODO test that can continue using an instance of this type after this exception
            throw new BufferBoundsException(_streamLength, index, bytesRequested);
        }
    }

    private boolean isValidIndex(int index, int bytesRequested) throws BufferBoundsException
    {
        if (index < 0 || bytesRequested < 0) {
            return false;
        }

        long endIndexLong = (long)index + bytesRequested - 1;

        if (endIndexLong > Integer.MAX_VALUE) {
            return false;
        }

        int endIndex = (int)endIndexLong;

        if (_isStreamFinished) {
            return endIndex < _streamLength;
        }

        int chunkIndex = endIndex / _chunkLength;

        // TODO test loading several chunks for a single request
        while (chunkIndex >= _chunks.size()) {
            assert (!_isStreamFinished);

            byte[] chunk = new byte[_chunkLength];
            int totalBytesRead = 0;
            while (!_isStreamFinished && totalBytesRead != _chunkLength) {
                int bytesRead;
                try {
                    bytesRead = _stream.read(chunk, totalBytesRead, _chunkLength - totalBytesRead);
                } catch (IOException e) {
                    throw new BufferBoundsException("IOException reading from stream", e);
                }
                if (bytesRead == -1) {
                    // the stream has ended, which may be ok
                    _isStreamFinished = true;
                    _streamLength = _chunks.size() * _chunkLength + totalBytesRead;

                    // check we have enough bytes for the requested index
                    if (endIndex >= _streamLength) {
                        return false;
                    }
                } else {
                    totalBytesRead += bytesRead;
                }
            }

            _chunks.add(chunk);
        }

        return true;
    }

    private byte getByte(int index)
    {
        assert(index >= 0);

        final int chunkIndex = index / _chunkLength;
        final int innerIndex = index % _chunkLength;
        final byte[] chunk = _chunks.get(chunkIndex);

        return chunk[innerIndex];
    }

    @Override
    public short getUInt8(int index) throws BufferBoundsException
    {
        validateIndex(index, 1);

        return (short) (getByte(index) & 0xFF);
    }

    @Override
    public byte getInt8(int index) throws BufferBoundsException
    {
        validateIndex(index, 1);

        return getByte(index);
    }

    @Override
    public int getUInt16(int index) throws BufferBoundsException
    {
        validateIndex(index, 2);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first
            return (getByte(index    ) << 8 & 0xFF00) |
                   (getByte(index + 1)      & 0xFF);
        } else {
            // Intel ordering - LSB first
            return (getByte(index + 1) << 8 & 0xFF00) |
                   (getByte(index    )      & 0xFF);
        }
    }

    @Override
    public short getInt16(int index) throws BufferBoundsException
    {
        validateIndex(index, 2);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first
            return (short) (((short)getByte(index    ) << 8 & (short)0xFF00) |
                            ((short)getByte(index + 1)      & (short)0xFF));
        } else {
            // Intel ordering - LSB first
            return (short) (((short)getByte(index + 1) << 8 & (short)0xFF00) |
                            ((short)getByte(index    )      & (short)0xFF));
        }
    }

    @Override
    public long getUInt32(int index) throws BufferBoundsException
    {
        validateIndex(index, 4);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (((long)getByte(index    )) << 24 & 0xFF000000L) |
                   (((long)getByte(index + 1)) << 16 & 0xFF0000L) |
                   (((long)getByte(index + 2)) << 8  & 0xFF00L) |
                   (((long)getByte(index + 3))       & 0xFFL);
        } else {
            // Intel ordering - LSB first (little endian)
            return (((long)getByte(index + 3)) << 24 & 0xFF000000L) |
                   (((long)getByte(index + 2)) << 16 & 0xFF0000L) |
                   (((long)getByte(index + 1)) << 8  & 0xFF00L) |
                   (((long)getByte(index    ))       & 0xFFL);
        }
    }

    @Override
    public int getInt32(int index) throws BufferBoundsException
    {
        validateIndex(index, 4);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (getByte(index    ) << 24 & 0xFF000000) |
                   (getByte(index + 1) << 16 & 0xFF0000) |
                   (getByte(index + 2) << 8  & 0xFF00) |
                   (getByte(index + 3)       & 0xFF);
        } else {
            // Intel ordering - LSB first (little endian)
            return (getByte(index + 3) << 24 & 0xFF000000) |
                   (getByte(index + 2) << 16 & 0xFF0000) |
                   (getByte(index + 1) << 8  & 0xFF00) |
                   (getByte(index    )       & 0xFF);
        }
    }

    @Override
    public long getInt64(int index) throws BufferBoundsException
    {
        validateIndex(index, 8);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first
            return ((long)getByte(index    ) << 56 & 0xFF00000000000000L) |
                   ((long)getByte(index + 1) << 48 & 0xFF000000000000L) |
                   ((long)getByte(index + 2) << 40 & 0xFF0000000000L) |
                   ((long)getByte(index + 3) << 32 & 0xFF00000000L) |
                   ((long)getByte(index + 4) << 24 & 0xFF000000L) |
                   ((long)getByte(index + 5) << 16 & 0xFF0000L) |
                   ((long)getByte(index + 6) << 8  & 0xFF00L) |
                   ((long)getByte(index + 7)       & 0xFFL);
        } else {
            // Intel ordering - LSB first
            return ((long)getByte(index + 7) << 56 & 0xFF00000000000000L) |
                   ((long)getByte(index + 6) << 48 & 0xFF000000000000L) |
                   ((long)getByte(index + 5) << 40 & 0xFF0000000000L) |
                   ((long)getByte(index + 4) << 32 & 0xFF00000000L) |
                   ((long)getByte(index + 3) << 24 & 0xFF000000L) |
                   ((long)getByte(index + 2) << 16 & 0xFF0000L) |
                   ((long)getByte(index + 1) << 8  & 0xFF00L) |
                   ((long)getByte(index    )       & 0xFFL);
        }
    }

    @Override
    public float getS15Fixed16(int index) throws BufferBoundsException
    {
        validateIndex(index, 4);

        if (_isMotorolaByteOrder) {
            float res = (getByte(index    ) & 0xFF) << 8 |
                        (getByte(index + 1) & 0xFF);
            int d =     (getByte(index + 2) & 0xFF) << 8 |
                        (getByte(index + 3) & 0xFF);
            return (float)(res + d/65536.0);
        } else {
            // this particular branch is untested
            float res = (getByte(index + 3) & 0xFF) << 8 |
                        (getByte(index + 2) & 0xFF);
            int d =     (getByte(index + 1) & 0xFF) << 8 |
                        (getByte(index    ) & 0xFF);
            return (float)(res + d/65536.0);
        }
    }

    @Override
    public float getFloat32(int index) throws BufferBoundsException
    {
        return Float.intBitsToFloat(getInt32(index));
    }

    @Override
    public double getDouble64(int index) throws BufferBoundsException
    {
        return Double.longBitsToDouble(getInt64(index));
    }

    @NotNull
    @Override
    public byte[] getBytes(int index, int count) throws BufferBoundsException
    {
        validateIndex(index, count);

        byte[] bytes = new byte[count];

        int remaining = count;
        int fromIndex = index;
        int toIndex = 0;

        while (remaining != 0) {
            int fromChunkIndex = fromIndex / _chunkLength;
            int fromInnerIndex = fromIndex % _chunkLength;
            int length = Math.min(remaining, _chunkLength - fromInnerIndex);

            byte[] chunk = _chunks.get(fromChunkIndex);

            System.arraycopy(chunk, fromInnerIndex, bytes, toIndex, length);

            remaining -= length;
            fromIndex += length;
            toIndex += length;
        }

        return bytes;
    }

    @NotNull
    @Override
    public String getString(int index, int bytesRequested) throws BufferBoundsException
    {
        return new String(getBytes(index, bytesRequested));
    }

    @NotNull
    @Override
    public String getString(int index, int bytesRequested, String charset) throws BufferBoundsException
    {
        byte[] bytes = getBytes(index, bytesRequested);
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(bytes);
        }
    }

    @NotNull
    @Override
    public String getNullTerminatedString(int index, int maxLengthBytes) throws BufferBoundsException
    {
        // NOTE currently only really suited to single-byte character strings

        byte[] bytes = getBytes(index, maxLengthBytes);

        // Check for null terminators
        int length = 0;
        while (length < bytes.length && bytes[length] != '\0' && length < maxLengthBytes)
            length++;

//        byte[] outputBytes = new byte[length];
//        System.arraycopy(bytes, 0, outputBytes, 0, length);

        return new String(bytes, 0, length);
    }
}

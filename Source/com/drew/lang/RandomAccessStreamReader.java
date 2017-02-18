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
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class RandomAccessStreamReader extends RandomAccessReader
{
    public final static int DEFAULT_CHUNK_LENGTH = 2 * 1024;

    @NotNull
    private final InputStream _stream;
    private final int _chunkLength;

    private final ArrayList<byte[]> _chunks = new ArrayList<byte[]>();

    private boolean _isStreamFinished;
    private long _streamLength;

    public RandomAccessStreamReader(@NotNull InputStream stream)
    {
        this(stream, DEFAULT_CHUNK_LENGTH, -1);
    }

    public RandomAccessStreamReader(@NotNull InputStream stream, int chunkLength)
    {
        this(stream, chunkLength, -1);
    }

    public RandomAccessStreamReader(@NotNull InputStream stream, int chunkLength, long streamLength)
    {
        if (stream == null)
            throw new NullPointerException();
        if (chunkLength <= 0)
            throw new IllegalArgumentException("chunkLength must be greater than zero");

        _chunkLength = chunkLength;
        _stream = stream;
        _streamLength = streamLength;
    }

    /**
     * Reads to the end of the stream, in order to determine the total number of bytes.
     * In general, this is not a good idea for this implementation of {@link RandomAccessReader}.
     *
     * @return the length of the data source, in bytes.
     */
    @Override
    public long getLength() throws IOException
    {
        if (_streamLength != -1) {
            return _streamLength;
        }

        isValidIndex(Integer.MAX_VALUE, 1);
        assert(_isStreamFinished);
        return _streamLength;
    }

    /**
     * Ensures that the buffered bytes extend to cover the specified index. If not, an attempt is made
     * to read to that point.
     * <p>
     * If the stream ends before the point is reached, a {@link BufferBoundsException} is raised.
     *
     * @param index the index from which the required bytes start
     * @param bytesRequested the number of bytes which are required
     * @throws BufferBoundsException if the stream ends before the required number of bytes are acquired
     */
    @Override
    protected void validateIndex(int index, int bytesRequested) throws IOException
    {
        if (index < 0) {
            throw new BufferBoundsException(String.format("Attempt to read from buffer using a negative index (%d)", index));
        } else if (bytesRequested < 0) {
            throw new BufferBoundsException("Number of requested bytes must be zero or greater");
        } else if ((long)index + bytesRequested - 1 > Integer.MAX_VALUE) {
            throw new BufferBoundsException(String.format("Number of requested bytes summed with starting index exceed maximum range of signed 32 bit integers (requested index: %d, requested count: %d)", index, bytesRequested));
        }

        if (!isValidIndex(index, bytesRequested)) {
            assert(_isStreamFinished);
            // TODO test that can continue using an instance of this type after this exception
            throw new BufferBoundsException(index, bytesRequested, _streamLength);
        }
    }

    @Override
    protected boolean isValidIndex(int index, int bytesRequested) throws IOException
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
                int bytesRead = _stream.read(chunk, totalBytesRead, _chunkLength - totalBytesRead);
                if (bytesRead == -1) {
                    // the stream has ended, which may be ok
                    _isStreamFinished = true;
                    int observedStreamLength = _chunks.size() * _chunkLength + totalBytesRead;
                    if (_streamLength == -1) {
                        _streamLength = observedStreamLength;
                    } else if (_streamLength != observedStreamLength) {
                        assert(false);
                    }

                    // check we have enough bytes for the requested index
                    if (endIndex >= _streamLength) {
                        _chunks.add(chunk);
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

    @Override
    public int toUnshiftedOffset(int localOffset)
    {
        return localOffset;
    }

    @Override
    public byte getByte(int index) throws IOException
    {
        assert(index >= 0);

        final int chunkIndex = index / _chunkLength;
        final int innerIndex = index % _chunkLength;
        final byte[] chunk = _chunks.get(chunkIndex);

        return chunk[innerIndex];
    }

    @NotNull
    @Override
    public byte[] getBytes(int index, int count) throws IOException
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
}

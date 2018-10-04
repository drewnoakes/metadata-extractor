/*
 * Copyright 2002-2017 Drew Noakes
 * Copyright 2018 Kevin Mott
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
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.HashMap;
/**
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
public class RandomAccessStream
{
    private final RandomAccessFile _raFile;
    private final InputStream _inputStream;
    
    private long _streamLength = -1;
    
    private boolean _canSeek = false;
    private boolean _isStreamFinished;
    
    public final static int DEFAULT_CHUNK_LENGTH = 2 * 1024;
    private final int _chunkLength;
    
    @NotNull
    public final HashMap<Long, byte[]> _chunks = new HashMap<Long, byte[]>();
    
    private long _totalBytesRead = 0;
    
    
    public RandomAccessStream(@NotNull RandomAccessFile raFile) throws IOException
    {
        if (raFile == null)
            throw new NullPointerException();

        _raFile = raFile;
        _canSeek = true;
        _chunkLength = DEFAULT_CHUNK_LENGTH;
        
        _streamLength = raFile.length();
        
        _inputStream = null;
    }

    public RandomAccessStream(@NotNull InputStream stream, long streamLength)
    {
        if (stream == null)
            throw new NullPointerException();
        if (streamLength <= 0L)
            throw new IllegalArgumentException("streamLength must be greater than zero");
        
        _inputStream = stream;
        _canSeek = false;
        _streamLength = streamLength;
        
        _chunkLength = DEFAULT_CHUNK_LENGTH;
        
        _raFile = null;
    }
    
    public RandomAccessStream(@NotNull byte[] bytes)
    {
        if (bytes == null)
            throw new NullPointerException();

        _canSeek = true;

        _chunks.put(0L, bytes);
        _chunkLength = bytes.length;

        _streamLength = bytes.length;
        _isStreamFinished = true;
        
        _raFile = null;
        _inputStream = null;
    }
    
    public boolean canSeek()
    {
        return _canSeek;
    }
    
    public long getLength()
    {
        return _streamLength;
    }
    
    public ReaderInfo createReader()
    {
        return createReader(-1L, -1L, true);
    }
    public ReaderInfo createReader(boolean isMotorolaByteOrder)
    {
        return createReader(-1L, -1L, isMotorolaByteOrder);
    }
    public ReaderInfo createReader(long startPosition, long length, boolean isMotorolaByteOrder)
    {
        long pos = startPosition >= 0 ? startPosition : 0;

        //var rdrInfo = new ReaderInfo(this, pos, 0, length, isMotorolaByteOrder);
        //rdrList.Add(rdrInfo);
        //return rdrInfo;
        return new ReaderInfo(this, pos, 0L, length, isMotorolaByteOrder);
    }

    /**
     * Retrieves bytes, writing them into a caller-provided buffer. 
     * SequentialFlag as index indicates this call should read sequentially
     *
     * @param index position within the data buffer to read bytes
     * @param buffer array to write bytes to
     * @param offset starting position with buffer to write to
     * @param count The number of bytes to be written
     * @param isSequential flag indicating if caller is using sequential access
     * @return The requested bytes
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public int read(long index, byte[] buffer, int offset, int count, boolean isSequential) throws IOException, EOFException, BufferBoundsException
    {
        return read(index, buffer, offset, count, isSequential, true);
    }
    
    /**
     * Retrieves bytes, writing them into a caller-provided buffer. 
     * SequentialFlag as index indicates this call should read sequentially
     *
     * @param index position within the data buffer to read bytes
     * @param buffer array to write bytes to
     * @param offset starting position with buffer to write to
     * @param count The number of bytes to be written
     * @param isSequential flag indicating if caller is using sequential access
     * @param allowPartial flag indicating if fewer than count bytes can be returned
     * @return The requested bytes
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public int read(long index, byte[] buffer, int offset, int count, boolean isSequential, boolean allowPartial) throws IOException, EOFException, BufferBoundsException
    {
        count = (int)validateIndex(index, count, isSequential, allowPartial);

        // This bypasses a lot of checks particularly when the input was a byte[]
        if (_isStreamFinished && _chunks.size() == 1)
        {
            System.arraycopy(_chunks.get(0L), (int)index, buffer, 0, count);
            return count;
        }

        int remaining = count;                      // how many bytes are requested
        int fromOffset = (int)index;
        int toIndex = offset > 0 ? offset : 0;
        while (remaining != 0)
        {
            int fromChunkIndex = fromOffset / _chunkLength;     // chunk integer key
            int fromInnerIndex = fromOffset % _chunkLength;     // index inside the chunk to start reading
            int length = Math.min(remaining, _chunkLength - fromInnerIndex);
            byte[] chunk = _chunks.get((long)fromChunkIndex);
            System.arraycopy(chunk, fromInnerIndex, buffer, toIndex, length);
            remaining -= length;
            fromOffset += length;
            toIndex += length;
        }

        return toIndex - offset;
    }

    /**
     * Gets the byte value at the specified byte <code>index</code>.
     * <p>
     * Implementations should not perform any bounds checking in this method. That should be performed
     * in <code>validateIndex</code> and <code>bytesAvailable</code>.
     *
     * @param index The index from which to read the byte
     * @param isSequential flag indicating if caller is using sequential access
     * @return The read byte value
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public byte getByte(long index, boolean isSequential) throws IOException, EOFException, BufferBoundsException
    {
        validateIndex(index, 1, isSequential);

        // This bypasses a lot of checks particularly when the input was a byte[]
        if (_isStreamFinished && _chunks.size() == 1)
            return _chunks.get(0L)[(int)index];

        long chunkIndex = index / _chunkLength;
        long innerIndex = index % _chunkLength;

        if (_chunks.containsKey(chunkIndex))
            return (byte)_chunks.get(chunkIndex)[(int)innerIndex];
        else
            return (byte)255; // unchecked((byte)-1);
    }

    /**
     * Returns an unsigned 16-bit int calculated from two bytes of data at the specified index.
     *
     * @param index position within the data buffer to read first byte
     * @param isMotorolaByteOrder <code>true</code> for Motorola/big endian, <code>false</code> for Intel/little endian
     * @param isSequential flag indicating if caller is using sequential access
     * @return the 16 bit int value, between 0x0000 and 0xFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public int getUInt16(long index, boolean isMotorolaByteOrder, boolean isSequential) throws IOException, EOFException, BufferBoundsException
    {
        byte[] bytes = new byte[2];
        read(index, bytes, 0, bytes.length, isSequential, false);

        if (isMotorolaByteOrder) {
            // Motorola - MSB first
            return (bytes[0] << 8 & 0xFF00) |
                   (bytes[1]      & 0xFF);
        } else {
            // Intel ordering - LSB first
            return (bytes[1] << 8 & 0xFF00) |
                   (bytes[0]      & 0xFF);
        }
    }

    /**
     * Returns a signed 16-bit int calculated from two bytes of data at the specified index.
     *
     * @param index position within the data buffer to read first byte
     * @param isMotorolaByteOrder <code>true</code> for Motorola/big endian, <code>false</code> for Intel/little endian
     * @param isSequential flag indicating if caller is using sequential access
     * @return the 16 bit int value, between 0x0000 and 0xFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public short getInt16(long index, boolean isMotorolaByteOrder, boolean isSequential) throws IOException, EOFException, BufferBoundsException
    {
        byte[] bytes = new byte[2];
        read(index, bytes, 0, bytes.length, isSequential, false);

        if (isMotorolaByteOrder) {
            // Motorola - MSB first
            return (short) (((short)bytes[0] << 8 & (short)0xFF00) |
                            ((short)bytes[1]      & (short)0xFF));
        } else {
            // Intel ordering - LSB first
            return (short) (((short)bytes[1] << 8 & (short)0xFF00) |
                            ((short)bytes[0]      & (short)0xFF));
        }
    }

    /**
     * Get a 24-bit unsigned integer from the buffer, returning it as an int.
     *
     * @param index position within the data buffer to read first byte
     * @param isMotorolaByteOrder <code>true</code> for Motorola/big endian, <code>false</code> for Intel/little endian
     * @param isSequential flag indicating if caller is using sequential access
     * @return the unsigned 24-bit int value as a long, between 0x00000000 and 0x00FFFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public int getInt24(long index, boolean isMotorolaByteOrder, boolean isSequential) throws IOException, EOFException, BufferBoundsException
    {
        byte[] bytes = new byte[3];
        read(index, bytes, 0, bytes.length, isSequential, false);

        if (isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (((int)bytes[0]) << 16 & 0xFF0000) |
                   (((int)bytes[1]) << 8  & 0xFF00) |
                   (((int)bytes[2])       & 0xFF);
        } else {
            // Intel ordering - LSB first (little endian)
            return (((int)bytes[2]) << 16 & 0xFF0000) |
                   (((int)bytes[1]) << 8  & 0xFF00) |
                   (((int)bytes[0])       & 0xFF);
        }
    }

    /**
     * Get a 32-bit unsigned integer from the buffer, returning it as a long.
     *
     * @param index position within the data buffer to read first byte
     * @param isMotorolaByteOrder <code>true</code> for Motorola/big endian, <code>false</code> for Intel/little endian
     * @param isSequential flag indicating if caller is using sequential access
     * @return the unsigned 32-bit int value as a long, between 0x00000000 and 0xFFFFFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public long getUInt32(long index, boolean isMotorolaByteOrder, boolean isSequential) throws IOException, EOFException, BufferBoundsException
    {
        byte[] bytes = new byte[4];
        read(index, bytes, 0, bytes.length, isSequential, false);

        if (isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (((long)bytes[0]) << 24 & 0xFF000000L) |
                   (((long)bytes[1]) << 16 & 0xFF0000L) |
                   (((long)bytes[2]) << 8  & 0xFF00L) |
                   (((long)bytes[3])       & 0xFFL);
        } else {
            // Intel ordering - LSB first (little endian)
            return (((long)bytes[3]) << 24 & 0xFF000000L) |
                   (((long)bytes[2]) << 16 & 0xFF0000L) |
                   (((long)bytes[1]) << 8  & 0xFF00L) |
                   (((long)bytes[0])       & 0xFFL);
        }
    }

    /**
     * Get a 32-bit signed integer from the buffer, returning it as a long.
     *
     * @param index position within the data buffer to read first byte
     * @param isMotorolaByteOrder <code>true</code> for Motorola/big endian, <code>false</code> for Intel/little endian
     * @param isSequential flag indicating if caller is using sequential access
     * @return the signed 32-bit int value as a long, between 0x00000000 and 0xFFFFFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public int getInt32(long index, boolean isMotorolaByteOrder, boolean isSequential) throws IOException, EOFException, BufferBoundsException
    {
        byte[] bytes = new byte[4];
        read(index, bytes, 0, bytes.length, isSequential, false);

        if (isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (bytes[0] << 24 & 0xFF000000) |
                   (bytes[1] << 16 & 0xFF0000) |
                   (bytes[2] << 8  & 0xFF00) |
                   (bytes[3]       & 0xFF);
        } else {
            // Intel ordering - LSB first (little endian)
            return (bytes[3] << 24 & 0xFF000000) |
                   (bytes[2] << 16 & 0xFF0000) |
                   (bytes[1] << 8  & 0xFF00) |
                   (bytes[0]       & 0xFF);
        }
    }

    /**
     * Get a signed 64-bit integer from the buffer.
     *
     * @param index position within the data buffer to read first byte
     * @param isMotorolaByteOrder <code>true</code> for Motorola/big endian, <code>false</code> for Intel/little endian
     * @param isSequential flag indicating if caller is using sequential access
     * @return the 64 bit int value, between 0x0000000000000000 and 0xFFFFFFFFFFFFFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public long getInt64(long index, boolean isMotorolaByteOrder, boolean isSequential) throws IOException, EOFException, BufferBoundsException
    {
        byte[] bytes = new byte[8];
        read(index, bytes, 0, bytes.length, isSequential, false);

        if (isMotorolaByteOrder) {
            // Motorola - MSB first
            return ((long)bytes[0] << 56 & 0xFF00000000000000L) |
                   ((long)bytes[1] << 48 & 0xFF000000000000L) |
                   ((long)bytes[2] << 40 & 0xFF0000000000L) |
                   ((long)bytes[3] << 32 & 0xFF00000000L) |
                   ((long)bytes[4] << 24 & 0xFF000000L) |
                   ((long)bytes[5] << 16 & 0xFF0000L) |
                   ((long)bytes[6] << 8  & 0xFF00L) |
                   ((long)bytes[7]       & 0xFFL);
        } else {
            // Intel ordering - LSB first
            return ((long)bytes[7] << 56 & 0xFF00000000000000L) |
                   ((long)bytes[6] << 48 & 0xFF000000000000L) |
                   ((long)bytes[5] << 40 & 0xFF0000000000L) |
                   ((long)bytes[4] << 32 & 0xFF00000000L) |
                   ((long)bytes[3] << 24 & 0xFF000000L) |
                   ((long)bytes[2] << 16 & 0xFF0000L) |
                   ((long)bytes[1] << 8  & 0xFF00L) |
                   ((long)bytes[0]       & 0xFFL);
        }
    }

    /**
     * Gets a s15.16 fixed point float from the buffer.
     * <p>
     * This particular fixed point encoding has one sign bit, 15 numerator bits and 16 denominator bits.
     * @param index position within the data buffer to read first byte
     * @param isMotorolaByteOrder <code>true</code> for Motorola/big endian, <code>false</code> for Intel/little endian
     * @param isSequential flag indicating if caller is using sequential access
     * @return the floating point value
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public float getS15Fixed16(long index, boolean isMotorolaByteOrder, boolean isSequential) throws IOException, EOFException, BufferBoundsException
    {
        byte[] bytes = new byte[4];
        read(index, bytes, 0, bytes.length, isSequential, false);

        if (isMotorolaByteOrder) {
            float res = (bytes[0] & 0xFF) << 8 |
                        (bytes[1] & 0xFF);
            int d =     (bytes[2] & 0xFF) << 8 |
                        (bytes[3] & 0xFF);
            return (float)(res + d/65536.0);
        } else {
            // this particular branch is untested
            float res = (bytes[3] & 0xFF) << 8 |
                        (bytes[2] & 0xFF);
            int d =     (bytes[1] & 0xFF) << 8 |
                        (bytes[0] & 0xFF);
            return (float)(res + d/65536.0);
        }
    }
    
    /**
     * Skips to a specific index in the sequence. If the sequence ends, an {@link EOFException} is thrown.
     *
     * @param index the number of bytes to skip.
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public void seek(final long index) throws IOException, EOFException, BufferBoundsException
    {
        validateIndex((index == 0) ? 0 : (index - 1), 1L, false);
    }

    /**
     * Ensures that the buffered bytes extend to cover the specified index. If not, an attempt is made
     * to read to that point.
     * <p>
     * If the stream ends before the point is reached, a {@link BufferBoundsException} is raised.
     *
     * @param index the index from which the required bytes start
     * @param bytesRequested the number of bytes which are required
     * @param isSequential flag indicating if caller is using sequential access
     * @return count of bytes available at the given index
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public long validateIndex(long index, long bytesRequested, boolean isSequential) throws IOException, EOFException, BufferBoundsException
    {
        return validateIndex(index, bytesRequested, isSequential, false);
    }
    
    /**
     * Ensures that the buffered bytes extend to cover the specified index. If not, an attempt is made
     * to read to that point.
     * <p>
     * If the stream ends before the point is reached, a {@link BufferBoundsException} might be raised.
     *
     * @param index the index from which the required bytes start
     * @param bytesRequested the number of bytes which are required
     * @param isSequential flag indicating if caller is using sequential access
     * @param allowPartial flag indicating if fewer than count bytes can be returned
     * @return count of bytes available at the given index
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public long validateIndex(long index, long bytesRequested, boolean isSequential, boolean allowPartial) throws IOException, EOFException, BufferBoundsException
    {
        long available = bytesAvailable(index, bytesRequested);
        if(available != bytesRequested && !allowPartial)
        {
            if(index < 0)
                throw new BufferBoundsException("Attempt to read from buffer using a negative index (" + ((Long)index).toString() + ")");
            if (bytesRequested < 0)
                throw new BufferBoundsException("Number of requested bytes must be zero or greater");
            if (index + bytesRequested - 1 > Integer.MAX_VALUE)
                throw new BufferBoundsException("Number of requested bytes summed with starting index exceed maximum range of signed 32 bit integers (requested index: " + ((Long)index).toString() + ", requested count: " + ((Long)bytesRequested).toString() + ")");
            if (index + bytesRequested >= _streamLength && isSequential)
                throw new EOFException("End of data reached.");

            // TODO test that can continue using an instance of this type after this exception
            throw new BufferBoundsException((int)index, (int)bytesRequested, _streamLength);
        }
        
        return available;
    }

    /**
     * Determines how many bytes of bytesRequested are available at index
     * @param index the index from which the required bytes start
     * @param bytesRequested the number of bytes which are required
     * @return number of bytes available on and after the given index
     * @throws IOException
     */
    private long bytesAvailable(long index, long bytesRequested) throws IOException
    {
        if (index < 0 || bytesRequested < 0)
            return 0;

        // if there's only one chunk, there's no need to calculate anything.
        // This bypasses a lot of checks particularly when the input was a byte[]
        if (_isStreamFinished && _chunks.size() == 1)
        {
            if ((index + bytesRequested) < _streamLength)
                return bytesRequested;
            else if (index > _streamLength)
                return 0;
            else
                return _streamLength - index;
        }

        long endIndex = index + bytesRequested - 1;
        if (endIndex < 0) endIndex = 0;

        // Maybe don't check this?
        if (endIndex > Integer.MAX_VALUE)
            return 0;

        // zero-based
        long chunkstart = index / _chunkLength;
        long chunkend = ((index + bytesRequested) / _chunkLength) + 1;


        if (!_chunks.containsKey(chunkstart))
        {
            if(!_canSeek)
                chunkstart = _chunks.isEmpty() ? 0 : Collections.max(_chunks.keySet()) + 1;
        }

        for (long i = chunkstart; i < chunkend; i++)
        {
            if (!_chunks.containsKey(i))
            {
                _isStreamFinished = false;

                
                // chunkstart can be anywhere. Try to seek
                if (_canSeek && _raFile != null)
                    _raFile.seek(i * _chunkLength);

                byte[] chunk = new byte[_chunkLength];

                int totalBytesRead = 0;
                while (!_isStreamFinished && totalBytesRead != _chunkLength)
                {
                    int bytesRead;
                    if(_canSeek && _raFile != null)
                        bytesRead = _raFile.read(chunk, totalBytesRead, _chunkLength - totalBytesRead);
                    else
                        bytesRead = _inputStream.read(chunk, totalBytesRead, _chunkLength - totalBytesRead);

                    if (bytesRead == -1)
                    {
                        // the stream has ended, which may be ok
                        _isStreamFinished = true;
                        _streamLength = i * _chunkLength + totalBytesRead;

                        // check we have enough bytes for the requested index
                        if (endIndex >= _streamLength)
                        {
                            _totalBytesRead += totalBytesRead;
                            _chunks.put(i, chunk);
                            return (index + bytesRequested) <= _streamLength ? bytesRequested : _streamLength - index;
                        }
                    }
                    else
                    {
                        totalBytesRead += bytesRead;
                    }
                }

                _totalBytesRead += totalBytesRead;
                _chunks.put(i, chunk);
            }
        }

        if (_isStreamFinished)
            return (index + bytesRequested) <= _streamLength ? bytesRequested : 0;
        else
            return bytesRequested;
    }
    
    public long getTotalBytesRead()
    {
        return _totalBytesRead;
    }
    
    public byte[] toArray(long index, int count) throws IOException
    {
        byte[] buffer = null;
        // if this was a byte array and asking for the whole thing...
        if (_isStreamFinished &&
            _chunks.size() == 1 &&
            index == 0 &&
            count == getLength())
        {
            buffer = _chunks.get(0L);
        }
        else
        {
            buffer = new byte[count];
            read(index, buffer, 0, count, false);
        }

        return buffer;
    }
}

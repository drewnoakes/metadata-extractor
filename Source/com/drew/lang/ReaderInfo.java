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
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.StringValue;

import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
/**
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
public class ReaderInfo
{
    // this flag is compared to index inputs and indicates sequential access
    private final int SequentialFlag = Integer.MIN_VALUE;

    private RandomAccessStream _ras = null;
    private long _length = -1;
    
    private long _startPosition;
    private long _localPosition;
    private boolean _isMotorolaByteOrder;
    
    public ReaderInfo(RandomAccessStream parent)
    {
        this(parent, 0L);
    }
    
    public ReaderInfo(RandomAccessStream parent, long startPosition)
    {
        this(parent, startPosition, 0L);
    }
    
    public ReaderInfo(RandomAccessStream parent, long startPosition, long localPosition)
    {
        this(parent, startPosition, localPosition, -1L);
    }
    
    public ReaderInfo(RandomAccessStream parent, long startPosition, long localPosition, long length)
    {
        this(parent, startPosition, localPosition, length, true);
    }
    
    public ReaderInfo(RandomAccessStream parent, long startPosition, long localPosition, long length, boolean isMotorolaByteOrder)
    {
        _ras = parent;
        _startPosition = startPosition;
        _localPosition = localPosition;
        _length = length;

        _isMotorolaByteOrder = isMotorolaByteOrder;
    }

    public static ReaderInfo createFromArray(@NotNull byte[] bytes) throws NullPointerException
    {
        if(bytes == null)
            throw new NullPointerException();
            
        return new RandomAccessStream(bytes).createReader();
    }
    
    private long getGlobalPosition()
    {
        return getStartPosition() + getLocalPosition();
    }
    
    public long getStartPosition()
    {
        return _startPosition;
    }
    
    public long getLocalPosition()
    {
        return _localPosition;
    }

    public long getLength() throws IOException
    {
        return (_length != -1) ? _length : (_ras.getLength() - getStartPosition());
    }

    /**
     * Gets the endianness of this reader.
     * <ul>
     * <li><code>true</code> for Motorola (or big) endianness (also known as network byte order), with MSB before LSB.</li>
     * <li><code>false</code> for Intel (or little) endianness, with LSB before MSB.</li>
     * </ul>
     */
    public boolean isMotorolaByteOrder()
    {
        return _isMotorolaByteOrder;
    }
    
    /**
     * Sets the endianness of this reader.
     * <ul>
     * <li><code>true</code> for Motorola (or big) endianness (also known as network byte order), with MSB before LSB.</li>
     * <li><code>false</code> for Intel (or little) endianness, with LSB before MSB.</li>
     * </ul>
     *
     * @param value <code>true</code> for Motorola/big endian, <code>false</code> for Intel/little endian
     */
    public void setMotorolaByteOrder(boolean value)
    {
        _isMotorolaByteOrder = value;
    }
    
    /**
     * Creates a new ReaderInfo with the current properties of this reader
     * @return a clones ReaderInfo
     * @throws IOException 
     */
    public ReaderInfo Clone() throws IOException
    {
        return Clone(0L, -1L, true);
    }
    public ReaderInfo Clone(boolean useByteOrder) throws IOException
    {
        return Clone(0L, useByteOrder);
    }
    public ReaderInfo Clone(long length) throws IOException
    {
        return Clone(0L, length, true);
    }
    public ReaderInfo Clone(long offset, long length) throws IOException
    {
        return Clone(offset, length, true);
    }
    public ReaderInfo Clone(long offset, boolean useByteOrder) throws IOException
    {
        return Clone(offset, -1L, useByteOrder);
    }
    public ReaderInfo Clone(long offset, long length, boolean useByteOrder) throws IOException
    {
        return _ras.createReader(getGlobalPosition() + offset, (length > -1 ? length : getLength() - offset), useByteOrder ? isMotorolaByteOrder() : !isMotorolaByteOrder());
    }
    
    /**
     * Skips forward or backward in the sequence. If the sequence ends, an {@link EOFException} is thrown.
     *
     * @param offset the number of bytes to skip.
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public void skip(long offset) throws IOException, EOFException, BufferBoundsException
    {
        if (offset + getLocalPosition() < 0)
            offset = -getLocalPosition();

        _ras.seek(getLocalPosition() + offset);

        _localPosition += offset;
    }
    
    /**
     * Skips forward or backward in the sequence, returning a boolean indicating whether the skip succeeded, or whether the sequence ended.
     *
     * @param n the number of byte to skip. Must be zero or greater.
     * @return a boolean indicating whether the skip succeeded, or whether the sequence ended.
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public boolean trySkip(long n) throws IOException, EOFException, BufferBoundsException
    {
        try
        {
            skip(n);
            return true;
        }
        catch (IOException ex)
        {
            // Stream ended, or error reading from underlying source
            return false;
        }
    }
    
    /**
     * Retrieves bytes, writing them into a caller-provided buffer. 
     *
     * @param buffer array to write bytes to
     * @param offset starting position with buffer to write to
     * @param count The number of bytes to be written
     * @return The requested bytes
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public int read(byte[] buffer, int offset, int count) throws IOException, EOFException, BufferBoundsException
    {
        return read(buffer, offset, SequentialFlag, count);
    }
    
    /**
     * Retrieves bytes, writing them into a caller-provided buffer. 
     * SequentialFlag as index indicates this call should read sequentially
     *
     * @param buffer array to write bytes to
     * @param offset starting position with buffer to write to
     * @param index position within the data buffer to read bytes
     * @param count The number of bytes to be written
     * @return The requested bytes
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public int read(byte[] buffer, int offset, long index, int count) throws IOException, EOFException, BufferBoundsException
    {
        boolean isSeq = (index == SequentialFlag);
        long readat = isSeq ? getGlobalPosition() : (getStartPosition() + index);

        return readAtGlobal(readat, buffer, offset, count, isSeq, true);
    }
    
    private int readAtGlobal(long readat, byte[] buffer, int offset, int count, boolean isSequential, boolean allowPartial) throws IOException, EOFException, BufferBoundsException
    {
        int read = _ras.read(readat, buffer, offset, count, isSequential, allowPartial);

        if (isSequential && read > 0)
            _localPosition += read; // advance the sequential position

        return read;
    }
    
    /**
     * Determine if the next bytes match the input pattern. Internal sequential variables are unaffected
     * 
     * @param pattern the byte pattern to match
     * @return true if this pattern is found or false if any part of it is not found
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public boolean startsWith(byte[] pattern) throws IOException, EOFException, BufferBoundsException
    {
        if (getLength() < pattern.length)
            return false;

        boolean ret = true;
        int i = 0;
        for (i = 0; i < pattern.length; i++)
        {
            if (getByte(i) != pattern[i])
            {
                ret = false;
                break;
            }
        }

        return ret;
    }

    /**
     * Gets the byte value at the current sequential index
     * <p>
     * Implementations should not perform any bounds checking in this method. That should be performed
     * in <code>validateIndex</code> and <code>bytesAvailable</code>.
     *
     * @return The read byte value
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public byte getByte() throws IOException, EOFException, BufferBoundsException
    {
        return getByte(SequentialFlag);
    }

    /**
     * Gets the byte value at the specified byte <code>index</code>.
     * <p>
     * Implementations should not perform any bounds checking in this method. That should be performed
     * in <code>validateIndex</code> and <code>bytesAvailable</code>.
     *
     * @param index The index from which to read the byte
     * @return The read byte value
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public byte getByte(long index) throws IOException, EOFException, BufferBoundsException
    {
        boolean isSeq = (index == SequentialFlag);
        long readat = isSeq ? getGlobalPosition() : (getStartPosition() + index);

        byte read = _ras.getByte(readat, isSeq);

        if (isSeq)
            _localPosition++; // advance the sequential position

        return read;
    }

    /**
     * Returns the required number of bytes sequentially from the underlying source.
     *
     * @param count The number of bytes to be returned
     * @return The requested bytes
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public byte[] getBytes(int count) throws IOException, EOFException, BufferBoundsException
    {
        return getBytes(SequentialFlag, count);
    }

    /**
     * Returns the required number of bytes from the specified index from the underlying source.
     *
     * @param index The index from which the bytes begins in the underlying source
     * @param count The number of bytes to be returned
     * @return The requested bytes
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public byte[] getBytes(long index, int count) throws IOException, EOFException, BufferBoundsException
    {
        // validate the index now to avoid creating a byte array that could cause a heap overflow
        boolean isSeq = (index == SequentialFlag);
        long readat = isSeq ? getGlobalPosition() : (getStartPosition() + index);
        long available = _ras.validateIndex(readat, count, isSeq, false);
        
        if(available <= 0)
            return new byte[0];
        
        byte[] bytes = new byte[count];
        readAtGlobal(readat, bytes, 0, count, isSeq, false);

        return bytes;
    }

    /**
     * Gets whether a bit at the current sequential index is set or not.
     *
     * @return true if the bit is set, otherwise false
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public boolean getBit() throws IOException, EOFException, BufferBoundsException
    {
        return getBit(SequentialFlag);
    }

    /**
     * Gets whether a bit at a specific index is set or not.
     *
     * @param index the number of bits at which to test
     * @return true if the bit is set, otherwise false
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public boolean getBit(int index) throws IOException, EOFException, BufferBoundsException
    {
        int byteIndex = index / 8;
        int bitIndex = index % 8;

        byte b = getByte(byteIndex);
        return ((b >> bitIndex) & 1) == 1;
    }
    
    /**
     * Returns an unsigned 8-bit int calculated from one byte of data at the current sequential index.
     *
     * @return the 8 bit int value, between 0 and 255
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public short getUInt8() throws IOException, EOFException, BufferBoundsException
    {
        return getUInt8(SequentialFlag);
    }
    
    /**
     * Returns an unsigned 8-bit int calculated from one byte of data at the specified index.
     *
     * @param index position within the data buffer to read byte
     * @return the 8 bit int value, between 0 and 255
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public short getUInt8(int index) throws IOException, EOFException, BufferBoundsException
    {
        return (short) (getByte(index) & 0xFF);
    }

    /**
     * Returns a signed 8-bit int calculated from one byte of data at the current sequential index.
     *
     * @return the 8 bit int value, between 0x00 and 0xFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public byte getInt8() throws IOException, EOFException, BufferBoundsException
    {
        return getInt8(SequentialFlag);
    }
    
    /**
     * Returns a signed 8-bit int calculated from one byte of data at the specified index.
     *
     * @param index position within the data buffer to read byte
     * @return the 8 bit int value, between 0x00 and 0xFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public byte getInt8(int index) throws IOException, EOFException, BufferBoundsException
    {
        return getByte(index);
    }
    
    /**
     * Returns an unsigned 16-bit int calculated from two bytes of data at the current sequential index.
     *
     * @return the 16 bit int value, between 0x0000 and 0xFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public int getUInt16() throws IOException, EOFException, BufferBoundsException
    {
        return getUInt16(SequentialFlag);
    }
    
    /**
     * Returns an unsigned 16-bit int calculated from two bytes of data at the specified index.
     *
     * @param index position within the data buffer to read first byte
     * @return the 16 bit int value, between 0x0000 and 0xFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public int getUInt16(int index) throws IOException, EOFException, BufferBoundsException
    {
        boolean isSeq = (index == SequentialFlag);
        long readat = isSeq ? getGlobalPosition() : (getStartPosition() + index);

        int read = _ras.getUInt16(readat, isMotorolaByteOrder(), isSeq);

        if (isSeq)
            _localPosition += 2; // advance the sequential position

        return read;
    }

    /**
     * Returns a signed 16-bit int calculated from two bytes of data at the current sequential index (MSB, LSB).
     *
     * @return the 16 bit int value, between 0x0000 and 0xFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public short getInt16() throws IOException, EOFException, BufferBoundsException
    {
        return getInt16(SequentialFlag);
    }
    
    /**
     * Returns a signed 16-bit int calculated from two bytes of data at the specified index (MSB, LSB).
     *
     * @param index position within the data buffer to read first byte
     * @return the 16 bit int value, between 0x0000 and 0xFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public short getInt16(int index) throws IOException, EOFException, BufferBoundsException
    {
        boolean isSeq = (index == SequentialFlag);
        long readat = isSeq ? getGlobalPosition() : (getStartPosition() + index);

        short read = _ras.getInt16(readat, isMotorolaByteOrder(), isSeq);

        if (isSeq)
            _localPosition += 2; // advance the sequential position

        return read;
    }
    
    /**
     * Get a 24-bit unsigned integer from the current sequential index, returning it as an int.
     *
     * @return the unsigned 24-bit int value as a long, between 0x00000000 and 0x00FFFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public int getInt24() throws IOException, EOFException, BufferBoundsException
    {
        return getInt24(SequentialFlag);
    }
    
    /**
     * Get a 24-bit unsigned integer from the buffer, returning it as an int.
     *
     * @param index position within the data buffer to read first byte
     * @return the unsigned 24-bit int value as a long, between 0x00000000 and 0x00FFFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public int getInt24(int index) throws IOException, EOFException, BufferBoundsException
    {
        boolean isSeq = (index == SequentialFlag);
        long readat = isSeq ? getGlobalPosition() : (getStartPosition() + index);

        int read = _ras.getInt24(readat, isMotorolaByteOrder(), isSeq);

        if (isSeq)
            _localPosition += 3; // advance the sequential position

        return read;
    }
    
    /**
     * Get a 32-bit unsigned integer from the current sequential index, returning it as a long.
     *
     * @return the unsigned 32-bit int value as a long, between 0x00000000 and 0xFFFFFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public long getUInt32() throws IOException, EOFException, BufferBoundsException
    {
        return getUInt32(SequentialFlag);
    }
    
    /**
     * Get a 32-bit unsigned integer from the buffer, returning it as a long.
     *
     * @param index position within the data buffer to read first byte
     * @return the unsigned 32-bit int value as a long, between 0x00000000 and 0xFFFFFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public long getUInt32(int index) throws IOException, EOFException, BufferBoundsException
    {
        boolean isSeq = (index == SequentialFlag);
        long readat = isSeq ? getGlobalPosition() : (getStartPosition() + index);

        long read = _ras.getUInt32(readat, isMotorolaByteOrder(), isSeq);

        if (isSeq)
            _localPosition += 4; // advance the sequential position

        return read;
    }
    
    /**
     * Returns a signed 32-bit integer from four bytes of data at the current sequential index of the buffer.
     *
     * @return the signed 32 bit int value, between 0x00000000 and 0xFFFFFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public int getInt32() throws IOException, EOFException, BufferBoundsException
    {
        return getInt32(SequentialFlag);
    }
    
    /**
     * Returns a signed 32-bit integer from four bytes of data at the specified index of the buffer.
     *
     * @param index position within the data buffer to read first byte
     * @return the signed 32 bit int value, between 0x00000000 and 0xFFFFFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public int getInt32(int index) throws IOException, EOFException, BufferBoundsException
    {
        boolean isSeq = (index == SequentialFlag);
        long readat = isSeq ? getGlobalPosition() : (getStartPosition() + index);

        int read = _ras.getInt32(readat, isMotorolaByteOrder(), isSeq);

        if (isSeq)
            _localPosition += 4; // advance the sequential position

        return read;
    }

    /**
     * Get a signed 64-bit integer from the current sequential index of the buffer.
     *
     * @return the 64 bit int value, between 0x0000000000000000 and 0xFFFFFFFFFFFFFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */    
    public long getInt64() throws IOException, EOFException, BufferBoundsException
    {
        return getInt64(SequentialFlag);
    }

    /**
     * Get a signed 64-bit integer from the buffer.
     *
     * @param index position within the data buffer to read first byte
     * @return the 64 bit int value, between 0x0000000000000000 and 0xFFFFFFFFFFFFFFFF
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public long getInt64(int index) throws IOException, EOFException, BufferBoundsException
    {
        boolean isSeq = (index == SequentialFlag);
        long readat = isSeq ? getGlobalPosition() : (getStartPosition() + index);

        long read = _ras.getInt64(readat, isMotorolaByteOrder(), isSeq);

        if (isSeq)
            _localPosition += 8; // advance the sequential position

        return read;
    }
    
    /**
     * Gets a s15.16 fixed point float from the buffer sequentially.
     * <p>
     * This particular fixed point encoding has one sign bit, 15 numerator bits and 16 denominator bits.
     *
     * @return the floating point value
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public float getS15Fixed16() throws IOException, EOFException, BufferBoundsException
    {
        return getS15Fixed16(SequentialFlag);
    }

    /**
     * Gets a s15.16 fixed point float from the buffer.
     * <p>
     * This particular fixed point encoding has one sign bit, 15 numerator bits and 16 denominator bits.
     *
     * @param index position within the data buffer to read first byte
     * @return the floating point value
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public float getS15Fixed16(int index) throws IOException, EOFException, BufferBoundsException
    {
        boolean isSeq = (index == SequentialFlag);
        long readat = isSeq ? getGlobalPosition() : (getStartPosition() + index);

        float read = _ras.getS15Fixed16(readat, isMotorolaByteOrder(), isSeq);

        if (isSeq)
            _localPosition += 4; // advance the sequential position

        return read;
    }
    
    public float getFloat32() throws IOException, EOFException, BufferBoundsException
    {
        return getFloat32(SequentialFlag);
    }

    public float getFloat32(int index) throws IOException, EOFException, BufferBoundsException
    {
        return Float.intBitsToFloat(getInt32(index));
    }

    public double getDouble64() throws IOException, EOFException, BufferBoundsException
    {
        return getDouble64(SequentialFlag);
    }

    public double getDouble64(int index) throws IOException, EOFException, BufferBoundsException
    {
        return Double.longBitsToDouble(getInt64(index));
    }

    @NotNull
    public StringValue getStringValue(int bytesRequested, @Nullable Charset charset) throws IOException, EOFException, BufferBoundsException
    {
        return getStringValue(SequentialFlag, bytesRequested, charset);
    }
    
    @NotNull
    public StringValue getStringValue(int index, int bytesRequested, @Nullable Charset charset) throws IOException, EOFException, BufferBoundsException
    {
        return new StringValue(getBytes(index, bytesRequested), charset);
    }

    @NotNull
    public String getString(int bytesRequested, @NotNull Charset charset) throws IOException, EOFException, BufferBoundsException
    {
        return getString(SequentialFlag, bytesRequested, charset);
    }
    
    @NotNull
    public String getString(int index, int bytesRequested, @NotNull Charset charset) throws IOException, EOFException, BufferBoundsException
    {
        return new String(getBytes(index, bytesRequested), charset.name());
    }

    @NotNull
    public String getString(int index, int bytesRequested, @NotNull String charset) throws IOException, EOFException, BufferBoundsException
    {
        byte[] bytes = getBytes(index, bytesRequested);
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(bytes);
        }
    }

    /**
     * Creates a String from the _data buffer starting at the current sequential index,
     * and ending where <code>byte=='\0'</code> or where <code>length==maxLength</code>.
     *
     * @param maxLengthBytes The maximum number of bytes to read.  If a zero-byte is not reached within this limit,
     *                       reading will stop and the string will be truncated to this length.
     * @param charset The <code>Charset</code> to register with the returned <code>StringValue</code>, or <code>null</code> if the encoding
     *                is unknown
     * @return The read string.
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    @NotNull
    public String getNullTerminatedString(int maxLengthBytes, @NotNull Charset charset) throws IOException, EOFException, BufferBoundsException
    {
        return getNullTerminatedString(SequentialFlag, maxLengthBytes, charset);
    }

    /**
     * Creates a String from the _data buffer starting at the specified index,
     * and ending where <code>byte=='\0'</code> or where <code>length==maxLength</code>.
     *
     * @param index          The index within the buffer at which to start reading the string.
     * @param maxLengthBytes The maximum number of bytes to read.  If a zero-byte is not reached within this limit,
     *                       reading will stop and the string will be truncated to this length.
     * @param charset The <code>Charset</code> to register with the returned <code>StringValue</code>, or <code>null</code> if the encoding
     *                is unknown
     * @return The read string.
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    @NotNull
    public String getNullTerminatedString(int index, int maxLengthBytes, @NotNull Charset charset) throws IOException, EOFException, BufferBoundsException
    {
        return new String(getNullTerminatedBytes(index, maxLengthBytes), charset.name());
    }

    /**
     * Creates a String from the _data buffer starting at the current sequential index,
     * and ending where <code>byte=='\0'</code> or where <code>length==maxLength</code>.
     *
     * @param maxLengthBytes The maximum number of bytes to read.  If a zero-byte is not reached within this limit,
     *                       reading will stop and the string will be truncated to this length.
     * @param charset The <code>Charset</code> to register with the returned <code>StringValue</code>, or <code>null</code> if the encoding
     *                is unknown
     * @return The read string.
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    @NotNull
    public StringValue getNullTerminatedStringValue(int maxLengthBytes, @Nullable Charset charset) throws IOException, EOFException, BufferBoundsException
    {
        return getNullTerminatedStringValue(SequentialFlag, maxLengthBytes, charset);
    }
    
    /**
     * Creates a String from the _data buffer starting at the specified index,
     * and ending where <code>byte=='\0'</code> or where <code>length==maxLength</code>.
     *
     * @param index          The index within the buffer at which to start reading the string.
     * @param maxLengthBytes The maximum number of bytes to read.  If a zero-byte is not reached within this limit,
     *                       reading will stop and the string will be truncated to this length.
     * @param charset The <code>Charset</code> to register with the returned <code>StringValue</code>, or <code>null</code> if the encoding
     *                is unknown
     * @return The read string.
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    @NotNull
    public StringValue getNullTerminatedStringValue(int index, int maxLengthBytes, @Nullable Charset charset) throws IOException, EOFException, BufferBoundsException
    {
        byte[] bytes = getNullTerminatedBytes(index, maxLengthBytes);

        return new StringValue(bytes, charset);
    }

    /**
     * Returns the sequence of bytes punctuated by a <code>\0</code> value.
     *
     * @param maxLengthBytes The maximum number of bytes to read. If a <code>\0</code> byte is not reached within this limit,
     * the returned array will be <code>maxLengthBytes</code> long.
     * @return The read byte array, excluding the null terminator.
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    @NotNull
    public byte[] getNullTerminatedBytes(int maxLengthBytes) throws IOException, EOFException, BufferBoundsException
    {
        return getNullTerminatedBytes(SequentialFlag, maxLengthBytes);
    }

    /**
     * Returns the sequence of bytes punctuated by a <code>\0</code> value.
     *
     * @param index The index within the buffer at which to start reading the string.
     * @param maxLengthBytes The maximum number of bytes to read. If a <code>\0</code> byte is not reached within this limit,
     * the returned array will be <code>maxLengthBytes</code> long.
     * @return The read byte array, excluding the null terminator.
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    @NotNull
    public byte[] getNullTerminatedBytes(int index, int maxLengthBytes) throws IOException, EOFException, BufferBoundsException
    {   
        boolean isSeq = (index == SequentialFlag);
        byte[] buffer = !isSeq ? getBytes(index, maxLengthBytes) : new byte[maxLengthBytes];

        // Count the number of non-null bytes
        int length = 0;
        if(!isSeq)
        {
            while (length < buffer.length && buffer[length] != 0)
                length++;
        }
        else
        {
            byte getB = getByte();
            while(length < buffer.length && getB != 0)
            {
                buffer[length] = getB;
                getB = getByte();
                length++;
            }
        }

        if (length == maxLengthBytes)
            return buffer;

        byte[] bytes = new byte[length];
        if (length > 0)
            System.arraycopy(buffer, 0, bytes, 0, length);
        return bytes;
    }
    
    /// <summary>Returns the bytes described by this particular reader</summary>
    /// <returns></returns>
    /**
     * Returns the bytes described by this particular reader
     * @return byte array
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public byte[] toArray() throws IOException, EOFException, BufferBoundsException
    {
        return _ras.toArray(getStartPosition(), (int)getLength());
    }

    public String readLine() throws IOException
    {
        StringBuilder sb = new StringBuilder();
        while (true)
        {
            if (getLocalPosition() == getLength())
                break;

            int ch = getByte();
            if (ch == -1) break;
            if (ch == '\r' || ch == '\n')
            {
                byte nextbyte = 0;
                if(getGlobalPosition() + 1 < getLength())
                    nextbyte = getByte();
                if (!(ch == '\r' && nextbyte == '\n'))
                    skip(-1);

                return sb.toString();
            }
            sb.append((char)ch);
        }
        if (sb.length() > 0) return sb.toString();
        return null;
    }
    
    /**
     * Returns true in case the sequence supports length checking and distance to the end of the stream is less then number of bytes in parameter.
     * Otherwise false.
     * @param numberOfBytes count of bytes to try and read
     * @return True if we are going to have an exception while reading next numberOfBytes bytes from the stream
     * @throws IOException if the byte is unable to be read
     * @throws EOFException if the requested bytes extend beyond the end of the underlying data source
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     */
    public boolean isCloserToEnd(long numberOfBytes) throws IOException, EOFException, BufferBoundsException
    {
        return (getLocalPosition() + numberOfBytes) > getLength();
    }
}

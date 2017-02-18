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
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.StringValue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Base class for random access data reading operations of common data types.
 * <p>
 * By default, the reader operates with Motorola byte order (big endianness).  This can be changed by calling
 * {@link com.drew.lang.RandomAccessReader#setMotorolaByteOrder(boolean)}.
 * <p>
 * Concrete implementations include:
 * <ul>
 *     <li>{@link ByteArrayReader}</li>
 *     <li>{@link RandomAccessStreamReader}</li>
 * </ul>
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class RandomAccessReader
{
    private boolean _isMotorolaByteOrder = true;

    public abstract int toUnshiftedOffset(int localOffset);

    /**
     * Gets the byte value at the specified byte <code>index</code>.
     * <p>
     * Implementations should not perform any bounds checking in this method. That should be performed
     * in <code>validateIndex</code> and <code>isValidIndex</code>.
     *
     * @param index The index from which to read the byte
     * @return The read byte value
     * @throws IllegalArgumentException <code>index</code> is negative
     * @throws BufferBoundsException if the requested byte is beyond the end of the underlying data source
     * @throws IOException if the byte is unable to be read
     */
    public abstract byte getByte(int index) throws IOException;

    /**
     * Returns the required number of bytes from the specified index from the underlying source.
     *
     * @param index The index from which the bytes begins in the underlying source
     * @param count The number of bytes to be returned
     * @return The requested bytes
     * @throws IllegalArgumentException <code>index</code> or <code>count</code> are negative
     * @throws BufferBoundsException if the requested bytes extend beyond the end of the underlying data source
     * @throws IOException if the byte is unable to be read
     */
    @NotNull
    public abstract byte[] getBytes(int index, int count) throws IOException;

    /**
     * Ensures that the buffered bytes extend to cover the specified index. If not, an attempt is made
     * to read to that point.
     * <p>
     * If the stream ends before the point is reached, a {@link BufferBoundsException} is raised.
     *
     * @param index the index from which the required bytes start
     * @param bytesRequested the number of bytes which are required
     * @throws IOException if the stream ends before the required number of bytes are acquired
     */
    protected abstract void validateIndex(int index, int bytesRequested) throws IOException;

    protected abstract boolean isValidIndex(int index, int bytesRequested) throws IOException;

    /**
     * Returns the length of the data source in bytes.
     * <p>
     * This is a simple operation for implementations (such as {@link RandomAccessFileReader} and
     * {@link ByteArrayReader}) that have the entire data source available.
     * <p>
     * Users of this method must be aware that sequentially accessed implementations such as
     * {@link RandomAccessStreamReader} will have to read and buffer the entire data source in
     * order to determine the length.
     *
     * @return the length of the data source, in bytes.
     */
    public abstract long getLength() throws IOException;

    /**
     * Sets the endianness of this reader.
     * <ul>
     * <li><code>true</code> for Motorola (or big) endianness (also known as network byte order), with MSB before LSB.</li>
     * <li><code>false</code> for Intel (or little) endianness, with LSB before MSB.</li>
     * </ul>
     *
     * @param motorolaByteOrder <code>true</code> for Motorola/big endian, <code>false</code> for Intel/little endian
     */
    public void setMotorolaByteOrder(boolean motorolaByteOrder)
    {
        _isMotorolaByteOrder = motorolaByteOrder;
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
     * Gets whether a bit at a specific index is set or not.
     *
     * @param index the number of bits at which to test
     * @return true if the bit is set, otherwise false
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public boolean getBit(int index) throws IOException
    {
        int byteIndex = index / 8;
        int bitIndex = index % 8;

        validateIndex(byteIndex, 1);

        byte b = getByte(byteIndex);
        return ((b >> bitIndex) & 1) == 1;
    }

    /**
     * Returns an unsigned 8-bit int calculated from one byte of data at the specified index.
     *
     * @param index position within the data buffer to read byte
     * @return the 8 bit int value, between 0 and 255
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public short getUInt8(int index) throws IOException
    {
        validateIndex(index, 1);

        return (short) (getByte(index) & 0xFF);
    }

    /**
     * Returns a signed 8-bit int calculated from one byte of data at the specified index.
     *
     * @param index position within the data buffer to read byte
     * @return the 8 bit int value, between 0x00 and 0xFF
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public byte getInt8(int index) throws IOException
    {
        validateIndex(index, 1);

        return getByte(index);
    }

    /**
     * Returns an unsigned 16-bit int calculated from two bytes of data at the specified index.
     *
     * @param index position within the data buffer to read first byte
     * @return the 16 bit int value, between 0x0000 and 0xFFFF
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public int getUInt16(int index) throws IOException
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

    /**
     * Returns a signed 16-bit int calculated from two bytes of data at the specified index (MSB, LSB).
     *
     * @param index position within the data buffer to read first byte
     * @return the 16 bit int value, between 0x0000 and 0xFFFF
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public short getInt16(int index) throws IOException
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

    /**
     * Get a 24-bit unsigned integer from the buffer, returning it as an int.
     *
     * @param index position within the data buffer to read first byte
     * @return the unsigned 24-bit int value as a long, between 0x00000000 and 0x00FFFFFF
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public int getInt24(int index) throws IOException
    {
        validateIndex(index, 3);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (((int)getByte(index    )) << 16 & 0xFF0000) |
                   (((int)getByte(index + 1)) << 8  & 0xFF00) |
                   (((int)getByte(index + 2))       & 0xFF);
        } else {
            // Intel ordering - LSB first (little endian)
            return (((int)getByte(index + 2)) << 16 & 0xFF0000) |
                   (((int)getByte(index + 1)) << 8  & 0xFF00) |
                   (((int)getByte(index    ))       & 0xFF);
        }
    }

    /**
     * Get a 32-bit unsigned integer from the buffer, returning it as a long.
     *
     * @param index position within the data buffer to read first byte
     * @return the unsigned 32-bit int value as a long, between 0x00000000 and 0xFFFFFFFF
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public long getUInt32(int index) throws IOException
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

    /**
     * Returns a signed 32-bit integer from four bytes of data at the specified index the buffer.
     *
     * @param index position within the data buffer to read first byte
     * @return the signed 32 bit int value, between 0x00000000 and 0xFFFFFFFF
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public int getInt32(int index) throws IOException
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

    /**
     * Get a signed 64-bit integer from the buffer.
     *
     * @param index position within the data buffer to read first byte
     * @return the 64 bit int value, between 0x0000000000000000 and 0xFFFFFFFFFFFFFFFF
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public long getInt64(int index) throws IOException
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

    /**
     * Gets a s15.16 fixed point float from the buffer.
     * <p>
     * This particular fixed point encoding has one sign bit, 15 numerator bits and 16 denominator bits.
     *
     * @return the floating point value
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public float getS15Fixed16(int index) throws IOException
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

    public float getFloat32(int index) throws IOException
    {
        return Float.intBitsToFloat(getInt32(index));
    }

    public double getDouble64(int index) throws IOException
    {
        return Double.longBitsToDouble(getInt64(index));
    }

    @NotNull
    public StringValue getStringValue(int index, int bytesRequested, @Nullable Charset charset) throws IOException
    {
        return new StringValue(getBytes(index, bytesRequested), charset);
    }

    @NotNull
    public String getString(int index, int bytesRequested, @NotNull Charset charset) throws IOException
    {
        return new String(getBytes(index, bytesRequested), charset.name());
    }

    @NotNull
    public String getString(int index, int bytesRequested, @NotNull String charset) throws IOException
    {
        byte[] bytes = getBytes(index, bytesRequested);
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(bytes);
        }
    }

    /**
     * Creates a String from the _data buffer starting at the specified index,
     * and ending where <code>byte=='\0'</code> or where <code>length==maxLength</code>.
     *
     * @param index          The index within the buffer at which to start reading the string.
     * @param maxLengthBytes The maximum number of bytes to read.  If a zero-byte is not reached within this limit,
     *                       reading will stop and the string will be truncated to this length.
     * @return The read string.
     * @throws IOException The buffer does not contain enough bytes to satisfy this request.
     */
    @NotNull
    public String getNullTerminatedString(int index, int maxLengthBytes, @NotNull Charset charset) throws IOException
    {
        return new String(getNullTerminatedBytes(index, maxLengthBytes), charset.name());
    }

    @NotNull
    public StringValue getNullTerminatedStringValue(int index, int maxLengthBytes, @Nullable Charset charset) throws IOException
    {
        byte[] bytes = getNullTerminatedBytes(index, maxLengthBytes);

        return new StringValue(bytes, charset);
    }

    /**
     * Returns the sequence of bytes punctuated by a <code>\0</code> value.
     *
     * @param index The index within the buffer at which to start reading the string.
     * @param maxLengthBytes The maximum number of bytes to read. If a <code>\0</code> byte is not reached within this limit,
     * the returned array will be <code>maxLengthBytes</code> long.
     * @return The read byte array, excluding the null terminator.
     * @throws IOException The buffer does not contain enough bytes to satisfy this request.
     */
    @NotNull
    public byte[] getNullTerminatedBytes(int index, int maxLengthBytes) throws IOException
    {
        byte[] buffer = getBytes(index, maxLengthBytes);

        // Count the number of non-null bytes
        int length = 0;
        while (length < buffer.length && buffer[length] != 0)
            length++;

        if (length == maxLengthBytes)
            return buffer;

        byte[] bytes = new byte[length];
        if (length > 0)
            System.arraycopy(buffer, 0, bytes, 0, length);
        return bytes;
    }
}

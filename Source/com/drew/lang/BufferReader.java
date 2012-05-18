/*
 * Copyright 2002-2011 Drew Noakes
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

import java.io.UnsupportedEncodingException;

/**
 * Provides methods to read specific values from a byte array, with a consistent, checked exception structure for
 * issues.
 * <p/>
 * By default, the buffer reader operates with Motorola byte order (big endianness).  This can be changed by calling
 * <code>setMotorolaByteOrder</code>.
 * 
 * @author Drew Noakes http://drewnoakes.com
 * */
public class BufferReader
{
    @NotNull
    private final byte[] _buffer;
    private boolean _isMotorolaByteOrder = true;

    @SuppressWarnings({ "ConstantConditions" })
    @com.drew.lang.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Design intent")
    public BufferReader(@NotNull byte[] buffer)
    {
        if (buffer == null)
            throw new NullPointerException();
        
        _buffer = buffer;
    }

    /**
     * Returns the length of the buffer.  This value represents the total number of bytes in the underlying array.
     * @return The number of bytes in the buffer.
     */
    public int getLength()
    {
        return _buffer.length;
    }


    /**
     * Sets the endianness of this reader.
     * <ul>
     *     <li><code>true</code> for Motorola (or big) endianness</li>
     *     <li><code>false</code> for Intel (or little) endianness</li>
     * </ul>
     *
     * @param motorolaByteOrder <code>true</code> for motorola/big endian, <code>false</code> for intel/little endian
     */
    public void setMotorolaByteOrder(boolean motorolaByteOrder)
    {
        _isMotorolaByteOrder = motorolaByteOrder;
    }

    /**
     * Gets the endianness of this reader.
     * <ul>
     *     <li><code>true</code> for Motorola (or big) endianness</li>
     *     <li><code>false</code> for Intel (or little) endianness</li>
     * </ul>
     */
    public boolean isMotorolaByteOrder()
    {
        return _isMotorolaByteOrder;
    }

    /**
     * Returns the byte at the specified index.
     *
     * @param index position within the data buffer to read byte
     * @return the 8 bit int value, between 0x0 and 0xF
     * @throws BufferBoundsException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public byte getByte(int index) throws BufferBoundsException
    {
        CheckBounds(index, 1);
        return _buffer[index];
    }

    /**
     * Returns an int calculated from one byte of data at the specified index.
     *
     * @param index position within the data buffer to read byte
     * @return the 8 bit int value, between 0x0 and 0xF
     * @throws BufferBoundsException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public int getUInt8(int index) throws BufferBoundsException
    {
        CheckBounds(index, 1);

        return _buffer[index] & 255;
    }

    /**
     * Returns an int calculated from four bytes of data at the specified index (MSB, LSB).
     * 
     * @param index position within the data buffer to read first byte
     * @return the 32 bit int value, between 0x0000 and 0xFFFF
     * @throws BufferBoundsException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public int getUInt16(int index) throws BufferBoundsException
    {
        CheckBounds(index, 2);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first
            return (_buffer[index    ] << 8 & 0xFF00) |
                   (_buffer[index + 1]      & 0xFF);
        } else {
            // Intel ordering - LSB first
            return (_buffer[index + 1] << 8 & 0xFF00) |
                   (_buffer[index    ]      & 0xFF);
        }
    }

    /**
     * Get a 32-bit signed integer from the buffer.
     * 
     * @param index position within the data buffer to read first byte
     * @return the signed 32 bit int value, between 0x00000000 and 0xFFFFFFFF
     * @throws BufferBoundsException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public int getInt32(int index) throws BufferBoundsException
    {
        CheckBounds(index, 4);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (_buffer[index    ] << 24 & 0xFF000000) |
                   (_buffer[index + 1] << 16 & 0xFF0000) |
                   (_buffer[index + 2] << 8  & 0xFF00) |
                   (_buffer[index + 3]       & 0xFF);
        } else {
            // Intel ordering - LSB first (little endian)
            return (_buffer[index + 3] << 24 & 0xFF000000) |
                   (_buffer[index + 2] << 16 & 0xFF0000) |
                   (_buffer[index + 1] << 8  & 0xFF00) |
                   (_buffer[index    ]       & 0xFF);
        }
    }

    /**
     * Get a 32-bit unsigned integer from the buffer, returning it as a long.
     *
     * @param index position within the data buffer to read first byte
     * @return the unsigned 32-bit int value as a long, between 0x00000000 and 0xFFFFFFFF
     * @throws BufferBoundsException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public long getUInt32(int index) throws BufferBoundsException
    {
        CheckBounds(index, 4);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (((long)_buffer[index    ]) << 24 & 0xFF000000L) |
                   (((long)_buffer[index + 1]) << 16 & 0xFF0000L) |
                   (((long)_buffer[index + 2]) << 8  & 0xFF00L) |
                   (((long)_buffer[index + 3])       & 0xFFL);
        } else {
            // Intel ordering - LSB first (little endian)
            return (((long)_buffer[index + 3]) << 24 & 0xFF000000L) |
                   (((long)_buffer[index + 2]) << 16 & 0xFF0000L) |
                   (((long)_buffer[index + 1]) << 8  & 0xFF00L) |
                   (((long)_buffer[index    ])       & 0xFFL);
        }
    }

    /**
     * Get a 64-bit integer from the buffer.
     *
     * @param index position within the data buffer to read first byte
     * @return the 64 bit int value, between 0x0000000000000000 and 0xFFFFFFFFFFFFFFFF
     * @throws BufferBoundsException the buffer does not contain enough bytes to service the request, or index is negative
     */
    public long getInt64(int index) throws BufferBoundsException
    {
        CheckBounds(index, 8);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first
            return ((long)_buffer[index    ] << 56 & 0xFF00000000000000L) |
                   ((long)_buffer[index + 1] << 48 & 0xFF000000000000L) |
                   ((long)_buffer[index + 2] << 40 & 0xFF0000000000L) |
                   ((long)_buffer[index + 3] << 32 & 0xFF00000000L) |
                   ((long)_buffer[index + 4] << 24 & 0xFF000000L) |
                   ((long)_buffer[index + 5] << 16 & 0xFF0000L) |
                   ((long)_buffer[index + 6] << 8  & 0xFF00L) |
                   ((long)_buffer[index + 7]       & 0xFFL);
        } else {
            // Intel ordering - LSB first
            return ((long)_buffer[index + 7] << 56 & 0xFF00000000000000L) |
                   ((long)_buffer[index + 6] << 48 & 0xFF000000000000L) |
                   ((long)_buffer[index + 5] << 40 & 0xFF0000000000L) |
                   ((long)_buffer[index + 4] << 32 & 0xFF00000000L) |
                   ((long)_buffer[index + 3] << 24 & 0xFF000000L) |
                   ((long)_buffer[index + 2] << 16 & 0xFF0000L) |
                   ((long)_buffer[index + 1] << 8  & 0xFF00L) |
                   ((long)_buffer[index    ]       & 0xFFL);
        }
    }

    public float getS15Fixed16(int index) throws BufferBoundsException
    {
        CheckBounds(index, 4);

        if (_isMotorolaByteOrder) {
            float res = (_buffer[index    ] & 255) << 8 |
                        (_buffer[index + 1] & 255);
            int d =     (_buffer[index + 2] & 255) << 8 |
                        (_buffer[index + 3] & 255);
            return (float)(res + d/65536.0);
        } else {
            // this particular branch is untested
            float res = (_buffer[index + 3] & 255) << 8 |
                        (_buffer[index + 2] & 255);
            int d =     (_buffer[index + 1] & 255) << 8 |
                        (_buffer[index    ] & 255);
            return (float)(res + d/65536.0);
        }
    }

    public float getFloat32(int index) throws BufferBoundsException
    {
        return Float.intBitsToFloat(getInt32(index));
    }

    public double getDouble64(int index) throws BufferBoundsException
    {
        return Double.longBitsToDouble(getInt64(index));
    }

    
    @NotNull
    public byte[] getBytes(int index, int count) throws BufferBoundsException
    {
        CheckBounds(index, count);
        byte[] bytes = new byte[count];
        System.arraycopy(_buffer, index, bytes, 0, count);
        return bytes;
    }

    @NotNull
    public String getString(int index, int bytesRequested) throws BufferBoundsException
    {
        CheckBounds(index, bytesRequested);

        byte[] bytes = getBytes(index, bytesRequested);
        return new String(bytes);
    }

    @NotNull
    public String getString(int index, int bytesRequested, String charset) throws BufferBoundsException
    {
        CheckBounds(index, bytesRequested);

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
     * @param index The index within the buffer at which to start reading the string.
     * @param maxLengthBytes The maximum number of bytes to read.  If a zero-byte is not reached within this limit,
     * reading will stop and the string will be truncated to this length.
     * @return The read string.
     * @throws BufferBoundsException The buffer does not contain enough bytes to satisfy this request.
     */
    @NotNull
    public String getNullTerminatedString(int index, int maxLengthBytes) throws BufferBoundsException
    {
        // NOTE currently only really suited to single-byte character strings

        CheckBounds(index, maxLengthBytes);

        // Check for null terminators
        int length = 0;
        while ((index + length) < _buffer.length && _buffer[index + length] != '\0' && length < maxLengthBytes)
            length++;

        byte[] bytes = getBytes(index, length);
        return new String(bytes);
    }

    private void CheckBounds(final int index, final int bytesRequested) throws BufferBoundsException
    {
        if (bytesRequested < 0 || index < 0 || (long)index + (long)bytesRequested - 1L >= (long)_buffer.length)
            throw new BufferBoundsException(_buffer, index, bytesRequested);
    }
}

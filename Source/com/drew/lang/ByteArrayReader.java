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

import java.io.UnsupportedEncodingException;

/**
 * Provides methods to read specific values from a byte array, with a consistent, checked exception structure for
 * issues.
 * <p/>
 * By default, the reader operates with Motorola byte order (big endianness).  This can be changed by calling
 * {@see setMotorolaByteOrder(boolean)}.
 * 
 * @author Drew Noakes http://drewnoakes.com
 * */
public class ByteArrayReader implements RandomAccessReader
{
    @NotNull
    private final byte[] _buffer;
    private boolean _isMotorolaByteOrder = true;

    @SuppressWarnings({ "ConstantConditions" })
    @com.drew.lang.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Design intent")
    public ByteArrayReader(@NotNull byte[] buffer)
    {
        if (buffer == null)
            throw new NullPointerException();
        
        _buffer = buffer;
    }

    @Override
    public long getLength()
    {
        return _buffer.length;
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

    @Override
    public short getUInt8(int index) throws BufferBoundsException
    {
        checkBounds(index, 1);

        return (short) (_buffer[index] & 0xFF);
    }

    @Override
    public byte getInt8(int index) throws BufferBoundsException
    {
        checkBounds(index, 1);

        return _buffer[index];
    }

    @Override
    public int getUInt16(int index) throws BufferBoundsException
    {
        checkBounds(index, 2);

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

    @Override
    public short getInt16(int index) throws BufferBoundsException
    {
        checkBounds(index, 2);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first
            return (short) (((short)_buffer[index    ] << 8 & (short)0xFF00) |
                            ((short)_buffer[index + 1]      & (short)0xFF));
        } else {
            // Intel ordering - LSB first
            return (short) (((short)_buffer[index + 1] << 8 & (short)0xFF00) |
                            ((short)_buffer[index    ]      & (short)0xFF));
        }
    }

    @Override
    public long getUInt32(int index) throws BufferBoundsException
    {
        checkBounds(index, 4);

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

    @Override
    public int getInt32(int index) throws BufferBoundsException
    {
        checkBounds(index, 4);

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

    @Override
    public long getInt64(int index) throws BufferBoundsException
    {
        checkBounds(index, 8);

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

    @Override
    public float getS15Fixed16(int index) throws BufferBoundsException
    {
        checkBounds(index, 4);

        if (_isMotorolaByteOrder) {
            float res = (_buffer[index    ] & 0xFF) << 8 |
                        (_buffer[index + 1] & 0xFF);
            int d =     (_buffer[index + 2] & 0xFF) << 8 |
                        (_buffer[index + 3] & 0xFF);
            return (float)(res + d/65536.0);
        } else {
            // this particular branch is untested
            float res = (_buffer[index + 3] & 0xFF) << 8 |
                        (_buffer[index + 2] & 0xFF);
            int d =     (_buffer[index + 1] & 0xFF) << 8 |
                        (_buffer[index    ] & 0xFF);
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
    
    @Override
    @NotNull
    public byte[] getBytes(int index, int count) throws BufferBoundsException
    {
        checkBounds(index, count);

        byte[] bytes = new byte[count];
        System.arraycopy(_buffer, index, bytes, 0, count);
        return bytes;
    }

    @Override
    @NotNull
    public String getString(int index, int bytesRequested) throws BufferBoundsException
    {
        return new String(getBytes(index, bytesRequested));
    }

    @Override
    @NotNull
    public String getString(int index, int bytesRequested, String charset) throws BufferBoundsException
    {
        byte[] bytes = getBytes(index, bytesRequested);
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(bytes);
        }
    }

    @Override
    @NotNull
    public String getNullTerminatedString(int index, int maxLengthBytes) throws BufferBoundsException
    {
        // NOTE currently only really suited to single-byte character strings

        checkBounds(index, maxLengthBytes);

        // Check for null terminators
        int length = 0;
        while ((index + length) < _buffer.length && _buffer[index + length] != '\0' && length < maxLengthBytes)
            length++;

        byte[] bytes = getBytes(index, length);
        return new String(bytes);
    }

    private void checkBounds(final int index, final int bytesRequested) throws BufferBoundsException
    {
        if (bytesRequested < 0 || index < 0 || (long)index + (long)bytesRequested - 1L >= (long)_buffer.length)
            throw new BufferBoundsException(_buffer.length, index, bytesRequested);
    }
}

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
import java.io.RandomAccessFile;
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
public class RandomAccessFileReader implements BufferReader
{
    @NotNull
    private final RandomAccessFile _file;
    private final long _length;
    private int _currentIndex;
    private boolean _isMotorolaByteOrder = true;

    @SuppressWarnings({ "ConstantConditions" })
    @com.drew.lang.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Design intent")
    public RandomAccessFileReader(@NotNull RandomAccessFile file) throws IOException
    {
        if (file == null)
            throw new NullPointerException();
        
        _file = file;
        _length = _file.length();
    }

    @Override
    public long getLength()
    {
        return _length;
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

    private byte read() throws BufferBoundsException
    {
        final int b;
        try {
            b = _file.read();
        } catch (IOException e) {
            throw new BufferBoundsException("IOException reading from file.", e);
        }

        if (b < 0)
            throw new BufferBoundsException("Unexpected end of file encountered.");
        assert(b <= 0xff);
        _currentIndex++;
        return (byte) b;
    }

    private void seek(final int index) throws BufferBoundsException
    {
        if (index == _currentIndex)
            return;
        try {
            _file.seek(index);
            _currentIndex = index;
        } catch (IOException e) {
            throw new BufferBoundsException("IOException seeking in file.", e);
        }
    }

    @Override
    public short getUInt8(int index) throws BufferBoundsException
    {
        checkBounds(index, 1);
        seek(index);

        return (short) (read() & 255);
    }

    @Override
    public byte getInt8(int index) throws BufferBoundsException
    {
        checkBounds(index, 1);
        seek(index);

        return read();
    }

    @Override
    public int getUInt16(int index) throws BufferBoundsException
    {
        checkBounds(index, 2);
        seek(index);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first
            return (read() << 8 & 0xFF00) |
                   (read()      & 0xFF);
        } else {
            // Intel ordering - LSB first
            return (read()      & 0xFF) |
                   (read() << 8 & 0xFF00);
        }
    }

    @Override
    public short getInt16(int index) throws BufferBoundsException
    {
        checkBounds(index, 2);
        seek(index);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first
            return (short) (((short) read() << 8 & (short)0xFF00) |
                            ((short) read()      & (short)0xFF));
        } else {
            // Intel ordering - LSB first
            return (short) (((short) read()      & (short)0xFF) |
                            ((short) read() << 8 & (short)0xFF00));
        }
    }

    @Override
    public long getUInt32(int index) throws BufferBoundsException
    {
        checkBounds(index, 4);
        seek(index);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (((long) read()) << 24 & 0xFF000000L) |
                   (((long) read()) << 16 & 0xFF0000L) |
                   (((long) read()) << 8  & 0xFF00L) |
                   (((long) read())       & 0xFFL);
        } else {
            // Intel ordering - LSB first (little endian)
            return (((long) read())       & 0xFFL) |
                   (((long) read()) << 8  & 0xFF00L) |
                   (((long) read()) << 16 & 0xFF0000L) |
                   (((long) read()) << 24 & 0xFF000000L);
        }
    }

    @Override
    public int getInt32(int index) throws BufferBoundsException
    {
        checkBounds(index, 4);
        seek(index);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (read() << 24 & 0xFF000000) |
                   (read() << 16 & 0xFF0000) |
                   (read() << 8  & 0xFF00) |
                   (read()       & 0xFF);
        } else {
            // Intel ordering - LSB first (little endian)
            return (read()       & 0xFF) |
                   (read() << 8  & 0xFF00) |
                   (read() << 16 & 0xFF0000) |
                   (read() << 24 & 0xFF000000);

        }
    }

    @Override
    public long getInt64(int index) throws BufferBoundsException
    {
        checkBounds(index, 8);
        seek(index);

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first
            return ((long) read() << 56 & 0xFF00000000000000L) |
                   ((long) read() << 48 & 0xFF000000000000L) |
                   ((long) read() << 40 & 0xFF0000000000L) |
                   ((long) read() << 32 & 0xFF00000000L) |
                   ((long) read() << 24 & 0xFF000000L) |
                   ((long) read() << 16 & 0xFF0000L) |
                   ((long) read() << 8  & 0xFF00L) |
                   ((long) read()       & 0xFFL);
        } else {
            // Intel ordering - LSB first
            return ((long) read()       & 0xFFL) |
                   ((long) read() << 8  & 0xFF00L) |
                   ((long) read() << 16 & 0xFF0000L) |
                   ((long) read() << 24 & 0xFF000000L) |
                   ((long) read() << 32 & 0xFF00000000L) |
                   ((long) read() << 40 & 0xFF0000000000L) |
                   ((long) read() << 48 & 0xFF000000000000L) |
                   ((long) read() << 56 & 0xFF00000000000000L);

        }
    }

    @Override
    public float getS15Fixed16(int index) throws BufferBoundsException
    {
        checkBounds(index, 4);
        seek(index);

        if (_isMotorolaByteOrder) {
            float res = (read() & 255) << 8 |
                        (read() & 255);
            int d =     (read() & 255) << 8 |
                        (read() & 255);
            return (float)(res + d/65536.0);
        } else {
            // this particular branch is untested
            int d =     (read() & 255) |
                        (read() & 255) << 8;
            float res = (read() & 255) |
                        (read() & 255) << 8;
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
        seek(index);

        byte[] bytes = new byte[count];
        final int bytesRead;
        try {
            bytesRead = _file.read(bytes);
            _currentIndex += bytesRead;
        } catch (IOException e) {
            throw new BufferBoundsException("Unexpected end of file encountered.", e);
        }
        if (bytesRead != count)
            throw new BufferBoundsException("Unexpected end of file encountered.");
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
        checkBounds(index, bytesRequested);

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
        seek(index);

        // Check for null terminators
        int length = 0;
        while ((index + length) < _length && read() != '\0' && length < maxLengthBytes)
            length++;

        byte[] bytes = getBytes(index, length);
        return new String(bytes);
    }

    private void checkBounds(final int index, final int bytesRequested) throws BufferBoundsException
    {
        if (bytesRequested < 0)
            throw new BufferBoundsException("Requested negative number of bytes.");
        if (index < 0)
            throw new BufferBoundsException("Requested data from a negative index within the file.");
        if ((long)index + (long)bytesRequested - 1L >= _length)
            throw new BufferBoundsException("Requested data from beyond the end of the file.");
    }
}

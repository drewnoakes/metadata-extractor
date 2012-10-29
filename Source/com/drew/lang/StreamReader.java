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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class StreamReader implements SequentialReader
{
    @NotNull
    private final InputStream _stream;
    private boolean _isMotorolaByteOrder;

    public StreamReader(@NotNull InputStream stream)
    {
        _stream = stream;
        _isMotorolaByteOrder = true;
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
    public short getUInt8() throws IOException
    {
        int value = _stream.read();
        if (value == -1)
            throw new EOFException("End of stream reached.");
        return (short)(value & 0xFF);
    }

    @Override
    public byte getInt8() throws IOException
    {
        int value = _stream.read();
        if (value == -1)
            throw new EOFException("End of stream reached.");
        return (byte)value;
    }

    @Override
    public int getUInt16() throws IOException
    {
        byte byte1 = getInt8();
        byte byte2 = getInt8();

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (byte1 << 8 | byte2 & 0xFF) & 0xFFFF;
        } else {
            // Intel ordering - LSB first (little endian)
            return (byte2 << 8 | byte1 & 0xFF) & 0xFFFF;
        }
    }

    @Override
    public short getInt16() throws IOException
    {
        byte byte1 = getInt8();
        byte byte2 = getInt8();

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (short) ((byte1 << 8 | byte2 & 0xFF) & 0xFFFF);
        } else {
            // Intel ordering - LSB first (little endian)
            return (short) ((byte2 << 8 | byte1 & 0xFF) & 0xFFFF);
        }
    }

    @Override
    public int getInt32() throws IOException
    {
        byte byte1 = getInt8();
        byte byte2 = getInt8();
        byte byte3 = getInt8();
        byte byte4 = getInt8();

        if (_isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (byte1 << 24) |
                   (byte2 << 16) |
                   (byte3 << 8 ) |
                   (byte4      );
        } else {
            // Intel ordering - LSB first (little endian)
            return (byte4      ) |
                   (byte3 << 8 ) |
                   (byte2 << 16) |
                   (byte1 << 24);
        }
    }

    @NotNull
    @Override
    public byte[] getBytes(int count) throws IOException
    {
        byte[] bytes = new byte[count];
        int totalBytesRead = 0;

        while (totalBytesRead != count) {
            final int bytesRead = _stream.read(bytes, totalBytesRead, count - totalBytesRead);
            if (bytesRead == -1)
                throw new EOFException("End of stream reached.");
            totalBytesRead += bytesRead;
            assert(totalBytesRead <= count);
        }

        return bytes;
    }
}

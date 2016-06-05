/*
 * Copyright 2002-2016 Drew Noakes
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
import java.io.UnsupportedEncodingException;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class SequentialReader
{
    // TODO review whether the masks are needed (in both this and RandomAccessReader)

    private boolean _isMotorolaByteOrder = true;

    /**
     * Gets the next byte in the sequence.
     *
     * @return The read byte value
     */
    protected abstract byte getByte() throws IOException;

    /**
     * Returns the required number of bytes from the sequence.
     *
     * @param count The number of bytes to be returned
     * @return The requested bytes
     */
    @NotNull
    public abstract byte[] getBytes(int count) throws IOException;

    /**
     * Skips forward in the sequence. If the sequence ends, an {@link EOFException} is thrown.
     *
     * @param n the number of byte to skip. Must be zero or greater.
     * @throws EOFException the end of the sequence is reached.
     * @throws IOException an error occurred reading from the underlying source.
     */
    public abstract void skip(long n) throws IOException;

    /**
     * Skips forward in the sequence, returning a boolean indicating whether the skip succeeded, or whether the sequence ended.
     *
     * @param n the number of byte to skip. Must be zero or greater.
     * @return a boolean indicating whether the skip succeeded, or whether the sequence ended.
     * @throws IOException an error occurred reading from the underlying source.
     */
    public abstract boolean trySkip(long n) throws IOException;

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
     * Returns an unsigned 8-bit int calculated from the next byte of the sequence.
     *
     * @return the 8 bit int value, between 0 and 255
     */
    public short getUInt8() throws IOException
    {
        return (short) (getByte() & 0xFF);
    }

    /**
     * Returns a signed 8-bit int calculated from the next byte the sequence.
     *
     * @return the 8 bit int value, between 0x00 and 0xFF
     */
    public byte getInt8() throws IOException
    {
        return getByte();
    }

    /**
     * Returns an unsigned 16-bit int calculated from the next two bytes of the sequence.
     *
     * @return the 16 bit int value, between 0x0000 and 0xFFFF
     */
    public int getUInt16() throws IOException
    {
        if (_isMotorolaByteOrder) {
            // Motorola - MSB first
            return (getByte() << 8 & 0xFF00) |
                   (getByte()      & 0xFF);
        } else {
            // Intel ordering - LSB first
            return (getByte()      & 0xFF) |
                   (getByte() << 8 & 0xFF00);
        }
    }

    /**
     * Returns a signed 16-bit int calculated from two bytes of data (MSB, LSB).
     *
     * @return the 16 bit int value, between 0x0000 and 0xFFFF
     * @throws IOException the buffer does not contain enough bytes to service the request
     */
    public short getInt16() throws IOException
    {
        if (_isMotorolaByteOrder) {
            // Motorola - MSB first
            return (short) (((short)getByte() << 8 & (short)0xFF00) |
                            ((short)getByte()      & (short)0xFF));
        } else {
            // Intel ordering - LSB first
            return (short) (((short)getByte()      & (short)0xFF) |
                            ((short)getByte() << 8 & (short)0xFF00));
        }
    }

    /**
     * Get a 32-bit unsigned integer from the buffer, returning it as a long.
     *
     * @return the unsigned 32-bit int value as a long, between 0x00000000 and 0xFFFFFFFF
     * @throws IOException the buffer does not contain enough bytes to service the request
     */
    public long getUInt32() throws IOException
    {
        if (_isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (((long)getByte()) << 24 & 0xFF000000L) |
                   (((long)getByte()) << 16 & 0xFF0000L) |
                   (((long)getByte()) << 8  & 0xFF00L) |
                   (((long)getByte())       & 0xFFL);
        } else {
            // Intel ordering - LSB first (little endian)
            return (((long)getByte())       & 0xFFL) |
                   (((long)getByte()) << 8  & 0xFF00L) |
                   (((long)getByte()) << 16 & 0xFF0000L) |
                   (((long)getByte()) << 24 & 0xFF000000L);
        }
    }

    /**
     * Returns a signed 32-bit integer from four bytes of data.
     *
     * @return the signed 32 bit int value, between 0x00000000 and 0xFFFFFFFF
     * @throws IOException the buffer does not contain enough bytes to service the request
     */
    public int getInt32() throws IOException
    {
        if (_isMotorolaByteOrder) {
            // Motorola - MSB first (big endian)
            return (getByte() << 24 & 0xFF000000) |
                   (getByte() << 16 & 0xFF0000) |
                   (getByte() << 8  & 0xFF00) |
                   (getByte()       & 0xFF);
        } else {
            // Intel ordering - LSB first (little endian)
            return (getByte()       & 0xFF) |
                   (getByte() << 8  & 0xFF00) |
                   (getByte() << 16 & 0xFF0000) |
                   (getByte() << 24 & 0xFF000000);
        }
    }

    /**
     * Get a signed 64-bit integer from the buffer.
     *
     * @return the 64 bit int value, between 0x0000000000000000 and 0xFFFFFFFFFFFFFFFF
     * @throws IOException the buffer does not contain enough bytes to service the request
     */
    public long getInt64() throws IOException
    {
        if (_isMotorolaByteOrder) {
            // Motorola - MSB first
            return ((long)getByte() << 56 & 0xFF00000000000000L) |
                   ((long)getByte() << 48 & 0xFF000000000000L) |
                   ((long)getByte() << 40 & 0xFF0000000000L) |
                   ((long)getByte() << 32 & 0xFF00000000L) |
                   ((long)getByte() << 24 & 0xFF000000L) |
                   ((long)getByte() << 16 & 0xFF0000L) |
                   ((long)getByte() << 8  & 0xFF00L) |
                   ((long)getByte()       & 0xFFL);
        } else {
            // Intel ordering - LSB first
            return ((long)getByte()       & 0xFFL) |
                   ((long)getByte() << 8  & 0xFF00L) |
                   ((long)getByte() << 16 & 0xFF0000L) |
                   ((long)getByte() << 24 & 0xFF000000L) |
                   ((long)getByte() << 32 & 0xFF00000000L) |
                   ((long)getByte() << 40 & 0xFF0000000000L) |
                   ((long)getByte() << 48 & 0xFF000000000000L) |
                   ((long)getByte() << 56 & 0xFF00000000000000L);
        }
    }

    /**
     * Gets a s15.16 fixed point float from the buffer.
     * <p>
     * This particular fixed point encoding has one sign bit, 15 numerator bits and 16 denominator bits.
     *
     * @return the floating point value
     * @throws IOException the buffer does not contain enough bytes to service the request
     */
    public float getS15Fixed16() throws IOException
    {
        if (_isMotorolaByteOrder) {
            float res = (getByte() & 0xFF) << 8 |
                        (getByte() & 0xFF);
            int d =     (getByte() & 0xFF) << 8 |
                        (getByte() & 0xFF);
            return (float)(res + d/65536.0);
        } else {
            // this particular branch is untested
            int d =     (getByte() & 0xFF) |
                        (getByte() & 0xFF) << 8;
            float res = (getByte() & 0xFF) |
                        (getByte() & 0xFF) << 8;
            return (float)(res + d/65536.0);
        }
    }

    public float getFloat32() throws IOException
    {
        return Float.intBitsToFloat(getInt32());
    }

    public double getDouble64() throws IOException
    {
        return Double.longBitsToDouble(getInt64());
    }

    @NotNull
    public String getString(int bytesRequested) throws IOException
    {
        return new String(getBytes(bytesRequested));
    }

    @NotNull
    public String getString(int bytesRequested, String charset) throws IOException
    {
        byte[] bytes = getBytes(bytesRequested);
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(bytes);
        }
    }

    /**
     * Creates a String from the stream, ending where <code>byte=='\0'</code> or where <code>length==maxLength</code>.
     *
     * @param maxLengthBytes The maximum number of bytes to read.  If a zero-byte is not reached within this limit,
     *                       reading will stop and the string will be truncated to this length.
     * @return The read string.
     * @throws IOException The buffer does not contain enough bytes to satisfy this request.
     */
    @NotNull
    public String getNullTerminatedString(int maxLengthBytes) throws IOException
    {
        // NOTE currently only really suited to single-byte character strings

        byte[] bytes = new byte[maxLengthBytes];

        // Count the number of non-null bytes
        int length = 0;
        while (length < bytes.length && (bytes[length] = getByte()) != '\0')
            length++;

        return new String(bytes, 0, length);
    }
}

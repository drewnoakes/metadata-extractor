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

public interface RandomAccessReader
{
    /**
     * Returns the length of the data source in bytes.
     * <p/>
     * This is a simple operation for implementations (such as {@link RandomAccessFileReader} and
     * {@link ByteArrayReader}) that have the entire data source available.
     * <p/>
     * Users of this method must be aware that sequentially accessed implementations such as
     * {@link RandomAccessStreamReader} will have to read and buffer the entire data source in
     * order to determine the length.
     *
     * @return the length of the data source, in bytes.
     */
    long getLength();

    /**
     * Sets the endianness of this reader.
     * <ul>
     * <li><code>true</code> for Motorola (or big) endianness</li>
     * <li><code>false</code> for Intel (or little) endianness</li>
     * </ul>
     *
     * @param motorolaByteOrder <code>true</code> for motorola/big endian, <code>false</code> for intel/little endian
     */
    void setMotorolaByteOrder(boolean motorolaByteOrder);

    /**
     * Gets the endianness of this reader.
     * <ul>
     * <li><code>true</code> for Motorola (or big) endianness</li>
     * <li><code>false</code> for Intel (or little) endianness</li>
     * </ul>
     */
    boolean isMotorolaByteOrder();

    /**
     * Returns an unsigned 8-bit int calculated from one byte of data at the specified index.
     *
     * @param index position within the data buffer to read byte
     * @return the 8 bit int value, between 0 and 255
     * @throws BufferBoundsException the buffer does not contain enough bytes to service the request, or index is negative
     */
    short getUInt8(int index) throws BufferBoundsException;

    /**
     * Returns a signed 8-bit int calculated from one byte of data at the specified index.
     *
     * @param index position within the data buffer to read byte
     * @return the 8 bit int value, between 0x00 and 0xFF
     * @throws BufferBoundsException the buffer does not contain enough bytes to service the request, or index is negative
     */
    byte getInt8(int index) throws BufferBoundsException;

    /**
     * Returns an unsigned 16-bit int calculated from two bytes of data at the specified index.
     *
     * @param index position within the data buffer to read first byte
     * @return the 16 bit int value, between 0x0000 and 0xFFFF
     * @throws BufferBoundsException the buffer does not contain enough bytes to service the request, or index is negative
     */
    int getUInt16(int index) throws BufferBoundsException;

    /**
     * Returns a signed 16-bit int calculated from two bytes of data at the specified index (MSB, LSB).
     *
     * @param index position within the data buffer to read first byte
     * @return the 16 bit int value, between 0x0000 and 0xFFFF
     * @throws BufferBoundsException the buffer does not contain enough bytes to service the request, or index is negative
     */
    short getInt16(int index) throws BufferBoundsException;

    /**
     * Get a 32-bit unsigned integer from the buffer, returning it as a long.
     *
     * @param index position within the data buffer to read first byte
     * @return the unsigned 32-bit int value as a long, between 0x00000000 and 0xFFFFFFFF
     * @throws BufferBoundsException the buffer does not contain enough bytes to service the request, or index is negative
     */
    long getUInt32(int index) throws BufferBoundsException;

    /**
     * Returns a signed 32-bit integer from four bytes of data at the specified index the buffer.
     *
     * @param index position within the data buffer to read first byte
     * @return the signed 32 bit int value, between 0x00000000 and 0xFFFFFFFF
     * @throws BufferBoundsException the buffer does not contain enough bytes to service the request, or index is negative
     */
    int getInt32(int index) throws BufferBoundsException;

    /**
     * Get a signed 64-bit integer from the buffer.
     *
     * @param index position within the data buffer to read first byte
     * @return the 64 bit int value, between 0x0000000000000000 and 0xFFFFFFFFFFFFFFFF
     * @throws BufferBoundsException the buffer does not contain enough bytes to service the request, or index is negative
     */
    long getInt64(int index) throws BufferBoundsException;

    float getS15Fixed16(int index) throws BufferBoundsException;

    float getFloat32(int index) throws BufferBoundsException;

    double getDouble64(int index) throws BufferBoundsException;

    @NotNull
    byte[] getBytes(int index, int count) throws BufferBoundsException;

    @NotNull
    String getString(int index, int bytesRequested) throws BufferBoundsException;

    @NotNull
    String getString(int index, int bytesRequested, String charset) throws BufferBoundsException;

    /**
     * Creates a String from the _data buffer starting at the specified index,
     * and ending where <code>byte=='\0'</code> or where <code>length==maxLength</code>.
     *
     * @param index          The index within the buffer at which to start reading the string.
     * @param maxLengthBytes The maximum number of bytes to read.  If a zero-byte is not reached within this limit,
     *                       reading will stop and the string will be truncated to this length.
     * @return The read string.
     * @throws BufferBoundsException The buffer does not contain enough bytes to satisfy this request.
     */
    @NotNull
    String getNullTerminatedString(int index, int maxLengthBytes) throws BufferBoundsException;
}

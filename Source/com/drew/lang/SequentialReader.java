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

/**
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public interface SequentialReader
{
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
     * @return the 8 bit int value, between 0 and 255
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    short getUInt8() throws IOException;

    /**
     * Returns a signed 8-bit int calculated from one byte of data at the specified index.
     *
     * @return the 8 bit int value, between 0x00 and 0xFF
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    byte getInt8() throws IOException;

    /**
     * Returns an unsigned 16-bit int calculated from two bytes of data at the specified index.
     *
     * @return the 16 bit int value, between 0x0000 and 0xFFFF
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    int getUInt16() throws IOException;

    /**
     * Returns a signed 16-bit int calculated from two bytes of data (MSB, LSB).
     *
     * @return the 16 bit int value, between 0x0000 and 0xFFFF
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    short getInt16() throws IOException;

//    /**
//     * Get a 32-bit unsigned integer from the buffer, returning it as a long.
//     *
//     * @return the unsigned 32-bit int value as a long, between 0x00000000 and 0xFFFFFFFF
//     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
//     */
//    long getUInt32() throws IOException;

    /**
     * Returns a signed 32-bit integer from four bytes of data.
     *
     * @return the signed 32 bit int value, between 0x00000000 and 0xFFFFFFFF
     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
     */
    int getInt32() throws IOException;

//    /**
//     * Get a signed 64-bit integer from the buffer.
//     *
//     * @return the 64 bit int value, between 0x0000000000000000 and 0xFFFFFFFFFFFFFFFF
//     * @throws IOException the buffer does not contain enough bytes to service the request, or index is negative
//     */
//    long getInt64() throws IOException;
//
//    float getS15Fixed16() throws IOException;
//
//    float getFloat32() throws IOException;
//
//    double getDouble64() throws IOException;

    @NotNull
    byte[] getBytes(int count) throws IOException;

//    @NotNull
//    String getString(int bytesRequested) throws IOException;
//
//    @NotNull
//    String getString(int bytesRequested, String charset) throws IOException;
//
//    /**
//     * Creates a String from the stream, ending where <code>byte=='\0'</code> or where <code>length==maxLength</code>.
//     *
//     * @param maxLengthBytes The maximum number of bytes to read.  If a zero-byte is not reached within this limit,
//     *                       reading will stop and the string will be truncated to this length.
//     * @return The read string.
//     * @throws IOException The buffer does not contain enough bytes to satisfy this request.
//     */
//    @NotNull
//    String getNullTerminatedString(int maxLengthBytes) throws IOException;
}

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
package com.drew.metadata.jfif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.MetadataReader;

/**
 * Reader for JFIF data, found in the APP0 Jpeg segment.
 * <p/>
 * More info at: http://en.wikipedia.org/wiki/JPEG_File_Interchange_Format
 *
 * @author Yuri Binev, Drew Noakes
 */
public class JfifReader implements MetadataReader
{
    // TODO add unit tests for JFIF data

    /** The SOF0 data segment. */
    @NotNull
    private final byte[] _data;

    /**
     * Initialises a new JfifReader for a given byte array.
     *
     * @param data the byte array to read Jfif data from
     */
    public JfifReader(@NotNull byte[] data)
    {
        if (data == null)
            throw new NullPointerException();
        _data = data;
    }

    /**
     * Performs the Jfif data extraction, adding found values to the specified
     * instance of <code>Metadata</code>.
     */
    public void extract(@NotNull Metadata metadata)
    {
        JfifDirectory directory = metadata.getOrCreateDirectory(JfifDirectory.class);

        try {
            int ver = get32Bits(JfifDirectory.TAG_JFIF_VERSION);
            directory.setInt(JfifDirectory.TAG_JFIF_VERSION, ver);

            int units = get16Bits(JfifDirectory.TAG_JFIF_UNITS);
            directory.setInt(JfifDirectory.TAG_JFIF_UNITS, units);

            int height = get32Bits(JfifDirectory.TAG_JFIF_RESX);
            directory.setInt(JfifDirectory.TAG_JFIF_RESX, height);

            int width = get32Bits(JfifDirectory.TAG_JFIF_RESY);
            directory.setInt(JfifDirectory.TAG_JFIF_RESY, width);

        } catch (MetadataException me) {
            directory.addError("MetadataException: " + me);
        }
    }

    /**
     * Returns an int calculated from two bytes of data at the specified offset (MSB, LSB).
     *
     * @param offset position within the data buffer to read first byte
     * @return the 32 bit int value, between 0x0000 and 0xFFFF
     */
    private int get32Bits(int offset) throws MetadataException
    {
        if (offset + 1 >= _data.length)
            throw new MetadataException("Attempt to read bytes from outside Jfif segment data buffer");
        return ((_data[offset] & 255) << 8) | (_data[offset + 1] & 255);
    }

    /**
     * Returns an int calculated from one byte of data at the specified offset.
     *
     * @param offset position within the data buffer to read byte
     * @return the 16 bit int value, between 0x00 and 0xFF
     */
    private int get16Bits(int offset) throws MetadataException
    {
        if (offset >= _data.length)
            throw new MetadataException("Attempt to read bytes from outside Jfif segment data buffer");
        return (_data[offset] & 255);
    }
}
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
package com.drew.metadata.jpeg;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.MetadataReader;

import java.io.File;
import java.io.InputStream;

/**
 * Decodes Jpeg SOF0 data, populating a <code>Metadata</code> object with tag values in a <code>JpegDirectory</code>.
 *
 * @author Darrell Silver http://www.darrellsilver.com and Drew Noakes http://drewnoakes.com
 */
public class JpegReader implements MetadataReader
{
    /**
     * The SOF0 data segment.
     */
    private final byte[] _data;

    /**
     * Creates a new JpegReader for the specified Jpeg jpegFile.
     */
    public JpegReader(File jpegFile) throws JpegProcessingException
    {
        this(new JpegSegmentReader(jpegFile).readSegment(JpegSegmentReader.SEGMENT_SOF0));
    }

    /**
     * Creates a JpegReader for a JPEG stream.
     * @param is JPEG stream. Stream will be closed.
     */
    public JpegReader(InputStream is) throws JpegProcessingException
    {
        this(new JpegSegmentReader(is).readSegment(JpegSegmentReader.SEGMENT_APPD));
    }

    /**
     * Creates a JpegReader for a byte[] containing JPEG data.
     * @param data the contents of an entire JPEG file as a byte[]
     */
    public JpegReader(byte[] data)
    {
        _data = data;
    }

    /**
     * Performs the Jpeg data extraction, adding found values to the specified
     * instance of <code>Metadata</code>.
     */
    public void extract(Metadata metadata)
    {
        if (_data==null)
            return;

        JpegDirectory directory = (JpegDirectory)metadata.getDirectory(JpegDirectory.class);

        try {
            // data precision
            int dataPrecision = get16Bits(JpegDirectory.TAG_JPEG_DATA_PRECISION);
            directory.setInt(JpegDirectory.TAG_JPEG_DATA_PRECISION, dataPrecision);

            // process height
            int height = get32Bits(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT);
            directory.setInt(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT, height);

            // process width
            int width = get32Bits(JpegDirectory.TAG_JPEG_IMAGE_WIDTH);
            directory.setInt(JpegDirectory.TAG_JPEG_IMAGE_WIDTH, width);

            // number of components
            int numberOfComponents = get16Bits(JpegDirectory.TAG_JPEG_NUMBER_OF_COMPONENTS);
            directory.setInt(JpegDirectory.TAG_JPEG_NUMBER_OF_COMPONENTS, numberOfComponents);

            // for each component, there are three bytes of data:
            // 1 - Component ID: 1 = Y, 2 = Cb, 3 = Cr, 4 = I, 5 = Q
            // 2 - Sampling factors: bit 0-3 vertical, 4-7 horizontal
            // 3 - Quantization table number
            int offset = 6;
            for (int i=0; i<numberOfComponents; i++)
            {
                int componentId = get16Bits(offset++);
                int samplingFactorByte = get16Bits(offset++);
                int quantizationTableNumber = get16Bits(offset++);
                JpegComponent component = new JpegComponent(componentId, samplingFactorByte, quantizationTableNumber);
                directory.setObject(JpegDirectory.TAG_JPEG_COMPONENT_DATA_1 + i, component);
            }

        } catch (MetadataException me) {
            directory.addError("MetadataException: " + me);
        }
    }

    /**
     * Returns an int calculated from two bytes of data at the specified offset (MSB, LSB).
     * @param offset position within the data buffer to read first byte
     * @return the 32 bit int value, between 0x0000 and 0xFFFF
     */
    private int get32Bits(int offset) throws MetadataException
    {
        if (offset+1>=_data.length) {
            throw new MetadataException("Attempt to read bytes from outside Jpeg segment data buffer");
        }

        return ((_data[offset] & 255) << 8) | (_data[offset + 1] & 255);
    }

    /**
     * Returns an int calculated from one byte of data at the specified offset.
     * @param offset position within the data buffer to read byte
     * @return the 16 bit int value, between 0x00 and 0xFF
     */
    private int get16Bits(int offset) throws MetadataException
    {
        if (offset>=_data.length) {
            throw new MetadataException("Attempt to read bytes from outside Jpeg segment data buffer");
        }

        return (_data[offset] & 255);
    }
}

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
package com.drew.metadata.jpeg;

import com.drew.lang.BufferBoundsException;
import com.drew.lang.BufferReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;

/**
 * Decodes Jpeg SOF0 data, populating a <code>Metadata</code> object with tag values in a <code>JpegDirectory</code>.
 *
 * @author Darrell Silver http://www.darrellsilver.com and Drew Noakes http://drewnoakes.com
 */
public class JpegReader implements MetadataReader
{
    /**
     * Performs the Jpeg data extraction, adding found values to the specified
     * instance of <code>Metadata</code>.
     */
    public void extract(@NotNull final BufferReader reader, @NotNull Metadata metadata)
    {
        JpegDirectory directory = metadata.getOrCreateDirectory(JpegDirectory.class);

        try {
            // data precision
            int dataPrecision = reader.getUInt8(JpegDirectory.TAG_JPEG_DATA_PRECISION);
            directory.setInt(JpegDirectory.TAG_JPEG_DATA_PRECISION, dataPrecision);

            // process height
            int height = reader.getUInt16(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT);
            directory.setInt(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT, height);

            // process width
            int width = reader.getUInt16(JpegDirectory.TAG_JPEG_IMAGE_WIDTH);
            directory.setInt(JpegDirectory.TAG_JPEG_IMAGE_WIDTH, width);

            // number of components
            int numberOfComponents = reader.getUInt8(JpegDirectory.TAG_JPEG_NUMBER_OF_COMPONENTS);
            directory.setInt(JpegDirectory.TAG_JPEG_NUMBER_OF_COMPONENTS, numberOfComponents);

            // for each component, there are three bytes of data:
            // 1 - Component ID: 1 = Y, 2 = Cb, 3 = Cr, 4 = I, 5 = Q
            // 2 - Sampling factors: bit 0-3 vertical, 4-7 horizontal
            // 3 - Quantization table number
            int offset = 6;
            for (int i = 0; i < numberOfComponents; i++) {
                int componentId = reader.getUInt8(offset++);
                int samplingFactorByte = reader.getUInt8(offset++);
                int quantizationTableNumber = reader.getUInt8(offset++);
                JpegComponent component = new JpegComponent(componentId, samplingFactorByte, quantizationTableNumber);
                directory.setObject(JpegDirectory.TAG_JPEG_COMPONENT_DATA_1 + i, component);
            }

        } catch (BufferBoundsException ex) {
            directory.addError(ex.getMessage());
        }
    }
}

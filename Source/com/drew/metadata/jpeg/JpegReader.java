/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
package com.drew.metadata.jpeg;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.util.Arrays;

/**
 * Decodes JPEG SOFn data, populating a {@link Metadata} object with tag values in a {@link JpegDirectory}.
 *
 * @author Drew Noakes https://drewnoakes.com
 * @author Darrell Silver http://www.darrellsilver.com
 */
public class JpegReader implements JpegSegmentMetadataReader
{
    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        // NOTE that some SOFn values do not exist
        return Arrays.asList(
            JpegSegmentType.SOF0,
            JpegSegmentType.SOF1,
            JpegSegmentType.SOF2,
            JpegSegmentType.SOF3,
//            JpegSegmentType.SOF4,
            JpegSegmentType.SOF5,
            JpegSegmentType.SOF6,
            JpegSegmentType.SOF7,
//            JpegSegmentType.JPG,
            JpegSegmentType.SOF9,
            JpegSegmentType.SOF10,
            JpegSegmentType.SOF11,
//            JpegSegmentType.SOF12,
            JpegSegmentType.SOF13,
            JpegSegmentType.SOF14,
            JpegSegmentType.SOF15
        );
    }

    public void readJpegSegments(@NotNull Iterable<byte[]> segments, @NotNull Metadata metadata, @NotNull JpegSegmentType segmentType)
    {
        for (byte[] segmentBytes : segments) {
            extract(segmentBytes, metadata, segmentType);
        }
    }

    public void extract(byte[] segmentBytes, Metadata metadata, JpegSegmentType segmentType)
    {
        JpegDirectory directory = new JpegDirectory();
        metadata.addDirectory(directory);

        // The value of TAG_COMPRESSION_TYPE is determined by the segment type found
        directory.setInt(JpegDirectory.TAG_COMPRESSION_TYPE, segmentType.byteValue - JpegSegmentType.SOF0.byteValue);

        SequentialReader reader = new SequentialByteArrayReader(segmentBytes);

        try {
            directory.setInt(JpegDirectory.TAG_DATA_PRECISION, reader.getUInt8());
            directory.setInt(JpegDirectory.TAG_IMAGE_HEIGHT, reader.getUInt16());
            directory.setInt(JpegDirectory.TAG_IMAGE_WIDTH, reader.getUInt16());
            short componentCount = reader.getUInt8();
            directory.setInt(JpegDirectory.TAG_NUMBER_OF_COMPONENTS, componentCount);

            // for each component, there are three bytes of data:
            // 1 - Component ID: 1 = Y, 2 = Cb, 3 = Cr, 4 = I, 5 = Q
            // 2 - Sampling factors: bit 0-3 vertical, 4-7 horizontal
            // 3 - Quantization table number
            for (int i = 0; i < (int)componentCount; i++) {
                final int componentId = reader.getUInt8();
                final int samplingFactorByte = reader.getUInt8();
                final int quantizationTableNumber = reader.getUInt8();
                final JpegComponent component = new JpegComponent(componentId, samplingFactorByte, quantizationTableNumber);
                directory.setObject(JpegDirectory.TAG_COMPONENT_DATA_1 + i, component);
            }
        } catch (IOException ex) {
            directory.addError(ex.getMessage());
        }
    }
}

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
import com.drew.metadata.jpeg.HuffmanTablesDirectory.HuffmanTable;
import com.drew.metadata.jpeg.HuffmanTablesDirectory.HuffmanTable.HuffmanTableClass;
import java.io.IOException;
import java.util.Collections;

/**
 * Reader for JPEG Huffman tables, found in the DHT JPEG segment.
 *
 * @author Nadahar
 */
public class JpegDhtReader implements JpegSegmentMetadataReader
{
    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Collections.singletonList(JpegSegmentType.DHT);
    }

    public void readJpegSegments(@NotNull Iterable<byte[]> segments, @NotNull Metadata metadata, @NotNull JpegSegmentType segmentType)
    {
        for (byte[] segmentBytes : segments) {
            extract(new SequentialByteArrayReader(segmentBytes), metadata);
        }
    }

    /**
     * Performs the DHT tables extraction, adding found tables to the specified
     * instance of {@link Metadata}.
     */
    public void extract(@NotNull final SequentialReader reader, @NotNull final Metadata metadata)
    {
        HuffmanTablesDirectory directory = metadata.getFirstDirectoryOfType(HuffmanTablesDirectory.class);
        if (directory == null) {
            directory = new HuffmanTablesDirectory();
            metadata.addDirectory(directory);
        }

        try {
            while (reader.available() > 0) {
                byte header = reader.getByte();
                HuffmanTableClass tableClass = HuffmanTableClass.typeOf((header & 0xF0) >> 4);
                int tableDestinationId = header & 0xF;

                byte[] lBytes = getBytes(reader, 16);
                int vCount = 0;
                for (byte b : lBytes) {
                    vCount += (b & 0xFF);
                }
                byte[] vBytes = getBytes(reader, vCount);
                directory.getTables().add(new HuffmanTable(tableClass, tableDestinationId, lBytes, vBytes));
            }
        } catch (IOException me) {
            directory.addError(me.getMessage());
        }

        directory.setInt(HuffmanTablesDirectory.TAG_NUMBER_OF_TABLES, directory.getTables().size());
    }

    private byte[] getBytes(@NotNull final SequentialReader reader, int count) throws IOException {
        byte[] bytes = new byte[count];
        for (int i = 0; i < count; i++) {
            byte b = reader.getByte();
            if ((b & 0xFF) == 0xFF) {
                byte stuffing = reader.getByte();
                if (stuffing != 0x00) {
                    throw new IOException("Marker " + JpegSegmentType.fromByte(stuffing) + " found inside DHT segment");
                }
            }
            bytes[i] = b;
        }
        return bytes;
    }
}

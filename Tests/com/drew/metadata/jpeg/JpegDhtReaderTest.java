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

import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.jpeg.HuffmanTablesDirectory.HuffmanTable.HuffmanTableClass;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * @author Nadahar
 */
public class JpegDhtReaderTest
{
    @NotNull
    public static HuffmanTablesDirectory processBytes(String filePath) throws Exception
    {
        Metadata metadata = new Metadata();
        JpegSegmentData segmentData = JpegSegmentReader.readSegments(
            new File(filePath),
            Collections.singletonList(JpegSegmentType.DHT));

        Iterable<byte[]> segments = segmentData.getSegments(JpegSegmentType.DHT);
        for (byte[] segment : segments) {
            new JpegDhtReader().extract(new SequentialByteArrayReader(segment), metadata);
        }


        HuffmanTablesDirectory directory = metadata.getFirstDirectoryOfType(HuffmanTablesDirectory.class);
        assertNotNull(directory);
        assertEquals(1, metadata.getDirectoriesOfType(HuffmanTablesDirectory.class).size());
        return directory;
    }

    private HuffmanTablesDirectory _directory;

    @Before
    public void setUp() throws Exception
    {
        _directory = processBytes("Tests/Data/withExifAndIptc.jpg");
    }

    @Test
    public void testExtract_NumberOfTables() throws Exception
    {
        assertEquals(4, _directory.getInt(HuffmanTablesDirectory.TAG_NUMBER_OF_TABLES));
        assertEquals(4, _directory.getNumberOfTables());
    }

    @Test
    public void testExtract_Tables() throws Exception
    {
        byte[] l = {0, 1, 4, 1, 2, 3, 3, 8, 5, 9, 6, 4, 6, 2, 3, 0};
        byte[] v = {0, 1, 3, 2, 4, 5};

        assertArrayEquals(l, _directory.getTable(1).getLengthBytes());
        assertArrayEquals(v, _directory.getTable(2).getValueBytes());
        assertEquals(HuffmanTableClass.DC, _directory.getTable(0).getTableClass());
        assertEquals(HuffmanTableClass.AC, _directory.getTable(1).getTableClass());
        assertEquals(HuffmanTableClass.DC, _directory.getTable(2).getTableClass());
        assertEquals(HuffmanTableClass.AC, _directory.getTable(3).getTableClass());
        assertEquals(0, _directory.getTable(0).getTableDestinationId());
        assertEquals(0, _directory.getTable(1).getTableDestinationId());
        assertEquals(1, _directory.getTable(2).getTableDestinationId());
        assertEquals(1, _directory.getTable(3).getTableDestinationId());
        assertEquals(25, _directory.getTable(0).getTableLength());
        assertEquals(74, _directory.getTable(1).getTableLength());
        assertEquals(23, _directory.getTable(2).getTableLength());
        assertEquals(38, _directory.getTable(3).getTableLength());
    }
}
